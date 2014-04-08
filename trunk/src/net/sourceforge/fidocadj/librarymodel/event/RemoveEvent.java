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

public class RemoveEvent
{
	private Object removedNode;
	private Object parentNode;

	public RemoveEvent(Object parentNode,Object removedNode)
	{
		this.parentNode = parentNode;
		this.removedNode = removedNode;
	}
	
	/**
	 * Returns the value of renamedNode.
	 */

	public Object getRemovedNode() {
		return removedNode;
	}

	/**
	 * Returns the value of parentNode.
	 */

	public Object getParentNode() {
		return parentNode;
	}
}
