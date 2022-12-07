/**
 * File: Controller.java
 * Assignment: CSC335PA3
 * @author Cezar Rata
 *
 * Description: This is the WordleGame class. It represents the current state of the Wordle game. 
 * It does not handle any guess checking/evaluation functionality.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class WordleGame {

	private static String[][] guesses;
	private static String[][] guessEvaluations;
	private static HashSet<String> possAnswers;
	private static String answer;
	private static boolean isGameOver;
	private static final String CORRECT = "c";
	private static final String PRESENT = "p";
	private static final String ABSENT = "a";
	private static final String ENGLISH_ANSWERS = "src/main/java/answers.txt";
	private static final int numRows = 6;
	private static final int numCols = 5;

	public WordleGame() throws FileNotFoundException {
		readAnswersFile();
		init();
	}
	
	/**
	 * Reads the file that contains all the possible answers and stores them in the possAnswers HashSet
	 * @throws FileNotFoundException
	 */
	private static void readAnswersFile() throws FileNotFoundException {
		possAnswers = new HashSet<String>();
		Scanner scanner = new Scanner(new File(ENGLISH_ANSWERS));
        while (scanner.hasNext()) {
            String s = scanner.nextLine();
            possAnswers.add(s.toLowerCase());
        }

	}

	/**
	 * Initalizes the game
	 */
	public void init() {
		guesses = new String[numRows][numCols];
		guessEvaluations = new String[numRows][numCols];
//		answer = generateAnswer();
		answer = "cloak";
		isGameOver = false;
	}
	
	/**
	 * Generates a random answer for the game
	 * @return
	 */
	private static String generateAnswer() {
		String[] answers = possAnswers.toArray(new String[possAnswers.size()]);
		Random random = new Random();
		int randomIdx = random.nextInt(possAnswers.size());
		System.out.println("Answer: "+answers[randomIdx]);
		return answers[randomIdx];
	}

	/**
	 * Adds a guess to the array of guesses
	 * @param guess
	 * @param row
	 * @param numLetters
	 */
	public void addGuess(String guess, int row, int numLetters) {
		for (int i = 0; i < numCols; i++) {
			if (i < numLetters) {
				guesses[row][i] = String.valueOf(guess.charAt(i));
			} else {
				guesses[row][i] = " ";
			}
		}
		
		if (row == numRows-1) {
			isGameOver = true;
		}

	}

	/**
	 * Adds a guess evaluation to the array of guess evaluations
	 * @param guessEvaluation
	 * @param row
	 */
	public void addGuessEvaluation(String[] guessEvaluation, int row) {
		for (int i = 0; i < numCols; i++) {
			guessEvaluations[row][i] = guessEvaluation[i];
		}
		
		if (allCorrect(guessEvaluation)) {
			isGameOver = true;
		}
		
	}
	
	/**
	 * Checks if all the evaluations in a guess evaluation are correct
	 * @param guessEvaluation
	 * @return
	 */
	public boolean allCorrect(String[] guessEvaluation) {
		for (int i = 0; i < numCols; i++) {
			if (!(guessEvaluation[i].equals(CORRECT))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Getter for the 2D array of guesses
	 * @return
	 */
	public String[][] getGuesses() { return guesses; }

	/**
	 * Getter for the 2D array of guess evaluations
	 * @return
	 */
	public String[][] getGuessEvaluations() { return guessEvaluations; }

	/**
	 * Returns true if game is over
	 * @return
	 */
	public boolean isGameOver() { return isGameOver; }

	/**
	 * Starts a new Wordle game
	 */
	public void newGame() { init(); }

	/**
	 * Getter for how big the guessses are
	 * @return
	 */
	public int getMaxGuessSize() { return numCols; }

	/**
	 * Getter for how many allowable guesses
	 * @return
	 */
	public int getMaxGuesses() { return numRows; }

	/**
	 * Returns the answer for the game
	 * @return
	 */
	public String getAnswer() { return answer; }
	
	/**
	 * Changes the answer for a game
	 * @param ans
	 */
	public void changeAnswer(String ans) { answer = ans; }
	
}
