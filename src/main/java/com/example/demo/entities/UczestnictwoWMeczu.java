package com.example.demo.entities;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "uczestnictwo_wmeczu")
@Getter
@Setter
public class UczestnictwoWMeczu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    private Mecz mecz;
    
    @ManyToOne
    private Pilkarz pilkarz;
    
    private String rola;
    private Integer minutaWejscia;
    private Integer minutaZejscia;
    private Integer zolteKartki;
    private Boolean czerwonaKartka;
    private Integer faule;
}
