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
