package fidocadj.circuit.controllers;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import fidocadj.circuit.model.DrawingModel;
import fidocadj.geom.MapCoordinates;
import fidocadj.geom.ChangeCoordinatesListener;
import fidocadj.globals.Globals;
import fidocadj.graphic.PointG;
import fidocadj.layers.StandardLayers;
import fidocadj.primitives.GraphicPrimitive;
import fidocadj.primitives.PrimitiveLine;
import fidocadj.primitives.PrimitiveOval;
import fidocadj.primitives.PrimitiveRectangle;
import fidocadj.primitives.PrimitiveComplexCurve;
import fidocadj.primitives.PrimitivePolygon;
import fidocadj.primitives.PrimitivePCBLine;
import fidocadj.primitives.PrimitiveMacro;


/** Extends ElementsEdtActions in order to support those events such as
    MouseMove, which require a continuous interaction and an immediate
    rendering.

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

    Copyright 2014-2024 by Davide Bucci
</pre>

    @author Davide Bucci
*/

public class ContinuosMoveActions extends ElementsEdtActions
{
    // A coordinates listener
    private ChangeCoordinatesListener coordinatesListener=null;

    private int oldx;
    private int oldy;
    
    // Variables for moving selected primitives with Move command
    private boolean isMovingSelected = false;
    private Map<GraphicPrimitive, PointG> originalPositions;
    // Center point of all selected primitives (only for Move command)
    private int selectionCenterX;
    private int selectionCenterY;

    /** Constructor
        @param pp the DrawingModel to be associated to the controller
        @param s the selection controller.
        @param u undo controller, exploited here
        @param e editor controller, exploited here
    */
    public ContinuosMoveActions(DrawingModel pp, SelectionActions s,
        UndoActions u,
        EditorActions e)
    {
        super(pp, s, u, e);
        oldx=-1;
        oldy=-1;
    }

    /** Define the listener to be called when the coordinates of the mouse
        cursor are changed
        @param c the new coordinates listener
    */
    public void addChangeCoordinatesListener(ChangeCoordinatesListener c)
    {
        coordinatesListener=c;
    }

    /** Get the listener to be called when the coordinates of the mouse
        cursor are changed
        @return the coordinates listener
    */
    public ChangeCoordinatesListener getChangeCoordinatesListener()
    {
        System.out.println("Here we are! "+coordinatesListener);
        return coordinatesListener;
    }

    /** Handle a continuous move of the pointer device. It can be the result
        of a mouse drag for a rectangular selection, or a component move.
        @param cs the coordinate mapping which should be used
        @param xa the pointer position (x), in screen coordinates
        @param ya the pointer position (y), in screen coordinates
        @param isControl true if the CTRL key is pressed. This modifies
            some behaviors (for example, when introducing an ellipse it is
            forced to be a circle and so on).
        @return true if a repaint should be done
    */
    public boolean continuosMove(MapCoordinates cs,
        int xa, int ya,
        boolean isControl)
    {
        // Handle Move command mode (center-anchored movement)
        // This is separate from normal drag operations
        if (isMovingSelected) {
            updateMovePositions(cs.unmapXsnap(xa), cs.unmapYsnap(ya));
            return true;
        }
        
        // This transformation/antitrasformation is useful to take care
        // of the snapping.
        int x=cs.mapX(cs.unmapXsnap(xa),0);
        int y=cs.mapY(0,cs.unmapYsnap(ya));

        // If there is anything interesting to do, leave immediately.
        if(oldx==x && oldy==y) {
            return false;
        }

        oldx=x;
        oldy=y;

        // Notify the current pointer coordinates, if a listener is available.
        if (coordinatesListener!=null) {
            coordinatesListener.changeCoordinates(
                cs.unmapXsnap(xa),
                cs.unmapYsnap(ya));
        }

        boolean toRepaint=false;
        // If primEdit is different from null, it will be drawn in the
        // paintComponent event.
        // We need to differentiate this case since when we are entering a
        // macro, primEdit contains some useful hints about the orientation
        // and the mirroring

        if (actionSelected !=ElementsEdtActions.MACRO) {
            primEdit = null;
        }

        /*  MACRO ***********************************************************

             +1+#       #
                 #     #
                #########
               ## ##### ##
              #############
             ###############
             # ########### #
             # #         # #
                #### ####
        */
        if (actionSelected == ElementsEdtActions.MACRO) {
            try {
                int orientation = 0;
                boolean mirror = false;

                if (primEdit instanceof PrimitiveMacro)  {
                    orientation = ((PrimitiveMacro)primEdit).getOrientation();
                    mirror = ((PrimitiveMacro)primEdit).isMirrored();
                }

                PrimitiveMacro n = new PrimitiveMacro(dmp.getLibrary(),
                    StandardLayers.createEditingLayerArray(), cs.unmapXsnap(x),
                    cs.unmapYsnap(y),macroKey,"", cs.unmapXsnap(x)+10,
                    cs.unmapYsnap(y)+5, "", cs.unmapXsnap(x)+10,
                    cs.unmapYsnap(y)+10,
                    dmp.getTextFont(),
                    dmp.getTextFontSize(), orientation, mirror);
                n.setDrawOnlyLayer(-1);
                primEdit = n;
                toRepaint=true;
                successiveMove = true;
            } catch (IOException eE) {
                // Here we do not do nothing.
                System.out.println("Warning: exception while handling macro: "
                    +eE);
            }
        }


        if (clickNumber == 0) {
            return toRepaint;
        }

        /*  LINE **************************************************************

               +1+
                 **
                   **
                     **
                       **
                         +2+

        */
        if (actionSelected == ElementsEdtActions.LINE) {

            primEdit = new PrimitiveLine(xpoly[1],
                ypoly[1], cs.unmapXsnap(x), cs.unmapYsnap(y), 0,
                false, false, 0, 3, 2, 0, dmp.getTextFont(),
                dmp.getTextFontSize());

            toRepaint=true;
            successiveMove = true;

            if (coordinatesListener!=null) {
                double w = Math.sqrt((xpoly[1]-
                    cs.unmapXsnap(xa))*
                    (xpoly[1]-cs.unmapXsnap(xa))+
                    (ypoly[1]-cs.unmapYsnap(ya))*
                    (ypoly[1]-cs.unmapYsnap(ya)));
                double wmm = w*127/1000;
                coordinatesListener.changeInfos(
                    Globals.messages.getString("length")+Globals.roundTo(w,2)+
                    " ("+Globals.roundTo(wmm,2)+" mm)");

            }
        }
        /*  PCBLINE ***********************************************************

               +1+
                ***
                  ***
                    ***
                      ***
                        +2+

        */
        if (actionSelected == ElementsEdtActions.PCB_LINE) {
            primEdit = new PrimitivePCBLine(xpoly[1],
                ypoly[1], cs.unmapXsnap(x), cs.unmapYsnap(y),
                ae.getPcbThickness(), 0, dmp.getTextFont(),
                dmp.getTextFontSize());

            toRepaint=true;
            successiveMove = true;
            if (coordinatesListener!=null) {
                double w = Math.sqrt((xpoly[1]-
                    cs.unmapXsnap(xa))*
                    (xpoly[1]-cs.unmapXsnap(xa))+
                    (ypoly[1]-cs.unmapYsnap(ya))*
                    (ypoly[1]-cs.unmapYsnap(ya)));
                coordinatesListener.changeInfos(
                    Globals.messages.getString("length")+
                    Globals.roundTo(w,2));
            }
        }
        /*  BEZIER ************************************************************
                                +3+
               +1+   ******
                 ****      **
                      +2+    **
                             **
                           **
                        +4+

        */
        if (actionSelected == ElementsEdtActions.BEZIER) {
            // Since we do not know how to fabricate a cubic curve with less
            // than four points, we use a polygon instead.

            primEdit = new PrimitivePolygon(false, 0, 0,
                    dmp.getTextFont(), dmp.getTextFontSize());

            for(int i=1; i<=clickNumber; ++i) {
                ((PrimitivePolygon)primEdit).addPoint(xpoly[i], ypoly[i]);
            }


            ((PrimitivePolygon)primEdit).addPoint(cs.unmapXsnap(x),
                cs.unmapYsnap(y));

            toRepaint=true;
            successiveMove = true;

        }
        /*  POLYGON ***********************************************************

               +1+            +3+
                 **************
                   ***********
                     ********
                       *****
                         +2+

        */
        if (actionSelected == ElementsEdtActions.POLYGON) {
            primEdit = new PrimitivePolygon(false, 0, 0,
                    dmp.getTextFont(), dmp.getTextFontSize());

            for(int i=1; i<=clickNumber && i<ElementsEdtActions.NPOLY; ++i) {
                ((PrimitivePolygon)primEdit).addPoint(xpoly[i], ypoly[i]);
            }

            ((PrimitivePolygon)primEdit).addPoint(cs.unmapXsnap(x),
                cs.unmapYsnap(y));

            toRepaint=true;
            successiveMove = true;
        }

        /*  COMPLEX CURVE ****************************************************

               +1+            +5+
                 *****   *****
                   ****************+4+
               +2+***********
                     ****
                      +3+

        */
        if (actionSelected == ElementsEdtActions.COMPLEXCURVE) {
            primEdit = new PrimitiveComplexCurve(false, false, 0,
                false, false, 0, 3, 2, 0,
                dmp.getTextFont(),dmp.getTextFontSize());

            for(int i=1; i<=clickNumber && i<ElementsEdtActions.NPOLY; ++i) {
                ((PrimitiveComplexCurve)primEdit).addPoint(xpoly[i], ypoly[i]);
            }

            ((PrimitiveComplexCurve)primEdit).addPoint(cs.unmapXsnap(x),
                cs.unmapYsnap(y));

            toRepaint=true;
            successiveMove = true;
        }

        /*  RECTANGLE *********************************************************

              +1+
                **********
                **********
                **********
                **********
                         +2+

       */
        if (actionSelected == ElementsEdtActions.RECTANGLE) {
            // If control is hold, trace a square
            int ymm;
            if(isControl&&clickNumber>0) {
                ymm=cs.mapY(xpoly[1],ypoly[1])+x-cs.mapX(xpoly[1],ypoly[1]);
            } else {
                ymm=y;
            }
            primEdit = new PrimitiveRectangle(xpoly[1],
                ypoly[1], cs.unmapXsnap(x), cs.unmapYsnap(ymm),
                false,  0, 0, dmp.getTextFont(), dmp.getTextFontSize());

            toRepaint=true;
            successiveMove = true;
        }
        /*  ELLIPSE ***********************************************************

               +1+
                    ***
                  *******
                 *********
                 *********
                  *******
                    ***
                          +2+

        */
        if (actionSelected == ElementsEdtActions.ELLIPSE) {
            // If control is hold, trace a circle
            int ymm;
            if(isControl&&clickNumber>0) {
                ymm=cs.mapY(xpoly[1],ypoly[1])+x-cs.mapX(xpoly[1],ypoly[1]);
            } else {
                ymm=y;
            }

            primEdit = new PrimitiveOval(xpoly[1],
                ypoly[1], cs.unmapXsnap(x), cs.unmapYsnap(ymm),
                false,  0, 0, dmp.getTextFont(), dmp.getTextFontSize());

            toRepaint=true;
            successiveMove = true;
        }
        return toRepaint;
    }
    
    /** Start moving selected primitives with Move command.
        This saves the original positions and calculates the center.
        NOTE: This is ONLY for the Move command, not for normal drag operations.
    */
    public void startMovingSelected(MapCoordinates cs)
    {
        if (sa.getFirstSelectedPrimitive() == null) {
            return;
        }

        isMovingSelected = true;
        originalPositions = new HashMap<>();

        // Calculate bounds of all selected primitives to find center
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        // Save original positions and find bounds
        for (GraphicPrimitive g : dmp.getPrimitiveVector()) {
            if (g.isSelected()) {
                originalPositions.put(g, new PointG(
                    g.getFirstPoint().x, 
                    g.getFirstPoint().y));

                // Update bounds
                int x = g.getFirstPoint().x;
                int y = g.getFirstPoint().y;
                if (x < minX) minX = x;
                if (x > maxX) maxX = x;
                if (y < minY) minY = y;
                if (y > maxY) maxY = y;
            }
        }

        // Calculate center of selection
        int centerX = (minX + maxX) / 2;
        int centerY = (minY + maxY) / 2;

        // IMPORTANT: Snap the center to the grid!
        // This ensures that when we calculate offsets, everything stays aligned
        int screenX = cs.mapX(centerX, centerY);
        int screenY = cs.mapY(centerX, centerY);

        selectionCenterX = cs.unmapXsnap(screenX);
        selectionCenterY = cs.unmapYsnap(screenY);
    }
    
    /** Check if we are currently moving selected primitives.
        @return true if in move mode
    */
    public boolean isMovingSelected()
    {
        return isMovingSelected;
    }

    /** Update positions of selected primitives during Move command.
        This anchors the selection center to the mouse position.
        @param x current x coordinate (where the mouse is)
        @param y current y coordinate (where the mouse is)
    */
    public void updateMovePositions(int x, int y)
    {
        if (!isMovingSelected || originalPositions == null) {
            return;
        }

        // Calculate offset from selection center to mouse position
        int dx = x - selectionCenterX;
        int dy = y - selectionCenterY;

        // Move all selected primitives relative to their original positions
        for (Map.Entry<GraphicPrimitive, PointG> entry : 
                originalPositions.entrySet()) {
            GraphicPrimitive g = entry.getKey();
            PointG originalPos = entry.getValue();

            // Calculate new position: original position + offset from center
            int newX = originalPos.x + dx;
            int newY = originalPos.y + dy;

            // Move to new position (relative to current position)
            int currentX = g.getFirstPoint().x;
            int currentY = g.getFirstPoint().y;
            g.movePrimitive(newX - currentX, newY - currentY);
        }
    }

    /** Confirm the move operation and save undo state.
    */
    public void confirmMove()
    {
        if (!isMovingSelected) {
            return;
        }

        isMovingSelected = false;
        originalPositions = null;
        
        if (ua != null) {
            ua.saveUndoState();
        }
    }

    /** Cancel the Move command operation and restore original positions.
    */
    public void cancelMove()
    {
        if (!isMovingSelected || originalPositions == null) {
            return;
        }

        // Restore all original positions
        for (Map.Entry<GraphicPrimitive, PointG> entry : 
                originalPositions.entrySet()) {
            GraphicPrimitive g = entry.getKey();
            PointG originalPos = entry.getValue();

            // Move back to original position
            int currentX = g.getFirstPoint().x;
            int currentY = g.getFirstPoint().y;
            g.movePrimitive(originalPos.x - currentX, originalPos.y - currentY);
        }

        isMovingSelected = false;
        originalPositions = null;
    }
}