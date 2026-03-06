package com.skillstorm.animalshelter.dtos.response;

import java.time.Instant;
import java.util.UUID;

public class AnimalResponse {

    private UUID id;
    private String name;
    private String species;
    private String breed;
    private String sex;
    private Integer ageMonths;
    private Boolean goodWithKids;
    private Boolean goodWithOtherPets;
    private Boolean medicallyComplex;
    private String description;
    private String status;
    private Long currentShelterId;
    private UUID currentFosterUserId;
    private String currentShelterName;
    private Instant createdAt;
    private Instant updatedAt;

    public AnimalResponse() {
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

    public UUID getCurrentFosterUserId() {
        return currentFosterUserId;
    }

    public void setCurrentFosterUserId(UUID currentFosterUserId) {
        this.currentFosterUserId = currentFosterUserId;
    }

    public String getCurrentShelterName() {
        return currentShelterName;
    }

    public void setCurrentShelterName(String currentShelterName) {
        this.currentShelterName = currentShelterName;
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
