package com.kibuti.flowercounterbackend.Repo;

import com.kibuti.flowercounterbackend.Entity.ImagesEntity;
import com.kibuti.flowercounterbackend.GlobeResponseBody.GlobalJsonResponseBody;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImageRecordRepo extends JpaRepository<ImagesEntity, UUID> {

}
