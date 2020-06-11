package net.sourceforge.fidocadj.librarymodel.utils;

import net.sourceforge.fidocadj.librarymodel.event.LibraryListener;
import net.sourceforge.fidocadj.librarymodel.event.AddEvent;
import net.sourceforge.fidocadj.librarymodel.event.KeyChangeEvent;
import net.sourceforge.fidocadj.librarymodel.event.RemoveEvent;
import net.sourceforge.fidocadj.librarymodel.event.RenameEvent;

import net.sourceforge.fidocadj.circuit.controllers.ParserActions;
import net.sourceforge.fidocadj.circuit.model.DrawingModel;

/** Class implementing a library listener, with some callback methods.

    ANDROID version. For the moment, there is nothing very exciting happening
    here and this class is only a placeholder.

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

    Copyright 2014 Kohta Ozaki, Davide Bucci
    </pre>
*/
public class CircuitPanelUpdater implements LibraryListener
{

    /** Constructor.
    */
    public CircuitPanelUpdater()
    {
    }

    /** Called when a library has been loaded.
    */
    public void libraryLoaded()
    {
    }

    /** Called when a library has been renamed.
        @param e the renaming event.
    */
    public void libraryNodeRenamed(RenameEvent e)
    {
        //NOP
    }

    /** Called when a node has been removed from a library.
        @param e the remove event.
    */
    public void libraryNodeRemoved(RemoveEvent e)
    {
    }

    /** Called when a node has been added.
        @param e the adding event.
    */
    public void libraryNodeAdded(AddEvent e)
    {
        //NOP
    }

    /** Called when the key for a node (macro) has been changed.
        @param e the node key changing event.
    */
    public void libraryNodeKeyChanged(KeyChangeEvent e)
    {
    }

    /** Parse again the circuit and redraw everything.
    */
    private void updateCircuitPanel()
    {
    }
}

