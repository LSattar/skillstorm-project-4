package com.skillstorm.animalshelter.dtos.request;

import jakarta.validation.constraints.NotNull;

public class MoveToShelterRequest {

    @NotNull(message = "Shelter ID is required")
    private Long toShelterId;

    private String notes;

    public MoveToShelterRequest() {
    }

    public Long getToShelterId() {
        return toShelterId;
    }

    public void setToShelterId(Long toShelterId) {
        this.toShelterId = toShelterId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
