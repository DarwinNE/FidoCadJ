package dialogs;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.*;
import java.net.*;

import globals.*;
import toolbars.*;

/** 
<pre>
Shows a rather standard "About" dialog. Nothing more exotic than showing the 
nice icon of the program, its name as well as three lines of description.


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

    Copyright 2007-2009 by Davide Bucci
</pre>
    @author Davide Bucci
    
*/
    
public class DialogAbout extends JFrame implements ComponentListener 
{
	// The minimu size in pixels.
    private static final int MIN_WIDTH=300;
    private static final int MIN_HEIGHT=250;
    
    
    private boolean export;     // Indicates that the export should be done
  
  	/**	Required for the implementation of the ComponentListener interface.
  		In this case, prevents from resizing the dialog in a size which is
  		too small.
  	*/
    public void componentResized(ComponentEvent e) 
    {
        int width = getWidth();
        int height = getHeight();
        
        boolean resize = false;
        if (width < MIN_WIDTH) {
            resize = true;
            width = MIN_WIDTH;
         }
         if (height < MIN_HEIGHT) {
            resize = true;
            height = MIN_HEIGHT;
         }
         if (resize) {
            setSize(width, height);
         }
    }
    public void componentMoved(ComponentEvent e) 
    {
    }
    public void componentShown(ComponentEvent e) 
    {
    }
    public void componentHidden(ComponentEvent e) 
    {
    }
    
    /** Standard constructor: it needs the parent frame.
    
        @param parent the dialog's parent
    */
    public DialogAbout (JFrame parent)
    {
        // super(parent,"", true);
        super("");
        DialogUtil.center(this, .30,.35,350,300);
		setResizable(false);
        addComponentListener(this); 
        
 		// Shows the icon of the program and then three lines read from the
 		// resources which describe the software and give the credits.
 		
        GridBagLayout bgl=new GridBagLayout();
        GridBagConstraints constraints=new GridBagConstraints();
        Container contentPane=getContentPane();
        contentPane.setLayout(bgl);

        URL url=DialogAbout.class.getResource(
            "program_icons/icona_fidocadj_128x128.png");
        JLabel icon=new JLabel("");
        constraints.weightx=100;
        constraints.weighty=100;
        constraints.gridx=0;
        constraints.gridy=0;
        constraints.gridwidth=1;
        constraints.gridheight=1;
        constraints.anchor=GridBagConstraints.CENTER;
        constraints.insets=new Insets(10,20,0,20);

        if (url != null) icon.setIcon(new ImageIcon(url));
        contentPane.add(icon, constraints); 
        
        
        JLabel programName=new JLabel("FidoCadJ");
        
        Font f=new Font("Lucida Grande",Font.BOLD,18);
        
        programName.setFont(f);
        constraints.gridx=0;
        constraints.gridy=1;
        constraints.gridwidth=1;
        constraints.gridheight=1;
        constraints.anchor=GridBagConstraints.CENTER;
        constraints.insets=new Insets(0,20,0,20);
        contentPane.add(programName, constraints);  
        
        
        JLabel programVersion=new JLabel(Globals.messages.getString("Version")+Globals.version);
        constraints.gridx=0;
        constraints.gridy=2;
        constraints.gridwidth=1;
        constraints.gridheight=1;
        constraints.anchor=GridBagConstraints.CENTER;
        contentPane.add(programVersion, constraints);
        
        JLabel programDescription1=new JLabel(Globals.messages.getString("programDescription1"));
        constraints.gridx=0;
        constraints.gridy=3;
        constraints.gridwidth=1;
        constraints.gridheight=1;
        constraints.anchor=GridBagConstraints.CENTER;
        contentPane.add(programDescription1, constraints);
        
        JLabel programDescription2=new JLabel(Globals.messages.getString("programDescription2"));
        constraints.gridx=0;
        constraints.gridy=4;
        constraints.gridwidth=1;
        constraints.gridheight=1;
        constraints.anchor=GridBagConstraints.CENTER;
        constraints.insets=new Insets(0,20,20,20);

        contentPane.add(programDescription2, constraints);
        
        JLabel programDescription3=new JLabel(Globals.messages.getString("programDescription3"));
        constraints.gridx=0;
        constraints.gridy=5;
        constraints.gridwidth=1;
        constraints.gridheight=1;
        constraints.anchor=GridBagConstraints.CENTER;
        constraints.insets=new Insets(0,20,20,20);

        contentPane.add(programDescription3, constraints);
        
    }
}