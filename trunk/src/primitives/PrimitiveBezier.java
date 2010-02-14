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



public class PrimitiveBezier extends GraphicPrimitive
{

	// A Bézier is defined by four points.
	static final int N_POINTS=4;
	
	private boolean arrowStart;
	private boolean arrowEnd;
	
	private int arrowLength;
	private int arrowHalfWidth;
	private int arrowStyle;	
	
	private int dashStyle;
	
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
		
		r = new Rectangle();
		
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
		@param arrowS Arrow to be drawn at the beginning of the curve
		@param arrowE Arrow to be drawn at the beginning of the curve
		@param arrowSt Arrow style
		@param dashSt Dash style
		
	*/
	
	public PrimitiveBezier(int x1, int y1, int x2, int y2, 
						 int x3, int y3, int x4, int y4, 
							int layer, boolean arrowS, boolean arrowE,
							int arrowSt, int arrowLe, int arrowWi, int dashSt)
	{
		super();
		
		arrowLength = arrowLe;
		arrowHalfWidth = arrowWi;
		arrowStart = arrowS;
		arrowEnd = arrowE;
		arrowStyle =arrowSt;
		dashStyle=dashSt;
		
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
		pd.parameter=new ArrowInfo(arrowStyle);
		pd.description="Arrow style";
		pd.isExtension = true;
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
		if (pd.parameter instanceof ArrowInfo)
			arrowStyle=((ArrowInfo)pd.parameter).style;
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
	
	private Shape shape1;
	private Stroke oldStroke;
	private float w;
	private Rectangle r;
		
	/** Draw the graphic primitive on the given graphic context.
		@param g the graphic context in which the primitive should be drawn.
		@param coordSys the graphic coordinates system to be applied.
		@param layerV the layer description.
	*/
	final public void draw(Graphics2D g, MapCoordinates coordSys,
							  Vector layerV)
	{
	
		if(!selectLayer(g,layerV))
			return;
			
		/* in the Bézier primitive, the four virtual points represent
		   the control point of the shape */
 		
 		
 			
 		shape1 = new CubicCurve2D.Float(
 			coordSys.mapX(virtualPoint[0].x,virtualPoint[0].y),
			coordSys.mapY(virtualPoint[0].x,virtualPoint[0].y),
			coordSys.mapX(virtualPoint[1].x,virtualPoint[1].y),
			coordSys.mapY(virtualPoint[1].x,virtualPoint[1].y),
			coordSys.mapX(virtualPoint[2].x,virtualPoint[2].y),
			coordSys.mapY(virtualPoint[2].x,virtualPoint[2].y),
			coordSys.mapX(virtualPoint[3].x,virtualPoint[3].y),
			coordSys.mapY(virtualPoint[3].x,virtualPoint[3].y));
			
	/* booh? it slows down the execution!
		r=g.getClipBounds(r);
		if(!shape1.intersects(r.x, r.y, r.width, r.height))
			return;
	*/	
 		
 		w = (float)(Globals.lineWidth*coordSys.getXMagnitude());
 		if (w<D_MIN) w=D_MIN;

	                                
		oldStroke=g.getStroke();
		if (dashStyle>0) 
			g.setStroke(new BasicStroke(w, 
                            	BasicStroke.CAP_BUTT, 
                                BasicStroke.JOIN_MITER, 
                                10.0f, Globals.dash[dashStyle], 0.0f));
		else 
			g.setStroke(new BasicStroke(w));
			
		g.draw(shape1);
 		g.setStroke(oldStroke);				 
 		
 		
 		
		if (arrowStart || arrowEnd) {
			int h=coordSys.mapXi(arrowHalfWidth,arrowHalfWidth,false)-
 				coordSys.mapXi(0,0, false);
			int l=coordSys.mapXi(arrowLength,arrowLength,false)-
				coordSys.mapXi(0,0, false);
	
 			if (arrowStart) Arrow.drawArrow(g, 		
 				coordSys.mapX(virtualPoint[0].x,virtualPoint[0].y),
				coordSys.mapY(virtualPoint[0].x,virtualPoint[0].y),
				coordSys.mapX(virtualPoint[1].x,virtualPoint[1].y),
				coordSys.mapY(virtualPoint[1].x,virtualPoint[1].y), l, h, 
				arrowStyle);
		
			if (arrowEnd) Arrow.drawArrow(g, 		
 				coordSys.mapX(virtualPoint[3].x,virtualPoint[3].y),
				coordSys.mapY(virtualPoint[3].x,virtualPoint[3].y),
				coordSys.mapX(virtualPoint[2].x,virtualPoint[2].y),
				coordSys.mapY(virtualPoint[2].x,virtualPoint[2].y), l, h, 
				arrowStyle);
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
 			
 			if(N>10 && tokens[10].equals("FCJ")) {
 				int arrows = Integer.parseInt(tokens[11]);
 				arrowStart = (arrows & 0x01) !=0;
 				arrowEnd = (arrows & 0x02) !=0;
 				
 				arrowStyle = Integer.parseInt(tokens[12]);
 				arrowLength = Integer.parseInt(tokens[13]);
 				arrowHalfWidth = Integer.parseInt(tokens[14]);
 				dashStyle = Integer.parseInt(tokens[15]);
 				// Parameters validation and correction
				if(dashStyle>=Globals.dashNumber)
					dashStyle=Globals.dashNumber-1;
				if(dashStyle<0)
					dashStyle=0;
 			}	
  			
 			
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
			
		
		String s = "BE "+virtualPoint[0].x+" "+virtualPoint[0].y+" "+
			+virtualPoint[1].x+" "+virtualPoint[1].y+" "+
			+virtualPoint[2].x+" "+virtualPoint[2].y+" "+
			+virtualPoint[3].x+" "+virtualPoint[3].y+" "+
			getLayer()+"\n";
			
		if(extensions) {
		 	int arrows = (arrowStart?0x01:0x00)|(arrowEnd?0x02:0x00);
		 			 	
		 	if (arrows>0 || dashStyle>0) 
		 		s+="FCJ "+arrows+" "+arrowStyle+" "+arrowLength+" "+
		 		arrowHalfWidth+" "+dashStyle+"\n";
		
		}
		return s;
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
					   getLayer(),
					   arrowStart, arrowEnd, arrowStyle, arrowLength, 
					   arrowHalfWidth, dashStyle); 
	}
}