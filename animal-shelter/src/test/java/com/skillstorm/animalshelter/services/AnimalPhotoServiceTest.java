package com.skillstorm.animalshelter.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.skillstorm.animalshelter.dtos.request.CreateAnimalPhotoRequest;
import com.skillstorm.animalshelter.exceptions.ResourceNotFoundException;
import com.skillstorm.animalshelter.models.AnimalPhoto;
import com.skillstorm.animalshelter.repositories.AnimalPhotoRepository;
import com.skillstorm.animalshelter.repositories.AnimalRepository;

@ExtendWith(MockitoExtension.class)
class AnimalPhotoServiceTest {

    @Mock
    private AnimalPhotoRepository animalPhotoRepository;

    @Mock
    private AnimalRepository animalRepository;

    @InjectMocks
    private AnimalPhotoService animalPhotoService;

    private UUID animalId;
    private UUID photoId;
    private CreateAnimalPhotoRequest createRequest;

    @BeforeEach
    void setUp() {
        animalId = UUID.randomUUID();
        photoId = UUID.randomUUID();
        createRequest = new CreateAnimalPhotoRequest();
        createRequest.setAnimalId(animalId);
        createRequest.setS3Key("photos/animal-1/photo.jpg");
        createRequest.setUrl("https://example.com/photo.jpg");
        createRequest.setIsPrimary(false);
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("saves photo when animal exists")
        void savesPhotoWhenAnimalExists() {
            when(animalRepository.existsById(animalId)).thenReturn(true);
            when(animalPhotoRepository.save(any(AnimalPhoto.class))).thenAnswer(inv -> {
                AnimalPhoto p = inv.getArgument(0);
                p.setId(photoId);
                p.setCreatedAt(Instant.now());
                return p;
            });

            AnimalPhoto result = animalPhotoService.create(createRequest);

            assertThat(result).isNotNull();
            assertThat(result.getAnimalId()).isEqualTo(animalId);
            assertThat(result.getS3Key()).isEqualTo(createRequest.getS3Key());
            assertThat(result.getUrl()).isEqualTo(createRequest.getUrl());
            verify(animalRepository).existsById(animalId);
            verify(animalPhotoRepository).save(any(AnimalPhoto.class));
        }

        @Test
        @DisplayName("throws when animal does not exist")
        void throwsWhenAnimalDoesNotExist() {
            when(animalRepository.existsById(animalId)).thenReturn(false);

            assertThatThrownBy(() -> animalPhotoService.create(createRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Animal not found");

            verify(animalPhotoRepository, never()).save(any(AnimalPhoto.class));
        }
    }

    @Nested
    @DisplayName("findByIdOrThrow")
    class FindByIdOrThrow {

        @Test
        @DisplayName("returns photo when found")
        void returnsPhotoWhenFound() {
            AnimalPhoto photo = new AnimalPhoto();
            photo.setId(photoId);
            photo.setAnimalId(animalId);
            when(animalPhotoRepository.findById(photoId)).thenReturn(Optional.of(photo));

            AnimalPhoto result = animalPhotoService.findByIdOrThrow(photoId);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(photoId);
        }

        @Test
        @DisplayName("throws when not found")
        void throwsWhenNotFound() {
            when(animalPhotoRepository.findById(photoId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> animalPhotoService.findByIdOrThrow(photoId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Animal photo not found");
        }
    }

    @Nested
    @DisplayName("findByAnimalId")
    class FindByAnimalId {

        @Test
        @DisplayName("returns list from repository")
        void returnsListFromRepository() {
            AnimalPhoto photo = new AnimalPhoto();
            photo.setId(photoId);
            photo.setAnimalId(animalId);
            when(animalPhotoRepository.findByAnimalIdOrderByIsPrimaryDesc(animalId)).thenReturn(List.of(photo));

            List<AnimalPhoto> result = animalPhotoService.findByAnimalId(animalId);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(photoId);
            verify(animalPhotoRepository).findByAnimalIdOrderByIsPrimaryDesc(animalId);
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("deletes when photo exists")
        void deletesWhenPhotoExists() {
            when(animalPhotoRepository.existsById(photoId)).thenReturn(true);

            animalPhotoService.delete(photoId);

            verify(animalPhotoRepository).deleteById(photoId);
        }

        @Test
        @DisplayName("throws when photo does not exist")
        void throwsWhenPhotoDoesNotExist() {
            when(animalPhotoRepository.existsById(photoId)).thenReturn(false);

            assertThatThrownBy(() -> animalPhotoService.delete(photoId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Animal photo not found");

            verify(animalPhotoRepository, never()).deleteById(photoId);
        }
    }
}
