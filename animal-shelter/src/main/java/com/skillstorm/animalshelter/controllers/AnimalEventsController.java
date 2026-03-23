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
import org.springframework.web.bind.annotation.RestController;

import com.skillstorm.animalshelter.dtos.response.AnimalEventResponse;
import com.skillstorm.animalshelter.models.AnimalEvent;
import com.skillstorm.animalshelter.services.AnimalEventService;

@RestController
@RequestMapping("/api/animals")
public class AnimalEventsController {

    private static final Logger log = LoggerFactory.getLogger(AnimalEventsController.class);

    private final AnimalEventService animalEventService;

    public AnimalEventsController(AnimalEventService animalEventService) {
        this.animalEventService = animalEventService;
    }

    @GetMapping("/{id}/events")
    public ResponseEntity<List<AnimalEventResponse>> getEvents(@PathVariable UUID id) {
        log.info("Fetching events for animal id={}", id);
        List<AnimalEvent> events = animalEventService.findByAnimalIdOrderByOccurredAtDesc(id);
        return ResponseEntity.ok(events.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    private AnimalEventResponse toResponse(AnimalEvent e) {
        AnimalEventResponse r = new AnimalEventResponse();
        r.setId(e.getId());
        r.setAnimalId(e.getAnimalId());
        r.setEventType(e.getEventType());
        r.setFromShelterId(e.getFromShelterId());
        r.setToShelterId(e.getToShelterId());
        r.setFromFosterUserId(e.getFromFosterUserId());
        r.setToFosterUserId(e.getToFosterUserId());
        r.setPerformedByUserId(e.getPerformedByUserId());
        r.setNotes(e.getNotes());
        r.setOccurredAt(e.getOccurredAt());
        return r;
    }
}
