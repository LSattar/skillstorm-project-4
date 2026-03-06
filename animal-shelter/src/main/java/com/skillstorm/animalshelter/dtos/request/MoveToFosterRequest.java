package com.skillstorm.animalshelter.dtos.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public class MoveToFosterRequest {

    @NotNull(message = "Foster user ID is required")
    private UUID toFosterUserId;

    private String notes;

    public MoveToFosterRequest() {
    }

    public UUID getToFosterUserId() {
        return toFosterUserId;
    }

    public void setToFosterUserId(UUID toFosterUserId) {
        this.toFosterUserId = toFosterUserId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
