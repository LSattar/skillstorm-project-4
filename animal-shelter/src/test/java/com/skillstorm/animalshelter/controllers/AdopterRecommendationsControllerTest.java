package com.skillstorm.animalshelter.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.server.ResponseStatusException;

import com.skillstorm.animalshelter.dtos.response.AdopterRecommendationResponse;
import com.skillstorm.animalshelter.services.RecommendationService;

@ExtendWith(MockitoExtension.class)
class AdopterRecommendationsControllerTest {

    @Mock
    private RecommendationService recommendationService;

    private AdopterRecommendationsController controller;
    private UUID adopterId;
    private Authentication auth;

    @BeforeEach
    void setUp() {
        controller = new AdopterRecommendationsController(recommendationService);
        adopterId = UUID.randomUUID();
        auth = new UsernamePasswordAuthenticationToken(adopterId, null, List.of(new SimpleGrantedAuthority("ROLE_ADOPTER")));
    }

    @Test
    void getRecommendationsRequiresAuth() {
        assertThatThrownBy(() -> controller.getRecommendations(null, 10))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("401");
    }

    @Test
    void getRecommendationsReturnsList() {
        AdopterRecommendationResponse response = new AdopterRecommendationResponse();
        response.setAnimalId(UUID.randomUUID());
        response.setAnimalName("Buddy");
        response.setScore(82);
        response.setReasonCodes(List.of("GOOD_WITH_KIDS_MATCH"));
        response.setMatchingMode("HYBRID");
        when(recommendationService.getRecommendations(adopterId, 10)).thenReturn(List.of(response));

        ResponseEntity<List<AdopterRecommendationResponse>> result = controller.getRecommendations(auth, 10);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).hasSize(1);
        assertThat(result.getBody().get(0).getAnimalName()).isEqualTo("Buddy");
        assertThat(result.getBody().get(0).getScore()).isEqualTo(82);
    }

    @Test
    void getRecommendationsCapsLimit() {
        when(recommendationService.getRecommendations(adopterId, 20)).thenReturn(List.of());

        ResponseEntity<List<AdopterRecommendationResponse>> result = controller.getRecommendations(auth, 1000);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEmpty();
    }
}
