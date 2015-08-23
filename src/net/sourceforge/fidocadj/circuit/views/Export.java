package net.sourceforge.fidocadj.circuit.views;

import java.io.*;

import net.sourceforge.fidocadj.graphic.*;
import net.sourceforge.fidocadj.circuit.model.*;
import net.sourceforge.fidocadj.export.*;
import net.sourceforge.fidocadj.geom.*;
import net.sourceforge.fidocadj.layers.*;
import net.sourceforge.fidocadj.primitives.*;

/** Export: export the FidoCadJ drawing. This is a view of the drawing.
    
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
public class Export 
{
	private final DrawingModel P;
	
	// Border to be used in the export in logical coordinates
    public static final int exportBorder=6;

	/** Creator
	*/
    public Export(DrawingModel pp)
    {
    	P=pp;
    }

    /** Export the file using the given interface
    
        @param exp the selected exporting interface
        @param header specify if an header and a tail should be written or not
        @param exportInvisible specify that the primitives on invisible layers
        should be exported
    */
    public void exportDrawing(ExportInterface exp, boolean header, 
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
		synchronized(this) {
        	if (header) {
        		PointG o=new PointG(0,0);
         		DimensionG d = DrawingSize.getImageSize(P, 1, true,o);
				d.width+=exportBorder;
				d.height+=exportBorder;
			
        		// We remeber that getImageSize works only with logical 
        		// coordinates so we may trasform them:
        	
        		d.width *= mp.getXMagnitude();
        		d.height *= mp.getYMagnitude();
        	
        		// We finally write the header
            	exp.exportStart(d, P.layerV, mp.getXGridStep());
        	}
        
        	if(P.drawOnlyLayer>=0 && !P.drawOnlyPads){
            	for (i=0; i<P.getPrimitiveVector().size(); ++i){
                	g=(GraphicPrimitive)P.getPrimitiveVector().get(i);
                	l=g.getLayer();
                	if(l==P.drawOnlyLayer && 
                    	!(g instanceof PrimitiveMacro)) {
                    	if(((LayerDesc)(P.layerV.get(l))).isVisible||
                    		exportInvisible)
                        	g.export(exp, mp);
                    
                	} else if(g instanceof PrimitiveMacro) {
 
                    	((PrimitiveMacro)g).setDrawOnlyLayer(P.drawOnlyLayer);
                    	((PrimitiveMacro)g).setExportInvisible(exportInvisible);
 
                    	if(((LayerDesc)(P.layerV.get(l))).isVisible||
                    		exportInvisible)
                        	g.export(exp, mp); 
                	}
            	}
            	return;
        	} else if (!P.drawOnlyPads) {
            	for(j=0;j<P.layerV.size(); ++j) {
                	for (i=0; i<P.getPrimitiveVector().size(); ++i){
                
                    	g=(GraphicPrimitive)P.getPrimitiveVector().get(i);
                    	l=g.getLayer();         

                    	if(l==j && !(g instanceof PrimitiveMacro)){
                        	if(((LayerDesc)(P.layerV.get(l))).isVisible||
                        		exportInvisible)
                            	g.export(exp, mp);
                        
                    	} else if(g instanceof PrimitiveMacro) {

                        	((PrimitiveMacro)g).setDrawOnlyLayer(j);
                        	((PrimitiveMacro)g).setExportInvisible(
                        		exportInvisible);
    
                        	if(((LayerDesc)(P.layerV.get(l))).isVisible||
                        		exportInvisible)
                            	g.export(exp, mp);
                    	}
                	}
            	}
        	}
        
        	// Export in a second time only the PCB pads, in order to ensure
        	// that the drills are always open.
        
        	for (i=0; i<P.getPrimitiveVector().size(); ++i){
            	if ((g=(GraphicPrimitive)P.getPrimitiveVector().get(i)) 
            		instanceof 
                	PrimitivePCBPad) {
                	((PrimitivePCBPad)g).setDrawOnlyPads(true);
                	l=g.getLayer();
    
                	if(((LayerDesc)(P.layerV.get(l))).isVisible||exportInvisible)
                   		g.export(exp, mp);
                	((PrimitivePCBPad)g).setDrawOnlyPads(false);
            	} else if (g instanceof PrimitiveMacro) { 
            		// Uhm... not beautiful
                	((PrimitiveMacro)g).setExportInvisible(exportInvisible);
                	((PrimitiveMacro)g).setDrawOnlyPads(true);
                	l=g.getLayer();
                	if(((LayerDesc)(P.layerV.get(l))).isVisible||exportInvisible)
                    	g.export(exp, mp);
                	((PrimitiveMacro)g).setDrawOnlyPads(false);
                	((PrimitiveMacro)g).resetExport();
            	}
        	}   
        
        	if (header)
            	exp.exportEnd();
        }
    }
            
}