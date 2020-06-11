package net.sourceforge.fidocadj.graphic.nil;

import android.graphics.*;
import java.util.Vector;

import net.sourceforge.fidocadj.graphic.*;


/**     ANDROID VERSION

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
    along with FidoCadJ.  If not, see <http://www.gnu.org/licenses/>.

    Copyright 2014 by Davide Bucci
</pre>
*/
public class PolygonNull implements PolygonInterface
{

    private Path path;
    private int npoints;

    Vector<Integer> xpoints;
    Vector<Integer> ypoints;

    /** Get a Path element containing the polygon.
        @return the Path object.
    */
    public Path getPath()
    {
        return path;
    }

    /** Close the current polygon.
    */
    public void close()
    {
        path.close();
    }

    /** Constructor
    */
    public PolygonNull()
    {
        path=new Path();
        npoints=0;
        xpoints = new Vector<Integer>();
        ypoints = new Vector<Integer>();
    }

    /** Add a point to the current polygon.
        @param x the x coordinate of the point.
        @param y the y coordinate of the point.
    */
    public void addPoint(int x, int y)
    {
        if(npoints++==0)
            path.moveTo(x, y);
        else
            path.lineTo(x, y);

        xpoints.add(x);
        ypoints.add(y);
    }

    /** Reset the current polygon by deleting all the points.
    */
    public void reset()
    {
        path.reset();
        npoints=0;
        xpoints.clear();
        ypoints.clear();
    }

    /** Get the current number of points in the polygon.
        @return the number of points.
    */
    public int getNpoints()
    {
        return npoints;
    }

    /** Get a vector containing the x coordinates of the points.
        @return a vector containing the x coordinates of all points.
    */
    public int[] getXpoints()
    {
        //  ☠ Something better??? ☠
        int[] xvector= new int[npoints];
        int k=0;
        for(Integer v : xpoints)
            xvector[k++]=v;

        return xvector;
    }

    /** Get a vector containing the y coordinates of the points.
        @return a vector containing the y coordinates of all points.
    */
    public int[] getYpoints()
    {
        //  ☠ Something better??? ☠
        int[] yvector= new int[npoints];
        int k=0;
        for(Integer v : ypoints)
            yvector[k++]=v;

        return yvector;
    }

    /** Check if a given point is contained inside the polygon.
        @param x the x coordinate of the point to be checked.
        @param y the y coordinate of the point to be checked.
        @return true of the point is internal to the polygon, false otherwise.
    */
    public boolean contains(int x, int y)
    {
        RectF rectF = new RectF();
        path.computeBounds(rectF, true);
        Region r = new Region();
        r.setPath(path, new Region((int) rectF.left,
            (int) rectF.top, (int) rectF.right,
            (int) rectF.bottom));
        return r.contains(x,y);
    }
}