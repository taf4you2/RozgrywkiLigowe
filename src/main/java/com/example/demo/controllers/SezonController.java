package com.example.demo.controllers;

import com.example.demo.entities.Sezon;
import com.example.demo.repositories.SezonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/sezon")
public class SezonController {

    @Autowired
    SezonRepository sezonRepo;

    @GetMapping
    public Iterable<Sezon> pobierzSezony() {
        return sezonRepo.findAll();
    }

    @GetMapping("/{id}")
    public EntityModel<Sezon> pobierzSezon(@PathVariable Integer id) {
        Sezon sezon = sezonRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono sezonu"));
        return dodajLinki(sezon);
    }

    @PostMapping
    public EntityModel<Sezon> dodajSezon(@RequestBody Sezon sezon) {
        return dodajLinki(sezonRepo.save(sezon));
    }

    @PutMapping("/{id}")
    public EntityModel<Sezon> aktualizujSezon(@PathVariable Integer id, @RequestBody Sezon sezon) {
        Sezon istniejacy = sezonRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono sezonu"));
        istniejacy.setNazwa(sezon.getNazwa());
        return dodajLinki(sezonRepo.save(istniejacy));
    }

    @DeleteMapping("/{id}")
    public void usunSezon(@PathVariable Integer id) {
        sezonRepo.deleteById(id);
    }

    private EntityModel<Sezon> dodajLinki(Sezon sezon) {
        EntityModel<Sezon> model = EntityModel.of(sezon);
        model.add(linkTo(methodOn(SezonController.class).pobierzSezon(sezon.getId())).withSelfRel());
        model.add(linkTo(methodOn(SezonController.class).pobierzSezony()).withRel("sezony"));
        return model;
    }
}
