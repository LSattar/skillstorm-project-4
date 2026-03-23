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

import com.skillstorm.animalshelter.dtos.request.UpdateAdopterProfileRequest;
import com.skillstorm.animalshelter.dtos.response.AdopterProfileResponse;
import com.skillstorm.animalshelter.models.AdopterProfile;
import com.skillstorm.animalshelter.services.AdopterProfileService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/adopter/profile")
public class AdopterProfileController {

    private static final Logger log = LoggerFactory.getLogger(AdopterProfileController.class);

    private final AdopterProfileService adopterProfileService;

    public AdopterProfileController(AdopterProfileService adopterProfileService) {
        this.adopterProfileService = adopterProfileService;
    }

    @GetMapping
    public ResponseEntity<AdopterProfileResponse> getProfile(Authentication authentication) {
        UUID currentUserId = currentUserId(authentication);
        AdopterProfile profile = adopterProfileService.getByUserId(currentUserId)
                .orElseThrow(() -> new com.skillstorm.animalshelter.exceptions.ResourceNotFoundException("Profile not found"));
        return ResponseEntity.ok(toResponse(profile));
    }

    @PutMapping
    public ResponseEntity<AdopterProfileResponse> updateProfile(Authentication authentication, @Valid @RequestBody UpdateAdopterProfileRequest req) {
        UUID currentUserId = currentUserId(authentication);
        AdopterProfile profile = adopterProfileService.upsert(currentUserId, req);
        return ResponseEntity.ok(toResponse(profile));
    }

    private AdopterProfileResponse toResponse(AdopterProfile p) {
        AdopterProfileResponse r = new AdopterProfileResponse();
        r.setUserId(p.getUserId());
        r.setAddressLine1(p.getAddressLine1());
        r.setAddressLine2(p.getAddressLine2());
        r.setCity(p.getCity());
        r.setState(p.getState());
        r.setZip(p.getZip());
        r.setHouseholdSize(p.getHouseholdSize());
        r.setHousingType(p.getHousingType());
        r.setHasYard(p.getHasYard());
        r.setHasKids(p.getHasKids());
        r.setHasOtherPets(p.getHasOtherPets());
        r.setNeedsGoodWithKids(p.getNeedsGoodWithKids());
        r.setNeedsGoodWithOtherPets(p.getNeedsGoodWithOtherPets());
        r.setWillingMedicallyComplex(p.getWillingMedicallyComplex());
        r.setNotes(p.getNotes());
        return r;
    }

    private UUID currentUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UUID userId) {
            return userId;
        }
        log.warn("Adopter profile endpoint accessed without valid authentication");
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
    }
}
