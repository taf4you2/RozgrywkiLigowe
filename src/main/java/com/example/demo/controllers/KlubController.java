package com.example.demo.controllers;

import com.example.demo.entities.Klub;
import com.example.demo.repositories.KlubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/klub")
public class KlubController {

    @Autowired
    KlubRepository klubRepo;

    @GetMapping
    public Iterable<Klub> pobierzKluby() {
        return klubRepo.findAll();
    }

    @PostMapping
    public Klub dodajKlub(@RequestBody Klub klub) {
        if (klubRepo.existsByNazwa(klub.getNazwa())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Klub o takiej nazwie juz istnieje");
        }
        return klubRepo.save(klub);
    }

    @PutMapping("/{id}")
    public Klub aktualizujKlub(@PathVariable Integer id, @RequestBody Klub klub) {
        Klub istniejacy = klubRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono klubu"));
        if (klubRepo.existsByNazwaAndIdNot(klub.getNazwa(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Klub o takiej nazwie juz istnieje");
        }
        istniejacy.setNazwa(klub.getNazwa());
        return klubRepo.save(istniejacy);
    }

    @DeleteMapping("/{id}")
    public void usunKlub(@PathVariable Integer id) {
        klubRepo.deleteById(id);
    }
}
