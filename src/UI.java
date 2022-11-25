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
import java.util.Timer;
import java.util.*;
import javax.swing.*;
public class UI {
	
	private static String mode;
	private static Controller controller;
	private static JFrame frame;
	private static boolean isGameOver;
	private static boolean freeze, gameOver;
	private static JLabel[][] grid;
	private static JPanel panel;
	private static JLabel state, box, swtch;
	private static final String WORDLE = "WORDLE";
	private static final String BACKSPACE = "BACKSPACE";
	private static final String ENTER = "ENTER";
	private static final int GAME_FONTSIZE = 35;
	private static final Font globalFont = new Font("Cursive", Font.BOLD, 30);
	private static final Font displayFont = new Font("Arial",Font.BOLD, GAME_FONTSIZE);
	private static final int xPadding = 50;
	private static final int yPadding = 90;
	private static final int xBoxDist = 120;
	private static final int yBoxDist = 115;
	private static final Color BLACK = Color.black;
	private static final Color WHITE = Color.white;
	private static final Color GREEN = Color.green;
	private static final Color YELLOW = new Color(245, 224, 66);
	private static final Color DARK_GRAY = Color.DARK_GRAY;
	private static final Color LIGHT_GRAY = Color.LIGHT_GRAY;
	private static Color  labelBack, labelFore, panelCol;
	private int labelSize = 100;
	private static final Map<String, Color> colorEvaluationMap = Map.of(
			"c", GREEN,
			"p", YELLOW,
			"a", DARK_GRAY
	);
	
	/**
	 * Constructor to create the basic view
	 */
	public UI(String mode) 
	{ 
		this.mode = mode;
		if(mode.equals("light"))
		{
			labelBack = WHITE;
			labelFore = BLACK;
		}
		else
		{	
			labelBack = BLACK;
			labelFore = WHITE;
		}
		freeze = false;
		gameOver = false;
		grid = new JLabel[6][5];
	}
	
	private static void displayBoard() {
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
					System.out.println("EVALUTAION: "+evaluation);
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
	
	public void start() {
		
		//Creating main frame
		frame = new JFrame(WORDLE);
		frame.setLayout(null);
		frame.setBounds(0, 0, 700, 820);
		frame.setVisible(true);
		
		//Creating panel to add all labels and graphic elements
		panel = new JPanel();
		frame.getContentPane().add(panel);
		panel.setLayout(null);
		panel.setBounds(0,0,700,800);
		
		//Creating Label for game state
		state = new JLabel("W   O   R   D   L   E", SwingConstants.CENTER);
		state.setFont(globalFont);
		state.setBounds(200,10,300,50);
		state.setForeground(labelFore);
		panel.add(state);
		
		panel.setBackground(labelBack);
		//Adding Toggle
		box = new JLabel("  LIGHT    DARK", SwingConstants.LEFT);
		box.setFont(new Font("Arial",Font.BOLD,10));
		box.setBounds(15,10,100,60);
		box.setBorder(BorderFactory.createLineBorder(Color.black, 5));
		
		swtch = new JLabel("SWITCH");
		swtch.setFont(new Font("Arial",Font.BOLD,8));
		if(mode.equals("light"))
		{
			box.setBorder(BorderFactory.createLineBorder(Color.black, 5));
			swtch.setBounds(25,20,40,40);
			box.setForeground(Color.black);
			swtch.setForeground(Color.black);
		}
		else
		{
			box.setBorder(BorderFactory.createLineBorder(Color.white, 5));
			swtch.setBounds(65,20,40,40);
			box.setForeground(Color.white);
			swtch.setForeground(Color.white);
		}
		swtch.setOpaque(true);
		swtch.setBackground(Color.red);
		swtch.setBorder(BorderFactory.createLineBorder(Color.red, 5));
		panel.add(swtch);
		panel.add(box);
		
		swtch.addMouseListener(new MouseListener(){
				@Override
				public void mouseClicked(MouseEvent e) {
					if(!freeze)
						animateToggle();
				}
				
				@Override
				public void mousePressed(MouseEvent e) {}

				@Override
				public void mouseReleased(MouseEvent e) {}

				@Override
				public void mouseEntered(MouseEvent e) {}

				@Override
				public void mouseExited(MouseEvent e) {}});

		for(int i=0;i<6;i++)
		{
			for(int j=0;j<5;j++)
			{
				JLabel label = new JLabel("",SwingConstants.CENTER);
				label.setFont(globalFont);
				label.setBounds(xPadding+(xBoxDist*j),yPadding+(yBoxDist*i),100,100);
				label.setBorder(BorderFactory.createLineBorder(labelFore, 5));
				grid[i][j] = label;
				panel.add(label);
			}
		}
		displayBoard();
		
		//Adding the KeyListener to frame
		frame.addKeyListener(new KeyListener(){
				boolean animation = false;
				@Override
				public void keyTyped(KeyEvent e) {
					if(!freeze && !gameOver)
					{
						if (Character.isLetter(e.getKeyChar())) {
							animation = controller.update(String.valueOf(e.getKeyChar()));
						}
						if(!animation)
							displayBoard();
					}
				}
				@Override
				public void keyPressed(KeyEvent e)
				{
					if(!freeze && !gameOver)
					{
						System.out.println("KEYPRESSED");
						System.out.println("Pressed: "+e.getKeyCode());
						if(e.getKeyCode() == 8) {
							animation = controller.update(BACKSPACE);
						} else if(e.getKeyCode() == 10) {
							animation = controller.update(ENTER);
						}
						if(!animation)
							displayBoard();
					}
				}
				@Override
				public void keyReleased(KeyEvent e) {}
			});
	}
	
	public void animateToggle()
	{
		freeze = true;
		this.toggleMode();
		Timer time = new Timer();
		for(int x=0;x<5;x++)
		{
			long time1 = System.currentTimeMillis();
			TimerTask Anim = new Toggle(time);
			time.schedule(Anim, 0, 2);
		}
	}
	
	public void addController(Controller c) { controller = c; }
	
	public void throwWarning(String msg) {
		state.setText(msg);
	}
	
	public void animateRow(int rowIdx) {
		freeze = true;
		Timer time = new Timer();
		int delay = 500;
		for(int x=0;x<5;x++)
		{
			long time1 = System.currentTimeMillis();
			TimerTask Anim = new Animate(rowIdx,x,time);
			time.schedule(Anim, delay*x, 10);
		}
	}
	
	public void endGame() {
		gameOver = true;
		state.setText("G A M E   O V E R !");
	}
	
	public void toggleMode()
	{
		if(mode.equals("light"))
		{
			panel.setBackground(Color.black);
			labelFore = Color.white;
			labelBack = Color.black;
			box.setBorder(BorderFactory.createLineBorder(Color.white, 5));
			box.setForeground(Color.white);
			swtch.setForeground(Color.white);
			for(int i = 0;i<6;i++)
				for(int j = 0;j<5;j++)
					grid[i][j].setBorder(BorderFactory.createLineBorder(Color.white, 5));
		}
		else
		{
			panel.setBackground(Color.white);
			labelFore = Color.black;
			labelBack = Color.white;
			box.setBorder(BorderFactory.createLineBorder(Color.black, 5));
			box.setForeground(Color.black);
			swtch.setForeground(Color.black);
			for(int i = 0;i<6;i++)
				for(int j = 0;j<5;j++)
					grid[i][j].setBorder(BorderFactory.createLineBorder(Color.black, 5));
		}
		displayBoard();
	}
	
	class Toggle extends TimerTask
	{
		int rate;
		int x1;
		int y1 = 0;
		int y2 = 0;
		Timer timer;
		public Toggle(Timer timer)
		{
			rate = 7;
			x1 = 25;
			if(mode.equals("dark"))
				x1 = 65;
			this.timer = timer;
		}
		@Override
		public void run() {
			y2+=1;
			if(y2==(8-rate))
			{
				y2=0;
				y1+=1;
				if(mode.equals("dark"))
					x1-=1;
				else
					x1+=1;
			}
			if(y1==rate)
			{
				rate-=1;
				y1=0;
			}
			if(x1>=65 && mode.equals("light"))
			{
				mode = "dark";
				freeze = false;
				timer.cancel();
			}
			else if(x1<=25 && mode.equals("dark"))
			{
				mode = "light";
				freeze = false;
				timer.cancel();
			}
			swtch.setBounds(x1,20,40,40);
		}
		
	}
	
	class Animate extends TimerTask
	{
		int rowIdx;
		int size;
		int time = 0;
		int x;
		boolean increasing = false;
		boolean colorChanged = false;
		Timer timer;
		public Animate(int rowIdx, int x, Timer timer)
		{
			this.rowIdx = rowIdx;
			size = labelSize;
			this.x = x;
			this.timer = timer;
		}
		
		public void run() 
		{
			if(size>0 && !increasing)
			{
				System.out.println("Time at: "+time);
				size-=4;
				grid[rowIdx][x].setBounds(xPadding+(xBoxDist*x)+(labelSize-size)/2, yPadding+(yBoxDist*rowIdx)+(labelSize-size)/2, size, size);
				System.out.println("Size:"+size);
			}
			else
			{
				increasing = true;
				if(!colorChanged)
				{
					String evaluation = controller.getGame().getGuessEvaluations()[rowIdx][x];
					grid[rowIdx][x].setBackground(colorEvaluationMap.get(evaluation));
					colorChanged = true;
				}
				if(size<labelSize)
				{
					size+=4;
					grid[rowIdx][x].setBounds(xPadding+(xBoxDist*x)+(labelSize-size)/2, yPadding+(yBoxDist*rowIdx)+(labelSize-size)/2, size, size);
				}
				else if(x==4)
				{
					freeze = false;
					timer.cancel();
				}
			}
		}
	}
}