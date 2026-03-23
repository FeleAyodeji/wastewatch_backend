package com.wastewatch.reports;

import com.wastewatch.auth.CurrentUser;
import com.wastewatch.common.ApiResponse;
import com.wastewatch.reports.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final CurrentUser currentUser;

    // POST /reports — citizen submits a new report
    @PostMapping
    public ResponseEntity<ApiResponse<ReportResponse>> submit(
            @Valid @RequestBody CreateReportRequest req) {

        ReportResponse response = reportService.create(req, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response));
    }

    // GET /reports/:id — get one report by ID
    @GetMapping("/{reportId}")
    public ResponseEntity<ApiResponse<ReportResponse>> getById(
            @PathVariable UUID reportId) {

        return ResponseEntity.ok(
                ApiResponse.ok(reportService.getById(reportId)));
    }

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

    // POST /reports/:id/upvote — citizen upvotes a report
    @PostMapping("/{reportId}/upvote")
    public ResponseEntity<ApiResponse<ReportResponse>> upvote(
            @PathVariable UUID reportId) {

        return ResponseEntity.ok(
                ApiResponse.ok(reportService.upvote(reportId,
                        currentUser.getId())));
    }

    // GET /authority/:id/reports — authority dashboard feed
    @GetMapping("/authority/{authorityId}/reports")
    public ResponseEntity<ApiResponse<List<ReportResponse>>> getByAuthority(
            @PathVariable UUID authorityId) {

        return ResponseEntity.ok(
                ApiResponse.ok(reportService.getByAuthority(authorityId)));
    }

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
