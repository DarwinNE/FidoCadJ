package net.sourceforge.fidocadj.toolbars;

/** Interface used to callback notify that the current layer has changed
    @version 1.0
    @author Davide Bucci
*/

public interface ChangeSelectedLayer {
    /** The callback method which is called when the current layer has changed. 
    
    */
    void changeSelectedLayer(int s);
}