package net.sourceforge.fidocadj.circuit.controllers;

import java.io.*;
import java.util.Vector;

import net.sourceforge.fidocadj.circuit.model.*;
import net.sourceforge.fidocadj.geom.*;
import net.sourceforge.fidocadj.layers.*;
import net.sourceforge.fidocadj.primitives.*;
import graphic.*;

/** ElementsEdtActions: contains a controller for adding/modifying elements
	to a drawing model.
	
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

    Copyright 2014 by Davide Bucci
</pre>

    @author Davide Bucci
*/

public class ElementsEdtActions
{
	protected final DrawingModel P;
	protected final UndoActions ua;
	protected final EditorActions edt;
	
	// The current layer being edited
	public int currentLayer;
	
	// Array used to keep track of the insertion of elements which require
	// more than one click (logical coordinates). Index begins at 1 to
	// clickNumber.
    public int[] xpoly;	
    public int[] ypoly;

	// used when entering a macro
    public String macroKey;               
    
    // Nuber of clicks done when entering an object.
    public int clickNumber;     

	// The primitive being edited
	public transient GraphicPrimitive primEdit;
	
	// editing action being done
    public int actionSelected;
    
    // Track wether an editing action is being made.
    public boolean successiveMove;
    
    // TO IMPROVE: this must be synchronized with the value in PrimitivePolygon
    // Maximum number of polygon vertices
    public static final int NPOLY=256;
    
    // Default sizes for PCB elements
    public int PCB_pad_sizex;
    public int PCB_pad_sizey;
    public int PCB_pad_style;  
    public int PCB_pad_drill;
    public int PCB_thickness;    

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
    
    protected PrimitivesParInterface primitivesParListener;

	/** Standard constructor: provide the database class.
		@param pp the Model containing the database.
		@param u the Undo controller, to ease undo operations.
		@param e the Basic editing controller, for handling selection 
			operations.
	*/
	public ElementsEdtActions (DrawingModel pp, UndoActions u, 
		EditorActions e)
	{
		P=pp;
		ua=u;
		edt=e;
		xpoly = new int[NPOLY];
        ypoly = new int[NPOLY];
        currentLayer=0;
        
        PCB_thickness = 5;
        PCB_pad_sizex=5;
        PCB_pad_sizey=5;
        PCB_pad_drill=2;   
	
		primEdit = null;
		primitivesParListener=null;
		
		actionSelected = SELECTION;
	}
	
	
	/** Sets the action mode.
		@param a the wanted editing mode. 
	*/
	public void setActionSelected(int a)
	{
		if (a!=actionSelected)
			clickNumber=0;
			
		actionSelected = a;
	}
	
	/** Set the listener for showing popups and editing actions which are
		platform-dependent.
	*/
	public void setPrimitivesParListener(PrimitivesParInterface l)
	{
		primitivesParListener=l;
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
    
    /** Determine wether the current primitive being added is a macro.
    */
    public boolean isEnteringMacro()
    {
    	return primEdit instanceof PrimitiveMacro;
    }
    
    /** Chooses the entering state.
    */
    public void setState(int s, String macro)
    {
    	actionSelected=s;
        clickNumber=0;
        successiveMove=false;
        macroKey=macro;
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
    
	
    /** Add a connection primitive at the given point.
    	@param x the x coordinate of the connection (logical) 
    	@param y the y coordinate of the connection (logical) 
    */
    private void addConnection(int x, int y)
    {    
        PrimitiveConnection g=new PrimitiveConnection(x, y, currentLayer,
            P.getTextFont(), P.getTextFontSize());
    	g.setMacroFont(P.getTextFont(), P.getTextFontSize());

        P.addPrimitive(g, true, ua);
    }	
	
	/** Introduce a line. You can introduce lines point by point, so you
    	should keep track of the number of clicks you received (clickNumber).
    	You must count the number of clicks and see if there is a modification
    	needed on it (the return value).
    	
    	@param x coordinate of the click (logical)
    	@param y coordinate of the click (logical)
    	@param clickNumber the click number: 1 is the first click, 2 is the 
    		second (and final) one.
    	@return the new value of clickNumber.
    */
    private int addLine(int x, int y, int clickNumber, boolean altButton)
    {            
    	int cn=clickNumber;
        
        // clickNumber == 0 means that no line is being drawn  
            	
        xpoly[clickNumber] = x;
        ypoly[clickNumber] = y;
        if (clickNumber == 2 || altButton) {
            // Here we know the two points needed for creating
            // the line. The object is thus added to the database.
            PrimitiveLine g= new PrimitiveLine(xpoly[1],
                                 ypoly[1],
                                 xpoly[2],
                                 ypoly[2],
                                 currentLayer,
                                 false,
                                 false,
                                 0,3,2,0,
                                 P.getTextFont(), 
                                 P.getTextFontSize());
            P.addPrimitive(g, true, ua);
                        
            if(altButton) {
             	cn = 0;
            } else {
                cn = 1;
                xpoly[1] = xpoly[2];
                ypoly[1] = ypoly[2];
            }
        }
        return cn;
    }
    
    /** Introduce the macro being edited at the given coordinate.
    	@param x the x coordinate (logical)
    	@param y the y coordinate (logical)
    */
    private void addMacro(int x, int y)
    {
        try {
            // Here we add a macro. There is a remote risk that the macro
            // we are inserting contains an error. This is not something
            // which would happen frequently, since if the macro is in the
            // library this means it is available, but we need to use
            // the block try anyway.
            	
            edt.setSelectionAll(false);
                
            int orientation = 0;
            boolean mirror = false;
            	
            if (primEdit instanceof PrimitiveMacro)  {
            	orientation = ((PrimitiveMacro)primEdit).getOrientation();
				mirror = ((PrimitiveMacro)primEdit).isMirrored();
            }
            P.addPrimitive(new PrimitiveMacro(P.getLibrary(), 
                    P.getLayers(), x, y, macroKey,"",
                    x+10, y+5, "", x+10, y+10,
                    P.getTextFont(),
                    P.getTextFontSize(), orientation, mirror), true, ua);
            primEdit=null;
                    
        } catch (IOException G) {
        	// A simple error message on the console will be enough
            System.out.println(G);
        }                
    }
    
    /** Introduce an ellipse. You can introduce ellipses with two clicks, so 
    	you should keep track of the number of clicks you received 
    	(clickNumber).
    	You must count the number of clicks and see if there is a modification
    	needed on it (the return value).
    	
    	@param x coordinate of the click (logical)
    	@param ty coordinate of the click (logical)
    	@param clickNumber the click number: 1 is the first click, 2 is the 
    		second (and final) one.
    	@param isCircle if true, force the ellipse to be a circle
    	@return the new value of clickNumber.
    */
    private int addEllipse(int x, int ty, int clickNumber, boolean isCircle)
    {
    	int y=ty;
    	int cn=clickNumber;
		if(isCircle) 
            y=ypoly[1]+x-xpoly[1];

        // clickNumber == 0 means that no ellipse is being drawn
            
        xpoly[clickNumber] = x;
	   	ypoly[clickNumber] = y;
        if (cn == 2) {
            PrimitiveOval g=new PrimitiveOval(xpoly[1],
                                         ypoly[1],
                                         xpoly[2],
                                         ypoly[2],
                                         false,
                                         currentLayer,0,
                                         P.getTextFont(), P.getTextFontSize());

            P.addPrimitive(g, true, ua);
        
            cn = 0;
              
        }
        return cn;    
    }
    
	/** Introduce a Bézier curve. You can introduce this with four clicks, so 
    	you should keep track of the number of clicks you received 
    	(clickNumber).
    	You must count the number of clicks and see if there is a modification
    	needed on it (the return value). In other words, when using this 
    	method, you are responsible of storing this value somewhere and
    	providing it any time you need to call addBezier again.
    	
    	@param x coordinate of the click (logical)
    	@param y coordinate of the click (logical)
    	@param clickNumber the click number: 1 is the first click, 2 is the 
    		second one, and so on...
    	@return the new value of clickNumber.
    */
	private int addBezier(int x, int y, int clickNumber)
	{
		int cn=clickNumber;
                    
        // clickNumber == 0 means that no bezier is being drawn
                             
    	xpoly[clickNumber] = x;
        ypoly[clickNumber] = y;
        // a polygon definition is ended with a double click
        if (clickNumber == 4) {
          	PrimitiveBezier g=new PrimitiveBezier(xpoly[1],
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
                                        0,3,2,0,
                                        P.getTextFont(), 
                                        P.getTextFontSize());

            P.addPrimitive(g, true, ua);
        
    	   	cn = 0;
        }
        return cn;
	}
	/** Introduce a rectangle. You can introduce this with two clicks, so 
    	you should keep track of the number of clicks you received 
    	(clickNumber).
    	You must count the number of clicks and see if there is a modification
    	needed on it (the return value).
    	
    	@param x coordinate of the click (logical)
    	@param ty coordinate of the click (logical)
    	@param clickNumber the click number: 1 is the first click, 2 is the 
    		second (and final) one.
    	@param isSquare force the rectangle to be a square.
    	@return the new value of clickNumber.
    */
	private int addRectangle(int x, int ty, int clickNumber, boolean isSquare)
	{
		int y=ty;
		int cn=clickNumber;
		if(isSquare)
            y=ypoly[1]+x-xpoly[1];
        
        // clickNumber == 0 means that no rectangle is being drawn
           
        xpoly[clickNumber] = x;
        ypoly[clickNumber] = y;
        if (cn == 2) {
            // The second click ends the rectangle introduction.
            // We thus create the primitive and store it.
            PrimitiveRectangle g=new PrimitiveRectangle(xpoly[1],
                                         ypoly[1],
                                         xpoly[2],
                                         ypoly[2],
                                         false,
                                         currentLayer,0,
                                         P.getTextFont(), P.getTextFontSize());

            P.addPrimitive(g, true, ua);
            cn = 0;              
        }
        if (cn>=2) cn = 0;
        return cn;	
	}
	
	/** Introduce a PCB line. You can introduce this with two clicks, so 
    	you should keep track of the number of clicks you received 
    	(clickNumber).
    	You must count the number of clicks and see if there is a modification
    	needed on it (the return value).
    	
    	@param x coordinate of the click (logical)
    	@param ty coordinate of the click (logical)
    	@param clickNumber the click number: 1 is the first click, 2 is the 
    		second (and final) one.
    	@param altButton if true, the introduction of PCBlines should be 
    		stopped.
    	@param thickness the thickness of the PCB line.
    	@return the new value of clickNumber.
    */	
	private int addPCBLine(int x, int y, int clickNumber, boolean altButton,
		float thickness)
	{           
        int cn=clickNumber;
        // clickNumber == 0 means that no pcb line is being drawn
            
        xpoly[cn] = x;
        ypoly[cn] = y;
        if (cn == 2|| altButton) {
            // Here is the end of the PCB line introduction: we create the
            // primitive.
            PrimitivePCBLine g=new PrimitivePCBLine(xpoly[1],
                                         ypoly[1],
                                         xpoly[2],
                                         ypoly[2],
                                         thickness,
                                         currentLayer,
                                         P.getTextFont(), P.getTextFontSize());
            P.addPrimitive(g, true,ua);
                
            // Check if the user has clicked with the right button.
            if(altButton) {
                // We stop the PCB line here
                cn = 0;
            } else {
            	// We then make sort that a new PCB line will be beginning
                // exactly at the same coordinates at which the previous 
                // one was stopped.
                cn = 1;
                xpoly[1] = xpoly[2];
                ypoly[1] = ypoly[2];
            }
        }
        return cn;	
	}
	
	/** Here we analyze and handle the mouse click. The behaviour is 
		different depending on which selection state we are.
		@param x the x coordinate of the click (in screen coordinates)
		@param y the y coordinate of the click (in screen coordinates)
		@param button3 true if the alternate button has been pressed
		@param toggle if true, circle the selection state or activate alternate
		 	input method (i.e. ellipses are forced to be circles, rectangles
		 	squares and so on...)
		@param doubleClick true if a double click has to be processed
		@return true if a repaint is needed.
	*/
    public boolean handleClick(MapCoordinates cs, 
    	int x, int y, boolean button3, boolean toggle,
    	boolean doubleClick)
    {
        String cmd;
        int i;
        GraphicPrimitive g;
		boolean repaint=false;
		
        if(clickNumber>NPOLY-1)
            clickNumber=NPOLY-1;
            
        
  //*************** coordinatesListener.changeInfos("");
	
		// We need to differentiate this case since when we are entering a
		// macro, primEdit already contains some useful hints about the 
		// orientation and the mirroring, so we need to keep it.
		if (actionSelected !=MACRO) 
        	primEdit = null;
        
        // Right-click in certain cases shows the parameters dialog.
        if(button3 && 
            actionSelected!=NONE &&
            actionSelected!=SELECTION &&
            actionSelected!=ZOOM &&
            actionSelected!=TEXT &&
            primitivesParListener!=null){
            
			primitivesParListener.selectAndSetProperties(x,y);
            return false;
        }
        
        switch(actionSelected) {        
        	// No action: ignore
        	case NONE:	
            	clickNumber = 0;
            	break;
                    
        	// Selection state
        	case SELECTION:
            	clickNumber = 0;
            	// Double click shows the Parameters dialog.
            	if(doubleClick&&primitivesParListener!=null) {
					primitivesParListener.setPropertiesForPrimitive();
                	break;
            	} else  // Show a pop up menu if the user does a right-click
            	if(button3 && primitivesParListener!=null) {
					primitivesParListener.showPopUpMenu(x,y);
            	} else {	
            		// Select elements
            		edt.handleSelection(cs, x, y, toggle);
            	}
            	break;
        
        	// Zoom state
        	case ZOOM:
        		if(primitivesParListener!=null) 
					primitivesParListener.changeZoomByStep(!button3, x,y);
            	break;
        
        	// Put a connection (easy: just one click is needed)
        	case CONNECTION:
				addConnection(cs.unmapXsnap(x),cs.unmapXsnap(y));                   
            	repaint=true;
            	break;

        	// Put a PCB pad (easy: just one click is needed)       
        	case PCB_PAD:
            	// Add a PCB pad primitive at the given point
            	g=new PrimitivePCBPad(cs.unmapXsnap(x),
                                  cs.unmapYsnap(y), 
                                  PCB_pad_sizex,
                                  PCB_pad_sizey,                                                                                                                
                                  PCB_pad_drill,
                                  PCB_pad_style,
                                  currentLayer,
                                  P.getTextFont(), P.getTextFontSize());

            	P.addPrimitive(g, true, ua);
            	repaint=true;
            	break;     
         
        	// Add a line: two clicks needed
        	case LINE:
            	if (doubleClick) {
           			clickNumber=0;
        		} else {
        			successiveMove=false;
         			clickNumber=addLine(cs.unmapXsnap(x),
                    	cs.unmapYsnap(y), 
                    	++clickNumber, 
         				button3);
            		repaint=true;
         		}
            	break; 
            
        	// Add a text line: just one click is needed
        	case TEXT:
            	if (doubleClick && primitivesParListener!=null) {
					primitivesParListener.selectAndSetProperties(x,y);
                	break;
            	}
            	PrimitiveAdvText newtext = 
            		new PrimitiveAdvText(cs.unmapXsnap(x),
                                        cs.unmapYsnap(y), 
                                        3,4,P.getTextFont(),0,0,
                                        "String", currentLayer);
            	edt.setSelectionAll(false);
            	P.addPrimitive(newtext, true, ua);
            	newtext.setSelected(true);
            	repaint=true;
            	if(primitivesParListener!=null)
					primitivesParListener.setPropertiesForPrimitive();
            
            	break;
        
        	// Add a Bézier polygonal curve: we need four clicks.
        	case BEZIER:
        		repaint=true;
            	if(button3) {
            		clickNumber = 0;
            	} else {  
            		if(doubleClick) successiveMove=false;
                	clickNumber=addBezier(cs.unmapXsnap(x),    
                            	cs.unmapYsnap(y), ++clickNumber);
            	}
            	break;   
        
        	// Insert a polygon: continue until double click.
        	case POLYGON:        
            	// a polygon definition is ended with a double click
            	if (doubleClick) {
         
                	PrimitivePolygon poly=new PrimitivePolygon(false,
                                         currentLayer,0,
                                         P.getTextFont(), P.getTextFontSize());
                	for(i=1; i<=clickNumber; ++i) 
                    	poly.addPoint(xpoly[i],ypoly[i]);
        
                	P.addPrimitive(poly, true,ua);
                	clickNumber = 0;
                	repaint=true;
                	break;
            	}
            	++ clickNumber;
            	if(doubleClick) successiveMove=false;
            	// clickNumber == 0 means that no polygon is being drawn
				// prevent that we exceed the number of allowed points
            	if (clickNumber==NPOLY)
            		return false;
				// prevent that we exceed the number of allowed points
            	if (clickNumber==NPOLY)
            		return false;            
            	xpoly[clickNumber] = cs.unmapXsnap(x);
            	ypoly[clickNumber] = cs.unmapYsnap(y);
            	break;   
        
        	// Insert a complex curve: continue until double click.
        	case COMPLEXCURVE:     
            	// a polygon definition is ended with a double click
            	if (doubleClick) {
               	 	PrimitiveComplexCurve compc=new PrimitiveComplexCurve(false,
                						false,
                                        currentLayer,
            							false, false, 0, 3, 2, 0,
            							P.getTextFont(), P.getTextFontSize());
                	for(i=1; i<=clickNumber; ++i) 
                    	compc.addPoint(xpoly[i],ypoly[i]);
        
                	P.addPrimitive(compc, true,ua);
                	clickNumber = 0;
                	repaint=true;
                	break;
            	}
            	++ clickNumber;
            	if(doubleClick) successiveMove=false;
            	// prevent that we exceed the number of allowed points
            	if (clickNumber==NPOLY)
            		return false;
            	// clickNumber == 0 means that no polygon is being drawn
            	xpoly[clickNumber] = cs.unmapXsnap(x);
            	ypoly[clickNumber] = cs.unmapYsnap(y);
            	break;   
            
        	// Enter an ellipse: two clicks needed
        	case ELLIPSE:
            	// If control is hold, trace a circle
            	successiveMove=false;

            	clickNumber=addEllipse(cs.unmapXsnap(x), cs.unmapYsnap(y), 
            		++clickNumber,
            		toggle&&clickNumber>0);
				repaint=true;
            	break;   
        
        	// Enter a rectangle: two clicks needed
        	case RECTANGLE:
            	// If control is hold, trace a square
            	successiveMove=false;
            	clickNumber=addRectangle(cs.unmapXsnap(x), cs.unmapYsnap(y),
            		++clickNumber,
            		toggle&&clickNumber>0);
            	repaint=true;
            	break;   
            
        	// Insert a PCB line: two clicks needed.      
        	case PCB_LINE:
				if (doubleClick) {
                	clickNumber = 0;
                	break;
            	}
            	successiveMove=false;
            
            	clickNumber = addPCBLine(cs.unmapXsnap(x), cs.unmapYsnap(y),
            		++clickNumber, 
            		button3,
            		PCB_thickness);
            	repaint=true;
            	break;  
            
        	// Enter a macro: just one click is needed.
        	case MACRO:
        		successiveMove=false;
        		addMacro(cs.unmapXsnap(x), cs.unmapYsnap(y));
        		repaint=true;
            	break;
        	default:
        		break;
        } 
        
    	return repaint;   
    }  

    /** Draws the current editing primitive.
    
    */
    public void drawPrimEdit(GraphicsInterface g, MapCoordinates cs)
    {
    	int x, y;
    	if(primEdit!=null) {
			primEdit.draw(g, cs, StandardLayers.createEditingLayerArray());
		}
    }
    
    /** Shows the clicks done by the user 
    	@param g the graphic context where one should write.
    	@param cs the current coordinate mapping.
    */
    public void showClicks(GraphicsInterface g, MapCoordinates cs)
    {
    	int x, y;
		g.setColor(g.getColor().red());
		// The data here begins at index 1, due to the internal construction.
		
		int mult=(int)Math.round(g.getScreenDensity()/112);
		g.applyStroke(2.0f*mult,0);
		
		for(int i=1; i<=clickNumber; ++i) {
			x = cs.mapXi(xpoly[i], ypoly[i], false);
			y = cs.mapYi(xpoly[i], ypoly[i], false);
			g.drawLine(x-15*mult, y, x+15*mult, y);
			g.drawLine(x, y-15*mult, x, y+15*mult);		
		}    
    }
    
    /** Get the current editing action (see the constants defined in this 
    	class)
    
        @return the current editing action
    */
    public int getSelectionState()
    {
        return actionSelected;
    }  
    
    /** Sets the current editing primitive
    */
    public void setPrimEdit(GraphicPrimitive gp)
    {
    	primEdit=gp;
    }
    
    /** Gets the current editing primitive
    */
    public GraphicPrimitive getPrimEdit(GraphicPrimitive gp)
    {
    	return primEdit;
    }
}