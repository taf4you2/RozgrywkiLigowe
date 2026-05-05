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

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Mecz getMecz() { return mecz; }
    public void setMecz(Mecz mecz) { this.mecz = mecz; }
    public Pilkarz getPilkarz() { return pilkarz; }
    public void setPilkarz(Pilkarz pilkarz) { this.pilkarz = pilkarz; }
    public String getRola() { return rola; }
    public void setRola(String rola) { this.rola = rola; }
    public Integer getMinutaWejscia() { return minutaWejscia; }
    public void setMinutaWejscia(Integer minutaWejscia) { this.minutaWejscia = minutaWejscia; }
    public Integer getMinutaZejscia() { return minutaZejscia; }
    public void setMinutaZejscia(Integer minutaZejscia) { this.minutaZejscia = minutaZejscia; }
    public Integer getZolteKartki() { return zolteKartki; }
    public void setZolteKartki(Integer zolteKartki) { this.zolteKartki = zolteKartki; }
    public Boolean getCzerwonaKartka() { return czerwonaKartka; }
    public void setCzerwonaKartka(Boolean czerwonaKartka) { this.czerwonaKartka = czerwonaKartka; }
    public Integer getFaule() { return faule; }
    public void setFaule(Integer faule) { this.faule = faule; }
}
