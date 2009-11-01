/**	Interface used to callback notify that the current selection state has 
	changed
	
	@version 1.0
	@author Davide Bucci
*/

public interface ChangeSelectionListener {
	/**	The callback method which is called when the current selection state
		has changed. 
	*/
	public void setSelectionState(int s, String macroKey);
	public int getSelectionState();
	
}