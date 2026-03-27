package com.skillstorm.animalshelter.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
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

import com.skillstorm.animalshelter.dtos.request.UpdateAdopterProfileRequest;
import com.skillstorm.animalshelter.dtos.response.AdopterProfileResponse;
import com.skillstorm.animalshelter.models.AdopterProfile;
import com.skillstorm.animalshelter.services.AdopterProfileService;

@ExtendWith(MockitoExtension.class)
class AdopterProfileControllerTest {

    @Mock
    private AdopterProfileService adopterProfileService;

    private AdopterProfileController controller;
    private UUID adopterId;
    private Authentication auth;

    @BeforeEach
    void setUp() {
        controller = new AdopterProfileController(adopterProfileService);
        adopterId = UUID.randomUUID();
        auth = new UsernamePasswordAuthenticationToken(adopterId, null, java.util.List.of(new SimpleGrantedAuthority("ROLE_ADOPTER")));
    }

    @Test
    void getProfileRequiresAuth() {
        assertThatThrownBy(() -> controller.getProfile(null))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("401");
    }

    @Test
    void getProfileReturnsData() {
        AdopterProfile profile = new AdopterProfile();
        profile.setUserId(adopterId);
        profile.setCity("Hartford");
        when(adopterProfileService.getByUserId(adopterId)).thenReturn(Optional.of(profile));

        ResponseEntity<AdopterProfileResponse> response = controller.getProfile(auth);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUserId()).isEqualTo(adopterId);
        assertThat(response.getBody().getCity()).isEqualTo("Hartford");
    }

    @Test
    void getProfileReturnsEmptyWhenNotFound() {
        when(adopterProfileService.getByUserId(adopterId)).thenReturn(Optional.empty());

        ResponseEntity<AdopterProfileResponse> response = controller.getProfile(auth);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUserId()).isEqualTo(adopterId);
        assertThat(response.getBody().getCity()).isNull();
    }

    @Test
    void updateProfileReturnsUpdatedProfile() {
        UpdateAdopterProfileRequest req = new UpdateAdopterProfileRequest();
        req.setCity("West Hartford");

        AdopterProfile profile = new AdopterProfile();
        profile.setUserId(adopterId);
        profile.setCity("West Hartford");
        when(adopterProfileService.upsert(any(UUID.class), any(UpdateAdopterProfileRequest.class))).thenReturn(profile);

        ResponseEntity<AdopterProfileResponse> response = controller.updateProfile(auth, req);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCity()).isEqualTo("West Hartford");
    }
}
