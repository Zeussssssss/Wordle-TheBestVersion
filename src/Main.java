import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		JFrame jf = new JFrame("WORDLE");
		jf.setBounds(400,50,700,700);
		jf.setVisible(true);
		jf.setLayout(null);
		JLabel title = new JLabel("WORDLE");
		title.setFont(new Font("Arial",Font.BOLD,50));
		jf.add(title);
		title.setBounds(235, 100, 226, 100);
		
		JButton single = new JButton("New SinglePlayer");
		single.setLayout(null);
		single.setBounds(235,300,226,50);
		JButton multi  = new JButton("New MultiPlayer");
		multi.setLayout(null);
		multi.setBounds(235,400,226,50);
		jf.add(multi);
		jf.add(single);
		single.setVisible(true);
		multi.setVisible(true);
		single.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							newGame();
							jf.dispose();
						} catch (FileNotFoundException e1) {}
					}
				});
	}
	
	public static void newGame() throws FileNotFoundException
	{
		UI ui = new UI("light");	
		WordleGame game = new WordleGame();
		Controller controller = new Controller(ui, game);
		ui.addController(controller);
		controller.start();
	}
}