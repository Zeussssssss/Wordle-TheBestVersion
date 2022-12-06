import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.UnknownHostException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import static com.mongodb.client.model.Filters.eq;
import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Main {

	private static WordleGame game;
	private static Controller controller;
	private static UI ui;
	private static WordleServer server;
	private static MongoCollection<Document> collection; 
	private static MongoDatabase db;
	private static MongoClient mongoClient; 
	private static String playerName = "player";
	

	public static void main(String[] args) throws FileNotFoundException, UnknownHostException {
		
		setUpDatabaseConnection();

		Clip music = playMusic("Menu.wav", true);
		JFrame jf = new JFrame("WORDLE");
		JPanel jp = new JPanel();
		jf.getContentPane().add(jp);
		jp.setBounds(0,0,700,700);
		jp.setOpaque(false);
		jp.setVisible(true);
		jf.setBounds(400, 50, 700, 700);
		jf.setVisible(true);
		jf.setLayout(null);

		JLabel title = new JLabel("W O R D L E", SwingConstants.CENTER);
		title.setFont(new Font("Helvetica", Font.BOLD, 70));
		jf.getContentPane().add(title);
		title.setBounds(0, 200, 700, 100);
		title.setForeground(Color.white);

		Font buttons = new Font("Angwie", Font.BOLD, 20);
		Font hover = new Font("Angwie", Font.BOLD, 23);
		JTextArea nameField = new JTextArea("  Enter your Name!");
		nameField.setBounds(230, 315, 250, 50);
		nameField.setBorder(BorderFactory.createLineBorder(Color.white, 7, true));
		nameField.setBackground(Color.black);
		nameField.setForeground(Color.white);
		nameField.setFont(hover);
		JLabel single = new JLabel("Single Player Mode", SwingConstants.CENTER);
		single.setBounds(0, 375, 700, 50);
		single.setForeground(Color.white);
		single.setFont(buttons);
		JLabel multi = new JLabel("Collabortative Mode", SwingConstants.CENTER);
		multi.setBounds(0, 450, 700, 50);
		multi.setForeground(Color.white);
		multi.setFont(buttons);
		JLabel create = new JLabel("Create New Game", SwingConstants.CENTER);
		create.setBounds(0, 325, 700, 50);
		create.setForeground(Color.white);
		create.setFont(buttons);
		JLabel join = new JLabel("Join Existing Game", SwingConstants.CENTER);
		join.setBounds(0, 400, 700, 50);
		join.setForeground(Color.white);
		join.setFont(buttons);

		jf.getContentPane().add(multi);
		jf.getContentPane().add(single);
		jf.getContentPane().add(nameField);
		single.setVisible(true);
		multi.setVisible(true);
		nameField.setVisible(true);
		ImageIcon image = new ImageIcon("Resources/Menu.jpg");
		JLabel bg = new JLabel();
		bg.setBounds(0, 0, 700, 700);
		bg.setIcon(image);
		jf.getContentPane().add(bg);
		bg.repaint();
		single.addMouseListener(new MouseListener() {

			int x = 20;
			boolean entered = false;

			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					playerName = nameField.getText();
					newGame(true, "", music);
					jf.dispose();
				} catch (FileNotFoundException e1) {
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				e.getComponent().setFont(hover);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				e.getComponent().setFont(buttons);
			}

		});

		multi.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				new PopUp("COPIED TO CLIPBOARD!",jp);
				single.setVisible(false);
				single.setEnabled(false);
				multi.setVisible(false);
				multi.setEnabled(false);
				nameField.setVisible(false);
				nameField.setEnabled(false);
				playerName = nameField.getText();

				bg.add(create);
				bg.add(join);
				create.setVisible(true);
				join.setVisible(true);

				create.addMouseListener(new MouseListener() {

					@Override
					public void mouseClicked(MouseEvent e) {
						try {
							new Thread(server = new WordleServer()).start();
							newGame(false, server.getAddress(), music);
							String str = ""+server.getAddress();
							StringSelection stringSelection = new StringSelection(str);
							Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
							clipboard.setContents(stringSelection, null);
						} catch (Exception exception) {
						}
						jf.dispose();
					}

					@Override
					public void mousePressed(MouseEvent e) {
					}

					@Override
					public void mouseReleased(MouseEvent e) {
					}

					@Override
					public void mouseEntered(MouseEvent e) {
						e.getComponent().setFont(hover);
					}

					@Override
					public void mouseExited(MouseEvent e) {
						e.getComponent().setFont(buttons);
					}

				});
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				e.getComponent().setFont(hover);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				e.getComponent().setFont(buttons);
			}
		});

		join.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				create.setVisible(false);
				create.setEnabled(false);
				join.setVisible(false);
				join.setEnabled(false);
				JTextArea inputField = new JTextArea("Enter Server Address");
				inputField.setBounds(215, 350, 270, 50);
				inputField.setBorder(BorderFactory.createLineBorder(Color.white, 7, true));
				inputField.setBackground(Color.black);
				inputField.setForeground(Color.white);
				inputField.setFont(hover);
				JLabel start = new JLabel("JOIN", SwingConstants.CENTER);
				start.setBounds(0, 400, 700, 50);
				start.setForeground(Color.white);
				start.setFont(buttons);

				bg.add(inputField);
				bg.add(start);
				inputField.setVisible(true);
				start.setVisible(true);

				start.addMouseListener(new MouseListener() {

					@Override
					public void mouseClicked(MouseEvent e) {
						String ipt = inputField.getText();
						try {
							newGame(false, ipt, music);
						} catch (Exception exception) {
						}
						jf.dispose();
					}

					@Override
					public void mousePressed(MouseEvent e) {
					}

					@Override
					public void mouseReleased(MouseEvent e) {
					}

					@Override
					public void mouseEntered(MouseEvent e) {
						e.getComponent().setFont(hover);
					}

					@Override
					public void mouseExited(MouseEvent e) {
						e.getComponent().setFont(buttons);
					}

				});
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				e.getComponent().setFont(hover);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				e.getComponent().setFont(buttons);
			}
		});
	}

	private static void setUpDatabaseConnection() throws UnknownHostException {
		mongoClient = MongoClients.create("mongodb+srv://adityakumar:775NRisvDQGSwl8Q@wordle.w9bbtv3.mongodb.net/?retryWrites=true&w=majority");
		db = mongoClient.getDatabase("Wordle");
		collection = db.getCollection("Wordle");
	}

	public static Clip playMusic(String path, boolean loop) {
		path = "Resources/" + path;
		try {
			File music = new File(path);
			AudioInputStream audio = AudioSystem.getAudioInputStream(music);
			Clip clip = AudioSystem.getClip();
			clip.open(audio);
			clip.start();
			if (loop)
				clip.loop(1000);
			return clip;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void newGame(boolean single, String serverAddress, Clip clip) throws FileNotFoundException {
		clip.stop();
		ui = new UI("dark");
		game = new WordleGame();
		controller = new Controller(ui, game, single, serverAddress, collection, playerName);
		ui.addController(controller);
		controller.start();
	}
}