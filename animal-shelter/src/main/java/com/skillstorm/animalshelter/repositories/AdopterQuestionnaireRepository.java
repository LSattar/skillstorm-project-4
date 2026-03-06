package com.skillstorm.animalshelter.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skillstorm.animalshelter.models.AdopterQuestionnaire;

public interface AdopterQuestionnaireRepository extends JpaRepository<AdopterQuestionnaire, UUID> {

    Optional<AdopterQuestionnaire> findByUserId(UUID userId);
}
