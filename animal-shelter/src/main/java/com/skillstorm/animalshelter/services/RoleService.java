package com.skillstorm.animalshelter.services;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skillstorm.animalshelter.exceptions.ResourceNotFoundException;
import com.skillstorm.animalshelter.models.Role;
import com.skillstorm.animalshelter.repositories.RoleRepository;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Role> findById(Long id) {
        return roleRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Role findByNameOrThrow(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> {
                    var ex = new ResourceNotFoundException("Role not found: " + name);
                    org.slf4j.LoggerFactory.getLogger(RoleService.class).error("Role not found: name={}", name);
                    return ex;
                });
    }
}
