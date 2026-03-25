package com.wastewatch.reports;

import com.wastewatch.reports.dto.ReportResponse;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.OffsetDateTime;

@Component
public class ReportMapper {

    public ReportResponse toResponse(ReportEntity entity) {
        return ReportResponse.builder()
                .id(entity.getId())
                // Hide citizen identity if anonymous
                .citizenId(entity.getIsAnonymous() ? null : entity.getCitizenId())
                .authorityId(entity.getAuthorityId())
                .lat(entity.getLat())
                .lng(entity.getLng())
                .address(entity.getAddress())
                .photoUrl(entity.getPhotoUrl())
                .wasteCategory(entity.getWasteCategory())
                .isAnonymous(entity.getIsAnonymous())
                .status(entity.getStatus())
                .healthRisk(entity.getHealthRisk())
                .healthWarning(entity.getHealthWarning())
                .communityAlert(entity.getCommunityAlert())
                .upvoteCount(entity.getUpvoteCount())
                .createdAt(entity.getCreatedAt())
                .resolvedAt(entity.getResolvedAt())
                .resolutionPhotoUrl(entity.getResolutionPhotoUrl())
                .timeOpen(calculateTimeOpen(entity.getCreatedAt(),
                        entity.getResolvedAt()))
                .build();
    }

    private String calculateTimeOpen(OffsetDateTime createdAt,
                                     OffsetDateTime resolvedAt) {

        // createdAt can be null immediately after save before Hibernate flushes
        if (createdAt == null) return "0m";

        OffsetDateTime end = resolvedAt != null ? resolvedAt : OffsetDateTime.now();
        Duration duration = Duration.between(createdAt, end);

        long hours = duration.toHours();
        long days = duration.toDays();

        if (days > 0) return days + "d " + (hours % 24) + "h";
        if (hours > 0) return hours + "h";
        return duration.toMinutes() + "m";
    }
}