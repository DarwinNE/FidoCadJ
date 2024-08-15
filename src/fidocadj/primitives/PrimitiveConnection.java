package fidocadj.primitives;

import java.io.*;
import java.util.*;

import fidocadj.export.ExportInterface;
import fidocadj.geom.MapCoordinates;
import fidocadj.geom.GeometricDistances;
import fidocadj.globals.Globals;
import fidocadj.graphic.GraphicsInterface;

/** Class to handle the Connection primitive.

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

    Copyright 2007-2023 by Davide Bucci
    </pre>

    @author Davide Bucci
*/
public final class PrimitiveConnection
    extends GraphicPrimitive
{
    // A connection is defined by one points.
    // We take into account the optional Name and Value text tags.
    static final int N_POINTS=3;

    // Those are data which are kept for the fast redraw of this primitive.
    // Basically, they are calculated once and then used as much as possible
    // without having to calculate everything from scratch.
    private int x1;         // NOPMD
    private int y1;         // NOPMD
    private int xa1;        // NOPMD
    private int ya1;        // NOPMD
    private int ni;         // NOPMD
    private double nn;      // NOPMD
    private float w;        // NOPMD

    /** Gets the number of control points used.
        @return the number of points used by the primitive
    */
    public int getControlPointNumber()
    {
        return N_POINTS;
    }

    /** Constructor.
        @param f the name of the font for attached text.
        @param size the size of the font for attached text.
    */
    public PrimitiveConnection(String f, int size)
    {
        super();
        initPrimitive(-1, f, size);
    }

    /** Create a connection in the given point.
        @param x the x coordinate (logical unit) of the connection.
        @param y the y coordinate (logical unit) of the connection.
        @param layer the layer to be used.
        @param f the name of the font for attached text.
        @param size the size of the font for attached text.
    */
    public PrimitiveConnection(int x, int y, int layer, String f, int size)
    {
        super();

        initPrimitive(-1, f, size);

        virtualPoint[0].x=x;
        virtualPoint[0].y=y;

        virtualPoint[getNameVirtualPointNumber()].x=x+5;
        virtualPoint[getNameVirtualPointNumber()].y=y+5;
        virtualPoint[getValueVirtualPointNumber()].x=x+5;
        virtualPoint[getValueVirtualPointNumber()].y=y+10;

        setLayer(layer);
    }

    /** Draw the graphic primitive on the given graphic context.
        @param g the graphic context in which the primitive should be drawn.
        @param coordSys the graphic coordinates system to be applied.
        @param layerV the layer description.
    */
    public void draw(GraphicsInterface g, MapCoordinates coordSys,
        List layerV)
    {
        if(!selectLayer(g,layerV)) {
            return;
        }

        drawText(g, coordSys, layerV, -1);

        if (changed) {
            changed=false;
            /* in the Connection primitive, the virtual point represents
            the position of the center of the circle to be drawn. */
            x1=virtualPoint[0].x;
            y1=virtualPoint[0].y;

            nn=Math.abs(coordSys.mapXr(0,0)-
                coordSys.mapXr(10,10))*Globals.diameterConnection/10.0;


            // a little boost for small zooms :-)
            if (nn<2.0) {
                nn=(int)(Math.abs(coordSys.mapXr(0,0)-
                    coordSys.mapXr(20,20))*Globals.diameterConnection/12);
            }

            xa1=(int)Math.round(coordSys.mapX(x1,y1)-nn/2.0);
            ya1=(int)Math.round(coordSys.mapY(x1,y1)-nn/2.0);

            ni=(int)Math.round(nn);
            // Make sure that something is drawn even for very small
            // connections
            if(ni==0) {
                ni=1;
            }

            w = (float)(Globals.lineWidth*coordSys.getXMagnitude());
            if (w<D_MIN) { w=D_MIN; }
        }

        if(!g.hitClip(xa1, ya1, ni, ni)) {
            return;
        }

        g.applyStroke(w, 0);

        // When the circle is very small, it is better to set a single pixel
        // than trying to fill the oval.
        if(ni>1) {
            g.fillOval(xa1, ya1, ni, ni);
        } else {
            g.fillRect(xa1, ya1, ni, ni);
        }
    }

    /** Parse a token array and store the graphic data for a given primitive
        Obviously, that routine should be called *after* having recognized
        that the called primitive is correct.
        That routine also sets the current layer.
        @param tokens the tokens to be processed. tokens[0] should be the
        command of the actual primitive.
        @param nn the number of tokens present in the array
        @throws IOException if the arguments are incorrect or the primitive
            is invalid.
    */
    public void parseTokens(String[] tokens, int nn)
        throws IOException
    {
        changed=true;

        if ("SA".equals(tokens[0])) {   // Connection
            if (nn<3)  {
                throw new IOException("Bad arguments on SA");
            }
            // Load the points in the virtual points associated to the
            // current primitive.

            int x1 = virtualPoint[0].x=Integer.parseInt(tokens[1]);
            int y1 = virtualPoint[0].y=Integer.parseInt(tokens[2]);
            virtualPoint[getNameVirtualPointNumber()].x=x1+5;
            virtualPoint[getNameVirtualPointNumber()].y=y1+5;
            virtualPoint[getValueVirtualPointNumber()].x=x1+5;
            virtualPoint[getValueVirtualPointNumber()].y=y1+10;
            if(nn>3) { parseLayer(tokens[3]); }


        } else {
            throw new IOException("Invalid primitive:"+
                                          " programming error?");
        }
    }

    /** Gets the distance (in primitive's coordinates space) between a
        given point and the primitive.
        When it is reasonable, the behaviour can be binary (polygons,
        ovals...). In other cases (lines, points), it can be proportional.
        @param px the x coordinate of the given point.
        @param py the y coordinate of the given point.
        @return the distance in logical units.
    */
    public int getDistanceToPoint(int px, int py)
    {
        // Here we check if the given point lies inside the text areas

        if(checkText(px, py)) {
            return 0;
        }

        // If not, we check for the distance with the connection center.
        return GeometricDistances.pointToPoint(
                virtualPoint[0].x,virtualPoint[0].y,
                px,py)-1;
    }

    /** Obtain a string command descripion of the primitive.
        @param extensions true if FidoCadJ extensions to the old FidoCAD format
            should be active.
        @return the FIDOCAD command line.
    */
    public String toString(boolean extensions)
    {
        StringBuffer s=new StringBuffer(100);
        s.append("SA ");
        s.append(virtualPoint[0].x);
        s.append(" ");
        s.append(virtualPoint[0].y);
        s.append(" ");
        s.append(getLayer());
        s.append("\n");

        s.append(saveText(extensions));

        return s.toString();
    }

    /** Export the primitive on a vector graphic format.
        @param exp the export interface to employ.
        @param cs the coordinate mapping to employ.
        @throws IOException if a problem occurs, such as it is impossible to
            write on the output file.
    */
    public void export(ExportInterface exp, MapCoordinates cs)
        throws IOException
    {
        exportText(exp, cs, -1);
        exp.exportConnection(cs.mapX(virtualPoint[0].x,virtualPoint[0].y),
                       cs.mapY(virtualPoint[0].x,virtualPoint[0].y), getLayer(),
                       Globals.diameterConnection*cs.getXMagnitude());
    }

    /** Get the number of the virtual point associated to the Name property
        @return the number of the virtual point associated to the Name property
    */
    public int getNameVirtualPointNumber()
    {
        return 1;
    }

    /** Get the number of the virtual point associated to the Value property
        @return the number of the virtual point associated to the Value property
    */
    public  int getValueVirtualPointNumber()
    {
        return 2;
    }
}