package com.skillstorm.animalshelter.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skillstorm.animalshelter.models.Shelter;

public interface ShelterRepository extends JpaRepository<Shelter, Long> {
}
