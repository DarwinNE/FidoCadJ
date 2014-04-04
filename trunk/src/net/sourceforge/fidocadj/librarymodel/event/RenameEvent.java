package net.sourceforge.fidocadj.librarymodel.event;

public class RenameEvent
{
	private Object renamedNode;
	private Object parentNode;
	private String oldName;


	public RenameEvent(Object parentNode,Object renamedNode,String oldName)
	{
		this.parentNode = parentNode;
		this.renamedNode = renamedNode;
		this.oldName = oldName;
	}

	/**
	 * Returns the value of renamedNode.
	 */

	public Object getRenamedNode()
	{
		return renamedNode;
	}

	/**
	 * Returns the value of parentNode.
	 */

	public Object getParentNode()
	{
		return parentNode;
	}

	/**
	 * Returns the value of oldName.
	 */

	public String getOldName()
	{
		return oldName;
	}
}
