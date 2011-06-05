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


/** Class to handle the Bézier primitive.

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

public final class PrimitiveBezier extends GraphicPrimitive
{

	// A Bézier is defined by four points.
	static final int N_POINTS=6;
	
	private boolean arrowStart;
	private boolean arrowEnd;
	
	private int arrowLength;
	private int arrowHalfWidth;
	private int arrowStyle;	
	
	private int dashStyle;
	
	/** Gets the number of control points used.
		@return the number of points used by the primitive
	*/
	
	public int getControlPointNumber()
	{
		return N_POINTS;
	}
	
	/** Standard constructor. It creates an empty shape.
	
	*/
	public PrimitiveBezier()
	{
		super();
		
		r = new Rectangle();
   		initPrimitive(-1);
		
	}
	/** Create a Bézier curve specified by four control points
		@param x1 the x coordinate (logical unit) of P1.
		@param y1 the y coordinate (logical unit) of P1.
		@param x2 the x coordinate (logical unit) of P2.
		@param y2 the y coordinate (logical unit) of P2.
		@param x3 the x coordinate (logical unit) of P3.
		@param y3 the y coordinate (logical unit) of P3.
		@param x4 the x coordinate (logical unit) of P4.
		@param y4 the y coordinate (logical unit) of P4.
		@param layer the layer to be used.
		@param arrowS Arrow to be drawn at the beginning of the curve
		@param arrowE Arrow to be drawn at the beginning of the curve
		@param arrowSt Arrow style
		@param dashSt Dash style
		
	*/
	
	public PrimitiveBezier(int x1, int y1, int x2, int y2, 
						 int x3, int y3, int x4, int y4, 
							int layer, boolean arrowS, boolean arrowE,
							int arrowSt, int arrowLe, int arrowWi, int dashSt)
	{
		super();

		
		arrowLength = arrowLe;
		arrowHalfWidth = arrowWi;
		arrowStart = arrowS;
		arrowEnd = arrowE;
		arrowStyle =arrowSt;
		dashStyle=dashSt;
		
		r = new Rectangle();
		
		initPrimitive(-1);
			
		// Store the coordinates of the points 
		virtualPoint[0].x=x1;
		virtualPoint[0].y=y1;
		virtualPoint[1].x=x2;
		virtualPoint[1].y=y2;
		virtualPoint[2].x=x3;
		virtualPoint[2].y=y3;
		virtualPoint[3].x=x4;
		virtualPoint[3].y=y4;
		
		virtualPoint[getNameVirtualPointNumber()].x=x1+5;
		virtualPoint[getNameVirtualPointNumber()].y=y1+5;
		virtualPoint[getValueVirtualPointNumber()].x=x1+5;
		virtualPoint[getValueVirtualPointNumber()].y=y1+10;		
		// Store the layer
		setLayer(layer);
		
	}
	
	/**	Get the control parameters of the given primitive.
	
		@return a vector of ParameterDescription containing each control
				parameter.
				
	*/
	public Vector getControls()
	{
		Vector v=super.getControls();
		
		ParameterDescription pd = new ParameterDescription();

		pd = new ParameterDescription();
		pd.parameter=new Boolean(arrowStart);
		pd.description=Globals.messages.getString("ctrl_arrow_start");
		pd.isExtension = true;
		v.add(pd);
		pd = new ParameterDescription();
		pd.parameter=new Boolean(arrowEnd);
		pd.description=Globals.messages.getString("ctrl_arrow_end");
		pd.isExtension = true;
		v.add(pd);
		pd = new ParameterDescription();
		pd.parameter=new Integer(arrowLength);
		pd.description=Globals.messages.getString("ctrl_arrow_length");
		pd.isExtension = true;
		v.add(pd);
		pd = new ParameterDescription();
		pd.parameter=new Integer(arrowHalfWidth);
		pd.description=Globals.messages.getString("ctrl_arrow_half_width");
		pd.isExtension = true;
		v.add(pd);
		pd = new ParameterDescription();
		pd.parameter=new ArrowInfo(arrowStyle);
		pd.description=Globals.messages.getString("ctrl_arrow_style");
		pd.isExtension = true;
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
	public void setControls(Vector v)
	{
		super.setControls(v);
		int i=getControlPointNumber()+3;		
		ParameterDescription pd;
		
		pd=(ParameterDescription)v.get(i++);
		if (pd.parameter instanceof Boolean)
			arrowStart=((Boolean)pd.parameter).booleanValue();
		else
		 	System.out.println("Warning: unexpected parameter 1!"+pd);
		pd=(ParameterDescription)v.get(i++);
		if (pd.parameter instanceof Boolean)
			arrowEnd=((Boolean)pd.parameter).booleanValue();
		else
		 	System.out.println("Warning: unexpected parameter 2!"+pd);
		 	
		pd=(ParameterDescription)v.get(i++);
		if (pd.parameter instanceof Integer)
			arrowLength=((Integer)pd.parameter).intValue();
		else
		 	System.out.println("Warning: unexpected parameter 3!"+pd);
		 	
		pd=(ParameterDescription)v.get(i++);
		if (pd.parameter instanceof Integer)
			arrowHalfWidth=((Integer)pd.parameter).intValue();
		else
		 	System.out.println("Warning: unexpected parameter 4!"+pd);

		pd=(ParameterDescription)v.get(i++);
		if (pd.parameter instanceof ArrowInfo)
			arrowStyle=((ArrowInfo)pd.parameter).style;
		else
		 	System.out.println("Warning: unexpected parameter 5!"+pd);
		
		pd=(ParameterDescription)v.get(i++);
		if (pd.parameter instanceof DashInfo)
			dashStyle=((DashInfo)pd.parameter).style;
		else
		 	System.out.println("Warning: unexpected parameter 6!"+pd);
		
		// Parameters validation and correction
		if(dashStyle>=Globals.dashNumber)
			dashStyle=Globals.dashNumber-1;
		if(dashStyle<0)
			dashStyle=0;
	}
	
	
	// Those are data which are kept for the fast redraw of this primitive. 
	// Basically, they are calculated once and then used as much as possible
	// without having to calculate everything from scratch.
	private Shape shape1;
	private Stroke stroke;
	private float w;
	private Rectangle r;
	
	private int xmin, ymin;
	private int width, height;

		
	/** Draw the graphic primitive on the given graphic context.
		@param g the graphic context in which the primitive should be drawn.
		@param coordSys the graphic coordinates system to be applied.
		@param layerV the layer description.
	*/
	final public void draw(Graphics2D g, MapCoordinates coordSys,
							  Vector layerV)
	{
	
		if(!selectLayer(g,layerV))
			return;

		drawText(g, coordSys, layerV, -1);
	
		// in the Bézier primitive, the four virtual points represent
		//   the control points of the shape 
 		
 		if (changed) {
 			changed=false;
 			
 			// Create the Bézier curve, which in the Java library is called a 
 			// cubic curve (and indeed it is!)
 			shape1 = new CubicCurve2D.Float(
 				coordSys.mapX(virtualPoint[0].x,virtualPoint[0].y),
				coordSys.mapY(virtualPoint[0].x,virtualPoint[0].y),
				coordSys.mapX(virtualPoint[1].x,virtualPoint[1].y),
				coordSys.mapY(virtualPoint[1].x,virtualPoint[1].y),
				coordSys.mapX(virtualPoint[2].x,virtualPoint[2].y),
				coordSys.mapY(virtualPoint[2].x,virtualPoint[2].y),
				coordSys.mapX(virtualPoint[3].x,virtualPoint[3].y),
				coordSys.mapY(virtualPoint[3].x,virtualPoint[3].y));
			
			// Calculating the bounds of this curve is useful since we can 
			// check if it is visible and thus choose wether draw it or not.
			Rectangle r = shape1.getBounds();
			
			xmin = r.x;
			ymin = r.y;
			width  = r.width;
			height = r.height;
 		
 			// Calculating stroke width
 			
 			w = (float)(Globals.lineWidth*coordSys.getXMagnitude());
 			if (w<D_MIN) w=D_MIN;

			// Check if there is a dash to be used for the stroke and 
			// create a new stroke.
			/*
			if (dashStyle>0) 
				stroke=new BasicStroke(w, 
                            	BasicStroke.CAP_BUTT, 
                                BasicStroke.JOIN_MITER, 
                                10.0f, Globals.dash[dashStyle], 0.0f);
			else 
				stroke=new BasicStroke(w);
			*/
			if (strokeStyle==null) {
				strokeStyle = new StrokeStyle();
			}
			stroke = strokeStyle.getStroke(w, dashStyle);
		}
		
		// If the curve is not visible, exit immediately
		
		if(!g.hitClip(xmin,ymin, width, height))
 			return;
		
		// This allows to save time on some systems where setting up a new 
		// stroke style takes some time.
		
		if(!stroke.equals(g.getStroke())) 
			g.setStroke(stroke);
		
		// Draw the curve
		
		g.draw(shape1);
 		
 		// Check if there are arrows to be drawn and eventually draw them.
 		
		if (arrowStart || arrowEnd) {
			int h=coordSys.mapXi(arrowHalfWidth,arrowHalfWidth,false)-
 				coordSys.mapXi(0,0, false);
			int l=coordSys.mapXi(arrowLength,arrowLength,false)-
				coordSys.mapXi(0,0, false);
	
			
 			if (arrowStart) {
 				// We must check if the cubic curve is degenerate
 				int psx, psy, pex, pey;
 				psx = virtualPoint[0].x;
 				psy = virtualPoint[0].y; 				
 				if(virtualPoint[0].x!=virtualPoint[1].x ||
 				   virtualPoint[0].y!=virtualPoint[1].y) {
 					pex = virtualPoint[1].x;
 					pey = virtualPoint[1].y;
				} else if(virtualPoint[0].x!=virtualPoint[2].x ||
 				   virtualPoint[2].y!=virtualPoint[1].y) {
 					pex = virtualPoint[2].x;
 					pey = virtualPoint[2].y;
				} else {
 					pex = virtualPoint[3].x;
 					pey = virtualPoint[3].y;
				}
				
				Arrow.drawArrow(g, 		
 					coordSys.mapX(psx,psy),
					coordSys.mapY(psx,psy),
					coordSys.mapX(pex,pey),
					coordSys.mapY(pex,pey), 
					l, h, arrowStyle);
			}
			
			if (arrowEnd) {
				// We must check if the cubic curve is degenerate
 				int psx, psy, pex, pey;
				psx = virtualPoint[3].x;
 				psy = virtualPoint[3].y;
				if(virtualPoint[3].x!=virtualPoint[2].x ||
 				   virtualPoint[3].y!=virtualPoint[2].y) {
					pex = virtualPoint[2].x;
 					pey = virtualPoint[2].y;
				} else if(virtualPoint[3].x!=virtualPoint[1].x ||
 				   virtualPoint[3].y!=virtualPoint[1].y) {
 				  	pex = virtualPoint[1].x;
 					pey = virtualPoint[1].y;
				} else {
					pex = virtualPoint[0].x;
 					pey = virtualPoint[0].y;
				}
				
				Arrow.drawArrow(g, 		
 					coordSys.mapX(psx,psy),
					coordSys.mapY(psx,psy),
					coordSys.mapX(pex,pey),
					coordSys.mapY(pex,pey), 
					l, h, arrowStyle);
				
				
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
		if (tokens[0].equals("BE")) {	// Bézier
 			if (N<9) {
 				IOException E=new IOException("bad arguments on BE");
				throw E;
 			}
 			// Parse the coordinates of all points of the Bézier curve
 			int x1 = virtualPoint[0].x=Integer.parseInt(tokens[1]);
 			int y1 = virtualPoint[0].y=Integer.parseInt(tokens[2]);
 			virtualPoint[1].x=Integer.parseInt(tokens[3]);
 			virtualPoint[1].y=Integer.parseInt(tokens[4]);
 			virtualPoint[2].x=Integer.parseInt(tokens[5]);
 			virtualPoint[2].y=Integer.parseInt(tokens[6]);
 			virtualPoint[3].x=Integer.parseInt(tokens[7]);
 			virtualPoint[3].y=Integer.parseInt(tokens[8]);
 			virtualPoint[getNameVirtualPointNumber()].x=x1+5;
			virtualPoint[getNameVirtualPointNumber()].y=y1+5;
			virtualPoint[getValueVirtualPointNumber()].x=x1+5;
			virtualPoint[getValueVirtualPointNumber()].y=y1+10;		 			
 			if(N>9) parseLayer(tokens[9]);
 			
 			if(N>10 && tokens[10].equals("FCJ")) {
 				int arrows = Integer.parseInt(tokens[11]);
 				arrowStart = (arrows & 0x01) !=0;
 				arrowEnd = (arrows & 0x02) !=0;
 				
 				arrowStyle = Integer.parseInt(tokens[12]);
 				arrowLength = Integer.parseInt(tokens[13]);
 				arrowHalfWidth = Integer.parseInt(tokens[14]);
 				dashStyle = Integer.parseInt(tokens[15]);
 				// Parameters validation and correction
				if(dashStyle>=Globals.dashNumber)
					dashStyle=Globals.dashNumber-1;
				if(dashStyle<0)
					dashStyle=0;
 			}	
  			
 			
 		} else {
 			IOException E=new IOException("Invalid primitive: "+
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
	
		// If not, we check for the distance to the Bézier curve.
		return GeometricDistances.pointToBezier(
				virtualPoint[0].x, virtualPoint[0].y,
				virtualPoint[1].x, virtualPoint[1].y,
				virtualPoint[2].x, virtualPoint[2].y,
				virtualPoint[3].x, virtualPoint[3].y,
				px,  py);
		
	}
	
	/** Obtain a string command descripion of the primitive.
		@return the FIDOCAD command line.
	*/
	public String toString(boolean extensions)
	{
		String cmd;
			
		
		String s = "BE "+virtualPoint[0].x+" "+virtualPoint[0].y+" "+
			+virtualPoint[1].x+" "+virtualPoint[1].y+" "+
			+virtualPoint[2].x+" "+virtualPoint[2].y+" "+
			+virtualPoint[3].x+" "+virtualPoint[3].y+" "+
			getLayer()+"\n";
			
		if(extensions) {
		 	int arrows = (arrowStart?0x01:0x00)|(arrowEnd?0x02:0x00);
		 			 	
		 	if (arrows>0 || dashStyle>0 || name.length()!=0 
		 		|| value.length()!=0) {
		 		String text = "0";
		 		// We take into account that there may be some text associated
		 		// to that primitive.
		 		if (name.length()!=0 || value.length()!=0) 
		 			text = "1";
		 		s+="FCJ "+arrows+" "+arrowStyle+" "+arrowLength+" "+
		 		arrowHalfWidth+" "+dashStyle+" "+text+"\n";
		 	}
		
		}
		
		// The false is needed since saveText should not write the FCJ tag.
		s+=saveText(false);
		
		return s;
	}
	
	public void export(ExportInterface exp, MapCoordinates cs) 
		throws IOException
	{
		exportText(exp, cs, -1);
		exp.exportBezier(cs.mapX(virtualPoint[0].x,virtualPoint[0].y),
					   cs.mapY(virtualPoint[0].x,virtualPoint[0].y),
					   cs.mapX(virtualPoint[1].x,virtualPoint[1].y),
					   cs.mapY(virtualPoint[1].x,virtualPoint[1].y),
					   cs.mapX(virtualPoint[2].x,virtualPoint[2].y),
					   cs.mapY(virtualPoint[2].x,virtualPoint[2].y),
					   cs.mapX(virtualPoint[3].x,virtualPoint[3].y),
					   cs.mapY(virtualPoint[3].x,virtualPoint[3].y),
					   getLayer(),
					   arrowStart, arrowEnd, arrowStyle, 
					   (int)(arrowLength*cs.getXMagnitude()), 
					   (int)(arrowHalfWidth*cs.getXMagnitude()), 
					   dashStyle,Globals.lineWidth*cs.getXMagnitude()); 
	}
		/** Get the number of the virtual point associated to the Name property
		@return the number of the virtual point associated to the Name property
	*/
	public int getNameVirtualPointNumber()
	{
		return 4;
	}
	
	/** Get the number of the virtual point associated to the Value property
		@return the number of the virtual point associated to the Value property
	*/
	public  int getValueVirtualPointNumber()
	{
		return 5;
	}
	
}