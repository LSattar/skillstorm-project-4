package com.skillstorm.animalshelter.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
    }
}
