package com.wastewatch.points;

import com.wastewatch.citizens.CitizenRepository;
import com.wastewatch.common.exceptions.NotFoundException;
import com.wastewatch.points.dto.*;
import com.wastewatch.points.enums.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointsService {

    private final PointsRepository pointsRepository;
    private final CitizenRepository citizenRepository;
    private final PointsMapper pointsMapper;

    // ─────────────────────────────────────────
    // CREDIT — add points to a citizen account
    // ─────────────────────────────────────────
    @Transactional
    public void credit(UUID citizenId, int amount,
                       TransactionType type, UUID referenceId) {
        PointsEntity entry = new PointsEntity();
        entry.setCitizenId(citizenId);
        entry.setPoints(amount);
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
        entry.setPoints(-amount);
        entry.setTransactionType(type);
        entry.setReferenceId(referenceId);
        pointsRepository.save(entry);
        log.info("Debited {} points from citizen {} for {}",
                amount, citizenId, type);
    }

    // ─────────────────────────────────────────
    // BALANCE + HISTORY
    // ─────────────────────────────────────────
    public PointsBalanceResponse getBalanceAndHistory(UUID citizenId) {
        // Confirm citizen exists
        citizenRepository.findById(citizenId)
                .orElseThrow(() -> new NotFoundException(
                        "Citizen not found: " + citizenId));

        int balance = pointsRepository.sumPointsByCitizenId(citizenId);

        List<PointsTransactionResponse> transactions = pointsRepository
                .findByCitizenIdOrderByCreatedAtDesc(citizenId)
                .stream()
                .map(pointsMapper::toResponse)
                .collect(Collectors.toList());

        return PointsBalanceResponse.builder()
                .balance(balance)
                .transactions(transactions)
                .build();
    }

    // ─────────────────────────────────────────
    // RAW BALANCE — used internally by
    // RedemptionService before debiting
    // ─────────────────────────────────────────
    public int getBalance(UUID citizenId) {
        return pointsRepository.sumPointsByCitizenId(citizenId);
    }

    // ─────────────────────────────────────────
    // LEADERBOARD — top 10 citizens in an LGA
    // ─────────────────────────────────────────
    public List<LeaderboardEntryResponse> getLeaderboard(UUID lgaId) {
        List<Object[]> rows = citizenRepository
                .findTop10ByLgaIdOrderByTotalPointsDesc(lgaId);

        AtomicInteger rank = new AtomicInteger(1);

        return rows.stream()
                .map(row -> LeaderboardEntryResponse.builder()
                        .rank(rank.getAndIncrement())
                        .citizenId((UUID) row[0])
                        .name((String) row[1])
                        .totalPoints((Integer) row[2])
                        .build())
                .collect(Collectors.toList());
    }
}