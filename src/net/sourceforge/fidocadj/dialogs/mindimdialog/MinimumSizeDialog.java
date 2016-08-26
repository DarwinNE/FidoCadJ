package net.sourceforge.fidocadj.dialogs.mindimdialog;

import javax.swing.*;
import java.awt.event.*;

/** Implements a dialog having a minimum size.
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

    Copyright 2015 by Davide Bucci
    </pre>
    @author Davide Bucci
*/
public class MinimumSizeDialog extends JDialog implements ComponentListener
{
    // The minimum size in pixels.
    private final int min_width;
    private final int min_height;

    /** Constructor.
        @param min_w minimum width in pixels.
        @param min_h minimum height in pixels.
        @param parent the parent frame.
        @param title the tile of the dialog.
        @param modal true if it is a modal dialog, false otherwise.
    */
    public MinimumSizeDialog(
        int min_w, int min_h,
        JFrame parent, String title,
        boolean modal)
    {
        super(parent, title, modal);
        min_width=min_w;
        min_height=min_h;
    }

    /** Required for the implementation of the ComponentListener interface.
        In this case, prevents from resizing the dialog in a size which is
        too small.
        @param e the component event which happened.
    */
    public void componentResized(ComponentEvent e)
    {
        int width = getWidth();
        int height = getHeight();

        boolean resize = false;
        if (width < min_width) {
            resize = true;
            width = min_width;
        }
        if (height < min_height) {
            resize = true;
            height = min_height;
        }
        if (resize) {
            setSize(width, height);
        }
    }

    /** Required for the implementation of the ComponentListener interface.
        @param e the component event which happened.
    */
    public void componentMoved(ComponentEvent e)
    {
        // Nothing to do
    }

    /** Required for the implementation of the ComponentListener interface.
        @param e the component event which happened.
    */
    public void componentShown(ComponentEvent e)
    {
        // Nothing to do
    }

    /** Required for the implementation of the ComponentListener interface.
        @param e the component event which happened.
    */
    public void componentHidden(ComponentEvent e)
    {
        // Nothing to do
    }
}