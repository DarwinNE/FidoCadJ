package globals;

import java.util.*;
import java.awt.*;
import java.io.*;
import javax.swing.*;

import layers.*;


/* Globals.java


What? Global variables should not be used?

But... who cares!!!

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

	Copyright 2008-2011 by Davide Bucci

</pre>
*/


public class Globals 
{

	// message bundle
    public static ResourceBundle messages;
    			  
    // shortcut key to be used:
    public static int shortcutKey;		
   	// META (Command) for Macintoshes
    // CTRL elsewhere
    
    // This may be interesting on Macintoshes
    public static boolean useMetaForMultipleSelection;
    
    // Native file dialogs are far better on MacOSX than Linux
   	public static boolean useNativeFileDialogs; 	
   								
   	// We are on a Mac!!!
   	public static boolean weAreOnAMac; 
   	
   	// Quaqua is a better Mac L&F
   	public static boolean quaquaActive; 
   	
   	// Show the cancel button to the right of the OK button, as it is done in
   	// Windows
   	public static boolean okCancelWinOrder;
    
    // Track the total number of FidoCadJ open windows
    public static int openWindowsNumber;
    
    // A pointer to the active window
    public static JFrame activeWindow;
    
    public static HashSet openWindows = new HashSet();
   
    // Line width expressed in FidoCadJ coordinates 
    public static final double lineWidthDefault = 0.5;  
    public static double lineWidth = lineWidthDefault;  
  	
    // Line width expressed in FidoCadJ coordinates (ovals)
    public static final double lineWidthCirclesDefault = 0.35;
    public static double lineWidthCircles = lineWidthCirclesDefault;  
  															
    // Connection size in FidoCadJ coordinates (diameter)
    public static final double diameterConnectionDefault = 2.0;
    public static double diameterConnection = diameterConnectionDefault; 
    
    // Border to be used in the export in logical coordinates
    public static final int exportBorder=6;
    
  								
    // Version. This is shown in the main window title bar
    public static final String version = "0.24 beta";
    // Is it a beta version?
    public static final boolean isBeta = true;		
    
    // The default file extension
    public static final String DEFAULT_EXTENSION = "fcd";	
    
    // The default font
    public static final String defaultTextFont = "Courier New";
    
    public static final int dashNumber = 5;
    public static final float dash[][] = {{10.0f,0f},{5.0f,5.0f},{2.0f, 2.0f}, 
    	{2.0f, 5.0f},
    	{2.0f, 5.0f,5.0f,5.0f}}; 
    
    // Minimum height in pixels of a text to be drawn.
    public static final int textSizeLimit = 4;
 
 	// Number of layers to be treated
	public static final int MAX_LAYERS=16;
	
	// The encoding to be used by FidoCadJ
	public static final String encoding = "UTF8";
	
	
	 /** Create the standard array containing the layer descriptions, colors
    	and transparency. The name of the layers are read from the resources
    	which may be initizialized. If Globals.messages==null, no description
    	is given.
    	
    	@return the list of the layers being created.
    */
    public static Vector createStandardLayers()
    {
        Vector layerDesc=new Vector();
        String s="";
        
        if(Globals.messages!=null) s=Globals.messages.getString("Circuit_l");
        layerDesc.add(new LayerDesc(Color.black, true, s,1.0f));	// 0
        if(Globals.messages!=null) s=Globals.messages.getString("Bottom_copper");
        layerDesc.add(new LayerDesc(new Color(0,0,128),true, s,1.0f));	// 1
        if(Globals.messages!=null) s=Globals.messages.getString("Top_copper");
        layerDesc.add(new LayerDesc(Color.red, true,s,1.0f));			// 2
        if(Globals.messages!=null) s=Globals.messages.getString("Silkscreen");
        layerDesc.add(new LayerDesc(new Color(0,128,128), true,s,1.0f));// 3
        if(Globals.messages!=null) s=Globals.messages.getString("Other_1");
        layerDesc.add(new LayerDesc(Color.orange, true,s,1.0f));		// 4
        if(Globals.messages!=null) s=Globals.messages.getString("Other_2");    
        layerDesc.add(new LayerDesc(new Color(-8388864), true,s,1.0f));	// 5
        if(Globals.messages!=null) s=Globals.messages.getString("Other_3");
        layerDesc.add(new LayerDesc(new Color(-16711681), true,s,1.0f));// 6
        if(Globals.messages!=null) s=Globals.messages.getString("Other_4");
        layerDesc.add(new LayerDesc(new Color(-16744448), true,s,1.0f));// 7
        if(Globals.messages!=null) s=Globals.messages.getString("Other_5");
        layerDesc.add(new LayerDesc(new Color(-6632142), true, s,1.0f));// 8
        if(Globals.messages!=null) s=Globals.messages.getString("Other_6");
        layerDesc.add(new LayerDesc(new Color(-60269), true,s,1.0f));	// 9
        if(Globals.messages!=null) s=Globals.messages.getString("Other_7");
        layerDesc.add(new LayerDesc(new Color(-4875508), true,s,1.0f));	// 10
        if(Globals.messages!=null) s=Globals.messages.getString("Other_8");
        layerDesc.add(new LayerDesc(new Color(-16678657), true,s,1.0f));// 11
        if(Globals.messages!=null) s=Globals.messages.getString("Other_9");
        layerDesc.add(new LayerDesc(new Color(-1973791), true,s,0.95f));// 12
        if(Globals.messages!=null) s=Globals.messages.getString("Other_10");
        layerDesc.add(new LayerDesc(new Color(-6118750), true,s,0.9f));	// 13
        if(Globals.messages!=null) s=Globals.messages.getString("Other_11");
        layerDesc.add(new LayerDesc(new Color(-10526881), true,s,0.9f));// 14
        if(Globals.messages!=null) s=Globals.messages.getString("Other_12");
        layerDesc.add(new LayerDesc(Color.black, true, s,1.0f));		// 15
            
        return layerDesc;
    }

    /**	Adjust a long string in order to cope with space limitations.
    	Tipically, it will be used to show long paths in the window caption.
    	@param s the string to be treated
    	@param l the total maximum length of the result
    */
    public static String prettifyPath(String s, int l)
    {
    	if(s.length()<l)
    		return s;
    	
    	if (l<10)
    		l=10;
    		
     	String R;
     	R= s.substring(0,l/2-5)+ "...  "+
    	   s.substring(s.length()-(l/2));
    	
    	return R;
    	
    }
    /** Check if an extension is present in a file name and, if it is not the
    	case, add or adjust it in order to obtain the specified extension.
    	If the string contains " somewhere, this character is removed and the
    	extension is not added. In this way, if the user wants to avoid the
    	automatic extension add, he should put the file name between "'s
    	
    	@param p the file name
    	@param ext the extension that should be added if the file name does not
    		contain one. This extension should NOT contain the dot.
    	@return the absolutely gorgeous file name, completed with an extension
    */
    public static boolean checkExtension(String p, String ext)
    {
    	int i;
    	String s="";
    	
    	// Check if we have a " somewhere
    	// Not particularly fast here, but the code is easy to read and we are
    	// not in a speed sensitive context...
    	boolean skip=false;
    	
    	for (i=0; i<p.length(); ++i) {
    		if(p.charAt(i)!='"') 
    			s += p.charAt(i);
    		else
    			skip=true;
    	}
    	
    	if (skip)
    		return true;
    		
    	// We need to check only the file name and not the entire path.
    	// So we begin our research only after the last file separation
    	    	
    	int start=s.lastIndexOf(System.getProperty("file.separator"));
    	int search=s.lastIndexOf(".");
    	
    	// If the separator has not been found, start is negative.
    	if(start<0) start=0;
    	
    	
    	// Search if there is a dot (separation of the extension)
    	if (search>start && search>=0) {
    		// There is already an extension.
    		// We do not need to add anything but instead we need to check if
    		// the extension is correct.
    		String extension = s.substring(search+1);
    		System.out.println(extension);
    		if(!extension.equals(ext)) {
    			return false;
    		}
    		
    	} else {
    		return false;
    	}	
    	return true;
    }
    
    /** Check if an extension is present in a file name and, if it is not the
    	case, add or adjust it in order to obtain the specified extension.
    	If the string contains " somewhere, this character is removed and the
    	extension is not added. In this way, if the user wants to avoid the
    	automatic extension add, he should put the file name between "'s
    	
    	@param p the file name
    	@param ext the extension that should be added if the file name does not
    		contain one. This extension should NOT contain the dot.
    	@return the absolutely gorgeous file name, completed with an extension
    */
    public static String adjustExtension(String p, String ext)
    {
    	int i;
    	String s="";
    	
    	// Check if we have a " somewhere
    	// Not particularly fast here, but the code is easy to read and we are
    	// not in a speed sensitive context...
    	boolean skip=false;
    	
    	for (i=0; i<p.length(); ++i) {
    		if(p.charAt(i)!='"') 
    			s += p.charAt(i);
    		else
    			skip=true;
    	}
    	
    	if (skip)
    		return s;
    		
    	// We need to check only the file name and not the entire path.
    	// So we begin our research only after the last file separation
    	    	
    	int start=s.lastIndexOf(System.getProperty("file.separator"));
    	int search=s.lastIndexOf(".");
    	
    	// If the separator has not been found, start is negative.
    	if(start<0) start=0;
    	
    	
    	// Search if there is a dot (separation of the extension)
    	if (search>start && search>=0) {
    		// There is already an extension.
    		// We do not need to add anything but instead we need to check if
    		// the extension is correct.
    		s = s.substring(0, search)+"."+ext;
    		
    	} else {
    		s+="."+ext;
    	}	
    	
    	System.out.println(s);
    	return s;
    }
    
    /** Get the file name, without extensions
    	@param s the file name with path and extension to be processed
    */
    public static String getFileNameOnly(String s)
    {
    	int i;
    
    	// We need to check only the file name and not the entire path.
    	// So we begin our research only after the last file separation
    	    	
    	int start=s.lastIndexOf(System.getProperty("file.separator"));
    	int search=s.lastIndexOf(".");
    	
    	// If the separator has not been found, start is negative.
    	
    	if(start<0) 
    		start=0;
    	else
    		start+=1;
    	if(search<0) search=s.length();
    	
    	return s.substring(start,search);
    }
    
    /** When we have a path and a filename to put together, we need to separate
    	them with a system file separator, being careful not to add it when
    	it is already include in the path.
    
    	@param path the path
    	@param filename the file name
    	@return the complete filename, including path
    */
    public static String createCompleteFileName(String path, String filename)
    {
    	boolean incl=!path.endsWith(System.getProperty("file.separator"));
    	String completeFileName= path +  
    		(incl?System.getProperty("file.separator") : "") + 
    		filename;
    	return completeFileName;
    }
    
    /**	Change characters which could give an error in some situations with
    	their corresponding code, or escape sequence.
    	
    	@param p the input string, eventually containing the characters to be
    		changed.
    	@param bc an hash table <char, String> in which each character to be 
    		changed is associated with its code, or escape sequence.
    	@return the string with the characters changed.	
    	
    */
    public static String substituteBizarreChars(String p, Map bc)
    {
    	StringBuffer s=new StringBuffer("");
    	for (int i=0; i<p.length(); ++i) {
    		if((String)bc.get(""+p.charAt(i))==null) 
    			s.append(p.charAt(i));
    		else
    			s.append((String)bc.get(""+p.charAt(i)));
    	}
    	
    	return s.toString();
    }
}