package com.example;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private final String name;
    private final List<Card> cards;

    public Player(String name, Card card1, Card card2) {
        this.name = name;
        cards = new ArrayList<>();
        cards.add(card1);
        cards.add(card2);
    }

    public String getName() {
        return name;
    }

    public List<Card> getCards() {
        return cards;
    }

    @Override
    public String toString() {
        return name + " Karten: " + cards.get(0) + ", " + cards.get(1);
    }
}