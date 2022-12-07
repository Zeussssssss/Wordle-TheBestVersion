/**
 * 
 * @author Aditya
 *
 */

public class Game {

	private Player currPlayer; // the player that is supposed to be playing the game
	private Player playerOne;
	private Player playerTwo;
	private String answer;

	public Game() {
		currPlayer = null;
		playerOne = null;
		playerTwo = null;

	}

	public Player getPlayerOne() {
		return playerOne;
	}

	public void setPlayerOne(Player player) {
		playerOne = player;
		currPlayer = playerOne;
	}

	public Player getPlayerTwo() {
		return playerTwo;
	}

	public void setPlayerTwo(Player playerTwo) {
		this.playerTwo = playerTwo;
	}

	public Player getCurrentPlayer() {
		return currPlayer;
	}

	public void setCurrentPlayer(Player player) {
		currPlayer = player;
	}

	public void setAnswer(String ans) {
		answer = ans;
	}

	public String getAnswer() {
		return answer;
	}

}