package com.wastewatch.points.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class LeaderboardEntryResponse {

    private int rank;
    private UUID citizenId;
    private String name;
    private int totalPoints;
    private int reportsSubmitted;
}