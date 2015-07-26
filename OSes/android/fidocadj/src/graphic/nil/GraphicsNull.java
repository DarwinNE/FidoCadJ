package graphic.nil;

import graphic.*;
import net.sourceforge.fidocadj.geom.*;
import net.sourceforge.fidocadj.layers.*;

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
	Paint paint;
	
	public GraphicsNull()
	{
		paint = new Paint();
	}
	
	public void setColor(ColorInterface c) 
	{
		// Nothing to do
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
		// Nothing to do
	}
	
	/** Draws a rectangle on the current graphic context.
		@param x the x coordinate of the uppermost left corner
		@param y the y coordinate of the uppermost left corner
		@param width the width of the rectangle
		@param height the height of the rectangle
	*/
	public void drawRect(int x, int y, int width, int height)
	{
		// Nothing to do
	}
	
	/** Fills a rectangle on the current graphic context.
		@param x the x coordinate of the uppermost left corner
		@param y the y coordinate of the uppermost left corner
		@param width the width of the rectangle
		@param height the height of the rectangle
	*/
	public void fillRect(int x, int y, int width, int height)
	{
		// Nothing to do
	}
	
    public void fillRoundRect(int x,
                                   int y,
                                   int width,
                                   int height,
                                   int arcWidth,
                                   int arcHeight)
    {
		// Nothing to do
    }

    public boolean hitClip(int x,
                       int y,
                       int width,
                       int height)
    {
    	return true;
    }
	public void drawLine(int x1,
                              int y1,
                              int x2,
                              int y2)
	{
		// Nothing to do
	}
                              
	public void setFont(String name, int size, boolean isItalic, 
		boolean isBold)
	{
		int style;
		if(isBold && isItalic) 
			style=Typeface.BOLD_ITALIC;
		else if (isBold)
			style=Typeface.BOLD;
		else if (isItalic)
			style=Typeface.ITALIC;
		else
			style=Typeface.NORMAL;
		
		Typeface tf = Typeface.create(name, style);
   		paint.setTypeface(tf);
   		paint.setTextSize(size);
	}
	
	public void setFont(String name, int size)
	{
		setFont(name, size, false, false);
	}
	

	public int getFontAscent()
	{
		// Note: an ascent is "going up", so it is negative 
		// as in the FontMetrics documentation...
		// FidoCadJ requires a positive size, instead.
		return -(int)paint.getFontMetrics().ascent;
	}

	public int getFontDescent()
	{
		return  (int)paint.getFontMetrics().descent;
	}

	public int getStringWidth(String s)
	{
		return (int)paint.measureText(s);
	}

	public void drawString(String str,
                                int x,
                                int y)
    {
		// Nothing to do
    }
    
    public void setAlpha(float alpha)
    {
		// Nothing to do
    }

    public void fillOval(int x,
                              int y,
                              int width,
                              int height)
	{
		// Nothing to do
	}
	public void drawOval(int x,
                              int y,
                              int width,
                              int height)
    {
		// Nothing to do
    }                          
    public void fill(ShapeInterface s)
    {
		// Nothing to do
    }
    public void draw(ShapeInterface s)
    {
		// Nothing to do
    }
    public void fillPolygon(PolygonInterface p)
    {		
		// Nothing to do
    }
    public void drawPolygon(PolygonInterface p)
    {
		// Nothing to do
    }
    
    public void activateSelectColor(LayerDesc l)
    {
		// Nothing to do
    }
    
    public void drawAdvText(double xyfactor, int xa, int ya,
  		int qq, int h, int w, int th, boolean needsStretching,
  		int orientation, boolean mirror,		
  		String txt)
  	{
		// Nothing to do
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
	
	/** Retrieve the current screen density in dots-per-inch.
		@return the screen resolution (density) in dots-per-inch.
	*/
	public float getScreenDensity()
	{
		// The magic number should not be important in this context
		return 42;
	}	
}