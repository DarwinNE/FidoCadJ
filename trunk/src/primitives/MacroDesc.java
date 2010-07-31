package primitives;
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
    along with FidoCadJ.  If not, see <http://www.gnu.org/licenses/>.

	Copyright 2008-2009 by Davide Bucci
</pre>	
	
	
*/

public class MacroDesc {
	public String name;
	public String key;
	public String description;
	public String category;
	public String library;
	
	/** Standard constructor. Give the macro's name, description and cathegory 
		@param ke the key to be used
		@param na the name of the macro
		@param de the description of the macro (the list of commands)
		@param cat the category of the macro
		@param lib the library name
	*/
	public MacroDesc(String ke, String na, String de, String cat, String lib)
	{
		name = na;
		key=ke;
		description = de;
		category = cat;
		library = lib;
		
	}
	
	public String toString() 
	{
		return name.trim();//+", "+key+", "+category+", "+library;
	}
}