package fidocadj.librarymodel.event;
/** Remove event data on a library.

    This file is part of FidoCadJ.

    <pre>
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
    </pre>
*/
public class RemoveEvent
{
    final private Object removedNode;
    final private Object parentNode;

    /** Standard constructor.
        @param parentNode the node which is the parent to the removed node.
        @param removedNode the removed node.
    */
    public RemoveEvent(Object parentNode,Object removedNode)
    {
        this.parentNode = parentNode;
        this.removedNode = removedNode;
    }

    /** Returns the value of removedNode.
        @return the value of removedNode.
     */
    public Object getRemovedNode()
    {
        return removedNode;
    }

    /** Returns the value of parentNode.
        @return the value of parentNode.
     */
    public Object getParentNode()
    {
        return parentNode;
    }
}
