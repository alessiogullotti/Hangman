/*
 * Code used in the "Software Engineering" course.
 *
 * Copyright 2017 by Claudio Cusano (claudio.cusano@unipv.it)
 * Dept of Electrical, Computer and Biomedical Engineering,
 * University of Pavia.
 */
import hangman.Game;
import hangman.Hangman;
import hangman.Player;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Manage a player playing with the terminal.
 *
 * @author Claudio Cusano <claudio.cusano@unipv.it>
 */
public class ConnectedClient extends Player implements Runnable{

    BufferedReader console;
    Socket clientsocket;
    boolean play;

    BufferedReader in;
    DataOutputStream out = null;
    String stampa;


    /**
     * Constructor.
     */
    public ConnectedClient(Socket socket) throws IOException {
        stampa="";
        console = new BufferedReader(new InputStreamReader(System.in));
        play=true;
        clientsocket=socket;
        in = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
        out = new DataOutputStream(clientsocket.getOutputStream());



    }

    public String invia() throws  IOException {
        System.out.println("4. aspetto un messaggio");
        String  console = in.readLine();//////////////////////////////////////

        System.out.println("5. messsaggio ricevuto " + console);
        String risposta = console.toUpperCase();

        return risposta;

    }

    @Override
    public void update(Game game) {
        switch(game.getResult()) {
            case FAILED:
                stampa="";
                stampa+="CLOSED\n";
                printBanner("Hai perso!  La parola da indovinare era '" +
                        game.getSecretWord() + "'");
                try {
                    System.out.println("update fine");
                    out.writeBytes(stampa);
                    String response=in.readLine();
                    System.out.println(response);
                    if(response.toUpperCase().equals("NO")){
                        play=false;
                    }
                }
                catch (Exception e){}

                break;
            case SOLVED:
                stampa="";
                stampa+="CLOSED\n";
                printBanner("Hai indovinato!   (" + game.getSecretWord() + ")");
                try{
                    System.out.println("update vinto");
                    out.writeBytes(stampa);
                    String response=in.readLine();
                    System.out.println("gioca di nuovo");

                    if(response.toUpperCase().equals("NO")){
                        play=false;
                    }
                } catch (Exception e){}
                break;
            case OPEN:
                int rem = Game.MAX_FAILED_ATTEMPTS - game.countFailedAttempts();
                stampa="";
                stampa+="OPEN";
                System.out.print("\n" + rem + " tentativi rimasti\n");
                System.out.println(this.gameRepresentation(game));
                System.out.println(game.getKnownLetters());
                stampa+="\n" + rem + " tentativi rimasti\n"+this.gameRepresentation(game)+'\n'+game.getKnownLetters()+'\n'+"End\n";
                try {
                    System.out.println("qua");
                    out.writeBytes(stampa);
                }
                catch (Exception e){}
                break;
        }
    }

    private String gameRepresentation(Game game) {
        int a = game.countFailedAttempts();

        String s = "   ___________\n  /       |   \n  |       ";
        s += (a == 0 ? "\n" : "O\n");
        s += "  |     " + (a == 0 ? "\n" : (a < 5
                ? "  +\n"
                : (a == 5 ? "--+\n" : "--+--\n")));
        s += "  |       " + (a < 2 ? "\n" : "|\n");
        s += "  |      " + (a < 3 ? "\n" : (a == 3 ? "/\n" : "/ \\\n"));
        s += "  |\n================\n";
        return s;
    }

    private void printBanner(String message) {
        System.out.println("");
        for (int i = 0; i < 80; i++)
            System.out.print("*");
        System.out.println("\n***  " + message);
        for (int i = 0; i < 80; i++)
            System.out.print("*");
        System.out.println("\n");
        stampa+="************************\n*** "+message+"\n**********************\nVuoi giocare ancora?\n"+"End\n";
    }

    /**
     * Ask the user to guess a letter.
     *
     * @param game
     * @return
     */
    public char chooseLetter(Game game) {
        for (;;) {


            String line = null;
            try {

                line = invia();
            } catch (IOException e) {
                line = "";
            }
            if (line.length() == 1 && Character.isLetter(line.charAt(0))) {
                System.out.println("giusto");
                return line.charAt(0);
            } else {
                System.out.println("Lettera non valida.\n");
                try {
                    out.writeBytes("Lettera non valida\n");
                }
                catch (IOException e){}

            }
        }
    }

    public boolean isPlay() {
        return play;
    }
    public void run() {
        Hangman game = new Hangman();
        // Player player = new ArtificialPlayer();
        while(play){
            game.playGame(this);
        }
    }
}