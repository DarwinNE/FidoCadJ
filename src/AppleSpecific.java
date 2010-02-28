import javax.swing.*;

import com.apple.eawt.*;

import globals.*;
import dialogs.*;


/** The class AppleSpecific implements a few mechanism for interacting with
	the MacOSX operating system. This class will only be used if the program 
	detects it is being run on a MacOSX operating system.


*/

class AppleSpecific implements ApplicationListener{

	/** Create an application listener able to respond to a few Finder events
	
	*/
	public void answerFinder() {
		Application app = new Application();
		app.setEnabledPreferencesMenu(true);
		app.getApplication().addApplicationListener(this);
	}
	
	/** Respond to an user clicking on an About menu.
	
	*/
	public void handleAbout(ApplicationEvent evt) 
	{
		DialogAbout d=new DialogAbout(Globals.activeWindow);
		d.setVisible(true);
		evt.setHandled(true);

	}
    
    public void handleOpenApplication(ApplicationEvent evt) 
    {
    }
    /** Respond to an user double clicking on a FCD file
	
	*/
    public void handleOpenFile(ApplicationEvent evt) {
    	String file = evt.getFilename();
    	((FidoFrame)Globals.activeWindow).Load(file);
    }
    
    /** Respond to an user clicking on the Preferences menu.
	
	*/
    public void handlePreferences(ApplicationEvent evt) 
	{	
		((FidoFrame)Globals.activeWindow).showPrefs();
	}
	
	public void handlePrintFile(ApplicationEvent evt) 
	{
	}
	
	/** Ask for confirmation when quitting.
	
	*/
	public void handleQuit(ApplicationEvent evt) 
	{
		boolean ca = false;
		
		if(JOptionPane.showConfirmDialog(null, 
			Globals.messages.getString("Warning_quit"),
			Globals.messages.getString("Warning"),
			JOptionPane.OK_CANCEL_OPTION, 
			JOptionPane.WARNING_MESSAGE)!=JOptionPane.OK_OPTION)
		{
			ca = false;
		} else {
			ca = true;
		}
		
		evt.setHandled(ca);
	}
	
	public void handleReOpenApplication(ApplicationEvent evt) 
	{
	}
	
}