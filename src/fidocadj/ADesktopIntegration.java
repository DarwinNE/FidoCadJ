package fidocadj;

import java.awt.*;
import java.awt.desktop.*;

import javax.swing.*;

import fidocadj.dialogs.DialogAbout;
import fidocadj.globals.Globals;

/** The class ADesktopIntegration implements a few mechanism for interacting
 * with the operating system. This class requires Java 9 at least, with the
 * (much welcomed from my part) java.awt.Desktop object. Previously, the
 * integration with the OS was handled only on MacOSX, thanks to a class
 * called AppleSpecific, relying on the obsolete com.apple.eawt package.
 *
 * <pre>
 * This file is part of FidoCadJ.
 *
 * FidoCadJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FidoCadJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FidoCadJ. If not,
 * @see <a href=http://www.gnu.org/licenses/>http://www.gnu.org/licenses/</a>.
 *
 * Copyright 2020-2023 by Davide Bucci
 * </pre>
 */
public class ADesktopIntegration implements AboutHandler,
                                            PreferencesHandler,
                                            QuitHandler,
                                            OpenFilesHandler
{

    private boolean handleAbout;         // True if the About action is handled
    private boolean handlePreferences;   // True if the Pref action is handled

    ADesktopIntegration()
    {
        // Empty constructor.
    }

    /** Check if some actions are made available by the operating system and if
     * it is the case, register them.
     * For example, MacOS usually inserts the About and Preferences menus in a
     * specific place in the "FidoCadJ" menu, that does not exist in other
     * platforms. This means that the OS needs to know what to call when one
     * of those two actions is selected. This is given to the operating system
     * by this routine.
     */
    public void registerActions()
    {
        if (!Desktop.isDesktopSupported()) {
            return;
        }

        handleAbout = true;
        handlePreferences = true;
        Desktop d = Desktop.getDesktop();
        try {
            d.setOpenFileHandler(this);
            d.setQuitHandler(this);
        } catch (UnsupportedOperationException eE) {
            // This can be ignored, we are going to live without it.
            /*
             * System.err.println(
             * "Warning: unsupported exception while setting handlers.");
             */
        }
        try {
            d.setAboutHandler(this);
        } catch (UnsupportedOperationException eE) {
            handleAbout = false;
        }
        try {
            d.setPreferencesHandler(this);
        } catch (UnsupportedOperationException eE) {
            handlePreferences = false;
        }
    }

    /** Respond to an user double clicking on a FCD file
     *
     * @param e event referring for application.
     */
    @Override
    public void openFiles(OpenFilesEvent e)
    {
        String file = e.getFiles().get(0).getAbsolutePath();
        ((FidoFrame) Globals.activeWindow).getFileTools().load(file);
    }

    /** Respond to an user clicking on an About menu.
     *
     * @param e event referring for application.
     */
    @Override
    public void handleAbout(AboutEvent e)
    {
        DialogAbout d = new DialogAbout((JFrame) Globals.activeWindow);
        d.setVisible(true);
    }

    /** Respond to an user clicking on the Preferences menu.
     *
     * @param e event referring for application.
     */
    @Override
    public void handlePreferences(PreferencesEvent e)
    {
        ((FidoFrame) Globals.activeWindow).showPrefs();
    }

    /** Ask for confirmation when quitting.
     *
     * @param e event referring for application.
     * @param response the type of the response (quit or abort).
     */
    @Override
    public void handleQuitRequestWith(QuitEvent e,
            QuitResponse response)
    {
        boolean ca = true;
        // I tried with an iterator, but when closing windows the map is
        // changed and the iterator does not like that at all.
        // This method seems safer and avoids raising exception
        // java.util.ConcurrentModificationException (#179).
        Object[] windowArray = Globals.openWindows.toArray();
        FidoFrame fff;
        /*
         * for(int i=0; i<windowArray.length;++i) { */
        for (Object ff : windowArray) {
            fff = (FidoFrame) ff;
            if (fff.getFileTools().checkIfToBeSaved()) {
                fff.closeThisFrame();
            } else {
                ca = false;
            }
        }

        if (ca) {
            response.performQuit();
        } else {
            response.cancelQuit();
        }
    }

    /**
     Check if the About action is handled.

     @return true if the About action is handled, false otherwise.
     */
    public boolean getHandleAbout()
    {
        return handleAbout;
    }

    /**
     Check if the Preferences action is handled.

     @return true if the Preferences action is handled, false otherwise.
     */
    public boolean getHandlePreferences()
    {
        return handlePreferences;
    }
}
