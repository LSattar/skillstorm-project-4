package com.skillstorm.animalshelter.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.skillstorm.animalshelter.exceptions.ConflictException;
import com.skillstorm.animalshelter.models.Adoption;
import com.skillstorm.animalshelter.models.AdoptionApplication;
import com.skillstorm.animalshelter.models.Animal;
import com.skillstorm.animalshelter.repositories.AdoptionApplicationRepository;
import com.skillstorm.animalshelter.repositories.AdoptionRepository;
import com.skillstorm.animalshelter.repositories.AnimalRepository;

@ExtendWith(MockitoExtension.class)
class AdoptionServiceTest {

    @Mock
    private AdoptionRepository adoptionRepository;
    @Mock
    private AdoptionApplicationRepository applicationRepository;
    @Mock
    private AnimalRepository animalRepository;
    @Mock
    private AnimalEventService animalEventService;

    @InjectMocks
    private AdoptionService service;

    private UUID applicationId;
    private UUID animalId;
    private UUID adopterId;
    private UUID staffId;

    @BeforeEach
    void setUp() {
        applicationId = UUID.randomUUID();
        animalId = UUID.randomUUID();
        adopterId = UUID.randomUUID();
        staffId = UUID.randomUUID();
    }

    @Test
    void finalizeAdoptionRequiresApprovedApplication() {
        AdoptionApplication app = new AdoptionApplication();
        app.setId(applicationId);
        app.setAnimalId(animalId);
        app.setStatus("SUBMITTED");
        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(app));

        assertThatThrownBy(() -> service.finalizeAdoption(applicationId, staffId, "notes"))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("approved");
    }

    @Test
    void finalizeAdoptionRejectsAlreadyAdoptedAnimal() {
        AdoptionApplication app = new AdoptionApplication();
        app.setId(applicationId);
        app.setAnimalId(animalId);
        app.setAdopterUserId(adopterId);
        app.setStatus("APPROVED");
        Animal animal = new Animal();
        animal.setId(animalId);
        animal.setStatus("ADOPTED");

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(app));
        when(adoptionRepository.findByApplicationId(applicationId)).thenReturn(Optional.empty());
        when(animalRepository.findById(animalId)).thenReturn(Optional.of(animal));

        assertThatThrownBy(() -> service.finalizeAdoption(applicationId, staffId, "notes"))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already adopted");
    }

    @Test
    void finalizeAdoptionCreatesRecordUpdatesAnimalAndRecordsEvent() {
        AdoptionApplication app = new AdoptionApplication();
        app.setId(applicationId);
        app.setAnimalId(animalId);
        app.setAdopterUserId(adopterId);
        app.setStatus("APPROVED");
        Animal animal = new Animal();
        animal.setId(animalId);
        animal.setStatus("IN_SHELTER");

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(app));
        when(adoptionRepository.findByApplicationId(applicationId)).thenReturn(Optional.empty());
        when(animalRepository.findById(animalId)).thenReturn(Optional.of(animal));
        when(adoptionRepository.save(any(Adoption.class))).thenAnswer(inv -> inv.getArgument(0));
        when(animalRepository.save(any(Animal.class))).thenAnswer(inv -> inv.getArgument(0));

        Adoption adoption = service.finalizeAdoption(applicationId, staffId, "finalized");

        assertThat(adoption.getApplicationId()).isEqualTo(applicationId);
        assertThat(animal.getStatus()).isEqualTo("ADOPTED");
        verify(animalEventService).recordEvent(any(), any(), any(), any(), any(), any(), any(), any(), any());
    }
}
