package export;

import java.awt.*;
import java.util.*;
import java.io.*;
import java.awt.geom.*;

/** ExportInterface.java 

	Interface which allows to export a FidoCad draw under an arbitrary format.
	The primitive handling system of FidoCadJ will call each primitive export
	function and provide every informations about the primitive state.
	
	Each coordinate is given in FidoCadJ coordinate space, which means that 
	normally one unit corresponds to 5 mils (127 microns).
	
	I do not consider the export phase as a speed sensitive context. For this
	reason, I try to write that interface in order to achieve the maximum ease
	of use of the various parameters involving each primitive. 


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

	Copyright 2008-2012 by Davide Bucci
   </pre>

    
    @author Davide Bucci
*/

public interface ExportInterface {

	
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
		@param sizeMagnification is the factor to which every coordinate in a 
			vector drawing should be multiplicated.
	*/
	
	public void exportStart(Dimension totalSize, Vector la, int grid)  
		throws IOException;
	
	/** Called at the end of the export phase.
	*/
	public void exportEnd()
		throws IOException;

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
		throws IOException;
	
	/** Called when exporting a B�zier primitive.
	
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
		@param strokeWidth the width of the pen to be used when drawing
		
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
		throws IOException;
	
	/** Called when exporting a Connection primitive.
	
		@param x the x position of the position of the connection
		@param y the y position of the position of the connection
		
		@param layer the layer that should be used
	*/
	public void exportConnection (int x, int y, int layer, double size)
		throws IOException;
		
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
		@param strokeWidth the width of the pen to be used when drawing

		
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
		throws IOException;
		
	
	/** Called when exporting a Macro call.
		This function can just return false, to indicate that the macro should 
		be rendered by means of calling the other primitives. Please note that 
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
		int fontSize, Map m)
		throws IOException;
	
	/** Called when exporting an Oval primitive. Specify the bounding box.
			
		@param x1 the x position of the first corner
		@param y1 the y position of the first corner
		@param x2 the x position of the second corner
		@param y2 the y position of the second corner
		@param isFilled it is true if the oval should be filled
		
		@param layer the layer that should be used
		@param dashStyle dashing style
		@param strokeWidth the width of the pen to be used when drawing


	*/	
	public void exportOval(int x1, int y1, int x2, int y2,
		boolean isFilled, int layer, int dashStyle, double strokeWidth)
		throws IOException;
		
	/** Called when exporting a PCBLine primitive.
	
		@param x1 the x position of the first point of the segment
		@param y1 the y position of the first point of the segment
		@param x2 the x position of the second point of the segment
		@param y2 the y position of the second point of the segment
		@param width the width ot the line
		@param layer the layer that should be used
	*/
	public void exportPCBLine(int x1, int y1, int x2, int y2, int width, 
		int layer) throws IOException;
		
	
	/** Called when exporting a PCBPad primitive.
	
		@param x the x position of the pad 
		@param y the y position of the pad
		@param style the style of the pad (0: oval, 1: square, 2: rounded 
			square)
		@param six the x size of the pad
		@param siy the y size of the pad
		@param indiam the hole internal diameter
		@param layer the layer that should be used
	*/
	
	public void exportPCBPad(int x, int y, int style, int six, int siy, 
		int indiam, int layer, boolean onlyHole) throws IOException;
	
	/**	Called when exporting a Polygon primitive
	
		@param vertices array containing the position of each vertex
		@param nVertices number of vertices
		@param isFilled true if the polygon is filled
		@param layer the layer that should be used
		@param dashStyle dashing style
		@param strokeWidth the width of the pen to be used when drawing


	
	*/
	public void exportPolygon(Point2D.Double[] vertices, int nVertices, 
		boolean isFilled, int layer, int dashStyle, double strokeWidth)
		throws IOException;
		
		
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
	public boolean exportCurve(Point2D.Double[] vertices, int nVertices, 
		boolean isFilled, boolean isClosed, int layer, 
		boolean arrowStart, 
		boolean arrowEnd, 
		int arrowStyle, 
		int arrowLength, 
		int arrowHalfWidth, 
		int dashStyle,
		double strokeWidth)
		throws IOException;	
		
	/** Called when exporting a Rectangle primitive.
			
		@param x1 the x position of the first corner
		@param y1 the y position of the first corner
		@param x2 the x position of the second corner
		@param y2 the y position of the second corner
		@param isFilled it is true if the rectangle should be filled
		
		@param layer the layer that should be used
		@param dashStyle dashing style
		@param strokeWidth the width of the pen to be used when drawing


	*/
	public void exportRectangle(int x1, int y1, int x2, int y2,
		boolean isFilled, int layer, int dashStyle, double strokeWidth)
		throws IOException;
		
	/** Called when exporting an arrow.
		@param x 
		@param y
		@param xc
		@param yc
		@param l
		@param h
		@param style
	*/
	public void exportArrow(double x, double y, double xc, double yc, 
		double l, double h, 
		int style)
		throws IOException;

}