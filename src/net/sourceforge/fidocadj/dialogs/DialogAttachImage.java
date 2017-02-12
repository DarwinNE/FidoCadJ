package net.sourceforge.fidocadj.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import net.sourceforge.fidocadj.globals.*;
import net.sourceforge.fidocadj.dialogs.mindimdialog.MinimumSizeDialog;


/** The class DialogAttachImage allows to determine which image has to be
    attached and shown as a background (for retracing/vectorization purposes).

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

    Copyright 2017 by Davide Bucci
    </pre>

    @author Davide Bucci
*/

public class DialogAttachImage extends MinimumSizeDialog
{
    /** Standard constructor.
        @param parent the dialog parent
    */
    public DialogAttachImage(JFrame parent)
    {
        super(500, 450, parent, Globals.messages.getString("Attach_image_t"),
            true);
        int ygrid=0;
        // Obtain the current content pane and create the grid layout manager
        // which will be used for putting the elements of the interface.
        GridBagLayout bgl=new GridBagLayout();
        GridBagConstraints constraints;
        Container contentPane=getContentPane();
        contentPane.setLayout(bgl);

        JLabel lblfilename=
            new JLabel(Globals.messages.getString("Image_file_attach"));

        constraints = DialogUtil.createConst(0,ygrid,1,1,100,100,
            GridBagConstraints.EAST, GridBagConstraints.NONE,
            new Insets(6,6,6,6));

        contentPane.add(lblfilename, constraints);

        JTextField filename=new JTextField(10);
        filename.setText("");

        constraints = DialogUtil.createConst(1,ygrid++,1,1,100,100,
            GridBagConstraints.EAST, GridBagConstraints.NONE,
            new Insets(6,6,6,6));

        contentPane.add(filename, constraints);

        JLabel lblresolution=
            new JLabel(Globals.messages.getString("Image_resolution"));

        constraints = DialogUtil.createConst(0,ygrid,1,1,100,100,
            GridBagConstraints.EAST, GridBagConstraints.NONE,
            new Insets(6,6,6,6));

        contentPane.add(lblresolution, constraints);

        JTextField resolution=new JTextField(10);
        filename.setText("");

        constraints = DialogUtil.createConst(1,ygrid++,1,1,100,100,
            GridBagConstraints.EAST, GridBagConstraints.NONE,
            new Insets(6,6,6,6));

        contentPane.add(resolution, constraints);
    
        // Put the OK and Cancel buttons and make them active.
        JButton ok=new JButton(Globals.messages.getString("Ok_btn"));
        JButton cancel=new JButton(Globals.messages.getString("Cancel_btn"));
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
        constraints = DialogUtil.createConst(1,ygrid++,1,1,0,0,
            GridBagConstraints.EAST, GridBagConstraints.NONE,
            new Insets(12,40,0,0));
        contentPane.add(b, constraints);            // Add OK/cancel buttons
                ok.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
            }
        });
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
        getRootPane().setDefaultButton(ok);
    }
}