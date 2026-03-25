package com.wastewatch.points.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PointsBalanceResponse {

    private int balance;
    private List<PointsTransactionResponse> transactions;
}