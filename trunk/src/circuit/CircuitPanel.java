package circuit;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;



import globals.*;
import geom.*;
import dialogs.*;
import primitives.*;
import timer.*;
import toolbars.*;
import layers.*;
import clipboard.*;


/** Circuit panel: draw the circuit inside this panel. This is one of the most 
    important components, as it is responsible of all editing actions.
    In many ways, this class contains the 
    
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

    Copyright 2007-2012 by Davide Bucci
</pre>
   The circuit panel will contain the whole drawing.
    This class is able to perform its profiling, which is in particular
    the measurement of the time needed to draw the circuit.
    
    @author Davide Bucci
*/
public class CircuitPanel extends JPanel implements ActionListener,
											 MouseMotionListener,
                                             MouseListener,
                                             ChangeSelectedLayer,
                                             ChangeGridState,
                                             ChangeZoomListener,
                                             ChangeSelectionListener
{ 

	// *********** DRAWING ***********


    // This parsing object is used for normal graphic objects.
    // Maybe, should it be kept private?
    public transient ParseSchem P;
    
    // Coordinate system to be used.
    private transient MapCoordinates cs;
    
	// Default filled state of polygons, rectangles and ovals when they are
	// created
    public boolean isFilled;
    
    // Use anti alias in drawings 
    public boolean antiAlias;  
	
	// Draw the grid
    private boolean isGridVisible;
    
    // Default sizes for PCB elements
    public int PCB_pad_sizex;
    public int PCB_pad_sizey;
    public int PCB_pad_style;  
    public int PCB_pad_drill;
    public int PCB_thickness;

	// Default background color
    private Color backgroundColor;

	// Position of the rectangle used for the selection
    private Rectangle evidenceRect;

	// Margin size in pixels when calculating component sizes.
	public static final int MARGIN=20;     
                                    
	// Color of elements during editing
    static final Color editingColor=Color.green;                                

	// Font to be used to draw the ruler
	private static final String rulerFont = "Lucida Sans Regular";
    
    
	// ********** PROFILING **********

	// Specify that the profiling mode should be activated.
    public boolean profileTime;		
    private double average;
    
    // Record time for the redrawing.
    private double record;			
   
	// Number of times the redraw has occourred.
    private double runs;
    
    // Record time for mouse down handle event in selection.
    private double record_c;		
    
    // Record time for click up event in selection.
    private double record_d;		
    				
   
	// ********** INTERFACE **********
    
    // Track the old mouse coordinates. This is useful during editing
    // See mouseMoved method
    private int oldx;
    private int oldy;
    
    // If this variable is different from null, the component will ensure that
    // the corresponding rectangle will be shown in a scroll panel during the
    // next redraw.
	private Rectangle scrollRectangle;


	// Track wether an editing action is being made.
    private boolean successiveMove;
    
    // Strict FidoCad compatibility
    public boolean extStrict;  
    
     // Settings for macro splitting.
    public boolean splitNonStandardMacro_s;    // split non standard macro
                                                // when saving
    public boolean splitNonStandardMacro_c;    // split non standard macro
                                                // when copying
    
    // Nuber of clicks done when entering an object.
    private int clickNumber;
    
    // TO IMPROVE: this must be synchronized with the value in PrimitivePolygon
    // Maximum number of polygon vertices
    public static final int NPOLY=256;
    
    // Array used to keep track of polygon insertion
    private int[] xpoly;	
    private int[] ypoly;
    
  
    // Tolerance in pixel to select an object
    static final int SEL_TOLERANCE = 10; 
  
  	// Current layer for editing.
    private int currentLayer;

	// editing action being done
    private int actionSelected;      
    
    // used when entering a macro
    private String macroKey;                    

	// The primitive being edited
	private transient GraphicPrimitive primEdit;

    // Selection states
    public static final int NONE = 0;
    public static final int SELECTION = 1;
    public static final int ZOOM = 2;
    public static final int HAND = 3;
    public static final int LINE = 4;
    public static final int TEXT = 5;
    public static final int BEZIER = 6;
    public static final int POLYGON = 7;
    public static final int ELLIPSE = 8;
    public static final int RECTANGLE = 9;
    public static final int CONNECTION = 10;
    public static final int PCB_LINE = 11;
    public static final int PCB_PAD = 12;
    public static final int MACRO = 13;
    public static final int COMPLEXCURVE = 14;
    
    
    // ********** INTERFACE ELEMENTS **********
    
    JPopupMenu popup;
    JMenuItem editCut;
    JMenuItem editCopy;
    JMenuItem editPaste;
    JMenuItem editRotate;
    JMenuItem editMirror;
        
    
	// ********** LISTENERS **********

    private ChangeZoomListener zoomListener;
    private ChangeSelectionListener selectionListener;
    private ChangeSelectionListener scrollGestureSelectionListener;
    private ChangeCoordinatesListener coordinatesListener;


    /** Standard constructor
        @param isEditable indicates whether the panel should be responsible
               to keyboard and mouse inputs.
               
    */
    public CircuitPanel (boolean isEditable) 
    {
        backgroundColor=Color.white; 
        P=new ParseSchem();
        isGridVisible=true;
        zoomListener=null;
        antiAlias = true;
        record = 1e100;
        record_c = record;
        record_d = record;
        evidenceRect = new Rectangle(0,0,-1,-1);
        primEdit = null;
        // Set up the standard view settings:
        // top left corner, 400% zoom. 
        cs = new MapCoordinates();
        cs.setXCenter(0.0);
        cs.setYCenter(0.0);
        cs.setXMagnitude(4.0);  
        cs.setYMagnitude(4.0);  
        cs.setOrientation(0);
        setOpaque(true);
        runs = 0;
        average = 0;
        currentLayer = 0;
        PCB_thickness = 5;
        PCB_pad_sizex=5;
        PCB_pad_sizey=5;
        PCB_pad_drill=2;
        
        xpoly = new int[NPOLY];
        ypoly = new int[NPOLY];
        
        
        splitNonStandardMacro_s= false;
        splitNonStandardMacro_c= false;
        	
        // This is useful when preparing the applet: the circuit panel will
        // not be editable in this case.
        if (isEditable) {
            addMouseListener(this);
            addMouseMotionListener(this);
            setFocusable(true);
            registerActiveKeys();
            //Create the popup menu.
    		popup = new JPopupMenu();
    		editCut = new JMenuItem(Globals.messages.getString("Cut"));
        	editCopy = new JMenuItem(Globals.messages.getString("Copy"));
        	editPaste = new	JMenuItem(Globals.messages.getString("Paste"));
        	editRotate = new JMenuItem(Globals.messages.getString("Rotate"));
    	    editMirror = new JMenuItem(Globals.messages.getString("Mirror_E"));
        
        	popup.add(editCut);
        	popup.add(editCopy);
        	popup.add(editPaste);
        	
        	popup.addSeparator();
        	popup.add(editRotate);
        	popup.add(editMirror);
        	
        	// Adding the action listener
        	
        	editCut.addActionListener(this);
        	editCopy.addActionListener(this);
        	editPaste.addActionListener(this);
        	editRotate.addActionListener(this);
        	editMirror.addActionListener(this);
        	
        }
    }
    
    /**	Register an action involving the editing
    	@param actionString the action name to be associated to this action
    	@param key the key to be used. It will be associated either in
    		lower case as well as in upper case.
    	@param state the wanted state to be used (see definitions INTERFACE).
    */
    private void registerAction(String actionString, char key, final int state)
    {
        
        // We need to make this indipendent to the case. So we start by
        // registering the action for the upper case
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(Character.toUpperCase(key)), 
            actionString);
        // And then we repeat the operation for the lower case
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(Character.toLowerCase(key)), 
            actionString);
        
        getActionMap().put(actionString, new AbstractAction() {
            public void actionPerformed(ActionEvent ignored) {
            	// We now set the new editing state
                setSelectionState(state,"");
                // If we are entering or modifying a primitive or a macro,
                // we should be sure it disappears when the state changes
                primEdit = null;
                repaint();
            }
        });    
            
    }
    
    /** Register a certain number of keyboard actions with an associated 
        meaning:
    <pre>
        [A] or [space]      Selection
        [L]                 Line
        [T]                 Text
        [B]                 Bézier
        [P]                 Polygon
        [O]					Complex curve
        [E]                 Ellipse
        [G]                 Rectangle
        [C]                 Connection
        [I]                 PCB track
        [Z]                 PCB pad
        [ESC]				Exit from current editing action
        [DEL] or [BACKSPC]	Delete the selected objects
    </pre>
    */
    public void registerActiveKeys() 
    {
    	
        registerAction("selection", 'a', SELECTION);
   		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
        	.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,0,false), 
            "selection");
        registerAction("line", 'l', LINE);
        registerAction("text", 't', TEXT);
        registerAction("bezier", 'b', BEZIER);
        registerAction("polygon", 'p', POLYGON);
        registerAction("complexcurve", 'o', COMPLEXCURVE);
        registerAction("ellipse", 'e', ELLIPSE);
        registerAction("rectangle", 'g', RECTANGLE);
        registerAction("connection", 'c', CONNECTION);
        registerAction("pcbline", 'i', PCB_LINE);
        registerAction("pcbpad", 'z', PCB_PAD);

        final String delete = "delete"; 
    
        // Delete key
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke("DELETE"), delete);
    
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke("BACK_SPACE"), delete);
            
        getActionMap().put(delete, new AbstractAction() {
            public void actionPerformed(ActionEvent ignored) {
                P.deleteAllSelected();
                repaint();
            }
        });
    
    
        final String escape = "escape";
        
        // Escape: clear everything
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke("ESCAPE"), escape);
    
        getActionMap().put(escape, new AbstractAction() {
            public void actionPerformed(ActionEvent ignored) {
                if(clickNumber>0){
                	// Here we need to clear the variables which are used 
                	// during the primitive introduction and editing.
                	// see mouseMoved method for details.
                	
                    successiveMove = false;
                    clickNumber = 0;
                    primEdit = null;
                    repaint();
                }
            }
        });
        
        
        final String left = "lleft";
         // left key
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,
            java.awt.event.InputEvent.ALT_MASK,false), left);
                
        getActionMap().put(left, new AbstractAction() {
            public void actionPerformed(ActionEvent ignored) {
                P.moveAllSelected(-1,0);
                repaint();
            }
        });
        final String right = "lright";
         // right key
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,
            java.awt.event.InputEvent.ALT_MASK,false), right);
                
        getActionMap().put(right, new AbstractAction() {
            public void actionPerformed(ActionEvent ignored) {
                P.moveAllSelected(1,0);
                repaint();
            }
        });
        
        final String up = "lup";
         // up key
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_UP,
            java.awt.event.InputEvent.ALT_MASK,false), up);
                
        getActionMap().put(up, new AbstractAction() {
            public void actionPerformed(ActionEvent ignored) {
                P.moveAllSelected(0,-1);
                repaint();
            }
        });
         final String down = "ldown";
         // down key
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,
            java.awt.event.InputEvent.ALT_MASK,false), down);
                
        getActionMap().put(down, new AbstractAction() {
            public void actionPerformed(ActionEvent ignored) {
                P.moveAllSelected(0,1);
                repaint();
            }
        });
        
    }
    
    /** Determine wether the current primitive being added is a macro.
    */
    public boolean isEnteringMacro()
    {
    	return ((primEdit !=null) && (primEdit instanceof PrimitiveMacro));
    }
    
    /** Rotate the macro being edited around its first control point 
    	(90 degrees clockwise rotation).
    */
    public void rotateMacro()
    {
    	if(primEdit instanceof PrimitiveMacro) {
    		primEdit.rotatePrimitive(false, 
    			primEdit.getFirstPoint().x, primEdit.getFirstPoint().y);
    	}
    	
    }
    
    /** Mirror the macro being edited around the x coordinate of the first 
    	control point.
    
    */
    public void mirrorMacro()
    {
    	if(primEdit instanceof PrimitiveMacro) {
    		primEdit.mirrorPrimitive(primEdit.getFirstPoint().x);
    	}
    	
    }
    
    /** ChangeSelectionListener interface implementation 
    	@param s the selection state
    	@param macro the macro key (if applies)
    */    
    public void setSelectionState(int s, String macro)
    {
    	
        if (selectionListener!=null && s!=actionSelected) {
            selectionListener.setSelectionState(s, macro);
            selectionListener.setStrictCompatibility(extStrict);
        }
            
        actionSelected=s;
        
        if (scrollGestureSelectionListener!=null) {
            scrollGestureSelectionListener.setSelectionState(s, 
            	macro);
        }
        
        clickNumber=0;
        successiveMove=false;
        selectCursor(); 
        macroKey=macro;
        
    }
    
    /** Get the current editing action (see the constants defined in this 
    	class)
    
        @return the current editing action
    
    */
    public int getSelectionState()
    {
        return actionSelected;
    }   
    
    /** Set the rectangle which will be shown during the next redraw.
    	@param r the rectangle to show.
    */
    public void setScrollRectangle(Rectangle r)
    {
    	scrollRectangle = r;
    	scrollRectToVisible(r);
    }
    
    /***********************************************************************/
    
    /** Define the listener to be called when the zoom is changed
        @param c the new zoom listener
    
    */
    public void addChangeZoomListener(ChangeZoomListener c)
    {
        zoomListener=c;
    }
    /** Define the listener to be called when the selected action is changed
        @param c the new selection listener
    */
    public void addChangeSelectionListener(ChangeSelectionListener c)
    {
        selectionListener=c;
    }
    
    /** Define the listener to be called when the selected action is changed
        (this is explicitly done for the ScrollGestureSelection)
        @param c the new selection listener
    */
    public void addScrollGestureSelectionListener(ChangeSelectionListener c)
    {
        scrollGestureSelectionListener=c;
    }
    
    /** Define the listener to be called when the coordinates of the mouse 
        cursor are changed
        @param c the new coordinates listener
    */
    public void addChangeCoordinatesListener(ChangeCoordinatesListener c)
    {
        coordinatesListener=c;
    }

    /** Return the current editing layer
        @return the index of the layer
    */
    public int getCurrentLayer()
    {
        return currentLayer;
    }
    
    /** Set the current editing layer
        @param l the wanted layer
    */
    public void setCurrentLayer(int l)
    {
        /* two little checks... */
        if (l<0)
            l=0;
        if (l>=P.getLayers().size())
            l=P.getLayers().size()-1;
            
        currentLayer=l;
    }
    
    /** Sets the circuit.
        @param c the circuit string
    */
    public void setCirc(StringBuffer c)
        throws IOException
    {
        P.parseString(c);
    }
    
    /** Get the circuit in the Fidocad format, without the [FIDOCAD] header
        @param extensions allow for FCJ extensions
        @return the circuit in the Fidocad format
    */
    public StringBuffer getCirc(boolean extensions)
    {
        return P.getText(extensions);
    }
    
    /** Change the current layer state
        @param s the layer to be selected
    */
    public void changeSelectedLayer(int s)
    {
        currentLayer=s;
    }

    /** The callback method which is called when the current grid visibility 
    	has changed. 
        @param v is the wanted grid visibility state
    */
    public void setGridVisibility(boolean v)
    {
        isGridVisible=v;
        repaint();
    }
    
    /** The callback method which is called when the current snap visibility 
    	has changed. 
        @param v is the wanted snap state
    */
    public void setSnapState(boolean v)
    {
        cs.setSnap(v);
    }
    

    /** Called when the mouse is clicked inside the control
        0.23.2: the Java click event is a bit too much restrictive. The mouse
        need to be hold still during the click. This is apparently a problem for
        a number of user. I have thus decided to use the mouse release event
        instead of the complete click.
    */
    public void mouseClicked(MouseEvent evt)
    {
        requestFocusInWindow();       
        if(actionSelected==SELECTION) {
            // Double click shows the Parameters dialog.
            if(evt.getClickCount() >= 2) 
                setPropertiesForPrimitive();
            
        } else {
        	//evidenceRect=null;
        }
    }

	/** Here we analyze and handle the mouse click. The behaviour is 
		different depending on which selection state we are.
	
	*/
    private void handleClick(MouseEvent evt)
    {
    	            
        int x = evt.getX();
        int y = evt.getY();
        requestFocusInWindow(); 
        
        String cmd;
        int i;
        GraphicPrimitive g;

        if(clickNumber>NPOLY-1)
            clickNumber=NPOLY-1;
            
        coordinatesListener.changeInfos("");
	
		// We need to differentiate this case since when we are entering a
		// macro, primEdit contains some useful hints about the orientation
		// and the mirroring
		if (actionSelected !=MACRO) 
        	primEdit = null;
        
        switch(actionSelected) {
        default:
        	break;
        
        // No action: ignore
        case NONE:	
            clickNumber = 0;
            break;
                    
        // Selection state
        case SELECTION:
            clickNumber = 0;
            
            // Double click shows the Parameters dialog.
            if(evt.getClickCount() >= 2) {
                setPropertiesForPrimitive();
                break;
            }
            
            // Show a pop up menu if the user does a right-click
            if(evt.getButton() == MouseEvent.BUTTON3) {
            	boolean s=false;
            	// A certain number of menu options are applied to selected 
            	// primitives. We therefore check wether are there some 
            	// of them available and in this case we activate what should
            	// be activated in the pop up menu.
            	
            	if(P.getFirstSelectedPrimitive()!=null) 
            		s=true;
            	
            	editCut.setEnabled(s);
            	editCopy.setEnabled(s);
            	editRotate.setEnabled(s);
            	editMirror.setEnabled(s);
            	
            	// We just check if the clipboard is empty. It would be better
            	// to see if there is some FidoCadJ code wich might be pasted
            	
            	TextTransfer textTransfer = new TextTransfer();
            	
            	if(textTransfer.getClipboardContents().equals(""))
            		editPaste.setEnabled(false);
            	else
            		editPaste.setEnabled(true);
            	
            	popup.show(evt.getComponent(), evt.getX(), evt.getY());
                break;
            }
            
            boolean toggle = false;
            
            if(Globals.useMetaForMultipleSelection) {
                toggle = evt.isMetaDown();
            } else {
                toggle = evt.isControlDown();
            }
            
            // Deselect primitives if needed.       

            if(!toggle) 
            	P.deselectAll();
            
    
            // Calculate a reasonable tolerance. If it is too small, we ensure
            // that it is rounded up to 2.
            int toll= cs.unmapXnosnap(x+SEL_TOLERANCE)-
                              cs.unmapXnosnap(x);
            
            if (toll<2) toll=2;
            
             P.selectPrimitive(cs.unmapXnosnap(x), cs.unmapYnosnap(y),
             	toll, toggle);
            break;
        
        // Zoom state
        case ZOOM:      
        //////// TO IMPROVE: should center the viewport
            cs.unmapXsnap(x);
            cs.unmapYsnap(y);
            
            double z=cs.getXMagnitude();
            
            // Click+Meta reduces the zoom
            // Click raises the zoom
            if((evt.getModifiers() & InputEvent.BUTTON3_MASK)==0) 
                z=z*3.0/2.0;
            else
                z=z*2.0/3.0;
            
            // Checking that reasonable limits are not exceeded.
            if(z>20) z=20;
            if(z<.25) z=.25;
            
            z=Math.round(z*100.0)/100.0;
            
            cs.setMagnitudes(z,z);
            repaint();
            
            break;
        
        // Put a connection (easy: just one click is needed)
        case CONNECTION:
            if((evt.getModifiers() & InputEvent.BUTTON3_MASK)==
            	InputEvent.BUTTON3_MASK) {
                selectAndSetProperties(x,y);
                break;
            }
            // Add a connection primitive at the given point.

            g=new PrimitiveConnection(cs.unmapXsnap(x),
                                        cs.unmapYsnap(y), currentLayer);
            g.setMacroFont(P.getTextFont(), P.getTextFontSize());

            P.addPrimitive(g, true, true);

                    
            repaint();
            break;
                    
        // Put a PCB pad (easy: just one click is needed)       
        case PCB_PAD:
            if((evt.getModifiers() & InputEvent.BUTTON3_MASK)==
            	InputEvent.BUTTON3_MASK) {
                selectAndSetProperties(x,y);
                break;
            }
            // Add a PCB pad primitive at the given point
            g=new PrimitivePCBPad(cs.unmapXsnap(x),
                                  cs.unmapYsnap(y), 
                                  PCB_pad_sizex,
                                  PCB_pad_sizey,                                                                                                                
                                  PCB_pad_drill,
                                  PCB_pad_style,
                                  currentLayer);
            g.setMacroFont(P.getTextFont(), P.getTextFontSize());

            P.addPrimitive(g, true, true);
            repaint();
            break;     
         
        // Add a line: two clicks needed
        case LINE:
            if((evt.getModifiers() & InputEvent.BUTTON3_MASK)!=0 && 
                clickNumber == 0) {
                selectAndSetProperties(x,y);
                break;
            } 
         
            ++ clickNumber;
            if (evt.getClickCount() >= 2) {
                clickNumber = 0;
                break;
            }
            
            successiveMove=false;
            // clickNumber == 0 means that no line is being drawn
            
            	
            xpoly[clickNumber] = cs.unmapXsnap(x);
            ypoly[clickNumber] = cs.unmapYsnap(y);
            if (clickNumber == 2 || (evt.getModifiers() & 
            	InputEvent.BUTTON3_MASK)!=0) {
            	// Here we know the two points needed for creating
            	// the line. The object is thus added to the database.
            	g= new PrimitiveLine(xpoly[1],
                                                         ypoly[1],
                                                         xpoly[2],
                                                         ypoly[2],
                                                         currentLayer,
                                                         false,
                                                        false,
                                                        0,3,2,0);
                g.setMacroFont(P.getTextFont(), P.getTextFontSize());

                P.addPrimitive(g, true, true);
                        
                if((evt.getModifiers() & InputEvent.BUTTON3_MASK)==0) {
               	 	clickNumber = 1;
                	xpoly[1] = xpoly[2];
                	ypoly[1] = ypoly[2];
                } else
                	clickNumber = 0;
                repaint();
                              
            }
            break; 
            
        // Add a text line: just one click is needed
        case TEXT:
            if((evt.getModifiers() & InputEvent.BUTTON3_MASK)!=0) {
                selectAndSetProperties(x,y);
                break;
            }
            if (evt.getClickCount() >= 2) {
                selectAndSetProperties(x,y);
                break;
            }
            PrimitiveAdvText newtext = new PrimitiveAdvText(cs.unmapXsnap(x),
                                        cs.unmapYsnap(y), 
                                        3,4,P.getTextFont(),0,0,
                                        "String", currentLayer);
            P.deselectAll();
            P.addPrimitive(newtext, true, true);
            newtext.setSelected(true);
            repaint();
            setPropertiesForPrimitive();
            
            break;
        
        // Add a Bézier polygonal curve: we need four clicks.
        case BEZIER:
            if((evt.getModifiers() & InputEvent.BUTTON3_MASK)!=0 && 
                clickNumber == 0) {
                selectAndSetProperties(x,y);
                break;
            } else if((evt.getModifiers() & InputEvent.BUTTON3_MASK)!=0) {
                clickNumber = 0;
                repaint();
                break;
            }
            ++ clickNumber;
            if(clickNumber<=2) successiveMove=false;
                    
            // clickNumber == 0 means that no bezier is being drawn
                             
            xpoly[clickNumber] = cs.unmapXsnap(x);
            ypoly[clickNumber] = cs.unmapYsnap(y);
            // a polygon definition is ended with a double click
            if (clickNumber == 4) {
            	g=new PrimitiveBezier(xpoly[1],
                                         ypoly[1],
                                         xpoly[2],
                                         ypoly[2],
                                         xpoly[3],
                                         ypoly[3],
                                         xpoly[4],
                                         ypoly[4],
                                         currentLayer,
                                         false,
                                         false,
                                         0,3,2,0);
                g.setMacroFont(P.getTextFont(), P.getTextFontSize());

                P.addPrimitive(g, true, true);
        
                clickNumber = 0;
                repaint();
            }
            break;   
        
        // Insert a polygon: continue until double click.
        case POLYGON:
            if((evt.getModifiers() & InputEvent.BUTTON3_MASK)!=0 && 
                clickNumber == 0) {
                selectAndSetProperties(x,y);
                break;
            }      
            
            // a polygon definition is ended with a double click
            if (evt.getClickCount() >= 2) {
         
                PrimitivePolygon poly=new PrimitivePolygon(isFilled,
                                         currentLayer,0);
                for(i=1; i<=clickNumber; ++i) 
                    poly.addPoint(xpoly[i],ypoly[i]);
        
                poly.setMacroFont(P.getTextFont(), P.getTextFontSize());

                P.addPrimitive(poly, true,true);
                clickNumber = 0;
                repaint();
                break;
            }
            ++ clickNumber;
            if(clickNumber<=2) successiveMove=false;
            // clickNumber == 0 means that no polygon is being drawn
			// prevent that we exceed the number of allowed points
            if (clickNumber==NPOLY)
            	return;
			// prevent that we exceed the number of allowed points
            if (clickNumber==NPOLY)
            	return;            
            xpoly[clickNumber] = cs.unmapXsnap(x);
            ypoly[clickNumber] = cs.unmapYsnap(y);
            break;   
        
        // Insert a complex curve: continue until double click.
        case COMPLEXCURVE:
            if((evt.getModifiers() & InputEvent.BUTTON3_MASK)!=0 && 
                clickNumber == 0) {
                selectAndSetProperties(x,y);
                break;
            }      
            
            // a polygon definition is ended with a double click
            if (evt.getClickCount() >= 2) {
         
                PrimitiveComplexCurve compc=new PrimitiveComplexCurve(isFilled,
                						 false,
                                         currentLayer,
            							false, false, 0, 3, 2, 0);
                for(i=1; i<=clickNumber; ++i) 
                    compc.addPoint(xpoly[i],ypoly[i]);
        
                compc.setMacroFont(P.getTextFont(), P.getTextFontSize());

                P.addPrimitive(compc, true,true);
                clickNumber = 0;
                repaint();
                break;
            }
            ++ clickNumber;
            if(clickNumber<=2) successiveMove=false;
            // prevent that we exceed the number of allowed points
            if (clickNumber==NPOLY)
            	return;
            // clickNumber == 0 means that no polygon is being drawn
            xpoly[clickNumber] = cs.unmapXsnap(x);
            ypoly[clickNumber] = cs.unmapYsnap(y);
            break;   
        
        
        // Enter an ellipse: two clicks needed
        case ELLIPSE:
        
            if((evt.getModifiers() & InputEvent.BUTTON3_MASK)!=0 && 
                clickNumber == 0) {
                selectAndSetProperties(x,y);
                break;
            }
            
            // If control is hold, trace a circle
            if(evt.isControlDown()&&clickNumber>0) {
                y=cs.mapY(xpoly[1],ypoly[1])+(x-cs.mapX(xpoly[1],ypoly[1]));
            }
            ++ clickNumber;
            successiveMove=false;
            // clickNumber == 0 means that no ellipse is being drawn
            
            xpoly[clickNumber] = cs.unmapXsnap(x);
            ypoly[clickNumber] = cs.unmapYsnap(y);
            if (clickNumber == 2) {
            	g=new PrimitiveOval(xpoly[1],
                                         ypoly[1],
                                         xpoly[2],
                                         ypoly[2],
                                         isFilled,
                                         currentLayer,0);
                g.setMacroFont(P.getTextFont(), P.getTextFontSize());

                P.addPrimitive(g, true, true);
        
        
                clickNumber = 0;
                repaint();
              
            }
            break;   
        
        // Enter a rectangle: two clicks needed
        case RECTANGLE:
            if((evt.getModifiers() & InputEvent.BUTTON3_MASK)!=0 && 
                clickNumber == 0) {
                selectAndSetProperties(x,y);
                break;
            }
            // If control is hold, trace a square
            if(evt.isControlDown()&&clickNumber>0) {
                y=cs.mapY(xpoly[1],ypoly[1])+(x-cs.mapX(xpoly[1],ypoly[1]));
            }
            ++ clickNumber;
            successiveMove=false;
            // clickNumber == 0 means that no rectangle is being drawn
           
            xpoly[clickNumber] = cs.unmapXsnap(x);
            ypoly[clickNumber] = cs.unmapYsnap(y);
            if (clickNumber == 2) {
                // The second click ends the rectangle introduction.
                // We thus create the primitive and store it.
                g=new PrimitiveRectangle(xpoly[1],
                                         ypoly[1],
                                         xpoly[2],
                                         ypoly[2],
                                         isFilled,
                                         currentLayer,0);
                g.setMacroFont(P.getTextFont(), P.getTextFontSize());

                P.addPrimitive(g, true, true);
                clickNumber = 0;
                repaint();
              
            }
            if (clickNumber>=2) clickNumber = 0;
            break;   
            
        // Insert a PCB line: two clicks needed.      
        case PCB_LINE:
            if((evt.getModifiers() & InputEvent.BUTTON3_MASK)!=0 && 
                clickNumber == 0) {
                selectAndSetProperties(x,y);
                break;
            }
            ++ clickNumber;
            if (evt.getClickCount() >= 2) {
                clickNumber = 0;
                break;
            }
            successiveMove=false;
            // clickNumber == 0 means that no pcb line is being drawn
            
            xpoly[clickNumber] = cs.unmapXsnap(x);
            ypoly[clickNumber] = cs.unmapYsnap(y);
            if (clickNumber == 2|| (evt.getModifiers() & 
            	InputEvent.BUTTON3_MASK)!=0) {
            	// Here is the end of the PCB line introduction: we create the
            	// primitive.
            	g=new PrimitivePCBLine(xpoly[1],
                                         ypoly[1],
                                         xpoly[2],
                                         ypoly[2],
                                         PCB_thickness,
                                         currentLayer);
                g.setMacroFont(P.getTextFont(), P.getTextFontSize());
                P.addPrimitive(g, true,true);
                
                // Check if the user has clicked with the right button.
                if((evt.getModifiers() & InputEvent.BUTTON3_MASK)==0) {
                	// We then make sort that a new PCB line will be beginning
                	// exactly at the same coordinates at which the previous 
                	// one was stopped.
                	clickNumber = 1;
                	xpoly[1] = xpoly[2];
                	ypoly[1] = ypoly[2];
                } else
                	// We stop the PCB line here
                	clickNumber = 0;
                repaint();  
            }
            break;  
            
        // Enter a macro: just one click is needed.
        case MACRO:
            successiveMove=false;
            if((evt.getModifiers() & InputEvent.BUTTON3_MASK)!=0) {
                selectAndSetProperties(x,y);
                break;
            }
            try {
            	// Here we add a macro. There is a remote risk that the macro
            	// we are inserting contains an error. This is not something
            	// which would happen frequently, since if the macro is in the
            	// library this means it is available, but we need to use
            	// the block try anyway.
            	
                P.deselectAll();
                
                int orientation = 0;
                boolean mirror = false;
            	
            	if (primEdit instanceof PrimitiveMacro)  {
            		orientation = ((PrimitiveMacro)primEdit).getOrientation();
					mirror = ((PrimitiveMacro)primEdit).isMirrored();
            	}
                P.addPrimitive(new PrimitiveMacro(P.getLibrary(), 
                    P.getLayers(), cs.unmapXsnap(x),
                    cs.unmapYsnap(y),macroKey,"", cs.unmapXsnap(x)+10,
                    cs.unmapYsnap(y)+5, "", cs.unmapXsnap(x)+10,
                    cs.unmapYsnap(y)+10,
                    P.getTextFont(),
                    P.getTextFontSize(), orientation, mirror), true,true);
                primEdit=null;
                successiveMove=false;
                    
            } catch (IOException G) {
            	// A simple error message on the console will be enough
                System.out.println(G);
            }
                    
            repaint();
            break;
        }    
    }    
    
    /** Handle the mouse movements when editing a graphic primitive.
        This procedure is important since it is used to show interactively 
        to the user which element is being modified.
    */
    public void mouseMoved(MouseEvent evt)
    {
        int xa=evt.getX();
        int ya=evt.getY();
 
        /*  Important note: the technique used here is always the XOR 
            combination as a toggle.
        */
 
        // This transformation/antitrasformation is useful to take care
        // of the snapping
        int x=cs.mapX(cs.unmapXsnap(xa),0);
        int y=cs.mapY(0,cs.unmapYsnap(ya));
        if (coordinatesListener!=null)
            coordinatesListener.changeCoordinates(
                cs.unmapXsnap(xa),
                cs.unmapYsnap(ya));
        
        // If no action is needed, exit immediately.
        if(x==oldx && y==oldy)
            return;


		// This code follows an old convention and is not optimally handled in
		// modern graphic environments. In the development of FidoCadJ, I am
		// progressively switching from primitives directly drawn from here, 
		// to a cleaner solution in which all the drawing is done exclusively
		// in the paintComponent event. Te result is double buffered and is
		// much less prone to flickering.
		
        Graphics g = getGraphics();
        
//        Graphics2D g2d = (Graphics2D)g;
        
        // This is the newer code: if primEdit is different from null, it will
        // be drawn in the paintComponent event
        // We need to differentiate this case since when we are entering a
		// macro, primEdit contains some useful hints about the orientation
		// and the mirroring
        if (actionSelected !=MACRO) 
        	primEdit = null;
        else if(primEdit!=null) {
        	// This prevents that the R and S keys are sent to the search field
        	// if it has the focus (this happens when the user has found 
        	// something in the libraries with it).
  	    	requestFocusInWindow();
        }
        
        /*  MACRO ***********************************************************
            
                #       #
                 #     #
                #########
               ## ##### ##
              #############
             ###############
             # ########### #
             # #         # #
                #### ####
        */
        if (actionSelected == MACRO) {
            try {
                int orientation = 0;
                boolean mirror = false;
            	
            	if (primEdit instanceof PrimitiveMacro)  {
            		orientation = ((PrimitiveMacro)primEdit).getOrientation();
					mirror = ((PrimitiveMacro)primEdit).isMirrored();
            	}
            	
            	PrimitiveMacro n = new PrimitiveMacro(P.getLibrary(), 
                    createEditingLayerArray(), cs.unmapXsnap(x),
                    cs.unmapYsnap(y),macroKey,"", cs.unmapXsnap(x)+10,
                    cs.unmapYsnap(y)+5, "", cs.unmapXsnap(x)+10,
                    cs.unmapYsnap(y)+10,
                    P.getTextFont(),
                    P.getTextFontSize(), orientation, mirror);
                n.setDrawOnlyLayer(-1);
				primEdit = n;
                repaint();
                successiveMove = true;

            } catch (IOException E) {
                // Here we do not do nothing.
            }
        }    

        
        if (clickNumber == 0) {
            g.dispose();   
            oldx=x;
            oldy=y;
            return;
        }
                
        /*  LINE **************************************************************
                
               ++
                 ** 
                   **
                     **
                       **
                         ++
                        
        */
        if (actionSelected == LINE) {
     
     		primEdit = new PrimitiveLine(xpoly[1], 
            	ypoly[1], cs.unmapXsnap(x), cs.unmapYsnap(y), 0,
            	false, false, 0, 3, 2, 0);
            
            repaint();
            successiveMove = true;  
                   
            if (coordinatesListener!=null){
                double w = Math.sqrt((xpoly[1]-
                	cs.unmapXsnap(xa))*
                    (xpoly[1]-cs.unmapXsnap(xa))+
                    (ypoly[1]-cs.unmapYsnap(ya))*
                    (ypoly[1]-cs.unmapYsnap(ya)));
                double wmm = w*127/1000;
                coordinatesListener.changeInfos(
                    Globals.messages.getString("length")+roundTo(w,2)+
                    " ("+roundTo(wmm,2)+" mm)");
                
            }   
        

        }
        /*  PCBLINE ***********************************************************
            
               ++
                *** 
                  ***
                    ***
                      ***
                        ++
                        
        */
        if (actionSelected == PCB_LINE) {
            primEdit = new PrimitivePCBLine(xpoly[1], 
            	ypoly[1], cs.unmapXsnap(x), cs.unmapYsnap(y), 
            	PCB_thickness, 0);
            
            repaint();
            successiveMove = true;    
            if (coordinatesListener!=null){
                double w = Math.sqrt((xpoly[1]-
                	cs.unmapXsnap(xa))*
                    (xpoly[1]-cs.unmapXsnap(xa))+
                    (ypoly[1]-cs.unmapYsnap(ya))*
                    (ypoly[1]-cs.unmapYsnap(ya)));
                coordinatesListener.changeInfos(
                    Globals.messages.getString("length")+roundTo(w,2));
                
            }   
        
        }
        /*  BEZIER ************************************************************
            
               ++    ******
                 ****      **
                             **
                             **
                           **
                        ++
                        
        */
        if (actionSelected == BEZIER) {
        	// Since we do not know how to fabricate a cubic curve with less
        	// than four points, we use a polygon instead.
        	
            primEdit = new PrimitivePolygon(false, 0, 0);
            
			for(int i=1; i<=clickNumber; ++i)
 				((PrimitivePolygon)primEdit).addPoint(xpoly[i], ypoly[i]);
 			
 			
 			((PrimitivePolygon)primEdit).addPoint(cs.unmapXsnap(x), cs.unmapYsnap(y));

 			repaint();
            successiveMove = true;
    
        }
        /*  POLYGON ***********************************************************
            
               ++             ++
                 ************** 
                   ***********
                     ********
                       *****
                         ++
                        
        */
        if (actionSelected == POLYGON) {
            primEdit = new PrimitivePolygon(false, 0, 0);
            
            
			for(int i=1; i<=clickNumber && i<NPOLY; ++i)
 				((PrimitivePolygon)primEdit).addPoint(xpoly[i], ypoly[i]);
 			
 			
 			((PrimitivePolygon)primEdit).addPoint(cs.unmapXsnap(x), cs.unmapYsnap(y));

 			repaint();
            successiveMove = true;

        }
        
        /*  COMPLEX CURVE ****************************************************
            
               ++             ++
                 *****   ***** 
                   ****************++
               ++***********
                     ****
                      ++
                        
        */
        if (actionSelected == COMPLEXCURVE) {
            primEdit = new PrimitiveComplexCurve(false, false, 0,
            	false, false, 0, 3, 2, 0);
            
			for(int i=1; i<=clickNumber && i<NPOLY; ++i)
 				((PrimitiveComplexCurve)primEdit).addPoint(xpoly[i], ypoly[i]);
 			
 			
 			((PrimitiveComplexCurve)primEdit).addPoint(cs.unmapXsnap(x), 
 				cs.unmapYsnap(y));

 			repaint();
            successiveMove = true;

        }
        
        /*  RECTANGLE *********************************************************
            
               +          +
                **********
                **********
                **********
                **********
               +          +
                        
       */
        if (actionSelected == RECTANGLE) {
        // If control is hold, trace a square
            if(evt.isControlDown()&&clickNumber>0) {
                y=cs.mapY(xpoly[1],ypoly[1])+(x-cs.mapX(xpoly[1],ypoly[1]));
            }
            primEdit = new PrimitiveRectangle(xpoly[1], 
            	ypoly[1], cs.unmapXsnap(x), cs.unmapYsnap(y), 
            	false,	0, 0);
            
            repaint();
            successiveMove = true;
        }
        /*  ELLIPSE ***********************************************************
            
               +
                    ***
                  *******
                 *********
                 *********
                  *******
                    *** 
                           +
                        
        */
        if (actionSelected == ELLIPSE) {
            // If control is hold, trace a circle
            if(evt.isControlDown()&&clickNumber>0) {
                y=cs.mapY(xpoly[1],ypoly[1])+(x-cs.mapX(xpoly[1],ypoly[1]));
            }
               
			primEdit = new PrimitiveOval(xpoly[1], 
            	ypoly[1], cs.unmapXsnap(x), cs.unmapYsnap(y), 
            	false,	0, 0);
             
            repaint();
			successiveMove = true;
        }    


        oldx=x;
        oldy=y;
        g.dispose();
    }
    
    private int rulerStartX;
    private int rulerStartY;
    private int rulerEndX;
    private int rulerEndY;
    private boolean ruler;
    
    /** Mouse interface: dragging operations.
    
    */
    public void mousePressed(MouseEvent evt)
    {
    	MyTimer mt = new MyTimer();
		
        int px=evt.getX();
        int py=evt.getY();
        
        ruler=false;
		rulerStartX = px;
        rulerStartY = py;
        rulerEndX=px;
        rulerEndY=py;
        boolean multiple=evt.isControlDown();
        
        if(Globals.useMetaForMultipleSelection)
            multiple=evt.isMetaDown();
            
        if(actionSelected == SELECTION &&
            (evt.getModifiers() & InputEvent.BUTTON3_MASK)==0 &&
            !evt.isShiftDown()) { 
            P.dragHandleStart(px, py, SEL_TOLERANCE,multiple, cs);
        } else if(actionSelected == SELECTION){ // Right click during selection
            ruler = true;
        }
        
        if(profileTime) {
            double elapsed=mt.getElapsed();
            if(elapsed<record_c) {
                record_c=elapsed;
            }
            System.out.println("MP: Time elapsed: "+elapsed+
               	"; record: "+record_c+" ms");
        }    

    }
    public void mouseDragged(MouseEvent evt)
    {
		MyTimer mt = new MyTimer();
		int px=evt.getX();
        int py=evt.getY();
        Graphics g = getGraphics();
        Graphics2D g2d = (Graphics2D)g;
        
        // Handle the ruler. Basically, we just save the coordinates and
        // we launch a repaint which will be done as soon as possible.
        // No graphical elements are drawn outside a repaint.
        if((evt.getModifiers() & InputEvent.BUTTON3_MASK)!=0 || 
        	evt.isShiftDown()) {
            rulerEndX=px;
            rulerEndY=py;
            repaint();
            return;
        }
               
        P.dragHandleDrag(this, g2d, px, py, cs);
        // A little profiling if necessary. I noticed that time needed for 
        // handling clicks is not negligible in large drawings, hence the
        // need of controlling it.
        
        if(profileTime) {
            double elapsed=mt.getElapsed();
            if(elapsed<record_d) {
                record_d=elapsed;
            }
            System.out.println("MD: Time elapsed: "+elapsed+
               	"; record: "+record_d+" ms");
        }   
    }
    
    /** Mouse release event
    */
    public void mouseReleased(MouseEvent evt)
    {
    	MyTimer mt = new MyTimer();
        int px=evt.getX();
        int py=evt.getY();
        
        boolean multiple=evt.isControlDown();
        
        if(Globals.useMetaForMultipleSelection)
            multiple=evt.isMetaDown();
        
        // If we are in the selection state, either we are ending the editing
        // of an element (and thus the dragging of a handle) or we are 
        // making a click.
        
        if(actionSelected==SELECTION) {
            if(rulerStartX!=px || rulerStartY!=py)
            	P.dragHandleEnd(this,px, py, multiple, cs);
            else {
            	ruler=false;
            	handleClick(evt);
            }
           	repaint();
        } else {
            handleClick(evt);
        }
        
        // Having an idea of the release time is useful for the optimization
        // of the click event handling. The most time-consuming operation 
        // which is done in this phase is finding the closest component to 
        // the mouse pointer and eventually selecting it.
        
        if(profileTime) {
            double elapsed=mt.getElapsed();
            if(elapsed<record_d) {
                record_d=elapsed;
            }
            System.out.println("MR: Time elapsed: "+elapsed+
               	"; record: "+record_d+" ms");
        }    
    }

    public void mouseEntered(MouseEvent evt)
    {
        selectCursor();
    }
    
    /**
    	Define the icon used for the mouse cursor, depending on the current
        editing action.
    */
    public void selectCursor()
    { 
        switch(actionSelected) {
            case NONE:
            case SELECTION:
            default:
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                break;
                
            case ZOOM:
            case HAND:
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                break;
            case LINE:
            case TEXT:
            case BEZIER:
            case POLYGON:
            case COMPLEXCURVE:
            case ELLIPSE:
            case RECTANGLE:
            case CONNECTION:
            case PCB_LINE:
            case PCB_PAD:       
                setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

        }
    }
    
    public void mouseExited(MouseEvent evt)
    {
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        if(successiveMove) {
            successiveMove = false;
            primEdit = null;
            repaint();
        }
    }
    
    /** The zoom listener
    
        @param z the zoom factor to be used
    */
    
    public void changeZoom(double z) 
    {
        z=Math.round(z*100.0)/100.0;
        cs.setMagnitudes(z,z);
        successiveMove=false;
        
        //revalidate();
        repaint();
    }
    
    /** Sets the background color.
        @param sfondo the background color to be used.
    */
    public void setBackground(Color sfondo)
    {
        backgroundColor=sfondo;
    }
    
    
    /** Activate and sets an evidence rectangle which will be put on screen
        at the next redraw. All sizes are given in pixel.
        
        @param lx   the x coordinate of the left top corner
        @param ly   the y coordinate of the left top corner
        @param w    the width of the rectangle
        @param h    the height of the rectangle
    
    */
    public void setEvidenceRect(int lx, int ly, int w, int h)
    {
    	evidenceRect = new Rectangle();
        evidenceRect.x=lx;
        evidenceRect.y=ly;
        evidenceRect.height=h;
        evidenceRect.width=w;        
    }

    /** Repaint the panel */
    public void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        MyTimer mt;
        mt = new MyTimer();
        
        
        Graphics2D g2 = (Graphics2D)g; 
    	g2.setRenderingHint(RenderingHints.KEY_RENDERING, 
                RenderingHints.VALUE_RENDER_SPEED);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, 
                RenderingHints.VALUE_COLOR_RENDER_SPEED);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, 
                RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
                
        // Activate anti-aliasing when necessary.
        
        if (antiAlias) g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
             RenderingHints.VALUE_ANTIALIAS_ON);
        else {
          // Faster graphic (??? true??? I do not think so on modern systems)
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, 
                RenderingHints.VALUE_RENDER_SPEED);
            g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, 
                RenderingHints.VALUE_COLOR_RENDER_SPEED);
            g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, 
                RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
            g2.setRenderingHint(RenderingHints.KEY_DITHERING, 
                RenderingHints.VALUE_DITHER_DISABLE);
         }
     

        // Draw all the primitives.
        g.setColor(backgroundColor);
        g.fillRect(0, 0, getWidth(), getHeight());
        if(isGridVisible){  
            P.drawGrid(g2,cs,0,0,getWidth(),
                              getHeight());
        }
        
        // The standard color is black.
        g.setColor(Color.black);
        
        // Perform the drawing operation.
        P.draw(g2, cs);
       
        if (zoomListener!=null) 
            zoomListener.changeZoom(cs.getXMagnitude());
        
        // Draw the handles of all selected primitives.
        P.drawSelectedHandles(g2, cs);
    
        // If an evidence rectangle is active, draw it.
        
        g.setColor(editingColor);

        g2.setStroke(new BasicStroke(1));

        if(evidenceRect!=null && actionSelected == SELECTION)
        	g.drawRect(evidenceRect.x,evidenceRect.y, evidenceRect.width,   
            	evidenceRect.height);
        else
        	evidenceRect = null;
		
		// If there is a primitive or a macro being edited, draws it.
		drawPrimEdit(g2);


        // If a ruler is active, draw it.
        if (ruler) {    
            drawRuler(g,rulerStartX, rulerStartY, rulerEndX, rulerEndY);
        }
                
       
        if (cs.getXMax()>0 && 
            cs.getYMax()>0){
            setPreferredSize(new Dimension(cs.getXMax()
               +MARGIN,cs.getYMax()+MARGIN));
            revalidate();
        }
        
        if(scrollRectangle!=null) {
        	Rectangle r=scrollRectangle;
        	scrollRectangle = null;
  	   		//scrollRectToVisible(r);
        }
        
        
        // Since the redraw speed is a capital parameter which determines the
        // perceived speed, we monitor it very carefully if the program
        // profiling is active.
        
        if(profileTime) {
            double elapsed=mt.getElapsed();
            g2.drawString("Version: "+
                Globals.version, 0,100);
            g.drawString("Time elapsed: " +
                elapsed+" ms" ,0,50);
            ++runs;
            average += elapsed;
            if(elapsed<record) {
                record=elapsed;
            }
            System.out.println("R: Time elapsed: "+
                elapsed+
                " averaging "+
                average/runs+
                "ms in "+runs+
                " redraws; record: "+record+" ms");
        }   
        
    }
        
    public void validate()
    {
		if (cs.getXMax()>0 && 
            cs.getYMax()>0){
    		setPreferredSize(new Dimension(cs.getXMax()
                +MARGIN,cs.getYMax()+MARGIN));
        }        
        super.validate();
    }
    /** Draws the current editing primitive.
    
    */
    private void drawPrimEdit(Graphics2D g2)
    {
    	if(primEdit!=null) {
    		
			primEdit.draw(g2,cs, createEditingLayerArray());
		}
    }
    
   	/**  Create a fictionous Array List without making use of alpha 
         channels and colours.
         
         @return an Vector composed by Globals.MAX_LAYERS opaque layers in 
         	green.
    */
   	private Vector<LayerDesc> createEditingLayerArray()
    {
          	
       	Vector<LayerDesc> ll=new Vector<LayerDesc>();
       	for(int i=0; i<Globals.MAX_LAYERS;++i) 
       		ll.add(new LayerDesc(editingColor, true,"",1.0f));
       		
       	return ll;
    }
 
    /** Draws a ruler to ease measuring distances.
    
    */
    private void drawRuler(Graphics g, int sx, int sy, int ex, int ey)
    {
        double length;
        //MapCoordinates cs=P.getMapCoordinates();
        
        int xa = cs.unmapXnosnap(sx);
        int ya = cs.unmapYnosnap(sy);
        
        int xb = cs.unmapXnosnap(ex);
        int yb = cs.unmapYnosnap(ey);
        
        int x1, y1, x2, y2;
        double x, y;
        
        // Calculates the ruler length.
        length = Math.sqrt((double)(xa-xb)*(xa-xb)+(ya-yb)*(ya-yb));
        
        
        g.drawLine(sx, sy, ex, ey);
        
//        int tot=(int)length;
        
        // A little bit of trigonometry :-)
        
        double alpha;
        if (sx!=ex)
            alpha = Math.atan((double)(ey-sy)/(double)(ex-sx));
        else
            alpha = Math.PI/2.0+((ey-sy<0)?0:Math.PI);
        
        alpha += (ex-sx>0)?0:Math.PI;
        
        double l = 5.0;
        
        if (cs.getXMagnitude()<1.0) {
            l=10;
        } else if(cs.getXMagnitude() > 5) {
            l=1;
        } else {
            l=5;
        }
        
        
        double ll=0.0;
        double ld=5.0;
        int m = 5;
        int j=0;
        
        double dex = sx + (length*Math.cos(alpha)*cs.getXMagnitude());
        double dey = sy + (length*Math.sin(alpha)*cs.getYMagnitude());
        
        alpha += Math.PI/2.0;
        
        
        
        boolean debut=true;
        
        // Draw the ticks.
        
        for(double i=0; i<=length; i+=l) {
            if (j++==m || debut) {
                j=1;
                ll=2*ld;
                debut=false;
            } else {
                ll=ld;
            }
            x = (dex*i)/length+((double)sx*(length-i))/length;
            y = (dey*i)/length+((double)sy*(length-i))/length;
            
            x1 = (int)(x - ll*Math.cos(alpha));
            x2 = (int)(x + ll*Math.cos(alpha));
            y1 = (int)(y - ll*Math.sin(alpha));
            y2 = (int)(y + ll*Math.sin(alpha));
            
            g.drawLine(x1, y1, x2, y2);

        }
        
        Font f=new Font(rulerFont,Font.PLAIN,10);
        g.setFont(f);
        
        String t1 = roundTo(length,2);
        
        // Remember that one FidoCadJ logical unit is 127 microns.
        String t2 = roundTo(length*.127,2)+" mm";
        
        FontMetrics fm = g.getFontMetrics(f);
//        int h = fm.getAscent();
//        int th = h+fm.getDescent();
        
        // Draw the box at the end, with the measurement results.
        g.setColor(Color.white);
        g.fillRect(ex+10, ey, Math.max(fm.stringWidth(t1),
            fm.stringWidth(t2))+1, 24);
        
        g.setColor(editingColor);
        g.drawRect(ex+9, ey-1, Math.max(fm.stringWidth(t1),
            fm.stringWidth(t2))+2, 25);
        g.setColor(editingColor.darker().darker());
        g.drawString(t1, ex+10, ey+10);
        g.drawString(t2, ex+10, ey+20);
        
    }
    
    /** Sets the default PCB pad size x.
        @param s    the wanted size in logical units.
    */
    public void setPCB_pad_sizex(int s)
    {
        PCB_pad_sizex=s;
    }
    /** Gets the default PCB pad size x.
        @return     the x size in logical units.
    */
    public int getPCB_pad_sizex()
    {
        return PCB_pad_sizex;
    }
    /** Sets the default PCB pad size y.
        @param s    the wanted size in logical units.
    */

    public void setPCB_pad_sizey(int s)
    {
        PCB_pad_sizey=s;
    }
    
    /** Gets the default PCB pad size y.
        @return     the size in logical units.
    */
    
    public int getPCB_pad_sizey()
    {
        return PCB_pad_sizey;
    }
    
    /** Sets the default PCB pad style.
        @param s    the style.
    */
    public void setPCB_pad_style(int s)
    {
        PCB_pad_style=s;  
    }
    
    /** Gets the default PCB pad style.
        @return     the style.
    */
    
    public int getPCB_pad_style()
    {
        return PCB_pad_style;  
    }
    
    
    /** Sets the default PCB pad drill size.
        @param s    the wanted drill size, in logical units.
    */
    public void setPCB_pad_drill(int s)
    {
        PCB_pad_drill=s;
    }
    
    /** Gets the default PCB pad drill size.
        @return     the drill size, in logical units.
    */
    
    public int getPCB_pad_drill()
    {
        return PCB_pad_drill;
    }
    
    /** Sets the default PCB track thickness.
        @param s the wanted thickness in logical units.
    */
    
    public void setPCB_thickness(int s)
    {
        PCB_thickness=s;
    }
    
    /** Gets the default PCB track thickness.
        @return     the track thickness in logical units.
    */
    
    public int getPCB_thickness()
    {
        return PCB_thickness;
    }
     
    /** Shows a dialog which allows the user modify the parameters of a given
    	primitive.
    */
    private void setPropertiesForPrimitive()
    {
        GraphicPrimitive gp=P.getFirstSelectedPrimitive();
        if (gp!=null) {
            Vector<ParameterDescription> v=gp.getControls();
            DialogParameters dp = new DialogParameters(null,v, extStrict, 
                P.getLayers());
            dp.setVisible(true);
            if(dp.active) {
                gp.setControls(dp.getCharacteristics());
                P.setChanged(true);
                
                // We need to check and sort the layers, since the user can
                // change the layer associated to a given primitive thanks to
                // the dialog window which has been shown.
                
                P.sortPrimitiveLayers();
                P.saveUndoState();
                repaint();
            }
        }
    }
    
    /** Select the closest object to the given point (in logical coordinates)
    	and pop up a dialog for the editing of its properties
    	
    	@param x the x logical coordinate of the point used for the selection
    	@param y the y logical coordinate of the point used for the selection
    
    */
    private void selectAndSetProperties(int x, int y)
    {
        //MapCoordinates cs=P.getMapCoordinates();

        P.deselectAll();
        P.selectPrimitive(cs.unmapXnosnap(x), cs.unmapYnosnap(y),
                              cs.unmapXnosnap(x+SEL_TOLERANCE)-
                              cs.unmapXnosnap(x),false);
        repaint();
        setPropertiesForPrimitive();
    }
    
    /** Checks if FidoCadJ should strictly comply with the FidoCad
        format (and limitations).
        
        @return the compliance mode.
    
    */
    public boolean getStrictCompatibility()
    {
        return extStrict;
    }
    
    /** Set if the strict FidoCAD compatibility mode is active
   		@param strict true if the compatibility with FidoCAD should be 
   		obtained.
   	
   	*/
   	public void setStrictCompatibility(boolean strict)
   	{
   		extStrict=strict;
   	}
    
    /** Round the specified number to the specified number of decimal digits.
    
    	@param n the number to be represented
 		@param ch the number of decimal digits to be retained
 		@return a string containing the result
    
    */
    private String roundTo(double n, int ch)
    {
        return ""+ (((int)(n*Math.pow(10,ch)))/Math.pow(10,ch));
    }
    
    public void setMapCoordinates(MapCoordinates m)
    {
    	cs=m;
    }
    
    public MapCoordinates getMapCoordinates()
    {
    	return cs;
    }
    
    /** The action listener. Recognize menu events and behaves consequently.
    
    */
    public void actionPerformed(ActionEvent evt)
    {
    	
    	// TODO: Avoid some copy/paste of code from FidoFrame class
    	
        // Recognize and handle menu events
        if(evt.getSource() instanceof JMenuItem) 
        {
            String arg=evt.getActionCommand();
            
            // Some wild copy and paste from FidoFrame class. How to refactor
            // that?
			
            if (arg.equals(Globals.messages.getString("Copy"))) {
            	// Copy all selected elements in the clipboard
                P.copySelected(!extStrict, splitNonStandardMacro_c,
                	getMapCoordinates().getXGridStep(), 
                	getMapCoordinates().getYGridStep());   
            } else if (arg.equals(Globals.messages.getString("Cut"))) {
            	// Cut elements
                P.copySelected(!extStrict, splitNonStandardMacro_c,
                	getMapCoordinates().getXGridStep(), 
                	getMapCoordinates().getYGridStep());   
                P.deleteAllSelected();
                repaint();
            } else if (arg.equals(Globals.messages.getString("Paste"))) {
            	// Paste elements from the clipboard
                P.paste(getMapCoordinates().getXGridStep(), 
                	getMapCoordinates().getYGridStep());   
                repaint();
            } else if (arg.equals(Globals.messages.getString("Rotate"))) {
            	// Rotate the selected element
                if(!isEnteringMacro())
                	P.rotateAllSelected();
                else
                	rotateMacro();
                repaint();
            } else if(arg.equals(Globals.messages.getString("Mirror_E"))) {
            	// Mirror the selected element
            	if(!isEnteringMacro())
                	P.mirrorAllSelected();
                else
                	mirrorMacro();               
                repaint();
            }
            
       }
   }
}

