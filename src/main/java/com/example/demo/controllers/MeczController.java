package com.example.demo.controllers;

import com.example.demo.entities.Mecz;
import com.example.demo.repositories.MeczRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
        sprawdzMecz(mecz);
        return meczRepo.save(mecz);
    }

    @PutMapping("/{id}")
    public Mecz aktualizujMecz(@PathVariable Integer id, @RequestBody Mecz mecz) {
        Mecz istniejacy = meczRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono meczu"));
        sprawdzMecz(mecz);
        istniejacy.setDataRozpoczecia(mecz.getDataRozpoczecia());
        istniejacy.setSezon(mecz.getSezon());
        istniejacy.setGospodarz(mecz.getGospodarz());
        istniejacy.setGosc(mecz.getGosc());
        return meczRepo.save(istniejacy);
    }

    @DeleteMapping("/{id}")
    public void usunMecz(@PathVariable Integer id) {
        meczRepo.deleteById(id);
    }

    private void sprawdzMecz(Mecz mecz) {
        if (mecz.getGospodarz() != null && mecz.getGosc() != null
                && mecz.getGospodarz().getId() != null
                && mecz.getGospodarz().getId().equals(mecz.getGosc().getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Gospodarz i gosc nie moga byc tym samym klubem"
            );
        }
    }
}
