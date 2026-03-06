package com.skillstorm.animalshelter.services;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skillstorm.animalshelter.dtos.request.CreateAnimalPhotoRequest;
import com.skillstorm.animalshelter.exceptions.ResourceNotFoundException;
import com.skillstorm.animalshelter.models.AnimalPhoto;
import com.skillstorm.animalshelter.repositories.AnimalPhotoRepository;
import com.skillstorm.animalshelter.repositories.AnimalRepository;

@Service
public class AnimalPhotoService {

    private static final Logger log = LoggerFactory.getLogger(AnimalPhotoService.class);

    private final AnimalPhotoRepository animalPhotoRepository;
    private final AnimalRepository animalRepository;

    public AnimalPhotoService(AnimalPhotoRepository animalPhotoRepository, AnimalRepository animalRepository) {
        this.animalPhotoRepository = animalPhotoRepository;
        this.animalRepository = animalRepository;
    }

    @Transactional
    public AnimalPhoto create(CreateAnimalPhotoRequest req) {
        if (!animalRepository.existsById(req.getAnimalId())) {
            log.error("Animal not found for photo, animalId={}", req.getAnimalId());
            throw new ResourceNotFoundException("Animal not found: " + req.getAnimalId());
        }
        AnimalPhoto photo = new AnimalPhoto();
        photo.setId(UUID.randomUUID());
        photo.setAnimalId(req.getAnimalId());
        photo.setS3Key(req.getS3Key());
        photo.setUrl(req.getUrl());
        photo.setIsPrimary(req.getIsPrimary() != null ? req.getIsPrimary() : false);
        photo.setContentType(req.getContentType());
        photo.setFileSizeBytes(req.getFileSizeBytes());
        photo.setCreatedAt(Instant.now());
        photo = animalPhotoRepository.save(photo);
        log.info("Created animal photo id={}, animalId={}", photo.getId(), photo.getAnimalId());
        return photo;
    }

    @Transactional(readOnly = true)
    public AnimalPhoto findByIdOrThrow(UUID id) {
        return animalPhotoRepository.findById(id).orElseThrow(() -> {
            log.error("Animal photo not found for id={}", id);
            return new ResourceNotFoundException("Animal photo not found: " + id);
        });
    }

    @Transactional(readOnly = true)
    public List<AnimalPhoto> findByAnimalId(UUID animalId) {
        return animalPhotoRepository.findByAnimalIdOrderByIsPrimaryDesc(animalId);
    }

    @Transactional
    public void delete(UUID id) {
        if (!animalPhotoRepository.existsById(id)) {
            log.error("Animal photo not found for delete id={}", id);
            throw new ResourceNotFoundException("Animal photo not found: " + id);
        }
        animalPhotoRepository.deleteById(id);
        log.info("Deleted animal photo id={}", id);
    }
}
