package com.skillstorm.animalshelter.services;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skillstorm.animalshelter.exceptions.ResourceNotFoundException;
import com.skillstorm.animalshelter.models.Animal;
import com.skillstorm.animalshelter.models.AnimalEvent;
import com.skillstorm.animalshelter.repositories.AnimalEventRepository;
import com.skillstorm.animalshelter.repositories.AnimalRepository;

@Service
public class AnimalEventService {

    private static final Logger log = LoggerFactory.getLogger(AnimalEventService.class);

    private final AnimalEventRepository animalEventRepository;
    private final AnimalRepository animalRepository;

    public AnimalEventService(AnimalEventRepository animalEventRepository, AnimalRepository animalRepository) {
        this.animalEventRepository = animalEventRepository;
        this.animalRepository = animalRepository;
    }

    @Transactional
    public AnimalEvent recordEvent(UUID animalId, String eventType, Long fromShelterId, Long toShelterId,
                                    UUID fromFosterUserId, UUID toFosterUserId, UUID performedByUserId,
                                    String notes, Instant occurredAt) {
        if (!animalRepository.existsById(animalId)) {
            log.error("Animal not found for event, animalId={}", animalId);
            throw new ResourceNotFoundException("Animal not found: " + animalId);
        }
        AnimalEvent event = new AnimalEvent();
        event.setId(UUID.randomUUID());
        event.setAnimalId(animalId);
        event.setEventType(eventType);
        event.setFromShelterId(fromShelterId);
        event.setToShelterId(toShelterId);
        event.setFromFosterUserId(fromFosterUserId);
        event.setToFosterUserId(toFosterUserId);
        event.setPerformedByUserId(performedByUserId);
        event.setNotes(notes);
        event.setOccurredAt(occurredAt != null ? occurredAt : Instant.now());
        event = animalEventRepository.save(event);
        log.info("Recorded animal event id={}, animalId={}, eventType={}", event.getId(), animalId, eventType);
        return event;
    }

    @Transactional(readOnly = true)
    public List<AnimalEvent> findByAnimalIdOrderByOccurredAtDesc(UUID animalId) {
        return animalEventRepository.findByAnimalIdOrderByOccurredAtDesc(animalId);
    }
}
