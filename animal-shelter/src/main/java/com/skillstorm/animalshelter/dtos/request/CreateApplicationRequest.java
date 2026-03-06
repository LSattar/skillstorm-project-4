package com.skillstorm.animalshelter.dtos.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public class CreateApplicationRequest {

    @NotNull(message = "Animal ID is required")
    private UUID animalId;

    private String questionnaireSnapshotJson;

    public CreateApplicationRequest() {
    }

    public UUID getAnimalId() {
        return animalId;
    }

    public void setAnimalId(UUID animalId) {
        this.animalId = animalId;
    }

    public String getQuestionnaireSnapshotJson() {
        return questionnaireSnapshotJson;
    }

    public void setQuestionnaireSnapshotJson(String questionnaireSnapshotJson) {
        this.questionnaireSnapshotJson = questionnaireSnapshotJson;
    }
}
