package com.skillstorm.animalshelter.dtos.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateAnimalRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 255)
    private String name;

    @NotBlank(message = "Species is required")
    @Size(max = 20)
    private String species;

    @Size(max = 100)
    private String breed;

    @Size(max = 20)
    private String sex;

    private Integer ageMonths;

    @NotNull
    private Boolean goodWithKids = false;

    @NotNull
    private Boolean goodWithOtherPets = false;

    @NotNull
    private Boolean medicallyComplex = false;

    private String description;

    @NotBlank(message = "Status is required")
    @Size(max = 30)
    private String status;

    private Long currentShelterId;
    private UUID currentFosterUserId;

    public CreateAnimalRequest() {
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
}
