package fidocadj.dialogs;

// Editor personalizzato per JComboBox per gestire il clic sull'icona di visibilità

import fidocadj.FidoFrame;
import fidocadj.circuit.CircuitPanel;
import fidocadj.graphic.swing.ColorSwing;
import fidocadj.layers.LayerDesc;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class LayerEditor extends AbstractCellEditor implements ComboBoxEditor {
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

    public LayerEditor(JComboBox comboBox, FidoFrame fidoFrame) {
        this.fidoFrame = fidoFrame;
        this.circuitPanel = this.fidoFrame.cc;
        panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 4));
        colorLabel = new JLabel();
        visibilityLabel = new JLabel();
        nameLabel = new JLabel();
        
        visibleIcon = new ImageIcon("./icons/layer-on.png");
        invisibleIcon = new ImageIcon("./icons/layer-off.png");

        visibilityLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); 
        
        panel.add(colorLabel);
        panel.add(visibilityLabel);
        panel.add(nameLabel);
        
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                comboBox.showPopup();
            }
        });
        
        visibilityLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                currentLayer.setVisible(!currentLayer.getVisible());
                visibilityLabel.setIcon(currentLayer.getVisible() ? visibleIcon : invisibleIcon);
                circuitPanel.getDrawingModel().setChanged(true);
                fidoFrame.repaint();
            }
        });
    }

    @Override
    public Component getEditorComponent() {
        return panel;
    }

    @Override
    public Object getItem() {
        return currentLayer;
    }

    @Override
    public void setItem(Object item) {
        currentLayer = (LayerDesc) item;

        colorLabel.setOpaque(true);
        ColorSwing color=(ColorSwing) currentLayer.getColor();
        colorLabel.setBackground(color.getColorSwing());
        colorLabel.setPreferredSize(new Dimension(25, ICON_SIZE));

        visibilityLabel.setIcon(currentLayer.getVisible() ? visibleIcon : invisibleIcon);
        visibilityLabel.setPreferredSize(new Dimension(ICON_SIZE, ICON_SIZE));

        nameLabel.setText(currentLayer.getDescription());
    }

    @Override
    public void addActionListener(ActionListener l) {
        actionListeners.add(l);
    }

    @Override
    public void removeActionListener(ActionListener l) {
        actionListeners.remove(l);
    }

    @Override
    public void selectAll() {
        // Non necessario per questo esempio
    }

    @Override
    public Object getCellEditorValue() {
        return currentLayer;
    }
}