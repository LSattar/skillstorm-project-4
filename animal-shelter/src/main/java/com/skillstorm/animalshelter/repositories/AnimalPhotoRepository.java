package com.skillstorm.animalshelter.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skillstorm.animalshelter.models.AnimalPhoto;

public interface AnimalPhotoRepository extends JpaRepository<AnimalPhoto, UUID> {

    List<AnimalPhoto> findByAnimalIdOrderByIsPrimaryDesc(UUID animalId);
}
