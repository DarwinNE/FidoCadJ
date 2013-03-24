package undo;

import java.util.*;

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
    along with FidoCadJ.  If not, see <http://www.gnu.org/licenses/>.

	Copyright 2008-2013 by Davide Bucci
</pre>

@author Davide Bucci
*/
public class UndoState {
	
	public String text;
	public boolean isModified;
	public String fileName;
	
	public String libraryDir;
	
	public UndoState()
	{
		text="";
		isModified=false;
		fileName="";
		libraryDir="";
	}

	public String toString() 
	{
		String s="text="+text+"\nfileName="+fileName+
			"\nlibraryDir="+libraryDir;
		return s;
	}
	
}