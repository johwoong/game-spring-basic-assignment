package com.gamebasic.runcard.dto;

import lombok.Getter;

@Getter
public class DeckCountResponse {

    private final Long gameId;
    private final Long deckCount;

    public DeckCountResponse(Long gameId, Long deckCount) {
        this.gameId = gameId;
        this.deckCount = deckCount;
    }

}
