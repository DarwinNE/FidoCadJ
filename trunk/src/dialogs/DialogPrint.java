package dialogs;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.*;

import globals.*;


/** DialogPrint.java v.1.3

   Choose file format, size and options of the graphic exporting.
   The class dialogPrint implements a modal dialog to select printing options.
	

<pre>
   ****************************************************************************
   Version History 

Version   Date           Author       Remarks
------------------------------------------------------------------------------
1.0     February 2008		D. Bucci    First working version
1.1		June 2008		    D. Bucci	A few more options
1.2	    July 2008			D. Bucci	idem.
1.3		June 2009			D. Bucci 	Capitalize the first letters                                     

                                 

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
	
	@author Davide Bucci
	@version 1.3 June 2009
	
*/
	
public class DialogPrint extends JDialog implements ComponentListener 
{
  	private static final int MIN_WIDTH=400;
  	private static final int MIN_HEIGHT=350;
  	
  	private JCheckBox mirror_CB;
  	private JCheckBox fit_CB;
  	private JCheckBox bw_CB;
	private JCheckBox landscape_CB;
  	
  	private boolean export;		// Indicates that the export should be done
  
  
  
	/** Standard constructor: it needs the parent frame.
  		@param parent the dialog's parent
  	*/
  	public DialogPrint (JFrame parent)
  	{
  		super(parent,Globals.messages.getString("Print_dlg"), true);
  		addComponentListener(this);	
  		export=false;
  		
  		
		GridBagLayout bgl=new GridBagLayout();
		GridBagConstraints constraints=new GridBagConstraints();
		Container contentPane=getContentPane();
		contentPane.setLayout(bgl);

  				
		constraints.insets.right=30;

		
		JLabel empty=new JLabel("  ");
		constraints.weightx=100;
		constraints.weighty=100;
		constraints.gridx=0;
		constraints.gridy=0;
		constraints.gridwidth=1;
		constraints.gridheight=1;	
		contentPane.add(empty, constraints);			// Add "   " label
		
		JLabel empty1=new JLabel("  ");
		constraints.weightx=100;
		constraints.weighty=100;
		constraints.gridx=3;
		constraints.gridy=0;
		constraints.gridwidth=1;
		constraints.gridheight=1;	
		contentPane.add(empty1, constraints);			// Add "   " label
		
		mirror_CB=new JCheckBox(Globals.messages.getString("Mirror"));
		constraints.gridx=1;
		constraints.gridy=0;
		constraints.gridwidth=2;
		constraints.gridheight=1;
		constraints.anchor=GridBagConstraints.WEST;
		contentPane.add(mirror_CB, constraints);		// Add Print Mirror cb
		
		fit_CB=new JCheckBox(Globals.messages.getString("FitPage"));
		constraints.gridx=1;
		constraints.gridy=1;
		constraints.gridwidth=2;
		constraints.gridheight=1;
		constraints.anchor=GridBagConstraints.WEST;
		contentPane.add(fit_CB, constraints);		// Add Fit to page cb
		
		bw_CB=new JCheckBox(Globals.messages.getString("B_W"));
		constraints.gridx=1;
		constraints.gridy=2;
		constraints.gridwidth=2;
		constraints.gridheight=1;
		constraints.anchor=GridBagConstraints.WEST;
		contentPane.add(bw_CB, constraints);		// Add BlackWhite cb
		
		landscape_CB=new JCheckBox(Globals.messages.getString("Landscape"));
		constraints.gridx=1;
		constraints.gridy=3;
		constraints.gridwidth=2;
		constraints.gridheight=1;
		constraints.anchor=GridBagConstraints.WEST;
		contentPane.add(landscape_CB, constraints);		// Add landscape cb
		
		
		
		JButton ok=new JButton(Globals.messages.getString("Ok_btn"));
		JButton cancel=new JButton(Globals.messages.getString("Cancel_btn"));
	
		constraints.gridx=0;
		constraints.gridy=4;
		constraints.gridwidth=4;
		constraints.gridheight=1;
		constraints.anchor=GridBagConstraints.EAST;
		Box b=Box.createHorizontalBox();
		b.add(Box.createHorizontalGlue());
		b.add(ok);
		b.add(cancel);
		b.add(Box.createHorizontalStrut(20));
		contentPane.add(b, constraints);			// Add cancel button	
		
		
		
		ok.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				export=true;
				setVisible(false);
			}
		});
		cancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				setVisible(false);
			}
		});
		// Here is an action in which the dialog is closed

      	AbstractAction cancelAction = new AbstractAction ()
      	{
        	public void actionPerformed (ActionEvent e)
         	{
            	setVisible(false);
        	}
      	};
		DialogUtil.addCancelEscape (this, cancelAction);
		pack();
		DialogUtil.center(this);
		getRootPane().setDefaultButton(ok);
  	}  
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
  	
  
  	public boolean getMirror()
  	{
  		return mirror_CB.isSelected();
  	}
  	
  	
  	public boolean getFit()
  	{
  		return fit_CB.isSelected();
  	}
  	
  	public boolean getLandscape()
  	{
  		return landscape_CB.isSelected();
  	}
  	public boolean getBW()
  	{
  		return bw_CB.isSelected();
  	}
  	
  	
  	public void setMirror(boolean m)
  	{
  		mirror_CB.setSelected(m);
  	}
  	
  	
  	public void setFit(boolean f)
  	{
  		fit_CB.setSelected(f);
  	}
  	
  	public void setLandscape(boolean l)
  	{
  		landscape_CB.setSelected(l);
  	}	
  	
  	public void setBW(boolean l)
  	{
  		bw_CB.setSelected(l);
  	}	
  
  	/** Indicates that the printing should be done: the user selected the "ok"
  		button 
  		
  		@return a boolean value which indicates if the printing should be done
  	*/
  	public boolean shouldPrint()
  	{
 		return export;
  	}  


}