package com.example.demo.controllers;

import com.example.demo.entities.Pilkarz;
import com.example.demo.repositories.PilkarzRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pilkarz")
public class PilkarzController {

    @Autowired
    PilkarzRepository pilkarzRepo;

    @GetMapping
    public Iterable<Pilkarz> pobierzPilkarzy() {
        return pilkarzRepo.findAll();
    }

    @PostMapping
    public Pilkarz dodajPilkarza(@RequestBody Pilkarz pilkarz) {
        return pilkarzRepo.save(pilkarz);
    }

    @DeleteMapping("/{id}")
    public void usunPilkarza(@PathVariable Integer id) {
        pilkarzRepo.deleteById(id);
    }
}