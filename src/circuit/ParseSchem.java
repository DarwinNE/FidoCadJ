package circuit;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import java.awt.geom.*;
import java.awt.datatransfer.*;
import java.net.*;

import globals.*;
import layers.*;
import primitives.*;
import geom.*;
import clipboard.*;
import export.*;
import undo.*;

/** ParseSchem.java v.2.5

<pre>
   A FIDOCAD schematics draw class.
   
   ****************************************************************************
   Version History 

Version   Date           Author       Remarks
------------------------------------------------------------------------------
1.0     March 2007      D. Bucci    First working version
1.1     December 2007   D. Bucci    Improved PCB rendering:
                                     - Pad dimensions and style
                                     - Drill
                                     - Mirrored text and size handling
1.2     December 2007   D. Bucci    Improved speed
                                    Supports layer
1.3     December 2007   D. Bucci    Better text rendering
                                    Improved speed
1.3.1   December 2007   D. Bucci    coordSys becomes private
                                    Provided methods to obtain zoom settings
                                    Provided methods to obtain circuit surface
                                    Improved speed by optimized redrawing
                                    Uses the new high resolution coordinates
                                    Improved layer handling
1.3.2   January 2008    D. Bucci    Handle PCB library macros
2.0     May 2008        D. Bucci    Redesigned primitive handling
2.1		June 2008		D. Bucci	Drag & drop 
2.2 	August 2008		D. Bucci	
2.3     November 2008   D. Bucci    library becomes a TreeMap
2.4		December 2008	D. Bucci	FCJ extensions
2.5		June 2009		D. Bucci 	Capitalize the first letters                                     
2.6		November 2009 	D. Bucci	New FCJ extensions
									Macro font selection
2.7		February 2010	D. Bucci	General optimization


   Written by Davide Bucci, March 2007 - Feb 2010, davbucci at tiscali dot it
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

   Main parsing class 
    
    @author Davide Bucci
    @version 2.7, February 2010
*/

public class ParseSchem
{   
    static final int MAX_TOKENS=100;
    static final boolean useWindowsLineFeed=false;
    
	public String openFileName;

    private boolean drawOnlyPads;
    private int drawOnlyLayer;
    
    private String[] tokens;
    private int lineNum;
    ArrayList primitiveVector;
    ArrayList layerV;
    MapCoordinates cs;
    private Map library;
    private boolean firstDrag;
    private BufferedImage bufferedImage; // Useful for grid calculation
    private double oldZoom;
    private boolean needHoles;
    
    private boolean fastTest;
    
    private String macroFont;
    
    private int oldpx;
    private int oldpy;
    
    private UndoManager um;
    private final int MAX_UNDO=100;
    
    private GraphicPrimitive primBeingDragged;
    private int handleBeingDragged;
   
    // Old cursor position for handle drag
    private int opx;
    private int opy;
    
    private boolean hasMoved;
    
    private int maxLayer;
   
   // A drawing modification flag. If true, there are unsaved changes
    private boolean isModified;
    
    private HasChangedListener cl;
    
    private boolean hasFCJOriginVisible;


	
    public ParseSchem()
    {
        tokens=new String[MAX_TOKENS];
        primitiveVector=new ArrayList(100);
        cs=new MapCoordinates();
        layerV=new ArrayList(16);
        library=new TreeMap();
        firstDrag=false;
        um=new UndoManager(MAX_UNDO);
        oldZoom=-1;
        drawOnlyPads=false;
        drawOnlyLayer=-1;
        cl=null;
        macroFont = "Courier New";
        
        cs.xCenter=0;
        cs.yCenter=0;
        cs.setXMagnitude(4.0);	// OK
        cs.setXMagnitude(4.0);	// OK
        cs.orientation=0;
        handleBeingDragged=GraphicPrimitive.NO_DRAG;
    }
    
    /** Get the layer description vector
    
    	@return a vector of LayerDesc describing layers.
    
    */

    public ArrayList getLayers()
    {
        return layerV;
    }
    
    /** Set the layer description vector
    
    	@param v a vector of LayerDesc describing layers.
    
    */
    public void setLayers(ArrayList v)
    {
        layerV=v;
    }
    
    /**	Get the current library
    	@return a map String/String describing the current library.
    
    */
    
    public Map getLibrary()
    {
        return library;
    }
    
    /** Specify the current library.
        @param l the new library (a String/String hash table)
    */
    public void setLibrary(Map l)
    {
        library=l;
    }
    
    
    /** Try to load all libraries ("*.fcl") files in the given directory.
    
    	FCDstdlib.fcl if exists will be considered as standard library.
    	
    	@param s the directory in which the libraries should be present.
    */
    public void loadLibraryDirectory(String s)
    {
        String[] files;  // The names of the files in the directory.
        File dir = new File(s);
        
        
        
        files = dir.list(new FilenameFilter()
        { 
        	public boolean accept(File dir, String name)
        	{
        		return name.toLowerCase().endsWith(".fcl");
        	}
        	
        
        });
        
        if((!dir.exists()) || files==null) {
			if (!s.equals("")){
        		System.out.println("Warning! Library directory is incorrect:");
        		System.out.println(s);
        	}
        	System.out.println("Using only standard libraries");
        	return;
        }
        
        for (int i = 0; i < files.length; i++) {
            File f;  // One of the files in the directory.
            f = new File(dir, files[i]);
            try {
            	readLibraryFile(f.getPath());
            
            	//System.out.println("loaded library: "+f.getName());
            } catch (IOException E) {
            	System.out.println("Problems reading library: "+f.getName());
            }
        } 
           
    }
    
    /** Try to load all libraries ("*.fcl") files in the given directory.
    
    	FCDstdlib.fcl if exists will be considered as standard library.
    	
    	@param s the directory in which the libraries should be present.
    */
    public void loadLibraryInJar(URL s, String prefix)
    {
       	try {
       		readLibraryBufferedReader(new BufferedReader(new 
       			InputStreamReader(s.openStream())), prefix);
				
           
        } catch (IOException E) {
          	System.out.println("Problems reading library: "+s.toString());
        }
           
    }
    
    /** Read the library contained in a string 
        @param S the string containing the Fidocad library
    */
    public void readLibraryString(String S, String prefix)
    {
        String line;
        
        /*StringTokenizer is a legacy class that is retained for compatibility
          reasons although its use is discouraged in new code. It is 
          recommended that anyone seeking this functionality use the split
          method of String or the java.util.regex package instead. */
        
        StringTokenizer t=new StringTokenizer(S, "\n");
        String macroName="";
        String longName="";
        String categoryName="";
        String libraryName="";
        int i;
        
         /* This function could be made much faster with an intelligent
               use of StringBuffer, rather than String. Anyway, for the moment
               the perceived speed should be OK with the current libraries */
            
        while (t.hasMoreTokens()){
            
            line=t.nextToken();
            
            line=line.trim();       // Avoid trailing spaces
            
            if (line.length()<=1)	// Avoid processing shorter lines 
                continue;
                
            if(line.charAt(0)=='{') { // A category
            	categoryName="";
                for(i=1; line.charAt(i)!='}' &&
                         i<line.length(); ++i){
                    categoryName+=line.charAt(i);
                }
                continue;
            }
                
            if(line.charAt(0)=='[') { // A macro
            	macroName="";
            		
                longName="";
                for(i=1; line.charAt(i)!=' ' &&
                         line.charAt(i)!=']' &&
                         i<line.length(); ++i){
                    macroName+=line.charAt(i);
                }
                for(int j=i; line.charAt(j)!=']' &&
                         j<line.length(); ++j){
                    longName+=line.charAt(j);
                }
                
                if (macroName.equals("FIDOLIB")) {
                	libraryName = longName;
                	continue;
                } else {
                	if(!prefix.equals(""))
            			macroName=prefix+"."+macroName;
            		macroName=macroName.toLowerCase();
                	library.put(macroName, new 
                		MacroDesc(macroName,"","","",""));
                	continue;
                }
            }
            
           
            if(!macroName.equals("")){
                library.put(macroName, new MacroDesc(macroName.toLowerCase(),longName,
                ((MacroDesc)library.get(macroName)).description+"\n"+line,
                categoryName, libraryName));
            }       
        }   
    }
    
    /** Read the library contained in a file
        @param openFileName the name of the file to be loaded
    */
    public void readLibraryFile(String openFileName)
    	throws IOException
    {
       	FileReader input = new FileReader(openFileName);
        BufferedReader bufRead = new BufferedReader(input);
        String prefix="";

        prefix = Globals.getFileNameOnly(openFileName);
       	if (prefix.equals("FCDstdlib"))
       		prefix="";
       		
        readLibraryBufferedReader(bufRead, prefix);

		bufRead.close();
    }

	public void readLibraryBufferedReader(BufferedReader bufRead, String prefix)
		throws IOException
	{
	        String macroName="";
        String longName="";
        String categoryName="";
        String libraryName="";
       
       
       	
       	
        int i;
        
      
                
        StringBuffer txt= new StringBuffer();    
        String line="";
             
       
        
        while(true) {
            
            line = bufRead.readLine();
            
            if(line==null)
            	break; 
            	
            line=line.trim();       // Avoid trailing spaces
            
            if (line.length()<=1)	// Avoid processing shorter lines 
                continue;
                
            if(line.charAt(0)=='{') { // A category
            	categoryName="";
                for(i=1; line.charAt(i)!='}' &&
                         i<line.length(); ++i){
                    categoryName+=line.charAt(i);
                }
                continue;
            }
                
            if(line.charAt(0)=='[') { // A macro
                macroName="";
            		
                longName="";
                for(i=1; line.charAt(i)!=' ' &&
                         line.charAt(i)!=']' &&
                         i<line.length(); ++i){
                    macroName+=line.charAt(i);
                }
                for(int j=i; line.charAt(j)!=']' &&
                         j<line.length(); ++j){
                    longName+=line.charAt(j);
                }
                
                if (macroName.equals("FIDOLIB")) {
                	libraryName = longName;
                	continue;
                } else {
                	if(!prefix.equals(""))
            			macroName=prefix+"."+macroName;
            		
   		      		macroName=macroName.toLowerCase();
                	library.put(macroName, new 
                		MacroDesc(macroName,"","","",""));
                	continue;
                }
            }
            
           
            if(!macroName.equals("")){
   	      		macroName=macroName.toLowerCase();
                library.put(macroName, new MacroDesc(macroName,longName,
                ((MacroDesc)library.get(macroName)).description+"\n"+line,
                categoryName, libraryName));
            }       
                    
        } 
        
	}
	
    
    /** Obtain the description of the current coordinate mapping.
        @return the current coordinate mapping.
    
    */
    final public MapCoordinates getMapCoordinates()
    {
        return cs;
    }
    
    /**	Set the current coordinate mapping.
    	@param m the new coordinate mapping to be used.
    */
    final public void setMapCoordinates(MapCoordinates m)
    {
        cs=m;
    }
    /** Add a graphic primitive.
        @param p the primitive to be added.
        @param save save the undo state.
    
    */
    public void addPrimitive(GraphicPrimitive p, boolean save)
    {   
    	// Make sure the layers are ordered. This increases the drawing speed
    	GraphicPrimitive g;
    	int i;
    	
  		for (i=0; i<primitiveVector.size(); ++i){
    	
        	g=(GraphicPrimitive)primitiveVector.get(i);
			
        	if(g.getLayer()>p.getLayer()) {
				break;
        	}
        }	
        
        primitiveVector.add(i,p);
        if (save) saveUndoState();

    }
    
    
    /** Get the Fidocad text file.
    
    	@param extensions specify if FCJ extensions should be used
        @return the sketch in the text Fidocad format
    */
    public StringBuffer getText(boolean extensions)
    {
        int i;
        StringBuffer s=new StringBuffer();
        
        
        for (i=0; i<primitiveVector.size(); ++i){
            s.append(new StringBuffer(
                ((GraphicPrimitive)primitiveVector.get(i)).toString(
                	extensions)));
            if(useWindowsLineFeed) 
            	s.append("\r");
        }
        
        return s;
    }
    
    final public int getMaxLayer()
    {
    	return maxLayer;
    }
    
    /** Draw all primitives.
        @param G the graphic context to be used.
    */
    final public void draw(Graphics2D G)
    {
    	draw_p(G, false);
    }
    
    
    /** Draw (fast but inaccurate) all primitives.
        @param G the graphic context to be used.
    */
    final public void drawFast(Graphics2D G)
    {
    	draw_p(G, true);
    }
    
    private double oZ, oX, oY, oO;
    final private void draw_p(Graphics2D G, boolean isFast)
    {
		GraphicPrimitive gg;
		int i_index;
    	int j_index;
    	
    	if(oZ!=cs.getXMagnitude() || oX!=cs.getXCenter() || oY!=cs.getYCenter() ||
    	   oO!=cs.getOrientation()) {
    		oZ=cs.getXMagnitude();
    		oX=cs.getXCenter();
    		oY=cs.getYCenter();
    		oO=cs.getOrientation();
    		for (i_index=0; i_index<primitiveVector.size(); ++i_index){
    			gg=(GraphicPrimitive)primitiveVector.get(i_index);
    			gg.setChanged(true);
    		}
    	}
    	

        needHoles=drawOnlyPads;
        
        // If it is needed, at first, show the macro origin (100, 100) in
        // logical coordinates.
        
   		maxLayer=-1;
   		
   		if (hasFCJOriginVisible) {
        	G.setColor(Color.red);
        	G.fillOval(cs.mapXi(100, 100, false)-4,cs.mapYi(100, 100, false)-4, 8, 8);
        }
        if(drawOnlyLayer>=0 && !drawOnlyPads){
        	for (i_index=0; i_index<primitiveVector.size(); ++i_index){
        		gg=(GraphicPrimitive)primitiveVector.get(i_index);
    
   				if (gg.getLayer()>maxLayer) 
   					maxLayer = gg.getLayer();
        		
				// This should improve the redrawing speed.
				// The layers are kept ordered
				if (gg.getLayer()>drawOnlyLayer)
					break;
			
        		if(gg.getLayer()==drawOnlyLayer && 
        			!(gg instanceof PrimitiveMacro)) {
        			if (isFast) 
        				gg.drawFast(G, cs, layerV);
        			else
        				gg.draw(G, cs, layerV);      		
        			
        		} else if(gg instanceof PrimitiveMacro) {
        			((PrimitiveMacro)gg).setDrawOnlyLayer(drawOnlyLayer);
        			if (isFast) 
        				gg.drawFast(G, cs, layerV);
        			else
        				gg.draw(G, cs, layerV);    
					needHoles=((PrimitiveMacro)gg).getNeedHoles();
					if (((PrimitiveMacro)gg).getMaxLayer()>maxLayer) 
        					maxLayer = ((PrimitiveMacro)gg).getMaxLayer();

        		}

       			if(gg instanceof PrimitivePCBPad)
       				needHoles=true;

       		}
       		return;
       	} else if (!drawOnlyPads) {
        	cs.resetMinMax();

        	for(j_index=0;j_index<layerV.size(); ++j_index) {

        		for (i_index=0; i_index<primitiveVector.size(); ++i_index){
        			gg=(GraphicPrimitive)primitiveVector.get(i_index);
    
    				if (gg.getLayer()>maxLayer) 
        					maxLayer = gg.getLayer();
					// this should improve the redrawing speed. 

					if (j_index>1 && gg.getLayer()>j_index)
						break;
        			
        		
        			if(gg.getLayer()==j_index && !(gg instanceof PrimitiveMacro)){
        				if (isFast) 
        					gg.drawFast(G, cs, layerV);
        				else
        					gg.draw(G, cs, layerV);    
        			} else if(gg instanceof PrimitiveMacro) {
        				((PrimitiveMacro)gg).setDrawOnlyLayer(j_index);
        				if (isFast) 
        					gg.drawFast(G, cs, layerV);
        				else
        					gg.draw(G, cs, layerV);    
        				
						if(((PrimitiveMacro)gg).getNeedHoles())
							needHoles=true;
       					if (((PrimitiveMacro)gg).getMaxLayer()>maxLayer) 
       						maxLayer = ((PrimitiveMacro)gg).getMaxLayer();

       				}
       				if(gg instanceof PrimitivePCBPad)
       					needHoles=true;
        			
        		}
        		if (j_index>maxLayer)
        			break;
       		}
        }
        
        
        
        // Draw in a second time only the PCB pads, in order to ensure that the
        // drills are always open.
        if(needHoles) {
        	for (i_index=0; i_index<primitiveVector.size(); ++i_index){
            	if ((gg=(GraphicPrimitive)primitiveVector.get(i_index)) 
            		instanceof PrimitivePCBPad) {
					((PrimitivePCBPad)gg).setDrawOnlyPads(true);
					
					if(isFast) {
            			((PrimitivePCBPad)gg).drawFast(G, cs, layerV);
            		} else {
            			((PrimitivePCBPad)gg).draw(G, cs, layerV);
            		}
            		((PrimitivePCBPad)gg).setDrawOnlyPads(false);
            	} else if (gg instanceof PrimitiveMacro) { 
            		// Uhm... not beautiful
            		((PrimitiveMacro)gg).setDrawOnlyPads(true);

					if(isFast) {
            			((PrimitiveMacro)gg).drawFast(G, cs, layerV);
            		} else {
            			((PrimitiveMacro)gg).draw(G, cs, layerV);
            		}
            		((PrimitiveMacro)gg).setDrawOnlyPads(false);
            	}
        	}
        }
        
        
    }
    
    public void setDrawOnlyPads(boolean pd)
 	{
 		drawOnlyPads=pd;
 	}
 	
 	/** Set the layer to be drawn. If it is negative, draw all layers.
 	
 	@param la the layer to be drawn.
 	
 	*/
 	
 	public void setDrawOnlyLayer(int la)
 	{
 		drawOnlyLayer=la;
 	}
 	
    /** Draw the grid in the given graphic context
        @param G the graphic context to be used
        @param xmin the x (screen) coordinate of the upper left corner
        @param ymin the y (screen) coordinate of the upper left corner
        @param xmax the x (screen) coordinate of the bottom right corner
        @param ymax the y (screen) coordinate of the bottom right corner
    
    */
    public void drawGrid(Graphics2D G, int xmin, int ymin, int xmax, int ymax) 
    {

    	int dx=cs.getXGridStep();
    	int dy=cs.getYGridStep();
    	int mul=1;
    	double toll=0.01;
    	double z=cs.getYMagnitude();
    	
    	double x;
    	double y;
    	
    	double m=1.0;
    	
    	// Calculate the minimum common integer multiple of the dot 
    	// spacement and calculate the image size.
    	
    	for (double l=1; l<105; ++l) {
    		if (Math.abs(l*z-Math.round(l*z))<toll) {
    			mul=(int)l;
    			break;
    		}
    	}
    	
    	double ddx=Math.abs(cs.mapXi(dx,0,false)-cs.mapXi(0,0,false));
    	double ddy=Math.abs(cs.mapYi(0,dy,false)-cs.mapYi(0,0,false));
    	int d=1;
    	
    	if (ddx>50 || ddy>50) {
    		d=2;
    	} else if (ddx<3 || ddy <3) {
    		dx=5*cs.getXGridStep();
    		dy=5*cs.getYGridStep();
    		ddx=Math.abs(cs.mapXi(dx,0,false)-cs.mapXi(0,0,false));
    		ddy=Math.abs(cs.mapYi(0,dy,false)-cs.mapYi(0,0,false));
    	
    	}
				
		int width=Math.abs(cs.mapX(mul*dx,0)-cs.mapX(0,0));
		if (width<=0) width=1;
				
		int height=Math.abs(cs.mapY(0,0)-cs.mapY(0,mul*dy));
        if (height<=0) height=1;
        
        if (width>1000 || height>1000) {
        	//System.out.println("traditional grid");
        	G.setColor(Color.white);
        	G.fillRect(xmin,ymin,xmax,ymax);
        	G.setColor(Color.gray);
        	for (x=cs.unmapXsnap(xmin); x<=cs.unmapXsnap(xmax); x+=dx) {
        		for (y=cs.unmapYsnap(ymin); y<=cs.unmapYsnap(ymax); y+=dy) {
        		   	G.fillRect(cs.mapXi((int)x,(int)y, false),cs.mapYi((int)x,
        		   		(int)y, false),d,d);
				}
			}
			
			return;
        }
		
		if(oldZoom!=z) {
			try {
        		// Create a buffered image in which to draw
        		bufferedImage = new BufferedImage(width, height, 
        								  BufferedImage.TYPE_INT_RGB);
    		} catch (java.lang.OutOfMemoryError E) {
    			System.out.println("Out of memory error when painting grid");
    			return;
    		}
    	
        	// Create a graphics contents on the buffered image
        	Graphics2D g2d = bufferedImage.createGraphics();
        	g2d.setColor(Color.white);
        	g2d.fillRect(0,0,width,height);
        	g2d.setColor(Color.gray);
        
        	for (x=0; x<=cs.unmapXsnap(width); x+=dx) {
        		for (y=0; y<=cs.unmapYsnap(height); y+=dy) {
        		   	g2d.fillRect(cs.mapX((int)x,(int)y),cs.mapY((int)x,
        		   		(int)y),d,d);
				}
			}
			oldZoom=z;
		}
		
		Rectangle anchor = new Rectangle(width, height);

		TexturePaint tp= new TexturePaint(bufferedImage, anchor);
    	G.setPaint(tp);
    	G.fillRect(0, 0, xmax, ymax);
    	
    }
    
    /** Draw the handles of all selected primitives
        @param G the graphic context to be used.
    */
    public void drawSelectedHandles(Graphics2D G)
    {
        int i;
        for (i=0; i<primitiveVector.size(); ++i){
            if(((GraphicPrimitive)primitiveVector.get(i)).getSelected())
                ((GraphicPrimitive)primitiveVector.get(i)).drawHandles(G,
                   cs);
        }
        
    }
    
    
    /** Deselect all primitives.
        
    */
    public void deselectAll()
    {
        int i;
        for (i=0; i<primitiveVector.size(); ++i){
            ((GraphicPrimitive)primitiveVector.get(i)).setSelected(false);
        }
        
    }
    
    
    /** Select all primitives.
        
    */
    public void selectAll()
    {
        int i;
        for (i=0; i<primitiveVector.size(); ++i){
            ((GraphicPrimitive)primitiveVector.get(i)).setSelected(true);
        }
        
    }
    
    /** Delete all selected primitives.
        
    */
    public void deleteAllSelected()
    {
        int i;

        for (i=0; i<primitiveVector.size(); ++i){
            if(((GraphicPrimitive)primitiveVector.get(i)).getSelected())
                primitiveVector.remove((GraphicPrimitive)
                                        primitiveVector.get(i--));
        }
       	saveUndoState();
        
    }
    
        
    /** Set the font of all macros.
        @param f the font name
    */
    public void setMacroFont(String f)
    {
        int i;
        macroFont=f;

        for (i=0; i<primitiveVector.size(); ++i){
            if((GraphicPrimitive)primitiveVector.get(i) instanceof PrimitiveMacro) {
            	((PrimitiveMacro)primitiveVector.get(i)).setMacroFont(f);
            }
        }
        
    }
    
    
    /** Get the font of all macros.
        @return the font name
    */
    public String getMacroFont()
    {        
        return macroFont;
    }
    
    /** Copy in the system clipboard all selected primitives.
        @param extensions specify if FCJ extensions should be applied
        @param splitNonStandard specify if non standard macros should be split
    */
    public void copySelected(boolean extensions, boolean splitNonStandard)
    {
        int i;
        StringBuffer s=new StringBuffer("[FIDOCAD]\n");
        
        for (i=0; i<primitiveVector.size(); ++i){
            if(((GraphicPrimitive)primitiveVector.get(i)).getSelected())
                s.append(((GraphicPrimitive)primitiveVector.get(i
                	)).toString(extensions));
        }
        
        /*  If we have to split non standard macros, we need to work on a 
        	temporary file, since the splitting works on the basis of the 
        	export technique.        
        */
        
        // SCHIFIO... tutto questo non è molto efficiente né elegante
        
        if (splitNonStandard) {
			ParseSchem Q=new ParseSchem();
			Q.setLibrary(library); 			// Inherit the library
 			Q.setLayers(layerV);	// Inherit the layers
 			
 			// from the obtained string, obtain the new Q object which will
 			// be exported and then loaded into the clipboard.
 			
 			try {
 				Q.parseString(new StringBuffer(s)); 
 				File temp= File.createTempFile("copy", ".fcd");
 				temp.deleteOnExit();
 				
 				ExportGraphic.export(temp,  Q, "fcd", 1,true,false, 
 					splitNonStandard);
 				
 				FileReader input = new FileReader(temp);
        		BufferedReader bufRead = new BufferedReader(input);
                
        		StringBuffer txt;    
        		String line="";
                        
        		txt = new StringBuffer(bufRead.readLine());
                        
        		txt.append("\n");
                        
        		while (line != null){
            		line =bufRead.readLine();
            		txt.append(line);
            		txt.append("\n");
        		}
            
        		bufRead.close();
 				
 				s = txt;
 			} catch(IOException e) {
                	System.out.println("Error: "+e); 
            }
 		

		}
        
        
        
        
        // get the system clipboard
		Clipboard systemClipboard =Toolkit.getDefaultToolkit()
			.getSystemClipboard();
		
		Transferable transferableText = new StringSelection(s.toString());
		systemClipboard.setContents(transferableText,null);
    }
    
    /** Paste from the system clipboard
        
    */
    public void paste()
    {
        int i;
        TextTransfer textTransfer = new TextTransfer();
		
		deselectAll();
		
		try {
        	addString(new 
        		StringBuffer(textTransfer.getClipboardContents()),true);
        } catch (Exception E) {}
        
        moveAllSelected(cs.getXGridStep(), cs.getYGridStep());
        
    	saveUndoState();
        
    }
    
    /** Get the first selected primitive
    	@return the selected primitive, null if none.
    */
    public GraphicPrimitive getFirstSelectedPrimitive()
    {
    	int i;
    	GraphicPrimitive gp;
        for (i=0; i<primitiveVector.size(); ++i){
        	gp=(GraphicPrimitive)primitiveVector.get(i);
        	if (gp.getSelected())
        		return gp;
        }
        return null;
    }
    
    /** Calculates the minimum distance between the given point and
    	a set of primitive. Every coordinate is logical.
    	
    	@param px the x coordinate of the given point.
        @param py the y coordinate of the given point.
    	@return the distance in logical units.
    */
    public int distancePrimitive(int px, int py)
    {
        int i;
        int distance;
        int mindistance=Integer.MAX_VALUE;
        int isel=0;
        int layer=0;
        
        for (i=0; i<primitiveVector.size(); ++i){
            distance=((GraphicPrimitive)
                       primitiveVector.get(i)).getDistanceToPoint(px,py);
                       
            layer= ((GraphicPrimitive)
                       primitiveVector.get(i)).getLayer();
                       
            if(((LayerDesc)layerV.get(layer)).getVisible() &&
            	distance<=mindistance) {
                
                isel=i;
                mindistance=distance;
                
            }
        }
        
        return mindistance;
        
        
    }
    
    
    /** Select primitives close to the given point. Every parameter is given in
        logical coordinates.
        @param px the x coordinate of the given point.
        @param py the y coordinate of the given point.
        @param tolerance tolerance for the selection.
        @param toggle select always if false, toggle selection on/off if true
        @return true if a primitive has been selected.
    */
    public boolean selectPrimitive(int px, int py, int tolerance, 
    	boolean toggle)
    {
    
    	int i;
        int distance;
        int mindistance=Integer.MAX_VALUE;
        int isel=0;
        int layer;
        GraphicPrimitive gp;
        
        for (i=0; i<primitiveVector.size(); ++i){
        
        	layer= ((GraphicPrimitive)
                       primitiveVector.get(i)).getLayer();
                       
            if(((LayerDesc)layerV.get(layer)).getVisible() ||
            	(GraphicPrimitive)primitiveVector.get(i) instanceof PrimitiveMacro) {
            	distance=((GraphicPrimitive)
                       primitiveVector.get(i)).getDistanceToPoint(px,py);
            
            	if (distance<=mindistance) {
             	   isel=i;
            	    mindistance=distance;
                
           		}
           	}
        }
    
    	if (mindistance<tolerance){
        	gp=((GraphicPrimitive)primitiveVector.get(isel));
        	if(!toggle) {
        		gp.setSelected(true);
        	} else {
        		boolean sel=gp.getSelected();
        		gp.setSelected(!sel);
        	}
        	return true;
    	} else {
        return false;
        }
     
    
    }    
    
    /** Rotate all selected primitives.
        
    */
    public void rotateAllSelected()
    {
        int i;
        int ix=100; // rotation point
        int iy=100;
		boolean firstPrimitive=true;

        for (i=0; i<primitiveVector.size(); ++i){
            if(((GraphicPrimitive)primitiveVector.get(i)).getSelected()) {
            
            	// The rotation point is given by the first primitive
            	if(firstPrimitive){ 
            		ix=((GraphicPrimitive)primitiveVector.get(i
               			)).getFirstPoint().x;
               		iy=((GraphicPrimitive)primitiveVector.get(i
               			)).getFirstPoint().y;
               	}
               	
               	firstPrimitive=false;
            
               ((GraphicPrimitive)primitiveVector.get(i
               			)).rotatePrimitive(false, ix, iy);
            }
        }
       	saveUndoState();
        
    }
    
    /** Move all selected primitives.
    	
    	@param dx relative x movement
    	@param dy relative y movement
        
    */
    public void moveAllSelected(int dx, int dy)
    {
        int i;

        for (i=0; i<primitiveVector.size(); ++i){
            if(((GraphicPrimitive)primitiveVector.get(i)).getSelected())
               ((GraphicPrimitive)primitiveVector.get(i
               			)).movePrimitive(dx, dy);
        }
       	saveUndoState();
        
    }
    
    /** Mirror all selected primitives.
        
    */
    public void mirrorAllSelected()
    {
        int i;
        int ix=100;
        boolean firstPrimitive=true;

        for (i=0; i<primitiveVector.size(); ++i){
            if(((GraphicPrimitive)primitiveVector.get(i)).getSelected()) {

            	// The rotation point is given by the first primitive
            	if(firstPrimitive){ 
            		ix=((GraphicPrimitive)primitiveVector.get(i
               			)).getFirstPoint().x;
               	}
               	
               	firstPrimitive=false;



               ((GraphicPrimitive)primitiveVector.get(i
               			)).mirrorPrimitive(ix);

			}

        }
       	saveUndoState();
        
    }
    
    /** Select primitives in a rectangular region (given in logical coordinates)
        @param px the x coordinate of the top left point.
        @param py the y coordinate of the top left point.
        @param w the width of the region
        @param h the height of the region
        @return true if at least a primitive has been selected
    */
    public boolean selectRect(int px, int py, int w, int h)
    {
        int i,j;
        int distance;
        int mindistance=Integer.MAX_VALUE;
        int isel=0;
        GraphicPrimitive gp;
        int layer;
        boolean s=false;
        
        for (i=0; i<primitiveVector.size(); ++i){
            gp=((GraphicPrimitive) primitiveVector.get(i));
            
            layer= ((GraphicPrimitive)
                       primitiveVector.get(i)).getLayer();
                       
            if(((LayerDesc)layerV.get(layer)).getVisible() ||
            	(GraphicPrimitive)primitiveVector.get(i) instanceof PrimitiveMacro) {
            	if(gp.selectRect(px,py,w,h))
            		s=true;
                
           	}
            
            
        }
            
        
        return s;
    }
    
    
    /** Start dragging handle. Check if the pointer is on the handle of a 
        primitive and if it is the case, enter the dragging state.
        
        @param px the (screen) x coordinate of the pointer
        @param py the (screen) y coordinate of the pointer
        @param tolerance the tolerance (screen. i.e. no of pixel)
        @param multiple specifies whether multiple selection is active
    
    */
    public void dragHandleStart(int px, int py, int tolerance, boolean multiple)
    {
        int i;
        int isel=0;
        int mindistance=Integer.MAX_VALUE;
        int distance;
        int layer;
        
        hasMoved=false;
        
        GraphicPrimitive gp;
        
        oldpx=cs.unmapXnosnap(px);
        oldpy=cs.unmapXnosnap(py);
        
        
        firstDrag=true;
        
        int sptol=Math.abs(cs.unmapXnosnap(px+tolerance)-cs.unmapXnosnap(px));
        if (sptol<2) sptol=2; 
        
        
        for (i=0; i<primitiveVector.size(); ++i){
            gp=(GraphicPrimitive)primitiveVector.get(i);
      		layer= gp.getLayer();
                       
            if(!((LayerDesc)layerV.get(layer)).getVisible() &&
            	!(gp instanceof PrimitiveMacro))
            	continue;
            
            if(gp.getSelected()){
                handleBeingDragged=gp.onHandle(cs, px, py);
                
                // Verify if the pointer is on a handle
                if(handleBeingDragged!=GraphicPrimitive.NO_DRAG){
                    primBeingDragged=gp;
                    
                    continue;
                } 
            }
            distance=gp.getDistanceToPoint(oldpx,oldpy);
            if (distance<=mindistance) {
                isel=i;
                mindistance=distance;   
            }
        }
        // Verify if the whole primitive should be drag
        if (mindistance<sptol && handleBeingDragged<0){
            
            primBeingDragged=(GraphicPrimitive)primitiveVector.get(isel);
            if (!multiple && !primBeingDragged.getSelected())
                deselectAll();
            if(!multiple) {
            	primBeingDragged.setSelected(true);
            }
            handleBeingDragged=GraphicPrimitive.DRAG_PRIMITIVE;
            firstDrag=true;
            oldpx=cs.unmapXsnap(px);
            oldpy=cs.unmapXsnap(py);
        } else if (handleBeingDragged<0) { 
        	// We want to select things in a rectangular area
        	oldpx=cs.unmapXsnap(px);
            oldpy=cs.unmapXsnap(py);
        	handleBeingDragged=GraphicPrimitive.RECT_SELECTION;
        }
        
       
    }
    
    
    /** End dragging handle.
        @param px the (screen) x coordinate of the pointer
        @param py the (screen) y coordinate of the pointer
        @param multiple specifies whether multiple selection is active
    
    
    */
    public void dragHandleEnd(CircuitPanel P, int px, int py, boolean multiple)
    {
        // Check if we are effectively dragging something...
        if(handleBeingDragged<0){
        	P.resetOldEvidence();
        	if(handleBeingDragged==GraphicPrimitive.RECT_SELECTION){
        		P.setEvidenceRect(0,0,-1,-1);
        		int xa=Math.min(oldpx, cs.unmapXnosnap(px));
        		int ya=Math.min(oldpy, cs.unmapYnosnap(py));
        		int xb=Math.max(oldpx, cs.unmapXnosnap(px));
        		int yb=Math.max(oldpy, cs.unmapYnosnap(py));
        		if(!multiple) deselectAll();
        		selectRect(xa, ya, (xb-xa), (yb-ya)); 
        	}
        	// Test if we are anyway dragging an entire primitive
        	if(handleBeingDragged==GraphicPrimitive.DRAG_PRIMITIVE){
        		if (hasMoved) saveUndoState();

        	}
            handleBeingDragged=GraphicPrimitive.NO_DRAG;
            return;
        }

        // Here we adjust the new positions...
        primBeingDragged.virtualPoint[handleBeingDragged].x=cs.unmapXsnap(px);
        primBeingDragged.virtualPoint[handleBeingDragged].y=cs.unmapYsnap(py);
        handleBeingDragged=GraphicPrimitive.NO_DRAG;
       	saveUndoState();
        

    }
    
   
    /** Drag an handle.
        @param g the graphic context. It hould be in the XOR mode.
        @param px the (screen) x coordinate of the pointer
        @param py the (screen) y coordinate of the pointer
    
    
    */
    public void dragHandleDrag(CircuitPanel P, Graphics2D g, int px, int py)
    {
        hasMoved=true;

    	
        // Check if we are effectively dragging and handle...
        if(handleBeingDragged<0){
            if(handleBeingDragged==GraphicPrimitive.DRAG_PRIMITIVE)
            	dragPrimitives(P,g, px, py);
            	
            // if not, we are performing a rectangular selection
            if(handleBeingDragged==GraphicPrimitive.RECT_SELECTION) {
            	
    	
            	int xa = cs.mapXi(oldpx, oldpy, false);
            	int ya = cs.mapYi(oldpx, oldpy, false);
        		int xb = opx;
        		int yb = opy;
        		g.setStroke(new BasicStroke(1));
        		
        
        		if(!firstDrag) {
        			if(!Globals.doNotUseXOR) 
        			    // In the XOR mode, here we delete the previous draw
        				g.drawRect(Math.min(xa,xb), Math.min(ya,yb), 
        					   Math.abs(xb-xa), Math.abs(yb-ya));
        			else{
        				P.setOldEvidence(Math.min(xa,xb), Math.min(ya,yb), 
        					   Math.abs(xb-xa), Math.abs(yb-ya));
        				xb=px;
						yb=py;
						opx=px;
						opy=py;
						
        				P.setEvidenceRect(Math.min(xa,xb), Math.min(ya,yb), 
        					   Math.abs(xb-xa), Math.abs(yb-ya));
        				P.repaint();
        				//P.resetOldEvidence();
        				
        				return;
        				        				
        			}
        		}
				P.setOldEvidence(Math.min(xa,xb), Math.min(ya,yb), 
        					   Math.abs(xb-xa), Math.abs(yb-ya));
				xb=px;
				yb=py;
				opx=px;
				opy=py;
        		firstDrag=false;
        		if(Globals.doNotUseXOR) 
        			g.setColor(Color.green);

        		g.drawRect(Math.min(xa,xb), Math.min(ya,yb), 
        					   Math.abs(xb-xa), Math.abs(yb-ya));
            }
            	
            
            return;
        }
        
        
        // In the XOR mode, here we delete the previous position.
        if(!firstDrag) {
        	if(!Globals.doNotUseXOR) 
        		primBeingDragged.drawFast(g,cs,layerV);
        	else
       			P.repaint();
       	}
        firstDrag=false;

        // Here we adjust the new positions...
        primBeingDragged.virtualPoint[handleBeingDragged].x=cs.unmapXsnap(px);
        primBeingDragged.virtualPoint[handleBeingDragged].y=cs.unmapYsnap(py);
        primBeingDragged.setChanged(true);
        

        // Here we show the new place of the primitive.

        if(!Globals.doNotUseXOR) primBeingDragged.drawFast(g,cs,layerV);

    }
    
    /** Drag all the selected primitives during a drag operation. 
        Position the primitives in the given (screen) position
        
        @param g the graphic context
        @param px the x position (screen coordinates)
        @param py the y position (screen coordinates)
    
    */
    public void dragPrimitives(CircuitPanel P,Graphics2D g, int px, int py)
    {
        // Check if we are effectively dragging the whole primitive...
        if(handleBeingDragged!=GraphicPrimitive.DRAG_PRIMITIVE)
            return;
    
        
        firstDrag=false;
        
        int dx=cs.unmapXsnap(px)-oldpx;
        int dy=cs.unmapYsnap(py)-oldpy;
        
        oldpx=cs.unmapXsnap(px);
        oldpy=cs.unmapXsnap(py);
        
        if(dx==0 && dy==0)
        	return;

        // Here we adjust the new positions for all selected elements...
        for (int i=0; i<primitiveVector.size(); ++i){               
            primBeingDragged=(GraphicPrimitive)primitiveVector.get(i);
            if(primBeingDragged.getSelected()) {
            
            	// This code is needed to ensure that all layer are printed
            	// when dragging a component (it solves bug #24)
    			if (primBeingDragged instanceof PrimitiveMacro) {
        			((PrimitiveMacro)primBeingDragged).setDrawOnlyLayer(-1);
				}
		        primBeingDragged.setChanged(true);

                if(!firstDrag){
                	if(!Globals.doNotUseXOR) 		
                		primBeingDragged.drawFast(g,cs,layerV);
                	else
                		P.repaint();
                }
                for(int j=0; j<primBeingDragged.getControlPointNumber();++j){
                    
                    primBeingDragged.virtualPoint[j].x+=dx;
                    primBeingDragged.virtualPoint[j].y+=dy;
                    // Here we show the new place of the primitive.
                }
                if(!Globals.doNotUseXOR)
                	primBeingDragged.drawFast(g,cs,layerV);
                
            }
        }
  
    }   
    
    
    /** Parse the circuit contained in the StringBuffer specified.
    	This function resets the primitive database and then parses the circuit.
    	
    	@param s the string containing the circuit
    	
    
    */
    public void parseString(StringBuffer s) 
    	throws IOException
    {
        primitiveVector.clear();
		addString(s, false);
    }
    
    /** Parse the circuit contained in the StringBuffer specified.
    	this funcion add the circuit to the current primitive database.
    	
    	@param s the string containing the circuit
    	@param selectNew specify that the added primitives should be selected.
    
    */
    
    public void addString(StringBuffer s, boolean selectNew) 
    	throws IOException
    {
        int i; // Character pointer within the string 
        int j; // token counter within the string
        boolean hasFCJ=false; // the last primitive has FCJ extensions
        StringBuffer token=new StringBuffer(); 
        GraphicPrimitive g;
        String[] old_tokens=new String[MAX_TOKENS];;
        String[] name=new String[MAX_TOKENS];;
        String[] value=new String[MAX_TOKENS];;

		int vn=0, vv=0;
        int old_j=0;
        int macro_counter=0;

        /* 	This code is not very easy to read. If more extension of the
        	original FidoCad format (performed with the FCJ tag) are to be 
        	implemented, it can be interesting to rewrite the parser as a
        	state machine.
        */
        int k;      
        char c='\n';
        int len;

        
        lineNum=1;
        j=0;    // A fairy simple tokenizer
        token.setLength(0);
        len=s.length();
        for(i=0; i<len;++i){
            c=s.charAt(i);
            if(c=='\n' || c=='\r'|| i==len-1) { //The string finished
                if(i==len-1 && c!='\n' && c!=' '){
                    token.append(c);
                }
                ++lineNum;
                tokens[j]=token.toString();
                if (token.length()==0)  // Avoids trailing spaces
                    j--;
                
             /*   System.out.println("Reading...");
                for(int l=0; l<j+1; ++l)
                    System.out.println("l="+l+"  "+tokens[l]);*/
                try{
                	if(hasFCJ && !tokens[0].equals("FCJ")) {
                		if (old_tokens[0].equals("MC")) {
                			g=new PrimitiveMacro(library,layerV);
                        	g.parseTokens(old_tokens, old_j+1);
                        	g.setSelected(selectNew);
                        	addPrimitive(g,false);
                        	hasFCJ=false;
                        } else if (old_tokens[0].equals("LI")) {
		                    g=new PrimitiveLine();
           	                g.parseTokens(old_tokens, old_j+1);
            	            g.setSelected(selectNew);
                	        addPrimitive(g,false);
                        	hasFCJ=false;
                     
                        } else if (old_tokens[0].equals("BE")) {
		                    g=new PrimitiveBezier();
           	                g.parseTokens(old_tokens, old_j+1);
            	            g.setSelected(selectNew);
                	        addPrimitive(g,false);
                        	hasFCJ=false;
                     
                        } else if (old_tokens[0].equals("RP")||
                        	old_tokens[0].equals("RV")) {
		                    g=new PrimitiveRectangle();
           	                g.parseTokens(old_tokens, old_j+1);
            	            g.setSelected(selectNew);
                	        addPrimitive(g,false);
                        	hasFCJ=false;
                     
                        } else if (old_tokens[0].equals("EP")||
                        	old_tokens[0].equals("EV")) {
		                    g=new PrimitiveOval();
           	                g.parseTokens(old_tokens, old_j+1);
            	            g.setSelected(selectNew);
                	        addPrimitive(g,false);
                        	hasFCJ=false;
                     
                        } else if (old_tokens[0].equals("PP")||
               				old_tokens[0].equals("PV")) {
		       				g=new PrimitivePolygon();
           	    			g.parseTokens(old_tokens, old_j+1);
                			g.setSelected(selectNew);
                			addPrimitive(g,false);
                        	hasFCJ=false;
                     
            			}
                	}
                	
                	
                
                    if(tokens[0].equals("FCJ")) {	// FidoCadJ extension!
                    	if(hasFCJ && old_tokens[0].equals("MC")) {
                    		macro_counter=2;
                    	} else if (hasFCJ && old_tokens[0].equals("LI")) {
		                    g=new PrimitiveLine();
		                    
		                    for(int l=0; l<j+1; ++l)
                        		old_tokens[l+old_j+1]=tokens[l];
                        	
                        	old_j+=j+1;
        	                g.parseTokens(old_tokens, old_j+1);
            	            g.setSelected(selectNew);
                	        addPrimitive(g,false);
                     
                        } else if (hasFCJ && old_tokens[0].equals("BE")) {
		                    g=new PrimitiveBezier();
		                    
		                    for(int l=0; l<j+1; ++l)
                        		old_tokens[l+old_j+1]=tokens[l];
                        	
                        	old_j+=j+1;
        	                g.parseTokens(old_tokens, old_j+1);
            	            g.setSelected(selectNew);
                	        addPrimitive(g,false);
                     
                        } else if (hasFCJ && (old_tokens[0].equals("RV")||
                        	old_tokens[0].equals("RP"))) {
		                    g=new PrimitiveRectangle();
		                    
		                    for(int l=0; l<j+1; ++l)
                        		old_tokens[l+old_j+1]=tokens[l];
                        	
                        	old_j+=j+1;
        	                g.parseTokens(old_tokens, old_j+1);
            	            g.setSelected(selectNew);
                	        addPrimitive(g,false);
                    	} else if (hasFCJ && (old_tokens[0].equals("EV")||
                        	old_tokens[0].equals("EP"))) {
		                    g=new PrimitiveOval();
		                    
		                    for(int l=0; l<j+1; ++l)
                        		old_tokens[l+old_j+1]=tokens[l];
                        	
                        	old_j+=j+1;
        	                g.parseTokens(old_tokens, old_j+1);
            	            g.setSelected(selectNew);
                	        addPrimitive(g,false);
                    	} else if (hasFCJ && (old_tokens[0].equals("PV")||
                        	old_tokens[0].equals("PP"))) {
		                    g=new PrimitivePolygon();
		                    
		                    for(int l=0; l<j+1; ++l)
                        		old_tokens[l+old_j+1]=tokens[l];
                        	
                        	old_j+=j+1;
        	                g.parseTokens(old_tokens, old_j+1);
            	            g.setSelected(selectNew);
                	        addPrimitive(g,false);
                    	}
                    	hasFCJ=false;
                    }if(tokens[0].equals("LI")) {
                        // Save the tokenized line.
                        // We cannot create the macro until we parse the 
                        // following line (which can be FCJ)
                    	macro_counter=0;
    
                        for(int l=0; l<j+1; ++l)
                        	old_tokens[l]=tokens[l];
                        old_j=j;
                        hasFCJ=true;
        
                    } else if(tokens[0].equals("BE")) {
                        macro_counter=0;
    
                        for(int l=0; l<j+1; ++l)
                        	old_tokens[l]=tokens[l];
                        old_j=j;
                        hasFCJ=true;
                    } else if(tokens[0].equals("MC")) {
                        // Save the tokenized line.
                        // We cannot create the macro until we parse the 
                        // following line (which can be FCJ)
                    	macro_counter=0;
                        for(int l=0; l<j+1; ++l)
                        	old_tokens[l]=tokens[l];
                        old_j=j;
                        hasFCJ=true;
        

                    } else if(tokens[0].equals("TE")) {
                    	hasFCJ=false;
                    	macro_counter=0;
                        g=new PrimitiveAdvText();
                        g.parseTokens(tokens, j+1);
                        g.setSelected(selectNew);
                        addPrimitive(g,false);
                    } else if(tokens[0].equals("TY")) {
                        hasFCJ=false;
                        
                        if(macro_counter==2) {
                        	macro_counter--;
                        	for(int l=0; l<j+1;++l)
                        	   	name[l]=tokens[l];
                        	vn=j;  	
                        	 
                        } else if(macro_counter==1) {
                        	for(int l=0; l<j+1;++l)
                        	   	value[l]=tokens[l];
                        	vv=j;   	
                        	g=new PrimitiveMacro(library,layerV);
                        	g.parseTokens(old_tokens, old_j+1);
                        	((PrimitiveMacro)g).setName(name,vn+1);
                        	((PrimitiveMacro)g).setValue(value,vv+1);

                        	g.setSelected(selectNew);
                        	addPrimitive(g, false);
                        	macro_counter=0;
                        } else {
                        	g=new PrimitiveAdvText();
                        	g.parseTokens(tokens, j+1);
                        	g.setSelected(selectNew);
                       	 	addPrimitive(g,false);
                       	 }
                    } else if(tokens[0].equals("PL")) {
                    	hasFCJ=false;
                    	macro_counter=0;
                        g=new PrimitivePCBLine();
                        g.parseTokens(tokens, j+1);
                        g.setSelected(selectNew);
                        addPrimitive(g,false);
                    } else if(tokens[0].equals("PA")) {
                    	hasFCJ=false;
                    	macro_counter=0;
                        g=new PrimitivePCBPad();
                        g.parseTokens(tokens, j+1);
                        g.setSelected(selectNew);
                        addPrimitive(g,false);
                    } else if(tokens[0].equals("SA")) {
                        hasFCJ=false;
                    	macro_counter=0;
                        g=new PrimitiveConnection();
                        g.parseTokens(tokens, j+1);
                        g.setSelected(selectNew);
                        addPrimitive(g,false);
                    }  else if(tokens[0].equals("EV")||tokens[0].equals("EP")) {
                    	macro_counter=0;
    
                        for(int l=0; l<j+1; ++l)
                        	old_tokens[l]=tokens[l];
                        old_j=j;
                        hasFCJ=true;
                } else if(tokens[0].equals("RV")||tokens[0].equals("RP")) {
                        macro_counter=0;
    
                        for(int l=0; l<j+1; ++l)
                        	old_tokens[l]=tokens[l];
                        old_j=j;
                        hasFCJ=true;
                    } else if(tokens[0].equals("PV")||tokens[0].equals("PP")) {
                        macro_counter=0;
    
                        for(int l=0; l<j+1; ++l)
                        	old_tokens[l]=tokens[l];
                        old_j=j;
                        hasFCJ=true;
     
                    }
                    
                } catch(IOException E) {
                    System.out.println("Error encountered: "+E.toString());
                    System.out.println("string parsing line: "+lineNum);
                } catch(NumberFormatException F) {
                    System.out.println("I could not read a number at line: "
                                       +lineNum);
                }
                
                
                
                j=0;
                token.setLength(0);
            } else if (c==' '){ // Ready for next token
                tokens[j]=token.toString();
                ++j;
                if (j>=MAX_TOKENS) {
                	IOException e=new IOException("Too much tokens!");
                	throw e;
                }
                token.setLength(0);
            } else {
                token.append(c);
            }
            
        }
        
        
        if(hasFCJ && !tokens[0].equals("FCJ")) {
        	if (old_tokens[0].equals("MC")) {
        		g=new PrimitiveMacro(library,layerV);
               	g.parseTokens(old_tokens, old_j+1);
               	g.setSelected(selectNew);
               addPrimitive(g,false);
            } else if (old_tokens[0].equals("LI")) {
		        g=new PrimitiveLine();
                g.parseTokens(old_tokens, old_j+1);
                g.setSelected(selectNew);
                addPrimitive(g,false);
                     
            } else if (old_tokens[0].equals("BE")) {
		        g=new PrimitiveBezier();
           	    g.parseTokens(old_tokens, old_j+1);
            	g.setSelected(selectNew);
                addPrimitive(g,false);  
            } else if (old_tokens[0].equals("RP")||
               	old_tokens[0].equals("RV")) {
		        g=new PrimitiveRectangle();
           	    g.parseTokens(old_tokens, old_j+1);
                g.setSelected(selectNew);
                addPrimitive(g,false);
                     
            } else if (old_tokens[0].equals("EP")||
               	old_tokens[0].equals("EV")) {
		        g=new PrimitiveOval();
           	    g.parseTokens(old_tokens, old_j+1);
                g.setSelected(selectNew);
                addPrimitive(g,false);
                     
            } else if (old_tokens[0].equals("PP")||
               	old_tokens[0].equals("PV")) {
		        g=new PrimitivePolygon();
           	    g.parseTokens(old_tokens, old_j+1);
                g.setSelected(selectNew);
                addPrimitive(g,false);
                     
            }
        }
    }
    

    
    /**	Export the file using the given interface
    
    @param exp the selected exporting interface
    @param header specify if an header and a tail should be written or not
    */
    public void exportDrawing(ExportInterface exp, boolean header)
    	throws IOException
    {
    	
    	// SCHIFIO ************************************************************
    	// Il codice è replicato dalla funzione draw().
    	// Il copia/incolla funziona abbastanza, ma potrebbe essere opportuno 
    	// fare un po' di pulizia.
    	
    	MapCoordinates mp = new MapCoordinates();
    	mp.setXMagnitude(1);
		mp.setYMagnitude(1);

 		mp.xCenter = cs.xCenter;
 		mp.yCenter= cs.yCenter;
		mp.orientation=cs.orientation;
		mp.mirror=cs.mirror;
 		mp.isMacro=cs.isMacro;
 		
 		int l;
     	int i;
		int j;
        
        GraphicPrimitive g;
    	
    	if (header)
    		exp.exportStart(ExportGraphic.getImageSize(this, 1, false), 
    			layerV, mp.getXGridStep());
        
        if(drawOnlyLayer>=0 && !drawOnlyPads){
        	for (i=0; i<primitiveVector.size(); ++i){
        		g=(GraphicPrimitive)primitiveVector.get(i);
        		l=g.getLayer();
        		if(l==drawOnlyLayer && 
        			!(g instanceof PrimitiveMacro)) {
        			if(((LayerDesc)(layerV.get(l))).getVisible())
            			g.export(exp, mp);
        			
        		}else if(g instanceof PrimitiveMacro) {
        			((PrimitiveMacro)g).setDrawOnlyLayer(drawOnlyLayer);
        			if(((LayerDesc)(layerV.get(l))).getVisible())
            			g.export(exp, mp); 
        		}
       		}
       		return;
       	} else if (!drawOnlyPads) {
        	cs.resetMinMax();
        	for(j=0;j<layerV.size(); ++j) {
        		for (i=0; i<primitiveVector.size(); ++i){
        		
        			g=(GraphicPrimitive)primitiveVector.get(i);
        			l=g.getLayer();
        			if(l==j && !(g instanceof PrimitiveMacro)){
        				if(((LayerDesc)(layerV.get(l))).getVisible())
            				g.export(exp, mp);
        				
        			} else if(g instanceof PrimitiveMacro) {
        				((PrimitiveMacro)g).setDrawOnlyLayer(j);
        				if(((LayerDesc)(layerV.get(l))).getVisible())
            				g.export(exp, mp);
        				
        			}
        		}
       		}
       		
       		
        
        }
        
        
        
        // Draw in a second time only the PCB pads, in order to ensure that the
        // drills are always open.
        
        for (i=0; i<primitiveVector.size(); ++i){
            if ((g=(GraphicPrimitive)primitiveVector.get(i)) instanceof 
            	PrimitivePCBPad) {
				((PrimitivePCBPad)g).setDrawOnlyPads(true);
				l=g.getLayer();
				if(((LayerDesc)(layerV.get(l))).getVisible())
            		g.export(exp, mp);
            	((PrimitivePCBPad)g).setDrawOnlyPads(false);
            } else if (g instanceof PrimitiveMacro) { // Uhm... not beautiful
            	((PrimitiveMacro)g).setDrawOnlyPads(true);
            	l=g.getLayer();
				if(((LayerDesc)(layerV.get(l))).getVisible())
            		g.export(exp, mp);
            	((PrimitiveMacro)g).setDrawOnlyPads(false);
            	((PrimitiveMacro)g).resetExport();
            }
        }	
        
        if (header)
    		exp.exportEnd();
    }
    
    
    
    /**	Undo the last editing action
    
    */
    public void undo()
    {
    	try {
    		UndoState r = (UndoState)um.undoPop();
    		StringBuffer s=new StringBuffer(r.text);
    		parseString(s);
    		isModified = r.isModified;
    		openFileName = r.fileName;
    	
    		if(cl!=null) cl.somethingHasChanged();
    		
    	} catch (Exception E) 
    	{
    	}
    	
    }
    
    /**	Redo the last undo action
    
    */
    public void redo()
    {
    	try {
    	
    		UndoState r = (UndoState)um.undoRedo();
    		StringBuffer s=new StringBuffer(r.text);
    		parseString(s);
    		isModified = r.isModified;
    		openFileName = r.fileName;
    	
    		if(cl!=null) cl.somethingHasChanged();
    		
    	} catch (Exception E) 
    	{
    	}
    	
    }
    
        
    /** Save the undo state
    
    */
    public void saveUndoState()
    {
    	UndoState s = new UndoState();
    	s.text=getText(true).toString();
    	s.isModified=isModified;
    	s.fileName=openFileName;
    	
    	um.undoPush(s);
       	isModified = true;
       	if(cl!=null) cl.somethingHasChanged();

    }
    
    
    /** Determine if the drawing has been modified
    	@return the state
    */
    public boolean getModified ()
    {
       	return isModified;
    }

    /** Set the drawing modified state
    	@param s the new state to be set
    */
    public void setModified (boolean s)
    {
       	isModified = s;
      	if(cl!=null) cl.somethingHasChanged();

    }
    
    /** Set the listener of the state change
    	@param l the new listener
    */
    public void setHasChangedListener (HasChangedListener l)
    {
		cl = l;
	}
	
	/** Set the visibility of the macro origin
	
		@param s the visibility state
	*/
	public void setMacroOriginVisible(boolean s)
	{
		hasFCJOriginVisible = s;
	}
	
	/** Returns true if there is no drawing in memory
	*/
	public boolean isEmpty()
	{
		return primitiveVector.size()==0;
	}
	
	public final boolean getNeedHoles()
	{
		return needHoles;
	}
}

