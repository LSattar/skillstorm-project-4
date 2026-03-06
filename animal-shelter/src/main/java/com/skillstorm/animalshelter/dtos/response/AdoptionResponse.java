package com.skillstorm.animalshelter.dtos.response;

import java.time.Instant;
import java.util.UUID;

public class AdoptionResponse {

    private UUID id;
    private UUID animalId;
    private UUID adopterUserId;
    private UUID applicationId;
    private Instant adoptedAt;
    private UUID finalizedByUserId;
    private String notes;

    public AdoptionResponse() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAnimalId() {
        return animalId;
    }

    public void setAnimalId(UUID animalId) {
        this.animalId = animalId;
    }

    public UUID getAdopterUserId() {
        return adopterUserId;
    }

    public void setAdopterUserId(UUID adopterUserId) {
        this.adopterUserId = adopterUserId;
    }

    public UUID getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(UUID applicationId) {
        this.applicationId = applicationId;
    }

    public Instant getAdoptedAt() {
        return adoptedAt;
    }

    public void setAdoptedAt(Instant adoptedAt) {
        this.adoptedAt = adoptedAt;
    }

    public UUID getFinalizedByUserId() {
        return finalizedByUserId;
    }

    public void setFinalizedByUserId(UUID finalizedByUserId) {
        this.finalizedByUserId = finalizedByUserId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
