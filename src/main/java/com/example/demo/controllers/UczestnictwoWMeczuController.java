package com.example.demo.controllers;
import com.example.demo.entities.UczestnictwoWMeczu;
import com.example.demo.repositories.UczestnictwoWMeczuRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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

    @PostMapping
    public UczestnictwoWMeczu create(@RequestBody UczestnictwoWMeczu uczestnictwo) {
        Integer wejscie = uczestnictwo.getMinutaWejscia();
        Integer zejscie = uczestnictwo.getMinutaZejscia();
        if (wejscie != null && zejscie != null) {
            if (wejscie < 0 || zejscie > 120 || wejscie >= zejscie) {
                throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, 
                    "Niepoprawny czas wejścia lub zejścia (wymagane: 0 <= wejscie < zejscie <= 120)"
                );
            }
        }
        if (uczestnictwo.getMecz() != null && uczestnictwo.getPilkarz() != null) {
            if (repository.existsByMeczIdAndPilkarzId(uczestnictwo.getMecz().getId(), uczestnictwo.getPilkarz().getId())) {
                throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, 
                    "Ten zawodnik jest już zarejestrowany w tym meczu"
                );
            }
        }
        return repository.save(uczestnictwo);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        repository.deleteById(id);
    }
}
