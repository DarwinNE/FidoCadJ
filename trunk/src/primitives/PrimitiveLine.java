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


/** Class to handle the line primitive.

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


public class PrimitiveLine extends GraphicPrimitive
{

	static final int N_POINTS=4;
	
	private boolean arrowStart;
	private boolean arrowEnd;
	
	private int arrowLength;
	private int arrowHalfWidth;
	
	private int arrowStyle;
	
	private int dashStyle;

	/** Constructor
		@param x1		the x coordinate of the start point of the line
		@param y1		the y coordinate of the start point of the line
		@param x2		the x coordinate of the end point of the line
		@param y2		the y coordinate of the end point of the line
		@param layer	the layer to be used
		@param arrowS	true if there is an arrow at the beginning of the line
		@param arrowE	true if there is an arrow at the end of the line 
		@param arrowSt	style of the arrow
		@param arrowLe	length of the arrow
		@param arrowWi	width of the arrow
	*/
	public PrimitiveLine(int x1, int y1, int x2, int y2, int layer,
						boolean arrowS, boolean arrowE,
						int arrowSt, int arrowLe, int arrowWi, int dashSt)
	{
		super();
		
		arrowLength = arrowLe;
		arrowHalfWidth = arrowWi;
		arrowStart = arrowS;
		arrowEnd = arrowE;
		arrowStyle=arrowSt;
		dashStyle = dashSt;
		
		initPrimitive(-1);
		
		virtualPoint[0].x=x1;
		virtualPoint[0].y=y1;
		virtualPoint[1].x=x2;
		virtualPoint[1].y=y2;
		virtualPoint[getNameVirtualPointNumber()].x=x1+5;
		virtualPoint[getNameVirtualPointNumber()].y=y1+5;
		virtualPoint[getValueVirtualPointNumber()].x=x1+5;
		virtualPoint[getValueVirtualPointNumber()].y=y1+10;		
				
		setLayer(layer);
	}
	
	public PrimitiveLine()
	{
		super();
		arrowLength = 3;
		arrowHalfWidth = 1;
		initPrimitive(-1);
	}
	
	/** Gets the number of control points used.
		@return the number of points used by the primitive
	*/
	
	public int getControlPointNumber()
	{
		return N_POINTS;
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

		pd = new ParameterDescription();
		pd.parameter=new Boolean(arrowStart);
		pd.description="Arrow at start";
		pd.isExtension = true;
		v.add(pd);
		pd = new ParameterDescription();
		pd.parameter=new Boolean(arrowEnd);
		pd.description="Arrow at end";
		pd.isExtension = true;
		v.add(pd);
		pd = new ParameterDescription();
		pd.parameter=new Integer(arrowLength);
		pd.description="Arrow length";
		pd.isExtension = true;
		v.add(pd);
		pd = new ParameterDescription();
		pd.parameter=new Integer(arrowHalfWidth);
		pd.description="Arrow half width";
		pd.isExtension = true;

		v.add(pd);
		pd = new ParameterDescription();
		pd.parameter=new ArrowInfo(arrowStyle);
		pd.description="Arrow style";
		pd.isExtension = true;

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
		
		pd=(ParameterDescription)v.get(i++);
		if (pd.parameter instanceof Boolean)
			arrowStart=((Boolean)pd.parameter).booleanValue();
		else
		 	System.out.println("Warning: 1-unexpected parameter!"+pd);
		pd=(ParameterDescription)v.get(i++);
		if (pd.parameter instanceof Boolean)
			arrowEnd=((Boolean)pd.parameter).booleanValue();
		else
		 	System.out.println("Warning: 2-unexpected parameter!"+pd);
		 	
		pd=(ParameterDescription)v.get(i++);
		if (pd.parameter instanceof Integer)
			arrowLength=((Integer)pd.parameter).intValue();
		else
		 	System.out.println("Warning: 3-unexpected parameter!"+pd);
		 	
		pd=(ParameterDescription)v.get(i++);
		if (pd.parameter instanceof Integer)
			arrowHalfWidth=((Integer)pd.parameter).intValue();
		else
		 	System.out.println("Warning: 4-unexpected parameter!"+pd);

		pd=(ParameterDescription)v.get(i++);
		if (pd.parameter instanceof ArrowInfo)
			arrowStyle=((ArrowInfo)pd.parameter).style;
		else
		 	System.out.println("Warning: 5-unexpected parameter!"+pd);
		
		pd=(ParameterDescription)v.get(i++);
		if (pd.parameter instanceof DashInfo)
			dashStyle=((DashInfo)pd.parameter).style;
		else
		 	System.out.println("Warning: 6-unexpected parameter!"+pd);
		 	
		// Parameters validation and correction
		if(dashStyle>=Globals.dashNumber)
			dashStyle=Globals.dashNumber-1;
		if(dashStyle<0)
			dashStyle=0;
	
	}
	
	/** Create a segment between two points
		@param x1 the start x coordinate (logical unit).
		@param y1 the start y coordinate (logical unit).
		@param x2 the end x coordinate (logical unit).
		@param y2 the end y coordinate (logical unit).
		@param layer the layer to be used.
		
	*/
	
	
	// Those are data which are kept for the fast redraw of this primitive. 
	// Basically, they are calculated once and then used as much as possible
	// without having to calculate everything from scratch.
	private int xa, ya, xb, yb;
	private int x1, y1,x2,y2; 	
	private int h,l;
	private float w;
	private BasicStroke stroke;
	private int length2;
	private int xbpap1, ybpap1;
	private boolean arrows;
	
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
			
		drawText(g, coordSys, layerV, -1);
		
		// in the line primitive, the first two virtual points represent
		//   the beginning and the end of the segment to be drawn. 

		if(changed) {
			changed=false;
			x1=coordSys.mapX(virtualPoint[0].x,virtualPoint[0].y);
 			y1=coordSys.mapY(virtualPoint[0].x,virtualPoint[0].y);
 			x2=coordSys.mapX(virtualPoint[1].x,virtualPoint[1].y);
 			y2=coordSys.mapY(virtualPoint[1].x,virtualPoint[1].y);
 			
 			// We store the coordinates in an ordered way in order to ease
 			// the determination of the clip rectangle.
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
 			
 			// Heigth and width of the arrows in pixels
 			h=Math.abs(coordSys.mapXi(arrowHalfWidth,arrowHalfWidth,false)-
 				coordSys.mapXi(0,0, false));
 			l=Math.abs(coordSys.mapXi(arrowLength,arrowLength, false)-
 				coordSys.mapXi(0,0,false));

			// Calculate the width of the stroke in pixel. It should not
			// make our lines disappear, even at very small zoom ratios.
		 	// So we put a limit D_MIN.
			w = (float)(Globals.lineWidth*coordSys.getXMagnitude());
 			if (w<D_MIN) w=D_MIN;
 			
 			// Calculate the length in pixel.
 			length2=(xa-xb)*(xa-xb)+(ya-yb)*(ya-yb);
 			if(dashStyle>0) {
				stroke=new BasicStroke(w, BasicStroke.CAP_BUTT, 
                                          BasicStroke.JOIN_MITER, 
                                          10.0f, Globals.dash[dashStyle], 
                                          0.0f);
			} else {
    			stroke =new BasicStroke(w);

			}
			arrows = arrowStart || arrowEnd;

			// This correction solves bug #3101041
			// We do need to apply a correction to the clip calculation
			// rectangle if necessary to take into account the arrow heads
			if (arrows) {
				xa -= h;
				ya -= h;
				xb += h;
				yb += h;
			}
			xbpap1=(xb-xa)+1;
			ybpap1=(yb-ya)+1;
		}

		// This is a trick. We skip drawing the line if it is too short.
		if(length2>2) {
			if(!g.hitClip(xa,ya, xbpap1,ybpap1))
 				return;
			
			// Apparently, on some systems (like my iMac G5 with MacOSX 10.4.11)
			// setting the stroke takes a lot of time!
			if(!stroke.equals(g.getStroke())) 
				g.setStroke(stroke);			
				
			g.drawLine(x1, y1, x2, y2);
		
			// Eventually, we draw the arrows at the extremes.
			if (arrows) {	
				if (arrowStart) 
					Arrow.drawArrow(g, x1, y1, x2, y2, l, h, arrowStyle);
				if (arrowEnd) 
					Arrow.drawArrow(g, x2, y2, x1, y1, l, h, arrowStyle);
			}
		}
 		return;
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
		int i;
		changed=true;

		if (tokens[0].equals("LI")) {	// Line
 			if (N<5) {
 				IOException E=new IOException("bad arguments on LI");
				throw E;
 			}
 			// Load the points in the virtual points associated to the 
 			// current primitive.

 			virtualPoint[0].x=Integer.parseInt(tokens[1]);
 			virtualPoint[0].y=Integer.parseInt(tokens[2]);
 			virtualPoint[1].x=Integer.parseInt(tokens[3]);
 			virtualPoint[1].y=Integer.parseInt(tokens[4]);
 			if(N>5) parseLayer(tokens[5]);
 			
 			// FidoCadJ extensions
 			
 			if(N>6 && tokens[6].equals("FCJ")) {
 				int arrows = Integer.parseInt(tokens[7]);
 				arrowStart = (arrows & 0x01) !=0;
 				arrowEnd = (arrows & 0x02) !=0;
 				
 				arrowStyle = Integer.parseInt(tokens[8]);
 				arrowLength = Integer.parseInt(tokens[9]);
 				arrowHalfWidth = Integer.parseInt(tokens[10]);
 				dashStyle = Integer.parseInt(tokens[11]);
 				// Parameters validation and correction
				if(dashStyle>=Globals.dashNumber)
					dashStyle=Globals.dashNumber-1;
				if(dashStyle<0)
					dashStyle=0;
 			}		
 		} else {
 			IOException E=new IOException("LI: Invalid primitive:"+tokens[0]+
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
	    // Here we check if the given point lies inside the text areas
        
	    if(checkText(px, py))
	    	return 0;

		return GeometricDistances.pointToSegment(
				virtualPoint[0].x,virtualPoint[0].y,
				virtualPoint[1].x,virtualPoint[1].y,
				px,py);
            
    }
	
	/** Obtain a string command descripion of the primitive.
		@return the FIDOCAD command line.
	*/
	public String toString(boolean extensions)
	{
		String s= "LI "+virtualPoint[0].x+" "+virtualPoint[0].y+" "+
			+virtualPoint[1].x+" "+virtualPoint[1].y+" "+
			getLayer()+"\n";
		
		if(extensions) {
		 	int arrows = (arrowStart?0x01:0x00)|(arrowEnd?0x02:0x00);
		 			 	
		 	if (arrows>0 || dashStyle>0) 
		 		s+="FCJ "+arrows+" "+arrowStyle+" "+arrowLength+" "+
		 		arrowHalfWidth+" "+dashStyle+"\n";
		
		}
		return s;
	}
	
	/** The export routine
	*/
	public void export(ExportInterface exp, MapCoordinates cs) 
		throws IOException
	{
		exportText(exp, cs, -1);
		exp.exportLine(cs.mapX(virtualPoint[0].x,virtualPoint[0].y),
					   cs.mapY(virtualPoint[0].x,virtualPoint[0].y),
					   cs.mapX(virtualPoint[1].x,virtualPoint[1].y),
					   cs.mapY(virtualPoint[1].x,virtualPoint[1].y),
					   getLayer(),
					   arrowStart, arrowEnd, arrowStyle, arrowLength, 
					   arrowHalfWidth, dashStyle, Globals.lineWidth); 
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