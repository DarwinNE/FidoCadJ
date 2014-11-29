package net.sourceforge.fidocadj.primitives;

import java.io.*;
import java.util.*;

import net.sourceforge.fidocadj.dialogs.*;
import net.sourceforge.fidocadj.export.*;
import net.sourceforge.fidocadj.geom.*;
import net.sourceforge.fidocadj.globals.*;
import graphic.*;


/** Class to handle the Oval primitive.

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

public final class PrimitiveOval extends GraphicPrimitive
{

	// An oval is defined by two points.
	static final int N_POINTS=4;
	private boolean isFilled;
	private int dashStyle;
	
	// Those are data which are kept for the fast redraw of this primitive. 
	// Basically, they are calculated once and then used as much as possible
	// without having to calculate everything from scratch.
	private int xa, ya, xb, yb;
	private int x1, x2, y1, y2;
	private float w;
	

	/** Gets the number of control points used.
		@return the number of points used by the primitive
	*/	
	public int getControlPointNumber()
	{
		return N_POINTS;
	}
	
	/** Standard constructor.
	*/
	public PrimitiveOval(String f, int size)
	{
		super();
		isFilled=false;
		initPrimitive(-1, f, size);
	}
	/** Create an oval defined by two points.
		@param x1 the start x coordinate (logical unit).
		@param y1 the start y coordinate (logical unit).
		@param x2 the end x coordinate (logical unit).
		@param y2 the end y coordinate (logical unit).
		@param f specifies if the ellipse should be filled. 
		@param layer the layer to be used.
		@param dashSt the style of the dashing to be used.
		
	*/
	
	public PrimitiveOval(int x1, int y1, int x2, int y2, boolean f, int layer, 
		int dashSt, String font, int size)
	{
		super();
		initPrimitive(-1, font, size);
			
		virtualPoint[0].x=x1;
		virtualPoint[0].y=y1;
		virtualPoint[1].x=x2;
		virtualPoint[1].y=y2;
		virtualPoint[getNameVirtualPointNumber()].x=x1+5;
		virtualPoint[getNameVirtualPointNumber()].y=y1+5;
		virtualPoint[getValueVirtualPointNumber()].x=x1+5;
		virtualPoint[getValueVirtualPointNumber()].y=y1+10;		
		
		isFilled=f;
		dashStyle =dashSt;
		
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
		
		// in the oval primitive, the first two virtual points represent
		// the two corners of the oval diagonal 
 		
 		if(changed) {
 			changed=false;
			x1=coordSys.mapX(virtualPoint[0].x,virtualPoint[0].y);
 			y1=coordSys.mapY(virtualPoint[0].x,virtualPoint[0].y);
 			x2=coordSys.mapX(virtualPoint[1].x,virtualPoint[1].y);
 			y2=coordSys.mapY(virtualPoint[1].x,virtualPoint[1].y);

			// Sort the coordinates
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
 			coordSys.trackPoint(xa,ya);
 			coordSys.trackPoint(xb,yb);			
 			w = (float)(Globals.lineWidth*coordSys.getXMagnitude());
			if (w<D_MIN) w=D_MIN;
		}

		if(!g.hitClip(xa,ya, xb-xa,yb-ya))
 			return;
 		
		g.applyStroke(w, dashStyle);
		
		// Draw the oval, filled or not.
 		if (isFilled)
 			g.fillOval(xa,ya,xb-xa,yb-ya);
 		else {
			g.drawOval(xa,ya,xb-xa,yb-ya);
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
		if (tokens[0].equals("EV")||tokens[0].equals("EP")) {	// Oval
 			if (N<5) {
 				IOException E=new IOException("bad arguments on EV/EP");
				throw E;
 			}
 			int x1 = virtualPoint[0].x=Integer.parseInt(tokens[1]);
 			int y1 = virtualPoint[0].y=Integer.parseInt(tokens[2]);
 			virtualPoint[1].x=Integer.parseInt(tokens[3]);
 			virtualPoint[1].y=Integer.parseInt(tokens[4]);
 			
 			virtualPoint[getNameVirtualPointNumber()].x=x1+5;
			virtualPoint[getNameVirtualPointNumber()].y=y1+5;
			virtualPoint[getValueVirtualPointNumber()].x=x1+5;
			virtualPoint[getValueVirtualPointNumber()].y=y1+10;		
 			
 			if(N>5) parseLayer(tokens[5]);
 			if(tokens[0].equals("EP"))
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
 			IOException E=new IOException("EV/EP: Invalid primitive:"+tokens[0]+
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

		pd.parameter=Boolean.valueOf(isFilled);
		pd.description=Globals.messages.getString("ctrl_filled");
		v.add(pd);
		pd = new ParameterDescription();
		pd.parameter=new DashInfo(dashStyle);
		pd.description=Globals.messages.getString("ctrl_dash_style");
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
	public int setControls(Vector<ParameterDescription> v)
	{
		int i=super.setControls(v);				
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
	    	
		int xa=Math.min(virtualPoint[0].x,virtualPoint[1].x);
        int ya=Math.min(virtualPoint[0].y,virtualPoint[1].y);
        int xb=Math.max(virtualPoint[0].x,virtualPoint[1].x);
        int yb=Math.max(virtualPoint[0].y,virtualPoint[1].y);
     
     	if(isFilled){
        	if(GeometricDistances.pointInEllipse(xa,ya,xb-xa,yb-ya,px,py))
          		return 0;
          	else
          		return 1000;
        } else
        	return GeometricDistances.pointToEllipse(xa,ya,
        		xb-xa,yb-ya,px,py);
	}
	
	/** Obtain a string command descripion of the primitive.
		@return the FIDOCAD command line.
	*/
	public String toString(boolean extensions)
	{
		String cmd;
		
		if (isFilled)
			cmd="EP ";
		else
			cmd="EV ";
		
		cmd+=virtualPoint[0].x+" "+virtualPoint[0].y+" "+
			+virtualPoint[1].x+" "+virtualPoint[1].y+" "+
			getLayer()+"\n";
			
		if(extensions && (dashStyle>0 || hasName() || hasValue())) {
			String text = "0";
			if (name.length()!=0 || value.length()!=0)
				text = "1";
		 	cmd+="FCJ "+dashStyle+" "+text+"\n";
		}
		// The false is needed since saveText should not write the FCJ tag.
		cmd+=saveText(false);
		
		return cmd;
	}
	
	public void export(ExportInterface exp, MapCoordinates cs) 
		throws IOException
	{
		exportText(exp, cs, -1);
		exp.exportOval(cs.mapX(virtualPoint[0].x,virtualPoint[0].y),
					   cs.mapY(virtualPoint[0].x,virtualPoint[0].y),
					   cs.mapX(virtualPoint[1].x,virtualPoint[1].y),
					   cs.mapY(virtualPoint[1].x,virtualPoint[1].y),
					   isFilled,
					   getLayer(),
					   dashStyle,
					   Globals.lineWidth*cs.getXMagnitude()); 
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