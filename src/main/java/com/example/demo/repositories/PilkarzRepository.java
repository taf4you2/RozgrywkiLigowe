package com.example.demo.repositories;

import com.example.demo.entities.Pilkarz;
import org.springframework.data.repository.CrudRepository;

// Zapewnia dostęp do danych piłkarzy w bazie.
public interface PilkarzRepository extends CrudRepository<Pilkarz, Integer> {
}