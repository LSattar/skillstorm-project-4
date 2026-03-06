package com.skillstorm.animalshelter.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillstorm.animalshelter.dtos.request.CreateShelterRequest;
import com.skillstorm.animalshelter.dtos.request.UpdateShelterRequest;
import com.skillstorm.animalshelter.dtos.response.ShelterResponse;
import com.skillstorm.animalshelter.models.Shelter;
import com.skillstorm.animalshelter.services.ShelterService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/staff/shelters")
public class StaffSheltersController {

    private final ShelterService shelterService;

    public StaffSheltersController(ShelterService shelterService) {
        this.shelterService = shelterService;
    }

    @GetMapping
    public ResponseEntity<List<ShelterResponse>> list() {
        List<Shelter> list = shelterService.findAll();
        return ResponseEntity.ok(list.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<ShelterResponse> create(@Valid @RequestBody CreateShelterRequest req) {
        Shelter shelter = shelterService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(shelter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShelterResponse> getById(@PathVariable Long id) {
        Shelter shelter = shelterService.findByIdOrThrow(id);
        return ResponseEntity.ok(toResponse(shelter));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShelterResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateShelterRequest req) {
        Shelter shelter = shelterService.update(id, req);
        return ResponseEntity.ok(toResponse(shelter));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        shelterService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private ShelterResponse toResponse(Shelter s) {
        ShelterResponse r = new ShelterResponse();
        r.setId(s.getId());
        r.setName(s.getName());
        r.setAddressLine1(s.getAddressLine1());
        r.setAddressLine2(s.getAddressLine2());
        r.setCity(s.getCity());
        r.setState(s.getState());
        r.setZip(s.getZip());
        r.setCapacityTotal(s.getCapacityTotal());
        r.setCreatedAt(s.getCreatedAt());
        r.setUpdatedAt(s.getUpdatedAt());
        return r;
    }
}
