# Kod encji projektu RozgrywkiLigowe

Poniżej znajduje się kod źródłowy wszystkich encji w projekcie, uwzględniający wprowadzone zmiany w strategii generowania kluczy głównych (`IDENTITY`).

## Bramka
```java
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
```

## Klub
```java
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
```

## Mecz
```java
package com.example.demo.entities;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
@Entity
@Getter
@Setter
public class Mecz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private LocalDateTime dataRozpoczecia;
    @ManyToOne
    private Sezon sezon;
    @ManyToOne
    private Klub gospodarz;
    @ManyToOne
    private Klub gosc;
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public LocalDateTime getDataRozpoczecia() { return dataRozpoczecia; }
    public void setDataRozpoczecia(LocalDateTime dataRozpoczecia) { this.dataRozpoczecia = dataRozpoczecia; }
    public Sezon getSezon() { return sezon; }
    public void setSezon(Sezon sezon) { this.sezon = sezon; }
    public Klub getGospodarz() { return gospodarz; }
    public void setGospodarz(Klub gospodarz) { this.gospodarz = gospodarz; }
    public Klub getGosc() { return gosc; }
    public void setGosc(Klub gosc) { this.gosc = gosc; }
}
```

## Pilkarz
```java
package com.example.demo.entities;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Entity
@Getter
@Setter
public class Pilkarz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
```

## Sezon
```java
package com.example.demo.entities;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Entity
@Getter
@Setter
public class Sezon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nazwa;
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNazwa() { return nazwa; }
    public void setNazwa(String nazwa) { this.nazwa = nazwa; }
}
```

## UczestnictwoWMeczu
```java
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
```
