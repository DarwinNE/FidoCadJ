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



/** Class to handle the ComplexCurve primitive.

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

	Copyright 2011-2012 by Davide Bucci
	
	Spline calculations by Tim Lambert
	http://www.cse.unsw.edu.au/~lambert/splines/
</pre>

@author Davide Bucci
*/

public final class PrimitiveComplexCurve extends GraphicPrimitive
{

	private int nPoints;
	private boolean isFilled;
	private boolean isClosed;
	
	private boolean arrowStart;
	private boolean arrowEnd;
	
	private int arrowLength;
	private int arrowHalfWidth;
	
	private int arrowStyle;
	private int dashStyle;
	
	// The natural spline is drawn as a polygon. Even if this is a rather
	// crude technique, it fits well with the existing architecture (in 
	// particular for the export facilities), since everything that it is
	// needed for a polygon is available and can be reused here.
	
	// A first polygon stored in screen coordinates
	private Polygon p;
	
	// A second polygon stored in logical coordinates
 	private Polygon q;
 	
	// 5 points is the initial size, which is increased if needed
 	int N_POINTS=5;
	
	static final int STEPS=24;

	
	/** Gets the number of control points used.
		@return the number of points used by the primitive
	*/
	
	public int getControlPointNumber()
	{
		return nPoints+2;
	}
	
	/** Create a ComplexCurve. Add points with the addPoint method.
	*/
	public PrimitiveComplexCurve()
	{
		super();
		isFilled=false;
		nPoints=0;
		p = new Polygon();
		initPrimitive(N_POINTS);
	}
	/** Create a ComplexCurve. Add points with the addPoint method.
		
		@param f specifies if the ComplexCurve should be filled 
		@param c specifies if the ComplexCurve should be closed
		@param layer the layer to be used.
		@param dashSt the dash style
		
	*/
	
	public PrimitiveComplexCurve(boolean f, boolean c, int layer, 
		boolean arrowS, boolean arrowE,
		int arrowSt, int arrowLe, int arrowWi, int dashSt)
	{
		super();
		
		arrowLength = arrowLe;
		arrowHalfWidth = arrowWi;
		arrowStart = arrowS;
		arrowEnd = arrowE;
		arrowStyle =arrowSt;
		dashStyle=dashSt;
		
		p = new Polygon();
		initPrimitive(N_POINTS);
		nPoints=0;
		isFilled=f;
		isClosed=c;
		dashStyle=dashSt;
		setLayer(layer);
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
        
        int d;
        int minv=0;
        
        for(int i=0; i<q.npoints-1; ++i) {
        	d=GeometricDistances.pointToSegment(q.xpoints[i],
        		q.ypoints[i], q.xpoints[i+1],
        		q.ypoints[i+1], px,py);
        		
        	if(d<distance) {
        		distance = d;
        		minv=i-1;
        	}
        }
        
        minv /= STEPS;
        ++minv;
        if(minv<0) minv=nPoints-1;
              
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
	
	/** Add a point at the current ComplexCurve
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
	
	/** Create the polygon associated to the complex curve. This is a crude
		technique, but it is very easy to be implemented.
	
	*/
	public final Polygon createComplexCurve(MapCoordinates coordSys)
	{
     		
        xmin = Integer.MAX_VALUE;
        ymin = Integer.MAX_VALUE;
        
        int xmax = -Integer.MAX_VALUE;
        int ymax = -Integer.MAX_VALUE;
        
        int np=nPoints;
                
        double [] xPoints = new double[np];
        double [] yPoints = new double[np];
        
        int i;
        
        for (i=0; i<nPoints; ++i) {
        	xPoints[i] = coordSys.mapXr(virtualPoint[i].x,virtualPoint[i].y);
        	yPoints[i] = coordSys.mapYr(virtualPoint[i].x,virtualPoint[i].y);
        }
        
        // If the curve is closed, we need to add a last point which is the
        // same as the first one.
        
        Cubic[] X;
        Cubic[] Y;
        
        if(isClosed) {
        	X = calcNaturalCubicClosed(np-1, xPoints);
      		Y = calcNaturalCubicClosed(np-1, yPoints);
        } else {
        	X = calcNaturalCubic(np-1, xPoints);
      		Y = calcNaturalCubic(np-1, yPoints);
      	}
      	
      	if(X==null || Y==null) return null;
      	
      	// very crude technique: just break each segment up into steps lines 
      	Polygon poly = new Polygon();
      	poly.addPoint((int) Math.round(X[0].eval(0)),
		 	(int) Math.round(Y[0].eval(0)));
		 	
		int x, y;
		 	
      	for (i = 0; i < X.length; ++i) {
			for (int j = 1; j <= STEPS; ++j) {
	  			double u = j / (double) STEPS;
	  			x=(int)Math.round(X[i].eval(u));
	  			y=(int)Math.round(Y[i].eval(u));
	  			poly.addPoint(x, y);
	  			coordSys.trackPoint(x,y);
	  			if (x<xmin) 
      				xmin=x;
      			if (x>xmax)
      				xmax=x;
      			
      			if(y<ymin)
      				ymin=y;
      			if(y>ymax)
      				ymax=y;
			}
      	} 
      	width = xmax-xmin;
 		height = ymax-ymin;
      	
      	return poly;    	
	}
 
        
    /** Code adapted from Tim Lambert's snippets:
    	http://www.cse.unsw.edu.au/~lambert/splines/
    	
    	Used here with permissions (hey, thanks a lot, Tim!)
    */
    Cubic[] calcNaturalCubic(int n, double[] x) {
  	  	
		if(n<1) return null;
		
  	  	double[] gamma = new double[n+1];
    	double[] delta = new double[n+1];
    	double[] D = new double[n+1];
    	int i;
   
   		/* We solve the equation
       	[2 1       ] [D[0]]   [3(x[1] - x[0])  ]
       	|1 4 1     | |D[1]|   |3(x[2] - x[0])  |
       	|  1 4 1   | | .  | = |      .         |
       	|    ..... | | .  |   |      .         |
       	|     1 4 1| | .  |   |3(x[n] - x[n-2])|
       	[       1 2] [D[n]]   [3(x[n] - x[n-1])]
       
       	by using row operations to convert the matrix to upper triangular
       	and then back substitution.  The D[i] are the derivatives at the knots.
       */
    	gamma[0] = 1.0/2.0;
    	for (i = 1; i<n; ++i) {
      		gamma[i] = 1.0/(4.0-gamma[i-1]);
    	}
    	gamma[n] = 1.0/(2.0-gamma[n-1]);
    
    	delta[0] = 3*(x[1]-x[0])*gamma[0];
    	for (i = 1; i < n; ++i) {
      		delta[i] = (3.0*(x[i+1]-x[i-1])-delta[i-1])*gamma[i];
    	}
    	delta[n] = (3.0*(x[n]-x[n-1])-delta[n-1])*gamma[n];
    
    	D[n] = delta[n];
    	for (i = n-1; i>=0; --i) {
      		D[i] = delta[i] - gamma[i]*D[i+1];
    	}

    	/* now compute the coefficients of the cubics */
    	Cubic[] C = new Cubic[n];
    	for (i = 0; i<n; ++i) {
      		C[i] = new Cubic(x[i], D[i], 3.0*(x[i+1] - x[i]) -2.0*D[i]-D[i+1],
		       2.0*(x[i] - x[i+1]) + D[i] + D[i+1]);
    	}
    	return C;
  	}
  	
  	/** Code mainly taken from Tim Lambert's snippets:
    	http://www.cse.unsw.edu.au/~lambert/splines/
    	
    	Used here with permissions (hey, thanks a lot, Tim!)
      	calculates the closed natural cubic spline that interpolates
    	 x[0], x[1], ... x[n]
     	The first segment is returned as
     	C[0].a + C[0].b*u + C[0].c*u^2 + C[0].d*u^3 0<=u <1
     	the other segments are in C[1], C[2], ...  C[n] */

  	Cubic[] calcNaturalCubicClosed(int n, double[] x) {
  		
		if(n<1) return null;
		
    	double[] w = new double[n+1];
    	double[] v = new double[n+1];
    	double[] y = new double[n+1];
    	double[] D = new double[n+1];
    	double z, F, G, H;
    	int k;
    	/* We solve the equation
    	   [4 1      1] [D[0]]   [3(x[1] - x[n])  ]
   		   |1 4 1     | |D[1]|   |3(x[2] - x[0])  |
    	   |  1 4 1   | | .  | = |      .         |
    	   |    ..... | | .  |   |      .         |
    	   |     1 4 1| | .  |   |3(x[n] - x[n-2])|
    	   [1      1 4] [D[n]]   [3(x[0] - x[n-1])]
       
       		by decomposing the matrix into upper triangular and lower matrices
       		and then back substitution.  See Spath "Spline Algorithms for 
       		Curves and Surfaces" pp 19--21. The D[i] are the derivatives at 
       		the knots.
       	*/
    	w[1] = v[1] = z = 1.0f/4.0f;
    	y[0] = z * 3 * (x[1] - x[n]);
    	H = 4;
    	F = 3 * (x[0] - x[n-1]);
    	G = 1;
    	for (k = 1; k < n; ++k) {
      		v[k+1] = z = 1/(4 - v[k]);
      		w[k+1] = -z * w[k];
     		y[k] = z * (3*(x[k+1]-x[k-1]) - y[k-1]);
      		H = H - G * w[k];
      		F = F - G * y[k-1];
      		G = -v[k] * G;
    	}
   		H = H - (G+1)*(v[n]+w[n]);
    	y[n] = F - (G+1)*y[n-1];
    
    	D[n] = y[n]/H;
    	D[n-1] = y[n-1] - (v[n]+w[n])*D[n]; /* This equation is WRONG! in 
    										   my copy of Spath */
    	for (k = n-2; k >= 0; --k) {
      		D[k] = y[k] - v[k+1]*D[k+1] - w[k+1]*D[n];
    	}


    	/* now compute the coefficients of the cubics */
    	Cubic[] C = new Cubic[n+1];
    	for ( k = 0; k < n; ++k) {
      		C[k] = new Cubic((float)x[k], D[k], 
      			3*(x[k+1] - x[k]) - 2*D[k] - D[k+1],
		       	2*(x[k] - x[k+1]) + D[k] + D[k+1]);
    	}
    	C[n] = new Cubic((float)x[n], D[n], 3*(x[0] - x[n]) - 2*D[n] - D[0],
		     2*(x[n] - x[0]) + D[n] + D[0]);
    	return C;
  	}


	/** Remove the control point of the spline closest to the given
		coordinates, if the distance is less than a certain tolerance
	
		@param x			the x coordinate of the target
		@param y			the y coordinate of the target
		@param tolerance	the tolerance
	
	*/
	public void removePoint(int x, int y, double tolerance)
	{
	
		// We can not have a spline with less than three vertices
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


	// Those are data which are kept for the fast redraw of this primitive. 
	// Basically, they are calculated once and then used as much as possible
	// without having to calculate everything from scratch.
	private Stroke stroke;
	private float w;
	private GeneralPath gp;
	
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

			// Important: note that createComplexCurve has some important
			// side effects as the update of the xmin, ymin, width and height
			// variables. This means that the order of the two following 
			// commands is important!
   			q=createComplexCurve(new MapCoordinates());
    		p=createComplexCurve(coordSys);
    		
    		gp = new GeneralPath();
    		
    		for(int i=0; i<q.npoints-1; ++i) {
   		
 			w = (float)(Globals.lineWidth*coordSys.getXMagnitude());
 			if (w<D_MIN) w=D_MIN;
			if (strokeStyle==null) {
				strokeStyle = new StrokeStyle();
			}
			stroke = strokeStyle.getStroke(w, dashStyle);
		}
		
		if (p==null)
			return;
		
		// If the curve is outside of the shown portion of the drawing,
		// exit immediately.
		if(!g.hitClip(xmin,ymin, width, height))
 			return;
 			
		// Apparently, on some systems (like my iMac G5 with MacOSX 10.4.11)
		// setting the stroke takes a lot of time!
 		if(!stroke.equals(g.getStroke())) 
			g.setStroke(stroke);		

		// If needed, fill the interior of the shape
        if (isFilled) 
 			g.fillPolygon(p);
 		
 		/*
 		// Explicit drawing of the polygon.
 		for(int i=0; i<p.npoints-1; ++i) {
 			g.drawLine(p.xpoints[i], p.ypoints[i], p.xpoints[i+1],
 				p.ypoints[i+1]);
 		}
 		if(isClosed)
 			g.drawLine(p.xpoints[p.npoints-1], p.ypoints[p.npoints-1], 
 			p.xpoints[0], p.ypoints[0]);
 		
 		*/
 		
 		
 		
 		
 		
 		// Ensure that there are enough points to calculate the derivative.
 		if (p.npoints<2)
 			return;
 		
 		// Draw the arrows if they are needed
 		if (arrowStart || arrowEnd) {
			int h=coordSys.mapXi(arrowHalfWidth,arrowHalfWidth,false)-
 				coordSys.mapXi(0,0, false);
			int l=coordSys.mapXi(arrowLength,arrowLength,false)-
				coordSys.mapXi(0,0, false);
			
 			if (arrowStart&&!isClosed) {
 				Arrow.drawArrow(g, p.xpoints[0], p.ypoints[0],
					p.xpoints[1], p.ypoints[1],l, h, arrowStyle);
			}
			
			if (arrowEnd&&!isClosed) {
				Arrow.drawArrow(g, p.xpoints[p.npoints-1], 
				p.ypoints[p.npoints-1],
					p.xpoints[p.npoints-2], p.ypoints[p.npoints-2],l, h, 
					arrowStyle);	
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
		
		if (tokens[0].equals("CP")||tokens[0].equals("CV")) {
 			if (N<6) {
 				IOException E=new IOException("bad arguments on CP/CV");
				throw E;
 			}
 			// Load the points in the virtual points associated to the 
 			// current primitive.
			int j=1;
			int i=0;
        	int x1 = 0;
        	int y1 = 0;
        	
        	// The first token says if the spline is opened or closed
        	if(tokens[j++].equals("1"))
        		isClosed = true;
        	else
        		isClosed = false;
        	// Then we have the points defining the curve
      		while(j<N-1){
      		    if (j+1<N-1 && tokens[j+1].equals("FCJ")) 
      		    	break;
      			x1 =Integer.parseInt(tokens[j++]);
     			y1 =Integer.parseInt(tokens[j++]);
     			++i;
     			addPoint(x1,y1);
      		}	      				
      		nPoints=i;
      		
      		// We specify now the standard position of the name and value 
      		virtualPoint[getNameVirtualPointNumber()].x=x1+5;
			virtualPoint[getNameVirtualPointNumber()].y=y1+5;
			virtualPoint[getValueVirtualPointNumber()].x=x1+5;
			virtualPoint[getValueVirtualPointNumber()].y=y1+10;		
			
			// And we check finally for extensions (FCJ)
			if(N>j) {
      			parseLayer(tokens[j++]);
      			
				if(N>j && tokens[j++].equals("FCJ")) {
 					int arrows = Integer.parseInt(tokens[j++]);
 					arrowStart = (arrows & 0x01) !=0;
 					arrowEnd = (arrows & 0x02) !=0;
 				
 					arrowStyle = Integer.parseInt(tokens[j++]);
 					arrowLength = Integer.parseInt(tokens[j++]);
 					arrowHalfWidth = Integer.parseInt(tokens[j++]);
 					dashStyle = Integer.parseInt(tokens[j++]);
 					// Parameters validation and correction
					if(dashStyle>=Globals.dashNumber)
						dashStyle=Globals.dashNumber-1;
					if(dashStyle<0)
						dashStyle=0;
 				}
 			}
  		
			
 			if (tokens[0].equals("CP"))
 				isFilled=true;
 			else
 				isFilled=false;
 			
 		} else {
 			IOException E=new IOException("CP/CV: Invalid primitive:"+tokens[0]+
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
		pd.parameter=new Boolean(isClosed);
		pd.description=Globals.messages.getString("ctrl_closed_curve");
		pd.isExtension = true;
		v.add(pd);

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
		// Check, just for sure...
		if (pd.parameter instanceof Boolean)
			isClosed=((Boolean)pd.parameter).booleanValue();
		else
		 	System.out.println("Warning: unexpected parameter!"+pd);
		
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
		
		return i;
	}

	
	/** Gets the distance (in primitive's coordinates space) between a 
	    given point and the primitive. 
	    When it is reasonable, the behaviour can be binary (ComplexCurves, 
	    ovals...). In other cases (lines, points), it can be proportional.
		@param px the x coordinate of the given point
		@param py the y coordinate of the given point
	*/
	public int getDistanceToPoint(int px, int py)
	{
		// Here we check if the given point lies inside the text areas
        
	    if(checkText(px, py))
	    	return 0;
        
        int distance = 100;
        
        // In this case, the user has not introduced a complete curve,
        // but just one point.
        if(p==null) {
        	return GeometricDistances.pointToPoint(virtualPoint[0].x,
        		virtualPoint[0].y,
				px,py);	
        }
        
        // If the curve is filled, we check if the given point lies inside
        // the polygon.
        if(isFilled && q.contains(px, py)) {
        	return 1;
        }
        
        // If the curve is not filled, we calculate the distance between the
        // given point and all the segments composing the curve and we 
        // take the smallest one.
        for(int i=0; i<q.npoints-1; ++i) {
        	int d=GeometricDistances.pointToSegment(q.xpoints[i],
        		q.ypoints[i], q.xpoints[i+1],
        		q.ypoints[i+1], px,py);
        		
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
			temp.append("CP ");
		else
			temp.append("CV ");
			
		if(isClosed)
			temp.append("1 ");
		else
			temp.append("0 ");

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
		 	int arrows = (arrowStart?0x01:0x00)|(arrowEnd?0x02:0x00);
		 			 	
		 	if (arrows>0 || dashStyle>0 || hasName() || hasValue()) {
		 		String text = "0";
		 		// We take into account that there may be some text associated
		 		// to that primitive.
		 		if (name.length()!=0 || value.length()!=0) 
		 			text = "1";
		 		cmd+="FCJ "+arrows+" "+arrowStyle+" "+arrowLength+" "+
		 		arrowHalfWidth+" "+dashStyle+" "+text+"\n";
		 	}
		}
		// The false is needed since saveText should not write the FCJ tag.
		cmd+=saveText(false);
		return cmd;
	}
	
	
	public void export(ExportInterface exp, MapCoordinates cs) 
		throws IOException
	{
        double [] xPoints = new double[nPoints];
        double [] yPoints = new double[nPoints];
        Point2D.Double[] vertices = new Point2D.Double[nPoints*STEPS+1];
      
        int i;
        
        for (i=0; i<nPoints; ++i) {
        	xPoints[i] = cs.mapXr(virtualPoint[i].x,virtualPoint[i].y);
        	yPoints[i] = cs.mapYr(virtualPoint[i].x,virtualPoint[i].y);
        	
        	// This is a trick: we do not use another array, but we pre-charge
        	// the control points in vertices (sure we have some place, at
        	// least if STEPS>-1). If the export is done via a polygon, those
        	// points will be discarded and the array reused.
        	vertices[i] = new Point2D.Double();
        	vertices[i].x = xPoints[i];
        	vertices[i].y = yPoints[i];
        }
		
		// Check if the export is handled via a dedicated curve primitive.
		// If not, we continue using a polygon with an high number of 
		// vertex
		if (!exp.exportCurve(vertices, nPoints, isFilled, isClosed, getLayer(), 
			    arrowStart, arrowEnd, arrowStyle, 
				(int)(arrowLength*cs.getXMagnitude()), 
				(int)(arrowHalfWidth*cs.getXMagnitude()), 
				dashStyle, Globals.lineWidth*cs.getXMagnitude())) {
			
			exportAsPolygon(xPoints, yPoints, vertices, exp, cs);
    	        
        
			// Draw the arrows if they are needed
			if(q.npoints>2) {
				if (arrowStart&&!isClosed) {
					exp.exportArrow(vertices[0].x, vertices[0].y,
						vertices[1].x, vertices[1].y, 
						arrowLength*cs.getXMagnitude(), 
						arrowHalfWidth*cs.getXMagnitude(), 
						arrowStyle);
				}
			
				if (arrowEnd&&!isClosed) {
					exp.exportArrow(vertices[q.npoints-1].x, 
						vertices[q.npoints-1].y,
						vertices[q.npoints-2].x, vertices[q.npoints-2].y, 
						arrowLength*cs.getXMagnitude(), 
						arrowHalfWidth*cs.getXMagnitude(), 
						arrowStyle);	
				}
			}
		}
		exportText(exp, cs, -1);

	}
	
	/** Expansion of the curve in a polygon with a big number of corners.
		This is useful when some sort of spline command is not available on
		the export format chosen.
	
	*/
	private void exportAsPolygon(double [] xPoints, double [] yPoints,
		Point2D.Double[] vertices,
		ExportInterface exp, MapCoordinates cs)
		throws IOException
	{
		Cubic[] X;
        Cubic[] Y;
        int i;
        
        if(isClosed) {
        	X = calcNaturalCubicClosed(nPoints-1, xPoints);
      		Y = calcNaturalCubicClosed(nPoints-1, yPoints);
        } else {
        	X = calcNaturalCubic(nPoints-1, xPoints);
      		Y = calcNaturalCubic(nPoints-1, yPoints);
      	}
      	
      	if(X==null || Y==null) return;
      	
      	/* very crude technique - just break each segment up into steps lines */
      	
      	vertices[0]=new Point2D.Double();
	  			
	  	vertices[0].x=X[0].eval(0);
	  	vertices[0].y=Y[0].eval(0);
		 	
		int x, y;
		 	
      	for (i = 0; i < X.length; ++i) {
			for (int j = 1; j <= STEPS; ++j) {
	  			double u = j / (double) STEPS;
				vertices[i*STEPS+j]=new Point2D.Double();
	  			
	  			vertices[i*STEPS+j].x=X[i].eval(u);
	  			vertices[i*STEPS+j].y=Y[i].eval(u);
			}
      	} 
		
		
		if (isClosed) {
			exp.exportPolygon(vertices, q.npoints, isFilled, getLayer(), 
				dashStyle, Globals.lineWidth*cs.getXMagnitude());
		} else {
			for(i=1; i<q.npoints;++i){
				exp.exportLine(vertices[i-1].x,
					   vertices[i-1].y,
					   vertices[i].x,
					   vertices[i].y,
					   getLayer(),
					   false, false,
					   0, 0, 0,
					   dashStyle, 
					   Globals.lineWidth*cs.getXMagnitude()); 
			}
		}
	}
	/** Get the number of the virtual point associated to the Name property
		@return the number of the virtual point associated to the Name property
	*/
	public int getNameVirtualPointNumber()
	{
		return nPoints;
	}
	
	/** Get the number of the virtual point associated to the Value property
		@return the number of the virtual point associated to the Value 
		property
	*/
	public  int getValueVirtualPointNumber()
	{
		return nPoints+1;
	}
	
}

/** this class represents a cubic polynomial, by Tim Lambert */

class Cubic {

  double a,b,c,d;         /* a + b*u + c*u^2 +d*u^3 */

  public Cubic(double a, double b, double c, double d){
    this.a = a;
    this.b = b;
    this.c = c;
    this.d = d;
  }

  
  /** evaluate cubic */
  public double eval(double u) {
    return (((d*u) + c)*u + b)*u + a;
  }
}