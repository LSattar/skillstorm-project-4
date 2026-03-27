package com.skillstorm.animalshelter.services;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skillstorm.animalshelter.dtos.request.CreateAnimalRequest;
import com.skillstorm.animalshelter.dtos.request.UpdateAnimalRequest;
import com.skillstorm.animalshelter.exceptions.ResourceNotFoundException;
import com.skillstorm.animalshelter.models.Animal;
import com.skillstorm.animalshelter.repositories.AdoptionApplicationRepository;
import com.skillstorm.animalshelter.repositories.AnimalRepository;

@Service
public class AnimalService {

    private static final Logger log = LoggerFactory.getLogger(AnimalService.class);
    private static final List<String> AVAILABLE_STATUSES = Arrays.asList("IN_SHELTER", "IN_FOSTER");
    private static final ZoneId INTAKE_ZONE = ZoneId.of("UTC");

    private final AnimalRepository animalRepository;
    private final AnimalEventService animalEventService;
    private final AdoptionApplicationRepository adoptionApplicationRepository;

    public AnimalService(AnimalRepository animalRepository,
                         AnimalEventService animalEventService,
                         AdoptionApplicationRepository adoptionApplicationRepository) {
        this.animalRepository = animalRepository;
        this.animalEventService = animalEventService;
        this.adoptionApplicationRepository = adoptionApplicationRepository;
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
    public List<Animal> findAllStaff(String status, String species, Long shelterId, UUID fosterId,
                                       Boolean medicallyComplex, LocalDate intakeDate, String adoptionStatus) {
        List<Animal> list = new ArrayList<>(animalRepository.findAll());
        if (status != null && !status.isBlank()) {
            list.removeIf(a -> !status.equals(a.getStatus()));
        }
        if (species != null && !species.isBlank()) {
            list.removeIf(a -> a.getSpecies() == null || !species.equalsIgnoreCase(a.getSpecies()));
        }
        if (shelterId != null) {
            list.removeIf(a -> !shelterId.equals(a.getCurrentShelterId()));
        }
        if (fosterId != null) {
            list.removeIf(a -> !fosterId.equals(a.getCurrentFosterUserId()));
        }
        if (medicallyComplex != null) {
            list.removeIf(a -> !medicallyComplex.equals(Boolean.TRUE.equals(a.getMedicallyComplex())));
        }
        if (intakeDate != null) {
            list.removeIf(a -> a.getCreatedAt() == null
                    || !intakeDate.equals(a.getCreatedAt().atZone(INTAKE_ZONE).toLocalDate()));
        }
        if (adoptionStatus != null && !adoptionStatus.isBlank()) {
            Set<UUID> animalIds = new HashSet<>(adoptionApplicationRepository.findDistinctAnimalIdsByStatus(adoptionStatus.trim()));
            list.removeIf(a -> !animalIds.contains(a.getId()));
        }
        return list;
    }

    @Transactional
    public Animal update(UUID id, UpdateAnimalRequest req, UUID performedByUserId) {
        Animal animal = findByIdOrThrow(id);
        String previousStatus = animal.getStatus();
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
        if (req.getStatus() != null && !req.getStatus().equals(previousStatus)) {
            animalEventService.recordEvent(id, "STATUS_CHANGE", null, null, null, null, performedByUserId,
                    "Status updated via animal edit", Instant.now());
        }
        log.info("Updated animal id={}", id);
        return animal;
    }

    @Transactional
    public void softDelete(UUID id, UUID performedByUserId) {
        Animal animal = findByIdOrThrow(id);
        String previousStatus = animal.getStatus();
        animal.setStatus("INACTIVE");
        animal.setCurrentShelterId(null);
        animal.setCurrentFosterUserId(null);
        animal.setUpdatedAt(Instant.now());
        animalRepository.save(animal);
        if (!"INACTIVE".equals(previousStatus)) {
            animalEventService.recordEvent(id, "STATUS_CHANGE", null, null, null, null, performedByUserId,
                    "Soft delete: INACTIVE", Instant.now());
        }
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
        if ("ADOPTED".equals(previousStatus) && !"ADOPTED".equals(status)) {
            throw new IllegalArgumentException("Invalid status transition from ADOPTED to " + status);
        }
        animal.setStatus(status);
        animal.setUpdatedAt(Instant.now());
        animal = animalRepository.save(animal);
        animalEventService.recordEvent(animalId, "STATUS_CHANGE", null, null, null, null, performedByUserId, notes, Instant.now());
        log.info("Animal id={} status changed from {} to {}", animalId, previousStatus, status);
        return animal;
    }
}
