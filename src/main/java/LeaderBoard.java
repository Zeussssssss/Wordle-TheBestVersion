import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class LeaderBoard {

	String arr[];
	Color labelFore, labelBack;
	private String mode;

	public LeaderBoard(String[] array, Color labelFore, Color labelBack, String mode) {
		this.arr = array;
		this.labelBack = labelBack;
		this.labelFore = labelFore;
		this.mode = mode;
		this.display();
	}

	public void display() {
		JFrame window = new JFrame("BOT");
		window.setLayout(null);
		window.setBounds(300, 30, 500, 600);
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(0, 0, 500, 600);
		panel.setBackground(labelBack);
		window.getContentPane().add(panel);

		Font f = new Font("Arial", Font.BOLD, 25);
		JLabel title = new JLabel("GLOBAL LEADERBOARD - TOP 5", SwingConstants.CENTER);
		title.setBounds(0, 0, 500, 50);
		title.setFont(f);
		panel.add(title);
		title.setForeground(labelFore);
		for (int x = 1; x < 6; x++) {
			JLabel number = new JLabel(x + "", SwingConstants.CENTER);
			number.setBounds(75, x * 100 - 25, 50, 50);
			number.setForeground(labelBack);
			number.setBackground(labelFore);
			number.setOpaque(true);
			JLabel name = new JLabel(" " + arr[x * 2 - 2], SwingConstants.LEFT);
			name.setBounds(135, x * 100 - 25, 190, 50);
			name.setForeground(labelFore);
			name.setBackground(Color.red.darker());
			name.setOpaque(true);
			JLabel score = new JLabel(arr[x * 2 - 1] + "", SwingConstants.CENTER);
			score.setBounds(325, x * 100 - 25, 100, 50);
			score.setForeground(labelFore);
			score.setBackground(Color.green.darker());
			score.setOpaque(true);
			panel.add(number);
			panel.add(name);
			panel.add(score);
			number.setFont(f);
			name.setFont(f);
			score.setFont(f);
			number.setBorder(BorderFactory.createLineBorder(labelFore, 3, false));
			name.setBorder(BorderFactory.createLineBorder(labelFore, 3, false));
			score.setBorder(BorderFactory.createLineBorder(labelFore, 3, false));
		}
		panel.repaint();
		window.setVisible(true);
	}
}