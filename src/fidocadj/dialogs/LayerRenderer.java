package fidocadj.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import fidocadj.layers.LayerDesc;
import fidocadj.graphic.swing.ColorSwing;


public class LayerRenderer extends JPanel implements ListCellRenderer<LayerDesc> {
    private JLabel colorLabel;
    private JLabel visibilityLabel;
    private JLabel nameLabel;
    private Icon visibleIcon;
    private Icon invisibleIcon;
    private final int ICON_SIZE = 20;

    public LayerRenderer(boolean visibilityCtrl) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        colorLabel = new JLabel();
        visibilityLabel = new JLabel();
        nameLabel = new JLabel();
        
        visibleIcon = new ImageIcon("./icons/layer-on.png");
        invisibleIcon = new ImageIcon("./icons/layer-off.png");

        add(colorLabel);
        add(visibilityLabel);
        add(nameLabel);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends LayerDesc> list, LayerDesc layer, int index, boolean isSelected, boolean cellHasFocus) {
        colorLabel.setOpaque(true);
        
        ColorSwing color=(ColorSwing) layer.getColor();
        
        colorLabel.setBackground(color.getColorSwing());
        colorLabel.setPreferredSize(new Dimension(25, ICON_SIZE));

        visibilityLabel.setIcon(layer.getVisible() ? visibleIcon : invisibleIcon);
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

