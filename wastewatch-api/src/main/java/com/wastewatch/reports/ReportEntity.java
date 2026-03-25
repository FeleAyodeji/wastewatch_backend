package com.wastewatch.reports;

import com.wastewatch.common.BaseEntity;
import com.wastewatch.reports.enums.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "reports")
@Getter @Setter
@NoArgsConstructor
public class ReportEntity extends BaseEntity {

    @Column(name = "citizen_id")
    private UUID citizenId;

    @Column(name = "authority_id")
    private UUID authorityId;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal lat;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal lng;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "photo_url", columnDefinition = "TEXT")
    private String photoUrl;


    @Column(name = "waste_category", nullable = false,
            columnDefinition = "waste_category")
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.NAMED_ENUM)
    private WasteCategory wasteCategory;

    @Column(name = "is_anonymous", nullable = false)
    private Boolean isAnonymous = false;


    @Column(nullable = false, columnDefinition = "report_status")
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.NAMED_ENUM)
    private ReportStatus status = ReportStatus.SUBMITTED;

    // Always set by Java — never by the client
    @Column(name = "health_risk", nullable = false,
            columnDefinition = "health_risk")
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.NAMED_ENUM)
    private HealthRisk healthRisk;

    @Column(name = "health_warning", columnDefinition = "TEXT")
    private String healthWarning;

    @Column(name = "community_alert", nullable = false)
    private Boolean communityAlert = false;

    @Column(name = "upvote_count", nullable = false)
    private Integer upvoteCount = 0;

    @Column(name = "resolved_at")
    private OffsetDateTime resolvedAt;

    @Column(name = "escalated_at")
    private OffsetDateTime escalatedAt;

    @Column(name = "resolution_photo_url", columnDefinition = "TEXT")
    private String resolutionPhotoUrl;
}