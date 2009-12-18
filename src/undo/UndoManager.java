package undo;

import java.util.*;


/**	Implementation of a circular buffer of the given size.

*/
public class UndoManager {
	
	private Vector undoBuffer;
	private int pointer;
	private boolean isRedoable;
	
	public UndoManager (int size)
	{
		undoBuffer = new Vector(size);
		undoReset();
	}
	
	public void undoReset()
	{
		undoBuffer.removeAllElements();
		undoPush(new UndoState());
		pointer=1;
		isRedoable=false;
	}
	
	public void undoPush(Object state)
	{	
		if(undoBuffer.size()==undoBuffer.capacity()) {
			undoBuffer.removeElementAt(0);
			--pointer;
		}
		undoBuffer.add(pointer++, state);
//		System.out.println(undoBuffer);

		isRedoable=false;
		for(int i=pointer; i<undoBuffer.size();++i)
			undoBuffer.removeElementAt(pointer);
	}


	public Object undoPop()
		throws NoSuchElementException
	{	
		--pointer;
		if(pointer<1)
			pointer=1;
		Object o=undoBuffer.get(pointer-1);
			
		//undoBuffer.removeElementAt(undoBuffer.size()-1);
//		System.out.println(undoBuffer);

		isRedoable=true;
		return o;
		
	}
	
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