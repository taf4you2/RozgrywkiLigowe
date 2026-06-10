package com.example.demo.controllers;

import com.example.demo.entities.Sezon;
import com.example.demo.repositories.SezonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/sezon")
public class SezonController {

    @Autowired
    SezonRepository sezonRepo;

    @GetMapping
    public Iterable<Sezon> pobierzSezony() {
        return sezonRepo.findAll();
    }

    @PostMapping
    public Sezon dodajSezon(@RequestBody Sezon sezon) {
        return sezonRepo.save(sezon);
    }

    @PutMapping("/{id}")
    public Sezon aktualizujSezon(@PathVariable Integer id, @RequestBody Sezon sezon) {
        Sezon istniejacy = sezonRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono sezonu"));
        istniejacy.setNazwa(sezon.getNazwa());
        return sezonRepo.save(istniejacy);
    }

    @DeleteMapping("/{id}")
    public void usunSezon(@PathVariable Integer id) {
        sezonRepo.deleteById(id);
    }
}
