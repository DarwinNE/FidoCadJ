package fidocadj.circuit.controllers;

import java.io.*;

import fidocadj.circuit.model.DrawingModel;
import fidocadj.geom.MapCoordinates;
import fidocadj.geom.ChangeCoordinatesListener;
import fidocadj.globals.Globals;
import fidocadj.layers.StandardLayers;
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

    Copyright 2014-2023 by Davide Bucci
</pre>

    @author Davide Bucci
*/

public class ContinuosMoveActions extends ElementsEdtActions
{
    // A coordinates listener
    private ChangeCoordinatesListener coordinatesListener=null;

    private int oldx;
    private int oldy;

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
        // This is the newer code: if primEdit is different from null,
        // it will be drawn in the paintComponent event
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
}