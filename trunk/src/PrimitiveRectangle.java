import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.awt.geom.*;



public class PrimitiveRectangle extends GraphicPrimitive
{

	
	// A rectangle is defined by two points.
	static final int N_POINTS=2;
	private boolean isFilled;
	
	
	/** Gets the number of control points used.
		@return the number of points used by the primitive
	*/
	
	public int getControlPointNumber()
	{
		return N_POINTS;
	}
	
	public PrimitiveRectangle()
	{
		super();
		isFilled=false;
		
		virtualPoint = new Point[N_POINTS];
		for(int i=0;i<N_POINTS;++i)
			virtualPoint[i]=new Point();
		
	}
	/** Create a rectangle defined by two points
		@param x1 the start x coordinate (logical unit).
		@param y1 the start y coordinate (logical unit).
		@param x2 the end x coordinate (logical unit).
		@param y2 the end y coordinate (logical unit).
		@param f specifies if the rectangle should be filled.
		@param layer the layer to be used.
		
	*/
	
	public PrimitiveRectangle(int x1, int y1, int x2, int y2, boolean f, 
							  int layer)
	{
		super();
		virtualPoint = new Point[N_POINTS];
		for(int i=0;i<N_POINTS;++i)
			virtualPoint[i]=new Point();
			
		virtualPoint[0].x=x1;
		virtualPoint[0].y=y1;
		virtualPoint[1].x=x2;
		virtualPoint[1].y=y2;
		isFilled=f;
		
		
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
			
		/* in the rectangle primitive, the first two virtual points represent
		   the two corners of the segment */
 		
 		int xa=Math.min(coordSys.mapX(virtualPoint[0].x,virtualPoint[0].y),
 		             coordSys.mapX(virtualPoint[1].x,virtualPoint[1].y));
 		int ya=Math.min(coordSys.mapY(virtualPoint[0].x,virtualPoint[0].y),
 		             coordSys.mapY(virtualPoint[1].x,virtualPoint[1].y));
 		int xb=Math.max(coordSys.mapX(virtualPoint[0].x,virtualPoint[0].y),
 		             coordSys.mapX(virtualPoint[1].x,virtualPoint[1].y));
 		int yb=Math.max(coordSys.mapY(virtualPoint[0].x,virtualPoint[0].y),
 		             coordSys.mapY(virtualPoint[1].x,virtualPoint[1].y));
 			
 		if(!g.hitClip(xa,ya, (xb-xa)+1,(yb-ya)+1))
 				return;
 			
 		
 		if(isFilled){
 			// We need to add 1 to the rectangle, since the behaviour of 
 			// Java api is to skip the rightmost and bottom pixels 
 			g.fillRect(xa,ya,(xb-xa)+1,(yb-ya)+1);
 		} else {
 			g.drawRect(xa,ya,(xb-xa),(yb-ya));			
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
		if (tokens[0].equals("RV")||tokens[0].equals("RP")) {	// Oval
 			if (N<5) {
 				IOException E=new IOException("bad arguments on RV/RP");
				throw E;
 			}
 			virtualPoint[0].x=Integer.parseInt(tokens[1]);
 			virtualPoint[0].y=Integer.parseInt(tokens[2]);
 			virtualPoint[1].x=Integer.parseInt(tokens[3]);
 			virtualPoint[1].y=Integer.parseInt(tokens[4]);
 			if(N>5) parseLayer(tokens[5]);
 			
 			if(tokens[0].equals("RP"))
 				isFilled=true;
 			else
 				isFilled=false;
 			
 		} else {
 			IOException E=new IOException("Invalid primitive: "+
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

		pd.parameter=new Boolean(isFilled);
		pd.description="Filled";
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
		if (pd.parameter instanceof Boolean)
			isFilled=((Boolean)pd.parameter).booleanValue();
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
		int xa=Math.min(virtualPoint[0].x,virtualPoint[1].x);
        int ya=Math.min(virtualPoint[0].y,virtualPoint[1].y);
        int xb=Math.max(virtualPoint[0].x,virtualPoint[1].x);
        int yb=Math.max(virtualPoint[0].y,virtualPoint[1].y);
            
        if(isFilled) {
	        if(GeometricDistances.pointInRectangle(xa,ya,(xb-xa),(yb-ya),px,py))
	          	return 0;
   	     else
   		     	return Integer.MAX_VALUE;
   		}
   		
   		return GeometricDistances.pointToRectangle(xa,ya,(xb-xa),
   												(yb-ya),px,py);
   		
	}
	
	/** Obtain a string command descripion of the primitive.
		@return the FIDOCAD command line.
	*/
	public String toString(boolean extensions)
	{
		String cmd;
		
		if (isFilled)
			cmd="RP ";
		else
			cmd="RV ";
		
		return cmd+virtualPoint[0].x+" "+virtualPoint[0].y+" "+
			+virtualPoint[1].x+" "+virtualPoint[1].y+" "+
			getLayer()+"\n";
	}
	public void export(ExportInterface exp, MapCoordinates cs)
		throws IOException	
	{
		exp.exportRectangle(cs.mapX(virtualPoint[0].x,virtualPoint[0].y),
					   cs.mapY(virtualPoint[0].x,virtualPoint[0].y),
					   cs.mapX(virtualPoint[1].x,virtualPoint[1].y),
					   cs.mapY(virtualPoint[1].x,virtualPoint[1].y),
					   isFilled,
					   getLayer()); 
	}

}