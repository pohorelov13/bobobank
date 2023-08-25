package com.pohorelov.bankingdemo.views;

import com.pohorelov.bankingdemo.backend.model.Card;
import com.pohorelov.bankingdemo.backend.model.Transaction;
import com.pohorelov.bankingdemo.backend.model.User;
import com.pohorelov.bankingdemo.backend.service.AuthService;
import com.pohorelov.bankingdemo.backend.service.CardService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

@Route("")
@PageTitle("Home")
@PermitAll
class MainView extends AppLayout {
    //home page
    private final User user;

    private MainView(AuthService authService, CardService cardService) {
        //get auth user from context
        user = authService.getUser().orElse(new User());
        if (user.getEmail() == null) UI.getCurrent().getPage().setLocation("/login");
        configureView(authService, cardService, user);
    }

    private void configureView(AuthService authService, CardService cardService, User user) {

        Set<Transaction> transactions = new HashSet<>();

        //add tittle
        H1 title = new H1("SimpleBankingApp");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("left", "var(--lumo-space-l)").set("margin", "0")
                .set("position", "absolute");
        title.addClickListener(event -> UI.getCurrent().getPage().setLocation("/"));

        Button send = new Button("SEND");
        Button addCard = new Button("ADD CARD");
        addCard.addClickListener(e -> {
            cardService.addCard(user);
            UI.getCurrent().getPage().reload();
        });


        TextField cardFrom = createCard("Your card");

        TextField cardTo = createCard("Card to");

        NumberField sum = new NumberField("SUM");
        sum.setClearButtonVisible(true);

        send.addClickListener(e -> {
            cardService.sendMoney(user, sum.getValue(), cardFrom.getValue(), cardTo.getValue());

        });


        FormLayout formLayout = getFormLayout(cardFrom, cardTo, sum, send);

        TabSheet tabSheet = getTabSheet(addCard);

        Button logoutButton = new Button("Logout");
        logoutButton.addClickListener(e -> authService.logout());


        addToNavbar(title, tabSheet, logoutButton);

        Grid<Card> grid = getGridCard();

        Grid<Transaction> gridTransaction = getGridTransaction();


        setContent(new HorizontalLayout(formLayout, new Text("CHOOSE CARD"), grid));

        grid.addSelectionListener(event -> {
            Set<Card> allSelectedItems = event.getAllSelectedItems();
            if (!allSelectedItems.isEmpty()) {
                Card selectedCard = allSelectedItems.iterator().next();
                cardFrom.setValue(selectedCard.getNumber());
            }
        });

        //change page content after click tab
        tabSheet.addSelectedChangeListener(event -> {
            Tab selectedTab = tabSheet.getSelectedTab();
            switch (selectedTab.getLabel()) {
                case "Cards" -> setContent(new HorizontalLayout(formLayout, new Text("CHOOSE CARD"), grid));
                case "History" -> {
                    user.getBankCard().forEach(card -> transactions.addAll(card.getTransactions()));
                    gridTransaction.setItems(transactions);
                    setContent(new HorizontalLayout(gridTransaction));
                }
            }

        });
    }

    private TabSheet getTabSheet(Button addCard) {
        TabSheet tabSheet = new TabSheet();
        Button settings = new Button("Go to settings");

        settings.addClickListener(event -> UI.getCurrent().getPage().setLocation("settings"));
        tabSheet.add("Cards",
                new Div(new Text("You can add a few card and transaction"), addCard));
        tabSheet.add("History",
                new Div(new Text("History of your transactions")));
        tabSheet.add("Settings", new Div(
                new Text("You can change info"), settings));

        tabSheet.getStyle().set("margin", "auto");
        return tabSheet;
    }

    private FormLayout getFormLayout(TextField cardFrom, TextField cardTo, NumberField sum, Button send) {
        FormLayout formLayout = new FormLayout();
        formLayout.add(cardFrom, cardTo, sum, send);
        formLayout.setResponsiveSteps(
                // Use one column by default
                new FormLayout.ResponsiveStep("0", 1),
                // Use two columns, if layout's width exceeds 500px
                new FormLayout.ResponsiveStep("500px", 2));
// Stretch the username field over 2 columns
        return formLayout;
    }

    private TextField createCard(String label) {
        TextField cardFrom = new TextField(label);
        cardFrom.setPattern("[0-9]{16}?");
        cardFrom.setClearButtonVisible(true);
        cardFrom.setErrorMessage("ENTER CORRECT NUMBER");
        return cardFrom;
    }

    private Grid<Card> getGridCard() {
        Grid<Card> grid = new Grid<>(Card.class, false);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.addColumn(Card::getNumber).setHeader("NUMBER")
                .setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(Card::getBalance).setHeader("BALANCE").setSortable(true)
                .setAutoWidth(true);
        grid.addColumn(createStatusComponentRenderer()).setHeader("Status")
                .setAutoWidth(true);

        grid.setItems(user.getBankCard());
        return grid;
    }

    private Grid<Transaction> getGridTransaction() {
        Grid<Transaction> grid = new Grid<>(Transaction.class, false);
        grid.addColumn(e -> e.getCardFrom().getNumber()).setHeader("YOUR CARD")
                .setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(e -> e.getCardTo().getNumber()).setHeader("CARD TO")
                .setAutoWidth(true);
        grid.addColumn(Transaction::getSum).setHeader("SUM").setSortable(true)
                .setAutoWidth(true);
        grid.addColumn(t -> t.getTime().format(DateTimeFormatter.ofPattern("dd/MM/yy(HH:mm:ss)"))).setHeader("DATE").setSortable(true)
                .setAutoWidth(true);
        return grid;
    }


    private final SerializableBiConsumer<Span, Card> statusComponentUpdater = (
            span, card) -> {
        boolean isAvailable = card.getIsActive();
        String theme = String.format("badge %s",
                isAvailable ? "success" : "error");
        span.getElement().setAttribute("theme", theme);
        span.setText(card.getStatus());
    };

    private ComponentRenderer<Span, Card> createStatusComponentRenderer() {
        return new ComponentRenderer<>(Span::new, statusComponentUpdater);
    }
}
