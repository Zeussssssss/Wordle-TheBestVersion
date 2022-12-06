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
	class BoxAnim extends TimerTask
	{
		boolean down = true;
		boolean pause = false;
		boolean up = false;
		int duration = 0;
		public void run() 
		{
			if(down && label.getBounds().y<20)
			{
				System.out.println("DOWN");
				Rectangle d = label.getBounds();
				label.setBounds(d.x,d.y+1,d.width,d.height);
			}
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
			else if(label.getBounds().y>-56)
			{
				if(up)
				{
					System.out.println("UP");
					Rectangle d = label.getBounds();
					label.setBounds(d.x,d.y-1,d.width,d.height);
				}
			}
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
