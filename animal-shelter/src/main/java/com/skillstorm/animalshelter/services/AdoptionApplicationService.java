package com.skillstorm.animalshelter.services;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skillstorm.animalshelter.dtos.request.CreateApplicationRequest;
import com.skillstorm.animalshelter.exceptions.ConflictException;
import com.skillstorm.animalshelter.exceptions.ResourceNotFoundException;
import com.skillstorm.animalshelter.models.AdoptionApplication;
import com.skillstorm.animalshelter.models.Animal;
import com.skillstorm.animalshelter.repositories.AdoptionApplicationRepository;
import com.skillstorm.animalshelter.repositories.AnimalRepository;
import com.skillstorm.animalshelter.repositories.UserRepository;

@Service
public class AdoptionApplicationService {

    private static final Logger log = LoggerFactory.getLogger(AdoptionApplicationService.class);
    private static final List<String> ACTIVE_STATUSES = List.of("SUBMITTED", "IN_REVIEW", "APPROVED");

    private final AdoptionApplicationRepository applicationRepository;
    private final AnimalRepository animalRepository;
    private final UserRepository userRepository;
    private final AnimalEventService animalEventService;
    private final AdopterQuestionnaireService adopterQuestionnaireService;

    public AdoptionApplicationService(AdoptionApplicationRepository applicationRepository,
                                      AnimalRepository animalRepository,
                                      UserRepository userRepository,
                                      AnimalEventService animalEventService,
                                      AdopterQuestionnaireService adopterQuestionnaireService) {
        this.applicationRepository = applicationRepository;
        this.animalRepository = animalRepository;
        this.userRepository = userRepository;
        this.animalEventService = animalEventService;
        this.adopterQuestionnaireService = adopterQuestionnaireService;
    }

    @Transactional
    public AdoptionApplication create(UUID adopterUserId, CreateApplicationRequest req) {
        Animal animal = animalRepository.findById(req.getAnimalId()).orElse(null);
        if (animal == null) {
            log.error("Animal not found for application, animalId={}", req.getAnimalId());
            throw new ResourceNotFoundException("Animal not found: " + req.getAnimalId());
        }
        if ("ADOPTED".equals(animal.getStatus())) {
            log.error("Cannot create application for adopted animal, animalId={}", req.getAnimalId());
            throw new ConflictException("Cannot apply for an adopted animal");
        }
        if (!userRepository.existsById(adopterUserId)) {
            log.error("User not found for application, adopterUserId={}", adopterUserId);
            throw new ResourceNotFoundException("User not found: " + adopterUserId);
        }
        boolean duplicate = applicationRepository.findByAdopterUserId(adopterUserId).stream()
                .anyMatch(app -> req.getAnimalId().equals(app.getAnimalId()) && ACTIVE_STATUSES.contains(app.getStatus()));
        if (duplicate) {
            log.error("Duplicate application: adopter id={}, animal id={}", adopterUserId, req.getAnimalId());
            throw new ConflictException("An active application already exists for this animal");
        }

        String snapshotJson;
        if (req.getQuestionnaireAnswers() != null) {
            adopterQuestionnaireService.upsert(adopterUserId, req.getQuestionnaireAnswers());
            snapshotJson = adopterQuestionnaireService.buildQuestionnaireSnapshotJson(adopterUserId);
        } else if (req.getQuestionnaireSnapshotJson() != null && !req.getQuestionnaireSnapshotJson().isBlank()) {
            snapshotJson = req.getQuestionnaireSnapshotJson();
        } else {
            snapshotJson = adopterQuestionnaireService.buildQuestionnaireSnapshotJson(adopterUserId);
        }

        AdoptionApplication app = new AdoptionApplication();
        app.setId(UUID.randomUUID());
        app.setAnimalId(req.getAnimalId());
        app.setAdopterUserId(adopterUserId);
        app.setStatus("SUBMITTED");
        app.setQuestionnaireSnapshotJson(snapshotJson);
        Instant now = Instant.now();
        app.setCreatedAt(now);
        app.setUpdatedAt(now);
        app = applicationRepository.save(app);
        animalEventService.recordEvent(req.getAnimalId(), "APPLICATION_SUBMITTED", null, null, null, null, adopterUserId, null, now);
        log.info("Submitted application id={} for animal id={} by user id={}", app.getId(), req.getAnimalId(), adopterUserId);
        return app;
    }

    @Transactional(readOnly = true)
    public AdoptionApplication findByIdOrThrow(UUID id) {
        return applicationRepository.findById(id).orElseThrow(() -> {
            log.error("Adoption application not found for id={}", id);
            return new ResourceNotFoundException("Adoption application not found: " + id);
        });
    }

    @Transactional(readOnly = true)
    public List<AdoptionApplication> findByAdopterUserId(UUID adopterUserId) {
        return applicationRepository.findByAdopterUserId(adopterUserId);
    }

    @Transactional(readOnly = true)
    public List<AdoptionApplication> findAllStaff(String status, UUID animalId, String adopterEmail) {
        List<AdoptionApplication> list = applicationRepository.findAll();
        if (status != null && !status.isBlank()) {
            list = list.stream().filter(a -> status.equals(a.getStatus())).collect(Collectors.toList());
        }
        if (animalId != null) {
            list = list.stream().filter(a -> animalId.equals(a.getAnimalId())).collect(Collectors.toList());
        }
        if (adopterEmail != null && !adopterEmail.isBlank()) {
            list = list.stream().filter(a -> userRepository.findById(a.getAdopterUserId())
                    .map(u -> adopterEmail.equalsIgnoreCase(u.getEmail())).orElse(false)).collect(Collectors.toList());
        }
        return list;
    }

    @Transactional
    public AdoptionApplication approve(UUID applicationId, UUID staffUserId, String decisionNotes) {
        AdoptionApplication app = findByIdOrThrow(applicationId);
        app.setStatus("APPROVED");
        app.setStaffReviewerUserId(staffUserId);
        app.setDecisionNotes(decisionNotes);
        app.setUpdatedAt(Instant.now());
        app = applicationRepository.save(app);
        UUID animalId = app.getAnimalId();

        Animal animal = animalRepository.findById(animalId).orElseThrow(() -> {
            log.error("Animal missing on approve, id={}", animalId);
            return new ResourceNotFoundException("Animal not found: " + animalId);
        });
        animal.setStatus("ADOPTION_PENDING");
        animal.setUpdatedAt(Instant.now());
        animalRepository.save(animal);

        animalEventService.recordEvent(app.getAnimalId(), "APPLICATION_APPROVED", null, null, null, null, staffUserId, decisionNotes, Instant.now());
        log.info("Application id={} approved by user id={}", applicationId, staffUserId);
        return app;
    }

    @Transactional
    public AdoptionApplication deny(UUID applicationId, UUID staffUserId, String decisionNotes) {
        AdoptionApplication app = findByIdOrThrow(applicationId);
        UUID animalId = app.getAnimalId();

        Animal animal = animalRepository.findById(animalId).orElseThrow(() -> {
            log.error("Animal missing on deny, id={}", animalId);
            return new ResourceNotFoundException("Animal not found: " + animalId);
        });
        if ("ADOPTION_PENDING".equals(animal.getStatus())) {
            if (animal.getCurrentFosterUserId() != null) {
                animal.setStatus("IN_FOSTER");
            } else {
                animal.setStatus("IN_SHELTER");
            }
            animal.setUpdatedAt(Instant.now());
            animalRepository.save(animal);
        }

        app.setStatus("DENIED");
        app.setStaffReviewerUserId(staffUserId);
        app.setDecisionNotes(decisionNotes);
        app.setUpdatedAt(Instant.now());
        app = applicationRepository.save(app);
        animalEventService.recordEvent(app.getAnimalId(), "APPLICATION_DENIED", null, null, null, null, staffUserId, decisionNotes, Instant.now());
        log.info("Application id={} denied by user id={}", applicationId, staffUserId);
        return app;
    }

    @Transactional
    public AdoptionApplication withdraw(UUID applicationId, UUID adopterUserId) {
        AdoptionApplication app = findByIdOrThrow(applicationId);
        if (!adopterUserId.equals(app.getAdopterUserId())) {
            log.error("Withdraw forbidden: app id={} not owned by user id={}", applicationId, adopterUserId);
            throw new ResourceNotFoundException("Application not found: " + applicationId);
        }
        if (!"SUBMITTED".equals(app.getStatus()) && !"IN_REVIEW".equals(app.getStatus())) {
            throw new ConflictException("Application cannot be withdrawn in its current state");
        }
        app.setStatus("WITHDRAWN");
        app.setUpdatedAt(Instant.now());
        app = applicationRepository.save(app);
        log.info("Application id={} withdrawn by user id={}", applicationId, adopterUserId);
        return app;
    }
}
