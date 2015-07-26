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

public class Library
{
    String libraryName;
    String filename;
    boolean isStd;
    ArrayList<Category> categories;

    Library(String libraryName, String filename,boolean isStd)
    {
        this.libraryName = libraryName;
        this.filename = filename;
        this.isStd = isStd;
        categories = new ArrayList<Category>();
    }

    public String getName()
    {
        return libraryName;
    }

    public void setName(String name)
    {
        this.libraryName = name;
    }


    public String getFileName()
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

    public void addCategory(Category category)
    {
        categories.add(category);
    }

    public void removeCategory(Category category)
    {
        categories.remove(category);
    }

    public boolean isStdLib()
    {
        return isStd;
    }

    public boolean isHidden()
    {
        return false;
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

        for(Category category:categories) {
            if(category.containsMacroKey(key)) {
                return true;
            }
        }
        return false;
    }

	public String toString()
	{
		return getName();
	}
}
