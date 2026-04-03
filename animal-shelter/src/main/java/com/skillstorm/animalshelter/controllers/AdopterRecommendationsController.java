package com.skillstorm.animalshelter.controllers;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.skillstorm.animalshelter.dtos.response.AdopterRecommendationResponse;
import com.skillstorm.animalshelter.services.RecommendationService;

@RestController
@RequestMapping("/api/adopter/recommendations")
public class AdopterRecommendationsController {

    private static final Logger log = LoggerFactory.getLogger(AdopterRecommendationsController.class);
    private static final int DEFAULT_LIMIT = 10;

    private final RecommendationService recommendationService;

    public AdopterRecommendationsController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping
    public ResponseEntity<List<AdopterRecommendationResponse>> getRecommendations(
            Authentication authentication,
            @RequestParam(name = "limit", defaultValue = "10") Integer limit) {
        UUID currentUserId = currentUserId(authentication);
        int safeLimit = limit == null ? DEFAULT_LIMIT : Math.max(1, Math.min(limit, 20));
        log.info("Fetching recommendations for adopter userId={} limit={}", currentUserId, safeLimit);
        List<AdopterRecommendationResponse> recommendations = recommendationService.getRecommendations(currentUserId, safeLimit);
        return ResponseEntity.ok(recommendations);
    }

    private UUID currentUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UUID userId) {
            return userId;
        }
        log.warn("Adopter recommendations endpoint accessed without valid authentication");
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
    }
}
