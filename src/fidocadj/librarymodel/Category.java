package fidocadj.librarymodel;

import java.util.*;

import fidocadj.primitives.MacroDesc;

/** The Category class provides a set of methods to manipulate
    categories in a library.

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
*/
public class Category
{
    String name;
    Library parentLibrary;
    List<MacroDesc> macros;
    boolean isHidden;

    /** Standard constructor.
        @param name the name of the category.
        @param parentLibrary the parent library to which the category belongs.
        @param idHidden true if the category should not be shown.
    */
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

    /** Set the parent library.
        @param parentLibrary the parent library to be employed.
    */
    public void setParentLibrary(Library parentLibrary)
    {
        this.parentLibrary = parentLibrary;
    }

    /** Get the name of the category.
        @return the name.
    */
    public String getName()
    {
        return name;
    }

    /** Set the name of the category.
        @param name the name.
    */
    public void setName(String name)
    {
        this.name = name;
    }

    /** Add a macro to this category.
        @param macroDesc the macro to be added.
    */
    public void addMacro(MacroDesc macroDesc)
    {
        macros.add(macroDesc);
    }

    /** Remove a macro from this category.
        @param macroDesc the macro to be removed.
    */
    public void removeMacro(MacroDesc macroDesc)
    {
        macros.remove(macroDesc);
    }

    /** Get a list of all macros comprised in the category.
        @return the list of macros.
    */
    public List<MacroDesc> getAllMacros()
    {
        return macros;
    }

    /** Check if the category is hidden.
        @return true if the category is hidden, false if it is visible.
    */
    public boolean isHidden()
    {
        return isHidden;
    }

    /** Check if the category name is valid.
        TODO: this method doesn't do much: it always returns true.
        @param name the name of the category to be checked.
        @return true if the name is valid.
    */
    public static boolean isValidName(String name)
    {
        return true;
    }

    /** Check if a macro having a given key is already contained in the
        category.
        @param key the key of the macro to search for.
        @return true if the macro is already present.
    */
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
