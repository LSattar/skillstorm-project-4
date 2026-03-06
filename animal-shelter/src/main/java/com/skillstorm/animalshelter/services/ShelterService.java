package com.skillstorm.animalshelter.services;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skillstorm.animalshelter.dtos.request.CreateShelterRequest;
import com.skillstorm.animalshelter.dtos.request.UpdateShelterRequest;
import com.skillstorm.animalshelter.exceptions.ResourceNotFoundException;
import com.skillstorm.animalshelter.models.Shelter;
import com.skillstorm.animalshelter.repositories.ShelterRepository;

@Service
public class ShelterService {

    private static final Logger log = LoggerFactory.getLogger(ShelterService.class);

    private final ShelterRepository shelterRepository;

    public ShelterService(ShelterRepository shelterRepository) {
        this.shelterRepository = shelterRepository;
    }

    @Transactional
    public Shelter create(CreateShelterRequest req) {
        Shelter shelter = new Shelter();
        shelter.setName(req.getName());
        shelter.setAddressLine1(req.getAddressLine1());
        shelter.setAddressLine2(req.getAddressLine2());
        shelter.setCity(req.getCity());
        shelter.setState(req.getState());
        shelter.setZip(req.getZip());
        shelter.setCapacityTotal(req.getCapacityTotal());
        Instant now = Instant.now();
        shelter.setCreatedAt(now);
        shelter.setUpdatedAt(now);
        shelter = shelterRepository.save(shelter);
        log.info("Created shelter id={}, name={}", shelter.getId(), shelter.getName());
        return shelter;
    }

    @Transactional(readOnly = true)
    public Shelter findByIdOrThrow(Long id) {
        return shelterRepository.findById(id).orElseThrow(() -> {
            log.error("Shelter not found for id={}", id);
            return new ResourceNotFoundException("Shelter not found: " + id);
        });
    }

    @Transactional(readOnly = true)
    public List<Shelter> findAll() {
        return shelterRepository.findAll();
    }

    @Transactional
    public Shelter update(Long id, UpdateShelterRequest req) {
        Shelter shelter = findByIdOrThrow(id);
        if (req.getName() != null) shelter.setName(req.getName());
        if (req.getAddressLine1() != null) shelter.setAddressLine1(req.getAddressLine1());
        if (req.getAddressLine2() != null) shelter.setAddressLine2(req.getAddressLine2());
        if (req.getCity() != null) shelter.setCity(req.getCity());
        if (req.getState() != null) shelter.setState(req.getState());
        if (req.getZip() != null) shelter.setZip(req.getZip());
        if (req.getCapacityTotal() != null) shelter.setCapacityTotal(req.getCapacityTotal());
        shelter.setUpdatedAt(Instant.now());
        shelter = shelterRepository.save(shelter);
        log.info("Updated shelter id={}", id);
        return shelter;
    }

    @Transactional
    public void delete(Long id) {
        if (!shelterRepository.existsById(id)) {
            log.error("Shelter not found for delete id={}", id);
            throw new ResourceNotFoundException("Shelter not found: " + id);
        }
        shelterRepository.deleteById(id);
        log.info("Deleted shelter id={}", id);
    }
}
