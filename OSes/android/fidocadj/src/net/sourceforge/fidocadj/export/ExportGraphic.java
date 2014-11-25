package net.sourceforge.fidocadj.export;

import java.io.*;
import java.util.*;
import java.lang.*; 


import globals.*;
import net.sourceforge.fidocadj.layers.*;
import graphic.*;
import graphic.nil.*;

import net.sourceforge.fidocadj.geom.*;
import net.sourceforge.fidocadj.circuit.controllers.*;
import net.sourceforge.fidocadj.circuit.model.*;
import net.sourceforge.fidocadj.circuit.views.*;

/** 		ANDROID VERSION - now empty!

	ExportGraphic.java

	Handle graphic export of a Fidocad file
	This class should be used to export the circuit under different graphic
	formats.
	
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
  
	@author Davide Bucci
*/
public final class ExportGraphic 
{

	private ExportGraphic()
	{
		// Nothing to do.
	}
	
	/** Exports the circuit contained in circ using the specified parsing 
		class.
		
		@param file the file name of the graphic file which will be created.
		@param P the parsing schematics class which should be used (libraries).
		@param format the graphic format which should be used {png|jpg}.
		@param unitPerPixel the number of unit for each graphic pixel.
		@param antiAlias specify whether the anti alias option should be on.
		@param blackWhite specify that the export should be done in B/W.
		@param ext activate FidoCadJ extensions when exporting
		@param shiftMin shift the exported image at the origin.


	*/
	public static void export(File file, 
						DrawingModel P, 
						String format,
						double unitPerPixel,
						boolean antiAlias,
						boolean blackWhite,
						boolean ext,
						boolean shiftMin)
	throws IOException
	{
		exportSizeP( file, 
						 P, 
						 format,
						 0,
						 0,
						 unitPerPixel,
						 false,
						 antiAlias,
						 blackWhite,
						 ext,
						 shiftMin);
	}	
	
	/** Exports the circuit contained in circ using the specified parsing 
		class.
		
		@param file the file name of the graphic file which will be created.
		@param P the parsing schematics class which should be used (libraries).
		@param format the graphic format which should be used {png|jpg}.
		@param width the image width in pixels (raster images only)
		@param height the image heigth in pixels (raster images only)
		@param antiAlias specify whether the anti alias option should be on.
		@param blackWhite specify that the export should be done in B/W.
		@param ext activate FidoCadJ extensions when exporting
		@param shiftMin shift the exported image at the origin.

	*/
	public static void exportSize(File file, 
						DrawingModel P, 
						String format,
						int width,
						int height,
						boolean antiAlias,
						boolean blackWhite,
						boolean ext,
						boolean shiftMin)
	throws IOException
	{
		exportSizeP( file, 
						 P, 
						 format,
						 width,
						 height,
						 1,
						 true,
						 antiAlias,
						 blackWhite,
						 ext,
						 shiftMin);
	}
	
	/** Exports the circuit contained in circ using the specified parsing 
		class.
		
		@param file the file name of the graphic file which will be created.
		@param P the parsing schematics class which should be used (libraries).
		@param format the graphic format which should be used {png|jpg}.
		@param unitperpixel the number of unit for each graphic pixel.
		@param width the image width in pixels (raster images only)
		@param heith the image heigth in pixels (raster images only)
		@param setSize if true, calculate resolution from size. If false, it 
			does the opposite strategy.
		@param antiAlias specify whether the anti alias option should be on.
		@param blackWhite specify that the export should be done in B/W.
		@param ext activate FidoCadJ extensions when exporting.
		@param shiftMin shift the exported image at the origin.

	*/
	private static void exportSizeP(File file, 
						DrawingModel P, 
						String format,
						int width_t,
						int height_t,
						double unitPerPixel_t,
						boolean setSize,
						boolean antiAlias,
						boolean blackWhite,
						boolean ext,
						boolean shiftMin)
	throws IOException
	{
    }
    
    /**	Get the image size.
    	@param P the parsing class to be used.
    	@param unitperpixel the zoom set to be used.
    	@param countMin specifies that the size should be calculated counting 
    		the minimum x and y coordinates, and not the origin.
    	@param origin is updated with the image origin.
    
    */
    public static DimensionG getImageSize(DrawingModel P, 
    							  double unitperpixel, 
    							  boolean countMin,
    							  PointG origin)
    {

		return new DimensionG(0, 0);
    }
    
    /**	Get the image origin.
    	@param P the parsing class to be used.
    	@param unitperpixel the zoom set to be used.
    */
    public static PointG getImageOrigin(DrawingModel P, double unitperpixel)
    {
		return new PointG(0, 0);
    }
    
    /** Calculate the zoom to fit the given size in pixel (i.e. the viewport
    	size).
    	
    	@param sizex the width of the area to be used for calculations.
    	@param sizey the height of the area to be used for calculations.
    	@param countMin specify if the absolute or relative size should be
    		taken into account
    
    */
    public static MapCoordinates calculateZoomToFit(DrawingModel P, int sizex, 
    	int sizey, boolean countMin)
    {

		MapCoordinates newZoom=new MapCoordinates();

		return newZoom;
	}    
}