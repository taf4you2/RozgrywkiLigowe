package com.example.demo;

import com.example.demo.entities.Bramka;
import com.example.demo.entities.Klub;
import com.example.demo.entities.Mecz;
import com.example.demo.entities.Pilkarz;
import com.example.demo.entities.Sezon;
import com.example.demo.entities.UczestnictwoWMeczu;
import com.example.demo.repositories.BramkaRepository;
import com.example.demo.repositories.KlubRepository;
import com.example.demo.repositories.MeczRepository;
import com.example.demo.repositories.PilkarzRepository;
import com.example.demo.repositories.SezonRepository;
import com.example.demo.repositories.UczestnictwoWMeczuRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:statystyki_endpointy_test",
        "spring.jpa.show-sql=false"
})
@AutoConfigureMockMvc
public class StatystykiEndpointyTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KlubRepository klubRepository;

    @Autowired
    private PilkarzRepository pilkarzRepository;

    @Autowired
    private SezonRepository sezonRepository;

    @Autowired
    private MeczRepository meczRepository;

    @Autowired
    private BramkaRepository bramkaRepository;

    @Autowired
    private UczestnictwoWMeczuRepository uczestnictwoRepository;

    // 7. Scenariusze dla statystyk (/statystyki)
    // Test 7.1: Pobieranie rankingu najlepszych strzelcow
    @Test
    public void testNajlepsiStrzelcyZLimitemIFiltrowaniem() throws Exception {
        Sezon sezon = zapiszSezon("Statystyki gole 2026");
        Sezon innySezon = zapiszSezon("Statystyki gole inny sezon");
        Klub klubA = zapiszKlub("Statystyki gole A");
        Klub klubB = zapiszKlub("Statystyki gole B");
        Pilkarz lider = zapiszPilkarza("Adam", "Strzelec", klubA);
        Pilkarz drugi = zapiszPilkarza("Bartosz", "Rezerwowy", klubB);
        Pilkarz zInnegoSezonu = zapiszPilkarza("Cezary", "PozaSezonem", klubA);
        Mecz mecz = zapiszMecz(sezon, klubA, klubB);
        Mecz innyMecz = zapiszMecz(innySezon, klubA, klubB);

        zapiszBramki(mecz, lider, 3, false);
        zapiszBramki(mecz, drugi, 1, false);
        zapiszBramki(mecz, lider, 1, true);
        zapiszBramki(innyMecz, zInnegoSezonu, 5, false);

        mockMvc.perform(get("/statystyki/najlepsi-strzelcy")
                .param("sezonId", sezon.getId().toString())
                .param("limit", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].pilkarzId").value(lider.getId()))
                .andExpect(jsonPath("$[0].imie").value("Adam"))
                .andExpect(jsonPath("$[0].nazwisko").value("Strzelec"))
                .andExpect(jsonPath("$[0].klub").value("Statystyki gole A"))
                .andExpect(jsonPath("$[0].liczbaBramek").value(3));

        // Test 7.2: Filtrowanie najlepszych strzelcow i pomijanie bramek samobojczych
        mockMvc.perform(get("/statystyki/najlepsi-strzelcy")
                .param("sezonId", sezon.getId().toString())
                .param("klubId", klubB.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].pilkarzId").value(drugi.getId()))
                .andExpect(jsonPath("$[0].liczbaBramek").value(1));

        // Test 7.5: Odrzucenie niepoprawnej wartosci parametru limit
        mockMvc.perform(get("/statystyki/najlepsi-strzelcy")
                .param("limit", "0"))
                .andExpect(status().isBadRequest());
    }

    // Test 7.3: Pobieranie rankingu kartek
    @Test
    public void testRankingKartekZwracaPoprawnaKolejnoscISumy() throws Exception {
        Sezon sezon = zapiszSezon("Statystyki kartki 2026");
        Sezon innySezon = zapiszSezon("Statystyki kartki inny sezon");
        Klub klubA = zapiszKlub("Statystyki kartki A");
        Klub klubB = zapiszKlub("Statystyki kartki B");
        Pilkarz lider = zapiszPilkarza("Damian", "Ostry", klubA);
        Pilkarz drugi = zapiszPilkarza("Ernest", "Waleczny", klubB);
        Pilkarz zInnegoSezonu = zapiszPilkarza("Filip", "PozaSezonem", klubA);
        Mecz mecz = zapiszMecz(sezon, klubA, klubB);
        Mecz innyMecz = zapiszMecz(innySezon, klubA, klubB);

        zapiszUczestnictwo(mecz, lider, 2, true);
        zapiszUczestnictwo(mecz, drugi, 4, false);
        zapiszUczestnictwo(innyMecz, zInnegoSezonu, 10, true);

        mockMvc.perform(get("/statystyki/ranking-kartek")
                .param("sezonId", sezon.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].pilkarzId").value(lider.getId()))
                .andExpect(jsonPath("$[0].zolteKartki").value(2))
                .andExpect(jsonPath("$[0].czerwoneKartki").value(1))
                .andExpect(jsonPath("$[0].punktyKarne").value(5))
                .andExpect(jsonPath("$[1].pilkarzId").value(drugi.getId()))
                .andExpect(jsonPath("$[1].zolteKartki").value(4))
                .andExpect(jsonPath("$[1].czerwoneKartki").value(0))
                .andExpect(jsonPath("$[1].punktyKarne").value(4));

        mockMvc.perform(get("/statystyki/ranking-kartek")
                .param("sezonId", sezon.getId().toString())
                .param("klubId", klubB.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].pilkarzId").value(drugi.getId()));
    }

    // Test 7.4: Pobieranie rankingu najwiekszej liczby wystepow
    @Test
    public void testNajwiecejWystepowZwracaRankingIFiltrKlubu() throws Exception {
        Sezon sezon = zapiszSezon("Statystyki wystepy 2026");
        Sezon innySezon = zapiszSezon("Statystyki wystepy inny sezon");
        Klub klubA = zapiszKlub("Statystyki wystepy A");
        Klub klubB = zapiszKlub("Statystyki wystepy B");
        Pilkarz lider = zapiszPilkarza("Grzegorz", "Regularny", klubA);
        Pilkarz drugi = zapiszPilkarza("Hubert", "Zmiennik", klubB);
        Pilkarz zInnegoSezonu = zapiszPilkarza("Igor", "PozaSezonem", klubA);
        Mecz mecz1 = zapiszMecz(sezon, klubA, klubB);
        Mecz mecz2 = zapiszMecz(sezon, klubB, klubA);
        Mecz mecz3 = zapiszMecz(sezon, klubA, klubB);
        Mecz innyMecz = zapiszMecz(innySezon, klubA, klubB);

        zapiszUczestnictwo(mecz1, lider, 0, false);
        zapiszUczestnictwo(mecz2, lider, 0, false);
        zapiszUczestnictwo(mecz3, lider, 1, false);
        zapiszUczestnictwo(mecz1, drugi, 1, false);
        zapiszUczestnictwo(mecz2, drugi, 0, false);
        zapiszUczestnictwo(innyMecz, zInnegoSezonu, 0, false);

        mockMvc.perform(get("/statystyki/najwiecej-wystepow")
                .param("sezonId", sezon.getId().toString())
                .param("limit", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].pilkarzId").value(lider.getId()))
                .andExpect(jsonPath("$[0].liczbaWystepow").value(3));

        mockMvc.perform(get("/statystyki/najwiecej-wystepow")
                .param("sezonId", sezon.getId().toString())
                .param("klubId", klubB.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].pilkarzId").value(drugi.getId()))
                .andExpect(jsonPath("$[0].liczbaWystepow").value(2));
    }

    // 8. Scenariusze transferu pilkarza (/pilkarz/{id}/transfer)
    // Test 8.1: Przeniesienie pilkarza do innego klubu
    @Test
    public void testTransferPilkarzaPrzenosiDoInnegoKlubuIWalidujeBledy() throws Exception {
        Klub staryKlub = zapiszKlub("Transfer stary klub");
        Klub nowyKlub = zapiszKlub("Transfer nowy klub");
        Pilkarz pilkarz = zapiszPilkarza("Jakub", "Transferowy", staryKlub);

        mockMvc.perform(patch("/pilkarz/" + pilkarz.getId() + "/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("nowyKlubId", nowyKlub.getId()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(pilkarz.getId()))
                .andExpect(jsonPath("$.klub.id").value(nowyKlub.getId()))
                .andExpect(jsonPath("$.klub.nazwa").value("Transfer nowy klub"));

        Pilkarz poTransferze = pilkarzRepository.findById(pilkarz.getId()).orElseThrow();
        assertEquals(nowyKlub.getId(), poTransferze.getKlub().getId());

        // Test 8.2: Proba transferu do tego samego klubu
        mockMvc.perform(patch("/pilkarz/" + pilkarz.getId() + "/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("nowyKlubId", nowyKlub.getId()))))
                .andExpect(status().isBadRequest());

        // Test 8.3: Transfer nieistniejacego pilkarza
        mockMvc.perform(patch("/pilkarz/999999/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("nowyKlubId", nowyKlub.getId()))))
                .andExpect(status().isNotFound());

        // Test 8.4: Transfer do nieistniejacego klubu
        mockMvc.perform(patch("/pilkarz/" + pilkarz.getId() + "/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("nowyKlubId", 999999))))
                .andExpect(status().isNotFound());

        // Test 8.5: Brak identyfikatora nowego klubu
        mockMvc.perform(patch("/pilkarz/" + pilkarz.getId() + "/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    private Sezon zapiszSezon(String nazwa) {
        Sezon sezon = new Sezon();
        sezon.setNazwa(nazwa);
        return sezonRepository.save(sezon);
    }

    private Klub zapiszKlub(String nazwa) {
        Klub klub = new Klub();
        klub.setNazwa(nazwa);
        return klubRepository.save(klub);
    }

    private Pilkarz zapiszPilkarza(String imie, String nazwisko, Klub klub) {
        Pilkarz pilkarz = new Pilkarz();
        pilkarz.setImie(imie);
        pilkarz.setNazwisko(nazwisko);
        pilkarz.setKlub(klub);
        return pilkarzRepository.save(pilkarz);
    }

    private Mecz zapiszMecz(Sezon sezon, Klub gospodarz, Klub gosc) {
        Mecz mecz = new Mecz();
        mecz.setSezon(sezon);
        mecz.setGospodarz(gospodarz);
        mecz.setGosc(gosc);
        mecz.setDataRozpoczecia(LocalDateTime.now());
        return meczRepository.save(mecz);
    }

    private void zapiszBramki(Mecz mecz, Pilkarz strzelec, int liczba, boolean samobojcza) {
        for (int i = 0; i < liczba; i++) {
            Bramka bramka = new Bramka();
            bramka.setMecz(mecz);
            bramka.setStrzelec(strzelec);
            bramka.setMinuta(String.valueOf(10 + i));
            bramka.setSamobojcza(samobojcza);
            bramkaRepository.save(bramka);
        }
    }

    private UczestnictwoWMeczu zapiszUczestnictwo(
            Mecz mecz,
            Pilkarz pilkarz,
            Integer zolteKartki,
            Boolean czerwonaKartka
    ) {
        UczestnictwoWMeczu uczestnictwo = new UczestnictwoWMeczu();
        uczestnictwo.setMecz(mecz);
        uczestnictwo.setPilkarz(pilkarz);
        uczestnictwo.setMinutaWejscia(0);
        uczestnictwo.setMinutaZejscia(90);
        uczestnictwo.setZolteKartki(zolteKartki);
        uczestnictwo.setCzerwonaKartka(czerwonaKartka);
        uczestnictwo.setFaule(0);
        return uczestnictwoRepository.save(uczestnictwo);
    }
}
