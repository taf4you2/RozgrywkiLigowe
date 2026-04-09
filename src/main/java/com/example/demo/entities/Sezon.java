package com.example.demo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Sezon {
    
    @Id
    @GeneratedValue
    private Integer id;
    private String nazwa;

    @JsonIgnore
    @OneToMany(mappedBy = "sezon", cascade = CascadeType.ALL)
    private Set<Mecz> mecze;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNazwa() { return nazwa; }
    public void setNazwa(String nazwa) { this.nazwa = nazwa; }
    public Set<Mecz> getMecze() { return mecze; }
    public void setMecze(Set<Mecz> mecze) { this.mecze = mecze; }
}