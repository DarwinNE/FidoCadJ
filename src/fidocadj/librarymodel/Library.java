package fidocadj.librarymodel;

import java.util.*;

/** The Library class provides a set of methods to manipulate
    libraries.

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

   Copyright 2014-2023 Kohta Ozaki
*/
public class Library
{
    private String libraryName;
    private final String filename;
    private final boolean isStd;
    private final List<Category> categories;

    /** Standard constructor.
        @param libraryName the name of the library.
        @param filename the name of the file where the library is contained.
        @param isStd true if the library should be considered as standard.
    */
    Library(String libraryName, String filename,boolean isStd)
    {
        this.libraryName = libraryName;
        this.filename = filename;
        this.isStd = isStd;
        categories = new ArrayList<Category>();
    }

    /** Get the name of the library.
        @return the library name.
    */
    public String getName()
    {
        return libraryName;
    }

    /** Set the name of the library.
        @param name the name to be employed.
    */
    public void setName(String name)
    {
        this.libraryName = name;
    }

    /** Get the name of the file of the library.
        @return the filename.
    */
    public String getFilename()
    {
        return filename;
    }

    /** Gets all the categories contained in the library.
        @return a list containing all categories.
    */
    public List<Category> getAllCategories()
    {
        return categories;
    }

    /** Get a category in the library.
        @param name the name of the category to be retrieved.
        @return the category with the required name or null if nothing has
            been found.
    */
    public Category getCategory(String name)
    {
        Category result=null;
        for(Category c:categories) {
            if(c.getName().equals(name)) {
                result=c;
                break;
            }
        }
        return result;
    }

    /** Add the given category to the library.
        @param category the category to be added.
    */
    public void addCategory(Category category)
    {
        categories.add(category);
    }

    /** Remove a category from the library.
        @param category the category to be removed.
    */
    public void removeCategory(Category category)
    {
        categories.remove(category);
    }

    /** Check if the library is standard.
        @return true if the library is standard.
    */
    public boolean isStdLib()
    {
        return isStd;
    }

    /** Check if the library is hidden.
        TODO: this method always returns false.
        @return true if the library is hidden.
    */
    public boolean isHidden()
    {
        return false;
    }

    /** Check if the name of the library is valid.
        TODO: this method always returns true.
        @param name the name to be checked.
        @return true if the name is valid.
    */
    public static boolean isValidName(String name)
    {
        return true;
    }

    /** Check if the library contains the macro specified with the key.
        @param key the key to be searched for.
        @return true if the key is found, false otherwise.
    */
    public boolean containsMacroKey(String key)
    {
        if(key==null) {
            return true;
        }

        for(Category category:categories) {
            if(category.containsMacroKey(key)) {
                return true;
            }
        }
        return false;
    }

    /** Provide a string description of the library.
        @return the string description (simply the name) of the library.
    */
    @Override public String toString()
    {
        return getName();
    }
}
