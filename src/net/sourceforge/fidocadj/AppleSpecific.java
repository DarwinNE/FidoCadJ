package net.sourceforge.fidocadj;

import javax.swing.*;

import java.util.*;

import com.apple.eawt.*;

import net.sourceforge.fidocadj.*;
import net.sourceforge.fidocadj.dialogs.*;
import net.sourceforge.fidocadj.globals.*;


/** The class AppleSpecific implements a few mechanism for interacting with
	the MacOSX operating system. This class will only be used if the program 
	detects it is being run on a MacOSX operating system.
	This can be a problem when the program is not compiled on a MacOSX 
	operating system, since the com.apple.eawt package is made available 
	only under this platform. You should thus remove each reference to the
	AppleSpecific class in the code when compiling under an alternative
	system. See the README file.

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
    along with FidoCadJ.  If not, see <http://www.gnu.org/licenses/>.

	Copyright 2009-2010 by Davide Bucci
	
*/

class AppleSpecific implements ApplicationListener
{

	/** Create an application listener able to respond to a few Finder events
	
	*/
	public void answerFinder() 
	{
		Application app = new Application();
		app.setEnabledPreferencesMenu(true);
		app.getApplication().addApplicationListener(this);
	}
	
	/** Respond to an user clicking on an About menu.
	
	*/
	public void handleAbout(ApplicationEvent evt) 
	{
		DialogAbout d=new DialogAbout((JFrame)Globals.activeWindow);
		d.setVisible(true);
		evt.setHandled(true);

	}
    /** Respond to an user opening the application.
	
	*/
    public void handleOpenApplication(ApplicationEvent evt) 
    {
    	String file = evt.getFilename();
    	if(file!=null)
    	  ((FidoFrame)Globals.activeWindow).load(file);
    }
    /** Respond to an user double clicking on a FCD file
	
	*/
    public void handleOpenFile(ApplicationEvent evt) 
    {
    	String file = evt.getFilename();
    	((FidoFrame)Globals.activeWindow).load(file);
    }
    
    /** Respond to an user clicking on the Preferences menu.
	
	*/
    public void handlePreferences(ApplicationEvent evt) 
	{	
		((FidoFrame)Globals.activeWindow).showPrefs();
	}
	
	/** Respond to an user wanting to print a particular file.
	*/
	public void handlePrintFile(ApplicationEvent evt) 
	{
		// does nothing
	}
	
	/** Ask for confirmation when quitting.
	
	*/
	public void handleQuit(ApplicationEvent evt) 
	{
		boolean ca = true;		

		//Create a iterator
    	Iterator iterator = Globals.openWindows.iterator();     
    	while (iterator.hasNext()){
      		if(!((FidoFrame)iterator.next()).checkIfToBeSaved()) {
      			ca = false;
      		}
    	}
		evt.setHandled(ca);
	}
	
	public void handleReOpenApplication(ApplicationEvent evt) 
	{
		// does nothing
	}
}