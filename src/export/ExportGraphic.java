package export;

import javax.imageio.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.lang.*; 


import globals.*;
import layers.*;

import geom.*;
import circuit.*;

/** ExportGraphic.java

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

	Copyright 2007-2011 by Davide Bucci
</pre>   
  
	@author Davide Bucci
*/
public class ExportGraphic 
{

	
	/** Exports the circuit contained in circ using the specified parsing 
		class.
		
		@param file the file name of the graphic file which will be created.
		@param P the parsing schematics class which should be used (libraries).
		@param format the graphic format which should be used {png|jpg}.
		@param unitperpixel the number of unit for each graphic pixel.
		@param antiAlias specify whether the anti alias option should be on.
		@param blackWhite specify that the export should be done in B/W.
		@param ext activate FidoCadJ extensions when exporting
		@param shiftMin shift the exported image at the origin.


	*/
	public static void export(File file, 
						ParseSchem P, 
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
		@param heith the image heigth in pixels (raster images only)
		@param antiAlias specify whether the anti alias option should be on.
		@param blackWhite specify that the export should be done in B/W.
		@param ext activate FidoCadJ extensions when exporting
		@param shiftMin shift the exported image at the origin.

	*/
	public static void exportSize(File file, 
						ParseSchem P, 
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
						ParseSchem P, 
						String format,
						int width,
						int height,
						double unitPerPixel,
						boolean setSize,
						boolean antiAlias,
						boolean blackWhite,
						boolean ext,
						boolean shiftMin)
	throws IOException
	{

		// obtain drawing size
		MapCoordinates m=new MapCoordinates();
		
		// This solves bug #3299281
		P.deselectAll();

		Point org=new Point(0,0);
		
		if (setSize) {
			// In this case, the image size is set and so we need to calculate
			// the correct zoom factor in order to fit the drawing in the 
			// specified area.
			Dimension d = getImageSize(P, 1,true);

			d.width+=Globals.exportBorder;
			d.height+=Globals.exportBorder;
			
			
			unitPerPixel = Math.min((double)width/(double)d.width, 
				(double)height/(double)d.height);
		} else {
			// In this situation, we do have to calculate the size from the
			// specified resolution.
			Dimension d = getImageSize(P, 1,true);
			
			width=(int)((d.width+Globals.exportBorder)*unitPerPixel);
			height=(int)((d.height+Globals.exportBorder)*unitPerPixel);
		}
	  	org=getImageOrigin(P,1);
		
		org.x *=unitPerPixel;
		org.y *=unitPerPixel;
		
		org.x -= Globals.exportBorder*unitPerPixel/2.0;
		org.y -= Globals.exportBorder*unitPerPixel/2.0;
		
		Vector ol=P.getLayers();

		BufferedImage bufferedImage;
		
		// To print in black and white, we only need to create a single layer
		// in which all layers will be exported and drawn.
		// Clearly, the choosen color will be black.
		if(blackWhite) {
			Vector v=new Vector();
			for (int i=0; i<16;++i)
				v.add(new LayerDesc(Color.black, 
					((LayerDesc)ol.get(i)).getVisible(),
					"B/W",((LayerDesc)ol.get(i)).getAlpha()));
			
			P.setLayers(v);
		}

		// Center the drawing in the given space.
        
        m.setMagnitudes(unitPerPixel, unitPerPixel);
        
        if(shiftMin) {
        	m.setXCenter(-org.x);
	   		m.setYCenter(-org.y);
		}	       
    	if (format.equals("png")||format.equals("jpg")) {
	
        	// Create a buffered image in which to draw

			try {
        		bufferedImage = new BufferedImage(width, height, 
        								  BufferedImage.TYPE_INT_RGB);
    
        		// Create a graphics contents on the buffered image
        		Graphics2D g2d = bufferedImage.createGraphics();
        
        		if(antiAlias) {
        			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                   		RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                   		RenderingHints.VALUE_ANTIALIAS_ON);
        			}
        		g2d.setColor(Color.white);
        		g2d.fillRect(0,0, width, height);
				// Save bitmap
				P.draw(g2d,m);
       		
        		ImageIO.write(bufferedImage, format, file);
        		// Graphics context no longer needed so dispose it
        		g2d.dispose();
        	} catch (java.lang.OutOfMemoryError E) {
        		IOException D=new IOException("Memory Error");
       			
       			P.setLayers(ol);
    			throw D;
        	} catch (Exception E) {
        		IOException D=new IOException("Size error"+E);
       			P.setLayers(ol);
    			throw D;
			}
    	} else if(format.equals("svg")) {
    		ExportSVG es = new ExportSVG(file);
    		P.exportDrawing(es, true, false, m);
    	} else if(format.equals("eps")) {
    		ExportEPS ep = new ExportEPS(file);
    		P.exportDrawing(ep, true, false, m);
    	} else if(format.equals("pgf")) {
    		ExportPGF ef = new ExportPGF(file);
    		P.exportDrawing(ef, true, false, m);
    	} else if(format.equals("pdf")) {
    		ExportPDF ef = new ExportPDF(file);
    		P.exportDrawing(ef, true, false, m);
    	} else if(format.equals("scr")) {
    		ExportEagle ef = new ExportEagle(file);
    		P.exportDrawing(ef, true, false, m);
    	} else if(format.equals("fcd")) {
    		ExportFidoCad ef = new ExportFidoCad(file);
    		ef.setExtensions(ext);
    		P.exportDrawing(ef, true, true, m);
    	} else {
    		IOException E=new IOException(
    			"Wrong file format");
    		throw E;
    	}
    	
       	P.setLayers(ol);
    	return;
    }
    
    /**	Get the image size.
    	@param P the parsing class to be used.
    	@param unitperpixel the zoom set to be used.
    	@param countMin specifies that the size should be calculated counting 
    		the minimum x and y coordinates, and not the origin.
    
    */
    public static Dimension getImageSize(ParseSchem P, 
    							  double unitperpixel, 
    							  boolean countMin)
    {
    	int width;
		int height;
		// Unfortunately, to get the image size, we need to redraw it.	
		// I do not like it, even if here we are not in a speed sensitive
		// context!
		
	
		BufferedImage bufferedImage = new BufferedImage(10, 10, 
        								  BufferedImage.TYPE_INT_RGB);
    
        // Create a graphics contents on the buffered image
		MapCoordinates m=new MapCoordinates();
       	m.setMagnitudes(unitperpixel, unitperpixel);
       	m.setXCenter(0);
       	m.setYCenter(0);
       	m.resetMinMax();
       	
        Graphics2D g2d = bufferedImage.createGraphics();
       	// force an in deep recalculation
       	P.setChanged(true);

        P.draw(g2d,m);
        // Graphics context no longer needed so dispose it
        g2d.dispose();
    	
    	// Verify that the image size is correct
    	if(countMin) {
			width=m.getXMax()-
				m.getXMin();
			height=m.getYMax()-
				m.getYMin();
		} else {
			width=m.getXMax();
			height=m.getYMax();
		}
		
		if(width<=0 || height<=0) {
			System.out.println("Warning: Image has a zero"+
							   "sized image");					   
			width=100;
			height=100;
		}

		return new Dimension(width, height);
    }
    
    /**	Get the image origin.
    	@param P the parsing class to be used.
    	@param unitperpixel the zoom set to be used.
    */
    public static Point getImageOrigin(ParseSchem P, double unitperpixel)
    {
    	int originx;
		int originy;
		// Unfortunately, to get the image size, we need to redraw it.	
		// I do not like it, even if here we are not in a speed sensitive
		// context!
		
		P.setChanged(true);		
		// Create a dummy image on which the drawing will be done
		BufferedImage bufferedImage = new BufferedImage(100, 100, 
        								  BufferedImage.TYPE_INT_RGB);
    
        // Create a graphics contents on the buffered image
		MapCoordinates m=new MapCoordinates();
       	m.setMagnitudes(unitperpixel, unitperpixel);
       	m.setXCenter(0);
       	m.setYCenter(0);
       	
       	Graphics2D g2d = bufferedImage.createGraphics();
		// Draw the image. In this way, the min and max coordinates will be
		// tracked.
        P.draw(g2d, m);
        // Graphics context no longer needed so dispose it
        g2d.dispose();
    
    	// Verify that the image size is correct
		if (m.getXMax() >= m.getXMin() && 
			m.getYMax() >= m.getYMin()){
			originx=m.getXMin();
			originy=m.getYMin();
		} else {
			originx=0;
			originy=0;
		}

		
		return new Point(originx, originy);
    }
    
    /** Calculate the zoom to fit the given size in pixel (i.e. the viewport
    	size).
    	
    	@param sizex the width of the area to be used for calculations.
    	@param sizey the height of the area to be used for calculations.
    	@param countMin specify if the absolute or relative size should be
    		taken into account
    
    */
    public static MapCoordinates calculateZoomToFit(ParseSchem P, int sizex, int sizey, 
    				boolean forceCalc, boolean countMin)
    {
 		// Here we calculate the zoom to fit parameters
		double maxsizex;
		double maxsizey;
		Point org=new Point(0,0);
		
		P.setChanged(true);
		MapCoordinates newZoom=new MapCoordinates();
	
		// If the size is invalid (for example because it's the first time
		// the circuit has been drawn).
		
		forceCalc=true;	// 0.20.5
		
		Dimension D = getImageSize(P,1,countMin); 
		maxsizex=D.width;
		maxsizey=D.height;
			
		if (countMin) 
			org=getImageOrigin(P,1);
			
		double zoomx=1.0/((maxsizex)/(double)sizex);
		double zoomy=1.0/((maxsizey)/(double)sizey);				
		
		double z=(zoomx>zoomy)?zoomy:zoomx;
		
		z=Math.round(z*100.0)/100.0;		// 0.20.5
		
		newZoom.setMagnitudes(z,z);
		newZoom.setXCenter(-(org.x*z));
		newZoom.setYCenter(-(org.y*z));
	
		return newZoom;
	}    
}