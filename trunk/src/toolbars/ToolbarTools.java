package toolbars;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.net.*;

import globals.*;
import circuit.*;


/**
    ToolbarTools class
    
    @author Davide Bucci
    @version 1.0, May 2008
 
   <pre>
   Written by Davide Bucci, May 2008, davbucci at tiscali dot it
   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
   </pre>
.
   

    I have to admit that I do not like so much how I implemented this class.
    It seems to me that I did a few unnecessary cut and paste and the code is 
    simple but tedious and potentially error prone.
    I have left this way, as the whole is still maintenable.
*/
public class ToolbarTools extends JToolBar
	implements ActionListener,
			   ChangeSelectionListener

{
    
    

    
    private JToggleButton selection;
    private JToggleButton zoom;
    private JToggleButton hand;  
    private JToggleButton line;
    private JToggleButton advtext;
    private JToggleButton bezier;
    private JToggleButton polygon;
    private JToggleButton ellipse;
    private JToggleButton rectangle;
    private JToggleButton connection;
    private JToggleButton pcbline;
    private JToggleButton pcbpad;
    private ChangeSelectionListener selectionListener;
    
      
    
    public ToolbarTools (boolean showText, boolean smallIcons) {
        
        String base;
        setOpaque(false);
        
        if(smallIcons)
        	base="icons16/";
        else
        	base="icons32/";
        
        setBorderPainted(false);
               	
        URL url=ToolbarTools.class.getResource(base+"arrow.png");
        selection = new JToggleButton(
        	(showText?Globals.messages.getString("Selection"):""),
        	new ImageIcon(url));
        selection.setActionCommand("selection");
        selection.setToolTipText(
        	Globals.messages.getString("tooltip_selection"));
        selection.setVerticalTextPosition(SwingConstants.BOTTOM);
        selection.setHorizontalTextPosition(SwingConstants.CENTER);
        
        url=ToolbarTools.class.getResource(base+"magnifier.png");
        zoom = new JToggleButton(
        	(showText?Globals.messages.getString("Zoom_p"):""),
        	new ImageIcon(url));
       	zoom.setActionCommand("zoom");
        zoom.setToolTipText(
        	Globals.messages.getString("tooltip_zoom"));
        zoom.setVerticalTextPosition(SwingConstants.BOTTOM);
        zoom.setHorizontalTextPosition(SwingConstants.CENTER);	
        
        	
        url=ToolbarTools.class.getResource(base+"move.png");
        hand = new JToggleButton(
        	(showText?Globals.messages.getString("Hand"):""),
        	new ImageIcon(url));
        hand.setActionCommand("hand");
        hand.setToolTipText(
        	Globals.messages.getString("tooltip_hand"));
       	hand.setVerticalTextPosition(SwingConstants.BOTTOM);
        hand.setHorizontalTextPosition(SwingConstants.CENTER);
        
        url=ToolbarTools.class.getResource(base+"line.png");
        line = new JToggleButton(
        	(showText?Globals.messages.getString("Line"):""),
        	new ImageIcon(url));
        line.setActionCommand("line");
        line.setToolTipText(
        	Globals.messages.getString("tooltip_line"));
        line.setVerticalTextPosition(SwingConstants.BOTTOM);
        line.setHorizontalTextPosition(SwingConstants.CENTER);
        
       
        url=ToolbarTools.class.getResource(base+"text.png");
        advtext = new JToggleButton(
        	(showText?Globals.messages.getString("Text"):""),
        	new ImageIcon(url));
        advtext.setActionCommand("text");
        advtext.setToolTipText(
        	Globals.messages.getString("tooltip_text"));
        advtext.setVerticalTextPosition(SwingConstants.BOTTOM);
        advtext.setHorizontalTextPosition(SwingConstants.CENTER);
        
        url=ToolbarTools.class.getResource(base+"bezier.png");
        bezier = new JToggleButton(
        	(showText?Globals.messages.getString("Bezier"):""),
        	new ImageIcon(url));
        bezier.setActionCommand("bezier");
        bezier.setToolTipText(
        	Globals.messages.getString("tooltip_bezier"));
        bezier.setVerticalTextPosition(SwingConstants.BOTTOM);
        bezier.setHorizontalTextPosition(SwingConstants.CENTER);
        
        url=ToolbarTools.class.getResource(base+"polygon.png");
        polygon = new JToggleButton(
        	(showText?Globals.messages.getString("Polygon"):""),
        	new ImageIcon(url));
        polygon.setActionCommand("polygon");
        polygon.setToolTipText(
        	Globals.messages.getString("tooltip_polygon"));
        polygon.setVerticalTextPosition(SwingConstants.BOTTOM);
        polygon.setHorizontalTextPosition(SwingConstants.CENTER);
                
        
        url=ToolbarTools.class.getResource(base+"ellipse.png");
        ellipse = new JToggleButton(
        	(showText?Globals.messages.getString("Ellipse"):""),
        	new ImageIcon(url));
        ellipse.setActionCommand("ellipse");
        ellipse.setToolTipText(
        	Globals.messages.getString("tooltip_ellipse"));
        ellipse.setVerticalTextPosition(SwingConstants.BOTTOM);
        ellipse.setHorizontalTextPosition(SwingConstants.CENTER);
        
        
        url=ToolbarTools.class.getResource(base+"rectangle.png");
        rectangle = new JToggleButton(
        	(showText?Globals.messages.getString("Rectangle"):""),
        	new ImageIcon(url));
        rectangle.setActionCommand("rectangle");
        rectangle.setToolTipText(
        	Globals.messages.getString("tooltip_rectangle"));
        rectangle.setVerticalTextPosition(SwingConstants.BOTTOM);
        rectangle.setHorizontalTextPosition(SwingConstants.CENTER);
        
        
        url=ToolbarTools.class.getResource(base+"connection.png");
        connection = new JToggleButton(
        	(showText?Globals.messages.getString("Connection"):""),
        	new ImageIcon(url));
        connection.setActionCommand("connection");
        connection.setToolTipText(
        	Globals.messages.getString("tooltip_connection"));
        connection.setVerticalTextPosition(SwingConstants.BOTTOM);
        connection.setHorizontalTextPosition(SwingConstants.CENTER);
        
        
        
        url=ToolbarTools.class.getResource(base+"pcbline.png");
        pcbline = new JToggleButton(
        	(showText?Globals.messages.getString("PCBline"):""),
        	new ImageIcon(url));
        pcbline.setActionCommand("pcbline");
        pcbline.setToolTipText(
        	Globals.messages.getString("tooltip_pcbline"));
        pcbline.setVerticalTextPosition(SwingConstants.BOTTOM);
        pcbline.setHorizontalTextPosition(SwingConstants.CENTER);
        
        
        
        url=ToolbarTools.class.getResource(base+"pcbpad.png");
        pcbpad = new JToggleButton(
        	(showText?Globals.messages.getString("PCBpad"):""),
        	new ImageIcon(url));
        pcbpad.setActionCommand("pcbpad");
        pcbpad.setToolTipText(
        	Globals.messages.getString("tooltip_pcbpad"));
       	pcbpad.setVerticalTextPosition(SwingConstants.BOTTOM);
        pcbpad.setHorizontalTextPosition(SwingConstants.CENTER);
        

        selection.addActionListener(this);
        zoom.addActionListener(this);
        hand.addActionListener(this);
        line.addActionListener(this);
        advtext.addActionListener(this);
        bezier.addActionListener(this);
        polygon.addActionListener(this);
        ellipse.addActionListener(this);
        rectangle.addActionListener(this);
        connection.addActionListener(this);
        pcbline.addActionListener(this);
        pcbpad.addActionListener(this);

	
        add(selection);
        add(zoom);
        add(hand);
        add(line);
        add(advtext);
        add(bezier);
        add(polygon);
        add(ellipse);
        add(rectangle);
        add(connection);
        add(pcbline);
        add(pcbpad);
     	
        add(Box.createGlue());
		
		// MacOSX Quaqua style settings
		
/*		selection.putClientProperty("Quaqua.Button.style","toggleWest");
		zoom.putClientProperty("Quaqua.Button.style","toggleCenter");
		hand.putClientProperty("Quaqua.Button.style","toggleCenter");
		line.putClientProperty("Quaqua.Button.style","toggleCenter");
		advtext.putClientProperty("Quaqua.Button.style","toggleCenter");
		bezier.putClientProperty("Quaqua.Button.style","toggleCenter");
		polygon.putClientProperty("Quaqua.Button.style","toggleCenter");
		ellipse.putClientProperty("Quaqua.Button.style","toggleCenter");
		rectangle.putClientProperty("Quaqua.Button.style","toggleCenter");
		connection.putClientProperty("Quaqua.Button.style","toggleCenter");
		pcbline.putClientProperty("Quaqua.Button.style","toggleCenter");
		pcbpad.putClientProperty("Quaqua.Button.style","toggleEast");
*/	
		selection.putClientProperty("Quaqua.Button.style","toolBarTab");
		zoom.putClientProperty("Quaqua.Button.style","toolBarTab");
		hand.putClientProperty("Quaqua.Button.style","toolBarTab");
		line.putClientProperty("Quaqua.Button.style","toolBarTab");
		advtext.putClientProperty("Quaqua.Button.style","toolBarTab");
		bezier.putClientProperty("Quaqua.Button.style","toolBarTab");
		polygon.putClientProperty("Quaqua.Button.style","toolBarTab");
		ellipse.putClientProperty("Quaqua.Button.style","toolBarTab");
		rectangle.putClientProperty("Quaqua.Button.style","toolBarTab");
		connection.putClientProperty("Quaqua.Button.style","toolBarTab");
		pcbline.putClientProperty("Quaqua.Button.style","toolBarTab");
		pcbpad.putClientProperty("Quaqua.Button.style","toolBarTab");
        setFloatable(false);
        setRollover(true);
    }
    
    
    
    public int getSelectionState()
    {
   	 	if(selection.isSelected())
   	 		return CircuitPanel.SELECTION;
   	 	if(zoom.isSelected())
   	 		return CircuitPanel.ZOOM;
		if(hand.isSelected())
   	 		return CircuitPanel.HAND; 
		if(line.isSelected())
   	 		return CircuitPanel.LINE;        
   	 	if(advtext.isSelected())
   	 		return CircuitPanel.TEXT; 
		if(bezier.isSelected())
   	 		return CircuitPanel.BEZIER;        
   	 	if(polygon.isSelected())
   	 		return CircuitPanel.POLYGON;        
   	 	if(ellipse.isSelected())
   	 		return CircuitPanel.ELLIPSE;
        if(rectangle.isSelected())
   	 		return CircuitPanel.RECTANGLE;
        if(connection.isSelected())
   	 		return CircuitPanel.CONNECTION;
        if(pcbline.isSelected())
   	 		return CircuitPanel.PCB_LINE;
        if(pcbpad.isSelected())
   	 		return CircuitPanel.PCB_PAD;
     	
     	return CircuitPanel.NONE;
    }
    
    public void setSelectionState(int s, String m)
    {
    	selection.setSelected(false);
    	zoom.setSelected(false);
    	hand.setSelected(false);
    	line.setSelected(false);
    	advtext.setSelected(false);
    	bezier.setSelected(false);
    	polygon.setSelected(false);
    	ellipse.setSelected(false);
    	rectangle.setSelected(false);
    	connection.setSelected(false);
    	pcbline.setSelected(false);
    	pcbpad.setSelected(false);
    	
    	switch (s) {
    		case CircuitPanel.NONE:
    			break;
    		
    		case CircuitPanel.SELECTION:
    			selection.setSelected(true);
    			break;
    			
    		case CircuitPanel.ZOOM:
    			zoom.setSelected(true);
    			break;
    			
    		case CircuitPanel.HAND:
    			hand.setSelected(true);
    			break;
    			
    		case CircuitPanel.LINE:
    			line.setSelected(true);
    			break;
    			
    		case CircuitPanel.TEXT:
    			advtext.setSelected(true);
    			break;
    			
    		case CircuitPanel.BEZIER:
    			bezier.setSelected(true);
    			break;
    			
    		case CircuitPanel.POLYGON:
    			polygon.setSelected(true);
    			break;
    			
    		case CircuitPanel.ELLIPSE:
    			ellipse.setSelected(true);
    			break;
    			
    		case CircuitPanel.RECTANGLE:
    			rectangle.setSelected(true);
    			break;
    			
    		case CircuitPanel.CONNECTION:
    			connection.setSelected(true);
    			break;
    			
    		case CircuitPanel.PCB_LINE:
    			pcbline.setSelected(true);
    			break;
    			
    		case CircuitPanel.PCB_PAD:
    			pcbpad.setSelected(true);
    			break;
    			
    		case CircuitPanel.MACRO: 
    			break;
    			
    	}
    }
    
    /** Add a selection listener (object implementing the ChangeSelection 
    	interface) whose change method will be called when the current
    	selected action should be changed.
    
    */
    public void addSelectionListener(ChangeSelectionListener c)
    {
    	selectionListener=c;
    }
    
    
    public void actionPerformed(ActionEvent evt)
    {
        String s = evt.getActionCommand();
        int oldsel=selectionListener.getSelectionState();
        int sel=CircuitPanel.NONE;
        JToggleButton actualButton=null;

        if(s.equals("selection")) { 
           
            sel = CircuitPanel.SELECTION;
            actualButton = selection;
        } else
            selection.setSelected(false);
            
        if(s.equals("zoom")) { 
            sel= CircuitPanel.ZOOM;
            actualButton = zoom;
        } else
            zoom.setSelected(false);
        if(s.equals("hand")) { 
            sel= CircuitPanel.HAND;
            actualButton = hand;
        } else
            hand.setSelected(false);
        if(s.equals("line")) { 
            sel= CircuitPanel.LINE;
            actualButton = line;
        } else
            line.setSelected(false);
        if(s.equals("text")) { 
            sel= CircuitPanel.TEXT;
            actualButton = advtext;
        } else
            advtext.setSelected(false);
            
        if(s.equals("bezier")) { 
            sel= CircuitPanel.BEZIER;
            actualButton = bezier;
        } else
            bezier.setSelected(false);
            
        if(s.equals("polygon")) { 
            sel= CircuitPanel.POLYGON;
            actualButton = polygon;
        } else
            polygon.setSelected(false);
        if(s.equals("ellipse")) { 
            sel= CircuitPanel.ELLIPSE;
            actualButton = ellipse;
        } else
            ellipse.setSelected(false);
        
        if(s.equals("rectangle")) { 
            sel= CircuitPanel.RECTANGLE;
            actualButton = rectangle;
        } else
            rectangle.setSelected(false);
        if(s.equals("connection")) { 
            sel= CircuitPanel.CONNECTION;
            actualButton = connection;
        } else
            connection.setSelected(false);
        if(s.equals("pcbline")) { 
            sel= CircuitPanel.PCB_LINE;
            actualButton = pcbline;
        } else
            pcbline.setSelected(false);
        if(s.equals("pcbpad")) { 
            sel= CircuitPanel.PCB_PAD;
            actualButton = pcbpad;
        } else
            pcbpad.setSelected(false);
        if(actualButton!=null) {
            if(oldsel==sel) {
                sel=CircuitPanel.NONE;
                actualButton.setSelected(false);
            } else
                actualButton.setSelected(true);
        }
        selectionListener.setSelectionState(sel,"");
    }
    

}