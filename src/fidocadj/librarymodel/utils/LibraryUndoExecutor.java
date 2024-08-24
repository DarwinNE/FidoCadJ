package fidocadj.librarymodel.utils;

import java.io.*;

import fidocadj.undo.LibraryUndoListener;
import fidocadj.globals.FileUtils;
import fidocadj.globals.LibUtils;
import fidocadj.librarymodel.LibraryModel;
import fidocadj.FidoFrame;

/** Execute undo actions on libraries.
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
    along with FidoCadJ. If not,
    @see <a href=http://www.gnu.org/licenses/>http://www.gnu.org/licenses/</a>.

    Copyright 2014 Kohta Ozaki
    </pre>
*/
public class LibraryUndoExecutor implements LibraryUndoListener
{
    FidoFrame fidoFrame;
    LibraryModel libraryModel;

    /** Constructor.
        @param frame the main UI window to which this class will be associated.
        @param model the drawing model.
    */
    public LibraryUndoExecutor(FidoFrame frame, LibraryModel model)
    {
        fidoFrame = frame;
        libraryModel = model;
    }

    /** Execute an undo operation on a library.
        @param s the path to the temporary library directory where the
            libraries are stored immediately before the operation which should
            be undone has been performed.
    */
    public void undoLibrary(String s)
    {
        try {
            File sourceDir = new File(s);
            String d=LibUtils.getLibDir();
            File destinationDir = new File(d);
            FileUtils.copyDirectory(sourceDir, destinationDir);
            fidoFrame.loadLibraries();
            libraryModel.forceUpdate();
        } catch (IOException e) {
            System.out.println("Cannot restore library directory contents.");
        }
    }
}

