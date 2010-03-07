package dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import primitives.*;

/** The class CellArrow is a simple panel showing the arrow characteristics. 
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

	Copyright 2009-2010 by Davide Bucci
</pre>
*/
public class CellArrow extends JPanel
{
	private boolean isSelected;
	private ArrowInfo arrow;
	private JList list;

	
	
	CellArrow(ArrowInfo la,JList l, boolean is)
	{
		arrow=la;
		list=l;
		isSelected=is;
		Box b=Box.createHorizontalBox();
		setPreferredSize(new Dimension(50,18));
	}
	
	public void paintComponent(Graphics g)
	{
		g.setColor(isSelected ? list.getSelectionBackground(): 
								list.getBackground());
								
		g.fillRect(0,0, getWidth(), getHeight());
		g.setColor(list.getForeground());
		g.drawLine(getWidth()/3, getHeight()/2,2*getWidth()/3, getHeight()/2);
		Arrow.drawArrow((Graphics2D)g, getWidth()/3, getHeight()/2,
			2*getWidth()/3, getHeight()/2, 10, 4, arrow.style);
		
		
		/*
		g.drawString(layer.getDescription(), 5*getHeight()/4, 
			(int)(3.8*getHeight()/5));
		*/
	}
	
	
}