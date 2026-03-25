package com.wastewatch.points;

import com.wastewatch.common.BaseEntity;
import com.wastewatch.points.enums.TransactionType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "points")
@Getter @Setter
@NoArgsConstructor
public class PointsEntity extends BaseEntity {

    @Column(name = "citizen_id", nullable = false)
    private UUID citizenId;

    // Positive = credit, negative = debit
    @Column(nullable = false)
    private Integer points;


    @Column(name = "transaction_type", nullable = false,
            columnDefinition = "transaction_type")
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.NAMED_ENUM)
    private TransactionType transactionType;

    @Column(name = "reference_id")
    private UUID referenceId;
}