package dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import primitives.*;
import globals.*;

/** The class CellArrow is a simple panel showing the dash style characteristics. 
    To be used with ArrowCellRenderer.

    @author Davide Bucci
    @version 1.0 December 2009
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

    Copyright 2009 by Davide Bucci
</pre>
*/
public class CellDash extends JPanel
{
    private boolean isSelected;
    private DashInfo dash;
    private JList list;

    
    
    CellDash(DashInfo la,JList l, boolean is)
    {
        dash=la;
        list=l;
        isSelected=is;
        Box b=Box.createHorizontalBox();
        setPreferredSize(new Dimension(50,18));
    }
    
    public void paintComponent(Graphics g)
    {
    	
    	// Show the dashing styles in a list.
    	
        g.setColor(isSelected ? list.getSelectionBackground(): 
                                list.getBackground());
                                
        g.fillRect(0,0, getWidth(), getHeight());
        g.setColor(list.getForeground());

        BasicStroke dashed = new BasicStroke(1, 
                                          BasicStroke.CAP_BUTT, 
                                          BasicStroke.JOIN_MITER, 
                                          10.0f, Globals.dash[dash.style], 0.0f);
                                         
        ((Graphics2D) g).setStroke(dashed);
        g.drawLine(getWidth()/3, getHeight()/2,2*getWidth()/3, getHeight()/2);

    }
    
    
}