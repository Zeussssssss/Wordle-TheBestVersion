
/**
 * Name: Alankrit Jacinth Moses
 * FileName: UI.java
 * Description: This is View class from the 
 * 				MVC architecture of the client side
 */
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.border.BevelBorder;

public class UI {
	private String mode;
	private Controller controller;
	private JFrame frame, mainFrame;
	private boolean freeze, gameOver;
	private JLabel[][] grid;
	private JPanel panel, pseudoPanel;
	private JLabel state, box, swtch;
	private final String WORDLE = "WORDLE";
	private final String BACKSPACE = "BACKSPACE";
	private final String ENTER = "ENTER";
	private final int GAME_FONTSIZE = 35;
	private Font globalFont;
	private final Font displayFont = new Font("Helvetica", Font.BOLD, GAME_FONTSIZE);
	private int xPadding, yPadding, xBoxDist, yBoxDist, labelSize, height, width, buttonPaddingX, buttonPaddingY;
	private final Color BLACK = new Color(40, 40, 40);
	private final Color WHITE = new Color(220, 220, 215);
	private final Color GREEN = new Color(16, 176, 52);
	private final Color YELLOW = new Color(204, 172, 8);
	private final Color DARK_GRAY = Color.DARK_GRAY;
	private final Color LIGHT_GRAY = Color.LIGHT_GRAY;
	private Color labelBack, labelFore, panelCol;
	private final Map<String, Color> colorEvaluationMap = Map.of("c", GREEN, "p", YELLOW, "a", DARK_GRAY);
	private Map<JLabel, Color> keys;
	private char keyboard[][] = { { 'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P' },
			{ 'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L' }, { 'Z', 'X', 'C', 'V', 'B', 'N', 'M' } };

	/**
	 * Constructor to create the View object with the mode light or dark
	 * @param mode
	 */
	public UI(String mode) {
		playMusic("start.wav");
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.height = (int) (screenSize.getHeight() * 0.9);
		this.width = (int) (screenSize.getWidth() * 0.8);
		labelSize = (int) ((height / 10) - 15);
		xPadding = (width - (labelSize * 5 + 40)) / 2;
		yPadding = height / 10;
		xBoxDist = yBoxDist = labelSize + 7;
		globalFont = new Font("Sans-serif", Font.BOLD, (int) (labelSize * 0.7));
		keys = new HashMap<JLabel, Color>();
		this.mode = mode;
		if (mode.equals("light")) {
			labelBack = WHITE;
			labelFore = BLACK;
		} else {
			labelBack = BLACK;
			labelFore = WHITE;
		}
		freeze = false;
		gameOver = false;
		grid = new JLabel[6][5];
	}

	/**
	 * Method for refreshing and displaying the board/grid on the screen
	 */
	public void displayBoard() {
		// loop to build the board
		Graphics graphics = panel.getGraphics();
		graphics.setColor(labelFore);
		for (int i = 0; i < controller.getGame().getMaxGuesses(); i++) {
			for (int j = 0; j < controller.getGame().getMaxGuessSize(); j++) {
				String letter = controller.getGame().getGuesses()[i][j];
				String evaluation = controller.getGame().getGuessEvaluations()[i][j];
				JLabel label = grid[i][j];
				letter = letter != null ? letter.toUpperCase() : " ";
				label.setText(letter);
				if (evaluation != null) {
//					System.out.println("EVALUTAION: "+evaluation);
					label.setBackground(colorEvaluationMap.get(evaluation));
				} else {
					label.setBackground(labelBack);
				}
				label.setOpaque(true);
				label.setForeground(labelFore);
				state.setForeground(labelFore);
				panel.revalidate();
				panel.repaint();
			}
		}
	}

	/**
	 * Method to start the UI and add GUI elements to it
	 * @param server: The server address to be connected to
	 */
	public void start(String server) {

		// Creating main frame
		freeze = false;
		gameOver = false;
		mainFrame = new JFrame(WORDLE);
		mainFrame.setLayout(null);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		mainFrame.setBounds((int) ((screenSize.getWidth() - width) / 2), (int) (screenSize.getHeight() * 0.1 / 2) - 10,
				width, height);
		mainFrame.setVisible(true);

		// Creating panel to add all labels and graphic elements
		panel = new JPanel();
		pseudoPanel = new JPanel();
		mainFrame.getContentPane().add(pseudoPanel);
		mainFrame.getContentPane().add(panel);
		panel.setLayout(null);
		panel.setBounds(0, 0, width, height);
		pseudoPanel.setBounds(0, 0, width, height);
		pseudoPanel.setOpaque(false);
		panel.setVisible(true);
		// Creating Label for game state
		state = new JLabel("WORDLE", SwingConstants.CENTER);
		state.setFont(globalFont);
		state.setBounds(0, 0, width, (int) (height * 0.1));
		state.setForeground(labelFore);
		panel.add(state);
		if (server != "") {
			new PopUp("Server copied to clipboard", panel);
		}
		panel.setBackground(labelBack);
		// Adding Toggle
		box = new JLabel("LIGHT    DARK", SwingConstants.CENTER);
		box.setFont(new Font("Arial", Font.BOLD, 10));
		box.setBounds(15, (int) (height * 0.02), 100, (int) (height * 0.06));
		box.setBorder(BorderFactory.createLineBorder(BLACK, 3));

		// Adding the swtich to toggle light/dark mode
		swtch = new JLabel("SWITCH");
		swtch.setFont(new Font("Arial", Font.BOLD, 8));
		if (mode.equals("light")) {
			box.setBorder(BorderFactory.createLineBorder(BLACK, 5));
			swtch.setBounds(25, (int) (height * 0.035), 40, (int) (height * 0.04));
			box.setForeground(BLACK);
			swtch.setForeground(BLACK);
		} else {
			box.setBorder(BorderFactory.createLineBorder(WHITE, 5));
			swtch.setBounds(65, (int) (height * 0.035), 40, (int) (height * 0.03));
			box.setForeground(WHITE);
			swtch.setForeground(WHITE);
		}
		swtch.setOpaque(true);
		swtch.setBackground(Color.red);
		swtch.setBorder(BorderFactory.createLineBorder(Color.red, 5));
		panel.add(swtch);
		panel.add(box);

		//Adding mouseListeners
		swtch.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!freeze)
					animateToggle();
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
		});

		// Initializing labels/grid boxes
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 5; j++) {
				JLabel label = new JLabel("", SwingConstants.CENTER);
				label.setFont(globalFont);
				label.setBounds(xPadding + (xBoxDist * j), yPadding + (yBoxDist * i), labelSize, labelSize);
				label.setBorder(BorderFactory.createLineBorder(labelFore, 3, true));
				grid[i][j] = label;
				panel.add(label);
			}
		}
		int offset = (int) (height * 0.65);
		int keySize = (int) (labelSize * 0.9);
		int offsetX = (int) (width * 0.15);
		int labelX = (int) (width * 0.06);
		
		// Adding keyboard to the frame
		for (int x = 0; x < 3; x++) {
			offsetX = (width - ((int) (keyboard[x].length * (labelX + 10) - 10))) / 2;
			for (int y = 0; y < keyboard[x].length; y++) {
				JLabel key = new JLabel(keyboard[x][y] + "", SwingConstants.CENTER);
				key.setFont(new Font("Arial", Font.BOLD, (int) (keySize * 0.4)));
				key.setBounds(offsetX, offset, labelX, labelSize);
				key.setBackground(LIGHT_GRAY);
				key.setOpaque(true);
				key.setForeground(BLACK);
				panel.add(key);
				keys.put(key, LIGHT_GRAY);
				offsetX += (labelX + 10);
				key.addMouseListener(new MouseListener() {

					@Override
					public void mouseClicked(MouseEvent e) {
						if (!freeze && !gameOver) {
							JLabel pointed = (JLabel) e.getComponent();
							boolean animation = controller
									.update(Character.toLowerCase(pointed.getText().charAt(0)) + "", true);
							if (!animation)
								displayBoard();
						}

					}

					@Override
					public void mousePressed(MouseEvent e) {
						JLabel pointed = (JLabel) e.getComponent();
						pointed.setBackground(new Color(10, 10, 10));
					}

					@Override
					public void mouseReleased(MouseEvent e) {
						JLabel pointed = (JLabel) e.getComponent();
						pointed.setBackground(keys.get(pointed));
					}

					@Override
					public void mouseEntered(MouseEvent e) {
						JLabel pointed = (JLabel) e.getComponent();
						pointed.setBackground(DARK_GRAY);
					}

					@Override
					public void mouseExited(MouseEvent e) {
						JLabel pointed = (JLabel) e.getComponent();
						pointed.setBackground(keys.get(pointed));
					}

				});
			}
			offset += (keySize + 15);
		}
		displayBoard();

		// Adding the KeyListener to frame
		mainFrame.addKeyListener(new KeyListener() {
			boolean animation = false;

			@Override
			public void keyTyped(KeyEvent e) {
				if (!freeze && !gameOver) {
					if (Character.isLetter(e.getKeyChar())) {
						animation = controller.update(String.valueOf(e.getKeyChar()), true);
					}
					if (!animation)
						displayBoard();
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (!freeze && !gameOver) {
					if (e.getKeyCode() == 8) {
						animation = controller.update(BACKSPACE, true);
					} else if (e.getKeyCode() == 10) {
						animation = controller.update(ENTER, true);
					}
					if (!animation)
						displayBoard();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
		});
	}
	
	/**
	 * Method to start the toggle animation of the switch
	 */
	public void animateToggle() {
		freeze = true;
		this.toggleMode();
		Timer time = new Timer();
		for (int x = 0; x < 5; x++) {
			long time1 = System.currentTimeMillis();
			TimerTask Anim = new Toggle(time);
			time.schedule(Anim, 0, 2);
		}
	}

	/**
	 * Update the controller inside the view object
	 * @param c
	 */
	public void addController(Controller c) {
		controller = c;
	}

	/**
	 * Method to throw warning messages as PopUps
	 * @param msg
	 */
	public void throwWarning(String msg) {
		// state.setText(msg);
		new PopUp(msg, pseudoPanel);
	}

	/**
	 * Method to initiate the animation of Row when a correct is entered
	 * @param rowIdx
	 * @param type
	 */
	public void animateRow(int rowIdx, String type) {

		freeze = true;
		Timer time = new Timer();
		if (type.equals("right")) {
			int delay = 500;
			for (int x = 0; x < 5; x++) {
				TimerTask Anim = new Animate(rowIdx, x, time);
				time.schedule(Anim, delay * x, 10);
			}
		} else {
			for (int x = 0; x < 5; x++) {
				TimerTask Anim = new Shake(rowIdx, x, time);
				time.schedule(Anim, 0, 10);
			}
		}
	}

	/**
	 * Update variables, display stats and freeze game when the game has ended
	 */
	public void endGame() {
		gameOver = true;
		state.setText("G A M E   O V E R !");
		try {
			controller.saveGame();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Method to play Music present in the Resources folder
	 * @param path
	 */
	public void playMusic(String path) {
		path = "Resources/" + path;
		try {
			File music = new File(path);
			AudioInputStream audio = AudioSystem.getAudioInputStream(music);
			Clip clip = AudioSystem.getClip();
			clip.open(audio);
			clip.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to close the game and stats window.
	 */
	public void disposeFrames() {
		System.out.println("!!!!!!!Trying to CLOSE FRAMES!!!!!!!!");
		mainFrame.dispose();
		frame.dispose();
	}

	/**
	 * Method to toggle the change the color of the UI elements to toggle the light/dark mode.
	 */
	public void toggleMode() {
		playMusic("switch.wav");
		if (mode.equals("light")) {
			panel.setBackground(BLACK);
			labelFore = WHITE;
			labelBack = BLACK;
			box.setBorder(BorderFactory.createLineBorder(WHITE, 3));
			box.setForeground(WHITE);
			swtch.setForeground(WHITE);
			for (int i = 0; i < 6; i++)
				for (int j = 0; j < 5; j++) {
					if (grid[i][j].getBorder() != null)
						grid[i][j].setBorder(BorderFactory.createLineBorder(WHITE, 3));
				}
		} else {
			panel.setBackground(WHITE);
			labelFore = BLACK;
			labelBack = WHITE;
			box.setBorder(BorderFactory.createLineBorder(BLACK, 3));
			box.setForeground(BLACK);
			swtch.setForeground(BLACK);
			for (int i = 0; i < 6; i++)
				for (int j = 0; j < 5; j++) {
					if (grid[i][j].getBorder() != null)
						grid[i][j].setBorder(BorderFactory.createLineBorder(BLACK, 3));
				}
		}
		displayBoard();
	}

	/**
	 * Class for animating the Toggle switch
	 * @author Alankrit Moses
	 *
	 */
	class Toggle extends TimerTask {
		int rate;
		int x1;
		int y1 = 0;
		int y2 = 0;
		Timer timer;

		public Toggle(Timer timer) {
			rate = 7;
			x1 = 25;
			if (mode.equals("dark"))
				x1 = 65;
			this.timer = timer;
		}

		@Override
		public void run() {
			y2 += 1;
			if (y2 == (8 - rate)) {
				y2 = 0;
				y1 += 1;
				if (mode.equals("dark"))
					x1 -= 1;
				else
					x1 += 1;
			}
			if (y1 == rate) {
				rate -= 1;
				y1 = 0;
			}
			if (x1 >= 65 && mode.equals("light")) {
				mode = "dark";
				freeze = false;
				timer.cancel();
			} else if (x1 <= 25 && mode.equals("dark")) {
				mode = "light";
				freeze = false;
				timer.cancel();
			}
			swtch.setBounds(x1, (int) (height * 0.035), 40, (int) (height * 0.03));
		}
	}

	/**
	 * Method to display Stats in a new Frame
	 */
	public void displayStats() {
		// Array format
		// Played,Won,CurrStreak,HighestStreal,1guess,2guess,3guess,4guess,5guess,6guess
		int arr[] = controller.load();
		// Alter for change in Statistics
		if (controller.won())
			playMusic("Win.wav");
		else
			playMusic("Lose.wav");

		int x = (int) (0.8 * height);
		frame = new JFrame("STATS");
		frame.setBounds(500, 100, x, x+10);
		JPanel jp = new JPanel();
		frame.getContentPane().add(jp);
		frame.setLayout(null);
		jp.setLayout(null);
		jp.setBounds(0, 0, x, x+10);
		jp.setBackground(labelBack);

		JLabel status = new JLabel(controller.won() ? "YOU WON" : "YOU LOST", SwingConstants.CENTER);
		status.setFont(displayFont);
		status.setBackground(labelBack);
		status.setForeground(labelFore);
		status.setBounds(0, 2, x, x / 10);
		jp.add(status);

		JLabel gd = new JLabel("GUESS DISTRIBUTION", SwingConstants.LEFT);
		gd.setFont(new Font("Helvetic", Font.BOLD, 17));
		gd.setBackground(labelBack);
		gd.setForeground(labelFore);
		gd.setBounds(x / 10, (x / 15) + (10 + x / 15) - 30, x / 2, 25);
		jp.add(gd);

		JLabel word = new JLabel("WORD WAS: " + controller.getGame().getAnswer(), SwingConstants.RIGHT);
		word.setFont(new Font("Helvetic", Font.BOLD, 17));
		word.setBackground(labelBack);
		word.setForeground(labelFore);
		word.setBounds(x / 10, (x / 15) + (10 + x / 15) - 30, x - (2 * (x / 9)), 25);
		jp.add(word);

		int barWidth = (int) (x / 1.425);

		String ar[] = { "GAMES PLAYED", "GAMES WON", "CURRENT STREAK", "HIGHEST STREAK" };
		int leftPad = x / 10;
		int midPad = (int) ((barWidth + (x / 15) + 10 - (4 * (x / 15))) / 2.28);
		for (int x1 = 0; x1 < 4; x1++) {
			JLabel quant = new JLabel(arr[x1] + "", SwingConstants.CENTER);
			quant.setFont(new Font("Arial", Font.BOLD, (int) (0.6 * x / 10)));
			quant.setBounds(leftPad + (midPad * x1), (x / 15 + x / 15 + 20) + (10 + x / 15) * 6, x / 10, x / 10);
			quant.setForeground(labelFore);
			quant.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
			jp.add(quant);

			JLabel name = new JLabel(ar[x1] + "", SwingConstants.CENTER);
			name.setFont(new Font("Arial", Font.BOLD, (int) (0.15 * x / 10)));
			name.setBounds((int) (leftPad - 12 + ((midPad) * x1)), (x / 10 + x / 15 + x / 15) + (10 + x / 15) * 6,
					x / 10 + 24, x / 10);
			name.setForeground(labelFore);
			jp.add(name);
		}
		frame.setVisible(true);
		int currWait = 0;
		for (int y = 1; y <= 6; y++) {
			System.out.println("Entered " + y + " time");
			JLabel label = new JLabel(y + "", SwingConstants.CENTER);
			label.setForeground(labelBack);
			label.setBackground(labelFore);
			label.setFont(new Font("Arial", Font.BOLD, (int) (0.7 * x / 15)));
			label.setBounds(x / 10, (x / 15) + (10 + x / 15) * y, x / 15, x / 15);
			label.setOpaque(true);
			jp.add(label);

			int width = (int) (((double) arr[3 + y] / arr[0]) * barWidth);
			int percentage = (int) (((double) arr[3 + y] / arr[0]) * 100);

			JLabel percent = new JLabel(percentage + "%", SwingConstants.CENTER);
			percent.setBounds((x / 10 + x / 15 + 10), (x / 15) + (10 + x / 15) * y, barWidth, x / 15);
			jp.add(percent);
			percent.setVisible(true);
			JLabel greenBar = new JLabel("");
			greenBar.setBounds((x / 10 + x / 15 + 10), (x / 15) + (10 + x / 15) * y, 0, x / 15);
			greenBar.setBackground(GREEN.brighter());
			greenBar.setOpaque(true);
			jp.add(greenBar);

			JLabel grayBar = new JLabel();
			grayBar.setBounds((x / 10 + x / 15 + 10), (x / 15) + (10 + x / 15) * y, barWidth, x / 15);
			grayBar.setBackground(Color.lightGray.brighter());
			grayBar.setOpaque(true);
			jp.add(grayBar);

			Timer t = new Timer();
			long time1 = System.currentTimeMillis();
			TimerTask Anim = new BarAnim(greenBar, width, t);
			t.schedule(Anim, 500 + (int) (1 * currWait), 10);
			currWait += (width * 11);
		}

		// Adding the last four buttons at the bottom of the Stats display page
		JLabel restart = new JLabel("RESTART", SwingConstants.CENTER);
		restart.setFont(new Font("Arial", Font.BOLD, (int) (0.3 * x / 10)));
		restart.setBounds(x / 4, x - x / 5, x / 4 - 5, x / 16);
		restart.setForeground(labelFore);
		restart.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		restart.setBackground(new Color(170, 0, 0));
		restart.setOpaque(true);
		jp.add(restart);
		JLabel wordleBot = new JLabel("WORDLE BOT", SwingConstants.CENTER);
		wordleBot.setFont(new Font("Arial", Font.BOLD, (int) (0.3 * x / 10)));
		wordleBot.setBounds(x / 2 + 5, x - x / 5, x / 4 - 5, x / 16);
		wordleBot.setForeground(labelFore);
		wordleBot.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		wordleBot.setBackground(GREEN.darker());
		wordleBot.setOpaque(true);
		jp.add(wordleBot);
		JLabel share = new JLabel("SHARE", SwingConstants.CENTER);
		share.setFont(new Font("Arial", Font.BOLD, (int) (0.3 * x / 10)));
		share.setBounds(x / 4, x - x / 5 + x / 16 + 10, x / 4 - 5, x / 16);
		share.setForeground(labelFore);
		share.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		share.setBackground(new Color(0, 170, 170));
		share.setOpaque(true);
		jp.add(share);
		JLabel leaderBoard = new JLabel("LEADERBOARD", SwingConstants.CENTER);
		leaderBoard.setFont(new Font("Arial", Font.BOLD, (int) (0.3 * x / 10)));
		leaderBoard.setBounds(x / 2 + 5, x - x / 5 + x / 16 + 10, x / 4 - 5, x / 16);
		leaderBoard.setForeground(labelFore);
		leaderBoard.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		leaderBoard.setBackground(new Color(170, 170, 0));
		leaderBoard.setOpaque(true);
		jp.add(leaderBoard);

		jp.repaint();

		// Adding MouseListeners to last four buttons
		wordleBot.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				BotDisplay bd = new BotDisplay(height, controller, labelFore, labelBack);
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
		});
		restart.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				controller.restart(true, "");
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
		});
		share.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				controller.clipboard();
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
		});
		leaderBoard.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String arr[] = controller.getLeaderBoard();
				new LeaderBoard(arr, labelFore, labelBack, mode);
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
		});
	}

	/**
	 * Class to control the animation of the guess distribution bars
	 * @author Alankrit Moses
	 *
	 */
	class BarAnim extends TimerTask {
		Timer timer;
		int x;
		JLabel label;

		public BarAnim(JLabel label, int x, Timer timer) {
			this.label = label;
			this.x = x;
			this.timer = timer;
		}

		public void run() {
			Dimension dim = label.getSize();
			if (dim.width < x)
				label.setBounds(label.getX(), label.getY(), dim.width + 1, dim.height);
			else
				timer.cancel();
		}
	}

	/**
	 * Class to control animation of the valid word entry row animation
	 * @author Alankrit Moses
	 *
	 */
	class Animate extends TimerTask {
		int rowIdx;
		int size;
		int time = 0;
		int x;
		boolean increasing = false;
		boolean colorChanged = false;
		Timer timer;

		public Animate(int rowIdx, int x, Timer timer) {
			this.rowIdx = rowIdx;
			size = labelSize;
			this.x = x;
			this.timer = timer;
			state.setText("WORDLE");
		}

		public void run() {
			if (size > 0 && !increasing) {
				size -= 4;
				grid[rowIdx][x].setBounds(xPadding + (xBoxDist * x) + (labelSize - size) / 2,
						yPadding + (yBoxDist * rowIdx) + (labelSize - size) / 2, size, size);
			} else {
				increasing = true;
				if (!colorChanged) {
					String evaluation = controller.getGame().getGuessEvaluations()[rowIdx][x];
					grid[rowIdx][x].setBackground(colorEvaluationMap.get(evaluation));
					for (JLabel k : keys.keySet())
						if (k.getText().equals(grid[rowIdx][x].getText())) {
							keys.put(k, colorEvaluationMap.get(evaluation));
							k.setBackground(colorEvaluationMap.get(evaluation));
						}
					grid[rowIdx][x].setBorder(null);
					colorChanged = true;
					playMusic("increase.wav");
				}
				if (size < labelSize) {
					size += 4;
					grid[rowIdx][x].setBounds(xPadding + (xBoxDist * x) + (labelSize - size) / 2,
							yPadding + (yBoxDist * rowIdx) + (labelSize - size) / 2, size, size);
				} else if (x == 4) {
					freeze = false;
					timer.cancel();
					if (gameOver)
						displayStats();
				}
			}
		}
	}

	/**
	 * Class to control the animation when invalid word is entered
	 * @author Alankrit Moses
	 *
	 */
	class Shake extends TimerTask {
		int rowIdx;
		int size;
		int time = 0;
		int x, initialX, changeable, change;
		boolean completed = false;
		Timer timer;

		public Shake(int rowIdx, int x, Timer timer) {
			playMusic("wrong.wav");
			this.rowIdx = rowIdx;
			size = labelSize;
			this.x = x;
			initialX = changeable = xPadding + (xBoxDist * x);
			this.timer = timer;
			change = 0;
		}

		public void run() {
			if (change == 5) {
				changeable -= 1;
				if (changeable == initialX)
					change += 1;
			} else if (change % 2 == 0) {
				changeable += 1;
				if (changeable == initialX + 3)
					change += 1;
			} else {
				changeable -= 2;
				if (changeable == initialX - 3)
					change += 1;
			}
			grid[rowIdx][x].setBounds(changeable, yPadding + (yBoxDist) * rowIdx, labelSize, labelSize);
			if (change == 6) {
				timer.cancel();
				freeze = false;
			}
		}
	}
}