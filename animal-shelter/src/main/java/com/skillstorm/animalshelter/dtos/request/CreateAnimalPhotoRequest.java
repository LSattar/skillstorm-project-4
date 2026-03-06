package com.skillstorm.animalshelter.dtos.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateAnimalPhotoRequest {

    @NotNull(message = "Animal ID is required")
    private UUID animalId;

    @NotBlank(message = "S3 key is required")
    @Size(max = 500)
    private String s3Key;

    @NotBlank(message = "URL is required")
    @Size(max = 500)
    private String url;

    private Boolean isPrimary = false;

    @Size(max = 100)
    private String contentType;

    private Long fileSizeBytes;

    public CreateAnimalPhotoRequest() {
    }

    public UUID getAnimalId() {
        return animalId;
    }

    public void setAnimalId(UUID animalId) {
        this.animalId = animalId;
    }

    public String getS3Key() {
        return s3Key;
    }

    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public void setFileSizeBytes(Long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }
}
