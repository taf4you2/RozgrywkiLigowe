package com.example.demo.controllers;

import com.example.demo.entities.Bramka;
import com.example.demo.repositories.BramkaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/bramka")
public class BramkaController {

    @Autowired
    BramkaRepository bramkaRepo;

    @GetMapping
    public Iterable<Bramka> pobierzBramki() {
        return bramkaRepo.findAll();
    }

    @GetMapping("/{id}")
    public EntityModel<Bramka> pobierzBramke(@PathVariable Integer id) {
        Bramka bramka = bramkaRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono bramki"));
        return dodajLinki(bramka);
    }

    @PostMapping
    public EntityModel<Bramka> dodajBramke(@RequestBody Bramka bramka) {
        return dodajLinki(bramkaRepo.save(bramka));
    }

    @PutMapping("/{id}")
    public EntityModel<Bramka> aktualizujBramke(@PathVariable Integer id, @RequestBody Bramka bramka) {
        Bramka istniejaca = bramkaRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono bramki"));
        przepiszBramke(istniejaca, bramka);
        return dodajLinki(bramkaRepo.save(istniejaca));
    }

    @PatchMapping("/{id}")
    public EntityModel<Bramka> czesciowoAktualizujBramke(@PathVariable Integer id, @RequestBody Bramka bramka) {
        Bramka istniejaca = bramkaRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono bramki"));
        if (bramka.getMinuta() != null) {
            istniejaca.setMinuta(bramka.getMinuta());
        }
        if (bramka.getSamobojcza() != null) {
            istniejaca.setSamobojcza(bramka.getSamobojcza());
        }
        if (bramka.getMecz() != null) {
            istniejaca.setMecz(bramka.getMecz());
        }
        if (bramka.getStrzelec() != null) {
            istniejaca.setStrzelec(bramka.getStrzelec());
        }
        if (bramka.getAsystujacy() != null) {
            istniejaca.setAsystujacy(bramka.getAsystujacy());
        }
        return dodajLinki(bramkaRepo.save(istniejaca));
    }

    @DeleteMapping("/{id}")
    public void usunBramke(@PathVariable Integer id) {
        bramkaRepo.deleteById(id);
    }

    private EntityModel<Bramka> dodajLinki(Bramka bramka) {
        EntityModel<Bramka> model = EntityModel.of(bramka);
        model.add(linkTo(methodOn(BramkaController.class).pobierzBramke(bramka.getId())).withSelfRel());
        model.add(linkTo(methodOn(BramkaController.class).pobierzBramki()).withRel("bramki"));
        if (bramka.getMecz() != null && bramka.getMecz().getId() != null) {
            model.add(Link.of("/mecz/" + bramka.getMecz().getId()).withRel("mecz"));
        }
        if (bramka.getStrzelec() != null && bramka.getStrzelec().getId() != null) {
            model.add(Link.of("/pilkarz/" + bramka.getStrzelec().getId()).withRel("strzelec"));
        }
        if (bramka.getAsystujacy() != null && bramka.getAsystujacy().getId() != null) {
            model.add(Link.of("/pilkarz/" + bramka.getAsystujacy().getId()).withRel("asystujacy"));
        }
        return model;
    }

    private void przepiszBramke(Bramka cel, Bramka zrodlo) {
        cel.setMinuta(zrodlo.getMinuta());
        cel.setSamobojcza(zrodlo.getSamobojcza());
        cel.setMecz(zrodlo.getMecz());
        cel.setStrzelec(zrodlo.getStrzelec());
        cel.setAsystujacy(zrodlo.getAsystujacy());
    }
}
