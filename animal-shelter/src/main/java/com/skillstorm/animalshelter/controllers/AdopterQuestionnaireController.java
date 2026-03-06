package com.skillstorm.animalshelter.controllers;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillstorm.animalshelter.dtos.request.UpsertQuestionnaireRequest;
import com.skillstorm.animalshelter.dtos.response.AdopterQuestionnaireResponse;
import com.skillstorm.animalshelter.models.AdopterQuestionnaire;
import com.skillstorm.animalshelter.services.AdopterQuestionnaireService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/adopter/questionnaire")
public class AdopterQuestionnaireController {

    private final AdopterQuestionnaireService questionnaireService;

    public AdopterQuestionnaireController(AdopterQuestionnaireService questionnaireService) {
        this.questionnaireService = questionnaireService;
    }

    @GetMapping
    public ResponseEntity<AdopterQuestionnaireResponse> getQuestionnaire(@RequestHeader(name = "X-User-Id", required = false) UUID currentUserId) {
        if (currentUserId == null) {
            throw new com.skillstorm.animalshelter.exceptions.ResourceNotFoundException("Not authenticated");
        }
        AdopterQuestionnaire q = questionnaireService.getByUserId(currentUserId)
                .orElseThrow(() -> new com.skillstorm.animalshelter.exceptions.ResourceNotFoundException("Questionnaire not found"));
        return ResponseEntity.ok(toResponse(q));
    }

    @PutMapping
    public ResponseEntity<AdopterQuestionnaireResponse> upsertQuestionnaire(@RequestHeader(name = "X-User-Id", required = false) UUID currentUserId, @Valid @RequestBody UpsertQuestionnaireRequest req) {
        if (currentUserId == null) {
            throw new com.skillstorm.animalshelter.exceptions.ResourceNotFoundException("Not authenticated");
        }
        AdopterQuestionnaire q = questionnaireService.upsert(currentUserId, req);
        return ResponseEntity.ok(toResponse(q));
    }

    private AdopterQuestionnaireResponse toResponse(AdopterQuestionnaire q) {
        AdopterQuestionnaireResponse r = new AdopterQuestionnaireResponse();
        r.setId(q.getId());
        r.setUserId(q.getUserId());
        r.setSchemaVersion(q.getSchemaVersion());
        r.setHouseholdSize(q.getHouseholdSize());
        r.setHousingType(q.getHousingType());
        r.setHasYard(q.getHasYard());
        r.setHasKids(q.getHasKids());
        r.setHasOtherPets(q.getHasOtherPets());
        r.setNeedsGoodWithKids(q.getNeedsGoodWithKids());
        r.setNeedsGoodWithOtherPets(q.getNeedsGoodWithOtherPets());
        r.setWillingMedicallyComplex(q.getWillingMedicallyComplex());
        r.setNotes(q.getNotes());
        r.setCreatedAt(q.getCreatedAt());
        r.setUpdatedAt(q.getUpdatedAt());
        return r;
    }
}
