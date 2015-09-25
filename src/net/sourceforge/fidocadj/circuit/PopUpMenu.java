package net.sourceforge.fidocadj.circuit;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import net.sourceforge.fidocadj.globals.*;
import net.sourceforge.fidocadj.primitives.*;
import net.sourceforge.fidocadj.circuit.controllers.*;
import net.sourceforge.fidocadj.clipboard.*;

/** Pop up menu for the main editing panel.

    <pre>
    actionHandler file is part of FidoCadJ.

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
public class PopUpMenu
{
    // Elements to be included in the popup menu.
    private JMenuItem editProperties;
    private JMenuItem editCut;
    private JMenuItem editCopy;
    private JMenuItem editPaste;
    private JMenuItem editDuplicate;
    private JMenuItem editSelectAll;
    private JMenuItem editRotate;
    private JMenuItem editMirror;
    private JMenuItem editSymbolize; // phylum
    private JMenuItem editUSymbolize; // phylum
    private JMenuItem editAddNode;
    private JMenuItem editRemoveNode;

    private final ActionListener actionHandler;
    private final SelectionActions sa;
    private final JPopupMenu pp;

    // We need to save the position where the popup menu appears.
    private int menux;
    private int menuy;

    /** Constructor. Create a PopUpMenu and associates it to the provided
        handler.
        @param p the ActionListener.
        @param s the Selection controller.
    */
    public PopUpMenu(ActionListener p, SelectionActions s)
    {
        sa=s;
        actionHandler=p;
        pp = new JPopupMenu();
        definePopupMenu();
    }

    /** Get the x coordinate of the place where the user clicked for the
        popup menu.
        @return the x coordinate.
    */
    public int getMenuX()
    {
        return menux;
    }

    /** Get the y coordinate of the place where the user clicked for the
        popup menu.
        @return the y coordinate.
    */
    public int getMenuY()
    {
        return menuy;
    }

    /** Create the popup menu.
    */
    private void definePopupMenu()
    {
        editProperties = new
            JMenuItem(Globals.messages.getString("Param_opt"));

        editCut = new JMenuItem(Globals.messages.getString("Cut"));
        editCopy = new JMenuItem(Globals.messages.getString("Copy"));
        editSelectAll = new JMenuItem(Globals.messages.getString("SelectAll"));

        editPaste = new JMenuItem(Globals.messages.getString("Paste"));
        editDuplicate = new JMenuItem(Globals.messages.getString("Duplicate"));
        editRotate = new JMenuItem(Globals.messages.getString("Rotate"));
        editMirror = new JMenuItem(Globals.messages.getString("Mirror_E"));

        editSymbolize = new JMenuItem(Globals.messages.getString("Symbolize"));
        editUSymbolize =
            new JMenuItem(Globals.messages.getString("Unsymbolize"));

        editAddNode = new JMenuItem(Globals.messages.getString("Add_node"));
        editRemoveNode =
            new JMenuItem(Globals.messages.getString("Remove_node"));

        pp.add(editProperties);
        pp.addSeparator();

        pp.add(editCut);
        pp.add(editCopy);
        pp.add(editPaste);
        pp.add(editDuplicate);
        pp.addSeparator();
        pp.add(editSelectAll);

        pp.addSeparator();
        pp.add(editRotate);
        pp.add(editMirror);

        pp.add(editAddNode);
        pp.add(editRemoveNode);

        pp.addSeparator();
        pp.add(editSymbolize); // by phylum
        pp.add(editUSymbolize); // phylum

        // Adding the action listener

        editProperties.addActionListener(actionHandler);
        editCut.addActionListener(actionHandler);
        editCopy.addActionListener(actionHandler);
        editSelectAll.addActionListener(actionHandler);
        editPaste.addActionListener(actionHandler);
        editDuplicate.addActionListener(actionHandler);
        editRotate.addActionListener(actionHandler);
        editMirror.addActionListener(actionHandler);
        editAddNode.addActionListener(actionHandler);
        editRemoveNode.addActionListener(actionHandler);
        editSymbolize.addActionListener(actionHandler); // phylum
        editUSymbolize.addActionListener(actionHandler); // phylum
    }

    /** Show a popup menu representing the actions that can be done on the
        selected context.
        @param j the panel to which this menu should be associated.
        @param x the x coordinate where the popup menu should be put.
        @param y the y coordinate where the popup menu should be put.
    */
    public void showPopUpMenu(JPanel j, int x, int y)
    {
        menux=x; menuy=y;
        boolean s=false;
        GraphicPrimitive g=sa.getFirstSelectedPrimitive();
        boolean somethingSelected=g!=null;


        // A certain number of menu options are applied to selected
        // primitives. We therefore check wether are there some
        // of them available and in actionHandler case we activate what should
        // be activated in the pop up menu.
        s=somethingSelected;

        editProperties.setEnabled(s);
        editCut.setEnabled(s);
        editCopy.setEnabled(s);
        editRotate.setEnabled(s);
        editMirror.setEnabled(s);
        editDuplicate.setEnabled(s);

        editSelectAll.setEnabled(true);

        if(g instanceof PrimitiveComplexCurve ||
            g instanceof PrimitivePolygon)
        {
            s=true;
        } else
            s=false;

        if (!sa.isUniquePrimitiveSelected())
            s=false;

        editAddNode.setEnabled(s);
        editRemoveNode.setEnabled(s);
        editAddNode.setVisible(s);
        editRemoveNode.setVisible(s);

        // We just check if the clipboard is empty. It would be better
        // to see if there is some FidoCadJ code wich might be pasted

        TextTransfer textTransfer = new TextTransfer();

        if(textTransfer.getClipboardContents().equals(""))
            editPaste.setEnabled(false);
        else
            editPaste.setEnabled(true);

        editSymbolize.setEnabled(somethingSelected);

        editUSymbolize.setEnabled(sa.selectionCanBeSplitted()); // phylum

        pp.show(j, x, y);
    }
}