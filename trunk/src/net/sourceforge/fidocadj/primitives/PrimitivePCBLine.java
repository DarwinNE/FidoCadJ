package net.sourceforge.fidocadj.primitives;

import java.io.*;
import java.util.*;

import geom.*;
import net.sourceforge.fidocadj.dialogs.*;
import net.sourceforge.fidocadj.export.*;
import globals.*;
import graphic.*;

/** Class to handle the PCB line primitive.

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

public final class PrimitivePCBLine extends GraphicPrimitive
{

	private float width;

	// A PCB segment is defined by two points.

	static final int N_POINTS=4;
	
	// Those are data which are kept for the fast redraw of this primitive. 
	// Basically, they are calculated once and then used as much as possible
	// without having to calculate everything from scratch.
	private int xa, ya, xb, yb;
	private int x1, y1,x2,y2; 		
	private int wi_pix;
	private int xbpap1, ybpap1;
	
	/** Gets the number of control points used.
		@return the number of points used by the primitive
	*/
	public int getControlPointNumber()
	{
		return N_POINTS;
	}
	
	/** Standard constructor.
	*/
	public PrimitivePCBLine(String f, int size)
	{
		super();
		width=0;
		initPrimitive(-1, f, size);
	}
	/** Create a PCB line between two points
		@param x1 the start x coordinate (logical unit).
		@param y1 the start y coordinate (logical unit).
		@param x2 the end x coordinate (logical unit).
		@param y2 the end y coordinate (logical unit).
		@param w specifies the line width. 
		@param layer the layer to be used.
	*/
	public PrimitivePCBLine(int x1, int y1, int x2, int y2, float w, int layer,
			String f, int size)
	{
		super();
		initPrimitive(-1, f, size);
			
		virtualPoint[0].x=x1;
		virtualPoint[0].y=y1;
		virtualPoint[1].x=x2;
		virtualPoint[1].y=y2;
		virtualPoint[getNameVirtualPointNumber()].x=x1+5;
		virtualPoint[getNameVirtualPointNumber()].y=y1+5;
		virtualPoint[getValueVirtualPointNumber()].x=x1+5;
		virtualPoint[getValueVirtualPointNumber()].y=y1+10;	
		width=w;
		
		setLayer(layer);
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
		
		/* in the PCB line primitive, the first two virtual points represent
		   the beginning and the end of the segment to be drawn. */
		   
		if(changed) {
			changed=false;
			x1=coordSys.mapX(virtualPoint[0].x,virtualPoint[0].y);
 			y1=coordSys.mapY(virtualPoint[0].x,virtualPoint[0].y);
 			x2=coordSys.mapX(virtualPoint[1].x,virtualPoint[1].y);
 			y2=coordSys.mapY(virtualPoint[1].x,virtualPoint[1].y);
 			wi_pix=Math.abs(coordSys.mapXi(virtualPoint[0].x,
 				virtualPoint[0].y, false)
		    -coordSys.mapXi((int)(virtualPoint[0].x+width),
		    	(int)(virtualPoint[0].y+width), false));
		
 			xa=Math.min(x1, x2)-wi_pix/2;
 			ya=Math.min(y1, y2)-wi_pix/2;
 			xb=Math.max(x1, x2)+wi_pix/2;
 			yb=Math.max(y1, y2)+wi_pix/2;
 			
 			coordSys.trackPoint(xa,ya);
 			coordSys.trackPoint(xb,yb);

			xbpap1=xb-xa+1;
			ybpap1=yb-ya+1;	
		}
 		   
		// Exit if the primitive is offscreen. This is a simplification, but
 		// ensures that the primitive is correctly drawn when it is 
 		// partially visible.
 				
 		if(!g.hitClip(xa,ya, xbpap1,ybpap1))
 			return;
	
		g.applyStroke(wi_pix, 0);	
		g.drawLine(x1, y1, x2, y2);
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
		
		if (tokens[0].equals("PL")) {	// Line
 			if (N<6) {
 				IOException E=new IOException("bad arguments on PL");
				throw E;
 			}
 			// Load the points in the virtual points associated to the 
 			// current primitive.

 			int x1 = virtualPoint[0].x=Integer.parseInt(tokens[1]);
 			int y1 = virtualPoint[0].y=Integer.parseInt(tokens[2]);
 			virtualPoint[1].x=Integer.parseInt(tokens[3]);
 			virtualPoint[1].y=Integer.parseInt(tokens[4]);
 			
 			virtualPoint[getNameVirtualPointNumber()].x=x1+5;
			virtualPoint[getNameVirtualPointNumber()].y=y1+5;
			virtualPoint[getValueVirtualPointNumber()].x=x1+5;
			virtualPoint[getValueVirtualPointNumber()].y=y1+10;		
 			
 			width=Float.parseFloat(tokens[5]);
 			if(N>6) parseLayer(tokens[6]);

 			
 		} else {
 			IOException E=new IOException("PL: Invalid primitive:"+tokens[0]+
 										  " programming error?");
			throw E;
 		}
		
		
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

		pd.parameter= Float.valueOf(width);
		pd.description=Globals.messages.getString("ctrl_width");
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
		if (pd.parameter instanceof Float)
			width=((Float)pd.parameter).floatValue();
		else
		 	System.out.println("Warning: unexpected parameter!"+pd);
		
		return i;
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

		int distance=(int)(GeometricDistances.pointToSegment(
				virtualPoint[0].x,virtualPoint[0].y,
				virtualPoint[1].x,virtualPoint[1].y,
				px,py)-width/2.0f);
            
        return distance<0?0:distance;
	}
	
	/** Obtain a string command descripion of the primitive.
		@return the FIDOCAD command line.
	*/
	public String toString(boolean extensions)
	{
		String s= "PL "+virtualPoint[0].x+" "+virtualPoint[0].y+" "+
			+virtualPoint[1].x+" "+virtualPoint[1].y+" "+(int)width+" "+
			getLayer()+"\n";
			
		
		s+=saveText(extensions);
		
		return s;	
	}
	
	public void export(ExportInterface exp, MapCoordinates cs)
		throws IOException
	{
		exportText(exp, cs, -1);
		exp.exportPCBLine(cs.mapX(virtualPoint[0].x,virtualPoint[0].y),
				cs.mapY(virtualPoint[0].x,virtualPoint[0].y), 
				cs.mapX(virtualPoint[1].x,virtualPoint[1].y),
				cs.mapY(virtualPoint[1].x,virtualPoint[1].y), 
				(int)(width*cs.getXMagnitude()), getLayer()); 
	}
	/** Get the number of the virtual point associated to the Name property
		@return the number of the virtual point associated to the Name property
	*/
	public int getNameVirtualPointNumber()
	{
		return 2;
	}
	
	/** Get the number of the virtual point associated to the Value property
		@return the number of the virtual point associated to the Value property
	*/
	public  int getValueVirtualPointNumber()
	{
		return 3;
	}
	
}