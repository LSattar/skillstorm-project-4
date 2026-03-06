package com.skillstorm.animalshelter.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.skillstorm.animalshelter.exceptions.ResourceNotFoundException;
import com.skillstorm.animalshelter.models.Animal;
import com.skillstorm.animalshelter.models.AnimalPhoto;
import com.skillstorm.animalshelter.services.AnimalPhotoService;
import com.skillstorm.animalshelter.services.AnimalService;
import com.skillstorm.animalshelter.services.S3Service;

@ExtendWith(MockitoExtension.class)
class AnimalsControllerTest {

    @Mock
    private AnimalService animalService;

    @Mock
    private AnimalPhotoService animalPhotoService;

    @Mock
    private S3Service s3Service;

    private AnimalsController controller;

    @BeforeEach
    void setUp() {
        controller = new AnimalsController(animalService, animalPhotoService, s3Service);
    }

    @Nested
    @DisplayName("GET /api/animals - listAvailable")
    class ListAvailable {

        @Test
        @DisplayName("returns 200 and empty list when no animals")
        void returns200AndEmptyList() {
            when(animalService.findAvailableForAdoption(any(), any(), any(), any(), any(), any())).thenReturn(List.of());

            ResponseEntity<List<com.skillstorm.animalshelter.dtos.response.AnimalResponse>> result =
                    controller.listAvailable(null, null, null, null, null, null);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody()).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("returns 200 and list of animals")
        void returns200AndListOfAnimals() {
            Animal animal = new Animal();
            animal.setId(UUID.randomUUID());
            animal.setName("Buddy");
            animal.setSpecies("DOG");
            animal.setStatus("IN_SHELTER");
            when(animalService.findAvailableForAdoption(any(), any(), any(), any(), any(), any())).thenReturn(List.of(animal));

            ResponseEntity<List<com.skillstorm.animalshelter.dtos.response.AnimalResponse>> result =
                    controller.listAvailable(null, null, null, null, null, null);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody()).hasSize(1);
            assertThat(result.getBody().get(0).getName()).isEqualTo("Buddy");
            assertThat(result.getBody().get(0).getSpecies()).isEqualTo("DOG");
        }
    }

    @Nested
    @DisplayName("GET /api/animals/{id} - getById")
    class GetById {

        @Test
        @DisplayName("returns 200 and animal when available")
        void returns200WhenAvailable() {
            UUID id = UUID.randomUUID();
            Animal animal = new Animal();
            animal.setId(id);
            animal.setName("Buddy");
            animal.setSpecies("DOG");
            animal.setStatus("IN_SHELTER");
            when(animalService.findByIdOrThrow(id)).thenReturn(animal);

            ResponseEntity<com.skillstorm.animalshelter.dtos.response.AnimalResponse> result = controller.getById(id);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody()).isNotNull();
            assertThat(result.getBody().getName()).isEqualTo("Buddy");
        }

        @Test
        @DisplayName("throws when animal not available for adoption")
        void throwsWhenNotAvailable() {
            UUID id = UUID.randomUUID();
            Animal animal = new Animal();
            animal.setId(id);
            animal.setStatus("ADOPTED");
            when(animalService.findByIdOrThrow(id)).thenReturn(animal);

            assertThatThrownBy(() -> controller.getById(id))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("not available");
        }
    }

    @Nested
    @DisplayName("GET /api/animals/{id}/photos - getPhotos")
    class GetPhotos {

        @Test
        @DisplayName("returns 200 and list of photos")
        void returns200AndPhotos() {
            UUID animalId = UUID.randomUUID();
            UUID photoId = UUID.randomUUID();
            AnimalPhoto photo = new AnimalPhoto();
            photo.setId(photoId);
            photo.setAnimalId(animalId);
            photo.setS3Key("key");
            photo.setUrl("https://example.com/photo.jpg");
            when(animalPhotoService.findByAnimalId(animalId)).thenReturn(List.of(photo));
            when(s3Service.generatePresignedGetUrl("key")).thenReturn(null);

            ResponseEntity<List<com.skillstorm.animalshelter.dtos.response.AnimalPhotoResponse>> result =
                    controller.getPhotos(animalId);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody()).hasSize(1);
            assertThat(result.getBody().get(0).getUrl()).isEqualTo("https://example.com/photo.jpg");
        }

        @Test
        @DisplayName("uses presigned URL when S3 returns one")
        void usesPresignedUrlWhenS3ReturnsOne() {
            UUID animalId = UUID.randomUUID();
            AnimalPhoto photo = new AnimalPhoto();
            photo.setId(UUID.randomUUID());
            photo.setAnimalId(animalId);
            photo.setS3Key("photos/1.jpg");
            photo.setUrl("https://fallback.com/old.jpg");
            when(animalPhotoService.findByAnimalId(animalId)).thenReturn(List.of(photo));
            when(s3Service.generatePresignedGetUrl("photos/1.jpg")).thenReturn("https://s3-presigned.example.com/signed");

            ResponseEntity<List<com.skillstorm.animalshelter.dtos.response.AnimalPhotoResponse>> result =
                    controller.getPhotos(animalId);

            assertThat(result.getBody().get(0).getUrl()).isEqualTo("https://s3-presigned.example.com/signed");
        }
    }
}
