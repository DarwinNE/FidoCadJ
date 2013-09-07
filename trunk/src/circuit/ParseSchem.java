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
import timer.*;


/**

<pre>
   A FIDOCAD schematics draw class.
   
   TODO: this file is way too big. This big class might be subdivided in 
        several smaller classes.
   
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
2.1     June 2008       D. Bucci    Drag & drop 
2.2     August 2008     D. Bucci    
2.3     November 2008   D. Bucci    library becomes a TreeMap
2.4     December 2008   D. Bucci    FCJ extensions
2.5     June 2009       D. Bucci    Capitalize the first letters                                     
2.6     November 2009   D. Bucci    New FCJ extensions
                                    Macro font selection
2.7     February 2010   D. Bucci    General optimization
2.8     March 2010      D. Bucci    Optimization and improvements
2.9     May 2010        D. Bucci    Optimized, code cleaned

  ... then we used Subversion to track changes!

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

    Copyright 2007-2013 by Davide Bucci
</pre>

   Main parsing class 
    
    @author Davide Bucci
*/

public class ParseSchem implements UndoActorListener
{   
    // ********** CONFIGURATION **********
    
    // This is the maximum number of tokens which will be considered in a line
    static final int MAX_TOKENS=512;
  
    // True if FidoCadJ should use Windows style line feeds (appending \r
    // to the text generated).
    static final boolean useWindowsLineFeed=false;
    
    // Name of the last file opened
    public String openFileName;
    
    
    // ************* DRAWING *************
    
    // True if the drawing characteristics have been modified. This implies
    // that during the first redraw a in-depth calculation of all coordinates
    // will be done. For performance reasons, this is indeed done only when
    // necessary.
    private boolean changed;

    // True if only pads should be drawn.
    private boolean drawOnlyPads;
    
    // Positive if during the redraw step only a particular layer should be
    // drawn
    private int drawOnlyLayer;

    // Font and size to be used for the text associated to the macros.
    private String macroFont;
    private int macroFontSize;
    
    // Array used to determine which layer is used in the drawing.
    private boolean[] layersUsed;   

    // Higher priority layer used in the drawing.
    private int maxLayer;
   
    // True if the drawing needs holes. This implies that the redrawing
    // step must include a cycle at the end to draw all holes.
    private boolean needHoles;

    // True if during redraw, the macro origin (100,100) should be represented.    
    private boolean hasFCJOriginVisible;

   
    // ******** DRAG & INTERFACE *********
    
    // True if we are at the beginning of a dragging operation.
    private boolean firstDrag;
    
    // The graphic primitive being treated.
    private GraphicPrimitive primBeingDragged;
    // The handle of the active graphic primitive being treated.
    private int handleBeingDragged;
    // True if elements should be shifted when copy/pasted
    private boolean shiftCP;
   
    // Old cursor position for handle drag.
    private int opx;
    private int opy;
    
    // Other old cursor position for handle drag...
    private int oldpx;
    private int oldpy;
    
    // True if the primitive has moved.
    private boolean hasMoved;
        
    // ******* PRIMITIVE DATABASE ********
    
    // Actual line number. This is useful to indicate errors.
    private int lineNum;
    
    // Vector containing all primitives in the drawing.
    private Vector<GraphicPrimitive> primitiveVector;
    // Vector containing all layers used in the drawing.
    Vector<LayerDesc> layerV;

    // Library of macros loaded.
    private Map<String, MacroDesc> library;
  
    // ************ UNDO *************
    
    // Undo manager
    private UndoManager um;
    
    // Database of the temporary directories
    private Vector<String> tempDir;
    
    // Maximum number of levels to be retained for undo operations.
    private static final int MAX_UNDO=100;
            
    // A drawing modification flag. If true, there are unsaved changes.
    // This is different from the "change" flag defined above, since while the
    // "change=true" implies that the drawing should be redrawn recalculating
    // all the coordinate scaling and so on, here "isModified=true" implies that
    // a permanent change to the drawing has been made and the user might be
    // prompted in the future if there is the risk of losing unsaved changes.
    private boolean isModified;
    
    // ********** LISTENERS **********
    
    private HasChangedListener cl;
    private LibraryUndoListener libraryUndoListener;
     

    /** The standard constructor. Not so much interesting, apart for the
        fact that it allocates memory of a few internal objects and reset all
        state flags.
    
    */
    public ParseSchem()
    {
        //tokens=new String[MAX_TOKENS];
        setPrimitiveVector(new Vector<GraphicPrimitive>(25));
        layerV=new Vector<LayerDesc>(Globals.MAX_LAYERS);
        library=new TreeMap<String, MacroDesc>();
        firstDrag=false;
        um=new UndoManager(MAX_UNDO);
        oldZoom=-1;
        drawOnlyPads=false;
        drawOnlyLayer=-1;
        cl=null;
        macroFont = "Courier New";
        layersUsed = new boolean[Globals.MAX_LAYERS];
        handleBeingDragged=GraphicPrimitive.NO_DRAG;
        changed=true;
        isModified=false;
        libraryUndoListener=null;
        tempDir=new Vector<String>();
        
    }
    
    /** Returns true if the elements are shifted when copy/pasted
    
    */
    public boolean getShiftCopyPaste()
    {
    	return shiftCP;
    }
    
    /** Determines if the elements are shifted when copy/pasted
    
    */
    public void setShiftCopyPaste(boolean s)
    {
    	shiftCP=s;
    }  
    
    /** Get the layer description vector
    
        @return a vector of LayerDesc describing layers.
    
    */

    public Vector<LayerDesc> getLayers()
    {
        return layerV;
    }
    
    /** Set the layer description vector
    
        @param v a vector of LayerDesc describing layers.
    
    */
    public void setLayers(Vector<LayerDesc> v)
    {
        layerV=v;
        changed=true;
    }
    
    /** Get the current library
        @return a map String/String describing the current library.
    
    */
    
    public Map<String, MacroDesc> getLibrary()
    {
        return library;
    }
    
    /** Specify the current library.
        @param l the new library (a String/String hash table)
    */
    public void setLibrary(Map<String, MacroDesc> l)
    {
        library=l;
        changed=true;
    }
    
    /** Resets the current library.
    */
    public void resetLibrary()
    {
        setLibrary(new TreeMap<String, MacroDesc>());
    }
    
    /** Try to load all libraries ("*.fcl") files in the given directory.
    
        FCDstdlib.fcl if exists will be considered as standard library.
        
        @param s the directory in which the libraries should be present.
    */
    public void loadLibraryDirectory(String s)
    {
        String[] files;  // The names of the files in the directory.
        File dir = new File(s);
        
        // Obtain the list of files in the specified directory.
        files = dir.list(new FilenameFilter()
        { 
            public boolean accept(File dir, String name)
            {
                // This filter allows to obtain all files with the fcd 
                // file extension
                return name.toLowerCase().endsWith(".fcl");
            }
        });
        
        // We first check if the directory is existing or is not empty.        
        if((!dir.exists()) || files==null) {
            if (!s.equals("")){
                System.out.println("Warning! Library directory is incorrect:");
                System.out.println(s);
            }
            System.out.println("Activated FidoCadJ internal libraries and symbols.");
            return;
        }
        // We read all the directory content, file by file 
        for (int i = 0; i < files.length; i++) {
            File f;  // One of the files in the directory.
            f = new File(dir, files[i]);
            try {
                // Here we have a hopefully valid file in f, so we may read its
                // contents
                readLibraryFile(f.getPath());
            } catch (IOException E) {
                System.out.println("Problems reading library "+f.getName()+" "+E);
            }
        } 
           
    }
    
    /** Read all librairies contained in the given URL at the given prefix.
        This is particularly useful to read librairies shipped in a jar 
        file.
    */
    public void loadLibraryInJar(URL s, String prefix)
    {
        try {
            readLibraryBufferedReader(new BufferedReader(new 
                InputStreamReader(s.openStream(), Globals.encoding)), prefix);
        } catch (IOException E) {
            System.out.println("Problems reading library: "+s.toString());
        }
           
    }
    
    /** Read the library contained in a file
        @param openFileName the name of the file to be loaded
    */
    public void readLibraryFile(String openFileName)
        throws IOException
    {
        InputStreamReader input = new InputStreamReader(new 
            FileInputStream(openFileName), Globals.encoding);
        
        BufferedReader bufRead = new BufferedReader(input);
        String prefix="";

        prefix = Globals.getFileNameOnly(openFileName);
        if (prefix.equals("FCDstdlib"))
            prefix="";
            
        readLibraryBufferedReader(bufRead, prefix);

        bufRead.close();
    }

    /** Read a library provided by a buffered reader. Adds all the macro keys
        in memory, with the given prefix.
        @param bufRead The buffered reader prepared with the stream containing
            the library we want to read.
        @param prefix The prefix which should be added to the macro key when 
            using a non standard macro.
    */
    public void readLibraryBufferedReader(BufferedReader bufRead, String prefix)
        throws IOException
    {
        String macroName="";
        String longName="";
        String categoryName="";
        String libraryName="";
        int i;
//        StringBuffer txt= new StringBuffer();    
        String line="";
        
        while(true) {
            // Read and process line by line.
            line = bufRead.readLine();
            
            if(line==null)
                break; 
            
            // Avoid trailing spaces
            line=line.trim();       
            
            // Avoid processing shorter lines
            if (line.length()<=1)    
                continue;
                
            // A category
            if(line.charAt(0)=='{') { 
                categoryName="";
                StringBuffer temp=new StringBuffer(25);
                for(i=1; i<line.length()&&line.charAt(i)!='}'; ++i){
                    temp.append(line.charAt(i));
                }
                categoryName=temp.toString().trim();
                if(i==line.length()) {
                	IOException e=new IOException("Category non terminated with }.");
                    throw e;
                }
                continue;
            }
                
            // A macro
            if(line.charAt(0)=='[') { 
                macroName="";
                    
                longName="";
                StringBuffer temp=new StringBuffer(25);
                for(i=1; line.charAt(i)!=' ' &&
                         line.charAt(i)!=']' &&
                         i<line.length(); ++i){
                    temp.append(line.charAt(i));
                }
                macroName=temp.toString().trim();
                int j;
                temp=new StringBuffer(25);
                for(j=i; j<line.length()&&line.charAt(j)!=']'; ++j){
                    temp.append(line.charAt(j));
                }
                longName=temp.toString();
                if(j==line.length()) {
                	IOException e=new IOException("Macro name non terminated with ].");
                    throw e;
                }
                
                if (macroName.equals("FIDOLIB")) {
                    libraryName = longName.trim();
                    continue;
                } else {
                    if(!prefix.equals(""))
                        macroName=prefix+"."+macroName;
                    
                    macroName=macroName.toLowerCase();
                    library.put(macroName, new 
                        MacroDesc(macroName,"","","","", prefix));
                    continue;
                }
            }
            
           
            if(!macroName.equals("")){
                // Add the macro name.
                // NOTE: in FidoCAD, the macro prefix is somewhat case 
                // insensitive, since it indicates a file name and in 
                // Windows all file names are case insensitive. Under
                // other operating systems, we need to be waaay much
                // careful, hence we convert the macro name to lower case.
                
                macroName=macroName.toLowerCase();
                library.put(macroName, new MacroDesc(macroName,longName,
                ((MacroDesc)library.get(macroName)).description+"\n"+line,
                categoryName, libraryName, prefix));
                
                // Is it OK to use prefix as the macro filename? Yes!
            }                
        } 
    }
       
    /** Add a graphic primitive.
        @param p the primitive to be added.
        @param save save the undo state.
        @param sort if true, sort the primitive layers
    
    */
    public void addPrimitive(GraphicPrimitive p, boolean save, boolean sort)
    {   
        // The primitive database MUST be ordered. The idea is that we insert
        // primitives without ordering them and then we call a sorter.
                
        getPrimitiveVector().add(p);
        if (save) saveUndoState();

        // We check if the primitives should be sorted depending of their layer
        // If there are more than a few primitives to insert, it is wise to
        // sort only once, at the end of the insertion process.
        if (sort)
            sortPrimitiveLayers();
        
        // We now have to track that something has changed. This forces all the
        // caching system used by the drawing routines to be refreshed.
        changed=true;
        isModified=true;
    }
    
    /** Returns true if the specified layer is contained in the schematic
        being drawn. The analysis is done when the schematics is created, so
        the results of this method are ready before the redraw step.
        
        @return true if the specified layer is contained in the drawing.
    */
    public boolean containsLayer(int l)
    {
        return layersUsed[l];
    }    
    
    /** Get the FidoCad text file.
    
        @param extensions specify if FCJ extensions should be used
        @return the sketch in the text Fidocad format
    */
    public StringBuffer getText(boolean extensions)
    {
        int i;
        StringBuffer s=registerConfiguration(extensions);   
        for (i=0; i<getPrimitiveVector().size(); ++i){
            s.append(
                ((GraphicPrimitive)getPrimitiveVector().get(i)).toString(
                    extensions));
            if(useWindowsLineFeed) 
                s.append("\r");
        }        
        return s;
    }
    
    /** If it is needed, provides all the configurations settings at
        the beginning of the FidoCadJ file.
        @param extensions it is true when FidoCadJ should export using 
            its extensions.
    
    */
    public StringBuffer registerConfiguration(boolean extensions)
    {
    	// This is something which is not contemplated by the original
    	// FidoCAD for Windows. If extensions are not activated, just exit.
        if(!extensions) {
        	return new StringBuffer();
		}
		
        StringBuffer s = new StringBuffer();
        // Here is the beginning of the output. We can eventually provide
        // some hints about the configuration of the software (if needed).
        
        // We start by checking if the diameter of the electrical connection
        // should be written.
        
        // We consider that a difference of 1e-5 is small enough 

        if(Math.abs(Globals.diameterConnectionDefault-
            Globals.diameterConnection)>1e-5) {           
            s.append("FJC C "+Globals.diameterConnection+"\n");
        }
        
        
        // Check if the layers should be indicated    
        Vector<LayerDesc> standardLayers = Globals.createStandardLayers();
        for(int i=0; i<layerV.size();++i) {
            LayerDesc l = (LayerDesc)layerV.get(i);
            String defaultName=
        	   	((LayerDesc)standardLayers.get(i)).getDescription();
            if (l.getModified()) {
                int rgb=l.getColor().getRGB();
                float alpha=l.getAlpha();
                s.append("FJC L "+i+" "+rgb+" "+alpha+"\n");
                // We compare the layers to the standard configuration.
              	// If the name has been modified, the layer configuration 
               	// is saved.
                if (!l.getDescription().equals(defaultName)) {
                  	s.append("FJC N "+i+" "+l.getDescription()+"\n");
                }
            }
        }
        
        // Check if the line widths should be indicated
        if(Math.abs(Globals.lineWidth -
           	Globals.lineWidthDefault)>1e-5) {         
           	s.append("FJC A "+Globals.lineWidth+"\n");
        }
        if(Math.abs(Globals.lineWidthCircles -
           	Globals.lineWidthCirclesDefault)>1e-5) {          
           	s.append("FJC B "+Globals.lineWidthCircles+"\n");
       	}
       
        return s;
    }
    
    /** Get the maximum layer which contains something. This value is updated
        after a redraw. This is tracked for efficiency reasons.
        
        @return the maximum layer number.
    */
    final public int getMaxLayer()
    {
        return maxLayer;
    }
    
    // Here are some counters and local variables. We made them class members
    // to ensure that their place is reserved in memory and we do not need
    // some time expensive allocations, since speed is important in the draw
    // operation.
    
    private double oZ, oX, oY, oO;
    private GraphicPrimitive gg;
    private int i_index;
    private int j_index;

    /** Draw the current drawing.
        This code is rather critical. Do not touch it unless you know very
        precisely what to do.
        
        @param G the graphic context in which the drawing should be drawn.
        
    */
    public synchronized void draw(Graphics2D G, MapCoordinates cs)
    {   
		if(cs==null) {
			System.out.println(
				"ParseSchem.draw: ouch... cs not initialized :-(");
			return;
		}
        // At first, we check if the current view has changed. 
        if(changed 	|| oZ!=cs.getXMagnitude() || oX!=cs.getXCenter() || 
        	oY!=cs.getYCenter() || oO!=cs.getOrientation()) {
            oZ=cs.getXMagnitude();
            oX=cs.getXCenter();
            oY=cs.getYCenter();
            oO=cs.getOrientation();
            changed = false;
            
            // Here we force for a global refresh of graphic data at the 
            // primitive level. 
            
            for (i_index=0; i_index<getPrimitiveVector().size(); ++i_index){
                ((GraphicPrimitive)getPrimitiveVector().get(i_index)).
                	setChanged(true);
            }
            
            if (!drawOnlyPads) 
                cs.resetMinMax();
        }
        
        needHoles=drawOnlyPads;
        
        // If it is needed, at first, show the macro origin (100, 100) in
        // logical coordinates.

        if (hasFCJOriginVisible) {
            G.setColor(Color.red);
            G.fillOval(cs.mapXi(100, 100, false)-4,
                cs.mapYi(100, 100, false)-4, 8, 8);
        }
       
        /* First possibility: we need to draw only one layer (for example 
           in a macro). This is indicated by the fact that drawOnlyLayer
           is non negative.
        */
        if(drawOnlyLayer>=0 && !drawOnlyPads){
            
            // At first, we check if the layer is effectively used in the
            // drawing. If not, we exit directly.
            
            if(!layersUsed[drawOnlyLayer])
                return;
            
            drawPrimitives(drawOnlyLayer, G, cs);
            
            return;
        } else if (!drawOnlyPads) {
            // If we want to draw all layers, we need to process with order.
            for(j_index=0;j_index<Globals.MAX_LAYERS; ++j_index) {
                if(!layersUsed[j_index])
                    continue;
                drawPrimitives(j_index, G,cs);             
            }
        }
        // Draw in a second time only the PCB pads, in order to ensure that the
        // drills are always open.
        if(needHoles) {
            for (i_index=0; i_index<getPrimitiveVector().size(); ++i_index){
                
                // We will process only primitive which require holes (pads
                // as well as macros containing pads).
                
                if ((gg=(GraphicPrimitive)getPrimitiveVector().get(i_index)).
                    needsHoles()) {
                    gg.setDrawOnlyPads(true);
                    gg.draw(G, cs, layerV);
                    gg.setDrawOnlyPads(false);
                } 
            }
        }
    }
    
    /** Draws all the primitives and macros contained in the specified layer.
        This function is used mainly by the draw member.
        @param j_index the layer to be considered
        @param G the graphic context in which to draw    
    */
    private void drawPrimitives(int j_index, Graphics2D G, MapCoordinates cs)
    {
        GraphicPrimitive gg;
        int i_index;
        
        // Here we process all the primitives, one by one!
        for (i_index=0; i_index<getPrimitiveVector().size(); ++i_index){
            gg=(GraphicPrimitive)getPrimitiveVector().get(i_index);
                
            // Layers are ordered. This improves the redrawing speed. 
            if (j_index>0 && gg.layer>j_index) {
                break;
            } 
                    
            // Process a particular primitive if it is in the layer
            // being processed.
                    
            if(gg.containsLayer(j_index)) {
                gg.setDrawOnlyLayer(j_index);
                gg.draw(G, cs, layerV);    
            }
                    
            if(gg.needsHoles())
                needHoles=true;
        }
    }
    
    /** Specify that the drawing process should only draw holes of the pcb
        pad
        
        @param pd it is true if only holes should be drawn   
    */
    
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
        
    // Here are some other local variables made global for avoiding memory
    // allocations. 
    private BufferedImage bufferedImage; // Useful for grid calculation
    private double oldZoom;
    private TexturePaint tp;
    private int width;
    private int height;
    
    /** Draw the grid in the given graphic context.
        @param G the graphic context to be used
        @param xmin the x (screen) coordinate of the upper left corner
        @param ymin the y (screen) coordinate of the upper left corner
        @param xmax the x (screen) coordinate of the bottom right corner
        @param ymax the y (screen) coordinate of the bottom right corner  
    */
    public void drawGrid(Graphics2D G, MapCoordinates cs, int xmin, int ymin, int xmax, int ymax) 
    {
        // Drawing the grid seems easy, but it appears that setting a pixel
        // takes a lot of time. Basically, we create a textured brush and we
        // use it to paint the entire specified region.

        int dx=cs.getXGridStep();
        int dy=cs.getYGridStep();
        int mul=1;
        double toll=0.01;
        double z=cs.getYMagnitude();
        
        double x;
        double y;
        
        double m=1.0;   

        // Fabricate a new image only if necessary, to save time.   
        if(oldZoom!=z || bufferedImage == null || tp==null) {
            // It turns out that drawing the grid in an efficient way is not a 
            // trivial problem. What it is done here is that the program tries
            // to calculate the minimum common integer multiple of the dot 
            // espacement to calculate the size of an image in order to be an 
            // integer.
            // The pattern filling (which is fast) is then used to replicate the
            // image (very fast!) over the working surface.
            
            for (double l=1; l<105; ++l) {
                if (Math.abs(l*z-Math.round(l*z))<toll) {
                    mul=(int)l;
                    break;
                }
            }
            tp = null;
            double ddx=Math.abs(cs.mapXi(dx,0,false)-cs.mapXi(0,0,false));
            double ddy=Math.abs(cs.mapYi(0,dy,false)-cs.mapYi(0,0,false));
            int d=1;
        
            // This code applies a correction: draws bigger points if the pitch
            // is very big, or draw much less points if it is too dense.
            if (ddx>50 || ddy>50) {
                d=2;
            } else if (ddx<3 || ddy <3) {
                dx=5*cs.getXGridStep();
                dy=5*cs.getYGridStep();
                ddx=Math.abs(cs.mapXi(dx,0,false)-cs.mapXi(0,0,false));
//                ddy=Math.abs(cs.mapYi(0,dy,false)-cs.mapYi(0,0,false)); 
            }
                
            width=Math.abs(cs.mapX(mul*dx,0)-cs.mapX(0,0));
            if (width<=0) width=1;
                
            height=Math.abs(cs.mapY(0,0)-cs.mapY(0,mul*dy));
            if (height<=0) height=1;
        
            /* Nowadays computers have generally a lot of memory, but this is 
               not a good reason to waste it. If it turns out that the image
               size is utterly impratical, use the standard dot by dot grid 
               construction.
               This should happen rarely, only for particular zoom sizes.
            */
            if (width>1000 || height>1000) {
                G.setColor(Color.white);
                G.fillRect(xmin,ymin,xmax,ymax);
                G.setColor(Color.gray);
                for (x=cs.unmapXsnap(xmin); x<=cs.unmapXsnap(xmax); x+=dx) {
                    for (y=cs.unmapYsnap(ymin); y<=cs.unmapYsnap(ymax); y+=dy) {
                        G.fillRect(cs.mapXi((int)x,(int)y, 
                            false),cs.mapYi((int)x,
                            (int)y, false),d,d);
                    }
                }
                return;
            }
        
            try {
                // Create a buffered image in which to draw
                //bufferedImage = new BufferedImage(width, height,
                //System.out.println(""+width+ "   "+height);
                bufferedImage = new BufferedImage(width, height,
                                          BufferedImage.TYPE_INT_BGR);
                                          
            } catch (java.lang.OutOfMemoryError E) {
                System.out.println("Out of memory error when painting grid");
                return;
            }
        
            // Create a graphics contents on the buffered image
            Graphics2D g2d = bufferedImage.createGraphics();
            g2d.setColor(Color.white);
            g2d.fillRect(0,0,width,height);
            g2d.setColor(Color.gray);
            
            // Prepare the image with the grid.
            for (x=0; x<=cs.unmapXsnap(width); x+=dx) {
                for (y=0; y<=cs.unmapYsnap(height); y+=dy) {
                    g2d.fillRect(cs.mapX((int)x,(int)y),cs.mapY((int)x,
                        (int)y),d,d);
                }
            }
            oldZoom=z;
            Rectangle anchor = new Rectangle(width, height);

            tp = new TexturePaint(bufferedImage, anchor);
        }
        
        // Textured paint :-)
        G.setPaint(tp);
        G.fillRect(0, 0, xmax, ymax);
    }
    
    /** Draw the handles of all selected primitives
        @param G the graphic context to be used.
    */
    public void drawSelectedHandles(Graphics2D G, MapCoordinates cs)
    {
        int i;
        for (i=0; i<getPrimitiveVector().size(); ++i){
            if(((GraphicPrimitive)getPrimitiveVector().get(i)).getSelected()) 
                ((GraphicPrimitive)getPrimitiveVector().get(i)).drawHandles(G,
                   cs);
        }      
    }
    
    
    /** Deselect all primitives.        
    */
    public void deselectAll()
    {
        int i;
        for (i=0; i<getPrimitiveVector().size(); ++i){
            ((GraphicPrimitive)getPrimitiveVector().get(i)).setSelected(false);
        }      
    }
    
    /** Select all primitives.     
    */
    public void selectAll()
    {
        int i;
        for (i=0; i<getPrimitiveVector().size(); ++i){
            ((GraphicPrimitive)getPrimitiveVector().get(i)).setSelected(true);
        }   
    }
    
    /** Delete all selected primitives. 
    */
    public void deleteAllSelected()
    {
        int i;

        for (i=0; i<getPrimitiveVector().size(); ++i){
            if(((GraphicPrimitive)getPrimitiveVector().get(i)).getSelected())
                getPrimitiveVector().remove((GraphicPrimitive)
                                        getPrimitiveVector().get(i--));
        }
        saveUndoState();   
    }
    
        
    /** Set the font of all macros.
        @param f the font name
    */
    public void setTextFont(String f, int size)
    {
        int i;
        macroFont=f;
        macroFontSize = size;

        for (i=0; i<getPrimitiveVector().size(); ++i){
            ((GraphicPrimitive)getPrimitiveVector().get(i)).setMacroFont(f, size);
        }
        changed=true;
        isModified=true;
    }
    
    
    /** Get the font of all macros.
        @return the font name
    */
    public String getTextFont()
    {   
    /*
        for (int i=0; i<primitiveVector.size(); ++i){
            return ((GraphicPrimitive)primitiveVector.get(i)).getMacroFont();
        }
        */
        return macroFont;
    }
    
    /** Get the size of the font used for all macros.
        @return the font name
    */
    public int getTextFontSize()
    {        
        for (int i=0; i<getPrimitiveVector().size(); ++i){
            return ((GraphicPrimitive)getPrimitiveVector().get(i))
                    .getMacroFontSize();            
        }
        return macroFontSize;
    }
    
    /** Copy in the system clipboard all selected primitives.
        @param extensions specify if FCJ extensions should be applied
        @param splitNonStandard specify if non standard macros should be split
    */
    public void copySelected(boolean extensions, boolean splitNonStandard,
    	int xstep, int ystep)
    {
        StringBuffer s = getSelectedString(extensions);
        
        /*  If we have to split non standard macros, we need to work on a 
            temporary file, since the splitting works on the basis of the 
            export technique.       
            The temporary file will then be loaded in the clipboard.
        */
        if (splitNonStandard) {
			s=splitMacros(s,  false);
        }
        
        // get the system clipboard
        Clipboard systemClipboard =Toolkit.getDefaultToolkit()
            .getSystemClipboard();
        
        Transferable transferableText = new StringSelection(s.toString());
        systemClipboard.setContents(transferableText,null);
    }
    
    /** Obtain a string containing all the selected elements.
    	@return the string
    */
    public StringBuffer getSelectedString(boolean extensions)
    {
    	StringBuffer s=new StringBuffer("[FIDOCAD]\n");
        
        s.append(registerConfiguration(extensions));
        //moveAllSelected(xstep, ystep);

        for (int i=0; i<getPrimitiveVector().size(); ++i){
            if(((GraphicPrimitive)getPrimitiveVector().get(i)).getSelected())
                s.append(((GraphicPrimitive)getPrimitiveVector().get(i
                    )).toString(extensions));
        }
        
        return s;
    }
    
    /** Renders a split version of the macros contained in the given string.
    	@param s a string containing macros to be splitted.
    	@param splitStandardMacros if it is true, even the standard macros will be splitted.
    	@return the splitted macros
    */
    public StringBuffer splitMacros(StringBuffer s, 
    	boolean splitStandardMacros)
    {
        StringBuffer txt= new StringBuffer("");    
		
        ParseSchem Q=new ParseSchem();
        Q.setLibrary(library);  // Inherit the library
        Q.setLayers(layerV);    // Inherit the layers
            
        // from the obtained string, obtain the new Q object which will
        // be exported and then loaded into the clipboard.
            
        try {
            Q.parseString(s); 
            File temp= File.createTempFile("copy", ".fcd");
            temp.deleteOnExit();
            String frm="";
            if(splitStandardMacros) 
            	frm = "fcda";
            else
            	frm = "fcd";
            	
            ExportGraphic.export(temp,  Q, frm, 1,true,false, 
                true,false);
                
            FileInputStream input = new FileInputStream(temp);
            BufferedReader bufRead = new BufferedReader(
                new InputStreamReader(input, Globals.encoding));
                
            String line="";
                        
            txt = new StringBuffer(bufRead.readLine());
                        
            txt.append("\n");
                        
            while (line != null){
                line =bufRead.readLine();
                if (line==null)
                 	break;
                txt.append(line);
                txt.append("\n");
            }
            
            bufRead.close();
                
        } catch(IOException e) {
            System.out.println("Error: "+e); 
        }
        
        return txt;
    }
    
    /** Paste from the system clipboard
        
    */
    public void paste(int xstep, int ystep)
    {
        TextTransfer textTransfer = new TextTransfer();
        
        deselectAll();
        
        try {
            addString(new 
                StringBuffer(textTransfer.getClipboardContents()),true);
        } catch (Exception E) {}
        
        if(shiftCP)
        	moveAllSelected(xstep, ystep);
        
        saveUndoState();
        setChanged(true);
        
    }
    
    /** Get the first selected primitive
        @return the selected primitive, null if none.
    */
    public GraphicPrimitive getFirstSelectedPrimitive()
    {
        int i;
        GraphicPrimitive gp;
        for (i=0; i<getPrimitiveVector().size(); ++i){
            gp=(GraphicPrimitive)getPrimitiveVector().get(i);
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
        int layer=0;
        GraphicPrimitive gg;
        
        // Check the minimum distance by searching among all
        // primitives
        for (i=0; i<getPrimitiveVector().size(); ++i){
            gg =(GraphicPrimitive)getPrimitiveVector().get(i);           
            distance=gg.getDistanceToPoint(px,py);
            if(distance<=mindistance) {
                layer= gg.getLayer();
                
                if(((LayerDesc)layerV.get(layer)).isVisible)
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
        
        /*  The search method is very simple: we compute the distance of the
            given point from each primitive and we retain the minimum value, if
            it is less than a given tolerance.   
        */
        for (i=0; i<getPrimitiveVector().size(); ++i){
        
            layer= ((GraphicPrimitive)
                       getPrimitiveVector().get(i)).getLayer();
                       
            if(((LayerDesc)layerV.get(layer)).isVisible ||
                (GraphicPrimitive)getPrimitiveVector().get(i) instanceof PrimitiveMacro) {
                distance=((GraphicPrimitive)
                       getPrimitiveVector().get(i)).getDistanceToPoint(px,py);
            	
                if (distance<=mindistance) {
                   isel=i;
                    mindistance=distance;
                }
            }
        }
        
        // Check if we found something!
        if (mindistance<tolerance){
            gp=((GraphicPrimitive)getPrimitiveVector().get(isel));
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

        for (i=0; i<getPrimitiveVector().size(); ++i){
            if(((GraphicPrimitive)getPrimitiveVector().get(i)).getSelected()) {
            
                // The rotation point is given by the first primitive
                if(firstPrimitive){ 
                    ix=((GraphicPrimitive)getPrimitiveVector().get(i
                        )).getFirstPoint().x;
                    iy=((GraphicPrimitive)getPrimitiveVector().get(i
                        )).getFirstPoint().y;
                }
                
                firstPrimitive=false;
            
               ((GraphicPrimitive)getPrimitiveVector().get(i
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

        for (i=0; i<getPrimitiveVector().size(); ++i){
            if(((GraphicPrimitive)getPrimitiveVector().get(i)).getSelected())
               ((GraphicPrimitive)getPrimitiveVector().get(i
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

        for (i=0; i<getPrimitiveVector().size(); ++i){
            if(((GraphicPrimitive)getPrimitiveVector().get(i)).getSelected()) {

                // The rotation point is given by the first primitive
                if(firstPrimitive){ 
                    ix=((GraphicPrimitive)getPrimitiveVector().get(i
                        )).getFirstPoint().x;
                }
                
                firstPrimitive=false;

               ((GraphicPrimitive)getPrimitiveVector().get(i
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
        
        // Avoid processing a trivial case.
        if(w<1 || h <1)
            return false;
        
        // Process every primitive, if the corresponding layer is visible.
        for (i=0; i<getPrimitiveVector().size(); ++i){
            gp=((GraphicPrimitive) getPrimitiveVector().get(i));
            
            layer= ((GraphicPrimitive)
                       getPrimitiveVector().get(i)).getLayer();
                       
            if(layer>=layerV.size() || 
            	((LayerDesc)layerV.get(layer)).isVisible ||
                (GraphicPrimitive)getPrimitiveVector().get(i) instanceof 
                PrimitiveMacro) 
            {
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
    public void dragHandleStart(int px, int py, int tolerance, boolean multiple,
    	MapCoordinates cs)
    {
        int i;
        int isel=0;
        int mindistance=Integer.MAX_VALUE;
        int distance=mindistance;
        int layer;
        
        hasMoved=false;
        
        GraphicPrimitive gp;
        
        oldpx=cs.unmapXnosnap(px);
        oldpy=cs.unmapXnosnap(py);

        firstDrag=true;
        
        int sptol=Math.abs(cs.unmapXnosnap(px+tolerance)-cs.unmapXnosnap(px));
        if (sptol<2) sptol=2; 
        
        // Search for the closest primitive to the given point
        // Performs a cycle through all primitives and check their 
        // distance.
        
        for (i=0; i<getPrimitiveVector().size(); ++i){
            gp=(GraphicPrimitive)getPrimitiveVector().get(i);
            layer= gp.getLayer();
                       
            // Does not allow for selecting an invisible primitive
            if(layer<layerV.size()) {
                if(!((LayerDesc)layerV.get(layer)).isVisible &&
                    !(gp instanceof PrimitiveMacro))
                    continue;
            } 
            if(gp.selectedState){
                // Verify if the pointer is on a handle
                handleBeingDragged=gp.onHandle(cs, px, py);
                
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
            
            primBeingDragged=(GraphicPrimitive)getPrimitiveVector().get(isel);
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
    public void dragHandleEnd(CircuitPanel P, int px, int py, boolean multiple,
    	MapCoordinates cs)
    {
        // Check if we are effectively dragging something...
        P.setEvidenceRect(0,0,-1,-1);
        if(handleBeingDragged<0){
        	if(handleBeingDragged==GraphicPrimitive.RECT_SELECTION){        		
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
    public void dragHandleDrag(CircuitPanel P, Graphics2D g, int px, int py,
    	MapCoordinates cs)
    {
        hasMoved=true;

        // Check if we are effectively dragging and handle...
        if(handleBeingDragged<0){
            if(handleBeingDragged==GraphicPrimitive.DRAG_PRIMITIVE)
                dragPrimitives(P,g, px, py, cs);
                
            // if not, we are performing a rectangular selection
            if(handleBeingDragged==GraphicPrimitive.RECT_SELECTION) {

                int xa = cs.mapXi(oldpx, oldpy, false);
                int ya = cs.mapYi(oldpx, oldpy, false);
                int xb = opx;
                int yb = opy;
                g.setStroke(new BasicStroke(1));

                if(!firstDrag) {
                    int a,b,c,d;
                    boolean flip=false;
                        
                    a = Math.min(xa,xb);
                    b = Math.min(ya,yb);
                    c = Math.abs(xb-xa);
                    d = Math.abs(yb-ya);
                    if(opx>xa && px<xa)
                        flip=true;
                    if(opy>ya && py<ya)
                        flip=true;
    
                    xb=px;
                    yb=py;
                    opx=px;
                    opy=py;
                    
                    P.setEvidenceRect(Math.min(xa,xb), Math.min(ya,yb), 
                           Math.abs(xb-xa), Math.abs(yb-ya));

                    a=Math.min(a, Math.min(xa,xb));
                    b=Math.min(b, Math.min(ya,yb));
                    c=Math.max(c, Math.abs(xb-xa));
                    d=Math.max(d, Math.abs(yb-ya));

                    if (!flip) 
                        P.repaint(a,b,c+10,d+10);
                    else    
                        P.repaint();
                    
                    return;
                }
                xb=px;
                yb=py;
                opx=px;
                opy=py;
                firstDrag=false;
                g.setColor(Color.green);

                g.drawRect(Math.min(xa,xb), Math.min(ya,yb), 
                               Math.abs(xb-xa), Math.abs(yb-ya));
            }
            return;
        }

        if(!firstDrag) {
            P.repaint();
        }
        firstDrag=false;

        // Here we adjust the new positions...
        primBeingDragged.virtualPoint[handleBeingDragged].x=cs.unmapXsnap(px);
        primBeingDragged.virtualPoint[handleBeingDragged].y=cs.unmapYsnap(py);
        primBeingDragged.setChanged(true);
    }
    
    /** Drag all the selected primitives during a drag operation. 
        Position the primitives in the given (screen) position
        
        @param g the graphic context
        @param px the x position (screen coordinates)
        @param py the y position (screen coordinates)
    
    */
    public void dragPrimitives(CircuitPanel P,Graphics2D g, int px, int py,
    	MapCoordinates cs)
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
        for (int i=0; i<getPrimitiveVector().size(); ++i){               
            primBeingDragged=(GraphicPrimitive)getPrimitiveVector().get(i);
            if(primBeingDragged.getSelected()) {
            
                // This code is needed to ensure that all layer are printed
                // when dragging a component (it solves bug #24)
                if (primBeingDragged instanceof PrimitiveMacro) {
                    ((PrimitiveMacro)primBeingDragged).setDrawOnlyLayer(-1);
                }
                primBeingDragged.setChanged(true);

                if(!firstDrag){
                    P.repaint();
                }
                for(int j=0; j<primBeingDragged.getControlPointNumber();++j){
                    
                    primBeingDragged.virtualPoint[j].x+=dx;
                    primBeingDragged.virtualPoint[j].y+=dy;
                    // Here we show the new place of the primitive.
                }
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
        getPrimitiveVector().clear();
        addString(s, false);
        setChanged(true);
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
        GraphicPrimitive g = new PrimitiveLine();
        String[] tokens=new String[MAX_TOKENS];
        String[] old_tokens=new String[MAX_TOKENS];
        String[] name=null;
        String[] value=null;
        double newConnectionSize = -1.0;
        double newLineWidth = -1.0;
        double newLineWidthCircles = -1.0;
        
        int vn=0, vv=0;
        int old_j=0;
        int macro_counter=0;
        int l;
        
        token.ensureCapacity(256);

        /*  This code is not very easy to read. If more extension of the
            original FidoCad format (performed with the FCJ tag) are to be 
            implemented, it can be interesting to rewrite the parser as a
            state machine.
        */
        synchronized(this) {

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
                
                try{
                    if(hasFCJ && !tokens[0].equals("FCJ")) {
                        hasFCJ = registerPrimitivesWithFCJ(hasFCJ, tokens, g, 
                            old_tokens, old_j, selectNew);
                    }
                    
                    if(tokens[0].equals("FCJ")) {   // FidoCadJ extension!
                        if(hasFCJ && old_tokens[0].equals("MC")) {
                            macro_counter=2;
                            g=new PrimitiveMacro(library,layerV);
                            g.parseTokens(old_tokens, old_j+1);
                        } else if (hasFCJ && old_tokens[0].equals("LI")) {
                            g=new PrimitiveLine();
                            
                            for(l=0; l<j+1; ++l)
                                old_tokens[l+old_j+1]=tokens[l];
                            
                            old_j+=j+1;
                            g.parseTokens(old_tokens, old_j+1);
                            g.setSelected(selectNew);

                            if(old_j>5 && old_tokens[old_j].equals("1")) {
                            	macro_counter = 2;
                            } else {
                                addPrimitive(g,false,false);
                            }
                     
                        } else if (hasFCJ && old_tokens[0].equals("BE")) {
                            g=new PrimitiveBezier();
                            
                            for(l=0; l<j+1; ++l)
                                old_tokens[l+old_j+1]=tokens[l];
                            
                            old_j+=j+1;
                            g.parseTokens(old_tokens, old_j+1);
                            g.setSelected(selectNew);
                            if(old_j>5 && old_tokens[old_j].equals("1")) {
                            	macro_counter = 2;
                            } else {
                                addPrimitive(g,false,false);
                            }
                            
                        } else if (hasFCJ && (old_tokens[0].equals("RV")||
                            old_tokens[0].equals("RP"))) {
                            g=new PrimitiveRectangle();
                            
                            for(l=0; l<j+1; ++l)
                                old_tokens[l+old_j+1]=tokens[l];
                            
                            old_j+=j+1;
                            g.parseTokens(old_tokens, old_j+1);
                            g.setSelected(selectNew);
                            if(old_j>2 && old_tokens[old_j].equals("1")) {
                            	macro_counter = 2;
                            } else {
                                addPrimitive(g,false,false);
                            }                        
                        } else if (hasFCJ && (old_tokens[0].equals("EV")||
                            old_tokens[0].equals("EP"))) {
                            g=new PrimitiveOval();
                            
                            for(l=0; l<j+1; ++l)
                                old_tokens[l+old_j+1]=tokens[l];
                            
                            old_j+=j+1;
                            g.parseTokens(old_tokens, old_j+1);
                            g.setSelected(selectNew);
                            if(old_j>2 && old_tokens[old_j].equals("1")) {
                            	macro_counter = 2;
                            } else {
                                addPrimitive(g,false,false);
                            }                        
                        } else if (hasFCJ && (old_tokens[0].equals("PV")||
                            old_tokens[0].equals("PP"))) {
                            g=new PrimitivePolygon();
                            
                            for(l=0; l<j+1; ++l)
                                old_tokens[l+old_j+1]=tokens[l];
                            
                            old_j+=j+1;
                            g.parseTokens(old_tokens, old_j+1);
                            g.setSelected(selectNew);
                            if(old_j>2 && old_tokens[old_j].equals("1")) {
                            	macro_counter = 2;
                            } else {
                                addPrimitive(g,false,false);
                            }     
    					} else if (hasFCJ && (old_tokens[0].equals("CV")||
                            old_tokens[0].equals("CP"))) {
                            g=new PrimitiveComplexCurve();
                            
                            for(l=0; l<j+1; ++l)
                                old_tokens[l+old_j+1]=tokens[l];
                            
                            old_j+=j+1;
                            g.parseTokens(old_tokens, old_j+1);
                            g.setSelected(selectNew);
                            if(old_j>2 && old_tokens[old_j].equals("1")) {
                            	macro_counter = 2;
                            } else {
                                addPrimitive(g,false,false);
                            }     
    					} else if (hasFCJ && (old_tokens[0].equals("PL"))) {
                    		macro_counter = 2;
                    	} else if (hasFCJ && (old_tokens[0].equals("PA"))) {
                    		macro_counter = 2;
						} else if (hasFCJ && (old_tokens[0].equals("SA"))) {                    
                    		macro_counter = 2;                       
                        }
                        hasFCJ=false;
                    
                    } else if(tokens[0].equals("FJC")) {
                        // FidoCadJ Configuration
                    
                        if(tokens[1].equals("C")) {
                            // Connection size
                            newConnectionSize = 
                                Double.parseDouble(tokens[2]);
                        
                        } else if(tokens[1].equals("L")) {
                            // Layer configuration
                            int layerNum = Integer.parseInt(tokens[2]);
                            if (layerNum>=0&&layerNum<layerV.size()){
                                int rgb=Integer.parseInt(tokens[3]);
                                float alpha=Float.parseFloat(tokens[4]);
                                LayerDesc ll=(LayerDesc)(layerV.get(layerNum));
                                ll.setColor(new Color(rgb));
                                ll.setAlpha(alpha);
                                ll.setModified(true);  
                            }
                                
                        } else if(tokens[1].equals("N")) {
                        	// Layer name
                        	
                        	int layerNum = Integer.parseInt(tokens[2]);
                            if (layerNum>=0&&layerNum<layerV.size()){
                                String lName="";
                                
                                StringBuffer temp=new StringBuffer(25);
                                for(int t=3; t<j+1; ++t) {
                                	temp.append(tokens[t]);
                                	temp.append(" ");
                                }
                                
                                lName=temp.toString();
                                LayerDesc ll=(LayerDesc)(layerV.get(layerNum));
                                ll.setDescription(lName);
                                ll.setModified(true);  
                            }
                        
                        } else if(tokens[1].equals("A")) {
                            // Connection size
                            newLineWidth = 
                                Double.parseDouble(tokens[2]);
                        
                        } else if(tokens[1].equals("B")) {
                            // Connection size
                            newLineWidthCircles = 
                                Double.parseDouble(tokens[2]);                      
                        }
                        
                    } else if(tokens[0].equals("LI")) {
                        // Save the tokenized line.
                        // We cannot create the macro until we parse the 
                        // following line (which can be FCJ)
                        macro_counter=0;
    
                        for(l=0; l<j+1; ++l)
                            old_tokens[l]=tokens[l];
                        old_j=j;
                        hasFCJ=true;
        
                    } else if(tokens[0].equals("BE")) {
                        macro_counter=0;
    
                        for(l=0; l<j+1; ++l)
                            old_tokens[l]=tokens[l];
                        old_j=j;
                        hasFCJ=true;
                    } else if(tokens[0].equals("MC")) {
                        // Save the tokenized line.
                        macro_counter=0;
                        for(l=0; l<j+1; ++l)
                            old_tokens[l]=tokens[l];
                        old_j=j;
                        hasFCJ=true;
                    } else if(tokens[0].equals("TE")) {
                        hasFCJ=false;
                        macro_counter=0;
                        g=new PrimitiveAdvText();
                        g.parseTokens(tokens, j+1);
                        g.setSelected(selectNew);
                        addPrimitive(g,false,false);
                    } else if(tokens[0].equals("TY")) {
                        hasFCJ=false;
                        
                        if(macro_counter==2) {
                            macro_counter--;
                            name=new String[j+1];
        					for(l=0; l<j+1;++l)
                                name[l]=tokens[l];
                            vn=j;                                
                        } else if(macro_counter==1) {
                        	value=new String[j+1];
                            for(l=0; l<j+1;++l)
                                value[l]=tokens[l];
                            vv=j;       
                            if (name!=null) g.setName(name,vn+1);
                            g.setValue(value,vv+1);

                            g.setSelected(selectNew);
                            addPrimitive(g, false,false);
                            macro_counter=0;
                        } else {
                            g=new PrimitiveAdvText();
                            g.parseTokens(tokens, j+1);
                            g.setSelected(selectNew);
                            addPrimitive(g,false,false);
                         }
                    } else if(tokens[0].equals("PL")) {
                        hasFCJ=true;
                        for(l=0; l<j+1; ++l)
                            old_tokens[l]=tokens[l];
                            
                        macro_counter=0;
                        old_j=j;
                        g=new PrimitivePCBLine();
                        g.parseTokens(tokens, j+1);
                        g.setSelected(selectNew);
                        //addPrimitive(g,false,false);
                    } else if(tokens[0].equals("PA")) {
                        hasFCJ=true;
                        for(l=0; l<j+1; ++l)
                            old_tokens[l]=tokens[l];
                        macro_counter=0;
                        g=new PrimitivePCBPad();
                        old_j=j;
                        g.parseTokens(tokens, j+1);
                        g.setSelected(selectNew);
                        //addPrimitive(g,false,false);
                    } else if(tokens[0].equals("SA")) {
                        hasFCJ=true;
                        for(l=0; l<j+1; ++l)
                            old_tokens[l]=tokens[l];
                        old_j=j;
                        macro_counter=0;
                        g=new PrimitiveConnection();
                        g.parseTokens(tokens, j+1);
                        g.setSelected(selectNew);
                        //addPrimitive(g,false,false);
                    }  else if(tokens[0].equals("EV")||tokens[0].equals("EP")) {
                        macro_counter=0;
                        for(l=0; l<j+1; ++l)
                            old_tokens[l]=tokens[l];
                        old_j=j;
                        hasFCJ=true;
                	} else if(tokens[0].equals("RV")||tokens[0].equals("RP")) {
                        macro_counter=0;
    
                        for(l=0; l<j+1; ++l)
                            old_tokens[l]=tokens[l];
                        old_j=j;
                        hasFCJ=true;
                    } else if(tokens[0].equals("PV")||tokens[0].equals("PP")) {
                        macro_counter=0;
    
                        for(l=0; l<j+1; ++l)
                            old_tokens[l]=tokens[l];
                        old_j=j;
                        hasFCJ=true;
                    } else if(tokens[0].equals("CV")||tokens[0].equals("CP")) {
                        macro_counter=0;
    
                        for(l=0; l<j+1; ++l)
                            old_tokens[l]=tokens[l];
                        old_j=j;
                        hasFCJ=true;
                    }
                } catch(IOException E) {
                    System.out.println("Error encountered: "+E.toString());
                    System.out.println("string parsing line: "+lineNum);
                    hasFCJ = false;
                    macro_counter = 0;
                } catch(NumberFormatException F) {
                    System.out.println("I could not read a number at line: "
                                       +lineNum);
                    hasFCJ = false;
                    macro_counter = 0;                  
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
       
        try{
            registerPrimitivesWithFCJ(hasFCJ, tokens, g, old_tokens, old_j,
                selectNew);
        } catch(IOException E) {
            System.out.println("Error encountered: "+E.toString());
            System.out.println("string parsing line: "+lineNum);
        } catch(NumberFormatException F) {
            System.out.println("I could not read a number at line: "
                                     +lineNum);
        }
        
        // If the schematics has some configuration informations, we need
        // to set them up.
        if (newConnectionSize>0) {
            Globals.diameterConnection=newConnectionSize;
        }
        if (newLineWidth>0) {
            Globals.lineWidth = newLineWidth;
        }
        
        if (newLineWidthCircles>0) {
            Globals.lineWidthCircles = newLineWidthCircles;
        }
        sortPrimitiveLayers();
        }
        
    }
    
    private boolean registerPrimitivesWithFCJ(boolean hasFCJ, String[] tokens,
        GraphicPrimitive g, String[] old_tokens, int old_j, boolean selectNew)
        throws IOException
    {
    	boolean addPrimitive = false;
        if(hasFCJ && !tokens[0].equals("FCJ")) {
            if (old_tokens[0].equals("MC")) {
                g=new PrimitiveMacro(library,layerV);
                addPrimitive = true;
            } else if (old_tokens[0].equals("LI")) {
                g=new PrimitiveLine();
                addPrimitive = true;
            } else if (old_tokens[0].equals("BE")) {
                g=new PrimitiveBezier();
                addPrimitive = true;
            } else if (old_tokens[0].equals("RP")||
                old_tokens[0].equals("RV")) {
                g=new PrimitiveRectangle();
                addPrimitive = true;
            } else if (old_tokens[0].equals("EP")||
                old_tokens[0].equals("EV")) {
                g=new PrimitiveOval();
                addPrimitive = true;
            } else if (old_tokens[0].equals("PP")||
                old_tokens[0].equals("PV")) {
                g=new PrimitivePolygon();
                addPrimitive = true;
            } else if(old_tokens[0].equals("PL")) {
                g=new PrimitivePCBLine();
                addPrimitive = true;
             } else if (old_tokens[0].equals("CP")||
                old_tokens[0].equals("CV")) {
                g=new PrimitiveComplexCurve();
                addPrimitive = true;
            }  else if(old_tokens[0].equals("PA")) {
                g=new PrimitivePCBPad();
                addPrimitive = true;
            } else if(old_tokens[0].equals("SA")) {
                g=new PrimitiveConnection();
                addPrimitive = true;
            }
        }     
        
        if(addPrimitive) {
            g.parseTokens(old_tokens, old_j+1);
            g.setSelected(selectNew);
            addPrimitive(g,false,false);
            hasFCJ = false;        
        }
        return hasFCJ;
    }
   
    /** Performs a sort of the primitives on the basis of their layer.
        The sorting metod adopted is the Shell sort. By the practical point
        of view, this seems to be rather good even for large drawings. This is
        because the primitive list is always more or less already ordered.
        
    */
    public void sortPrimitiveLayers()
    {
        int i;
        GraphicPrimitive t,g,gg;
        boolean cont=true;
        //MyTimer mt=new MyTimer();
        maxLayer = 0;
        
        
        // Indexes
        int j,k,l;
        // Swap temporary variable
        GraphicPrimitive s;
        
        // Shell sort. This is a farly standard implementation
        for(l = getPrimitiveVector().size()/2; l>0; l/=2) {
            for(j = l; j< getPrimitiveVector().size(); ++j) {
                for(i=j-l; i>=0; i-=l) {
                    if(((GraphicPrimitive)getPrimitiveVector().get(i+l)).layer>=
                      ((GraphicPrimitive)getPrimitiveVector().get(i)).layer)
                        break;
                    else {
                        // Swap
                        s = (GraphicPrimitive)getPrimitiveVector().get(i);
                        getPrimitiveVector().set(i,getPrimitiveVector().get(i+l));
                        getPrimitiveVector().set(i+l, s);
                    }
                }
            }
        }
    
        // Since for sorting we need to analyze all the primitives in the 
        // database, this is a good place to calculate which layers are
        // used. We thus start by resetting the array.
        maxLayer = -1;
        k=0;
        
        for (l=0; l<Globals.MAX_LAYERS; ++l) {
            layersUsed[l] = false;
            
            for (i=k; i<getPrimitiveVector().size(); ++i){
                g=(GraphicPrimitive)getPrimitiveVector().get(i);
                
                
                if (g.containsLayer(l)) {
                    layersUsed[l]=true;
                    k=i;
                    for (int z = 0; z<l; z++) layersUsed[z]=true;
                    break;
                }
                    
                // We keep track of the maximum layer number used in the 
                // drawing.
                if (g.layer>maxLayer)
                        maxLayer = g.layer;
                
            }
        }
    }

    /** Export the file using the given interface
    
        @param exp the selected exporting interface
        @param header specify if an header and a tail should be written or not
        @param exportInvisible specify that the primitives on invisible layers
        should be exported
    */
    public synchronized void exportDrawing(ExportInterface exp, boolean header, 
        boolean exportInvisible, MapCoordinates mp)
        throws IOException
    {
        
        int l;
        int i;
        int j;
        
        GraphicPrimitive g;
		// If it is needed, we should write the header of the file. This is 
		// not to be done for example when we are exporting a macro and this
		// routine is called recursively.
		
        if (header) {
        	Point o=new Point(0,0);
        	Dimension d = ExportGraphic.getImageSize(this, 1, true,o);

			d.width+=Globals.exportBorder;
			d.height+=Globals.exportBorder;
			
        	// We remeber that getImageSize works only with logical coordinates
        	// so we may trasform them:
        	
        	d.width *= mp.getXMagnitude();
        	d.height *= mp.getYMagnitude();
        	
        	// We finally write the header
            exp.exportStart(d, layerV, mp.getXGridStep());
        }
        
        if(drawOnlyLayer>=0 && !drawOnlyPads){
            for (i=0; i<getPrimitiveVector().size(); ++i){
                g=(GraphicPrimitive)getPrimitiveVector().get(i);
                l=g.getLayer();
                if(l==drawOnlyLayer && 
                    !(g instanceof PrimitiveMacro)) {
                    if(((LayerDesc)(layerV.get(l))).isVisible||exportInvisible)
                        g.export(exp, mp);
                    
                }else if(g instanceof PrimitiveMacro) {
 
                    ((PrimitiveMacro)g).setDrawOnlyLayer(drawOnlyLayer);
                    ((PrimitiveMacro)g).setExportInvisible(exportInvisible);
 
                    if(((LayerDesc)(layerV.get(l))).isVisible||exportInvisible)
                        g.export(exp, mp); 
                }
            }
            return;
        } else if (!drawOnlyPads) {
            for(j=0;j<layerV.size(); ++j) {
                for (i=0; i<getPrimitiveVector().size(); ++i){
                
                    g=(GraphicPrimitive)getPrimitiveVector().get(i);
                    l=g.getLayer();         

                    if(l==j && !(g instanceof PrimitiveMacro)){
                        if(((LayerDesc)(layerV.get(l))).isVisible||exportInvisible)
                            g.export(exp, mp);
                        
                    } else if(g instanceof PrimitiveMacro) {

                        ((PrimitiveMacro)g).setDrawOnlyLayer(j);
                        ((PrimitiveMacro)g).setExportInvisible(exportInvisible);
    
                        if(((LayerDesc)(layerV.get(l))).isVisible||exportInvisible)
                            g.export(exp, mp);
                        
                    }
                }
            }
        }
        
        // Export in a second time only the PCB pads, in order to ensure that 
        // the
        // drills are always open.
        
        for (i=0; i<getPrimitiveVector().size(); ++i){
            if ((g=(GraphicPrimitive)getPrimitiveVector().get(i)) instanceof 
                PrimitivePCBPad) {
                ((PrimitivePCBPad)g).setDrawOnlyPads(true);
                l=g.getLayer();
    
                if(((LayerDesc)(layerV.get(l))).isVisible||exportInvisible)
                    g.export(exp, mp);
                ((PrimitivePCBPad)g).setDrawOnlyPads(false);
            } else if (g instanceof PrimitiveMacro) { // Uhm... not beautiful
                ((PrimitiveMacro)g).setExportInvisible(exportInvisible);
            
                ((PrimitiveMacro)g).setDrawOnlyPads(true);
                l=g.getLayer();
                if(((LayerDesc)(layerV.get(l))).isVisible||exportInvisible)
                    g.export(exp, mp);
                ((PrimitiveMacro)g).setDrawOnlyPads(false);
                ((PrimitiveMacro)g).resetExport();
            }
        }   
        
        if (header)
            exp.exportEnd();
    }
    
    /** Undo the last editing action
    */
    public void undo()
    {
    	//um.printUndoState();
        try {
            UndoState r = (UndoState)um.undoPop();
            if(!r.libraryDir.equals("")) {
            	if(libraryUndoListener!=null) {
            		libraryUndoListener.undoLibrary(r.libraryDir);
            	}
            } 
            
            if(!r.text.equals("")) {
            	StringBuffer s=new StringBuffer(r.text);
            	parseString(s);
            }
            isModified = r.isModified;
            openFileName = r.fileName;
        
            if(cl!=null) cl.somethingHasChanged();
            
        } catch (IOException E) 
        {
        	System.out.println("Could not undo.");
        }
        
    }
    
    /** Redo the last undo action
    
    */
    public void redo()
    {
        try {
        
            UndoState r = (UndoState)um.undoRedo();
            if(!r.libraryDir.equals("")) {
            	if(libraryUndoListener!=null) {
            		libraryUndoListener.undoLibrary(r.libraryDir);
            	}
            } 
            
            if(!r.text.equals("")) {
            	StringBuffer s=new StringBuffer(r.text);
            	parseString(s);
            }
            
            isModified = r.isModified;
            openFileName = r.fileName;
        
            if(cl!=null) cl.somethingHasChanged();
            
        } catch (IOException E) 
        {
        	System.out.println("Could not redo.");
        }
        
    }
    
    private String tempLibraryDirectory="";
    /** Save the undo state
    
    */
    public void saveUndoState()
    {
        UndoState s = new UndoState();
        s.text=getText(true).toString();

        s.isModified=isModified;
        s.fileName=openFileName;
        s.libraryDir=tempLibraryDirectory;
        
        um.undoPush(s);
        isModified = true;
        if(cl!=null) cl.somethingHasChanged();
    }
    
    public void saveUndoLibrary(String t)
    {
    	tempLibraryDirectory=t;
    	UndoState s = new UndoState();
    	s.text=getText(true).toString();
        s.libraryDir=tempLibraryDirectory;
        s.isModified=isModified;
        s.fileName=openFileName;
        tempDir.add(t);
        
        um.undoPush(s);
    }
    
    public void setLibraryUndoListener(LibraryUndoListener l)
   	{
   		libraryUndoListener = l;
   	}
   
    /** Determine if the drawing has been modified.
        @return the state.
    */
    public boolean getModified ()
    {
        return isModified;
    }

    /** Set the drawing modified state.
        @param s the new state to be set.
    */
    public void setModified (boolean s)
    {
        isModified = s;
        if(cl!=null) cl.somethingHasChanged();
    }
    
    /** Set the listener of the state change.
        @param l the new listener.
    */
    public void setHasChangedListener (HasChangedListener l)
    {
        cl = l;
    }
    
    /** Set the visibility of the macro origin
        @param s the visibility state
    */
    public synchronized void setMacroOriginVisible(boolean s)
    {
        hasFCJOriginVisible = s;
    }
    
    /** Returns true if there is no drawing in memory
        @return true if the drawing is empty.
    */
    public boolean isEmpty()
    {
        return getPrimitiveVector().size()==0;
    }
    
    /** Returns true if there is the need of drawing holes in the actual
        drawing.
        
        @return true if holes are needed.
    
    */
    public final boolean getNeedHoles()
    {
        return needHoles;
    }
    
    /** Set the change state of the class. This is different from being 
        "modified", since modifies implies that the current drawing has not
        been saved yet. Changed just means that we want to  recalculate
        everything in deep during the first redraw.
    
        @param c if true, force a deep recalculation of all primitive 
            parameters at the first redraw.
    
    */
    public final void setChanged(boolean c)
    {
        changed=c;
    }

	public Vector<GraphicPrimitive> getPrimitiveVector() {
		return primitiveVector;
	}

	public void setPrimitiveVector(Vector<GraphicPrimitive> primitiveVector) {
		this.primitiveVector = primitiveVector;
	}
	
	public void doTheDishes()
	{
		//System.out.println("Do the dishes...");
		for (int i=0; i<tempDir.size();++i) 
		{
			Globals.deleteDirectory(new File(tempDir.get(i)));
		}
	}
	
}

