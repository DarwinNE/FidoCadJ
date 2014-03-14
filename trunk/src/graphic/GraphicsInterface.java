package graphic;

import geom.*;
import layers.*;

/** Provides a general way to draw on the screen.
   
<pre>
    This file is part of FidoCadJ.

    FidoCadJ is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FidoCadJ is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with FidoCadJ.  If not, see <http://www.gnu.org/licenses/>.

    Copyright 2014 by Davide Bucci
</pre>
*/

public interface GraphicsInterface
{
	public void setColor(ColorInterface c);
	public ColorInterface getColor();
	
	/** Retrieves or create a BasicStroke object having the wanted with and
		style and apply it to the current graphic context.
		@param w the width in pixel
		@param dashStyle the style of the stroke
	*/
	public void applyStroke(float w, int dashStyle);
	//public Stroke getStroke();
	//public void setStroke(Stroke s);
	
	/** Draws a rectangle on the current graphic context.
		@param x the x coordinate of the uppermost left corner
		@param y the y coordinate of the uppermost left corner
		@param width the width of the rectangle
		@param height the height of the rectangle
	*/
	public void drawRect(int x, int y, int width, int height);
	
	/** Fills a rectangle on the current graphic context.
		@param x the x coordinate of the uppermost left corner
		@param y the y coordinate of the uppermost left corner
		@param width the width of the rectangle
		@param height the height of the rectangle
	*/
	public void fillRect(int x, int y, int width, int height);
	
    public void fillRoundRect(int x,
                                   int y,
                                   int width,
                                   int height,
                                   int arcWidth,
                                   int arcHeight);

    public boolean hitClip(int x,
                       int y,
                       int width,
                       int height);
	public void drawLine(int x1,
                              int y1,
                              int x2,
                              int y2);
                              
	public void setFont(String name, int size);
	public void setFont(String name, int size, boolean isItalic, 
		boolean isBold);
	
	public int getFontAscent();
	public int getFontDescent();
	public int getStringWidth(String s);

	
	public void drawString(String str,
                                int x,
                                int y);
    
    public void setAlpha(float alpha);

    public void fillOval(int x,
                              int y,
                              int width,
                              int height);
	public void drawOval(int x,
                              int y,
                              int width,
                              int height);                          
    public void fill(ShapeInterface s);
    public void draw(ShapeInterface s);
    public void fillPolygon(PolygonInterface p);
    public void drawPolygon(PolygonInterface p);
    
    public void activateSelectColor(LayerDesc l);
    
    public void drawAdvText(double xyfactor, int xa, int ya,
  		int qq, int h, int w, int th, boolean needsStretching,
  		int orientation, boolean mirror,		
  		String txt);
    
    public void drawGrid(MapCoordinates cs, 
    	int xmin, int ymin, 
    	int xmax, int ymax);
    	
    	
    public PolygonInterface createPolygon();
    public ColorInterface createColor();
	public ShapeInterface createShape();
}