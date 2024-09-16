package com.kibuti.flowercounterbackend.Service;

import com.kibuti.flowercounterbackend.GlobeResponseBody.GlobalJsonResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImagesRecordsService {
    GlobalJsonResponseBody processBatchOfImages(List<MultipartFile> files);
    GlobalJsonResponseBody getAllProcessedImages();
}
