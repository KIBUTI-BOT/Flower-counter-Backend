package com.kibuti.flowercounterbackend.DTO;

import jakarta.persistence.Column;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ImagesEntityDTO {
    private UUID id;
    private String imageName;

    private String imageUrl;

    private Integer numberOfFlowers;

    private String flowerPoints;

    private LocalDateTime dateCreated;

    private LocalDateTime dateUpdated;
}
