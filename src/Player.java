import java.io.PrintWriter;
import java.util.Scanner;

/**
 * 
 * @author Aditya
 *
 */
public class Player {
	private Player teamMate;
	private Scanner input;
	private PrintWriter output;

	public Scanner getInput() {
		return input;
	}

	public void setInput(Scanner scanner) {
		input = scanner;
	}

	public PrintWriter getOutput() {
		return output;
	}

	public void setOutput(PrintWriter printWriter) {
		output = printWriter;
	}

	public Player getTeamMate() {
		return teamMate;
	}

	public void setTeamMate(Player teamMate) {
		this.teamMate = teamMate;
	}



}
