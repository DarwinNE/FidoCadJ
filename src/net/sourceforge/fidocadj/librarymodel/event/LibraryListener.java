package net.sourceforge.fidocadj.librarymodel.event;

/** Interface for a listener of events on library.

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

    Copyright 2014 Kohta Ozaki
    </pre>
*/
public interface LibraryListener
{
    /** Called when a library has been loaded.
    */
    public void libraryLoaded();

    /** Called when a node has been renamed.
        @param e information about the rename event.
    */
    public void libraryNodeRenamed(RenameEvent e);

    /** Called when a node has been removed.
        @param e information about the remove event.
    */
    public void libraryNodeRemoved(RemoveEvent e);

    /** Called when a node has been added.
        @param e information about the added event.
    */
    public void libraryNodeAdded(AddEvent e);

    /** Called when a key has been changed in a node
        @param e information about the key change event.
    */
    public void libraryNodeKeyChanged(KeyChangeEvent e);
}
