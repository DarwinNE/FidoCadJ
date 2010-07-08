package dialogs;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import globals.*;
import dialogs.*;

/** EnterCircuitFrame.java v.1.1

   ****************************************************************************
   Version History 

<pre>
Version   Date           Author       Remarks
------------------------------------------------------------------------------
1.0     December 2007       D. Bucci    First working version
1.1     January 2008        D. Bucci    Minimum windows size
1.2		February 2009		D. Bucci	Add margins to respect Apple HIG
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
</pre>


    A dialog useful to past the Fidocad code.

    @author Davide Bucci
    @version 1.3, June 2009
    

*/
public class EnterCircuitFrame extends JDialog implements ComponentListener 
{
    private static final int MIN_WIDTH=400;
    private static final int MIN_HEIGHT=350;

    private JTextArea textArea;
    
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
    
    /** The stringCircuit property gives the modified string
        if the user selected the Ok button.
        The property is not well incapsulated. One should implement
        the getStringCircuit() or setStringCircuit(String) methods
        to improve the code style. The next time.
    */
    public String stringCircuit;
    
    /** The constructor. 
        @param parent the parent frame
        @param circuit the circuit Fidocad code
    */
    public EnterCircuitFrame (JFrame parent, String circuit)
    {
        super(parent, Globals.messages.getString("Enter_code"), true);
        addComponentListener(this); 
		
		GridBagConstraints constraints=new GridBagConstraints();
        Container contentPane=getContentPane();
        contentPane.setLayout(new GridBagLayout());
        
        DialogUtil.center(this,.5,.5);

        stringCircuit="[FIDOCAD]\n"+circuit;
        textArea=new JTextArea(stringCircuit,2,10);
        JScrollPane scrollPane=new JScrollPane(textArea);
        constraints.weightx=100;
		constraints.weighty=100;
		constraints.gridx=0;
		constraints.gridy=0;
		constraints.gridwidth=1;
		constraints.gridheight=1;	
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor=GridBagConstraints.WEST;
		constraints.insets=new Insets(20,20,12,20);
        contentPane.add(scrollPane, constraints);
        
        
        Box b=Box.createHorizontalBox();
        
        JButton ok=new JButton("Ok");
        JButton cancel=new JButton("Cancel");
        
        
		
        b.add(Box.createGlue());
		b.add(cancel);
		b.add(Box.createHorizontalStrut(10));
		ok.setPreferredSize(cancel.getPreferredSize());
		b.add(ok);
		b.add(Box.createHorizontalStrut(20));

        constraints.gridx=0;
		constraints.gridy=1;
		constraints.weighty=0;

 		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor=GridBagConstraints.WEST;
		constraints.insets=new Insets(0,0,20,0);

		
        contentPane.add(b, constraints);

        ok.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                setVisible(false);
                stringCircuit=textArea.getText();
            }
        });
        cancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                setVisible(false);
            }
        });
        
    }
  


}