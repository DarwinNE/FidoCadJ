package dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import primitives.*;
import globals.*;

/** The class CellArrow is a simple panel showing the arrow characteristics. 
	To be used with ArrowCellRenderer

	@author Davide Bucci
	@version 1.0 December 2009
	
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