package net.sourceforge.fidocadj.macropicker;


// TODO: restrict import to what indispensable.
import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.tree.*;

import net.sourceforge.fidocadj.circuit.CircuitPanel;
import net.sourceforge.fidocadj.circuit.controllers.ElementsEdtActions;
import net.sourceforge.fidocadj.export.ExportGraphic;
import net.sourceforge.fidocadj.geom.DrawingSize;
import net.sourceforge.fidocadj.geom.MapCoordinates;
import net.sourceforge.fidocadj.globals.Globals;
import net.sourceforge.fidocadj.toolbars.ChangeSelectionListener;
import net.sourceforge.fidocadj.librarymodel.LibraryModel;
import net.sourceforge.fidocadj.librarymodel.Library;
import net.sourceforge.fidocadj.librarymodel.Category;
import net.sourceforge.fidocadj.layermodel.LayerModel;
import net.sourceforge.fidocadj.librarymodel.event.LibraryListenerAdapter;
import net.sourceforge.fidocadj.librarymodel.event.LibraryListener;
import net.sourceforge.fidocadj.macropicker.model.MacroTreeModel;
import net.sourceforge.fidocadj.macropicker.model.MacroTreeNode;
import net.sourceforge.fidocadj.primitives.MacroDesc;

/** The cell renderer: show the appropriate icon.

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

    Copyright 2014-2015 Kohta Ozaki, Davide Bucci
    </pre>

    @author Kohta Ozaki, Davide Bucci
*/
public class MacroTreeCellRenderer extends DefaultTreeCellRenderer
{
    /** Create a component able to generate a rendered apt to show the
        elements in the Macro Tree.
        In our version, the value is checked and if it is an instance of
        MacroTreeNode, it contains an icon which is retrieved and employed
        to show the state of the node.
        @param tree the tree on which the renderer should be employed.
        @param value the MacroTreeNode to render
        @param sel true if selected.
        @param expanded true if expanded.
        @param leaf true if it is a leaf.
        @param row the row index.
        @param hasFocus true if it has focus.
    */
    @Override
    public Component getTreeCellRendererComponent(JTree tree,
            Object value,
            boolean sel,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus)
    {
        Component c = super.getTreeCellRendererComponent(
                          tree, value, sel,
                          expanded, leaf, row,
                          hasFocus);
        if(value instanceof MacroTreeNode) {
            Icon icon = ((MacroTreeNode)value).getIcon();

            if(icon == null) {
                return c;
            } else {
                setIcon(icon);
            }
        }
        return this;
    }
}