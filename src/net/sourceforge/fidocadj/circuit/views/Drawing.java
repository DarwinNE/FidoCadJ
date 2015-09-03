package net.sourceforge.fidocadj.circuit.views;

import java.util.*;

import net.sourceforge.fidocadj.circuit.model.*;
import net.sourceforge.fidocadj.geom.*;
import net.sourceforge.fidocadj.globals.*;
import net.sourceforge.fidocadj.layers.*;
import net.sourceforge.fidocadj.primitives.*;
import net.sourceforge.fidocadj.graphic.*;

/** Drawing: draws the FidoCadJ drawing. This is a view of the drawing.
    
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

    Copyright 2007-2014 by Davide Bucci
</pre>
*/
public class Drawing 
{
    private final DrawingModel P;

    // True if the drawing needs holes. This implies that the redrawing
    // step must include a cycle at the end to draw all holes.
    private boolean needHoles;
    
    
    // *********** CACHE *************
    
    // Here are some counters and local variables. We made them class members
    // to ensure that their place is reserved in memory and we do not need
    // some time expensive allocations, since speed is important in the draw
    // operation (used in draw).
    
    private double oZ, oX, oY, oO;
    private GraphicPrimitive gg;
    private int i_index;
    private int j_index;
        
    public Drawing (DrawingModel pp)
    {
        P=pp;
    }

    /** Draw the handles of all selected primitives
        @param gi the graphic context to be used.
    */
    public void drawSelectedHandles(GraphicsInterface gi, MapCoordinates cs)
    {
        int i;
        for (GraphicPrimitive gp : P.getPrimitiveVector()) {
            if(gp.getSelected()) 
                gp.drawHandles(gi, cs);
        }      
    }
    
    /** Draw the current drawing.
        This code is rather critical. Do not touch it unless you know very
        precisely what to do.
        
        @param G the graphic context in which the drawing should be drawn.
        @param cs the coordinate mapping to be used.
        
    */
    public void draw(GraphicsInterface G, MapCoordinates cs)
    {   
        if(cs==null) {
            System.out.println(
                "DrawingModel.draw: ouch... cs not initialized :-(");
            return;
        }
        
        synchronized (this) {
            // At first, we check if the current view has changed. 
            if(P.changed || oZ!=cs.getXMagnitude() || oX!=cs.getXCenter() || 
                oY!=cs.getYCenter() || oO!=cs.getOrientation()) {
                oZ=cs.getXMagnitude();
                oX=cs.getXCenter();
                oY=cs.getYCenter();
                oO=cs.getOrientation();
                P.changed = false;
            
                // Here we force for a global refresh of graphic data at the 
                // primitive level. 
                for (GraphicPrimitive gp : P.getPrimitiveVector()) 
                    gp.setChanged(true);
                    
                if (!P.drawOnlyPads) 
                    cs.resetMinMax();
            }
        
            needHoles=P.drawOnlyPads;
       
            /* First possibility: we need to draw only one layer (for example 
                in a macro). This is indicated by the fact that drawOnlyLayer
                is non negative.
            */
            if(P.drawOnlyLayer>=0 && !P.drawOnlyPads){
            
                // At first, we check if the layer is effectively used in the
                // drawing. If not, we exit directly.
            
                if(!P.layersUsed[P.drawOnlyLayer])
                    return;
            
                drawPrimitives(P.drawOnlyLayer, G, cs);
                return;
            } else if (!P.drawOnlyPads) {
                // If we want to draw all layers, we need to process with order.
                for(j_index=0;j_index<LayerDesc.MAX_LAYERS; ++j_index) {
                    if(!P.layersUsed[j_index])
                        continue;
                    drawPrimitives(j_index, G,cs);             
                }
            }
            // Draw in a second time only the PCB pads, in order to ensure that
            // the drills are always open.
            if(needHoles) {
                for (i_index=0; i_index<P.getPrimitiveVector().size(); 
                    ++i_index){
                
                    // We will process only primitive which require holes (pads
                    // as well as macros containing pads).
                
                    if ((gg=(GraphicPrimitive)P.getPrimitiveVector().
                        get(i_index)).needsHoles()) {
                        gg.setDrawOnlyPads(true);
                        gg.draw(G, cs, P.layerV);
                        gg.setDrawOnlyPads(false);
                    } 
                }
            }
        }
    }    
    
    /** Returns true if there is the need of drawing holes in the actual
        drawing.
        
        @return true if holes are needed.
    
    */
    public final boolean getNeedHoles()
    {
        return needHoles;
    }
    /** Draws all the primitives and macros contained in the specified layer.
        This function is used mainly by the draw member.
        @param j_index the layer to be considered.
        @param G the graphic context in which to draw.
    */
    private void drawPrimitives(int j_index, GraphicsInterface graphic, 
        MapCoordinates cs)
    {
        GraphicPrimitive gg;
        int i_index;
        
        // Here we process all the primitives, one by one!
        for (i_index=0; i_index<P.getPrimitiveVector().size(); ++i_index) {
            gg=(GraphicPrimitive)P.getPrimitiveVector().get(i_index);
                
            // Layers are ordered. This improves the redrawing speed. 
            if (j_index>0 && gg.layer>j_index) {
                break;
            } 
                    
            // Process a particular primitive if it is in the layer
            // being processed.
                    
            if(gg.containsLayer(j_index)) {
                gg.setDrawOnlyLayer(j_index);
                gg.draw(graphic, cs, P.layerV);    
            }
                    
            if(gg.needsHoles())
                needHoles=true;
        }
    }
            
}