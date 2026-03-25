package com.wastewatch.points;

import com.wastewatch.points.dto.PointsTransactionResponse;
import com.wastewatch.points.enums.TransactionType;
import org.springframework.stereotype.Component;

@Component
public class PointsMapper {

    public PointsTransactionResponse toResponse(PointsEntity entity) {
        return PointsTransactionResponse.builder()
                .id(entity.getId())
                .points(entity.getPoints())
                .type(entity.getTransactionType())
                .referenceId(entity.getReferenceId())
                .createdAt(entity.getCreatedAt())
                .description(buildDescription(entity))
                .build();
    }

    private String buildDescription(PointsEntity entity) {
        return switch (entity.getTransactionType()) {
            case SUBMISSION        -> "+50 pts — Report submitted";
            case RESOLUTION_BONUS  -> "+25 pts — Your report was resolved";
            case UPVOTE            -> "+10 pts — Report upvoted";
            case REDEMPTION        -> entity.getPoints() + " pts — Airtime redeemed";
            case REVERSAL          -> "Redemption reversed — points restored";
        };
    }
}