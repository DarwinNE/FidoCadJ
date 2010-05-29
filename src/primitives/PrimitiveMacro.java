package primitives;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.awt.geom.*;

import geom.*;
import circuit.*;
import dialogs.*;
import export.*;
import globals.*;


/** Class to handle the macro primitive. Code is somewhat articulated since
	I use ricorsion (a macro is another drawing seen as an unbreakable symbol.

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

	Copyright 2007-2010 by Davide Bucci
</pre>

@author Davide Bucci
*/

public class PrimitiveMacro extends GraphicPrimitive
{

	static final int N_POINTS=3;
	private Map library;
	private ArrayList layers;
	private int o;
	private boolean m; 
	private boolean drawOnlyPads;
	private int drawOnlyLayer;
	private boolean alreadyExported;
	private ParseSchem macro;
	private MapCoordinates macroCoord;
	private boolean selected;

	private int macroFontSize;
	private String macroName;
	private String macroDesc;
	private String macroFont;
	
	private String name;
	private String value;

	
	/** Gets the number of control points used.
		@return the number of points used by the primitive
	*/
	
	public int getControlPointNumber()
	{
		return N_POINTS;
	}
	
	/** Constructor
	
		@param lib the library to be inherited
		@param l the list of layers
	*/
	public PrimitiveMacro(Map lib, ArrayList l)
	{
		super();
		library=lib;
		layers=l;
		drawOnlyPads=false;
		drawOnlyLayer=-1;
		macroFontSize = 3;
		name="";
		value="";
		macroFont="Courier New";
		macro=new ParseSchem();
		macroCoord=new MapCoordinates();
		changed=true;
		
		virtualPoint = new Point[N_POINTS];
		for(int i=0;i<N_POINTS;++i)
			virtualPoint[i]=new Point();
		macroStore(layers);
	}
	
	/** Constructor
	
		@param lib the library to be inherited
		@param l the list of layers
		@param x the x coordinate of the control point of the macro
		@param y the y coordinate of the control point of the macro
		@param key the key to be used to uniquely identify the macro (it will 
			be converted to lowercase)
		@param na the name to be shown
		@param xa the x coordinate of the name of the macro
		@param ya the y coordinate of the name of the macro
		@param va the value to be shown
		@param xv the x coordinate of the value of the macro
		@param yv the y coordinate of the value of the macro
		@param macroF the font to be used for the name and the value of the 
			macro
		@param macroS the size of the font
		@param oo the macro orientation
	*/	
	public PrimitiveMacro(Map lib, ArrayList l, int x, int y, String key, 
		 String na, int xa, int ya, String va, int xv, int yv, String macroF, int macroS,
		 int oo)
		throws IOException
	{
		super();
		library=lib;
		layers=l;
		key=key.toLowerCase();
		macro=new ParseSchem();
		macroCoord=new MapCoordinates();
		changed=true;
		macroFontSize = macroS;
		o=oo;
		
		// A segment is defined by two points.
		virtualPoint = new Point[N_POINTS];
		for(int i=0;i<N_POINTS;++i)
			virtualPoint[i]=new Point();
		
		virtualPoint[0].x=x;
		virtualPoint[0].y=y;
		virtualPoint[1].x=xa;
		virtualPoint[1].y=ya;
		virtualPoint[2].x=xv;
		virtualPoint[2].y=yv;
		
		/*
		virtualPoint[1].x=x+10;
		virtualPoint[1].y=y+5;
		virtualPoint[2].x=x+10;
		virtualPoint[2].y=y+10;*/
		name=na;
		value=va;
		
		MacroDesc macro=(MacroDesc)library.get(key);
 			
 		if (macro==null){
 			
 			IOException G=new IOException("Unrecognized macro " 
 										  + key);
			throw G;
 		}
 		macroDesc = macro.description;
 		macroName = key;
 		macroFont = macroF;
 		
 		macroStore(layers);
 					
	}
	
	public boolean containsLayer(int l)
 	{
 		return macro.containsLayer(l);
 	}
		
	
	/** Set the font to be used for name and value
	
		@param f the font name
		
	*/
	
	public void setMacroFont(String f, int size)
	{
		macroFont = f;
		macroFontSize = size;
		
		changed=true;	
	}
	
	
	/** Get the font used for name and value
	
		@return the font name
		
	*/
	
	public String getMacroFont()
	{
		return macroFont;
	}
	
	/** Get the size of the macro font.
		@return the size of the macro font.
	
	*/
	public int getMacroFontSize()
	{
		return macroFontSize;
	}
	
	private int z;
	private int xa, ya, xb, yb;
	// Text sizes
	private int h,th, w1, w2;
	private int t_h,t_th, t_w1, t_w2;
	
	
	/** Writes the macro name and value fields
	
	*/
	final private void drawText(Graphics2D g, MapCoordinates coordSys,
							  ArrayList layerV)
	{
		if (value.length()==0 && name.length()==0)
			return;
			
 		if(drawOnlyLayer!=getLayer())
 			return;
    
 		
 		if(changed) {
 			x2=virtualPoint[1].x;
 			y2=virtualPoint[1].y;
 			x3=virtualPoint[2].x;
 			y3=virtualPoint[2].y;
 		
 			xa=coordSys.mapX(x2,y2);
 			ya=coordSys.mapY(x2,y2);
 			xb=coordSys.mapX(x3,y3);
 			yb=coordSys.mapY(x3,y3);

 			// At first, write the name and the value fields in the given positions
 			f = new Font(macroFont,Font.PLAIN,
 				(int)( macroFontSize*12*coordSys.getYMagnitude()/7+.5));
 			
	   		fm = g.getFontMetrics(f);
    		h = fm.getAscent();
    		th = h+fm.getDescent();
   			w1 = fm.stringWidth(name);
   			w2 = fm.stringWidth(value);

    		coordSys.trackPoint(xa,ya);
	  		coordSys.trackPoint(xa+w1,ya+th);
	    	coordSys.trackPoint(xb,yb);
    		coordSys.trackPoint(xb+w2, yb+th);

		}
	   	
	   	// This is useful and faster for small zooms
	   	
	   	if(!g.hitClip(xa,ya, w1,th) && !g.hitClip(xb,yb, w2,th))
 			return;
	   	
	   	if(th<Globals.textSizeLimit) {
	   		g.drawLine(xa,ya, xa+w1-1,ya);
	   		return;
	   	} 		
 		if(!g.getFont().equals(f))
	   		g.setFont(f);

   		/* The if's have been added thanks to this information:
   		 http://sourceforge.net/projects/fidocadj/forums/forum/997486/topic/3474689?message=7798139
   		*/
  		if (name.length()!=0) {
    		g.drawString(name,xa,ya+h);
    	}
    	if (value.length()!=0) {
    		g.drawString(value,xb,yb+h);
    	}
    	
	}
	
	int x1, y1;
	/**	Draw the macro contents
	
	*/
	final private void drawMacroContents(Graphics2D g, MapCoordinates coordSys,
							  ArrayList layerV, boolean isFast)
	{
		/* in the macro primitive, the the virtual point represents
		   the position of the reference point of the macro to be drawn. */
		
		if(changed) {
			changed = false;
			x1=virtualPoint[0].x;
 			y1=virtualPoint[0].y;
 		
 				
 			macroCoord.setXMagnitude(coordSys.getXMagnitude());
			macroCoord.setYMagnitude(coordSys.getYMagnitude());
		 		
 			macroCoord.setXCenter(coordSys.mapXr(x1,y1));
 			macroCoord.setYCenter(coordSys.mapYr(x1,y1));
			macroCoord.orientation=(o+coordSys.orientation)%4;
			macroCoord.mirror=m ^ coordSys.mirror;
 			macroCoord.isMacro=true;
 			macroCoord.resetMinMax();
 		 		 			
 			macro.setMapCoordinates(macroCoord);
			macro.setChanged(true);
		}
		
		if(getSelected()) {
 			macro.selectAll();
 			selected = true;
		} else if (selected) {
			macro.deselectAll();
			selected = false;
		}

		macro.setDrawOnlyLayer(drawOnlyLayer);
 		macro.setDrawOnlyPads(drawOnlyPads);
 		
		if (isFast) 
 			macro.drawFast(g);
		else
			macro.draw(g);
		
		if (macroCoord.getXMax()>macroCoord.getXMin() && 
			macroCoord.getYMax()>macroCoord.getYMin()) {
			coordSys.trackPoint(macroCoord.getXMax(),macroCoord.getYMax());
			coordSys.trackPoint(macroCoord.getXMin(),macroCoord.getYMin());	
		}
	}
	
	/** Parse and store the tokenized version of the macro.
		@layerV the array containing the layer description to be inherited.
	
	*/
	private void macroStore(ArrayList layerV)
	{
	 	macro.setLibrary(library); 			// Inherit the library
 		macro.setLayers(layerV);	// Inherit the layers
 		changed=true;	
 		//macroDesc=(String)library.get(macroName);
 		
 		
 		if (macroDesc!=null) {
 			try {
 				macro.parseString(new StringBuffer(macroDesc)); 
 				// Recursive call	
 			} catch(IOException e) {
                	System.out.println("Error: "+e); 
            }
 		}
	}
	
	/** Draw the graphic primitive on the given graphic context.
	
		@param g the graphic context in which the primitive should be drawn.
		@param coordSys the graphic coordinates system to be applied.
		@param layerV the layer description.
	*/
	final public void draw(Graphics2D g, MapCoordinates coordSys,
							  ArrayList layerV)
	{
		drawText(g, coordSys, layerV);
		drawMacroContents(g, coordSys, layerV, false);
		
 	}
 	
 	private int x2,y2,x3,y3;
 	private Font f;
 	private FontMetrics fm;
 	
 	/** Draw the graphic primitive on the given graphic context.
		Normally, this method calls the usual draw method. Of course, if for a 
		given primitive (text for example) the drawing operations are too much
		slow, this method should be overridden in the derived class.
		
		@param g the graphic context in which the primitive should be drawn.
		@param coordSys the graphic coordinates system to be applied.
		@param layerV the layer description.
	*/
	final public void drawFast(Graphics2D g, MapCoordinates coordSys,
							  ArrayList layerV)
	{
		drawMacroContents(g, coordSys, layerV, true);
		
		x2=virtualPoint[1].x;
 		y2=virtualPoint[1].y;
 		x3=virtualPoint[2].x;
 		y3=virtualPoint[2].y;
 		xa=coordSys.mapX(x2,y2);
 		ya=coordSys.mapY(x2,y2);
 		xb=coordSys.mapX(x3,y3);
 		yb=coordSys.mapY(x3,y3);
 		
		f = new Font(macroFont,Font.PLAIN,12);
		
	   	g.setFont(f);
		fm = g.getFontMetrics(f);
    	
   		
		g.drawRect(Math.min(xa, xa+w1),ya,Math.abs(w1),th);
		g.drawRect(Math.min(xb, xb+w2),yb,Math.abs(w2),th);

	}
	
	/** Set the Draw Only Pads mode.
	
		@param pd the wanted value
	
	*/
								  
 	final public void setDrawOnlyPads(boolean pd)
 	{
 		drawOnlyPads=pd;
 	}
	
	/** Set the Draw Only Layer mode.
	
		@param la the layer that should be drawn.
	
	*/
	
	final public void setDrawOnlyLayer(int la)
 	{
 		drawOnlyLayer=la;
 	}
	
	
	public void setName(String[] tokens, int N)
		throws IOException
	{
		StringBuffer txtb=new StringBuffer();
		int j=8;
		changed=true;	
		if (tokens[0].equals("TY")) {	// Text (advanced)
 			if (N<9) {
 				IOException E=new IOException("bad arguments on TY");
				throw E;
			}
			
 			virtualPoint[1].x=Integer.parseInt(tokens[1]);
 			virtualPoint[1].y=Integer.parseInt(tokens[2]);
 			
 		 					
      		while(j<N-1){
      			txtb.append(tokens[++j]);
      			if (j<N-1) txtb.append(" ");
      		}
			name=txtb.toString();
      		
 					
			
 		} else {
 			IOException E=new IOException("Invalid primitive:"+tokens[0]+
 										  " programming error?");
			throw E;
 		} 
 	
		
	}
	
	public int getMaxLayer()
    {
    	return macro.getMaxLayer();
    }
	
	public void setValue(String[] tokens, int N)
		throws IOException
	{
		StringBuffer txtb=new StringBuffer();
		int j=8;
		changed=true;	
		if (tokens[0].equals("TY")) {	// Text (advanced)
 			if (N<9) {
 				IOException E=new IOException("bad arguments on TY");
				throw E;
			}
			
 			virtualPoint[2].x=Integer.parseInt(tokens[1]);
 			virtualPoint[2].y=Integer.parseInt(tokens[2]);
 			
			if(tokens[8].equals("*")) {
      			macroFont = "Courier New";
      		} else {
      			macroFont = tokens[8].replaceAll("\\+\\+"," ");
      		} 			
 		 					
      		while(j<N-1){
      			txtb.append(tokens[++j]);
      			if (j<N-1) txtb.append(" ");
      		}
			value=txtb.toString();
      		
 					
			
 		} else {
 			IOException E=new IOException("Invalid primitive: "+tokens[0]+
 										  " programming error?");
			throw E;
 		} 
 	
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
		if (tokens[0].equals("MC")) {	// Line
 			if (N<6) {
 				IOException E=new IOException("bad arguments on MC");
				throw E;
 			}
 			// Load the points in the virtual points associated to the 
 			// current primitive.

 			virtualPoint[0].x=Integer.parseInt(tokens[1]);
 			virtualPoint[0].y=Integer.parseInt(tokens[2]);
 			virtualPoint[1].x=virtualPoint[0].x+10;
			virtualPoint[1].y=virtualPoint[0].y+10;
			virtualPoint[2].x=virtualPoint[0].x+10;
			virtualPoint[2].y=virtualPoint[0].y+5;
 			o=Integer.parseInt(tokens[3]);  // orientation
 			m=(Integer.parseInt(tokens[4])==1);  // mirror
 			macroName=tokens[5];
 			
 			for (int i=6; i<N; ++i) 
 				macroName+=" "+tokens[i];
 			
      		macroName=macroName.toLowerCase();

 			MacroDesc macro=(MacroDesc)library.get(macroName);
 			
 			if (macro==null){
 			
 				IOException G=new IOException("Unrecognized macro '" 
 											  + macroName+"'");
				throw G;
 			}
 			macroDesc = macro.description;
 			macroStore(layers);
 				
 				
 		} else {
 			IOException E=new IOException("MC: Invalid primitive:"+tokens[0]+
 										  " programming error?");
			throw E;
 		}
		
	}
	
	public final boolean needsHoles()
	{	
		return macro.getNeedHoles();
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
		/* in the macro primitive, the the first virtual point represents
		   the position of the reference point of the macro to be drawn. */
		   
		int x1=virtualPoint[0].x;
 		int y1=virtualPoint[0].y;
 		int dt=Integer.MAX_VALUE;

		// Calculate the distance with text objects

		int xa=virtualPoint[1].x;
        int ya=virtualPoint[1].y;
        int xb=virtualPoint[2].x;
        int yb=virtualPoint[2].y;
        
   		        
        
	    if(GeometricDistances.pointInRectangle(xa,ya,t_w1,t_th,px,py))
	       	return 0;
	    if(GeometricDistances.pointInRectangle(xb,yb,t_w2,t_th,px,py))
	       	return 0;
	        	
	
 		MapCoordinates mc=new MapCoordinates();
 			
 		mc.setXMagnitude(1.0);
 		mc.setYMagnitude(1.0);
 			
 		mc.setXCenter(0.0);
 		mc.setYCenter(0.0);
		mc.orientation=o%4;
		mc.mirror=m;
 		mc.isMacro=true;
	
 		MapCoordinates omc = macro.getMapCoordinates();
 		
 		macro.setMapCoordinates(mc);
 		
	
 		int vx=px-x1+100;
 		int vy= py-y1+100;
 		

 		
 		if(m) {
            switch(o){
                case 1:
                    vx=py-y1+100;
                    vy=px-x1+100;
                    
                    break;
                
                case 2:
                    vx=px-x1+100;
                    vy=-(py-y1)+100;
                    break;
                
                case 3:
                    vx=-(py-y1)+100;
                    vy=-(px-x1)+100;
                    break;
    
                case 0:
                    vx=-(px-x1)+100;
                    vy=py-y1+100;
                    break;
    
                default:
                    vx=0;
                    vy=0;
            }
        } else {
            switch(o){
                case 1:
                    vx=(py-y1)+100;
                    vy=-(px-x1)+100;
                    break;
                
                case 2:
                    vx=-(px-x1)+100;
                    vy=-(py-y1)+100;
                    break;
                
                case 3:
                    vx=-(py-y1)+100;
                    vy=(px-x1)+100;
                    break;
    
                case 0:
                    vx=px-x1+100;
                    vy=py-y1+100;
                    break;
    
                default:
                    vx= 0;
                    vy= 0;
            }
        }   
        
 		macro.setMapCoordinates(omc);	
 		
 		if (macroDesc==null)
 			System.out.println("1-Unrecognized macro "+
 			        "WARNING this can be a programming problem...");
 		else {

 			return Math.min(macro.distancePrimitive(vx, vy), dt);
		}
		return Integer.MAX_VALUE;
	}
	
	/**	Select the primitive if one of its virtual point is in the specified
		rectangular region (given in logical coordinates).
        @param px the x coordinate of the top left point.
        @param py the y coordinate of the top left point.
        @param w the width of the region
        @param h the height of the region
        @return true if at least a primitive has been selected
    */
    public boolean selectRect(int px, int py, int w, int h)
	{
		// Here is a trick: if there is at least one active layer, 
		// distancePrimitive will return a value less than the maximum.
		if (macro.distancePrimitive(0, 0)<Integer.MAX_VALUE) 
			return super.selectRect(px, py, w, h);
		else
			return false;
	}
	
	/** Rotate the primitive. For a macro, it is different than for the other
		primitive, since we need to rotate its coordinate system.
		@param bCounterClockWise specify if the rotation should be done 
				counterclockwise.
		@param ix the x coordinate of the center of rotation
		@param iy the y coordinate of the center of rotation
	*/
	public void rotatePrimitive(boolean bCounterClockWise,int ix, int iy)
	{
		super.rotatePrimitive(bCounterClockWise, ix, iy);
		
		if (!bCounterClockWise)
			o=++o%4;
		else
			o=(o+3)%4;
		changed=true;
	}
	
	/** Mirror the primitive. For a macro, it is different than for the other
		primitive, since we just need to toggle the mirror flag.
		
	*/
	public void mirrorPrimitive(int xpos)
	{
		super.mirrorPrimitive(xpos);
		m ^= true;
		changed=true;
	}
	
	/** Obtain a string command descripion of the primitive.
		@return the FIDOCAD command line.
	*/
	public String toString(boolean extensions)
	{
		String mirror="0";
		if(m)	
			mirror="1";
			
		String s="MC "+virtualPoint[0].x+" "+virtualPoint[0].y+" "+o+" "
				+mirror+" "+macroName+"\n";
		
		String subsFont;
		
		if (macroFont.equals("Courier New")) {
			subsFont = "*";
		} else {
			StringBuffer s1=new StringBuffer("");
    		
    		for (int i=0; i<macroFont.length(); ++i) {
    		if(macroFont.charAt(i)!=' ') 
    			s1.append(macroFont.charAt(i));
    		else
    			s1.append("++");
    		}
			subsFont=s1.toString();
		}
		
		if (!name.equals("") || !value.equals("")) {
			if(extensions) s+="FCJ\n";
			s+="TY "+virtualPoint[1].x+" "+virtualPoint[1].y+" "+
				macroFontSize*4/3+" "+macroFontSize+" "+"0"+" "+"0"+" "+getLayer()
				+" "+subsFont+" "+name+"\n";
			s+="TY "+virtualPoint[2].x+" "+virtualPoint[2].y+" "+
				macroFontSize*4/3+" "+macroFontSize+" "+"0"+" "+"0"+" "+getLayer()
				+" "+subsFont+" "+value+"\n";
		}
		
		return s;
		
	}
	
		/**	Get the control parameters of the given primitive.
	
		@return a vector of ParameterDescription containing each control
				parameter.
				The first parameters should always be the virtual points.
				
	*/
	public Vector getControls()
	{
		Vector v=new Vector(10);
		ParameterDescription pd = new ParameterDescription();

		pd.parameter=name;
		pd.description="Name:";
		pd.isExtension = true;
		v.add(pd);
		
		pd = new ParameterDescription();
		
		pd.parameter=value;
		pd.description="Value:";
		pd.isExtension = true;

		v.add(pd);
		
		pd = new ParameterDescription();
		pd.parameter=new Integer(getLayer());
		pd.description="Layer:";
		v.add(pd);
		
		pd = new ParameterDescription();
		pd.parameter=virtualPoint[0];
		pd.description="Control point:";
		v.add(pd);
		
		pd = new ParameterDescription();
		pd.parameter=virtualPoint[1];
		pd.description="Name point:";
		v.add(pd);
		
		pd = new ParameterDescription();
		pd.parameter=virtualPoint[2];
		pd.description="Value point:";
		v.add(pd);
		
		
	
		return v;
	}
	/**	Set the control parameters of the given primitive.
		This method is specular to getControls().
		
		@param v a vector of ParameterDescription containing each control
				parameter.
				The first parameters should always be the virtual points.
				
	*/

	public void setControls(Vector v)
	{
		int i=0;		
		ParameterDescription pd;
		
		changed=true;	
		
		pd=(ParameterDescription)v.get(i);
		++i;
		// Check, just for sure...
		if (pd.parameter instanceof String)
			name=((String)pd.parameter);
		else
		 	System.out.println("Warning: unexpected parameter!"+pd);
		
		pd=(ParameterDescription)v.get(i);
		++i;
		// Check, just for sure...
		if (pd.parameter instanceof String)
			value=((String)pd.parameter);
		else
		 	System.out.println("Warning: unexpected parameter!"+pd);
		 	
		pd = (ParameterDescription)v.get(i);
		++i;
		// Check, just for sure...
		if (pd.parameter instanceof Integer)
			setLayer(((Integer)pd.parameter).intValue());
		else
		 	System.out.println("Warning: unexpected parameter!");
		
		
		
		pd = (ParameterDescription)v.get(i);
		++i;
		// Check, just for sure...
		if (pd.parameter instanceof Point)
			virtualPoint[0]=(Point)pd.parameter;
		else
		 	System.out.println("Warning: unexpected parameter!");
		 	
		pd = (ParameterDescription)v.get(i);
		++i;
		// Check, just for sure...
		if (pd.parameter instanceof Point)
			virtualPoint[1]=(Point)pd.parameter;
		else
		 	System.out.println("Warning: unexpected parameter!");
		
		pd = (ParameterDescription)v.get(i);
		++i;
		// Check, just for sure...
		if (pd.parameter instanceof Point)
			virtualPoint[2]=(Point)pd.parameter;
		else
		 	System.out.println("Warning: unexpected parameter!");
		
	}
	
	
	/** Ensure that the next time the macro is exported, it will be done.
		Macro that are not expanded during exportation does not need to be
		replicated thru the layers. For this reason, there is an inibition system which is activated. Calling this method resets the inibition flag.
	*/
	public void resetExport()
	{
		alreadyExported=false;
	}
	
	public void export(ExportInterface exp, MapCoordinates cs) 
		throws IOException
	{
	
		if(alreadyExported)
			return;
			
		// Call the macro interface, to see if the macro should be expanded
		if (exp.exportMacro(virtualPoint[0].x, virtualPoint[0].y, 
			m, o*90, macroName, macroDesc, name, virtualPoint[1].x, 
			virtualPoint[1].y, value, virtualPoint[2].x, virtualPoint[2].y, 
			macroFont, macroFontSize,library)) {
			alreadyExported = true;
			return;
		}
		/* in the macro primitive, the virtual point represents
		   the position of the reference point of the macro to be drawn. */
		   
		int x1=virtualPoint[0].x;
 		int y1=virtualPoint[0].y;
 		
		MapCoordinates macroCoord=new MapCoordinates();
 			
 			
 		macroCoord.setXMagnitude(1.0);
		macroCoord.setYMagnitude(1.0);

 		macroCoord.setXCenter(cs.mapXr(x1,y1));
 		macroCoord.setYCenter(cs.mapYr(x1,y1));
		macroCoord.orientation=(o+cs.orientation)%4;
		macroCoord.mirror=m ^ cs.mirror;
 		macroCoord.isMacro=true;
 				 			
 		ParseSchem macro=new ParseSchem();
 		macro.setMapCoordinates(macroCoord);
 		macro.setDrawOnlyLayer(drawOnlyLayer);

 		
 		macro.setLibrary(library); 			// Inherit the library
 		macro.setLayers(layers);	// Inherit the layers
 		
 		if (macroDesc==null)
 			System.out.println("2-Unrecognized macro "+
 			        "WARNING this can be a programming problem...");
 		else {
 			macro.parseString(new StringBuffer(macroDesc)); // Recursive call	
 			// Propagate selection state
 			if(getSelected())
 				macro.selectAll();
 			 
 			macro.setDrawOnlyPads(drawOnlyPads);
 			macro.exportDrawing(exp, false);
 			
 			if(drawOnlyLayer==getLayer()) {
 				if(!name.equals(""))
 					exp.exportAdvText (cs.mapX(virtualPoint[1].x,virtualPoint[1].y),
						cs.mapY(virtualPoint[1].x,virtualPoint[1].y), macroFontSize, 
						(int)( macroFontSize*12/7+.5),
						macroFont, 
						false,
						false,
						false,
						0, getLayer(), name);
				
				if(!value.equals(""))
					exp.exportAdvText (cs.mapX(virtualPoint[2].x,virtualPoint[2].y),
						cs.mapY(virtualPoint[2].x,virtualPoint[2].y), macroFontSize, 
						(int)( macroFontSize*12/7+.5),
						macroFont, 
						false,
						false,
						false,
						0, getLayer(), value);
			}
		}
		
	}
}