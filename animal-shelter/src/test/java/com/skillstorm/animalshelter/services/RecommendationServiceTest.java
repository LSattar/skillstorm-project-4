package com.skillstorm.animalshelter.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.skillstorm.animalshelter.models.AdopterProfile;
import com.skillstorm.animalshelter.models.AdopterQuestionnaire;
import com.skillstorm.animalshelter.models.Animal;
import com.skillstorm.animalshelter.repositories.AdopterProfileRepository;
import com.skillstorm.animalshelter.repositories.AdopterQuestionnaireRepository;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private AnimalService animalService;
    @Mock
    private AdopterProfileRepository adopterProfileRepository;
    @Mock
    private AdopterQuestionnaireRepository adopterQuestionnaireRepository;
    @Mock
    private EmbeddingProvider embeddingProvider;

    private RecommendationService service;

    @BeforeEach
    void setUp() {
        service = new RecommendationService(
                animalService,
                adopterProfileRepository,
                adopterQuestionnaireRepository,
                embeddingProvider,
                60,
                30);
    }

    @Test
    void returnsRuleOnlyWhenEmbeddingsUnavailable() {
        UUID adopterId = UUID.randomUUID();
        AdopterProfile profile = new AdopterProfile();
        profile.setUserId(adopterId);
        profile.setHasYard(false);
        profile.setHousingType("APARTMENT");
        profile.setNotes("small space living");

        AdopterQuestionnaire questionnaire = new AdopterQuestionnaire();
        questionnaire.setUserId(adopterId);
        questionnaire.setNeedsGoodWithKids(true);
        questionnaire.setWillingMedicallyComplex(false);

        Animal animal = new Animal();
        animal.setId(UUID.randomUUID());
        animal.setName("Rocket");
        animal.setDescription("energetic dog needs exercise");
        animal.setGoodWithKids(true);
        animal.setMedicallyComplex(false);
        animal.setStatus("IN_SHELTER");

        when(adopterProfileRepository.findById(adopterId)).thenReturn(Optional.of(profile));
        when(adopterQuestionnaireRepository.findByUserId(adopterId)).thenReturn(Optional.of(questionnaire));
        when(animalService.findAvailableForAdoption(null, null, null, null, null, null)).thenReturn(List.of(animal));
        when(embeddingProvider.embed(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        var results = service.getRecommendations(adopterId, 10);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getMatchingMode()).isEqualTo("RULES_ONLY");
        assertThat(results.get(0).getReasonCodes()).contains("GOOD_WITH_KIDS_MATCH");
    }
}
