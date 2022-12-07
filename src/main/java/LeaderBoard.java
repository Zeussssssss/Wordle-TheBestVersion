import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class LeaderBoard {
	
	String arr[];
	Color labelFore,labelBack;
	public LeaderBoard(String[] array,Color labelFore, Color labelBack)
	{
		this.arr = array;
		this.labelBack = labelBack;
		this.labelFore = labelFore;
		this.display();
	}
	
	public void display()
	{
		JFrame window = new JFrame("BOT");
		window.setLayout(null);
		window.setBounds(300,30,500,600);
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(0,0,500,600);
		panel.setBackground(labelBack);
		window.getContentPane().add(panel);
		
		JLabel title = new JLabel("GLOBAL LEADERBOARD - TOP 5",SwingConstants.CENTER);
		title.setBounds(0,0,500,50);
		for(int x=1;x<6;x++)
		{
			JLabel number = new JLabel(x+"",SwingConstants.CENTER);
			number.setBounds(0,x*100,100,50);
			JLabel name = new JLabel(arr[x*2-2]+"",SwingConstants.CENTER);
			name.setBounds(100,x*100,200,50);
			JLabel score = new JLabel(arr[x*2-1]+"",SwingConstants.RIGHT);
			score.setBounds(300,x*100,100,50);
			panel.add(number);
			panel.add(name);
			panel.add(score);
		}
		panel.repaint();
	}
}
