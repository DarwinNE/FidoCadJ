package net.sourceforge.fidocadj.dialogs;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import javax.swing.*;

import java.io.*;

import javax.imageio.*;

import net.sourceforge.fidocadj.globals.*;
import net.sourceforge.fidocadj.dialogs.mindimdialog.MinimumSizeDialog;

/** Choose file format, size and options of the graphic exporting.
    The class dialogPrint implements a modal dialog to select printing options.

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

    Copyright 2007-2015 by Davide Bucci
</pre>

    @author Davide Bucci
*/

public class DialogPrint extends MinimumSizeDialog
{
    private final JCheckBox mirror_CB;
    private final JCheckBox fit_CB;
    private final JCheckBox bw_CB;
    private final JCheckBox landscape_CB;

    private boolean export;     // Indicates that the export should be done
    /** Standard constructor: it needs the parent frame.
        @param parent the dialog's parent
    */
    public DialogPrint (JFrame parent)
    {
        super(400,350, parent,Globals.messages.getString("Print_dlg"), true);
        addComponentListener(this);
        export=false;

        // Ensure that under MacOSX >= 10.5 Leopard, this dialog will appear
        // as a document modal sheet

        getRootPane().putClientProperty("apple.awt.documentModalSheet",
                Boolean.TRUE);

        GridBagLayout bgl=new GridBagLayout();
        GridBagConstraints constraints=new GridBagConstraints();
        Container contentPane=getContentPane();
        contentPane.setLayout(bgl);

        constraints.insets.right=30;

        JLabel empty=new JLabel("  ");
        constraints.weightx=100;
        constraints.weighty=100;
        constraints.gridx=0;
        constraints.gridy=0;
        constraints.gridwidth=1;
        constraints.gridheight=1;
        contentPane.add(empty, constraints);            // Add "   " label

        JLabel empty1=new JLabel("  ");
        constraints.weightx=100;
        constraints.weighty=100;
        constraints.gridx=4;
        constraints.gridy=0;
        constraints.gridwidth=1;
        constraints.gridheight=1;
        contentPane.add(empty1, constraints);           // Add "   " label

        JLabel lTopMargin=new JLabel(Globals.messages.getString("TopMargin"));
        constraints.anchor=GridBagConstraints.EAST;
        constraints.weightx=100;
        constraints.weighty=100;
        constraints.gridx=1;
        constraints.gridy=0;
        constraints.gridwidth=1;
        constraints.gridheight=1;
        contentPane.add(lTopMargin, constraints);           // Top margin label

        JTextField tTopMargin=new JTextField("0");
        constraints.weightx=100;
        constraints.weighty=100;
        constraints.gridx=2;
        constraints.gridy=0;
        constraints.gridwidth=1;
        constraints.gridheight=1;
        contentPane.add(tTopMargin, constraints);           // Top margin text

        JLabel lBottomMargin=new JLabel(
            Globals.messages.getString("BottomMargin"));
        constraints.anchor=GridBagConstraints.EAST;
        constraints.weightx=100;
        constraints.weighty=100;
        constraints.gridx=1;
        constraints.gridy=1;
        constraints.gridwidth=1;
        constraints.gridheight=1;
        contentPane.add(lBottomMargin, constraints);    // Bottom margin label

        JTextField tBottomMargin=new JTextField("0");
        constraints.weightx=100;
        constraints.weighty=100;
        constraints.gridx=2;
        constraints.gridy=1;
        constraints.gridwidth=1;
        constraints.gridheight=1;
        contentPane.add(tBottomMargin, constraints);    // Bottom margin text

        JLabel lLeftMargin=new JLabel(Globals.messages.getString("LeftMargin"));
        constraints.anchor=GridBagConstraints.EAST;
        constraints.weightx=100;
        constraints.weighty=100;
        constraints.gridx=1;
        constraints.gridy=2;
        constraints.gridwidth=1;
        constraints.gridheight=1;
        contentPane.add(lLeftMargin, constraints);    // Left margin label

        JTextField tLeftMargin=new JTextField("0");
        constraints.weightx=100;
        constraints.weighty=100;
        constraints.gridx=2;
        constraints.gridy=2;
        constraints.gridwidth=1;
        constraints.gridheight=1;
        contentPane.add(tLeftMargin, constraints);    // Left margin text

        JLabel lRightMargin=new JLabel(
            Globals.messages.getString("RightMargin"));
        constraints.anchor=GridBagConstraints.EAST;
        constraints.weightx=100;
        constraints.weighty=100;
        constraints.gridx=1;
        constraints.gridy=3;
        constraints.gridwidth=1;
        constraints.gridheight=1;
        contentPane.add(lRightMargin, constraints);     // Right margin label

        JTextField tRightMargin=new JTextField("0");
        constraints.weightx=100;
        constraints.weighty=100;
        constraints.gridx=2;
        constraints.gridy=3;
        constraints.gridwidth=1;
        constraints.gridheight=1;
        contentPane.add(tRightMargin, constraints);    // Right margin text

        mirror_CB=new JCheckBox(Globals.messages.getString("Mirror"));
        constraints.gridx=3;
        constraints.gridy=0;
        constraints.gridwidth=2;
        constraints.gridheight=1;
        constraints.anchor=GridBagConstraints.WEST;
        contentPane.add(mirror_CB, constraints);        // Add Print Mirror cb

        fit_CB=new JCheckBox(Globals.messages.getString("FitPage"));
        constraints.gridx=4;
        constraints.gridy=1;
        constraints.gridwidth=2;
        constraints.gridheight=1;
        constraints.anchor=GridBagConstraints.WEST;
        contentPane.add(fit_CB, constraints);       // Add Fit to page cb

        bw_CB=new JCheckBox(Globals.messages.getString("B_W"));
        constraints.gridx=3;
        constraints.gridy=2;
        constraints.gridwidth=2;
        constraints.gridheight=1;
        constraints.anchor=GridBagConstraints.WEST;
        contentPane.add(bw_CB, constraints);        // Add BlackWhite cb

        landscape_CB=new JCheckBox(Globals.messages.getString("Landscape"));
        constraints.gridx=3;
        constraints.gridy=3;
        constraints.gridwidth=2;
        constraints.gridheight=1;
        constraints.anchor=GridBagConstraints.WEST;
        contentPane.add(landscape_CB, constraints);     // Add landscape cb

        // Put the OK and Cancel buttons and make them active.
        JButton ok=new JButton(Globals.messages.getString("Ok_btn"));
        JButton cancel=new JButton(Globals.messages.getString("Cancel_btn"));

        constraints.gridx=1;
        constraints.gridy=4;
        constraints.gridwidth=4;
        constraints.gridheight=1;
        constraints.anchor=GridBagConstraints.EAST;

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

        ok.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                export=true;
                setVisible(false);
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
        contentPane.add(b, constraints);        // Add OK/cancel dialog

        DialogUtil.addCancelEscape (this, cancelAction);
        pack();
        DialogUtil.center(this);
        getRootPane().setDefaultButton(ok);
    }

    /** Check if the drawing should be mirrored.
        @return true wether the mirroring should be done.
    */
    public boolean getMirror()
    {
        return mirror_CB.isSelected();
    }

    /** Check if the drawing should be fit to the page
        @return true wether the fitting should be done.
    */
    public boolean getFit()
    {
        return fit_CB.isSelected();
    }
    /** Check if the page orientation should be landscape
        @return true wether the orientation is landscape.
    */
    public boolean getLandscape()
    {
        return landscape_CB.isSelected();
    }

    /** Check if the black and white checkbox is selected.
        @return true if the checkbox is active.
    */
    public boolean getBW()
    {
        return bw_CB.isSelected();
    }

    /** Set the mirror attribute
        @param m true if the printout should be done in mirroring mode.
    */
    public void setMirror(boolean m)
    {
        mirror_CB.setSelected(m);
    }

    /** Set the resize to fit option
        @param f true if the drawing should be stretched in order to fit the
            page.
    */
    public void setFit(boolean f)
    {
        fit_CB.setSelected(f);
    }

    /** Set the landscape mode
        @param l true if the output should be in landscape mode. It will be
            in portrait orientation otherwise.
    */
    public void setLandscape(boolean l)
    {
        landscape_CB.setSelected(l);
    }

    /** Print in black and white
        @param l if true, print in black and white, if false respect the colors
            associated to the layers.
    */
    public void setBW(boolean l)
    {
        bw_CB.setSelected(l);
    }

    /** Indicates that the printing should be done: the user selected the "ok"
        button
        @return a boolean value which indicates if the printing should be done
    */
    public boolean shouldPrint()
    {
        return export;
    }
}