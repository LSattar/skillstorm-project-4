package com.skillstorm.animalshelter.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.skillstorm.animalshelter.models.AdoptionApplication;

public interface AdoptionApplicationRepository extends JpaRepository<AdoptionApplication, UUID> {

    List<AdoptionApplication> findByAdopterUserId(UUID adopterUserId);

    @Query("select distinct a.animalId from AdoptionApplication a where a.status = :status")
    List<UUID> findDistinctAnimalIdsByStatus(@Param("status") String status);
}
