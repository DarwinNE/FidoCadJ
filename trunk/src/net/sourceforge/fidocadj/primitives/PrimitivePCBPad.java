package net.sourceforge.fidocadj.primitives;

import java.io.*;
import java.util.*;

import net.sourceforge.fidocadj.dialogs.*;
import net.sourceforge.fidocadj.export.*;
import net.sourceforge.fidocadj.geom.*;
import net.sourceforge.fidocadj.globals.*;
import graphic.*;

/** Class to handle the PCB pad primitive.

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

@author Davide Bucci
*/

public final class PrimitivePCBPad extends GraphicPrimitive
{

	private int rx;
	private int ry;
	private int sty;
	private int ri;
	
	// The radius of the rounded corner in logical units. This is hardcoded
	// here as it has been done for FidoCadJ, but one may consider let this 
	// value to be changed by the user interactively
	private static final int CORNER_DIAMETER = 5;
	
	private boolean drawOnlyPads;
	
	// A PCB pad is defined by one points, plus text tags

	static final int N_POINTS=3;
	
	// Those are data which are kept for the fast redraw of this primitive. 
	// Basically, they are calculated once and then used as much as possible
	// without having to calculate everything from scratch.
	private int x1, y1, rrx, rry, xa,ya, rox, roy, rix, riy;
	private int rrx2, rry2, rix2, riy2;
	
	/** Gets the number of control points used.
		@return the number of points used by the primitive
	*/
	public int getControlPointNumber()
	{
		return N_POINTS;
	}
	
	/** Create a PCB pad
		@param x1 the x coordinate (logical unit).
		@param y1 the y coordinate (logical unit).
		@param wx the width of the pad
		@param wy the height of the pad
		@param st the style of the pad
		@param layer the layer to be used.
		
	*/
	
	public PrimitivePCBPad(int x1, int y1, int wx, int wy, int radi, int st, 
		int layer, String f, int size)
	{
		super();
		initPrimitive(-1, f, size);

		virtualPoint[0].x=x1;
		virtualPoint[0].y=y1;
		virtualPoint[getNameVirtualPointNumber()].x=x1+5;
		virtualPoint[getNameVirtualPointNumber()].y=y1+5;
		virtualPoint[getValueVirtualPointNumber()].x=x1+5;
		virtualPoint[getValueVirtualPointNumber()].y=y1+10;	
		
		rx=wx;
		ry=wy;
		ri=radi;
		sty=st;
		
		setLayer(layer);	
	}
	

	
	public PrimitivePCBPad(String f, int size)
	{
		super();
		rx=0;
		ry=0;
		sty=0;
		ri=0;
		initPrimitive(-1, f, size);
	}

	public boolean needsHoles()
	{	
		return true;
	}

	/** Draw the graphic primitive on the given graphic context.
		@param g the graphic context in which the primitive should be drawn.
		@param coordSys the graphic coordinates system to be applied.
		@param layerV the layer description.
	*/
	public void draw(GraphicsInterface g, MapCoordinates coordSys,
							  Vector layerV)
	{
	
		if(!selectLayer(g,layerV))
			return;
		drawText(g, coordSys, layerV, -1);

		if(changed) {
			changed=false;
		
			/* in the PCB pad primitive, the the virtual points represent
		   the position of the pad to be drawn. */
			x1=virtualPoint[0].x;
 			y1=virtualPoint[0].y;
 		
 			rrx=Math.abs(coordSys.mapXi(x1,y1, false)-
 				coordSys.mapXi(x1+rx,y1+ry, false));
 			rry=Math.abs(coordSys.mapYi(x1,y1, false)-
 				coordSys.mapYi(x1+rx,y1+ry, false));
 			rrx2 = rrx/2;
 			rry2 = rry/2;
 		
 			xa=coordSys.mapX(x1,y1);
 			ya=coordSys.mapY(x1,y1);
 			
 			coordSys.trackPoint(x1-rrx,y1-rry);
 			coordSys.trackPoint(x1+rrx,y1+rry);
 		
 			rox=Math.abs(xa-coordSys.mapXi(x1+CORNER_DIAMETER,
 			 	y1+CORNER_DIAMETER, false));
 			roy=Math.abs(ya-coordSys.mapYi(x1+CORNER_DIAMETER,
 				y1+CORNER_DIAMETER, false));
 		
 			rix=Math.abs(xa-coordSys.mapXi(x1+ri,y1+ri, false));
 			riy=Math.abs(ya-coordSys.mapYi(x1+ri,y1+ri, false));
 			
 			rix2 = rix/2;
 			riy2 = riy/2;
 		}
 		
 		// Exit if the primitive is offscreen. This is a simplification, but
 		// ensures that the primitive is correctly drawn when it is 
 		// partially visible.
 		
 		if(!g.hitClip(xa-rrx2,ya-rry2, rrx, rry))
 			return;
 		
 		g.applyStroke(1, 0);
 		
 		if (drawOnlyPads) {
 			g.setColor(g.getColor().white()); // Drill the hole
 			g.fillOval(xa-rix2, ya-riy2,rix,riy);
 		} else {
 			switch(sty) {
 			case 1:
 				// Rectangular pad 
 				g.fillRect(xa-rrx2, ya-rry2,rrx,rry);
 				break;
 			case 2:
 				// Rounded corner rectangular pad 
 				g.fillRoundRect(xa-rrx2, ya-rry2,rrx,rry,rox,roy);
 				break;
 			case 0: //NOPMD
 			default:
 				// Oval Pad 
 				g.fillOval(xa-rrx2, ya-rry2,rrx,rry);
 				break;
 			}
 		} 
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
		changed=true;

		// assert it is the correct primitive
		
		if (tokens[0].equals("PA")) {	// PCB Area pad 
 			/* Example PA 752 50 15 15 4 1 1 */
 			
 			if (N<7)  {
 				IOException E=new IOException("bad arguments on PA");
				throw E;
 			}
 			// Load the points in the virtual points associated to the 
 			// current primitive.

 			int x1 = virtualPoint[0].x=Integer.parseInt(tokens[1]);
 			int y1 = virtualPoint[0].y=Integer.parseInt(tokens[2]);
  			virtualPoint[getNameVirtualPointNumber()].x=x1+5;
			virtualPoint[getNameVirtualPointNumber()].y=y1+5;
			virtualPoint[getValueVirtualPointNumber()].x=x1+5;
			virtualPoint[getValueVirtualPointNumber()].y=y1+10;					
 			rx=Integer.parseInt(tokens[3]);
 			ry=Integer.parseInt(tokens[4]);
 			ri=Integer.parseInt(tokens[5]);
 			sty=Integer.parseInt(tokens[6]);
 			
 			if(N>7) parseLayer(tokens[7]);

 			
 		} else {
 			IOException E=new IOException("PA: Invalid primitive:"+tokens[0]+
 										  " programming error?");
			throw E;
 		}
		
 			
		
	}
	
	public void setDrawOnlyPads(boolean pd)
 	{
 		drawOnlyPads=pd;
 	}
 	
	/**	Get the control parameters of the given primitive. 
	
		@return a vector of ParameterDescription containing each control
				parameter.
				The first parameters should always be the virtual points.
				
	*/
	public Vector<ParameterDescription> getControls()
	{
		Vector<ParameterDescription> v=super.getControls();
		ParameterDescription pd = new ParameterDescription();

		pd.parameter= Integer.valueOf(rx);
		pd.description=Globals.messages.getString("ctrl_x_radius");
		v.add(pd);
		pd = new ParameterDescription();
		pd.parameter= Integer.valueOf(ry);
		pd.description=Globals.messages.getString("ctrl_y_radius");
		v.add(pd);
		pd = new ParameterDescription();
		pd.parameter= Integer.valueOf(ri);
		pd.description=Globals.messages.getString("ctrl_internal_radius");
		v.add(pd);
		pd = new ParameterDescription();
		pd.parameter= Integer.valueOf(sty);	// A list should be better
		pd.description=Globals.messages.getString("ctrl_pad_style");
		v.add(pd);
		
		return v;
	}
	
	/**	Set the control parameters of the given primitive.
		This method is specular to getControls().
		
		@param v a vector of ParameterDescription containing each control
				parameter.
				The first parameters should always be the virtual points.
				
	*/
	public int setControls(Vector<ParameterDescription> v)
	{
		int i=super.setControls(v);			
		ParameterDescription pd;
		
		pd=(ParameterDescription)v.get(i);
		++i;
		// Check, just for sure...
		if (pd.parameter instanceof Integer)
			rx=((Integer)pd.parameter).intValue();
		else
		 	System.out.println("Warning: unexpected parameter!"+pd);
		 	
		pd=(ParameterDescription)v.get(i);
		++i;
		// Check, just for sure...
		if (pd.parameter instanceof Integer)
			ry=((Integer)pd.parameter).intValue();
		else
		 	System.out.println("Warning: unexpected parameter!"+pd);
		 	
		pd=(ParameterDescription)v.get(i);
		++i;
		// Check, just for sure...
		if (pd.parameter instanceof Integer)
			ri=((Integer)pd.parameter).intValue();
		else
		 	System.out.println("Warning: unexpected parameter!"+pd);
		 pd=(ParameterDescription)v.get(i);
		++i;
		// Check, just for sure...
		if (pd.parameter instanceof Integer)
			sty=((Integer)pd.parameter).intValue();
		else
		 	System.out.println("Warning: unexpected parameter!"+pd);
		return i;
	}

	/** Rotate the primitive. Here we just rotate 90° by 90° by swapping the
		x and y size of the pad
	
		@param bCounterClockWise specify if the rotation should be done 
				counterclockwise.
	*/
	public void rotatePrimitive(boolean bCounterClockWise, int ix, int iy)
	{
		super.rotatePrimitive(bCounterClockWise, ix, iy);
		int swap=rx;
		rx=ry;
		ry=swap;
		
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
	    // Here we check if the given point lies inside the text areas
        
	    if(checkText(px, py))
	    	return 0;
	    	
		int distance=GeometricDistances.pointToPoint(
				virtualPoint[0].x,virtualPoint[0].y,
				px,py)-Math.min(rx,ry)/2;
		return distance>0?distance:0;
	}
	
	/** Obtain a string command descripion of the primitive.
		@return the FIDOCAD command line.
	*/
	public String toString(boolean extensions)
	{
		String s = "PA "+virtualPoint[0].x+" "+virtualPoint[0].y+" "+
			rx+" "+ry+" "+ri+" "+sty+" "+getLayer()+"\n";
		
		s+=saveText(extensions);
		return s;
	}
	
	public void export(ExportInterface exp, MapCoordinates cs)
		throws IOException
	{
		exportText(exp, cs, -1);
		exp.exportPCBPad(cs.mapX(virtualPoint[0].x,virtualPoint[0].y),
					   cs.mapY(virtualPoint[0].x,virtualPoint[0].y), 
					   sty, 
					   Math.abs(cs.mapX(virtualPoint[0].x+rx,
					   		virtualPoint[0].y+ry)-
					   cs.mapX(virtualPoint[0].x,virtualPoint[0].y)),
					   Math.abs(cs.mapY(virtualPoint[0].x+rx,
					   		virtualPoint[0].y+ry)-
					   cs.mapY(virtualPoint[0].x,virtualPoint[0].y)),
					   (int)(ri*cs.getXMagnitude()), getLayer(),drawOnlyPads);
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