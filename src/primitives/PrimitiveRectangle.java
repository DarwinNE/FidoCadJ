package primitives;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.awt.geom.*;

import globals.*;
import geom.*;
import dialogs.*;
import export.*;


/** Class to handle the rectangle primitive.

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

public class PrimitiveRectangle extends GraphicPrimitive
{

	
	// A rectangle is defined by two points.
	static final int N_POINTS=2;
	// The state: filled or not.
	private boolean isFilled;
	// The dashing style. 
	private int dashStyle;
	
	// This is the value which is given for the distance calculation when the
	// user clicks inside the rectangle:
	static final int DISTANCE_IN = 1;
	
	// This is the value given instead when the clicks is done outside:
	static final int DISTANCE_OUT = 1000;
	
	
	/** Gets the number of control points used.
		@return the number of points used by the primitive
	*/
	public int getControlPointNumber()
	{
		return N_POINTS;
	}
	
	/** Standard constructor.
	*/
	public PrimitiveRectangle()
	{
		super();
		isFilled=false;
		
		virtualPoint = new Point[N_POINTS];
		for(int i=0;i<N_POINTS;++i)
			virtualPoint[i]=new Point();
		changed=true;
		
	}
	/** Create a rectangle defined by two points
		@param x1 the start x coordinate (logical unit).
		@param y1 the start y coordinate (logical unit).
		@param x2 the end x coordinate (logical unit).
		@param y2 the end y coordinate (logical unit).
		@param f specifies if the rectangle should be filled.
		@param layer the layer to be used.
		
	*/
	
	public PrimitiveRectangle(int x1, int y1, int x2, int y2, boolean f, 
							  int layer, int dashSt)
	{
		super();
		virtualPoint = new Point[N_POINTS];
		for(int i=0;i<N_POINTS;++i)
			virtualPoint[i]=new Point();
			
		virtualPoint[0].x=x1;
		virtualPoint[0].y=y1;
		virtualPoint[1].x=x2;
		virtualPoint[1].y=y2;
		isFilled=f;
		dashStyle=dashSt;
		changed = true;
		
		setLayer(layer);
		
	}
	
	
	// Those are data which are kept for the fast redraw of this primitive. 
	// Basically, they are calculated once and then used as much as possible
	// without having to calculate everything from scratch.
	private int xa, ya, xb, yb;
	private int x1, y1,x2,y2; 		
	private Stroke stroke;
	private float w;

	/** Draw the graphic primitive on the given graphic context.
		@param g the graphic context in which the primitive should be drawn.
		@param coordSys the graphic coordinates system to be applied.
		@param layerV the layer description.
	*/
	final public void draw(Graphics2D g, MapCoordinates coordSys,
							  ArrayList layerV)
	{
	
		if(!selectLayer(g,layerV))
			return;
			
		// in the rectangle primitive, the first two virtual points represent
		//   the two corners of the segment 
		   
 		if(changed) {
 			changed=false;
			x1=coordSys.mapX(virtualPoint[0].x,virtualPoint[0].y);
 			y1=coordSys.mapY(virtualPoint[0].x,virtualPoint[0].y);
 			x2=coordSys.mapX(virtualPoint[1].x,virtualPoint[1].y);
 			y2=coordSys.mapY(virtualPoint[1].x,virtualPoint[1].y);

 			// Sort the coordinates.
 			if (x1>x2) {
 				xa=x2;
 				xb=x1;
 			} else {
 				xa=x1;
 				xb=x2;
 			}
 			if (y1>y2) {
 				ya=y2;
 				yb=y1;
 			} else {
 				ya=y1;
 				yb=y2;
 			}
 			// Calculate the stroke width
 			w = (float)(Globals.lineWidth*coordSys.getXMagnitude());
			if (w<D_MIN) w=D_MIN;

			// Check if we need to create a dashed stroke and fabricate the
			// stroke style with the good characteristics
			if (dashStyle>0) 
				stroke=new BasicStroke(w, 
                                         BasicStroke.CAP_BUTT, 
                                         BasicStroke.JOIN_MITER, 
                                         10.0f, Globals.dash[dashStyle], 0.0f);
			else 
				stroke=new BasicStroke(w);		
		}
		
		// If we do not need to perform the drawing, exit immediately		
        if(!g.hitClip(xa,ya, (xb-xa)+1,(yb-ya)+1))
 			return;

		// Apparently, on some systems (like my iMac G5 with MacOSX 10.4.11)
		// setting the stroke takes a lot of time!
		if(!stroke.equals(g.getStroke())) 
			g.setStroke(stroke); 			
			
 		if(isFilled){
 			// We need to add 1 to the rectangle, since the behaviour of 
 			// Java api is to skip the rightmost and bottom pixels 
 			g.fillRect(xa,ya,(xb-xa)+1,(yb-ya)+1);
 		} else {
 			if(xb!=xa || yb!=ya)
 				g.drawRect(xa,ya,(xb-xa),(yb-ya));			
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
		if (tokens[0].equals("RV")||tokens[0].equals("RP")) {	// Oval
 			if (N<5) {
 				IOException E=new IOException("bad arguments on RV/RP");
				throw E;
 			}
 			virtualPoint[0].x=Integer.parseInt(tokens[1]);
 			virtualPoint[0].y=Integer.parseInt(tokens[2]);
 			virtualPoint[1].x=Integer.parseInt(tokens[3]);
 			virtualPoint[1].y=Integer.parseInt(tokens[4]);
 			if(N>5) parseLayer(tokens[5]);
 			
 			if(tokens[0].equals("RP"))
 				isFilled=true;
 			else
 				isFilled=false;
 			
 			if(N>6 && tokens[6].equals("FCJ")) {
 				dashStyle = Integer.parseInt(tokens[7]);
 				// Parameters validation and correction
				if(dashStyle>=Globals.dashNumber)
					dashStyle=Globals.dashNumber-1;
				if(dashStyle<0)
					dashStyle=0;
 			}	
 			
 		} else {
 			IOException E=new IOException("RV/RP: Invalid primitive: "+tokens[0]+
 										  " programming error?");
			throw E;
 		}
		
		
	}
	
	/**	Get the control parameters of the given primitive.
	
		@return a vector of ParameterDescription containing each control
				parameter.
				The first parameters should always be the virtual points.
				
	*/
	public Vector getControls()
	{
		Vector v=super.getControls();
		ParameterDescription pd = new ParameterDescription();

		pd.parameter=new Boolean(isFilled);
		pd.description="Filled";
		v.add(pd);
		
		pd = new ParameterDescription();
		pd.parameter=new DashInfo(dashStyle);
		pd.description="Dash style";
		pd.isExtension = true;
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
		super.setControls(v);
		int i=getControlPointNumber()+1;		
		ParameterDescription pd;
		
		pd=(ParameterDescription)v.get(i);
		++i;
		// Check, just for sure...
		if (pd.parameter instanceof Boolean)
			isFilled=((Boolean)pd.parameter).booleanValue();
		else
		 	System.out.println("Warning: unexpected parameter!"+pd);
		
		pd=(ParameterDescription)v.get(i++);
		if (pd.parameter instanceof DashInfo)
			dashStyle=((DashInfo)pd.parameter).style;
		else
		 	System.out.println("Warning: unexpected parameter!"+pd);
		
		// Parameters validation and correction
		if(dashStyle>=Globals.dashNumber)
			dashStyle=Globals.dashNumber-1;
		if(dashStyle<0)
			dashStyle=0;
		
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
		int xa=Math.min(virtualPoint[0].x,virtualPoint[1].x);
        int ya=Math.min(virtualPoint[0].y,virtualPoint[1].y);
        int xb=Math.max(virtualPoint[0].x,virtualPoint[1].x);
        int yb=Math.max(virtualPoint[0].y,virtualPoint[1].y);
            
        if(isFilled) {
	        if(GeometricDistances.pointInRectangle(xa,ya,(xb-xa),(yb-ya),px,py))
	          	return DISTANCE_IN;
   	     else
   		     	return DISTANCE_OUT;
   		}
   		
   		return GeometricDistances.pointToRectangle(xa,ya,(xb-xa),
   												(yb-ya),px,py);
   		
	}
	
	/** Obtain a string command descripion of the primitive.
		@return the FIDOCAD command line.
	*/
	public String toString(boolean extensions)
	{
		String cmd;
		
		if (isFilled)
			cmd="RP ";
		else
			cmd="RV ";
		
		cmd+=virtualPoint[0].x+" "+virtualPoint[0].y+" "+
			+virtualPoint[1].x+" "+virtualPoint[1].y+" "+
			getLayer()+"\n";
		
		if(extensions) {
			if (dashStyle>0) 
		 		cmd+="FCJ "+dashStyle+"\n";
		
		}
		
		return cmd;
	}
	
	/** Export the rectangle
	*/
	public void export(ExportInterface exp, MapCoordinates cs)
		throws IOException	
	{
		exp.exportRectangle(cs.mapX(virtualPoint[0].x,virtualPoint[0].y),
					   cs.mapY(virtualPoint[0].x,virtualPoint[0].y),
					   cs.mapX(virtualPoint[1].x,virtualPoint[1].y),
					   cs.mapY(virtualPoint[1].x,virtualPoint[1].y),
					   isFilled,
					   getLayer(),
					   dashStyle,
					   Globals.lineWidth); 
	}

}