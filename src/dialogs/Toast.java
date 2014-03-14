package dialogs;

import globals.Globals;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Window;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.plaf.metal.MetalBorders;

public class Toast {
	
	public static void display(String s) {
		JFrame toast = new JFrame();
		final JLabel tm = new JLabel(" ");
		toast.setAlwaysOnTop(true);					
		toast.setLocationRelativeTo(null);		
		toast.setUndecorated(true);
		JPanel bck = new JPanel();
		bck.setBorder(new MetalBorders.Flush3DBorder());
		JTextArea msg = new JTextArea(s);
		msg.setLineWrap(true);
		msg.setForeground(Color.blue);
		bck.setBackground(Color.yellow.brighter());
		tm.setForeground(Color.green);
		msg.setBackground(bck.getBackground());		
		msg.setSize(bck.getWidth(), bck.getHeight());		
		bck.add(msg,BorderLayout.CENTER);		
		toast.add(bck,BorderLayout.CENTER);
		bck.add(tm,BorderLayout.CENTER);
		toast.setVisible(true);		
		msg.setMargin(new Insets(1,1,1,1));
		msg.setSize(290,50);
		msg.setFont(new Font("Arial",0,12));
		msg.setEditable(false);
		toast.setSize(300, 55);		
		toast.setLocation(35 + toast.getX()-toast.getSize().width/2, 
			toast.getY() + 20);
				
		final long expire = System.currentTimeMillis() + 10 * 1000;
		final JFrame inst = toast;
		
		
		TimerTask tt = new TimerTask() {			
			@Override
			public void run() {	
				long n = (expire - System.currentTimeMillis()) / 1000;				
				tm.setText(String.valueOf(n));						
				if (n <= 0) { 
					inst.setVisible(false);
					inst.dispose();
					this.cancel();
				}				
			}
		};
		Timer t = new Timer();
		t.schedule(tt, 0, 999);
	}

}
