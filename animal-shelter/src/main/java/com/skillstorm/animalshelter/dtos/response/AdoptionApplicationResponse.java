package com.skillstorm.animalshelter.dtos.response;

import java.time.Instant;
import java.util.UUID;

public class AdoptionApplicationResponse {

    private UUID id;
    private UUID animalId;
    private UUID adopterUserId;
    private String status;
    private String questionnaireSnapshotJson;
    private UUID staffReviewerUserId;
    private String decisionNotes;
    private Instant createdAt;
    private Instant updatedAt;

    public AdoptionApplicationResponse() {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getQuestionnaireSnapshotJson() {
        return questionnaireSnapshotJson;
    }

    public void setQuestionnaireSnapshotJson(String questionnaireSnapshotJson) {
        this.questionnaireSnapshotJson = questionnaireSnapshotJson;
    }

    public UUID getStaffReviewerUserId() {
        return staffReviewerUserId;
    }

    public void setStaffReviewerUserId(UUID staffReviewerUserId) {
        this.staffReviewerUserId = staffReviewerUserId;
    }

    public String getDecisionNotes() {
        return decisionNotes;
    }

    public void setDecisionNotes(String decisionNotes) {
        this.decisionNotes = decisionNotes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
