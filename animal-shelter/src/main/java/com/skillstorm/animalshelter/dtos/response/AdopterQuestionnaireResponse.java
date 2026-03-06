package com.skillstorm.animalshelter.dtos.response;

import java.time.Instant;
import java.util.UUID;

public class AdopterQuestionnaireResponse {

    private UUID id;
    private UUID userId;
    private Integer schemaVersion;
    private Integer householdSize;
    private String housingType;
    private Boolean hasYard;
    private Boolean hasKids;
    private Boolean hasOtherPets;
    private Boolean needsGoodWithKids;
    private Boolean needsGoodWithOtherPets;
    private Boolean willingMedicallyComplex;
    private String notes;
    private Instant createdAt;
    private Instant updatedAt;

    public AdopterQuestionnaireResponse() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Integer getSchemaVersion() {
        return schemaVersion;
    }

    public void setSchemaVersion(Integer schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    public Integer getHouseholdSize() {
        return householdSize;
    }

    public void setHouseholdSize(Integer householdSize) {
        this.householdSize = householdSize;
    }

    public String getHousingType() {
        return housingType;
    }

    public void setHousingType(String housingType) {
        this.housingType = housingType;
    }

    public Boolean getHasYard() {
        return hasYard;
    }

    public void setHasYard(Boolean hasYard) {
        this.hasYard = hasYard;
    }

    public Boolean getHasKids() {
        return hasKids;
    }

    public void setHasKids(Boolean hasKids) {
        this.hasKids = hasKids;
    }

    public Boolean getHasOtherPets() {
        return hasOtherPets;
    }

    public void setHasOtherPets(Boolean hasOtherPets) {
        this.hasOtherPets = hasOtherPets;
    }

    public Boolean getNeedsGoodWithKids() {
        return needsGoodWithKids;
    }

    public void setNeedsGoodWithKids(Boolean needsGoodWithKids) {
        this.needsGoodWithKids = needsGoodWithKids;
    }

    public Boolean getNeedsGoodWithOtherPets() {
        return needsGoodWithOtherPets;
    }

    public void setNeedsGoodWithOtherPets(Boolean needsGoodWithOtherPets) {
        this.needsGoodWithOtherPets = needsGoodWithOtherPets;
    }

    public Boolean getWillingMedicallyComplex() {
        return willingMedicallyComplex;
    }

    public void setWillingMedicallyComplex(Boolean willingMedicallyComplex) {
        this.willingMedicallyComplex = willingMedicallyComplex;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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
