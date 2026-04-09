package com.example.demo.controllers;

import com.example.demo.entities.Mecz;
import com.example.demo.repositories.MeczRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mecz")
public class MeczController {

    @Autowired
    MeczRepository meczRepo;

    @GetMapping
    public Iterable<Mecz> pobierzMecze() {
        return meczRepo.findAll();
    }

    @PostMapping
    public Mecz dodajMecz(@RequestBody Mecz mecz) {
        return meczRepo.save(mecz);
    }
}