package com.example.demo.dto;

public record RankingWystepowDto(
        Integer pilkarzId,
        String imie,
        String nazwisko,
        String klub,
        Long liczbaWystepow
) {
}
