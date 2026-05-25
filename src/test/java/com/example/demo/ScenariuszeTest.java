package com.example.demo;

import com.example.demo.entities.*;
import com.example.demo.repositories.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class ScenariuszeTest {

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
    private UczestnictwoWMeczuRepository uczestnictwoWMeczuRepository;

    // 1. Scenariusze dla Klubów (/klub)
    @Test
    public void testKlubScenariusze() throws Exception {
        // Ścieżka szczęśliwa - Pobieranie klubów
        mockMvc.perform(get("/klub"))
                .andExpect(status().isOk());

        // Ścieżka szczęśliwa - Dodawanie klubu
        Klub nowyKlub = new Klub();
        nowyKlub.setNazwa("Nowy Klub Testowy");
        String klubJson = objectMapper.writeValueAsString(nowyKlub);

        String response = mockMvc.perform(post("/klub")
                .contentType(MediaType.APPLICATION_JSON)
                .content(klubJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        Klub zapisanyKlub = objectMapper.readValue(response, Klub.class);
        assertNotNull(zapisanyKlub.getId());

        // Ścieżka szczęśliwa - Usuwanie klubu
        mockMvc.perform(delete("/klub/" + zapisanyKlub.getId()))
                .andExpect(status().isOk());
        assertFalse(klubRepository.existsById(zapisanyKlub.getId()));

        // Przypadek brzegowy - Usuwanie nieistniejącego klubu
        mockMvc.perform(delete("/klub/9999"))
                .andExpect(status().isOk()); // Spring Data JPA deleteById with 9999 may throw EmptyResultDataAccessException which translates to 500 or just ignore depending on version, let's assert it doesn't crash the whole app. In newer Spring versions it throws EmptyResultDataAccessException which yields 500, or ignores if customized. Let's just run it.

        // Przypadek brzegowy (Zależności) - Usuwanie klubu z piłkarzami
        Klub klubZPilkarzem = new Klub();
        klubZPilkarzem.setNazwa("Klub z Pilkarzem");
        klubZPilkarzem = klubRepository.save(klubZPilkarzem);
        final Klub finalKlubZPilkarzem = klubZPilkarzem;

        Pilkarz pilkarz = new Pilkarz();
        pilkarz.setImie("Jan");
        pilkarz.setNazwisko("Kowalski");
        pilkarz.setKlub(klubZPilkarzem);
        pilkarzRepository.save(pilkarz);

        Exception exception = assertThrows(Exception.class, () -> {
            mockMvc.perform(delete("/klub/" + finalKlubZPilkarzem.getId()));
        });
        assertTrue(exception.getCause() instanceof org.springframework.dao.DataIntegrityViolationException);

        // Cleanup
        pilkarzRepository.delete(pilkarz);
        klubRepository.delete(klubZPilkarzem);
    }

    // 2. Scenariusze dla Piłkarzy (/pilkarz)
    @Test
    public void testPilkarzScenariusze() throws Exception {
        // Ścieżka szczęśliwa - Pobieranie piłkarzy
        mockMvc.perform(get("/pilkarz"))
                .andExpect(status().isOk());

        Klub klub = new Klub();
        klub.setNazwa("Klub dla Pilkarza");
        klub = klubRepository.save(klub);

        // Ścieżka szczęśliwa - Dodawanie piłkarza
        Pilkarz nowyPilkarz = new Pilkarz();
        nowyPilkarz.setImie("Robert");
        nowyPilkarz.setNazwisko("Lewandowski");
        nowyPilkarz.setKlub(klub);

        String pilkarzJson = objectMapper.writeValueAsString(nowyPilkarz);
        String response = mockMvc.perform(post("/pilkarz")
                .contentType(MediaType.APPLICATION_JSON)
                .content(pilkarzJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        Pilkarz zapisanyPilkarz = objectMapper.readValue(response, Pilkarz.class);
        assertNotNull(zapisanyPilkarz.getId());

        // Przypadek brzegowy - Piłkarz z nieistniejącym klubem
        Pilkarz zlyPilkarz = new Pilkarz();
        zlyPilkarz.setImie("Zły");
        zlyPilkarz.setNazwisko("Piłkarz");
        Klub nieistniejacyKlub = new Klub();
        nieistniejacyKlub.setId(9999);
        zlyPilkarz.setKlub(nieistniejacyKlub);

        // This might return 500 DataIntegrityViolationException due to foreign key failure
        Exception exception2 = assertThrows(Exception.class, () -> {
            mockMvc.perform(post("/pilkarz")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(zlyPilkarz)));
        });
        assertTrue(exception2.getCause() instanceof org.springframework.dao.DataIntegrityViolationException);

        // Ścieżka szczęśliwa - Usuwanie piłkarza
        mockMvc.perform(delete("/pilkarz/" + zapisanyPilkarz.getId()))
                .andExpect(status().isOk());
        assertFalse(pilkarzRepository.existsById(zapisanyPilkarz.getId()));

        klubRepository.delete(klub);
    }

    // 3. Scenariusze dla Sezonów (/sezon)
    @Test
    public void testSezonScenariusze() throws Exception {
        // Pobieranie sezonów
        mockMvc.perform(get("/sezon"))
                .andExpect(status().isOk());

        // Dodawanie sezonu
        Sezon nowySezon = new Sezon();
        nowySezon.setNazwa("2026/2027");
        String json = objectMapper.writeValueAsString(nowySezon);

        String response = mockMvc.perform(post("/sezon")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        Sezon zapisanySezon = objectMapper.readValue(response, Sezon.class);
        assertNotNull(zapisanySezon.getId());
        final Sezon finalZapisanySezon = zapisanySezon;

        // Przypadek brzegowy - Usuwanie sezonu z meczami
        Klub k1 = new Klub(); k1.setNazwa("K1"); k1 = klubRepository.save(k1);
        Klub k2 = new Klub(); k2.setNazwa("K2"); k2 = klubRepository.save(k2);

        Mecz mecz = new Mecz();
        mecz.setSezon(zapisanySezon);
        mecz.setGospodarz(k1);
        mecz.setGosc(k2);
        mecz.setDataRozpoczecia(LocalDateTime.now());
        mecz = meczRepository.save(mecz);

        Exception exception3 = assertThrows(Exception.class, () -> {
            mockMvc.perform(delete("/sezon/" + finalZapisanySezon.getId()));
        });
        assertTrue(exception3.getCause() instanceof org.springframework.dao.DataIntegrityViolationException);

        meczRepository.delete(mecz);
        klubRepository.delete(k1);
        klubRepository.delete(k2);

        // Usuwanie sezonu (pustego)
        mockMvc.perform(delete("/sezon/" + zapisanySezon.getId()))
                .andExpect(status().isOk());
    }

    // 4. Scenariusze dla Meczów (/mecz)
    @Test
    public void testMeczScenariusze() throws Exception {
        mockMvc.perform(get("/mecz")).andExpect(status().isOk());

        Sezon sezon = new Sezon(); sezon.setNazwa("Sezon Meczowy"); sezon = sezonRepository.save(sezon);
        Klub k1 = new Klub(); k1.setNazwa("Klub Domowy"); k1 = klubRepository.save(k1);
        Klub k2 = new Klub(); k2.setNazwa("Klub Gość"); k2 = klubRepository.save(k2);

        // Dodawanie meczu
        Mecz mecz = new Mecz();
        mecz.setSezon(sezon);
        mecz.setGospodarz(k1);
        mecz.setGosc(k2);
        mecz.setDataRozpoczecia(LocalDateTime.now());

        String response = mockMvc.perform(post("/mecz")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mecz)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        Mecz zapisanyMecz = objectMapper.readValue(response, Mecz.class);
        assertNotNull(zapisanyMecz.getId());

        // Przypadek brzegowy - Mecz z samym sobą (oczekujemy błędu po dodaniu walidacji)
        Mecz meczZSamymSoba = new Mecz();
        meczZSamymSoba.setSezon(sezon);
        meczZSamymSoba.setGospodarz(k1);
        meczZSamymSoba.setGosc(k1);
        meczZSamymSoba.setDataRozpoczecia(LocalDateTime.now());
        
        mockMvc.perform(post("/mecz")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(meczZSamymSoba)))
                .andExpect(status().is4xxClientError());

        // Usuwanie
        mockMvc.perform(delete("/mecz/" + zapisanyMecz.getId())).andExpect(status().isOk());

        klubRepository.delete(k1);
        klubRepository.delete(k2);
        sezonRepository.delete(sezon);
    }

    // 5. Scenariusze dla Bramek (/bramka)
    @Test
    public void testBramkaScenariusze() throws Exception {
        mockMvc.perform(get("/bramka")).andExpect(status().isOk());

        Sezon sezon = new Sezon(); sezon.setNazwa("Sezon Bramkowy"); sezon = sezonRepository.save(sezon);
        Klub k1 = new Klub(); k1.setNazwa("K1"); k1 = klubRepository.save(k1);
        Pilkarz pStrzelec = new Pilkarz(); pStrzelec.setImie("A"); pStrzelec.setKlub(k1); pStrzelec = pilkarzRepository.save(pStrzelec);
        Pilkarz pAsystent = new Pilkarz(); pAsystent.setImie("B"); pAsystent.setKlub(k1); pAsystent = pilkarzRepository.save(pAsystent);

        Mecz mecz = new Mecz(); mecz.setSezon(sezon); mecz.setGospodarz(k1); mecz.setGosc(k1); mecz = meczRepository.save(mecz);

        // Pełna bramka
        Bramka pelnaBramka = new Bramka();
        pelnaBramka.setMecz(mecz);
        pelnaBramka.setStrzelec(pStrzelec);
        pelnaBramka.setAsystujacy(pAsystent);
        pelnaBramka.setMinuta("15");
        pelnaBramka.setSamobojcza(false);

        String res = mockMvc.perform(post("/bramka")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pelnaBramka)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Bramka zb = objectMapper.readValue(res, Bramka.class);

        // Bez asystenta
        Bramka solowa = new Bramka();
        solowa.setMecz(mecz);
        solowa.setStrzelec(pStrzelec);
        solowa.setMinuta("30");
        solowa.setSamobojcza(false);

        mockMvc.perform(post("/bramka")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(solowa)))
                .andExpect(status().isOk());

        // Samobójcza
        Bramka samobojcza = new Bramka();
        samobojcza.setMecz(mecz);
        samobojcza.setStrzelec(pStrzelec);
        samobojcza.setMinuta("45");
        samobojcza.setSamobojcza(true);

        mockMvc.perform(post("/bramka")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(samobojcza)))
                .andExpect(status().isOk());

        // Usuwanie
        mockMvc.perform(delete("/bramka/" + zb.getId())).andExpect(status().isOk());

        // Cleanup ignores remaining for brevity in test DB, but let's delete
        bramkaRepository.deleteAll();
        meczRepository.delete(mecz);
        pilkarzRepository.delete(pStrzelec);
        pilkarzRepository.delete(pAsystent);
        klubRepository.delete(k1);
        sezonRepository.delete(sezon);
    }

    // 6. Scenariusze dla Uczestnictwa w Meczu (/uczestnictwo-wmeczu)
    @Test
    public void testUczestnictwoWMeczuScenariusze() throws Exception {
        mockMvc.perform(get("/uczestnictwo-wmeczu")).andExpect(status().isOk());

        Sezon sezon = new Sezon(); sezon.setNazwa("S"); sezon = sezonRepository.save(sezon);
        Klub k = new Klub(); k.setNazwa("K"); k = klubRepository.save(k);
        Pilkarz p = new Pilkarz(); p.setImie("P"); p.setKlub(k); p = pilkarzRepository.save(p);
        Mecz m = new Mecz(); m.setSezon(sezon); m.setGospodarz(k); m.setGosc(k); m = meczRepository.save(m);

        // Rejestracja zawodnika
        UczestnictwoWMeczu u1 = new UczestnictwoWMeczu();
        u1.setMecz(m);
        u1.setPilkarz(p);
        u1.setMinutaWejscia(0);
        u1.setMinutaZejscia(90);
        u1.setFaule(2);
        u1.setZolteKartki(1);
        u1.setCzerwonaKartka(false);

        String r1 = mockMvc.perform(post("/uczestnictwo-wmeczu")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(u1)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        UczestnictwoWMeczu zu1 = objectMapper.readValue(r1, UczestnictwoWMeczu.class);

        // Zablokowanie wielokrotnego wejścia tego samego gracza do tego samego meczu
        mockMvc.perform(post("/uczestnictwo-wmeczu")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(u1)))
                .andExpect(status().is4xxClientError());

        Pilkarz p2 = new Pilkarz(); p2.setImie("P2"); p2.setKlub(k); p2 = pilkarzRepository.save(p2);
        // Czerwona kartka = true
        UczestnictwoWMeczu u2 = new UczestnictwoWMeczu();
        u2.setMecz(m);
        u2.setPilkarz(p2);
        u2.setCzerwonaKartka(true);
        mockMvc.perform(post("/uczestnictwo-wmeczu")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(u2)))
                .andExpect(status().isOk());

        Pilkarz p3 = new Pilkarz(); p3.setImie("P3"); p3.setKlub(k); p3 = pilkarzRepository.save(p3);
        // Niepoprawny czas gry (oczekujemy błędu walidacji 400)
        UczestnictwoWMeczu u3 = new UczestnictwoWMeczu();
        u3.setMecz(m);
        u3.setPilkarz(p3);
        u3.setMinutaWejscia(90);
        u3.setMinutaZejscia(15);
        mockMvc.perform(post("/uczestnictwo-wmeczu")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(u3)))
                .andExpect(status().is4xxClientError());

        // Usunięcie uczestnictwa
        mockMvc.perform(delete("/uczestnictwo-wmeczu/" + zu1.getId()))
                .andExpect(status().isOk());

        uczestnictwoWMeczuRepository.deleteAll();
        meczRepository.delete(m);
        pilkarzRepository.delete(p);
        pilkarzRepository.delete(p2);
        pilkarzRepository.delete(p3);
        klubRepository.delete(k);
        sezonRepository.delete(sezon);
    }
}
