package fidocadj.circuit.controllers;

import java.io.*;

import fidocadj.circuit.model.DrawingModel;
import fidocadj.primitives.GraphicPrimitive;
import fidocadj.primitives.PrimitivePCBPad;
import fidocadj.primitives.PrimitivePCBLine;
import fidocadj.primitives.PrimitiveRectangle;
import fidocadj.primitives.PrimitiveBezier;
import fidocadj.primitives.PrimitiveOval;
import fidocadj.primitives.PrimitiveMacro;
import fidocadj.primitives.PrimitiveConnection;
import fidocadj.primitives.PrimitiveLine;

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
    along with FidoCadJ. If not,
    @see <a href=http://www.gnu.org/licenses/>http://www.gnu.org/licenses/</a>.

    Copyright 2015-2023 by Davide Bucci
</pre>

    @author Davide Bucci
*/
public class AddElements
{

    final private DrawingModel dmp;
    final private UndoActions ua;

    // Default sizes for PCB elements
    public int pcbPadSizeX;
    public int pcbPadSizeY;
    public int pcbPadStyle;
    public int pcbPadDrill;
    public int pcbThickness;

    /** Standard constructor.
        @param pp the drawing model object on which this controller operates.
        @param u an undo controller (if available)
    */
    public AddElements(DrawingModel pp, UndoActions u)
    {
        dmp=pp;
        ua=u;
        pcbThickness = 5;
        pcbPadSizeX=5;
        pcbPadSizeY=5;
        pcbPadDrill=2;
    }

    /** Sets the default PCB pad size x.
        @param s    the wanted size in logical units.
    */
    public void setPcbPadSizeX(int s)
    {
        pcbPadSizeX=s;
    }

    /** Gets the default PCB pad size x.
        @return     the x size in logical units.
    */
    public int getPcbPadSizeX()
    {
        return pcbPadSizeX;
    }

    /** Sets the default PCB pad size y.
        @param s    the wanted size in logical units.
    */
    public void setPcbPadSizeY(int s)
    {
        pcbPadSizeY=s;
    }

    /** Gets the default PCB pad size y.
        @return     the size in logical units.
    */
    public int getPcbPadSizeY()
    {
        return pcbPadSizeY;
    }

    /** Sets the default PCB pad style.
        @param s    the style.
    */
    public void setPcbPadStyle(int s)
    {
        pcbPadStyle=s;
    }

    /** Gets the default PCB pad style.
        @return     the style.
    */
    public int getPcbPadStyle()
    {
        return pcbPadStyle;
    }

    /** Sets the default PCB pad drill size.
        @param s    the wanted drill size, in logical units.
    */
    public void setPcbPadDrill(int s)
    {
        pcbPadDrill=s;
    }

    /** Gets the default PCB pad drill size.
        @return     the drill size, in logical units.
    */
    public int getPcbPadDrill()
    {
        return pcbPadDrill;
    }

    /** Sets the default PCB track thickness.
        @param s the wanted thickness in logical units.
    */
    public void setPcbThickness(int s)
    {
        pcbThickness=s;
    }

    /** Gets the default PCB track thickness.
        @return     the track thickness in logical units.
    */
    public int getPcbThickness()
    {
        return pcbThickness;
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
            dmp.getTextFont(), dmp.getTextFontSize());
        g.setMacroFont(dmp.getTextFont(), dmp.getTextFontSize());

        dmp.addPrimitive(g, true, ua);
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
                                 dmp.getTextFont(),
                                 dmp.getTextFontSize());
            dmp.addPrimitive(g, true, ua);
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
            dmp.addPrimitive(new PrimitiveMacro(dmp.getLibrary(),
                    dmp.getLayers(), x, y, macroKey,"",
                    x+10, y+5, "", x+10, y+10,
                    dmp.getTextFont(),
                    dmp.getTextFontSize(), orientation, mirror), true, ua);
            primEdit=null;

        } catch (IOException gG) {
            // A simple error message on the console will be enough
            System.out.println(gG);
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
        if(isCircle) {
            y=ypoly[1]+x-xpoly[1];
        }

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
                                         dmp.getTextFont(),
                                         dmp.getTextFontSize());
            dmp.addPrimitive(g, true, ua);
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
                                        dmp.getTextFont(),
                                        dmp.getTextFontSize());

            dmp.addPrimitive(g, true, ua);

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
        if(isSquare) {
            y=ypoly[1]+x-xpoly[1];
        }

        // clickNumber == 0 means that no rectangle is being drawn.
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
                                         dmp.getTextFont(),
                                         dmp.getTextFontSize());
            dmp.addPrimitive(g, true, ua);
            cn = 0;
        }
        if (cn>=2)  { cn = 0; }
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
                                         dmp.getTextFont(),
                                         dmp.getTextFontSize());
            dmp.addPrimitive(g, true,ua);

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
                                  pcbPadSizeX,
                                  pcbPadSizeY,
                                  pcbPadDrill,
                                  pcbPadStyle,
                                  currentLayer,
                                  dmp.getTextFont(), dmp.getTextFontSize());

        dmp.addPrimitive(g, true, ua);
    }
}