// reports/ReportRepository.java
package com.wastewatch.reports;

import com.wastewatch.reports.enums.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<ReportEntity, UUID> {

    List<ReportEntity> findByCitizenIdOrderByCreatedAtDesc(UUID citizenId);

    List<ReportEntity> findByAuthorityIdOrderByHealthRiskAscCreatedAtDesc(UUID authorityId);

    // Used by the escalation job
    @Query("""
        SELECT r FROM ReportEntity r
        WHERE r.healthRisk = 'HIGH'
        AND r.status = 'SUBMITTED'
        AND r.createdAt < :cutoff
    """)
    List<ReportEntity> findHighRiskUnacknowledged(@Param("cutoff") OffsetDateTime cutoff);

    // Used by the overdue job
    @Query("""
        SELECT r FROM ReportEntity r
        WHERE r.status NOT IN ('RESOLVED', 'ESCALATED')
        AND r.createdAt < :cutoff
    """)
    List<ReportEntity> findOverdueReports(@Param("cutoff") OffsetDateTime cutoff);
}