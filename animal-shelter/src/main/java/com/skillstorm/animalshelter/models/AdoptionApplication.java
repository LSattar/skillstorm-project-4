package com.skillstorm.animalshelter.models;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "adoption_applications")
public class AdoptionApplication {

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

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "questionnaire_snapshot_json", columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private String questionnaireSnapshotJson;

    @Column(name = "staff_reviewer_user_id", columnDefinition = "CHAR(36)")
    private UUID staffReviewerUserId;

    @ManyToOne
    @JoinColumn(name = "staff_reviewer_user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User staffReviewerUser;

    @Column(name = "decision_notes", columnDefinition = "TEXT")
    private String decisionNotes;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME")
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME")
    private Instant updatedAt;

    public AdoptionApplication() {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getQuestionnaireSnapshotJson() {
        return questionnaireSnapshotJson;
    }

    public void setQuestionnaireSnapshotJson(String questionnaireSnapshotJson) {
        this.questionnaireSnapshotJson = questionnaireSnapshotJson;
    }

    public UUID getStaffReviewerUserId() {
        return staffReviewerUserId;
    }

    public void setStaffReviewerUserId(UUID staffReviewerUserId) {
        this.staffReviewerUserId = staffReviewerUserId;
    }

    public User getStaffReviewerUser() {
        return staffReviewerUser;
    }

    public void setStaffReviewerUser(User staffReviewerUser) {
        this.staffReviewerUser = staffReviewerUser;
    }

    public String getDecisionNotes() {
        return decisionNotes;
    }

    public void setDecisionNotes(String decisionNotes) {
        this.decisionNotes = decisionNotes;
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
