package com.skillstorm.animalshelter.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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

import com.skillstorm.animalshelter.dtos.request.ApproveApplicationRequest;
import com.skillstorm.animalshelter.dtos.response.AdoptionApplicationResponse;
import com.skillstorm.animalshelter.models.AdoptionApplication;
import com.skillstorm.animalshelter.services.AdoptionApplicationService;

@ExtendWith(MockitoExtension.class)
class StaffApplicationsControllerTest {

    @Mock
    private AdoptionApplicationService service;

    private StaffApplicationsController controller;
    private UUID staffUserId;
    private Authentication staffAuth;

    @BeforeEach
    void setUp() {
        controller = new StaffApplicationsController(service);
        staffUserId = UUID.randomUUID();
        staffAuth = new UsernamePasswordAuthenticationToken(staffUserId, null, List.of(new SimpleGrantedAuthority("ROLE_STAFF")));
    }

    @Test
    void approveReturnsUnauthorizedWhenAuthenticationMissing() {
        UUID applicationId = UUID.randomUUID();
        ApproveApplicationRequest req = new ApproveApplicationRequest();
        req.setDecisionNotes("approved");

        assertThatThrownBy(() -> controller.approve(applicationId, null, req))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("401");
    }

    @Test
    void approveUsesAuthenticatedStaffId() {
        UUID applicationId = UUID.randomUUID();
        ApproveApplicationRequest req = new ApproveApplicationRequest();
        req.setDecisionNotes("approved");
        AdoptionApplication app = new AdoptionApplication();
        app.setId(applicationId);
        app.setStatus("APPROVED");
        app.setStaffReviewerUserId(staffUserId);
        when(service.approve(any(UUID.class), any(UUID.class), any())).thenReturn(app);

        ResponseEntity<AdoptionApplicationResponse> result = controller.approve(applicationId, staffAuth, req);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getStaffReviewerUserId()).isEqualTo(staffUserId);
    }

    @Test
    void listReturns200() {
        when(service.findAllStaff(null, null, null)).thenReturn(List.of(new AdoptionApplication()));

        ResponseEntity<List<AdoptionApplicationResponse>> result = controller.list(null, null, null);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).hasSize(1);
    }
}
