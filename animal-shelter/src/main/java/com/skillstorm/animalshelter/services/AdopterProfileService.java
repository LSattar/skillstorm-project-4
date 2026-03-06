package com.skillstorm.animalshelter.services;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skillstorm.animalshelter.dtos.request.UpdateAdopterProfileRequest;
import com.skillstorm.animalshelter.exceptions.ResourceNotFoundException;
import com.skillstorm.animalshelter.models.AdopterProfile;
import com.skillstorm.animalshelter.repositories.AdopterProfileRepository;
import com.skillstorm.animalshelter.repositories.UserRepository;

@Service
public class AdopterProfileService {

    private static final Logger log = LoggerFactory.getLogger(AdopterProfileService.class);

    private final AdopterProfileRepository adopterProfileRepository;
    private final UserRepository userRepository;

    public AdopterProfileService(AdopterProfileRepository adopterProfileRepository, UserRepository userRepository) {
        this.adopterProfileRepository = adopterProfileRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Optional<AdopterProfile> getByUserId(java.util.UUID userId) {
        return adopterProfileRepository.findById(userId);
    }

    @Transactional(readOnly = true)
    public AdopterProfile getByUserIdOrThrow(java.util.UUID userId) {
        return adopterProfileRepository.findById(userId).orElseThrow(() -> {
            log.error("Adopter profile not found for user id={}", userId);
            return new ResourceNotFoundException("Adopter profile not found for user: " + userId);
        });
    }

    @Transactional
    public AdopterProfile upsert(java.util.UUID userId, UpdateAdopterProfileRequest req) {
        if (!userRepository.existsById(userId)) {
            log.error("User not found for adopter profile upsert, userId={}", userId);
            throw new ResourceNotFoundException("User not found: " + userId);
        }
        AdopterProfile profile = adopterProfileRepository.findById(userId).orElseGet(() -> {
            AdopterProfile p = new AdopterProfile();
            p.setUserId(userId);
            return p;
        });
        if (req.getAddressLine1() != null) profile.setAddressLine1(req.getAddressLine1());
        if (req.getAddressLine2() != null) profile.setAddressLine2(req.getAddressLine2());
        if (req.getCity() != null) profile.setCity(req.getCity());
        if (req.getState() != null) profile.setState(req.getState());
        if (req.getZip() != null) profile.setZip(req.getZip());
        if (req.getHouseholdSize() != null) profile.setHouseholdSize(req.getHouseholdSize());
        if (req.getHousingType() != null) profile.setHousingType(req.getHousingType());
        if (req.getHasYard() != null) profile.setHasYard(req.getHasYard());
        if (req.getHasKids() != null) profile.setHasKids(req.getHasKids());
        if (req.getHasOtherPets() != null) profile.setHasOtherPets(req.getHasOtherPets());
        if (req.getNeedsGoodWithKids() != null) profile.setNeedsGoodWithKids(req.getNeedsGoodWithKids());
        if (req.getNeedsGoodWithOtherPets() != null) profile.setNeedsGoodWithOtherPets(req.getNeedsGoodWithOtherPets());
        if (req.getWillingMedicallyComplex() != null) profile.setWillingMedicallyComplex(req.getWillingMedicallyComplex());
        if (req.getNotes() != null) profile.setNotes(req.getNotes());
        profile = adopterProfileRepository.save(profile);
        log.info("Upserted adopter profile for user id={}", userId);
        return profile;
    }
}
