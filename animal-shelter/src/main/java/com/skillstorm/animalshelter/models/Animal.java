package com.skillstorm.animalshelter.models;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "animals")
public class Animal {

    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)", nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "species", nullable = false, length = 20)
    private String species;

    @Column(name = "breed", length = 100)
    private String breed;

    @Column(name = "sex", length = 20)
    private String sex;

    @Column(name = "age_months")
    private Integer ageMonths;

    @Column(name = "good_with_kids", nullable = false)
    private Boolean goodWithKids = false;

    @Column(name = "good_with_other_pets", nullable = false)
    private Boolean goodWithOtherPets = false;

    @Column(name = "medically_complex", nullable = false)
    private Boolean medicallyComplex = false;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "current_shelter_id")
    private Long currentShelterId;

    @ManyToOne
    @JoinColumn(name = "current_shelter_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Shelter currentShelter;

    @Column(name = "current_foster_user_id", columnDefinition = "CHAR(36)")
    private UUID currentFosterUserId;

    @ManyToOne
    @JoinColumn(name = "current_foster_user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User currentFosterUser;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME")
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME")
    private Instant updatedAt;

    public Animal() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getAgeMonths() {
        return ageMonths;
    }

    public void setAgeMonths(Integer ageMonths) {
        this.ageMonths = ageMonths;
    }

    public Boolean getGoodWithKids() {
        return goodWithKids;
    }

    public void setGoodWithKids(Boolean goodWithKids) {
        this.goodWithKids = goodWithKids;
    }

    public Boolean getGoodWithOtherPets() {
        return goodWithOtherPets;
    }

    public void setGoodWithOtherPets(Boolean goodWithOtherPets) {
        this.goodWithOtherPets = goodWithOtherPets;
    }

    public Boolean getMedicallyComplex() {
        return medicallyComplex;
    }

    public void setMedicallyComplex(Boolean medicallyComplex) {
        this.medicallyComplex = medicallyComplex;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCurrentShelterId() {
        return currentShelterId;
    }

    public void setCurrentShelterId(Long currentShelterId) {
        this.currentShelterId = currentShelterId;
    }

    public Shelter getCurrentShelter() {
        return currentShelter;
    }

    public void setCurrentShelter(Shelter currentShelter) {
        this.currentShelter = currentShelter;
    }

    public UUID getCurrentFosterUserId() {
        return currentFosterUserId;
    }

    public void setCurrentFosterUserId(UUID currentFosterUserId) {
        this.currentFosterUserId = currentFosterUserId;
    }

    public User getCurrentFosterUser() {
        return currentFosterUser;
    }

    public void setCurrentFosterUser(User currentFosterUser) {
        this.currentFosterUser = currentFosterUser;
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
