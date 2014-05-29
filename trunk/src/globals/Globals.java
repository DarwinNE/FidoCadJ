package primitives;

import java.io.*;
import java.util.*;

import net.sourceforge.fidocadj.FidoEditor;
import net.sourceforge.fidocadj.R;
import android.app.Activity;
import geom.*;
import dialogs.*;
import export.*;
import globals.*;
import graphic.*;
import graphic.nil.*;

/** Class to handle the advanced text primitive.

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

public final class PrimitiveAdvText extends GraphicPrimitive
{

	private String txt;
	private int six;
	private int siy;
	private int sty;
	private int o;
	private String fontName;
	
	private boolean recalcSize;

	/* Text style patterns */
	static final int TEXT_BOLD=1;
	static final int TEXT_MIRRORED=4;
	static final int TEXT_ITALIC=2;
	
	// A text is defined by one point.
	static final int N_POINTS=1;
	
	// Those are data which are kept for the fast redraw of this primitive. 
	// Basically, they are calculated once and then used as much as possible
	// without having to calculate everything from scratch.	
//	private static BufferedImage sizeCalculationImage;
	private int xaSCI;
	private int yaSCI;
	private int orientationSCI;
	private int hSCI, thSCI, wSCI;
	private int[] xpSCI, ypSCI;
	
	private boolean mirror;
	private int orientation;
	
	private int h, th, w;
	private double ymagnitude;
	//private int coordorientation;
	private boolean	coordmirroring;
	
	private int x1, y1, xa, ya, qq;
	private double xyfactor, si, co;
	private boolean needsStretching;

		
	/** Gets the number of control points used.
		@return the number of points used by the primitive
	*/
	public int getControlPointNumber()
	{
		return N_POINTS;
	}
	/** Standard "empty" constructor.
	
	*/
	public PrimitiveAdvText()
	{
		super();
		six=3;
		siy=4;
		o=0;
		txt="";
		fontName = Globals.defaultTextFont;
		virtualPoint = new PointG[N_POINTS];
		for(int i=0;i<N_POINTS;++i)
			virtualPoint[i]=new PointG();
		
		changed=true;
		recalcSize=true;		
	}
	
	/** Complete constructor.
		@param x the x position of the control point of the text
		@param y the y position of the control point of the text
		@param sx the x size of the font
		@param sy the y size of the font
		@param fn font name
		@param or the orientation of the text
		@param st the style of the text
		@param t the text to be used
		@param l the layer to be used
	*/
	public PrimitiveAdvText(int x, int y, int sx, int sy, String fn, int or, 
							int st, String t, int l)
	{
		this();
		virtualPoint[0]=new PointG(x,y);
		six=sx;
		siy=sy;
		sty=st;
		txt=t;
		o=or;
		fontName=fn;
		setLayer(l);
		changed=true;
		recalcSize=true;
	}
	
	/** Draw the graphic primitive on the given graphic context.
		@param g the graphic context in which the primitive should be drawn.
		@param coordSys the graphic coordinates system to be applied.
		@param layerV the layer description.
	*/
	public void draw(GraphicsInterface g, MapCoordinates coordSys, 
		Vector layerV)
	{
		if(!selectLayer(g,layerV))
			return;
			
		// For this:
		// http://sourceforge.net/tracker/?func=detail&aid=2908420&group_id=274886&atid=1167997
		// we are now checking if the text is "" before printing it.
			
		if(txt.length()==0)
			return;		
		changed=true;
		ymagnitude=coordSys.getYMagnitude();
		//coordorientation=coordSys.getOrientation();
		coordmirroring=coordSys.getMirror();
		
		if(changed) {
 			changed=false;
			mirror=false;
 			recalcSize =true;
 			/* in the simple text primitive, the the virtual point represents
		   		the position of the text to be drawn. */
			x1=virtualPoint[0].x;
 			y1=virtualPoint[0].y;
 			xa=coordSys.mapX(x1,y1);
 			ya=coordSys.mapY(x1,y1);
 			/* siy is the font horizontal size in mils (1/1000 of an inch).
 		   		1 typographical point is 1/72 of an inch.
 			*/
 			
 			g.setFont(fontName, (int)(six*12*coordSys.getYMagnitude()/7+.5),
 				(sty & TEXT_ITALIC)!=0, (sty & TEXT_BOLD)!=0);
	    	
	    	orientation=o;
	    	mirror=false;
    		if((sty & TEXT_MIRRORED)!=0){
    	 		mirror=!mirror;
    	 		orientation=-orientation;
    		}
    		if (six==0 || siy==0) {
    			siy=10;
    			six=7;
    		}
    		orientation-=coordSys.getOrientation()*90;
    		
			if(coordmirroring){
    	 		mirror=!mirror;
    	 		orientation=-orientation;
    		}


    		// Determination of the size of the text string.
    		h = g.getFontAscent();
    		th = h+g.getFontDescent();
    		w = g.getStringWidth(txt);
    				
 			xyfactor=1.0;
 			needsStretching = false;
 		
 			if(siy/six != 10/7){
    			// Create a transformation for the font. 
				xyfactor=(double)siy/(double)six*22.0/40.0;
				needsStretching = true;
   			}
 		 			
    		if(orientation==0) {
  				if (mirror){
  					coordSys.trackPoint(xa-w,ya);
					coordSys.trackPoint(xa,ya+(int)(th*xyfactor));
				} else {
  					coordSys.trackPoint(xa+w,ya);
					coordSys.trackPoint(xa,ya+(int)(h*xyfactor));
				}	
			} else {
    			if(mirror){
    				si=Math.sin(Math.toRadians(-orientation));
					co=Math.cos(Math.toRadians(-orientation));
   				} else {
    				si=Math.sin(Math.toRadians(orientation));
					co=Math.cos(Math.toRadians(orientation));
   				}
   				// Calculate the bounding box.

   				double bbx1=xa;
   				double bby1=ya;
   			
   				double bbx2=xa+th*si;
   				double bby2=ya+th*co*xyfactor;
   				
   				double bbx3=xa+w*co+th*si;
   				double bby3=ya+(th*co-w*si)*xyfactor;
   				
   				double bbx4=xa+w*co;
   				double bby4=ya-w*si*xyfactor;
						
    			if(mirror) {
   				 	bbx2=xa-th*si;
   					bbx3=xa-w*co-th*si;
   					bbx4=xa-w*co;
    			} 
    	 		
    	 		coordSys.trackPoint((int)bbx1,(int)bby1);
    	 		coordSys.trackPoint((int)bbx2,(int)bby2);
    	 		coordSys.trackPoint((int)bbx3,(int)bby3);
    	 		coordSys.trackPoint((int)bbx4,(int)bby4);
			} 
    		qq=(int)(ya/xyfactor);
		}
		
		// not draws out of the drawingPanel (particularly in the toolsbar) while scrolling.
		Activity context = (Activity) Globals.context; 
		FidoEditor drawingPanel = (FidoEditor) context.findViewById(R.id.drawingPanel);
		int deltaY = drawingPanel.getScrollY();
		if( ya-deltaY > 0 && orientation == 0 || 
			ya-deltaY-w > 0 && orientation != 0 ) 
		//
				g.drawAdvText(xyfactor, xa, ya, qq, h, w, h, needsStretching, 
					orientation, mirror, txt);
	}
	
	/**	Parse a token array and store the graphic data for a given primitive
		Obviously, that routine should be called *after* having recognized
		that the called primitive is correct.
		That routine also sets the current layer.
		
		@param tokens the tokens to be processed. tokens[0] should be the
		command of the actual primitive.
		@param N the number of tokens present in the array
		
	*/
	public void parseTokens(String[] tokens, int N)		
		throws IOException
	{
		// assert it is the correct primitive
		changed=true;
		recalcSize = true;
		
 		if (tokens[0].equals("TY")) {	// Text (advanced)
 			if (N<9) {
 				IOException E=new IOException("bad arguments on TY");
				throw E;
			}
			
 			virtualPoint[0].x=Integer.parseInt(tokens[1]);
 			virtualPoint[0].y=Integer.parseInt(tokens[2]);
 			siy=Integer.parseInt(tokens[3]);
 			six=Integer.parseInt(tokens[4]);
 			o=Integer.parseInt(tokens[5]);
 			sty=Integer.parseInt(tokens[6]);
 			parseLayer(tokens[7]);
 			
 			int j=8;
			StringBuffer txtb=new StringBuffer();
      		
      		if(tokens[8].equals("*")) {
      			fontName = Globals.defaultTextFont;
      		} else {
      			fontName = tokens[8].replaceAll("\\+\\+"," ");
      		}
  
 			
 			/* siy is the font horizontal size in mils (1/1000 of an inch).
 			   1 typographical point is 1/72 of an inch.
			*/
 		 					
      		while(j<N-1){
      			txtb.append(tokens[++j]);
      			if (j<N-1) txtb.append(" ");
      		}
			txt=txtb.toString();
      		
 					
			
 		} else if (tokens[0].equals("TE")) {	// Text (simple)
 			if (N<4) {
 				IOException E=new IOException("bad arguments on TE");
				throw E;
			}
			
 			virtualPoint[0].x=Integer.parseInt(tokens[1]);
 			virtualPoint[0].y=Integer.parseInt(tokens[2]);
 			// Default sizes and styles
 			six=3;
 			siy=4;
 			o=0;
 			sty=0;
 			
 			int j=2;
			txt="";
      				
      		while(j<N-1)
      			txt+=tokens[++j]+" ";
      		
 			// In the original simple text primitive, the layer was not
 			// handled. Here we just suppose it is always 0.
 			
 			parseLayer("0");
			
 		} else {
 			IOException E=new IOException("Invalid primitive:"+
 										  " programming error?");
			throw E;
 		} 			
 	
	}
		
	/** Gets the distance (in primitive's coordinates space) between a 
	    given point and the primitive. 
	    When it is reasonable, the behaviour can be binary (polygons, 
	    ovals...). In other cases (lines, points), it can be proportional.
		@param px the x coordinate of the given point
		@param py the y coordinate of the given point
	*/
	public int getDistanceToPoint(int px, int py)
	{ 					
		// This calculation takes a lot of time, since we need to obtain the
		// size of the font used, calculate the area which is active for the 
		// mouse and so on. For this reason, we make it only when necessary, 
		// by exploiting exactly the same principle of the optimized drawing
		// routines.
		
		if (changed||recalcSize) {
			if(changed) {
				GraphicsNull gSCI = new GraphicsNull();
 				
	   			gSCI.setFont(fontName, (int)(six*12.0/7.0+.5),
	   				(sty & TEXT_ITALIC)!=0, (sty & TEXT_BOLD)!=0);
		
    			hSCI = gSCI.getFontAscent();
    			thSCI = hSCI+gSCI.getFontDescent();
   				wSCI = gSCI.getStringWidth(txt);
   			} else {
   				hSCI =(int)(h/ymagnitude);
				thSCI=(int)(th/ymagnitude);
				wSCI=(int)(w/ymagnitude);
   			}
   			// recalcSize is set to true when the draw method detects that the
 			// graphical appearance of the text should be recalculated.
 			
 			recalcSize = false;
        	
			xaSCI=virtualPoint[0].x;
 			yaSCI=virtualPoint[0].y;

    		orientationSCI=o;
    					
			if(siy/six != 10/7){
    			hSCI=(int)Math.round(hSCI*((double)siy*22.0/40.0/(double)six)); 
				thSCI=(int)Math.round((double)thSCI*((double)siy*
					22.0/40.0/(double)six)); 
   			}
   			
   			// TODO: the calculation fails when mirrored text or rotated is 
   			// included into a mirrored or rotated macro. 

   			// Corrections for the mirrored text.
 			if((sty & TEXT_MIRRORED)!=0){
    	 		orientationSCI=-orientationSCI;
    	 		wSCI=-wSCI;
    		}
    		
    		if (coordmirroring) {
    			//orientationSCI=-orientationSCI;
    			wSCI=-wSCI;
    		}
 			
 			// If there is a tilt of the text, we calculate the four corner
 			// of the tilted text area and we put them in a polygon.
    		if(orientationSCI!=0){
    			double si=Math.sin(Math.toRadians(orientation));
				double co=Math.cos(Math.toRadians(orientation));
		
 				xpSCI=new int[4];
        		ypSCI=new int[4];
                        
        		xpSCI[0]=xaSCI;
            	ypSCI[0]=yaSCI;
            	xpSCI[1]=(int)(xaSCI+thSCI*si);
            	ypSCI[1]=(int)(yaSCI+thSCI*co);
            	xpSCI[2]=(int)(xaSCI+thSCI*si+wSCI*co);
            	ypSCI[2]=(int)(yaSCI+thSCI*co-wSCI*si);
            	xpSCI[3]=(int)(xaSCI+wSCI*co);
            	ypSCI[3]=(int)(yaSCI-wSCI*si);
            }
        }

       	if(orientationSCI==0) {
			if(GeometricDistances.pointInRectangle(Math.min(xaSCI, 
				xaSCI+wSCI),yaSCI,Math.abs(wSCI),thSCI,px,py))
	           	return 0;
		} else {	
       		if(GeometricDistances.pointInPolygon(xpSCI,ypSCI,4, px,py))
          		return 0;
		} 
		
		// It is better not to obtain Integer.MAX_VALUE, but a value which
		// is very large yet smaller than Integer.MAX_VALUE. In fact, in some
		// cases, a test is done to see if a layer is present and this test
		// tries to see if the distance of a symbol is less than 
		// Integer.MAX_VALUE, as this should be true when a symbol is present
		// and visible on the screen.
		
		return Integer.MAX_VALUE/2;
		
	}	
	
	/**	Get the control parameters of the given primitive.
	
		@return a vector of ParameterDescription containing each control
				parameter.
				The first parameters should always be the virtual points.
				
	*/
	public Vector<ParameterDescription> getControls()
	{
		Vector<ParameterDescription> v = new Vector<ParameterDescription>(10);
		int i;
		ParameterDescription pd = new ParameterDescription();

		pd.parameter=txt;
		pd.description=Globals.messages.getString("ctrl_text");
		v.add(pd);
						
	/*	for (i=0;i<getControlPointNumber();++i) {
			pd = new ParameterDescription();
			pd.parameter=virtualPoint[i];
			pd.description=Globals.messages.getString("ctrl_control")+(i+1)+":";
			v.add(pd);
		} */
		
		pd = new ParameterDescription();
		pd.parameter=new LayerInfo(getLayer());
		pd.description=Globals.messages.getString("ctrl_layer");
		v.add(pd);
		
		pd = new ParameterDescription();
		pd.parameter=Integer.valueOf(six);
		pd.description=Globals.messages.getString("ctrl_xsize");
		v.add(pd);
		
		pd = new ParameterDescription();
		pd.parameter=Integer.valueOf(siy);
		pd.description=Globals.messages.getString("ctrl_ysize");
		v.add(pd);
		
	 	pd = new ParameterDescription();
		pd.parameter=Integer.valueOf(o);
		pd.description=Globals.messages.getString("ctrl_angle");
		v.add(pd);
		pd = new ParameterDescription();
		pd.parameter=Boolean.valueOf((sty & TEXT_MIRRORED)!=0);
		pd.description=Globals.messages.getString("ctrl_mirror");
		v.add(pd);
		pd = new ParameterDescription();
		pd.parameter=Boolean.valueOf((sty & TEXT_ITALIC)!=0);
		pd.description=Globals.messages.getString("ctrl_italic");
		v.add(pd);
		pd = new ParameterDescription();
		pd.parameter=Boolean.valueOf((sty & TEXT_BOLD)!=0);
		pd.description=Globals.messages.getString("ctrl_boldface");
		v.add(pd);
		pd = new ParameterDescription();
		pd.parameter=new FontG(fontName);
		pd.description=Globals.messages.getString("ctrl_font");
		v.add(pd);
		return v;
	}
	/**	Set the control parameters of the given primitive.
		This method is specular to getControls().
		
		@param v a vector of ParameterDescription containing each control
				parameter.
				The first parameters should always be the virtual points.
				
	*/
	public int setControls(Vector<ParameterDescription> v)
	{
		int i=0;
		changed=true;
		recalcSize = true;
		ParameterDescription pd;
		
		pd=(ParameterDescription)v.get(i);
		++i;
		// Check, just for sure...
		if (pd.parameter instanceof String)
			txt=(String)pd.parameter;
		else
		 	System.out.println("Warning: unexpected parameter!"+pd);

	/*	for (i=1;i<getControlPointNumber()+1;++i) {
			pd = (ParameterDescription)v.get(i);
			
			// Check, just for sure...
			if (pd.parameter instanceof PointG)
				virtualPoint[i-1]=(PointG)pd.parameter;
			else
			 	System.out.println("Warning: unexpected parameter!");
			
		}*/
		pd = (ParameterDescription)v.get(i);
		++i;
		// Check, just for sure...
		if (pd.parameter instanceof LayerInfo)
			setLayer(((LayerInfo)pd.parameter).getLayer());
		else
		 	System.out.println("Warning: unexpected parameter!");		
		pd=(ParameterDescription)v.get(i);
		++i;
		if (pd.parameter instanceof Integer)
			six=((Integer)pd.parameter).intValue();
		else
		 	System.out.println("Warning: unexpected parameter!"+pd);
		
		pd=(ParameterDescription)v.get(i);
		++i;
		if (pd.parameter instanceof Integer)
			siy=((Integer)pd.parameter).intValue();
		else
		 	System.out.println("Warning: unexpected parameter!"+pd);
		
		pd=(ParameterDescription)v.get(i);
		++i;
		if (pd.parameter instanceof Integer)
			o=((Integer)pd.parameter).intValue();
		else
		 	System.out.println("Warning: unexpected parameter!"+pd);
		
		
		pd=(ParameterDescription)v.get(i++);
		if (pd.parameter instanceof Boolean)
			sty = ((Boolean)pd.parameter).booleanValue()?
				sty | TEXT_MIRRORED:
				sty & (~TEXT_MIRRORED);
		else
		 	System.out.println("Warning: unexpected parameter!"+pd);
		 	
		pd=(ParameterDescription)v.get(i++);
		if (pd.parameter instanceof Boolean)
			sty = ((Boolean)pd.parameter).booleanValue() ?
				sty | TEXT_ITALIC:
				sty & (~TEXT_ITALIC);
		else
		 	System.out.println("Warning: unexpected parameter!"+pd);
		 	
		pd=(ParameterDescription)v.get(i++);
		if (pd.parameter instanceof Boolean)
			
			sty= ((Boolean)pd.parameter).booleanValue() ? 
				sty | TEXT_BOLD:
				sty & (~TEXT_BOLD);
		else
		 	System.out.println("Warning: unexpected parameter!"+pd);
		
		pd=(ParameterDescription)v.get(i++);
		if (pd.parameter instanceof FontG)
			fontName = ((FontG)pd.parameter).getFamily();
		else
		 	System.out.println("Warning: unexpected parameter!"+pd);
		return i;
	}
	
	/** Rotate the primitive. Here we just rotate 90° by 90°
	
		@param bc specify if the rotation should be done 
				counterclockwise.
		@param ix the x coordinate of the rotation center
		@param iy the y coordinate of the rotation center
	*/
	public void rotatePrimitive(boolean bc, int ix, int iy)
	{
		boolean bCounterClockWise=bc;
		super.rotatePrimitive(bCounterClockWise, ix, iy);
		
		int po=o/90;
		
		if((sty & TEXT_MIRRORED)!=0) {
			bCounterClockWise=!bCounterClockWise;
		}
		
		if (bCounterClockWise)
			po=++po%4;
		else
			po=(po+3)%4;
	
		o=90*po;
	}
	
	/** Mirror the primitive. For the text, it is different than for the other
		primitives, since we just need to toggle the mirror flag.
		@param xPos is the symmetry axis
		
	*/
	public void mirrorPrimitive(int xPos)
	{
		super.mirrorPrimitive(xPos);
		sty ^= TEXT_MIRRORED;
		changed=true;
		recalcSize = true;
	}
	
	/** Obtain a string command descripion of the primitive.
		@return the FidoCad code corresponding to the primitive.
	*/
	public String toString(boolean extensions)
	{
		String subsFont;
		
		// The standard font is indicated with an asterisk
		if (fontName.equals(Globals.defaultTextFont)) {
			subsFont = "*";
		} else {
			StringBuffer s=new StringBuffer("");
    		// All spaces are substituted with "++" in order to avoid problems
    		// during the parsing phase
    		for (int i=0; i<fontName.length(); ++i) {
    		if(fontName.charAt(i)==' ') 
    			s.append("++");
    		else
    			s.append(fontName.charAt(i));	
    		}
			subsFont=s.toString();
		}
		
		String s= "TY "+virtualPoint[0].x+" "+virtualPoint[0].y+" "+siy+" "
			+six+" "+o+" "+sty+" "+getLayer()+" "+subsFont+" "+txt+"\n";
	
		return s;
	}
	
	public void export(ExportInterface exp, MapCoordinates cs) 
		throws IOException
	{
		int resulting_o=o-cs.getOrientation()*90;
		
		exp.exportAdvText (cs.mapX(virtualPoint[0].x,virtualPoint[0].y),
			cs.mapY(virtualPoint[0].x,virtualPoint[0].y), 
			(int)Math.abs(cs.mapXr(six,six)-cs.mapXr(0,0)),
			(int)Math.abs(cs.mapYr(siy,siy)-cs.mapYr(0,0)),
			fontName, 
			(sty & TEXT_BOLD)!=0,
			(sty & TEXT_MIRRORED)!=0,
			(sty & TEXT_ITALIC)!=0,
			resulting_o, getLayer(), txt);
			
	}
	/** Get the number of the virtual point associated to the Name property
		@return the number of the virtual point associated to the Name property
	*/
	public int getNameVirtualPointNumber()
	{
		return -1;
	}
	
	/** Get the number of the virtual point associated to the Value property
		@return the number of the virtual point associated to the Value property
	*/
	public  int getValueVirtualPointNumber()
	{
		return -1;
	}
}
