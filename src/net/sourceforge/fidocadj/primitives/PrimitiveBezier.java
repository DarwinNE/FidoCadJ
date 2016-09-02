package net.sourceforge.fidocadj.primitives;

import java.io.*;
import java.util.*;

import net.sourceforge.fidocadj.dialogs.*;
import net.sourceforge.fidocadj.export.*;
import net.sourceforge.fidocadj.geom.*;
import net.sourceforge.fidocadj.globals.*;
import net.sourceforge.fidocadj.graphic.*;

/** Class to handle the Bézier primitive.

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

    Copyright 2007-2016 by Davide Bucci
    </pre>

    @author Davide Bucci
*/
public final class PrimitiveBezier extends GraphicPrimitive
{

    // A Bézier is defined by four points.
    static final int N_POINTS=6;

    private int dashStyle;
    private final Arrow arrowData;

    // Those are data which are kept for the fast redraw of this primitive.
    // Basically, they are calculated once and then used as much as possible
    // without having to calculate everything from scratch.
    private ShapeInterface shape1;
    private float w;

    private int xmin, ymin;
    private int width, height;

    /** Gets the number of control points used.
        @return the number of points used by the primitive
    */
    public int getControlPointNumber()
    {
        return N_POINTS;
    }

    /** Standard constructor. It creates an empty shape.
        @param f the name of the font for attached text.
        @param size the size of the font for attached text.
    */
    public PrimitiveBezier(String f, int size)
    {
        super();
        arrowData=new Arrow();
        initPrimitive(-1, f, size);
    }

    /** Create a Bézier curve specified by four control points
        @param x1 the x coordinate (logical unit) of P1.
        @param y1 the y coordinate (logical unit) of P1.
        @param x2 the x coordinate (logical unit) of P2.
        @param y2 the y coordinate (logical unit) of P2.
        @param x3 the x coordinate (logical unit) of P3.
        @param y3 the y coordinate (logical unit) of P3.
        @param x4 the x coordinate (logical unit) of P4.
        @param y4 the y coordinate (logical unit) of P4.
        @param layer the layer to be used.
        @param arrowS arrow to be drawn at the beginning of the curve.
        @param arrowE arrow to be drawn at the beginning of the curve.
        @param arrowSt arrow style.
        @param arrowLe the arrow length.
        @param arrowWi the arrow half width.
        @param dashSt dash style.
        @param font the name of the font for attached text.
        @param size the size of the font for attached text.
    */
    public PrimitiveBezier(int x1, int y1, int x2, int y2,
                         int x3, int y3, int x4, int y4,
                            int layer, boolean arrowS, boolean arrowE,
                            int arrowSt, int arrowLe, int arrowWi, int dashSt,
                            String font, int size)
    {
        super();

        arrowData=new Arrow();
        arrowData.setArrowStart(arrowS);
        arrowData.setArrowEnd(arrowE);
        arrowData.setArrowHalfWidth(arrowWi);
        arrowData.setArrowLength(arrowLe);
        arrowData.setArrowStyle(arrowSt);
        dashStyle=dashSt;

        initPrimitive(-1, font, size);

        // Store the coordinates of the points
        virtualPoint[0].x=x1;
        virtualPoint[0].y=y1;
        virtualPoint[1].x=x2;
        virtualPoint[1].y=y2;
        virtualPoint[2].x=x3;
        virtualPoint[2].y=y3;
        virtualPoint[3].x=x4;
        virtualPoint[3].y=y4;

        virtualPoint[getNameVirtualPointNumber()].x=x1+5;
        virtualPoint[getNameVirtualPointNumber()].y=y1+5;
        virtualPoint[getValueVirtualPointNumber()].x=x1+5;
        virtualPoint[getValueVirtualPointNumber()].y=y1+10;
        setLayer(layer);
    }

    /** Get the control parameters of the given primitive.
        @return a vector of ParameterDescription containing each control
                parameter.
    */
    public Vector<ParameterDescription> getControls()
    {
        Vector<ParameterDescription> v=super.getControls();

        ParameterDescription pd = new ParameterDescription();

        pd = new ParameterDescription();
        pd.parameter=Boolean.valueOf(arrowData.isArrowStart());
        pd.description=Globals.messages.getString("ctrl_arrow_start");
        pd.isExtension = true;
        v.add(pd);
        pd = new ParameterDescription();
        pd.parameter=Boolean.valueOf(arrowData.isArrowEnd());
        pd.description=Globals.messages.getString("ctrl_arrow_end");
        pd.isExtension = true;
        v.add(pd);
        pd = new ParameterDescription();
        pd.parameter=Integer.valueOf(arrowData.getArrowLength());
        pd.description=Globals.messages.getString("ctrl_arrow_length");
        pd.isExtension = true;
        v.add(pd);
        pd = new ParameterDescription();
        pd.parameter=Integer.valueOf(arrowData.getArrowHalfWidth());
        pd.description=Globals.messages.getString("ctrl_arrow_half_width");
        pd.isExtension = true;
        v.add(pd);
        pd = new ParameterDescription();
        pd.parameter=new ArrowInfo(arrowData.getArrowStyle());
        pd.description=Globals.messages.getString("ctrl_arrow_style");
        pd.isExtension = true;
        v.add(pd);

        pd = new ParameterDescription();
        pd.parameter=new DashInfo(dashStyle);
        pd.description=Globals.messages.getString("ctrl_dash_style");
        pd.isExtension = true;
        v.add(pd);
        return v;
    }

    /** Set the control parameters of the given primitive.
        This method is specular to getControls().
        @param v a vector of ParameterDescription containing each control
                parameter.
                The first parameters should always be the virtual points.
        @return the next index in v to be scanned (if needed) after the
            execution of this function.
    */
    public int setControls(Vector<ParameterDescription> v)
    {
        int i=super.setControls(v);
        ParameterDescription pd;

        pd=(ParameterDescription)v.get(i++);
        if (pd.parameter instanceof Boolean)
            arrowData.setArrowStart(((Boolean)pd.parameter).booleanValue());
        else
            System.out.println("Warning: 1-unexpected parameter!"+pd);
        pd=(ParameterDescription)v.get(i++);
        if (pd.parameter instanceof Boolean)
             arrowData.setArrowEnd(((Boolean)pd.parameter).booleanValue());
        else
            System.out.println("Warning: 2-unexpected parameter!"+pd);

        pd=(ParameterDescription)v.get(i++);
        if (pd.parameter instanceof Integer)
            arrowData.setArrowLength(((Integer)pd.parameter).intValue());
        else
            System.out.println("Warning: 3-unexpected parameter!"+pd);

        pd=(ParameterDescription)v.get(i++);
        if (pd.parameter instanceof Integer)
            arrowData.setArrowHalfWidth(((Integer)pd.parameter).intValue());
        else
            System.out.println("Warning: 4-unexpected parameter!"+pd);

        pd=(ParameterDescription)v.get(i++);
        if (pd.parameter instanceof ArrowInfo)
            arrowData.setArrowStyle(((ArrowInfo)pd.parameter).style);
        else
            System.out.println("Warning: 5-unexpected parameter!"+pd);

        pd=(ParameterDescription)v.get(i++);
        if (pd.parameter instanceof DashInfo)
            dashStyle=((DashInfo)pd.parameter).style;
        else
            System.out.println("Warning: 6-unexpected parameter!"+pd);

        // Parameters validation and correction
        if(dashStyle>=Globals.dashNumber)
            dashStyle=Globals.dashNumber-1;
        if(dashStyle<0)
            dashStyle=0;

        return i;
    }

    /** Draw the graphic primitive on the given graphic context.
        @param g the graphic context in which the primitive should be drawn.
        @param coordSys the graphic coordinates system to be applied.
        @param layerV the layer description.
    */
    public void draw(GraphicsInterface g, MapCoordinates coordSys,
        Vector layerV)
    {

        if(!selectLayer(g,layerV))
            return;

        drawText(g, coordSys, layerV, -1);

        // in the Bézier primitive, the four virtual points represent
        // the control points of the shape.
        if (changed) {
            changed=false;

            shape1=g.createShape();
            // Create the Bézier curve
            shape1.createCubicCurve(
                coordSys.mapX(virtualPoint[0].x,virtualPoint[0].y),
                coordSys.mapY(virtualPoint[0].x,virtualPoint[0].y),
                coordSys.mapX(virtualPoint[1].x,virtualPoint[1].y),
                coordSys.mapY(virtualPoint[1].x,virtualPoint[1].y),
                coordSys.mapX(virtualPoint[2].x,virtualPoint[2].y),
                coordSys.mapY(virtualPoint[2].x,virtualPoint[2].y),
                coordSys.mapX(virtualPoint[3].x,virtualPoint[3].y),
                coordSys.mapY(virtualPoint[3].x,virtualPoint[3].y));

            int h=0;
            if(arrowData.atLeastOneArrow())
                h=arrowData.prepareCoordinateMapping(coordSys);

            // Calculating the bounds of this curve is useful since we can
            // check if it is visible and thus choose wether draw it or not.
            RectangleG r = shape1.getBounds();
            xmin = r.x-h;
            ymin = r.y-h;
            width  = r.width+2*h;
            height = r.height+2*h;

            // Calculating stroke width
            w = (float)(Globals.lineWidth*coordSys.getXMagnitude());
            if (w<D_MIN) w=D_MIN;
        }

        // If the curve is not visible, exit immediately
        if(!g.hitClip(xmin,ymin, width+1, height+1))
            return;

        // Apply the stroke style
        g.applyStroke(w, dashStyle);

        if(width==0 ||height==0) {
            // Degenerate case: horizontal or vertical segment.
            g.drawLine(coordSys.mapX(virtualPoint[0].x,virtualPoint[0].y),
                coordSys.mapY(virtualPoint[0].x,virtualPoint[0].y),
                coordSys.mapX(virtualPoint[3].x,virtualPoint[3].y),
                coordSys.mapY(virtualPoint[3].x,virtualPoint[3].y));
        } else {
            // Draw the curve.
            g.draw(shape1);
        }

        // Check if there are arrows to be drawn and eventually draw them.
        if (arrowData.atLeastOneArrow()) {
            if (arrowData.isArrowStart())
                drawArrow(g, coordSys, 0,1,2,3);

            if (arrowData.isArrowEnd())
                drawArrow(g, coordSys, 3,2,1,0);
        }
    }

    /** Draw an arrow checking that the coordinates given are not degenerate.
        @param g the graphical context on which to write.
        @param coordSyst the coordinate system.
        @param A the index of the first point (the head of the arrow)
        @param B the index of the second arrow (indicates the direction, if
            the coordinates are diffrent from point A. If it is not true, the
            coordinates of point C are used.
        @param C if the test of point B fails, employs this point to indicate
            the direction, unless equal to point A.
        @param D employs this point as a last resort!
    */
    private void drawArrow(GraphicsInterface g, MapCoordinates coordSys,
        int A, int B, int C, int D)
    {
        int psx, psy; // starting coordinates.
        int pex, pey; // ending coordinates.

        // We must check if the cubic curve is degenerate. In this case,
        // the correct arrow orientation will be determined by successive
        // points in the curve.
        psx = virtualPoint[A].x;
        psy = virtualPoint[A].y;

        if(virtualPoint[A].x!=virtualPoint[B].x ||
            virtualPoint[A].y!=virtualPoint[B].y)
        {
            pex = virtualPoint[B].x;
            pey = virtualPoint[B].y;
        } else if(virtualPoint[A].x!=virtualPoint[C].x ||
            virtualPoint[A].y!=virtualPoint[C].y)
        {
            pex = virtualPoint[C].x;
            pey = virtualPoint[C].y;
        } else {
            pex = virtualPoint[D].x;
            pey = virtualPoint[D].y;
        }

        arrowData.drawArrow(g,
            coordSys.mapX(psx,psy),
            coordSys.mapY(psx,psy),
            coordSys.mapX(pex,pey),
            coordSys.mapY(pex,pey));
    }

    /** Parse a token array and store the graphic data for a given primitive
        Obviously, that routine should be called *after* having recognized
        that the called primitive is correct.
        That routine also sets the current layer.
        @param tokens the tokens to be processed. tokens[0] should be the
        command of the actual primitive.
        @param N the number of tokens present in the array.
        @throws IOException if the arguments are incorrect or the primitive
            is invalid.
    */
    public void parseTokens(String[] tokens, int N)
        throws IOException
    {
        changed=true;

        // assert it is the correct primitive
        if (tokens[0].equals("BE")) {   // Bézier
            if (N<9) {
                IOException E=new IOException("bad arguments on BE");
                throw E;
            }
            // Parse the coordinates of all points of the Bézier curve
            int x1 = virtualPoint[0].x=Integer.parseInt(tokens[1]);
            int y1 = virtualPoint[0].y=Integer.parseInt(tokens[2]);
            virtualPoint[1].x=Integer.parseInt(tokens[3]);
            virtualPoint[1].y=Integer.parseInt(tokens[4]);
            virtualPoint[2].x=Integer.parseInt(tokens[5]);
            virtualPoint[2].y=Integer.parseInt(tokens[6]);
            virtualPoint[3].x=Integer.parseInt(tokens[7]);
            virtualPoint[3].y=Integer.parseInt(tokens[8]);
            virtualPoint[getNameVirtualPointNumber()].x=x1+5;
            virtualPoint[getNameVirtualPointNumber()].y=y1+5;
            virtualPoint[getValueVirtualPointNumber()].x=x1+5;
            virtualPoint[getValueVirtualPointNumber()].y=y1+10;
            if(N>9) parseLayer(tokens[9]);

            if(N>10 && tokens[10].equals("FCJ")) {
                int i=arrowData.parseTokens(tokens, 11);
                dashStyle = checkDashStyle(Integer.parseInt(tokens[i]));
            }
        } else {
            IOException E=new IOException("Invalid primitive: "+
                                          " programming error?");
            throw E;
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
        // Here, we check if the given point lies inside the text areas.
        if(checkText(px, py))
            return 0;

        // If not, we check for the distance to the Bézier curve.
        return GeometricDistances.pointToBezier(
                virtualPoint[0].x, virtualPoint[0].y,
                virtualPoint[1].x, virtualPoint[1].y,
                virtualPoint[2].x, virtualPoint[2].y,
                virtualPoint[3].x, virtualPoint[3].y,
                px,  py);
    }

    /** Obtain a string command descripion of the primitive.
        @param extensions true if FidoCadJ extensions to the old FidoCAD format
            should be active.
        @return the FIDOCAD command line.
    */
    public String toString(boolean extensions)
    {
        String cmd;

        String s = "BE "+virtualPoint[0].x+" "+virtualPoint[0].y+" "+
            +virtualPoint[1].x+" "+virtualPoint[1].y+" "+
            +virtualPoint[2].x+" "+virtualPoint[2].y+" "+
            +virtualPoint[3].x+" "+virtualPoint[3].y+" "+
            getLayer()+"\n";

        if(extensions && (arrowData.atLeastOneArrow()|| dashStyle>0 ||
                hasName() || hasValue()))
        {
            String text = "0";
            // We take into account that there may be some text associated
            // to that primitive.
            if (name.length()!=0 || value.length()!=0)
                text = "1";
            s+="FCJ "+arrowData.createArrowTokens()+" "+dashStyle+
                " "+text+"\n";
        }
        // The false is needed since saveText should not write the FCJ tag.
        s+=saveText(false);

        return s;
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
        exp.exportBezier(cs.mapX(virtualPoint[0].x,virtualPoint[0].y),
                       cs.mapY(virtualPoint[0].x,virtualPoint[0].y),
                       cs.mapX(virtualPoint[1].x,virtualPoint[1].y),
                       cs.mapY(virtualPoint[1].x,virtualPoint[1].y),
                       cs.mapX(virtualPoint[2].x,virtualPoint[2].y),
                       cs.mapY(virtualPoint[2].x,virtualPoint[2].y),
                       cs.mapX(virtualPoint[3].x,virtualPoint[3].y),
                       cs.mapY(virtualPoint[3].x,virtualPoint[3].y),
                       getLayer(),
                       arrowData.isArrowStart(), arrowData.isArrowEnd(),
                       arrowData.getArrowStyle(),
                       (int)(arrowData.getArrowLength()*cs.getXMagnitude()),
                       (int)(arrowData.getArrowHalfWidth()*cs.getXMagnitude()),
                       dashStyle,Globals.lineWidth*cs.getXMagnitude());
    }

    /** Get the number of the virtual point associated to the Name property
        @return the number of the virtual point associated to the Name property
    */
    public int getNameVirtualPointNumber()
    {
        return 4;
    }

    /** Get the number of the virtual point associated to the Value property
        @return the number of the virtual point associated to the Value property
    */
    public  int getValueVirtualPointNumber()
    {
        return 5;
    }
}