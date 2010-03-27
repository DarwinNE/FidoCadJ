import java.awt.*;
import java.applet.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;


/** FidoReadApplet.java v.1.3

<pre>
   ****************************************************************************
   Version History 

 * Command to compile:
 ---------------------
javac -source 1.4 -target 1.4 *.java 

 * Command to assemble jar file:
 -------------------------------
jar cvfm fidoreadj_applet.jar Manifest_applet.txt CircuitPanel.class FCDstdlib.class FidoReadApplet.class MyTimer.class globals.class ParseSchem.class mapCoordinates.class sortLayer.class layerDesc.class globals.class 



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

    Copyright march 2007- february 2008 by Davide Bucci
</pre>

</pre>
*/


public class FidoReadApplet extends JApplet
{
	CircuitPanel CC;
	Color sfondo;
    JScrollPane SC;

	/*JScrollBar xScroll;
	JScrollBar yScroll;
	static private int STEPS=1000;*/
	
	int xvalue;
	int yvalue;
	private boolean runInAFrame;
	private JFrame popFrame;

 

	public void init()
	{
		
		
		
		sfondo=Color.lightGray;
		CC=new CircuitPanel(sfondo);
        SC= new JScrollPane((Component)CC);
		SC.getVerticalScrollBar().setUnitIncrement(20);
        SC.getHorizontalScrollBar().setUnitIncrement(20);
		CC.P.readLibraryString(FCDstdlib.standardLibrary);

/*		CC.P.readLibraryString(FCDstdlib.PCBLibraryA);
		CC.P.readLibraryString(FCDstdlib.PCBLibraryB);
		CC.P.readLibraryString(FCDstdlib.PCBLibraryC);
		CC.P.readLibraryString(FCDstdlib.PCBLibraryD);
*/
		Container contentPane;
		if(runInAFrame) {
			popFrame=new JFrame();
  		    contentPane=popFrame.getContentPane();
  		} else {
  			contentPane=getContentPane();
  		}
  			
		getRootPane().setOpaque(true);
		getLayeredPane().setOpaque(true);
		((JComponent)getContentPane()).setOpaque(true);
		
    	// Create two scroll bars to set real and imaginary parts
        contentPane.add(SC,"Center");

    	xvalue=0;
    	yvalue=0;
		CC.profileTime=false;
    	CC.circ.setLength(0);
		CC.antiAlias=true;
	}
	/*
	public static void main(String[] args)
	{
		
		System.out.println("Debug messages on the console");
		sortLayer SL=new sortLayer();
		String s="RV 770 168 690 248 3\nPA 752 50 15 15 4 1 2\nPL 169 207 169 268 8 3 \nPL 239 209 239 268 8 2 \nPL 704 228 727 228 8 1"; 
		
		System.out.println("Processed string:\n"+SL.sort(s));
		
		
	}*/
	
	public void trace(String c, int zoom)
	{
		CC.P.setZoom(zoom, zoom);
		sortLayer SL=new sortLayer();
		
		try {
			CC.circ=SL.sort(c.toString());
		} catch (IOException E) {
			JOptionPane.showMessageDialog(this,
				"Too much consecutive errors. Bad format?");
		}
		CC.repaint();
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
				sfondo=Color.black;
				break;
			case 1:
				sfondo=Color.blue;
				break;
			case 2:
				sfondo=Color.green;
				break;
			case 3:
				sfondo=Color.cyan.darker();
				break;
			case 4:
				sfondo=Color.red;
				break;
			case 5:
				sfondo=Color.magenta;
				break;
			case 6:
				sfondo=Color.orange;
				break;
			case 7:
				sfondo=Color.lightGray;
				break;
			case 8:
				sfondo=Color.gray;
				break;
			case 9:
				sfondo=new Color(128,128,255);
				break;
			case 10:
				sfondo=new Color(128,255,128);
				break;
			case 11:
				sfondo=new Color(128,255,255);
				break;
			case 12:
				sfondo=new Color(255,128,128);
				break;
			case 13:
				sfondo=Color.pink;
				break;
			case 14:
				sfondo=Color.yellow;
				break;
			case 15:
				sfondo=Color.white;
				break;		
		}
		CC.setBackground(sfondo);
	}
	public void update(Graphics g){paint(g);} 


}