package src.jeu;

import java.util.ArrayList;
import java.util.Scanner;

public class Game {
    ArrayList<Player> players;

    Game(){
        players = new ArrayList<>();
    }

    public void start(){
        System.out.println("Welcome to Munchkin, Please enter between 3 to 6 players :");
        System.out.println("Enter the player's name : ");
        Scanner scan = new Scanner(System.in);
        while(players.size() < 6){
            Player p = new Player(scan.nextLine());
            players.add(p);
            System.out.println("Added player : " + p);
        }
    }
}
