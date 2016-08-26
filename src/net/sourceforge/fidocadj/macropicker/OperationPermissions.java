package net.sourceforge.fidocadj.macropicker;

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

/** Describe which permissions are available.

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
public class OperationPermissions
{
    public boolean copyAvailable;
    public boolean pasteAvailable;
    public boolean renameAvailable;
    public boolean removeAvailable;
    public boolean renKeyAvailable;

    /** Get the value of copyAvailable.
        @return true if it can be copied.
    */
    public boolean isCopyAvailable()
    {
        return copyAvailable;
    }

    /** Get the value of pasteAvailable.
        @return true if the paste operation is possible
    */
    public boolean isPasteAvailable()
    {
        return pasteAvailable;
    }

    /** Get the value of renameAvailable.
        @return true if it can be renamed.
    */
    public boolean isRenameAvailable()
    {
        return renameAvailable;
    }

    /** Returns the value of removeAvailable.
        @return true if it can be removed/deleted.
    */
    public boolean isRemoveAvailable()
    {
        return removeAvailable;
    }

    /** Returns the value of renKeyAvailable.
        @return true if its key can be changed.
    */
    public boolean isRenKeyAvailable()
    {
        return renKeyAvailable;
    }

    /** Disable all permissions.
    */
    public void disableAll()
    {
        copyAvailable=false;
        pasteAvailable=false;
        renameAvailable=false;
        removeAvailable=false;
        renKeyAvailable=false;
    }
}
