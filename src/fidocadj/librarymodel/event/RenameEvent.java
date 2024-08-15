package fidocadj.librarymodel.event;

/** Rename event containing data.

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
public class RenameEvent
{
    final private Object renamedNode;
    final private Object parentNode;
    final private String oldName;

    /** Standard constructor.
        @param parentNode the parent node to the renamed one.
        @param renamedNode the renamed node.
        @param oldName the old name of the renamed node.
    */
    public RenameEvent(Object parentNode,Object renamedNode,String oldName)
    {
        this.parentNode = parentNode;
        this.renamedNode = renamedNode;
        this.oldName = oldName;
    }

    /** Returns the value of renamedNode.
        @return the value of renamedNode.
     */
    public Object getRenamedNode()
    {
        return renamedNode;
    }

    /** Returns the value of parentNode.
        @return the value of parentNode.
     */
    public Object getParentNode()
    {
        return parentNode;
    }

    /** Returns the value of oldName.
        @return the old name of the node.
     */
    public String getOldName()
    {
        return oldName;
    }
}
