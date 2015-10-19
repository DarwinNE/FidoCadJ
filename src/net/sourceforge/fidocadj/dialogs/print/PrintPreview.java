package net.sourceforge.fidocadj.dialogs.print;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;

import java.awt.print.*;
import java.awt.*;

import net.sourceforge.fidocadj.circuit.*;
import net.sourceforge.fidocadj.circuit.controllers.ParserActions;
import net.sourceforge.fidocadj.globals.*;
import net.sourceforge.fidocadj.geom.*;
import net.sourceforge.fidocadj.*;

/** Shows a print preview.

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
public class PrintPreview extends CircuitPanel
{
    private PageFormat pageDescription;
    private double topMargin;
    private double bottomMargin;
    private double leftMargin;
    private double rightMargin;

    /** Constructor.
        @param isEditable true if the panel should be editable.
        @param p the PageFormat description.
    */
    public PrintPreview(boolean isEditable, PageFormat p)
    {
        super(isEditable);
        pageDescription=p;
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    }

    /** Set the size of the margins, in centimeters. The orientation of those
        margins should correspond to the page in the portrait orientation.
        @param tm top margin.
        @param bm bottom margin.
        @param lm left margin.
        @param rm right margin.
    */
    public void setMargins(double tm, double bm, double lm, double rm)
    {
        topMargin=tm;
        bottomMargin=bm;
        leftMargin=lm;
        rightMargin=rm;
    }

    /** Show the margins.
        @param g the graphic context where to draw.
    */
    @Override
    public void paintComponent (Graphics g)
    {
        getDrawingModel().setChanged(true); // Needed?
        Color c = g.getColor();
        Graphics2D g2 = (Graphics2D) g;
        double baseline=getWidth()*0.6;     // TODO: correct getHeight small
        double ratio=pageDescription.getHeight()/pageDescription.getWidth();

        MapCoordinates mc=getMapCoordinates();
        mc.setXCenter((int)Math.round(getWidth()/2.0-baseline/2.0));
        mc.setYCenter((int)Math.round(getHeight()/2.0-baseline*ratio/2.0));

        super.paintComponent(g);

        g2.setColor(Color.red);

        g2.drawRect((int)Math.round(getWidth()/2.0-baseline/2.0),
            (int)Math.round(getHeight()/2.0-baseline*ratio/2.0),
            (int)Math.round(baseline),
            (int)Math.round(baseline*ratio));

        g.setColor(c);
    }
}