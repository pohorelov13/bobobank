package com.pohorelov.bankingdemo.backend.repo;


import com.pohorelov.bankingdemo.backend.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepo extends JpaRepository<Transaction, Long> {

}
