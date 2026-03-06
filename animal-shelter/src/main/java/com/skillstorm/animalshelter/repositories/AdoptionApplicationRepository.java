package com.skillstorm.animalshelter.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skillstorm.animalshelter.models.AdoptionApplication;

public interface AdoptionApplicationRepository extends JpaRepository<AdoptionApplication, UUID> {

    List<AdoptionApplication> findByAdopterUserId(UUID adopterUserId);
}
