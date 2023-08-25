package com.pohorelov.bankingdemo.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Entity
@Table(name = "cards")
@NoArgsConstructor
@ToString(exclude = "user")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(unique = true)
    private String number = "";
    private double balance;
    private Boolean isActive = true;
    @ManyToOne
    private User user;
    @OneToMany(mappedBy = "cardFrom", fetch = FetchType.EAGER)
    private List<Transaction> transactions = new ArrayList<>();

    public Card(String number, double balance, User user) {
        this.number = number;
        this.balance = balance;
        this.user = user;
    }


    public String getStatus() {
        return isActive ? "ACTIVE" : "BLOCKED";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return Double.compare(balance, card.balance) == 0 && Objects.equals(id, card.id) && Objects.equals(number, card.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, number, balance);
    }
}
