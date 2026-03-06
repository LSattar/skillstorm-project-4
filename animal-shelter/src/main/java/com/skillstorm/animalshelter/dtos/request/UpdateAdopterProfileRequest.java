package com.skillstorm.animalshelter.dtos.request;

import jakarta.validation.constraints.Size;

public class UpdateAdopterProfileRequest {

    @Size(max = 255)
    private String addressLine1;

    @Size(max = 255)
    private String addressLine2;

    @Size(max = 100)
    private String city;

    @Size(max = 50)
    private String state;

    @Size(max = 20)
    private String zip;

    private Integer householdSize;

    @Size(max = 20)
    private String housingType;

    private Boolean hasYard;
    private Boolean hasKids;
    private Boolean hasOtherPets;
    private Boolean needsGoodWithKids;
    private Boolean needsGoodWithOtherPets;
    private Boolean willingMedicallyComplex;

    private String notes;

    public UpdateAdopterProfileRequest() {
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
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
}
