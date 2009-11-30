package dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import primitives.*;

/** The class CellArrow is a simple panel showing the arrow characteristics. 
	To be used with ArrowCellRenderer

	@author Davide Bucci
	@version 1.0 December 2009
	
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