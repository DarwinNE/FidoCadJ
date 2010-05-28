package geom;

/** 
	Calculate geometric distances between a given point and a few
	geometric objects.


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

	Copyright 2008 by Davide Bucci
</pre>	

    
    @author Davide Bucci
    @version 1.1, June 2008
*/

public class GeometricDistances {

	// Number of segments evaluated when calculatin the distance between a 
	// point and a Bézier curve.
	public static final int MAX_BEZIER_SEGMENTS=10;
	
	/** Calculate the euclidean distance between two points.
	
	    	    
	    @param xa the X coordinate of the first point
	    @param ya the Y coordinate of the first point
	    @param xb the X coordinate of the second point
	    @param yb the Y coordinate of the second point
	*/
	public static double pointToPoint(double xa, double ya,
								 double xb, double yb)
	{
		return Math.sqrt((xa-xb)*(xa-xb)+(ya-yb)*(ya-yb));
	
	}
	
	
	/** Calculate the euclidean distance between two points.
	
	    @param xa the X coordinate of the first point
	    @param ya the Y coordinate of the first point
	    @param xb the X coordinate of the second point
	    @param yb the Y coordinate of the second point
	*/
	public static int pointToPoint(int xa, int ya,
						 	int xb, int yb)
	{
		return (int)Math.sqrt((xa-xb)*(xa-xb)+(ya-yb)*(ya-yb));
	
	}
	
	/** Calculate the euclidean distance between a point and a segment.
	    Adapted from http://www.vb-helper.com/howto_distance_point_to_line.html
	    	    
	    @param xa the X coordinate of the starting point of the segment
	    @param ya the Y coordinate of the starting point of the segment
	    @param xb the X coordinate of the ending point of the segment
	    @param yb the Y coordinate of the ending point of the segment
	    @param x the X coordinate of the point
	    @param y the Y coordinate of the point
	*/
	public static double pointToSegment(double xa, double ya,
								 double xb, double yb,
								 double x, double y )
	{
		double dx;
		double dy;
		double t;
		
		
		dx=xb-xa;
		dy=yb-ya;
		
		if (dx==0 && dy==0) {
			dx=x-xa;
			dy=y-yb;
			return Math.sqrt(dx*dx+dy*dy);
		}
		
		t=((x-xa)*dx+(y-ya)*dy)/(dx*dx+dy*dy);
		if (t<0) {
			dx=x-xa;
			dy=y-ya;
		} else if (t>1){
			dx=x-xb;
			dy=y-yb;
		} else {
			dx=x-(xa+t*dx);
			dy=y-(ya+t*dy);
		}
		return Math.sqrt(dx*dx+dy*dy);
	
	}
	public static int pointToSegment(int xa, int ya,
							  int xb, int yb,
							  int x, int y)
	{
		return (int)GeometricDistances.pointToSegment((double)xa,(double)ya,
					(double)xb,(double)yb, (double)x,(double)y);

	
	}

	/** Tells if a point lies inside a polygon, using the alternance rule
		adapted from a snippet by Randolph Franklin, in Paul Bourke pages:
		http://local.wasp.uwa.edu.au/~pbourke/geometry/insidepoly/
		
		@param npol number of vertex
		@param xp vector of x coordinates of vertices
		@param yp vector of y coordinates of vertices
		@param x x coordinate of the point
		@param y y coordinate of the point
		@return true if the point lies in the polygon, false otherwise.
	*/
	public static boolean pointInPolygon(int npol, 
			double[] xp, double[] yp, double x, double y)
    {
      	int i, j;
      	boolean c = false;
      	
      	for (i = 0,j = npol-1; i < npol; j=i++) {
      		if ((((yp[i] <= y) && (y < yp[j])) ||
             	((yp[j] <= y) && (y < yp[i]))) &&
            	(x < (xp[j] - xp[i]) * (y - yp[i]) / (yp[j] - yp[i]) + xp[i]))
          		c = !c;
        	j=i;
      	}
        return c;
    }
	/** Tells if a point lies inside an ellipse
			
		@param ex x coordinate of the top left corner of the ellipse
		@param ey y coordinate of the top left corner of the ellipse
		@param w width of the ellipse
		@param h height of the ellipse
		@param px x coordinate of the point
		@param py y coordinate of the point
		@return true if the point lies in the ellipse, false otherwise.
	*/
	public static boolean pointInEllipse(double ex,double ey,double w,
								  double h,double px,double py) 
	{
    	//Determine and normalize quadrant.
    	double dx = Math.abs(px-(ex+w/2));
    	double dy = Math.abs(py-(ey+h/2));
    	double l;
	
		
    	//Shortcut
    	if( dx > w/2 || dy > h/2 ) {
     	 	return false;
   		 }

    	// Calculate the semi-latus rectum of the ellipse at the given point
    	// The multiplication by four is mandatory as the principal axis of an 
    	// ellipse are the half of the width and the height.
    	
		l=4*dx*dx/w/w+4*dy*dy/h/h;
		
    	return l<1;
  	}  
  	/** Tells if a point lies inside an ellipse (integer version)
		
		
		@param ex x coordinate of the top left corner of the ellipse
		@param ey y coordinate of the top left corner of the ellipse
		@param w width of the ellipse
		@param h height of the ellipse
		@param px x coordinate of the point
		@param py y coordinate of the point
		@return true if the point lies in the ellipse, false otherwise.
	*/
  	public static boolean pointInEllipse(int ex,int ey,int w,
								  int h, int px,int py) 
	{
		return pointInEllipse((double) ex,(double) ey,(double) w,
							  (double) h,(double) px,(double) py); 
	}
	
	/** Give the distance between the given point and the ellipse path. The
		difference with pointInEllipse is that pointInEllipse gives 0 when
		the point is inside the ellipse, where here we get the distance
		with the contour of the ellipse.
			
		@param ex x coordinate of the top left corner of the ellipse
		@param ey y coordinate of the top left corner of the ellipse
		@param w width of the ellipse
		@param h height of the ellipse
		@param px x coordinate of the point
		@param py y coordinate of the point
		@return the distance to the contour of the ellipse
	*/
	public static double pointToEllipse(double ex,double ey,double w,
								  double h,double px,double py) 
	{
    	//Determine and normalize quadrant.
    	double dx = Math.abs(px-(ex+w/2));
    	double dy = Math.abs(py-(ey+h/2));
    	double l;
	
		// Treat separately the degenerate cases. This will avoid a divide
		// by zero anomalous situation.
		
    	if (w==0) 
    		return pointToSegment(ex, ey, ex, ey+h, px, py);
    	
    	if (h==0) 
    		return pointToSegment(ex, ey, ex+w, ey, px, py);
    	
		
    	// Calculate the semi-latus rectum of the ellipse at the given point
    	// The multiplication by four is mandatory as the principal axis of an 
    	// ellipse are the half of the width and the height.
    	
    	
		l=4.0*dx*dx/w/w+4.0*dy*dy/h/h;

		// I had to divide by 2 to compensate the loss of precision in certain
		// cases. Maybe, this is due to the integer conversion when using
		// the integer routine?
		
	
		l=Math.sqrt(Math.abs(l-1.0)*Math.min(w,h)*Math.min(w,h)/4)/2;
    	return l;
  	}  
  	/** Give the distance between the given point and the ellipse path
  		(integer version)
		
		
		@param ex x coordinate of the top left corner of the ellipse
		@param ey y coordinate of the top left corner of the ellipse
		@param w width of the ellipse
		@param h height of the ellipse
		@param px x coordinate of the point
		@param py y coordinate of the point
		@return the distance to the contour of the ellipse
	*/
  	public static int pointToEllipse(int ex,int ey,int w,
								  int h, int px,int py) 
	{
		return (int)pointToEllipse((double) ex,(double) ey,(double) w,
							  (double) h,(double) px,(double) py); 
	}
	
	
	
	/** Tells if a point lies inside a rectangle
		
		
		@param ex x coordinate of the top left corner of the rectangle
		@param ey y coordinate of the top left corner of the rectangle
		@param w width of the rectangle
		@param h height of the rectangle
		@param px x coordinate of the point
		@param py y coordinate of the point
		@return true if the point lies in the ellipse, false otherwise.
	*/
	public static boolean pointInRectangle(double ex,double ey,double w,
								  double h,double px,double py) 
	{
		if((ex<=px)&&(px<ex+w) && (ey<=py)&&(py<ey+h))
			return true;
		else
			return false;
	}
	
	/** Tells if a point lies inside a rectangle, integer version
		
		
		@param ex x coordinate of the top left corner of the rectangle
		@param ey y coordinate of the top left corner of the rectangle
		@param w width of the rectangle
		@param h height of the rectangle
		@param px x coordinate of the point
		@param py y coordinate of the point
		@return true if the point lies in the ellipse, false otherwise.
	*/
	public static boolean pointInRectangle(int ex,int ey,int w,
								  int h, int px,int py) 
	{
/*		globals.actualG.drawRect(globals.actualMap.mapX(ex,ey),
			globals.actualMap.mapY(ex,ey),
			w*(int)globals.actualMap.getXMagnitude(),
			h*(int)globals.actualMap.getYMagnitude());
		globals.actualG.drawRect(globals.actualMap.mapX(px,py),
			globals.actualMap.mapY(px,py),
			1,
			1);*/
		
		
		if((ex<=px)&&(px<ex+w) && (ey<=py)&&(py<ey+h))
			return true;
		else
			return false;
	}
	
	
	/** Give the distance between the given point and the borders of a 
		rectangle
		
		@param ex x coordinate of the top left corner of the rectangle
		@param ey y coordinate of the top left corner of the rectangle
		@param w width of the rectangle
		@param h height of the rectangle
		@param px x coordinate of the point
		@param py y coordinate of the point
		@return the distance to one of the border of the rectangle
	*/
	public static double pointToRectangle(double ex,double ey,double w,
								   double h, double px,double py) 
	{
		double d1=pointToSegment(ex,ey,ex+w,ey,px,py);
		double d2=pointToSegment(ex+w,ey,ex+w,ey+h,px,py);
		double d3=pointToSegment(ex+w,ey+h,ex,ey+h,px,py);
		double d4=pointToSegment(ex,ey+h,ex,ey,px,py);
		
		return Math.min(Math.min(d1,d2),Math.min(d3,d4));
	}
	
	/** Give the distance between the given point and the borders of a 
		rectangle (integer version)
		
		@param ex x coordinate of the top left corner of the rectangle
		@param ey y coordinate of the top left corner of the rectangle
		@param w width of the rectangle
		@param h height of the rectangle
		@param px x coordinate of the point
		@param py y coordinate of the point
		@return the distance to one of the border of the rectangle
	*/
	public static int pointToRectangle(int ex,int ey,int w,
								  int h, int px,int py) 
	{
		return (int)pointToRectangle((double) ex,(double) ey,(double) w,
								   (double) h, (double) px,(double) py); 
	}


	/** Give an approximation of the distance between a point and
	    a Bézier curve. The curve is divided into MAX_BEZIER_SEGMENTS 
	    linear pieces and the distance is calculated with each piece.
	    The given distance is the minimum distance found for all pieces.
	    Freely inspired from the original Fidocad method.
	    
	    @param x1 x coordinate of the first control point of the Bézier curve.
	    @param y1 y coordinate of the first control point of the Bézier curve.
	    @param x2 x coordinate of the second control point of the Bézier curve.
	    @param y2 y coordinate of the second control point of the Bézier curve.
	    @param x3 x coordinate of the third control point of the Bézier curve.
	    @param y3 y coordinate of the third control point of the Bézier curve.
	    @param x4 x coordinate of the fourth control point of the Bézier curve.
	    @param y4 y coordinate of the fourth control point of the Bézier curve.

		@param px x coordinate of the point
		@param py y coordinate of the point	    
		
		@return an approximate value of the distance between the given point 
		        and the Bézier curve specified by the control points.
	*/

	public static int pointToBezier(int x1, int y1,
								 int x2, int y2,
								 int x3, int y3,
								 int x4, int y4,
								 int px,  int py)
	{
		int distance=Integer.MAX_VALUE;

		double b03, b13, b23, b33;
		double umu;
		double u;
		int j;
		int i=0;
		double[] x=new double[MAX_BEZIER_SEGMENTS+1];
		double[] y=new double[MAX_BEZIER_SEGMENTS+1];
		double limit=1.0/(double)(MAX_BEZIER_SEGMENTS);

		// (1+MAX_BEZIER_SEGMENTS/100) is to avoid roundoff
		
		for(u = 0; u < (1+MAX_BEZIER_SEGMENTS/100); u += limit)
		{		
			// This is the parametric form of the Bézier curve.
			// Probably, this is not the most convenient way to draw the
			// curve (one should probably use De Casteljau's Algorithm), 
			// but it indeed OK to find a few values such the one we need
			
		    umu=1-u;
			b03 = umu*umu*umu;
			b13 = 3 * u * umu*umu;
			b23 = 3 * u * u * umu;
			b33 = u*u*u;

			x[i] = (int)(x1 * b03 +
						x2 * b13 +
						x3 * b23 +
						x4 * b33);
			y[i] = (int)(y1 * b03 +
						y2 * b13 +
						y3 * b23 +
						y4 * b33);
			++i;				
		
		}
		
		// Calculate the distance of the given point with each of the
		// obtained segments.
		
		for(j=0;j<MAX_BEZIER_SEGMENTS;++j) {
			distance=(int)Math.min(distance, pointToSegment(x[j], y[j], 
												x[j+1], y[j+1],px, py));
		}
		return distance;
	}

}
