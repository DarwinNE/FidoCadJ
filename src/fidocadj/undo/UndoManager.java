package fidocadj.undo;

import java.util.*;

/**
    Implementation of a circular buffer of the given size.
    This is tailored in particular for the undo/redo system.
    The choice of the circular buffer is reasonable since one expects that
    in the classical undo systems the very old states are overwritten when
    the maximum number of undo steps is reached.

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
    along with FidoCadJ. If not,
    @see <a href=http://www.gnu.org/licenses/>http://www.gnu.org/licenses/</a>.

    Copyright 2008-2023 by Davide Bucci
    </pre>

    @author Davide Bucci
*/

public class UndoManager
{
    private final List<UndoState> undoBuffer;
    private int pointer;
    private int sizeMax;
    private boolean isRedoable;

    /** Creates a new undo buffer of the given size.
        @param s the size of the circular buffer.
    */
    public UndoManager (int s)
    {
        sizeMax=s;
        undoBuffer = new Vector<UndoState>(sizeMax);
        undoReset();
    }

    /** For debug purposes, print the contents of the undo buffer.
    */
    public void printUndoState()
    {
        System.out.println("===============================================");
        for (int i=0; i<undoBuffer.size();++i) {
            if(i==pointer-2) {
                System.out.println("*****************");
            }
            System.out.println("undoBuffer["+i+"]="+undoBuffer.get(i));
            if(i==pointer-2) {
                System.out.println("*****************");
            }
        }
        System.out.println("Is the next operation on a library? "+
            (isNextOperationOnALibrary()?"Yes":"No"));
    }

    /** Removes all the elements from the circular buffer.
    */
    public final void undoReset()
    {
        undoBuffer.clear();
        pointer=0;
        isRedoable=false;
    }

    /** Pushes a new undo state in the buffer.
        @param state the state to be committed.
    */
    public void undoPush(UndoState state)
    {
        if(undoBuffer.size()==sizeMax) {
            undoBuffer.remove(0);
            --pointer;
        }
        undoBuffer.add(pointer++, state);

        isRedoable=false;

        // If the buffer contains other elements after the pointer, erase
        // them. This happens when several undo operation is followed by an
        // edit: you can not redo or merge the old undo "timeline" with the
        // new one.

        for(int i=pointer; i<undoBuffer.size();++i) {
            undoBuffer.remove(pointer);
        }
    }

    /** Checks if the next operation is done on a library instead than on a
        drawing.
        @return true if the next operation is on a library.
    */
    public boolean isNextOperationOnALibrary()
    {
        if(pointer>=undoBuffer.size() || pointer<1) {
            return false;
        }

        try {
            if(undoBuffer.get(pointer).libraryOperation) {
                return true;
            }
        } catch (NoSuchElementException e) {
            return false;
        }
        return false;
    }

    /** Pops the last undo state from the buffer
        @return the recovered state.
        @throws NoSuchElementException if the buffer is empty.
    */
    public UndoState undoPop()
        throws NoSuchElementException
    {
        --pointer;
        if(pointer<1) {
            pointer=1;
        }
        UndoState o=undoBuffer.get(pointer-1);

        isRedoable=true;
        return o;
    }

    /** Redo the last undo state from the buffer
        @return the recovered state.
        @throws NoSuchElementException if the buffer is empty.
    */
    public UndoState undoRedo()
        throws NoSuchElementException
    {
        if (!isRedoable) {
            NoSuchElementException e=new NoSuchElementException();
            throw e;
        }

        ++pointer;
        if(pointer>undoBuffer.size()) {
            pointer=undoBuffer.size();
        }

        return undoBuffer.get(pointer-1);
    }
}