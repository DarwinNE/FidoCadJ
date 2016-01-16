package net.sourceforge.fidocadj.storage;

import net.sourceforge.fidocadj.*;

/** Store information about the current editor object. All information is
    stored in a static object, so the information is the same everywhere.

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

    Copyright 2014-2015 Kohta Ozaki, Davide Bucci
    </pre>

    @author Davide Bucci

*/
public class StaticStorage
{
    private static FidoEditor currentEditor;

    /** Set the current editor.
        @param f the current editor.
    */
    public static void setCurrentEditor(FidoEditor f)
    {
        currentEditor = f;
    }

    /** Get the current editor.
        @return the current editor object.
    */
    public static FidoEditor getCurrentEditor()
    {
        return currentEditor;
    }
}