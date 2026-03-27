package com.skillstorm.animalshelter.dtos.request;

import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class CreateApplicationRequest {

    @NotNull(message = "Animal ID is required")
    private UUID animalId;

    @Valid
    private UpsertQuestionnaireRequest questionnaireAnswers;

    /** Optional; used by tests or clients that pre-build JSON. Ignored when {@link #questionnaireAnswers} is set. */
    private String questionnaireSnapshotJson;

    public CreateApplicationRequest() {
    }

    public UUID getAnimalId() {
        return animalId;
    }

    public void setAnimalId(UUID animalId) {
        this.animalId = animalId;
    }

    public UpsertQuestionnaireRequest getQuestionnaireAnswers() {
        return questionnaireAnswers;
    }

    public void setQuestionnaireAnswers(UpsertQuestionnaireRequest questionnaireAnswers) {
        this.questionnaireAnswers = questionnaireAnswers;
    }

    public String getQuestionnaireSnapshotJson() {
        return questionnaireSnapshotJson;
    }

    public void setQuestionnaireSnapshotJson(String questionnaireSnapshotJson) {
        this.questionnaireSnapshotJson = questionnaireSnapshotJson;
    }
}
