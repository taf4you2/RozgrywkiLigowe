package com.example.demo.controllers;

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
