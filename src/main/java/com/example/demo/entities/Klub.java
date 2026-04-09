package com.example.demo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Klub {
    
    @Id
    @GeneratedValue
    private Integer id;
    private String nazwa;

    @JsonIgnore
    @OneToMany(mappedBy = "klub")
    private Set<Pilkarz> pilkarze;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNazwa() { return nazwa; }
    public void setNazwa(String nazwa) { this.nazwa = nazwa; }
    public Set<Pilkarz> getPilkarze() { return pilkarze; }
    public void setPilkarze(Set<Pilkarz> pilkarze) { this.pilkarze = pilkarze; }
}