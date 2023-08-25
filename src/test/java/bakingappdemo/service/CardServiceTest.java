package bakingappdemo.service;

import com.pohorelov.bankingdemo.backend.model.Card;
import com.pohorelov.bankingdemo.backend.model.Role;
import com.pohorelov.bankingdemo.backend.model.Transaction;
import com.pohorelov.bankingdemo.backend.model.User;
import com.pohorelov.bankingdemo.backend.repo.CardRepo;
import com.pohorelov.bankingdemo.backend.service.CardService;
import com.pohorelov.bankingdemo.backend.service.TransactionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepo cardRepo;

    @Mock
    private TransactionService transactionService;
    private AutoCloseable autoCloseable;
    private CardService cardService;
    private User user;
    private double sum;
    private String nCardFrom;
    private String nCardTo;
    private double balanceFrom;
    private Card cardFrom;
    private Card cardTo;
    private double balanceTo;
    private List<Card> cards;

    @BeforeEach
    void setUp() {
        autoCloseable = openMocks(this);
        user = new User("testEmail", "testFirstName", "testLastName", "password", Role.USER);
        cardService = new CardService(cardRepo, transactionService);

        sum = 50;
        nCardFrom = "4149499115678345";
        nCardTo = "4149499115678344";

        balanceFrom = 1000.00;
        cardFrom = new Card(nCardFrom, balanceFrom, user);

        balanceTo = 100.00;
        cardTo = new Card(nCardTo, balanceTo, user);
        cards = List.of(cardFrom, cardTo);
    }

    @Test
    void sendMoneyWhenAllValid() {

        user.getBankCard().add(cardFrom);
        user.getBankCard().add(cardTo);

        when(cardRepo.findByNumber(nCardFrom)).thenReturn(Optional.of(cardFrom));
        when(cardRepo.findByNumber(nCardTo)).thenReturn(Optional.of(cardTo));
        when(transactionService.addTransaction(any(), any(), anyDouble())).thenReturn(new Transaction());

        ArgumentCaptor<List<Card>> cardsList = ArgumentCaptor.forClass(List.class);
        cardService.sendMoney(user, sum, nCardFrom, nCardTo);

        verify(cardRepo).saveAll(cardsList.capture());

        assertEquals(cardFrom.getBalance(), balanceFrom - sum);
        assertEquals(cardTo.getBalance(), balanceTo + sum);
        assertEquals(cards, cardsList.getValue());
        assertNotNull(cardFrom.getTransactions().get(0));
    }

    @Test
    void sendMoneyWhenCardFromDoesNotExist() {

        when(cardRepo.findByNumber(nCardFrom)).thenReturn(Optional.empty());

        cardService.sendMoney(user, sum, nCardFrom, nCardTo);

        verify(cardRepo, times(0)).saveAll(any());
        verify(transactionService, times(0)).addTransaction(any(), any(), any());

    }

    @Test
    void sendMoneyWhenCardToDoesNotExist() {
        user.getBankCard().add(cardFrom);
        when(cardRepo.findByNumber(nCardFrom)).thenReturn(Optional.of(cardFrom));
        when(cardRepo.findByNumber(nCardTo)).thenReturn(Optional.empty());

        cardService.sendMoney(user, sum, nCardFrom, nCardTo);

        verify(cardRepo, times(0)).saveAll(any());
        verify(transactionService, times(0)).addTransaction(any(), any(), any());

    }

    @Test
    void sendMoneyWhenBalanceLessThenSum() {
        sum = 6000.00;

        user.getBankCard().add(cardFrom);
        user.getBankCard().add(cardTo);

        when(cardRepo.findByNumber(nCardFrom)).thenReturn(Optional.of(cardFrom));
        when(cardRepo.findByNumber(nCardTo)).thenReturn(Optional.of(cardTo));

        cardService.sendMoney(user, sum, nCardFrom, nCardTo);

        verify(cardRepo, times(0)).saveAll(any());
        verify(transactionService, times(0)).addTransaction(any(), any(), any());
    }

    @Test
    void sendMoneyWhenCardFromBlocked() {

        cardFrom.setIsActive(false);

        user.getBankCard().add(cardFrom);
        user.getBankCard().add(cardTo);

        when(cardRepo.findByNumber(nCardFrom)).thenReturn(Optional.of(cardFrom));

        cardService.sendMoney(user, sum, nCardFrom, nCardTo);

        verify(cardRepo, times(0)).saveAll(any());
        verify(transactionService, times(0)).addTransaction(any(), any(), any());
    }

    @Test
    void sendMoneyWhenCardToBlocked() {

        cardTo.setIsActive(false);

        user.getBankCard().add(cardFrom);
        user.getBankCard().add(cardTo);

        when(cardRepo.findByNumber(nCardFrom)).thenReturn(Optional.of(cardFrom));
        when(cardRepo.findByNumber(nCardTo)).thenReturn(Optional.of(cardTo));

        cardService.sendMoney(user, sum, nCardFrom, nCardTo);

        verify(cardRepo, times(0)).saveAll(any());
        verify(transactionService, times(0)).addTransaction(any(), any(), any());
    }

    @Test
    void addCard() {
        cardService.addCard(user);
        ArgumentCaptor<Card> cardArgumentCaptor = ArgumentCaptor.forClass(Card.class);
        verify(cardRepo).save(cardArgumentCaptor.capture());

        assertEquals(16, cardArgumentCaptor.getValue().getNumber().length());
        assertEquals(user, cardArgumentCaptor.getValue().getUser());

    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }
}