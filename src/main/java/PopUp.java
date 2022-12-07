/**
 * Author: Alankrit Jacinth Moses
 * Description: This class is part of the UI(view) which instantiates and controls
 * 				the animation of the PopUp messages.
 */
import java.awt.Color;
import java.awt.Rectangle;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PopUp {
	
	JLabel label;
	JPanel jp;
	Timer timer;
	private JFrame jf;
	
	/**
	 * Constructor to add a PopUp label to the given JPanel
	 * @param message
	 * @param jp1
	 */
	public PopUp(String message,JPanel jp1)
	{
		this.jp = jp1;
		System.out.println("REACHED POPUP");
		label = new JLabel(message);
		label.setBackground(Color.white);
		label.setOpaque(true);
		jp.add(label);
		label.setBounds(jp.getSize().width/2-50,-55,100,50);
		label.setVisible(true);
		timer = new Timer();
		TimerTask ba = new BoxAnim();
		timer.schedule(ba, 0, 5);
	}
	
	/**
	 * Constructor to add a PopUp label to the given JFrame
	 * @param message
	 * @param jf
	 */
	public PopUp(String message,JFrame jf)
	{
		this.jf = jf;
		System.out.println("REACHED POPUP");
		label = new JLabel(message);
		label.setBackground(Color.white);
		label.setOpaque(true);
		jf.add(label);
		label.setBounds(jf.getSize().width/2-50,-55,100,50);
		label.setVisible(true);
		timer = new Timer();
		TimerTask ba = new BoxAnim();
		timer.schedule(ba, 0, 5);
	}
	
	/**
	 * A TimerTask class used for animating the PopUp label.
	 * @author Alankrit Moses
	 *
	 */
	class BoxAnim extends TimerTask
	{
		boolean down = true;
		boolean pause = false;
		boolean up = false;
		int duration = 0;
		
		/**
		 * Run method which runs as a parallel thread to animate the label
		 */
		public void run() 
		{
			// Controlling the downwards animation
			if(down && label.getBounds().y<20)
			{
				System.out.println("DOWN");
				Rectangle d = label.getBounds();
				label.setBounds(d.x,d.y+1,d.width,d.height);
			}
			// Pausing the animation
			else if(down && label.getBounds().y==20)
			{
				System.out.println("PAUSING");
				down = false;
				pause = true;
			}
			else if(pause && duration <50)
				duration+=1;
			else if(pause && duration==50)
			{
				pause = false;
				up = true;
			}
			// Animating the upwards motion of the label
			else if(label.getBounds().y>-56)
			{
				if(up)
				{
					System.out.println("UP");
					Rectangle d = label.getBounds();
					label.setBounds(d.x,d.y-1,d.width,d.height);
				}
			}
			// Stoping the Animation and deleting the label from the Panel/Frame
			else
			{
				System.out.println("CANCELLING");
				timer.cancel();
				label.setVisible(false);
				label.disable();
			}
			if(jp!=null)
				jp.repaint();
			else
				jf.repaint();
		}
	}
}