import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

// skill is (entropy / max_entropy)

class GuessEvaluator {

	private static HashSet<String> allowedGuesses;
	private static final String CORRECT = "c";
	private static final String PRESENT = "p";
	private static final String ABSENT = "a";
	private static final String ENGLISH_ANSWERS = "src/answers.txt";
	private static final String ENGLISH_GUESSES = "src/allowed_guesses.txt";

	public GuessEvaluator() {
		try {
			readAllowedGuesses();
		} catch (FileNotFoundException e) {
			System.out.println("Could not find file :(");
			e.printStackTrace();
		}
	}

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
			String curLetter = String.valueOf(guess.charAt(i));
			if (!(evaluation.containsKey(i))) {
				if (removeCorrect(evaluation, answer).contains(curLetter) && frequency.get(curLetter) > 0) {
					evaluation.put(i, PRESENT);
					frequency.put(curLetter, frequency.get(curLetter)-1);
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