package com.wastewatch.points;

import com.wastewatch.auth.CurrentUser;
import com.wastewatch.common.ApiResponse;
import com.wastewatch.common.exceptions.ForbiddenException;
import com.wastewatch.points.dto.LeaderboardEntryResponse;
import com.wastewatch.points.dto.PointsBalanceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Points", description = "WastePoints balance, history and leaderboard")
@RestController
@RequiredArgsConstructor
public class PointsController {

    private final PointsService pointsService;
    private final CurrentUser currentUser;

    @Operation(summary = "Get points balance and transaction history",
            description = "Citizens can only view their own points.")
    // GET /citizens/:id/points — balance + full transaction history
    // Citizens can only view their own points
    @GetMapping("/citizens/{citizenId}/points")
    public ResponseEntity<ApiResponse<PointsBalanceResponse>> getPoints(
            @PathVariable UUID citizenId) {

        if (!currentUser.getId().equals(citizenId)) {
            throw new ForbiddenException(
                    "You can only view your own points.");
        }

        return ResponseEntity.ok(
                ApiResponse.ok(
                        pointsService.getBalanceAndHistory(citizenId)));
    }

    @Operation(summary = "Get LGA leaderboard",
            description = "Top 10 citizens by WastePoints in a Local Government Area. " +
                    "Public endpoint — no token required.")
    // GET /leaderboard/:lgaId — top 10 citizens in an LGA
    // Public endpoint — no JWT needed
    @GetMapping("/leaderboard/{lgaId}")
    public ResponseEntity<ApiResponse<List<LeaderboardEntryResponse>>> getLeaderboard(
            @PathVariable UUID lgaId) {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        pointsService.getLeaderboard(lgaId)));
    }
}