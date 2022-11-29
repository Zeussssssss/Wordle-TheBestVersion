import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

public class Main {

	private static WordleGame game;
	private static Controller controller;
	private static UI ui;
	private static WordleServer server;

	public static void main(String[] args) throws FileNotFoundException {
		JFrame jf = new JFrame("WORDLE");
		jf.setBounds(400, 50, 700, 700);
		jf.setVisible(true);
		jf.setLayout(null);
		JLabel title = new JLabel("WORDLE");
		title.setFont(new Font("Arial", Font.BOLD, 50));
		jf.add(title);
		title.setBounds(235, 100, 226, 100);

		JButton single = new JButton("New SinglePlayer");
		single.setLayout(null);
		single.setBounds(235, 300, 226, 50);
		JButton multi = new JButton("New MultiPlayer");
		multi.setLayout(null);
		multi.setBounds(235, 400, 226, 50);
		JButton create = new JButton("Create New Game");
		create.setLayout(null);
		create.setBounds(235, 300, 226, 50);
		JButton join = new JButton("Join Existing Game");
		join.setLayout(null);
		join.setBounds(235, 400, 226, 50);

		jf.add(multi);
		jf.add(single);
		single.setVisible(true);
		multi.setVisible(true);

		single.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					newGame(true, "");
					jf.dispose();
				} catch (FileNotFoundException e1) {
				}
			}
		});

		multi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				single.setVisible(false);
				single.setEnabled(false);
				multi.setVisible(false);
				multi.setEnabled(false);

				jf.add(create);
				jf.add(join);
				create.setVisible(true);
				join.setVisible(true);

				create.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							new Thread(server = new WordleServer());
							newGame(false, server.getAddress());
						} catch (Exception exception) {
						}
						jf.dispose();
					}
				});

				join.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						create.setVisible(false);
						create.setEnabled(false);
						join.setVisible(false);
						join.setEnabled(false); // Alankrit please make this pretty
						JTextArea inputField = new JTextArea("Enter Server Address");
						inputField.setBounds(235, 300, 226, 50);
						JButton start = new JButton("Join Existing Game");
						start.setLayout(null);
						start.setBounds(235, 400, 226, 50);

						jf.add(inputField);
						jf.add(start);
						inputField.setVisible(true);
						start.setVisible(true);

						start.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								String ipt = inputField.getText();
								try {
									newGame(false, ipt);
								} catch (Exception exception) {
								}
								jf.dispose();
							}

						});

					}
				});
			}
		});
	}

	public static void newGame(boolean single, String serverAddress) throws FileNotFoundException {
		ui = new UI("dark");
		game = new WordleGame();
		controller = new Controller(ui, game);
		ui.addController(controller);
		controller.start();
	}
}