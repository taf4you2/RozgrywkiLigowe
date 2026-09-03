package com.example.demo.controllers;

import com.example.demo.entities.Mecz;
import com.example.demo.repositories.MeczRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/mecz")
public class MeczController {

    @Autowired
    MeczRepository meczRepo;

    @GetMapping
    public Iterable<Mecz> pobierzMecze() {
        return meczRepo.findAll();
    }

    @GetMapping("/{id}")
    public EntityModel<Mecz> pobierzMecz(@PathVariable Integer id) {
        Mecz mecz = meczRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono meczu"));
        return dodajLinki(mecz);
    }

    @PostMapping
    public EntityModel<Mecz> dodajMecz(@RequestBody Mecz mecz) {
        sprawdzMecz(mecz);
        return dodajLinki(meczRepo.save(mecz));
    }

    @PutMapping("/{id}")
    public EntityModel<Mecz> aktualizujMecz(@PathVariable Integer id, @RequestBody Mecz mecz) {
        Mecz istniejacy = meczRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono meczu"));
        sprawdzMecz(mecz);
        istniejacy.setDataRozpoczecia(mecz.getDataRozpoczecia());
        istniejacy.setSezon(mecz.getSezon());
        istniejacy.setGospodarz(mecz.getGospodarz());
        istniejacy.setGosc(mecz.getGosc());
        return dodajLinki(meczRepo.save(istniejacy));
    }

    @DeleteMapping("/{id}")
    public void usunMecz(@PathVariable Integer id) {
        meczRepo.deleteById(id);
    }

    private EntityModel<Mecz> dodajLinki(Mecz mecz) {
        EntityModel<Mecz> model = EntityModel.of(mecz);
        model.add(linkTo(methodOn(MeczController.class).pobierzMecz(mecz.getId())).withSelfRel());
        model.add(linkTo(methodOn(MeczController.class).pobierzMecze()).withRel("mecze"));
        if (mecz.getSezon() != null && mecz.getSezon().getId() != null) {
            model.add(Link.of("/sezon/" + mecz.getSezon().getId()).withRel("sezon"));
        }
        if (mecz.getGospodarz() != null && mecz.getGospodarz().getId() != null) {
            model.add(Link.of("/klub/" + mecz.getGospodarz().getId()).withRel("gospodarz"));
        }
        if (mecz.getGosc() != null && mecz.getGosc().getId() != null) {
            model.add(Link.of("/klub/" + mecz.getGosc().getId()).withRel("gosc"));
        }
        return model;
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
