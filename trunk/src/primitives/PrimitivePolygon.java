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
import java.awt.geom.*;



/** Class to handle the Polygon primitive.

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

	Copyright 2007-2012 by Davide Bucci
</pre>

@author Davide Bucci
*/

public final class PrimitivePolygon extends GraphicPrimitive
{

	private int nPoints;
	private boolean isFilled;
	private int dashStyle;
	private Polygon p;
 

	// If needed, we increase this stuff.
	
	int N_POINTS=5;
	
	/** Gets the number of control points used.
		@return the number of points used by the primitive
	*/
	
	public int getControlPointNumber()
	{
		return nPoints+2;
	}
	 
	public PrimitivePolygon(String f, int size)
	{
		super();
		isFilled=false;
		nPoints=0;
		p = new Polygon();
		initPrimitive(N_POINTS, f, size);
	}
	/** Create a polygon. Add points with the addPoint method.
		
		@param f specifies if the polygon should be filled 
		@param layer the layer to be used.
		@param dashSt the dash style
		
	*/
	
	public PrimitivePolygon(boolean f, int layer, int dashSt, 
		String font, int size)
	{
		super();
		p = new Polygon();
		initPrimitive(N_POINTS, font,  size);
		nPoints=0;
		isFilled=f;
		dashStyle=dashSt;
		setLayer(layer);
	}
	
	/** Remove the control point of the polygon closest to the given
		coordinates, if the distance is less than a certain tolerance
	
		@param x			the x coordinate of the target
		@param y			the y coordinate of the target
		@param tolerance	the tolerance
	
	*/
	public void removePoint(int x, int y, double tolerance)
	{
	
		// We can not have a polygon with less than three vertices
		if (nPoints<=3)
			return;
		
		int i;
		double distance;
		double min_distance= GeometricDistances.pointToPoint(virtualPoint[0].x,
				virtualPoint[0].y,x,y);
		int sel_i=-1;
		
		for(i=1;i<nPoints;++i) {
			distance = GeometricDistances.pointToPoint(virtualPoint[i].x,
				virtualPoint[i].y,x,y);
				
			if (distance<min_distance) {
				min_distance=distance;
				sel_i=i;
			}
		}
		
		
		// Check if the control node losest to the given coordinates
		// is closer than the given tolerance
		if(min_distance<=tolerance){
			--nPoints;
			for(i=0;i<nPoints;++i) {
				// Shift all the points subsequent to the one which needs
				// to be erased.
				if(i>=sel_i) {
					virtualPoint[i].x=virtualPoint[i+1].x;
					virtualPoint[i].y=virtualPoint[i+1].y;
				}
				changed=true;
			}
		}	
		
	}
	
	public void addPointClosest(int px, int py)
	{
		int[] xp=new int[N_POINTS];
        int[] yp=new int[N_POINTS];
        
        int k;
                
        for(k=0;k<nPoints;++k){
        	xp[k]=virtualPoint[k].x;
            yp[k]=virtualPoint[k].y;
        }     
	    // we calculate the distance between the
        // given point and all the segments composing the polygon and we 
        // take the smallest one.
        
        int distance=(int)Math.sqrt((px-xp[0])*(px-xp[0])+
        	(py-yp[0])*(py-yp[0]));
        
        int j;
        int d;
        int minv=0;
        for(int i=0; i<nPoints; ++i) {
        	j=i;
        	if (j==nPoints-1)
        		j=-1;
        		
        	d=GeometricDistances.pointToSegment(xp[i],
        		yp[i], xp[j+1],
        		yp[j+1], px,py);
        		
        	if(d<distance) {
        		distance = d;
        		minv=j+1;
        	}
        }

        
        // Now minv contains the index of the vertex before the one which 
        // should be entered. We begin to enter the new vertex at the end...
        
        addPoint(px, py);
        
        
		
        // ...then we do the swap
        
        int dummy;
        
        
        for(int i=nPoints-1; i>minv; --i) {  
			virtualPoint[i].x=virtualPoint[i-1].x;
			virtualPoint[i].y=virtualPoint[i-1].y;
		}
		
		virtualPoint[minv].x=px;
		virtualPoint[minv].y=py;

        changed = true;
	}
	
	/** Add a point at the current polygon
		@param x the x coordinate of the point.
		@param y the y coordinate of the point.
	*/
	public void addPoint(int x, int y)
	{
		if(nPoints+2>=N_POINTS) {
			int o_n=N_POINTS;
			int i;
			N_POINTS += 10;
			Point[] nv = new Point[N_POINTS];
			for(i=0;i<o_n;++i) {
				nv[i]=virtualPoint[i];
			}
			for(;i<N_POINTS;++i) {
				nv[i]=new Point();
			}
			virtualPoint=nv;
		}
		// And now we enter the position of the point we are interested with
		virtualPoint[nPoints].x=x;
		virtualPoint[nPoints++].y=y;
		// We do need to shift the two points describing the position
		// of the text lines
		virtualPoint[getNameVirtualPointNumber()].x=x+5;
		virtualPoint[getNameVirtualPointNumber()].y=y+5;
		virtualPoint[getValueVirtualPointNumber()].x=x+5;
		virtualPoint[getValueVirtualPointNumber()].y=y+10;				
		changed = true;
	}

	private int xmin, ymin;
	private int width, height;
	
	public final void createPolygon(MapCoordinates coordSys)
	{
     		
     	int j;
        xmin = Integer.MAX_VALUE;
        ymin = Integer.MAX_VALUE;
        
        int xmax = -Integer.MAX_VALUE;
        int ymax = -Integer.MAX_VALUE;
        
        int x, y;
        
        p.reset();
     	for(j=0;j<nPoints;++j) {
     		x = coordSys.mapX(virtualPoint[j].x,virtualPoint[j].y);
     		y = coordSys.mapY(virtualPoint[j].x,virtualPoint[j].y);
      		p.addPoint(x,y);
      		
      		if (x<xmin) 
      			xmin=x;
      		if (x>xmax)
      			xmax=x;
      			
      		if(y<ymin)
      			ymin=y;
      		if(y>ymax)
      			ymax=y;
      
 		}
 		width = xmax-xmin;
 		height = ymax-ymin;
	}
	
	
	// Those are data which are kept for the fast redraw of this primitive. 
	// Basically, they are calculated once and then used as much as possible
	// without having to calculate everything from scratch.
	private Stroke stroke;
	private float w;
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
    	if(changed) {
    		changed=false;
    		createPolygon(coordSys);
   
 			w = (float)(Globals.lineWidth*coordSys.getXMagnitude());
 			if (w<D_MIN) w=D_MIN;
			
			if (strokeStyle==null) {
				strokeStyle = new StrokeStyle();
			}
			stroke = strokeStyle.getStroke(w, dashStyle);
		}
		
		if(!g.hitClip(xmin,ymin, width, height))
 			return;

		// Apparently, on some systems (like my iMac G5 with MacOSX 10.4.11)
		// setting the stroke takes a lot of time!
 		if(!stroke.equals(g.getStroke())) 
			g.setStroke(stroke);		

		// Here we implement a small optimization: when the polygon is very
		// small, it is not filled.
        if (isFilled && width>=2 && height >=2) 
 			g.fillPolygon(p);
 			
 		//g.drawPolygon(p);
 		// It seems that under MacOSX, drawing a polygon by cycling with
 		// the lines is much more efficient than the drawPolygon method.
 		// Probably, a further investigation is needed to determine if
 		// this situation is the same with more recent Java runtimes
 		// (mine is 1.5.something on an iMac G5 at 2 GHz and I made
 		// the same comparison with the same results with a MacBook 2GHz).
 		 
 		for(int i=0; i<nPoints-1; ++i) {
 			g.drawLine(p.xpoints[i], p.ypoints[i], p.xpoints[i+1],
 				p.ypoints[i+1]);
 		}
 		g.drawLine(p.xpoints[nPoints-1], p.ypoints[nPoints-1], p.xpoints[0],
 			p.ypoints[0]);
			
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
		
		if (tokens[0].equals("PP")||tokens[0].equals("PV")) {
 			if (N<6) {
 				IOException E=new IOException("bad arguments on PP/PV");
				throw E;
 			}
 			// Load the points in the virtual points associated to the 
 			// current primitive.
			int j=1;
			int i=0;
        	int x1 = 0;
        	int y1 = 0;
        	
      		while(j<N-1){
      		    if (j+1<N-1 && tokens[j+1].equals("FCJ")) 
      		    	break;
      			x1 = Integer.parseInt(tokens[j++]);
     			y1 = Integer.parseInt(tokens[j++]);
     			++i;
     			addPoint(x1,y1);
      		}	      				
      		nPoints=i;
      		virtualPoint[getNameVirtualPointNumber()].x=x1+5;
			virtualPoint[getNameVirtualPointNumber()].y=y1+5;
			virtualPoint[getValueVirtualPointNumber()].x=x1+5;
			virtualPoint[getValueVirtualPointNumber()].y=y1+10;		
      		if(N>j) {
      			parseLayer(tokens[j++]);
      			 
      			if(j<N-1 && tokens[j++].equals("FCJ")) {
 					dashStyle = Integer.parseInt(tokens[j++]);
 					// Parameters validation and correction
					if(dashStyle>=Globals.dashNumber)
						dashStyle=Globals.dashNumber-1;
					if(dashStyle<0)
						dashStyle=0;
 				}
 			}
      			
 			if (tokens[0].equals("PP"))
 				isFilled=true;
 			else
 				isFilled=false;
 			
 		} else {
 			IOException E=new IOException("PP/PV: Invalid primitive:"+tokens[0]+
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

		pd.parameter=new Boolean(isFilled);
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
	    	
    	int[] xp=new int[N_POINTS];
        int[] yp=new int[N_POINTS];
        
        int k;
                
        for(k=0;k<nPoints;++k){
        	xp[k]=virtualPoint[k].x;
            yp[k]=virtualPoint[k].y;
        }     
        
        if(isFilled && GeometricDistances.pointInPolygon(xp,yp,nPoints, px,py))
          	return 1;
            	
        
        // If the curve is not filled, we calculate the distance between the
        // given point and all the segments composing the curve and we 
        // take the smallest one.
        
        int distance=(int)Math.sqrt((px-xp[0])*(px-xp[0])+
        	(py-yp[0])*(py-yp[0]));
        
        int j;
        int d;
        for(int i=0; i<nPoints; ++i) {
        	j=i;
        	if (j==nPoints-1)
        		j=-1;
        		
        	d=GeometricDistances.pointToSegment(xp[i],
        		yp[i], xp[j+1],
        		yp[j+1], px,py);
        		
        	if(d<distance) 
        		distance = d;
        }
        
        return distance;
	}
	
	/** Obtain a string command descripion of the primitive.
		@return the FIDOCAD command line.
	*/
	public String toString(boolean extensions)
	{
		StringBuffer temp=new StringBuffer(25);

		if(isFilled)
			temp.append("PP ");
		else
			temp.append("PV ");
			
		for(int i=0; i<nPoints;++i) {
			temp.append(virtualPoint[i].x);
			temp.append(" ");
			temp.append(virtualPoint[i].y);
			temp.append(" ");
		}
		
		temp.append(getLayer());
		temp.append("\n");
		
		String cmd=temp.toString();
		
		if(extensions) {
			if (dashStyle>0 || hasName() || hasValue()) {
				String text = "0";
				if (name.length()!=0 || value.length()!=0)
					text = "1";
		 		cmd+="FCJ "+dashStyle+" "+text+"\n";
			}
		}
		// The false is needed since saveText should not write the FCJ tag.
		cmd+=saveText(false);
		return cmd;
	}
	
	public void export(ExportInterface exp, MapCoordinates cs) 
		throws IOException
	{
		exportText(exp, cs, -1);
		Point2D.Double[] vertices = new Point2D.Double[nPoints]; 
		
		for(int i=0; i<nPoints;++i){
			vertices[i]=new Point2D.Double();
			vertices[i].x=cs.mapX(virtualPoint[i].x,virtualPoint[i].y);
			vertices[i].y=cs.mapY(virtualPoint[i].x,virtualPoint[i].y);
		}
		
		exp.exportPolygon(vertices, nPoints, isFilled, getLayer(), dashStyle,
			Globals.lineWidth*cs.getXMagnitude());
	}
	/** Get the number of the virtual point associated to the Name property
		@return the number of the virtual point associated to the Name property
	*/
	public int getNameVirtualPointNumber()
	{
		return nPoints;
	}
	
	/** Get the number of the virtual point associated to the Value property
		@return the number of the virtual point associated to the Value property
	*/
	public  int getValueVirtualPointNumber()
	{
		return nPoints+1;
	}
	
}