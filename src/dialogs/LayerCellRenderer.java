package dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import layers.*;

/** The class LayerCellRenderer is used in the layer list in order to 
	show the characteristics of each layer (visibility, color).

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
</pre>

	@author Davide Bucci
	@version 1.0 December 2007 - February 2014
	
	*/
public class LayerCellRenderer implements ListCellRenderer<LayerDesc> 
{
	/** Method required for the ListCellRenderer interface; it draws
		a layer element in the cell and adds its event listeners */
	public Component getListCellRendererComponent(
		final JList<? extends LayerDesc> list, 
		final LayerDesc value, final int index, final boolean isSelected, 
		final boolean cellHasFocus)
	{
		final LayerDesc layer=(LayerDesc) value;
		
		return new CellLayer(layer, list, isSelected);
		
	}
	
}
