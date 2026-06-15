package com.example.demo.controllers;

import com.example.demo.dto.TransferPilkarzaRequest;
import com.example.demo.entities.Klub;
import com.example.demo.entities.Pilkarz;
import com.example.demo.repositories.KlubRepository;
import com.example.demo.repositories.PilkarzRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/pilkarz")
public class PilkarzController {

    @Autowired
    PilkarzRepository pilkarzRepo;

    @Autowired
    KlubRepository klubRepo;

    @GetMapping
    public Iterable<Pilkarz> pobierzPilkarzy() {
        return pilkarzRepo.findAll();
    }

    @PostMapping
    public Pilkarz dodajPilkarza(@RequestBody Pilkarz pilkarz) {
        sprawdzKlub(pilkarz);
        return pilkarzRepo.save(pilkarz);
    }

    @PatchMapping("/{id}/transfer")
    public Pilkarz przeniesPilkarza(@PathVariable Integer id, @RequestBody TransferPilkarzaRequest request) {
        if (request == null || request.nowyKlubId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Brak id nowego klubu");
        }
        Pilkarz pilkarz = pilkarzRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono pilkarza"));
        Klub nowyKlub = klubRepo.findById(request.nowyKlubId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono klubu"));
        if (pilkarz.getKlub() != null && request.nowyKlubId().equals(pilkarz.getKlub().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pilkarz jest juz w tym klubie");
        }
        pilkarz.setKlub(nowyKlub);
        return pilkarzRepo.save(pilkarz);
    }

    @DeleteMapping("/{id}")
    public void usunPilkarza(@PathVariable Integer id) {
        pilkarzRepo.deleteById(id);
    }

    private void sprawdzKlub(Pilkarz pilkarz) {
        if (pilkarz.getKlub() == null || pilkarz.getKlub().getId() == null
                || !klubRepo.existsById(pilkarz.getKlub().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Niepoprawny klub pilkarza");
        }
    }
}
