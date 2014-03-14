package graphic.nil;

import graphic.*;
import geom.*;
import layers.*;

import android.graphics.*;
import android.graphics.Paint.*;


/** 	ANDROID VERSION

	Null graphic class. Does nothing. Nil. Zero. :-)
	Except... calculate text size correctly!

	Yes. There is a reason for that.
	    
<pre>
    This file is part of FidoCadJ.

    FidoCadJ is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FidoCadJ is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY{} without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with FidoCadJ.  If not, see <http://www.gnu.org/licenses/>.

    Copyright 2014 by Davide Bucci
</pre>
*/

public class GraphicsNull implements GraphicsInterface 
{

	Canvas g;
	
	public GraphicsNull()
	{

	}
	
	public void setColor(ColorInterface c) 
	{
		// nothing to do
	}
	public ColorInterface getColor()
	{
		return new ColorNull();
	}
	
	/** Retrieves or create a BasicStroke object having the wanted with and
		style and apply it to the current graphic context.
		@param w the width in pixel
		@param dashStyle the style of the stroke
	*/
	public void applyStroke(float w, int dashStyle)
	{
		// nothing to do
	}
	
	/** Draws a rectangle on the current graphic context.
		@param x the x coordinate of the uppermost left corner
		@param y the y coordinate of the uppermost left corner
		@param width the width of the rectangle
		@param height the height of the rectangle
	*/
	public void drawRect(int x, int y, int width, int height)
	{
		// nothing to do
	}
	
	/** Fills a rectangle on the current graphic context.
		@param x the x coordinate of the uppermost left corner
		@param y the y coordinate of the uppermost left corner
		@param width the width of the rectangle
		@param height the height of the rectangle
	*/
	public void fillRect(int x, int y, int width, int height)
	{
		// nothing to do	
	}
	
    public void fillRoundRect(int x,
                                   int y,
                                   int width,
                                   int height,
                                   int arcWidth,
                                   int arcHeight)
    {
		// nothing to do    
    }

    public boolean hitClip(int x,
                       int y,
                       int width,
                       int height)
    {
    	return false;
    }
	public void drawLine(int x1,
                              int y1,
                              int x2,
                              int y2)
	{		
		// nothing to do
	}
                              
	public void setFont(String name, int size, boolean isItalic, 
		boolean isBold)
	{

	}
	
	public void setFont(String name, int size)
	{
		setFont(name, size, false, false);
	}
	
	/** TODO: is there a way to implement something without a graphic context?
	*/
	public int getFontAscent()
	{
		return 0;
	}
	/** TODO: is there a way to implement something without a graphic context?
	*/
	public int getFontDescent()
	{
		return 0;
	}
	/** TODO: is there a way to implement something without a graphic context?
	*/
	public int getStringWidth(String s)
	{
		return 0;
	}

	
	public void drawString(String str,
                                int x,
                                int y)
    {
  		// nothing to do
    }
    
    public void setAlpha(float alpha)
    {
		// nothing to do    
    }

    public void fillOval(int x,
                              int y,
                              int width,
                              int height)
	{
		// nothing to do	
	}
	public void drawOval(int x,
                              int y,
                              int width,
                              int height)
    {
		// nothing to do
    }                          
    public void fill(ShapeInterface s)
    {
		// nothing to do
    }
    public void draw(ShapeInterface s)
    {
		// nothing to do
    }
    public void fillPolygon(PolygonInterface p)
    {		
    	// nothing to do
    }
    public void drawPolygon(PolygonInterface p)
    {
		// nothing to do    
    }
    
    public void activateSelectColor(LayerDesc l)
    {
		// nothing to do    
    }
    
    public void drawAdvText(double xyfactor, int xa, int ya,
  		int qq, int h, int w, int th, boolean needsStretching,
  		int orientation, boolean mirror,		
  		String txt)
  	{
		// nothing to do
  	}
    
    public void drawGrid(MapCoordinates cs, 
    	int xmin, int ymin, 
    	int xmax, int ymax)
    {
    		// nothing to do
    }
    	
    	
    public PolygonInterface createPolygon()
    {
    	return new PolygonNull();
    }
    public ColorInterface createColor()
    {
    	return new ColorNull();
    }
	public ShapeInterface createShape()
	{
		return new ShapeNull();
	}
}