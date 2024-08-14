package fidocadj.circuit.controllers;

import fidocadj.graphic.ColorInterface;
import java.awt.Color;

/** PrimitivesParInterface specifies some actions useful to modify
    characteristics of primitives.
    They are usually provided by the editor component.

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
*/
public interface PrimitivesParInterface
{
    /** Selects the closest object to the given point (in logical coordinates)
        and pops up a dialog for the editing of its Param_opt.
        @param x the x logical coordinate of the point used for the selection
        @param y the y logical coordinate of the point used for the selection
    */
    void selectAndSetProperties(int x,int y);

    /** Shows a dialog which allows the user modify the parameters of a given
        primitive. If more than one primitive is selected, modify only the
        layer of all selected primitives.
    */
    void setPropertiesForPrimitive();

    /** Show a popup menu representing the actions that can be done on the
        selected context.
        @param x the x coordinate where the popup menu should be put
        @param y the y coordinate where the popup menu should be put
    */
    void showPopUpMenu(int x, int y);

    /** Increases or decreases the zoom by a step of 33%
        @param increase if true, increase the zoom, if false decrease
        @param x coordinate to which center the viewport (screen coordinates)
        @param y coordinate to which center the viewport (screen coordinates)
        @param rate amount the zoom must be multiplied. Must be >1.0
    */
    void changeZoomByStep(boolean increase, int x, int y, double rate);

    /** Makes sure the object gets focus.
    */
    void getFocus();

    /** Forces a repaint event.
    */
    void forcesRepaint();

    /** Forces a repaint.
        @param x the x leftmost corner of the dirty region to repaint.
        @param y the y leftmost corner of the dirty region to repaint.
        @param width the width of the dirty region.
        @param height the height of the dirty region.
    */
    void forcesRepaint(int x, int y, int width, int height);

    /** Activate and sets an evidence rectangle which will be put on screen
        at the next redraw. All sizes are given in pixel.
        @param lx   the x coordinate of the left top corner
        @param ly   the y coordinate of the left top corner
        @param w    the width of the rectangle
        @param h    the height of the rectangle
    */
    void setEvidenceRect(int lx, int ly, int w, int h);

    /** Set the current color for selection box
     
        @param color the color
     */
    void setSelectionColor(Color color);
}