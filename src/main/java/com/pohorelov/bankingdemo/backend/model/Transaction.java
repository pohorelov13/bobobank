package com.pohorelov.bankingdemo.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Entity
@Table(name = "transactions")
@ToString(exclude = {"cardFrom", "cardTo"})
@EqualsAndHashCode
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double sum;
    @Getter
    private LocalDateTime time;
    @ManyToOne
    private Card cardFrom;
    @ManyToOne
    private Card cardTo;

    public Transaction() {
        this.time = LocalDateTime.now();
    }

}
