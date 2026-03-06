package com.skillstorm.animalshelter.controllers;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skillstorm.animalshelter.dtos.request.ApproveApplicationRequest;
import com.skillstorm.animalshelter.dtos.request.DenyApplicationRequest;
import com.skillstorm.animalshelter.dtos.response.AdoptionApplicationResponse;
import com.skillstorm.animalshelter.models.AdoptionApplication;
import com.skillstorm.animalshelter.services.AdoptionApplicationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/staff/applications")
public class StaffApplicationsController {

    private final AdoptionApplicationService applicationService;

    public StaffApplicationsController(AdoptionApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping
    public ResponseEntity<List<AdoptionApplicationResponse>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID animalId,
            @RequestParam(required = false) String adopterEmail) {
        List<AdoptionApplication> list = applicationService.findAllStaff(status, animalId, adopterEmail);
        return ResponseEntity.ok(list.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdoptionApplicationResponse> getById(@PathVariable UUID id) {
        AdoptionApplication app = applicationService.findByIdOrThrow(id);
        return ResponseEntity.ok(toResponse(app));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<AdoptionApplicationResponse> approve(@PathVariable UUID id, @RequestHeader(name = "X-User-Id", required = false) UUID staffUserId, @Valid @RequestBody(required = false) ApproveApplicationRequest req) {
        String notes = req != null ? req.getDecisionNotes() : null;
        AdoptionApplication app = applicationService.approve(id, staffUserId != null ? staffUserId : UUID.randomUUID(), notes);
        return ResponseEntity.ok(toResponse(app));
    }

    @PostMapping("/{id}/deny")
    public ResponseEntity<AdoptionApplicationResponse> deny(@PathVariable UUID id, @RequestHeader(name = "X-User-Id", required = false) UUID staffUserId, @Valid @RequestBody(required = false) DenyApplicationRequest req) {
        String notes = req != null ? req.getDecisionNotes() : null;
        AdoptionApplication app = applicationService.deny(id, staffUserId != null ? staffUserId : UUID.randomUUID(), notes);
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
