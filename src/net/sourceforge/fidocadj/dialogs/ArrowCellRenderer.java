package net.sourceforge.fidocadj.dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import primitives.*;

/** The class ArrowCellRenderer is used in the arrow list.

    @author Davide Bucci
    
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
    
*/
public class ArrowCellRenderer implements ListCellRenderer 
{
    /** Method required for the ListCellRenderer interface; it draws
        a layer element in the cell and adds its event listeners */
    public Component getListCellRendererComponent(final JList list, 
        final Object value, final int index, final boolean isSelected, 
        final boolean cellHasFocus)
    {
        final ArrowInfo arrow=(ArrowInfo) value;
        
        return new CellArrow(arrow, list, isSelected);
        
    }
    
}
