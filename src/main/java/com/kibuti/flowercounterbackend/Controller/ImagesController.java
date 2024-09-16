package com.kibuti.flowercounterbackend.Controller;

import com.kibuti.flowercounterbackend.GlobeResponseBody.GlobalJsonResponseBody;
import com.kibuti.flowercounterbackend.Service.ImagesRecordsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/images")
public class ImagesController {

    private final ImagesRecordsService imagesRecordsService;

    @PostMapping("/upload")
    public ResponseEntity<GlobalJsonResponseBody> uploadMultipleImages(@RequestParam("files") List<MultipartFile> files) {
        GlobalJsonResponseBody response = imagesRecordsService.processBatchOfImages(files);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<GlobalJsonResponseBody> getAllProcessedImages() {
        GlobalJsonResponseBody response = imagesRecordsService.getAllProcessedImages();
        return ResponseEntity.ok(response);
    }
}
