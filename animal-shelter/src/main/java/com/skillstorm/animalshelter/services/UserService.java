package com.skillstorm.animalshelter.services;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skillstorm.animalshelter.dtos.request.CreateEmployeeRequest;
import com.skillstorm.animalshelter.dtos.request.RegisterRequest;
import com.skillstorm.animalshelter.dtos.request.UpdateEmployeeRequest;
import com.skillstorm.animalshelter.exceptions.ConflictException;
import com.skillstorm.animalshelter.exceptions.ResourceNotFoundException;
import com.skillstorm.animalshelter.models.Role;
import com.skillstorm.animalshelter.models.User;
import com.skillstorm.animalshelter.models.UserRole;
import com.skillstorm.animalshelter.models.UserRoleId;
import com.skillstorm.animalshelter.repositories.UserRepository;
import com.skillstorm.animalshelter.repositories.UserRoleRepository;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserRoleRepository userRoleRepository,
                       RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            log.error("Registration failed: username already exists, username={}", req.getUsername());
            throw new ConflictException("Username already exists");
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            log.error("Registration failed: email already exists, email={}", req.getEmail());
            throw new ConflictException("Email already exists");
        }
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setDisplayName(req.getDisplayName());
        user.setPhone(req.getPhone());
        user.setIsEnabled(true);
        Instant now = Instant.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        user = userRepository.save(user);
        Role adopterRole = roleService.findByNameOrThrow("ADOPTER");
        userRoleRepository.save(new UserRole(user.getId(), adopterRole.getId()));
        log.info("Registered user username={}, id={}", user.getUsername(), user.getId());
        return user;
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public User findByIdOrThrow(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> {
            log.error("User not found for id={}", id);
            return new ResourceNotFoundException("User not found: " + id);
        });
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<String> getRoleNamesByUserId(UUID userId) {
        return userRoleRepository.findByUserId(userId).stream()
                .map(ur -> {
                    Role r = ur.getRole();
                    return r != null ? r.getName() : null;
                })
                .filter(n -> n != null)
                .collect(Collectors.toList());
    }

    @Transactional
    public User createEmployee(CreateEmployeeRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            log.error("Create employee failed: username already exists, username={}", req.getUsername());
            throw new ConflictException("Username already exists");
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            log.error("Create employee failed: email already exists, email={}", req.getEmail());
            throw new ConflictException("Email already exists");
        }
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setDisplayName(req.getDisplayName());
        user.setPhone(req.getPhone());
        user.setIsEnabled(true);
        Instant now = Instant.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        user = userRepository.save(user);
        Role staffRole = roleService.findByNameOrThrow("STAFF");
        userRoleRepository.save(new UserRole(user.getId(), staffRole.getId()));
        log.info("Created employee id={}, username={}", user.getId(), user.getUsername());
        return user;
    }

    @Transactional
    public User updateEmployee(UUID id, UpdateEmployeeRequest req) {
        User user = findByIdOrThrow(id);
        if (req.getEmail() != null) user.setEmail(req.getEmail());
        if (req.getDisplayName() != null) user.setDisplayName(req.getDisplayName());
        if (req.getPhone() != null) user.setPhone(req.getPhone());
        user.setUpdatedAt(Instant.now());
        user = userRepository.save(user);
        log.info("Updated employee id={}", id);
        return user;
    }

    @Transactional
    public void deactivate(UUID id) {
        User user = findByIdOrThrow(id);
        user.setIsEnabled(false);
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
        log.info("User id={} deactivated", id);
    }

    @Transactional
    public void reactivate(UUID id) {
        User user = findByIdOrThrow(id);
        user.setIsEnabled(true);
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
        log.info("User id={} reactivated", id);
    }

    @Transactional
    public void resetPassword(UUID id, String newPassword) {
        User user = findByIdOrThrow(id);
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
        log.info("Password reset for user id={}", id);
    }

    /**
     * Find or create a user from Google OAuth (public "Sign in with Google" flow).
     * Only adopters can sign in this way; existing staff/foster by email must use password.
     *
     * @param googleSub   Google "sub" claim (stable user id)
     * @param email       Google email
     * @param displayName Google display name (optional)
     * @return the user to log in
     * @throws com.skillstorm.animalshelter.exceptions.OAuthUsePasswordRequiredException if a user with this email has STAFF or FOSTER role
     */
    @Transactional
    public User findOrCreateFromGoogle(String googleSub, String email, String displayName) {
        Optional<User> byGoogleSub = userRepository.findByGoogleSubjectId(googleSub);
        if (byGoogleSub.isPresent()) {
            User u = byGoogleSub.get();
            if (!u.getIsEnabled()) {
                log.warn("Disabled user attempted Google sign-in, id={}", u.getId());
                throw new ResourceNotFoundException("Account is disabled");
            }
            return u;
        }
        Optional<User> byEmail = userRepository.findByEmail(email);
        if (byEmail.isPresent()) {
            User existing = byEmail.get();
            List<String> roles = getRoleNamesByUserId(existing.getId());
            boolean hasStaffOrFoster = roles.stream().anyMatch(r -> "STAFF".equals(r) || "FOSTER".equals(r));
            if (hasStaffOrFoster) {
                log.warn("Staff/foster user attempted Google sign-in without linking first, email={}", email);
                throw new com.skillstorm.animalshelter.exceptions.OAuthUsePasswordRequiredException(
                        "Please sign in with your username and password.");
            }
            existing.setGoogleSubjectId(googleSub);
            if (displayName != null && !displayName.isBlank()) {
                existing.setDisplayName(displayName);
            }
            existing.setUpdatedAt(Instant.now());
            userRepository.save(existing);
            return existing;
        }
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(email);
        user.setUsername(uniqueUsernameFromEmail(email));
        user.setPasswordHash(null);
        user.setDisplayName(displayName);
        user.setGoogleSubjectId(googleSub);
        user.setIsEnabled(true);
        Instant now = Instant.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        user = userRepository.save(user);
        Role adopterRole = roleService.findByNameOrThrow("ADOPTER");
        userRoleRepository.save(new UserRole(user.getId(), adopterRole.getId()));
        log.info("Created adopter from Google sign-in, id={}, email={}", user.getId(), email);
        return user;
    }

    /**
     * Link Google account to an existing user (staff/foster after password login).
     */
    @Transactional
    public void linkGoogle(UUID userId, String googleSub) {
        User user = findByIdOrThrow(userId);
        if (user.getGoogleSubjectId() != null && user.getGoogleSubjectId().equals(googleSub)) {
            return;
        }
        user.setGoogleSubjectId(googleSub);
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
        log.info("Linked Google account for user id={}", userId);
    }

    private String uniqueUsernameFromEmail(String email) {
        String base = email != null && email.contains("@") ? email.substring(0, email.indexOf('@')) : "user";
        base = base.replaceAll("[^a-zA-Z0-9]", "_");
        if (base.isEmpty()) base = "user";
        String candidate = base;
        int n = 0;
        while (userRepository.existsByUsername(candidate)) {
            candidate = base + "_" + (++n);
        }
        return candidate;
    }
}
