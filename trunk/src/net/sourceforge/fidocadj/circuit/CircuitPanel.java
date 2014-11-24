package net.sourceforge.fidocadj.circuit;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

import javax.swing.*;

import java.util.*;
import java.io.*;

import globals.*;
import geom.*;
import net.sourceforge.fidocadj.dialogs.*;
import net.sourceforge.fidocadj.primitives.*;
import net.sourceforge.fidocadj.timer.*;
import net.sourceforge.fidocadj.toolbars.*;
import net.sourceforge.fidocadj.circuit.controllers.*;
import net.sourceforge.fidocadj.circuit.model.*;
import net.sourceforge.fidocadj.circuit.views.*;
import net.sourceforge.fidocadj.clipboard.*;
import graphic.*;
import graphic.swing.*;
import net.sourceforge.fidocadj.export.*;
import net.sourceforge.fidocadj.layers.*;

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

    Copyright 2007-2014 by Davide Bucci
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
                                             ChangeSelectionListener,
                                             PrimitivesParInterface
                                             
{ 

	// *********** DRAWING ***********

	Graphics2DSwing graphicSwing; 
    
    // Coordinate system to be used.
    private transient MapCoordinates cs;
        
    // Use anti alias in drawings 
    public boolean antiAlias;  
	
	// Draw the grid
    private boolean isGridVisible;

	// Default background color
    private Color backgroundColor;

	// Position of the rectangle used for the selection
    private Rectangle evidenceRect;

	// Margin size in pixels when calculating component sizes.
	public static final int MARGIN=20;     
                                    
	// Color of elements during editing
    static final ColorSwing editingColor=new ColorSwing(Color.green);                                

	// Font to be used to draw the ruler
	private static final String rulerFont = "Lucida Sans Regular";
    
    // Model:

    // Maybe, should it be kept private?
    public transient DrawingModel P;
    
    public JScrollPane father;
    
    // Views:
    public Drawing drawingAgent;
    
    // Controllers:
    private final EditorActions edt;	
    private final CopyPasteActions cpa;
    private final HandleActions haa;
    private final ParserActions pa;
    private final UndoActions ua;
    private final ContinuosMoveActions eea;
    
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
    
    // If this variable is different from null, the component will ensure that
    // the corresponding rectangle will be shown in a scroll panel during the
    // next redraw.
	private Rectangle scrollRectangle;
    
    // Strict FidoCad compatibility
    public boolean extStrict;
    
	// ********** RULER **********
	
    private boolean ruler;	// Is it to be drawn?
    private int rulerStartX;
    private int rulerStartY;
    private int rulerEndX;
    private int rulerEndY;        
    
    // ********** INTERFACE ELEMENTS **********
    
    // Popup menu
    JPopupMenu popup;
    
    // Elements to be included in the popup menu
    JMenuItem editProperties;
    JMenuItem editCut;
    JMenuItem editCopy;
    JMenuItem editPaste;
    JMenuItem editSelectAll;
    JMenuItem editRotate;
    JMenuItem editMirror;
    JMenuItem editSymbolize; // phylum
    JMenuItem editUSymbolize; // phylum
    
    JMenuItem editAddNode;
    JMenuItem editRemoveNode;
    
    // We need to save the position where the popup menu appears
    int menux;
    int menuy;
        
    
	// ********** LISTENERS **********

    private ChangeZoomListener zoomListener;
    private ChangeSelectionListener selectionListener;
    private ChangeSelectionListener scrollGestureSelectionListener;

    /** Standard constructor
        @param isEditable indicates whether the panel should be responsible
               to keyboard and mouse inputs.
               
    */
    public CircuitPanel (boolean isEditable) 
    {
        backgroundColor=Color.white; 
        P=new DrawingModel();
        pa=new ParserActions(P);
        ua=new UndoActions(pa);
        edt=new EditorActions(P, ua);
        eea = new ContinuosMoveActions(P, ua, edt);
        eea. setPrimitivesParListener(this);
        
        haa=new HandleActions(P, edt, ua);
        cpa=new CopyPasteActions(P, edt, pa, ua, new TextTransfer());
       
       	graphicSwing = new Graphics2DSwing();
        
        drawingAgent=new Drawing(P);
        
        isGridVisible=true;
        zoomListener=null;
        antiAlias = true;
        record = 1e100;
        record_c = record;
        record_d = record;
        evidenceRect = new Rectangle(0,0,-1,-1);
        
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
    
                	
        // This is unot seful when preparing the applet: the circuit panel will
        // not be editable in this case.
        if (isEditable) {
            addMouseListener(this);
            addMouseMotionListener(this);
            setFocusable(true);
            registerActiveKeys();
            //Create the popup menu.
    		popup = new JPopupMenu();
    		editProperties = new 
    			JMenuItem(Globals.messages.getString("Param_opt"));

    		editCut = new JMenuItem(Globals.messages.getString("Cut"));
        	editCopy = new JMenuItem(Globals.messages.getString("Copy"));
        	editSelectAll = new JMenuItem(
        		Globals.messages.getString("SelectAll"));
        	
        	editPaste = new	JMenuItem(Globals.messages.getString("Paste"));
        	editRotate = new JMenuItem(Globals.messages.getString("Rotate"));
    	    editMirror = new JMenuItem(Globals.messages.getString("Mirror_E"));
    	    
    	    editSymbolize = 
    	    	new JMenuItem(Globals.messages.getString("Symbolize"));
    	    editUSymbolize = 
    	    	new JMenuItem(Globals.messages.getString("Unsymbolize")); 
    	    
    	    editAddNode = new JMenuItem(Globals.messages.getString("Add_node"));
    	    editRemoveNode = 	
    	    	new JMenuItem(Globals.messages.getString("Remove_node"));
        
        	popup.add(editProperties);
        	popup.addSeparator();
        	
        	popup.add(editCut);
        	popup.add(editCopy);
        	popup.add(editPaste);
        	popup.addSeparator();
        	popup.add(editSelectAll);
        	
        	popup.addSeparator();
        	popup.add(editRotate);
        	popup.add(editMirror);
        	
        	popup.add(editAddNode);
        	popup.add(editRemoveNode);
        	
        	popup.addSeparator();
        	popup.add(editSymbolize); // by phylum
        	popup.add(editUSymbolize); // phylum
        	
        	// Adding the action listener
        	
        	editProperties.addActionListener(this);
        	editCut.addActionListener(this);
        	editCopy.addActionListener(this);
        	editSelectAll.addActionListener(this);
        	editPaste.addActionListener(this);
        	editRotate.addActionListener(this);
        	editMirror.addActionListener(this);
        	
        	editAddNode.addActionListener(this);
        	editRemoveNode.addActionListener(this);
        	
        	editSymbolize.addActionListener(this); // phylum
        	editUSymbolize.addActionListener(this); // phylum
        	
        	// Patchwork for bug#54.
        	// When mouse pointer enters into CircuitPanel with macro,
        	// grab focus from macropicker.
        	// NOTE: MouseListener.mouseEntered doesn't works stable.
        	addMouseMotionListener(new MouseMotionAdapter() {
				public void mouseMoved(MouseEvent e)
				{
					if(eea.isEnteringMacro() && !isFocusOwner()){
						requestFocus();
					}
				}
        	});
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
                eea.primEdit = null;
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
    public final void registerActiveKeys() 
    {
    	
        registerAction("selection", 'a', ElementsEdtActions.SELECTION);
   		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
        	.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,0,false), 
            "selection");
        registerAction("line", 'l', ElementsEdtActions.LINE);
        registerAction("text", 't', ElementsEdtActions.TEXT);
        registerAction("bezier", 'b', ElementsEdtActions.BEZIER);
        registerAction("polygon", 'p', ElementsEdtActions.POLYGON);
        registerAction("complexcurve", 'o', ElementsEdtActions.COMPLEXCURVE);
        registerAction("ellipse", 'e', ElementsEdtActions.ELLIPSE);
        registerAction("rectangle", 'g', ElementsEdtActions.RECTANGLE);
        registerAction("connection", 'c', ElementsEdtActions.CONNECTION);
        registerAction("pcbline", 'i', ElementsEdtActions.PCB_LINE);
        registerAction("pcbpad", 'z', ElementsEdtActions.PCB_PAD);

        final String delete = "delete"; 
    
        // Delete key
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke("DELETE"), delete);
    
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke("BACK_SPACE"), delete);
            
        getActionMap().put(delete, new AbstractAction() {
            public void actionPerformed(ActionEvent ignored) {
                edt.deleteAllSelected(true);
                repaint();
            }
        });
    
    
        final String escape = "escape";
        
        // Escape: clear everything
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke("ESCAPE"), escape);
    
        getActionMap().put(escape, new AbstractAction() {
            public void actionPerformed(ActionEvent ignored) {
                if(eea.clickNumber>0){
                	// Here we need to clear the variables which are used 
                	// during the primitive introduction and editing.
                	// see mouseMoved method for details.
                	
                    eea.successiveMove = false;
                    eea.clickNumber = 0;
                    eea.primEdit = null;
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
                edt.moveAllSelected(-1,0);
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
                edt.moveAllSelected(1,0);
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
                edt.moveAllSelected(0,-1);
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
                edt.moveAllSelected(0,1);
                repaint();
            }
        });  
    }
    
    /** Makes sure the object gets focus.
    */
    public void getFocus()
    {
    	requestFocusInWindow();
    }
    
    /** ChangeSelectionListener interface implementation 
    	@param s the selection state
    	@param macro the macro key (if applies)
    */    
    public void setSelectionState(int s, String macro)
    {
        if (selectionListener!=null && s!=eea.actionSelected) {
            selectionListener.setSelectionState(s, macro);
            selectionListener.setStrictCompatibility(extStrict);
        }    
        
        if (scrollGestureSelectionListener!=null) {
            scrollGestureSelectionListener.setSelectionState(s, 
            	macro);
        }
        eea.setState(s, macro);
        selectCursor();
    }
    
    /** Set the rectangle which will be shown during the next redraw.
    	@param r the rectangle to show.
    */
    public void setScrollRectangle(Rectangle r)
    {
    	scrollRectangle = r;
    	repaint();
    }
        
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
    
    /** Return the current editing layer
        @return the index of the layer
    */
    public int getCurrentLayer()
    {
        return eea.currentLayer;
    }
    
    /** Set the current editing layer
        @param cl the wanted layer
    */
    public void setCurrentLayer(int cl)
    {
    	int l=cl;
        /* two little checks... */
        if (l<0)
            l=0;
        if (l>=P.getLayers().size())
            l=P.getLayers().size()-1;
            
        eea.currentLayer=l;
    }
    
    /** Sets the circuit. TODO: remove this method. One can use ParserAction.
        @param c the circuit string
    */
    public void setCirc(StringBuffer c)
    {
        pa.parseString(c);
    }
    
    /** TODO: eliminate this method. 
    	Get the circuit in the FidoCadJ format, without the [FIDOCAD] header
        @param extensions allow for FCJ extensions
        @return the circuit in the Fidocad format
    */
    public StringBuffer getCirc(boolean extensions)
    {
        return pa.getText(extensions);
    }
    
    /** Change the current layer state. Change the layer of all selected
    	primitives.
        @param s the layer to be selected
    */
    public void changeSelectedLayer(int s)
    {
    	// Change the current layer
        eea.currentLayer=s;
        // Change also the layer of all selected primitives
        if(edt.setLayerForSelectedPrimitives(s)) {
        	repaint();
        }
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
    
    
    /** Increase or decrease the zoom by a step of 33%
    	@param increase if true, increase the zoom, if false decrease
    	@param x coordinate to which center the viewport (screen coordinates)
    	@param y coordinate to which center the viewport (screen coordinates)
    */
    public void changeZoomByStep(boolean increase, int x, int y)
    {
        int xpos = cs.unmapXnosnap(x);
        int ypos = cs.unmapYnosnap(y);
        double z=cs.getXMagnitude();
        
        // Calculate the scroll position to center the scroll
        // where the user has done the click.
        
        PointG origin=new PointG();
        DimensionG d=DrawingSize.getImageSize(P, 1.0, false, origin);
    	double xs,ys;
    					
        xs=(double)xpos/(d.width+MARGIN);
        if(xs<0.0) xs=0.0;
        ys=(double)ypos/(d.height+MARGIN);
        if(ys<0.0) ys=0.0;
                    
        // Click+Meta reduces the zoom
        // Click raises the zoom
        double oldz=z;
        if(increase) 
            z=z*3.0/2.0;
        else
            z=z*2.0/3.0;
            
        // Checking that reasonable limits are not exceeded.
        if(z>20) z=20;
        if(z<.25) z=.25;
            
       	z=Math.round(z*100.0)/100.0;
        cs.setMagnitudes(z,z);

        // A little strong...
        
        int width = father.getViewport().getExtentSize().width;
        int height = father.getViewport().getExtentSize().height;
        
        Rectangle r=new Rectangle((int)(xpos*z-width/2),
       			(int)(ypos*z-height/2),
       			width, height);
       	
       	setScrollRectangle(r); 

    }
    
    /** Show a popup menu representing the actions that can be done on the
    	selected context.
    	@param x the x coordinate where the popup menu should be put
    	@param y the y coordinate where the popup menu should be put
    */
    public void showPopUpMenu(int x, int y)
    {
    	menux=x; menuy=y;
    	boolean s=false;
    	boolean somethingSelected=edt.getFirstSelectedPrimitive()!=null;
    	GraphicPrimitive g=edt.getFirstSelectedPrimitive();
		
		// A certain number of menu options are applied to selected 
       	// primitives. We therefore check wether are there some 
       	// of them available and in this case we activate what should
       	// be activated in the pop up menu.
     	s=somethingSelected;
        
       	editProperties.setEnabled(s);
      	editCut.setEnabled(s);
        editCopy.setEnabled(s);
        editRotate.setEnabled(s);
        editMirror.setEnabled(s);
            	
        if(g instanceof PrimitiveComplexCurve ||
            g instanceof PrimitivePolygon) {
            s=true;
		} else
			s=false;
		
		if (!edt.isUniquePrimitiveSelected())
			s=false;
		
		editAddNode.setEnabled(s);
        editRemoveNode.setEnabled(s);
        editAddNode.setVisible(s);
        editRemoveNode.setVisible(s);
            	
        // We just check if the clipboard is empty. It would be better
        // to see if there is some FidoCadJ code wich might be pasted
            	
        TextTransfer textTransfer = new TextTransfer();
            	
        if(textTransfer.getClipboardContents().equals(""))
            editPaste.setEnabled(false);
        else
            editPaste.setEnabled(true);
            	
        editSymbolize.setEnabled(somethingSelected);
        
        editUSymbolize.setEnabled(edt.selectionCanBeSplitted()); // phylum
        
        popup.show(this, x, y);
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
    }
    
    /** Get the current editing action (see the constants defined in this 
    	class)
    
        @return the current editing action
    */
    public int getSelectionState()
    {
        return eea.getSelectionState();
    } 
    
    /** Handle the mouse movements when editing a graphic primitive.
        This procedure is important since it is used to show interactively 
        to the user which element is being modified.
    */
    public void mouseMoved(MouseEvent evt)
    {
        int xa=evt.getX();
        int ya=evt.getY();
        
        boolean toggle = false;
        
        if(Globals.useMetaForMultipleSelection) {
        	toggle = evt.isMetaDown();
    	} else {
        	toggle = evt.isControlDown();
        }
 
		if (eea.continuosMove(cs, xa, ya, toggle))
			repaint();

    }

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
        boolean toggle = false;
        
        if(Globals.useMetaForMultipleSelection) {
        	toggle = evt.isMetaDown();
    	} else {
        	toggle = evt.isControlDown();
        }
                    
        if(eea.actionSelected == ElementsEdtActions.SELECTION &&
            (evt.getModifiers() & InputEvent.BUTTON3_MASK)==0 &&
            !evt.isShiftDown()) { 
            haa.dragHandleStart(px, py, edt.getSelectionTolerance(),
            	toggle, cs);
        } else if(eea.actionSelected == ElementsEdtActions.SELECTION){ 
        	// Right click during selection
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
               
        haa.dragHandleDrag(this, px, py, cs);
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
        
        boolean toRepaint = false;
        boolean toggle = false;
        boolean button3 = false;
        
        System.out.println ("evt: "+evt);
        
        if(Globals.useMetaForMultipleSelection) {
        	toggle = evt.isMetaDown();
    	} else {
        	toggle = evt.isControlDown();
        }
        
    	// Key bindings are a little different with MacOSX.
        if(Globals.weAreOnAMac) {
        	if(evt.getButton()==MouseEvent.BUTTON3)
        		button3=true;
        	else if(evt.getButton()==MouseEvent.BUTTON1 && evt.isControlDown())
        		button3=true;
        } else {
			button3 = (evt.getModifiers() & InputEvent.BUTTON3_MASK)==
            		InputEvent.BUTTON3_MASK;
        }

        
        
        // If we are in the selection state, either we are ending the editing
        // of an element (and thus the dragging of a handle) or we are 
        // making a click.
        
        if(eea.actionSelected==ElementsEdtActions.SELECTION) {
            if(rulerStartX!=px || rulerStartY!=py) // NOPMD
            	haa.dragHandleEnd(this, px, py, toggle, cs);
            else {
            	ruler=false;
            	requestFocusInWindow();

            	toRepaint = eea.handleClick(cs,evt.getX(), evt.getY(), 
            		button3, toggle, evt.getClickCount() >= 2);
            }
           	repaint();
        } else {
            requestFocusInWindow();
            toRepaint=eea.handleClick(cs,evt.getX(), evt.getY(),
            	button3, toggle, evt.getClickCount() >= 2);
        }
        if (toRepaint)
        	repaint();
        
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
        switch(eea.actionSelected) {
            case ElementsEdtActions.NONE:
            case ElementsEdtActions.ZOOM:
            case ElementsEdtActions.HAND:
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                break;
            case ElementsEdtActions.LINE:
            case ElementsEdtActions.TEXT:
            case ElementsEdtActions.BEZIER:
            case ElementsEdtActions.POLYGON:
            case ElementsEdtActions.COMPLEXCURVE:
            case ElementsEdtActions.ELLIPSE:
            case ElementsEdtActions.RECTANGLE:
            case ElementsEdtActions.CONNECTION:
            case ElementsEdtActions.PCB_LINE:
            case ElementsEdtActions.PCB_PAD:       
                setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                break;
			case ElementsEdtActions.SELECTION:
            default:
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                break;
        }
    }
    
    public void mouseExited(MouseEvent evt)
    {
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        if(eea.successiveMove) {
            eea.successiveMove = false;
            //eea.primEdit = null;
            repaint();
        }
    }
    
    /** The zoom listener
        @param tz the zoom factor to be used
    */
    
    public void changeZoom(double tz) 
    {
        double z=Math.round(tz*100.0)/100.0;
        cs.setMagnitudes(z,z);
        eea.successiveMove=false;
        
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

    /** Repaint the panel.
    	This method performs the following operations:
    	1. set the anti aliasing on (or off, depending on antiAlias).
    	2. paint in white the background and draw the grid.
    	3. call drawingAgent draw
    	4. draw all active handles
    	5. if needed, draw the primitive being edited
    	6. draw the ruler, if needed
    	7. if requested, print information about redraw speed
    
    */
    public void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        MyTimer mt;
        mt = new MyTimer();
        
        
        Graphics2D g2 = (Graphics2D)g; 
       	graphicSwing.setGraphicContext(g2);
       
                
        // Activate anti-aliasing when necessary.
        
        if (antiAlias) {
        	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
             	RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, 
                RenderingHints.VALUE_RENDER_QUALITY);
        	g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, 
                RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        	g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, 
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        	g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, 
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        } else {
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
            graphicSwing.drawGrid(cs,0,0,getWidth(),
                              getHeight());
        }
        
        // The standard color is black.
        g.setColor(Color.black);
        
        // Perform the drawing operation.
        drawingAgent.draw(graphicSwing, cs);
       
        if (zoomListener!=null) 
            zoomListener.changeZoom(cs.getXMagnitude());
        
        // Draw the handles of all selected primitives.
        drawingAgent.drawSelectedHandles(graphicSwing, cs);
    
        // If an evidence rectangle is active, draw it.
        
        g.setColor(editingColor.getColorSwing());

        g2.setStroke(new BasicStroke(1));

        if(evidenceRect!=null && eea.actionSelected == 
        	ElementsEdtActions.SELECTION)
        	g.drawRect(evidenceRect.x,evidenceRect.y, evidenceRect.width,   
            	evidenceRect.height);
        else
        	evidenceRect = null;
		
		// If there is a primitive or a macro being edited, draws it.
		//graphicSwing.setGraphicContext(g2);
		eea.drawPrimEdit(graphicSwing, cs);


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
  	   		scrollRectToVisible(r);
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
            //System.out.println("Validate: "+(cs.getXMax()
            //    +MARGIN)+"x"+(cs.getYMax()+MARGIN));
    		setPreferredSize(new Dimension(cs.getXMax()
                +MARGIN,cs.getYMax()+MARGIN));
        }        
        super.validate();
        
    }
 
    /** Draws a ruler to ease measuring distances.
    	@param g the graphic context
    	@param sx the x position of the starting corner
    	@param sy the y position of the starting corner
    	@param ex the x position of the end corner
    	@param ey the y position of the end corner
    	
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
                
        // A little bit of trigonometry :-)
        
        double alpha;
        if (sx==ex)
            alpha = Math.PI/2.0+(ey-sy<0?0:Math.PI);
        else
        	alpha = Math.atan((double)(ey-sy)/(double)(ex-sx));
        
        alpha += ex-sx>0?0:Math.PI;
        
        // Those magic numers are the lenghts of the tics (major and minor)
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
        
        double dex = sx + length*Math.cos(alpha)*cs.getXMagnitude();
        double dey = sy + length*Math.sin(alpha)*cs.getYMagnitude();
        
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
        
        String t1 = Globals.roundTo(length,2);
        
        // Remember that one FidoCadJ logical unit is 127 microns.
        String t2 = Globals.roundTo(length*.127,2)+" mm";
        
        FontMetrics fm = g.getFontMetrics(f);
     
        // Draw the box at the end, with the measurement results.
        g.setColor(Color.white);
        g.fillRect(ex+10, ey, Math.max(fm.stringWidth(t1),
            fm.stringWidth(t2))+1, 24);
        
        g.setColor(editingColor.getColorSwing());
        g.drawRect(ex+9, ey-1, Math.max(fm.stringWidth(t1),
            fm.stringWidth(t2))+2, 25);
        g.setColor(editingColor.getColorSwing().darker().darker());
        g.drawString(t1, ex+10, ey+10);
        g.drawString(t2, ex+10, ey+20);
        
    }
    
    /** Get the current instance of EditorActions controller class
    	@return the class
    */
    public EditorActions getEditorActions()
    {
    	return edt;
    }
 
    /** Get the current instance of CopyPasteActions controller class
    	@return the class
    */
    public CopyPasteActions getCopyPasteActions()
    {
    	return cpa;
    }
    
    /** Get the current instance of ParserActions controller class
    	@return the class
    */
    public ParserActions getParserActions()
    {
    	return pa;
    }
    
    /** Get the current instance of UndoActions controller class
    	@return the class
    */
    public UndoActions getUndoActions()
    {
    	return ua;
    }
    /** Get the current instance of ElementsEdtActions controller class
    	@return the class
    */
    public ContinuosMoveActions getContinuosMoveActions()
    {
    	return eea;
    } 
     
    /** Shows a dialog which allows the user modify the parameters of a given
    	primitive. If more than one primitive is selected, modify only the
    	layer of all selected primitives.
    */
    public void setPropertiesForPrimitive()
    {    	
        GraphicPrimitive gp=edt.getFirstSelectedPrimitive();
        if (gp==null) 
        	return;
        	
        Vector<ParameterDescription> v;
        if (edt.isUniquePrimitiveSelected()) {
           	v=gp.getControls();
        } else {
          	// If more than a primitive is selected, 
           	v=new Vector<ParameterDescription>(1);
           	ParameterDescription pd = new ParameterDescription();
			pd.parameter=new LayerInfo(gp.getLayer());
			pd.description=Globals.messages.getString("ctrl_layer");
			v.add(pd);
        }
        DialogParameters dp = new DialogParameters(
           	(JFrame)Globals.activeWindow,
           	v, extStrict, 
            P.getLayers());
        dp.setVisible(true);
        if(dp.active) {
        	if (edt.isUniquePrimitiveSelected()) {
        	    gp.setControls(dp.getCharacteristics());	
        	} else { 
        		ParameterDescription pd=(ParameterDescription)v.get(0);
        		v=dp.getCharacteristics();
        		if (pd.parameter instanceof LayerInfo) {
					int l=((LayerInfo)pd.parameter).getLayer();
					edt.setLayerForSelectedPrimitives(l);
				} else {
		 			System.out.println(
		 				"Warning: unexpected parameter! (layer)");
		 		}
            }
            P.setChanged(true);
                
            // We need to check and sort the layers, since the user can
            // change the layer associated to a given primitive thanks to
            // the dialog window which has been shown.
                
            P.sortPrimitiveLayers();
            ua.saveUndoState();
            repaint();
        }
    }
    
    /** Selects the closest object to the given point (in logical coordinates)
    	and pops up a dialog for the editing of its Param_opt.
    	
    	@param x the x logical coordinate of the point used for the selection
    	@param y the y logical coordinate of the point used for the selection
    
    */
    public void selectAndSetProperties(int x, int y)
    {
        edt.setSelectionAll(false);
        edt.handleSelection(cs, x, y, false);
        repaint();
        setPropertiesForPrimitive();
    }
    
    /** Checks if FidoCadJ should strictly comply with the FidoCAD
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
    
    
    /** Change the current coordinate mapping.
    	@param m the new coordinate mapping to be adopted.
    */
    public void setMapCoordinates(MapCoordinates m)
    {
    	cs=m;
    	// Force an in-depth redraw.
    	P.setChanged(true);
    }
    
    /** Get the current coordinate mapping.
    	@return the current coordinate mapping
    */
    public MapCoordinates getMapCoordinates()
    {
    	return cs;
    }
    
    /** The action listener. Recognize menu events and behaves consequently.
    */
    public void actionPerformed(ActionEvent evt)
    {
    	// TODO: Avoid some copy/paste of code from FidoFrame class
    	
        // Recognize and handle popup menu events
        if(evt.getSource() instanceof JMenuItem) 
        {
            String arg=evt.getActionCommand();                        
			
			if (arg.equals(Globals.messages.getString("Param_opt"))) {
				setPropertiesForPrimitive();
            } else if (arg.equals(Globals.messages.getString("Copy"))) {
            	// Copy all selected elements in the clipboard
                cpa.copySelected(!extStrict, false);   
            } else if (arg.equals(Globals.messages.getString("Cut"))) {
            	// Cut elements
                cpa.copySelected(!extStrict, false);   
                edt.deleteAllSelected(true);
                repaint();
            } else if (arg.equals(Globals.messages.getString("Paste"))) {
            	// Paste elements from the clipboard
                cpa.paste(getMapCoordinates().getXGridStep(), 
                	getMapCoordinates().getYGridStep());   
                repaint();
            } else if (arg.equals(Globals.messages.getString("SelectAll"))) {
            	// Select all in the drawing.
            	edt.setSelectionAll(true);   
                // Even if the drawing is not changed, a repaint operation is 
                // needed since all selected elements are rendered in green.
                repaint();
            }else if (arg.equals(Globals.messages.getString("Rotate"))) {
            	// Rotate the selected element
                if(eea.isEnteringMacro())
                	eea.rotateMacro();
                else
                	edt.rotateAllSelected();
                repaint();
            } else if(arg.equals(Globals.messages.getString("Mirror_E"))) {
            	// Mirror the selected element
            	if(eea.isEnteringMacro())
                	eea.mirrorMacro();
                else
                	edt.mirrorAllSelected();
                	          
                repaint();
            } 
            
            else if (arg.equals(Globals.messages.getString("Symbolize"))) { 	
            	if (edt.getFirstSelectedPrimitive() == null) return;
				DialogSymbolize s = new DialogSymbolize(this,P);
				s.setModal(true);
				s.setVisible(true);	
				try {
					LibUtils.saveLibraryState(ua);
				} catch (IOException e) {
					System.out.println("Exception: "+e);
		
				}
				repaint();
			}  
            
            else if (arg.equals(Globals.messages.getString("Unsymbolize"))) {
            	StringBuffer s=edt.getSelectedString(true, pa);
            	edt.deleteAllSelected(false);
           		pa.addString(pa.splitMacros(s,  true),true);
            	ua.saveUndoState();
            	repaint(); 
			}              
            
            else if(arg.equals(Globals.messages.getString("Remove_node"))) {
            	if(edt.getFirstSelectedPrimitive() 
            		instanceof PrimitivePolygon) {
            		PrimitivePolygon poly=
            			(PrimitivePolygon)edt.getFirstSelectedPrimitive();
            		poly.removePoint(getMapCoordinates().unmapXnosnap(menux),
            			getMapCoordinates().unmapYnosnap(menuy),1);
            		ua.saveUndoState();
            		repaint();
            	} else if(edt.getFirstSelectedPrimitive() 
            		instanceof PrimitiveComplexCurve) {
            		PrimitiveComplexCurve curve=
            			(PrimitiveComplexCurve)edt.getFirstSelectedPrimitive();
            		curve.removePoint(getMapCoordinates().unmapXnosnap(menux),
            			getMapCoordinates().unmapYnosnap(menuy),1);
            		ua.saveUndoState();
            		repaint();
            	}
            } else if(arg.equals(Globals.messages.getString("Add_node"))) {
            	if(edt.getFirstSelectedPrimitive() 
            		instanceof PrimitivePolygon) {
            		PrimitivePolygon poly=
            			(PrimitivePolygon)edt.getFirstSelectedPrimitive();
            		poly.addPointClosest(
            			getMapCoordinates().unmapXsnap(menux),
            			getMapCoordinates().unmapYsnap(menuy));
            		ua.saveUndoState();
            		repaint();
            	} else if(edt.getFirstSelectedPrimitive() instanceof 
            		PrimitiveComplexCurve) {
            		PrimitiveComplexCurve poly=
            			(PrimitiveComplexCurve)edt.getFirstSelectedPrimitive();
            		poly.addPointClosest(
            			getMapCoordinates().unmapXsnap(menux),
            			getMapCoordinates().unmapYsnap(menuy));
            		ua.saveUndoState();
            		repaint();
            	}
            }      
       }
   }
   
   /** Forces a repaint.
   */
   public void forcesRepaint()
   {
   		repaint();
   }
   
   /** Forces a repaint.
   */
   public void forcesRepaint(int a, int b, int c, int d)
   {
   		repaint(a, b, c, d);
   }
   
}

