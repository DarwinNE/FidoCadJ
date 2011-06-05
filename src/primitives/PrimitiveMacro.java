package primitives;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.awt.geom.*;

import geom.*;
import circuit.*;
import dialogs.*;
import export.*;
import globals.*;


/** Class to handle the macro primitive. Code is somewhat articulated since
	I use ricorsion (a macro is another drawing seen as an unbreakable symbol.

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

	Copyright 2007-2010 by Davide Bucci
</pre>

@author Davide Bucci
*/

public final class PrimitiveMacro extends GraphicPrimitive
{

	static final int N_POINTS=3;
	private Map library;
	private Vector layers;
	private int o;
	private boolean m; 
	private boolean drawOnlyPads;
	private int drawOnlyLayer;
	private boolean alreadyExported;
	private ParseSchem macro;
	private MapCoordinates macroCoord;
	private boolean selected;
	private String macroName;
	private String macroDesc;
	private boolean exportInvisible;
	
	public void setExportInvisible(boolean s)
	{
		exportInvisible = s;
	}

	
	/** Gets the number of control points used.
		@return the number of points used by the primitive
	*/
	
	public int getControlPointNumber()
	{
		return N_POINTS;
	}
	
	/** Constructor
	
		@param lib the library to be inherited
		@param l the list of layers
	*/
	public PrimitiveMacro(Map lib, Vector l)
	{
		super();
		library=lib;
		layers=l;
		drawOnlyPads=false;
		drawOnlyLayer=-1;
		macro=new ParseSchem();
		macroCoord=new MapCoordinates();
		changed=true;
		
		initPrimitive(-1);
				
		macroStore(layers);
	}
	
	/** Constructor
	
		@param lib the library to be inherited
		@param l the list of layers
		@param x the x coordinate of the control point of the macro
		@param y the y coordinate of the control point of the macro
		@param key the key to be used to uniquely identify the macro (it will 
			be converted to lowercase)
		@param na the name to be shown
		@param xa the x coordinate of the name of the macro
		@param ya the y coordinate of the name of the macro
		@param va the value to be shown
		@param xv the x coordinate of the value of the macro
		@param yv the y coordinate of the value of the macro
		@param macroF the font to be used for the name and the value of the 
			macro
		@param macroS the size of the font
		@param oo the macro orientation
		@param mm the macro mirroring
	*/	
	public PrimitiveMacro(Map lib, Vector l, int x, int y, String key, 
		 String na, int xa, int ya, String va, int xv, int yv, String macroF, int macroS,
		 int oo, boolean mm)
		throws IOException
	{
		super();
		library=lib;
		layers=l;
		key=key.toLowerCase();
		macro=new ParseSchem();
		macroCoord=new MapCoordinates();
		changed=true;
		macroFontSize = macroS;
		o=oo;
		m=mm;
		
		initPrimitive(-1);
		
		// Store the points of the macro and the text describing it.
		virtualPoint[0].x=x;
		virtualPoint[0].y=y;
		virtualPoint[1].x=xa;
		virtualPoint[1].y=ya;
		virtualPoint[2].x=xv;
		virtualPoint[2].y=yv;
		
		name=na;
		value=va;
		
		MacroDesc macro=(MacroDesc)library.get(key);
 		
 		// Check if the macro description is contained in the database
 		// containing all the libraries.
 		if (macro==null){	
 			IOException G=new IOException("Unrecognized macro " 
 										  + key);
			throw G;
 		}
 		macroDesc = macro.description;
 		macroName = key;
 		macroFont = macroF;
 		
 		macroStore(layers);
 					
	}
	
	public boolean containsLayer(int l)
 	{
 		return macro.containsLayer(l);
 	}
	
	
	

	int x1, y1;
	/**	Draw the macro contents
	
	*/
	final private void drawMacroContents(Graphics2D g, MapCoordinates coordSys,
							  Vector layerV)
	{
		/* in the macro primitive, the the virtual point represents
		   the position of the reference point of the macro to be drawn. */
		
		if(changed) {
			changed = false;
			x1=virtualPoint[0].x;
 			y1=virtualPoint[0].y;
 		
 				
 			macroCoord.setXMagnitude(coordSys.getXMagnitude());
			macroCoord.setYMagnitude(coordSys.getYMagnitude());
		 		
 			macroCoord.setXCenter(coordSys.mapXr(x1,y1));
 			macroCoord.setYCenter(coordSys.mapYr(x1,y1));
			macroCoord.orientation=(o+coordSys.orientation)%4;
			macroCoord.mirror=m ^ coordSys.mirror;
 			macroCoord.isMacro=true;
 			macroCoord.resetMinMax();
 		 		 			
 			macro.setMapCoordinates(macroCoord);
			macro.setChanged(true);
		}
		
		if(getSelected()) {
 			macro.selectAll();
 			selected = true;
		} else if (selected) {
			macro.deselectAll();
			selected = false;
		}

		macro.setDrawOnlyLayer(drawOnlyLayer);
 		macro.setDrawOnlyPads(drawOnlyPads);
 		
		macro.draw(g);
		
		if (macroCoord.getXMax()>macroCoord.getXMin() && 
			macroCoord.getYMax()>macroCoord.getYMin()) {
			coordSys.trackPoint(macroCoord.getXMax(),macroCoord.getYMax());
			coordSys.trackPoint(macroCoord.getXMin(),macroCoord.getYMin());	
		}
	}
	
	/** Specifies that the current primitive has been modified or not. 
		If it is true, during the redraw all parameters should be calulated
		from scratch. 
	*/
    public void setChanged(boolean c)
    {
    	super.setChanged(c);
    	macro.setChanged(c);
    }
	
	/** Parse and store the tokenized version of the macro.
		@layerV the array containing the layer description to be inherited.
	
	*/
	private void macroStore(Vector layerV)
	{
	 	macro.setLibrary(library); 			// Inherit the library
 		macro.setLayers(layerV);	// Inherit the layers
 		changed=true;	
		
 		if (macroDesc!=null) {
 			try {
 				macro.parseString(new StringBuffer(macroDesc)); 
 				// Recursive call	
 			} catch(IOException e) {
                	System.out.println("Error: "+e); 
            }
 		}
	}
	
	/** Draw the graphic primitive on the given graphic context.
	
		@param g the graphic context in which the primitive should be drawn.
		@param coordSys the graphic coordinates system to be applied.
		@param layerV the layer description.
	*/
	final public void draw(Graphics2D g, MapCoordinates coordSys,
							  Vector layerV)
	{
	
		if(selectLayer(g,layerV))
			drawText(g, coordSys, layerV, drawOnlyLayer);
		
		drawMacroContents(g, coordSys, layerV);
 	}
	
	/** Set the Draw Only Pads mode.
	
		@param pd the wanted value
	
	*/
								  
 	final public void setDrawOnlyPads(boolean pd)
 	{
 		drawOnlyPads=pd;
 	}
	
	/** Set the Draw Only Layer mode.
	
		@param la the layer that should be drawn.
	
	*/
	
	final public void setDrawOnlyLayer(int la)
 	{
 		drawOnlyLayer=la;
 	}
	
	public int getMaxLayer()
    {
    	return macro.getMaxLayer();
    }
	
	/**	Parse a token array and store the graphic data for a given primitive
		Obviously, that routine should be called *after* having recognized
		that the called primitive is correct.
		That routine also sets the current layer.
		
		@param tokens the tokens to be processed. tokens[0] should be the
		command of the actual primitive.
		@param N the number of tokens present in the array
		
	*/
	
	public void parseTokens(String[] tokens, int N)
		throws IOException
	{
		// assert it is the correct primitive
		changed=true;	
		if (tokens[0].equals("MC")) {	// Line
 			if (N<6) {
 				IOException E=new IOException("bad arguments on MC");
				throw E;
 			}
 			// Load the points in the virtual points associated to the 
 			// current primitive.

 			virtualPoint[0].x=Integer.parseInt(tokens[1]);
 			virtualPoint[0].y=Integer.parseInt(tokens[2]);
 			virtualPoint[1].x=virtualPoint[0].x+10;
			virtualPoint[1].y=virtualPoint[0].y+10;
			virtualPoint[2].x=virtualPoint[0].x+10;
			virtualPoint[2].y=virtualPoint[0].y+5;
 			o=Integer.parseInt(tokens[3]);  // orientation
 			m=(Integer.parseInt(tokens[4])==1);  // mirror
 			macroName=tokens[5];
 			
 			// This is useful when a filename contains spaces. However, it does
 			// not work when there are two or more consecutive spaces.
 			
 			for (int i=6; i<N; ++i) 
 				macroName+=" "+tokens[i];
 			
 			// The macro key recognition is made case insensitive by converting
 			// internally all keys to lower case. 
 			
      		macroName=macroName.toLowerCase();

			// Let's see if the macro is recognized and store it.
 			MacroDesc macro=(MacroDesc)library.get(macroName);
 			
 			if (macro==null){
 			
 				IOException G=new IOException("Unrecognized macro '" 
 											  + macroName+"'");
				throw G;
 			}
 			macroDesc = macro.description;
 			macroStore(layers);	
 				
 		} else {
 			IOException E=new IOException("MC: Invalid primitive:"+tokens[0]+
 										  " programming error?");
			throw E;
 		}
		
	}
	
	/** Check if the macro contains elements which need to draw holes.
		@return true if the macro contains elements requiring holes, false 
			otherwise.
	
	*/
	public final boolean needsHoles()
	{	
		return macro.getNeedHoles();
	}
	
	/** Gets the distance (in primitive's coordinates space) between a 
	    given point and the primitive. 
	    When it is reasonable, the behaviour can be binary (polygons, 
	    ovals...). In other cases (lines, points), it can be proportional.
		@param px the x coordinate of the given point
		@param py the y coordinate of the given point
	*/
	public int getDistanceToPoint(int px, int py)
	{
		/* in the macro primitive, the the first virtual point represents
		   the position of the reference point of the macro to be drawn. */
		   
		int x1=virtualPoint[0].x;
 		int y1=virtualPoint[0].y;
 		int dt=Integer.MAX_VALUE;
	    
	    // Here we check if the given point lies inside the text areas
        
	    if(checkText(px, py))
	    	return 0;
	    
		// If not, we need to see more throughly about the inners of the macro

 		int vx=px-x1+100;
 		int vy= py-y1+100;
 		
		// This is a sort of inelegant code: we need to translate the position
		// given in the macro's coordinate system.
 		
 		if(m) {
            switch(o){
                case 1:
                    vx=py-y1+100;
                    vy=px-x1+100;
                    
                    break;
                
                case 2:
                    vx=px-x1+100;
                    vy=-(py-y1)+100;
                    break;
                
                case 3:
                    vx=-(py-y1)+100;
                    vy=-(px-x1)+100;
                    break;
    
                case 0:
                    vx=-(px-x1)+100;
                    vy=py-y1+100;
                    break;
    
                default:
                    vx=0;
                    vy=0;
            }
        } else {
            switch(o){
                case 1:
                    vx=(py-y1)+100;
                    vy=-(px-x1)+100;
                    break;
                
                case 2:
                    vx=-(px-x1)+100;
                    vy=-(py-y1)+100;
                    break;
                
                case 3:
                    vx=-(py-y1)+100;
                    vy=(px-x1)+100;
                    break;
    
                case 0:
                    vx=px-x1+100;
                    vy=py-y1+100;
                    break;
    
                default:
                    vx= 0;
                    vy= 0;
            }
        }   
 		
 		if (macroDesc==null)
 			System.out.println("1-Unrecognized macro "+
 			        "WARNING this can be a programming problem...");
 		else {

 			return Math.min(macro.distancePrimitive(vx, vy), dt);
		}
		return Integer.MAX_VALUE;
	}
	
	/**	Select the primitive if one of its virtual point is in the specified
		rectangular region (given in logical coordinates).
        @param px the x coordinate of the top left point.
        @param py the y coordinate of the top left point.
        @param w the width of the region
        @param h the height of the region
        @return true if at least a primitive has been selected
    */
    public boolean selectRect(int px, int py, int w, int h)
	{
		// Here is a trick: if there is at least one active layer, 
		// distancePrimitive will return a value less than the maximum.
				
		if (macro.distancePrimitive(0, 0)<Integer.MAX_VALUE) 
			return super.selectRect(px, py, w, h);
		else
			return false;
	}
	
	/** Get the macro orientation
		@return the orientation. 
	*/
	public int getOrientation()
	{
		return o;
	}
	
	/** Determine wether the macro is mirrored or not
		@return true if the macro is mirrored
	
	*/
	public boolean isMirrored()
	{
		return m;
	}
	
	/** Rotate the primitive. For a macro, it is different than for the other
		primitive, since we need to rotate its coordinate system.
		@param bCounterClockWise specify if the rotation should be done 
				counterclockwise.
		@param ix the x coordinate of the center of rotation
		@param iy the y coordinate of the center of rotation
	*/
	public void rotatePrimitive(boolean bCounterClockWise,int ix, int iy)
	{
		super.rotatePrimitive(bCounterClockWise, ix, iy);
		
		if (!bCounterClockWise)
			o=++o%4;
		else
			o=(o+3)%4;
		changed=true;
	}
	
	
	/** Mirror the primitive. For a macro, it is different than for the other
		primitive, since we just need to toggle the mirror flag.
		
	*/
	public void mirrorPrimitive(int xpos)
	{
		super.mirrorPrimitive(xpos);
		m ^= true;
		changed=true;
	}
	
	/** Obtain a string command descripion of the primitive.
		@return the FIDOCAD command line.
	*/
	public String toString(boolean extensions)
	{
		String mirror="0";
		if(m)	
			mirror="1";
			
		String s="MC "+virtualPoint[0].x+" "+virtualPoint[0].y+" "+o+" "
				+mirror+" "+macroName+"\n";
				
		s+=saveText(extensions);
		
		return s;
	}
	
	/**	Get the control parameters of the given primitive.
	
		@return a vector of ParameterDescription containing each control
				parameter.
				The first parameters should always be the virtual points.
				
	*/
	public Vector getControls()
	{
		Vector v=new Vector(10);
		ParameterDescription pd = new ParameterDescription();

		pd.parameter=name;
		pd.description=Globals.messages.getString("ctrl_name");
		pd.isExtension = true;
		v.add(pd);
		
		pd = new ParameterDescription();
		
		pd.parameter=value;
		pd.description=Globals.messages.getString("ctrl_value");
		pd.isExtension = true;

		v.add(pd);
		
		pd = new ParameterDescription();
		pd.parameter=new Integer(getLayer());
		pd.description=Globals.messages.getString("ctrl_layer");
		v.add(pd);
		
		pd = new ParameterDescription();
		pd.parameter=virtualPoint[0];
		pd.description=Globals.messages.getString("ctrl_control");
		v.add(pd);
		
		pd = new ParameterDescription();
		pd.parameter=virtualPoint[1];
		pd.description=Globals.messages.getString("ctrl_name_point");
		v.add(pd);
		
		pd = new ParameterDescription();
		pd.parameter=virtualPoint[2];
		pd.description=Globals.messages.getString("ctrl_value_point");
		v.add(pd);
		
		
	
		return v;
	}
	/**	Set the control parameters of the given primitive.
		This method is specular to getControls().
		
		@param v a vector of ParameterDescription containing each control
				parameter.
				The first parameters should always be the virtual points.
				
	*/

	public void setControls(Vector v)
	{
		int i=0;		
		ParameterDescription pd;
		
		changed=true;	
		
		pd=(ParameterDescription)v.get(i);
		++i;
		// Check, just for sure...
		if (pd.parameter instanceof String)
			name=((String)pd.parameter);
		else
		 	System.out.println("Warning: unexpected parameter!"+pd);
		
		pd=(ParameterDescription)v.get(i);
		++i;
		// Check, just for sure...
		if (pd.parameter instanceof String)
			value=((String)pd.parameter);
		else
		 	System.out.println("Warning: unexpected parameter!"+pd);
		 	
		pd = (ParameterDescription)v.get(i);
		++i;
		// Check, just for sure...
		if (pd.parameter instanceof Integer)
			setLayer(((Integer)pd.parameter).intValue());
		else
		 	System.out.println("Warning: unexpected parameter!");
		
		
		
		pd = (ParameterDescription)v.get(i);
		++i;
		// Check, just for sure...
		if (pd.parameter instanceof Point)
			virtualPoint[0]=(Point)pd.parameter;
		else
		 	System.out.println("Warning: unexpected parameter!");
		 	
		pd = (ParameterDescription)v.get(i);
		++i;
		// Check, just for sure...
		if (pd.parameter instanceof Point)
			virtualPoint[1]=(Point)pd.parameter;
		else
		 	System.out.println("Warning: unexpected parameter!");
		
		pd = (ParameterDescription)v.get(i);
		++i;
		// Check, just for sure...
		if (pd.parameter instanceof Point)
			virtualPoint[2]=(Point)pd.parameter;
		else
		 	System.out.println("Warning: unexpected parameter!");
		
	}
	
	
	/** Ensure that the next time the macro is exported, it will be done.
		Macro that are not expanded during exportation does not need to be
		replicated thru the layers. For this reason, there is an inibition system which is activated. Calling this method resets the inibition flag.
	*/
	public void resetExport()
	{
		alreadyExported=false;
	}
	
	/**	Each graphic primitive should call the appropriate exporting method
		of the export interface specified.
		
		@param exp the export interface that should be used
		@param cs the actual coordinate mapping
	*/
	public void export(ExportInterface exp, MapCoordinates cs) 
		throws IOException
	{
	
		if(alreadyExported)
			return;
			
		// Call the macro interface, to see if the macro should be expanded
		
		if (exp.exportMacro(virtualPoint[0].x, virtualPoint[0].y, 
			m, o*90, macroName, macroDesc, name, virtualPoint[1].x, 
			virtualPoint[1].y, value, virtualPoint[2].x, virtualPoint[2].y, 
			macroFont, macroFontSize,library)) {
			alreadyExported = true;
			return;
		}
		/* in the macro primitive, the virtual point represents
		   the position of the reference point of the macro to be drawn. */
		   
		int x1=virtualPoint[0].x;
 		int y1=virtualPoint[0].y;
 		
		MapCoordinates macroCoord=new MapCoordinates();

		macroCoord.setXMagnitude(cs.getXMagnitude());
		macroCoord.setYMagnitude(cs.getYMagnitude());

 		macroCoord.setXCenter(cs.mapXr(x1,y1));
		macroCoord.setYCenter(cs.mapYr(x1,y1));

		macroCoord.orientation=(o+cs.orientation)%4;
		macroCoord.mirror=m ^ cs.mirror;
 		macroCoord.isMacro=true;
 				 			
 		ParseSchem macro=new ParseSchem();
 		macro.setMapCoordinates(macroCoord);
 		macro.setDrawOnlyLayer(drawOnlyLayer);

 		macro.setLibrary(library);  // Inherit the library
 		macro.setLayers(layers);	// Inherit the layers
 		
 		if (macroDesc==null)
 			System.out.println("2-Unrecognized macro "+
 			        "WARNING this can be a programming problem...");
 		else {
 			// Recursive call
 			macro.parseString(new StringBuffer(macroDesc)); 	
 			// Propagate selection state
 			if(getSelected())
 				macro.selectAll();
 			 
 			macro.setDrawOnlyPads(drawOnlyPads);
 			macro.exportDrawing(exp, false, exportInvisible);
			exportText(exp, cs, drawOnlyLayer);
		}
		
	}
		/** Get the number of the virtual point associated to the Name property
		@return the number of the virtual point associated to the Name property
	*/
	public int getNameVirtualPointNumber()
	{
		return 1;
	}
	
	/** Get the number of the virtual point associated to the Value property
		@return the number of the virtual point associated to the Value property
	*/
	public  int getValueVirtualPointNumber()
	{
		return 2;
	}
	
}