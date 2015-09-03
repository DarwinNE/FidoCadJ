package net.sourceforge.fidocadj;

import java.awt.*;
import java.applet.*;

import javax.swing.*;

import net.sourceforge.fidocadj.globals.*;

import java.awt.event.*;
import java.io.*;
import java.util.*;



/** FidoReadApplet.java v.2.0

This is the main file for the FidoCadJ reader applet.

<pre>

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

    Copyright march 2007-2014 by Davide Bucci
</pre>

@author Davide Bucci
*/

public class FidoCadApplet extends JApplet implements ActionListener
{
    // Increment this version number each time an important modification of
    // this class has been done.
    private static final long serialVersionUID = 10L;
    
    private FidoFrame popFrame;

    
    public void init()
    {
        // Here we create the main window object
        Button fidoButton = new Button("Launch FidoCadJapplet");
        fidoButton.addActionListener(this);
        getContentPane().add(fidoButton);
        
        popFrame=new FidoFrame(false, null);
        
        // FidoCadJ will try to determine the current locale configuration
        // in order to load the corresponding resource file and show an 
        // interface in the same language as the host operating system.
        
        popFrame.currentLocale = Locale.getDefault();
        
        // The following code has changed from version 0.20.1.
        // This way, it should tolerate systems in which resource file for the
        // current locale is not available. The English interface will be shown.
        
        try {
            // Try to load the program with the current locale
            Globals.messages = new 
                AccessResources (Utf8ResourceBundle.getBundle("MessagesBundle", 
               popFrame.currentLocale));                             
            
        } catch(MissingResourceException mre) {
            try {
                // If it does not work, try to use the standard English
                Globals.messages = new 
                AccessResources (ResourceBundle.getBundle("MessagesBundle",
                    new Locale("en", "US")));
                System.out.println("No locale available, sorry... "+
                    "interface will be in English");
            } catch(MissingResourceException mre1) {
                // Give up!!!
                JOptionPane.showMessageDialog(null,
                    "Unable to find language localization files: " + mre1);
                System.exit(1);
            }
        }     
             

        Globals.useNativeFileDialogs=false;
        Globals.useMetaForMultipleSelection=false;
        
        if (System.getProperty("os.name").startsWith("Mac")) {
            // From what I know, only Mac users expect to use the Command (meta)
            // key for shortcuts, while others will use Control.
            Globals.shortcutKey=InputEvent.META_MASK;
            Globals.useMetaForMultipleSelection=true;
            
            // Standard dialogs are vastly better on MacOSX than the Swing ones
            Globals.useNativeFileDialogs=true;

        } else {
            Globals.shortcutKey=InputEvent.CTRL_MASK;
        }
        
        /*******************************************************************
                        END OF THE PLATFORM SELECTION CODE
        *******************************************************************/
        
        popFrame.init();
    }
    

    public void actionPerformed(ActionEvent evt)
    {
        if(popFrame.isVisible())    
            popFrame.setVisible(false);
        else
            popFrame.setVisible(true);
        
    }
    
}