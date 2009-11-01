import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;


/**
 * A text field for search/filter interfaces. The extra functionality includes
 * a placeholder string (when the user hasn't yet typed anything), and a button
 * to clear the currently-entered text.
 * 
 * @author Elliott Hughes
 */


//
// TODO: add a menu of recent searches.
// TODO: make recent searches persistent.
// TODO: use rounded corners, at least on Mac OS X.
//


public class SearchField extends JTextField {

    private static final Border CANCEL_BORDER = new CancelBorder();
   

    private boolean sendsNotificationForEachKeystroke = false;
    private boolean showingPlaceholderText = false;
    private boolean armed = false;
	
/*	protected void paintComponent(Graphics g) {  
	    g.setColor(getBackground());
	    g.fillRect(0,0,getWidth(), getHeight());  
   	    super.paintComponent(g);  

	}  
 */   

    public SearchField(String placeholderText) {
        super(15);
        addFocusListener(new PlaceholderText(placeholderText));
        initBorder();
        initKeyListener();
    }

    

    public SearchField() {
        this("Search");
    }

    

    private void initBorder() {
        setBorder(new CompoundBorder(getBorder(),CANCEL_BORDER));
  
        MouseInputListener mouseInputListener = new CancelListener();
        addMouseListener(mouseInputListener);
        addMouseMotionListener(mouseInputListener);
        setMaximumSize(new Dimension(5000,30));
    }

    

    private void initKeyListener() {
        addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    cancel();
                } else if (sendsNotificationForEachKeystroke) {
                    maybeNotify();
                }
            }
        });
    }

    

    private void cancel() {
        setText("");
        postActionEvent();
    }

    

    private void maybeNotify() {
        if (showingPlaceholderText) {
            return;
        }
        postActionEvent();
    }


    public void setSendsNotificationForEachKeystroke(boolean eachKeystroke) {
        this.sendsNotificationForEachKeystroke = eachKeystroke;
    }



    /**
     * Draws the cancel button as a gray circle with a white cross inside.
     */

    static class CancelBorder extends EmptyBorder {

        private static final Color GRAY = new Color(0.7f, 0.7f, 0.7f);
       

        CancelBorder() {
            super(0, 20, 0, 15);
        }

        public void paintBorder(Component c, Graphics oldGraphics, int x, int y, int width, int height) {

            SearchField field = (SearchField) c;
            Graphics2D g = (Graphics2D) oldGraphics;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            final int circleL = 14;
            
            final int lensX = x;
            final int lensY = y;
            final int lensL = 12;
      
			g.setColor(Color.GRAY);

	
			g.fillOval(lensX, lensY, lensL, lensL);
            g.setStroke(new BasicStroke(3));

            g.drawLine(lensX+lensL/2, lensY+lensL/2, lensX+circleL, 
            	lensY+circleL);

            g.setStroke(new BasicStroke(1));
			
			g.setColor(Color.WHITE);
			
			g.fillOval(lensX+2, lensY+2, lensL-4, lensL-4);


			g.setColor(field.armed ? Color.GRAY : GRAY);

            if (field.showingPlaceholderText || field.getText().length() == 0) {
                return;
            }

            

            final int circleX = x + width - circleL;
            final int circleY = y + (height - 1 - circleL)/2;
            g.setColor(field.armed ? Color.GRAY : GRAY);
            g.fillOval(circleX, circleY, circleL, circleL);

            

            final int lineL = circleL - 8;
            final int lineX = circleX + 4;
            final int lineY = circleY + 4;

            g.setColor(Color.WHITE);
            g.drawLine(lineX, lineY, lineX + lineL, lineY + lineL);
            g.drawLine(lineX, lineY + lineL, lineX + lineL, lineY);
        }
    }

    

    /**
     * Handles a click on the cancel button by clearing the text and notifying
     * any ActionListeners.
     */

    class CancelListener extends MouseInputAdapter {

        private boolean isOverButton(MouseEvent e) {

            // If the button is down, we might be outside the component
            // without having had mouseExited invoked.

            if (contains(e.getPoint()) == false) {
                return false;
            }

            
            // In lieu of proper hit-testing for the circle, check that
            // the mouse is somewhere in the border.

            Rectangle innerArea = SwingUtilities.calculateInnerArea(SearchField.this, null);
            return (innerArea.contains(e.getPoint()) == false);
        }

        

        public void mouseDragged(MouseEvent e) {
            arm(e);
        }

        
        public void mouseEntered(MouseEvent e) {
            arm(e);
        }

        

        public void mouseExited(MouseEvent e) {
            disarm();
        }

        

        public void mousePressed(MouseEvent e) {
            arm(e);
        }

        

        public void mouseReleased(MouseEvent e) {
            if (armed) {
                cancel();
            }
            disarm();
        }

        

        private void arm(MouseEvent e) {
            armed = (isOverButton(e) && SwingUtilities.isLeftMouseButton(e));
            repaint();
        }

        

        private void disarm() {
            armed = false;
            repaint();
        }
    }

    

    /**
     * Replaces the entered text with a gray placeholder string when the
     * search field doesn't have the focus. The entered text returns when
     * we get the focus back.
     */

    class PlaceholderText implements FocusListener {

        private String placeholderText;
        private String previousText = "";
        private Color previousColor;


        PlaceholderText(String placeholderText) {
            this.placeholderText = placeholderText;
            focusLost(null);
        }


        public void focusGained(FocusEvent e) {
            setForeground(previousColor);
            setText(previousText);
            showingPlaceholderText = false;
        }


        public void focusLost(FocusEvent e) {
            previousText = getText();
            previousColor = getForeground();

            if (previousText.length() == 0) {
                showingPlaceholderText = true;
                setForeground(Color.GRAY);
                setText(placeholderText);
            }
        }
    }
}
