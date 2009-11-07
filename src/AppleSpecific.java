import javax.swing.*;

import com.apple.eawt.*;

import globals.*;
import dialogs.*;

class AppleSpecific implements ApplicationListener{

	private FidoFrame f;

	public void answerFinder(FidoFrame g) {
		Application app = new Application();
		app.setEnabledPreferencesMenu(true);
		app.getApplication().addApplicationListener(this);
		f=g;
	}

	public void handleAbout(ApplicationEvent evt) 
	{
		DialogAbout d=new DialogAbout(f);
		d.setVisible(true);
		evt.setHandled(true);

	}
    
    public void handleOpenApplication(ApplicationEvent evt) 
    {
    }
    
    public void handleOpenFile(ApplicationEvent evt) {
    	String file = evt.getFilename();
    	f.Load(file);
    }
    
    public void handlePreferences(ApplicationEvent evt) 
	{	
		f.showPrefs();
	}
	
	public void handlePrintFile(ApplicationEvent evt) 
	{
	}
	
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