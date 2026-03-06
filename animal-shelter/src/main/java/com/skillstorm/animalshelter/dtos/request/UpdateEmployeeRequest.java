package com.skillstorm.animalshelter.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UpdateEmployeeRequest {

    @Email
    @Size(max = 255)
    private String email;

    @Size(max = 255)
    private String displayName;

    @Size(max = 50)
    private String phone;

    public UpdateEmployeeRequest() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
