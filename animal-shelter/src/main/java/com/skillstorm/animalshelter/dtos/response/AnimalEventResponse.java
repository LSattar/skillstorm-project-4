package com.skillstorm.animalshelter.dtos.response;

import java.time.Instant;
import java.util.UUID;

public class AnimalEventResponse {

    private UUID id;
    private UUID animalId;
    private String eventType;
    private Long fromShelterId;
    private Long toShelterId;
    private UUID fromFosterUserId;
    private UUID toFosterUserId;
    private UUID performedByUserId;
    private String notes;
    private Instant occurredAt;

    public AnimalEventResponse() {
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

    public Long getToShelterId() {
        return toShelterId;
    }

    public void setToShelterId(Long toShelterId) {
        this.toShelterId = toShelterId;
    }

    public UUID getFromFosterUserId() {
        return fromFosterUserId;
    }

    public void setFromFosterUserId(UUID fromFosterUserId) {
        this.fromFosterUserId = fromFosterUserId;
    }

    public UUID getToFosterUserId() {
        return toFosterUserId;
    }

    public void setToFosterUserId(UUID toFosterUserId) {
        this.toFosterUserId = toFosterUserId;
    }

    public UUID getPerformedByUserId() {
        return performedByUserId;
    }

    public void setPerformedByUserId(UUID performedByUserId) {
        this.performedByUserId = performedByUserId;
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
