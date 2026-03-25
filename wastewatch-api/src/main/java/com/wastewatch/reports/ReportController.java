package com.wastewatch.reports;

import com.wastewatch.auth.CurrentUser;
import com.wastewatch.common.ApiResponse;
import com.wastewatch.reports.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Reports", description = "Waste report submission and management")
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final CurrentUser currentUser;

    @Operation(
            summary = "Submit a new waste report",
            description = "Creates a new report. Health risk is assigned server-side " +
                    "based on waste category — any client-supplied value is ignored. " +
                    "Credits 50 WastePoints to the citizen atomically."
    )

    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201", description = "Report created"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Validation error"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "Missing or invalid token")
    })

    // POST /reports — citizen submits a new report
    @PostMapping
    public ResponseEntity<ApiResponse<ReportResponse>> submit(
            @Valid @RequestBody CreateReportRequest req) {

        ReportResponse response = reportService.create(req, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response));
    }

    @Operation(summary = "Get a report by ID")
    // GET /reports/:id — get one report by ID
    @GetMapping("/{reportId}")
    public ResponseEntity<ApiResponse<ReportResponse>> getById(
            @PathVariable UUID reportId) {

        return ResponseEntity.ok(
                ApiResponse.ok(reportService.getById(reportId)));
    }

    @Operation(summary = "Get current citizen's reports",
            description = "Returns all reports submitted by the authenticated citizen.")
    // GET /citizens/:id/reports — citizen's own report history
    @GetMapping("/citizens/{citizenId}/reports")
    public ResponseEntity<ApiResponse<List<ReportResponse>>> getByCitizen(
            @PathVariable UUID citizenId) {

        // Citizens can only fetch their own reports
        if (!currentUser.getId().equals(citizenId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.fail("FORBIDDEN",
                            "You can only view your own reports."));
        }

        return ResponseEntity.ok(
                ApiResponse.ok(reportService.getByCitizen(citizenId)));
    }

    @Operation(summary = "Upvote a report",
            description = "Citizens cannot upvote their own reports or vote twice.")
    // POST /reports/:id/upvote — citizen upvotes a report
    @PostMapping("/{reportId}/upvote")
    public ResponseEntity<ApiResponse<ReportResponse>> upvote(
            @PathVariable UUID reportId) {

        return ResponseEntity.ok(
                ApiResponse.ok(reportService.upvote(reportId,
                        currentUser.getId())));
    }

    @Operation(summary = "Get authority dashboard reports",
            description = "Returns all reports assigned to this authority, " +
                    "sorted by urgency.")
    // GET /authority/:id/reports — authority dashboard feed
    @GetMapping("/authority/{authorityId}/reports")
    public ResponseEntity<ApiResponse<List<ReportResponse>>> getByAuthority(
            @PathVariable UUID authorityId) {

        return ResponseEntity.ok(
                ApiResponse.ok(reportService.getByAuthority(authorityId)));
    }

    @Operation(summary = "Update report status",
            description = "Officers only. Valid transitions: " +
                    "SUBMITTED→ACKNOWLEDGED→IN_PROGRESS→RESOLVED. " +
                    "RESOLVED credits 25 bonus points to the reporter.")
    // PATCH /reports/:id/status — officer updates report status
    @PatchMapping("/{reportId}/status")
    public ResponseEntity<ApiResponse<ReportResponse>> updateStatus(
            @PathVariable UUID reportId,
            @Valid @RequestBody UpdateStatusRequest req) {

        return ResponseEntity.ok(
                ApiResponse.ok(reportService.updateStatus(
                        reportId, req, currentUser.getId())));
    }
}
