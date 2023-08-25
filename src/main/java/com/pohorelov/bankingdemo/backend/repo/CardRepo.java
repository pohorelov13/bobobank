package com.pohorelov.bankingdemo.backend.repo;


import com.pohorelov.bankingdemo.backend.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepo extends JpaRepository<Card, Long> {

    Optional<Card> findByNumber(String number);
}
