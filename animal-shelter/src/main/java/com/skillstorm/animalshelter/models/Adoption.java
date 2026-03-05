package com.skillstorm.animalshelter.models;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "adoptions",
    uniqueConstraints = @UniqueConstraint(name = "uk_adoptions_application", columnNames = "application_id")
)
public class Adoption {

    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)", nullable = false)
    private UUID id;

    @Column(name = "animal_id", columnDefinition = "CHAR(36)", nullable = false)
    private UUID animalId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "animal_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Animal animal;

    @Column(name = "adopter_user_id", columnDefinition = "CHAR(36)", nullable = false)
    private UUID adopterUserId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "adopter_user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User adopterUser;

    @Column(name = "application_id", columnDefinition = "CHAR(36)", nullable = false)
    private UUID applicationId;

    @OneToOne(optional = false)
    @JoinColumn(name = "application_id", referencedColumnName = "id", insertable = false, updatable = false)
    private AdoptionApplication application;

    @Column(name = "adopted_at", nullable = false, columnDefinition = "DATETIME")
    private Instant adoptedAt;

    @Column(name = "finalized_by_user_id", columnDefinition = "CHAR(36)", nullable = false)
    private UUID finalizedByUserId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "finalized_by_user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User finalizedByUser;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public Adoption() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAnimalId() {
        return animalId;
    }

    public void setAnimalId(UUID animalId) {
        this.animalId = animalId;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public UUID getAdopterUserId() {
        return adopterUserId;
    }

    public void setAdopterUserId(UUID adopterUserId) {
        this.adopterUserId = adopterUserId;
    }

    public User getAdopterUser() {
        return adopterUser;
    }

    public void setAdopterUser(User adopterUser) {
        this.adopterUser = adopterUser;
    }

    public UUID getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(UUID applicationId) {
        this.applicationId = applicationId;
    }

    public AdoptionApplication getApplication() {
        return application;
    }

    public void setApplication(AdoptionApplication application) {
        this.application = application;
    }

    public Instant getAdoptedAt() {
        return adoptedAt;
    }

    public void setAdoptedAt(Instant adoptedAt) {
        this.adoptedAt = adoptedAt;
    }

    public UUID getFinalizedByUserId() {
        return finalizedByUserId;
    }

    public void setFinalizedByUserId(UUID finalizedByUserId) {
        this.finalizedByUserId = finalizedByUserId;
    }

    public User getFinalizedByUser() {
        return finalizedByUser;
    }

    public void setFinalizedByUser(User finalizedByUser) {
        this.finalizedByUser = finalizedByUser;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
