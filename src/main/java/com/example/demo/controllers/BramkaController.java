package com.example.demo.controllers;

import com.example.demo.entities.Bramka;
import com.example.demo.repositories.BramkaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/bramka")
public class BramkaController {

    @Autowired
    BramkaRepository bramkaRepo;

    @GetMapping
    public Iterable<Bramka> pobierzBramki() {
        return bramkaRepo.findAll();
    }

    @PostMapping
    public Bramka dodajBramke(@RequestBody Bramka bramka) {
        return bramkaRepo.save(bramka);
    }

    @PutMapping("/{id}")
    public Bramka aktualizujBramke(@PathVariable Integer id, @RequestBody Bramka bramka) {
        Bramka istniejaca = bramkaRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono bramki"));
        przepiszBramke(istniejaca, bramka);
        return bramkaRepo.save(istniejaca);
    }

    @PatchMapping("/{id}")
    public Bramka czesciowoAktualizujBramke(@PathVariable Integer id, @RequestBody Bramka bramka) {
        Bramka istniejaca = bramkaRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono bramki"));
        if (bramka.getMinuta() != null) {
            istniejaca.setMinuta(bramka.getMinuta());
        }
        if (bramka.getSamobojcza() != null) {
            istniejaca.setSamobojcza(bramka.getSamobojcza());
        }
        if (bramka.getMecz() != null) {
            istniejaca.setMecz(bramka.getMecz());
        }
        if (bramka.getStrzelec() != null) {
            istniejaca.setStrzelec(bramka.getStrzelec());
        }
        if (bramka.getAsystujacy() != null) {
            istniejaca.setAsystujacy(bramka.getAsystujacy());
        }
        return bramkaRepo.save(istniejaca);
    }

    @DeleteMapping("/{id}")
    public void usunBramke(@PathVariable Integer id) {
        bramkaRepo.deleteById(id);
    }

    private void przepiszBramke(Bramka cel, Bramka zrodlo) {
        cel.setMinuta(zrodlo.getMinuta());
        cel.setSamobojcza(zrodlo.getSamobojcza());
        cel.setMecz(zrodlo.getMecz());
        cel.setStrzelec(zrodlo.getStrzelec());
        cel.setAsystujacy(zrodlo.getAsystujacy());
    }
}
