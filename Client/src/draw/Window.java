package draw;
import java.awt.Dimension;
import javax.swing.JFrame;

import input.*;

public class Window
{
	public Window (int w, int h, String title, GameCanvas gc)
	{
			gc.setPreferredSize(new Dimension(w-10,h-10));

			JFrame frame = new JFrame(title);
			frame.add(gc);
			frame.pack();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setResizable(false);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
			gc.addKeyListener(new KeyInput());
			gc.addMouseListener(new MouseInput());
			gc.start();
	}
}