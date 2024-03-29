package net.sourceforge.fidocadj.dialogs;

import java.io.*;
import java.awt.*;
import javax.swing.*;

import net.sourceforge.fidocadj.graphic.swing.ColorSwing;
import net.sourceforge.fidocadj.layers.LayerDesc;

/** The class CellLayer is a simple panel showing the color, the visibility
    and the description of each layer. To be used with LayerCellRenderer

    @author Davide Bucci

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

    Copyright 2007-2023 by Davide Bucci
*/
public class CellLayer extends JPanel
{
    private final JList list;
    private final boolean isSelected;
    private final LayerDesc layer;

    /** Constructor. The user should provide the list in which the element is
        used, information about the layer as well as the selection
        state

        @param la the layer to be used
        @param l the JList in which the element is used
        @param is the selection state which will be used for the background
    */
    CellLayer(LayerDesc la, JList l, boolean is)
    {
        layer=la;
        list=l;
        isSelected=is;
        setPreferredSize(new Dimension(150,18));
    }

    /** By implementing writeObject method, 
    // we can prevent 
    // subclass from serialization 
    */
    private void writeObject(ObjectOutputStream out) throws IOException 
    { 
        throw new NotSerializableException(); 
    } 
      
    /* By implementing readObject method, 
    // we can prevent 
    // subclass from de-serialization 
    */
    private void readObject(ObjectInputStream in) throws IOException 
    { 
        throw new NotSerializableException(); 
    }

    /** Here we draw the layer description. A coloured box followed by the
        name of the layer. We need to take care if the element is selected
        or not. In this case, we change accordingly the background of the part
        where we are writing the layer name.
        @param g the graphic context on which to draw.
    */
    @Override public void paintComponent(Graphics g)
    {
        g.setColor(isSelected ? list.getSelectionBackground():
                                list.getBackground());
        g.fillRect(0,0, getWidth(), getHeight());
        ColorSwing c=(ColorSwing) layer.getColor();
        g.setColor(c.getColorSwing());
        g.fillRect(2,2, getHeight(), getHeight()-4);

        if(layer.getVisible()) {
            if (isSelected) {
                g.setColor(list.getSelectionForeground());
            }
        } else {
            g.setColor(SystemColor.textInactiveText);
        }
        Graphics2D g2=(Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        //g.setFont(new Font("Helvetica", Font.PLAIN, 14));
        g.drawString(layer.getDescription(), 6*getHeight()/4,
            (int)(3.8*getHeight()/5));
    }
}