package net.sourceforge.fidocadj.export;

import java.util.*;
import java.io.*;
import java.text.*;

import net.sourceforge.fidocadj.layers.*;
import globals.*;
import circuit.controllers.*;
import circuit.model.*;
import graphic.*;
import primitives.*;


/** 
	Export towards FidoCad (!)
	No pun intended :-) This is useful because we can split macros very easily.
	
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

	Copyright 2008-2014 by Davide Bucci
</pre>

    
    @author Davide Bucci
*/

public class ExportFidoCad implements ExportInterface {

	private final BufferedWriter out;
	private Vector<LayerDesc> layerV;
	private boolean extensions;		// use FidoCadJ extensions
	private boolean splitStandardMacros; // Split also the standard macros
	private String textFont;
	private int textFontSize;

	public void setMacroFont(String f, int size)
	{
		textFont = f;
		textFontSize = size;
	}

	public void setSplitStandardMacros(boolean s)
	{
		splitStandardMacros = s;
	}
	
	/** double to integer conversion. In some cases, some processing might be
		applied.
	*/
	public int cLe(double l)
	{
		return (int)l;
	}
		
	/** Constructor
	
		@param f the File object in which the export should be done.
		
	*/
	
	public ExportFidoCad (File f) throws IOException
	{	
		extensions = true;
		splitStandardMacros=false;
		textFont=Globals.defaultTextFont;
		textFontSize=3;
		OutputStreamWriter fstream = new OutputStreamWriter(
			new FileOutputStream(f), 
			Globals.encoding);
	    out = new BufferedWriter(fstream);
	}
	
	/** Specify whether the FidoCadJ extensions should be taken into account
	*/
	public void setExtensions(boolean e)
	{
		extensions=e;
	}
	
	/**	Called at the beginning of the export phase. Ideally, in this routine
		there should be the code to write the header of the file on which
		the drawing should be exported.
			
		@param totalSize the size of the image. Useful to calculate for example
		the	bounding box.
		@param la a vector describing the attributes of each layer.
		@param grid the grid size. This is useful when exporting to another 
			drawing program having some kind of grid concept. You might use
			this value to synchronize FidoCadJ's grid with the one used by
			the target.
	*/
	
	public void exportStart(DimensionG totalSize, Vector<LayerDesc> la,
		int grid)   
		throws IOException
	{ 
		// We need to save layers informations, since we will use them later.
		
		layerV=la;
	    int wi=totalSize.width;
	    int he=totalSize.height;
	    
		out.write("[FIDOCAD]\n");
		
		DrawingModel P = new DrawingModel();
		ParserActions pa = new ParserActions(P);
		P.setLayers(la);
		out.write(new String(pa.registerConfiguration(extensions)));		
	} 
	

	/** Called at the end of the export phase.
	*/
	public void exportEnd() 
		throws IOException
	{ 
		out.close();
	}

	/** Called when exporting an Advanced Text primitive.
	
		@param x the x position of the beginning of the string to be written
		@param y the y position of the beginning of the string to be written
		@param sizex the x size of the font to be used
		@param sizey the y size of the font to be used
		@param fontname the font to be used
		@param isBold true if the text should be written with a boldface font
		@param isMirrored true if the text should be mirrored
		@param isItalic true if the text should be written with an italic font
		@param orientation angle of orientation (degrees)
		@param layer the layer that should be used
		@param text the text that should be written
	*/
	
	public void exportAdvText (int x, int y, int sizex, int sizey,
		String fontname, boolean isBold, boolean isMirrored, boolean isItalic,
		int orientation, int layer, String text) 
		throws IOException
	{ 
		int style=0;
		
		if (isBold)
			style+=1;
		if (isItalic)
			style+=2;
		if (isMirrored)
			style+=4;
			
		out.write(new PrimitiveAdvText(cLe(x),
			cLe(y), 
			cLe(sizex), cLe(sizey), fontname, 
			orientation, style,text, layer).toString(extensions)); 
		
	}
	
	/** Called when exporting a Bézier primitive.
	
		@param x1 the x position of the first point of the trace
		@param y1 the y position of the first point of the trace
		@param x2 the x position of the second point of the trace
		@param y2 the y position of the second point of the trace
		@param x3 the x position of the third point of the trace
		@param y3 the y position of the third point of the trace
		@param x4 the x position of the fourth point of the trace
		@param y4 the y position of the fourth point of the trace
		@param layer the layer that should be used
		
				// from 0.22.1
		
		@param arrowStart specify if an arrow is present at the first point
		@param arrowEnd specify if an arrow is present at the second point
		@param arrowLength total lenght of arrows (if present)
		@param arrowHalfWidth half width of arrows (if present)
		@param dashStyle dashing style
		
	*/
	public void exportBezier (int x1, int y1,
		int x2, int y2,
		int x3, int y3,
		int x4, int y4,
		int layer,
		boolean arrowStart, 
		boolean arrowEnd, 
		int arrowStyle, 
		int arrowLength, 
		int arrowHalfWidth, 
		int dashStyle,
		double strokeWidth)
		throws IOException	
	{ 
		out.write(new PrimitiveBezier(cLe(x1),
			cLe(y1), cLe(x2),
			cLe(y2), cLe(x3),
			cLe(y3), cLe(x4),
			cLe(y4), layer,arrowStart,
			arrowEnd,arrowStyle,
			cLe(arrowLength),
			cLe(arrowHalfWidth),
			dashStyle,
			textFont, textFontSize).toString(extensions));			
	}
	
	/** Called when exporting a Connection primitive.
	
		@param x the x position of the position of the connection
		@param y the y position of the position of the connection
		
		@param layer the layer that should be used
	*/
	public void exportConnection (int x, int y, int layer, double size) 
		throws IOException
	{ 
		out.write(new PrimitiveConnection(cLe(x),
			cLe(y),layer,
			textFont, textFontSize).toString(extensions));
	}
		
	/** Called when exporting a Line primitive.
	
		@param x1 the x position of the first point of the segment
		@param y1 the y position of the first point of the segment
		@param x2 the x position of the second point of the segment
		@param y2 the y position of the second point of the segment
		
		@param layer the layer that should be used
		
		// from 0.22.1
		
		@param arrowStart specify if an arrow is present at the first point
		@param arrowEnd specify if an arrow is present at the second point
		@param arrowLength total lenght of arrows (if present)
		@param arrowHalfWidth half width of arrows (if present)
		@param dashStyle dashing style
		
	*/
	
	public void exportLine (double x1, double y1,
		double x2, double y2,
		int layer,
		boolean arrowStart, 
		boolean arrowEnd, 
		int arrowStyle, 
		int arrowLength, 
		int arrowHalfWidth, 
		int dashStyle,
		double strokeWidth)
		throws IOException
	{ 
		out.write(new PrimitiveLine(cLe(x1),cLe(y1),
			cLe(x2),cLe(y2),layer,arrowStart,
			arrowEnd,arrowStyle,
			cLe(arrowLength),
			cLe(arrowHalfWidth),
			dashStyle,
			textFont, textFontSize).toString(extensions));
	}
	
	/** Called when exporting a Macro call.
		This function can just return false, to indicate that the macro should 
		be rendered by means of calling other primitives. Please note that 
		a macro does not have a reference layer, since it is defined by its
		components.
		
		@param x the x position of the position of the macro
		@param y the y position of the position of the macro
		@param isMirrored true if the macro is mirrored
		@param orientation the macro orientation in degrees
		@param macroName the macro name
		@param macroDesc the macro description, in the FidoCad format
		@param name the shown name
		@param xn coordinate of the shown name
		@param yn coordinate of the shown name
		@param value the shown value
		@param xv coordinate of the shown value
		@param yv coordinate of the shown value
		@param font the used font
		@param fontSize the size of the font to be used
		@param m the library
	*/
	public boolean exportMacro(int x, int y, boolean isMirrored, 
		int orientation, String macroName, String macroDesc,
		String name, int xn, int yn, String value, int xv, int yv, String font,
		int fontSize, Map<String, MacroDesc> m)
		throws IOException
	{
		if(splitStandardMacros)
			return false;
			
		boolean isStandard=false;
		int dotpos=-1;
		
		// A first way to determine if a macro is standard is to see if its
		// name does not contains a dot (original FidoCAD standard library)
		
		if ((dotpos=macroName.indexOf("."))<0) { 
			isStandard = true;
		} else {
			// If the name contains a dot, we might check whether we have 
			// one of the new FidoCadJ standard libraries:
			// pcb, ihram, elettrotecnica.
			
			// Obtain the library name
			String library=macroName.substring(0,dotpos);
			
			// Check it
			if(extensions && "pcb".equals(library)) { 
				isStandard = true;
			} else if (extensions && "ihram".equals(library)) {
				isStandard = true;
			} else if (extensions && "elettrotecnica".equals(library)) {
				isStandard = true;
			}
		}
			
		// If the library is standard, we output the symbol just with a
		// single MC command.
		if(isStandard) {
			out.write(new PrimitiveMacro(m, layerV, cLe(x), 
				cLe(y), macroName, 
		    	name, cLe(xn), cLe(yn), value, 
		    	cLe(xv), cLe(yv), font, 
		    	cLe(fontSize), orientation/90, 
		    	isMirrored).toString(extensions));
			return true;
		} 
		// If it is not standard, the macro will be expanded into primitives.
		return false; 
	}
	
	

	/** Called when exporting an Oval primitive. Specify the bounding box.
			
		@param x1 the x position of the first corner
		@param y1 the y position of the first corner
		@param x2 the x position of the second corner
		@param y2 the y position of the second corner
		@param isFilled it is true if the oval should be filled
		
		@param layer the layer that should be used
		@param dashStyle dashing style

	*/	
	public void exportOval(int x1, int y1, int x2, int y2,
		boolean isFilled, int layer, int dashStyle, double strokeWidth)
		throws IOException
	{ 
		out.write(new PrimitiveOval(cLe(x1), 
			cLe(y1), cLe(x2), cLe(y2), 
			isFilled, layer, dashStyle,
			textFont, textFontSize).toString(extensions));
	}
		
	/** Called when exporting a PCBLine primitive.
	
		@param x1 the x position of the first point of the segment
		@param y1 the y position of the first point of the segment
		@param x2 the x position of the second point of the segment
		@param y2 the y position of the second point of the segment
		@param width the width ot the line
		@param layer the layer that should be used
	*/
	public void exportPCBLine(int x1, int y1, int x2, int y2, int width, 
		int layer) 
		throws IOException
	{ 
		out.write(new PrimitivePCBLine(cLe(x1),
			cLe(y1),cLe(x2),cLe(y2),
			cLe(width), cLe(layer),
			textFont, textFontSize).toString(extensions));
	}
		
	
	/** Called when exporting a PCBPad primitive.
	
		@param x the x position of the pad 
		@param y the y position of the pad
		@param style the style of the pad (0: oval, 1: square, 2: rounded 
			square)
		@param six the x size of the pad
		@param siy the y size of the pad
		@param indiam the hole internal diameter
		@param layer the layer that should be used
		@param onlyHole export only the hole
	*/
	
	public void exportPCBPad(int x, int y, int style, int six, int siy, 
		int indiam, int layer, boolean onlyHole) 
		throws IOException
	{ 
		if(!onlyHole)
			out.write(new PrimitivePCBPad(cLe(x),
				cLe(y),cLe(six),
				cLe(siy),cLe(indiam), style,
				layer,
				textFont, textFontSize).toString(extensions));	
	}
	/**	Called when exporting a Polygon primitive
	
		@param vertices array containing the position of each vertex
		@param nVertices number of vertices
		@param isFilled true if the polygon is filled
		@param layer the layer that should be used
		@param dashStyle dashing style

	
	*/
	public void exportPolygon(PointDouble[] vertices, int nVertices, 
		boolean isFilled, int layer, int dashStyle, double strokeWidth)
		throws IOException
	{ 
		
		PrimitivePolygon p=new PrimitivePolygon(isFilled, layer, dashStyle,
			textFont, textFontSize);
		
		for (int i=0; i <nVertices; ++i) {
			p.addPoint(cLe(vertices[i].x), 
				cLe(vertices[i].y));
		
		}
		out.write(p.toString(extensions));
	}
	/**	Called when exporting a Curve primitive
	
		@param vertices array containing the position of each vertex
		@param nVertices number of vertices
		@param isFilled true if the polygon is filled
		@param isClosed true if the curve is closed
		@param layer the layer that should be used
		@param dashStyle dashing style
		@param strokeWidth the width of the pen to be used when drawing
		
		@return false if the curve should be rendered using a polygon, true
			if it is handled by the function.
	*/
	public boolean exportCurve(PointDouble[] vertices, int nVertices, 
		boolean isFilled, boolean isClosed, int layer, 
		boolean arrowStart, 
		boolean arrowEnd, 
		int arrowStyle, 
		int arrowLength, 
		int arrowHalfWidth, 
		int dashStyle,
		double strokeWidth)
		throws IOException
	{
		PrimitiveComplexCurve p=new PrimitiveComplexCurve(isFilled, isClosed,
			layer, 
			arrowStart,
			arrowEnd,arrowStyle,
			cLe(arrowLength),
			cLe(arrowHalfWidth),
			dashStyle,
			textFont, textFontSize);
		
		for (int i=0; i <nVertices; ++i) {
			p.addPoint(cLe(vertices[i].x), 
				cLe(vertices[i].y));
		}
		out.write(p.toString(extensions));
		
		return true;
	}
	
	/** Called when exporting a Rectangle primitive.
			
		@param x1 the x position of the first corner
		@param y1 the y position of the first corner
		@param x2 the x position of the second corner
		@param y2 the y position of the second corner
		@param isFilled it is true if the rectangle should be filled
		
		@param layer the layer that should be used
		@param dashStyle dashing style

	*/
	public void exportRectangle(int x1, int y1, int x2, int y2,
		boolean isFilled, int layer, int dashStyle, double strokeWidth)
		throws IOException
	{ 
		out.write(new PrimitiveRectangle(cLe(x1), 
			cLe(y1), cLe(x2), 
			cLe(y2), isFilled, 
			layer, dashStyle,
			textFont, textFontSize).toString(extensions));
	}
	
	/** Called when exporting an arrow.
		@param x position of the tip of the arrow
		@param y position of the tip of the arrow
		@param xc direction of the tip of the arrow
		@param yc direction of the tip of the arrow
		@param l length of the arrow
		@param h width of the arrow
		@param style style of the arrow
	*/
	public void exportArrow(double x, double y, double xc, double yc, 
		double l, double h, 
		int style)
		throws IOException
	{
		// Does nothing, since it will not be useful here.
	}
}