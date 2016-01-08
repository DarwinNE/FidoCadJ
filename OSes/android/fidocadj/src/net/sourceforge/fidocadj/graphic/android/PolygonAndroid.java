package net.sourceforge.fidocadj.graphic.android;

import java.util.Vector;

import android.graphics.*;

import net.sourceforge.fidocadj.graphic.*;


/** PolygonInterface implementation for Android.

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
public class PolygonAndroid implements PolygonInterface
{
    private Path path;
    private int npoints;

    Vector<Integer> xpoints;
    Vector<Integer> ypoints;
    
    public Path getPath()
    {
        return path;
    }
    
    public void close()
    {
        path.close();
    }
    
    public PolygonAndroid()
    {
        path=new Path();
        npoints=0;
        xpoints = new Vector<Integer>();
        ypoints = new Vector<Integer>();
    }

    public void addPoint(int x, int y)
    {
        if(npoints++==0)
            path.moveTo(x, y);
        else
            path.lineTo(x, y);
            
        xpoints.add(x);
        ypoints.add(y);
    }

    public void reset()
    {
        path.reset();
        npoints=0;
        xpoints.clear();
        ypoints.clear();
    }
    public int getNpoints()
    {
        return npoints;
    }
    
    public int[] getXpoints()
    {
        //  ☠ Something better??? ☠
        int[] xvector= new int[npoints];
        int k=0;
        for(Integer v : xpoints)
            xvector[k++]=v;
            
        return xvector;
    }
    
    public int[] getYpoints()
    {
        //  ☠ Something better??? ☠
        int[] yvector= new int[npoints];
        int k=0;
        for(Integer v : ypoints)
            yvector[k++]=v;
            
        return yvector; 
    }
    
    /** Checks whether the given point lies inside of the polygon
        @param x the x coordinate of the point
        @param y the y coordinate of the point
        @return true if the point is inside the polygon
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