/**
 * File: Controller.java
 * Assignment: CSC335PA3
 * @author Aman Dwivedi
 *
 * Description: This is the Controller class. It handles all the back end operation. 
 */
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Controller {

	private WordleGame game;
	private UI ui;
	private static String curGuess;
	private static GuessEvaluator guessEvaluator;
	private static int curGuessIndex;
	private static int charIndex;
	private boolean won = false;

	private final static String ENTER = "ENTER";
	private final static String BACKSPACE = "BACKSPACE";

	// NETWORKING VARIABLES
	private static boolean singlePlayer;
	private static Socket socket = null;
	private static Scanner in;
	private static PrintWriter out;

	public Controller(UI newUI, WordleGame newGame, boolean single, String serverAdd) {
		ui = newUI;
		game = newGame;
		guessEvaluator = new GuessEvaluator();
		singlePlayer = single;
		if (!singlePlayer) {
			try {
				System.out.println(serverAdd);
				socket = new Socket(serverAdd, 59896);
				in = new Scanner(socket.getInputStream());
				out = new PrintWriter(socket.getOutputStream(), true);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		init();
	}

	private static void init() {
		curGuess = "";
		curGuessIndex = 0;
		charIndex = 0;
	}

	public void start() {
		// start the UI
		ui.start();
	}

	public WordleGame getGame() {
		return game;
	}

	public boolean update(String c) {
		if (c.equals(ENTER)) {
			if (!(guessEvaluator.isInVocabulary(curGuess))) {
				// checks if word is in vocab
				ui.throwWarning("Word not allowed");
				ui.animateRow(curGuessIndex, "wrong");
				// System.out.println("Word not allowed");
				return true;
			}
			String[] guessEvaluation = guessEvaluator.evaluateGuess(curGuess, game.getAnswer()); // returns color
																									// evaluation
			game.addGuessEvaluation(guessEvaluation, curGuessIndex);
			ui.animateRow(curGuessIndex, "right");
			if (game.allCorrect(guessEvaluation)) {
				this.won = true;
			}
			if (game.isGameOver()) {
				ui.endGame();
			}
			curGuess = "";
			curGuessIndex += 1;
			charIndex = 0;
			return true;
		} else if (c.equals(BACKSPACE)) {
			if (curGuess.length() == 0) {
				return false;
			}
			curGuess = curGuess.substring(0, curGuess.length() - 1); // take last char from string
			charIndex -= 1;
		} else if (!(Character.isLetter(c.charAt(0)))) {
			ui.throwWarning("Characters must be letters");
			// System.out.println("Chars must be letters");
			return false;
		} else {
			if (curGuess.length() == game.getMaxGuessSize()) {
				ui.throwWarning("Already typed 5 letters");
				// System.out.println("Typed 5 letters already");
				return false;
			}
			curGuess += c;
			charIndex += 1;
		}
		game.addGuess(curGuess, curGuessIndex, charIndex);
//		for (int i = 0; i < 6; i++) {
//			for (int j = 0; j < 5; j++) {
//				// System.out.print(game.getGuesses()[i][j]);
//			}
//			// System.out.println();
//		}
		return false;
	}

	private void newGame() {
		this.start();
	}
	
	public boolean won() {
		return won;
	}

	public void restart() {
		game.init();
		// guessEvaluator = new GuessEvaluator();
		init();
		// this.start();
	}

	public void saveGame() throws IOException {

		File file = new File("data.txt");
		FileWriter fr = new FileWriter(file, true);
		fr.write(this.won + " " + (curGuessIndex + 1) + "\n");
		fr.close();
	}
}