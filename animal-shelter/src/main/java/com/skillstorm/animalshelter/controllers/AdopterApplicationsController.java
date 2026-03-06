package com.skillstorm.animalshelter.controllers;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillstorm.animalshelter.dtos.request.CreateApplicationRequest;
import com.skillstorm.animalshelter.dtos.response.AdoptionApplicationResponse;
import com.skillstorm.animalshelter.models.AdoptionApplication;
import com.skillstorm.animalshelter.services.AdoptionApplicationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/adopter/applications")
public class AdopterApplicationsController {

    private final AdoptionApplicationService applicationService;

    public AdopterApplicationsController(AdoptionApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public ResponseEntity<AdoptionApplicationResponse> create(@RequestHeader(name = "X-User-Id", required = false) UUID currentUserId, @Valid @RequestBody CreateApplicationRequest req) {
        if (currentUserId == null) {
            throw new com.skillstorm.animalshelter.exceptions.ResourceNotFoundException("Not authenticated");
        }
        AdoptionApplication app = applicationService.create(currentUserId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(app));
    }

    @GetMapping
    public ResponseEntity<List<AdoptionApplicationResponse>> listOwn(@RequestHeader(name = "X-User-Id", required = false) UUID currentUserId) {
        if (currentUserId == null) {
            throw new com.skillstorm.animalshelter.exceptions.ResourceNotFoundException("Not authenticated");
        }
        List<AdoptionApplication> list = applicationService.findByAdopterUserId(currentUserId);
        return ResponseEntity.ok(list.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdoptionApplicationResponse> getById(@RequestHeader(name = "X-User-Id", required = false) UUID currentUserId, @PathVariable UUID id) {
        if (currentUserId == null) {
            throw new com.skillstorm.animalshelter.exceptions.ResourceNotFoundException("Not authenticated");
        }
        AdoptionApplication app = applicationService.findByIdOrThrow(id);
        if (!currentUserId.equals(app.getAdopterUserId())) {
            throw new com.skillstorm.animalshelter.exceptions.ResourceNotFoundException("Application not found: " + id);
        }
        return ResponseEntity.ok(toResponse(app));
    }

    private AdoptionApplicationResponse toResponse(AdoptionApplication a) {
        AdoptionApplicationResponse r = new AdoptionApplicationResponse();
        r.setId(a.getId());
        r.setAnimalId(a.getAnimalId());
        r.setAdopterUserId(a.getAdopterUserId());
        r.setStatus(a.getStatus());
        r.setQuestionnaireSnapshotJson(a.getQuestionnaireSnapshotJson());
        r.setStaffReviewerUserId(a.getStaffReviewerUserId());
        r.setDecisionNotes(a.getDecisionNotes());
        r.setCreatedAt(a.getCreatedAt());
        r.setUpdatedAt(a.getUpdatedAt());
        return r;
    }
}
