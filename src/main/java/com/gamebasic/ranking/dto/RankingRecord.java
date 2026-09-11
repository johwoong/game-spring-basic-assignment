package com.gamebasic.ranking.dto;

import lombok.Getter;

@Getter
public class RankingRecord {
    private long id;
    private Player player;
    private Run run;
    private BossFight bossFight;
    private Deck deck;
}
