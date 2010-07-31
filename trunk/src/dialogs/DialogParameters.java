package dialogs;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

import globals.*;
import dialogs.*;

/** Allows to create a generic dialog, capable of displaying and let the user
	modify the parameters of a graphic primitive.
	The idea is that the dialog  uses a ParameterDescripion vector which
	contains all the elements, their description as well as the type.
	Depending on the contents of the array, the window will be created 
	automagically.

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
	*/

public class DialogParameters extends JDialog implements ComponentListener 
{
  	private static final int MIN_WIDTH=350;
  	private static final int MIN_HEIGHT=300;
  	private static final int MAX=20;
  	
  	// Maximum number of user interface elements of the same type present
  	// in the dialog window.
  	private static final int MAX_ELEMENTS=100;
  	
	public boolean active; // true if the user selected Ok
	
	// Text box array and counter
	private JTextField jtf[];
	private int tc;
	
	// Check box array and counter
	private	JCheckBox jcb[];
	private int cc;
	private boolean extStrict;	// Strict compatibility
	
	private JComboBox jco[];
	private int co;
	
	private Vector v;
	
	private ArrayList layers;
	
  	/** Programmatically build a dialog frame containing the appropriate
  		elements, in order to let the user modify the characteristics of a 
  		graphic primitive.
  		
  		@param vec a ParameterDescription array containing the value and the
  			description of each parameter that should be edited by the
  			user.
  	
  	*/
  	public DialogParameters (JFrame parent, Vector vec, boolean strict, ArrayList l)
  	{
  		super(parent, Globals.messages.getString("Param_opt"), true);
  		
  		v=vec;
  		layers = l;
  		
  		int ycount=0;
  		
  		// We create dynamically all the needed elements.
  		// For this reason, we work on arrays of the potentially useful Swing
  		// objects.
  		
  		jtf=new JTextField[MAX_ELEMENTS];
  		jcb=new JCheckBox[MAX_ELEMENTS];
  		jco=new JComboBox[MAX_ELEMENTS];
  		
  		active = false;
  		addComponentListener(this);	
  		setSize(400,300);
      			
		GridBagLayout bgl=new GridBagLayout();
		GridBagConstraints constraints=new GridBagConstraints();
		Container contentPane=getContentPane();
		contentPane.setLayout(bgl);
		extStrict = strict;
	
		ParameterDescription pd;
		
		int top=0;
		
		JLabel lab;
		
		tc=0;
		cc=0;
		co=0;
		
		// We process all parameter passed. Depending on its type, a 
		// corresponding interface element will be created.
		// A symmetrical operation is done when validating parameters.
		
		for (ycount=0;ycount<v.size();++ycount) {
			if (ycount>MAX)
				break;
				
			pd = (ParameterDescription)v.elementAt(ycount);
			
			// We do not need to store label objects, since we do not need
			// to retrieve data from them.
			
			lab=new JLabel(pd.description);
			constraints.weightx=100;
			constraints.weighty=100;
			constraints.gridx=1;
			constraints.gridy=ycount;
			constraints.gridwidth=1;
			constraints.gridheight=1;
			
			// The first element needs a little bit more space at the top.
			if(ycount==0) 
				top=10;
			else
				top=0;
			
			// Here we configure the grid layout
			
			constraints.insets=new Insets(top,20,0,6);
			
			constraints.fill = GridBagConstraints.VERTICAL;
			constraints.anchor=GridBagConstraints.EAST;
			lab.setEnabled(!(pd.isExtension && extStrict));

			if (!(pd.parameter instanceof Boolean))
				contentPane.add(lab, constraints);
			

			constraints.anchor=GridBagConstraints.WEST;
			constraints.insets=new Insets(top,0,0,0);
 			constraints.fill = GridBagConstraints.HORIZONTAL;

			// Now, depending on the type of parameter we create interface
			// elements and we populate the dialog.
			
			if(pd.parameter instanceof Point) {
				jtf[tc]=new JTextField(10);
				jtf[tc].setText(""+((Point)(pd.parameter)).x);
				constraints.weightx=100;
				constraints.weighty=100;
				constraints.gridx=2;
				constraints.gridy=ycount;
				constraints.gridwidth=1;
				constraints.gridheight=1;	
				// Disable FidoCadJ extensions in the strict compatibility mode
				jtf[tc].setEnabled(!(pd.isExtension && extStrict));

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
     			jtf[tc].setEnabled(!(pd.isExtension && extStrict));
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
				jtf[tc].setEnabled(!(pd.isExtension && extStrict));

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
				jcb[cc].setEnabled(!(pd.isExtension && extStrict));
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
				jtf[tc].setEnabled(!(pd.isExtension && extStrict));
				contentPane.add(jtf[tc++], constraints);
			
			} else if(pd.parameter instanceof Font) {
				GraphicsEnvironment gE;  
				gE = GraphicsEnvironment.getLocalGraphicsEnvironment();
				String[] s = gE.getAvailableFontFamilyNames();  
				jco[co]=new JComboBox();
      			//System.out.println("%"+((Font)pd.parameter).getFamily()+"%");

    			for (int i = 0; i < s.length; ++i) {
      				jco[co].addItem(s[i]);
      				if (s[i].equals(((Font)pd.parameter).getFamily()))
      					jco[co].setSelectedIndex(i);
     			}
				constraints.weightx=100;
				constraints.weighty=100;
				constraints.gridx=2;
				constraints.gridy=ycount;
				constraints.gridwidth=2;
				constraints.gridheight=1;	
				constraints.insets=new Insets(top,0,0,20);
				constraints.fill = GridBagConstraints.HORIZONTAL;
				jco[co].setEnabled(!(pd.isExtension && extStrict));
				contentPane.add(jco[co++], constraints);
			} else if(pd.parameter instanceof LayerInfo) {
				GraphicsEnvironment gE;  
				gE = GraphicsEnvironment.getLocalGraphicsEnvironment();
				jco[co]=new JComboBox(new Vector(layers));
				jco[co].setSelectedIndex(((LayerInfo)pd.parameter).layer);
   				jco[co].setRenderer( new LayerCellRenderer());

				constraints.weightx=100;
				constraints.weighty=100;
				constraints.gridx=2;
				constraints.gridy=ycount;
				constraints.gridwidth=2;
				constraints.gridheight=1;	
				constraints.insets=new Insets(top,0,0,20);
				constraints.fill = GridBagConstraints.HORIZONTAL;
				jco[co].setEnabled(!(pd.isExtension && extStrict));
				contentPane.add(jco[co++], constraints);

			}else if(pd.parameter instanceof ArrowInfo) {
				GraphicsEnvironment gE;  
				gE = GraphicsEnvironment.getLocalGraphicsEnvironment();
				jco[co]=new JComboBox();
				jco[co].addItem(new ArrowInfo(0));
				jco[co].addItem(new ArrowInfo(1));
				jco[co].addItem(new ArrowInfo(2));
				jco[co].addItem(new ArrowInfo(3));
				
				jco[co].setSelectedIndex(((ArrowInfo)pd.parameter).style);
   				jco[co].setRenderer( new ArrowCellRenderer());

				constraints.weightx=100;
				constraints.weighty=100;
				constraints.gridx=2;
				constraints.gridy=ycount;
				constraints.gridwidth=2;
				constraints.gridheight=1;	
				constraints.insets=new Insets(top,0,0,20);
				constraints.fill = GridBagConstraints.HORIZONTAL;
				jco[co].setEnabled(!(pd.isExtension && extStrict));
				contentPane.add(jco[co++], constraints);

			} else if(pd.parameter instanceof DashInfo) {
				GraphicsEnvironment gE;  
				gE = GraphicsEnvironment.getLocalGraphicsEnvironment();
				jco[co]=new JComboBox();
				
				for(int k=0; k<Globals.dashNumber; ++k) {
					jco[co].addItem(new DashInfo(k));
				}
				
				jco[co].setSelectedIndex(((DashInfo)pd.parameter).style);
   				jco[co].setRenderer( new DashCellRenderer());

				constraints.weightx=100;
				constraints.weighty=100;
				constraints.gridx=2;
				constraints.gridy=ycount;
				constraints.gridwidth=2;
				constraints.gridheight=1;	
				constraints.insets=new Insets(top,0,0,20);
				constraints.fill = GridBagConstraints.HORIZONTAL;
				jco[co].setEnabled(!(pd.isExtension && extStrict));
				contentPane.add(jco[co++], constraints);
			}
		}
		// Put the OK and Cancel buttons and make them active.
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
		b.add(cancel);
		b.add(Box.createHorizontalStrut(12));
		ok.setPreferredSize(cancel.getPreferredSize());
		b.add(ok);
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
					co=0;
					
					// Here we read all the contents of the interface and we
					// update the contents of the parameter description array.
					
					for (ycount=0;ycount<v.size();++ycount) {
						if (ycount>MAX)
							break;
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
						} else if(pd.parameter instanceof Font) {
		      				pd.parameter=new Font((String)jco[co++].getSelectedItem(), Font.PLAIN, 12);
      					} else if(pd.parameter instanceof LayerInfo) {
		      				pd.parameter=new LayerInfo(jco[co++].getSelectedIndex());
		      			} else if(pd.parameter instanceof ArrowInfo) {
		      				pd.parameter=new ArrowInfo(jco[co++].getSelectedIndex());
		      			} else if(pd.parameter instanceof DashInfo) {
		      				pd.parameter=new DashInfo(jco[co++].getSelectedIndex());
		      			}
     				}
				} catch (NumberFormatException E) 
				{
					// Error detected. Probably, the user has entered an 
					// invalid string when FidoCadJ was expecting a numerical
					// input.
					
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
  	public Vector getCharacteristics()
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