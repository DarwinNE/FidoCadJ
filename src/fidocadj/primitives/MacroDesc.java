package fidocadj.primitives;
/** Class MacroDesc provides a standard description of the macro. It provides
    its name, its description and its category

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

    Copyright 2008-2013 by Davide Bucci
    </pre>
*/
public class MacroDesc
{
    public String name;         // The one which is shown
    public String key;          // Unequivocally used to identify the macro
    public String description;  // The list of commands included in the macro
    public String category;     // The category on which the macro is put
    public String library;      // The library name
    public String filename;     // The library file name
    public int level;           // The level (0: macro 1:category 2:library)

    // The library file name is usually identical to the library name, except
    // when an existing library is already present with a different filename.
    // This is a legacy from previous versions of FidoCadJ.

    /** Standard constructor. Give the macro's name, description and category.
        @param ke the key to be used.
        @param na the name of the macro.
        @param de the description of the macro (the list of commands).
        @param cat the category of the macro.
        @param lib the library name (prefix).
        @param fn the library file name.
    */
    public MacroDesc(String ke, String na, String de, String cat,
        String lib, String fn)
    {
        name = na;
        key=ke;
        description = de;
        category = cat;
        library = lib;
        filename = fn;
        level = 0;
    }

    /** Provide a text describing the macro, usually for debug purposes.
        @return the description.
    */
    @Override public String toString()
    {
        String s;
        switch (level) {
            case 1:
                s=category;
                break;
            case 2:
                s=library;
                break;
            case 0:
            default:
                s=name;
                break;
        }
        return s.trim();
    }
}