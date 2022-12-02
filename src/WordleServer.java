import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Server class
 * 
 * @author Aditya
 *
 */
public class WordleServer implements Runnable {
	private static Game game = new Game();
	private ServerSocket listener;
	private static ExecutorService pool;

	public WordleServer() throws Exception {
		listener = new ServerSocket(59896);
		pool = Executors.newFixedThreadPool(2);
		System.out.println(InetAddress.getLocalHost());
		System.out.println("The Wordle server is running...");

	}

	public void run() {
		try {
			while (true) {
				pool.execute(new ClientManager(listener.accept()));
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public String getAddress() throws UnknownHostException {
		return InetAddress.getLocalHost().toString().split("/")[1];
	}

	/**
	 * Manages every client that joins the server
	 * 
	 * @author Aditya
	 *
	 */
	static class ClientManager implements Runnable {

		private Player you;
		private Socket socket;

		public ClientManager(Socket socket) throws Exception {
			this.socket = socket;
			you = new Player(socket);
			if (game.getCurrentPlayer() == null) {
				game.setPlayerOne(you);
				you.getOutput().println("<< YOUR TURN STARTS");
			} else {
				game.setPlayerTwo(you);
				you.setTeamMate(game.getPlayerOne());
				game.getPlayerOne().setTeamMate(you);
				you.getOutput().println("<< WORD: " + game.getAnswer());
			}
		}

		@Override
		public void run() {
			try {
				System.out.println("Connected: " + socket);
				processCommands();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
				}
				System.out.println("Closed: " + socket);
			}
		}

		private synchronized void processCommands() {
			while (you.getInput().hasNextLine()) {
				if (you != game.getCurrentPlayer()) {
					// technically this piece of code should never be run
					// if a client receives this message, then I have messed up
					you.getOutput().println("<< REJECTED");
					continue;
				}

				var command = you.getInput().nextLine().replace("\n", "");
				if (command.startsWith(">> ADD LETTER")) {
					String letter = command.split(" ")[3];
					if (you.getTeamMate() != null)
						you.getTeamMate().getOutput().println("<< ADD LETTER " + letter);
				} else if (command.startsWith(">> SUBMIT")) { // Enter pressed
					if (you.getTeamMate() != null)
						you.getTeamMate().getOutput().println("<< SUBMIT" + command.split(" ")[2]);
					if (command.split(" ")[2].equals("CORRECT")) {
						switchCurrentPlayer();
						you.getOutput().println("<< YOUR TURN ENDS");
					}
					if (you.getTeamMate() != null)
						you.getTeamMate().getOutput().println("<< YOUR TURN STARTS");
				} else if (command.equals(">> DELETE")) {
					if (you.getTeamMate() != null)
						you.getTeamMate().getOutput().println("<< DELETE");
				} else if (command.equals(">> RESTART")) {
					if (you.getTeamMate() != null)
						you.getTeamMate().getOutput().println("<< RESTART");
				} else if (command.startsWith(">> WORD:")) {
					if (you.getTeamMate() != null)
						you.getTeamMate().getOutput().println("<< WORD: " + command.split(" ")[2]);
					game.setAnswer(command.split(" ")[2]);
				} else if (command.equals(">> QUIT")) {
					return;
				}
			}
		}

		private void switchCurrentPlayer() {
			if (game.getCurrentPlayer() == game.getPlayerOne())
				game.setCurrentPlayer(game.getPlayerTwo());
			else
				game.setCurrentPlayer(game.getPlayerOne());
		}

	}
}
