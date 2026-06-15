package com.example.demo.controllers;

import com.example.demo.dto.NajlepszyStrzelecDto;
import com.example.demo.dto.RankingKartekDto;
import com.example.demo.dto.RankingWystepowDto;
import com.example.demo.repositories.BramkaRepository;
import com.example.demo.repositories.UczestnictwoWMeczuRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/statystyki")
public class StatystykiController {

    private static final int DOMYSLNY_LIMIT = 10;

    private final BramkaRepository bramkaRepository;
    private final UczestnictwoWMeczuRepository uczestnictwoRepository;

    public StatystykiController(
            BramkaRepository bramkaRepository,
            UczestnictwoWMeczuRepository uczestnictwoRepository
    ) {
        this.bramkaRepository = bramkaRepository;
        this.uczestnictwoRepository = uczestnictwoRepository;
    }

    @GetMapping("/najlepsi-strzelcy")
    public List<NajlepszyStrzelecDto> najlepsiStrzelcy(
            @RequestParam(required = false) Integer sezonId,
            @RequestParam(required = false) Integer klubId,
            @RequestParam(required = false) Integer limit
    ) {
        return ogranicz(bramkaRepository.findNajlepsiStrzelcy(sezonId, klubId), limit);
    }

    @GetMapping("/ranking-kartek")
    public List<RankingKartekDto> rankingKartek(
            @RequestParam(required = false) Integer sezonId,
            @RequestParam(required = false) Integer klubId,
            @RequestParam(required = false) Integer limit
    ) {
        return ogranicz(uczestnictwoRepository.findRankingKartek(sezonId, klubId), limit);
    }

    @GetMapping("/najwiecej-wystepow")
    public List<RankingWystepowDto> najwiecejWystepow(
            @RequestParam(required = false) Integer sezonId,
            @RequestParam(required = false) Integer klubId,
            @RequestParam(required = false) Integer limit
    ) {
        return ogranicz(uczestnictwoRepository.findRankingWystepow(sezonId, klubId), limit);
    }

    private <T> List<T> ogranicz(List<T> wyniki, Integer limit) {
        int efektywnyLimit = limit == null ? DOMYSLNY_LIMIT : limit;
        if (efektywnyLimit <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Limit musi byc wiekszy od 0");
        }
        return wyniki.stream()
                .limit(efektywnyLimit)
                .toList();
    }
}
