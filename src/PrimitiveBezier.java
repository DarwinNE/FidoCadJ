import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.awt.geom.*;



public class PrimitiveBezier extends GraphicPrimitive
{

	// A Bézier is defined by four points.
	static final int N_POINTS=4;
	
	
	/** Gets the number of control points used.
		@return the number of points used by the primitive
	*/
	
	public int getControlPointNumber()
	{
		return N_POINTS;
	}
	
	public PrimitiveBezier()
	{
		super();
		
		virtualPoint = new Point[N_POINTS];
		for(int i=0;i<N_POINTS;++i)
			virtualPoint[i]=new Point();
		
	}
	/** Create a Bézier curve specified by four control points
		@param x1 the x coordinate (logical unit) of P1.
		@param y1 the y coordinate (logical unit) of P1.
		@param x2 the x coordinate (logical unit) of P2.
		@param y2 the y coordinate (logical unit) of P2.
		@param x3 the x coordinate (logical unit) of P3.
		@param y3 the y coordinate (logical unit) of P3.
		@param x4 the x coordinate (logical unit) of P4.
		@param y4 the y coordinate (logical unit) of P4.
		@param layer the layer to be used.
		
	*/
	
	public PrimitiveBezier(int x1, int y1, int x2, int y2, 
						 int x3, int y3, int x4, int y4, 
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
		virtualPoint[2].x=x3;
		virtualPoint[2].y=y3;
		virtualPoint[3].x=x4;
		virtualPoint[3].y=y4;
		
		
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
			
		/* in the Bézier primitive, the four virtual points represent
		   the control point of the shape */
 		
 		
 			
 		Shape shape1 = new CubicCurve2D.Float(
 			coordSys.mapX(virtualPoint[0].x,virtualPoint[0].y),
			coordSys.mapY(virtualPoint[0].x,virtualPoint[0].y),
			coordSys.mapX(virtualPoint[1].x,virtualPoint[1].y),
			coordSys.mapY(virtualPoint[1].x,virtualPoint[1].y),
			coordSys.mapX(virtualPoint[2].x,virtualPoint[2].y),
			coordSys.mapY(virtualPoint[2].x,virtualPoint[2].y),
			coordSys.mapX(virtualPoint[3].x,virtualPoint[3].y),
			coordSys.mapY(virtualPoint[3].x,virtualPoint[3].y));
			
							 
 		g.draw(shape1);			
		
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
		if (tokens[0].equals("BE")) {	// Bézier
 			if (N<9) {
 				IOException E=new IOException("bad arguments on BE");
				throw E;
 			}
 			virtualPoint[0].x=Integer.parseInt(tokens[1]);
 			virtualPoint[0].y=Integer.parseInt(tokens[2]);
 			virtualPoint[1].x=Integer.parseInt(tokens[3]);
 			virtualPoint[1].y=Integer.parseInt(tokens[4]);
 			virtualPoint[2].x=Integer.parseInt(tokens[5]);
 			virtualPoint[2].y=Integer.parseInt(tokens[6]);
 			virtualPoint[3].x=Integer.parseInt(tokens[7]);
 			virtualPoint[3].y=Integer.parseInt(tokens[8]);
 			
 			if(N>9) parseLayer(tokens[9]);
 			
 			
 		} else {
 			IOException E=new IOException("Invalid primitive: "+
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
		return GeometricDistances.pointToBezier(
				virtualPoint[0].x, virtualPoint[0].y,
				virtualPoint[1].x, virtualPoint[1].y,
				virtualPoint[2].x, virtualPoint[2].y,
				virtualPoint[3].x, virtualPoint[3].y,
				px,  py);
		
	}
	
	/** Obtain a string command descripion of the primitive.
		@return the FIDOCAD command line.
	*/
	public String toString(boolean extensions)
	{
		String cmd;
			
		
		return "BE "+virtualPoint[0].x+" "+virtualPoint[0].y+" "+
			+virtualPoint[1].x+" "+virtualPoint[1].y+" "+
			+virtualPoint[2].x+" "+virtualPoint[2].y+" "+
			+virtualPoint[3].x+" "+virtualPoint[3].y+" "+
			getLayer()+"\n";
	}
	
	public void export(ExportInterface exp, MapCoordinates cs) 
		throws IOException
	{
		exp.exportBezier(cs.mapX(virtualPoint[0].x,virtualPoint[0].y),
					   cs.mapY(virtualPoint[0].x,virtualPoint[0].y),
					   cs.mapX(virtualPoint[1].x,virtualPoint[1].y),
					   cs.mapY(virtualPoint[1].x,virtualPoint[1].y),
					   cs.mapX(virtualPoint[2].x,virtualPoint[2].y),
					   cs.mapY(virtualPoint[2].x,virtualPoint[2].y),
					   cs.mapX(virtualPoint[3].x,virtualPoint[3].y),
					   cs.mapY(virtualPoint[3].x,virtualPoint[3].y),
					   getLayer()); 
	}
}