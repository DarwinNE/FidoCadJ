package net.sourceforge.fidocadj.dialogs;
import java.awt.*;

import javax.swing.*;

import java.awt.event.*;

import net.sourceforge.fidocadj.dialogs.*;
import net.sourceforge.fidocadj.globals.*;

/** EnterCircuitFrame.java 

	This file is part of FidoCadJ.

<pre>
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

	Copyright 2007-2012 by Davide Bucci
</pre>


    A dialog useful to past the FidoCadJ code.

    @author Davide Bucci
    

*/
public class EnterCircuitFrame extends JDialog implements ComponentListener 
{
    private static final int MIN_WIDTH=400;
    private static final int MIN_HEIGHT=350;

    private final JTextArea textArea;
    
    /* The stringCircuit property gives the modified string
        if the user selected the Ok button.
    */
    private String stringCircuit;
      
    /** Defines the string containing the FidoCadJ code.
    	@param s the string
    */  
    public void setStringCircuit(String s)
    {
    	stringCircuit = s;
    }
    
    /** Gets the string containing the FidoCadJ code.
    	@return the string.
    */
    public String getStringCircuit()
    {
    	return stringCircuit;
    }
    
    /** Impose a minimum size for this dialog.
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
    	// Nothing to do
    }
    public void componentShown(ComponentEvent e) 
    {
    	// Nothing to do
    }
    public void componentHidden(ComponentEvent e) 
    {
    	// Nothing to do
    }

    
    /** The constructor. 
        @param parent the parent frame
        @param circuit the circuit Fidocad code
    */
    public EnterCircuitFrame (JFrame parent, String circuit)
    {
        super(parent, Globals.messages.getString("Enter_code"), true);
        addComponentListener(this); 
		
  		// Ensure that under MacOSX >= 10.5 Leopard, this dialog will appear
  		// as a document modal sheet
  		
  		getRootPane().putClientProperty("apple.awt.documentModalSheet", 
				Boolean.TRUE);
				
				
		GridBagConstraints constraints;
        Container contentPane=getContentPane();
        contentPane.setLayout(new GridBagLayout());
        
        DialogUtil.center(this,.5,.5);

        stringCircuit="[FIDOCAD]\n"+circuit;
        textArea=new JTextArea(stringCircuit,2,10);
        JScrollPane scrollPane=new JScrollPane(textArea);
        
   		constraints = DialogUtil.createConst(0,0,1,1,100,100,
			GridBagConstraints.WEST, GridBagConstraints.BOTH, 
			new Insets(20,20,12,20));

        contentPane.add(scrollPane, constraints);
               
        JButton ok=new JButton(Globals.messages.getString("Ok_btn"));
		JButton cancel=new JButton(Globals.messages.getString("Cancel_btn"));
        
        // Put the OK and Cancel buttons and make them active.
        Box b=Box.createHorizontalBox();
        b.add(Box.createHorizontalGlue());
		ok.setPreferredSize(cancel.getPreferredSize());

		if (Globals.okCancelWinOrder) {
			b.add(ok);
			b.add(Box.createHorizontalStrut(12));
			b.add(cancel);
		
		} else {
			b.add(cancel);
			b.add(Box.createHorizontalStrut(12));
			b.add(ok);
		}
		b.add(Box.createHorizontalStrut(20));

		constraints = DialogUtil.createConst(0,1,1,1,100,0,
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
			new Insets(0,0,20,0));
		
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
        
        // Here is an action in which the dialog is closed

        AbstractAction cancelAction = new AbstractAction ()
        {
            public void actionPerformed (ActionEvent e)
            {
                setVisible(false);
            }
        };
        DialogUtil.addCancelEscape (this, cancelAction);
        
    }
  


}