package net.sourceforge.fidocadj.librarymodel.event;

public class KeyChangeEvent
{
	private Object keyChangedNode;
	private Object parentNode;
	private String oldKey;

	public KeyChangeEvent(Object parentNode,Object keyChangedNode,String oldKey)
	{
		this.parentNode = parentNode;
		this.keyChangedNode = keyChangedNode;
		this.oldKey = oldKey;
	}
	
	/**
	 * Returns the value of renamedNode.
	 */

	public Object getKeyChangedNode()
	{
		return keyChangedNode;
	}

	/**
	 * Returns the value of parentNode.
	 */

	public Object getParentNode()
	{
		return parentNode;
	}
	
	public String getOldKey()
	{
		return oldKey;
	}
}
