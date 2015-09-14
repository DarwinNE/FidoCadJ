package net.sourceforge.fidocadj.dialogs;


import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.util.*;

import javax.swing.event.*;
import javax.swing.colorchooser.*;

import net.sourceforge.fidocadj.globals.*;
import net.sourceforge.fidocadj.layers.*;
import net.sourceforge.fidocadj.graphic.*;
import net.sourceforge.fidocadj.graphic.swing.*;


/** The class dialogEditLayer allows to choose the style, visibility and
    description of the current layer.

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

    Copyright 2007-2010 by Davide Bucci
</pre>


    @author Davide Bucci
*/

public class DialogEditLayer extends JDialog implements ComponentListener
{
    // Minimum size of the dialog window

    private static final int MIN_WIDTH=500;
    private static final int MIN_HEIGHT=450;
    static final int ALPHA_MIN = 0;
    static final int ALPHA_MAX = 100;

    private JButton color;
    private final JColorChooser tcc;
    private final JCheckBox visibility;
    private final JTextField description;
    private final JSlider opacity;
    private boolean active;             // true if the user selected ok
    private final LayerDesc ll;

    public void componentResized(ComponentEvent e)
    {
        int width = getWidth();
        int height = getHeight();

        boolean resize = false;
        if (width < MIN_WIDTH) {
            resize = true;
            width = MIN_WIDTH;
        }
        if (height < MIN_HEIGHT) {
            resize = true;
            height = MIN_HEIGHT;
        }
        if (resize) {
            setSize(width, height);
        }
    }
    public void componentMoved(ComponentEvent e)
    {
        // Nothing to do
    }
    public void componentShown(ComponentEvent e)
    {
        // Nothing to do
    }
    public void componentHidden(ComponentEvent e)
    {
        // Nothing to do
    }

    /** Standard constructor.
        @param parent the dialog parent
        @param l a LayerDesc containing the layer's attributes
    */
    public DialogEditLayer (JFrame parent, LayerDesc l)
    {
        super(parent, Globals.messages.getString("Layer_options")+
            l.getDescription(), true);

        ll = l;
        addComponentListener(this);
        active=false;

        Container contentPane=getContentPane();

        GridBagLayout bgl=new GridBagLayout();
        GridBagConstraints constraints=new GridBagConstraints();
        contentPane.setLayout(bgl);
        constraints.insets.right=30;
        ColorSwing c = (ColorSwing) l.getColor();
        tcc = new JColorChooser(c.getColorSwing());
        constraints = DialogUtil.createConst(0,0,3,1,100,100,
            GridBagConstraints.CENTER, GridBagConstraints.NONE,
            new Insets(20,20,6,20));

        contentPane.add(tcc, constraints);

        JLabel descrLabel=new JLabel(Globals.messages.getString("Description"));
        constraints = DialogUtil.createConst(1,1,1,1,100,100,
            GridBagConstraints.EAST, GridBagConstraints.NONE,
            new Insets(0,20,0,0));

        contentPane.add(descrLabel, constraints);

        description=new JTextField();
        description.setText(l.getDescription());
        constraints = DialogUtil.createConst(2,1,1,1,100,0,
            GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
            new Insets(0,0,0,120));

        contentPane.add(description, constraints);

        JLabel opacityLbl=new JLabel(Globals.messages.getString("Opacity"));
        constraints = DialogUtil.createConst(1,3,1,1,100,0,
            GridBagConstraints.EAST, GridBagConstraints.NONE,
            new Insets(0,0,0,20));

        contentPane.add(opacityLbl, constraints);


        opacity = new JSlider(JSlider.HORIZONTAL,
                                      ALPHA_MIN, ALPHA_MAX,
                                      Math.round(l.getAlpha()*100.0f));

        //Turn on labels at major tick marks.
        opacity.setMajorTickSpacing(20);
        opacity.setMinorTickSpacing(1);
        opacity.setPaintTicks(true);
        opacity.setPaintLabels(true);

        constraints = DialogUtil.createConst(2,3,1,1,100,0,
            GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
            new Insets(0,0,0,120));

        contentPane.add(opacity, constraints);

        visibility=new JCheckBox(Globals.messages.getString("IsVisible"));
        visibility.setSelected(l.getVisible());

        constraints = DialogUtil.createConst(2,4,1,1,100,0,
            GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
            new Insets(0,0,20,120));

        contentPane.add(visibility, constraints);

        JButton ok = new JButton(Globals.messages.getString("Ok_btn"));
        JButton cancel = new JButton(Globals.messages.getString("Cancel_btn"));

        // Put the OK and Cancel buttons and make them active.
        Box b=Box.createHorizontalBox();
        b.add(Box.createHorizontalGlue());
        ok.setPreferredSize(cancel.getPreferredSize());

        if (Globals.okCancelWinOrder) {
            b.add(ok);
            b.add(Box.createHorizontalStrut(12));
            b.add(cancel);

        } else {
            b.add(cancel);
            b.add(Box.createHorizontalStrut(12));
            b.add(ok);
        }

        constraints = DialogUtil.createConst(0,5,3,1,100,0,
            GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
            new Insets(0,20,20,20));

        contentPane.add(b, constraints);

        ok.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                active=true;
                setVisible(false);
            }
        });
        getRootPane().setDefaultButton(ok);

        cancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                setVisible(false);
            }
        });


        // Here is an action in which the dialog is closed

        AbstractAction cancelAction = new AbstractAction ()
        {
            public void actionPerformed (ActionEvent e)
            {
                setVisible(false);
            }
        };
        DialogUtil.addCancelEscape (this, cancelAction);

        pack();
        DialogUtil.center(this);
    }

    /** Get the layer description as specified in the layer edit dialog
    */
    public void acceptLayer()
    {
        // It is important that here we use exactly the same layer which has
        // been specified at the beginning. In this way, every reference to
        // that layer will be modified.

        ll.setVisible(visibility.isSelected());
        ll.setDescription(description.getText());
        ll.setColor(new ColorSwing(tcc.getColor()));
        ll.setAlpha(opacity.getValue()/100.0f);
        ll.setModified(true);
    }

    /** Determine if the user selected the Ok button.
        @return true if the user has quit the dialog box with the Ok button.
    */

    public boolean getActive()
    {
        return active;
    }
}