package com.skillstorm.animalshelter.services;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skillstorm.animalshelter.exceptions.ConflictException;
import com.skillstorm.animalshelter.exceptions.ResourceNotFoundException;
import com.skillstorm.animalshelter.models.Adoption;
import com.skillstorm.animalshelter.models.AdoptionApplication;
import com.skillstorm.animalshelter.models.Animal;
import com.skillstorm.animalshelter.repositories.AdoptionApplicationRepository;
import com.skillstorm.animalshelter.repositories.AdoptionRepository;
import com.skillstorm.animalshelter.repositories.AnimalRepository;

@Service
public class AdoptionService {

    private static final Logger log = LoggerFactory.getLogger(AdoptionService.class);

    private final AdoptionRepository adoptionRepository;
    private final AdoptionApplicationRepository applicationRepository;
    private final AnimalRepository animalRepository;
    private final AnimalEventService animalEventService;

    public AdoptionService(AdoptionRepository adoptionRepository,
                           AdoptionApplicationRepository applicationRepository,
                           AnimalRepository animalRepository,
                           AnimalEventService animalEventService) {
        this.adoptionRepository = adoptionRepository;
        this.applicationRepository = applicationRepository;
        this.animalRepository = animalRepository;
        this.animalEventService = animalEventService;
    }

    @Transactional
    public Adoption finalizeAdoption(UUID applicationId, UUID finalizedByUserId, String notes) {
        AdoptionApplication app = applicationRepository.findById(applicationId).orElseThrow(() -> {
            log.error("Application not found for adoption finalization, applicationId={}", applicationId);
            return new ResourceNotFoundException("Application not found: " + applicationId);
        });
        if (!"APPROVED".equals(app.getStatus())) {
            log.error("Application not approved, cannot finalize adoption, applicationId={}, status={}", applicationId, app.getStatus());
            throw new ConflictException("Application must be approved to finalize adoption");
        }
        if (adoptionRepository.findByApplicationId(applicationId).isPresent()) {
            log.error("Adoption already exists for application id={}", applicationId);
            throw new ConflictException("Adoption already recorded for this application");
        }
        Animal animal = animalRepository.findById(app.getAnimalId()).orElseThrow(() -> {
            log.error("Animal not found for adoption, animalId={}", app.getAnimalId());
            return new ResourceNotFoundException("Animal not found: " + app.getAnimalId());
        });
        if ("ADOPTED".equals(animal.getStatus())) {
            log.error("Animal already adopted, animalId={}", animal.getId());
            throw new ConflictException("Animal is already adopted");
        }
        Adoption adoption = new Adoption();
        adoption.setId(UUID.randomUUID());
        adoption.setAnimalId(app.getAnimalId());
        adoption.setAdopterUserId(app.getAdopterUserId());
        adoption.setApplicationId(applicationId);
        Instant now = Instant.now();
        adoption.setAdoptedAt(now);
        adoption.setFinalizedByUserId(finalizedByUserId);
        adoption.setNotes(notes);
        adoption = adoptionRepository.save(adoption);
        animal.setStatus("ADOPTED");
        animal.setCurrentShelterId(null);
        animal.setCurrentFosterUserId(null);
        animal.setUpdatedAt(now);
        animalRepository.save(animal);
        animalEventService.recordEvent(animal.getId(), "ADOPTED", null, null, null, null, finalizedByUserId, notes, now);
        log.info("Adoption finalized for application id={}, animal id={}", applicationId, animal.getId());
        return adoption;
    }

    @Transactional(readOnly = true)
    public Optional<Adoption> findById(UUID id) {
        return adoptionRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Adoption findByIdOrThrow(UUID id) {
        return adoptionRepository.findById(id).orElseThrow(() -> {
            log.error("Adoption not found for id={}", id);
            return new ResourceNotFoundException("Adoption not found: " + id);
        });
    }

    @Transactional(readOnly = true)
    public Optional<Adoption> findByApplicationId(UUID applicationId) {
        return adoptionRepository.findByApplicationId(applicationId);
    }

    @Transactional(readOnly = true)
    public List<Adoption> findAll() {
        return adoptionRepository.findAll();
    }
}
