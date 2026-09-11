package com.gamebasic.ranking.dto;

import lombok.Getter;

@Getter
public class Meta {
    private Season season;
    private String generatedAt;
    private int schemaVersion;
    private int totalRecords;
}
