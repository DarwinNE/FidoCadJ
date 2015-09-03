package net.sourceforge.fidocadj.circuit.controllers;

/**  PrimitivesParInterface specifies some actions useful to modify
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
    along with FidoCadJ.  If not, see <http://www.gnu.org/licenses/>.

    Copyright 2014 by Davide Bucci
</pre>
*/
public interface PrimitivesParInterface
{
    public void selectAndSetProperties(int x,int y);
    public void setPropertiesForPrimitive();
    public void showPopUpMenu(int x, int y);
    
    /** Increases or decreases the zoom by a step of 33%
        @param increase if true, increase the zoom, if false decrease
        @param x coordinate to which center the viewport (screen coordinates)
        @param y coordinate to which center the viewport (screen coordinates)
    */
    public void changeZoomByStep(boolean increase, int x, int y);
    
    
    /** Makes sure the object gets focus.
    */
    public void getFocus();
    
    /** Forces a repaint event.
    */
    public void forcesRepaint();
    
    /** Forces a repaint, specify the region to be updated.
    */
    public void forcesRepaint(int a, int b, int c, int d);
    
    /** Activate and sets an evidence rectangle which will be put on screen
        at the next redraw. All sizes are given in pixel.
        
        @param lx   the x coordinate of the left top corner
        @param ly   the y coordinate of the left top corner
        @param w    the width of the rectangle
        @param h    the height of the rectangle
    */
    public void setEvidenceRect(int lx, int ly, int w, int h);
}