package primitives;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.awt.geom.*;

import globals.*;
import geom.*;
import dialogs.*;
import export.*;



public class PrimitiveLine extends GraphicPrimitive
{

	static final int N_POINTS=2;
	
	private boolean arrowStart;
	private boolean arrowEnd;
	
	private int arrowLength;
	private int arrowHalfWidth;
	
	private int arrowStyle;
	
	private int dashStyle;

	/** Standard constructors
	*/
	
	public PrimitiveLine(int x1, int y1, int x2, int y2, int layer)
	{
		super();
		
		arrowLength = 3;
		arrowHalfWidth = 1;
		arrowStart = false;
		arrowEnd = false;
		
		virtualPoint = new Point[N_POINTS];
		for(int i=0;i<N_POINTS;++i)
			virtualPoint[i]=new Point();
			
		virtualPoint[0].x=x1;
		virtualPoint[0].y=y1;
		virtualPoint[1].x=x2;
		virtualPoint[1].y=y2;
				
		setLayer(layer);
		
	}
	
	public PrimitiveLine()
	{
		super();
		arrowLength = 3;
		arrowHalfWidth = 1;
		// A segment is defined by two points.
		virtualPoint = new Point[N_POINTS];
		for(int i=0;i<N_POINTS;++i)
			virtualPoint[i]=new Point();
		
	}
	
	/** Gets the number of control points used.
		@return the number of points used by the primitive
	*/
	
	public int getControlPointNumber()
	{
		return N_POINTS;
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

		pd = new ParameterDescription();
		pd.parameter=new Boolean(arrowStart);
		pd.description="Arrow at start";
		pd.isExtension = true;
		v.add(pd);
		pd = new ParameterDescription();
		pd.parameter=new Boolean(arrowEnd);
		pd.description="Arrow at end";
		pd.isExtension = true;
		v.add(pd);
		pd = new ParameterDescription();
		pd.parameter=new Integer(arrowLength);
		pd.description="Arrow length";
		pd.isExtension = true;
		v.add(pd);
		pd = new ParameterDescription();
		pd.parameter=new Integer(arrowHalfWidth);
		pd.description="Arrow half width";
		pd.isExtension = true;

		v.add(pd);
		pd = new ParameterDescription();
		pd.parameter=new Integer(arrowStyle);
		pd.description="Arrow style";
		pd.isExtension = true;

		v.add(pd);

		pd = new ParameterDescription();
		pd.parameter=new Integer(dashStyle);
		pd.description="Dash style";
		pd.isExtension = true;

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
		
		pd=(ParameterDescription)v.get(i++);
		if (pd.parameter instanceof Boolean)
			arrowStart=((Boolean)pd.parameter).booleanValue();
		else
		 	System.out.println("Warning: unexpected parameter!"+pd);
		pd=(ParameterDescription)v.get(i++);
		if (pd.parameter instanceof Boolean)
			arrowEnd=((Boolean)pd.parameter).booleanValue();
		else
		 	System.out.println("Warning: unexpected parameter!"+pd);
		 	
		pd=(ParameterDescription)v.get(i++);
		if (pd.parameter instanceof Integer)
			arrowLength=((Integer)pd.parameter).intValue();
		else
		 	System.out.println("Warning: unexpected parameter!"+pd);
		 	
		pd=(ParameterDescription)v.get(i++);
		if (pd.parameter instanceof Integer)
			arrowHalfWidth=((Integer)pd.parameter).intValue();
		else
		 	System.out.println("Warning: unexpected parameter!"+pd);

		pd=(ParameterDescription)v.get(i++);
		if (pd.parameter instanceof Integer)
			arrowStyle=((Integer)pd.parameter).intValue();
		else
		 	System.out.println("Warning: unexpected parameter!"+pd);
		
		pd=(ParameterDescription)v.get(i++);
		if (pd.parameter instanceof Integer)
			dashStyle=((Integer)pd.parameter).intValue();
		else
		 	System.out.println("Warning: unexpected parameter!"+pd);
		 	
		// Parameters validation and correction
		if(dashStyle>=Globals.dashNumber)
			dashStyle=Globals.dashNumber-1;
		if(dashStyle<0)
			dashStyle=0;
	
	}
	
	/** Create a segment between two points
		@param x1 the start x coordinate (logical unit).
		@param y1 the start y coordinate (logical unit).
		@param x2 the end x coordinate (logical unit).
		@param y2 the end y coordinate (logical unit).
		@param layer the layer to be used.
		
	*/
	

	/** Draw the graphic primitive on the given graphic context.
		@param g the graphic context in which the primitive should be drawn.
		@param coordSys the graphic coordinates system to be applied.
		@param layerV the layer description.
	*/
	public void draw(Graphics2D g, MapCoordinates coordSys,
							  Vector layerV)
	{
	
		if(!selectLayer(g,layerV))
			return;
 		
	
		/* in the line primitive, the first two virtual points represent
		   the beginning and the end of the segment to be drawn. */

		int x1=coordSys.mapX(virtualPoint[0].x,virtualPoint[0].y);
 		int y1=coordSys.mapY(virtualPoint[0].x,virtualPoint[0].y);
 		int x2=coordSys.mapX(virtualPoint[1].x,virtualPoint[1].y);
 		int y2=coordSys.mapY(virtualPoint[1].x,virtualPoint[1].y);
 		
 		int xa=Math.min(x1, x2);
 		int ya=Math.min(y1, y2);
 		int xb=Math.max(x1, x2);
 		int yb=Math.max(y1, y2);
 			
 		// Exit if the primitive is offscreen. This is a simplification, but
 		// ensures that the primitive is correctly drawn when it is 
 		// partially visible.
 		coordSys.trackPoint(xa,ya);
 		coordSys.trackPoint(xb,yb);
 		
 		
 		if(!g.hitClip(xa,ya, (xb-xa)+1,(yb-ya)+1))
 			return;
		
		if(dashStyle>0) {
			Stroke oldStroke;
				
			BasicStroke dashed = new BasicStroke(1.0f, 
                                          BasicStroke.CAP_BUTT, 
                                          BasicStroke.JOIN_MITER, 
                                          10.0f, Globals.dash[dashStyle], 0.0f);
                                         
			oldStroke=g.getStroke();
			g.setStroke(dashed);
			g.drawLine(x1, y1, x2, y2);
		    g.setStroke(oldStroke);

		} else {
			g.drawLine(x1, y1, x2, y2);
		}
		
		
		
		int h=coordSys.mapX(arrowHalfWidth,arrowHalfWidth)-coordSys.mapX(0,0);
		int l=coordSys.mapX(arrowLength,arrowLength)-coordSys.mapX(0,0);
		
		if (arrowStart) Arrow.drawArrow(g, x1, y1, x2, y2, l, h, arrowStyle);
		if (arrowEnd) Arrow.drawArrow(g, x2, y2, x1, y1, l, h, arrowStyle);

 		return;
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
		int i;

		
		if (tokens[0].equals("LI")) {	// Line
 			if (N<5) {
 				IOException E=new IOException("bad arguments on LI");
				throw E;
 			}
 			// Load the points in the virtual points associated to the 
 			// current primitive.

 			virtualPoint[0].x=Integer.parseInt(tokens[1]);
 			virtualPoint[0].y=Integer.parseInt(tokens[2]);
 			virtualPoint[1].x=Integer.parseInt(tokens[3]);
 			virtualPoint[1].y=Integer.parseInt(tokens[4]);
 			if(N>5) parseLayer(tokens[5]);
 			
 			// FidoCadJ extensions
 			
 			if(N>6 && tokens[6].equals("FCJ")) {
 				int arrows = Integer.parseInt(tokens[7]);
 				arrowStart = (arrows & 0x01) !=0;
 				arrowEnd = (arrows & 0x02) !=0;
 				
 				arrowStyle = Integer.parseInt(tokens[8]);
 				arrowLength = Integer.parseInt(tokens[9]);
 				arrowHalfWidth = Integer.parseInt(tokens[10]);
 				dashStyle = Integer.parseInt(tokens[11]);
 				// Parameters validation and correction
				if(dashStyle>=Globals.dashNumber)
					dashStyle=Globals.dashNumber-1;
				if(dashStyle<0)
					dashStyle=0;
 			}	
 			
 			
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
		return GeometricDistances.pointToSegment(
				virtualPoint[0].x,virtualPoint[0].y,
				virtualPoint[1].x,virtualPoint[1].y,
				px,py);
            
    }
	
	/** Obtain a string command descripion of the primitive.
		@return the FIDOCAD command line.
	*/
	public String toString(boolean extensions)
	{
		String s= "LI "+virtualPoint[0].x+" "+virtualPoint[0].y+" "+
			+virtualPoint[1].x+" "+virtualPoint[1].y+" "+
			getLayer()+"\n";
		
		if(extensions) {
		 	int arrows = (arrowStart?0x01:0x00)|(arrowEnd?0x02:0x00);
		 			 	
		 	if (arrows>0 || dashStyle>0) 
		 		s+="FCJ "+arrows+" "+arrowStyle+" "+arrowLength+" "+
		 		arrowHalfWidth+" "+dashStyle+"\n";
		
		}
		return s;
	}
	
	/** The export routine
	*/
	public void export(ExportInterface exp, MapCoordinates cs) 
		throws IOException
	{
		exp.exportLine(cs.mapX(virtualPoint[0].x,virtualPoint[0].y),
					   cs.mapY(virtualPoint[0].x,virtualPoint[0].y),
					   cs.mapX(virtualPoint[1].x,virtualPoint[1].y),
					   cs.mapY(virtualPoint[1].x,virtualPoint[1].y),
					   getLayer(),
					   arrowStart, arrowEnd, arrowStyle, arrowLength, arrowHalfWidth, dashStyle); 
	}
}