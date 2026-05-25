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
}
