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

    @Autowired
    private SezonRepository sezonRepository;

    @Autowired
    private MeczRepository meczRepository;

    @Autowired
    private BramkaRepository bramkaRepository;

    @Autowired
    private UczestnictwoWMeczuRepository uczestnictwoRepository;

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

    @Test
    public void testKlubZwracaLinkiHateoas() throws Exception {
        Klub klub = zapiszKlub("HATEOAS Klub");

        mockMvc.perform(get("/klub/" + klub.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href", containsString("/klub/" + klub.getId())))
                .andExpect(jsonPath("$._links.kluby.href", containsString("/klub")));
    }

    @Test
    public void testSezonZwracaLinkiHateoas() throws Exception {
        Sezon sezon = zapiszSezon("HATEOAS Sezon");

        mockMvc.perform(get("/sezon/" + sezon.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href", containsString("/sezon/" + sezon.getId())))
                .andExpect(jsonPath("$._links.sezony.href", containsString("/sezon")));
    }

    @Test
    public void testMeczZwracaLinkiHateoasDoPowiazanychObiektow() throws Exception {
        Sezon sezon = zapiszSezon("HATEOAS Sezon Mecz");
        Klub gospodarz = zapiszKlub("HATEOAS Gospodarz");
        Klub gosc = zapiszKlub("HATEOAS Gosc");
        Mecz mecz = zapiszMecz(sezon, gospodarz, gosc);

        mockMvc.perform(get("/mecz/" + mecz.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href", containsString("/mecz/" + mecz.getId())))
                .andExpect(jsonPath("$._links.mecze.href", containsString("/mecz")))
                .andExpect(jsonPath("$._links.sezon.href").value("/sezon/" + sezon.getId()))
                .andExpect(jsonPath("$._links.gospodarz.href").value("/klub/" + gospodarz.getId()))
                .andExpect(jsonPath("$._links.gosc.href").value("/klub/" + gosc.getId()));
    }

    @Test
    public void testBramkaZwracaLinkiHateoasDoPowiazanychObiektow() throws Exception {
        Sezon sezon = zapiszSezon("HATEOAS Sezon Bramka");
        Klub gospodarz = zapiszKlub("HATEOAS Gospodarz Bramka");
        Klub gosc = zapiszKlub("HATEOAS Gosc Bramka");
        Mecz mecz = zapiszMecz(sezon, gospodarz, gosc);
        Pilkarz strzelec = zapiszPilkarza("Jan", "Strzelec", gospodarz);
        Bramka bramka = zapiszBramke(mecz, strzelec);

        mockMvc.perform(get("/bramka/" + bramka.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href", containsString("/bramka/" + bramka.getId())))
                .andExpect(jsonPath("$._links.bramki.href", containsString("/bramka")))
                .andExpect(jsonPath("$._links.mecz.href").value("/mecz/" + mecz.getId()))
                .andExpect(jsonPath("$._links.strzelec.href").value("/pilkarz/" + strzelec.getId()));
    }

    @Test
    public void testUczestnictwoZwracaLinkiHateoasDoPowiazanychObiektow() throws Exception {
        Sezon sezon = zapiszSezon("HATEOAS Sezon Uczestnictwo");
        Klub gospodarz = zapiszKlub("HATEOAS Gospodarz Uczestnictwo");
        Klub gosc = zapiszKlub("HATEOAS Gosc Uczestnictwo");
        Mecz mecz = zapiszMecz(sezon, gospodarz, gosc);
        Pilkarz pilkarz = zapiszPilkarza("Adam", "Uczestnik", gospodarz);
        UczestnictwoWMeczu uczestnictwo = zapiszUczestnictwo(mecz, pilkarz);

        mockMvc.perform(get("/uczestnictwo-wmeczu/" + uczestnictwo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href", containsString("/uczestnictwo-wmeczu/" + uczestnictwo.getId())))
                .andExpect(jsonPath("$._links.uczestnictwa.href", containsString("/uczestnictwo-wmeczu")))
                .andExpect(jsonPath("$._links.mecz.href").value("/mecz/" + mecz.getId()))
                .andExpect(jsonPath("$._links.pilkarz.href").value("/pilkarz/" + pilkarz.getId()));
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

    private Sezon zapiszSezon(String nazwa) {
        Sezon sezon = new Sezon();
        sezon.setNazwa(nazwa);
        return sezonRepository.save(sezon);
    }

    private Mecz zapiszMecz(Sezon sezon, Klub gospodarz, Klub gosc) {
        Mecz mecz = new Mecz();
        mecz.setDataRozpoczecia(LocalDateTime.now());
        mecz.setSezon(sezon);
        mecz.setGospodarz(gospodarz);
        mecz.setGosc(gosc);
        return meczRepository.save(mecz);
    }

    private Bramka zapiszBramke(Mecz mecz, Pilkarz strzelec) {
        Bramka bramka = new Bramka();
        bramka.setMinuta("45");
        bramka.setSamobojcza(false);
        bramka.setMecz(mecz);
        bramka.setStrzelec(strzelec);
        return bramkaRepository.save(bramka);
    }

    private UczestnictwoWMeczu zapiszUczestnictwo(Mecz mecz, Pilkarz pilkarz) {
        UczestnictwoWMeczu uczestnictwo = new UczestnictwoWMeczu();
        uczestnictwo.setMecz(mecz);
        uczestnictwo.setPilkarz(pilkarz);
        uczestnictwo.setRola("PODSTAWOWY_SKLAD");
        return uczestnictwoRepository.save(uczestnictwo);
    }
}
