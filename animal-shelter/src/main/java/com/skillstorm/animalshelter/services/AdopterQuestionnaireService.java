package com.skillstorm.animalshelter.services;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skillstorm.animalshelter.dtos.request.UpsertQuestionnaireRequest;
import com.skillstorm.animalshelter.exceptions.ResourceNotFoundException;
import com.skillstorm.animalshelter.models.AdopterProfile;
import com.skillstorm.animalshelter.models.AdopterQuestionnaire;
import com.skillstorm.animalshelter.models.User;
import com.skillstorm.animalshelter.repositories.AdopterProfileRepository;
import com.skillstorm.animalshelter.repositories.AdopterQuestionnaireRepository;
import com.skillstorm.animalshelter.repositories.UserRepository;

@Service
public class AdopterQuestionnaireService {

    private static final Logger log = LoggerFactory.getLogger(AdopterQuestionnaireService.class);

    private final AdopterQuestionnaireRepository questionnaireRepository;
    private final AdopterProfileRepository adopterProfileRepository;
    private final UserRepository userRepository;

    public AdopterQuestionnaireService(AdopterQuestionnaireRepository questionnaireRepository,
                                       AdopterProfileRepository adopterProfileRepository,
                                       UserRepository userRepository) {
        this.questionnaireRepository = questionnaireRepository;
        this.adopterProfileRepository = adopterProfileRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Optional<AdopterQuestionnaire> getByUserId(UUID userId) {
        return questionnaireRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public AdopterQuestionnaire getByUserIdOrThrow(UUID userId) {
        return questionnaireRepository.findByUserId(userId).orElseThrow(() -> {
            log.error("Adopter questionnaire not found for user id={}", userId);
            return new ResourceNotFoundException("Adopter questionnaire not found for user: " + userId);
        });
    }

    @Transactional
    public AdopterQuestionnaire upsert(UUID userId, UpsertQuestionnaireRequest req) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.error("User not found for questionnaire upsert, userId={}", userId);
            return new ResourceNotFoundException("User not found: " + userId);
        });
        if (req.getPhone() != null) {
            user.setPhone(req.getPhone());
            user.setUpdatedAt(Instant.now());
            userRepository.save(user);
        }
        AdopterQuestionnaire q = questionnaireRepository.findByUserId(userId).orElseGet(() -> {
            AdopterQuestionnaire newQ = new AdopterQuestionnaire();
            newQ.setId(UUID.randomUUID());
            newQ.setUserId(userId);
            Instant now = Instant.now();
            newQ.setCreatedAt(now);
            newQ.setUpdatedAt(now);
            return newQ;
        });
        if (req.getSchemaVersion() != null) q.setSchemaVersion(req.getSchemaVersion());
        if (req.getHouseholdSize() != null) q.setHouseholdSize(req.getHouseholdSize());
        if (req.getHousingType() != null) q.setHousingType(req.getHousingType());
        if (req.getHasYard() != null) q.setHasYard(req.getHasYard());
        if (req.getHasKids() != null) q.setHasKids(req.getHasKids());
        if (req.getHasOtherPets() != null) q.setHasOtherPets(req.getHasOtherPets());
        if (req.getNeedsGoodWithKids() != null) q.setNeedsGoodWithKids(req.getNeedsGoodWithKids());
        if (req.getNeedsGoodWithOtherPets() != null) q.setNeedsGoodWithOtherPets(req.getNeedsGoodWithOtherPets());
        if (req.getWillingMedicallyComplex() != null) q.setWillingMedicallyComplex(req.getWillingMedicallyComplex());
        if (req.getNotes() != null) q.setNotes(req.getNotes());
        q.setUpdatedAt(Instant.now());
        q = questionnaireRepository.save(q);
        syncToAdopterProfile(userId, q);
        log.info("Upserted adopter questionnaire for user id={}", userId);
        return q;
    }

    private void syncToAdopterProfile(UUID userId, AdopterQuestionnaire q) {
        AdopterProfile profile = adopterProfileRepository.findById(userId).orElseGet(() -> {
            AdopterProfile p = new AdopterProfile();
            p.setUserId(userId);
            return p;
        });
        profile.setHouseholdSize(q.getHouseholdSize());
        profile.setHousingType(q.getHousingType());
        profile.setHasYard(q.getHasYard());
        profile.setHasKids(q.getHasKids());
        profile.setHasOtherPets(q.getHasOtherPets());
        profile.setNeedsGoodWithKids(q.getNeedsGoodWithKids());
        profile.setNeedsGoodWithOtherPets(q.getNeedsGoodWithOtherPets());
        profile.setWillingMedicallyComplex(q.getWillingMedicallyComplex());
        profile.setNotes(q.getNotes());
        adopterProfileRepository.save(profile);
    }
}
