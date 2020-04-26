package net.sourceforge.fidocadj;

import java.awt.*;
import java.awt.desktop.*;

import javax.swing.*;

import java.util.*;

import net.sourceforge.fidocadj.*;
import net.sourceforge.fidocadj.dialogs.*;
import net.sourceforge.fidocadj.globals.*;

/** The class ADesktopIntegration implements a few mechanism for interacting
    with the operating system. This class requires Java 9 at least, with the
    (much welcomed from my part) java.awt.Desktop object. Previously, the
    integration with the OS was handled only on MacOSX, thanks to a class
    called AppleSpecific, relying on the obsolete com.apple.eawt package.

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
    along with FidoCadJ. If not,
    @see <a href=http://www.gnu.org/licenses/>http://www.gnu.org/licenses/</a>.

    Copyright 2020 by Davide Bucci
    </pre>
*/

class ADesktopIntegration implements AboutHandler, PreferencesHandler, 
    QuitHandler, OpenFilesHandler
{
    ADesktopIntegration()
    {
    }
    
    public void registerActions()
    {
        if(!Desktop.isDesktopSupported())
            return;

        Desktop d=Desktop.getDesktop();
        d.setAboutHandler(this);
        d.setPreferencesHandler(this);
        d.setQuitHandler(this);
        d.setOpenFileHandler(this);
    }

    /** Respond to an user double clicking on a FCD file
        @param e event referring for application.
    */
    public void openFiles (OpenFilesEvent e)
    {
        String file = e.getFiles().get(0).getAbsolutePath();
        System.out.println("Open file:"+file);
        ((FidoFrame)Globals.activeWindow).getFileTools().load(file);
    }
    /** Respond to an user clicking on an About menu.
        @param e event referring for application.
    */
    public void handleAbout​(AboutEvent e)
    {
        DialogAbout d=new DialogAbout((JFrame)Globals.activeWindow);
        d.setVisible(true);
    }

    /** Respond to an user clicking on the Preferences menu.
        @param e event referring for application.
    */
    public void handlePreferences (PreferencesEvent e)
    {
        ((FidoFrame)Globals.activeWindow).showPrefs();
    }

    /** Ask for confirmation when quitting.
        @param e event referring for application.
        @param response the type of the response (quit or abort).
    */
    public void	handleQuitRequestWith​(QuitEvent e, QuitResponse response)
    {
        boolean ca = true;
        // Create a iterator to cycle through all open windows and ask for
        // confirmation.
        Iterator iterator = Globals.openWindows.iterator();
        FidoFrame fff;
        while (iterator.hasNext()) {
            if((fff=(FidoFrame)iterator.next()).getFileTools().
                checkIfToBeSaved())
            {
                fff.closeThisFrame();
            } else {
                ca = false;
            }
        }

        if(ca)
            response.performQuit();
        else
            response.cancelQuit();
    }
}