package com.wastewatch.reports;

import com.wastewatch.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "upvotes",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"citizen_id", "report_id"}
        ))
@Getter @Setter
@NoArgsConstructor
public class UpvoteEntity extends BaseEntity {

    @Column(name = "citizen_id", nullable = false)
    private UUID citizenId;

    @Column(name = "report_id", nullable = false)
    private UUID reportId;
}