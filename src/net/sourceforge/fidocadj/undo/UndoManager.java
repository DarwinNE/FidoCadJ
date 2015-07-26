package net.sourceforge.fidocadj.undo;

import java.util.*;
import java.io.*;

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

	Copyright 2008-2014 by Davide Bucci
</pre>

@author Davide Bucci
*/

public class UndoManager {
	
	private final Vector<UndoState> undoBuffer;
	private int pointer;
	private boolean isRedoable;
	
	/** Creates a new undo buffer of the given size
	
	*/
	public UndoManager (int size)
	{
		undoBuffer = new Vector<UndoState>(size);
		undoReset();
	}
	
	public void printUndoState()
	{
		System.out.println("===============================================");
		for (int i=0; i<undoBuffer.size();++i) {
			if(i==pointer-2)
				System.out.println("*****************");
			System.out.println("undoBuffer["+i+"]="+undoBuffer.get(i));
			if(i==pointer-2)
				System.out.println("*****************");
				
		}
		System.out.println("Is the next operation on a library? "+
			(isNextOperationOnALibrary()?"Yes":"No"));
	}
	
	/** Removes all the elements from the circular buffer
	
	*/
	public final void undoReset()
	{
		undoBuffer.removeAllElements();
		//undoPush(new UndoState());
		pointer=0;
		isRedoable=false;
	}
	
	/** Pushes a new undo state in the buffer
		@param state the state to be committed.
	*/
	public void undoPush(UndoState state)
	{
		//Thread.dumpStack();
		//System.out.println(""+state);
		if(undoBuffer.size()==undoBuffer.capacity()) {
			undoBuffer.removeElementAt(0);
			--pointer;
		}
		undoBuffer.add(pointer++, state);

		isRedoable=false;
		
		// If the buffer contains other elements after the pointer, erase
		// them. This happens when several undo operation is followed by an
		// edit: you can not redo or merge the old undo "timeline" with the 
		// new one.
		
		for(int i=pointer; i<undoBuffer.size();++i)
			undoBuffer.removeElementAt(pointer);
			
		// printUndoState();
	}
	
	public boolean isNextOperationOnALibrary()
	{
		if(pointer>=undoBuffer.size() || pointer<1)
			return false;
		try {
			if(undoBuffer.get(pointer).libraryOperation)
				return true;
		} catch (NoSuchElementException E) {
			return false;
		}
		return false;
	}


	/** Pops the last undo state from the buffer
		@return the recovered state.
	*/
	public UndoState undoPop()
		throws NoSuchElementException
	{	
		--pointer;
		if(pointer<1)
			pointer=1;
		UndoState o=undoBuffer.get(pointer-1);

		isRedoable=true;
		return o;
		
	}
	
	/** Redo the last undo state from the buffer
		@return the recovered state.
	*/
	public UndoState undoRedo()
		throws NoSuchElementException
	{
		if (!isRedoable) {
			NoSuchElementException E=new NoSuchElementException();
			throw E;
		}
		
		++pointer;
		if(pointer>undoBuffer.size())
			pointer=undoBuffer.size();
			
		UndoState o=undoBuffer.get(pointer-1);
		
		return o;
	}

}