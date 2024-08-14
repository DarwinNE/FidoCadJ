package fidocadj.geom;

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
    along with FidoCadJ. If not,
    @see <a href=http://www.gnu.org/licenses/>http://www.gnu.org/licenses/</a>.

    Copyright 2008-2023 by Davide Bucci
</pre>

    @author Davide Bucci
*/

public final class GeometricDistances
{

    public static final int MIN_DISTANCE = 100;

    // Number of segments evaluated when calculatin the distance between a
    // point and a Bézier curve.
    public static final int MAX_BEZIER_SEGMENTS=10;

    // Some caching data
    private static  int idx;
    private static  int idy;
    private static  int it;
    private static  int ixmin;
    private static  int ixmax;
    private static  int iymin;
    private static  int iymax;

    private static double dx;
    private static double dy;
    private static double t;
    private static double xmin;
    private static double ymin;
    private static double xmax;
    private static double ymax;


    private static int i;
    private static int j;
    private static  boolean c;

    private GeometricDistances()
    {
        // Does nothing.
    }

    /** Calculate the euclidean distance between two points.
        The distance calculated here is not accurate. It is meant only
        to discriminate objects during the selection and therefore a
        inaccurate way to calculate distances is employed if the
        result is known to be greater than MIN_DISTANCE.

        @param xa the X coordinate of the first point.
        @param ya the Y coordinate of the first point.
        @param xb the X coordinate of the second point.
        @param yb the Y coordinate of the second point.
        @return the distance.
    */
    public static double pointToPoint(double xa, double ya,
                                 double xb, double yb)
    {
        if(Math.abs(xa-xb) < MIN_DISTANCE || Math.abs(ya-yb) < MIN_DISTANCE) {
            return Math.sqrt((xa-xb)*(xa-xb)+(ya-yb)*(ya-yb));
        } else {
            return MIN_DISTANCE;
        }
    }


    /** Calculate the euclidean distance between two points.
        The distance calculated here is not accurate. It is meant only
        to discriminate objects during the selection and therefore a
        inaccurate way to calculate distances is employed if the
        result is known to be greater than MIN_DISTANCE.

        @param xa the X coordinate of the first point.
        @param ya the Y coordinate of the first point.
        @param xb the X coordinate of the second point.
        @param yb the Y coordinate of the second point.
        @return the distance.
    */
    public static int pointToPoint(int xa, int ya,
                            int xb, int yb)
    {
        if(Math.abs(xa-xb) < MIN_DISTANCE || Math.abs(ya-yb) < MIN_DISTANCE) {
            return (int)Math.sqrt((xa-xb)*(xa-xb)+(ya-yb)*(ya-yb));
        } else {
            return MIN_DISTANCE;
        }
    }

    /** Calculate the euclidean distance between a point and a segment.
        Adapted from http://www.vb-helper.com/howto_distance_point_to_line.html

        The distance calculated here is not accurate. It is meant only
        to discriminate objects during the selection and therefore a
        inaccurate way to calculate distances is employed if the
        result is known to be greater than MIN_DISTANCE.

        @param xa the X coordinate of the starting point of the segment.
        @param ya the Y coordinate of the starting point of the segment.
        @param xb the X coordinate of the ending point of the segment.
        @param yb the Y coordinate of the ending point of the segment.
        @param x the X coordinate of the point.
        @param y the Y coordinate of the point.
        @return the calculated distance.
    */
    public static double pointToSegment(double xa, double ya,
                                 double xb, double yb,
                                 double x, double y)
    {
        // Shortcuts
        if(xa>xb) {
            xmin = xb; xmax = xa;
        } else {
            xmin = xa; xmax = xb;
        }

        if(x<xmin-MIN_DISTANCE || x>xmax+MIN_DISTANCE) {
            return MIN_DISTANCE;
        }

        if(ya>yb) {
            ymin = yb; ymax = ya;
        } else {
            ymin = ya; ymax = yb;
        }

        if(y<ymin-MIN_DISTANCE || y>ymax+MIN_DISTANCE) {
            return MIN_DISTANCE;
        }

        dx=xb-xa;
        dy=yb-ya;

        if (dx==0 && dy==0) {
            dx=x-xa;
            dy=y-yb;
            return Math.sqrt(dx*dx+dy*dy);
        }

        t=((x-xa)*dx+(y-ya)*dy)/(dx*dx+dy*dy);
        if (t<0.0) {
            dx=x-xa;
            dy=y-ya;
        } else if (t>1.0){
            dx=x-xb;
            dy=y-yb;
        } else {
            dx=x-(xa+t*dx);
            dy=y-(ya+t*dy);
        }
        return Math.sqrt(dx*dx+dy*dy);
    }

    /** Calculate the euclidean distance between a point and a segment.
        Adapted from http://www.vb-helper.com/howto_distance_point_to_line.html
        This is a version which does all calculations in fixed point with
        three digits and it should be faster than the double precision version
        on some platforms.

        @param xa the X coordinate of the starting point of the segment.
        @param ya the Y coordinate of the starting point of the segment.
        @param xb the X coordinate of the ending point of the segment.
        @param yb the Y coordinate of the ending point of the segment.
        @param x the X coordinate of the point.
        @param y the Y coordinate of the point.
        @return the calculated distance.
    */
    public static int pointToSegment(int xa, int ya,
                              int xb, int yb,
                              int x, int y)
    {
        // Shortcuts

        if(xa>xb) {
            ixmin = xb; ixmax = xa;
        } else {
            ixmin = xa; ixmax = xb;
        }
        if(x<ixmin-MIN_DISTANCE || x>ixmax+MIN_DISTANCE) {
            return MIN_DISTANCE;
        }
        if(ya>yb) {
            iymin = yb; iymax = ya;
        } else {
            iymin = ya; iymax = yb;
        }
        if(y<iymin-MIN_DISTANCE || y>iymax+MIN_DISTANCE) {
            return MIN_DISTANCE;
        }
        if (xb==xa && yb==ya) {
            idx=x-xa;
            idy=y-yb;
            return (int)Math.sqrt(idx*idx+idy*idy);
        }


        idx=xb-xa;
        idy=yb-ya;

        // This is an integer, fixed point implementation. We suppose to make
        // calculations with three decimals.

        it=1000*((x-xa)*idx+(y-ya)*idy)/(idx*idx+idy*idy);
        if (it<0) {
            idx=x-xa;
            idy=y-ya;
        } else if (it>1000){
            idx=x-xb;
            idy=y-yb;
        } else {
            idx=x-(xa+it*idx/1000); // NOPMD parentheses are useful here!
            idy=y-(ya+it*idy/1000); // NOPMD parentheses are useful here!
        }
        return (int)Math.sqrt(idx*idx+idy*idy);
    }

    /** Tells if a point lies inside a polygon, using the alternance rule
        adapted from a snippet by Randolph Franklin, in Paul Bourke pages:
        http://local.wasp.uwa.edu.au/~pbourke/geometry/insidepoly/

        @param xp vector of x coordinates of vertices
        @param yp vector of y coordinates of vertices
        @param npol number of vertices
        @param x x coordinate of the point
        @param y y coordinate of the point
        @return true if the point lies in the polygon, false otherwise.
    */
    public static boolean pointInPolygon(
            int[] xp, int[] yp,int npol, double x, double y)
    {
        c = false;

        for (i = 0,j = npol-1; i < npol; j=i++) {
            if ((yp[i] <= y && y < yp[j] ||
                 yp[j] <= y && y < yp[i]) &&
                x < (xp[j] - xp[i]) * (y - yp[i]) / (yp[j] - yp[i]) + xp[i])
            {
                c = !c;
            }
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
        dx = Math.abs(px-(ex+w/2.0));   // NOPMD
        dy = Math.abs(py-(ey+h/2.0));   // NOPMD


        //Shortcut
        if( dx > w/2.0 || dy > h/2.0) {
            return false;
        }

        // The multiplication by four is mandatory as the principal axis of an
        // ellipse are the half of the width and the height.

        return (4.0*dx*dx/w/w+4.0*dy*dy/h/h)<1.0;
    }

    /** Tells if a point lies inside an ellipse (integer version).

        @param ex x coordinate of the top left corner of the ellipse.
        @param ey y coordinate of the top left corner of the ellipse.
        @param w width of the ellipse.
        @param h height of the ellipse.
        @param px x coordinate of the point.
        @param py y coordinate of the point.
        @return true if the point lies in the ellipse, false otherwise.
    */
    public static boolean pointInEllipse(int ex,int ey,int w,
                                  int h, int px,int py)
    {
        return pointInEllipse((double) ex,(double) ey,(double) w,
                              (double) h,(double) px,(double) py);

/*      // On my iMac G5 the integer code is SLOWER than the double precision
        // calculations.

        //Determine and normalize quadrant.
        idx = Math.abs(px-(ex+w/2));
        idy = Math.abs(py-(ey+h/2));


        //Shortcut
        if( idx > w/2 || idy > h/2 ) {
            return false;
         }

        // Calculate the semi-latus rectum of the ellipse at the given point
        // The multiplication by four is mandatory as the principal axis of an
        // ellipse are the half of the width and the height.
        // Integer calculation with three digit accuracy.

        return (4000*idx*idx/w/w+4000*dy*dy/h/h)<1000;
*/
    }

    /** Give the distance between the given point and the ellipse path. The
        difference with pointInEllipse is that pointInEllipse gives 0 when
        the point is inside the ellipse, where here we get the distance
        with the contour of the ellipse.

        @param ex x coordinate of the top left corner of the ellipse.
        @param ey y coordinate of the top left corner of the ellipse.
        @param w width of the ellipse.
        @param h height of the ellipse.
        @param px x coordinate of the point.
        @param py y coordinate of the point.
        @return the distance to the contour of the ellipse.
    */
    public static double pointToEllipse(double ex,double ey,double w,
                                  double h,double px,double py)
    {
        // Calculate distance of the point from center of ellipse.
        double dx = Math.abs(px-(ex+w/2.0)); // NOPMD parentheses are useful!
        double dy = Math.abs(py-(ey+h/2.0)); // NOPMD parentheses are useful!

        // Treat separately the degenerate cases. This will avoid a divide
        // by zero anomalous situation.

        if (w==0) {
            return pointToSegment(ex, ey, ex, ey+h, px, py);
        }

        if (h==0) {
            return pointToSegment(ex, ey, ex+w, ey, px, py);
        }

        // Calculate the semi-latus rectum of the ellipse at the given point
        double l=(dx*dx/w/w+dy*dy/h/h)*4.0;

        return Math.abs(l-1.0)*Math.min(w,h)/4.0;
    }

    /** Give the distance between the given point and the ellipse path
        (integer version).

        @param ex x coordinate of the top left corner of the ellipse.
        @param ey y coordinate of the top left corner of the ellipse.
        @param w width of the ellipse.
        @param h height of the ellipse.
        @param px x coordinate of the point.
        @param py y coordinate of the point.
        @return the distance to the contour of the ellipse.
    */
    public static int pointToEllipse(int ex,int ey,int w,
                                  int h, int px,int py)
    {
        return (int)Math.round(pointToEllipse((double) ex,(double)ey,(double) w,
                              (double) h,(double) px,(double) py));
    }



    /** Tells if a point lies inside a rectangle.

        @param ex x coordinate of the top left corner of the rectangle.
        @param ey y coordinate of the top left corner of the rectangle.
        @param w width of the rectangle.
        @param h height of the rectangle.
        @param px x coordinate of the point.
        @param py y coordinate of the point.
        @return true if the point lies in the ellipse, false otherwise.
    */
    public static boolean pointInRectangle(double ex,double ey,double w,
                                  double h,double px,double py)
    {
        return !(ex>px||px>ex+w || ey>py || py>ey+h);
    }

    /** Tells if a point lies inside a rectangle, integer version.

        @param ex x coordinate of the top left corner of the rectangle.
        @param ey y coordinate of the top left corner of the rectangle.
        @param w width of the rectangle.
        @param h height of the rectangle.
        @param px x coordinate of the point.
        @param py y coordinate of the point.
        @return true if the point lies in the ellipse, false otherwise.
    */
    public static boolean pointInRectangle(int ex,int ey,int w,
                                  int h, int px,int py)
    {
        return !(ex>px || px>ex+w || ey>py || py>ey+h);
    }


    /** Give the distance between the given point and the borders of a
        rectangle.

        @param ex x coordinate of the top left corner of the rectangle.
        @param ey y coordinate of the top left corner of the rectangle.
        @param w width of the rectangle.
        @param h height of the rectangle.
        @param px x coordinate of the point.
        @param py y coordinate of the point.
        @return the distance to one of the border of the rectangle.
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
        rectangle (integer version).

        @param ex x coordinate of the top left corner of the rectangle.
        @param ey y coordinate of the top left corner of the rectangle.
        @param w width of the rectangle.
        @param h height of the rectangle.
        @param px x coordinate of the point.
        @param py y coordinate of the point.
        @return the distance to one of the border of the rectangle.
    */
    public static int pointToRectangle(int ex,int ey,int w,
                                  int h, int px,int py)
    {
        int d1=pointToSegment(ex,ey,ex+w,ey,px,py);
        int d2=pointToSegment(ex+w,ey,ex+w,ey+h,px,py);
        int d3=pointToSegment(ex+w,ey+h,ex,ey+h,px,py);
        int d4=pointToSegment(ex,ey+h,ex,ey,px,py);

        return Math.min(Math.min(d1,d2),Math.min(d3,d4));
    }

    /** Give an approximation of the distance between a point and
        a Bézier curve. The curve is divided into MAX_BEZIER_SEGMENTS
        linear pieces and the distance is calculated with each piece.
        The given distance is the minimum distance found for all pieces.
        Freely inspired from the original FidoCAD code.

        @param x1 x coordinate of the first control point of the Bézier curve.
        @param y1 y coordinate of the first control point of the Bézier curve.
        @param x2 x coordinate of the second control point of the Bézier curve.
        @param y2 y coordinate of the second control point of the Bézier curve.
        @param x3 x coordinate of the third control point of the Bézier curve.
        @param y3 y coordinate of the third control point of the Bézier curve.
        @param x4 x coordinate of the fourth control point of the Bézier curve.
        @param y4 y coordinate of the fourth control point of the Bézier curve.
        @param px x coordinate of the point.
        @param py y coordinate of the point.

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

        double b03;
        double b13;
        double b23;
        double b33;
        double umu;
        double u;

        int[] x=new int[MAX_BEZIER_SEGMENTS+1];
        int[] y=new int[MAX_BEZIER_SEGMENTS+1];

        for(i=0; i<=MAX_BEZIER_SEGMENTS; ++i) {
            u=(double)i/MAX_BEZIER_SEGMENTS;
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
        }

        // Calculate the distance of the given point with each of the
        // obtained segments.
        for(j=0;j<MAX_BEZIER_SEGMENTS;++j) {
            distance=Math.min(distance, pointToSegment(x[j], y[j],
                                                x[j+1], y[j+1],px, py));
        }
        return distance;
    }
}
