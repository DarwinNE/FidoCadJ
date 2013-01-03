import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import circuit.*;
import toolbars.*;

/** @author Santhosh Kumar T - santhosh@in.fiorano.com 
   	Used in FidoCadJ with the author's permission.
*/ 
public class ScrollGestureRecognizer implements AWTEventListener,
	ChangeSelectionListener
{ 
    private int actionSelected;
    
    
    private static ScrollGestureRecognizer instance = new 
    ScrollGestureRecognizer(); 
    
    public ScrollGestureRecognizer(){ 
        start(); 
    } 
    
    public static ScrollGestureRecognizer getInstance(){ 
        return instance; 
    } 
    
    
    void start(){ 
    	Toolkit.getDefaultToolkit().addAWTEventListener(this, 
    	AWTEvent.MOUSE_EVENT_MASK); 
    } 
    
    void stop(){ 
        Toolkit.getDefaultToolkit().removeAWTEventListener(this); 
    } 
    
    public void eventDispatched(AWTEvent event){ 
        MouseEvent me = (MouseEvent)event; 
        boolean isGesture = (SwingUtilities.isMiddleMouseButton(me) || 
        actionSelected==CircuitPanel.HAND) && 
        me.getID()==MouseEvent.MOUSE_PRESSED; 
           
        Component co=me.getComponent();
           
        if (!(co instanceof CircuitPanel))
           	return;
           
           
        if(!isGesture) 
            return; 
    
        JViewport viewPort = 
        (JViewport)SwingUtilities.getAncestorOfClass(JViewport.class, 
        me.getComponent()); 
        if(viewPort==null) 
            return; 
        JRootPane rootPane = SwingUtilities.getRootPane(viewPort); 
        if(rootPane==null) 
            return; 
    
        Point location = SwingUtilities.convertPoint(me.getComponent(), 
        me.getPoint(), rootPane.getGlassPane()); 
        ScrollGlassPane glassPane = new ScrollGlassPane(rootPane.getGlassPane(), 
        viewPort, location); 
        rootPane.setGlassPane(glassPane); 
        glassPane.setVisible(true); 
    } 
    
    /** ChangeSelectionListener interface implementation */    
    public void setSelectionState(int s, String macro)
    {
 
        actionSelected=s;
    }
    
	/** Set if the strict FidoCAD compatibility mode is active
   		@param strict true if the compatibility with FidoCAD should be 
   		obtained.
   	
   	*/
   	public void setStrictCompatibility(boolean strict)
   	{
   		// Nothing is needed here.
   	}
    
    /** Get the current editing action (see the constants defined in this class)
    
        @return the current editing action
    
    */
    public int getSelectionState()
    {
        return actionSelected;
    }   
    
    
}
