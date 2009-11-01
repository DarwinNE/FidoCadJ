import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.awt.geom.*;



public class PrimitiveMacro extends GraphicPrimitive
{

	static final int N_POINTS=3;
	//private Map<String, String> library;
	private Map library;
	private Vector layers;
	private int o;
	private boolean m; 
	private boolean drawOnlyPads;
	private int drawOnlyLayer;
	private boolean alreadyExported;
	
	// Text sizes
	private int h,th, w1, w2;
	private int t_h,t_th, t_w1, t_w2;
	static final int text_size=3;

	String macroName;
	String macroDesc;
	
	String name;
	String value;

	
	/** Gets the number of control points used.
		@return the number of points used by the primitive
	*/
	
	public int getControlPointNumber()
	{
		return N_POINTS;
	}
	
	public PrimitiveMacro(Map lib, Vector l)
	{
		super();
		library=lib;
		layers=l;
		drawOnlyPads=false;
		drawOnlyLayer=-1;
		name="";
		value="";
		
		// A segment is defined by two points.
		virtualPoint = new Point[N_POINTS];
		for(int i=0;i<N_POINTS;++i)
			virtualPoint[i]=new Point();
		
	}
	
	public PrimitiveMacro(Map lib, Vector l, int x, int y, String key)
		throws IOException
	{
		super();
		library=lib;
		layers=l;
		key=key.toLowerCase();

		
		// A segment is defined by two points.
		virtualPoint = new Point[N_POINTS];
		for(int i=0;i<N_POINTS;++i)
			virtualPoint[i]=new Point();
		
		virtualPoint[0].x=x;
		virtualPoint[0].y=y;
		virtualPoint[1].x=x+10;
		virtualPoint[1].y=y+5;
		virtualPoint[2].x=x+10;
		virtualPoint[2].y=y+10;
		name="";
		value="";
		
		MacroDesc macro=(MacroDesc)library.get(key);
 			
 		if (macro==null){
 			
 			IOException G=new IOException("Unrecognized macro " 
 										  + key);
			throw G;
 		}
 		macroDesc = macro.description;
 		macroName = key;
 					
	}
	
	/** Writes the macro name and value fields
	
	*/
	private void drawText(Graphics2D g, MapCoordinates coordSys,
							  Vector layerV)
	{
		int x2=virtualPoint[1].x;
 		int y2=virtualPoint[1].y;
 		int x3=virtualPoint[2].x;
 		int y3=virtualPoint[2].y;
 		
 		if(drawOnlyLayer!=getLayer())
 			return;
 		
 		// At first, write the name and the value fields in the given positions
 		Font f = new Font("Courier",Font.PLAIN,
 				(int)( text_size*12*coordSys.getYMagnitude()/7+.5));
		
	   	g.setFont(f);
	   	FontMetrics fm = g.getFontMetrics(f);
    	
    	h = fm.getAscent();
    	th = h+fm.getDescent();
   		w1 = fm.stringWidth(name);
   		w2 = fm.stringWidth(value);
   		
   		t_h=coordSys.unmapYnosnap(h)-coordSys.unmapYnosnap(0);
   		t_th=coordSys.unmapYnosnap(th)-coordSys.unmapYnosnap(0);
   		t_w1=coordSys.unmapXnosnap(w1)-coordSys.unmapXnosnap(0);
   		t_w2=coordSys.unmapXnosnap(w2)-coordSys.unmapXnosnap(0);
   		
    	g.drawString(name,coordSys.mapX(x2,y2),coordSys.mapY(x2,y2)+h);	
    	g.drawString(value,coordSys.mapX(x3,y3),coordSys.mapY(x3,y3)+h);
    	
    	coordSys.trackPoint(coordSys.mapX(x2,y2),coordSys.mapY(x2,y2));
    	coordSys.trackPoint(coordSys.mapX(x3,y3),coordSys.mapY(x3,y3));
    	
		coordSys.trackPoint(coordSys.mapX(x2,y2)+t_w1,coordSys.mapY(x2,y2));
    	coordSys.trackPoint(coordSys.mapX(x3,y3)+t_w2,coordSys.mapY(x3,y3));
    	
  		coordSys.trackPoint(coordSys.mapX(x2,y2)+t_w1,
  			coordSys.mapY(x2,y2)+t_th);
    	coordSys.trackPoint(coordSys.mapX(x3,y3)+t_w2,
    		coordSys.mapY(x3,y3)+t_th);
  		
		
	}
	
	/**	Draw the macro contents
	
	*/
	private void drawMacroContents(Graphics2D g, MapCoordinates coordSys,
							  Vector layerV)
	{
		/* in the macro primitive, the the virtual point represents
		   the position of the reference point of the macro to be drawn. */
		   
		int x1=virtualPoint[0].x;
 		int y1=virtualPoint[0].y;
		
		
		// Then create and parse the macro
		MapCoordinates macroCoord=new MapCoordinates();
 		
 		
 			
 		macroCoord.setXMagnitude(coordSys.getXMagnitude());
		macroCoord.setYMagnitude(coordSys.getYMagnitude());

 		macroCoord.xCenter = coordSys.mapX(x1,y1);
 		macroCoord.yCenter= coordSys.mapY(x1,y1);
		macroCoord.orientation=(o+coordSys.orientation)%4;
		macroCoord.mirror=m ^ coordSys.mirror;
 		macroCoord.isMacro=true;
 		 		 			
 		ParseSchem macro=new ParseSchem();
 		macro.setMapCoordinates(macroCoord);
 		
 		macro.setLibrary(library); 			// Inherit the library
 		macro.setLayers(layerV);	// Inherit the layers
 		
 		//macroDesc=(String)library.get(macroName);
 		
 		macro.setDrawOnlyLayer(drawOnlyLayer);
 		
 		if (macroDesc==null)
 			System.out.println("Unrecognized macro "+
 			        "WARNING this can be a programming problem...");
 		else {
 			try {
 				macro.parseString(new StringBuffer(macroDesc)); 
 				// Recursive call	
 			} catch(IOException e) {
                	System.out.println("Error: "+e); 
            }
 			// Propagate selection state
 			if(getSelected())
 				macro.selectAll();
 			
 			macro.setDrawOnlyPads(drawOnlyPads);
 			macro.draw(g);
		}
		
		coordSys.trackPoint(macroCoord.getXMax(),macroCoord.getYMax());	
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
 		
	
		drawMacroContents(g, coordSys, layerV);
		drawText(g, coordSys, layerV);
		
		
 	}
 	
 	/** Draw the graphic primitive on the given graphic context.
		Normally, this method calls the usual draw method. Of course, if for a 
		given primitive (text for example) the drawing operations are too much
		slow, this method should be overridden in the derived class.
		
		@param g the graphic context in which the primitive should be drawn.
		@param coordSys the graphic coordinates system to be applied.
		@param layerDesc the layer description.
	*/
	public void drawFast(Graphics2D g, MapCoordinates coordSys,
							  Vector layerV)
	{
		drawMacroContents(g, coordSys, layerV);
		
		int x2=virtualPoint[1].x;
 		int y2=virtualPoint[1].y;
 		int x3=virtualPoint[2].x;
 		int y3=virtualPoint[2].y;
 		int xa=coordSys.mapX(x2,y2);
 		int ya=coordSys.mapY(x2,y2);
 		int xb=coordSys.mapX(x3,y3);
 		int yb=coordSys.mapY(x3,y3);
 		
		Font f = new Font("Courier",Font.PLAIN,12);
		
	   	g.setFont(f);
		FontMetrics fm = g.getFontMetrics(f);
    	
   		
		g.drawRect(Math.min(xa, xa+w1),ya,Math.abs(w1),th);
		g.drawRect(Math.min(xb, xb+w2),yb,Math.abs(w2),th);

	}
	
								  
 	public void setDrawOnlyPads(boolean pd)
 	{
 		drawOnlyPads=pd;
 	}
	
	public void setDrawOnlyLayer(int la)
 	{
 		drawOnlyLayer=la;
 	}
	
	
	public void setName(String[] tokens, int N)
		throws IOException
	{
		StringBuffer txtb=new StringBuffer();
		int j=8;
		
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
 			IOException E=new IOException("Invalid primitive:"+
 										  " programming error?");
			throw E;
 		} 
 	
		
	}
	public void setValue(String[] tokens, int N)
		throws IOException
	{
		StringBuffer txtb=new StringBuffer();
		int j=8;
		
		if (tokens[0].equals("TY")) {	// Text (advanced)
 			if (N<9) {
 				IOException E=new IOException("bad arguments on TY");
				throw E;
			}
			
 			virtualPoint[2].x=Integer.parseInt(tokens[1]);
 			virtualPoint[2].y=Integer.parseInt(tokens[2]);
 			
 		 					
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
	       	dt=0;
	    if(GeometricDistances.pointInRectangle(xb,yb,t_w2,t_th,px,py))
	       	dt=0;
	        	
	
 		MapCoordinates macroCoord=new MapCoordinates();
 			
 		macroCoord.setXMagnitude(1.0);
 		macroCoord.setYMagnitude(1.0);
 			
 		macroCoord.xCenter = 0;
 		macroCoord.yCenter= 0;
		macroCoord.orientation=o%4;
		macroCoord.mirror=m;
 		macroCoord.isMacro=true;
 			
 		ParseSchem macro=new ParseSchem();
 		macro.setMapCoordinates(macroCoord);
 		
 		macro.setLibrary(library); 	// Inherit the library
 		macro.setLayers(layers);	// Inherit the layers
 		
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
        
 			
 		
 		if (macroDesc==null)
 			System.out.println("Unrecognized macro "+
 			        "WARNING this can be a programming problem...");
 		else {
 			try {
 				macro.parseString(new StringBuffer(macroDesc)); 
 					// Recursive call	
 			} catch(IOException e) {
               	System.out.println("Error: "+e); 
            }
 			return Math.min(macro.distancePrimitive(vx, vy), dt);
		}
		return Integer.MAX_VALUE;
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
			
	}
	
	/** Mirror the primitive. For a macro, it is different than for the other
		primitive, since we just need to toggle the mirror flag.
		
	*/
	void mirrorPrimitive(int xpos)
	{
		super.mirrorPrimitive(xpos);
		m ^= true;
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
				
		
		if (!name.equals("") || !value.equals("")) {
			if(extensions) s+="FCJ\n";
			s+="TY "+virtualPoint[1].x+" "+virtualPoint[1].y+" "+
				text_size*4/3+" "+text_size+" "+"0"+" "+"0"+" "+getLayer()
				+" * "+name+"\n";
			s+="TY "+virtualPoint[2].x+" "+virtualPoint[2].y+" "+
				text_size*4/3+" "+text_size+" "+"0"+" "+"0"+" "+getLayer()
				+" * "+value+"\n";
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
		v.add(pd);
		
		pd = new ParameterDescription();
		
		pd.parameter=value;
		pd.description="Value:";
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
		replicated thru the layers. For this reason, there is an inibition system which is activated. Calling this method resets the inibition
		flag.
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
			virtualPoint[1].y, value, virtualPoint[2].x, virtualPoint[2].y)) {
			alreadyExported = true;
			return;
		}
		
		/* in the macro primitive, the virtual point represents
		   the position of the reference point of the macro to be drawn. */
		   
		int x1=virtualPoint[0].x;
 		int y1=virtualPoint[0].y;
 		
		MapCoordinates macroCoord=new MapCoordinates();
 			
 			
 		macroCoord.setXMagnitude(1);
		macroCoord.setYMagnitude(1);

 		macroCoord.xCenter = cs.mapX(x1,y1);
 		macroCoord.yCenter= cs.mapY(x1,y1);
		macroCoord.orientation=(o+cs.orientation)%4;
		macroCoord.mirror=m ^ cs.mirror;
 		macroCoord.isMacro=true;
 				 			
 		ParseSchem macro=new ParseSchem();
 		macro.setMapCoordinates(macroCoord);
 		macro.setDrawOnlyLayer(drawOnlyLayer);

 		
 		macro.setLibrary(library); 			// Inherit the library
 		macro.setLayers(layers);	// Inherit the layers
 		
 		if (macroDesc==null)
 			System.out.println("Unrecognized macro "+
 			        "WARNING this can be a programming problem...");
 		else {
 			macro.parseString(new StringBuffer(macroDesc)); // Recursive call	
 			// Propagate selection state
 			if(getSelected())
 				macro.selectAll();
 			 
 			macro.setDrawOnlyPads(drawOnlyPads);
 			macro.exportDrawing(exp, false);
 			
 			if(drawOnlyLayer==getLayer()) {
 				exp.exportAdvText (cs.mapX(virtualPoint[1].x,virtualPoint[1].y),
					cs.mapY(virtualPoint[1].x,virtualPoint[1].y), text_size, 
					(int)( text_size*12/7+.5),
					"Courier", 
					false,
					false,
					false,
					0, getLayer(), name);
				exp.exportAdvText (cs.mapX(virtualPoint[2].x,virtualPoint[2].y),
					cs.mapY(virtualPoint[2].x,virtualPoint[2].y), text_size, 
					(int)( text_size*12/7+.5),
					"Courier", 
					false,
					false,
					false,
					0, getLayer(), value);
			}
		}
		
	}
}