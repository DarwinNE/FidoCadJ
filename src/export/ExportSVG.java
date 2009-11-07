package export;

import java.awt.*;
import java.util.*;
import java.io.*;
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

public class ExportSVG implements ExportInterface {

	private File fileExp;
	private FileWriter fstream;
	private BufferedWriter out;
	private Vector layerV;
	private int numberPath;
	
	static final int NODE_SIZE = 1;
	static final double l_width=0.33;
	
	/** Constructor
	
		@param f the File object in which the export should be done.
		
	*/
	
	public ExportSVG (File f) throws IOException
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
	    
	    // A dumb, basic header of the SVG file
	    
    	out.write("<?xml version=\"1.0\" encoding=\"iso-8859-1\" " + 			
			"standalone=\"no\"?> \n<!DOCTYPE svg PUBLIC"+
			" \"-//W3C//Dtd SVG 1.1//EN\" " + "\"http://www.w3.org/Graphics/SVG/1.1/Dtd/svg11.dtd\">\n"+
			"<svg width=\""+wi+"\" height=\""+he+"\" version=\"1.1\" " + "xmlns=\"http://www.w3.org/2000/svg\" " +
			"xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n"+
			"<!-- Created by FidoCadJ ver. "+Globals.version+
			", export filter by Davide Bucci -->\n");
	} 
	
	/** Called at the end of the export phase.
	*/
	public void exportEnd() 
		throws IOException
	{ 
		out.write("</svg>");
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
		String path;
		
		/*  THIS VERSION OF TEXT EXPORT IS NOT COMPLETE! IN PARTICULAR, 
			MIRRORING EFFECTS, ANGLES AND A PRECISE SIZE CONTROL IS NOT
			HANDLED
		*/
		
		
	/*	
		if(isMirrored) {
			int dx=-x;
			int dy=0;
			
			++numberPath;
			path="M"+x+","+(y+sizey);
			path+="L"+(x+dx)+","+(y+sizey+dy);
			
			out.write("<path id=\"path"+numberPath+"\" d=\""+path+"\""+ 
			" style=\"stroke:red;fill:none;stroke-width:2\"/>\n");
		}
	*/	
		out.write("<text x=\""+x+"\" y=\""+(y+sizey)+"\" font-family=\""+
			fontname+"\" font-size=\""+sizey+"\" font-style=\""+
			(isItalic?"Italic":"")+"\" font-weigth=\""+
			(isBold?"bold":"")+"\" "+
			"fill=\"#"+
				convertToHex2(c.getRed())+
				convertToHex2(c.getGreen())+
				convertToHex2(c.getBlue())+"\""+
			
			">");
	/*	
		if(isMirrored)
			out.write("<textPath xlink:href=\"path"+numberPath+"\">");*/
		
		out.write(text);
	/*	
		if(isMirrored)
			out.write("</textPath>");
		
	*/		
		out.write("</text>\n");
			
		
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
		
		out.write("<path d=\"M "+x1+","+y1+" C "+
				  x2+ ","+y2+" "+x3+","+y3+" "+x4+","+y4+"\" "+
				  "style=\"stroke:#"+
				  convertToHex2(c.getRed())+
				  convertToHex2(c.getGreen())+
				  convertToHex2(c.getBlue())+";stroke-width:"+l_width+
				  "\" fill=\"none\"/>\n");
	
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
		
		out.write("<circle cx=\""+x+"\" cy=\""+y+"\""+
			" r=\""+NODE_SIZE+"\" style=\"stroke:#"+
				  convertToHex2(c.getRed())+
				  convertToHex2(c.getGreen())+
				  convertToHex2(c.getBlue())+";stroke-width:"+l_width+
				  "\" fill=\"#"+
				  convertToHex2(c.getRed())+
				  convertToHex2(c.getGreen())+
				  convertToHex2(c.getBlue())+"\"/>\n");
	
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
		
		out.write("<line x1=\""+x1+"\" y1=\""+y1+"\" x2=\""+
			x2+"\" y2=\""+y2+"\" style=\"stroke:#"+
				  convertToHex2(c.getRed())+
				  convertToHex2(c.getGreen())+
				  convertToHex2(c.getBlue())+";stroke-width:"+l_width+
				  "\"/>\n");
		
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
		//<ellipse cx="100" cy="100" rx="70" ry="50" style="fill:red"/>
		Color c=l.getColor();
		String fill_pattern="";
		
		if(isFilled) {
			fill_pattern="fill=\"#"+
				  convertToHex2(c.getRed())+
				  convertToHex2(c.getGreen())+
				  convertToHex2(c.getBlue())+"\"";
		} else {
			fill_pattern="fill=\"none\"";
		
		}
		 
		
		
		out.write("<ellipse cx=\""+(x1+x2)/2.0+"\" cy=\""+
				  (y1+y2)/2.0+
				  "\" rx=\""+Math.abs(x2-x1)/2.0+"\" ry=\""+
				  Math.abs(y2-y1)/2.0+"\" " 
				  + " style=\"stroke:#"+
				  convertToHex2(c.getRed())+
				  convertToHex2(c.getGreen())+
				  convertToHex2(c.getBlue())+";stroke-width:"+l_width+
				  "\" " + fill_pattern + "/>\n");
		
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
		
		out.write("<line x1=\""+x1+"\" y1=\""+y1+"\" x2=\""+
			x2+"\" y2=\""+y2+"\" style=\"stroke:#"+
				  convertToHex2(c.getRed())+
				  convertToHex2(c.getGreen())+
				  convertToHex2(c.getBlue())+";stroke-linejoin:round"+
				  ";stroke-width:"+width+
				  "\"/>\n");
		
		// Join the lines with small rounded corners
		
		out.write("<circle cx=\""+x1+"\" cy=\""+y1+"\""+
			" r=\""+width/2.0+"\" style=\"stroke:#"+
				  convertToHex2(c.getRed())+
				  convertToHex2(c.getGreen())+
				  convertToHex2(c.getBlue())+";stroke-width:"+l_width+
				  	"\" fill=\"#"+
				  convertToHex2(c.getRed())+
				  convertToHex2(c.getGreen())+
				  convertToHex2(c.getBlue())+ "\"/>\n");
		out.write("<circle cx=\""+x2+"\" cy=\""+y2+"\""+
			" r=\""+width/2.0+"\" style=\"stroke:#"+
				  convertToHex2(c.getRed())+
				  convertToHex2(c.getGreen())+
				  convertToHex2(c.getBlue())+";stroke-width:"+l_width+
				  	"\" fill=\"#"+
				  convertToHex2(c.getRed())+
				  convertToHex2(c.getGreen())+
				  convertToHex2(c.getBlue())+ "\"/>\n");
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
					out.write("<ellipse cx=\""+x+"\" cy=\""+y+"\""+
				  		" rx=\""+six/2.0+"\" ry=\""+siy/2.0+
				  		"\" style=\"stroke:#"+
				  		convertToHex2(c.getRed())+
					  	convertToHex2(c.getGreen())+
					  	convertToHex2(c.getBlue())+";stroke-width:"+l_width+
					  	"\" fill=\"#"+
					  	convertToHex2(c.getRed())+
					  	convertToHex2(c.getGreen())+
					  	convertToHex2(c.getBlue())+"\"/>\n");
					 break;
				case 1:	// Square pad
						xdd=((double)x-six/2.0);
						ydd=((double)y-siy/2.0);
					
						out.write("<rect x=\""+xdd+"\" y=\""+
					  		ydd+	"\" rx=\"0\" ry=\"0\" "+
					  		"width=\""+six+"\" height=\""+
					  		siy+"\" style=\"stroke:#"+
					  		convertToHex2(c.getRed())+
					  		convertToHex2(c.getGreen())+
					  		convertToHex2(c.getBlue())+
					  		";stroke-width:"+l_width+"\" fill=\"#"+
					 		convertToHex2(c.getRed())+
					  		convertToHex2(c.getGreen())+
					  		convertToHex2(c.getBlue())+"\"/>\n");
				
					break;
				case 2:	// Rounded pad
						xdd=((double)x-six/2.0);
						ydd=((double)y-siy/2.0);
						out.write("<rect x=\""+xdd+"\" y=\""+ydd+
					  		"\" rx=\"2.5\" ry=\"2.5\" "+
					  		"width=\""+six+"\" height=\""+
					  		siy+"\" style=\"stroke:#"+
					  		convertToHex2(c.getRed())+
					  		convertToHex2(c.getGreen())+
					  		convertToHex2(c.getBlue())+
					  		";stroke-width:"+l_width+"\" fill=\"#"+
					 		convertToHex2(c.getRed())+
					  		convertToHex2(c.getGreen())+
					  		convertToHex2(c.getBlue())+"\"/>\n");
					break;
			}
		}
		// ... then, drill the hole!
		out.write("<circle cx=\""+x+"\" cy=\""+y+"\""+
			" r=\""+indiam/2.0+"\" style=\"stroke:white;stroke-width:"+l_width+
				  	"\" fill=\"white\"/>\n");
		
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
		
		if(isFilled) {
			fill_pattern="fill=\"#"+
				  convertToHex2(c.getRed())+
				  convertToHex2(c.getGreen())+
				  convertToHex2(c.getBlue())+"\"";
		} else {
			fill_pattern="fill=\"none\"";
		
		}
		int i;
		
		//LayerDesc l=(LayerDesc)layerV.get(layer);
		out.write("<polygon points=\"");
		for (i=0; i<nVertices; ++i) {
			out.write(""+vertices[i].x+","+vertices[i].y+" ");
		
		}
		out.write("\" style=\"stroke:#"+
				  convertToHex2(c.getRed())+
				  convertToHex2(c.getGreen())+
				  convertToHex2(c.getBlue())+";stroke-width:"+l_width+
				  "\" " + fill_pattern + "/>\n");
		
		
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
		String fill_pattern="";
		
		if(isFilled) {
			fill_pattern="fill=\"#"+
				  convertToHex2(c.getRed())+
				  convertToHex2(c.getGreen())+
				  convertToHex2(c.getBlue())+"\"";
		} else {
			fill_pattern="fill=\"none\"";
		
		}
		
		out.write("<rect x=\""+Math.min(x1,x2)+"\" y=\""+
				  Math.min(y1,y2)+
				  "\" rx=\"0\" ry=\"0\" "+
				  "width=\""+Math.abs(x2-x1)+"\" height=\""+
				  Math.abs(y2-y1)+"\" style=\"stroke:#"+
				  convertToHex2(c.getRed())+
				  convertToHex2(c.getGreen())+
				  convertToHex2(c.getBlue())+";stroke-width:"+l_width+
				  "\" " + fill_pattern + "/>\n");
	}
	
	/** Just be sure that the HEX values are given with two digits...
		NOT a speed sensitive context.
	*/
	private String convertToHex2(int v)
	{
		String s=Integer.toHexString(v);
		if (s.length()==1)
			s="0"+s;
			
		return s;
	}
	

}