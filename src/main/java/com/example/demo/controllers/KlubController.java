package com.example.demo.controllers;

import com.example.demo.entities.Klub;
import com.example.demo.repositories.KlubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        return klubRepo.save(klub);
    }
}