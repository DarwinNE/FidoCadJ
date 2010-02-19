package primitives;


import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.awt.geom.*;

import geom.*;
import dialogs.*;
import export.*;


public class PrimitivePCBPad extends GraphicPrimitive
{

	private int rx;
	private int ry;
	private int sty;
	private int ri;

	private boolean drawOnlyPads;
	
	// A PCB pad is defined by one points.

	static final int N_POINTS=1;
	
	
	/** Gets the number of control points used.
		@return the number of points used by the primitive
	*/
	
	public int getControlPointNumber()
	{
		return N_POINTS;
	}
	
	
	
	/** Create a PCB pad
		@param x1 the x coordinate (logical unit).
		@param y1 the y coordinate (logical unit).
		@param wx the width of the pad
		@param wy the height of the pad
		@param st the style of the pad
		@param layer the layer to be used.
		
	*/
	
	public PrimitivePCBPad(int x1, int y1, int wx, int wy, int radi, int st, 
		int layer)
	{
		super();
		virtualPoint = new Point[N_POINTS];
		for(int i=0;i<N_POINTS;++i)
			virtualPoint[i]=new Point();
			
		virtualPoint[0].x=x1;
		virtualPoint[0].y=y1;
		rx=wx;
		ry=wy;
		ri=radi;
		sty=st;
		
		setLayer(layer);
		
	}
	

	
	public PrimitivePCBPad()
	{
		super();
		rx=0;
		ry=0;
		sty=0;
		ri=0;
		
		virtualPoint = new Point[N_POINTS];
		for(int i=0;i<N_POINTS;++i)
			virtualPoint[i]=new Point();
		
	}

	private int x1, y1, rrx, rry, xa,ya, rox, roy, rix, riy;
	
	/** Draw the graphic primitive on the given graphic context.
		@param g the graphic context in which the primitive should be drawn.
		@param coordSys the graphic coordinates system to be applied.
		@param layerV the layer description.
	*/
	final public void draw(Graphics2D g, MapCoordinates coordSys,
							  ArrayList layerV)
	{
	
		if(!selectLayer(g,layerV))
			return;

		if(changed) {
			changed=false;
		
			/* in the PCB pad primitive, the the virtual points represent
		   the position of the pad to be drawn. */
			x1=virtualPoint[0].x;
 			y1=virtualPoint[0].y;
 		
 			rrx=Math.abs(coordSys.mapXi(x1,y1, false)-coordSys.mapXi(x1+rx,y1+ry, false));
 			rry=Math.abs(coordSys.mapYi(x1,y1, false)-coordSys.mapYi(x1+rx,y1+ry, false));
 			
 		
 			xa=coordSys.mapX(x1,y1);
 			ya=coordSys.mapY(x1,y1);
 			
 			coordSys.trackPoint(x1-rrx,y1-rry);
 			coordSys.trackPoint(x1+rrx,y1+rry);
 		
 			rox=Math.abs(xa-coordSys.mapXi(x1+5,y1+5, false));
 			roy=Math.abs(ya-coordSys.mapYi(x1+5,y1+5, false));
 		
 			rix=Math.abs(xa-coordSys.mapXi(x1+ri,y1+ri, false));
 			riy=Math.abs(ya-coordSys.mapYi(x1+ri,y1+ri, false));
 		}
 		
 		// Exit if the primitive is offscreen. This is a simplification, but
 		// ensures that the primitive is correctly drawn when it is 
 		// partially visible.
 		
 		if(!g.hitClip(xa-rrx/2,ya-rry/2, rrx, rry))
 			return;
 		if (!drawOnlyPads) {
 			switch(sty) {
 			case 1:
 				/* Rectangular pad */
 				g.fillRect(xa-rrx/2,
 				    ya-rry/2,rrx,rry);
 				break;
 			case 2:
 				/* Rounded corner rectangular pad */
 				g.fillRoundRect(xa-rrx/2,
 				    ya-rry/2,rrx,rry,rox,roy);
 				break;
 			case 0:
 			default:
 				/* Oval Pad */ 
		
 				g.fillOval(xa-rrx/2,
 				    ya-rry/2,rrx,rry);
 			
 			}
 		} else {
      			
 			g.setColor(Color.white); /* Drill */
 			g.fillOval(xa-rix/2,
 				   ya-riy/2,rix,riy);
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
		changed=true;

		// assert it is the correct primitive
		
		if (tokens[0].equals("PA")) {	// PCB Area pad 
 			/* Example PA 752 50 15 15 4 1 1 */
 			
 			if (N<7)  {
 				IOException E=new IOException("bad arguments on PA");
				throw E;
 			}
 			// Load the points in the virtual points associated to the 
 			// current primitive.

 			virtualPoint[0].x=Integer.parseInt(tokens[1]);
 			virtualPoint[0].y=Integer.parseInt(tokens[2]);
 			
 			rx=Integer.parseInt(tokens[3]);
 			ry=Integer.parseInt(tokens[4]);
 			ri=Integer.parseInt(tokens[5]);
 			sty=Integer.parseInt(tokens[6]);
 			
 			if(N>7) parseLayer(tokens[7]);

 			
 		} else {
 			IOException E=new IOException("PA: Invalid primitive:"+tokens[0]+
 										  " programming error?");
			throw E;
 		}
		
 			
		
	}
	
	public void setDrawOnlyPads(boolean pd)
 	{
 		drawOnlyPads=pd;
 	}
 	
	/**	Get the control parameters of the given primitive. 
	
		@return a vector of ParameterDescription containing each control
				parameter.
				The first parameters should always be the virtual points.
				
	*/
	public Vector getControls()
	{
		Vector v=super.getControls();
		ParameterDescription pd = new ParameterDescription();

		pd.parameter=new Integer(rx);
		pd.description="X radius:";
		v.add(pd);
		pd = new ParameterDescription();
		pd.parameter=new Integer(ry);
		pd.description="Y radius:";
		v.add(pd);
		pd = new ParameterDescription();
		pd.parameter=new Integer(ri);
		pd.description="Internal radius:";
		v.add(pd);
		pd = new ParameterDescription();
		pd.parameter=new Integer(sty);	// A list should be better
		pd.description="Style:";
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
		super.setControls(v);
		int i=getControlPointNumber()+1;		
		ParameterDescription pd;
		
		pd=(ParameterDescription)v.get(i);
		++i;
		// Check, just for sure...
		if (pd.parameter instanceof Integer)
			rx=((Integer)pd.parameter).intValue();
		else
		 	System.out.println("Warning: unexpected parameter!"+pd);
		 	
		pd=(ParameterDescription)v.get(i);
		++i;
		// Check, just for sure...
		if (pd.parameter instanceof Integer)
			ry=((Integer)pd.parameter).intValue();
		else
		 	System.out.println("Warning: unexpected parameter!"+pd);
		 	
		pd=(ParameterDescription)v.get(i);
		++i;
		// Check, just for sure...
		if (pd.parameter instanceof Integer)
			ri=((Integer)pd.parameter).intValue();
		else
		 	System.out.println("Warning: unexpected parameter!"+pd);
		 pd=(ParameterDescription)v.get(i);
		++i;
		// Check, just for sure...
		if (pd.parameter instanceof Integer)
			sty=((Integer)pd.parameter).intValue();
		else
		 	System.out.println("Warning: unexpected parameter!"+pd);
	}

	/** Rotate the primitive. Here we just rotate 90° by 90° by swapping the
		x and y size of the pad
	
		@param bCounterClockWise specify if the rotation should be done 
				counterclockwise.
	*/
	public void rotatePrimitive(boolean bCounterClockWise, int ix, int iy)
	{
		super.rotatePrimitive(bCounterClockWise, ix, iy);
		int swap=rx;
		rx=ry;
		ry=swap;
		
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
		int distance=GeometricDistances.pointToPoint(
				virtualPoint[0].x,virtualPoint[0].y,
				px,py)-Math.min(rx,ry)/2;
		return distance>0?distance:0;
	}
	
	/** Obtain a string command descripion of the primitive.
		@return the FIDOCAD command line.
	*/
	public String toString(boolean extensions)
	{
		return "PA "+virtualPoint[0].x+" "+virtualPoint[0].y+" "+
			rx+" "+ry+" "+ri+" "+sty+" "+getLayer()+"\n";
	}
	
	public void export(ExportInterface exp, MapCoordinates cs)
		throws IOException
	{
		exp.exportPCBPad(cs.mapX(virtualPoint[0].x,virtualPoint[0].y),
					   cs.mapY(virtualPoint[0].x,virtualPoint[0].y), 
					   sty, 
					   Math.abs(cs.mapX(virtualPoint[0].x+rx,
					   		virtualPoint[0].y+ry)-
					   cs.mapX(virtualPoint[0].x,virtualPoint[0].y)),
					   Math.abs(cs.mapY(virtualPoint[0].x+rx,
					   		virtualPoint[0].y+ry)-
					   cs.mapY(virtualPoint[0].x,virtualPoint[0].y)),
					   ri, getLayer(),drawOnlyPads);
	}

}