package com.pohorelov.bankingdemo.backend.service;

import com.pohorelov.bankingdemo.backend.model.Card;
import com.pohorelov.bankingdemo.backend.model.Transaction;
import com.pohorelov.bankingdemo.backend.repo.TransactionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TransactionService {

    private final TransactionRepo transactionRepo;

    public Transaction addTransaction(Card cardFrom, Card cardTo, Double sum) {
        Transaction transaction = new Transaction();
        transaction.setCardFrom(cardFrom);
        transaction.setCardTo(cardTo);
        transaction.setSum(sum);
        transactionRepo.save(transaction);
        return transaction;
    }
}
