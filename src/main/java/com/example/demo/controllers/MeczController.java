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
        if (mecz.getGospodarz() != null && mecz.getGosc() != null 
            && mecz.getGospodarz().getId().equals(mecz.getGosc().getId())) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.BAD_REQUEST, 
                "Gospodarz i gość nie mogą być tym samym klubem"
            );
        }
        return meczRepo.save(mecz);
    }

    @DeleteMapping("/{id}")
    public void usunMecz(@PathVariable Integer id) {
        meczRepo.deleteById(id);
    }
}