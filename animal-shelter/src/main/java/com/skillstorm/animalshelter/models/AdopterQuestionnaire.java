package com.skillstorm.animalshelter.models;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "adopter_questionnaires",
    uniqueConstraints = @UniqueConstraint(name = "uk_adopter_questionnaires_user_id", columnNames = "user_id")
)
public class AdopterQuestionnaire {

    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)", nullable = false)
    private UUID id;

    @Column(name = "user_id", columnDefinition = "CHAR(36)", nullable = false)
    private UUID userId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;

    @Column(name = "schema_version", nullable = false)
    private Integer schemaVersion = 1;

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

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME")
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME")
    private Instant updatedAt;

    public AdopterQuestionnaire() {
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
