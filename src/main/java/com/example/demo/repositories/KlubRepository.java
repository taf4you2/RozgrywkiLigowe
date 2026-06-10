package com.example.demo.repositories;

import com.example.demo.entities.Klub;
import org.springframework.data.repository.CrudRepository;

public interface KlubRepository extends CrudRepository<Klub, Integer> {
    boolean existsByNazwa(String nazwa);
    boolean existsByNazwaAndIdNot(String nazwa, Integer id);
}
