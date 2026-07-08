package com.example.demo;

import com.example.demo.entities.Klub;
import com.example.demo.entities.Pilkarz;
import com.example.demo.repositories.KlubRepository;
import com.example.demo.repositories.PilkarzRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:dokumentacja_hateoas_test",
        "spring.jpa.show-sql=false"
})
@AutoConfigureMockMvc
public class DokumentacjaIHateoasTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KlubRepository klubRepository;

    @Autowired
    private PilkarzRepository pilkarzRepository;

    @Test
    public void testOpenApiUdostepniaDokumentacjeSwaggera() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").exists())
                .andExpect(jsonPath("$.info.title").value("Rozgrywki Ligowe API"))
                .andExpect(jsonPath("$['paths']['/statystyki/najlepsi-strzelcy']").exists())
                .andExpect(jsonPath("$['paths']['/pilkarz/{id}/transfer']").exists());

        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    public void testApiIndexZwracaLinkiHateoas() throws Exception {
        mockMvc.perform(get("/api"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.kluby.href", containsString("/klub")))
                .andExpect(jsonPath("$._links.pilkarze.href", containsString("/pilkarz")))
                .andExpect(jsonPath("$._links.ranking-kartek.href", containsString("/statystyki/ranking-kartek")))
                .andExpect(jsonPath("$._links.openapi.href").value("/v3/api-docs"))
                .andExpect(jsonPath("$._links.swagger-ui.href").value("/swagger-ui/index.html"));
    }

    @Test
    public void testTransferPilkarzaZwracaLinkiHateoas() throws Exception {
        Klub staryKlub = zapiszKlub("HATEOAS stary klub");
        Klub nowyKlub = zapiszKlub("HATEOAS nowy klub");
        Pilkarz pilkarz = zapiszPilkarza("Link", "Transferowy", staryKlub);

        mockMvc.perform(patch("/pilkarz/" + pilkarz.getId() + "/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("nowyKlubId", nowyKlub.getId()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(pilkarz.getId()))
                .andExpect(jsonPath("$.klub.id").value(nowyKlub.getId()))
                .andExpect(jsonPath("$._links.self.href", containsString("/pilkarz/" + pilkarz.getId())))
                .andExpect(jsonPath("$._links.pilkarze.href", containsString("/pilkarz")))
                .andExpect(jsonPath("$._links.transfer-pilkarza.href", containsString("/pilkarz/" + pilkarz.getId() + "/transfer")))
                .andExpect(jsonPath("$._links.klub.href").value("/klub/" + nowyKlub.getId()));
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
}
