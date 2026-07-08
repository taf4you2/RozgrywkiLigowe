package com.example.demo.controllers;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class ApiIndexController {

    @GetMapping({"/", "/api"})
    public RepresentationModel<?> apiIndex() {
        RepresentationModel<?> model = new RepresentationModel<>();
        model.add(linkTo(methodOn(KlubController.class).pobierzKluby()).withRel("kluby"));
        model.add(linkTo(methodOn(PilkarzController.class).pobierzPilkarzy()).withRel("pilkarze"));
        model.add(linkTo(methodOn(SezonController.class).pobierzSezony()).withRel("sezony"));
        model.add(linkTo(methodOn(MeczController.class).pobierzMecze()).withRel("mecze"));
        model.add(linkTo(methodOn(BramkaController.class).pobierzBramki()).withRel("bramki"));
        model.add(linkTo(methodOn(UczestnictwoWMeczuController.class).getAll()).withRel("uczestnictwa"));
        model.add(linkTo(StatystykiController.class).slash("najlepsi-strzelcy").withRel("najlepsi-strzelcy"));
        model.add(linkTo(StatystykiController.class).slash("ranking-kartek").withRel("ranking-kartek"));
        model.add(linkTo(StatystykiController.class).slash("najwiecej-wystepow").withRel("najwiecej-wystepow"));
        model.add(Link.of("/pilkarz/{id}/transfer").withRel("transfer-pilkarza"));
        model.add(Link.of("/v3/api-docs").withRel("openapi"));
        model.add(Link.of("/swagger-ui/index.html").withRel("swagger-ui"));
        return model;
    }
}
