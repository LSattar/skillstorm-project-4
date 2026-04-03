package com.skillstorm.animalshelter.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skillstorm.animalshelter.dtos.response.AdopterRecommendationResponse;
import com.skillstorm.animalshelter.models.AdopterProfile;
import com.skillstorm.animalshelter.models.AdopterQuestionnaire;
import com.skillstorm.animalshelter.models.Animal;
import com.skillstorm.animalshelter.repositories.AdopterProfileRepository;
import com.skillstorm.animalshelter.repositories.AdopterQuestionnaireRepository;

@Service
public class RecommendationService {

    private static final int HARD_MAX_LIMIT = 20;

    private final AnimalService animalService;
    private final AdopterProfileRepository adopterProfileRepository;
    private final AdopterQuestionnaireRepository adopterQuestionnaireRepository;
    private final EmbeddingProvider embeddingProvider;
    private final int semanticWeight;
    private final int structuredWeight;

    public RecommendationService(
            AnimalService animalService,
            AdopterProfileRepository adopterProfileRepository,
            AdopterQuestionnaireRepository adopterQuestionnaireRepository,
            EmbeddingProvider embeddingProvider,
            @Value("${app.ai.weights.semantic:60}") int semanticWeight,
            @Value("${app.ai.weights.structured:30}") int structuredWeight) {
        this.animalService = animalService;
        this.adopterProfileRepository = adopterProfileRepository;
        this.adopterQuestionnaireRepository = adopterQuestionnaireRepository;
        this.embeddingProvider = embeddingProvider;
        this.semanticWeight = semanticWeight;
        this.structuredWeight = structuredWeight;
    }

    @Transactional(readOnly = true)
    public List<AdopterRecommendationResponse> getRecommendations(UUID adopterUserId, int requestedLimit) {
        int limit = Math.max(1, Math.min(requestedLimit, HARD_MAX_LIMIT));
        Optional<AdopterProfile> profileOpt = adopterProfileRepository.findById(adopterUserId);
        Optional<AdopterQuestionnaire> questionnaireOpt = adopterQuestionnaireRepository.findByUserId(adopterUserId);
        AdopterContext adopterContext = new AdopterContext(profileOpt.orElse(null), questionnaireOpt.orElse(null));

        List<Animal> animals = animalService.findAvailableForAdoption(null, null, null, null, null, null);
        if (animals.isEmpty()) {
            return List.of();
        }

        String adopterText = buildAdopterText(adopterContext);
        String adopterKey = "adopter:" + adopterUserId + ":" + hash(adopterText);
        Optional<double[]> adopterEmbedding = embeddingProvider.embed(adopterKey, adopterText);
        String matchingMode = adopterEmbedding.isPresent() ? "HYBRID" : "RULES_ONLY";

        List<AdopterRecommendationResponse> ranked = new ArrayList<>();
        for (Animal animal : animals) {
            String animalText = buildAnimalText(animal);
            String animalKey = "animal:" + animal.getId() + ":" + hash(animalText);
            Optional<double[]> animalEmbedding = adopterEmbedding.isPresent() ? embeddingProvider.embed(animalKey, animalText) : Optional.empty();
            ScoreResult score = score(adopterContext, animal, adopterEmbedding, animalEmbedding);
            AdopterRecommendationResponse response = new AdopterRecommendationResponse();
            response.setAnimalId(animal.getId());
            response.setAnimalName(animal.getName());
            response.setSpecies(animal.getSpecies());
            response.setBreed(animal.getBreed());
            response.setScore(score.score());
            response.setReasonCodes(score.reasonCodes().stream().limit(4).collect(Collectors.toList()));
            response.setMatchingMode(matchingMode);
            response.setSummary(buildSummary(score.reasonCodes()));
            response.setRationale(buildRationale(score.reasonCodes(), matchingMode));
            ranked.add(response);
        }

        ranked.sort(Comparator.comparing(AdopterRecommendationResponse::getScore).reversed());
        if (ranked.size() > limit) {
            return ranked.subList(0, limit);
        }
        return ranked;
    }

    private ScoreResult score(
            AdopterContext adopterContext,
            Animal animal,
            Optional<double[]> adopterEmbedding,
            Optional<double[]> animalEmbedding) {
        int score = 0;
        List<String> reasonCodes = new ArrayList<>();
        Set<Signal> adopterSignals = extractSignals(buildAdopterText(adopterContext), ADOPTER_SIGNAL_KEYWORDS);
        Set<Signal> animalSignals = extractSignals(buildAnimalText(animal), ANIMAL_SIGNAL_KEYWORDS);

        int structuredScore = structuredCompatibilityScore(adopterContext, animal, reasonCodes);
        score += structuredScore;

        int ruleAdjustments = ruleAdjustments(adopterContext, animal, adopterSignals, animalSignals, reasonCodes);
        score += ruleAdjustments;

        if (adopterEmbedding.isPresent() && animalEmbedding.isPresent()) {
            double cosine = cosineSimilarity(adopterEmbedding.get(), animalEmbedding.get());
            int semanticScore = (int) Math.round(Math.max(0d, cosine) * semanticWeight);
            score += semanticScore;
            if (semanticScore >= 35) {
                reasonCodes.add("NOTES_SEMANTIC_MATCH");
            }
        }

        int capped = Math.max(0, Math.min(100, score));
        return new ScoreResult(capped, reasonCodes);
    }

    private int structuredCompatibilityScore(AdopterContext adopterContext, Animal animal, List<String> reasonCodes) {
        int score = 0;
        Boolean needsKids = adopterContext.needsGoodWithKids();
        Boolean needsPets = adopterContext.needsGoodWithOtherPets();
        Boolean willingMedical = adopterContext.willingMedicallyComplex();
        Boolean hasYard = adopterContext.hasYard();

        if (Boolean.TRUE.equals(needsKids) && Boolean.TRUE.equals(animal.getGoodWithKids())) {
            score += 12;
            reasonCodes.add("GOOD_WITH_KIDS_MATCH");
        } else if (Boolean.TRUE.equals(needsKids)) {
            score -= 35;
            reasonCodes.add("KIDS_SAFETY_MISMATCH");
        }
        if (Boolean.TRUE.equals(needsPets) && Boolean.TRUE.equals(animal.getGoodWithOtherPets())) {
            score += 10;
            reasonCodes.add("GOOD_WITH_PETS_MATCH");
        } else if (Boolean.TRUE.equals(needsPets)) {
            score -= 30;
            reasonCodes.add("OTHER_PETS_MISMATCH");
        }
        if (Boolean.TRUE.equals(willingMedical) && Boolean.TRUE.equals(animal.getMedicallyComplex())) {
            score += 8;
            reasonCodes.add("MEDICAL_WILLINGNESS_MATCH");
        } else if (Boolean.FALSE.equals(willingMedical) && Boolean.TRUE.equals(animal.getMedicallyComplex())) {
            score -= 30;
            reasonCodes.add("MEDICAL_COMPLEXITY_MISMATCH");
        }
        if (Boolean.TRUE.equals(hasYard) && hasSignal(animal.getDescription(), "energetic")) {
            score += 8;
            reasonCodes.add("YARD_ENERGY_MATCH");
        }
        return Math.min(structuredWeight, score);
    }

    private int ruleAdjustments(
            AdopterContext adopterContext,
            Animal animal,
            Set<Signal> adopterSignals,
            Set<Signal> animalSignals,
            List<String> reasonCodes) {
        int adjustment = 0;
        if (adopterSignals.contains(Signal.BIG_FAMILY_HOME)
                && (animalSignals.contains(Signal.SHY_OR_TIMID) || animalSignals.contains(Signal.NEEDS_QUIET_HOME))) {
            adjustment -= 35;
            reasonCodes.add("HOUSEHOLD_ACTIVITY_MISMATCH");
        }
        if (adopterSignals.contains(Signal.BIG_FAMILY_HOME)
                && (animalSignals.contains(Signal.GOOD_FOR_FAMILIES) || Boolean.TRUE.equals(animal.getGoodWithKids()))) {
            adjustment += 12;
            reasonCodes.add("FAMILY_FRIENDLY_MATCH");
        }
        if (adopterSignals.contains(Signal.SMALL_SPACE_LIVING)
                && animalSignals.contains(Signal.HIGH_ENERGY)
                && !Boolean.TRUE.equals(adopterContext.hasYard())) {
            adjustment -= 25;
            reasonCodes.add("ENERGY_SPACE_MISMATCH");
        }
        if (adopterSignals.contains(Signal.SMALL_SPACE_LIVING)
                && animalSignals.contains(Signal.HIGH_ENERGY)
                && adopterSignals.contains(Signal.ACTIVE_LIFESTYLE)) {
            adjustment += 10;
            reasonCodes.add("ACTIVE_OWNER_OFFSET");
        }
        if (animalSignals.contains(Signal.SPECIAL_HANDLING_NEEDS)
                && adopterSignals.contains(Signal.EXPERIENCED_OWNER)) {
            adjustment += 8;
            reasonCodes.add("EXPERIENCE_BEHAVIOR_MATCH");
        } else if (animalSignals.contains(Signal.SPECIAL_HANDLING_NEEDS)) {
            adjustment -= 12;
            reasonCodes.add("EXPERIENCE_GAP");
        }
        return adjustment;
    }

    private String buildAdopterText(AdopterContext adopterContext) {
        List<String> fragments = new ArrayList<>();
        if (adopterContext.profile() != null) {
            add(fragments, adopterContext.profile().getHousingType());
            add(fragments, adopterContext.profile().getCity());
            add(fragments, adopterContext.profile().getState());
            add(fragments, adopterContext.profile().getNotes());
            if (Boolean.TRUE.equals(adopterContext.profile().getHasKids())) add(fragments, "has kids");
            if (Boolean.TRUE.equals(adopterContext.profile().getHasYard())) add(fragments, "has yard");
            if (Boolean.TRUE.equals(adopterContext.profile().getHasOtherPets())) add(fragments, "has other pets");
        }
        if (adopterContext.questionnaire() != null) {
            add(fragments, adopterContext.questionnaire().getHousingType());
            add(fragments, adopterContext.questionnaire().getNotes());
            if (Boolean.TRUE.equals(adopterContext.questionnaire().getHasKids())) add(fragments, "household with kids");
            if (Boolean.TRUE.equals(adopterContext.questionnaire().getHasYard())) add(fragments, "yard");
        }
        return String.join(" ", fragments).trim();
    }

    private String buildAnimalText(Animal animal) {
        List<String> fragments = new ArrayList<>();
        add(fragments, animal.getName());
        add(fragments, animal.getSpecies());
        add(fragments, animal.getBreed());
        add(fragments, animal.getDescription());
        if (Boolean.TRUE.equals(animal.getGoodWithKids())) add(fragments, "good with kids");
        if (Boolean.TRUE.equals(animal.getGoodWithOtherPets())) add(fragments, "good with other pets");
        if (Boolean.TRUE.equals(animal.getMedicallyComplex())) add(fragments, "medically complex");
        return String.join(" ", fragments).trim();
    }

    private Set<Signal> extractSignals(String text, Map<Signal, List<String>> dictionary) {
        String lower = text == null ? "" : text.toLowerCase(Locale.ROOT);
        Set<Signal> signals = EnumSet.noneOf(Signal.class);
        for (Map.Entry<Signal, List<String>> entry : dictionary.entrySet()) {
            for (String keyword : entry.getValue()) {
                if (lower.contains(keyword)) {
                    signals.add(entry.getKey());
                    break;
                }
            }
        }
        return signals;
    }

    private boolean hasSignal(String text, String keyword) {
        return text != null && text.toLowerCase(Locale.ROOT).contains(keyword);
    }

    private double cosineSimilarity(double[] a, double[] b) {
        if (a.length == 0 || b.length == 0 || a.length != b.length) {
            return 0d;
        }
        double dot = 0d;
        double normA = 0d;
        double normB = 0d;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        if (normA == 0d || normB == 0d) {
            return 0d;
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private String buildSummary(List<String> reasonCodes) {
        if (reasonCodes == null || reasonCodes.isEmpty()) {
            return "General compatibility based on profile and pet details.";
        }
        List<String> unique = new ArrayList<>(new HashSet<>(reasonCodes));
        return "Top factors: " + String.join(", ", unique.stream().limit(3).toList());
    }

    private String buildRationale(List<String> reasonCodes, String matchingMode) {
        if (reasonCodes == null || reasonCodes.isEmpty()) {
            if ("HYBRID".equals(matchingMode)) {
                return "This recommendation is based on overall semantic similarity between your notes and this pet's profile.";
            }
            return "This recommendation is based on your profile preferences and available pet details.";
        }
        List<String> unique = reasonCodes.stream().distinct().limit(3).toList();
        String explanation = unique.stream()
                .map(this::reasonPhrase)
                .collect(Collectors.joining("; "));
        return "Why this pet: " + explanation + ".";
    }

    private String reasonPhrase(String reasonCode) {
        return switch (reasonCode) {
            case "GOOD_WITH_KIDS_MATCH" -> "matches your need for a pet that is good with kids";
            case "KIDS_SAFETY_MISMATCH" -> "may not fit your kids-safety preference";
            case "GOOD_WITH_PETS_MATCH" -> "matches your need for a pet that gets along with other pets";
            case "OTHER_PETS_MISMATCH" -> "may not fit your other-pets compatibility needs";
            case "MEDICAL_WILLINGNESS_MATCH" -> "aligns with your willingness to care for medically complex pets";
            case "MEDICAL_COMPLEXITY_MISMATCH" -> "may require medical care beyond your stated preference";
            case "YARD_ENERGY_MATCH" -> "fits an active pet profile for a home with yard access";
            case "HOUSEHOLD_ACTIVITY_MISMATCH" -> "household activity level may be a weaker fit";
            case "FAMILY_FRIENDLY_MATCH" -> "shows strong family-friendly compatibility";
            case "ENERGY_SPACE_MISMATCH" -> "energy level may be high for your available space";
            case "ACTIVE_OWNER_OFFSET" -> "your active lifestyle helps offset this pet's high energy";
            case "EXPERIENCE_BEHAVIOR_MATCH" -> "your prior experience fits this pet's behavior needs";
            case "EXPERIENCE_GAP" -> "this pet may benefit from more behavior-handling experience";
            case "NOTES_SEMANTIC_MATCH" -> "your notes and this pet's description are semantically similar";
            default -> "compatibility signals were detected in your profile and this pet's details";
        };
    }

    private void add(List<String> fragments, String value) {
        if (value != null && !value.isBlank()) {
            fragments.add(value);
        }
    }

    private String hash(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(encoded.length * 2);
            for (byte b : encoded) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return Integer.toHexString(text.hashCode());
        }
    }

    record ScoreResult(int score, List<String> reasonCodes) {}

    record AdopterContext(AdopterProfile profile, AdopterQuestionnaire questionnaire) {
        Boolean needsGoodWithKids() {
            return questionnaire != null && questionnaire.getNeedsGoodWithKids() != null
                    ? questionnaire.getNeedsGoodWithKids()
                    : profile != null ? profile.getNeedsGoodWithKids() : null;
        }

        Boolean needsGoodWithOtherPets() {
            return questionnaire != null && questionnaire.getNeedsGoodWithOtherPets() != null
                    ? questionnaire.getNeedsGoodWithOtherPets()
                    : profile != null ? profile.getNeedsGoodWithOtherPets() : null;
        }

        Boolean willingMedicallyComplex() {
            return questionnaire != null && questionnaire.getWillingMedicallyComplex() != null
                    ? questionnaire.getWillingMedicallyComplex()
                    : profile != null ? profile.getWillingMedicallyComplex() : null;
        }

        Boolean hasYard() {
            return questionnaire != null && questionnaire.getHasYard() != null
                    ? questionnaire.getHasYard()
                    : profile != null ? profile.getHasYard() : null;
        }
    }

    enum Signal {
        BIG_FAMILY_HOME,
        QUIET_HOME_PREFERENCE,
        ACTIVE_LIFESTYLE,
        SMALL_SPACE_LIVING,
        EXPERIENCED_OWNER,
        SHY_OR_TIMID,
        HIGH_ENERGY,
        NEEDS_QUIET_HOME,
        GOOD_FOR_FAMILIES,
        SPECIAL_HANDLING_NEEDS
    }

    private static final Map<Signal, List<String>> ADOPTER_SIGNAL_KEYWORDS = new HashMap<>();
    private static final Map<Signal, List<String>> ANIMAL_SIGNAL_KEYWORDS = new HashMap<>();

    static {
        ADOPTER_SIGNAL_KEYWORDS.put(Signal.BIG_FAMILY_HOME, List.of("big family", "large family", "multiple kids", "busy household"));
        ADOPTER_SIGNAL_KEYWORDS.put(Signal.QUIET_HOME_PREFERENCE, List.of("quiet home", "calm environment", "low noise"));
        ADOPTER_SIGNAL_KEYWORDS.put(Signal.ACTIVE_LIFESTYLE, List.of("jogging", "hiking", "runs daily", "very active", "active lifestyle"));
        ADOPTER_SIGNAL_KEYWORDS.put(Signal.SMALL_SPACE_LIVING, List.of("apartment", "condo", "no yard", "limited space", "small space"));
        ADOPTER_SIGNAL_KEYWORDS.put(Signal.EXPERIENCED_OWNER, List.of("experienced", "rescue", "handled anxious", "training background"));

        ANIMAL_SIGNAL_KEYWORDS.put(Signal.SHY_OR_TIMID, List.of("shy", "timid", "nervous", "fearful"));
        ANIMAL_SIGNAL_KEYWORDS.put(Signal.HIGH_ENERGY, List.of("energetic", "active", "playful", "needs exercise", "high energy"));
        ANIMAL_SIGNAL_KEYWORDS.put(Signal.NEEDS_QUIET_HOME, List.of("calm home", "quiet home", "low traffic", "no chaos"));
        ANIMAL_SIGNAL_KEYWORDS.put(Signal.GOOD_FOR_FAMILIES, List.of("family-friendly", "good with kids"));
        ANIMAL_SIGNAL_KEYWORDS.put(Signal.SPECIAL_HANDLING_NEEDS, List.of("reactive", "separation anxiety", "prey drive"));
    }
}
