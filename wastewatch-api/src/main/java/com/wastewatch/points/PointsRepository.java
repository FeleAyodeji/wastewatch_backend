// points/PointsRepository.java
package com.wastewatch.points;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PointsRepository extends JpaRepository<PointsEntity, UUID> {

    List<PointsEntity> findByCitizenIdOrderByCreatedAtDesc(UUID citizenId);

    @Query("SELECT COALESCE(SUM(p.points), 0) FROM PointsEntity p WHERE p.citizenId = :citizenId")
    Integer sumPointsByCitizenId(@Param("citizenId") UUID citizenId);
}