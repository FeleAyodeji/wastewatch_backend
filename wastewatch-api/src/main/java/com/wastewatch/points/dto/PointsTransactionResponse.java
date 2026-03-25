package com.wastewatch.points.dto;

import com.wastewatch.points.enums.TransactionType;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class PointsTransactionResponse {

    private UUID id;
    private int points;                  // positive = credit, negative = debit
    private TransactionType type;
    private UUID referenceId;            // the report or redemption this relates to
    private OffsetDateTime createdAt;

    // Human readable label shown in the citizen app
    private String description;
}