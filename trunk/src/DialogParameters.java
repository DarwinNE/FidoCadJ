import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

/** Allows to create a generic dialog, capable of displaying and let the user
	modify the parameters of a graphic primitive.

   ****************************************************************************
   Version History 

Version   Date           	Author       Remarks
------------------------------------------------------------------------------
1.0     June 2008      		D. Bucci  	First working version
1.2		June 2009			D. Bucci 	Capitalize the first letters                                     

                                     
    Written by Davide Bucci, June 2008, davbucci at tiscali dot it
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

	*/

public class DialogParameters extends JDialog implements ComponentListener 
{
  	private static final int MIN_WIDTH=350;
  	private static final int MIN_HEIGHT=300;
  	
  	// Maximum number of user interface elements of the same type present
  	// in the dialog window.
  	private static final int MAX_ELEMENTS=20;
  	
	public boolean active; // true if the user selected Ok
	
	// Text box array and counter
	private JTextField jtf[];
	private int tc;
	
	// Check box array and counter
	private	JCheckBox jcb[];
	private int cc;
	
	private Vector v;
	
  	
  	/** Programmatically build a dialog frame containing the appropriate
  		elements, in order to let the user modify the characteristics of a 
  		graphic primitive.
  		
  		@param vec a ParameterDescription array containing the value and the
  			description of each parameter that should be edited by the
  			user.
  	
  	*/
  	public DialogParameters (JFrame parent, Vector vec)
  	{
  		super(parent, Globals.messages.getString("Param_opt"), true);
  		
  		v=vec;
  		
  		int ycount=0;
  		
  		jtf=new JTextField[MAX_ELEMENTS];
  		jcb=new JCheckBox[MAX_ELEMENTS];
  		
  		active = false;
  		addComponentListener(this);	
  		setSize(400,300);
      			
		
		GridBagLayout bgl=new GridBagLayout();
		GridBagConstraints constraints=new GridBagConstraints();
		Container contentPane=getContentPane();
		contentPane.setLayout(bgl);
	
		ParameterDescription pd;
		
		int top=0;
		
		JLabel lab;
		
		tc=0;
		cc=0;
		
		for (ycount=0;ycount<v.size();++ycount) {
			pd = (ParameterDescription)v.elementAt(ycount);
			lab=new JLabel(pd.description);
			constraints.weightx=100;
			constraints.weighty=100;
			constraints.gridx=1;
			constraints.gridy=ycount;
			constraints.gridwidth=1;
			constraints.gridheight=1;
			if(ycount==0) 
				top=10;
			else
				top=0;
			
			constraints.insets=new Insets(top,20,0,6);
			
			constraints.fill = GridBagConstraints.VERTICAL;
			constraints.anchor=GridBagConstraints.EAST;
			if (!(pd.parameter instanceof Boolean))
				contentPane.add(lab, constraints);
			

			constraints.anchor=GridBagConstraints.WEST;
			constraints.insets=new Insets(top,0,0,0);
 			constraints.fill = GridBagConstraints.HORIZONTAL;

			if(pd.parameter instanceof Point) {
				jtf[tc]=new JTextField(10);
				jtf[tc].setText(""+((Point)(pd.parameter)).x);
				constraints.weightx=100;
				constraints.weighty=100;
				constraints.gridx=2;
				constraints.gridy=ycount;
				constraints.gridwidth=1;
				constraints.gridheight=1;	

				contentPane.add(jtf[tc++], constraints);
				
				jtf[tc]=new JTextField(10);
				jtf[tc].setText(""+((Point)(pd.parameter)).y);
				constraints.weightx=100;
				constraints.weighty=100;
				constraints.gridx=3;
				constraints.gridy=ycount;
				constraints.gridwidth=1;
				constraints.gridheight=1;	
				constraints.insets=new Insets(top,6,0,20);
				constraints.fill = GridBagConstraints.HORIZONTAL;
				contentPane.add(jtf[tc++], constraints);
			
			} else if(pd.parameter instanceof String) {
				jtf[tc]=new JTextField(24);
				jtf[tc].setText((String)(pd.parameter));
				// If we have a String text field in the first position, its 
				// contents should be evidenced, since it is supposed to be
				// the most important field (e.g. for the AdvText primitive)
				if(ycount==0)
					jtf[tc].selectAll();
				constraints.weightx=100;
				constraints.weighty=100;
				constraints.gridx=2;
				constraints.gridy=ycount;
				constraints.gridwidth=2;
				constraints.gridheight=1;	
				constraints.insets=new Insets(top,0,0,20);
				constraints.fill = GridBagConstraints.HORIZONTAL;

				contentPane.add(jtf[tc++], constraints);
				
			
			} else if(pd.parameter instanceof Boolean) {
				jcb[cc]=new JCheckBox(pd.description);
				jcb[cc].setSelected(((Boolean)(pd.parameter)).booleanValue());
				constraints.weightx=100;
				constraints.weighty=100;
				constraints.gridx=2;
				constraints.gridy=ycount;
				constraints.gridwidth=2;
				constraints.gridheight=1;	
				constraints.insets=new Insets(top,0,0,20);
				constraints.fill = GridBagConstraints.HORIZONTAL;
				contentPane.add(jcb[cc++], constraints);
				
			} else if(pd.parameter instanceof Integer) {
				jtf[tc]=new JTextField(24);
				jtf[tc].setText(((Integer)pd.parameter).toString());
				constraints.weightx=100;
				constraints.weighty=100;
				constraints.gridx=2;
				constraints.gridy=ycount;
				constraints.gridwidth=2;
				constraints.gridheight=1;	
				constraints.insets=new Insets(top,0,0,20);
				constraints.fill = GridBagConstraints.HORIZONTAL;
				contentPane.add(jtf[tc++], constraints);
				
			
			}
		
		}
		
	
		JButton ok=new JButton(Globals.messages.getString("Ok_btn"));
		JButton cancel=new JButton(Globals.messages.getString("Cancel_btn"));
	
		constraints.gridx=0;
		constraints.gridy=ycount;
		constraints.gridwidth=4;
		constraints.gridheight=1;
		constraints.anchor=GridBagConstraints.EAST;
		constraints.insets=new Insets(6,20,20,20);

		Box b=Box.createHorizontalBox();
		b.add(Box.createHorizontalGlue());
		b.add(ok);
		b.add(cancel);
		//b.add(Box.createHorizontalStrut(20));
		contentPane.add(b, constraints);			// Add cancel button	
		
		
		
		ok.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				
				

				try{
					int ycount;
					ParameterDescription pd;
					tc=0;
					cc=0;
					
					for (ycount=0;ycount<v.size();++ycount) {
						pd = (ParameterDescription)v.elementAt(ycount);
			
						if(pd.parameter instanceof Point) {
							((Point)(pd.parameter)).x=Integer.parseInt(
								jtf[tc++].getText());
							((Point)(pd.parameter)).y=Integer.parseInt(
								jtf[tc++].getText());
						} else if(pd.parameter instanceof String) {
							pd.parameter=jtf[tc++].getText();
						} else if(pd.parameter instanceof Boolean) {
							pd.parameter=new Boolean(jcb[cc++].isSelected());
						} else if(pd.parameter instanceof Integer) {
							pd.parameter=new Integer(Integer.parseInt(
								jtf[tc++].getText()));
						}	
					}
				} catch (NumberFormatException E) 
				{
					JOptionPane.showMessageDialog(null,
                        Globals.messages.getString("Format_invalid"),
                        "",
                        JOptionPane.INFORMATION_MESSAGE);
                    return;
				}
				
                active = true;
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
  
  	/** Get a ParameterDescription vector describing the characteristics 
  		modified by the user.
  		
  		@return a ParameterDescription vector describing each parameter.
  	
  	*/
  	Vector getCharacteristics()
  	{
		return v;
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

}