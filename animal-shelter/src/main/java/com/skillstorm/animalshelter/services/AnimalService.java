package com.skillstorm.animalshelter.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skillstorm.animalshelter.dtos.request.CreateAnimalRequest;
import com.skillstorm.animalshelter.dtos.request.UpdateAnimalRequest;
import com.skillstorm.animalshelter.exceptions.ResourceNotFoundException;
import com.skillstorm.animalshelter.models.Animal;
import com.skillstorm.animalshelter.repositories.AnimalRepository;

@Service
public class AnimalService {

    private static final Logger log = LoggerFactory.getLogger(AnimalService.class);
    private static final List<String> AVAILABLE_STATUSES = Arrays.asList("IN_SHELTER", "IN_FOSTER");

    private final AnimalRepository animalRepository;
    private final AnimalEventService animalEventService;

    public AnimalService(AnimalRepository animalRepository, AnimalEventService animalEventService) {
        this.animalRepository = animalRepository;
        this.animalEventService = animalEventService;
    }

    @Transactional
    public Animal create(CreateAnimalRequest req) {
        Animal animal = new Animal();
        animal.setId(UUID.randomUUID());
        animal.setName(req.getName());
        animal.setSpecies(req.getSpecies());
        animal.setBreed(req.getBreed());
        animal.setSex(req.getSex());
        animal.setAgeMonths(req.getAgeMonths());
        animal.setGoodWithKids(req.getGoodWithKids() != null ? req.getGoodWithKids() : false);
        animal.setGoodWithOtherPets(req.getGoodWithOtherPets() != null ? req.getGoodWithOtherPets() : false);
        animal.setMedicallyComplex(req.getMedicallyComplex() != null ? req.getMedicallyComplex() : false);
        animal.setDescription(req.getDescription());
        animal.setStatus(req.getStatus());
        animal.setCurrentShelterId(req.getCurrentShelterId());
        animal.setCurrentFosterUserId(req.getCurrentFosterUserId());
        Instant now = Instant.now();
        animal.setCreatedAt(now);
        animal.setUpdatedAt(now);
        animal = animalRepository.save(animal);
        animalEventService.recordEvent(animal.getId(), "INTAKE", null, req.getCurrentShelterId(), null, req.getCurrentFosterUserId(), null, null, now);
        log.info("Created animal id={}, name={}, status={}", animal.getId(), animal.getName(), animal.getStatus());
        return animal;
    }

    @Transactional(readOnly = true)
    public Optional<Animal> findById(UUID id) {
        return animalRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Animal findByIdOrThrow(UUID id) {
        return animalRepository.findById(id).orElseThrow(() -> {
            log.error("Animal not found for id={}", id);
            return new ResourceNotFoundException("Animal not found: " + id);
        });
    }

    @Transactional(readOnly = true)
    public List<Animal> findAvailableForAdoption(String species, Integer ageMin, Integer ageMax,
                                                  Boolean goodWithKids, Boolean goodWithOtherPets, Boolean medicallyComplex) {
        List<Animal> list = new ArrayList<>(animalRepository.findByStatusIn(AVAILABLE_STATUSES));
        if (species != null && !species.isBlank()) list.removeIf(a -> !species.equalsIgnoreCase(a.getSpecies()));
        if (ageMin != null) list.removeIf(a -> a.getAgeMonths() == null || a.getAgeMonths() < ageMin);
        if (ageMax != null) list.removeIf(a -> a.getAgeMonths() == null || a.getAgeMonths() > ageMax);
        if (goodWithKids != null) list.removeIf(a -> !Boolean.TRUE.equals(a.getGoodWithKids()) == goodWithKids);
        if (goodWithOtherPets != null) list.removeIf(a -> !Boolean.TRUE.equals(a.getGoodWithOtherPets()) == goodWithOtherPets);
        if (medicallyComplex != null) list.removeIf(a -> !Boolean.TRUE.equals(a.getMedicallyComplex()) == medicallyComplex);
        return list;
    }

    @Transactional(readOnly = true)
    public List<Animal> findAllStaff(String status, String species, Long shelterId, UUID fosterId) {
        if (status != null && !status.isBlank()) {
            return animalRepository.findByStatusIn(List.of(status));
        }
        if (shelterId != null) {
            return animalRepository.findByCurrentShelterId(shelterId);
        }
        if (fosterId != null) {
            return animalRepository.findByCurrentFosterUserId(fosterId);
        }
        if (species != null && !species.isBlank()) {
            List<Animal> all = new ArrayList<>(animalRepository.findAll());
            all.removeIf(a -> !species.equalsIgnoreCase(a.getSpecies()));
            return all;
        }
        return animalRepository.findAll();
    }

    @Transactional
    public Animal update(UUID id, UpdateAnimalRequest req) {
        Animal animal = findByIdOrThrow(id);
        if (req.getName() != null) animal.setName(req.getName());
        if (req.getSpecies() != null) animal.setSpecies(req.getSpecies());
        if (req.getBreed() != null) animal.setBreed(req.getBreed());
        if (req.getSex() != null) animal.setSex(req.getSex());
        if (req.getAgeMonths() != null) animal.setAgeMonths(req.getAgeMonths());
        if (req.getGoodWithKids() != null) animal.setGoodWithKids(req.getGoodWithKids());
        if (req.getGoodWithOtherPets() != null) animal.setGoodWithOtherPets(req.getGoodWithOtherPets());
        if (req.getMedicallyComplex() != null) animal.setMedicallyComplex(req.getMedicallyComplex());
        if (req.getDescription() != null) animal.setDescription(req.getDescription());
        if (req.getStatus() != null) animal.setStatus(req.getStatus());
        if (req.getCurrentShelterId() != null) animal.setCurrentShelterId(req.getCurrentShelterId());
        if (req.getCurrentFosterUserId() != null) animal.setCurrentFosterUserId(req.getCurrentFosterUserId());
        animal.setUpdatedAt(Instant.now());
        animal = animalRepository.save(animal);
        log.info("Updated animal id={}", id);
        return animal;
    }

    @Transactional
    public void softDelete(UUID id) {
        Animal animal = findByIdOrThrow(id);
        animal.setStatus("INACTIVE");
        animal.setCurrentShelterId(null);
        animal.setCurrentFosterUserId(null);
        animal.setUpdatedAt(Instant.now());
        animalRepository.save(animal);
        log.info("Soft deleted animal id={}, status set to INACTIVE", id);
    }

    @Transactional
    public Animal moveToShelter(UUID animalId, Long toShelterId, String notes, UUID performedByUserId) {
        Animal animal = findByIdOrThrow(animalId);
        Long fromShelterId = animal.getCurrentShelterId();
        UUID fromFosterUserId = animal.getCurrentFosterUserId();
        animal.setCurrentShelterId(toShelterId);
        animal.setCurrentFosterUserId(null);
        animal.setUpdatedAt(Instant.now());
        animal = animalRepository.save(animal);
        animalEventService.recordEvent(animalId, "SHELTER_MOVE", fromShelterId, toShelterId, fromFosterUserId, null, performedByUserId, notes, Instant.now());
        log.info("Animal id={} moved to shelter id={}", animalId, toShelterId);
        return animal;
    }

    @Transactional
    public Animal moveToFoster(UUID animalId, UUID toFosterUserId, String notes, UUID performedByUserId) {
        Animal animal = findByIdOrThrow(animalId);
        Long fromShelterId = animal.getCurrentShelterId();
        UUID fromFosterUserId = animal.getCurrentFosterUserId();
        animal.setCurrentShelterId(null);
        animal.setCurrentFosterUserId(toFosterUserId);
        animal.setUpdatedAt(Instant.now());
        animal = animalRepository.save(animal);
        animalEventService.recordEvent(animalId, "FOSTER_MOVE", fromShelterId, null, fromFosterUserId, toFosterUserId, performedByUserId, notes, Instant.now());
        log.info("Animal id={} moved to foster user id={}", animalId, toFosterUserId);
        return animal;
    }

    @Transactional
    public Animal updateStatus(UUID animalId, String status, String notes, UUID performedByUserId) {
        Animal animal = findByIdOrThrow(animalId);
        String previousStatus = animal.getStatus();
        animal.setStatus(status);
        animal.setUpdatedAt(Instant.now());
        animal = animalRepository.save(animal);
        animalEventService.recordEvent(animalId, "STATUS_CHANGE", null, null, null, null, performedByUserId, notes, Instant.now());
        log.info("Animal id={} status changed from {} to {}", animalId, previousStatus, status);
        return animal;
    }
}
