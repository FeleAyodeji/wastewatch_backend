package com.wastewatch.reports.dto;

import com.wastewatch.reports.enums.WasteCategory;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateReportRequest {

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0") @DecimalMax(value = "90.0")
    private BigDecimal lat;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0")
    private BigDecimal lng;

    @NotNull(message = "Waste category is required")
    private WasteCategory wasteCategory;

    // Optional — frontend may send this for UI preview but we ignore it
    // Health risk is ALWAYS computed server-side in ReportService
    private String healthRisk; // intentionally String — we discard it

    private Boolean isAnonymous = false;

    // Photo is handled separately as multipart — not in this request body
}