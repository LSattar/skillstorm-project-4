package com.skillstorm.animalshelter.controllers;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skillstorm.animalshelter.dtos.response.AnimalPhotoResponse;
import com.skillstorm.animalshelter.dtos.response.AnimalResponse;
import com.skillstorm.animalshelter.models.Animal;
import com.skillstorm.animalshelter.models.AnimalPhoto;
import com.skillstorm.animalshelter.services.AnimalPhotoService;
import com.skillstorm.animalshelter.services.AnimalService;
import com.skillstorm.animalshelter.services.S3Service;

@RestController
@RequestMapping("/api/animals")
public class AnimalsController {

    private static final Logger log = LoggerFactory.getLogger(AnimalsController.class);

    private final AnimalService animalService;
    private final AnimalPhotoService animalPhotoService;
    private final S3Service s3Service;

    public AnimalsController(AnimalService animalService, AnimalPhotoService animalPhotoService, S3Service s3Service) {
        this.animalService = animalService;
        this.animalPhotoService = animalPhotoService;
        this.s3Service = s3Service;
    }

    @GetMapping
    public ResponseEntity<List<AnimalResponse>> listAvailable(
            @RequestParam(required = false) String species,
            @RequestParam(required = false) Integer ageMin,
            @RequestParam(required = false) Integer ageMax,
            @RequestParam(required = false) Boolean goodWithKids,
            @RequestParam(required = false) Boolean goodWithOtherPets,
            @RequestParam(required = false) Boolean medicallyComplex) {
        List<Animal> list = animalService.findAvailableForAdoption(species, ageMin, ageMax, goodWithKids, goodWithOtherPets, medicallyComplex);
        return ResponseEntity.ok(list.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnimalResponse> getById(@PathVariable UUID id) {
        Animal animal = animalService.findByIdOrThrow(id);
        if (!"IN_SHELTER".equals(animal.getStatus()) && !"IN_FOSTER".equals(animal.getStatus())) {
            log.warn("Public request for unavailable animal id={}, status={}", id, animal.getStatus());
            throw new com.skillstorm.animalshelter.exceptions.ResourceNotFoundException("Animal not found or not available: " + id);
        }
        return ResponseEntity.ok(toResponse(animal));
    }

    @GetMapping("/{id}/photos")
    public ResponseEntity<List<AnimalPhotoResponse>> getPhotos(@PathVariable UUID id) {
        List<AnimalPhoto> photos = animalPhotoService.findByAnimalId(id);
        return ResponseEntity.ok(photos.stream().map(this::toPhotoResponse).collect(Collectors.toList()));
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
}
