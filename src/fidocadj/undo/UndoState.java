package fidocadj.undo;

/** Track the undo/redo state.
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

    Copyright 2008-2023 by Davide Bucci
    </pre>

    @author Davide Bucci
*/
public class UndoState
{
    // Contains a textual description of the drawing
    public String text;
    // Is true if there has been a modification of the drawing: something
    // that needs the file to be saved, unless the user wants to discard
    // changes.
    public boolean isModified;
    // The file name of the drawing.
    public String fileName;

    // True if an editing operation on the libraries has
    // been performed.
    public boolean libraryOperation;

    // The tempory directory where the libraries are be stored.
    public String libraryDir;

    /** Standard constructor.
    */
    public UndoState()
    {
        text="";
        isModified=false;
        fileName="";
        libraryDir="";
    }

    /** Convert to string the undo operation represented by this object.
        This method is useful mainly for debug purposes.
        @return a String completely describing the object.
    */
    @Override public String toString()
    {
        return "text="+text+"\nfileName="+fileName+
            "\nOperation on a library: "+libraryOperation+
            "\nlibraryDir="+libraryDir;
    }
}