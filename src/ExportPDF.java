import java.awt.*;
import java.util.*;
import java.io.*;
import java.text.*;

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
    @version 1.0, July 2009
*/

public class ExportPDF implements ExportInterface {

	private File fileExp;
	private File temp;
	private FileWriter fstream;
	private FileWriter fstreamt;
	private BufferedWriter out;
	private BufferedWriter outt;
	
	private String head;
	private String b1;
	private String b2;
	private String b3;
	private String b4;
	private String b5;
	private String b6;
	private String b7;
	private String b8;
	private String b9;
	
	
	private Vector layerV;
	private int numberPath;
	private int xsize;
	private int ysize;
	private Color actualColor;
	private double actualWidth;
	
	
	static final int NODE_SIZE = 1;
	static final double l_width=.33;
	
	/** Constructor
	
		@param f the File object in which the export should be done.
		
	*/
	
	public ExportPDF (File f) throws IOException
	{
		fileExp=f;
		
		fstream = new FileWriter(fileExp);
		
    	temp = File.createTempFile("real",".howto");
		temp.deleteOnExit();
		fstreamt = new FileWriter(temp);

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
	    
	    // To track the block sizes, we will first write it in a temporary file
	    
	    outt = new BufferedWriter(fstreamt);
	    numberPath=0;
	    	    
	    int wi=totalSize.width;
	    int he=totalSize.height;
	    
	    // An header of the EPS file
	    
	   	// 200 dpi is the internal resolution of FidoCadJ
	   	// 72 dpi is the internal resolution of the Postscript coordinates
	   	
	    double res_mult=200.0/72.0;
	    
	
		
		head = "%PDF-1.4\n";
		b5=	"5 0 obj\n"+
				"  <</Kids [4 0 R ]\n"+
				"    /Count 1\n"+
				"    /Type /Pages\n"+
				"    /MediaBox [ 0 0  "+(int)(totalSize.width/res_mult+1)+" "+
				(int)(totalSize.height/res_mult+1)+" ]\n"+
				"  >> endobj\n";
				
		b6=	"6 0 obj\n" +
				"  <<	/Type /Font\n" +
				"    /Subtype /Type1\n" +
				"    /Name /F1\n" +
				"    /BaseFont /Courier\n" + 
				"    /Encoding /MacRomanEncoding\n" +
				"  >> endobj\n";
		b7="7 0 obj\n" +
				"  <<	/Type /Font\n" +
				"    /Subtype /Type1\n" +
				"    /Name /F2\n" +
				"    /BaseFont /Courier-Bold\n" + 
				"    /Encoding /MacRomanEncoding\n" +
				"  >> endobj\n";
				
		out.write(head+b5+b6+b7);


		
		// Since in a postscript drawing, the origin is at the bottom left,
		// we introduce a coordinate transformation to have it at the top
		// left of the drawing.
		
		actualColor = null;
		actualWidth = -1;
		
		
		outt.write("   1 0 0 1 0 "+(totalSize.height/res_mult)+ "  cm\n");

		outt.write("  "+(1/res_mult)+" 0  0 "+(-1/res_mult)+" 0 0  cm\n");

		outt.write("1 J\n");
		
	} 
	
	/** Called at the end of the export phase.
	*/
	public void exportEnd() 
		throws IOException
	
	{ 
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();

		outt.close();
		
		long filelength=temp.length();
		
		b8="8 0 obj\n" +
				"  <<\n" +
				"    /Length "+filelength+"\n" +		
				"  >>\n"+
				"  stream\n";
		
		out.write (b8);
		
		BufferedInputStream bufRead = new BufferedInputStream(new FileInputStream(temp));
                
        int c=0;
        
        // Copy all the contents of the temporary file into the pdf file
        c =bufRead.read();
        while (c!=-1){
           	out.write(c);
        	c =bufRead.read();
        }
            
        bufRead.close();
	
		String b8e="endstream\n"+
				"endobj\n";
				
		b4 = "4 0 obj\n"+
				"<<	\n"+
				"  /Type /Page\n"+
				"  /Parent 5 0 R\n"+
				"  /Resources <<\n"+
				"  /Font <<\n"+
				"  /F1 6 0 R\n"+
				"  /F2 7 0 R\n"+
				">>\n"+
				"/ProcSet 2 0 R\n"+
				">>\n"+
				"  /Contents 8 0 R\n"+
				">>\n"+
				"endobj\n";
		
		b2 =	"2 0 obj\n"+
				"[ /PDF /Text  ]\n"+
				"endobj\n";
				
		
		b1="1 0 obj\n"+
				"<<\n"+
				"  /Creator (FidoCadJ"+Globals.version+
				", PDF export filter by Davide Bucci)\n"+
		//		"  /CreationDate ("+dateFormat.format(date)+")\n"+
				"  /Author ("+System.getProperty("user.name")+")\n"+
				"  /Producer (FidoCadJ)\n"+
				">>\n"+
				"endobj\n";
			
		b3 = "3 0 obj\n"+
				"<<\n"+
				"  /Pages 5 0 R\n"+
				"  /Type /Catalog\n"+
				">>\n"+
				"endobj\n";
		
		out.write(b8e+b4 + b2 + b1 +b3);
		
		// order: header, 5, 6, 7, 8, file, 8e, 4, 2, 1, 3 
		
		out.write("xref \n"+
				  "0 9\n"+
				  "0000000000 65535 f \n"+			// header
				  
				  addLeadZeros(head.length()+b5.length()+b6.length()+b7.length()+
				  	b8.length()+ filelength + b8e.length()+b4.length()+
				  	b2.length())+  " 00000 n \n"+ 		// 1
				  				  
				  addLeadZeros(head.length()+b5.length()+b6.length()+b7.length()+
				  	b8.length()+ filelength + b8e.length()+b4.length())+ 
				  	" 00000 n \n"+			// 2
				  
				  addLeadZeros(head.length()+b5.length()+b6.length()+b7.length()+
				  	b8.length()+ filelength + b8e.length()+b4.length()+
				  	b2.length()+b1.length())+  " 00000 n \n"+ 		// 3
				  
				  addLeadZeros(head.length()+b5.length()+b6.length()+b7.length()+
				  	b8.length()+ filelength + b8e.length())+
				  	" 00000 n \n"+ 		// 4
				  
				  addLeadZeros(head.length())+  " 00000 n \n"+ 		// 5
				  
				  addLeadZeros(head.length()+b5.length())+  " 00000 n \n"+ 		// 6
				  
				  addLeadZeros(head.length()+b5.length()+b6.length())+
				  " 00000 n \n"+ 		// 7
				  
				  addLeadZeros(head.length()+b5.length()+b6.length()+b7.length())+ 
				  " 00000 n \n"); 		// 8
				  
				  
		
		out.write("trailer\n"+
				"<<\n"+
				"  /Size 9\n"+
				"  /Root 3 0 R\n"+
				"  /Info 1 0 R\n"+
				">>\n"+
				"startxref\n"+
				(head.length()+b5.length()+b6.length()+b7.length()+
				  	b8.length()+ filelength + b8e.length()+b4.length()+
				  	b2.length()+b1.length()+b3.length())+"\n"+
				"%%EOF");
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
		if (text.equals(""))
			return;
			
		LayerDesc l=(LayerDesc)layerV.get(layer);
		Color c=l.getColor();
		String bold=""; 
		
		checkColorAndWidth(c, l_width);
        
   		outt.write("BT\n");

		if(isBold)
			outt.write("/F2"+" "+sizey+" Tf\n");       
		else
			outt.write("/F1"+" "+sizey+" Tf\n");       
		


		outt.write("q\n");
		
		outt.write("  1 0 0 1 "+ roundTo(x)+" "+ roundTo(y)+" cm\n");

		
		if(orientation !=0) {
			double alpha=(isMirrored?orientation:-orientation)/180.0*Math.PI;
			outt.write("  "+roundTo(Math.cos(alpha))+" "
			+ roundTo(Math.sin(alpha))+ " "
				+(roundTo(-Math.sin(alpha)))+ 
				" "+roundTo(Math.cos(alpha))+" 0 0 cm\n");
		}
		if(isMirrored)
			outt.write("  -1 0 0 -1 0 0 cm\n");
		else
			outt.write("  1 0 0 -1 0 0 cm\n");
		
					
		outt.write("  1 0 0 1 0 "+roundTo(-(double)sizey/1.38)+" cm\n");
		
		// Remember that we consider sizex/sizey=7/12 as the "normal" aspect 
		// ratio.
		
		
		
		outt.write("  "+ roundTo(100*(sizex/22.0/sizey*38.0))+" Tz\n");
		
		
		
		Map subst = new HashMap();
		subst.put("(","\\050");
		subst.put(")","\\051");
		text=Globals.substituteBizarreChars(text, subst);
			
		outt.write("  ("+text+") Tj\n");
		outt.write("Q\nET\n");
		
	
			
		
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
		checkColorAndWidth(c, l_width);

				  
		outt.write(""+x1+" "+y1+" m \n");
		outt.write(""+x2+" "+y2+" "+x3+" "+y3+" "+x4+" "+y4+" c S\n");
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

		checkColorAndWidth(c, l_width);


		ellipse((x-NODE_SIZE/2.0), (y-NODE_SIZE/2.0), 
				(x+NODE_SIZE/2.0), (y+NODE_SIZE/2.0), true);
		//outt.write("f\n");
		
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
		
		checkColorAndWidth(c, l_width);

				  
		outt.write("  "+x1+" "+y1+" m "+
			x2+" "+y2+" l S\n");
		
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
		checkColorAndWidth(c, l_width);

	
		ellipse(x1,y1, x2, y2, isFilled);
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

				  
		outt.write("  "+x1+" "+y1+" m "+
			x2+" "+y2+" l S\n");
		//outt.write("  " +l_width+" w\n");
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
		
		checkColorAndWidth(c, l_width);

		
		// At first, draw the pad...
		if(!onlyHole) {
			switch (style) {
				default:
				case 0: // Oval pad
					ellipse((x-six/2.0), (y-siy/2.0), 
							(x+six/2.0), (y+siy/2.0), true);	
				
					outt.write("f\n");	

					 break;
				case 2: // Rounded pad 
					roundRect((x-six/2.0), (y-siy/2.0), 
							six, siy, 4, true);
					break;
				case 1:	// Square pad
					xdd=((double)x-six/2.0);
					ydd=((double)y-siy/2.0);
					outt.write(""+xdd+" "+ydd+" m\n");
					outt.write(""+(xdd+six)+" "+ydd+" l\n");
					outt.write(""+(xdd+six)+" "+(ydd+siy)+" l\n");
					outt.write(""+xdd+" "+(ydd+siy)+" l\n");
					outt.write("B\n");	
					
					break;
				
			}
		}		
			// ... then, drill the hole!
		
		checkColorAndWidth(Color.WHITE, l_width);
		
		ellipse((x-indiam/2.0), (y-indiam/2.0), 
				(x+indiam/2.0), (y+indiam/2.0), true);
		outt.write("f\n");
				
	
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
		
		checkColorAndWidth(c, l_width);
		
		
			
		outt.write("  "+vertices[0].x+" "+vertices[0].y+" m\n");
		
		for (int i=1; i<nVertices; ++i) 
			outt.write("  "+vertices[i].x+" "+vertices[i].y+" l\n");
		
		//outt.write("closepath\n");
		if(isFilled) {
			outt.write("  f*\n");	
		} else {
			outt.write("  s\n");	
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
		
		checkColorAndWidth(c, l_width);

		
		outt.write("  "+x1+" "+y1+" m\n");
		outt.write("  "+x2+" "+y1+" l\n");
		outt.write("  "+x2+" "+y2+" l\n");
		outt.write("  "+x1+" "+y2+" l\n");
		if(isFilled) {
			outt.write("f\n");	
		} else {
			outt.write("s\n");	
		}


	}
	
	private void roundRect (double x1, double y1, double w, double h, 
		double r, boolean filled)
		throws IOException
	{
		outt.write(""+ (x1+r) + " " +(y1)+" m\n");
		outt.write(""+ (x1+w-r) + " " +(y1)+" l\n");
		outt.write(""+ (x1+w) + " " +(y1)+" "+ (x1+w) + " " +(y1+r)+" y\n");
		
		outt.write(""+ (x1+w) + " " +(y1+h-r)+" l\n");
		outt.write(""+ (x1+w) + " " +(y1+h)+" "+ (x1+w-r) + " " +(y1+h)+" y\n");
		
		outt.write(""+ (x1+r) + " " +(y1+h)+" l\n");
		outt.write(""+ (x1) + " " +(y1+h)+" "+ (x1) + " " +(y1+h-r)+" y\n");
		
		outt.write(""+ (x1) + " " +(y1+r)+" l\n");
		outt.write(""+ (x1) + " " +(y1)+" "+ (x1+r) + " " +(y1)+" y \n");
		
		outt.write("  "+(filled?"f\n":"s\n"));
	}
	
	private void ellipse(double x1, double y1, double x2, double y2, 
		boolean filled)
		throws IOException
	{
		int i;
		
		double cx = (x1+x2)/2.0;
		double cy = (y1+y2)/2.0;
		
		double rx = Math.abs(x2-x1)/2.0;
		double ry = Math.abs(y2-y1)/2.0;
		
		final int NMAX=32;
		
		double xA;
		double yA;
		
		double xB;
		double yB;
		
		double xC;
		double yC;
		
		double xD;
		double yD;
		
		double alpha;
		
		final double rr = 1.02;
		final double tt = 1.01;
		
		outt.write("  "+ roundTo(cx+rx)+" "+ roundTo(cy)+" m\n");
		
		for(i=0; i<NMAX; ++i) {
		
			alpha = 2.0*Math.PI*(double)i/(double)NMAX;
			xA = cx + rx * Math.cos(alpha);
			yA = cy + ry * Math.sin(alpha);
			
			alpha += 2.0*Math.PI/(double)NMAX/3.0;
			
			xB = cx + rr*rx * Math.cos(alpha);
			yB = cy + rr*ry * Math.sin(alpha);
			
			alpha += 2.0*Math.PI/(double)NMAX/3.0;
			
			xC = cx + tt*rx * Math.cos(alpha);
			yC = cy + tt*ry * Math.sin(alpha);
	
			alpha += 2.0*Math.PI/(double)NMAX/3.0;

			xD = cx + rx * Math.cos(alpha);
			yD = cy + ry * Math.sin(alpha);
			
			
			outt.write(roundTo(xC)+" "
				+ roundTo(yC)+" "+ roundTo(xD)+" "+ roundTo(yD)+" y\n");
		}
		outt.write("  "+(filled?"f\n":"s\n"));
		
		
		
	}
	
	private String addLeadZeros(long n)
	{
		String s=""+n;
		
		// simple and inefficient.
		while (s.length()<10) {
			s="0"+s;
		}
		
		return s;
	}
	
	private String roundTo(double n)
	{
		int ch = 2;
		return ""+ (((int)(n*Math.pow(10,ch)))/Math.pow(10,ch));
	}
	
	private void checkColorAndWidth(Color c, double wl)
		throws IOException
	{
		if(c != actualColor) {
			outt.write("  "+roundTo(c.getRed()/255.0)+" "+
				roundTo(c.getGreen()/255.0)+ " "
				+roundTo(c.getBlue()/255.0)+	" rg\n");
			outt.write("  "+roundTo(c.getRed()/255.0)+" "+
				roundTo(c.getGreen()/255.0)+ " "
				+roundTo(c.getBlue()/255.0)+	" RG\n");
			actualColor=c;
		}
		if(wl != actualWidth) {
			outt.write("  " +wl+" w\n");
			actualWidth = wl;
		}
	}
}