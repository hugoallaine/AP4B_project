package src.jeu;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

import src.jeu.Cards.*;
import src.jeu.Exceptions.*;

/**
 * This class represents the game and also contains the methods to display it in a console environement
 */
public class Game {
    private static final int MAX_PLAYER_NUM = 6;
    private static final int MIN_PLAYER_NUM = 3;
    public static final int MAX_CARD_IN_HAND = 5;

    private final ArrayList<Player> players;
    private final CardStack<TreasureCard> treasureCards;
    private final CardStack<EventCard> eventCards;
    private final CardStack<Card> discardPile;
    private Player currentPlayer;
    private final Random random;

    public Game(){
        players = new ArrayList<>();
        treasureCards = new CardStack<>();
        eventCards = new CardStack<>();
        discardPile = new CardStack<>();
        random = new Random();
        currentPlayer = null;
    }

    public String getPlayerString(){
        StringBuffer sb = new StringBuffer();
        for(Player player : this.players){
            sb.append("- " + player.getName() + "\n");
        }
        return sb.toString();
    }

    private boolean playerAlreadyExists(String player_name){
        for(Player p : players){
            if(p.getName().equals(player_name)){
                return true;
            }
        }
        return false;
    }

    public int getPlayerNum(){
        return this.players.size();
    }

    /**
     * 
     * @param playerName
     * @throws TooManyPlayersException
     * @throws SamePlayerException
     * @throws InvalidPlayerNameException
     */
    public void addPlayer(String playerName) throws TooManyPlayersException, SamePlayerException, InvalidPlayerNameException {
        if(this.players.size() >= MAX_PLAYER_NUM){
            throw new TooManyPlayersException();
        }
        if(!this.isNameValid(playerName)){
            throw new InvalidPlayerNameException();
        }
        if(this.playerAlreadyExists(playerName)){
            throw new SamePlayerException();
        }
        this.players.add(new Player(playerName));
    }

    private void registerPlayers(){
        Scanner scan = new Scanner(System.in);
        System.out.println("Welcome to Munchkin, Please enter between 3 to 6 players");
        boolean startGame = false;
        while (this.players.size() < MAX_PLAYER_NUM) {
            if (this.players.size() >= MIN_PLAYER_NUM) {
                String ans = "";
                do {
                    System.out.println("There are enough players to start the game. Do you want to start now? [y/n]");
                    ans = scan.nextLine();
                    if (ans.equals("y")) {
                        startGame = true;
                        break;
                    }
                } while (!ans.equals("n"));

                if (startGame) {
                    break;
                }
            }
            System.out.println("Enter the player's name: ");
            try{
                addPlayer(scan.nextLine());
            }
            catch(InvalidPlayerNameException invalidNameEx){
                System.out.println("The name you entered is invalid!");
            }
            catch(SamePlayerException spex){
                System.out.println("This name is already in use!");
            }
            catch(Exception ex){
                break;
            }
        }
        scan.close();
    }

    public void start() {
        this.createCards();
        this.eventCards.shuffle();
        this.treasureCards.shuffle();
        this.distributeCards();
        this.currentPlayer = this.players.get(this.random.nextInt(this.getPlayerNum()));
    }

    private void distributeCards() {
        for(Player player : players){
            for(int i = 0; i < 2; i++){
                player.addCard(treasureCards.draw());
                player.addCard(eventCards.draw());
            }
        }
    }

    public boolean isGameFinsihed() {
        for(Player player : players){
            if(player.getLevel() == 10){
                System.out.println("Game should be finished");
                return true;
            }
        }
        return false;
    }

    private void createCards(){
        // TODO
        // Hashtable<String, ArrayList<String>> cardData = JSONReader.readCSV("/home/olivier/Documents/Code/Java/AP4B_project/cards.csv");
        // cardData.forEach((k,v) -> {
        //     v.forEach(elm -> {
        //         if(k.equals("Amount")){
        //             int j = Integer.parseInt(elm);
        //         }
        //     });
        // });

        for(int i = 0; i < 80; i++) {
            this.eventCards.add(new ClassCard("Barbarian", "Description", "Barbarian", CardTargetMode.SELF));
            this.treasureCards.add(new XpCard("LevelUp", "Desc", 1, CardTargetMode.MULTIPLE));
        }
    }

    public void nextTurn() throws TooManyCardsInHandException{
        if(this.canFinishTurn()){
            int currentPlayerIndex = this.players.indexOf(this.currentPlayer);
            this.currentPlayer = this.players.get((currentPlayerIndex + 1) % this.players.size());
        }
    }

    @Override
    public String toString(){
        StringBuilder out = new StringBuilder("There are " + this.players.size() + " players\n");
        for(Player p : this.players){
            out.append(p + "\n");
        }
        return out.toString();
    }

    private boolean isNameValid(String name){
        return name != null && name.matches("^[a-zA-Z0-9]+$");
    }

    public Player getCurrentPlayer(){
        return this.currentPlayer;
    }

    public ArrayList<Player> getPlayers() {
        return this.players;
    }

    public EventCard drawFromEventStack() throws NoSuchElementException {
        EventCard cardDrawn = this.eventCards.draw();
        this.currentPlayer.addCard(cardDrawn);
        return cardDrawn;
    }

    public void drawFromTreasureStack() throws NoSuchElementException {
        this.currentPlayer.addCard(this.treasureCards.draw());
    }

    public boolean canFinishTurn() throws TooManyCardsInHandException{
        if(this.currentPlayer.getHand().size() > Game.MAX_CARD_IN_HAND){
            throw new TooManyCardsInHandException();
        }
        return true;
    }

    public void discard(Card card) {
        System.out.println(card instanceof EventCard);
        this.discardPile.add(card);
    }

}
