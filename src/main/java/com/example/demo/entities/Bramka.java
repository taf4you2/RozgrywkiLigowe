package com.example.demo.entities;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Bramka {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String minuta;
    private Boolean samobojcza;
    
    @ManyToOne
    private Mecz mecz;
    
    @ManyToOne
    private Pilkarz strzelec;
    
    @ManyToOne
    private Pilkarz asystujacy;
}
