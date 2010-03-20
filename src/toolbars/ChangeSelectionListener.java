package toolbars;
/**	Interface used to callback notify that the current selection state has 
	changed
	
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

    Copyright 2007-2008 by Davide Bucci
</pre>
    
   
	
	@version 1.0
	@author Davide Bucci
*/

public interface ChangeSelectionListener {
	/**	The callback method which is called when the current selection state
		has changed. 
		@param s the actual selection state (see the CircuitPanel class for the
		definition of the constants used here).
		@param m the key of the macro being used (if necessary).
	*/
	public void setSelectionState(int s, String macroKey);
	
	
	/** Get the actual selection state. 
		@return the actual selection state (see the CircuitPanel class for the
		definition of the constants used here).
	
	*/
	public int getSelectionState();
	
}