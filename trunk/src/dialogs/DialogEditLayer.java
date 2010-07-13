package dialogs;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import javax.swing.event.*;
import javax.swing.colorchooser.*;


import globals.*;
import layers.*;

/** DialogEditLayer.java v.1.3

    The class dialogEditLayer allows to choose the style, visibility and
    description of the current layer.

   ****************************************************************************
   Version History 

Version   Date           Author       Remarks
------------------------------------------------------------------------------
1.0     December 2007       D. Bucci    First working version
1.1     January 2008        D. Bucci    Internationalized
1.2     June 2008           D. Bucci    Better resizing, OK/Cancel actions
1.3     June 2009           D. Bucci    Capitalize the first letters                                     


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
    @version 1.3 June 2009
    
    */
    
public class DialogEditLayer extends JDialog implements ComponentListener
{
    // Minimum size of the dialog window
    
    private static final int MIN_WIDTH=500;
    private static final int MIN_HEIGHT=450;
    static final int ALPHA_MIN = 0;
	static final int ALPHA_MAX = 100;
    
    private JButton color;  
    private JColorChooser tcc;
    private JCheckBox visibility;
    private JTextField description;
    private JSlider opacity;
    private boolean active;             // true if the user selected ok

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
    
    

    /** Standard constructor.
        @param parent the dialog parent
        @param l a LayerDesc containing the layer's attributes 
    */
    public DialogEditLayer (JFrame parent, LayerDesc l)
    {
        super(parent, Globals.messages.getString("Layer_options")+
            l.getDescription(), true);
        
        addComponentListener(this);
        active=false;
        
        Container contentPane=getContentPane();
        
        GridBagLayout bgl=new GridBagLayout();
        GridBagConstraints constraints=new GridBagConstraints();
        contentPane.setLayout(bgl);
        constraints.insets.right=30;

       
        tcc = new JColorChooser(l.getColor());
        
        constraints.weightx=100;
        constraints.weighty=100;
        constraints.gridx=0;
        constraints.gridy=0;
        constraints.gridwidth=2;
        constraints.gridheight=1;   
        constraints.insets=new Insets(20,20,6,20);
        constraints.anchor=GridBagConstraints.CENTER;
        
        contentPane.add(tcc, constraints);
        
        JLabel descrLabel=new JLabel(Globals.messages.getString("Description"));
        constraints.weightx=100;
        constraints.weighty=0;
        constraints.gridx=1;
        constraints.gridy=1;
        constraints.gridwidth=1;
        constraints.gridheight=1;   
        constraints.insets=new Insets(0,20,0,0);
        constraints.anchor=GridBagConstraints.EAST;     
        contentPane.add(descrLabel, constraints);
        
        description=new JTextField();
        description.setText(l.getDescription());
        constraints.weightx=100;
        constraints.weighty=0;
        constraints.gridx=2;
        constraints.gridy=1;
        constraints.gridwidth=1;
        constraints.gridheight=1;   
        constraints.insets=new Insets(0,0,0,20);
        constraints.fill=GridBagConstraints.HORIZONTAL;
        constraints.anchor=GridBagConstraints.WEST;     
        contentPane.add(description, constraints);


        JLabel opacityLbl=new JLabel(Globals.messages.getString("Opacity"));
		constraints.weightx=100;
        constraints.weighty=0;
        constraints.gridx=1;
        constraints.gridy=3;
        constraints.gridwidth=1;
        constraints.gridheight=1;   
        constraints.fill=GridBagConstraints.NONE;
        constraints.anchor=GridBagConstraints.EAST;
        contentPane.add(opacityLbl, constraints);
        
        
        opacity = new JSlider(JSlider.HORIZONTAL,
                                      ALPHA_MIN, ALPHA_MAX, 
                                      Math.round(l.getAlpha()*100.0f));
		//opacity.addChangeListener(this);



		//Turn on labels at major tick marks.
		opacity.setMajorTickSpacing(10);
		opacity.setMinorTickSpacing(1);
		opacity.setPaintTicks(true);
		opacity.setPaintLabels(true);

		constraints.weightx=100;
        constraints.weighty=0;
        constraints.gridx=2;
        constraints.gridy=3;
        constraints.gridwidth=1;
        constraints.gridheight=1;   
        constraints.fill=GridBagConstraints.HORIZONTAL;
        constraints.anchor=GridBagConstraints.WEST;
        contentPane.add(opacity, constraints);
        
        visibility=new JCheckBox(Globals.messages.getString("IsVisible"));
        visibility.setSelected(l.getVisible());
        constraints.weightx=100;
        constraints.weighty=0;
        constraints.gridx=2;
        constraints.gridy=4;
        constraints.gridwidth=1;
        constraints.gridheight=1;   
        constraints.anchor=GridBagConstraints.WEST;
        contentPane.add(visibility, constraints);
        
        JButton ok = new JButton(Globals.messages.getString("Ok_btn"));
        JButton cancel = new JButton(Globals.messages.getString("Cancel_btn"));
    
        Box b=Box.createHorizontalBox();
        b.add(Box.createHorizontalGlue());
		b.add(cancel);
		b.add(Box.createHorizontalStrut(12));
		ok.setPreferredSize(cancel.getPreferredSize());
		b.add(ok);
        constraints.weightx=100;
        constraints.weighty=0;
        constraints.gridx=0;
        constraints.gridy=5;
        constraints.gridwidth=3;
        constraints.gridheight=1;   
        constraints.insets=new Insets(0,20,20,20);
        constraints.anchor=GridBagConstraints.WEST;
        contentPane.add(b, constraints);
        
        
        
        ok.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                active=true;
                setVisible(false);
            }
        });
        getRootPane().setDefaultButton(ok);

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

    }
    
    /** Get the layer description as specified in the layer edit dialog
    
        @return the layer object with the specified characteristics.
    
    */
    public LayerDesc getLayer()
    {
        LayerDesc l=new LayerDesc();
        l.setVisible(visibility.isSelected());
        l.setDescription(description.getText());
        l.setColor(tcc.getColor());
        l.setAlpha(opacity.getValue()/100.0f);
        l.setModified(true);
        
        return l;
    }
    
    /** Determine if the user selected the Ok button.
    
        @return true if the user has quit the dialog box with the Ok button.
    */
    
    public boolean getActive()
    {
        return active;
    }
    
 
}