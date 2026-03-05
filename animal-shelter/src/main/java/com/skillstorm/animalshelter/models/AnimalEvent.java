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
@Table(name = "animal_events")
public class AnimalEvent {

    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)", nullable = false)
    private UUID id;

    @Column(name = "animal_id", columnDefinition = "CHAR(36)", nullable = false)
    private UUID animalId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "animal_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Animal animal;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Column(name = "from_shelter_id")
    private Long fromShelterId;

    @ManyToOne
    @JoinColumn(name = "from_shelter_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Shelter fromShelter;

    @Column(name = "to_shelter_id")
    private Long toShelterId;

    @ManyToOne
    @JoinColumn(name = "to_shelter_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Shelter toShelter;

    @Column(name = "from_foster_user_id", columnDefinition = "CHAR(36)")
    private UUID fromFosterUserId;

    @ManyToOne
    @JoinColumn(name = "from_foster_user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User fromFosterUser;

    @Column(name = "to_foster_user_id", columnDefinition = "CHAR(36)")
    private UUID toFosterUserId;

    @ManyToOne
    @JoinColumn(name = "to_foster_user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User toFosterUser;

    @Column(name = "performed_by_user_id", columnDefinition = "CHAR(36)")
    private UUID performedByUserId;

    @ManyToOne
    @JoinColumn(name = "performed_by_user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User performedByUser;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "occurred_at", nullable = false, columnDefinition = "DATETIME")
    private Instant occurredAt;

    public AnimalEvent() {
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

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Long getFromShelterId() {
        return fromShelterId;
    }

    public void setFromShelterId(Long fromShelterId) {
        this.fromShelterId = fromShelterId;
    }

    public Shelter getFromShelter() {
        return fromShelter;
    }

    public void setFromShelter(Shelter fromShelter) {
        this.fromShelter = fromShelter;
    }

    public Long getToShelterId() {
        return toShelterId;
    }

    public void setToShelterId(Long toShelterId) {
        this.toShelterId = toShelterId;
    }

    public Shelter getToShelter() {
        return toShelter;
    }

    public void setToShelter(Shelter toShelter) {
        this.toShelter = toShelter;
    }

    public UUID getFromFosterUserId() {
        return fromFosterUserId;
    }

    public void setFromFosterUserId(UUID fromFosterUserId) {
        this.fromFosterUserId = fromFosterUserId;
    }

    public User getFromFosterUser() {
        return fromFosterUser;
    }

    public void setFromFosterUser(User fromFosterUser) {
        this.fromFosterUser = fromFosterUser;
    }

    public UUID getToFosterUserId() {
        return toFosterUserId;
    }

    public void setToFosterUserId(UUID toFosterUserId) {
        this.toFosterUserId = toFosterUserId;
    }

    public User getToFosterUser() {
        return toFosterUser;
    }

    public void setToFosterUser(User toFosterUser) {
        this.toFosterUser = toFosterUser;
    }

    public UUID getPerformedByUserId() {
        return performedByUserId;
    }

    public void setPerformedByUserId(UUID performedByUserId) {
        this.performedByUserId = performedByUserId;
    }

    public User getPerformedByUser() {
        return performedByUser;
    }

    public void setPerformedByUser(User performedByUser) {
        this.performedByUser = performedByUser;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }
}
