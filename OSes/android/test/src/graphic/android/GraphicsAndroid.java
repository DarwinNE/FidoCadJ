package graphic.android;

import android.graphics.*;
import android.graphics.Paint.*;

import graphic.*;
import geom.*;
import layers.*;
import globals.*;


/** Android graphic class.
	    
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

public class GraphicsAndroid implements GraphicsInterface 
{
	Canvas canvas;
	
	float actual_w;
	int actual_dash;
	
	// By default, we keep a "STROKE" paint. 
	Paint paint;
	
	/** Standard constructor. 
	*/
	public GraphicsAndroid(Canvas c)
	{
		canvas=c;
		paint = new Paint();
		paint.setColor(Color.BLUE);
		paint.setStyle(Style.STROKE);
		paint.setStrokeCap(Cap.ROUND);
		paint.setStrokeJoin(Join.ROUND);        
        paint.setAntiAlias(true);
        actual_w=-1.0f;
        actual_dash = 0;
	}
	
	/** Set the current drawing color.
		@param c the color to be used. It must be an instance of ColorAndroid
		in this context.
	*/
	public void setColor(ColorInterface c) 
	{
		ColorAndroid ca = (ColorAndroid)c;
		paint.setColor(ca.getColorAndroid());
	}
	
	/** Gets the color being used. 
		@return the current color. It is an instance of ColorAndroid.
	*/
	public ColorInterface getColor()
	{
		return new ColorAndroid(paint.getColor());
	}
	
	/** Retrieves or create a BasicStroke object having the wanted with and
		style and apply it to the current graphic context.
		@param w the width in pixel
		@param dashStyle the style of the stroke
	*/
	public void applyStroke(float w, int dashStyle)
	{
		// We check if we need to change anything.
		if(actual_w!=w || dashStyle!=actual_dash) {
			paint.setStrokeWidth(w);
			if (dashStyle==0) {
				paint.setPathEffect(null);
			} else {
				paint.setPathEffect(new DashPathEffect(Globals.dash[dashStyle],	
				 	0.0f));
        	}
        	actual_dash = dashStyle;
        	actual_w = w;
        }
	}
	
	/** Draws a rectangle on the current graphic context.
		@param x the x coordinate of the uppermost left corner
		@param y the y coordinate of the uppermost left corner
		@param width the width of the rectangle
		@param height the height of the rectangle
	*/
	public void drawRect(int x, int y, int width, int height)
	{
		// Dashing effects can be applied only to paths.
		// If this is not needed, we use the simple call drawLine
		// (I guess it is faster). If not, we create a path and we
		// draw it.
		// They are also interesting if the thickness is great than 1.5 pixels
		// because of their join and miter characteristics.
		if(actual_dash==0 && actual_w<1.5f) {
			canvas.drawRect(x, y, x+width, y+height, paint);
		} else {
			Path p = new Path();
			p.moveTo(x,y);
			p.lineTo(x+width,y);
			p.lineTo(x+width,y+height);
			p.lineTo(x,y+height);
			p.lineTo(x,y);
			canvas.drawPath(p, paint);
		}	
	}
	
	/** Fills a rectangle on the current graphic context.
		@param x the x coordinate of the uppermost left corner
		@param y the y coordinate of the uppermost left corner
		@param width the width of the rectangle
		@param height the height of the rectangle
	*/
	public void fillRect(int x, int y, int width, int height)
	{
		paint.setStyle(Style.FILL_AND_STROKE);
		canvas.drawRect(x, y, x+width, y+height, paint);
		paint.setStyle(Style.STROKE);
	}
	/** Fills a rounded rectangle on the current graphic context.
		@param x the x coordinate of the uppermost left corner
		@param y the y coordinate of the uppermost left corner
		@param width the width of the rectangle
		@param height the height of the rectangle
		@param arcWidth the width of the arc of the round corners
		@param arcHeight the height of the arc of the round corners
	*/
    public void fillRoundRect(int x,
                                   int y,
                                   int width,
                                   int height,
                                   int arcWidth,
                                   int arcHeight)
    {
		paint.setStyle(Style.FILL_AND_STROKE);
		canvas.drawRoundRect(new RectF(x, y, x+width, y+height), 
			(float)arcWidth/2.0f, (float)arcHeight/2.0f, paint);
		paint.setStyle(Style.STROKE);   
    }
    
	/** Checks whether the rectangle specified falls in a region which need
		to be updated because it is "dirty" on the screen.
		Implementing correctly this method is very important to achieve a good
		redrawing speed because only "dirty" regions on the screen will be
		actually redrawn.
	*/
    public boolean hitClip(int x,
                       int y,
                       int width,
                       int height)
    {
    	RectF rect=new RectF(x,y,x+width,y+height);
    	return !canvas.quickReject (rect, Canvas.EdgeType.AA);
    }
    
    /** Draws a segment between two points
    	@param x1 first coordinate x value
    	@param y1 first coordinate y value
    	@param x2 second coordinate x value
    	@param y2 second coordinate y value
    */
	public void drawLine(int x1,
                              int y1,
                              int x2,
                              int y2)
	{
		// Dashing effects can be applied only to paths.
		// If this is not needed, we use the simple call drawLine
		// (I guess it is faster). If not, we create a path and we
		// draw it.
		// They are also interesting if the thickness is great than 1.5 pixels
		// because of their join and miter characteristics.
		if(actual_dash==0 && actual_w<1.5f) {
			canvas.drawLine(x1,y1, x2, y2, paint);
		} else {
			Path p = new Path();
			p.moveTo(x1,y1);
			p.lineTo(x2,y2);
			canvas.drawPath(p, paint);
		}
	}
    
    /** Sets the current font for drawing text.
    	@param name the name of the typeface to be used.
    	@param size the size in pixels
    	@param isItalic true if an italic variant should be used
    	@param isBold true if a bold variant should be used
    */                    
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
	
	/** Simple version. It sets the current font.
		@param name the name of the typeface
		@param size the vertical size in pixels
	*/
	public void setFont(String name, int size)
	{

		setFont(name, size, false, false);
	}
	
	/** Gets the ascent metric of the current font.
		@returns the value of the ascent, in pixels.
	*/
	public int getFontAscent()
	{
		// Note: an ascent is "going up", so it is negative 
		// as in the FontMetrics documentation...
		// FidoCadJ requires a positive size, instead.
		return -(int)paint.getFontMetrics().ascent;
	}
	
	/** Gets the descent metric of the current font.
		@returns the value of the descent, in pixels.
	*/
	public int getFontDescent()
	{
		return  (int)paint.getFontMetrics().descent;
	}

	/** Gets the width of the given string with the current font.
		@param s the string to be used.
		@return the width of the string, in pixels.
	*/
	public int getStringWidth(String s)
	{
		return (int)paint.measureText(s);
	}

	/** Draws a string on the current graphic context
		@param str the string to be drawn
		@param x the x coordinate of the starting point
		@param y the y coordinate of the starting point
	*/
	public void drawString(String str,
                                int x,
                                int y)
    {
    	applyStroke(1.0f, 0);
		paint.setStyle(Style.FILL);
  		canvas.drawText (str, x,  y, paint);
		paint.setStyle(Style.STROKE);
    }
    
    /** Sets the transparency (alpha) of the current color.
    	@param alpha the transparency, between 0.0 (transparent) and 1.0 
    		(fully opaque).
    */
    public void setAlpha(float alpha)
    {
		paint.setAlpha((int)(alpha*255)); 
    }

	/** Draws a completely filled oval in the current graphic context.
		@param x the x coordinate of the starting point.
		@param y the y coordinate of the starting point.
		@param width the width of the oval.
		@param height the height of the oval.
	*/
    public void fillOval(int x,
                              int y,
                              int width,
                              int height)
	{
		paint.setStyle(Style.FILL_AND_STROKE);
		canvas.drawOval(new RectF (x, y, x+width, y+height), paint);
		paint.setStyle(Style.STROKE);
	}
	
	/** Draws an enmpty oval in the current graphic context.
		@param x the x coordinate of the starting point.
		@param y the y coordinate of the starting point.
		@param width the width of the oval.
		@param height the height of the oval.
	*/
	public void drawOval(int x,
                              int y,
                              int width,
                              int height)
    {
		canvas.drawOval(new RectF (x, y, x+width, y+height), paint);
    }          
    
    /** Fills a given  shape.
    	@param s the shape to be filled.
	*/             
    public void fill(ShapeInterface s)
    {
		ShapeAndroid ss = (ShapeAndroid) s;
		paint.setStyle(Style.FILL_AND_STROKE);
    	canvas.drawPath(ss.getPath(), paint);
    	paint.setStyle(Style.STROKE);
    }
    
    /** Draws a given  shape.
    	@param s the shape to be drawn.
	*/ 
    public void draw(ShapeInterface s)
    {
		ShapeAndroid ss = (ShapeAndroid) s;
    	canvas.drawPath(ss.getPath(), paint);
    }
    
    /** Fills a given  polygon.
    	@param p the polygon to be filled.
	*/ 
    public void fillPolygon(PolygonInterface p)
    {		
    	PolygonAndroid pp=(PolygonAndroid) p;
    	pp.close();
		paint.setStyle(Style.FILL_AND_STROKE);
    	canvas.drawPath(pp.getPath(), paint);
    	paint.setStyle(Style.STROKE);

    }
    
    /** Draws a given  polygon.
    	@param p the polygon to be drawn.
	*/
    public void drawPolygon(PolygonInterface p)
    {
		PolygonAndroid pp=(PolygonAndroid) p;
		pp.close();
    	canvas.drawPath(pp.getPath(), paint);
    }
    
    /** Selects a color associated to selected elements.
    	@param l the layer to which the selected element belongs.
    */
    public void activateSelectColor(LayerDesc l)
    {
		paint.setColor(Color.GREEN);
    }
    
    /** Draws a string by allowing for a certain degree of flexibility in
    	specifying how the text will be handled. NOTE: TO BE REMOVED.
    	@param xyfactor the text font is specified by giving its height in the 
    		setFont() method. If the text should be stretched (i.e. its width 
    		should be modified), this parameter gives the amount of stretching.
    	@param xa the x coordinate of the point where the text will be placed.
    	@param ya the y coordinate of the point where the text will be placed.
    	@param qq not used: NOTE: TO REMOVE???
    	@param h the height of the text, in pixels.
    	@param w the width of the string, in pixels.
    	@param th the total height of the text (ascent+descents).
    	@param needsStretching true if some stretching is needed.
    	@param orientation orientation in degrees of the text.
    	@param mirror true if the text is mirrored.
    	@param txt the string to be drawn.
    */
    public void drawAdvTextPath(double xyfactor, int xa, int ya,
  		int qq, int h, int w, int th, boolean needsStretching,
  		int orientation, boolean mirror,		
  		String txt)
  	{
  		// Note implementation using a path and drawTextOnPath (which is
  		// buggy and probably a little slow...
  		
  		applyStroke(1.0f, 0);
		paint.setStyle(Style.FILL);
		Path pp=new Path();
		pp.moveTo(xa,ya);
		
		if (mirror) {
			canvas.save();
			canvas.scale(-1f, 1f, xa, ya);
			orientation=-orientation;
		}
		
		if(needsStretching) {
			float size=paint.getTextSize();
			paint.setTextScaleX((float)(1.0f/xyfactor));
			paint.setTextSize(size*(float)xyfactor);
			th=(int)(th*xyfactor);
		} else
			paint.setTextScaleX(1.0f);
			
		double orientationRad=-orientation/180.0*Math.PI;
		
		pp.rLineTo(1000*(float)Math.cos(orientationRad), 
			1000*(float)Math.sin(orientationRad));
		
		// NOTE: there is an annoying bug in some versions of Android and 
		// drawTextOnPath has a somewhat weird behavior while calculating the
		// clipping area. This is true at least when the graphics HW
		// acceleration is on.
		canvas.drawTextOnPath(txt, pp, 0,th, paint);
		
		if (mirror) {
			canvas.restore();
		}
		
    	paint.setStyle(Style.STROKE);
    	paint.setTextScaleX(1.0f);
  	}
  	
  	/** Draws a string by allowing for a certain degree of flexibility in
    	specifying how the text will be handled.
    	@param xyfactor the text font is specified by giving its height in the 
    		setFont() method. If the text should be stretched (i.e. its width 
    		should be modified), this parameter gives the amount of stretching.
    	@param xa the x coordinate of the point where the text will be placed.
    	@param ya the y coordinate of the point where the text will be placed.
    	@param qq
    	@param h the height of the text, in pixels.
    	@param w the width of the string, in pixels.
    	@param th the total height of the text (ascent+descents).
    	@param needsStretching true if some stretching is needed.
    	@param orientation orientation in degrees of the text.
    	@param mirror true if the text is mirrored.
    	@param txt the string to be drawn.
    */
    public void drawAdvText(double xyfactor, int xa, int ya,
  		int qq, int h, int w, int th, boolean needsStretching,
  		int orientation, boolean mirror,		
  		String txt)
  	{
  		applyStroke(1.0f, 0);
		paint.setStyle(Style.FILL);
		
		canvas.save();
		
		if (mirror) {
			canvas.scale(-1f, 1f, xa, ya);
			orientation=-orientation;
		}
		
		if(needsStretching) {
			float size=paint.getTextSize();
			paint.setTextScaleX((float)(1.0f/xyfactor));
			paint.setTextSize(size*(float)xyfactor);
			th=(int)(th*xyfactor);
		} else
			paint.setTextScaleX(1.0f);
				
		canvas.rotate(-orientation, xa, ya);
		canvas.drawText(txt, xa,  ya+th, paint);
	
		canvas.restore();
		
    	paint.setStyle(Style.STROKE);
    	paint.setTextScaleX(1.0f);
  	}
    
    /** Draw as efficiently as possible a grid on the current shown drawing 
    	area. The grid shows the snapping point of the current MapCoordinates
    	used for the drawing.
    	@param cs the current MapCoordinates object used for the drawing.
    	@param xmin the minimum x value (in pixel) of the viewport.
    	@param ymin the minimum y value (in pixel) of the viewport.
    	@param xmax the maximum x value (in pixel) of the viewport.
    	@param ymax the maximum y value (in pixel) of the viewport.
    */
    public void drawGrid(MapCoordinates cs, 
    	int xmin, int ymin, 
    	int xmax, int ymax)
    {
    		// TODO: STILL TO BE IMPLEMENTED
    }
    	
    /** Create a polygon object, compatible with GraphicsAndroid.
    	@return a polygon object (instance of PolygonAndroid).
    */
    public PolygonInterface createPolygon()
    {
    	return new PolygonAndroid();
    }
    /** Create a color object, compatible with GraphicsAndroid.
    	@return a color object (instance of ColorAndroid).
    */
    public ColorInterface createColor()
    {
    	return new ColorAndroid();
    }
    /** Create a shape object, compatible with GraphicsAndroid.
    	@return a shape object (instance of ShapeAndroid).
    */
	public ShapeInterface createShape()
	{
		return new ShapeAndroid();
	}
}