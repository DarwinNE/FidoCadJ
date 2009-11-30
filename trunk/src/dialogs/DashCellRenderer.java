package dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import primitives.*;

/** The class ArrowCellRenderer is used in the arrow list.

	@author Davide Bucci
	@version 1.0 December 2009
	
	*/
public class DashCellRenderer implements ListCellRenderer 
{
	/** Method required for the ListCellRenderer interface; it draws
		a layer element in the cell and adds its event listeners */
	public Component getListCellRendererComponent(final JList list, 
		final Object value, final int index, final boolean isSelected, 
		final boolean cellHasFocus)
	{
		final DashInfo arrow=(DashInfo) value;
		
		return new CellDash(arrow, list, isSelected);
		
	}
	
}
