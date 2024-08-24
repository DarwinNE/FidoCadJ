package fidocadj.graphic.nil;

import java.awt.*;
import fidocadj.graphic.PolygonInterface;


/**     SWING VERSION


    PolygonInterface specifies methods for handling a polygon.
    TODO: reduce dependency on java.awt.*;

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
public class PolygonNull implements PolygonInterface
{

    private final Polygon p;

    /** Standard constructor.
    */
    public PolygonNull()
    {
        p=new Polygon();
    }

    /** Add a point to the current polygon.
        @param x the x coordinate of the point.
        @param y the y coordinate of the point.
    */
    public void addPoint(int x, int y)
    {
        p.addPoint(x,y);
    }

    /** Reset the current polygon by deleting all the points.
    */
    public void reset()
    {
        p.reset();
    }

    /** Get the current number of points in the polygon.
        @return the number of points.
    */
    public int getNpoints()
    {
        return p.npoints;
    }

    /** Get a vector containing the x coordinates of the points.
        @return a vector containing the x coordinates of all points.
    */
    public int[] getXpoints()
    {
        return p.xpoints;
    }

    /** Get a vector containing the y coordinates of the points.
        @return a vector containing the y coordinates of all points.
    */
    public int[] getYpoints()
    {
        return p.ypoints;
    }

    /** Check if a given point is contained inside the polygon.
        @param x the x coordinate of the point to be checked.
        @param y the y coordinate of the point to be checked.
        @return true of the point is internal to the polygon, false otherwise.
    */
    public boolean contains(int x, int y)
    {
        return p.contains(x,y);
    }
}