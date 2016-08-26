package net.sourceforge.fidocadj.dialogs;


// TODO: remove un-useful imports
import net.sourceforge.fidocadj.circuit.CircuitPanel;
import net.sourceforge.fidocadj.circuit.controllers.EditorActions;
import net.sourceforge.fidocadj.circuit.controllers.ParserActions;
import net.sourceforge.fidocadj.circuit.controllers.SelectionActions;
import net.sourceforge.fidocadj.circuit.model.DrawingModel;
import net.sourceforge.fidocadj.export.ExportGraphic;
import net.sourceforge.fidocadj.geom.DrawingSize;
import net.sourceforge.fidocadj.geom.MapCoordinates;
import net.sourceforge.fidocadj.globals.Globals;
import net.sourceforge.fidocadj.globals.LibUtils;
import net.sourceforge.fidocadj.layers.LayerDesc;
import net.sourceforge.fidocadj.primitives.GraphicPrimitive;
import net.sourceforge.fidocadj.primitives.MacroDesc;
import net.sourceforge.fidocadj.primitives.PrimitiveMacro;
import net.sourceforge.fidocadj.dialogs.mindimdialog.MinimumSizeDialog;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;

import javax.swing.*;

/** The class OriginCircuitPanel extends the CircuitPanel class by adding
    coordinate axis which can be moved.

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

    Copyright 2012-2015 Phylum2, Davide Bucci
    </pre>
    @author Phylum2, Davide Bucci

*/
public class OriginCircuitPanel extends CircuitPanel
{
    final float dash1[] = {2.0f};
    final BasicStroke dashed = new BasicStroke(1.0f,
                            BasicStroke.CAP_BUTT,
                            BasicStroke.JOIN_MITER,
                            1.0f, dash1, 1.0f);

    // x and y coordinates of the origin in pixel.
    private int dx = 20,dy = 20;

    // x and y coordinates of the origin in logical units.
    // TODO: improve data encapsulation (these should be private).
    public int xl=5, yl=5;

    /** Get the x coordinate of the origin in pixels.
        @return the x coordinate of the origin in pixel.
    */
    public int getDx()
    {
        return dx;
    }

    /** Get the y coordinate of the origin in pixels.
        @return the y coordinate of the origin in pixel.
    */
    public int getDy()
    {
        return dy;
    }

    /** Put the origin in the 10,10 logical coordinates.
    */
    public void resetOrigin()
    {
        xl=getMapCoordinates().unmapXsnap(10);
        yl=getMapCoordinates().unmapYsnap(10);

        dx=getMapCoordinates().mapXi(xl,yl,false);
        dy=getMapCoordinates().mapYi(xl,yl,false);
    }

    /** Set the new x coordinate of the origin.
        @param dx the new x coordinates in pixels.
    */
    public void setDx(int dx)
    {
        if (dx < 0 || dx>getWidth())
            return;
        this.dx = dx;
    }

    /** Set the new y coordinate of the origin.
        @param dy the new y coordinates in pixels.
    */
    public void setDy(int dy)
    {
        if (dy<0 || dy>getHeight())
            return;
        this.dy = dy;
    }

    /** Constructor.
        @param isEditable true if the panel should be editable.
    */
    public OriginCircuitPanel(boolean isEditable)
    {
        super(isEditable);
    }

    /** Show a red cross with dashed line and write "origin" near to the
        center. This should suggest to the user that it is worth clicking
        in the origin panel (some users reported they did not see the
        cross alone in a first instance).
    */
    @Override
    public void paintComponent (Graphics g)
    {
        super.paintComponent(g);
        Color c = g.getColor();
        Graphics2D g2 = (Graphics2D) g;
        g.setColor(Color.red);
        Stroke t=g2.getStroke();
        g2.setStroke(dashed);
        // Show the origin of axes (red cross)
        g.drawLine(dx, 0, dx, getHeight()); // y
        g.drawLine(0, dy, getWidth(), dy); // x

        Font f=new Font("Helvetica",0,12);
        FontMetrics fm = g.getFontMetrics(f);
        int h = fm.getAscent();
        int th = h+fm.getDescent();

        g.drawString(Globals.messages.getString("Origin"),
            dx+5, dy+th+2);
        g.setColor(c);
        g2.setStroke(t);
    }
}
