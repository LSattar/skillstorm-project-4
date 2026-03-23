package com.skillstorm.animalshelter.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.skillstorm.animalshelter.dtos.request.UpsertQuestionnaireRequest;
import com.skillstorm.animalshelter.exceptions.ResourceNotFoundException;
import com.skillstorm.animalshelter.models.AdopterProfile;
import com.skillstorm.animalshelter.models.AdopterQuestionnaire;
import com.skillstorm.animalshelter.models.User;
import com.skillstorm.animalshelter.repositories.AdopterProfileRepository;
import com.skillstorm.animalshelter.repositories.AdopterQuestionnaireRepository;
import com.skillstorm.animalshelter.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
class AdopterQuestionnaireServiceTest {

    @Mock
    private AdopterQuestionnaireRepository questionnaireRepository;
    @Mock
    private AdopterProfileRepository adopterProfileRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdopterQuestionnaireService service;

    private UUID userId;
    private UpsertQuestionnaireRequest request;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        request = new UpsertQuestionnaireRequest();
        request.setHouseholdSize(3);
        request.setHousingType("HOUSE");
        request.setHasYard(true);
        request.setHasKids(false);
        request.setHasOtherPets(true);
        request.setNeedsGoodWithKids(false);
        request.setNeedsGoodWithOtherPets(true);
        request.setWillingMedicallyComplex(true);
        request.setNotes("prefers medium dogs");
        request.setPhone("5551234567");
    }

    @Test
    void upsertThrowsWhenUserMissing() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.upsert(userId, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void upsertUpdatesUserPhoneAndSyncsProfile() {
        User user = new User();
        user.setId(userId);
        user.setUpdatedAt(Instant.now());
        AdopterQuestionnaire questionnaire = new AdopterQuestionnaire();
        questionnaire.setId(UUID.randomUUID());
        questionnaire.setUserId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(questionnaireRepository.findByUserId(userId)).thenReturn(Optional.of(questionnaire));
        when(questionnaireRepository.save(any(AdopterQuestionnaire.class))).thenAnswer(inv -> inv.getArgument(0));
        when(adopterProfileRepository.findById(userId)).thenReturn(Optional.of(new AdopterProfile()));

        AdopterQuestionnaire saved = service.upsert(userId, request);

        assertThat(saved.getHousingType()).isEqualTo("HOUSE");
        assertThat(saved.getHouseholdSize()).isEqualTo(3);
        verify(userRepository).save(any(User.class));
        verify(adopterProfileRepository).save(any(AdopterProfile.class));
    }
}
