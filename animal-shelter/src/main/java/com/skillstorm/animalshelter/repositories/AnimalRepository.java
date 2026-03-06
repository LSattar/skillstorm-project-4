package com.skillstorm.animalshelter.repositories;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skillstorm.animalshelter.models.Animal;

public interface AnimalRepository extends JpaRepository<Animal, UUID> {

    List<Animal> findByStatusIn(Collection<String> statuses);

    List<Animal> findByCurrentShelterId(Long currentShelterId);

    List<Animal> findByCurrentFosterUserId(UUID currentFosterUserId);
}
