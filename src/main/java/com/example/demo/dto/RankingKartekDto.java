package com.example.demo.dto;

public record RankingKartekDto(
        Integer pilkarzId,
        String imie,
        String nazwisko,
        String klub,
        Long zolteKartki,
        Long czerwoneKartki,
        Long punktyKarne
) {
}
