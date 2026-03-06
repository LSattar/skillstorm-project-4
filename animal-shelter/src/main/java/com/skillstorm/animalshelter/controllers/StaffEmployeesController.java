package com.skillstorm.animalshelter.controllers;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillstorm.animalshelter.dtos.request.CreateEmployeeRequest;
import com.skillstorm.animalshelter.dtos.request.UpdateEmployeeRequest;
import com.skillstorm.animalshelter.dtos.response.UserResponse;
import com.skillstorm.animalshelter.models.User;
import com.skillstorm.animalshelter.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/staff/employees")
public class StaffEmployeesController {

    private final UserService userService;

    public StaffEmployeesController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> list() {
        List<User> list = userService.findAll();
        return ResponseEntity.ok(list.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateEmployeeRequest req) {
        User user = userService.createEmployee(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable UUID id) {
        User user = userService.findByIdOrThrow(id);
        return ResponseEntity.ok(toResponse(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateEmployeeRequest req) {
        User user = userService.updateEmployee(id, req);
        return ResponseEntity.ok(toResponse(user));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        userService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<Void> reactivate(@PathVariable UUID id) {
        userService.reactivate(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reset-password")
    public ResponseEntity<Void> resetPassword(@PathVariable UUID id, @RequestBody(required = false) String newPassword) {
        userService.resetPassword(id, newPassword != null ? newPassword : "changeme");
        return ResponseEntity.noContent().build();
    }

    private UserResponse toResponse(User u) {
        UserResponse r = new UserResponse();
        r.setId(u.getId());
        r.setUsername(u.getUsername());
        r.setEmail(u.getEmail());
        r.setDisplayName(u.getDisplayName());
        r.setPhone(u.getPhone());
        r.setIsEnabled(u.getIsEnabled());
        r.setRoles(userService.getRoleNamesByUserId(u.getId()));
        r.setCreatedAt(u.getCreatedAt());
        return r;
    }
}
