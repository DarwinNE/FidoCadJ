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

package net.sourceforge.fidocadj.librarymodel.event;

public class KeyChangeEvent
{
	final private Object keyChangedNode;
	final private Object parentNode;
	final private String oldKey;

	public KeyChangeEvent(Object parentNode,Object keyChangedNode,String oldKey)
	{
		this.parentNode = parentNode;
		this.keyChangedNode = keyChangedNode;
		this.oldKey = oldKey;
	}
	
	/**
	 * Returns the value of renamedNode.
	 */

	public Object getKeyChangedNode()
	{
		return keyChangedNode;
	}

	/**
	 * Returns the value of parentNode.
	 */

	public Object getParentNode()
	{
		return parentNode;
	}
	
	public String getOldKey()
	{
		return oldKey;
	}
}
