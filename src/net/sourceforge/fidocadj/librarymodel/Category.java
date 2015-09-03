// This file is part of FidoCadJ.
// 
// FidoCadJ is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// FidoCadJ is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with FidoCadJ.  If not, see <http://www.gnu.org/licenses/>.
// 
// Copyright 2014 Kohta Ozaki

package net.sourceforge.fidocadj.librarymodel;

import java.util.*;

import net.sourceforge.fidocadj.primitives.MacroDesc;

public class Category
{
    String name;
    Library parentLibrary;
    List<MacroDesc> macros;
    boolean isHidden;

    Category(String name,Library parentLibrary,boolean isHidden)
    {
        macros = new ArrayList<MacroDesc>();
        this.name = name;
        this.parentLibrary = parentLibrary;
        this.isHidden = isHidden;
    }

    /** Get the parent library.
        @return the parent library.
    */
    public Library getParentLibrary()
    {
        return parentLibrary;
    }

    public void setParentLibrary(Library parentLibrary)
    {
        this.parentLibrary = parentLibrary;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void addMacro(MacroDesc macroDesc)
    {
        macros.add(macroDesc);
    }

    public void removeMacro(MacroDesc macroDesc)
    {
        macros.remove(macroDesc);
    }

    public List<MacroDesc> getAllMacros()
    {
        return macros;
    }

    public boolean isHidden()
    {
        return isHidden;
    }

    public static boolean isValidName(String name)
    {
        return true;
    }

    public boolean containsMacroKey(String key)
    {
        if(key==null) {
            return true;
        }

        for(MacroDesc macro:macros) {
            if(LibraryModel.getPlainMacroKey(macro).equals(key)) {
                return true;
            }
        }
        return false;
    }

}
