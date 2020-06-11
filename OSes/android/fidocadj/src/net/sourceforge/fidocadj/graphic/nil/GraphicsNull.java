package net.sourceforge.fidocadj.graphic.nil;

import net.sourceforge.fidocadj.graphic.*;
import net.sourceforge.fidocadj.geom.*;
import net.sourceforge.fidocadj.layers.*;

import android.graphics.*;
import android.graphics.Paint.*;


/**     ANDROID VERSION

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

    /** Constructor.
    */
    public GraphicsNull()
    {
        paint = new Paint();
    }

    /** Set the current zoom factor. Currently employed for resizing the dash
        styles.
        @param z the current zoom factor (pixels for logical units).
    */
    public void setZoom(double z)
    {
        // Nothing to do
    }

    /** Set the current color.
        @param c the current color.
    */
    public void setColor(ColorInterface c)
    {
        // Nothing to do
    }

    /** Get the current color.
        @return the current color.
    */
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

    /** Fill a rounded rectangle on the current graphic context.
        @param x the x coordinate of the uppermost left corner.
        @param y the y coordinate of the uppermost left corner.
        @param width the width of the rectangle.
        @param height the height of the rectangle.
        @param arcWidth the width of the arc of the round corners.
        @param arcHeight the height of the arc of the round corners.
    */
    public void fillRoundRect(int x,
                                   int y,
                                   int width,
                                   int height,
                                   int arcWidth,
                                   int arcHeight)
    {
        // Nothing to do
    }

    /** Check whether the rectangle specified falls in a region which need
        to be updated because it is "dirty" on the screen.
        Implementing correctly this method is very important to achieve a good
        redrawing speed because only "dirty" regions on the screen will be
        actually redrawn.
        @param x the x coordinate of the uppermost left corner of rectangle.
        @param y the y coordinate of the uppermost left corner of rectangle.
        @param width the width of the rectangle of the rectangle.
        @param height the height of the rectangle of the rectangle.
        @return true if the rectangle hits the dirty region.
    */
    public boolean hitClip(int x,
                       int y,
                       int width,
                       int height)
    {
        return true;
    }

    /** Draw a segment between two points
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
        // Nothing to do
    }

    /** Set the current font.
        @param name the name of the typeface.
        @param size the vertical size in pixels.
        @param isItalic true if an italic variant should be used.
        @param isBold true if a bold variant should be used.
    */
    public void setFont(String name, double size, boolean isItalic,
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
        paint.setTextSize((int)Math.round(size));
    }

    /** Set the current font for drawing text.
        @param name the name of the typeface to be used.
        @param size the size in pixels
    */
    public void setFont(String name, double size)
    {
        setFont(name, size, false, false);
    }

    /** Get the ascent metric of the current font.
        @return the value of the ascent, in pixels.
    */
    public int getFontAscent()
    {
        // Note: an ascent is "going up", so it is negative
        // as in the FontMetrics documentation...
        // FidoCadJ requires a positive size, instead.
        return -(int)paint.getFontMetrics().ascent;
    }

    /** Get the descent metric of the current font.
        @return the value of the descent, in pixels.
    */
    public int getFontDescent()
    {
        return  (int)paint.getFontMetrics().descent;
    }

    /** Get the width of the given string with the current font.
        @param s the string to be used.
        @return the width of the string, in pixels.
    */
    public int getStringWidth(String s)
    {
        return (int)paint.measureText(s);
    }

    /** Draw a string on the current graphic context.
        @param str the string to be drawn.
        @param x the x coordinate of the starting point.
        @param y the y coordinate of the starting point.
    */
    public void drawString(String str,
                                int x,
                                int y)
    {
        // Nothing to do
    }

    /** Set the transparency (alpha) of the current color.
        @param alpha the transparency, between 0.0 (transparent) and 1.0
            (fully opaque).
    */
    public void setAlpha(float alpha)
    {
        // Nothing to do
    }

    /** Draw a completely filled oval in the current graphic context.
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
        // Nothing to do
    }

    /** Draw an enmpty oval in the current graphic context.
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
        // Nothing to do
    }

    /** Fill a given  shape.
        @param s the shape to be filled.
    */
    public void fill(ShapeInterface s)
    {
        // Nothing to do
    }

    /** Draw a given  shape.
        @param s the shape to be drawn.
    */
    public void draw(ShapeInterface s)
    {
        // Nothing to do
    }

    /** Fill a given  polygon.
        @param p the polygon to be filled.
    */
    public void fillPolygon(PolygonInterface p)
    {
        // Nothing to do
    }

    /** Draw a given  polygon.
        @param p the polygon to be drawn.
    */
    public void drawPolygon(PolygonInterface p)
    {
        // Nothing to do
    }

    /** Select the selection color (normally, green) for the current graphic
        context.
        @param l the layer whose color should be blended with the selection
            color (green).
    */
    public void activateSelectColor(LayerDesc l)
    {
        // Nothing to do
    }

    /** Draw a string by allowing for a certain degree of flexibility in
        specifying how the text will be handled.
        @param xyfactor the text font is specified by giving its height in the
            setFont() method. If the text should be stretched (i.e. its width
            should be modified), this parameter gives the amount of stretching.
        @param xa the x coordinate of the point where the text will be placed.
        @param ya the y coordinate of the point where the rotation is
            calculated.
        @param qq the y coordinate of the point where the text will be placed.
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
        // Nothing to do
    }

    /** Draw the grid in the given graphic context.
        @param cs the coordinate map description.
        @param xmin the x (screen) coordinate of the upper left corner.
        @param ymin the y (screen) coordinate of the upper left corner.
        @param xmax the x (screen) coordinate of the bottom right corner.
        @param ymax the y (screen) coordinate of the bottom right corner.
    */
    public void drawGrid(MapCoordinates cs,
        int xmin, int ymin,
        int xmax, int ymax)
    {
            // nothing to do
    }

    /** Create a polygon object, compatible with the current implementation.
        @return a polygon object.
    */
    public PolygonInterface createPolygon()
    {
        return new PolygonNull();
    }

    /** Create a color object, compatible with the current implementation.
        @return a color object.
    */
    public ColorInterface createColor()
    {
        return new ColorNull();
    }

    /** Create a shape object, compatible with the current implementation.
        @return a shape object.
    */
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