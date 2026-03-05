package com.skillstorm.animalshelter.models;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "adopter_profiles")
public class AdopterProfile {

    @Id
    @Column(name = "user_id", columnDefinition = "CHAR(36)", nullable = false)
    private UUID userId;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;

    @Column(name = "address_line1", length = 255)
    private String addressLine1;

    @Column(name = "address_line2", length = 255)
    private String addressLine2;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 50)
    private String state;

    @Column(name = "zip", length = 20)
    private String zip;

    @Column(name = "household_size")
    private Integer householdSize;

    @Column(name = "housing_type", length = 20)
    private String housingType;

    @Column(name = "has_yard")
    private Boolean hasYard;

    @Column(name = "has_kids")
    private Boolean hasKids;

    @Column(name = "has_other_pets")
    private Boolean hasOtherPets;

    @Column(name = "needs_good_with_kids")
    private Boolean needsGoodWithKids;

    @Column(name = "needs_good_with_other_pets")
    private Boolean needsGoodWithOtherPets;

    @Column(name = "willing_medically_complex")
    private Boolean willingMedicallyComplex;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public AdopterProfile() {
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
