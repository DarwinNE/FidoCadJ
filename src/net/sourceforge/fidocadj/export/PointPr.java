package net.sourceforge.fidocadj.export;

/** A simple point featuring double-precision coordinates
*/
public class PointPr
{
    public double x;
    public double y;
    /** Standard constructor, yielding a (0,0) coordinate.
    */
    public PointPr()
    {
        x=0;y=0;
    }
    /** Constructor, yielding a generic coordinate.
        @param xx the x coordinate.
        @param yy the y coordinate.
    */
    public PointPr(double xx, double yy)
    {
        x=xx;y=yy;
    }
}