package com.gamebasic.ranking.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class BossFight {
    private List<Phase> phases;
    private String finishingCard;
    private int totalTurns;
}
