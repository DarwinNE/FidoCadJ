package fidocadj.dialogs.controls;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.AbstractCellEditor;
import javax.swing.ComboBoxEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fidocadj.FidoFrame;
import fidocadj.circuit.CircuitPanel;
import fidocadj.dialogs.DialogEditLayer;
import fidocadj.graphic.swing.ColorSwing;
import fidocadj.layers.LayerDesc;

/**
 * LayerEditor.java
 *
 * This class defines a custom editor for the layers in a JComboBox within
 * FidoCadJ.
 * It allows for editing the properties of a layer, such as color and
 * visibility, directly from the UI.
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
public class LayerComboBoxEditor extends AbstractCellEditor implements
                                                            ComboBoxEditor
{
    private JPanel panel;
    private JLabel colorLabel;
    private JLabel visibilityLabel;
    private JLabel nameLabel;
    private LayerDesc currentLayer;
    private Icon visibleIcon;
    private Icon invisibleIcon;
    private FidoFrame fidoFrame;
    private CircuitPanel circuitPanel;
    private final int ICON_SIZE = 20;

    private ArrayList<ActionListener> actionListeners = new ArrayList<>();

    /**
     * Constructs a LayerEditor associated with the provided ..
     * JComboBox and FidoFrame.
     *
     * @param comboBox the JComboBox that this editor is associated with.
     * @param fidoFrame the FidoFrame instance to which this editor is linked.
     */
    public LayerComboBoxEditor(JComboBox comboBox, FidoFrame fidoFrame) {
        this.fidoFrame = fidoFrame;
        this.circuitPanel = this.fidoFrame.getCircuitPanel();
        panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 4));
        colorLabel = new JLabel();
        visibilityLabel = new JLabel();
        nameLabel = new JLabel();

        visibleIcon = new ImageIcon(
                getClass().getResource("/icons/layer-on.png"));

        invisibleIcon = new ImageIcon(
                getClass().getResource("/icons/layer-off.png"));

        colorLabel.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        visibilityLabel.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        panel.add(colorLabel);
        panel.add(visibilityLabel);
        panel.add(nameLabel);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                comboBox.showPopup();
            }
        });

        visibilityLabel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                currentLayer.setVisible(!currentLayer.isVisible());
                visibilityLabel.setIcon(
                        currentLayer.isVisible() ? visibleIcon:invisibleIcon);

                circuitPanel.getDrawingModel().setChanged(true);
                fidoFrame.repaint();
            }
        });

        colorLabel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                DialogEditLayer del = new DialogEditLayer(null, currentLayer);
                del.setVisible(true);
                if (del.getActive()) {
                    del.acceptLayer();
                    ColorSwing c = (ColorSwing) currentLayer.getColor();
                    colorLabel.setBackground(c.getColorSwing());
                    circuitPanel.getDrawingModel().setChanged(true);
                    fidoFrame.repaint();
                }
            }
        });
    }

    /**
     * Returns the component used to edit the value in the JComboBox.
     *
     * @return the JPanel containing the layer editing controls.
     */
    @Override
    public Component getEditorComponent()
    {
        return panel;
    }

    /**
     * Returns the current layer being edited.
     *
     * @return the LayerDesc object representing the current layer.
     */
    @Override
    public Object getItem()
    {
        return currentLayer;
    }

    /**
     * Sets the current layer to be edited.
     *
     * @param item the LayerDesc object representing the layer to be edited.
     */
    @Override
    public void setItem(Object item)
    {
        currentLayer = (LayerDesc) item;

        colorLabel.setOpaque(true);
        ColorSwing color=(ColorSwing) currentLayer.getColor();
        colorLabel.setBackground(color.getColorSwing());
        colorLabel.setPreferredSize(new Dimension(25, ICON_SIZE));

        visibilityLabel.setIcon(
                currentLayer.isVisible() ? visibleIcon : invisibleIcon);
        visibilityLabel.setPreferredSize(new Dimension(ICON_SIZE, ICON_SIZE));

        nameLabel.setText(currentLayer.getDescription());
    }

    /**
     * Adds an ActionListener to the editor.
     *
     * @param l the ActionListener to be added.
     */
    @Override
    public void addActionListener(ActionListener l)
    {
        actionListeners.add(l);
    }

    /**
     * Removes an ActionListener from the editor.
     *
     * @param l the ActionListener to be removed.
     */
    @Override
    public void removeActionListener(ActionListener l)
    {
        actionListeners.remove(l);
    }

    /**
     * Selects all the text in the editor component.
     * This method is a no-op in this implementation.
     */
    @Override
    public void selectAll()
    {
        // No implementation needed for this editor
    }

    /**
     * Returns the current value of the editor, which is the current layer.
     *
     * @return the LayerDesc object representing the current layer.
     */
    @Override
    public Object getCellEditorValue()
    {
        return currentLayer;
    }
}
