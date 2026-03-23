package com.skillstorm.animalshelter.controllers;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.skillstorm.animalshelter.dtos.request.UpsertQuestionnaireRequest;
import com.skillstorm.animalshelter.dtos.response.AdopterQuestionnaireResponse;
import com.skillstorm.animalshelter.models.AdopterQuestionnaire;
import com.skillstorm.animalshelter.services.AdopterQuestionnaireService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/adopter/questionnaire")
public class AdopterQuestionnaireController {

    private static final Logger log = LoggerFactory.getLogger(AdopterQuestionnaireController.class);

    private final AdopterQuestionnaireService questionnaireService;

    public AdopterQuestionnaireController(AdopterQuestionnaireService questionnaireService) {
        this.questionnaireService = questionnaireService;
    }

    @GetMapping
    public ResponseEntity<AdopterQuestionnaireResponse> getQuestionnaire(Authentication authentication) {
        UUID currentUserId = currentUserId(authentication);
        AdopterQuestionnaire q = questionnaireService.getByUserId(currentUserId)
                .orElseThrow(() -> new com.skillstorm.animalshelter.exceptions.ResourceNotFoundException("Questionnaire not found"));
        return ResponseEntity.ok(toResponse(q));
    }

    @PutMapping
    public ResponseEntity<AdopterQuestionnaireResponse> upsertQuestionnaire(Authentication authentication, @Valid @RequestBody UpsertQuestionnaireRequest req) {
        UUID currentUserId = currentUserId(authentication);
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

    private UUID currentUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UUID userId) {
            return userId;
        }
        log.warn("Adopter questionnaire endpoint accessed without valid authentication");
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
    }
}
