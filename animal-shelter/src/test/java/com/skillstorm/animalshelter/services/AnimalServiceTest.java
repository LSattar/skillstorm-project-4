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

import com.skillstorm.animalshelter.models.Animal;
import com.skillstorm.animalshelter.repositories.AdoptionApplicationRepository;
import com.skillstorm.animalshelter.repositories.AnimalRepository;

@ExtendWith(MockitoExtension.class)
class AnimalServiceTest {

    @Mock
    private AnimalRepository animalRepository;
    @Mock
    private AnimalEventService animalEventService;
    @Mock
    private AdoptionApplicationRepository adoptionApplicationRepository;

    @InjectMocks
    private AnimalService service;

    private UUID animalId;
    private UUID staffId;

    @BeforeEach
    void setUp() {
        animalId = UUID.randomUUID();
        staffId = UUID.randomUUID();
    }

    @Test
    void moveToShelterClearsFosterAndRecordsEvent() {
        Animal animal = new Animal();
        animal.setId(animalId);
        animal.setCurrentShelterId(1L);
        animal.setCurrentFosterUserId(UUID.randomUUID());
        when(animalRepository.findById(animalId)).thenReturn(Optional.of(animal));
        when(animalRepository.save(any(Animal.class))).thenAnswer(inv -> inv.getArgument(0));

        Animal result = service.moveToShelter(animalId, 2L, "moved", staffId);

        assertThat(result.getCurrentShelterId()).isEqualTo(2L);
        assertThat(result.getCurrentFosterUserId()).isNull();
        verify(animalEventService).recordEvent(any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void updateStatusRejectsAdoptedToNonAdoptedTransition() {
        Animal animal = new Animal();
        animal.setId(animalId);
        animal.setStatus("ADOPTED");
        when(animalRepository.findById(animalId)).thenReturn(Optional.of(animal));

        assertThatThrownBy(() -> service.updateStatus(animalId, "IN_FOSTER", "invalid", staffId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid status transition");
    }

    @Test
    void moveToFosterClearsShelterAndRecordsEvent() {
        Animal animal = new Animal();
        animal.setId(animalId);
        animal.setCurrentShelterId(10L);
        animal.setCurrentFosterUserId(null);
        UUID fosterId = UUID.randomUUID();
        when(animalRepository.findById(animalId)).thenReturn(Optional.of(animal));
        when(animalRepository.save(any(Animal.class))).thenAnswer(inv -> inv.getArgument(0));

        Animal result = service.moveToFoster(animalId, fosterId, "foster", staffId);

        assertThat(result.getCurrentShelterId()).isNull();
        assertThat(result.getCurrentFosterUserId()).isEqualTo(fosterId);
        verify(animalEventService).recordEvent(any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void updateStatusRecordsStatusChangeEvent() {
        Animal animal = new Animal();
        animal.setId(animalId);
        animal.setStatus("IN_SHELTER");
        when(animalRepository.findById(animalId)).thenReturn(Optional.of(animal));
        when(animalRepository.save(any(Animal.class))).thenAnswer(inv -> inv.getArgument(0));

        service.updateStatus(animalId, "ON_HOLD", "hold", staffId);

        verify(animalEventService).recordEvent(any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void findAllStaffFiltersMedicallyComplex() {
        Animal complex = new Animal();
        complex.setId(UUID.randomUUID());
        complex.setStatus("IN_SHELTER");
        complex.setSpecies("DOG");
        complex.setMedicallyComplex(true);
        Animal simple = new Animal();
        simple.setId(UUID.randomUUID());
        simple.setStatus("IN_SHELTER");
        simple.setSpecies("DOG");
        simple.setMedicallyComplex(false);
        when(animalRepository.findAll()).thenReturn(List.of(complex, simple));

        List<Animal> out = service.findAllStaff(null, null, null, null, true, null, null);

        assertThat(out).extracting(Animal::getId).containsExactly(complex.getId());
    }
}
