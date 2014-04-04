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
