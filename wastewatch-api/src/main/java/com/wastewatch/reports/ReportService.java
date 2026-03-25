package com.wastewatch.reports;

import com.wastewatch.common.exceptions.*;
import com.wastewatch.points.PointsService;
import com.wastewatch.reports.dto.*;
import com.wastewatch.reports.enums.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UpvoteRepository upvoteRepository;
    private final PointsService pointsService;
    private final ReportMapper mapper;
    private final EntityManager entityManager;

    // ─────────────────────────────────────────────
    // SUBMIT REPORT
    // @Transactional — report save + points credit
    // happen together or not at all
    // ─────────────────────────────────────────────
    @Transactional
    public ReportResponse create(CreateReportRequest req, UUID citizenId) {

        ReportEntity report = new ReportEntity();
        report.setCitizenId(citizenId);
        report.setLat(req.getLat());
        report.setLng(req.getLng());
        report.setWasteCategory(req.getWasteCategory());
        report.setIsAnonymous(req.getIsAnonymous() != null
                && req.getIsAnonymous());
        report.setStatus(ReportStatus.SUBMITTED);

        // ⚠️ Health risk is ALWAYS assigned here — never from the request
        report.setHealthRisk(assignRisk(req.getWasteCategory()));
        report.setHealthWarning(assignWarning(req.getWasteCategory()));

        //TODO Phase 8 — replace with real geocoding + authority assignment
        report.setAddress("Address lookup coming in Phase 8");
        report.setAuthorityId(null); // will be set by geo-assignment in Phase 8

        ReportEntity saved = reportRepository.save(report);

        // Credit 50 WastePoints — rolls back if this throws
        pointsService.credit(citizenId, 50,
                com.wastewatch.points.enums.TransactionType.SUBMISSION,
                saved.getId());

        entityManager.flush();
        entityManager.refresh(saved);

        log.info("Report {} submitted by citizen {}", saved.getId(), citizenId);
        return mapper.toResponse(saved);
    }

    // ─────────────────────────────────────────────
    // GET ONE REPORT
    // ─────────────────────────────────────────────
    public ReportResponse getById(UUID reportId) {
        return mapper.toResponse(findOrThrow(reportId));
    }

    // ─────────────────────────────────────────────
    // GET CITIZEN'S OWN REPORTS
    // ─────────────────────────────────────────────
    public List<ReportResponse> getByCitizen(UUID citizenId) {
        return reportRepository
                .findByCitizenIdOrderByCreatedAtDesc(citizenId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────
    // GET AUTHORITY DASHBOARD REPORTS
    // ─────────────────────────────────────────────
    public List<ReportResponse> getByAuthority(UUID authorityId) {
        return reportRepository
                .findByAuthorityIdOrderByHealthRiskAscCreatedAtDesc(authorityId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────
    // UPVOTE
    // @Transactional — upvote save + count increment
    // happen together or not at all
    // ─────────────────────────────────────────────
    @Transactional
    public ReportResponse upvote(UUID reportId, UUID citizenId) {
        ReportEntity report = findOrThrow(reportId);

        // Citizens cannot upvote their own report
        if (report.getCitizenId() != null
                && report.getCitizenId().equals(citizenId)) {
            throw new ForbiddenException(
                    "You cannot upvote your own report.");
        }

        // Prevent double voting — unique constraint in DB also enforces this
        if (upvoteRepository.existsByCitizenIdAndReportId(citizenId, reportId)) {
            throw new IllegalArgumentException(
                    "You have already upvoted this report.");
        }

        UpvoteEntity upvote = new UpvoteEntity();
        upvote.setCitizenId(citizenId);
        upvote.setReportId(reportId);
        upvoteRepository.save(upvote);

        report.setUpvoteCount(report.getUpvoteCount() + 1);

        // Trigger community alert when 10 upvotes reached
        if (report.getUpvoteCount() >= 10 && !report.getCommunityAlert()) {
            report.setCommunityAlert(true);
            log.info("Community alert triggered for report {}", reportId);
        }

        return mapper.toResponse(reportRepository.save(report));
    }

    // ─────────────────────────────────────────────
    // UPDATE STATUS (officer only)
    // ─────────────────────────────────────────────
    @Transactional
    public ReportResponse updateStatus(UUID reportId,
                                       UpdateStatusRequest req,
                                       UUID officerId) {
        ReportEntity report = findOrThrow(reportId);

        validateStatusTransition(report.getStatus(), req.getStatus());

        report.setStatus(req.getStatus());

        if (req.getStatus() == ReportStatus.RESOLVED) {
            report.setResolvedAt(java.time.OffsetDateTime.now());
            report.setResolutionPhotoUrl(req.getResolutionPhotoUrl());

            // Credit resolution bonus to the original reporter
            if (report.getCitizenId() != null) {
                pointsService.credit(
                        report.getCitizenId(), 25,
                        com.wastewatch.points.enums.TransactionType.RESOLUTION_BONUS,
                        report.getId());
            }
        }

        log.info("Report {} status updated to {} by officer {}",
                reportId, req.getStatus(), officerId);
        return mapper.toResponse(reportRepository.save(report));
    }

    // ─────────────────────────────────────────────
    // PRIVATE HELPERS
    // ─────────────────────────────────────────────

    private ReportEntity findOrThrow(UUID reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new NotFoundException(
                        "Report not found: " + reportId));
    }

    // Health risk is ALWAYS computed here — client value is ignored
    private HealthRisk assignRisk(WasteCategory category) {
        return switch (category) {
            case SEWAGE, HAZARDOUS, MEDICAL_WASTE -> HealthRisk.HIGH;
            case SOLID_WASTE, E_WASTE             -> HealthRisk.MEDIUM;
        };
    }

    private String assignWarning(WasteCategory category) {
        return switch (category) {
            case SEWAGE        -> "Cholera/Lassa fever risk";
            case HAZARDOUS     -> "Chemical exposure risk";
            case MEDICAL_WASTE -> "Infection/disease risk";
            case SOLID_WASTE   -> "Pest and rodent risk";
            case E_WASTE       -> "Heavy metal contamination risk";
        };
    }

    // Prevents illegal status jumps e.g. RESOLVED → SUBMITTED
    private void validateStatusTransition(ReportStatus current,
                                          ReportStatus next) {
        boolean valid = switch (current) {
            case SUBMITTED    -> next == ReportStatus.ACKNOWLEDGED
                    || next == ReportStatus.ESCALATED;
            case ACKNOWLEDGED -> next == ReportStatus.IN_PROGRESS
                    || next == ReportStatus.ESCALATED;
            case IN_PROGRESS  -> next == ReportStatus.RESOLVED
                    || next == ReportStatus.ESCALATED;
            case RESOLVED, ESCALATED -> false; // terminal states
        };

        if (!valid) {
            throw new IllegalArgumentException(
                    "Invalid status transition: "
                            + current + " → " + next);
        }
    }
}