package dialogs;

import globals.Globals;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowAdapter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.basic.BasicBorders;

/** Create a small virtual keyboard for help inserting UTF-8 symbols such
	as greek alphabet letters and so on.

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

	Copyright 2012-2014 by phylum2, Davide Bucci
	
	TODO: avoid using magic numbers in the code
</pre>

@author phylum2
*/

@SuppressWarnings("serial")

public class OSKeybPanel extends JPanel 
{
	
	String symbols = 
		"\u0391\u0392\u0393\u0394\u0395\u0396\u0397\u0398\u0399\u039A" 
		+"\u039B\u039C\u039D\u039E\u039F\u03A0\u03A1\u03A3\u03A4\u03A5"
		+"\u03A6\u03A7\u03A8\u03A9\u03B1\u03B2\u03B3\u03B4\u03B5\u03B6"
		+"\u03B7\u03B8\u03B9\u03BA\u03BB\u03BC\u03BD\u03BE\u03BF\u03C0"
		+"\u03C1\u03C3\u03C4\u03C5\u03C6\u03C7\u03C8\u03C9@\00uA7\u00B7"
		+"\u00F7\u00D7\u00B1\u2264\u2265\u2260\u2261\u007E\u2248\u221E"
		+"\u221A\u00AF\u2211\u2202\u2229\u222B\u00AB\u00BB\u00A6\u007C"
		+"\u00F8\u00BC\u00BD\u00BE\u215B\u215C\u215D\u215E\u2030\u00BA"
		+"\u00AA\u00B9\u00B2\u00B3\u00B0\u02DC\u2194\u2192\u2190\u2193"
		+"\u2191\u0027";
	
	JButton[] k = new JButton[symbols.length()];
	Object txt;
	int posX=0,posY=0;
	
	public void setField(Object o)
	{
		txt = o;		
	}
	
	public enum KEYBMODES {GREEK, MATH, MISC};
	
	/** Create the keyboard panel of the selected type.
	*/
	public OSKeybPanel(KEYBMODES mode)
	{		
		super();

        GridBagLayout bgl=new GridBagLayout();
        GridBagConstraints constraints=new GridBagConstraints();        
        setLayout(bgl);
        constraints = DialogUtil.createConst(0,0,1,1,0,0,
    			GridBagConstraints.CENTER, GridBagConstraints.BOTH, 
    			new Insets(0,0,0,0));  
    			
    	Font standardF = UIManager.getDefaults().getFont("TextPane.font");
		int size = standardF.getSize();		
		Font f = new Font("Courier New",0,size+1);
		Font fbig = new Font("Courier New",0,size+2);	

		ActionListener al = new ActionListener() {
			
			public void actionPerformed(ActionEvent e) 
			{
				JDialog jd = (JDialog) txt;			
				// We must find a target for the results of the keyboard
				// actions.
				if (!(jd.getMostRecentFocusOwner() instanceof JTextField))
					return;
				JTextField jfd = (JTextField)jd.getMostRecentFocusOwner();			
				if (jfd.getSelectedText()!=null) 
				{
					String ee = jfd.getText().replace(
						jfd.getSelectedText(), "");
					jfd.setText(ee);
				} 
				int p = jfd.getCaretPosition();
				if (p<0)
					jfd.setText(jfd.getText()+e.getActionCommand());
				else
				{
					String s = jfd.getText().substring(0,p);
					String t = jfd.getText().substring(p);											
					jfd.setText(s+e.getActionCommand()+t);
					jfd.setCaretPosition(++p);
				}	
				
				jfd.requestFocus();
			}
		};
				
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 100;
		constraints.weighty = 100;
		
		// Create an array of buttons containing the array characters.
		// All is done automatically, so changing the array contents
		// automatically will change the buttons.
		for (int i=0;i<symbols.length();i++)
		{			
			k[i] = new JButton(String.valueOf(symbols.charAt(i)));
			if (mode == KEYBMODES.GREEK && i>47) 
				continue;
			if (mode == KEYBMODES.MISC && i<48) 
				continue;
				
			k[i].setFont(i>71 ? fbig : f);
			k[i].setFocusable(false);
			k[i].addActionListener(al);	
			k[i].putClientProperty("Quaqua.Button.style","toggleCenter");
			if (constraints.gridx>7) {
				k[i].putClientProperty("Quaqua.Button.style","toggleWest");
				constraints.gridy++; 
				constraints.gridx=0; 
				k[i-1].putClientProperty("Quaqua.Button.style","toggleEast");
			}
			
			add(k[i], constraints);
			constraints.gridx++;
		}
		
		// TODO: avoid using numbers in the code, but calculate automatically
		// the indices.
		k[0].putClientProperty("Quaqua.Button.style","toggleWest");
		k[symbols.length()-1].putClientProperty(
			"Quaqua.Button.style","toggleEast");
		k[47].putClientProperty("Quaqua.Button.style","toggleEast");
		k[48].putClientProperty(
			"Quaqua.Button.style","toggleWest");
	}
}
