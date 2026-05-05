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

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getMinuta() { return minuta; }
    public void setMinuta(String minuta) { this.minuta = minuta; }
    public Boolean getSamobojcza() { return samobojcza; }
    public void setSamobojcza(Boolean samobojcza) { this.samobojcza = samobojcza; }
    public Mecz getMecz() { return mecz; }
    public void setMecz(Mecz mecz) { this.mecz = mecz; }
    public Pilkarz getStrzelec() { return strzelec; }
    public void setStrzelec(Pilkarz strzelec) { this.strzelec = strzelec; }
    public Pilkarz getAsystujacy() { return asystujacy; }
    public void setAsystujacy(Pilkarz asystujacy) { this.asystujacy = asystujacy; }
}
