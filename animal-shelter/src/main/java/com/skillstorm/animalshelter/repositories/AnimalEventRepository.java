package com.skillstorm.animalshelter.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skillstorm.animalshelter.models.AnimalEvent;

public interface AnimalEventRepository extends JpaRepository<AnimalEvent, UUID> {

    List<AnimalEvent> findByAnimalIdOrderByOccurredAtDesc(UUID animalId);
}
