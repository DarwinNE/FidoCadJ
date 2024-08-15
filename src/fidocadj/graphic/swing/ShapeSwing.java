package fidocadj.graphic.swing;

import java.awt.*;
import java.awt.geom.*;
import fidocadj.graphic.ShapeInterface;
import fidocadj.graphic.RectangleG;



/** ShapeSwing is a wrapper around the Shape Swing class.

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

    Copyright 2014-2023 by Davide Bucci
</pre>
*/
public class ShapeSwing implements ShapeInterface
{
    Shape s;

    /** Standard constructor.
    */
    public ShapeSwing()
    {
        s=null;
    }

    /** Create a cubic curve (Bézier).
        @param x0 the x coord. of the starting point of the Bézier curve.
        @param y0 the y coord. of the starting point of the Bézier curve.
        @param x1 the x coord. of the first handle.
        @param y1 the y coord. of the first handle.
        @param x2 the x coord. of the second handle.
        @param y2 the y coord. of the second handle.
        @param x3 the x coord. of the ending point of the Bézier curve.
        @param y3 the y coord. of the ending point of the Bézier curve.
    */
    public void createCubicCurve(int x0, int y0, int x1, int y1,
        int x2, int y2, int x3, int y3)
    {
        s = new CubicCurve2D.Float(x0, y0, x1, y1, x2, y2, x3, y3);
    }

    /** Create a general path with the given number of points.
        @param npoints the number of points.
    */
    public void createGeneralPath(int npoints)
    {
        s = new GeneralPath(GeneralPath.WIND_EVEN_ODD, npoints);
    }

    /** Obtain the bounding box of the curve.
        @return the bounding box.
    */
    public RectangleG getBounds()
    {
        Rectangle r=s.getBounds();
        return new RectangleG(r.x, r.y, r.width, r.height);
    }

    /** Move the current position to the given coordinates.
        @param x the x coordinate.
        @param y the y coordinate.
    */
    public void moveTo(float x, float y)
    {
        GeneralPath gp=(GeneralPath)s;
        gp.moveTo(x,y);
    }

    /** Add a cubic curve from the current point.
        @param x0 the x coord. of the first handle.
        @param y0 the y coord. of the first handle
        @param x1 the x coord. of the second handle.
        @param y1 the y coord. of the second handle.
        @param x2 the x coord. of the ending point of the Bézier curve.
        @param y2 the y coord. of the ending point of the Bézier curve.
    */
    public void curveTo(float x0, float y0, float x1, float y1,
        float x2, float y2)
    {
        GeneralPath gp=(GeneralPath)s;
        gp.curveTo(x0, y0,x1, y1,x2, y2);
    }

    /** Close the current path.
    */
    public void closePath()
    {
        GeneralPath gp=(GeneralPath)s;
        gp.closePath();
    }

    /** Get the Swing version of the shape.
        @return the Swing version of the shape.
    */
    public Shape getShapeInSwing()
    {
        return s;
    }




}