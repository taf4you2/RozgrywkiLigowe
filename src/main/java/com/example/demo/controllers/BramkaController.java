package com.example.demo.controllers;

import com.example.demo.entities.Bramka;
import com.example.demo.repositories.BramkaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping("/{id}")
    public void usunBramke(@PathVariable Integer id) {
        bramkaRepo.deleteById(id);
    }
}