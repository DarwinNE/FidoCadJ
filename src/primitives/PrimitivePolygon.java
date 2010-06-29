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

	Copyright 2007-2010 by Davide Bucci
</pre>

@author Davide Bucci
*/

public class PrimitivePolygon extends GraphicPrimitive
{

	private int nPoints;
	private boolean isFilled;
	private int dashStyle;
	private Polygon p;
 

	// A polygon can be defined up to 100 points

	static final int N_POINTS=100;
	
	
	/** Gets the number of control points used.
		@return the number of points used by the primitive
	*/
	
	public int getControlPointNumber()
	{
		return nPoints;
	}
	 
	public PrimitivePolygon()
	{
		super();
		isFilled=false;
		nPoints=0;
		p = new Polygon();
		
		virtualPoint = new Point[N_POINTS];
		for(int i=0;i<N_POINTS;++i)
			virtualPoint[i]=new Point();
		
	}
	/** Create a polygon. Add points with the addPoint method.
		
		@param f specifies if the polygon should be filled 
		@param layer the layer to be used.
		@param dashSt the dash style
		
	*/
	
	public PrimitivePolygon(boolean f, int layer, int dashSt)
	{
		super();
		p = new Polygon();
		virtualPoint = new Point[N_POINTS];
		for(int i=0;i<N_POINTS;++i)
			virtualPoint[i]=new Point();
			
		nPoints=0;
		isFilled=f;
		dashStyle=dashSt;
		
		
		setLayer(layer);
	}
	
	/** Add a point at the current polygon
		@param x the x coordinate of the point.
		@param y the y coordinate of the point.
	*/
	public void addPoint(int x, int y)
	{
		if(nPoints>=N_POINTS)
			return;
		virtualPoint[nPoints].x=x;
		virtualPoint[nPoints++].y=y;
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
    	
    	if(changed) {
    		changed=false;
    		createPolygon(coordSys);
   
 			w = (float)(Globals.lineWidth*coordSys.getXMagnitude());
 			if (w<D_MIN) w=D_MIN;

			if (dashStyle>0) 
				stroke=new BasicStroke(w, 
                                BasicStroke.CAP_BUTT, 
                                BasicStroke.JOIN_MITER, 
                                10.0f, Globals.dash[dashStyle], 0.0f);
			else 
				stroke=new BasicStroke(w);
		}
		
		if(!g.hitClip(xmin,ymin, width, height))
 			return;

 		if(!stroke.equals(g.getStroke())) 
			g.setStroke(stroke);		

        if (isFilled) {
        	g.drawPolygon(p);
 			g.fillPolygon(p);	
 		} else {
 			g.drawPolygon(p);
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
		
		if (tokens[0].equals("PP")||tokens[0].equals("PV")) {
 			if (N<6) {
 				IOException E=new IOException("bad arguments on PP/PV");
				throw E;
 			}
 			// Load the points in the virtual points associated to the 
 			// current primitive.
			int j=1;
			int i=0;
        		
      		while(j<N-1){
      		    if (j+1<N-1 && tokens[j+1].equals("FCJ")) 
      		    	break;
      			virtualPoint[i].x=Integer.parseInt(tokens[j++]);
     			virtualPoint[i++].y=Integer.parseInt(tokens[j++]);
      		}	      				
      		nPoints=i;
      		
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
		
    	double[] xp=new double[N_POINTS];
        double[] yp=new double[N_POINTS];
        
        int k;
                
        for(k=0;k<nPoints;++k){
        	xp[k]=virtualPoint[k].x;
            yp[k]=virtualPoint[k].y;
        }     
        
        
        int distance=(int)Math.sqrt((px-xp[0])*(px-xp[0])+
        	(py-yp[0])*(py-yp[0]));
        
        if(GeometricDistances.pointInPolygon(nPoints,xp,yp, px,py))
          	distance=0;
            	
        return distance;
	}
	
	/** Obtain a string command descripion of the primitive.
		@return the FIDOCAD command line.
	*/
	public String toString(boolean extensions)
	{
		String cmd;
		if(isFilled)
			cmd="PP ";
		else
			cmd="PV ";
			
		for(int i=0; i<nPoints;++i)
			cmd+=virtualPoint[i].x+" "+virtualPoint[i].y+" ";
		
		cmd+=getLayer()+"\n";
		
		if(extensions) {
			if (dashStyle>0) 
		 		cmd+="FCJ "+dashStyle+"\n";
		
		}
		return cmd;
	}
	
	public void export(ExportInterface exp, MapCoordinates cs) 
		throws IOException
	{
	
		Point[] vertices = new Point[nPoints]; 
		
		for(int i=0; i<nPoints;++i){
			vertices[i]=new Point();
			vertices[i].x=cs.mapX(virtualPoint[i].x,virtualPoint[i].y);
			vertices[i].y=cs.mapY(virtualPoint[i].x,virtualPoint[i].y);
		}
		
		exp.exportPolygon(vertices, nPoints, isFilled, getLayer(), dashStyle,
			Globals.lineWidth);
		
	}
}