package ie.gmit.client;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Scanner;

public class GuessClient {

    private static String uriScores = "http://localhost:4567/scores";
    private static String uriReset = "http://localhost:4567/reset";
    private static String uriGuess = "http://localhost:4567/guess/";
    private static String uriGameState = "http://localhost:4567/current_game_state";
    private static         Scanner reader = new Scanner(System.in);


    public static void main(String[] args) throws IOException {
        newGame();
        play();
    }

    private static void displayScores() throws IOException {
        System.out.println(doGet(uriScores));
    }

    private static void newGame() throws IOException {
        System.out.println(doGet(uriReset));
    }

    private static void play() throws IOException {
        displayScores();
        boolean isGameInProgress = true;
        while (isGameInProgress) {
            int number = getUserInput();
            String result = doGet(uriGuess + number);
            System.out.println(result);
            isGameInProgress = doGet(uriGameState).equals("in_progress");
        }
        displayScores();
        askForNewGame();
    }

    private static int getUserInput() {
        System.out.println("Enter a number: ");
        String choice = reader.nextLine();
        return Integer.parseInt(choice);
    }

    private static void askForNewGame() throws IOException {
        System.out.println("Another game? Please press another game to continue or N|n to finish)");
        String choice  = reader.nextLine();
        if (! choice.toLowerCase().equals("n")){
            newGame();
            play();
        }else{
            System.out.println("Thank you for playing!");
            displayScores();
        }
    }

    private static String doGet(String uri) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(new HttpGet(uri));
         String bodyAsString = EntityUtils.toString(response.getEntity());
        return bodyAsString;
    }
}
