package globals;

import java.util.*;
import java.awt.*;
import java.io.*;
import javax.swing.*;


/* Globals.java v.1.2

   ****************************************************************************

What? Global variables should not be used?

But... who cares!!!

<pre>
   Version History 

Version   Date           Author       Remarks
-------------------------------------------------------------------------------
1.0     January 2008        D. Bucci    First working version
1.1		June 2008			D. Bucci	A few improvements
1.2		June 2009			D. Bucci	A few SCHIFIO's have been eliminated
    									Capitalized
    
   Written by Davide Bucci, June 2009, davbucci at tiscali dot it
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

</pre>
*/


public class Globals 
{

    public static ResourceBundle messages;             // message bundle
    public static int shortcutKey;		// shortcut key to be used:
    							// META (Command) for Mac
    							// CTRL elsewhere
    							
    public static boolean useMetaForMultipleSelection;
   	public static boolean useNativeFileDialogs; 	// Native file dialogs are 
   								// far better on MacOSX than Linux
   								
   	public static boolean weAreOnAMac; // We are on a Mac!!!
   	public static boolean quaquaActive; // Quaqua is a better Mac L&F
    
    
    public static int openWindows;
    public static JFrame activeWindow;
 
    
    // Line width expressed in FidoCadJ coordinates 
    public static final double lineWidth = 0.25;  
  	
    // Line width expressed in FidoCadJ coordinates (ovals)
    public static final double lineWidthCircles = 0.15;  
  															
  								
    
    public static final boolean doNotUseXOR = true;  // Avoid XOR paint mode
    public static final String version = "0.22.1 eta";     	// version
    public static final String DEFAULT_EXTENSION = "fcd";	// Extension
    
    public static final int dashNumber = 5;
    public static final float dash[][] = {{10.0f,0f},{5.0f},{2.0f, 2.0f}, {2.0f, 5.0f},
    	{2.0f, 5.0f,5.0f,5.0f}}; 
    
 	   
 

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
    	case, add the specified extension.
    	If the string contains " somewhere, this character is removed and the
    	extension is not added. In this way, if the user wants to avoid the
    	automatic extension add, he should put the file name between "'s
    	
    	@param p the file name
    	@param ext the extension that should be added if the file name does not
    		contain one. This extension should NOT contain the dot.
    	@return the absolutely gorgeous file name, completed with an extension
    */
    public static String checkExtension(String p, String ext)
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
    	if (search>start && search>=0)
    		return s;		// We do not need to add anything
    	else
    		return s+"."+ext;
    }
    
    /** Get the file name, without extensions
    	@param p the file name with path and extension to be processed
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
    	@param an hash table <char, String> in which each character to be 
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