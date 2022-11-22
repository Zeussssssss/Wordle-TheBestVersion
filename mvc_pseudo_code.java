class UI {
	/*
	Most of this class code is pseudocode

	Access the game through the controller object
	*/

	Controller controller;
	
	public UI() {}

	void display() {
		/* for (row in controller.getGame().getMaxGuess() {
			for (guess in controller.getGame().getNaxGuessSize()) {
				display tile with the letter and color
			}
		} */

	...

	addKeyListener(c => {
		controller.update(c);
	})

	}

	void addController(Controller newController) { controller = newController; }

	void animateRow(int row) {
		// animate row of tiles
	}

	void endGame() {
		// doesn't take in anymore input
	}

	void throwWarning() {
		// throws a little error box for a couple seconds
	}

}

class Game {

	private String[][] guesses;
	private String[][] guessEvaluations;
	private HashSet possAnswers;
	private String answer;
	private boolean isGameOver;

	private static final int numRows = 6;
	private static final int numCols = 5;

	public Game() {
		possAnswers = readAnswersFile();
		init();
	}

	private static void init() {
		guesses = String[numRows][numCols];
		guessEvaluations = String[numRows][numCols];
		answer = random.choice(possAnswers);
		isGameOver = false;
	}

	public void addGuess(String guess, int row) {
		// add the String to guesses
		if (row == numRows-1) {
			isGameOver = true;
		}
	}

	public void addGuessEvaluation(String[] guessEvaluation, int row) {
		// add the evaluation to guessEvaluations
		// if evaluation is all green, isGameOver = true;
		
	}

	public String[][] getGuesses() { return guesses; }

	public String[][] getGuessEvaluations() { return guessEvaluations; }

	public boolean isGameOver() { return isGameOver; }

	public void newGame() { init(); }

	public int getMaxGuessSize() { return numCols; }

	public int getMaxGuesses() { return numRows; }

	public String getAnswer() { return answer; }

}


class Controller {

	private WordleGame game;
	private UI ui;
	private String curGuess = "";
	private GuessEvaluator guessEvaluator = new GuessEvaluator();
	private int curGuessIndex = 0;

	private final static String ENTER = "escape";
	private final static String BACKSPACE = "backspace";

	public Controller(UI newUI, WordleGame newGame) {
		ui = newUI;
		game = newGame;
	}

	public void start() {
		// start the UI
	}

	public void update(char c) {
		if (c == ENTER) {
			if (!(guessEvaluator.inVocabulary(curGuess))) { // checks if word is in vocab
				ui.throwWarning("Word not allowed");
				return;
			}
			String[] guessEvaluation = guessEvaluator.evaluate(curGuess, game.getAnswer()); // returns color evaluation
			game.addGuessEvaluation(guessEvaluation, curGuessIndex);
			ui.animateRow(curGuessIndex);
			if (game.isGameOver()) {
				ui.endGame();
			}

			curGuess = "";
			curGuessIndex += 1;
			return;
		} else if (c == BACKSPACE) {
			if (curGuess.size < 0) {
				return;
			}
			curGuess = curGuess.subString(0, curGuess.size-1); // take last char from string
		} else if (c.isNotAlphaNumeric()) {
			ui.throwWarning("Characters must be letters");
			return;
		} else {
			if (curGuess.size > game.getMaxGuessSize()) {
				ui.throwWarning("Already typed 5 letters");
				return;
			}
			curGuess += c;
		}
		game.addGuess(curGuess, curGuessIndex);
		return true;
	}

	public Game getGame() { return game; }

}

import java.util.*;

// basically fully functional
class GuessEvaluator {

	private HashSet<String> allowedGuesses;
	private static final String CORRECT = "correct";
	private static final String PRESENT = "present, but not in the right place";
	private static final String ABSENT = "absent";

	public GuessEvaluator() {
		allowedGuesses = readAllowedGuesses();
	}

	private static HashSet<String> readAllowedGuesses() {
		// TODO:
		// read file
		return new HashSet<String>();
	}

	public boolean isInVocabulary(String word) {
		return allowedGuesses.contains(word);
	}

	public String[] evaluateGuess(String guess, String answer) {
		int wordSize = answer.length();
		HashMap<String, Integer> frequency = new HashMap<String, Integer>();

		// gets a frequency count of all letters
		for (int i = 0; i < wordSize; i++) {
			char letter = answer.charAt(i);
			int count = frequency.containsKey(String.valueOf(letter)) ? frequency.get(String.valueOf(letter)) : 0;
			frequency.put(String.valueOf(letter), count + 1);
		}

		HashMap<Integer, String> evaluation = new HashMap<Integer, String>();

		// first loop checks for correct guesses and decrements that letter from the frequency dictionary
		for (int i = 0; i < wordSize; i++) {
			char curLetter = guess.charAt(i);
			char corLetter = answer.charAt(i);

			if (curLetter == corLetter) {
				evaluation.put(i, CORRECT);
				frequency.put(String.valueOf(corLetter), frequency.get(String.valueOf(corLetter))-1);
			} else if (!(answer.contains(String.valueOf(curLetter)))) {
				evaluation.put(i, ABSENT);
			}
		} 

		// second loop checks for present letters and handles words with double letters
		for (int i = 0; i < wordSize; i++) {
			char curLetter = guess.charAt(i);
			if (!(evaluation.containsKey(i))) {
				if (removeCorrect(evaluation, answer).contains(String.valueOf(curLetter)) && frequency.get(String.valueOf(curLetter)) > 0) {
					evaluation.put(i, PRESENT);
				} else {
					evaluation.put(i, ABSENT);
				}
			}
		}

		// sort evaluations in order of indexes
		TreeMap<Integer, String> sortedEvaluation = new TreeMap<Integer, String>();
		sortedEvaluation.putAll(evaluation);

		// add evalutions to String arry
		String[] evaluations = new String[wordSize];
		for (int i = 0; i < wordSize; i++) {
			evaluations[i] = sortedEvaluation.get(i);
		}
		return evaluations;
	}

	private static String removeCorrect(HashMap<Integer, String> evaluation, String answer) {
		String newAnswer = "";
		for (int i = 0; i < answer.length(); i++) {
			char letter = answer.charAt(i);
			if (evaluation.containsKey(i) && !(evaluation.get(i).equals(CORRECT))) {
				newAnswer += letter;
			} else if (!(evaluation.containsKey(i))) {
				newAnswer += letter;
			}
		}
		return newAnswer;
	}


}


class Main {

	public static void main(String[] args) {
		UI ui = new UI();
		WordleGame game = new Game();
		Controller controller = new Controller(ui, game);
		controller.start();
	}

}