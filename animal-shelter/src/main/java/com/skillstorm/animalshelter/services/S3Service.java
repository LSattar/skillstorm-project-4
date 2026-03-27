package com.skillstorm.animalshelter.services;

import java.net.URL;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Service
public class S3Service {

    private static final Logger log = LoggerFactory.getLogger(S3Service.class);

    private final String bucketName;
    private final int presignExpirationMinutes;
    private final S3Presigner presigner;
    private volatile boolean presigningDisabled;

    public S3Service(
            @Value("${app.aws.s3.bucket-name:}") String bucketName,
            @Value("${app.aws.s3.region:us-east-1}") String region,
            @Value("${app.aws.s3.presign-expiration-minutes:60}") int presignExpirationMinutes) {
        this.bucketName = bucketName;
        this.presignExpirationMinutes = presignExpirationMinutes;
        if (bucketName == null || bucketName.isBlank()) {
            log.info("S3 bucket name not configured; presigned URL generation disabled");
            this.presigner = null;
        } else {
            this.presigner = S3Presigner.builder()
                    .region(Region.of(region))
                    .build();
        }
    }

    /**
     * Generates a presigned GET URL for the object at the given S3 key in the configured bucket.
     * Returns null if S3 is not configured, key is blank, or generation fails.
     */
    public String generatePresignedGetUrl(String s3Key) {
        if (presigner == null || presigningDisabled || s3Key == null || s3Key.isBlank()) {
            return null;
        }
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(presignExpirationMinutes))
                    .getObjectRequest(getObjectRequest)
                    .build();
            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
            URL url = presignedRequest.url();
            return url != null ? url.toString() : null;
        } catch (SdkClientException e) {
            // Local/dev often has no AWS credentials; disable further attempts and fall back to stored URL.
            presigningDisabled = true;
            log.warn("Disabling S3 presigned URL generation for this app run (credentials unavailable): {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("Failed to generate presigned URL for s3Key={}, reason={}", s3Key, e.getMessage());
            return null;
        }
    }
}
