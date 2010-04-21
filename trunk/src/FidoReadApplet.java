import java.awt.*;
import java.applet.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;


import circuit.*;
import layers.*;


/** FidoReadApplet.java v.2.0

This is the main file for the FidoCadJ reader applet.

<pre>
   ****************************************************************************

Version   Date           Author       Remarks
------------------------------------------------------------------------------
1.0     March 2007		D. Bucci     First working version
1.1     December 2007	D. Bucci     Improved PCB rendering:
                                     - Pad dimensions and style
                                     - Drill
                                     - Mirrored text and size handling
1.2		January 2008	D. Bucci	Improved speed
									Supports layer
1.3		February 2008	D. Bucci	Use a scroll pane		
2.0		march 30, 2010	D. Bucci 	Upgraded to up to date FidoCadJ version
                                     
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

    Copyright march 2007- march 2010 by Davide Bucci
</pre>

@author Davide Bucci
@version 2.0
*/


public class FidoReadApplet extends JApplet
{
	CircuitPanel CC;
	Color backgroundColor;
    JScrollPane SC;


	public void init()
	{
		backgroundColor=Color.white;
		CC=new CircuitPanel(backgroundColor);
        SC= new JScrollPane((Component)CC);
		SC.getVerticalScrollBar().setUnitIncrement(20);
        SC.getHorizontalScrollBar().setUnitIncrement(20);

    	CC.profileTime=false;
		CC.antiAlias=true;
		
        // Reads the standard libraries
        CC.P.loadLibraryInJar(FidoReadApplet.class.getResource("lib/IHRAM.FCL"), "ihram");
 		CC.P.loadLibraryInJar(FidoReadApplet.class.getResource("lib/FCDstdlib.fcl"), "");
 		CC.P.loadLibraryInJar(FidoReadApplet.class.getResource("lib/PCB_en.fcl"), "pcb");
	
		ArrayList layerDesc=CreateLayersNoDescription();
        CC.P.setLayers(layerDesc);

		Container contentPane;
		contentPane=getContentPane();
  			
		//getRootPane().setOpaque(true);
		//getLayeredPane().setOpaque(true);
		//((JComponent)getContentPane()).setOpaque(true);
		
        contentPane.add(SC,"Center");

	}
	
	public static ArrayList CreateLayersNoDescription()
	{
			// Create the layer array
		ArrayList LayerDesc=new ArrayList();
        
        LayerDesc.add(new LayerDesc(Color.black, true,""));
        LayerDesc.add(new LayerDesc(new Color(0,0,128),true,""));
        LayerDesc.add(new LayerDesc(Color.red, true,""));
        LayerDesc.add(new LayerDesc(new Color(0,128,128), true,""));
        LayerDesc.add(new LayerDesc(Color.orange, true,""));
        LayerDesc.add(new LayerDesc(Color.orange, true,""));
        LayerDesc.add(new LayerDesc(Color.orange, true,""));
        LayerDesc.add(new LayerDesc(Color.orange, true,""));
        LayerDesc.add(new LayerDesc(Color.orange, true,""));
        LayerDesc.add(new LayerDesc(Color.orange, true,""));
        LayerDesc.add(new LayerDesc(Color.orange, true,""));
        LayerDesc.add(new LayerDesc(Color.orange, true,""));
        LayerDesc.add(new LayerDesc(Color.orange, true,""));
        LayerDesc.add(new LayerDesc(Color.orange, true,""));
        LayerDesc.add(new LayerDesc(Color.orange, true,""));
        LayerDesc.add(new LayerDesc(Color.orange, true,""));
        
        return LayerDesc;
	}
	
	public void trace(String c, int zoom)
	{
		CC.P.getMapCoordinates().setMagnitudes((double)zoom, (double)zoom);
		try {
			CC.P.parseString(new StringBuffer(c));
		} catch (IOException E) {
			JOptionPane.showMessageDialog(this,
				"Too much consecutive errors. Bad format?");
		}
		repaint();
		getToolkit().sync();

		
	}
	
	public void setAntiAlias(boolean aa)
	{
		CC.antiAlias=aa;
	}

	public void setProfileTime(boolean pp)
	{	
		CC.profileTime=pp;
	}
	
	public void backColor(int color)
	{
		switch(color){
			case 0:
				backgroundColor=Color.black;
				break;
			case 1:
				backgroundColor=Color.blue;
				break;
			case 2:
				backgroundColor=Color.green;
				break;
			case 3:
				backgroundColor=Color.cyan.darker();
				break;
			case 4:
				backgroundColor=Color.red;
				break;
			case 5:
				backgroundColor=Color.magenta;
				break;
			case 6:
				backgroundColor=Color.orange;
				break;
			case 7:
				backgroundColor=Color.lightGray;
				break;
			case 8:
				backgroundColor=Color.gray;
				break;
			case 9:
				backgroundColor=new Color(128,128,255);
				break;
			case 10:
				backgroundColor=new Color(128,255,128);
				break;
			case 11:
				backgroundColor=new Color(128,255,255);
				break;
			case 12:
				backgroundColor=new Color(255,128,128);
				break;
			case 13:
				backgroundColor=Color.pink;
				break;
			case 14:
				backgroundColor=Color.yellow;
				break;
			case 15:
			default:
				backgroundColor=Color.white;
				break;		
		}
		CC.setBackground(backgroundColor);
	}
/*	public void update(Graphics g){
		SC.repaint();
		CC.revalidate();
	} */

}