package com.skillstorm.animalshelter.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skillstorm.animalshelter.models.AdopterProfile;

public interface AdopterProfileRepository extends JpaRepository<AdopterProfile, UUID> {
}
