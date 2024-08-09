package fidocadj.librarymodel.utils;

import fidocadj.circuit.CircuitPanel;
import fidocadj.circuit.controllers.ParserActions;
import fidocadj.circuit.model.DrawingModel;
import fidocadj.librarymodel.event.LibraryListener;
import fidocadj.librarymodel.event.AddEvent;
import fidocadj.librarymodel.event.KeyChangeEvent;
import fidocadj.librarymodel.event.RemoveEvent;
import fidocadj.librarymodel.event.RenameEvent;
import fidocadj.FidoFrame;

/** Class implementing a library listener, with some callback methods.

    SWING version

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

    Copyright 2014 Kohta Ozaki
    </pre>
*/
public class CircuitPanelUpdater implements LibraryListener
{
    FidoFrame fidoFrame;

    /** Constructor.
        @param fidoFrame the frame containing the user interface.
    */
    public CircuitPanelUpdater(FidoFrame fidoFrame)
    {
        this.fidoFrame = fidoFrame;
    }

    /** Called when a library has been loaded.
    */
    public void libraryLoaded()
    {
        updateCircuitPanel();
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
        updateCircuitPanel();
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
        updateCircuitPanel();
    }

    /** Parse again the circuit and redraw everything.
    */
    private void updateCircuitPanel()
    {
        CircuitPanel cp = fidoFrame.cc;
        DrawingModel ps = cp.dmp;
        ParserActions pa = new ParserActions(ps);
        cp.getParserActions().parseString(pa.getText(true));
        cp.repaint();
    }
}

