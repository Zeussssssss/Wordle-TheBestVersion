import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Map;

import javax.swing.*;

public class BotDisplay {
	
	int x,y;
	Controller controller;
	JPanel panel;
	WordleBot bot;
	JLabel grid[][];
	private int height,width,labelSize,xPadding,yPadding,xBoxDist,yBoxDist;
	private Font globalFont;
	private Color labelFore,labelBack;
	private final Color BLACK = new Color(40, 40, 40);
	private final Color WHITE = new Color(220, 220, 215);
	private final Color GREEN = new Color(16, 176, 52);
	private final Color YELLOW = new Color(204, 172, 8);
	private final Color DARK_GRAY = Color.DARK_GRAY;
	private final Color LIGHT_GRAY = Color.LIGHT_GRAY;
	private final Map<String, Color> colorEvaluationMap = Map.of("c", GREEN, "p", YELLOW, "a", DARK_GRAY);
	int currGuess;
	JLabel yourGuess;
	JLabel botGuesses[];
	public BotDisplay(int y, Controller controller,Color labelFore, Color labelBack)
	{
		bot = new WordleBot();
		bot.evaluate(controller.getGame());
		this.y = y;
		this.controller = controller;
		this.labelFore = labelFore;
		this.labelBack = labelBack;
		this.bot = bot;
		botGuesses = new JLabel[10];
		JFrame window = new JFrame("BOT");
		window.setLayout(null);
		window.setBounds(500,50,y,y);
		panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(0,0,y,y);
		panel.setBackground(labelBack);
		window.getContentPane().add(panel);
		addElements();
		currGuess = 1;
		display(currGuess);
		window.setVisible(true);
	}
	
	private void addElements()
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.labelSize = (int) ((y / 15) - 15);
		this.xPadding = y / 7;
		this.yPadding = y / 10;
		this.xBoxDist = yBoxDist = labelSize + 7;
		this.globalFont = new Font("Sans-serif", Font.BOLD, (int) (labelSize * 0.7));
		grid = new JLabel[6][5];
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 5; j++) {
				JLabel label = new JLabel("", SwingConstants.CENTER);
				label.setFont(globalFont);
				label.setBounds(xPadding + (xBoxDist * j), yPadding + (yBoxDist * i), labelSize, labelSize);
				label.setBorder(BorderFactory.createLineBorder(labelFore, 1));
				grid[i][j] = label;
				panel.add(label);
			}
		}
		int x1 = xPadding+xBoxDist*6;
		int xSize = y-x1-(y/7);
		int ySize = yBoxDist*5+labelSize;

		JLabel title = new JLabel("WORDLE BOT",SwingConstants.CENTER);
		title.setBounds(0,0,y,y/10);
		title.setFont(new Font("Sans-serif", Font.BOLD, y/20));
		title.setForeground(labelFore);
		panel.add(title);
		
		JLabel left = new JLabel("<",SwingConstants.CENTER);
		left.setFont(new Font("Sans-serif", Font.BOLD, (int)(0.7*y/10)));
		left.setForeground(labelFore);
		left.setBounds(0,(yPadding+ySize+10)/2-y/40,y/10,y/10);
		JLabel right= new JLabel(">",SwingConstants.CENTER);
		right.setFont(new Font("Sans-serif", Font.BOLD, (int)(0.7*y/10)));
		right.setBounds(y-y/8,(yPadding+ySize+10)/2-y/40,y/10,y/10);
		right.setForeground(labelFore);
		panel.add(left);
		panel.add(right);
		left.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent e) {
				if(currGuess>1)
					display(--currGuess);
			}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
	
		});
		right.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent e) {
				if(currGuess<controller.getCurrGuessInd())
					display(++currGuess);
			}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
		});
		yourGuess = new JLabel("",SwingConstants.CENTER);
		yourGuess.setBounds(x1,yPadding,xSize,ySize);
		yourGuess.setBackground(labelBack);
		yourGuess.setForeground(labelFore);
		yourGuess.setFont(new Font("Arial",Font.BOLD,(int)(0.07*xSize)));
		yourGuess.setBorder(BorderFactory.createLineBorder(labelFore,6));
		panel.add(yourGuess);
		
		JLabel optGuess = new JLabel("OPTIMAL GUESSES",SwingConstants.CENTER);
		optGuess.setBounds(0,yPadding+ySize+10,y,y/10);
		optGuess.setFont(new Font("Sans-serif", Font.BOLD, y/30));
		optGuess.setForeground(labelFore);
		optGuess.setBackground(Color.orange.darker());
		optGuess.setOpaque(true);
		panel.add(optGuess);
		
		for (int i = 0; i < 10; i++) 
		{
			JLabel label = new JLabel("", SwingConstants.CENTER);
			label.setFont(globalFont);
			label.setBorder(BorderFactory.createLineBorder(labelFore, 1));
			panel.add(label);
			int yPad = ySize+30+yPadding*2 + ((i/2)*y/13);
			if(i%2==0)
				label.setBounds((y-(y/3)*2-10)/2,yPad,(int)(y/3),y/15);
			else
				label.setBounds((y/2+y/3)-((y/3)),yPad,(int)(y/3),y/15);
			label.setBorder(BorderFactory.createLineBorder(labelFore,1));
			panel.add(label);
			botGuesses[i] = label;
			label.setFont(new Font("Arial",Font.BOLD,(int)(y/58)));
			label.setForeground(labelFore);
			label.setBackground(labelBack);
		}
		
		panel.repaint();
		
	}
	
	public void display(int guess)
	{
		Map<Integer,String> head = Map.of(1,"1st Guess<br>",2,"2nd Guess<br>",3,"3rd Guess<br>",4,"4th Guess<br>",5,"5th Guess<br>",6,"6th Guess<br>");
		for (int i = 0; i < 6; i++) 
		{
			if(i>=guess)
			{
				for (int j = 0; j < 5; j++) {
					grid[i][j].setText("");
					grid[i][j].setBorder(BorderFactory.createLineBorder(labelFore, 1));
					grid[i][j].setOpaque(false);
				}
			}
			else
			{
				String currentGuess[] = controller.getGame().getGuesses()[i];
				for (int j = 0; j < 5; j++) {
					String evaluation = controller.getGame().getGuessEvaluations()[i][j];
					JLabel label = grid[i][j];
					label.setText(currentGuess[j]);
					label.setOpaque(true);
					label.setForeground(labelFore);
					label.setFont(globalFont);
					label.setBorder(null);
					label.setBackground(colorEvaluationMap.get(evaluation));
				}
			}
		}
		System.out.println("Curr Guess: "+currGuess);
		Map<String,String> playerData = bot.getBotGuessEvaluations().get(currGuess).get("player");
		String your = head.get(currGuess)+"<br>";
		your+= ("<br>Guess: "+playerData.get("guess"));
		your+= ("<br>Skill: "+playerData.get("skill"));
		your+= ("<br>Entropy: "+playerData.get("entropy"));
		your+= ("<br>Solution after guess: "+playerData.get("solutions_after_guess"));
		yourGuess.setText("<html>"+your+"</html>");
		
		for(int i=1;i<10;i++)
		{
			if(i<bot.getBotGuessEvaluations().get(currGuess).size())
			{
				Map<String,String> optimal = bot.getBotGuessEvaluations().get(currGuess).get("optimal_"+i);
				String optimalStr= "";
				optimalStr+= ("Guess: "+optimal.get("word"));
				optimalStr+= ("&emsp;Skill: "+optimal.get("skill"));
				optimalStr+= ("&emsp;Entropy: "+optimal.get("entropy"));
				botGuesses[i-1].setText("<html>"+optimalStr+"</html>");
			}
			else
				botGuesses[i-1].setText("");
		}
		}
}