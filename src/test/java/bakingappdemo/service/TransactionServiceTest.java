package bakingappdemo.service;

import com.pohorelov.bankingdemo.backend.model.Card;
import com.pohorelov.bankingdemo.backend.model.Transaction;
import com.pohorelov.bankingdemo.backend.model.User;
import com.pohorelov.bankingdemo.backend.repo.TransactionRepo;
import com.pohorelov.bankingdemo.backend.service.TransactionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock
    private TransactionRepo repo;

    private TransactionService underTest;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = openMocks(this);
        underTest = new TransactionService(repo);
    }

    @Test
    void addTransaction() {
        User user = new User();
        double sum = 100.00;
        Card cardFrom = new Card("4149499115678314", 250.00, user);
        Card cardTo = new Card("4149499115678314", 250.00, user);
        ArgumentCaptor<Transaction> transactionArgumentCaptor = ArgumentCaptor.forClass(Transaction.class);

        underTest.addTransaction(cardFrom, cardTo, sum);

        verify(repo).save(transactionArgumentCaptor.capture());

        Transaction captorValue = transactionArgumentCaptor.getValue();

        assertEquals(cardFrom, captorValue.getCardFrom());
        assertEquals(cardTo, captorValue.getCardTo());
        assertEquals(sum, captorValue.getSum());
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }
}