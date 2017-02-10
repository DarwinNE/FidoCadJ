package net.sourceforge.fidocadj.dialogs;

import javax.swing.*;

import net.sourceforge.fidocadj.globals.*;
import net.sourceforge.fidocadj.dialogs.mindimdialog.MinimumSizeDialog;


/** The class DialogAttachImage allows to determine which image has to be
    attached and shown as a background (for retracing/vectorization purposes).

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

    Copyright 2017 by Davide Bucci
    </pre>

    @author Davide Bucci
*/

public class DialogAttachImage extends MinimumSizeDialog
{
    /** Standard constructor.
        @param parent the dialog parent
    */
    public DialogAttachImage(JFrame parent)
    {
        super(500, 450, parent, Globals.messages.getString("Attach_image_t"),
            true);
    }
}