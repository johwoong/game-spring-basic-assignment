package com.gamebasic.ranking.service;

import com.gamebasic.ranking.CardType;
import com.gamebasic.ranking.client.RankingClient;
import com.gamebasic.ranking.dto.RankingEntryResponse;
import com.gamebasic.ranking.dto.RankingRecord;
import com.gamebasic.ranking.dto.RankingResponse;
import com.gamebasic.ranking.dto.RankingSource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
@RequiredArgsConstructor
public class RankingService {

    private final RankingClient rankingClient;

    @Transactional
    public RankingResponse getRankings() {
        RankingSource source = rankingClient.fetch();
        List<RankingRecord> records = source.getRecords();

        List<RankingRecord> ranks = records.stream()
                .filter(r -> r.getRun().getStatus().equals("CLEARED")
                && r.getRun().getClearedFloor() == 10)
                .toList();
        int excludedCount = 0;
        List<RankingRecord> validranks = new ArrayList<>(ranks.stream()
                .filter(this::isValid).toList());
        validranks.sort(
                Comparator.comparingInt((RankingRecord r) -> r.getRun().getDurationSeconds())
                        .thenComparing((RankingRecord r) -> r.getRun().getFinalHp(), Comparator.reverseOrder())
                        .thenComparingLong((RankingRecord::getId))
        );
        excludedCount = ranks.size() - validranks.size();

        Set<String> seen = new HashSet<>();
        List<RankingRecord> uniqueRanks = new ArrayList<>();
        for (RankingRecord rank : validranks) {
            if (seen.add(rank.getPlayer().getId())) {
                uniqueRanks.add(rank);
            }
        }

        List<RankingEntryResponse> entries = new ArrayList<>();
        int rank = 1;
        for (RankingRecord record : uniqueRanks) {
            entries.add(new RankingEntryResponse(rank,
                    record.getPlayer().getName(),
                    record.getRun().getDurationSeconds(),
                    record.getRun().getFinalHp(),
                    record.getBossFight().getTotalTurns(),
                    record.getDeck().getCards().size()));
            rank++;
        }
        return new RankingResponse(
                source.getMeta().getSeason().getId(),
                records.size(),
                excludedCount,
                entries
        );
    }

    boolean isValid(RankingRecord r)
    {
        if (r == null || r.getRun() == null || r.getDeck() == null || r.getBossFight() == null) {
            return false;
        }

        // 조건 1: 클리어 시간
        boolean cond1 = r.getRun().getDurationSeconds() >= r.getRun().getClearedFloor() * 30;

        // 조건 2: 남은 HP
        boolean cond2 = r.getRun().getFinalHp() >= 1 && r.getRun().getFinalHp() <= 99;

        // 조건 3: 덱 크기
        var cards = r.getDeck().getCards();
        boolean cond3 = cards.size() >= 9
            && cards.size() <= 20
            && cards.size() == r.getDeck().getSize();

        // 조건 4: 카드 타입
        boolean cond4 = cards.stream().allMatch(card -> {
            try {
                CardType.valueOf(card.getCardType());
                return true;
            } catch (IllegalArgumentException | NullPointerException e) {
                return false;
            }
        });

        // 조건 5: 획득 층
        boolean cond5 = cards.stream().allMatch(card ->
                card.getAcquiredFloor() >= 0 && card.getAcquiredFloor() <= 9
        );

        // 조건 6: 보스 페이즈
        var phases = r.getBossFight().getPhases();
        boolean cond6 = false;
        if (phases != null && phases.size() == 3) {
            boolean isNameValid = "THRONE".equals(phases.get(0).getPhase())
                    && "UNBOUND".equals(phases.get(1).getPhase())
                    && "ECLIPSE".equals(phases.get(2).getPhase());

            boolean areTurnsValid = phases.get(0).getTurns() >= 1
                    && phases.get(1).getTurns() >= 1
                    && phases.get(2).getTurns() >= 1;

            int sumTurns = phases.get(0).getTurns()
                    + phases.get(1).getTurns()
                    + phases.get(2).getTurns();

            boolean isSumMatch = sumTurns == r.getBossFight().getTotalTurns();

            cond6 = isNameValid && areTurnsValid && isSumMatch;
        }

        // 조건 7: 마무리 카드
        String finishingCard = r.getBossFight().getFinishingCard();
        boolean cond7 = cards.stream().anyMatch(card ->
                card.getCardType() != null && card.getCardType().equals(finishingCard)
        );

        return cond1 && cond2 && cond3 && cond4 && cond5 && cond6 && cond7;
    }
}
