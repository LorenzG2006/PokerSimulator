package com.example;

public class Card {
    private final Suit suit;  // Farbe der Karte
    private final Rank rank;  // Wert der Karte

    public Card(Rank rank, Suit suit) {
        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}
