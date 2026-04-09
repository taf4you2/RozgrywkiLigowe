package com.example.demo.repositories;

import com.example.demo.entities.Mecz;
import org.springframework.data.repository.CrudRepository;

public interface MeczRepository extends CrudRepository<Mecz, Integer> {
}