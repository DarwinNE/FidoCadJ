package export;

import java.awt.*;
import java.util.*;
import java.io.*;
import java.text.*;

import globals.*;
import layers.*;
import primitives.*;
import java.awt.geom.*;


/** 
	Drawing export in Encapsulated Postscript

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

	Copyright 2008-2010 by Davide Bucci
</pre>
    @author Davide Bucci
    @version 1.2, April 2010
*/

public class ExportEPS implements ExportInterface {

	private File fileExp;
	private FileWriter fstream;
	private BufferedWriter out;
	private Vector layerV;
	private int numberPath;
	private int xsize;
	private int ysize;
	private double actualWidth;
	private Color actualColor;
	private int actualDash;
	
	
	// Number of digits to be used when representing coordinates
	static final int PREC = 3;
	// Dash patterns
	static final String dash[]={"[5.0 10]", "[2.5 2.5]",
		"[1.0 1.0]", "[1.0 2.5]", "[1.0 2.5 2.5 2.5]"};
	
	public int cLe(double l)
	{
		return (int)l;
	}
	
	/** Constructor
	
		@param f the File object in which the export should be done.
		
	*/
	
	public ExportEPS (File f) throws IOException
	{
		fileExp=f;
		
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
	    out = new BufferedWriter(fstream);
	    numberPath=0;
	    	    
	    int wi=totalSize.width;
	    int he=totalSize.height;
	    
	    // An header of the EPS file
	    
	   	// 200 dpi is the internal resolution of FidoCadJ
	   	// 72 dpi is the internal resolution of the Postscript coordinates
	   	
	    double res_mult=200.0/72.0;
	    
	    //res_mult /= getMagnification();
	    
    	out.write("%!PS-Adobe-3.0 EPSF-3.0\n");
		out.write("%%Pages: 0\n");
		out.write("%%BoundingBox: -1 -1 "+(int)(totalSize.width/res_mult+1)+" "+
			(int)(totalSize.height/res_mult+1)+"\n");
		out.write("%%Creator: FidoCadJ "+Globals.version+
			", EPS export filter by Davide Bucci\n");
			
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		out.write("%%CreationDate: "+dateFormat.format(date)+"\n");
		out.write("%%EndComments\n");

		
		// Create a new dictionary term: ellipse
		// This is based on an example of the Blue Book
		// http://www.science.uva.nl/~robbert/ps/bluebook/program_03.html
		out.write("/ellipsedict 8 dict def\n"+
			"ellipsedict /mtrx matrix put\n"+
			"/ellipse\n"+
			"	{ ellipsedict begin\n"+
	  		"	  /endangle exch def\n"+
	  		"	  /startangle exch def\n"+
	  		"	  /yrad exch def\n"+
	  		"	  /xrad exch def\n"+
	  		"	  /y exch def\n"+
	  		"	  /x exch def\n"+
	 		"	  /savematrix mtrx currentmatrix def\n"+
	  		"	  x y translate\n"+
	  		"	  xrad yrad scale\n"+
	  		"	  0 0 1 startangle endangle arc\n"+
	  		"	  savematrix setmatrix\n"+
	  		"	  end\n"+
			"	} def\n");
			
		
		// Since in a postscript drawing, the origin is at the bottom left,
		// we introduce a coordinate transformation to have it at the top
		// left of the drawing.

		out.write("0 "+(totalSize.height/res_mult)+" translate\n");
		out.write(""+(1/res_mult)+" "+(-1/res_mult)+" scale\n");
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
        
        // It seems that Postscript fonts can not handle spaces. So I substitute
        // every space with a "-" sign.
        
        Map substFont = new HashMap();
		substFont.put(" ","-");
		fontname=Globals.substituteBizarreChars(fontname, substFont);
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
		
		checkColorAndWidth(c, 0.33);

		//out.write("  "+c.getRed()/255.0+" "+c.getGreen()/255.0+ " "
		//	+c.getBlue()/255.0+	" setrgbcolor\n");
		
		Map subst = new HashMap();
		subst.put("(","\\050");
		subst.put(")","\\051");
		text=Globals.substituteBizarreChars(text, subst);
			
		out.write("  ("+text+") show\n");
		out.write("grestore\n");
		
	
			
		
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
		checkColorAndWidth(c, strokeWidth);
		registerDash(dashStyle);
				  
		out.write(""+x1+" "+y1+" moveto \n");
		out.write(""+x2+" "+y2+" "+x3+" "+y3+" "+x4+" "+y4+" curveto stroke\n");
		if (arrowStart) exportArrow(x1, y1, x2, y2, arrowLength, 
			arrowHalfWidth, arrowStyle);
		if (arrowEnd) exportArrow(x4, y4, x3, y3, arrowLength, 
			arrowHalfWidth, arrowStyle);

	}
	
	/** Called when exporting a Connection primitive.
	
		@param x the x position of the position of the connection
		@param y the y position of the position of the connection
		
		@param layer the layer that should be used
	*/
	public void exportConnection (int x, int y, int layer, double node_size) 
		throws IOException
	{ 
		LayerDesc l=(LayerDesc)layerV.get(layer);
		Color c=l.getColor();

		checkColorAndWidth(c, 0.33);


		out.write("newpath\n");
		out.write(""+x+" "+y+" "+
			node_size/2.0+ " " + node_size/2.0+ 
			" 0 360 ellipse\n");
		out.write("fill\n");	
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
	public void exportLine (int x1, int y1,
		int x2, int y2,
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
		checkColorAndWidth(c, strokeWidth);
		registerDash(dashStyle);


				  
		out.write(""+x1+" "+y1+" moveto "+
			x2+" "+y2+" lineto stroke\n");
		if (arrowStart) exportArrow(x1, y1, x2, y2, arrowLength, 
			arrowHalfWidth, arrowStyle);
		if (arrowEnd) exportArrow(x2, y2, x1, y1, arrowLength, 
			arrowHalfWidth, arrowStyle);
	}
	
	private void exportArrow(int x, int y, int xc, int yc, int l, int h, 
		int style)
		throws IOException
	{
		double s;
		double alpha;
		double x0;
		double y0;
		double x1;
		double y1;
		double x2;
		double y2;
		
		// At first we need the angle giving the direction of the arrow
		// a little bit of trigonometry :-)
		
		if (x!=xc)
			alpha = Math.atan((double)(y-yc)/(double)(x-xc));
		else
			alpha = Math.PI/2.0+((y-yc<0)?0:Math.PI);
		
		alpha += (x-xc>0)?0:Math.PI;
		
		
	
		// Then, we calculate the points for the polygon
		x0 = x - l*Math.cos(alpha);
		y0 = y - l*Math.sin(alpha);
		
		x1 = x0 - h*Math.sin(alpha);
		y1 = y0 + h*Math.cos(alpha);
		
		x2 = x0 + h*Math.sin(alpha);
		y2 = y0 - h*Math.cos(alpha);
		
		// Arrows are always done with dash 0
		registerDash(0);

		out.write("newpath\n");

			
     	out.write(""+roundTo(x)+" "+	roundTo(y)+" moveto\n");
      	out.write(""+roundTo(x1)+" "+roundTo(y1)+" lineto\n");
      	out.write(""+roundTo(x2)+" "+roundTo(y2)+" lineto\n");
      	
		out.write("closepath\n");


        if ((style & Arrow.flagEmpty) == 0)
			out.write("fill \n");
 		else
			out.write("stroke \n");
 			
 		if ((style & Arrow.flagLimiter) != 0) {
 			double x3;
			double y3;
			double x4;
			double y4;
			x3 = x - h*Math.sin(alpha);
			y3 = y + h*Math.cos(alpha);
		
			x4 = x + h*Math.sin(alpha);
			y4 = y - h*Math.cos(alpha);
			out.write(""+roundTo(x3)+" "+roundTo(y3)+" moveto\n"+
				roundTo(x4)+" "+roundTo(y4)+" lineto\nstroke\n"); 
 		}
 		
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
		@param dashStyle dashing style
		@param strokeWidth the width of the pen to be used when drawing


	*/	
	public void exportOval(int x1, int y1, int x2, int y2,
		boolean isFilled, int layer, int dashStyle, double strokeWidth)
		throws IOException
	{ 
		LayerDesc l=(LayerDesc)layerV.get(layer);
		Color c=l.getColor();

		checkColorAndWidth(c, strokeWidth);
		registerDash(dashStyle);


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
		
		checkColorAndWidth(c, width);
		registerDash(0);

		out.write("1 setlinecap\n");
		out.write(""+x1+" "+y1+" moveto "+
			x2+" "+y2+" lineto stroke\n");
		
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
		
		checkColorAndWidth(c, 0.33);

		
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
				
				case 2: // Rounded pad 
					roundRect((x-six/2.0), (y-siy/2.0), 
							six, siy, 4, true);
					break;
				
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
			}
		}		
			// ... then, drill the hole!
		
		//out.write("1 1 1 setrgbcolor\n");
		checkColorAndWidth(Color.white, 0.33);

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
		
		if (nVertices<1)
			return;
		
		checkColorAndWidth(c, strokeWidth);
		registerDash(dashStyle);


		out.write("newpath\n");
		
			
		out.write(""+vertices[0].x+" "+vertices[0].y+" moveto\n");
		
		for (int i=1; i<nVertices; ++i) 
			out.write(""+((int)vertices[i].x)+" "+((int)vertices[i].y)+" lineto\n");
		
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
		@param dashStyle dashing style
		@param strokeWidth the width of the pen to be used when drawing


	*/
	public void exportRectangle(int x1, int y1, int x2, int y2,
		boolean isFilled, int layer, int dashStyle, double strokeWidth)
		throws IOException
	{ 
		
		LayerDesc l=(LayerDesc)layerV.get(layer);
		Color c=l.getColor();
	
		checkColorAndWidth(c, strokeWidth);
		registerDash(dashStyle);

		
		
		out.write("newpath\n");
		out.write(""+roundTo(x1,PREC)+" "+roundTo(y1,PREC)+" moveto\n");
		out.write(""+roundTo(x2,PREC)+" "+roundTo(y1,PREC)+" lineto\n");
		out.write(""+roundTo(x2,PREC)+" "+roundTo(y2,PREC)+" lineto\n");
		out.write(""+roundTo(x1,PREC)+" "+roundTo(y2,PREC)+" lineto\n");
		out.write("closepath\n");
		if(isFilled) {
			out.write("fill\n");	
		} else {
			out.write("stroke\n");	
		}


	}
	
	private String roundTo(double n, int ch)
	{
		return ""+ (((int)(n*Math.pow(10,ch)))/Math.pow(10,ch));
	}
	private String roundTo(double n)
	{
		int ch = 2;
		return ""+ (((int)(n*Math.pow(10,ch)))/Math.pow(10,ch));
	}
	
	private void roundRect (double x1, double y1, double w, double h, 
		double r, boolean filled)
		throws IOException
	{
		out.write(""+(x1+r) + " " +(y1)+" moveto\n");
		out.write(""+(x1+w-r) + " " +(y1)+" lineto\n");
		out.write(""+(x1+w) + " " +(y1)+" "+ (x1+w) + " "+(y1)
			+ " "+(x1+w) + " " +(y1+r)+ " curveto\n");
		
		out.write(""+(x1+w) + " " +(y1+h-r)+" lineto\n");
		out.write(""+(x1+w) + " " +(y1+h)+" "+(x1+w) + " " +(y1+h)+
		" "+(x1+w-r)+" "+(y1+h)+" curveto\n");
		
		out.write(""+ (x1+r) + " " +(y1+h)+" lineto\n");
		out.write(""+ x1+ " " +(y1+h)+" "+ x1+ " " +(y1+h)+
		" "+ (x1) + " " +(y1+h-r)+" curveto\n");
		
		out.write(""+ (x1) + " " +(y1+r)+" lineto\n");
		out.write(""+(x1) + " " +(y1)+" "+(x1) + " " +(y1)+" "+
			(x1+r)+" "+(y1)+" curveto\n");
		
		out.write("  "+(filled?"fill\n":"stroke\n"));
	}
	
	private void checkColorAndWidth(Color c, double wl)
		throws IOException
	{
		if(c != actualColor) {
			out.write("  "+roundTo(c.getRed()/255.0)+" "+
				roundTo(c.getGreen()/255.0)+ " "
				+roundTo(c.getBlue()/255.0)+	" setrgbcolor\n");

			actualColor=c;
		}
		if(wl != actualWidth) {
			out.write("  " +wl+" setlinewidth\n");
			actualWidth = wl;
		}
	}
	
	private void registerDash(int dashStyle)
		throws IOException
	{
		if(actualDash!=dashStyle) {
			actualDash=dashStyle;
			if(dashStyle==0) 
				out.write("[] 0 setdash\n");
			else
				out.write(""+dash[dashStyle]+" 0 setdash\n");

		}
	}

}