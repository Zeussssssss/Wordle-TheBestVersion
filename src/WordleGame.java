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
	private static final String ENGLISH_ANSWERS = "src/answers.txt";
	private static final int numRows = 6;
	private static final int numCols = 5;

	public WordleGame() throws FileNotFoundException {
		readAnswersFile();
		init();
	}
	
	private static void readAnswersFile() throws FileNotFoundException {
		possAnswers = new HashSet<String>();
		Scanner scanner = new Scanner(new File(ENGLISH_ANSWERS));
        while (scanner.hasNext()) {
            String s = scanner.nextLine();
            possAnswers.add(s.toLowerCase());
        }

	}

	public void init() {
		guesses = new String[numRows][numCols];
		guessEvaluations = new String[numRows][numCols];
		answer = generateAnswer();
//		answer = ""
		isGameOver = false;
	}
	
	private static String generateAnswer() {
		String[] answers = possAnswers.toArray(new String[possAnswers.size()]);
		Random random = new Random();
		int randomIdx = random.nextInt(possAnswers.size());
		System.out.println("Answer: "+answers[randomIdx]);
		return answers[randomIdx];
	}

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

	public void addGuessEvaluation(String[] guessEvaluation, int row) {
		for (int i = 0; i < numCols; i++) {
			guessEvaluations[row][i] = guessEvaluation[i];
		}
		
		if (allCorrect(guessEvaluation)) {
			isGameOver = true;
		}
	}
	
	public boolean allCorrect(String[] guessEvaluation) {
		for (int i = 0; i < numCols; i++) {
			if (!(guessEvaluation[i].equals(CORRECT))) {
				return false;
			}
		}
		return true;
	}

	public String[][] getGuesses() { return guesses; }

	public String[][] getGuessEvaluations() { return guessEvaluations; }

	public boolean isGameOver() { return isGameOver; }

	public void newGame() { init(); }

	public int getMaxGuessSize() { return numCols; }

	public int getMaxGuesses() { return numRows; }

	public String getAnswer() { return answer; }
	
}