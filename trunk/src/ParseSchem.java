import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import java.awt.geom.*;
import java.awt.datatransfer.*;


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
2.5		June 2009			D. Bucci 	Capitalize the first letters                                     




   Written by Davide Bucci, March 2007 - June 2009, davbucci at tiscali dot it
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
    @version 2.5, June 2009
*/

public class ParseSchem
{   
    static final int MAX_TOKENS=100;
    static final boolean useWindowsLineFeed=false;
    
    private boolean drawOnlyPads;
    private int drawOnlyLayer;
    
    private String[] tokens;
    private int lineNum;
    Vector primitiveVector;
    Vector layerV;
    MapCoordinates cs;
    private Map library;
    private boolean firstDrag;
    BufferedImage bufferedImage; // Useful for grid calculation
    double oldZoom;
    
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
   
   // A drawing modification flag. If true, there are unsaved changes
    private boolean isModified;
    
    private HasChangedListener cl;
    
    private boolean isMacroOriginVisible;
    
    public ParseSchem()
    {
        tokens=new String[MAX_TOKENS];
        primitiveVector=new Vector(100);
        cs=new MapCoordinates();
        layerV=new Vector(16);
        library=new TreeMap();
        firstDrag=false;
        um=new UndoManager(MAX_UNDO);
        oldZoom=-1;
        drawOnlyPads=false;
        drawOnlyLayer=-1;
        cl=null;
        
        ////////////////////////  ERRORE DI INCAPSULAMENTO
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

    public Vector getLayers()
    {
        return layerV;
    }
    
    /** Set the layer description vector
    
    	@param v a vector of LayerDesc describing layers.
    
    */
    public void setLayers(Vector v)
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
       
        String macroName="";
        String longName="";
        String categoryName="";
        String libraryName="";
       
        String prefix="";
       
       	prefix = Globals.getFileNameOnly(openFileName);
       	if (prefix.equals("FCDstdlib"))
       		prefix="";
       		
       	
        int i;
        
        System.out.println("opening: "+openFileName);
        
        FileReader input = new FileReader(openFileName);
        BufferedReader bufRead = new BufferedReader(input);
                
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
        
        bufRead.close();

    }

    
    /** Obtain the description of the current coordinate mapping.
        @return the current coordinate mapping.
    
    */
    public MapCoordinates getMapCoordinates()
    {
        return cs;
    }
    
    /**	Set the current coordinate mapping.
    	@param m the new coordinate mapping to be used.
    */
    public void setMapCoordinates(MapCoordinates m)
    {
        cs=m;
    }
    /** Add a graphic primitive.
        @param p the primitive to be added.
    
    */
    public void addPrimitive(GraphicPrimitive p)
    {   
        primitiveVector.add(p);
    	saveUndoState();

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
                ((GraphicPrimitive)primitiveVector.elementAt(i)).toString(
                	extensions)));
            if(useWindowsLineFeed) 
            	s.append("\r");
        }
        
        return s;
    }
    
    /** Draw all primitives.
        @param G the graphic context to be used.
    */
    public void draw(Graphics2D G)
    {
        int i;
        int j;
        GraphicPrimitive g;
        
        // If it is needed, at first, show the macro origin (100, 100) in
        // logical coordinates.
        
        if (isMacroOriginVisible) {
        	G.setColor(Color.red);
        	G.fillOval(cs.mapX(100, 100)-4,cs.mapY(100, 100)-4, 8, 8);
        }
        if(drawOnlyLayer>=0 && !drawOnlyPads){
        	for (i=0; i<primitiveVector.size(); ++i){
        		g=(GraphicPrimitive)primitiveVector.elementAt(i);
        		
        		if(g.getLayer()==drawOnlyLayer && 
        			!(g instanceof PrimitiveMacro)) {
        			g.draw(G, cs, layerV);  
        			
        		} else if(g instanceof PrimitiveMacro) {
        			((PrimitiveMacro)g).setDrawOnlyLayer(drawOnlyLayer);
        			g.draw(G, cs, layerV);  
        		}
       		}
       		return;
       	} else if (!drawOnlyPads) {
        	cs.resetMinMax();
        	for(j=0;j<layerV.size(); ++j) {
        		for (i=0; i<primitiveVector.size(); ++i){
        		
        			g=(GraphicPrimitive)primitiveVector.elementAt(i);
        			if(g.getLayer()==j && !(g instanceof PrimitiveMacro)){
        				g.draw(G, cs, layerV);  
        				
        			} else if(g instanceof PrimitiveMacro) {
        				((PrimitiveMacro)g).setDrawOnlyLayer(j);
        				g.draw(G, cs, layerV);  
        				
        			}
        		}
       		}
        }
        
        
        
        // Draw in a second time only the PCB pads, in order to ensure that the
        // drills are always open.
        
        if(true) {
        	for (i=0; i<primitiveVector.size(); ++i){
            	if ((g=(GraphicPrimitive)primitiveVector.elementAt(i)) 
            		instanceof PrimitivePCBPad) {
					((PrimitivePCBPad)g).setDrawOnlyPads(true);
            		((PrimitivePCBPad)g).draw(G, cs, layerV);
            		((PrimitivePCBPad)g).setDrawOnlyPads(false);
            	} else if (g instanceof PrimitiveMacro) { 
            		// Uhm... not beautiful
            		((PrimitiveMacro)g).setDrawOnlyPads(true);
            		((PrimitiveMacro)g).draw(G, cs, layerV);
            		((PrimitiveMacro)g).setDrawOnlyPads(false);
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
    	
    	// Calculate the minimum common integer multiple of the dot 
    	// spacement and calculate the image size.
    	
    	for (double l=1; l<105; ++l) {
    		if (Math.abs(l*z-Math.round(l*z))<toll) {
    			mul=(int)l;
    			break;
    		}
    	}
    	
    	double ddx=Math.abs(cs.mapX(dx,0)-cs.mapX(0,0));
    	double ddy=Math.abs(cs.mapY(0,dy)-cs.mapY(0,0));
    	
		
		
		
		
		
		int width=Math.abs(cs.mapX(mul*dx,0)-cs.mapX(0,0));
		if (width<=0) width=1;
		
		
		int height=Math.abs(cs.mapY(0,0)-cs.mapY(0,mul*dy));
        if (height<=0) height=1;
		
		if(oldZoom!=z) {
        	// Create a buffered image in which to draw
        	bufferedImage = new BufferedImage(width, height, 
        								  BufferedImage.TYPE_INT_RGB);
    
    	
        	// Create a graphics contents on the buffered image
        	Graphics2D g2d = bufferedImage.createGraphics();
        	g2d.setColor(Color.white);
        	g2d.fillRect(0,0,width,height);
        	g2d.setColor(Color.gray);
        
        	for (x=0; x<=cs.unmapXsnap(width); x+=dx) {
        		for (y=0; y<=cs.unmapYsnap(height); y+=dy) {
        		   	g2d.fillRect(cs.mapX((int)x,(int)y),cs.mapY((int)x,
        		   		(int)y),1,1);
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
            if(((GraphicPrimitive)primitiveVector.elementAt(i)).getSelected())
                ((GraphicPrimitive)primitiveVector.elementAt(i)).drawHandles(G,
                   cs);
        }
        
    }
    
    
    /** Deselect all primitives.
        
    */
    public void deselectAll()
    {
        int i;
        for (i=0; i<primitiveVector.size(); ++i){
            ((GraphicPrimitive)primitiveVector.elementAt(i)).setSelected(false);
        }
        
    }
    
    
    /** Select all primitives.
        
    */
    public void selectAll()
    {
        int i;
        for (i=0; i<primitiveVector.size(); ++i){
            ((GraphicPrimitive)primitiveVector.elementAt(i)).setSelected(true);
        }
        
    }
    
    /** Select all selected primitives.
        
    */
    public void deleteAllSelected()
    {
        int i;

        for (i=0; i<primitiveVector.size(); ++i){
            if(((GraphicPrimitive)primitiveVector.elementAt(i)).getSelected())
                primitiveVector.remove((GraphicPrimitive)
                                        primitiveVector.elementAt(i--));
        }
       	saveUndoState();
        
    }
    
    /** Copy in the system clipboard all selected primitives.
        @param extensions specify if FCJ extensions should be applied
    */
    public void copySelected(boolean extensions)
    {
        int i;
        StringBuffer s=new StringBuffer("[FIDOCAD]\n");
        
        for (i=0; i<primitiveVector.size(); ++i){
            if(((GraphicPrimitive)primitiveVector.elementAt(i)).getSelected())
                s.append(((GraphicPrimitive)primitiveVector.elementAt(i
                	)).toString(extensions));
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
        	gp=(GraphicPrimitive)primitiveVector.elementAt(i);
        	if (gp.getSelected())
        		return gp;
        }
        return null;
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
                       primitiveVector.elementAt(i)).getLayer();
                       
            if(((LayerDesc)layerV.get(layer)).getVisible()) {
            	distance=((GraphicPrimitive)
                       primitiveVector.elementAt(i)).getDistanceToPoint(px,py);
            
            	if (distance<=mindistance) {
             	   isel=i;
            	    mindistance=distance;
                
           		}
           	}
        }
    
    	if (mindistance<tolerance){
        	gp=((GraphicPrimitive)primitiveVector.elementAt(isel));
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
            if(((GraphicPrimitive)primitiveVector.elementAt(i)).getSelected()) {
            
            	// The rotation point is given by the first primitive
            	if(firstPrimitive){ 
            		ix=((GraphicPrimitive)primitiveVector.elementAt(i
               			)).getFirstPoint().x;
               		iy=((GraphicPrimitive)primitiveVector.elementAt(i
               			)).getFirstPoint().y;
               	}
               	
               	firstPrimitive=false;
            
               ((GraphicPrimitive)primitiveVector.elementAt(i
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
            if(((GraphicPrimitive)primitiveVector.elementAt(i)).getSelected())
               ((GraphicPrimitive)primitiveVector.elementAt(i
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
            if(((GraphicPrimitive)primitiveVector.elementAt(i)).getSelected()) {

            	// The rotation point is given by the first primitive
            	if(firstPrimitive){ 
            		ix=((GraphicPrimitive)primitiveVector.elementAt(i
               			)).getFirstPoint().x;
               	}
               	
               	firstPrimitive=false;



               ((GraphicPrimitive)primitiveVector.elementAt(i
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
            gp=((GraphicPrimitive) primitiveVector.elementAt(i));
            
            layer= ((GraphicPrimitive)
                       primitiveVector.elementAt(i)).getLayer();
                       
            if(((LayerDesc)layerV.get(layer)).getVisible()) {
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
        
        for (i=0; i<primitiveVector.size(); ++i){
            gp=(GraphicPrimitive)primitiveVector.elementAt(i);
      		layer= gp.getLayer();
                       
            if(!((LayerDesc)layerV.get(layer)).getVisible())
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
            
            primBeingDragged=(GraphicPrimitive)primitiveVector.elementAt(isel);
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
        		//System.out.println("pippo");

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
            	
    	
            	int xa = cs.mapX(oldpx, oldpy);
            	int ya = cs.mapY(oldpx, oldpy);
        		int xb = opx;
        		int yb = opy;
        
        		if(!firstDrag) {
        			if(!Globals.doNotUseXOR) 
        			    // In the XOR mode, here we delete the previous draw
        				g.drawRect(Math.min(xa,xb), Math.min(ya,yb), 
        					   Math.abs(xb-xa), Math.abs(yb-ya));
        			else{
        				xb=px;
						yb=py;
						opx=px;
						opy=py;
        				P.setEvidenceRect(Math.min(xa,xb), Math.min(ya,yb), 
        					   Math.abs(xb-xa), Math.abs(yb-ya));
        				P.repaint();
        				return;
        				        				
        			}
        		}

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
            primBeingDragged=(GraphicPrimitive)primitiveVector.elementAt(i);
            if(primBeingDragged.getSelected()) {
            
            	// This code is needed to ensure that all layer are printed
            	// when dragging a component (it solves bug #24)
    			if (primBeingDragged instanceof PrimitiveMacro) {
        			((PrimitiveMacro)primBeingDragged).setDrawOnlyLayer(-1);
				}
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
        
        for (i=0; i<primitiveVector.size(); ++i){
            distance=((GraphicPrimitive)
                       primitiveVector.elementAt(i)).getDistanceToPoint(px,py);
                        
            if (distance<=mindistance) {
                isel=i;
                mindistance=distance;
                
            }
        }
        
        return mindistance;
        
        
    }
    
    
    
    /** Parse the circuit contained in the StringBuffer specified.
    	This function resets the primitive database and then parses the circuit.
    	
    	@param s the string containing the circuit
    	
    
    */
    public void parseString(StringBuffer s) 
    	throws IOException
    {
        primitiveVector=new Vector();
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
        boolean isMacro=false; // the last primitive was a macro
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
        char c;
        int len;

        
        lineNum=1;
        j=0;    // A fairy simple tokenizer
        token.setLength(0);
        len=s.length();
        for(i=0; i<len;++i){
            c=s.charAt(i);
            if(c=='\n' || c=='\r'|| i==len-1) { //The string finished
                if(i==len-1 && c!='\n'){
                    token.append(c);
                }
                ++lineNum;
                tokens[j]=token.toString();
                if (token.length()==0)  // Avoids trailing spaces
                    j--;
                try{
                	if(isMacro && !tokens[0].equals("FCJ")) {
                		g=new PrimitiveMacro(library,layerV);
                        g.parseTokens(old_tokens, old_j+1);
                        g.setSelected(selectNew);
                        primitiveVector.add(g);
                	}
                
                    if(tokens[0].equals("FCJ")) {	// FidoCadJ extension!
                    	if(isMacro)
                    		macro_counter=2;
                    	isMacro=false;
                    }if(tokens[0].equals("LI")) {
                    	isMacro=false;
                    	macro_counter=0;
                        g=new PrimitiveLine();
                        g.parseTokens(tokens, j+1);
                        g.setSelected(selectNew);
                        primitiveVector.add(g);
                    } else if(tokens[0].equals("BE")) {
                        isMacro=false;
                    	macro_counter=0;
                        g=new PrimitiveBezier();
                        g.parseTokens(tokens, j+1);
                        g.setSelected(selectNew);
                        primitiveVector.add(g);
                    } else if(tokens[0].equals("MC")) {
                        // Save the tokenized line.
                        // We cannot create the macro until we parse the 
                        // following line (which can be FCJ)
                    	macro_counter=0;
                        for(int l=0; l<j+1; ++l)
                        	old_tokens[l]=tokens[l];
                        old_j=j;
                        isMacro=true;
        

                    } else if(tokens[0].equals("TE")) {
                    	isMacro=false;
                    	macro_counter=0;
                        g=new PrimitiveAdvText();
                        g.parseTokens(tokens, j+1);
                        g.setSelected(selectNew);
                        primitiveVector.add(g);
                    } else if(tokens[0].equals("TY")) {
                        isMacro=false;
                        
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
                        	primitiveVector.add(g);
                        	macro_counter=0;
                        } else {
                        	g=new PrimitiveAdvText();
                        	g.parseTokens(tokens, j+1);
                        	g.setSelected(selectNew);
                       	 	primitiveVector.add(g);
                       	 }
                    } else if(tokens[0].equals("PL")) {
                    	isMacro=false;
                    	macro_counter=0;
                        g=new PrimitivePCBLine();
                        g.parseTokens(tokens, j+1);
                        g.setSelected(selectNew);
                        primitiveVector.add(g);
                    } else if(tokens[0].equals("PA")) {
                    	isMacro=false;
                    	macro_counter=0;
                        g=new PrimitivePCBPad();
                        g.parseTokens(tokens, j+1);
                        g.setSelected(selectNew);
                        primitiveVector.add(g);
                    } else if(tokens[0].equals("SA")) {
                        isMacro=false;
                    	macro_counter=0;
                        g=new PrimitiveConnection();
                        g.parseTokens(tokens, j+1);
                        g.setSelected(selectNew);
                        primitiveVector.add(g);
                    }  else if(tokens[0].equals("EV")||tokens[0].equals("EP")) {
                        isMacro=false;
                       	macro_counter=0;
                        g=new PrimitiveOval();                        g.parseTokens(tokens, j+1);
                        g.setSelected(selectNew);
                        primitiveVector.add(g);
                    } else if(tokens[0].equals("RV")||tokens[0].equals("RP")) {
                        isMacro=false;
                    	macro_counter=0;
                        g=new PrimitiveRectangle();
                        g.parseTokens(tokens, j+1);
                        g.setSelected(selectNew);
                        primitiveVector.add(g);
                    } else if(tokens[0].equals("PV")||tokens[0].equals("PP")) {
                        isMacro=false;
                    	macro_counter=0;
                        g=new PrimitivePolygon();
                        g.parseTokens(tokens, j+1);
                        g.setSelected(selectNew);
                        primitiveVector.add(g);
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
        if(isMacro) {
          	g=new PrimitiveMacro(library,layerV);
            g.parseTokens(old_tokens, old_j+1);
            g.setSelected(selectNew);
            primitiveVector.add(g);
        }
    
    }
    
    /**	Export the file using the given interface
    
    @param exp the selected exporting interface
    @param header specify if an header and a tail should be written or not
    */
    void exportDrawing(ExportInterface exp, boolean header)
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
        		g=(GraphicPrimitive)primitiveVector.elementAt(i);
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
        		
        			g=(GraphicPrimitive)primitiveVector.elementAt(i);
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
            if ((g=(GraphicPrimitive)primitiveVector.elementAt(i)) instanceof 
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
    		StringBuffer s=new StringBuffer((String)um.undoPop());
    		parseString(s);
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
			StringBuffer s=new StringBuffer((String)um.undoRedo());
    		parseString(s);
    		if(cl!=null) cl.somethingHasChanged();
    		
    	} catch (Exception E) 
    	{
    	}
    	
    }
    
    
    
    /** Save the undo state
    
    */
    public void saveUndoState()
    {
       	um.undoPush(getText(true).toString());
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
    
    public void setHasChangedListener (HasChangedListener l)
    {
		cl = l;
	}
	
	public void setMacroOriginVisible(boolean s)
	{
		isMacroOriginVisible = s;
	}
	
	/** Returns true if there is no drawing in memory
	*/
	public boolean isEmpty()
	{
		return primitiveVector.size()==0;
	}
}

