package com.skillstorm.animalshelter.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.skillstorm.animalshelter.dtos.request.CreateApplicationRequest;
import com.skillstorm.animalshelter.dtos.request.UpsertQuestionnaireRequest;
import com.skillstorm.animalshelter.exceptions.ConflictException;
import com.skillstorm.animalshelter.models.AdoptionApplication;
import com.skillstorm.animalshelter.models.Animal;
import com.skillstorm.animalshelter.repositories.AdoptionApplicationRepository;
import com.skillstorm.animalshelter.repositories.AnimalRepository;
import com.skillstorm.animalshelter.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
class AdoptionApplicationServiceTest {

    @Mock
    private AdoptionApplicationRepository applicationRepository;
    @Mock
    private AnimalRepository animalRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AnimalEventService animalEventService;
    @Mock
    private AdopterQuestionnaireService adopterQuestionnaireService;

    @InjectMocks
    private AdoptionApplicationService service;

    private UUID adopterUserId;
    private UUID animalId;
    private CreateApplicationRequest request;

    @BeforeEach
    void setUp() {
        adopterUserId = UUID.randomUUID();
        animalId = UUID.randomUUID();
        request = new CreateApplicationRequest();
        request.setAnimalId(animalId);
        request.setQuestionnaireSnapshotJson("{\"schemaVersion\":1}");
    }

    @Test
    void createRejectsDuplicateActiveApplication() {
        Animal animal = new Animal();
        animal.setId(animalId);
        animal.setStatus("IN_SHELTER");

        AdoptionApplication existing = new AdoptionApplication();
        existing.setAnimalId(animalId);
        existing.setStatus("SUBMITTED");

        when(animalRepository.findById(animalId)).thenReturn(Optional.of(animal));
        when(userRepository.existsById(adopterUserId)).thenReturn(true);
        when(applicationRepository.findByAdopterUserId(adopterUserId)).thenReturn(List.of(existing));

        assertThatThrownBy(() -> service.create(adopterUserId, request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("active application");
    }

    @Test
    void createRejectsAdoptedAnimal() {
        Animal animal = new Animal();
        animal.setId(animalId);
        animal.setStatus("ADOPTED");

        when(animalRepository.findById(animalId)).thenReturn(Optional.of(animal));

        assertThatThrownBy(() -> service.create(adopterUserId, request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("adopted");
    }

    @Test
    void createSavesAndRecordsApplicationSubmittedEvent() {
        Animal animal = new Animal();
        animal.setId(animalId);
        animal.setStatus("IN_SHELTER");

        when(animalRepository.findById(animalId)).thenReturn(Optional.of(animal));
        when(userRepository.existsById(adopterUserId)).thenReturn(true);
        when(applicationRepository.findByAdopterUserId(adopterUserId)).thenReturn(List.of());
        when(applicationRepository.save(any(AdoptionApplication.class))).thenAnswer(inv -> inv.getArgument(0));

        AdoptionApplication created = service.create(adopterUserId, request);

        assertThat(created.getStatus()).isEqualTo("SUBMITTED");
        assertThat(created.getQuestionnaireSnapshotJson()).isEqualTo("{\"schemaVersion\":1}");
        verify(animalEventService).recordEvent(any(), any(), any(), any(), any(), any(), any(), any(), any());
        verify(adopterQuestionnaireService, never()).upsert(any(), any());
        verify(adopterQuestionnaireService, never()).buildQuestionnaireSnapshotJson(any());
    }

    @Test
    void createWithQuestionnaireAnswersUpsertsAndBuildsSnapshot() {
        Animal animal = new Animal();
        animal.setId(animalId);
        animal.setStatus("IN_SHELTER");

        UpsertQuestionnaireRequest answers = new UpsertQuestionnaireRequest();
        answers.setHouseholdSize(2);
        request.setQuestionnaireSnapshotJson(null);
        request.setQuestionnaireAnswers(answers);

        when(animalRepository.findById(animalId)).thenReturn(Optional.of(animal));
        when(userRepository.existsById(adopterUserId)).thenReturn(true);
        when(applicationRepository.findByAdopterUserId(adopterUserId)).thenReturn(List.of());
        when(applicationRepository.save(any(AdoptionApplication.class))).thenAnswer(inv -> inv.getArgument(0));
        when(adopterQuestionnaireService.buildQuestionnaireSnapshotJson(adopterUserId)).thenReturn("{\"full\":true}");

        AdoptionApplication created = service.create(adopterUserId, request);

        verify(adopterQuestionnaireService).upsert(adopterUserId, answers);
        verify(adopterQuestionnaireService).buildQuestionnaireSnapshotJson(adopterUserId);
        assertThat(created.getQuestionnaireSnapshotJson()).isEqualTo("{\"full\":true}");
    }

    @Test
    void approveSetsAnimalToAdoptionPending() {
        UUID appId = UUID.randomUUID();
        AdoptionApplication app = new AdoptionApplication();
        app.setId(appId);
        app.setAnimalId(animalId);
        app.setAdopterUserId(adopterUserId);
        app.setStatus("SUBMITTED");

        Animal animal = new Animal();
        animal.setId(animalId);
        animal.setStatus("IN_SHELTER");

        when(applicationRepository.findById(appId)).thenReturn(Optional.of(app));
        when(applicationRepository.save(any(AdoptionApplication.class))).thenAnswer(inv -> inv.getArgument(0));
        when(animalRepository.findById(animalId)).thenReturn(Optional.of(animal));
        when(animalRepository.save(any(Animal.class))).thenAnswer(inv -> inv.getArgument(0));

        UUID staffId = UUID.randomUUID();
        service.approve(appId, staffId, "ok");

        assertThat(animal.getStatus()).isEqualTo("ADOPTION_PENDING");
    }

    @Test
    void denyRevertsAnimalFromAdoptionPendingToInShelter() {
        UUID appId = UUID.randomUUID();
        AdoptionApplication app = new AdoptionApplication();
        app.setId(appId);
        app.setAnimalId(animalId);
        app.setAdopterUserId(adopterUserId);
        app.setStatus("SUBMITTED");

        Animal animal = new Animal();
        animal.setId(animalId);
        animal.setStatus("ADOPTION_PENDING");
        animal.setCurrentFosterUserId(null);

        when(applicationRepository.findById(appId)).thenReturn(Optional.of(app));
        when(applicationRepository.save(any(AdoptionApplication.class))).thenAnswer(inv -> inv.getArgument(0));
        when(animalRepository.findById(animalId)).thenReturn(Optional.of(animal));
        when(animalRepository.save(any(Animal.class))).thenAnswer(inv -> inv.getArgument(0));

        service.deny(appId, UUID.randomUUID(), "no");

        assertThat(animal.getStatus()).isEqualTo("IN_SHELTER");
    }
}
