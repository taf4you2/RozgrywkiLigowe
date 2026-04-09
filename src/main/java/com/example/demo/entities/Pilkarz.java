package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Pilkarz {
    
    @Id
    @GeneratedValue
    private Integer id;
    private String imie;
    private String nazwisko;

    @ManyToOne
    private Klub klub;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getImie() { return imie; }
    public void setImie(String imie) { this.imie = imie; }
    public String getNazwisko() { return nazwisko; }
    public void setNazwisko(String nazwisko) { this.nazwisko = nazwisko; }
    public Klub getKlub() { return klub; }
    public void setKlub(Klub klub) { this.klub = klub; }
}