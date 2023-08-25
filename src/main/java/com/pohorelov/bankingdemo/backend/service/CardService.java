package com.pohorelov.bankingdemo.backend.service;

import com.pohorelov.bankingdemo.backend.model.Card;
import com.pohorelov.bankingdemo.backend.model.Transaction;
import com.pohorelov.bankingdemo.backend.model.User;
import com.pohorelov.bankingdemo.backend.repo.CardRepo;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepo cardRepo;
    private final TransactionService transactionService;

    public void sendMoney(User user, double sum, String from, String to) {
        //try to find cardFrom by number
        cardRepo.findByNumber(from).ifPresentOrElse(card -> {
            //if present
            //check is card active
            if (!card.getIsActive()) {
                if (isUINotNull())
                    Notification.show("YOUR CARD IS BLOCKED").addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else if (user.getBankCard().contains(card)) {
                // and if this card belongs the user

                //then find cardTo by number
                cardRepo.findByNumber(to).ifPresentOrElse(cardTo -> {
                    //if both cards are ok
                    // check if card have needed sum to send
                    if (card.getBalance() < sum) {
                        if (isUINotNull())
                            Notification.show("NOT ENOUGH MONEY").addThemeVariants(NotificationVariant.LUMO_ERROR);
                    } else if (!cardTo.getIsActive()) {
                        if (isUINotNull())
                            Notification.show("RECEIVER CARD IS BLOCKED").addThemeVariants(NotificationVariant.LUMO_ERROR);
                    } else {

                        card.setBalance(card.getBalance() - sum);
                        cardTo.setBalance(cardTo.getBalance() + sum);
                        cardRepo.saveAll(List.of(card, cardTo));
                        Transaction transaction = transactionService.addTransaction(card, cardTo, sum);
                        card.getTransactions().add(transaction);
                        UI current = UI.getCurrent();
                        if (current != null) current.getPage().reload();
                        showNotification(sum);
                    }
                }, () -> {
                    if (isUINotNull())
                        Notification.show("WRONG NUMBER SENDER").addThemeVariants(NotificationVariant.LUMO_ERROR);
                });

            }
        }, () -> {
            if (isUINotNull())
                Notification.show("WRONG NUMBER SENDER").addThemeVariants(NotificationVariant.LUMO_ERROR);
        });
    }

    private static boolean isUINotNull() {
        return UI.getCurrent() != null;
    }

    public void showNotification(double sum) {
        if (isUINotNull())
            Notification.show("YOU SENT " + sum + "$, WOW!").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    //generate card number
    public Card addCard(User user) {
        Card card = new Card(CardsUtil.generateNumber(), 1000.00, user);
        return cardRepo.save(card);
    }

    public static class CardsUtil {

        public static String generateNumber() {
            return "4149" + getRandom();
        }

        private static String getRandom() {
            StringBuilder stringBuffer = new StringBuilder();
            Random random = new Random();
            for (int i = 0; i < 3; i++) {
                stringBuffer.append(random.nextInt(1000, 9999));
            }

            return stringBuffer.toString();
        }
    }

}
