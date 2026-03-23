package com.skillstorm.animalshelter.controllers;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.skillstorm.animalshelter.dtos.request.CreateAdoptionRequest;
import com.skillstorm.animalshelter.dtos.response.AdoptionResponse;
import com.skillstorm.animalshelter.models.Adoption;
import com.skillstorm.animalshelter.services.AdoptionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/staff/adoptions")
public class StaffAdoptionsController {

    private static final Logger log = LoggerFactory.getLogger(StaffAdoptionsController.class);

    private final AdoptionService adoptionService;

    public StaffAdoptionsController(AdoptionService adoptionService) {
        this.adoptionService = adoptionService;
    }

    @PostMapping
    public ResponseEntity<AdoptionResponse> create(Authentication authentication, @Valid @RequestBody CreateAdoptionRequest req) {
        UUID staffUserId = currentUserId(authentication);
        Adoption adoption = adoptionService.finalizeAdoption(
                req.getApplicationId(),
                staffUserId,
                null);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(adoption));
    }

    @GetMapping
    public ResponseEntity<List<AdoptionResponse>> list() {
        List<Adoption> list = adoptionService.findAll();
        return ResponseEntity.ok(list.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdoptionResponse> getById(@PathVariable UUID id) {
        Adoption adoption = adoptionService.findByIdOrThrow(id);
        return ResponseEntity.ok(toResponse(adoption));
    }

    private AdoptionResponse toResponse(Adoption a) {
        AdoptionResponse r = new AdoptionResponse();
        r.setId(a.getId());
        r.setAnimalId(a.getAnimalId());
        r.setAdopterUserId(a.getAdopterUserId());
        r.setApplicationId(a.getApplicationId());
        r.setAdoptedAt(a.getAdoptedAt());
        r.setFinalizedByUserId(a.getFinalizedByUserId());
        r.setNotes(a.getNotes());
        return r;
    }

    private UUID currentUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UUID userId) {
            return userId;
        }
        log.warn("Staff adoptions endpoint accessed without valid authentication");
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
    }
}
