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
	
	public void setColor(ColorInterface c) 
	{
		ColorAndroid ca = (ColorAndroid)c;
		paint.setColor(ca.getColorAndroid());
	}
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
  		canvas.drawText (str, x,  y, paint);
    }
    
    public void setAlpha(float alpha)
    {
		paint.setAlpha((int)(alpha*255)); 
    }

    public void fillOval(int x,
                              int y,
                              int width,
                              int height)
	{
		paint.setStyle(Style.FILL_AND_STROKE);
		canvas.drawOval(new RectF (x, y, x+width, y+height), paint);
		paint.setStyle(Style.STROKE);
	}
	public void drawOval(int x,
                              int y,
                              int width,
                              int height)
    {
		canvas.drawOval(new RectF (x, y, x+width, y+height), paint);
    }                          
    public void fill(ShapeInterface s)
    {
		ShapeAndroid ss = (ShapeAndroid) s;
		paint.setStyle(Style.FILL_AND_STROKE);
    	canvas.drawPath(ss.getPath(), paint);
    	paint.setStyle(Style.STROKE);
    }
    public void draw(ShapeInterface s)
    {
		ShapeAndroid ss = (ShapeAndroid) s;
    	canvas.drawPath(ss.getPath(), paint);
    }
    public void fillPolygon(PolygonInterface p)
    {		
    	PolygonAndroid pp=(PolygonAndroid) p;
    	pp.close();
		paint.setStyle(Style.FILL_AND_STROKE);
    	canvas.drawPath(pp.getPath(), paint);
    	paint.setStyle(Style.STROKE);

    }
    public void drawPolygon(PolygonInterface p)
    {
		PolygonAndroid pp=(PolygonAndroid) p;
		pp.close();
    	canvas.drawPath(pp.getPath(), paint);
    }
    
    public void activateSelectColor(LayerDesc l)
    {
		paint.setColor(Color.GREEN);
    }
    
    public void drawAdvText(double xyfactor, int xa, int ya,
  		int qq, int h, int w, int th, boolean needsStretching,
  		int orientation, boolean mirror,		
  		String txt)
  	{
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
		
		canvas.drawTextOnPath(txt, pp, 0,th, paint);
		
		
		if (mirror) {
			canvas.restore();
		}
		
    	paint.setStyle(Style.STROKE);
  	}
    
    public void drawGrid(MapCoordinates cs, 
    	int xmin, int ymin, 
    	int xmax, int ymax)
    {
    		// nothing to do
    }
    	
    	
    public PolygonInterface createPolygon()
    {
    	return new PolygonAndroid();
    }
    public ColorInterface createColor()
    {
    	return new ColorAndroid();
    }
	public ShapeInterface createShape()
	{
		return new ShapeAndroid();
	}
}