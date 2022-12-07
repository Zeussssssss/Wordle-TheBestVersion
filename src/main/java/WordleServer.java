import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This is the server class that is used when a client decides to play the
 * collaborative mode
 * 
 * @author Aditya Kumar
 *
 */
public class WordleServer implements Runnable {
	private static Game game = new Game();
	private ServerSocket listener;
	private static ExecutorService pool;

	/**
	 * ctor
	 * 
	 * @throws Exception
	 */
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
	 * Manages every client that joins the server, and is responsible for sending
	 * and receiving messages
	 * 
	 * @author Aditya Kumar
	 *
	 */
	static class ClientManager implements Runnable {

		private Player you;
		private Socket socket;

		/**
		 * Ctor
		 * 
		 * @param socket
		 * @throws Exception
		 */
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
				// tells the client player one's word
				you.getOutput().println("<< WORD: " + game.getAnswer());
			}
		}

		/**
		 * Run method to run instances for this class for every client
		 * 
		 */
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
					e.printStackTrace();
				}
				System.out.println("Closed: " + socket);
			}
		}

		/**
		 * Control center for the server side Reads and replies to clients message's and
		 * modifies the game state accordingly
		 */
		private synchronized void processCommands() {
			while (you.getInput().hasNextLine()) {

				var command = you.getInput().nextLine().replace("\n", "");
				if (you == game.getCurrentPlayer() || command.equals(">> RESTART")) {
					if (command.startsWith(">> ADD LETTER")) {
						String letter = command.split(" ")[3];
						if (you.getTeamMate() != null)
							you.getTeamMate().getOutput().println("<< ADD LETTER " + letter);
					}
					if (command.startsWith(">> SUBMIT")) { 
						// Enter pressed
						if (you.getTeamMate() != null)
							you.getTeamMate().getOutput().println("<< SUBMIT" + command.split(" ")[2]);
						if (command.split(" ")[2].equals("CORRECT")) {
							you.getOutput().println("<< YOUR TURN ENDS");
							you.getTeamMate().getOutput().println("<< YOUR TURN STARTS");
							switchCurrentPlayer();
						}
					} else if (command.equals(">> DELETE")) {
						if (you.getTeamMate() != null)
							you.getTeamMate().getOutput().println("<< DELETE");
					} else if (command.startsWith(">> RESTART")) {
						if (you.getTeamMate() != null)
							you.getTeamMate().getOutput().println("<< RESTART " + command.split(" ")[2]);
					} else if (command.startsWith(">> WORD:")) {
						if (you.getTeamMate() != null)
							you.getTeamMate().getOutput().println("<< WORD: " + command.split(" ")[2]);
						game.setAnswer(command.split(" ")[2]);
					} else if (command.equals(">> QUIT"))
						return;
				}
			}
		}

		/**
		 * Switch the current player from player 1 to player 2 and vice and versa
		 */
		private void switchCurrentPlayer() {
			if (game.getCurrentPlayer() == game.getPlayerOne())
				game.setCurrentPlayer(game.getPlayerTwo());
			else
				game.setCurrentPlayer(game.getPlayerOne());
		}

	}
}