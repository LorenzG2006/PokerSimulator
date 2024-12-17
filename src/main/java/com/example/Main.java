package com.example;

import java.util.*;
import java.util.stream.Collectors;

public class Main {

    private Card[] deck;
    private int deckIndex = 0; // Zum Ziehen der nächsten Karte im Deck

    public Main() {
        // deck = createDeck(); // Deck erstellen
        // shuffleDeck(deck); // Deck mischen
        // playGame(); // Spiel starten

        poker();
    }

    private void poker() {
        List<Card> communityCards = Arrays.asList(new Card(Rank.FIVE, Suit.DIAMONDS), new Card(Rank.EIGHT, Suit.SPADES), new Card(Rank.FOUR, Suit.DIAMONDS), new Card(Rank.ACE, Suit.DIAMONDS), new Card(Rank.TEN, Suit.SPADES));

        List<Card> player1Hand = Arrays.asList(new Card(Rank.QUEEN, Suit.DIAMONDS), new Card(Rank.KING, Suit.CLUBS));
        List<Card> player2Hand = Arrays.asList(new Card(Rank.FIVE, Suit.SPADES), new Card(Rank.FIVE, Suit.CLUBS));

        Player player1 = new Player("Spieler 1", player1Hand.get(0), player1Hand.get(1));
        Player player2 = new Player("Spieler 2", player2Hand.get(0), player2Hand.get(1));

        printGameResult(player1, player2, communityCards);
    }

    public static void main(String[] args) {
        new Main();
    }

    /**
     * Erstellt ein vollständiges Deck aus 52 Karten.
     */
    private Card[] createDeck() {
        Card[] deck = new Card[52];
        int index = 0;
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                deck[index++] = new Card(rank, suit);
            }
        }
        return deck;
    }

    /**
     * Mischt das Kartendeck.
     */
    private void shuffleDeck(Card[] deck) {
        Random random = new Random();
        for (int i = deck.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Card temp = deck[i];
            deck[i] = deck[j];
            deck[j] = temp;
        }
    }

    /**
     * Zieht die nächste Karte aus dem Deck.
     */
    private Card drawCard() {
        return deck[deckIndex++];
    }

    private boolean isStraightFlush(List<Card> cards) {
        return isFlush(cards) && isStraight(cards);
    }

    private boolean isFourOfAKind(List<Card> cards) {
        return hasSameRank(cards, 4);
    }

    private boolean isFullHouse(List<Card> cards) {
        boolean hasThree = hasSameRank(cards, 3);

        if (!hasThree) {
            return false;
        }

        List<Card> remainingCards = new ArrayList<>(cards);

        // Remove the three of a kind cards

        Map<Rank, Integer> rankCount = getRankCount(cards);

        for (Rank rank : rankCount.keySet()) {
            if (rankCount.get(rank) == 3) {
                remainingCards.removeIf(card -> card.getRank() == rank);
            }
        }

        boolean hasPair = hasSameRank(remainingCards, 2);

        return hasPair;
    }

    private boolean isFlush(List<Card> cards) {
        Map<Suit, Integer> suitCount = new HashMap<>();
        for (Card card : cards) {
            suitCount.put(card.getSuit(), suitCount.getOrDefault(card.getSuit(), 0) + 1);
            if (suitCount.get(card.getSuit()) >= 5)
                return true;
        }
        return false;
    }

    private boolean isStraight(List<Card> cards) {
        Set<Integer> uniqueValues = cards.stream().map(card -> card.getRank().getValue()).collect(Collectors.toSet());
        List<Integer> sortedValues = new ArrayList<>(uniqueValues);
        Collections.sort(sortedValues);

        // Check for Ace low straight
        if (uniqueValues.contains(Rank.ACE.getValue()) && uniqueValues.contains(Rank.TWO.getValue()) && uniqueValues.contains(Rank.THREE.getValue()) && uniqueValues.contains(Rank.FOUR.getValue()) && uniqueValues.contains(Rank.FIVE.getValue())) {
            return true;
        }

        // Check for normal straight
        int consecutive = 1;
        for (int i = 1; i < sortedValues.size(); i++) {
            if (sortedValues.get(i) == sortedValues.get(i - 1) + 1) {
                consecutive++;
                if (consecutive >= 5)
                    return true;
            } else {
                consecutive = 1;
            }
        }
        return false;
    }

    private boolean isOnePair(List<Card> cards) {
        return hasSameRank(cards, 2);
    }

    private boolean isThreeOfAKind(List<Card> cards) {
        return hasSameRank(cards, 3);
    }

    private boolean isTwoPair(List<Card> cards) {
        Map<Rank, Integer> rankCount = getRankCount(cards);
        int pairCount = 0;

        for (int count : rankCount.values()) {
            if (count == 2)
                pairCount++;
        }
        return pairCount >= 2;
    }

    private boolean hasSameRank(List<Card> cards, int n) {
        Map<Rank, Integer> rankCount = getRankCount(cards);
        for (int count : rankCount.values()) {
            if (count >= n)
                return true;
        }
        return false;
    }

    private Map<Rank, Integer> getRankCount(List<Card> cards) {
        Map<Rank, Integer> rankCount = new HashMap<>();
        for (Card card : cards) {
            rankCount.put(card.getRank(), rankCount.getOrDefault(card.getRank(), 0) + 1);
        }
        return rankCount;
    }

    private List<Card> findBestFiveCardHand(List<Card> playerCards, List<Card> communityCards) {
        List<Card> allCards = new ArrayList<>(playerCards);
        allCards.addAll(communityCards);

        List<List<Card>> allCombinations = generateCombinations(allCards, 5);
        List<Card> bestHand = null;
        HandResult bestResult = null; // Change from int to HandResult

        for (List<Card> combination : allCombinations) {
            HandResult result = evaluateCombination(combination); // Get the HandResult
            if (bestResult == null || result.compareTo(bestResult) > 0) { // Compare HandResult objects
                bestResult = result;
                bestHand = combination;
            }
        }
        return bestHand;
    }

    private List<List<Card>> generateCombinations(List<Card> cards, int k) {
        List<List<Card>> combinations = new ArrayList<>();
        generateCombinationsHelper(cards, new ArrayList<>(), 0, k, combinations);
        return combinations;
    }

    private void generateCombinationsHelper(List<Card> cards, List<Card> current, int start, int k, List<List<Card>> result) {
        if (current.size() == k) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (int i = start; i < cards.size(); i++) {
            current.add(cards.get(i));
            generateCombinationsHelper(cards, current, i + 1, k, result);
            current.remove(current.size() - 1);
        }
    }

    private HandResult evaluateCombination(List<Card> hand) {
        hand.sort(Comparator.comparingInt(c -> c.getRank().getValue())); // Sort cards by rank

        if (isStraightFlush(hand)) {
            return new HandResult(8000, getStraightValues(hand));
        }
        if (isFourOfAKind(hand)) {
            return new HandResult(7000, getFourOfAKindValues(hand));
        }
        if (isFullHouse(hand)) {
            return new HandResult(6000, getFullHouseValues(hand));
        }
        if (isFlush(hand)) {
            return new HandResult(5000, getFlushValues(hand));
        }
        if (isStraight(hand)) {
            return new HandResult(4000, getStraightValues(hand));
        }
        if (isThreeOfAKind(hand)) {
            return new HandResult(3000, getThreeOfAKindValues(hand));
        }
        if (isTwoPair(hand)) {
            return new HandResult(2000, getTwoPairValues(hand));
        }
        if (isOnePair(hand)) {
            return new HandResult(1000, getOnePairValues(hand));
        }
        return new HandResult(0, getHighCardValues(hand)); // High Card
    }

    private List<Integer> getOnePairValues(List<Card> cards) {
        Map<Rank, Long> rankCount = getRankCounts(cards);
        List<Integer> pair = rankCount.entrySet().stream().filter(entry -> entry.getValue() == 2).map(entry -> entry.getKey().getValue()).sorted(Comparator.reverseOrder()).toList();

        List<Integer> highCards = cards.stream().filter(card -> card.getRank().getValue() != pair.get(0)) // Exclude pair cards
                .map(card -> card.getRank().getValue()).sorted(Comparator.reverseOrder()).limit(3) // Remaining 3 cards
                .toList();

        List<Integer> result = new ArrayList<>(pair);
        result.addAll(highCards);
        return result;
    }

    private List<Integer> getTwoPairValues(List<Card> cards) {
        Map<Rank, Long> rankCount = getRankCounts(cards);
        List<Integer> pairs = rankCount.entrySet().stream().filter(entry -> entry.getValue() == 2).map(entry -> entry.getKey().getValue()).sorted(Comparator.reverseOrder()).toList();

        List<Integer> highCard = cards.stream().filter(card -> !pairs.contains(card.getRank().getValue())) // Exclude pair cards
                .map(card -> card.getRank().getValue()).sorted(Comparator.reverseOrder()).limit(1) // Only 1 remaining card
                .toList();

        List<Integer> result = new ArrayList<>(pairs);
        result.addAll(highCard);
        return result;
    }

    private List<Integer> getThreeOfAKindValues(List<Card> cards) {
        Map<Rank, Long> rankCount = getRankCounts(cards);
        List<Integer> threeOfAKind = rankCount.entrySet().stream().filter(entry -> entry.getValue() == 3).map(entry -> entry.getKey().getValue()).toList();

        List<Integer> highCards = cards.stream().filter(card -> card.getRank().getValue() != threeOfAKind.get(0)).map(card -> card.getRank().getValue()).sorted(Comparator.reverseOrder()).limit(2) // Remaining 2 cards
                .toList();

        List<Integer> result = new ArrayList<>(threeOfAKind);
        result.addAll(highCards);
        return result;
    }

    private List<Integer> getStraightValues(List<Card> cards) {
        Set<Integer> uniqueValues = cards.stream().map(card -> card.getRank().getValue()).collect(Collectors.toSet());

        List<Integer> sortedValues = new ArrayList<>(uniqueValues);
        Collections.sort(sortedValues);

        for (int i = sortedValues.size() - 1; i >= 4; i--) {
            if (sortedValues.get(i) == sortedValues.get(i - 1) + 1 && sortedValues.get(i - 1) == sortedValues.get(i - 2) + 1 && sortedValues.get(i - 2) == sortedValues.get(i - 3) + 1 && sortedValues.get(i - 3) == sortedValues.get(i - 4) + 1) {
                return List.of(sortedValues.get(i));
            }
        }

        return List.of(); // No straight found
    }

    private List<Integer> getFlushValues(List<Card> cards) {
        Map<Suit, List<Integer>> suitToValues = cards.stream().collect(Collectors.groupingBy(Card::getSuit, Collectors.mapping(card -> card.getRank().getValue(), Collectors.toList())));

        for (List<Integer> values : suitToValues.values()) {
            if (values.size() >= 5) {
                return values.stream().sorted(Comparator.reverseOrder()).limit(5).toList();
            }
        }
        return List.of();
    }

    private List<Integer> getFullHouseValues(List<Card> cards) {
        Map<Rank, Long> rankCount = getRankCounts(cards);

        List<Integer> threeOfAKind = rankCount.entrySet().stream().filter(entry -> entry.getValue() >= 3).map(entry -> entry.getKey().getValue()).sorted(Comparator.reverseOrder()).toList();

        List<Integer> pairs = rankCount.entrySet().stream().filter(entry -> entry.getValue() >= 2).map(entry -> entry.getKey().getValue()).sorted(Comparator.reverseOrder()).toList();

        if (!threeOfAKind.isEmpty() && pairs.size() >= 2) {
            return List.of(threeOfAKind.get(0), pairs.get(1)); // Highest three, next highest pair
        }
        return List.of();
    }

    private List<Integer> getFourOfAKindValues(List<Card> cards) {
        Map<Rank, Long> rankCount = getRankCounts(cards);

        List<Integer> fourOfAKind = rankCount.entrySet().stream().filter(entry -> entry.getValue() == 4).map(entry -> entry.getKey().getValue()).toList();

        List<Integer> highCard = cards.stream().filter(card -> card.getRank().getValue() != fourOfAKind.get(0)).map(card -> card.getRank().getValue()).sorted(Comparator.reverseOrder()).limit(1).toList();

        List<Integer> result = new ArrayList<>(fourOfAKind);
        result.addAll(highCard);
        return result;
    }

    private Map<Rank, Long> getRankCounts(List<Card> cards) {
        return cards.stream().collect(Collectors.groupingBy(Card::getRank, Collectors.counting()));
    }

    private List<Integer> getHighCardValues(List<Card> cards) {
        return cards.stream().map(card -> card.getRank().getValue()).sorted(Comparator.reverseOrder()) // Absteigend sortiert
                .toList();
    }

    private void playGame() {
        // Spieler erstellen
        Player player1 = new Player("Spieler 1", drawCard(), drawCard());
        Player player2 = new Player("Spieler 2", drawCard(), drawCard());

        // 5 Karten auf den Tisch (Community Cards)
        List<Card> communityCards = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            communityCards.add(drawCard());
        }

        printGameResult(player1, player2, communityCards);
    }

    private void printGameResult(Player player1, Player player2, List<Card> communityCards) {

        // Ausgabe der Karten
        System.out.println("=== Community Cards ===");
        for (Card card : communityCards) {
            System.out.println(card);
        }

        System.out.println("\n=== Spieler-Karten ===");
        System.out.println(player1);
        System.out.println(player2);

        // Beste Hände bestimmen
        List<Card> player1BestHand = findBestFiveCardHand(player1.getCards(), communityCards);
        List<Card> player2BestHand = findBestFiveCardHand(player2.getCards(), communityCards);

        System.out.println("\n=== Beste Hände der Spieler ===");
        System.out.println(player1.getName() + " beste Hand: " + player1BestHand + " - Kombination: " + evaluateCombination(player1BestHand));
        System.out.println(player2.getName() + " beste Hand: " + player2BestHand + " - Kombination: " + evaluateCombination(player2BestHand));

        System.out.println();

        HandResult player1Result = evaluateCombination(player1BestHand);
        HandResult player2Result = evaluateCombination(player2BestHand);

        System.out.println(player1.getName() + " beste Hand: " + player1BestHand + " - " + player1Result);
        System.out.println(player2.getName() + " beste Hand: " + player2BestHand + " - " + player2Result);

        System.out.println();

        // Vergleiche Ergebnisse
        int result = player1Result.compareTo(player2Result);
        if (result > 0) {
            System.out.println(player1.getName() + " gewinnt mit der besseren Hand!");
        } else if (result < 0) {
            System.out.println(player2.getName() + " gewinnt mit der besseren Hand!");
        } else {
            System.out.println("Unentschieden!");
        }
    }

    class HandResult implements Comparable<HandResult> {
        private final int combinationRank; // Value of the combination (0-8)
        private final List<Integer> tiebreakerValues; // Card values for tiebreaking

        public HandResult(int combinationRank, List<Integer> tiebreakerValues) {
            this.combinationRank = combinationRank;
            this.tiebreakerValues = tiebreakerValues;
        }

        public int getCombinationRank() {
            return combinationRank;
        }

        public List<Integer> getTiebreakerValues() {
            return tiebreakerValues;
        }

        @Override
        public int compareTo(HandResult other) {
            if (this.combinationRank != other.combinationRank) {
                return Integer.compare(this.combinationRank, other.combinationRank);
            }
            // Tiebreaker: Compare the card values one by one
            for (int i = 0; i < Math.min(this.tiebreakerValues.size(), other.tiebreakerValues.size()); i++) {
                int compare = Integer.compare(this.tiebreakerValues.get(i), other.tiebreakerValues.get(i));
                if (compare != 0)
                    return compare;
            }
            return 0; // Tie if all tiebreakers are the same
        }

        @Override
        public String toString() {
            return "Combination Rank: " + combinationRank + ", Tiebreakers: " + tiebreakerValues;
        }
    }
}