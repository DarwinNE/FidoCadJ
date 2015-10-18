package net.sourceforge.fidocadj.dialogs.print;

import javax.swing.*;
import java.awt.*;

import net.sourceforge.fidocadj.circuit.*;
import net.sourceforge.fidocadj.circuit.controllers.ParserActions;
import net.sourceforge.fidocadj.globals.*;
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
    PrintTools pt;

    /** Constructor.
        @param isEditable true if the panel should be editable.
    */
    public PrintPreview(boolean isEditable)
    {
        super(isEditable);
    }

    /** Show the margins.
        @param g the graphic context where to draw.
    */
    @Override
    public void paintComponent (Graphics g)
    {
        super.paintComponent(g);

        Color c = g.getColor();
        Graphics2D g2 = (Graphics2D) g;
        g.setColor(Color.red);
        Stroke t=g2.getStroke();
        g.drawLine(0, 0, getWidth(), getHeight());
        g.setColor(c);
        g2.setStroke(t);
    }
}