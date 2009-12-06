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



public class PrimitiveOval extends GraphicPrimitive
{

	// An oval is defined by two points.
	static final int N_POINTS=2;
	private boolean isFilled;
	private int dashStyle;

	/** Gets the number of control points used.
		@return the number of points used by the primitive
	*/
	
	public int getControlPointNumber()
	{
		return N_POINTS;
	}
	
	public PrimitiveOval()
	{
		super();
		isFilled=false;
		
		virtualPoint = new Point[N_POINTS];
		for(int i=0;i<N_POINTS;++i)
			virtualPoint[i]=new Point();
		
	}
	/** Create an oval defined by two points
		@param x1 the start x coordinate (logical unit).
		@param y1 the start y coordinate (logical unit).
		@param x2 the end x coordinate (logical unit).
		@param y2 the end y coordinate (logical unit).
		@param f specifies if the ellipse should be filled 
		@param layer the layer to be used.
		
	*/
	
	public PrimitiveOval(int x1, int y1, int x2, int y2, boolean f, int layer)
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
		/* in the oval primitive, the first two virtual points represent
		   the two corners of the segment */
 		
 		int xa=Math.min(coordSys.mapX(virtualPoint[0].x,virtualPoint[0].y),
 		             coordSys.mapX(virtualPoint[1].x,virtualPoint[1].y));
 		int ya=Math.min(coordSys.mapY(virtualPoint[0].x,virtualPoint[0].y),
 		             coordSys.mapY(virtualPoint[1].x,virtualPoint[1].y));
 		int xb=Math.max(coordSys.mapX(virtualPoint[0].x,virtualPoint[0].y),
 		             coordSys.mapX(virtualPoint[1].x,virtualPoint[1].y));
 		int yb=Math.max(coordSys.mapY(virtualPoint[0].x,virtualPoint[0].y),
 		             coordSys.mapY(virtualPoint[1].x,virtualPoint[1].y));
 		
 		if(!g.hitClip(xa,ya, (xb-xa),(yb-ya)))
 				return;
 			
 		Stroke oldStroke;
			
 		float w = (float)(Globals.lineWidthCircles*coordSys.getXMagnitude());

				
		BasicStroke dashed = new BasicStroke(w, 
                                         BasicStroke.CAP_BUTT, 
                                         BasicStroke.JOIN_MITER, 
                                         10.0f, Globals.dash[dashStyle], 0.0f);
                                         
		oldStroke=g.getStroke();                                 
		if (dashStyle>0) 
			g.setStroke(dashed);
		else 
			g.setStroke(new BasicStroke(w));		
  		
        
 		if (isFilled)
 			g.fillOval(xa,ya,(xb-xa),(yb-ya));
 		else {
			if(xb!=xa || yb!=ya)
	 			g.drawOval(xa,ya,(xb-xa),(yb-ya));
 		}
	    g.setStroke(oldStroke);
 		
 		coordSys.trackPoint(xa,ya);
 		coordSys.trackPoint(xb,yb);
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
		if (tokens[0].equals("EV")||tokens[0].equals("EP")) {	// Oval
 			if (N<5) {
 				IOException E=new IOException("bad arguments on EV/EP");
				throw E;
 			}
 			virtualPoint[0].x=Integer.parseInt(tokens[1]);
 			virtualPoint[0].y=Integer.parseInt(tokens[2]);
 			virtualPoint[1].x=Integer.parseInt(tokens[3]);
 			virtualPoint[1].y=Integer.parseInt(tokens[4]);
 			if(N>5) parseLayer(tokens[5]);
 			if(tokens[0].equals("EP"))
 				isFilled=true;
 			else
 				isFilled=false;
 			if(N>6 && tokens[6].equals("FCJ")) {
 				dashStyle = Integer.parseInt(tokens[7]);
 				// Parameters validation and correction
				if(dashStyle>=Globals.dashNumber)
					dashStyle=Globals.dashNumber-1;
				if(dashStyle<0)
					dashStyle=0;
 			}
 		} else {
 			IOException E=new IOException("EV/EP: Invalid primitive:"+tokens[0]+
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
		pd = new ParameterDescription();
		pd.parameter=new DashInfo(dashStyle);
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
		
		pd=(ParameterDescription)v.get(i);
		++i;
		// Check, just for sure...
		if (pd.parameter instanceof Boolean)
			isFilled=((Boolean)pd.parameter).booleanValue();
		else
		 	System.out.println("Warning: unexpected parameter!"+pd);
		pd=(ParameterDescription)v.get(i++);
		if (pd.parameter instanceof DashInfo)
			dashStyle=((DashInfo)pd.parameter).style;
		else
		 	System.out.println("Warning: unexpected parameter!"+pd);
		
		// Parameters validation and correction
		if(dashStyle>=Globals.dashNumber)
			dashStyle=Globals.dashNumber-1;
		if(dashStyle<0)
			dashStyle=0;
		
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
     
     	if(isFilled){
        	if(GeometricDistances.pointInEllipse(xa,ya,(xb-xa),(yb-ya),px,py))
          		return 0;
          	else
          		return Integer.MAX_VALUE;
        } else
        	return GeometricDistances.pointToEllipse(xa,ya,
        		(xb-xa),(yb-ya),px,py);
          	
        
        
	}
	
	/** Obtain a string command descripion of the primitive.
		@return the FIDOCAD command line.
	*/
	public String toString(boolean extensions)
	{
		String cmd;
		
		if (isFilled)
			cmd="EP ";
		else
			cmd="EV ";
		
		cmd+=virtualPoint[0].x+" "+virtualPoint[0].y+" "+
			+virtualPoint[1].x+" "+virtualPoint[1].y+" "+
			getLayer()+"\n";
			
		if(extensions) {
			if (dashStyle>0) 
		 		cmd+="FCJ "+dashStyle+"\n";
		
		}
		return cmd;
	}
	
	public void export(ExportInterface exp, MapCoordinates cs) 
		throws IOException
	{
		exp.exportOval(cs.mapX(virtualPoint[0].x,virtualPoint[0].y),
					   cs.mapY(virtualPoint[0].x,virtualPoint[0].y),
					   cs.mapX(virtualPoint[1].x,virtualPoint[1].y),
					   cs.mapY(virtualPoint[1].x,virtualPoint[1].y),
					   isFilled,
					   getLayer(),
					   dashStyle); 
	}

}