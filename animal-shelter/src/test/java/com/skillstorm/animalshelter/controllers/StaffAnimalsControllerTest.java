package com.skillstorm.animalshelter.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.server.ResponseStatusException;

import com.skillstorm.animalshelter.dtos.request.CreateAnimalRequest;
import com.skillstorm.animalshelter.dtos.request.UpdateAnimalRequest;
import com.skillstorm.animalshelter.dtos.response.AnimalResponse;
import com.skillstorm.animalshelter.models.Animal;
import com.skillstorm.animalshelter.services.AnimalPhotoService;
import com.skillstorm.animalshelter.services.AnimalService;
import com.skillstorm.animalshelter.services.S3Service;

@ExtendWith(MockitoExtension.class)
class StaffAnimalsControllerTest {

    @Mock
    private AnimalService animalService;
    @Mock
    private AnimalPhotoService animalPhotoService;
    @Mock
    private S3Service s3Service;

    private StaffAnimalsController controller;
    private UUID staffId;
    private Authentication staffAuth;

    @BeforeEach
    void setUp() {
        controller = new StaffAnimalsController(animalService, animalPhotoService, s3Service);
        staffId = UUID.randomUUID();
        staffAuth = new UsernamePasswordAuthenticationToken(staffId, null, List.of(new SimpleGrantedAuthority("ROLE_STAFF")));
    }

    @Test
    void listReturns200() {
        Animal animal = new Animal();
        animal.setId(UUID.randomUUID());
        animal.setName("Buddy");
        animal.setStatus("IN_SHELTER");
        when(animalService.findAllStaff(null, null, null, null, null, null, null)).thenReturn(List.of(animal));

        ResponseEntity<List<AnimalResponse>> response = controller.list(null, null, null, null, null, null, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void updateRequiresAuthentication() {
        UpdateAnimalRequest req = new UpdateAnimalRequest();
        req.setName("Updated");

        assertThatThrownBy(() -> controller.update(UUID.randomUUID(), null, req))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("401");
    }

    @Test
    void createReturnsCreated() {
        CreateAnimalRequest req = new CreateAnimalRequest();
        req.setName("Buddy");
        req.setSpecies("DOG");
        req.setStatus("IN_SHELTER");
        req.setGoodWithKids(true);
        req.setGoodWithOtherPets(true);
        req.setMedicallyComplex(false);

        Animal created = new Animal();
        created.setId(UUID.randomUUID());
        created.setName("Buddy");
        created.setSpecies("DOG");
        created.setStatus("IN_SHELTER");
        when(animalService.create(any(CreateAnimalRequest.class))).thenReturn(created);

        ResponseEntity<AnimalResponse> response = controller.create(req);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Buddy");
    }

    @Test
    void updateUsesStaffAuth() {
        UUID animalId = UUID.randomUUID();
        UpdateAnimalRequest req = new UpdateAnimalRequest();
        req.setStatus("ON_HOLD");
        Animal updated = new Animal();
        updated.setId(animalId);
        updated.setStatus("ON_HOLD");
        when(animalService.update(animalId, req, staffId)).thenReturn(updated);

        ResponseEntity<AnimalResponse> response = controller.update(animalId, staffAuth, req);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("ON_HOLD");
    }
}
