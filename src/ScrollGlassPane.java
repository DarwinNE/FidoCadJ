import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import globals.*;




/** Based on snippets on:
http://jroller.com/pago/entry/improving_jscrollpane_with_mouse_based
http://www.jroller.com/santhosh/entry/enhanced_scrolling_in_swing


@author Santhosh Kumar T - santhosh@in.fiorano.com 


Autorization requested and obtained.

*/


public class ScrollGlassPane extends JPanel implements ActionListener, MouseInputListener, SwingConstants{ 
       private static final Image img = MouseScrollIcon.iconToImage(new MouseScrollIcon()); 
    
       Component oldGlassPane = null; 
       public Point location = null; 
    
       private Timer movingTimer; 
       private Point mouseLocation; 
       private JViewport viewport; 
    
       public ScrollGlassPane(Component oldGlassPane, JViewport viewport, Point location){ 
           this.oldGlassPane = oldGlassPane; 
           this.viewport = viewport; 
           this.location = mouseLocation = location; 
    
           setOpaque(false); 
    
           ScrollGestureRecognizer.getInstance().stop(); 
           addMouseListener(this); 
           addMouseMotionListener(this); 
    
           movingTimer = new Timer(100, this); 
           movingTimer.setRepeats(true); 
           movingTimer.start(); 
       } 
    
       protected void paintComponent(Graphics g){ 
           g.drawImage(img, location.x-15, location.y-15, this); 
       } 
    
       /*-------------------------------------------------[ ActionListener ]---------------------------------------------------*/ 
    
       public void actionPerformed(ActionEvent e) { 
           int deltax = (mouseLocation.x - location.x)/4; 
           int deltay = (mouseLocation.y - location.y)/4; 
    
    
           Point p = viewport.getViewPosition(); 
           p.translate(deltax, deltay); 
    
           if(p.x<0) 
               p.x=0; 
           else if(p.x>=viewport.getView().getWidth()-viewport.getWidth()) 
               p.x = viewport.getView().getWidth()-viewport.getWidth(); 
    
           if(p.y<0) 
               p.y = 0; 
           else if(p.y>=viewport.getView().getHeight()-viewport.getHeight()) 
               p.y = viewport.getView().getHeight()-viewport.getHeight(); 
    
           viewport.setViewPosition(p); 
       } 
    
       /*-------------------------------------------------[ MouseListener ]---------------------------------------------------*/ 
        
       public void mousePressed(MouseEvent e) { 
           movingTimer.stop(); 
           setVisible(false); 
           JRootPane rootPane = SwingUtilities.getRootPane(this); 
           rootPane.setGlassPane(oldGlassPane); 
           ScrollGestureRecognizer.getInstance().start(); 
       } 
    
       public void mouseClicked(MouseEvent e){ 
           mousePressed(e); 
       } 
    
       public void mouseMoved(MouseEvent e) { 
           mouseLocation = e.getPoint(); 
       } 
    
       public void mouseDragged(MouseEvent e){} 
       public void mouseEntered(MouseEvent e){} 
       public void mouseExited(MouseEvent e){} 
       public void mouseReleased(MouseEvent e){} 
   }
   
   class MouseScrollIcon implements Icon {
		public void paintIcon(Component c, Graphics g, int x, int y) {
			Graphics2D gfx = (Graphics2D)g;
			gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			
			
			// It seems that on some systems other than on MacOSX, the 
			// background of the icon is always painted in black.
			if (!Globals.weAreOnAMac) {
				gfx.setColor(Color.white);
				gfx.fillRect(x, y, getIconWidth(), getIconHeight());
			}
			
			gfx.setColor(Color.gray);
			gfx.drawOval(x+2, y+2, getIconWidth()-5, getIconHeight()-5);
			
			gfx.setColor(Color.black);
			gfx.fillOval(x+((getIconWidth()-1)/2)-3, y+((getIconHeight()-1)/2)-3, 6, 6);
			
			/* Create arrows */
			Polygon arrow1 = new Polygon();
			arrow1.addPoint(x+(getIconWidth()-1)/2,y);
			arrow1.addPoint(x+(getIconWidth()-1)/2+3,y+5);
			arrow1.addPoint(x+(getIconWidth()-1)/2-3,y+5);
			Polygon arrow2 = new Polygon();
			arrow2.addPoint(x+(getIconWidth()-1)/2,y+getIconHeight()-1);
			arrow2.addPoint(x+(getIconWidth()-1)/2+3,y+getIconHeight()-1-5);
			arrow2.addPoint(x+(getIconWidth()-1)/2-3,y+getIconHeight()-1-5);
			
			Polygon arrow3 = new Polygon();
			arrow3.addPoint(x,y+(getIconHeight()-1)/2);
			arrow3.addPoint(x+5,y+(getIconHeight()-1)/2-3);
			arrow3.addPoint(x+5,y+(getIconHeight()-1)/2+3);
			Polygon arrow4 = new Polygon();
			arrow4.addPoint(x+(getIconWidth()-1),y+(getIconHeight()-1)/2);
			arrow4.addPoint(x+(getIconWidth()-1)-5,y+(getIconHeight()-1)/2-3);
			arrow4.addPoint(x+(getIconWidth()-1)-5,y+(getIconHeight()-1)/2+3);
			
			gfx.fillPolygon(arrow1);
			gfx.fillPolygon(arrow2);

			gfx.fillPolygon(arrow3);
			gfx.fillPolygon(arrow4);

			
			gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_DEFAULT);
		}

		public int getIconWidth() {
			return 29;
		}

		public int getIconHeight() {
			return 29;
		}
		
	static Image iconToImage(Icon icon) {
        if (icon instanceof ImageIcon) {
            return ((ImageIcon)icon).getImage();
        } else {
            int w = icon.getIconWidth();
            int h = icon.getIconHeight();
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gd = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gd.getDefaultConfiguration();
            BufferedImage image = gc.createCompatibleImage(w, h);
            Graphics2D g = image.createGraphics();
            icon.paintIcon(null, g, 0, 0);
            g.dispose();
            return image;
        }
    }
	}