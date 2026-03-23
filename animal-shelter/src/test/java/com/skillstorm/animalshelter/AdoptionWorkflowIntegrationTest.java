package com.skillstorm.animalshelter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.skillstorm.animalshelter.dtos.request.CreateApplicationRequest;
import com.skillstorm.animalshelter.models.Adoption;
import com.skillstorm.animalshelter.models.AdoptionApplication;
import com.skillstorm.animalshelter.models.Animal;
import com.skillstorm.animalshelter.models.AnimalEvent;
import com.skillstorm.animalshelter.models.User;
import com.skillstorm.animalshelter.models.UserRole;
import com.skillstorm.animalshelter.repositories.AdoptionRepository;
import com.skillstorm.animalshelter.repositories.AnimalEventRepository;
import com.skillstorm.animalshelter.repositories.AnimalRepository;
import com.skillstorm.animalshelter.repositories.RoleRepository;
import com.skillstorm.animalshelter.repositories.UserRepository;
import com.skillstorm.animalshelter.repositories.UserRoleRepository;
import com.skillstorm.animalshelter.services.AdoptionApplicationService;
import com.skillstorm.animalshelter.services.AdoptionService;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class AdoptionWorkflowIntegrationTest {

    @Container
    static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("animal_shelter")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void mysqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("spring.sql.init.mode", () -> "always");
        registry.add("spring.sql.init.schema-locations", () -> "file:../database/schema.sql");
        registry.add("app.aws.s3.bucket-name", () -> "");
    }

    @Autowired
    private AdoptionApplicationService applicationService;
    @Autowired
    private AdoptionService adoptionService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AnimalRepository animalRepository;
    @Autowired
    private AdoptionRepository adoptionRepository;
    @Autowired
    private AnimalEventRepository animalEventRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;

    private UUID adopterUserId;
    private UUID staffUserId;
    private UUID animalId;

    @BeforeEach
    void setUp() {
        userRoleRepository.deleteAll();
        adoptionRepository.deleteAll();
        animalEventRepository.deleteAll();
        animalRepository.deleteAll();
        userRepository.deleteAll();

        User adopter = new User();
        adopterUserId = UUID.randomUUID();
        adopter.setId(adopterUserId);
        adopter.setEmail("adopter+" + adopterUserId + "@example.com");
        adopter.setUsername("adopter_" + adopterUserId.toString().substring(0, 8));
        adopter.setPasswordHash("hashed");
        adopter.setIsEnabled(true);
        adopter.setCreatedAt(Instant.now());
        adopter.setUpdatedAt(Instant.now());
        userRepository.save(adopter);

        User staff = new User();
        staffUserId = UUID.randomUUID();
        staff.setId(staffUserId);
        staff.setEmail("staff+" + staffUserId + "@example.com");
        staff.setUsername("staff_" + staffUserId.toString().substring(0, 8));
        staff.setPasswordHash("hashed");
        staff.setIsEnabled(true);
        staff.setCreatedAt(Instant.now());
        staff.setUpdatedAt(Instant.now());
        userRepository.save(staff);

        Long adopterRoleId = roleRepository.findByName("ADOPTER").orElseThrow().getId();
        Long staffRoleId = roleRepository.findByName("STAFF").orElseThrow().getId();
        userRoleRepository.save(new UserRole(adopterUserId, adopterRoleId));
        userRoleRepository.save(new UserRole(staffUserId, staffRoleId));

        Animal animal = new Animal();
        animalId = UUID.randomUUID();
        animal.setId(animalId);
        animal.setName("Buddy");
        animal.setSpecies("DOG");
        animal.setStatus("IN_SHELTER");
        animal.setGoodWithKids(true);
        animal.setGoodWithOtherPets(true);
        animal.setMedicallyComplex(false);
        animal.setCreatedAt(Instant.now());
        animal.setUpdatedAt(Instant.now());
        animalRepository.save(animal);
    }

    @Test
    void adoptionWorkflowPersistsStateAndEvents() {
        CreateApplicationRequest req = new CreateApplicationRequest();
        req.setAnimalId(animalId);
        req.setQuestionnaireSnapshotJson("{\"schemaVersion\":1}");

        AdoptionApplication submitted = applicationService.create(adopterUserId, req);
        applicationService.approve(submitted.getId(), staffUserId, "approved");
        Adoption adoption = adoptionService.finalizeAdoption(submitted.getId(), staffUserId, "finalized");

        assertThat(adoption.getApplicationId()).isEqualTo(submitted.getId());
        assertThat(animalRepository.findById(animalId).orElseThrow().getStatus()).isEqualTo("ADOPTED");

        List<AnimalEvent> events = animalEventRepository.findByAnimalIdOrderByOccurredAtDesc(animalId);
        assertThat(events).extracting(AnimalEvent::getEventType)
                .contains("APPLICATION_SUBMITTED", "APPLICATION_APPROVED", "ADOPTED");
    }

    @Test
    void enforcesUniqueEmailConstraint() {
        User dupe = new User();
        dupe.setId(UUID.randomUUID());
        dupe.setEmail("adopter+" + adopterUserId + "@example.com");
        dupe.setUsername("dupe_" + UUID.randomUUID().toString().substring(0, 8));
        dupe.setPasswordHash("hashed");
        dupe.setIsEnabled(true);
        dupe.setCreatedAt(Instant.now());
        dupe.setUpdatedAt(Instant.now());

        assertThatThrownBy(() -> userRepository.saveAndFlush(dupe))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
