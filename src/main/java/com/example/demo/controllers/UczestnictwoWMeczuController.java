package com.example.demo.controllers;

import com.example.demo.entities.UczestnictwoWMeczu;
import com.example.demo.repositories.UczestnictwoWMeczuRepository;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/uczestnictwo-wmeczu")
public class UczestnictwoWMeczuController {
    private final UczestnictwoWMeczuRepository repository;

    public UczestnictwoWMeczuController(UczestnictwoWMeczuRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<UczestnictwoWMeczu> getAll() {
        return (List<UczestnictwoWMeczu>) repository.findAll();
    }

    @GetMapping("/{id}")
    public EntityModel<UczestnictwoWMeczu> getOne(@PathVariable Integer id) {
        UczestnictwoWMeczu uczestnictwo = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono uczestnictwa"));
        return dodajLinki(uczestnictwo);
    }

    @PostMapping
    public EntityModel<UczestnictwoWMeczu> create(@RequestBody UczestnictwoWMeczu uczestnictwo) {
        sprawdzCzasGry(uczestnictwo);
        sprawdzDuplikat(uczestnictwo, null);
        return dodajLinki(repository.save(uczestnictwo));
    }

    @PutMapping("/{id}")
    public EntityModel<UczestnictwoWMeczu> update(@PathVariable Integer id, @RequestBody UczestnictwoWMeczu uczestnictwo) {
        UczestnictwoWMeczu istniejace = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono uczestnictwa"));
        sprawdzCzasGry(uczestnictwo);
        sprawdzDuplikat(uczestnictwo, id);
        przepiszUczestnictwo(istniejace, uczestnictwo);
        return dodajLinki(repository.save(istniejace));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        repository.deleteById(id);
    }

    private EntityModel<UczestnictwoWMeczu> dodajLinki(UczestnictwoWMeczu uczestnictwo) {
        EntityModel<UczestnictwoWMeczu> model = EntityModel.of(uczestnictwo);
        model.add(linkTo(methodOn(UczestnictwoWMeczuController.class).getOne(uczestnictwo.getId())).withSelfRel());
        model.add(linkTo(methodOn(UczestnictwoWMeczuController.class).getAll()).withRel("uczestnictwa"));
        if (uczestnictwo.getMecz() != null && uczestnictwo.getMecz().getId() != null) {
            model.add(Link.of("/mecz/" + uczestnictwo.getMecz().getId()).withRel("mecz"));
        }
        if (uczestnictwo.getPilkarz() != null && uczestnictwo.getPilkarz().getId() != null) {
            model.add(Link.of("/pilkarz/" + uczestnictwo.getPilkarz().getId()).withRel("pilkarz"));
        }
        return model;
    }

    private void sprawdzCzasGry(UczestnictwoWMeczu uczestnictwo) {
        Integer wejscie = uczestnictwo.getMinutaWejscia();
        Integer zejscie = uczestnictwo.getMinutaZejscia();
        if (wejscie != null && zejscie != null && (wejscie < 0 || zejscie > 120 || wejscie >= zejscie)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Niepoprawny czas wejscia lub zejscia (wymagane: 0 <= wejscie < zejscie <= 120)"
            );
        }
    }

    private void sprawdzDuplikat(UczestnictwoWMeczu uczestnictwo, Integer aktualizowaneId) {
        if (uczestnictwo.getMecz() == null || uczestnictwo.getPilkarz() == null
                || uczestnictwo.getMecz().getId() == null || uczestnictwo.getPilkarz().getId() == null) {
            return;
        }
        Integer meczId = uczestnictwo.getMecz().getId();
        Integer pilkarzId = uczestnictwo.getPilkarz().getId();
        boolean istnieje = aktualizowaneId == null
                ? repository.existsByMeczIdAndPilkarzId(meczId, pilkarzId)
                : repository.existsByMeczIdAndPilkarzIdAndIdNot(meczId, pilkarzId, aktualizowaneId);
        if (istnieje) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ten zawodnik jest juz zarejestrowany w tym meczu");
        }
    }

    private void przepiszUczestnictwo(UczestnictwoWMeczu cel, UczestnictwoWMeczu zrodlo) {
        cel.setMecz(zrodlo.getMecz());
        cel.setPilkarz(zrodlo.getPilkarz());
        cel.setRola(zrodlo.getRola());
        cel.setMinutaWejscia(zrodlo.getMinutaWejscia());
        cel.setMinutaZejscia(zrodlo.getMinutaZejscia());
        cel.setZolteKartki(zrodlo.getZolteKartki());
        cel.setCzerwonaKartka(zrodlo.getCzerwonaKartka());
        cel.setFaule(zrodlo.getFaule());
    }
}
