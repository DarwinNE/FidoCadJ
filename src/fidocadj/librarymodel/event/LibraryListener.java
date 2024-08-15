package fidocadj.librarymodel.event;

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
    along with FidoCadJ. If not,
    @see <a href=http://www.gnu.org/licenses/>http://www.gnu.org/licenses/</a>.

    Copyright 2014-2023 Kohta Ozaki, Davide Bucci
    </pre>
*/
public interface LibraryListener
{
    /** Called when a library has been loaded.
    */
    void libraryLoaded();

    /** Called when a node has been renamed.
        @param e information about the rename event.
    */
    void libraryNodeRenamed(RenameEvent e);

    /** Called when a node has been removed.
        @param e information about the remove event.
    */
    void libraryNodeRemoved(RemoveEvent e);

    /** Called when a node has been added.
        @param e information about the added event.
    */
    void libraryNodeAdded(AddEvent e);

    /** Called when a key has been changed in a node
        @param e information about the key change event.
    */
    void libraryNodeKeyChanged(KeyChangeEvent e);
}
