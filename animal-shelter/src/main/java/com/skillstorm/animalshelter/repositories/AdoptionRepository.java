package com.skillstorm.animalshelter.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skillstorm.animalshelter.models.Adoption;

public interface AdoptionRepository extends JpaRepository<Adoption, UUID> {

    Optional<Adoption> findByApplicationId(UUID applicationId);
}
