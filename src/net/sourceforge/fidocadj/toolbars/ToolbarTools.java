package net.sourceforge.fidocadj.toolbars;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import net.sourceforge.fidocadj.circuit.*;
import net.sourceforge.fidocadj.circuit.controllers.ElementsEdtActions;
import net.sourceforge.fidocadj.globals.*;

import java.util.*;
import java.net.*;

/** SWING VERSION.
 ToolbarTools class

 <p>This class allows to add and organise the buttons in the toolbar. Buttons
 are instances of <code>JToggleButton</code>, i.e. they have two states, 
 selected and not selected. To make it easier to add a button, they are defined
 first as a <code>ToolButton</code> ({@link ToolButton}), then they are 
 assigned their variable name, and they are finally added to the toolbar 
 using the appropiate method ({@link #addToolButton(JToggleButton, int)}).</p> 
 
 <p>Once they are added to the toolbar, their action when selected must be 
 defined. They each implement their own <code>ActionListener</code> (inner 
 classes) and <code>actionPerformed</code> methods so that they can each have 
 a different behavior if required.</p> 
 
 <p>When created they are automatically added to an <code>ArrayList</code> (to 
 loop through this list and find the selected button,
 {@link #getSelectedButton()}, this is used in {@link #getSelectionState()}), 
 to a <code>HashMap</code> (to assign and find the <code>CircuitPanel</code> 
 constant of each button, this is used in 
 {@link #setSelectionState(int, String)}) and to a <code>ButtonGroup</code>, 
 so that only one button is selected at a time.</p> 
 
 
 <pre>
 This file is part of FidoCadJ.
 
 FidoCadJ is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 
 FidoCadJ is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU General Public License
 along with FidoCadJ.  If not, see <http://www.gnu.org/licenses/>.
 
 Copyright 2008-2014 by Davide Bucci
 </pre>
 @author Davide Bucci & Jose Emilio Munoz
*/

public class ToolbarTools extends JToolBar implements ChangeSelectionListener
{
    private ChangeSelectionListener selectionListener;
    
    //Instance variable of each button
    private final JToggleButton selection;
    private final JToggleButton zoom;
    private final JToggleButton hand;  
    private final JToggleButton line;
    private final JToggleButton advtext;
    private final JToggleButton bezier;
    private final JToggleButton polygon;
    private final JToggleButton ellipse;
    private final JToggleButton complexcurve;
    private final JToggleButton rectangle;
    private final JToggleButton connection;    
    private final JToggleButton pcbline;
    private final JToggleButton pcbpad; 
    
    private static String base;
    private static boolean showText;
    
    private final ButtonGroup group;
    private final ArrayList<JToggleButton> toolButtonsList;
    private final HashMap<JToggleButton, Integer> circuitPanelConstants;
    
    /** <code>base</code> is passed to the <code>ToolbarTools</code> 
     	constructor to create the toolbar, but will need to be accessed by the 
     	<code>ToolButton</code> class to create each button.
     
     	@return base    
    */
    public static String getBase() 
    {
        return base;
    }
    
    /** <code>showText</code> is passed to the <code>ToolbarTools</code> 
     	constructor to create the toolbar, but will need to be accessed by the 
     	<code>ToolButton</code> class to create each button.
     
     	@return showText
     */
    
    public static boolean getShowText() 
    {
        return showText;
    }
    
    /** This method effectively adds the defined button to the toolbar.
     
     	@param button - Name of the button to be added to the toolbar.
     	@param circuitPanelConstant - Determines its function, see 
     	<code>circuitPanel</code> class.
    */
    public void addToolButton(JToggleButton button, int circuitPanelConstant) 
    {
        add(button);
        group.add(button);
        toolButtonsList.add(button);
        circuitPanelConstants.put(button, 
        	Integer.valueOf(circuitPanelConstant));
    }
    
    /** Class Constructor 
     	Creates the toolbar, consisting of all the buttons, which are displayed
     	from left to right, in the order they were added.
     
     	@param showText - True if the name of the tool is to be displayed 
     	underneath the icon.
     	@param smallIcons - True if 16x16 size icons are to be displayed.
    */
    public ToolbarTools (boolean showText, boolean smallIcons) 
    {
        base = smallIcons ? "icons16/" : "icons32/";
        this.showText = showText;
        
        putClientProperty("Quaqua.ToolBar.style", "title");
        
        setBorderPainted(false);
        
        group = new ButtonGroup();
        toolButtonsList = new ArrayList<JToggleButton>();
        circuitPanelConstants = new HashMap<JToggleButton, Integer>();
        
        /**
           First button to be added. Firstly a ToolButton object is created by
           defining an icon image, the text displaying the name of the tool,
           the button ActionCommand, and the tool description/tip. Then it is
           assigned to the appropriate instance variable using the
           ToolButton.getToolButton() method. Finally button behavior is
           defined. As the button circuitPanel constant was already defined
           when adding the button, the appropriate constant is now fetched from
           the circuitPanelConstants HashMap.
        */
        
        ToolButton selectionToolButton = new ToolButton("arrow.png", 
                                                        "Selection", 
                                                        "selection", 
                                                        "tooltip_selection");
        selection = selectionToolButton.getToolButton();
        addToolButton(selection, ElementsEdtActions.SELECTION);
        
        selection.addActionListener(new ActionListener() {                                                         
            public void actionPerformed(ActionEvent ev)               
            {   
                Integer circuitPanelConstantInteger = 
                    (Integer)(circuitPanelConstants.get(selection));
                int circuitPanelConstant = circuitPanelConstantInteger
                    .intValue();
                selectionListener.
                setSelectionState(circuitPanelConstant,"");
            }                                                        
        });   
        /* End of button definition. */
        
        ToolButton zoomToolButton = new ToolButton("magnifier.png", 
                                                   "Zoom_p", 
                                                   "zoom", 
                                                   "tooltip_zoom");
        zoom = zoomToolButton.getToolButton();
        addToolButton(zoom, ElementsEdtActions.ZOOM);
        
        zoom.addActionListener(new ActionListener() {                                                         
            public void actionPerformed(ActionEvent ev)               
            {                     
                Integer circuitPanelConstantInteger = (Integer)
                    (circuitPanelConstants.get(zoom));
                int circuitPanelConstant = circuitPanelConstantInteger
                    .intValue();                                   
                selectionListener.
                setSelectionState(circuitPanelConstant,"");
            }                                                        
        });   
        
        ToolButton handToolButton = new ToolButton("move.png", 
                                                   "Hand", 
                                                   "hand", 
                                                   "tooltip_hand");
        hand = handToolButton.getToolButton();
        addToolButton(hand, ElementsEdtActions.HAND);
        
        hand.addActionListener(new ActionListener() {                                                         
            public void actionPerformed(ActionEvent ev)               
            {                                                        
                Integer circuitPanelConstantInteger = (Integer)
                    (circuitPanelConstants.get(hand));
                int circuitPanelConstant = circuitPanelConstantInteger
                    .intValue();                                   
                selectionListener.
                setSelectionState(circuitPanelConstant,"");
            }                                                        
        });  
        
        ToolButton lineToolButton = new ToolButton("line.png", 
                                                   "Line", 
                                                   "line", 
                                                   "tooltip_line");
        line = lineToolButton.getToolButton();
        addToolButton(line, ElementsEdtActions.LINE);
        
        line.addActionListener(new ActionListener() {                                                         
            public void actionPerformed(ActionEvent ev)               
            {                                                        
                Integer circuitPanelConstantInteger = (Integer)
                    (circuitPanelConstants.get(line));
                int circuitPanelConstant = circuitPanelConstantInteger
                    .intValue();                                   
                selectionListener.
                setSelectionState(circuitPanelConstant,"");
            }                                                        
        });  
        
        ToolButton advtextToolButton = new ToolButton("text.png", 
                                                      "Text", 
                                                      "text", 
                                                      "tooltip_text");
        advtext = advtextToolButton.getToolButton();
        addToolButton(advtext, ElementsEdtActions.TEXT);
        
        advtext.addActionListener(new ActionListener() {                                                         
            public void actionPerformed(ActionEvent ev)               
            {                                                        
                Integer circuitPanelConstantInteger = (Integer)
                    (circuitPanelConstants.get(advtext));
                int circuitPanelConstant = circuitPanelConstantInteger
                    .intValue();                                   
                selectionListener.
                setSelectionState(circuitPanelConstant,"");
            }                                                        
        });  
        
        ToolButton bezierToolButton = new ToolButton("bezier.png", 
                                                     "Bezier", 
                                                     "bezier", 
                                                     "tooltip_bezier");
        bezier = bezierToolButton.getToolButton();
        addToolButton(bezier, ElementsEdtActions.BEZIER);
        
        bezier.addActionListener(new ActionListener() {                                                         
            public void actionPerformed(ActionEvent ev)               
            {                                                        
                Integer circuitPanelConstantInteger = (Integer)
                    (circuitPanelConstants.get(bezier));
                int circuitPanelConstant = circuitPanelConstantInteger
                    .intValue();                                   
                selectionListener.
                setSelectionState(circuitPanelConstant,"");
            }                                                        
        });  
        
        ToolButton polygonToolButton = new ToolButton("polygon.png", 
                                                      "Polygon", 
                                                      "polygon", 
                                                      "tooltip_polygon");
        polygon = polygonToolButton.getToolButton();
        addToolButton(polygon, ElementsEdtActions.POLYGON);
        
        polygon.addActionListener(new ActionListener() {                                                         
            public void actionPerformed(ActionEvent ev)               
            {                                                        
                Integer circuitPanelConstantInteger = (Integer)
                    (circuitPanelConstants.get(polygon));
                int circuitPanelConstant = circuitPanelConstantInteger
                    .intValue();                                   
                selectionListener.
                setSelectionState(circuitPanelConstant,"");
            }                                                        
        });  
        
        // TODO: add the description!
        ToolButton complexCurveToolButton = new ToolButton("complexcurve.png", 
                                                      "Complexcurve", 
                                                      "complexcurve", 
                                                      "tooltip_curve");
        complexcurve = complexCurveToolButton.getToolButton();
        addToolButton(complexcurve, ElementsEdtActions.COMPLEXCURVE);
        
        complexcurve.addActionListener(new ActionListener() {                                                         
            public void actionPerformed(ActionEvent ev)               
            {                                                        
                Integer circuitPanelConstantInteger = (Integer)
                    (circuitPanelConstants.get(complexcurve));
                int circuitPanelConstant = circuitPanelConstantInteger
                    .intValue();                                   
                selectionListener.
                setSelectionState(circuitPanelConstant,"");
            }                                                        
        });  
        
        ToolButton ellipseToolButton = new ToolButton("ellipse.png", 
                                                      "Ellipse", 
                                                      "ellipse", 
                                                      "tooltip_ellipse");
        ellipse = ellipseToolButton.getToolButton();
        addToolButton(ellipse, ElementsEdtActions.ELLIPSE);
        
        ellipse.addActionListener(new ActionListener() {                                                         
            public void actionPerformed(ActionEvent ev)               
            {                                                        
                Integer circuitPanelConstantInteger = (Integer)
                    (circuitPanelConstants.get(ellipse));
                int circuitPanelConstant = circuitPanelConstantInteger
                    .intValue();                                   
                selectionListener.
                setSelectionState(circuitPanelConstant,"");
            }                                                        
        });  
        
        ToolButton rectangleToolButton = new ToolButton("rectangle.png", 
                                                        "Rectangle", 
                                                        "rectangle", 
                                                        "tooltip_rectangle");
        rectangle = rectangleToolButton.getToolButton();
        addToolButton(rectangle, ElementsEdtActions.RECTANGLE);
        
        rectangle.addActionListener(new ActionListener() {                                                         
            public void actionPerformed(ActionEvent ev)               
            {                                                        
                Integer circuitPanelConstantInteger = (Integer)
                    (circuitPanelConstants.get(rectangle));
                int circuitPanelConstant = circuitPanelConstantInteger
                    .intValue();                                   
                selectionListener.
                setSelectionState(circuitPanelConstant,"");
            }                                                        
        });
        
        ToolButton connectionToolButton = new ToolButton("connection.png", 
                                                         "Connection", 
                                                         "connection", 
                                                         "tooltip_connection");
        connection = connectionToolButton.getToolButton();
        addToolButton(connection, ElementsEdtActions.CONNECTION);
        
        connection.addActionListener(new ActionListener() {                                                         
            public void actionPerformed(ActionEvent ev)               
            {                                                        
                Integer circuitPanelConstantInteger = (Integer)
                    (circuitPanelConstants.get(connection));
                int circuitPanelConstant = circuitPanelConstantInteger
                    .intValue();                                   
                selectionListener.
                setSelectionState(circuitPanelConstant,"");
            }                                                        
        });  
        
        ToolButton pcblineToolButton = new ToolButton("pcbline.png", 
                                                      "PCBline", 
                                                      "pcbline", 
                                                      "tooltip_pcbline");
        pcbline = pcblineToolButton.getToolButton();
        addToolButton(pcbline, ElementsEdtActions.PCB_LINE);
        
        pcbline.addActionListener(new ActionListener() {                                                         
            public void actionPerformed(ActionEvent ev)               
            {                                                        
                Integer circuitPanelConstantInteger = (Integer)
                    (circuitPanelConstants.get(pcbline));
                int circuitPanelConstant = circuitPanelConstantInteger
                    .intValue();                                   
                selectionListener.
                setSelectionState(circuitPanelConstant,"");
            }                                                        
        });  
        
        ToolButton pcbpadToolButton = new ToolButton("pcbpad.png", 
                                                     "PCBpad", 
                                                     "pcbpad", 
                                                     "tooltip_pcbpad");
        pcbpad = pcbpadToolButton.getToolButton();
        addToolButton(pcbpad, ElementsEdtActions.PCB_PAD);
        
        pcbpad.addActionListener(new ActionListener() {                                                         
            public void actionPerformed(ActionEvent ev)               
            {                                                        
                Integer circuitPanelConstantInteger = (Integer)
                    (circuitPanelConstants.get(pcbpad));
                int circuitPanelConstant = circuitPanelConstantInteger
                    .intValue();                                   
                selectionListener.
                setSelectionState(circuitPanelConstant,"");
            }                                                        
        });  
        
        
        add(Box.createGlue());  
        
        setFloatable(false);
        setRollover(true);
        
    }
    
    /** Add a selection listener (object implementing the ChangeSelection 
     	interface) whose change method will be called when the current
     	selected action should be changed.
    */
    
    public void addSelectionListener(ChangeSelectionListener c)
    {
        selectionListener=c;
    }
    
    
    /** This method finds the button selected at the moment.
     
     	@return selectedButton
    */
    
    public JToggleButton getSelectedButton()
    {
        JToggleButton selectedButton = null;
        for(int i=0;i<toolButtonsList.size();i++) {
            JToggleButton button = (JToggleButton) toolButtonsList.get(i);
            if (button.isSelected()) {
                selectedButton = button;
            }
        }
        return selectedButton;
    }
    
    /** Get the current selection state. Required for implementing the
     	ChangeSelectionListener interface.
     
     	@return the actual selection state (see the CircuitPanel class for the
     	definition of the constants used here).
    */
    
    public int getSelectionState()
    {
        JToggleButton selectedButton = getSelectedButton();
        if(selectedButton==null) {
        	// No button is selected. There is a specific state for that.
        	return ElementsEdtActions.NONE;
        } else {
            Integer circuitPanelConstantInteger = 
                (Integer)(circuitPanelConstants.get(selectedButton));
            return circuitPanelConstantInteger.intValue();
        }
    }
    
   	/** Set if the strict FidoCAD compatibility mode is active
   		@param strict true if the compatibility with FidoCAD should be 
   		obtained.
   	
   	*/
    public void setStrictCompatibility(boolean strict)
    {
    	complexcurve.setEnabled(!strict);
    }
    
    /** Set the current selection state. Required for implementing the
     	ChangeSelectionListener interface
     
     	@param s the selection state (see the CircuitPanel class for the
     	definition of the constants used here).
     	@param m not used here (useful when playing with macros).
    */
    
    public void setSelectionState(int s, String m)
    {
        /* I think this code is not useful
        for(int i=0; i<toolButtonsList.size(); ++i) {
            JToggleButton button = (JToggleButton) toolButtonsList.get(i);
            button.setSelected(false);
        }*/
        for(int i=0; i<toolButtonsList.size(); ++i) {
            if(s == ElementsEdtActions.NONE || s == ElementsEdtActions.MACRO)
            	break;
            JToggleButton button = (JToggleButton) toolButtonsList.get(i);
            Integer circuitPanelConstantInteger = 
                (Integer)(circuitPanelConstants.get(button));
            int circuitPanelConstant = circuitPanelConstantInteger.intValue();
            if(s == circuitPanelConstant) 
                button.setSelected(true);
        }  
    }
}
