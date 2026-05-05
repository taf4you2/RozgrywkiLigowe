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
        return repository.save(uczestnictwo);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        repository.deleteById(id);
    }
}
