// citizens/CitizenRepository.java
package com.wastewatch.citizens;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface CitizenRepository extends JpaRepository<CitizenEntity, UUID> {
    Optional<CitizenEntity> findByEmail(String email);
}