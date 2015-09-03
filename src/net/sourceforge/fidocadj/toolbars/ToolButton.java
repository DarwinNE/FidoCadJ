package net.sourceforge.fidocadj.toolbars;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import net.sourceforge.fidocadj.circuit.*;
import net.sourceforge.fidocadj.globals.*;

import java.util.*;
import java.net.*;

/**
    ToolButton class
 
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
 
    Copyright 2008-2014 by Davide Bucci
    </pre>
 
 
    This class contains a constructor, which allows to create buttons for the 
    FidoCadJ toolbar, {@link ToolbarTools}.
    Having the button created in this class allows to add a button in the 
    <code>ToolbarTools</code> class by defining most of the button parameters 
    on a single line. This also avoids code repetition and gives more 
    flexibility to the design the buttons.
 
    @author Davide Bucci & Jose Emilio Munoz
 
*/

public class ToolButton {
    
    private final JToggleButton toolButton;
    
    /** Class Constructor: Creates a new <code>JToggleButton</code> that has 
        the specified text and image, and that is initially unselected. It also 
        assigns an <code>actionCommand</code> and a <code>toolTip</code> to the 
        button.
     
        @param image Icon image file. 
        @param toolText Button text, a <code>Globals</code> message.
        @param actionCommand Button action command.
        @param toolTip Button description/tip, a <code>Globals</code> message.
    */
    
    public ToolButton (String image, 
                       String toolText, 
                       String actionCommand, 
                       String toolTip) 
    {
        
        String base = ToolbarTools.getBase();
        boolean showText = ToolbarTools.getShowText();
        
        URL url = ToolbarTools.class.getResource(base+image);
        
        toolButton = new JToggleButton((showText?Globals.messages.
                                        getString(toolText):""),
                                       new ImageIcon(url));
        
        toolButton.setActionCommand(actionCommand);
        toolButton.setToolTipText(Globals.messages.getString(toolTip));
        toolButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        toolButton.setHorizontalTextPosition(SwingConstants.CENTER); 
        
        // This property is very useful when using the Quaqua style 
        // with Apple Macintosh computers.
        toolButton.putClientProperty("Quaqua.Button.style","toolBarTab");
        
    }
    
    /** With this method, the button can be passed to the 
        <code>ToolbarTools</code> class as a <code>JToggleButton</code>.
     
        @return toolButton
    */
    public JToggleButton getToolButton() 
    {
        return toolButton;
    }
}