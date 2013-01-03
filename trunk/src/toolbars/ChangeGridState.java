package toolbars;
/**	Interface used to callback notify that the current grid or snap state has
	changed
	@version 1.0
	@author Davide Bucci
*/

public interface ChangeGridState {
	/**	The callback method which is called when the current grid visibility has 
		changed. 
		@param v is the wanted grid visibility state
	*/
	void setGridVisibility(boolean v);
	
	/**	The callback method which is called when the current snap visibility has 
		changed. 
		@param v is the wanted snap state
	*/
	void setSnapState(boolean v);
}