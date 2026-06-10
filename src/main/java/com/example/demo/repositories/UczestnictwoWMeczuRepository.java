package com.example.demo.repositories;
import com.example.demo.entities.UczestnictwoWMeczu;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UczestnictwoWMeczuRepository extends CrudRepository<UczestnictwoWMeczu, Integer> {
    boolean existsByMeczIdAndPilkarzId(Integer meczId, Integer pilkarzId);
    boolean existsByMeczIdAndPilkarzIdAndIdNot(Integer meczId, Integer pilkarzId, Integer id);
}
