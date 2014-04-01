package circuit.model;

import java.util.*;

import layers.*;
import primitives.*;
import geom.*;
import graphic.*;

import circuit.controllers.UndoActions; 	// TODO: remove this

/**
<pre>
   FidoCadJ schematics draw class.
   
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

    Copyright 2007-2014 by Davide Bucci
</pre>

   Main parsing class 
    
    @author Davide Bucci
*/

public class DrawingModel
{   
    // ************* DRAWING *************
    
    // Array used to determine which layer is used in the drawing.
    public boolean[] layersUsed;   

    // Higher priority layer used in the drawing.
    public int maxLayer;
    // True if only pads should be drawn.
    public boolean drawOnlyPads;
    
    // Positive if during the redraw step only a particular layer should be
    // drawn
    public int drawOnlyLayer;
    
    private int exportBorder;

    // Font and size to be used for the text associated to the macros.
    private String macroFont;
    private int macroFontSize;
    
    // True if the drawing characteristics have been modified. This implies
    // that during the first redraw a in-depth calculation of all coordinates
    // will be done. For performance reasons, this is indeed done only when
    // necessary.
    public boolean changed;		// TODO: should be private
    
    // ******* PRIMITIVE DATABASE ********
    
    // Vector containing all primitives in the drawing.
    private Vector<GraphicPrimitive> primitiveVector;
    // Vector containing all layers used in the drawing.
    public Vector<LayerDesc> layerV;

    // Library of macros loaded.
    private Map<String, MacroDesc> library;
  
    /** The standard constructor. Not so much interesting, apart for the
        fact that it allocates memory of a few internal objects and reset all
        state flags.
    */
        
    public DrawingModel()
    {
        //tokens=new String[MAX_TOKENS];
        setPrimitiveVector(new Vector<GraphicPrimitive>(25));
        layerV=new Vector<LayerDesc>(LayerDesc.MAX_LAYERS);
        library=new TreeMap<String, MacroDesc>();
        macroFont = "Courier New";
        
        drawOnlyPads=false;
        drawOnlyLayer=-1;
        layersUsed = new boolean[LayerDesc.MAX_LAYERS];
        exportBorder=0;
        changed=true;
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
        for (int i=0; i<getPrimitiveVector().size(); ++i) {
            GraphicPrimitive g= (GraphicPrimitive)getPrimitiveVector().get(i);
            if (g instanceof PrimitiveMacro) {
            	((PrimitiveMacro) g).setLayers(v);
            }
        }
        changed=true;
    }
    
    public void setExportBorder(int b)
    {
    	exportBorder=b;
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
        changed=true;
    }
    
       
    /** Add a graphic primitive.
        @param p the primitive to be added.
        @param sort if true, sort the primitive layers
        @param ua if different from <pre>null</pre>, the operation will be
        	undoable.
    */
    public void addPrimitive(GraphicPrimitive p, boolean sort,
    	UndoActions ua)
    {   
        // The primitive database MUST be ordered. The idea is that we insert
        // primitives without ordering them and then we call a sorter.
        synchronized(this) {        
        	getPrimitiveVector().add(p);

        	// We check if the primitives should be sorted depending of 
        	// their layer
        	// If there are more than a few primitives to insert, it is wise to
        	// sort only once, at the end of the insertion process.
        	if (sort)
            	sortPrimitiveLayers();
            
        	// Check if it should be undoable.
        	if (ua!=null) {
        		ua.saveUndoState();
        		ua.setModified(true);
        		// We now have to track that something has changed. This 
        		// forces all the
        		// caching system used by the drawing routines to be refreshed.
        		changed=true;
        	}
        }
    }

    /** Set the font of all macros.
        @param f the font name
        @param tsize the size
        @param ua the undo controller or null if not useful.
    */
    public void setTextFont(String f, int tsize, UndoActions ua)
    {
        int i;
        int size=tsize;
        
        // Silently correct a wrong size. This should never happen (the dialog
        // has a control, but avoids a wrong configuration to sneak somewhere
        // else.
		if(size<=0)
			size=1;

        macroFont=f;
        macroFontSize = size;

        for (i=0; i<getPrimitiveVector().size(); ++i){
            ((GraphicPrimitive)getPrimitiveVector().get(i)).setMacroFont(
            	f, size);
        }
        changed=true;
        if(ua!=null) ua.setModified(true);
    }
    
    
    /** Get the font of all macros.
        @return the font name
    */
    public String getTextFont()
    {   
        return macroFont;
    }
    
    /** Get the size of the font used for all macros.
        @return the font name
    */
    public int getTextFontSize()
    {   
    	// TODO: not very elegant piece of code
        for (int i=0; i<getPrimitiveVector().size(); ++i){
            return ((GraphicPrimitive)getPrimitiveVector().get(i))
                    .getMacroFontSize();            
        }
        return macroFontSize;
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
                        getPrimitiveVector().set(i,
                        	getPrimitiveVector().get(i+l));
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
        
        for (l=0; l<LayerDesc.MAX_LAYERS; ++l) {
            layersUsed[l] = false;
            
            for (i=k; i<getPrimitiveVector().size(); ++i){
                g=(GraphicPrimitive)getPrimitiveVector().get(i);
                
                // We keep track of the maximum layer number used in the 
                // drawing.
                if (g.layer>maxLayer)
                        maxLayer = g.layer;
                
                if (g.containsLayer(l)) {
                    layersUsed[l]=true;
                    k=i;
                    for (int z = 0; z<l; ++z) layersUsed[z]=true;
                    break;
                }                
            }
        }
    }
    /** Get the maximum layer which contains something. This value is updated
        after a redraw. This is tracked for efficiency reasons.
        
        @return the maximum layer number.
    */
    final public int getMaxLayer()
    {
        return maxLayer;
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

    
    /** Returns true if there is no drawing in memory
        @return true if the drawing is empty.
    */
    public boolean isEmpty()
    {
        return getPrimitiveVector().size()==0;
    }
    
    
    /** Set the change state of the class. Changed just means that we want 
    	to  recalculate everything in deep during the following redraw.
    	This is different from being "modified", since "modified" implies 
    	that the current drawing has not been saved yet. 
    
        @param c if true, force a deep recalculation of all primitive 
            parameters at the first redraw.
    
    */
    public final void setChanged(boolean c)
    {
        changed=c;
    }

	/** Obtains a vector containing all elements.
		@return the vector containing all graphical objects.
	*/
	public Vector<GraphicPrimitive> getPrimitiveVector() 
	{
		return primitiveVector;
	}

	/** Sets a vector containing all elements.
		@param primitiveVector the vector containing all graphical objects.
	*/
	public void setPrimitiveVector(Vector<GraphicPrimitive> primitiveVector) 
	{
		this.primitiveVector = primitiveVector;
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
}

