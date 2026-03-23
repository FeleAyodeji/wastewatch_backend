package com.wastewatch.points;

import com.wastewatch.points.enums.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointsService {

    private final PointsRepository pointsRepository;

    // ─────────────────────────────────────────
    // CREDIT — add points to a citizen account
    // ─────────────────────────────────────────
    @Transactional
    public void credit(UUID citizenId, int amount,
                       TransactionType type, UUID referenceId) {
        PointsEntity entry = new PointsEntity();
        entry.setCitizenId(citizenId);
        entry.setPoints(amount);           // positive = credit
        entry.setTransactionType(type);
        entry.setReferenceId(referenceId);
        pointsRepository.save(entry);
        log.info("Credited {} points to citizen {} for {}",
                amount, citizenId, type);
    }

    // ─────────────────────────────────────────
    // DEBIT — remove points from citizen account
    // ─────────────────────────────────────────
    @Transactional
    public void debit(UUID citizenId, int amount,
                      TransactionType type, UUID referenceId) {
        PointsEntity entry = new PointsEntity();
        entry.setCitizenId(citizenId);
        entry.setPoints(-amount);          // negative = debit
        entry.setTransactionType(type);
        entry.setReferenceId(referenceId);
        pointsRepository.save(entry);
        log.info("Debited {} points from citizen {} for {}",
                amount, citizenId, type);
    }

    // ─────────────────────────────────────────
    // BALANCE — sum all entries for a citizen
    // ─────────────────────────────────────────
    public int getBalance(UUID citizenId) {
        return pointsRepository.sumPointsByCitizenId(citizenId);
    }

    // ─────────────────────────────────────────
    // HISTORY — full ledger for a citizen
    // ─────────────────────────────────────────
    public List<PointsEntity> getHistory(UUID citizenId) {
        return pointsRepository
                .findByCitizenIdOrderByCreatedAtDesc(citizenId);
    }
}