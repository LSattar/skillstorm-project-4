package com.skillstorm.animalshelter.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.skillstorm.animalshelter.dtos.request.UpdateAdopterProfileRequest;
import com.skillstorm.animalshelter.exceptions.ResourceNotFoundException;
import com.skillstorm.animalshelter.models.AdopterProfile;
import com.skillstorm.animalshelter.repositories.AdopterProfileRepository;
import com.skillstorm.animalshelter.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
class AdopterProfileServiceTest {

    @Mock
    private AdopterProfileRepository adopterProfileRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdopterProfileService adopterProfileService;

    private UUID userId;
    private UpdateAdopterProfileRequest updateRequest;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        updateRequest = new UpdateAdopterProfileRequest();
        updateRequest.setCity("Boston");
        updateRequest.setHouseholdSize(2);
    }

    @Nested
    @DisplayName("getByUserId")
    class GetByUserId {

        @Test
        @DisplayName("returns empty when no profile")
        void returnsEmptyWhenNoProfile() {
            when(adopterProfileRepository.findById(userId)).thenReturn(Optional.empty());

            assertThat(adopterProfileService.getByUserId(userId)).isEmpty();
        }

        @Test
        @DisplayName("returns profile when found")
        void returnsProfileWhenFound() {
            AdopterProfile profile = new AdopterProfile();
            profile.setUserId(userId);
            profile.setCity("Boston");
            when(adopterProfileRepository.findById(userId)).thenReturn(Optional.of(profile));

            assertThat(adopterProfileService.getByUserId(userId)).contains(profile);
        }
    }

    @Nested
    @DisplayName("upsert")
    class Upsert {

        @Test
        @DisplayName("throws when user does not exist")
        void throwsWhenUserDoesNotExist() {
            when(userRepository.existsById(userId)).thenReturn(false);

            assertThatThrownBy(() -> adopterProfileService.upsert(userId, updateRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User not found");
            verify(adopterProfileRepository, never()).save(any(AdopterProfile.class));
        }

        @Test
        @DisplayName("creates new profile when none exists and saves")
        void createsNewProfileWhenNoneExists() {
            when(userRepository.existsById(userId)).thenReturn(true);
            when(adopterProfileRepository.findById(userId)).thenReturn(Optional.empty());
            when(adopterProfileRepository.save(any(AdopterProfile.class))).thenAnswer(inv -> inv.getArgument(0));

            AdopterProfile result = adopterProfileService.upsert(userId, updateRequest);

            assertThat(result).isNotNull();
            assertThat(result.getUserId()).isEqualTo(userId);
            assertThat(result.getCity()).isEqualTo("Boston");
            assertThat(result.getHouseholdSize()).isEqualTo(2);
            verify(adopterProfileRepository).save(any(AdopterProfile.class));
        }

        @Test
        @DisplayName("updates existing profile")
        void updatesExistingProfile() {
            AdopterProfile existing = new AdopterProfile();
            existing.setUserId(userId);
            existing.setCity("Old City");
            when(userRepository.existsById(userId)).thenReturn(true);
            when(adopterProfileRepository.findById(userId)).thenReturn(Optional.of(existing));
            when(adopterProfileRepository.save(any(AdopterProfile.class))).thenAnswer(inv -> inv.getArgument(0));

            AdopterProfile result = adopterProfileService.upsert(userId, updateRequest);

            assertThat(result.getCity()).isEqualTo("Boston");
            assertThat(result.getHouseholdSize()).isEqualTo(2);
            verify(adopterProfileRepository).save(existing);
        }
    }
}
