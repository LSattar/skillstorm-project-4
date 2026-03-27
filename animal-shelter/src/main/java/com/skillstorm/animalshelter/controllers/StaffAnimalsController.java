package com.skillstorm.animalshelter.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.skillstorm.animalshelter.dtos.request.CreateAnimalPhotoRequest;
import com.skillstorm.animalshelter.dtos.request.CreateAnimalRequest;
import com.skillstorm.animalshelter.dtos.request.MoveToFosterRequest;
import com.skillstorm.animalshelter.dtos.request.MoveToShelterRequest;
import com.skillstorm.animalshelter.dtos.request.UpdateAnimalRequest;
import com.skillstorm.animalshelter.dtos.request.UpdateStatusRequest;
import com.skillstorm.animalshelter.dtos.response.AnimalPhotoResponse;
import com.skillstorm.animalshelter.dtos.response.AnimalResponse;
import com.skillstorm.animalshelter.models.Animal;
import com.skillstorm.animalshelter.models.AnimalPhoto;
import com.skillstorm.animalshelter.services.AnimalPhotoService;
import com.skillstorm.animalshelter.services.AnimalService;
import com.skillstorm.animalshelter.services.S3Service;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/staff/animals")
public class StaffAnimalsController {

    private static final Logger log = LoggerFactory.getLogger(StaffAnimalsController.class);

    private final AnimalService animalService;
    private final AnimalPhotoService animalPhotoService;
    private final S3Service s3Service;

    public StaffAnimalsController(AnimalService animalService, AnimalPhotoService animalPhotoService, S3Service s3Service) {
        this.animalService = animalService;
        this.animalPhotoService = animalPhotoService;
        this.s3Service = s3Service;
    }

    @GetMapping
    public ResponseEntity<List<AnimalResponse>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String species,
            @RequestParam(required = false) Long shelterId,
            @RequestParam(required = false) UUID fosterId,
            @RequestParam(required = false) Boolean medicallyComplex,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate intakeDate,
            @RequestParam(required = false) String adoptionStatus) {
        List<Animal> list = animalService.findAllStaff(status, species, shelterId, fosterId, medicallyComplex, intakeDate, adoptionStatus);
        return ResponseEntity.ok(list.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<AnimalResponse> create(@Valid @RequestBody CreateAnimalRequest req) {
        Animal animal = animalService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(animal));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnimalResponse> getById(@PathVariable UUID id) {
        Animal animal = animalService.findByIdOrThrow(id);
        return ResponseEntity.ok(toResponse(animal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnimalResponse> update(@PathVariable UUID id, Authentication authentication, @Valid @RequestBody UpdateAnimalRequest req) {
        UUID staffUserId = currentUserId(authentication);
        Animal animal = animalService.update(id, req, staffUserId);
        return ResponseEntity.ok(toResponse(animal));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, Authentication authentication) {
        UUID staffUserId = currentUserId(authentication);
        animalService.softDelete(id, staffUserId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/photos")
    public ResponseEntity<AnimalPhotoResponse> addPhoto(@PathVariable UUID id, @Valid @RequestBody CreateAnimalPhotoRequest req) {
        req.setAnimalId(id);
        AnimalPhoto photo = animalPhotoService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(toPhotoResponse(photo));
    }

    @DeleteMapping("/{id}/photos/{photoId}")
    public ResponseEntity<Void> deletePhoto(@PathVariable UUID id, @PathVariable UUID photoId) {
        animalPhotoService.delete(photoId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/move/shelter")
    public ResponseEntity<AnimalResponse> moveToShelter(@PathVariable UUID id, Authentication authentication, @Valid @RequestBody MoveToShelterRequest req) {
        UUID staffUserId = currentUserId(authentication);
        Animal animal = animalService.moveToShelter(id, req.getToShelterId(), req.getNotes(), staffUserId);
        return ResponseEntity.ok(toResponse(animal));
    }

    @PostMapping("/{id}/move/foster")
    public ResponseEntity<AnimalResponse> moveToFoster(@PathVariable UUID id, Authentication authentication, @Valid @RequestBody MoveToFosterRequest req) {
        UUID staffUserId = currentUserId(authentication);
        Animal animal = animalService.moveToFoster(id, req.getToFosterUserId(), req.getNotes(), staffUserId);
        return ResponseEntity.ok(toResponse(animal));
    }

    @PostMapping("/{id}/status")
    public ResponseEntity<AnimalResponse> updateStatus(@PathVariable UUID id, Authentication authentication, @Valid @RequestBody UpdateStatusRequest req) {
        UUID staffUserId = currentUserId(authentication);
        Animal animal = animalService.updateStatus(id, req.getStatus(), req.getNotes(), staffUserId);
        return ResponseEntity.ok(toResponse(animal));
    }

    private AnimalResponse toResponse(Animal a) {
        AnimalResponse r = new AnimalResponse();
        r.setId(a.getId());
        r.setName(a.getName());
        r.setSpecies(a.getSpecies());
        r.setBreed(a.getBreed());
        r.setSex(a.getSex());
        r.setAgeMonths(a.getAgeMonths());
        r.setGoodWithKids(a.getGoodWithKids());
        r.setGoodWithOtherPets(a.getGoodWithOtherPets());
        r.setMedicallyComplex(a.getMedicallyComplex());
        r.setDescription(a.getDescription());
        r.setStatus(a.getStatus());
        r.setCurrentShelterId(a.getCurrentShelterId());
        r.setCurrentFosterUserId(a.getCurrentFosterUserId());
        r.setCurrentShelterName(a.getCurrentShelter() != null ? a.getCurrentShelter().getName() : null);
        r.setPhotoUrl(resolvePrimaryPhotoUrl(a.getId()));
        r.setCreatedAt(a.getCreatedAt());
        r.setUpdatedAt(a.getUpdatedAt());
        return r;
    }

    private String resolvePrimaryPhotoUrl(UUID animalId) {
        List<AnimalPhoto> photos = animalPhotoService.findByAnimalId(animalId);
        if (photos == null || photos.isEmpty()) {
            return null;
        }

        AnimalPhoto preferred = photos.stream()
                .filter(p -> Boolean.TRUE.equals(p.getIsPrimary()))
                .findFirst()
                .orElse(photos.get(0));

        String displayUrl = s3Service.generatePresignedGetUrl(preferred.getS3Key());
        return displayUrl != null ? displayUrl : preferred.getUrl();
    }

    private AnimalPhotoResponse toPhotoResponse(AnimalPhoto p) {
        AnimalPhotoResponse r = new AnimalPhotoResponse();
        r.setId(p.getId());
        r.setAnimalId(p.getAnimalId());
        String displayUrl = s3Service.generatePresignedGetUrl(p.getS3Key());
        r.setUrl(displayUrl != null ? displayUrl : p.getUrl());
        r.setS3Key(p.getS3Key());
        r.setIsPrimary(p.getIsPrimary());
        r.setContentType(p.getContentType());
        r.setFileSizeBytes(p.getFileSizeBytes());
        r.setCreatedAt(p.getCreatedAt());
        return r;
    }

    private UUID currentUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UUID userId) {
            return userId;
        }
        log.warn("Staff animals endpoint accessed without valid authentication");
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
    }
}
