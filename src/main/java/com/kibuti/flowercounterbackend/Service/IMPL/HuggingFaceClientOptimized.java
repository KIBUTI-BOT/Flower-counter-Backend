package com.kibuti.flowercounterbackend.Service.IMPL;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class HuggingFaceClientOptimized {

    private static final String API_URL = "https://api-inference.huggingface.co/models/smutuvi/flower_count_model";
    private static final String API_KEY = "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";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final WebClient webClient;

    @Value("${file.upload-dir}")
    static String directoryPath;

    private static final int MAX_RETRIES = 3;

    // If API accepts binary data
    public CompletableFuture<String> processSingleImageAsync(byte[] imageBytes) {
        return CompletableFuture.supplyAsync(() -> {
            // Directly send imageBytes
            return sendPostRequestAsync(imageBytes).toFuture();
        }).thenCompose(Function.identity());
    }

    // Update sendPostRequestAsync to handle binary data
    private Mono<String> sendPostRequestAsync(byte[] imageBytes) {
        return webClient.post()
                .uri(API_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + API_KEY)
                .contentType(MediaType.APPLICATION_OCTET_STREAM) // Use binary data content type
                .bodyValue(imageBytes)
                .retrieve()
                .bodyToMono(String.class)
                .retry(MAX_RETRIES)
                .doOnError(e -> System.err.println("Error during API call: " + e.getMessage()));
    }

    // Encode image file to base64 string
    private static String encodeFileToBase64String(String filePath) throws IOException {
        byte[] fileContent = Files.readAllBytes(new File(filePath).toPath());
        return Base64.getEncoder().encodeToString(fileContent);
    }

//    // Send POST request using WebClient asynchronously
//    private Mono<String> sendPostRequestAsync(String jsonInputString) {
//        return webClient.post()
//                .uri(API_URL)
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + API_KEY)
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(jsonInputString)
//                .retrieve()
//                .bodyToMono(String.class)
//                .retry(MAX_RETRIES)
//                .doOnError(e -> System.err.println("Error during API call: " + e.getMessage()));
//    }
}


