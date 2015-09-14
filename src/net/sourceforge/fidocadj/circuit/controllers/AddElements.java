package net.sourceforge.fidocadj.circuit.controllers;

import java.io.*;
import java.util.Vector;

import net.sourceforge.fidocadj.circuit.model.*;
import net.sourceforge.fidocadj.geom.*;
import net.sourceforge.fidocadj.layers.*;
import net.sourceforge.fidocadj.primitives.*;
import net.sourceforge.fidocadj.graphic.*;

/** AddElements: handle the dynamic insertion of graphic elements.

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

    Copyright 2015 by Davide Bucci
</pre>

    @author Davide Bucci
*/
public class AddElements
{

    final private DrawingModel P;
    final private UndoActions ua;

    // Default sizes for PCB elements
    public int PCB_pad_sizex;
    public int PCB_pad_sizey;
    public int PCB_pad_style;
    public int PCB_pad_drill;
    public int PCB_thickness;


    public AddElements(DrawingModel pp, UndoActions u)
    {
        P=pp;
        ua=u;

        PCB_thickness = 5;
        PCB_pad_sizex=5;
        PCB_pad_sizey=5;
        PCB_pad_drill=2;

    }

    /** Sets the default PCB pad size x.
        @param s    the wanted size in logical units.
    */
    public void setPCB_pad_sizex(int s)
    {
        PCB_pad_sizex=s;
    }

    /** Gets the default PCB pad size x.
        @return     the x size in logical units.
    */
    public int getPCB_pad_sizex()
    {
        return PCB_pad_sizex;
    }

    /** Sets the default PCB pad size y.
        @param s    the wanted size in logical units.
    */
    public void setPCB_pad_sizey(int s)
    {
        PCB_pad_sizey=s;
    }

    /** Gets the default PCB pad size y.
        @return     the size in logical units.
    */
    public int getPCB_pad_sizey()
    {
        return PCB_pad_sizey;
    }

    /** Sets the default PCB pad style.
        @param s    the style.
    */
    public void setPCB_pad_style(int s)
    {
        PCB_pad_style=s;
    }

    /** Gets the default PCB pad style.
        @return     the style.
    */
    public int getPCB_pad_style()
    {
        return PCB_pad_style;
    }

    /** Sets the default PCB pad drill size.
        @param s    the wanted drill size, in logical units.
    */
    public void setPCB_pad_drill(int s)
    {
        PCB_pad_drill=s;
    }

    /** Gets the default PCB pad drill size.
        @return     the drill size, in logical units.
    */
    public int getPCB_pad_drill()
    {
        return PCB_pad_drill;
    }

    /** Sets the default PCB track thickness.
        @param s the wanted thickness in logical units.
    */
    public void setPCB_thickness(int s)
    {
        PCB_thickness=s;
    }

    /** Gets the default PCB track thickness.
        @return     the track thickness in logical units.
    */
    public int getPCB_thickness()
    {
        return PCB_thickness;
    }


    /** Add a connection primitive at the given point.
        @param x the x coordinate of the connection (logical)
        @param y the y coordinate of the connection (logical)
        @param currentLayer the layer on which the primitive should
            be put.
    */
    public void addConnection(int x, int y, int currentLayer)
    {
        PrimitiveConnection g=new PrimitiveConnection(x, y, currentLayer,
            P.getTextFont(), P.getTextFontSize());
        g.setMacroFont(P.getTextFont(), P.getTextFontSize());

        P.addPrimitive(g, true, ua);
    }

    /** Introduce a line. You can introduce lines point by point, so you
        should keep track of the number of clicks you received (clickNumber).
        You must count the number of clicks and see if there is a modification
        needed on it (the return value).

        @param x coordinate of the click (logical)
        @param y coordinate of the click (logical)
        @param xpoly the array of x coordinates of points to be introduced.
        @param ypoly the array of x coordinates of points to be introduced.
        @param currentLayer the layer on which the primitive should be put.
        @param clickNumber the click number: 1 is the first click, 2 is the
            second (and final) one.
        @param altButton true if the alternate button is pressed (the
            introduction of lines is thus stopped).
        @return the new value of clickNumber.

    */
    public int addLine(int x, int y, int[] xpoly,
        int[] ypoly, int currentLayer, int clickNumber, boolean altButton)
    {
        int cn=clickNumber;

        // clickNumber == 0 means that no line is being drawn
        xpoly[clickNumber] = x;
        ypoly[clickNumber] = y;

        if (clickNumber == 2 || altButton) {
            // Here we know the two points needed for creating
            // the line (clickNumber=2 means that).
            // The object is thus added to the database.
            PrimitiveLine g= new PrimitiveLine(xpoly[1],
                                 ypoly[1],
                                 xpoly[2],
                                 ypoly[2],
                                 currentLayer,
                                 false,
                                 false,
                                 0,3,2,0,
                                 P.getTextFont(),
                                 P.getTextFontSize());
            P.addPrimitive(g, true, ua);
            // Check if the user has clicked with the right button.
            // In this case, the introduction is stopped, or we continue
            // with a second line (segment) continuous to the one just
            // introduced.
            if(altButton) {
                cn = 0;
            } else {
                cn = 1;
                xpoly[1] = xpoly[2];
                ypoly[1] = ypoly[2];
            }
        }
        return cn;
    }

    /** Introduce the macro being edited at the given coordinate.
        @param x the x coordinate (logical).
        @param y the y coordinate (logical).
        @param sa the SelectionActions controller to handle the selection
            state of the whole drawing, which will be unselected.
        @param pe the current primitive being edited.
        @param macroKey the macro key of the macro to insert (maybe it should
            be obtained directly from pe).
        @return the new primitive being edited.
    */
    public GraphicPrimitive addMacro(int x, int y, SelectionActions sa,
        GraphicPrimitive pe, String macroKey)
    {
        GraphicPrimitive primEdit=pe;
        try {
            // Here we add a macro. There is a remote risk that the macro
            // we are inserting contains an error. This is not something
            // which would happen frequently, since if the macro is in the
            // library this means it is available, but we need to use
            // the block try anyway.

            sa.setSelectionAll(false);

            int orientation = 0;
            boolean mirror = false;

            if (primEdit instanceof PrimitiveMacro)  {
                orientation = ((PrimitiveMacro)primEdit).getOrientation();
                mirror = ((PrimitiveMacro)primEdit).isMirrored();
            }
            P.addPrimitive(new PrimitiveMacro(P.getLibrary(),
                    P.getLayers(), x, y, macroKey,"",
                    x+10, y+5, "", x+10, y+10,
                    P.getTextFont(),
                    P.getTextFontSize(), orientation, mirror), true, ua);
            primEdit=null;

        } catch (IOException G) {
            // A simple error message on the console will be enough
            System.out.println(G);
        }
        return primEdit;
    }

    /** Introduce an ellipse. You can introduce ellipses with two clicks, so
        you should keep track of the number of clicks you received
        (clickNumber).
        You must count the number of clicks and see if there is a modification
        needed on it (the return value).

        @param x coordinate of the click (logical).
        @param ty coordinate of the click (logical).
        @param xpoly the array of x coordinates of points to be introduced.
        @param ypoly the array of x coordinates of points to be introduced.
        @param currentLayer the layer on which the primitive should be put.
        @param clickNumber the click number: 1 is the first click, 2 is the
            second (and final) one.
        @param isCircle if true, force the ellipse to be a circle
        @return the new value of clickNumber.
    */
    public int addEllipse(int x, int ty,
        int xpoly[], int ypoly[], int currentLayer,
        int clickNumber,
        boolean isCircle)
    {
        int y=ty;
        int cn=clickNumber;
        if(isCircle)
            y=ypoly[1]+x-xpoly[1];

        // clickNumber == 0 means that no ellipse is being drawn

        xpoly[clickNumber] = x;
        ypoly[clickNumber] = y;
        if (cn == 2) {
            PrimitiveOval g=new PrimitiveOval(xpoly[1],
                                         ypoly[1],
                                         xpoly[2],
                                         ypoly[2],
                                         false,
                                         currentLayer,0,
                                         P.getTextFont(), P.getTextFontSize());

            P.addPrimitive(g, true, ua);

            cn = 0;

        }
        return cn;
    }

    /** Introduce a Bézier curve. You can introduce this with four clicks, so
        you should keep track of the number of clicks you received
        (clickNumber).
        You must count the number of clicks and see if there is a modification
        needed on it (the return value). In other words, when using this
        method, you are responsible of storing this value somewhere and
        providing it any time you need to call addBezier again.

        @param x coordinate of the click (logical)
        @param y coordinate of the click (logical)
        @param xpoly the array of x coordinates of points to be introduced.
        @param ypoly the array of x coordinates of points to be introduced.
        @param currentLayer the layer on which the primitive should be put.
        @param clickNumber the click number: 1 is the first click, 2 is the
            second one, and so on...
        @return the new value of clickNumber.
    */
    public int addBezier(int x, int y, int xpoly[],
        int ypoly[], int currentLayer, int clickNumber)
    {
        int cn=clickNumber;

        // clickNumber == 0 means that no bezier is being drawn

        xpoly[clickNumber] = x;
        ypoly[clickNumber] = y;
        // a polygon definition is ended with a double click
        if (clickNumber == 4) {
            PrimitiveBezier g=new PrimitiveBezier(xpoly[1],
                                        ypoly[1],
                                        xpoly[2],
                                        ypoly[2],
                                        xpoly[3],
                                        ypoly[3],
                                        xpoly[4],
                                        ypoly[4],
                                        currentLayer,
                                        false,
                                        false,
                                        0,3,2,0,
                                        P.getTextFont(),
                                        P.getTextFontSize());

            P.addPrimitive(g, true, ua);

            cn = 0;
        }
        return cn;
    }

    /** Introduce a rectangle. You can introduce this with two clicks, so
        you should keep track of the number of clicks you received
        (clickNumber).
        You must count the number of clicks and see if there is a modification
        needed on it (the return value).

        @param x coordinate of the click (logical).
        @param ty coordinate of the click (logical).
        @param xpoly the array of x coordinates of points to be introduced.
        @param ypoly the array of x coordinates of points to be introduced.
        @param currentLayer the layer on which the primitive should be put.
        @param clickNumber the click number: 1 is the first click, 2 is the
            second (and final) one.
        @param isSquare force the rectangle to be a square.
        @return the new value of clickNumber.
    */
    public int addRectangle(int x, int ty,
        int xpoly[], int ypoly[], int currentLayer,
        int clickNumber, boolean isSquare)
    {
        int y=ty;
        int cn=clickNumber;
        if(isSquare)
            y=ypoly[1]+x-xpoly[1];

        // clickNumber == 0 means that no rectangle is being drawn

        xpoly[clickNumber] = x;
        ypoly[clickNumber] = y;
        if (cn == 2) {
            // The second click ends the rectangle introduction.
            // We thus create the primitive and store it.
            PrimitiveRectangle g=new PrimitiveRectangle(xpoly[1],
                                         ypoly[1],
                                         xpoly[2],
                                         ypoly[2],
                                         false,
                                         currentLayer,0,
                                         P.getTextFont(), P.getTextFontSize());

            P.addPrimitive(g, true, ua);
            cn = 0;
        }
        if (cn>=2) cn = 0;
        return cn;
    }

    /** Introduce a PCB line. You can introduce this with two clicks, so
        you should keep track of the number of clicks you received
        (clickNumber).
        You must count the number of clicks and see if there is a modification
        needed on it (the return value).

        @param x coordinate of the click (logical).
        @param y coordinate of the click (logical).
        @param xpoly the array of x coordinates of points to be introduced.
        @param ypoly the array of x coordinates of points to be introduced.
        @param currentLayer the layer on which the primitive should be put.
        @param clickNumber the click number: 1 is the first click, 2 is the
            second (and final) one.
        @param altButton if true, the introduction of PCBlines should be
            stopped.
        @param thickness the thickness of the PCB line.
        @return the new value of clickNumber.
    */
    public int addPCBLine(int x, int y,
        int xpoly[], int ypoly[], int currentLayer,
        int clickNumber, boolean altButton,
        float thickness)
    {
        int cn=clickNumber;
        // clickNumber == 0 means that no pcb line is being drawn

        xpoly[cn] = x;
        ypoly[cn] = y;
        if (cn == 2|| altButton) {
            // Here is the end of the PCB line introduction: we create the
            // primitive.
            final PrimitivePCBLine g=new PrimitivePCBLine(xpoly[1],
                                         ypoly[1],
                                         xpoly[2],
                                         ypoly[2],
                                         thickness,
                                         currentLayer,
                                         P.getTextFont(), P.getTextFontSize());
            P.addPrimitive(g, true,ua);

            // Check if the user has clicked with the right button.
            if(altButton) {
                // We stop the PCB line here
                cn = 0;
            } else {
                // We then make sort that a new PCB line will be beginning
                // exactly at the same coordinates at which the previous
                // one was stopped.
                cn = 1;
                xpoly[1] = xpoly[2];
                ypoly[1] = ypoly[2];
            }
        }
        return cn;
    }

    /** Introduce a new PCB pad.
        @param x coordinate of the click (logical).
        @param y coordinate of the click (logical).
        @param currentLayer the layer on which the primitive should be put.
    */
    public void addPCBPad(int x, int y, int currentLayer)
    {
        final PrimitivePCBPad g=new PrimitivePCBPad(x, y,
                                  PCB_pad_sizex,
                                  PCB_pad_sizey,
                                  PCB_pad_drill,
                                  PCB_pad_style,
                                  currentLayer,
                                  P.getTextFont(), P.getTextFontSize());

        P.addPrimitive(g, true, ua);
    }
}