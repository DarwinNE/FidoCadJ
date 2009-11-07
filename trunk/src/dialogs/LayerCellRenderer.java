package dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import layers.*;

/** The class LayerCellRenderer is used in the layer list in order to 
	show the characteristics of each layer (visibility, color).

	@author Davide Bucci
	@version 1.0 December 2007
	
	*/
public class LayerCellRenderer implements ListCellRenderer 
{
	/** Method required for the ListCellRenderer interface; it draws
		a layer element in the cell and adds its event listeners */
	public Component getListCellRendererComponent(final JList list, 
		final Object value, final int index, final boolean isSelected, 
		final boolean cellHasFocus)
	{
		final LayerDesc layer=(LayerDesc) value;
		
		return new CellLayer(layer, list, isSelected);
		
	}
	
}
