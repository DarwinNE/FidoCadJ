
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

/** The class CellLayer is a simple panel showing the color, the visibility
	and the description of each layer. To be used with LayerCellRenderer

	@author Davide Bucci
	@version 1.0 December 2007
	
	*/
public class CellLayer extends JPanel
{
	private JList list;
	private boolean isSelected;
	private LayerDesc layer;
	
	
	
	CellLayer(LayerDesc la, JList l, boolean is)
	{
		layer=la;
		list=l;
		isSelected=is;
		Box b=Box.createHorizontalBox();
		setPreferredSize(new Dimension(150,18));
	}
	
	public void paintComponent(Graphics g)
	{
		g.setColor(isSelected ? list.getSelectionBackground(): 
								list.getBackground());
		g.fillRect(0,0, getWidth(), getHeight());
		g.setColor(layer.getColor());
		g.fillRect(2,2, getHeight(), getHeight()-4);
		
		if(layer.getVisible()) {
			g.setColor(isSelected ? list.getSelectionForeground(): 
								list.getForeground());	
		} else {
			g.setColor(SystemColor.textInactiveText);
		}
								
		g.drawString(layer.getDescription(), 5*getHeight()/4, 
			(int)(3.8*getHeight()/5));
	}
	
	
}