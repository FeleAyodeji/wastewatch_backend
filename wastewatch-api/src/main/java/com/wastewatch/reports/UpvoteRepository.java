package com.wastewatch.reports;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UpvoteRepository extends JpaRepository<UpvoteEntity, UUID> {

    boolean existsByCitizenIdAndReportId(UUID citizenId, UUID reportId);
}