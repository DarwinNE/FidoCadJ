package primitives;

import java.awt.*;
import java.io.*;
import java.util.*;

import layers.*;
import dialogs.*;
import geom.*;
import export.*;
/*
	GraphicPrimitive is an abstract class implementing the basic behaviour
	of a graphic primitive, which should be derived from it.
	

	<pre>
   Written by Davide Bucci, March-April 2007, davbucci at tiscali dot it
   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

	</pre>

*/
public abstract class GraphicPrimitive
{	
	// Tell that the dragging handle is invalid
	public static final int NO_DRAG=-1;
	// Tell that we are dragging the whole primitive
	public static final int DRAG_PRIMITIVE=-2;	
	// Tell that we want to perform a selection in a rectangular area
	public static final int RECT_SELECTION=-3;
	
	// Handle dimension in pixels 
	private static final int HANDLE_WIDTH=8;

	// Maximum number of tokens
	private static final int MAX_TOKENS=120;
	
	// Number of layers to be treated
	private static final int MAX_LAYERS=16;
	
	// Indicates wether the primitive is selected or not
	private boolean selectedState;
	
	// Minimum width size of a line in pixel
	protected static final float D_MIN = 0.5f; 
	
	protected boolean changed;
	
	// The layer
	private int layer;
	
	// Array containing the points defining the primitive
	public Point[] virtualPoint;
	
	/* At first, non abstract methods */
	
	
	/** Standard constructor */
	public void GraphicPrimitive()
	{
		selectedState=false;
		layer=0;
        changed=true;

	}
	
    public final void setChanged(boolean c)
    {
    	changed=c;
    }

	
	/** Get the first control point of the primitive
	
	*/
	public Point getFirstPoint()
	{
		return virtualPoint[0];
	}
	
	/** Move the primitive. 
	
		@param dx the relative x displacement (logical units)
		@param dy the relative y displacement (logical units)
		
	*/
	public void movePrimitive(int dx, int dy)
	{
		int a, n=getControlPointNumber();		
		
		for(a=0; a<n; a++)
		{
			virtualPoint[a].x+=dx;	
			virtualPoint[a].y+=dy;	
		}
		changed=true;	
	}
	
	/** Mirror the primitive. Adapted from Lorenzo Lutti's original code.
		@param xPos is the symmetry axis
		
	*/
	public void mirrorPrimitive(int xPos)
	{
		int a, n=getControlPointNumber();		
		int xtmp;
		
		
		for(a=0; a<n; a++)
		{
			xtmp = virtualPoint[a].x;			
			virtualPoint[a].x = 2*xPos - xtmp;
		}
		changed=true;	
	}
	
	/** Rotate the primitive. Adapted from Lorenzo Lutti's original code.
		@param bCounterClockWise specify if the rotation should be done 
				counterclockwise.
				
		@param ix the x coordinate of the center of rotation
		@param iy the y coordinate of the center of rotation
	*/
	public void rotatePrimitive(boolean bCounterClockWise, int ix, int iy)
	{
	
		int b, m=getControlPointNumber();
		Point ptTmp=new Point();
		Point pt=new Point();
		
		pt.x=ix;
		pt.y=iy;
	
		for( b=0; b<m; ++b)
		{
			ptTmp.x = virtualPoint[b].x;
			ptTmp.y = virtualPoint[b].y;

			if( !bCounterClockWise)	{
				virtualPoint[b].x = pt.x - (ptTmp.y-pt.y);
				virtualPoint[b].y = pt.y + (ptTmp.x-pt.x);
			} else {
				virtualPoint[b].x = pt.x + (ptTmp.y-pt.y);
				virtualPoint[b].y = pt.y - (ptTmp.x-pt.x);
			}
		}
		
		changed=true;	
	}
	
	
	/** Set the primitive as selected. 
		@param s the new state*/
	final public void setSelected(boolean s)
	{
		selectedState=s;
		//changed=true;	
	};
	
	/** Get the selection state of the primitive.
		@return true if the primitive is selected, false otherwise. */
	final public boolean getSelected()
	{
		return selectedState;
	}
	
	/** Get the layer of the current primitive.
		@return the layer number.
	*/
	public final int getLayer()
	{
		return layer;
	}
	
	public void parseLayer(String token)
 	{
 		int l;
 		try{   
			l=Integer.parseInt(token);
		
		} catch (NumberFormatException E)
		{	// We are unable to get the layer. Just suppose it's zero.
			l=0;
		}
		
		if (layer<0 || layer>=MAX_LAYERS)
			layer=0;
		else
			layer=l;
		changed=true;	
	}
 	
	/** Set the layer of the current primitive. A quick check is done.
		@param l the desired layer. 
	*/
	final public void setLayer(int l)
	{
		if (l<0 || l>=MAX_LAYERS)
			layer=0;
		else
			layer=l;
		//changed=true;	
	}
	
	private LayerDesc l;
	/**	Treat the current layer. In particular, select the corresponding
		color in the actual graphic context. If the primitive is selected,
		select the corrisponding color. This is a speed sensitive context.
		
		@param g the graphic context used for the drawing.
		@param layerV a LayerDesc vector with the descriptions of the layers
				being used.
		
	*/
	protected final boolean selectLayer(Graphics2D g, ArrayList layerV)
	{
		l= (LayerDesc)layerV.get(layer);
		if (!l.getVisible())
			return false;
			
		if(selectedState)
			g.setColor(Color.green);
		else
			g.setColor(l.getColor());
			
		return true;
	}
	
	/**	Draw the handles for the current primitive.
		@param g the graphic context to be used.
		@param cs the coordinate mapping used.
	
	*/
	public void drawHandles(Graphics2D g, MapCoordinates cs)
	{
		int xa;
		int ya;
		
		g.setColor(Color.red);
		for(int i=0;i<getControlPointNumber();++i) {
			xa=cs.mapX(virtualPoint[i].x,virtualPoint[i].y);
 			ya=cs.mapY(virtualPoint[i].x,virtualPoint[i].y);


 			if(!g.hitClip(xa-HANDLE_WIDTH/2,ya-HANDLE_WIDTH/2,
 							HANDLE_WIDTH,HANDLE_WIDTH))
 				continue;
 			
	 		g.fillRect(xa-HANDLE_WIDTH/2,ya-HANDLE_WIDTH/2,
 							HANDLE_WIDTH,HANDLE_WIDTH);
 		}	
	}
	
	/**	Tells if the pointer is on an handle.
	
		@param cs the coordinate mapping used.
		@param px the x (screen) coordinate of the pointer. 
		@param py the y (screen) coordinate of the pointer.
		@return NO_DRAG if the pointer is not on an handle, or the index of the 
			handle selected.
	*/
	public int onHandle(MapCoordinates cs, int px, int py)
	{
		int xa;
		int ya;
		
		for(int i=0;i<getControlPointNumber();++i) {
			xa=cs.mapX(virtualPoint[i].x,virtualPoint[i].y);
 			ya=cs.mapY(virtualPoint[i].x,virtualPoint[i].y);


 			if(GeometricDistances.pointInRectangle(xa-HANDLE_WIDTH/2,
 							ya-HANDLE_WIDTH/2,
 							HANDLE_WIDTH,HANDLE_WIDTH,px,py))
 				return i;
 			
	 		
 		}	
 		
 		return NO_DRAG;
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
		for(int i=0;i<getControlPointNumber();++i) {
			int xa=virtualPoint[i].x;
 			int ya=virtualPoint[i].y;
 			
 			if(((px<=xa)&&(xa<(px+w)) && ((py<=ya)&&(ya<(py+h))))) {
 				setSelected(true);
 				return true;
 			}
		}
		
		return false;
	}
	
	/** Draw the graphic primitive on the given graphic context.
		Normally, this method calls the usual draw method. Of course, if for a 
		given primitive (text for example) the drawing operations are too much
		slow, this method should be overridden in the derived class.
		
		@param g the graphic context in which the primitive should be drawn.
		@param coordSys the graphic coordinates system to be applied.
		@param LayerDesc the layer description.
	*/
	public void drawFast(Graphics2D g, MapCoordinates coordSys,
							  ArrayList LayerDesc)
	{
		draw(g, coordSys, LayerDesc);
	}
	
	/**	Get the control parameters of the given primitive. Each 
		primitive should probably overload this version. We give here a very 
		general implementation, allowing to change only virtual points.
		
		@return a vector of ParameterDescription containing each control
				parameter.
				The first parameters should always be the virtual points.
				
	*/
	public Vector getControls()
	{
		int i;
		Vector v = new Vector(10);
		ParameterDescription pd;
		
		for (i=0;i<getControlPointNumber();++i) {
			pd = new ParameterDescription();
			pd.parameter=virtualPoint[i];
			pd.description="Control point "+(i+1)+":";
			v.add(pd);
		}
		
		pd = new ParameterDescription();
		pd.parameter=new LayerInfo(layer);
		pd.description="Layer:";
		v.add(pd);
		
		return v;
	}
		
	/**	Set the control parameters of the given primitive. Each 
		primitive should probably overload this version. We give here a very 
		general implementation, allowing to change only virtual points.
		This method is specular to getControls().
		
		@param v a vector of ParameterDescription containing each control
				parameter.
				The first parameters should always be the virtual points.
				
	*/
	public void setControls(Vector v)
	{
		int i;
		ParameterDescription pd;
		changed=true;	
		for (i=0;i<getControlPointNumber();++i) {
			pd = (ParameterDescription)v.get(i);
			
			// Check, just for sure...
			if (pd.parameter instanceof Point)
				virtualPoint[i]=(Point)pd.parameter;
			else
			 	System.out.println("Warning: unexpected parameter!");
			
		}
		pd = (ParameterDescription)v.get(i);
		// Check, just for sure...
		if (pd.parameter instanceof LayerInfo)
			layer=((LayerInfo)pd.parameter).getLayer();
		else
		 	System.out.println("Warning: unexpected parameter!");
	}
	
	/** Draw the graphic primitive on the given graphic context.
		@param g the graphic context in which the primitive should be drawn.
		@param coordSys the graphic coordinates system to be applied.
		@param LayerDesc the layer description.
	*/
	public abstract void draw(Graphics2D g, MapCoordinates coordSys,
							  ArrayList LayerDesc);
	
	/**	Parse a token array and store the graphic data for a given primitive
		Obviously, that routine should be called *after* having recognized
		that the called primitive is correct.
		That routine also sets the correct layer.
		An IOException is thrown if there is an error.
		
		@param tokens the tokens to be processed. tokens[0] should be the
		command of the actual primitive.
		@param N the number of tokens present in the array
		@param extra1 a first extra parameter
		@param extra2 a second extra parameter
	*/
	public abstract void parseTokens(String[] tokens, int N)
		throws IOException;
	
	
	/** Gets the distance (in primitive's coordinates space) between a 
	    given point and the primitive. 
	    When it is reasonable, the behaviour can be binary (polygons, 
	    ovals...). In other cases (lines, points), it can be proportional.
		@param px the x coordinate of the given point
		@param py the y coordinate of the given point
	*/
	public abstract int getDistanceToPoint(int px, int py);

	/** Gets the number of control points used.
		@return the number of points used by the primitive
	*/
	
	public abstract int getControlPointNumber();
	
	/** Obtain a string command descripion of the primitive.
		@param extensions produce a string eventually containing FidoCadJ
			extensions over the original FidoCad format.
		@return the FIDOCAD command line.
		
	*/
	
	public abstract String toString(boolean extensions);
	
	/**	Each graphic primitive should call the appropriate exporting method
		of the export interface specified.
		
		@param exp the export interface that should be used
		@param cs the actual coordinate mapping
	
	*/
	
	public abstract void export(ExportInterface exp, MapCoordinates cs)
		throws IOException;
}

