package fidocadj.librarymodel.event;

/** Key changed during library operations.
    I.e. the action of changing the key of a macro.

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

   Copyright 2014 Kohta Ozaki
*/
public class KeyChangeEvent
{
    final private Object keyChangedNode;
    final private Object parentNode;
    final private String oldKey;

    /** Standard constructor.
        @param parentNode node which will become the parent node.
        @param keyChangedNode node on which the key should be changed.
        @param oldKey the old key which was associated to the macro.
    */
    public KeyChangeEvent(Object parentNode,Object keyChangedNode,String oldKey)
    {
        this.parentNode = parentNode;
        this.keyChangedNode = keyChangedNode;
        this.oldKey = oldKey;
    }

    /** Returns the value of keyChangedNode.
        @return the value of keyChangedNode.
     */
    public Object getKeyChangedNode()
    {
        return keyChangedNode;
    }

    /** Returns the value of parentNode.
        @return the value of parentNode.
     */
    public Object getParentNode()
    {
        return parentNode;
    }

    /** Returns the old key.
        @return the old key.
    */
    public String getOldKey()
    {
        return oldKey;
    }
}
