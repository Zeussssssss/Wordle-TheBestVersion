
/**
 * File: Controller.java
 * Assignment: CSC335PA3
 * @author Aman Dwivedi
 *
 * Description: This is the Controller class. It handles all the back end operation. 
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

public class Controller {

	private WordleGame game;
	private UI ui;
	private static String curGuess;
	private static GuessEvaluator guessEvaluator;
	private static int curGuessIndex;
	private static int charIndex;
	private boolean won = false;
	private boolean myTurn = true;

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
				myTurn = false;

			} catch (IOException e) {
				System.out.println("Error on controller side");
				e.printStackTrace();
			}
			new Thread(new ServerReader()).start();
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

	private void newGame() {
		this.start();
	}

	public boolean won() {
		return won;
	}

	public void getBotEvaluations() {

	}

	public void restart() {
		game.init();
		// guessEvaluator = new GuessEvaluator();
		init();
		// this.start();
	}
	
	public int[] load() {
		// Played,Won,CurrStreak,HighestStreal,1guess,2guess,3guess,4guess,5guess,6guess
		int[] ret = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
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
	        if(data[0].equals("true")) {
	        	won++;
	        	games.add(true);
	        }
	        else {
	        	games.add(false);
	        }
	        if(data[1].equals("1")) {
	        	ret[4]++;
	        } else if(data[1].equals("2")) {
	        	ret[5]++;
	        } else if(data[1].equals("3")) {
	        	ret[6]++;
	        } else if(data[1].equals("4")) {
	        	ret[7]++;
	        } else if(data[1].equals("5")) {
	        	ret[8]++;
	        } else if(data[1].equals("6") && data[0].equals("true")) {
	        	ret[9]++;
	        }
	      }
	      myReader.close();
	      ret[1] = won;
	      ret[0] = games.size();
	      int curStreak = 0;
	      int high = 0;
	      int i = 0;
	      for (boolean b: games) {
	    	  System.out.println(b);
	    	  if(b == false) {
	    		  if(high < i) {
	    			  high = i;
	    		  }
	    		  i = 0;
	    	  }
	    	  i++;
	      }
	      curStreak = i;
	      if(curStreak > high)
	    	  high = curStreak;
	      ret[2] = curStreak;
	      ret[3] = high;
	      return ret;
	}
	
	public void saveGame() throws IOException {

		File file = new File("data.txt");
		FileWriter fr = new FileWriter(file, true);
		fr.write(this.won + " " + (curGuessIndex + 1) + "\n");
		fr.close();
	}

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
							System.out.println("UPDATED ANSWER: " + result.split(" ")[2]);
							game.changeAnswer(result.split(" ")[2]);
						}
					} else if (result.equals("<< DELETE")) {
						update(BACKSPACE, false);
					} else if (result.equals("<< RESTART")) {
						restart();// Don't really what else to do here
					} else if (result.startsWith("<< SUBMIT")) {
						update(ENTER, false);
					} else if (result.startsWith("<< ADD LETTER ")) {
						update(result.split(" ")[3], false);
					} else {
						System.out.println(result);
					}
				}
			}
		}
	}
}