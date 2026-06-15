package com.example.demo.repositories;
import com.example.demo.dto.RankingKartekDto;
import com.example.demo.dto.RankingWystepowDto;
import com.example.demo.entities.UczestnictwoWMeczu;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UczestnictwoWMeczuRepository extends CrudRepository<UczestnictwoWMeczu, Integer> {
    boolean existsByMeczIdAndPilkarzId(Integer meczId, Integer pilkarzId);
    boolean existsByMeczIdAndPilkarzIdAndIdNot(Integer meczId, Integer pilkarzId, Integer id);

    @Query("""
            SELECT new com.example.demo.dto.RankingKartekDto(
                p.id,
                p.imie,
                p.nazwisko,
                p.klub.nazwa,
                SUM(COALESCE(u.zolteKartki, 0)),
                SUM(CASE WHEN u.czerwonaKartka = true THEN 1 ELSE 0 END),
                SUM(COALESCE(u.zolteKartki, 0)) + SUM(CASE WHEN u.czerwonaKartka = true THEN 3 ELSE 0 END)
            )
            FROM UczestnictwoWMeczu u
            JOIN u.pilkarz p
            WHERE (:sezonId IS NULL OR u.mecz.sezon.id = :sezonId)
              AND (:klubId IS NULL OR p.klub.id = :klubId)
            GROUP BY p.id, p.imie, p.nazwisko, p.klub.nazwa
            ORDER BY SUM(COALESCE(u.zolteKartki, 0)) + SUM(CASE WHEN u.czerwonaKartka = true THEN 3 ELSE 0 END) DESC,
                     SUM(CASE WHEN u.czerwonaKartka = true THEN 1 ELSE 0 END) DESC,
                     SUM(COALESCE(u.zolteKartki, 0)) DESC,
                     p.nazwisko ASC,
                     p.imie ASC
            """)
    List<RankingKartekDto> findRankingKartek(
            @Param("sezonId") Integer sezonId,
            @Param("klubId") Integer klubId
    );

    @Query("""
            SELECT new com.example.demo.dto.RankingWystepowDto(
                p.id,
                p.imie,
                p.nazwisko,
                p.klub.nazwa,
                COUNT(u.id)
            )
            FROM UczestnictwoWMeczu u
            JOIN u.pilkarz p
            WHERE (:sezonId IS NULL OR u.mecz.sezon.id = :sezonId)
              AND (:klubId IS NULL OR p.klub.id = :klubId)
            GROUP BY p.id, p.imie, p.nazwisko, p.klub.nazwa
            ORDER BY COUNT(u.id) DESC, p.nazwisko ASC, p.imie ASC
            """)
    List<RankingWystepowDto> findRankingWystepow(
            @Param("sezonId") Integer sezonId,
            @Param("klubId") Integer klubId
    );
}
