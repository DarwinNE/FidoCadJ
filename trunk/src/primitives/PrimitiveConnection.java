package primitives;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.awt.geom.*;

import geom.*;
import export.*;
import globals.*;


/** Class to handle the Connection primitive.

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

public class PrimitiveConnection extends GraphicPrimitive
{


	// A connection is defined by one points.

	static final int N_POINTS=1;
	
	
	/** Gets the number of control points used.
		@return the number of points used by the primitive
	*/
	
	public int getControlPointNumber()
	{
		return N_POINTS;
	}
	
	public PrimitiveConnection()
	{
		super();
		
		virtualPoint = new Point[N_POINTS];
		for(int i=0;i<N_POINTS;++i)
			virtualPoint[i]=new Point();
		
	}

	/** Create a connection in the given point.
		@param x the x coordinate (logical unit) of the connection.
		@param y the y coordinate (logical unit) of the connection.
		
	*/
	
	public PrimitiveConnection(int x, int y, int layer)
	{
		super();
		virtualPoint = new Point[N_POINTS];
		for(int i=0;i<N_POINTS;++i)
			virtualPoint[i]=new Point();
			
		virtualPoint[0].x=x;
		virtualPoint[0].y=y;
		
		
		
		setLayer(layer);
		
	}
	
	private int x1, y1, xa, ya, ni;
	private double nn;
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
		
		if (changed) {
			changed=false;
			/* in the PCB pad primitive, the the virtual points represent
		   	the position of the pad to be drawn. */
			x1=virtualPoint[0].x;
 			y1=virtualPoint[0].y;
 			
 		
 			nn=Math.abs(coordSys.mapXr(0,0)-
 				coordSys.mapXr(10,10))*Globals.diameterConnection/10.0;
 		
 			// a little boost for small zooms :-)
 			if (nn<2) {
 				nn=(int)(Math.abs(coordSys.mapX(0,0)-
 					coordSys.mapX(20,20))*Globals.diameterConnection/12);
 			}
	
 			xa=(int)Math.round(coordSys.mapXr(x1,y1)- nn/2.0);
 			ya=(int)Math.round(coordSys.mapYr(x1,y1)- nn/2.0);
 			
 			coordSys.trackPoint(xa,ya);
 			ni=(int)Math.round(nn);
 			
 		}
 		
 		if(!g.hitClip(xa, ya, ni, ni))
 			return;
 		
 		g.fillOval(xa, ya, ni, ni);
		
 		
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

		if (tokens[0].equals("SA")) {	// Connection
 			
 			if (N<3)  {
 				IOException E=new IOException("bad arguments on SA");
				throw E;
 			}
 			// Load the points in the virtual points associated to the 
 			// current primitive.

 			virtualPoint[0].x=Integer.parseInt(tokens[1]);
 			virtualPoint[0].y=Integer.parseInt(tokens[2]);
 			
 			if(N>3) parseLayer(tokens[3]);

 			
 		} else {
 			IOException E=new IOException("Invalid primitive:"+
 										  " programming error?");
			throw E;
 		}
		
 			
		
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
		return GeometricDistances.pointToPoint(
				virtualPoint[0].x,virtualPoint[0].y,
				px,py)-1;
	}
	
	/** Obtain a string command descripion of the primitive.
		@return the FIDOCAD command line.
	*/
	public String toString(boolean extensions)
	{
		return "SA "+virtualPoint[0].x+" "+virtualPoint[0].y+
			" "+getLayer()+"\n";
	}
	
		/** The export routine
	*/
	public void export(ExportInterface exp, MapCoordinates cs) 
		throws IOException
	{
		exp.exportConnection(cs.mapX(virtualPoint[0].x,virtualPoint[0].y),
					   cs.mapY(virtualPoint[0].x,virtualPoint[0].y), getLayer(),
					   Globals.diameterConnection); 
	}
}