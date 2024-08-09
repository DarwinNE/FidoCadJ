package fidocadj.dialogs;

import java.awt.*;
import javax.swing.*;

import fidocadj.layers.LayerDesc;


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
    along with FidoCadJ. If not,
    @see <a href=http://www.gnu.org/licenses/>http://www.gnu.org/licenses/</a>.
    </pre>

    @author Davide Bucci
    @version December 2007 - 2023
*/
public class LayerCellRenderer implements ListCellRenderer<LayerDesc>
{
    /** Method required for the ListCellRenderer interface; it draws
        a layer element in the cell and adds its event listeners.
        @param list the list of elements.
        @param value the current layer description.
        @param index the index of the current element in the list.
        @param isSelected true if the element is selected.
        @param cellHasFocus true if the element has focus.
        @return the created component.
    */
    @Override public Component getListCellRendererComponent(
        final JList<? extends LayerDesc> list,
        final LayerDesc value, final int index, final boolean isSelected,
        final boolean cellHasFocus)
    {
        final LayerDesc layer=(LayerDesc) value;

        return new CellLayer(layer, list, isSelected);
    }
}
