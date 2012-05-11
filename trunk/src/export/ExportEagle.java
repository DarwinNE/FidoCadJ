package export;

import java.awt.*;
import java.util.*;
import java.io.*;
import java.text.*;

import globals.*;
import layers.*;
import java.awt.geom.*;


/** 

<pre>
	Circuit export towards Cadsoft Eagle
	
	
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

public class ExportEagle implements ExportInterface {

	private File fileExp;
	private FileWriter fstream;
	private BufferedWriter out;
	private Vector layerV;
	private int numberPath;
	private Dimension dim;
	private int oldtextsize;
	private String macroList;
	private String junctionList;
		
	static final double text_stretch = 0.73;
	static final String EagleFidoLib = "FidoCadJLIB";
	static final String ExportFormatString = "####.####";
	
	// Conversion between FidoCadJ units and Eagle units (1/10 inches)
	
	static double res=5e-2;
	

	public int cLe(double l)
	{
		return (int)l;
	}
	
	
	/** Constructor
	
		@param f the File object in which the export should be done.
		
	*/
	
	public ExportEagle (File f) throws IOException
	{
		fileExp=f;
		macroList = "";	
		junctionList = "";
		fstream = new FileWriter(fileExp);
    
		
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
	
	public void exportStart(Dimension totalSize, Vector la, int grid)   
		throws IOException
	{ 
		
		// We need to save layers informations, since we will use them later.
		
		layerV=la;
		dim=totalSize;
	    out = new BufferedWriter(fstream);
	    res = 5e-2;
	    numberPath=0;
		oldtextsize=-1;
		macroList = "";
		junctionList = "";
	    // A basic configuration of an Eagle script
	    
    	out.write("# Created by FidoCadJ "+Globals.version+
			" by Davide Bucci\n");
		out.write("Set Wire_Bend 2; \n");
		out.write("Grid inch "+een(grid*res)+";\n");
		out.write("Change font fixed;\n");
		out.write("Set auto_junction off;\n");


	} 
	
	/** Called at the end of the export phase.
	*/
	public void exportEnd() 
		throws IOException
	{ 
		out.write(macroList);
		out.write(junctionList);
		out.write("Window Fit; \n");
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
		LayerDesc l=(LayerDesc)layerV.get(layer);
		Color c=l.getColor();
		String mirror="";
		
		if(isMirrored) {
			mirror="M";
		}
		
		if(oldtextsize!=sizey)
			out.write("Change size "+sizey*res*text_stretch+"\n");
		oldtextsize=sizey;
		
		out.write("Text "+text+" "+mirror+"R"+(-orientation)+" ("+een(x*res)+" " 
			+een((dim.height-y)*res)+");\n");

			
		
	}
	
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
		throws IOException	
	{ 
		LayerDesc l=(LayerDesc)layerV.get(layer);
		Color c=l.getColor();
		
		out.write("# B�zier export not implemented yet\n");


	}
	
	/** Called when exporting a Connection primitive.
	
		@param x the x position of the position of the connection
		@param y the y position of the position of the connection
		
		@param layer the layer that should be used
	*/
	public void exportConnection (int x, int y, int layer, double size) 
		throws IOException
	{ 
		LayerDesc l=(LayerDesc)layerV.get(layer);
		Color c=l.getColor();
		junctionList += "Junction ("+een(x*res)+" "
			+een((dim.height-y)*res)+");\n";

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
		throws IOException
	{ 
		LayerDesc l=(LayerDesc)layerV.get(layer);
		Color c=l.getColor();
		
		//out.write("Layer "+layer);
		out.write("Net ("+een(x1*res)+" "+een((dim.height-y1)*res)+") ("+
			een(x2*res)+" "+een((dim.height-y2)*res)+");\n");
		
	}
	
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
		throws IOException
	{
		String mirror ="";
		if (isMirrored)
			mirror = "M";
		
		// The component name should not contain spaces. Substitute with
		// the underline character.
		Map subst = new HashMap();
		subst.put(" ","_");
		name=Globals.substituteBizarreChars(name, subst);
		
		macroList += "Add "+ macroName+"@"+EagleFidoLib+ " "+name+" "+mirror+"R"
			+(-orientation)+" ("+een(x*res)+" "+een((dim.height-y)*res)+");\n";

		macroList +="Value "+name+" "+value+";\n";
		
		// The macro will NOT be expanded into primitives.
		return true; 
	}
	
	
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
		throws IOException
	{ 
		LayerDesc l=(LayerDesc)layerV.get(layer);
		Color c=l.getColor();
		String fill_pattern="";
		
		//out.write("Layer "+layer);
		out.write("# Circle export not fully implemented\n");

		out.write("Circle ("+een(x1*res)+" "+een((dim.height-y1)*res)+") (" 
			+een((x2-x1)*res)+ " "+een((y2-y1)*res)+");");
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
		LayerDesc l=(LayerDesc)layerV.get(layer);
		Color c=l.getColor();
		
		out.write("# PCBLine export not implemented yet\n");

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
	*/
	
	public void exportPCBPad(int x, int y, int style, int six, int siy, 
		int indiam, int layer, boolean onlyHole) 
		throws IOException
	{ 
		double xdd;
		double ydd;
		
		LayerDesc l=(LayerDesc)layerV.get(layer);
		Color c=l.getColor();
		
		// At first, draw the pad...
		if(!onlyHole) {
			switch (style) {
				default:
				case 0: // Oval pad

					 break;
				case 1:	// Square pad
	
					break;
				case 2:	// Rounded pad

					break;
			}
		}
		// ... then, drill the hole!
		out.write("# PCBpad export not implemented yet\n");

	}
	
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
		throws IOException
	{ 
		LayerDesc l=(LayerDesc)layerV.get(layer);
		Color c=l.getColor();
		String fill_pattern="";
		
		/*if(isFilled) {
			fill_pattern="fill=\"#"+
				  convertToHex2(c.getRed())+
				  convertToHex2(c.getGreen())+
				  convertToHex2(c.getBlue())+"\"";
		} else {
			fill_pattern="fill=\"none\"";
		
		}*
		int i;
		
		//LayerDesc l=(LayerDesc)layerV.get(layer);
		out.write("POLYGON ");
		for (i=0; i<nVertices; ++i) {
			out.write("("+vertices[i].x+" "+vertices[i].y+") ");
		
		}
		out.write(";\n");
		
		*/
		
		out.write("# Polygon export not implemented yet\n");

	}
		
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
		throws IOException
	{ 
		
		LayerDesc l=(LayerDesc)layerV.get(layer);
		Color c=l.getColor();
		
		out.write("Layer 94;\n");
		
		if(isFilled) {
			out.write("Rect ("+een(x1*res)+" "+een((dim.height-y1)*res)+
				  ") ("+een(x2*res)+" "+een((dim.height-y2)*res)+");\n");
		} else {
			out.write("Set Wire_Bend 0;\n");
			out.write("Wire ("+een(x1*res)+" "+een((dim.height-y1)*res)+") ("+
			een(x2*res)+" "+een((dim.height-y2)*res)+");\n");
			out.write("Wire ("+een(x2*res)+" "+een((dim.height-y2)*res)+") ("+
			een(x1*res)+" "+een((dim.height-y1)*res)+");\n");
			out.write("Set Wire_Bend 2;\n");
		}
		
		out.write("Layer 91;\n");
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
	public boolean exportCurve(Point2D.Double[] vertices, int nVertices, 
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
		return false;
	}
	
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
		throws IOException
	{
		// Does nothing, since it will not be useful here.
		return;
	}

	/**	Export a number: truncate it to four decimals
	
	*/
	private String een(double n)
	{
		// Force the Java system to use ALWAYS the dot as a decimal separator,
		// regardless the locale settings (in Italy and France, the
		// decimal separator is the comma).
		
		DecimalFormatSymbols separators = new DecimalFormatSymbols();
		separators.setDecimalSeparator('.');

        DecimalFormat exportFormat = new DecimalFormat(ExportFormatString,
        	separators);
        return exportFormat.format(n);
        
	}
}