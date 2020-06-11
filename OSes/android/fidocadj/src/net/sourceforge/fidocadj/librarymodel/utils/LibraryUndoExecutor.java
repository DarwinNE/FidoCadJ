package net.sourceforge.fidocadj.librarymodel.utils;

import java.io.*;

import net.sourceforge.fidocadj.globals.FileUtils;
import net.sourceforge.fidocadj.globals.LibUtils;
import net.sourceforge.fidocadj.undo.LibraryUndoListener;

import net.sourceforge.fidocadj.librarymodel.LibraryModel;

/** Execute undo actions on libraries.
    ANDROID VERSION. This class is a placeholder for the moment. No real code
    is present yet.
    This class does not handle the temporary files and dir operations
    required by the undo operation on libraries. It just performs
    the low level file copy operations required.

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
public class LibraryUndoExecutor implements LibraryUndoListener
{
    LibraryModel libraryModel;

    /** Constructor.
        @param model the drawing model.
    */
    public LibraryUndoExecutor(LibraryModel model)
    {
        libraryModel = model;
    }

    /** Execute an undo operation on a library.
        @param s the path to the temporary library directory where the
            libraries are stored immediately before the operation which should
            be undone has been performed.
    */
    public void undoLibrary(String s)
    {
        // NONE
    }
}
