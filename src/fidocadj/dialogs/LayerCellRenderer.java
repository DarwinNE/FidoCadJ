package fidocadj.dialogs;

import javax.swing.*;
import java.awt.*;

import fidocadj.layers.LayerDesc;
import fidocadj.graphic.swing.ColorSwing;

/** 
 * LayerRenderer.java
 * 
 * This class defines a custom renderer for displaying layers in a JList within FidoCadJ.
 * It handles the visual representation of each layer, including its color, visibility, and name.
 *
 * <pre>
 * This file is part of FidoCadJ.
 *
 * FidoCadJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FidoCadJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FidoCadJ. If not,
 * @see<a href=http://www.gnu.org/licenses/>http://www.gnu.org/licenses/</a>.
 *
 * Copyright 2015-2023 by Davide Bucci
 * </pre>
 *
 * @author Manuel Finessi
 */
public class LayerCellRenderer extends JPanel implements 
                                                ListCellRenderer<LayerDesc> 
{
    private JLabel colorLabel;
    private JLabel visibilityLabel;
    private JLabel nameLabel;
    private Icon visibleIcon;
    private Icon invisibleIcon;
    private final int ICON_SIZE = 20;

    /**
     * Constructs a LayerRenderer with control over the visibility of layers.
     *
     * @param visibilityCtrl a boolean indicating whether visibility..
     *                       control is enabled.
     */
    public LayerCellRenderer() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        colorLabel = new JLabel();
        visibilityLabel = new JLabel();
        nameLabel = new JLabel();
        
        visibleIcon = new ImageIcon(
                getClass().getResource("/icons/layer-on.png"));
        
        invisibleIcon = new ImageIcon(
                getClass().getResource("/icons/layer-off.png"));

        add(colorLabel);
        add(visibilityLabel);
        add(nameLabel);
    }

    /**
     * Configures the renderer component for each cell in the JList.
     *
     * @param list the JList we're painting.
     * @param layer the layer to be rendered.
     * @param index the index of the cell being drawn.
     * @param isSelected true if the specified cell is currently selected.
     * @param cellHasFocus true if the cell has focus.
     * @return the component used to render the value.
     */
    @Override
    public Component getListCellRendererComponent(
            JList<? extends LayerDesc> list, LayerDesc layer, 
            int index, boolean isSelected, boolean cellHasFocus) 
    {
        colorLabel.setOpaque(true);
        
        ColorSwing color=(ColorSwing) layer.getColor();
        
        colorLabel.setBackground(color.getColorSwing());
        colorLabel.setPreferredSize(new Dimension(25, ICON_SIZE));

        visibilityLabel.setIcon(
                layer.getVisible() ? visibleIcon : invisibleIcon);
        
        visibilityLabel.setPreferredSize(new Dimension(ICON_SIZE, ICON_SIZE));

        nameLabel.setText(layer.getDescription());
        nameLabel.setForeground(Color.BLACK);
        
        if (!layer.getVisible()) 
            nameLabel.setForeground(SystemColor.textInactiveText);

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        return this;
    }
}
