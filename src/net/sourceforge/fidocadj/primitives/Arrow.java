package net.sourceforge.fidocadj.primitives;

import java.io.*;
import java.util.*;

import net.sourceforge.fidocadj.graphic.*;

/**
    Arrow class: draws an arrow of the given size, style and direction.
    This is a static class.

    @author Davide Bucci

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

    Copyright 2009-2015 by Davide Bucci
   </pre>

*/

public final class Arrow
{

    /** A few constants in order to define the arrow style.
    */
    public static final int flagLimiter = 0x01;
    public static final int flagEmpty = 0x02;

    /** Constructor is private since this is an utility class.
    */
    private Arrow()
    {
    }

    /** Draw an arrow at the given position.
        @param g the graphic context to be used
        @param x the x coordinate of the arrow point
        @param y the y coordinate of the arrow point
        @param xc the x coordinate of the direction point
        @param yc the y coordinate of the direction point
        @param l the length of the arrow
        @param h the half width of the arrow
        @param style the arrow style
        @return the coordinate of the base point of the arrow head
    */
    public static PointG drawArrow(GraphicsInterface g, int x, int y, int xc,
        int yc, int l, int h, int style)
    {
        double s;
        double alpha;
        double x0;
        double y0;
        double x1;
        double y1;
        double x2;
        double y2;

        // At first we need the angle giving the direction of the arrow
        // a little bit of trigonometry :-)
        // The idea is that the arrow head should be oriented in the direction
        // specified by the second point.

        if (x==xc)
            alpha = Math.PI/2.0+(y-yc<0.0?0.0:Math.PI);
        else
            alpha = Math.atan((double)(y-yc)/(double)(x-xc));

        // Alpha is the angle of the arrow, against an horizontal line with
        // the trigonometric convention (anti clockwise is positive).

        alpha += x-xc>0.0?0.0:Math.PI;

        // Then, we calculate the points for the polygon
        double cosalpha=Math.cos(alpha);
        double sinalpha=Math.sin(alpha);

        x0 = x - l*cosalpha;
        y0 = y - l*sinalpha;

        x1 = x0 - h*sinalpha;
        y1 = y0 + h*cosalpha;

        x2 = x0 + h*sinalpha;
        y2 = y0 - h*cosalpha;

        // The arrow head is traced using a polygon. Here we create the
        // object and populate it with the calculated coordinates.
        PolygonInterface p = g.createPolygon();

        p.addPoint((int)(x+0.5),(int)(y+0.5));
        p.addPoint((int)(x1+0.5),(int)(y1+0.5));
        p.addPoint((int)(x2+0.5),(int)(y2+0.5));


        if ((style & flagEmpty) == 0)
            g.fillPolygon(p);
        else
            g.drawPolygon(p);

        // Check if we need to draw the limiter or not
        // This is a small line useful for quotes.
        if ((style & flagLimiter) != 0) {
            double x3;
            double y3;
            double x4;
            double y4;
            x3 = x - h*sinalpha;
            y3 = y + h*cosalpha;

            x4 = x + h*sinalpha;
            y4 = y - h*cosalpha;
            g.drawLine((int)x3,(int)y3,(int)x4,(int)y4);
        }
        return new PointG((int)(x0),(int)(y0));
    }
}