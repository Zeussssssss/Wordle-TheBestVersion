/**
 * File: Controller.java
 * Assignment: CSC335 Final PA
 * @author Aman Dwivedi
 * Description: This is the Controller class. It handles all the back end operation.
 * And connects between UI and all other stuff. 
 */
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;

public class Controller {

	private WordleGame game;
	private UI ui;
	private String name;
	private String serverAddress;
	private static String curGuess;
	private static GuessEvaluator guessEvaluator;
	private static int curGuessIndex;
	private static int charIndex;
	private boolean won = false;
	private boolean myTurn = true;
	private static MongoCollection<Document> collection;

	private final static String ENTER = "ENTER";
	private final static String BACKSPACE = "BACKSPACE";

	// NETWORKING VARIABLES
	private static boolean singlePlayer;
	private static Socket socket = null;
	private static Scanner in;
	private static PrintWriter out;

	/**
	 * Constructor
	 * @param newUI
	 * @param newGame
	 * @param single
	 * @param serverAdd
	 * @param coll
	 * @param name
	 */
	public Controller(UI newUI, WordleGame newGame, boolean single, String serverAdd, MongoCollection<Document> coll,
			String name) {
		ui = newUI;
		game = newGame;
		guessEvaluator = new GuessEvaluator();
		singlePlayer = single;
		collection = coll;
		this.name = name;
		this.serverAddress = serverAdd;
		if (!singlePlayer) {
			try {
				socket = new Socket(serverAdd, 59896);
				in = new Scanner(socket.getInputStream());
				out = new PrintWriter(socket.getOutputStream(), true);
				myTurn = false;

			} catch (IOException e) {
				e.printStackTrace();
			}
			new Thread(new ServerReader()).start();
		}
		init();
	}
	
	/**
	 * Initializes everything to zero
	 */
	private static void init() {

		curGuess = "";
		curGuessIndex = 0;
		charIndex = 0;
	}
	
	/**
	 * Starts the UI
	 */
	public void start() {
		ui.start(this.serverAddress);
	}
	
	/**
	 * Returns the game object
	 * @return
	 */
	public WordleGame getGame() {
		return game;
	}
	
	/**
	 * Takes the string from the UI and checks for the validation of the word
	 * @param c
	 * @param notifyServer
	 * @return
	 */
	public boolean update(String c, boolean notifyServer) {
		// Since only the server reader would call this method with !notifyServer, this
		// special check
		// allows the update function to be called when the the second player's client
		// receives a message
		// from the server
		if (myTurn || !notifyServer) {
			if (c.equals(ENTER)) {
				if (!(guessEvaluator.isInVocabulary(curGuess))) {
					// checks if word is in vocab
					ui.throwWarning("Word not allowed");
					ui.animateRow(curGuessIndex, "wrong");
					if (notifyServer && !singlePlayer) {
						out.println(">> SUBMIT WRONG");
					}
					return true;
				}

				if (notifyServer && !singlePlayer) {
					out.println(">> SUBMIT CORRECT");
				}
				String[] guessEvaluation = guessEvaluator.evaluateGuess(curGuess, game.getAnswer());
				// returns color eval ^^
				game.addGuessEvaluation(guessEvaluation, curGuessIndex);
				ui.animateRow(curGuessIndex, "right");
				if (game.allCorrect(guessEvaluation)) {
					this.won = true;
					WordleBot bot = new WordleBot();
					bot.evaluate(game);
					bot.play(game);
				}
				if (game.isGameOver()) {
					ui.endGame();
				}
				curGuess = "";
				curGuessIndex += 1;
				charIndex = 0;
				return true;
			} else if (c.equals(BACKSPACE)) {
				if (notifyServer && !singlePlayer) {
					out.println(">> DELETE");
				}
				if (curGuess.length() == 0) {
					ui.displayBoard();
					return false;
				}
				curGuess = curGuess.substring(0, curGuess.length() - 1); // take last char from string
				charIndex -= 1;
			} else if (!(Character.isLetter(c.charAt(0)))) {
				ui.throwWarning("Characters must be letters");
				ui.displayBoard();
				return false;
			} else {
				if (notifyServer && !singlePlayer) {
					out.println(">> ADD LETTER " + c);
				}
				if (curGuess.length() == game.getMaxGuessSize()) {
					ui.throwWarning("Already typed 5 letters");
					ui.displayBoard();
					return false;
				}
				curGuess += c;
				charIndex += 1;
			}
			game.addGuess(curGuess, curGuessIndex, charIndex);
			ui.displayBoard();
			return false;
		} else {
			ui.throwWarning("Not your turn!");
			return true;
		}
	}
	
	/**
	 * Returns the state of game
	 * @return
	 */
	public boolean won() {
		return won;
	}
	
	/**
	 * Returns the leaderboard as a string array
	 * @return
	 */
	public String[] getLeaderBoard() {
		FindIterable<Document> iterDoc = collection.find().sort(Sorts.descending("Score"));
		MongoCursor<Document> it = iterDoc.iterator();
		int i = 0;
		String arr[] = new String[10];
		while (i < 10) {
			Document curr = it.next();
			arr[i] = curr.getString("_id");
			arr[i + 1] = Integer.toString(curr.getInteger("Score"));
			i += 2;
		}
		return arr;
	}

	/**
	 * Starts a new game
	 * @param notifyServer
	 * @param word
	 */
	public void restart(boolean notifyServer, String word) {
		ui.disposeFrames();
		game.init();
		guessEvaluator = new GuessEvaluator();
		init();
		this.start();
		if (!singlePlayer && notifyServer)
			out.println(">> RESTART " + game.getAnswer());
		else if (!singlePlayer && !notifyServer) {
			System.out.println("UPDATED ANSWER: " + word);
			game.changeAnswer(word);
		}
	}

	/**
	 * Copies result to clipboard
	 */
	public void clipboard() {
		String myString = "Today's word was " + game.getAnswer();
		if (this.won) {
			myString += ". I guessed it in " + (curGuessIndex + 1) + " attempts.";
		} else {
			myString += ". I could not guess it :((";
		}
		StringSelection stringSelection = new StringSelection(myString);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);
	}

	/**
	 * Load local statistics. Returns an array of stats.
	 * @return
	 */
	public int[] load() {
		// Played,Won,CurrStreak,HighestStreak,1guess,2guess,3guess,4guess,5guess,6guess
		int[] ret = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		File file = new File("data.txt");
		Scanner myReader = null;
		try {
			myReader = new Scanner(file);
		} catch (FileNotFoundException e) {
			return null;
		}
		ArrayList<Boolean> games = new ArrayList<Boolean>();
		int won = 0;
		while (myReader.hasNextLine()) {
			String line = myReader.nextLine();
			String[] data = line.split(" ");
			if (data[0].equals("true")) {
				won++;
				games.add(true);
			} else {
				games.add(false);
			}
			if (data[1].equals("1")) {
				ret[4]++;
			} else if (data[1].equals("2")) {
				ret[5]++;
			} else if (data[1].equals("3")) {
				ret[6]++;
			} else if (data[1].equals("4")) {
				ret[7]++;
			} else if (data[1].equals("5")) {
				ret[8]++;
			} else if (data[1].equals("6") && data[0].equals("true")) {
				ret[9]++;
			}
		}
		myReader.close();
		ret[1] = won;
		ret[0] = games.size();
		int curStreak = 0;
		int high = 0;
		int i = 0;
		for (boolean b : games) {
			if (b == false) {
				if (high < i) {
					high = i;
				}
				i = 0;
			}
			i++;
		}
		curStreak = i;
		if (curStreak > high)
			high = curStreak;
		ret[2] = curStreak;
		ret[3] = high;
		return ret;
	}

	/**
	 * Adds to mongodb
	 * @param score
	 * @return
	 */
	public Document playerAsADBObject(int score) {
		return new Document("_id", name).append("Score", score);
	}

	/**
	 * Save progress locally
	 * @throws IOException
	 */
	public void saveGame() throws IOException {

		File file = new File("data.txt");
		FileWriter fr = new FileWriter(file, true);
		fr.write(this.won + " " + (curGuessIndex + 1) + "\n");
		fr.close();
		// DB WRITING
		BasicDBObject query = new BasicDBObject();
		query.put("_id", name);
		if ((collection.find(query)).iterator().hasNext()) {
			Document curr = collection.find(query).iterator().next();
			int score = curr.getInteger("Score");
			if(won)
				collection.updateOne(Filters.eq("_id", name), Updates.set("Score", score + ((6 - curGuessIndex))));
		} else {
			if(won)
				collection.insertOne(playerAsADBObject((6 - curGuessIndex)));
			else 
				collection.insertOne(playerAsADBObject(0));
		}
	}

	/**
	 * Current number of guesses.
	 * @return
	 */
	public int getCurrGuessInd() {
		return curGuessIndex;
	}

	/**
	 * This class is for handling all the server connections. 
	 *
	 */
	class ServerReader implements Runnable {

		@Override
		public void run() {
			while (true) {
				if (in.hasNextLine()) {
					var result = in.nextLine().replace("\n", "");
					if (result.equals("<< YOUR TURN STARTS")) {
						myTurn = true;
						out.println(">> WORD: " + game.getAnswer());
					} else if (result.equals("<< YOUR TURN ENDS")) {
						myTurn = false;
					} else if (result.startsWith("<< WORD:")) {
						if (!game.getAnswer().equals(result.split(" ")[2])) {
							//System.out.println("UPDATED ANSWER: " + result.split(" ")[2]);
							game.changeAnswer(result.split(" ")[2]);
						}
					} else if (result.equals("<< DELETE")) {
						update(BACKSPACE, false);
					} else if (result.startsWith("<< RESTART")) {
						restart(false, result.split(" ")[2]);
					} else if (result.startsWith("<< SUBMIT")) {
						update(ENTER, false);
					} else if (result.startsWith("<< ADD LETTER ")) {
						update(result.split(" ")[3], false);
					} else {
						//System.out.println(result);
					}
				}
			}
		}
	}
}