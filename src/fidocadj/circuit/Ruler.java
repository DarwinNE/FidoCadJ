package fidocadj.circuit;

import java.awt.*;

import fidocadj.geom.MapCoordinates;
import fidocadj.globals.Globals;

/** Draw a ruler.

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

    Copyright 2015-2023 by Davide Bucci
    </pre>

    @author Davide Bucci
*/

public class Ruler
{
    // Font to be used to draw the ruler.
    private static final String rulerFont = "Lucida Sans Regular";

    // Color of elements during editing.
    private final Color rulerColor;
    private final Color textColor;

    // Begin and end coordinates.
    private int rulerStartX;
    private int rulerStartY;
    private int rulerEndX;
    private int rulerEndY;

    // Is the ruler active?
    private boolean isActiveRuler;

    /** Constructor, specify the colors to be employed.
        @param rc the color of ruler elements.
        @param tc the color of text elements.
    */
    public Ruler(Color rc, Color tc)
    {
        rulerColor=rc;
        textColor=tc;
        isActiveRuler=false;
    }

    /** Set wether the ruler should be drawn or not.
        @param s true if the ruler should be drawn.
    */
    public void setActive(boolean s)
    {
        isActiveRuler=s;
    }

    /** Gets the current status of the ruler.
        @return true if the ruler should be drawn, false if not.
    */
    public boolean isActive()
    {
        return isActiveRuler;
    }

    /** Draws a ruler to ease measuring distances.
        @param g the graphic context.
        @param cs the coordinate mapping.
    */
    public void drawRuler(Graphics g, MapCoordinates cs)
    {
        if (!isActiveRuler) {
            return;
        }

        int sx=rulerStartX;
        int sy=rulerStartY;
        int ex=rulerEndX;
        int ey=rulerEndY;

        double length;
        //MapCoordinates cs=dmp.getMapCoordinates();

        int xa = cs.unmapXnosnap(sx);
        int ya = cs.unmapYnosnap(sy);

        int xb = cs.unmapXnosnap(ex);
        int yb = cs.unmapYnosnap(ey);

        int x1;
        int y1;
        int x2;
        int y2;
        double x;
        double y;

        // Calculates the ruler length.
        length = Math.sqrt((double)(xa-xb)*(xa-xb)+(ya-yb)*(ya-yb));

        g.drawLine(sx, sy, ex, ey);

        // A little bit of trigonometry :-)

        double alpha;
        if (sx==ex) {
            alpha = Math.PI/2.0+(ey-sy<0?0:Math.PI);
        } else {
            alpha = Math.atan((double)(ey-sy)/(double)(ex-sx));
        }

        alpha += ex-sx>0?0:Math.PI;

        // Those magic numers are the lenghts of the tics (major and minor)
        double l = 5.0;

        if (cs.getXMagnitude()<1.0) {
            l=10;
        } else if(cs.getXMagnitude() > 5.0) {
            l=1;
        } else {
            l=5;
        }

        double ll = 0.0;
        double ld = 5.0;
        int m = 5;
        int j = 0;

        double dex = sx + length*Math.cos(alpha)*cs.getXMagnitude();
        double dey = sy + length*Math.sin(alpha)*cs.getYMagnitude();

        alpha += Math.PI/2.0;

        boolean debut=true;

        // Draw the ticks.
        for(double i=0; i<=length; i+=l) {
            if (j==m || debut) {
                j=0;
                ll=2*ld;
                debut=false;
            } else {
                ll=ld;
            }
            ++j;
            x = dex*i/length+(double)sx*(length-i)/length;
            y = dey*i/length+(double)sy*(length-i)/length;

            x1 = (int)(x - ll*Math.cos(alpha));
            x2 = (int)(x + ll*Math.cos(alpha));
            y1 = (int)(y - ll*Math.sin(alpha));
            y2 = (int)(y + ll*Math.sin(alpha));

            g.drawLine(x1, y1, x2, y2);
        }

        Font f=new Font(rulerFont,Font.PLAIN,10);
        g.setFont(f);

        String t1 = Globals.roundTo(length,2);

        // Remember that one FidoCadJ logical unit is 127 microns.
        String t2 = Globals.roundTo(length*.127,2)+" mm";

        FontMetrics fm = g.getFontMetrics(f);

        // Draw the box at the end, with the measurement results.
        g.setColor(Color.white);
        g.fillRect(ex+10, ey, Math.max(fm.stringWidth(t1),
            fm.stringWidth(t2))+1, 24);

        g.setColor(rulerColor);
        g.drawRect(ex+9, ey-1, Math.max(fm.stringWidth(t1),
            fm.stringWidth(t2))+2, 25);
        g.setColor(textColor);
        g.drawString(t1, ex+10, ey+10);
        g.drawString(t2, ex+10, ey+20);
    }

    /** Define the coordinates of the starting point of the ruler.
        @param sx the x coordinate.
        @param sy the y coordinate.
    */
    public void setRulerStart(int sx, int sy)
    {
        rulerStartX=sx;
        rulerStartY=sy;
    }

    /** Define the coordinates of the ending point of the ruler.
        @param sx the x coordinate.
        @param sy the y coordinate.
    */
    public void setRulerEnd(int sx, int sy)
    {
        rulerEndX=sx;
        rulerEndY=sy;
    }

    /** Get the x coordinate of the starting point of the ruler.
        @return the x coordinate.
    */
    public int getRulerStartX()
    {
        return rulerStartX;
    }

    /** Get the y coordinate of the starting point of the ruler.
        @return the y coordinate.
    */
    public int getRulerStartY()
    {
        return rulerStartY;
    }
}