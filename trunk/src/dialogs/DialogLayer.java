package dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import globals.*;
import layers.*;

/** dialogLayer.java v.1.1

   List and choose the layer to be edited.
   The class dialogLayer allows to choose which layers should be displayed,
    on which color and characteristics.

   
<pre>
   ****************************************************************************
   Version History 

Version   Date           Author       Remarks
------------------------------------------------------------------------------
1.0     December 2007       D. Bucci    First working version
1.1     January 2008        D. Bucci    Internazionalized version
1.2     June 2009           D. Bucci    Capitalize the first letters                                     

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
    @version 1.2 June 2009
    
    */
    
public class DialogLayer extends JDialog implements ComponentListener 
{
    private static final int MIN_WIDTH=400;
    private static final int MIN_HEIGHT=350;

    private ArrayList layers;
    private JList layerList;
    
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
        @param l a LayerDesc vector containing the layers' attributes 
    */
    public DialogLayer (JFrame parent, ArrayList l)
    {
        super(parent,Globals.messages.getString("Layer_editor"), true);
        DialogUtil.center(this, .40,.40,400,350);

        addComponentListener(this); 

        layers=l;
        
        
  		// Ensure that under MacOSX >= 10.5 Leopard, this dialog will appear
  		// as a document modal sheet
  		
  		getRootPane().putClientProperty("apple.awt.documentModalSheet", 
				Boolean.TRUE);
				
				
        GridBagLayout bgl=new GridBagLayout();
        GridBagConstraints constraints=new GridBagConstraints();
        Container contentPane=getContentPane();
        contentPane.setLayout(bgl);
                
        layerList = new JList(new Vector(layers));
        JScrollPane sl=new JScrollPane(layerList);
        
        layerList.setCellRenderer( new LayerCellRenderer());
        constraints.weightx=100;
        constraints.weighty=100;
        constraints.gridx=0;
        constraints.gridy=0;
        constraints.gridwidth=1;
        constraints.gridheight=1;   
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets=new Insets(20,20,6,20);

        
        contentPane.add(sl, constraints);
        
        JButton ok = new JButton(Globals.messages.getString("Ok_btn"));
        JButton cancel = new JButton(Globals.messages.getString("Cancel_btn"));
        JButton edit = new JButton(Globals.messages.getString("Edit"));
        
    
        Box b=Box.createHorizontalBox();
        b.add(edit);
        b.add(Box.createHorizontalGlue());
		b.add(cancel);
		b.add(Box.createHorizontalStrut(12));
		ok.setPreferredSize(cancel.getPreferredSize());
		b.add(ok);
        constraints.weightx=100;
        constraints.weighty=0;
        constraints.gridx=0;
        constraints.gridy=1;
        constraints.gridwidth=1;
        constraints.gridheight=1;   
        constraints.anchor=GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets=new Insets(0,20,20,20);
        contentPane.add(b,constraints);         // Add cancel button    
        
        edit.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                if (layerList.getSelectedIndex()>=0) {
                    DialogEditLayer del=new DialogEditLayer(null, 
                        (LayerDesc) layers.get(layerList.getSelectedIndex()));
                    del.setVisible(true);
                    if (del.getActive()){
                        layers.set(layerList.getSelectedIndex(),
                            del.getLayer());
                        repaint();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, 
                        Globals.messages.getString("Warning_select_layer"),
                        Globals.messages.getString("Layer_editor"),
                        JOptionPane.INFORMATION_MESSAGE, null);
                }
                    
            }
        });
        
        ok.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                
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

}



