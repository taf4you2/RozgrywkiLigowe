package com.example.demo.controllers;

import com.example.demo.entities.Klub;
import com.example.demo.repositories.KlubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/klub")
public class KlubController {

    @Autowired
    KlubRepository klubRepo;

    @GetMapping
    public Iterable<Klub> pobierzKluby() {
        return klubRepo.findAll();
    }

    @GetMapping("/{id}")
    public EntityModel<Klub> pobierzKlub(@PathVariable Integer id) {
        Klub klub = klubRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono klubu"));
        return dodajLinki(klub);
    }

    @PostMapping
    public EntityModel<Klub> dodajKlub(@RequestBody Klub klub) {
        if (klubRepo.existsByNazwa(klub.getNazwa())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Klub o takiej nazwie juz istnieje");
        }
        return dodajLinki(klubRepo.save(klub));
    }

    @PutMapping("/{id}")
    public EntityModel<Klub> aktualizujKlub(@PathVariable Integer id, @RequestBody Klub klub) {
        Klub istniejacy = klubRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono klubu"));
        if (klubRepo.existsByNazwaAndIdNot(klub.getNazwa(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Klub o takiej nazwie juz istnieje");
        }
        istniejacy.setNazwa(klub.getNazwa());
        return dodajLinki(klubRepo.save(istniejacy));
    }

    @DeleteMapping("/{id}")
    public void usunKlub(@PathVariable Integer id) {
        klubRepo.deleteById(id);
    }

    private EntityModel<Klub> dodajLinki(Klub klub) {
        EntityModel<Klub> model = EntityModel.of(klub);
        model.add(linkTo(methodOn(KlubController.class).pobierzKlub(klub.getId())).withSelfRel());
        model.add(linkTo(methodOn(KlubController.class).pobierzKluby()).withRel("kluby"));
        return model;
    }
}
