package export;

import java.awt.*;
import java.util.*;
import java.io.*;
import java.text.*;
import globals.*;
import layers.*;

/** 

   Written by Davide Bucci, davbucci at tiscali dot it
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

    
    @author Davide Bucci
    @version 1.0, June 2008
*/

public class ExportFidoCad implements ExportInterface {

	private File fileExp;
	private FileWriter fstream;
	private BufferedWriter out;
	private Vector layerV;
	private int numberPath;
	private int xsize;
	private int ysize;
	
	
	static final int NODE_SIZE = 1;
	static final double l_width=.33;
	
	/** Constructor
	
		@param f the File object in which the export should be done.
		
	*/
	
	public ExportFidoCad (File f) throws IOException
	{
		fileExp=f;
		
		fstream = new FileWriter(fileExp);
    
		
	}
	
	/**	Called at the beginning of the export phase. Ideally, in this routine
		there should be the code to write the header of the file on which
		the drawing should be exported.
			
		@param totalSize the size of the image. Useful to calculate for example
		the	bounding box.
		@param la a LayerDesc vector describing the attributes of each 
		layer.
		@param grid the grid size
	*/
	
	public void exportStart(Dimension totalSize, Vector la, int grid)  
		throws IOException
	{ 
		
		// We need to save layers informations, since we will use them later.
		
		layerV=la;
	    out = new BufferedWriter(fstream);
	    numberPath=0;
	    	    
	    int wi=totalSize.width;
	    int he=totalSize.height;
	    
		out.write("[FIDOCAD]\n");
			
		
		
	} 
	
	/** Called at the end of the export phase.
	*/
	public void exportEnd() 
		throws IOException
	{ 
		out.write("%%EOF\n");
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
		String bold=""; 
		
		if(isBold)
			bold="-Bold";
        
		out.write("/"+fontname+bold+" findfont\n"+
			sizey+" scalefont\n"+
			"setfont\n");       
		out.write("newpath\n");

		out.write("" +x+" "+y+" moveto\n");
		out.write("gsave\n");
		
		if(orientation !=0) 
			out.write("  "+(isMirrored?orientation:-orientation)+" rotate\n");
		
		if(isMirrored)
			out.write("  -1 -1 scale\n");
		else
			out.write("  1 -1 scale\n");
		out.write("  0 "+(int)(-(double)sizey/1.38)+" rmoveto\n");
		// Remember that we consider sizex/sizey=7/12 as the "normal" aspect 
		// ratio.
		
		out.write("  "+(sizex/22.0/sizey*40.0)+" 1 scale\n");
		out.write("  "+c.getRed()/255.0+" "+c.getGreen()/255.0+ " "
			+c.getBlue()/255.0+	" setrgbcolor\n");
		
		Map subst = new HashMap();
		subst.put("(","\\050");
		subst.put(")","\\051");
		text=Globals.substituteBizarreChars(text, subst);
			
		out.write("  ("+text+") show\n");
		out.write("grestore\n");
		
	
			
		
	}
	
	/** Called when exporting a B@zier primitive.
	
		@param x1 the x position of the first point of the trace
		@param y1 the y position of the first point of the trace
		@param x2 the x position of the second point of the trace
		@param y2 the y position of the second point of the trace
		@param x3 the x position of the third point of the trace
		@param y3 the y position of the third point of the trace
		@param x4 the x position of the fourth point of the trace
		@param y4 the y position of the fourth point of the trace
		@param layer the layer that should be used
	*/
	public void exportBezier (int x1, int y1,
		int x2, int y2,
		int x3, int y3,
		int x4, int y4,
		int layer) 
		throws IOException	
	{ 
		LayerDesc l=(LayerDesc)layerV.get(layer);
		Color c=l.getColor();
		out.write(""+c.getRed()/255.0+" "+c.getGreen()/255.0+ " "
			+c.getBlue()/255.0+	" setrgbcolor\n");
		out.write(""+l_width+" setlinewidth\n");
				  
		out.write(""+x1+" "+y1+" moveto \n");
		out.write(""+x2+" "+y2+" "+x3+" "+y3+" "+x4+" "+y4+" curveto stroke\n");
	}
	
	/** Called when exporting a Connection primitive.
	
		@param x the x position of the position of the connection
		@param y the y position of the position of the connection
		
		@param layer the layer that should be used
	*/
	public void exportConnection (int x, int y, int layer) 
		throws IOException
	{ 
		LayerDesc l=(LayerDesc)layerV.get(layer);
		Color c=l.getColor();

		out.write(""+c.getRed()/255.0+" "+c.getGreen()/255.0+ " "
			+c.getBlue()/255.0+	" setrgbcolor\n");
		out.write(""+l_width+" setlinewidth\n");

		out.write("newpath\n");
		out.write(""+x+" "+y+" "+
			NODE_SIZE/2.0+ " " + NODE_SIZE/2.0+ 
			" 0 360 ellipse\n");
		out.write("fill\n");	
	}
		
	/** Called when exporting a Line primitive.
	
		@param x1 the x position of the first point of the segment
		@param y1 the y position of the first point of the segment
		@param x2 the x position of the second point of the segment
		@param y2 the y position of the second point of the segment
		
		@param layer the layer that should be used
	*/
	public void exportLine (int x1, int y1,
		int x2, int y2,
		int layer) 
		throws IOException
	{ 
		LayerDesc l=(LayerDesc)layerV.get(layer);
		Color c=l.getColor();
		out.write(""+c.getRed()/255.0+" "+c.getGreen()/255.0+ " "
			+c.getBlue()/255.0+	" setrgbcolor\n");
		out.write(""+l_width+" setlinewidth\n");
				  
		out.write(""+x1+" "+y1+" moveto "+
			x2+" "+y2+" lineto stroke\n");
		
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
	*/
	public boolean exportMacro(int x, int y, boolean isMirrored, 
		int orientation, String macroName, String macroDesc,
		String name, int xn, int yn, String value, int xv, int yv)
		throws IOException
	{
		// The macro will be expanded into primitives.
		return false; 
	}
	
	
	/** Called when exporting an Oval primitive. Specify the bounding box.
			
		@param x1 the x position of the first corner
		@param y1 the y position of the first corner
		@param x2 the x position of the second corner
		@param y2 the y position of the second corner
		@param isFilled it is true if the oval should be filled
		
		@param layer the layer that should be used
	*/	
	public void exportOval(int x1, int y1, int x2, int y2,
		boolean isFilled, int layer) 
		throws IOException
	{ 
		LayerDesc l=(LayerDesc)layerV.get(layer);
		Color c=l.getColor();
		out.write(""+c.getRed()/255.0+" "+c.getGreen()/255.0+ " "
			+c.getBlue()/255.0+	" setrgbcolor\n");
		out.write(""+l_width+" setlinewidth\n");

		out.write("newpath\n");
		out.write(""+(x1+x2)/2.0+" "+(y1+y2)/2.0+" "+
			Math.abs(x2-x1)/2.0+ " " + Math.abs(y2-y1)/2.0+ 
			" 0 360 ellipse\n");
		if(isFilled) {
			out.write("fill\n");	
		} else {
			out.write("stroke\n");	
		}
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
		
		out.write(""+c.getRed()/255.0+" "+c.getGreen()/255.0+ " "
			+c.getBlue()/255.0+	" setrgbcolor\n");
		out.write(""+width+" setlinewidth\n");
		out.write("1 setlinecap\n");
		out.write(""+x1+" "+y1+" moveto "+
			x2+" "+y2+" lineto stroke\n");
		
		out.write(""+l_width+" setlinewidth\n");
/*
		out.write("newpath\n");
		out.write(""+x1+" "+y1+" "+
			width/2.0+ " " +width/2.0+ 
			" 0 360 ellipse\n");
		out.write("fill\n");	
		out.write("newpath\n");
		out.write(""+x2+" "+y2+" "+
			width/2.0+ " " +width/2.0+ 
			" 0 360 ellipse\n");
		out.write("fill\n");	*/
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
		
		out.write(""+c.getRed()/255.0+" "+c.getGreen()/255.0+ " "
			+c.getBlue()/255.0+	" setrgbcolor\n");
		out.write(""+l_width+" setlinewidth\n");
		
		// At first, draw the pad...
		if(!onlyHole) {
			switch (style) {
				default:
				case 0: // Oval pad
					out.write("newpath\n");
					out.write(""+x+" "+y+" "+
						six/2.0+ " " +siy/2.0+ 
						" 0 360 ellipse\n");
					out.write("fill\n");	

					 break;
				case 2: // Remove when the rounded pad is implemented
				case 1:	// Square pad
					xdd=((double)x-six/2.0);
					ydd=((double)y-siy/2.0);
					out.write("newpath\n");
					out.write(""+xdd+" "+ydd+" moveto\n");
					out.write(""+(xdd+six)+" "+ydd+" lineto\n");
					out.write(""+(xdd+six)+" "+(ydd+siy)+" lineto\n");
					out.write(""+xdd+" "+(ydd+siy)+" lineto\n");
					out.write("closepath\n");	
					out.write("fill\n");	
					
					break;
				/*case 2:	// Rounded pad
					xdd=((double)x-six/2.0);
					ydd=((double)y-siy/2.0);
					double radius=2.5;
					
					
					break;*/
			}
		}		
			// ... then, drill the hole!
		
		out.write("1 1 1 setrgbcolor\n");
		out.write("newpath\n");
		out.write(""+x+" "+y+" "+
					indiam/2.0+ " " +indiam/2.0+ 
					" 0 360 ellipse\n");
		out.write("fill\n");
				
	
	}
	
	/**	Called when exporting a Polygon primitive
	
		@param vertices array containing the position of each vertex
		@param nVertices number of vertices
		@param isFilled true if the polygon is filled
		@param layer the layer that should be used
	
	*/
	public void exportPolygon(Point[] vertices, int nVertices, 
		boolean isFilled, int layer) 
		throws IOException
	{ 
		LayerDesc l=(LayerDesc)layerV.get(layer);
		Color c=l.getColor();
		String fill_pattern="";
		
		if (nVertices<1)
			return;
		
		out.write(""+c.getRed()/255.0+" "+c.getGreen()/255.0+ " "
			+c.getBlue()/255.0+	" setrgbcolor\n");
		out.write(""+l_width+" setlinewidth\n");
		
		out.write("newpath\n");
		
			
		out.write(""+vertices[0].x+" "+vertices[0].y+" moveto\n");
		
		for (int i=1; i<nVertices; ++i) 
			out.write(""+vertices[i].x+" "+vertices[i].y+" lineto\n");
		
		out.write("closepath\n");
		if(isFilled) {
			out.write("fill\n");	
		} else {
			out.write("stroke\n");	
		}
		
	
	}
		
	/** Called when exporting a Rectangle primitive.
			
		@param x1 the x position of the first corner
		@param y1 the y position of the first corner
		@param x2 the x position of the second corner
		@param y2 the y position of the second corner
		@param isFilled it is true if the rectangle should be filled
		
		@param layer the layer that should be used
	*/
	public void exportRectangle(int x1, int y1, int x2, int y2,
		boolean isFilled, int layer) 
		throws IOException
	{ 
		
		LayerDesc l=(LayerDesc)layerV.get(layer);
		Color c=l.getColor();
		
		out.write(""+c.getRed()/255.0+" "+c.getGreen()/255.0+ " "
			+c.getBlue()/255.0+	" setrgbcolor\n");
		out.write(""+l_width+" setlinewidth\n");
		
		out.write("newpath\n");
		out.write(""+x1+" "+y1+" moveto\n");
		out.write(""+x2+" "+y1+" lineto\n");
		out.write(""+x2+" "+y2+" lineto\n");
		out.write(""+x1+" "+y2+" lineto\n");
		out.write("closepath\n");
		if(isFilled) {
			out.write("fill\n");	
		} else {
			out.write("stroke\n");	
		}


	}
	
	
	

}