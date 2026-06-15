package com.example.demo.repositories;

import com.example.demo.dto.NajlepszyStrzelecDto;
import com.example.demo.entities.Bramka;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.CrudRepository;

public interface BramkaRepository extends CrudRepository<Bramka, Integer> {
    @Query("""
            SELECT new com.example.demo.dto.NajlepszyStrzelecDto(
                p.id,
                p.imie,
                p.nazwisko,
                p.klub.nazwa,
                COUNT(b.id)
            )
            FROM Bramka b
            JOIN b.strzelec p
            WHERE (:sezonId IS NULL OR b.mecz.sezon.id = :sezonId)
              AND (:klubId IS NULL OR p.klub.id = :klubId)
              AND (b.samobojcza IS NULL OR b.samobojcza = false)
            GROUP BY p.id, p.imie, p.nazwisko, p.klub.nazwa
            ORDER BY COUNT(b.id) DESC, p.nazwisko ASC, p.imie ASC
            """)
    List<NajlepszyStrzelecDto> findNajlepsiStrzelcy(
            @Param("sezonId") Integer sezonId,
            @Param("klubId") Integer klubId
    );
}
