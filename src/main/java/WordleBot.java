/**
 * File: Controller.java
 * Assignment: CSC335PA3
 * @author Cezar Rata
 * 
 * Description: This is the WordleBot class. It contains functionality to evaluate how good the players skill were 
 * as well as play a game optimally.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class WordleBot {

	private static HashMap<Integer, HashMap<String, HashMap<String, String>>> botGuessEvaluations;
	private static HashSet<String> corpus;
	private static GuessEvaluator guessEvaluator;
	private static String[][] optimalGuesses;
	private static String[][] optimalGuessesEvaluations;
	private static final String ENGLISH_ANSWERS = "src/main/java/answers.txt";
	private static final int NUM_SOLUTIONS = 10;
	private static final String CORRECT = "c";
	private static final String PRESENT = "p";
	private static final String ABSENT = "a";
	private static final String FIRSTGUESS = "raise";
	
	public WordleBot() {
		guessEvaluator = new GuessEvaluator();
		botGuessEvaluations = new HashMap<Integer, HashMap<String, HashMap<String, String>>>();
		try {
			readCorpus();
		} catch (FileNotFoundException e) {
			System.out.println("Could not find file reading corpus :(");
		}
	};
	
	/**
	 * Returns the bots evaluations of the players guesses
	 * The data is stored in a map structured with the following schema:
	 * {
	 * 1 : {
	 * 	"player": {
	 * 		"guess": "least",
	 * 		"entropy": "4.8",
	 * 		"skill": "99",
	 * 		"solutions_after_guess": "293'
	 * 		},
	 * 	"optimal_1": {
	 * 		"word": "slate",
	 * 		"entropy": "4.8",
	 * 		"skill": "99"
	 * 		},
	 * 	"optimal_2": {
	 * 		},
	 * 	"optimal_10": {
	 * 		}
	 * 	},
	 * 2: {
	 *	},
	 * 6: {
	 * 	},
	 * }
	 * 
	 * Each integer key represents the guess number. 
	 * Inside inside that hashmap, the player key represents player data for that guess.
	 * The optimal_n key represents the data for the nth best guess after the players guess, up to the 10th best.
	 * @return
	 */
	public HashMap<Integer, HashMap<String, HashMap<String, String>>> getBotGuessEvaluations() { return botGuessEvaluations; } 
	
	/**
	 * Returns the 2D array of optimal guesses
	 * @return
	 */
	public String[][] getOptimalGuesses() { return optimalGuesses; }
	
	/**
	 * Returns the 2D array of the evaluations for the optimal guesses
	 * @return
	 */
	public String[][] getOptimalGuessesEvaluations() { return optimalGuessesEvaluations; }
	
	/**
	 * Calls the prettyPrint() function
	 */
	public void pprint() { prettyPrint(); }
	
	/**
	 * Returns the average skill for the player that game
	 * @return
	 */
	public float getAverageSkill() {
		int totalSkill = 0;
		for (int guessIdx: botGuessEvaluations.keySet()) {
			totalSkill += Integer.parseInt(botGuessEvaluations.get(guessIdx).get("player").get("skill"));
		}
		return totalSkill / botGuessEvaluations.size();	
	}
	
	/**
	 * Reads the file containing words it can use to guess with
	 * @throws FileNotFoundException
	 */
	private static void readCorpus() throws FileNotFoundException {
		corpus = new HashSet<String>();
		Scanner scanner = new Scanner(new File(ENGLISH_ANSWERS));
        while (scanner.hasNext()) {
            String s = scanner.nextLine();
            corpus.add(s.toLowerCase());
        }
	}
	
	/**
	 * Plays a game of wordle optimally
	 * @param game
	 */
	public void play(WordleGame game) {
		String answer = game.getAnswer();
		String guess = FIRSTGUESS;
		optimalGuessesEvaluations = new String[game.getMaxGuesses()][game.getMaxGuessSize()];
		optimalGuesses = new String[game.getMaxGuesses()][game.getMaxGuessSize()];
		HashSet<String> possSolutions = new HashSet<String>(corpus);
		
		for (int i = 0; i < game.getMaxGuesses(); i++) {
			String[] evaluation = guessEvaluator.evaluateGuess(guess, answer);			
			optimalGuessesEvaluations[i] = evaluation;
			for (int j = 0; j < game.getMaxGuessSize(); j++) {
				optimalGuesses[i][j] = String.valueOf(guess.charAt(j));
			}
			if (game.allCorrect(evaluation)) {
				return;
			}
			possSolutions = wordsAfterGuess(possSolutions, readArrayRow(evaluation), guess);
			guess = findOptimalGuess(possSolutions);
		}
	}
	
	/**
	 * Evalautes the players skill based on their guesses
	 * @param game
	 */
	public void evaluate(WordleGame game) {
		String[][] guesses = game.getGuesses();
		String[][] evaluations = game.getGuessEvaluations();
		HashSet<String> possSolutions = new HashSet<String>(corpus);
		
		for (int i = 0; i < guesses.length; i++) {
			if (guesses[i][0] == null) {
				break;
			}

			HashMap<String, String> playerData = new HashMap<String, String>();
			String guess = readArrayRow(guesses[i]);
			String evaluation = readArrayRow(evaluations[i]);
			
			playerData.put("guess", guess);
			float entropy = evaluateWordEntropy(guess, possSolutions);
			playerData.put("entropy", String.valueOf(roundToTens(entropy)));
			
			HashMap<String, Float> optimalGuesses = i > 0 ? findOptimalGuesses(possSolutions) : firstGuessOptimal();
			playerData.put("skill", String.valueOf(getGuessSkill(entropy, optimalGuesses, i)));

			possSolutions = wordsAfterGuess(possSolutions, evaluation, guess);
			playerData.put("solutions_after_guess", String.valueOf(possSolutions.size()));
			
			HashMap<String, HashMap<String, String>> guessData = new HashMap<String, HashMap<String, String>>();
			guessData.put("player", playerData);
			int counter = 0;
			for (String optimalGuess: optimalGuesses.keySet()) {
				if (counter == NUM_SOLUTIONS) {
					break;
				}
				HashMap<String, String> optimalGuessData = new HashMap<String, String>();
				optimalGuessData.put("word", optimalGuess);
				optimalGuessData.put("entropy", String.valueOf(roundToTens(optimalGuesses.get(optimalGuess))));
				optimalGuessData.put("skill", String.valueOf(getGuessSkill(optimalGuesses.get(optimalGuess), optimalGuesses, i)));
				guessData.put("optimal_" + (counter+1), optimalGuessData);
				counter ++;
			}
			botGuessEvaluations.put(i + 1, guessData);
			
		}
//		System.out.println("Math Page Structure:\n");
//		prettyPrint();
	}
	
	/**
	 * Rounds a float to the 10s decimal
	 * @param n
	 * @return
	 */
	private static float roundToTens(float n) { return (float) Math.round(n * 10) / 10; }
	
	/**
	 * Prints the guess evaluations in a readable manner
	 */
	private static void prettyPrint() {
		for (int guess: botGuessEvaluations.keySet()) {
			System.out.println(guess + ": {");
			HashMap<String, String> playerData = botGuessEvaluations.get(guess).get("player");
			System.out.println("\t'player': {");
			System.out.println("\t\t'guess': '" + playerData.get("guess") + "',");
			System.out.println("\t\t'skill': '" + playerData.get("skill") + "',");
			System.out.println("\t\tentropy': '" + playerData.get("entropy") + "',");
			System.out.println("\t\t'solutions_after_guess': '" + playerData.get("solutions_after_guess") + "'");
			System.out.println("\t\t},");
			
			for (int i = 1; i < botGuessEvaluations.get(guess).size(); i++) {
				try {
					HashMap<String, String> optimalData = botGuessEvaluations.get(guess).get("optimal_" + (i));
					System.out.println("\toptimal_'" + i + "': {");
					System.out.println("\t\t'word': '" + optimalData.get("word") + "',");
					System.out.println("\t\t'skill': '" + optimalData.get("skill") + "',");
					System.out.println("\t\t'entropy': '" + optimalData.get("entropy") + "'");
					System.out.println("\t\t},");
				} catch(Exception e) {
					
				}
			}
			System.out.println("\t},");
		}
	}
	
	/**
	 * Returns the skill of a guess based on the entropy of that word
	 * Uses a scaling function based on the minimum and maximum entropy of all possible guesses to calculate skill
	 * @param entropy
	 * @param optimalGuesses
	 * @param guessIdx
	 * @return
	 */
	private static int getGuessSkill(float entropy, HashMap<String, Float> optimalGuesses, Integer guessIdx) {
		float minEntropy = Collections.min(optimalGuesses.values());
		float maxEntropy = Collections.max(optimalGuesses.values());
		if (guessIdx == 0) {
			return (int) ((entropy / maxEntropy) * 99);
		}
		if (minEntropy == maxEntropy ) {
			if (entropy < minEntropy) {
				return 0;
			} else {
				return 99;
			}
		}
		return Math.max((int) ((entropy-minEntropy)/(maxEntropy-minEntropy) * 99), 0);
	}
	
	/**
	 * Returns a new array of words based on the guess evaluation of the previous guess
	 * @param possSolutions
	 * @param evaluation
	 * @param guess
	 * @return
	 */
	private static HashSet<String> wordsAfterGuess(HashSet<String> possSolutions, String evaluation, String guess) {
		HashSet<String> newPossSolutions = new HashSet<String>();
		for (String word: possSolutions) {
			if (!(word.equals(guess)) && wordMatchesGuess(word, guess, evaluation)) {
				newPossSolutions.add(word);
			}
		}
		return newPossSolutions;
	}
	
	/**
	 * Returns true if a word can be a solution after a particular evaluation
	 * 
	 * If the guess is "least" with an evaluation of "caaaa" (correct, absent x4)
	 * we now know the word "stale" cannot possibly be a solution because the correct letter for the first letter is "l"
	 * We also know that "e", "a", "s", and "t" are absent in the solution
	 * 
	 * This also handles tricky words with double/triple letters
	 * @param word
	 * @param guess
	 * @param evaluation
	 * @return
	 */
	private static boolean wordMatchesGuess(String word, String guess, String evaluation) {
		HashMap<Integer, String> correct = new HashMap<Integer, String>();
		HashMap<String, Integer> present = new HashMap<String, Integer>();
		HashSet<String> absent = new HashSet<String>();
		
		for (int i = 0; i < guess.length(); i++) {
			String letterEvaluation = String.valueOf(evaluation.charAt(i));
			String letter = String.valueOf(guess.charAt(i));
			if (letterEvaluation.equals(CORRECT)) {
				correct.put(i, letter);
			} else if (letterEvaluation.equals(PRESENT)) {
				if (present.containsKey(letter)) {
					present.put(letter, present.get(letter) + 1);
				} else {
					present.put(letter, 1);
				}
			} else if (letterEvaluation.equals(ABSENT)) {
				absent.add(letter);
			}
		}
		
		for (int i = 0; i < guess.length(); i++) {
			String wordLetter = String.valueOf(word.charAt(i));
			String guessLetter = String.valueOf(guess.charAt(i));
			if ((present.containsKey(guessLetter) || absent.contains(wordLetter)) && guessLetter.equals(wordLetter) && !(correct.containsKey(i))) {
				return false;
			} 
			
		}
		
		for (int idx: correct.keySet()) {
			if (!(String.valueOf(guess.charAt(idx)).equals(String.valueOf(word.charAt(idx))))) {
				return false;
			}
		}
		
		for (String letter: present.keySet()) {
			int count = present.get(letter);
			if (!(wordHasNPresent(word, letter, count, correct))) {
				return false;
			}
		}

		for (String letter: absent) {
			if (word.contains(letter) && (!(correct.containsValue(letter) || present.containsKey(letter)))) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns True if a wrod has N number of letters present in the evaluation
	 * @param word
	 * @param letter
	 * @param count
	 * @param correct
	 * @return
	 */
	private static boolean wordHasNPresent(String word, String letter, int count, HashMap<Integer, String> correct) {
		int found = 0;
		for (int i = 0; i < word.length(); i++) {
			String wordLetter = String.valueOf(word.charAt(i));
			if (wordLetter.equals(letter)) {
				if (correct.containsKey(i) && !(correct.get(i).equals(wordLetter))) {
					found += 1;
				} else if (!(correct.containsKey(i))) {
					found += 1;
				}
			}
		}
		return found >= count;
	}
	
	/**
	 * Hardcoded optimal first guesses due to the fact that it takes too long to run the algorithm for the first time
	 * These are based on the running the bot once and copying them from the ouput
	 * @return
	 */
	private static HashMap<String, Float> firstGuessOptimal() {
		// takes too long to calculate the optimal word for the first guess
		HashMap<String, Float> optimal = new HashMap<String, Float>();
		optimal.put("raise", 5.8f);
		optimal.put("slate", 5.8f);
		optimal.put("crate", 5.8f);
		optimal.put("irate", 5.8f);
		optimal.put("trace", 5.8f);
		optimal.put("arise", 5.8f);
		optimal.put("stare", 5.8f);
		optimal.put("snare", 5.7f);
		optimal.put("arose", 5.7f);
		optimal.put("least", 5.7f);
		return optimal;
	}
	
	/**
	 * Finds the optimal guess based on the current set of possible solutions
	 * @param possSolutions
	 * @return
	 */
	private static String findOptimalGuess(HashSet<String> possSolutions) {
		String guess = "";
		HashMap<String, Float> optimalGuesses = findOptimalGuesses(possSolutions);
		float maxEntropy = Collections.max(optimalGuesses.values());
		for (String possGuess: optimalGuesses.keySet()) {
			if (optimalGuesses.get(possGuess) == maxEntropy) {
				guess = possGuess;
			}
		}
		return guess;
	}
	
	/**
	 * Finds the N best guesses based on the current set of possible solutions
	 * @param possSolutions
	 * @return
	 */
	private static HashMap<String, Float> findOptimalGuesses(HashSet<String> possSolutions) {
		HashMap<String, Float> wordEntropy = new HashMap<String, Float>();
		
		for (String possSolution: possSolutions) {
			float entropy = evaluateWordEntropy(possSolution, possSolutions);
			wordEntropy.put(possSolution, entropy);
		}
		return sortAndTrimHashMap(wordEntropy, wordEntropy.size());
	}
	
	/**
	 * Sorts a HashMap based on the value and trims it such that it only has <trimAmount> keys
	 * @param map
	 * @param trimAmount
	 * @return
	 */
	private static HashMap<String, Float> sortAndTrimHashMap(HashMap<String, Float> map, int trimAmount) {
        List<Map.Entry<String, Float> > list =
               new LinkedList<Map.Entry<String, Float> >(map.entrySet());
 
        Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {
            public int compare(Map.Entry<String, Float> o1,
                               Map.Entry<String, Float> o2)
            {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
         
        HashMap<String, Float> newMap = new LinkedHashMap<String, Float>();
        int counter = 0;
        for (Map.Entry<String, Float> aa : list) {
        	if (counter == trimAmount) {
        		break;
        	}
        	newMap.put(aa.getKey(), aa.getValue());
            counter++;
        }
        return newMap;
	}
	
	/**
	 * Evaluates the entropy for a given word
	 * @param word
	 * @param words
	 * @return
	 */
	private static float evaluateWordEntropy(String word, HashSet<String> words) {
		HashMap<String, HashSet<String>> entropyMap = new HashMap<String, HashSet<String>>();
		for (String possAnswer: words) {
			String evaluation = readArrayRow(guessEvaluator.evaluateGuess(word, possAnswer));
			HashSet<String> evaluationWords;
			if (entropyMap.containsKey(evaluation)) {
				evaluationWords  = entropyMap.get(evaluation);
			} else {
				evaluationWords = new HashSet<String>();
			}
			evaluationWords.add(possAnswer);
			entropyMap.put(evaluation, evaluationWords);
		}
		return getEntropy(entropyMap, words);
	}
	
	/**
	 *  Calculates the entropy based on the way the remaining solutions are divided up by evaluations for the given word
	 *  The intuitive way to think of entropy is that if a word will cut the number of possible solutions in half on average, the entropy will be 1. 
	 *  If it cuts the number of solutuons down to 1/4 on average, the entropy is 2.
	 *  
	 *  We are looking for the word that divides the possible solutions as much as possible.
	 * @param entropyMap
	 * @param possAnswers
	 * @return
	 */
	private static float getEntropy(HashMap<String, HashSet<String>> entropyMap, HashSet<String> possAnswers) {
		float entropy = 0;
		for (String guess: entropyMap.keySet()) {
			float numWords = entropyMap.get(guess).size();
			float p = numWords / possAnswers.size();
			entropy += p * (Math.log(1/p) / Math.log(2)); // p * log2(1/p)
		}
		return entropy;
	}
	
	/**
	 * Converts a String array into a String
	 * @param row
	 * @return
	 */
	private static String readArrayRow(String[] row) {
		String word = "";
		for (String c: row) {
			word += c;
		}
		return word;
	}
}
