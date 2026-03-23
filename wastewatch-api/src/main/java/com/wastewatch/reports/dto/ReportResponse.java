package com.wastewatch.reports.dto;

import com.wastewatch.reports.enums.*;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class ReportResponse {

    private UUID id;
    private UUID citizenId;        // null if anonymous
    private UUID authorityId;
    private BigDecimal lat;
    private BigDecimal lng;
    private String address;
    private String photoUrl;
    private WasteCategory wasteCategory;
    private Boolean isAnonymous;
    private ReportStatus status;
    private HealthRisk healthRisk;
    private String healthWarning;
    private Boolean communityAlert;
    private Integer upvoteCount;
    private OffsetDateTime createdAt;
    private OffsetDateTime resolvedAt;
    private String resolutionPhotoUrl;

    // How long the report has been open — shown on citizen dashboard
    private String timeOpen;
}