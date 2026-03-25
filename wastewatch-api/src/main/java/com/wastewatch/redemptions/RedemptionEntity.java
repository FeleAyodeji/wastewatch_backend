package com.wastewatch.redemptions;

import com.wastewatch.common.BaseEntity;
import com.wastewatch.redemptions.enums.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "redemptions")
@Getter @Setter
@NoArgsConstructor
public class RedemptionEntity extends BaseEntity {

    @Column(name = "citizen_id", nullable = false)
    private UUID citizenId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "redemption_type")
    private RedemptionType type;

    @Column(name = "points_spent", nullable = false)
    private Integer pointsSpent;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "interswitch_ref", length = 100)
    private String interswitchRef;


    @Column(nullable = false, columnDefinition = "redemption_status")
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.NAMED_ENUM)
    private RedemptionStatus status = RedemptionStatus.PENDING;
}