package com.wastewatch.reports.dto;

import com.wastewatch.reports.enums.ReportStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStatusRequest {

    @NotNull(message = "Status is required")
    private ReportStatus status;

    // Officer uploads a photo when marking RESOLVED
    private String resolutionPhotoUrl;
}