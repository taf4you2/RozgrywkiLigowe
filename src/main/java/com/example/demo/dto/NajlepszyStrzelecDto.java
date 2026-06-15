package com.example.demo.dto;

public record NajlepszyStrzelecDto(
        Integer pilkarzId,
        String imie,
        String nazwisko,
        String klub,
        Long liczbaBramek
) {
}
