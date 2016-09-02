package net.sourceforge.fidocadj.primitives;

import java.io.*;
import java.util.*;

import net.sourceforge.fidocadj.dialogs.*;
import net.sourceforge.fidocadj.export.*;
import net.sourceforge.fidocadj.geom.*;
import net.sourceforge.fidocadj.globals.*;
import net.sourceforge.fidocadj.graphic.*;


/** Class to handle the line primitive.

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
public final class PrimitiveLine extends GraphicPrimitive
{
    static final int N_POINTS=4;

    // Info about arrow.
    private final Arrow arrowData;

    private int dashStyle;


    // Those are data which are kept for the fast redraw of this primitive.
    // Basically, they are calculated once and then used as much as possible
    // without having to calculate everything from scratch.
    private int xa, ya, xb, yb;
    private int x1, y1,x2,y2;
    private float w;
    private int length2;
    private int xbpap1, ybpap1;
    private boolean arrows;

    /** Constructor.
        @param x1       the x coordinate of the start point of the line.
        @param y1       the y coordinate of the start point of the line.
        @param x2       the x coordinate of the end point of the line.
        @param y2       the y coordinate of the end point of the line.
        @param layer    the layer to be used.
        @param arrowS   true if there is an arrow at the beginning of the line.
        @param arrowE   true if there is an arrow at the end of the line.
        @param arrowSt  style of the arrow.
        @param arrowLe  length of the arrow.
        @param arrowWi  width of the arrow.
        @param dashSt   the dashing style.
        @param f        the name of the font for attached text.
        @param size     the size of the font for attached text.
    */
    public PrimitiveLine(int x1, int y1, int x2, int y2, int layer,
                        boolean arrowS, boolean arrowE,
                        int arrowSt, int arrowLe, int arrowWi, int dashSt,
                        String f, int size)
    {
        super();

        arrowData=new Arrow();
        arrowData.setArrowStart(arrowS);
        arrowData.setArrowEnd(arrowE);
        arrowData.setArrowHalfWidth(arrowWi);
        arrowData.setArrowLength(arrowLe);
        arrowData.setArrowStyle(arrowSt);

        dashStyle = dashSt;

        initPrimitive(-1, f, size);

        virtualPoint[0].x=x1;
        virtualPoint[0].y=y1;
        virtualPoint[1].x=x2;
        virtualPoint[1].y=y2;
        virtualPoint[getNameVirtualPointNumber()].x=x1+5;
        virtualPoint[getNameVirtualPointNumber()].y=y1+5;
        virtualPoint[getValueVirtualPointNumber()].x=x1+5;
        virtualPoint[getValueVirtualPointNumber()].y=y1+10;

        setLayer(layer);
    }

    /** Constructor.
        @param f        the name of the font for attached text.
        @param size     the size of the font for attached text.
    */
    public PrimitiveLine(String f, int size)
    {
        super();
        arrowData=new Arrow();

        initPrimitive(-1, f, size);
    }

    /** Gets the number of control points used.
        @return the number of points used by the primitive
    */

    public int getControlPointNumber()
    {
        return N_POINTS;
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

        if(changed) {
            changed=false;
            // in the line primitive, the first two virtual points represent
            // the beginning and the end of the segment to be drawn.
            x1=coordSys.mapX(virtualPoint[0].x,virtualPoint[0].y);
            y1=coordSys.mapY(virtualPoint[0].x,virtualPoint[0].y);
            x2=coordSys.mapX(virtualPoint[1].x,virtualPoint[1].y);
            y2=coordSys.mapY(virtualPoint[1].x,virtualPoint[1].y);

            // We store the coordinates in an ordered way in order to ease
            // the determination of the clip rectangle.
            if (x1>x2) {
                xa=x2;
                xb=x1;
            } else {
                xa=x1;
                xb=x2;
            }
            if (y1>y2) {
                ya=y2;
                yb=y1;
            } else {
                ya=y1;
                yb=y2;
            }

            // Calculate the width of the stroke in pixel. It should not
            // make our lines disappear, even at very small zoom ratios.
            // So we put a limit D_MIN.
            w = (float)(Globals.lineWidth*coordSys.getXMagnitude());
            if (w<D_MIN) w=D_MIN;

            // Calculate the square of the length in pixel.
            length2=(xa-xb)*(xa-xb)+(ya-yb)*(ya-yb);

            arrows = arrowData.atLeastOneArrow();

            // This correction solves bug #3101041 (old SF code)
            // We do need to apply a correction to the clip calculation
            // rectangle if necessary to take into account the arrow heads
            if (arrows) {
                int h=arrowData.prepareCoordinateMapping(coordSys);
                xa -= Math.abs(h);
                ya -= Math.abs(h);
                xb += Math.abs(h);
                yb += Math.abs(h);
            }
            xbpap1=xb-xa+1;
            ybpap1=yb-ya+1;
        }
        // This is a trick. We skip drawing the line if it is too short.
        if(length2>2) {
            if(!g.hitClip(xa,ya, xbpap1,ybpap1))
                return;

            g.applyStroke(w, dashStyle);

            // Eventually, we draw the arrows at the extremes.
            if (arrows) {
                if (arrowData.isArrowStart()) {
                    PointG Pc=arrowData.drawArrow(g,x1,y1,x2,y2);
                }
                if (arrowData.isArrowEnd()) {
                    PointG Pc=arrowData.drawArrow(g,x2,y2,x1,y1);
                }
            }
            g.drawLine(x1,y1,x2,y2);
        }
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
        if (tokens[0].equals("LI")) {   // Line
            if (N<5) {
                IOException E=new IOException("bad arguments on LI");
                throw E;
            }
            // Load the points in the virtual points associated to the
            // current primitive.

            int x1 = virtualPoint[0].x=Integer.parseInt(tokens[1]);
            int y1 = virtualPoint[0].y=Integer.parseInt(tokens[2]);
            virtualPoint[1].x=Integer.parseInt(tokens[3]);
            virtualPoint[1].y=Integer.parseInt(tokens[4]);

            virtualPoint[getNameVirtualPointNumber()].x=x1+5;
            virtualPoint[getNameVirtualPointNumber()].y=y1+5;
            virtualPoint[getValueVirtualPointNumber()].x=x1+5;
            virtualPoint[getValueVirtualPointNumber()].y=y1+10;

            if(N>5) parseLayer(tokens[5]);

            // FidoCadJ extensions

            if(N>6 && tokens[6].equals("FCJ")) {
                int i=arrowData.parseTokens(tokens, 7);
                dashStyle = checkDashStyle(Integer.parseInt(tokens[i]));
            }
        } else {
            IOException E=new IOException("LI: Invalid primitive:"+tokens[0]+
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
        // Here we check if the given point lies inside the text areas

        if(checkText(px, py))
            return 0;

        return GeometricDistances.pointToSegment(
                virtualPoint[0].x,virtualPoint[0].y,
                virtualPoint[1].x,virtualPoint[1].y,
                px,py);
    }

    /** Obtain a string command descripion of the primitive.
        @param extensions true if FidoCadJ extensions to the old FidoCAD format
            should be active.
        @return the FIDOCAD command line.
    */
    public String toString(boolean extensions)
    {
        // A single point line without anything is not worth converting.
        if (name.length()==0 && value.length()==0 &&
            virtualPoint[0].x==virtualPoint[1].x &&
            virtualPoint[0].y==virtualPoint[1].y)
        {
            return "";
        }

        String s= "LI "+virtualPoint[0].x+" "+virtualPoint[0].y+" "+
            +virtualPoint[1].x+" "+virtualPoint[1].y+" "+
            getLayer()+"\n";

        if(extensions && (arrowData.atLeastOneArrow() || dashStyle>0 ||
            name!=null && name.length())!=0 ||
            value!=null && value.length()!=0)
        {
            String text = "0";
            // We take into account that there may be some text associated
            // to that primitive.
            if (hasName() || hasValue())
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
        exp.exportLine(cs.mapX(virtualPoint[0].x,virtualPoint[0].y),
                       cs.mapY(virtualPoint[0].x,virtualPoint[0].y),
                       cs.mapX(virtualPoint[1].x,virtualPoint[1].y),
                       cs.mapY(virtualPoint[1].x,virtualPoint[1].y),
                       getLayer(),
                       arrowData.isArrowStart(), arrowData.isArrowEnd(),
                       arrowData.getArrowStyle(),
                       (int)(arrowData.getArrowLength()*cs.getXMagnitude()),
                       (int)(arrowData.getArrowHalfWidth()*cs.getXMagnitude()),
                       dashStyle,
                       Globals.lineWidth*cs.getXMagnitude());
    }

    /** Get the number of the virtual point associated to the Name property
        @return the number of the virtual point associated to the Name property
    */
    public int getNameVirtualPointNumber()
    {
        return 2;
    }

    /** Get the number of the virtual point associated to the Value property
        @return the number of the virtual point associated to the Value property
    */
    public  int getValueVirtualPointNumber()
    {
        return 3;
    }
}