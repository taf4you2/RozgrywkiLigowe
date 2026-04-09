package com.example.demo.controllers;

import com.example.demo.entities.Sezon;
import com.example.demo.repositories.SezonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}