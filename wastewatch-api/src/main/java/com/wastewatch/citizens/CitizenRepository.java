package com.wastewatch.citizens;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CitizenRepository extends JpaRepository<CitizenEntity, UUID> {
    Optional<CitizenEntity> findByEmail(String email);

    // Leaderboard — top 10 citizens in an LGA by total points
    @Query("""
        SELECT c.id, c.name, c.totalPoints
        FROM CitizenEntity c
        WHERE c.lgaId = :lgaId
        ORDER BY c.totalPoints DESC
        LIMIT 10
    """)
    List<Object[]> findTop10ByLgaIdOrderByTotalPointsDesc(
            @Param("lgaId") UUID lgaId);

}