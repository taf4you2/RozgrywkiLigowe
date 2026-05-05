package com.example.demo;

import com.example.demo.entities.Sezon;
import com.example.demo.repositories.SezonRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EndpointsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SezonRepository sezonRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testAddAndDeleteSezon() throws Exception {
        Sezon sezon = new Sezon();
        sezon.setNazwa("Testowy Sezon");

        String json = objectMapper.writeValueAsString(sezon);

        // Add
        String response = mockMvc.perform(post("/sezon")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Sezon savedSezon = objectMapper.readValue(response, Sezon.class);
        Integer id = savedSezon.getId();
        assertTrue(sezonRepository.existsById(id));

        // Delete
        mockMvc.perform(delete("/sezon/" + id))
                .andExpect(status().isOk());

        assertFalse(sezonRepository.existsById(id));
    }
}
