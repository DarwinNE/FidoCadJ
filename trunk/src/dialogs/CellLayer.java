package dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import layers.*;

/** The class CellLayer is a simple panel showing the color, the visibility
    and the description of each layer. To be used with LayerCellRenderer

    @author Davide Bucci
    @version 1.0 December 2007
    
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

    Copyright 2007 by Davide Bucci
</pre>
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
    
    /** Here we draw the layer description. A coloured box followed by the
    	name of the layer. We need to take care if the element is selected
    	or not. In this case, we change accordingly the background of the part
    	where we are writing the layer name.
    
    */
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