import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.awt.geom.*;



public class PrimitivePCBLine extends GraphicPrimitive
{

	private int width;

	// A PCB segment is defined by two points.

	static final int N_POINTS=2;
	
	
	/** Gets the number of control points used.
		@return the number of points used by the primitive
	*/
	
	public int getControlPointNumber()
	{
		return N_POINTS;
	}
	
	public PrimitivePCBLine()
	{
		super();
		width=0;
		
		virtualPoint = new Point[N_POINTS];
		for(int i=0;i<N_POINTS;++i)
			virtualPoint[i]=new Point();
		
	}
	/** Create a PCB line between two points
		@param x1 the start x coordinate (logical unit).
		@param y1 the start y coordinate (logical unit).
		@param x2 the end x coordinate (logical unit).
		@param y2 the end y coordinate (logical unit).
		@param w specifies the line width. 
		@param layer the layer to be used.
		
	*/
	
	public PrimitivePCBLine(int x1, int y1, int x2, int y2, int w, int layer)
	{
		super();
		virtualPoint = new Point[N_POINTS];
		for(int i=0;i<N_POINTS;++i)
			virtualPoint[i]=new Point();
			
		virtualPoint[0].x=x1;
		virtualPoint[0].y=y1;
		virtualPoint[1].x=x2;
		virtualPoint[1].y=y2;
		width=w;
		
		
		setLayer(layer);
		
	}
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
		/* in the PCB line primitive, the first two virtual points represent
		   the beginning and the end of the segment to be drawn. */
		int x1=coordSys.mapX(virtualPoint[0].x,virtualPoint[0].y);
 		int y1=coordSys.mapY(virtualPoint[0].x,virtualPoint[0].y);
 		int x2=coordSys.mapX(virtualPoint[1].x,virtualPoint[1].y);
 		int y2=coordSys.mapY(virtualPoint[1].x,virtualPoint[1].y);
 		int wi_pix=Math.abs(coordSys.mapX(virtualPoint[0].x,virtualPoint[0].y)
		    -coordSys.mapX(virtualPoint[0].x+width,virtualPoint[0].y+width));
			
 		int xa=Math.min(x1, x2)-wi_pix/2;
 		int ya=Math.min(y1, y2)-wi_pix/2;
 		int xb=Math.max(x1, x2)+wi_pix/2;
 		int yb=Math.max(y1, y2)+wi_pix/2;
 			
 		// Exit if the primitive is offscreen. This is a simplification, but
 		// ensures that the primitive is correctly drawn when it is 
 		// partially visible.
 		coordSys.trackPoint(xa,ya);
 		coordSys.trackPoint(xb,yb);
 		if(!g.hitClip(xa,ya, (xb-xa)+1,(yb-ya)+1))
 			return;
		
		// Calculate line width in pixels
 			
		
 			
 			
		Stroke ss=g.getStroke();
 			
		BasicStroke UseStroke= new BasicStroke(wi_pix,
				java.awt.BasicStroke.CAP_ROUND,
				java.awt.BasicStroke.JOIN_ROUND);
			
		g.setStroke(UseStroke);
		g.drawLine(x1, y1, x2, y2);
                          
		g.setStroke(ss);
 		
 		
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
		
		if (tokens[0].equals("PL")) {	// Line
 			if (N<6) {
 				IOException E=new IOException("bad arguments on PL");
				throw E;
 			}
 			// Load the points in the virtual points associated to the 
 			// current primitive.

 			virtualPoint[0].x=Integer.parseInt(tokens[1]);
 			virtualPoint[0].y=Integer.parseInt(tokens[2]);
 			virtualPoint[1].x=Integer.parseInt(tokens[3]);
 			virtualPoint[1].y=Integer.parseInt(tokens[4]);
 			width=Integer.parseInt(tokens[5]);
 			if(N>6) parseLayer(tokens[6]);

 			
 		} else {
 			IOException E=new IOException("Invalid primitive:"+
 										  " programming error?");
			throw E;
 		}
		
		
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

		pd.parameter=new Integer(width);
		pd.description="Width:";
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
			width=((Integer)pd.parameter).intValue();
		else
		 	System.out.println("Warning: unexpected parameter!"+pd);
		
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
		int distance=GeometricDistances.pointToSegment(
				virtualPoint[0].x,virtualPoint[0].y,
				virtualPoint[1].x,virtualPoint[1].y,
				px,py)-width/2;
            
        return distance<0?0:distance;
	}
	
	/** Obtain a string command descripion of the primitive.
		@return the FIDOCAD command line.
	*/
	public String toString(boolean extensions)
	{
		return "PL "+virtualPoint[0].x+" "+virtualPoint[0].y+" "+
			+virtualPoint[1].x+" "+virtualPoint[1].y+" "+width+" "+
			getLayer()+"\n";
	}
	
	public void export(ExportInterface exp, MapCoordinates cs)
		throws IOException
	{
		exp.exportPCBLine(cs.mapX(virtualPoint[0].x,virtualPoint[0].y),
				cs.mapY(virtualPoint[0].x,virtualPoint[0].y), 
				cs.mapX(virtualPoint[1].x,virtualPoint[1].y),
				cs.mapY(virtualPoint[1].x,virtualPoint[1].y), 
				width, getLayer()); 
	}

}