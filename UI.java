/**
 * Name: Alankrit Jacinth Moses
 * FileName: UI.java
 * Description: This is View class from the 
 * 				MVC architecture of the client side
 */
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;
import javax.swing.border.*;
public class UI {
	
	int x=0;
	int y=0;
	JLabel[][] list;
	JFrame frame;
	String currentString;
	int currentRow,currentCol;
	
	/**
	 * Constructor to create the basic view
	 */
	public UI()
	{
		// Initializing the global variables
		currentString = "";
		currentRow = 0;
		currentCol = 0;
		
		//Creating main frame
		frame = new JFrame("WORDLE");
		frame.setLayout(null);
		frame.setBounds(0, 0, 700, 800);
		frame.setVisible(true);
		
		//Creating Label for game state
		Font jf = new Font("Cursive",Font.BOLD, 30);
		JLabel state = new JLabel("",SwingConstants.CENTER);
		state.setFont(jf);
		state.setBounds(0,10,700,50);
		
		//Creating panel to add all labels and graphic elements
		JPanel jp = new JPanel();
		frame.getContentPane().add(jp);
		jp.setLayout(null);
		jp.setBounds(0,0,700,800);
		jp.add(state);
		
		list = initBoard(jp);
		//Adding the KeyListener to frame
		frame.addKeyListener(new KeyListener(){

					@Override
					public void keyTyped(KeyEvent e) {
						char character = e.getKeyChar();
						character = Character.toUpperCase(character);
						if(currentCol<5)
						{
							if((character>='A' && character<='Z'))
							{
								currentString+=character;
								list[currentRow][currentCol].setText(character+"");
								currentCol+=1;
								System.out.println("currentCol: "+currentCol);
								state.setText("");
							}
						}
					}

					@Override
					public void keyPressed(KeyEvent e) {
						System.out.println("Pressed: "+e.getKeyCode());
						if(e.getKeyCode()==8)
						{
							if(currentCol>0)
							{
								currentCol-=1;
								currentString = currentString.substring(0,currentCol);
								list[currentRow][currentCol].setText("");
								System.out.println("currentCol: "+currentCol);
							}
							state.setText("");
						}
						if(e.getKeyCode()==10)
						{
							if(currentCol>=5)
							{
								state.setText("Checking for WORD and changing lines");
								if(currentRow<6)
								{
									currentRow+=1;
									currentCol=0;
								}
							}
							else
								state.setText("WORD TOO SHORT!");
						}
					}

					@Override
					public void keyReleased(KeyEvent e) {
					}
			
				});
	}
	
	/**
	 * Initializing the labels and returning the list
	 * @param  jp: JPanel to add all the labels
	 * @return list: 2D list of Labels
	 */
	public JLabel[][] initBoard(JPanel jp)
	{
		JLabel[][] list= new JLabel[6][5];
		Font jf = new Font("Arial",Font.BOLD, 25);
		Graphics g = jp.getGraphics();
		g.setColor(Color.black);
		for(int y=0;y<6;y++)
		{
			for(int x=0;x<5;x++)
			{
				// Creating and adding labels to the list
				JLabel lb = new JLabel("",SwingConstants.CENTER);
				lb.setFont(jf);
				lb.setBounds(80+(110*x),80+(110*y),80,80);
				jp.add(lb);
				lb.setVisible(true);
				lb.setBorder(BorderFactory.createLineBorder(Color.black,5));
				list[y][x] = lb;
			}
		}
		return list;
	}
}
