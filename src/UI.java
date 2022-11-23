/**
 * Name: Alankrit Jacinth Moses
 * FileName: UI.java
 * Description: This is View class from the 
 * 				MVC architecture of the client side
 */
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Map;

import javax.swing.*;
public class UI {
	
	private static Controller controller;
	private static JFrame frame;
	private static boolean isGameOver;
	
	private static final String WORDLE = "WORDLE";
	private static final String BACKSPACE = "BACKSPACE";
	private static final String ENTER = "ENTER";
	private static final int GAME_FONTSIZE = 35;
	private static final Font globalFont = new Font("Cursive", Font.BOLD, 30);
	private static final Font displayFont = new Font("Arial",Font.BOLD, GAME_FONTSIZE);
	private static final Color BLACK = Color.black;
	
	/**
	 * Constructor to create the basic view
	 */
	public UI() {}
	
	private static void displayBoard(JPanel panel) {
		// loop to build the board
		Graphics graphics = panel.getGraphics();
		panel.removeAll();
		graphics.setColor(BLACK);
		for (int i = 0; i < controller.getGame().getMaxGuesses(); i++) {
			for (int j = 0; j < controller.getGame().getMaxGuessSize(); j++) {
				String letter = controller.getGame().getGuesses()[i][j];
				String evaluation = controller.getGame().getGuessEvaluations()[i][j];
				letter = letter != null ? letter.toUpperCase() : " ";
				Cell cell = new Cell(letter, evaluation, displayFont, j, i);
				panel.add(cell.getLabel());
				panel.revalidate();
				panel.repaint();
			}
		}
	}
	
	public void start() {
		
		//Creating main frame
		frame = new JFrame(WORDLE);
		frame.setLayout(null);
		frame.setBounds(0, 0, 700, 800);
		frame.setVisible(true);
		
		//Creating Label for game state
		JLabel state = new JLabel("", SwingConstants.CENTER);
		state.setFont(globalFont);
		state.setBounds(0,10,700,50);
		
		//Creating panel to add all labels and graphic elements
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel);
		panel.setLayout(null);
		panel.setBounds(0,0,700,800);
		panel.add(state);
		panel.setBackground(BLACK);
		
		displayBoard(panel);
		
		//Adding the KeyListener to frame
		frame.addKeyListener(new KeyListener(){

			@Override
			public void keyTyped(KeyEvent e) {
				if (Character.isLetter(e.getKeyChar())) {
					controller.update(String.valueOf(e.getKeyChar()));
				}
				displayBoard(panel);
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == 8) {
					controller.update(BACKSPACE);
				} else if(e.getKeyCode() == 10) {
					controller.update(ENTER);
				}
				displayBoard(panel);
			}

			@Override
			public void keyReleased(KeyEvent e) {}
			});
	}
	
	public void addController(Controller c) { controller = c; }
	
	public void throwWarning(String msg) {
		// throw a small popup box that disappears when a key is pressed
	}
	
	public void animateRow(int rowIdx) {
		// animate the tiles
	}
	
	public void endGame() {
		// display stat screen
		// display play again button -> controller.newGame(); displayBoard();
		// display view stats button -> TBD
	}
}