package net.sourceforge.fidocadj.dialogs;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import net.sourceforge.fidocadj.primitives.*;

import java.util.*;

import net.sourceforge.fidocadj.graphic.*;
import net.sourceforge.fidocadj.graphic.swing.*;

/** The class CellArrow is a simple panel showing the arrow characteristics.
    To be used with ArrowCellRenderer.

    @author Davide Bucci

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

    Copyright 2009-2014 by Davide Bucci
    </pre>
*/
public class CellArrow extends JPanel
{
    private final boolean isSelected;
    private final ArrowInfo arrow;
    private final JList list;

    /** Constructor. The user should provide the list in which the element is
        used, information about the arrow style as well as the selection
        state

        @param la the arrow style to be used
        @param l the JList in which the element is used
        @param is the selection state which will be used for the background
    */
    CellArrow(ArrowInfo la,JList l, boolean is)
    {
        arrow=la;
        list=l;
        isSelected=is;
        //Box b=Box.createHorizontalBox();
        setPreferredSize(new Dimension(50,18));
    }

    /** Paint the arrow in the panel, using the current style.
        @param g the graphic context.
    */
    public void paintComponent(Graphics g)
    {
        g.setColor(isSelected ? list.getSelectionBackground():
                                list.getBackground());

        g.fillRect(0,0, getWidth(), getHeight());
        g.setColor(isSelected ? list.getSelectionForeground():
                                list.getForeground());

        g.drawLine(getWidth()/3, getHeight()/2,2*getWidth()/3, getHeight()/2);
        Arrow.drawArrow(new Graphics2DSwing(g), getWidth()/3, getHeight()/2,
            2*getWidth()/3, getHeight()/2, 10, 4, arrow.style);
    }
}