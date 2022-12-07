/**
 * File: Controller.java
 * Assignment: CSC335PA3
 * @author Cezar Rata
 * 
 * Description: This is the GuessEvaluatior class. It contains functionality to evaluate guesses and check if they are valid.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

class GuessEvaluator {

	private static HashSet<String> allowedGuesses;
	private static final String CORRECT = "c";
	private static final String PRESENT = "p";
	private static final String ABSENT = "a";
	private static final String ENGLISH_ANSWERS = "src/main/java/answers.txt";
	private static final String ENGLISH_GUESSES = "src/main/java/allowed_guesses.txt";

	public GuessEvaluator() {
		try {
			readAllowedGuesses();
		} catch (FileNotFoundException e) {
			System.out.println("Could not find file :(");
			e.printStackTrace();
		}
	}

	/**
	 * Reads the guesses that are allowed from files.
	 * @throws FileNotFoundException
	 */
	private static void readAllowedGuesses() throws FileNotFoundException {
		allowedGuesses = new HashSet<String>();
		Scanner scannerGuesses = new Scanner(new File(ENGLISH_GUESSES));
		while (scannerGuesses.hasNext()) {
			String s = scannerGuesses.nextLine();
			allowedGuesses.add(s.toLowerCase());
		}
		Scanner scannerAnswers = new Scanner(new File(ENGLISH_ANSWERS));
		while (scannerAnswers.hasNext()) {
			String s = scannerAnswers.nextLine();
			allowedGuesses.add(s.toLowerCase());
		}
	}

	/**
	 * Returns true if the word is in the vocabulary
	 * @param word
	 * @return
	 */
	public boolean isInVocabulary(String word) { return allowedGuesses.contains(word); }

	/**
	 * Returns the evaluation for a guess
	 * 
	 * "c" represents the letter is in the correct location
	 * "p" reprsents the letter is in the word but not in the correct location
	 * "a" represents the letter is not in the word
	 * 
	 * Handles words with double/triple letters
	 * @param guess
	 * @param answer
	 * @return
	 */
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

		// first loop checks for correct guesses and decrements that letter from the
		// frequency dictionary
		for (int i = 0; i < wordSize; i++) {
			char curLetter = guess.charAt(i);
			char corLetter = answer.charAt(i);

			if (curLetter == corLetter) {
				evaluation.put(i, CORRECT);
				frequency.put(String.valueOf(corLetter), frequency.get(String.valueOf(corLetter)) - 1);
			} else if (!(answer.contains(String.valueOf(curLetter)))) {
				evaluation.put(i, ABSENT);
			}
		}

		// second loop checks for present letters and handles words with double letters
		for (int i = 0; i < wordSize; i++) {
			String curLetter = String.valueOf(guess.charAt(i));
			if (!(evaluation.containsKey(i))) {
				if (removeCorrect(evaluation, answer).contains(curLetter) && frequency.get(curLetter) > 0) {
					evaluation.put(i, PRESENT);
					frequency.put(curLetter, frequency.get(curLetter) - 1);
				} else {
					evaluation.put(i, ABSENT);
				}
			}
		}

		// sort evaluations in order of indexes
		TreeMap<Integer, String> sortedEvaluation = new TreeMap<Integer, String>();
		sortedEvaluation.putAll(evaluation);

		// add evalutions to String array
		String[] evaluations = new String[wordSize];
		for (int i = 0; i < wordSize; i++) {
			evaluations[i] = sortedEvaluation.get(i);
		}
		return evaluations;
	}

	/**
	 * Helper method for evaluateGuess() which removes the letters that are in the correct location from the word
	 * @param evaluation
	 * @param answer
	 * @return
	 */
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
