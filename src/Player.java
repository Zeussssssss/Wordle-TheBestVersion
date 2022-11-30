import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
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
	
	public Player(Socket socket) throws IOException {
		this.setInput(new Scanner(socket.getInputStream()));
		this.setOutput(new PrintWriter(socket.getOutputStream(), true));
	}

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
