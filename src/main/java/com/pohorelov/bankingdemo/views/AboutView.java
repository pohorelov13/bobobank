package com.pohorelov.bankingdemo.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("about")
@PageTitle("About Me and App")
@CssImport(value = "./styles/about-page-styles.css")
@AnonymousAllowed
public class AboutView extends VerticalLayout {

    public AboutView() {

        H2 pageTitle = new H2("About Me and this project");

        Paragraph aboutMe = new Paragraph("Hello! I'm Bohdan - Junior Java Developer.");


        Paragraph aboutApp = new Paragraph("This is a personal project I've been working on. " +
                "This is a simple service to demonstrate my skills. " +
                "The project implements the following functionality - new user registration, login, change user info " +
                "adding cards, making transactions. For this project, I utilized technologies such as Spring Boot (Web, Security, Data), " +
                "Hibernate, MySQL, Maven, Lombok, Vaadin (create a simple front view) and JUnit with Mockito(for testing), " +
                "which allowed me to build a robust and user-friendly application.");

        Button start = new Button("Let's start");
        start.addClickListener(event -> UI.getCurrent().getPage().setLocation("/login"));

        Div content = new Div(pageTitle, aboutMe, aboutApp, start);

        content.addClassName("about-content");

        add(content);

        setAlignItems(Alignment.CENTER);
    }
}