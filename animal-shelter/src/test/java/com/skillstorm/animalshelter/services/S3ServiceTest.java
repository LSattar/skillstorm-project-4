package com.skillstorm.animalshelter.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class S3ServiceTest {

    /**
     * When bucket name is empty, S3Service is disabled and returns null for any key.
     * This allows unit tests to run without AWS credentials.
     */
    @Nested
    @DisplayName("when S3 disabled (no bucket)")
    class WhenS3Disabled {

        private final S3Service s3Service = new S3Service("", "us-east-1", 60);

        @Test
        @DisplayName("generatePresignedGetUrl returns null for any key")
        void returnsNullForAnyKey() {
            assertThat(s3Service.generatePresignedGetUrl("some/key.jpg")).isNull();
        }

        @Test
        @DisplayName("generatePresignedGetUrl returns null for null key")
        void returnsNullForNullKey() {
            assertThat(s3Service.generatePresignedGetUrl(null)).isNull();
        }

        @Test
        @DisplayName("generatePresignedGetUrl returns null for blank key")
        void returnsNullForBlankKey() {
            assertThat(s3Service.generatePresignedGetUrl("")).isNull();
            assertThat(s3Service.generatePresignedGetUrl("   ")).isNull();
        }
    }
}
