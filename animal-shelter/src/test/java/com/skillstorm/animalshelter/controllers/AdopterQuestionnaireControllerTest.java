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

import com.skillstorm.animalshelter.dtos.request.UpsertQuestionnaireRequest;
import com.skillstorm.animalshelter.dtos.response.AdopterQuestionnaireResponse;
import com.skillstorm.animalshelter.models.AdopterQuestionnaire;
import com.skillstorm.animalshelter.services.AdopterQuestionnaireService;

@ExtendWith(MockitoExtension.class)
class AdopterQuestionnaireControllerTest {

    @Mock
    private AdopterQuestionnaireService questionnaireService;

    private AdopterQuestionnaireController controller;
    private UUID adopterId;
    private Authentication auth;

    @BeforeEach
    void setUp() {
        controller = new AdopterQuestionnaireController(questionnaireService);
        adopterId = UUID.randomUUID();
        auth = new UsernamePasswordAuthenticationToken(adopterId, null, java.util.List.of(new SimpleGrantedAuthority("ROLE_ADOPTER")));
    }

    @Test
    void getQuestionnaireRequiresAuth() {
        assertThatThrownBy(() -> controller.getQuestionnaire(null))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("401");
    }

    @Test
    void getQuestionnaireReturnsData() {
        AdopterQuestionnaire q = new AdopterQuestionnaire();
        q.setId(UUID.randomUUID());
        q.setUserId(adopterId);
        q.setHousingType("HOUSE");
        when(questionnaireService.getByUserId(adopterId)).thenReturn(Optional.of(q));

        ResponseEntity<AdopterQuestionnaireResponse> response = controller.getQuestionnaire(auth);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getHousingType()).isEqualTo("HOUSE");
    }

    @Test
    void upsertReturnsUpdatedQuestionnaire() {
        UpsertQuestionnaireRequest req = new UpsertQuestionnaireRequest();
        req.setHousingType("CONDO");
        AdopterQuestionnaire q = new AdopterQuestionnaire();
        q.setId(UUID.randomUUID());
        q.setUserId(adopterId);
        q.setHousingType("CONDO");
        when(questionnaireService.upsert(any(UUID.class), any(UpsertQuestionnaireRequest.class))).thenReturn(q);

        ResponseEntity<AdopterQuestionnaireResponse> response = controller.upsertQuestionnaire(auth, req);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getHousingType()).isEqualTo("CONDO");
    }
}
