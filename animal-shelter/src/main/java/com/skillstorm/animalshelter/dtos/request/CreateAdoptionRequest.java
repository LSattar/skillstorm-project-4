package com.skillstorm.animalshelter.dtos.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public class CreateAdoptionRequest {

    @NotNull(message = "Application ID is required")
    private UUID applicationId;

    public CreateAdoptionRequest() {
    }

    public UUID getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(UUID applicationId) {
        this.applicationId = applicationId;
    }
}
