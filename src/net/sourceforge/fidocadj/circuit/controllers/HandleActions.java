package net.sourceforge.fidocadj.circuit.controllers;

import java.util.*;

import net.sourceforge.fidocadj.circuit.*;
import net.sourceforge.fidocadj.circuit.model.*;
import net.sourceforge.fidocadj.geom.*;
import net.sourceforge.fidocadj.layers.*;
import net.sourceforge.fidocadj.primitives.*;
import net.sourceforge.fidocadj.graphic.*;

/** CopyPasteActions: contains a controller which can perform handle drag and
    move actions on a primitive database

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

    Copyright 2014-2016 by Davide Bucci
</pre>

    @author Davide Bucci
*/

public class HandleActions
{
    private final DrawingModel dmp;
    private final EditorActions edt;
    private final UndoActions ua;
    private final SelectionActions sa;


    // ******** DRAG & INTERFACE *********

    // True if we are at the beginning of a dragging operation.
    private boolean firstDrag;

    // The graphic primitive being treated.
    private GraphicPrimitive primBeingDragged;
    // The handle of the active graphic primitive being treated.
    private int handleBeingDragged;

    // Old cursor position for handle drag.
    private int opx;
    private int opy;

    // True if the primitive has moved while dragging.
    private boolean hasMoved;

    // Other old cursor position for handle drag...
    private int oldpx;
    private int oldpy;


    /** Standard constructor: provide the database class.
        @param pp the drawing model
        @param e the editor controller
        @param s the selection controller
        @param u the undo controller
    */
    public HandleActions (DrawingModel pp, EditorActions e,
        SelectionActions s,
        UndoActions u)
    {
        dmp=pp;
        edt=e;
        ua=u;
        sa=s;
        firstDrag=false;
        handleBeingDragged=GraphicPrimitive.NO_DRAG;
    }

    /** Drag all the selected primitives during a drag operation.
        Position the primitives in the given (screen) position

        @param cc the element containing the drawing, which can receive
            repaint callbacks.
        @param px the x position (screen coordinates).
        @param py the y position (screen coordinates).
        @param cs the coordinate mapping.
    */
    public void dragPrimitives(PrimitivesParInterface cc, int px, int py,
        MapCoordinates cs)
    {
        // Check if we are effectively dragging the whole primitive...
        if(handleBeingDragged!=GraphicPrimitive.DRAG_PRIMITIVE)
            return;

        firstDrag=false;

        int dx=cs.unmapXsnap(px)-oldpx;
        int dy=cs.unmapYsnap(py)-oldpy;

        oldpx=cs.unmapXsnap(px);
        oldpy=cs.unmapXsnap(py);

        if(dx==0 && dy==0)
            return;

        // Here we adjust the new positions for all selected elements...
        for (GraphicPrimitive g : dmp.getPrimitiveVector()){
            if(g.getSelected()) {
                // This code is needed to ensure that all layer are printed
                // when dragging a component (it solves bug #24)
                if (g instanceof PrimitiveMacro) {
                    ((PrimitiveMacro)g).setDrawOnlyLayer(-1);
                }

                for(int j=0; j<g.getControlPointNumber();++j){
                    g.virtualPoint[j].x+=dx;
                    g.virtualPoint[j].y+=dy;
                    // Here we show the new place of the primitive.
                }
                g.setChanged(true);
            }
        }
        cc.forcesRepaint();
    }

    /** Start dragging handle. Check if the pointer is on the handle of a
        primitive and if it is the case, enter the dragging state.
        @param px the (screen) x coordinate of the pointer.
        @param py the (screen) y coordinate of the pointer.
        @param tolerance the tolerance (screen. i.e. no of pixel).
        @param multiple specifies whether multiple selection is active.
        @param cs the coordinate mapping to be used.

    */
    public void dragHandleStart(int px, int py, int tolerance, boolean multiple,
        MapCoordinates cs)
    {
        int i;
        int isel=0;
        int mindistance=Integer.MAX_VALUE;
        int distance=mindistance;
        int layer;

        hasMoved=false;

        GraphicPrimitive gp;
        Vector<LayerDesc> layerV=dmp.getLayers();

        oldpx=cs.unmapXnosnap(px);
        oldpy=cs.unmapXnosnap(py);

        firstDrag=true;

        int sptol=Math.abs(cs.unmapXnosnap(px+tolerance)-cs.unmapXnosnap(px));
        if (sptol<2) sptol=2;

        // Search for the closest primitive to the given point
        // Performs a cycle through all primitives and check their
        // distance.
        for (i=0; i<dmp.getPrimitiveVector().size(); ++i){
            gp=(GraphicPrimitive)dmp.getPrimitiveVector().get(i);
            layer= gp.getLayer();

            // Does not allow for selecting an invisible primitive
            if(layer<layerV.size() &&
                !((LayerDesc)layerV.get(layer)).isVisible &&
                !(gp instanceof PrimitiveMacro))
                continue;

            if(gp.selectedState){
                // Verify if the pointer is on a handle
                handleBeingDragged=gp.onHandle(cs, px, py);

                if(handleBeingDragged!=GraphicPrimitive.NO_DRAG){
                    primBeingDragged=gp;

                    continue;
                }
            }
            distance=gp.getDistanceToPoint(oldpx,oldpy);
            if (distance<=mindistance) {
                isel=i;
                mindistance=distance;
            }
        }
        // Verify if the whole primitive should be drag
        if (mindistance<sptol && handleBeingDragged<0){
            primBeingDragged=
                (GraphicPrimitive)dmp.getPrimitiveVector().get(isel);
            if (!multiple && !primBeingDragged.getSelected())
                sa.setSelectionAll(false);
            if(!multiple) {
                primBeingDragged.setSelected(true);
            }
            handleBeingDragged=GraphicPrimitive.DRAG_PRIMITIVE;
            firstDrag=true;
            oldpx=cs.unmapXsnap(px);
            oldpy=cs.unmapXsnap(py);
        } else if (handleBeingDragged<0) {
            // We want to select things in a rectangular area
            oldpx=cs.unmapXsnap(px);
            oldpy=cs.unmapXsnap(py);
            handleBeingDragged=GraphicPrimitive.RECT_SELECTION;
        }
    }

    /** End dragging handle.
        @param CC the editor object
        @param px the (screen) x coordinate of the pointer.
        @param py the (screen) y coordinate of the pointer.
        @param multiple specifies whether multiple selection is active.
        @param cs the coordinate mapping to be used.
    */
    public void dragHandleEnd(PrimitivesParInterface CC, int px, int py,
        boolean multiple,
        MapCoordinates cs)
    {
        // Check if we are effectively dragging something...
        CC.setEvidenceRect(0,0,-1,-1);
        if(handleBeingDragged<0){
            if(handleBeingDragged==GraphicPrimitive.RECT_SELECTION){
                int xa=Math.min(oldpx, cs.unmapXnosnap(px));
                int ya=Math.min(oldpy, cs.unmapYnosnap(py));
                int xb=Math.max(oldpx, cs.unmapXnosnap(px));
                int yb=Math.max(oldpy, cs.unmapYnosnap(py));
                if(!multiple) sa.setSelectionAll(false);
                edt.selectRect(xa, ya, xb-xa, yb-ya);
            }
            // Test if we are anyway dragging an entire primitive
            if(handleBeingDragged==GraphicPrimitive.DRAG_PRIMITIVE &&
                hasMoved && ua!=null)
                ua.saveUndoState();

            handleBeingDragged=GraphicPrimitive.NO_DRAG;
            return;
        }
        handleBeingDragged=GraphicPrimitive.NO_DRAG;
        if(ua!=null) ua.saveUndoState();
    }

    /** Drag a handle.
        @param CC the editor object.
        @param px the (screen) x coordinate of the pointer.
        @param py the (screen) y coordinate of the pointer.
        @param cs the coordinates mapping to be used.
        @param isControl true if the control key is held down.
    */
    public void dragHandleDrag(PrimitivesParInterface CC,
        int px, int py, MapCoordinates cs, boolean isControl)
    {
        hasMoved=true;
        boolean flip=false;

        // Check if we are effectively dragging a handle...
        if(handleBeingDragged<0){
            if(handleBeingDragged==GraphicPrimitive.DRAG_PRIMITIVE)
                dragPrimitives(CC, px, py, cs);

            // if not, we are performing a rectangular selection
            if(handleBeingDragged==GraphicPrimitive.RECT_SELECTION) {

                int xa = cs.mapXi(oldpx, oldpy, false);
                int ya = cs.mapYi(oldpx, oldpy, false);
                int xb = opx;
                int yb = opy;

                if(opx>xa && px<xa)
                    flip=true;
                if(opy>ya && py<ya)
                    flip=true;

                if(!firstDrag) {
                    int a,b,c,d;

                    a = Math.min(xa,xb);
                    b = Math.min(ya,yb);
                    c = Math.abs(xb-xa);
                    d = Math.abs(yb-ya);

                    xb=px;
                    yb=py;
                    opx=px;
                    opy=py;

                    CC.setEvidenceRect(Math.min(xa,xb), Math.min(ya,yb),
                           Math.abs(xb-xa), Math.abs(yb-ya));

                    a=Math.min(a, Math.min(xa,xb));
                    b=Math.min(b, Math.min(ya,yb));
                    c=Math.max(c, Math.abs(xb-xa));
                    d=Math.max(d, Math.abs(yb-ya));

                    if (flip)
                        CC.forcesRepaint();
                    else
                        CC.forcesRepaint(a,b,c+10,d+10);

                    return;
                }
                xb=px;
                yb=py;
                opx=px;
                opy=py;
                firstDrag=false;
            }
            return;
        }

        if(!firstDrag) {
            CC.forcesRepaint();
        }
        firstDrag=false;

        // Here we adjust the new positions for the handle being drag...
        primBeingDragged.virtualPoint[handleBeingDragged].x=cs.unmapXsnap(px);
        // If control is hold, trace a square
        int ymm;
        if(!isControl || !(primBeingDragged instanceof PrimitiveOval ||
            primBeingDragged instanceof PrimitiveRectangle))
        {
            ymm=py;
        } else {
            // Transform the rectangle in a square, or the oval in a circle.
            int hn=0;
            if(handleBeingDragged==0) hn=1;
            ymm=cs.mapYi(primBeingDragged.virtualPoint[hn].x,
                primBeingDragged.virtualPoint[hn].y,false)+px-
                cs.mapXi(primBeingDragged.virtualPoint[hn].x,
                primBeingDragged.virtualPoint[hn].y,false);
        }
        primBeingDragged.virtualPoint[handleBeingDragged].y=cs.unmapYsnap(ymm);
        primBeingDragged.setChanged(true);
    }
}