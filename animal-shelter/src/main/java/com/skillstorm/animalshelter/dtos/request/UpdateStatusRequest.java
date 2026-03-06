package com.skillstorm.animalshelter.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateStatusRequest {

    @NotBlank(message = "Status is required")
    @Size(max = 30)
    private String status;

    private String notes;

    public UpdateStatusRequest() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
