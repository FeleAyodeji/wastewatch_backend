// redemptions/RedemptionRepository.java
package com.wastewatch.redemptions;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface RedemptionRepository extends JpaRepository<RedemptionEntity, UUID> {
    List<RedemptionEntity> findByCitizenIdOrderByCreatedAtDesc(UUID citizenId);
}