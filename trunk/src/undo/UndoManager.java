package undo;

import java.util.*;




/**
   	Implementation of a circular buffer of the given size.
    This is tailored in particular for the undo/redo system.
    The choice of the circular buffer is reasonable since one expects that 
    in the classical undo systems the very old states are overwritten when
    the maximum number of undo steps is reached.
    
    @author Davide Bucci
 


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

	Copyright 2008-2012 by Davide Bucci
</pre>

@author Davide Bucci
*/

public class UndoManager {
	
	private Vector undoBuffer;
	private int pointer;
	private boolean isRedoable;
	
	/** Creates a new undo buffer of the given size
	
	*/
	public UndoManager (int size)
	{
		undoBuffer = new Vector(size);
		undoReset();
	}
	
	/** Removes all the elements from the circular buffer
	
	*/
	public void undoReset()
	{
		undoBuffer.removeAllElements();
		undoPush(new UndoState());
		pointer=1;
		isRedoable=false;
	}
	
	/** Pushes a new undo state in the buffer
		@argument state the state to be committed.
	*/
	public void undoPush(Object state)
	{	
		if(undoBuffer.size()==undoBuffer.capacity()) {
			undoBuffer.removeElementAt(0);
			--pointer;
		}
		undoBuffer.add(pointer++, state);

		isRedoable=false;
		for(int i=pointer; i<undoBuffer.size();++i)
			undoBuffer.removeElementAt(pointer);
	}


	/** Pops the last undo state from the buffer
		@return the recovered state.
	*/
	public Object undoPop()
		throws NoSuchElementException
	{	
		--pointer;
		if(pointer<1)
			pointer=1;
		Object o=undoBuffer.get(pointer-1);

		isRedoable=true;
		return o;
		
	}
	
	/** Redo the last undo state from the buffer
		@return the recovered state.
	*/
	public Object undoRedo()
		throws NoSuchElementException
	{
		if (!isRedoable) {
			NoSuchElementException E=new NoSuchElementException();
			throw E;
		}
		
		
		++pointer;
		if(pointer>undoBuffer.size())
			pointer=undoBuffer.size();
			
		Object o=undoBuffer.get(pointer-1);
		
		return o;
	}

}