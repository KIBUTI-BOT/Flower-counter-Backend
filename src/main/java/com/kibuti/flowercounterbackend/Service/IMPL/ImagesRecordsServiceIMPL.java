package com.kibuti.flowercounterbackend.Service.IMPL;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kibuti.flowercounterbackend.DTO.ImagesEntityDTO;
import com.kibuti.flowercounterbackend.Entity.ImagesEntity;
import com.kibuti.flowercounterbackend.GlobeResponseBody.GlobalJsonResponseBody;
import com.kibuti.flowercounterbackend.Repo.ImageRecordRepo;
import com.kibuti.flowercounterbackend.Service.ImagesRecordsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImagesRecordsServiceIMPL implements ImagesRecordsService {

    private final HuggingFaceClientOptimized huggingFaceClientOptimized;
    private final ImageRecordRepo imageRecordRepo;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${file.upload-dir}")
    private String directoryPath;

    @Value("${server.url}")
    private String serverUrl;

    // Read image bytes and process
    @Override
    public GlobalJsonResponseBody processBatchOfImages(List<MultipartFile> files) {
        GlobalJsonResponseBody responseBody = new GlobalJsonResponseBody();
        List<ImagesEntityDTO> imageDTOs = Collections.synchronizedList(new ArrayList<>());

        try {
            files.parallelStream().forEach(file -> {
                try {
                    byte[] fileBytes = file.getBytes();
                    CompletableFuture<String> future = huggingFaceClientOptimized.processSingleImageAsync(fileBytes);
                    String response = future.join();

                    BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(fileBytes));
                    int numberOfFlowers = extractFlowerCountFromResponse(response);
                    String imageUrl = drawBoundingBoxesFromResponse(originalImage, response);

                    ImagesEntity imageEntity = new ImagesEntity();
                    imageEntity.setImageName(file.getOriginalFilename());
                    imageEntity.setImageUrl(imageUrl);
                    imageEntity.setNumberOfFlowers(numberOfFlowers);
                    imageEntity.setFlowerPoints(response);
                    imageEntity.setDateCreated(LocalDateTime.now());
                    imageEntity.setDateUpdated(LocalDateTime.now());

                    imageRecordRepo.save(imageEntity);

                    ImagesEntityDTO dto = new ImagesEntityDTO();
                    dto.setId(imageEntity.getId());
                    dto.setImageName(imageEntity.getImageName());
                    dto.setImageUrl(imageEntity.getImageUrl());
                    dto.setNumberOfFlowers(imageEntity.getNumberOfFlowers());
                    dto.setFlowerPoints(imageEntity.getFlowerPoints());
                    dto.setDateCreated(imageEntity.getDateCreated());
                    dto.setDateUpdated(imageEntity.getDateUpdated());

                    imageDTOs.add(dto);

                } catch (Exception e) {
                    log.error("Error processing file: {}", file.getOriginalFilename(), e);
                }
            });

        } catch (Exception e) {
            log.error("Error during batch processing", e);
        }

        responseBody.setAction_time(new Date());
        responseBody.setStatus(true);
        responseBody.setMessage("Images processed successfully");
        responseBody.setHttpStatus(HttpStatus.OK);
        responseBody.setData(imageDTOs);

        return responseBody;
    }

    @Override
    public GlobalJsonResponseBody getAllProcessedImages() {
        GlobalJsonResponseBody responseBody = new GlobalJsonResponseBody();
        responseBody.setAction_time(new Date());
        responseBody.setStatus(true);
        responseBody.setHttpStatus(HttpStatus.OK);
        responseBody.setMessage("All processed images called successfully");

            List<ImagesEntity> imagesEntities = imageRecordRepo.findAll();
            List<ImagesEntityDTO> imageDTOs = imagesEntities.stream()
                    .map(imageEntity -> {
                        ImagesEntityDTO dto = new ImagesEntityDTO();
                        dto.setId(imageEntity.getId());
                        dto.setImageName(imageEntity.getImageName());
                        dto.setImageUrl(imageEntity.getImageUrl());
                        dto.setNumberOfFlowers(imageEntity.getNumberOfFlowers());
                        dto.setFlowerPoints(imageEntity.getFlowerPoints());
                        dto.setDateCreated(imageEntity.getDateCreated());
                        dto.setDateUpdated(imageEntity.getDateUpdated());
                        return dto;
                    })
                    .collect(Collectors.toList());

            responseBody.setData(imageDTOs);

        return responseBody;
    }


    public static int extractFlowerCountFromResponse(String response) {
        try {
            List<Map<String, Object>> responseList = objectMapper.readValue(response, new TypeReference<>() {});
            return responseList.size();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private String drawBoundingBoxesFromResponse(BufferedImage originalImage, String response) {
        try {
            List<Map<String, Object>> boxes = objectMapper.readValue(response, new TypeReference<>() {});
            Graphics2D g2d = originalImage.createGraphics();
            g2d.setColor(Color.BLUE);
            g2d.setStroke(new BasicStroke(3));

            for (Map<String, Object> boxData : boxes) {
                Map<String, Integer> box = (Map<String, Integer>) boxData.get("box");
                if (box != null) {
                    int xmin = box.get("xmin");
                    int ymin = box.get("ymin");
                    int xmax = box.get("xmax");
                    int ymax = box.get("ymax");
                    g2d.drawRect(xmin, ymin, xmax - xmin, ymax - ymin);
                }
            }

            g2d.dispose();
            return saveImage(originalImage);

        } catch (JsonProcessingException e) {
            System.err.println("Error parsing response: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            System.err.println("Error saving image: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private String saveImage(BufferedImage image) throws IOException {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String uniqueFileName = "processed_image_" + UUID.randomUUID().toString() + ".png";

        // Create the file object where the image will be saved
        File outputFile = new File(directoryPath + File.separator + uniqueFileName);
        ImageIO.write(image, "png", outputFile);

        System.out.println("Image saved at: " + outputFile.getAbsolutePath());

        // Return the URL of the saved image
        return serverUrl + "/images/" + uniqueFileName;
    }
}
