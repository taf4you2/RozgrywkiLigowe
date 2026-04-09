package com.example.demo.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Mecz {
    
    @Id
    @GeneratedValue
    private Integer id;
    private LocalDateTime dataRozpoczecia;

    @ManyToOne
    private Sezon sezon;

    @ManyToOne
    private Klub gospodarz;

    @ManyToOne
    private Klub gosc;

    @ElementCollection
    private List<Integer> skladGospodarzyWyjsciowy;
    @ElementCollection
    private List<Integer> skladGospodarzyLawkaRezerwowych;
    @ElementCollection
    private List<Integer> skladGosciWyjsciowy;
    @ElementCollection
    private List<Integer> skladGosciLawkaRezerwowych;

    @ElementCollection
    private List<String> kartki;
    @ElementCollection
    private List<String> faule;
    @ElementCollection
    private List<String> zmiany;

    @JsonIgnore
    @OneToMany(mappedBy = "mecz", cascade = CascadeType.ALL)
    private Set<Bramka> bramki;
}