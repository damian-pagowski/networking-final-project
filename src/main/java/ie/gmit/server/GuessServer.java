package ie.gmit.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;

import static spark.Spark.get;

public class GuessServer {
    private static int attempts;
    private static int number;
    private static String messageWon = "Correct – you win!";
    private static String messageLost = "You’re out of guesses – you lose!";
    private static String messageLow = "Too low – guess again";
    private static String messageHigh = "Too high – guess again";
    private static String messageGameReset = "New Game Started";
    private static int[] scores;
    private static boolean isGameInProgress = false;
    private static final Logger logger
            = LoggerFactory.getLogger(GuessServer.class);

    private static void generateNewNumber() {
        number = (int) (Math.random() * 1000);
    }

    private static void initScores() {
        scores = new int[]{0, 0};
    }

    private static void resetAttempts() {
        attempts = 10;
    }

    public static void main(String[] args) {
        initScores();
        newGame();
        get("/current_game_state", (request, response) -> isGameInProgress ? "in_progress": "finished");
        get("/current_game_attempts", (request, response) -> attempts);
        get("/guess/:number", (request, response) -> processGuess(request));
        get("/reset", (request, response) -> {
            newGame();
            return messageGameReset;
        });
        get("/scores", (request, response) ->
                String.format("Server won: %d Client won: %d", scores[0], scores[1]));
    }

    private static void newGame() {
        generateNewNumber();
        logger.info("Selected Number: {}", number);
        resetAttempts();
        logger.info("Attempts remaining: {}", attempts);
        isGameInProgress = true;
    }

    private static String processGuess(Request request) {
        if (isGameInProgress) {
            int guess = Integer.parseInt(request.params(":number"));
            attempts--;
            logger.info("Attempts remaining: {}", attempts);
            logger.info("Client Guess: {}", guess);
            if (attempts < 0) {
                isGameInProgress = false;
                logger.info("Game is over. Server won");
                scores[0]++;
                return messageLost;
            }
            if (guess == number) {
                isGameInProgress = false;
                logger.info("Game is over. Client won");
                scores[1]++;
                return messageWon;
            } else if (guess < number) {
                return messageLow;
            } else if (guess > number) {
                return messageHigh;
            }
        }
        logger.info("Guess received when game is over.");
        return "Game over. Please start another game";
    }
}
