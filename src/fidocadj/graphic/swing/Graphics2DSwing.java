package fidocadj.graphic.swing;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;    // Used in drawGrid

import fidocadj.geom.MapCoordinates;
import fidocadj.globals.Globals;
import fidocadj.layers.LayerDesc;
import fidocadj.graphic.DecoratedText;
import fidocadj.graphic.ColorInterface;
import fidocadj.graphic.PolygonInterface;
import fidocadj.graphic.ShapeInterface;
import fidocadj.graphic.TextInterface;
import fidocadj.graphic.GraphicsInterface;



/** This class maps the general interface to java.awt.Graphics2D.
    It also provides a method to draw grid. It turns out that it is not
    trivial to draw grids in an efficient way, and the best strategy generally
    depends on the particular context. So the drawGrid method is present
    in the GraphicsInterface and of course its implementation is here.

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
    along with FidoCadJ. If not,
    @see <a href=http://www.gnu.org/licenses/>http://www.gnu.org/licenses/</a>.

    Copyright 2014-2023 by Davide Bucci
</pre>
*/

public class Graphics2DSwing implements GraphicsInterface, TextInterface
{
    // Practical sizes to the image used a "brush" for the tiled fill.
    // Larger images may yield faster results, until the memory penalty will
    // decrease performance. Used to be 1000x1000, but it seems that this is
    // too big on some Linux systems (see issue #166).
    private final static int maxAllowableGridBrushWidth = 500;
    private final static int maxAllowableGridBrushHeight = 500;

    Graphics2D g;

    // Here are some other local variables made global for avoiding memory
    // allocations (used in drawGrid).
    private BufferedImage bufferedImage; // Useful for grid calculation
    private double oldZoom;              // TODO: maybe the same as actualZoom?
    private TexturePaint tp;
    private int width;                   // NOPMD (complains -> local variable)
    private int height;                  // NOPMD (complains -> local variable)

    private BasicStroke[] strokeList;
    private float actual_w;

    private double zoom;
    private double actualZoom;

    /*  Strategy in 0.24.7:
        -------------------
        The font size affects the way the font is drawn. For this reason (as
        things such as the zoom and scaling should not change the relative
        size of the text), the font size is kept always equal to 100. Then, a
        coordinate change is applied to the font so that it is rescaled to the
        wanted size.

        Strategy in 0.24.8:
        -------------------
        Due to a strange bug for some (large) font sizes, I (DB) reverted the
        mechanism and I derive a new font of a given calculated size from the
        original font.
        The situation does not seem to be now much worse than 0.24.7 as I can
        set up the font size as a float and thus cope better with smaller
        font sizes.
    */
    private final static int FONTSIZE=100; // The size of the unscaled font.
    private Font f;                 // This is the scaled font.
    private double fontScale=1.0;   // This is the scaling factor.
    private Font mf;                // This is the original (unscaled) font.

    /** Constructor: fabricate a new object form a java.awt.Graphics2D object.
        @param gg the java.awt.Graphics2D graphic context.
    */
    public Graphics2DSwing(Graphics2D gg)
    {
        g=gg;
        oldZoom = -1;
        actualZoom = -1;
        zoom=1;
        /* Is that useful??? */
        g.setRenderingHint(
            RenderingHints.KEY_FRACTIONALMETRICS,
            RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHint(
            RenderingHints.KEY_RENDERING,
            RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    /** Constructor: fabricate a new object form a java.awt.Graphics object.
        @param gg the java.awt.Graphics graphic context.
    */
    public Graphics2DSwing(Graphics gg)
    {
        g=(Graphics2D)gg;
        oldZoom = -1;
        actualZoom = -1;
        zoom=1;
        /* Is that useful??? */
        /*g.setRenderingHint(
            RenderingHints.KEY_FRACTIONALMETRICS,
            RenderingHints.VALUE_FRACTIONALMETRICS_ON);*/
        /*g.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIASING_ON);*/
    }

    /** Constructor: fabricate a new object without associating a graphic
        object. You should use {@link #setGraphicContext} method to setup a
        graphic object in a second time
        to avoid a runtime exception.
    */
    public Graphics2DSwing()
    {
        g=null;
        oldZoom = -1;
        actualZoom = -1;
        zoom=1;
    }

    /** Retrieves an object implementing an appropriate TextInterface.
        @return an object implementing TextInterface.
    */
    public TextInterface getTextInterface()
    {
        return this;
    }

    /** Retrieves or create a BasicStroke object having the wanted with and
        style and apply it to the current graphic context.
        @param w the width in pixel
        @param dashStyle the style of the stroke
    */
    public void applyStroke(float w, int dashStyle)
    {
        if (w!=actual_w && w>0 || zoom!=actualZoom) {
            strokeList = new BasicStroke[Globals.dashNumber];

            // If the line width has been changed, we need to update the
            // stroke table

            // The first entry is non dashed
            strokeList[0]=new BasicStroke(w, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND);
            // Resize the dash sizes depending on the current zoom size.
            float[] dashArrayStretched;
            // Then, the dashed stroke styles are created.
            if(zoom<1.0) {
                zoom=1.0;
            }
            for(int i=1; i<Globals.dashNumber; ++i) {
                // Prepare the resized dash array.
                dashArrayStretched = new float[Globals.dash[i].length];
                for(int j=0; j<Globals.dash[i].length;++j) {
                    dashArrayStretched[j]=Globals.dash[i][j]*(float)zoom/2.0f;
                }
                strokeList[i]=new BasicStroke(w, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND,
                    (float)(10.0f*zoom), dashArrayStretched,
                    0.0f);
            }
            actual_w=w;
            actualZoom=zoom;
        }

        // Here we retrieve the stroke style corresponding to the given
        // dashStyle
        BasicStroke stroke=(BasicStroke)strokeList[dashStyle];

        // Apparently, on some systems (like my iMac G5 with MacOSX 10.4.11)
        // setting the stroke takes a lot of time!
        if(!stroke.equals(g.getStroke())) {
            g.setStroke(stroke);
        }
    }

    /** Set the current zoom factor. Currently employed for resizing the dash
        styles.
        @param z the current zoom factor (pixels for logical units).
    */
    public void setZoom(double z)
    {
        zoom=z;
    }

    /** Get the current zoom factor. Currently employed for resizing the dash
        styles.
        @return the current zoom factor (pixels for logical units).
    */
    public double getZoom()
    {
        return zoom;
    }

    /** This is a Swing-related method: it sets the current graphic context
        to the given Swing one.
        @param gg the Swing graphic context.
    */
    public void setGraphicContext(Graphics2D gg)
    {
        g=gg;
    }

    /** This is a Swing-related method: it gets the current graphic context.
        @return the Swing graphic context
    */
    public Graphics2D getGraphicContext()
    {
        return g;
    }

    /** Sets the current color.
        @param c the color to be set. Must be cast-able to ColorSwing class.
    */
    public void setColor(ColorInterface c)
    {
        ColorSwing cc = (ColorSwing) c;
        g.setColor(cc.getColorSwing());
    }

    /** Gets the current color.
        @return the actual color. Can be cast-able to ColorSwing class.
    */
    public ColorInterface getColor()
    {
        return new ColorSwing(g.getColor());
    }

    /** Draw a rectangle on the current graphic context.
        @param x the x coordinate of the uppermost left corner
        @param y the y coordinate of the uppermost left corner
        @param width the width of the rectangle
        @param height the height of the rectangle
    */
    public void drawRect(int x, int y, int width, int height)
    {
        g.drawRect(x,y,width,height);
    }
    /** Fill a rectangle on the current graphic context.
        @param x the x coordinate of the uppermost left corner
        @param y the y coordinate of the uppermost left corner
        @param width the width of the rectangle
        @param height the height of the rectangle
    */
    public void fillRect(int x, int y, int width, int height)
    {
        g.fillRect(x,y,width,height);
    }

    /** Fill a rounded rectangle on the current graphic context.
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
        g.fillRoundRect(x,y,width,height,arcWidth,arcHeight);
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
        return g.hitClip(x,y,width,height);
    }

    /** Draw a segment between two points.
        @param x1 first coordinate x value.
        @param y1 first coordinate y value.
        @param x2 second coordinate x value.
        @param y2 second coordinate y value.
    */
    public void drawLine(int x1,
                              int y1,
                              int x2,
                              int y2)
    {
        g.drawLine(x1,y1,x2,y2);
    }

    /** Set the current font for drawing text.
        @param name the name of the typeface to be used.
        @param size the size in pixels.
        @param isItalic true if an italic variant should be used.
        @param isBold true if a bold variant should be used.
    */
    public void setFont(String name, double size, boolean isItalic,
        boolean isBold)
    {
        mf = new Font(name,
            Font.PLAIN+(isItalic?Font.ITALIC:0)+(isBold?Font.BOLD:0),
            FONTSIZE);
        fontScale=size;
        f = mf.deriveFont((float)size);

        // Check if there is the need to change the current font. Apparently,
        // on some systems (I have seen this on MacOSX), setting up the font
        // takes a surprisingly long amount of time.

        if(!g.getFont().equals(f)) {
            g.setFont(f);
        }
    }

    /** Simple version. It sets the current font.
        @param name the name of the typeface.
        @param size the vertical size in pixels.
    */
    public void setFont(String name, double size)
    {
        setFont(name, size, false, false);
    }
    /** Get the font size.
        @return the font size.
    */
    public double getFontSize()
    {
        return fontScale;
    }

    /** Set the font size.
        @param size the font size.
    */
    public void setFontSize(double size)
    {
        fontScale=size;
        if(mf==null) {
            return;
        }
        f = mf.deriveFont((float)size);

        if(!g.getFont().equals(f)) {
            g.setFont(f);
        }
    }

    /** Get the ascent metric of the current font.
        @return the value of the ascent, in pixels.
    */
    public int getFontAscent()
    {
        FontMetrics fm = g.getFontMetrics(g.getFont());
        return fm.getAscent();
    }

    /** Get the descent metric of the current font.
        @return the value of the descent, in pixels.
    */
    public int getFontDescent()
    {
        FontMetrics fm = g.getFontMetrics(g.getFont());
        return fm.getDescent();
    }

    /** Get the width of the given string with the current font.
        @param s the string to be used.
        @return the width of the string, in pixels.
    */
    public int getStringWidth(String s)
    {
        FontMetrics fm = g.getFontMetrics(g.getFont());
        return fm.stringWidth(s);
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
        g.drawString(str,x,y);
    }

    /** Set the transparency (alpha) of the current color.
        @param alpha the transparency, between 0.0 (transparent) and 1.0
            (fully opaque).
    */
    public void setAlpha(float alpha)
    {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
            alpha));
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
        g.fillOval(x,y,width,height);
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
        g.drawOval(x,y,width,height);
    }

    /** Fill a given  shape.
        @param s the shape to be filled.
    */
    public void fill(ShapeInterface s)
    {
        ShapeSwing ss=(ShapeSwing) s;
        g.fill(ss.getShapeInSwing());
    }

    /** Draw a given  shape.
        @param s the shape to be drawn.
    */
    public void draw(ShapeInterface s)
    {
        ShapeSwing ss=(ShapeSwing) s;
        g.draw(ss.getShapeInSwing());
    }

    /** Fill a given  polygon.
        @param p the polygon to be filled.
    */
    public void fillPolygon(PolygonInterface p)
    {
        PolygonSwing pp=(PolygonSwing) p;
        g.fillPolygon(pp.getSwingPolygon());
    }

    /** Draw a given  polygon.
        @param p the polygon to be drawn.
    */
    public void drawPolygon(PolygonInterface p)
    {
        PolygonSwing pp=(PolygonSwing) p;
        g.drawPolygon(pp.getSwingPolygon());
    }

    /** Select the selection color (normally, green) for the current graphic
        context.
        @param l the layer whose color should be blended with the selection
            color (green).
    */
    public void activateSelectColor(LayerDesc l)
    {
        // We blend the layer color with green, in such a way that the
        // selected objects bear a certain reminescence of their original
        // color.

        if (l==null) {
            g.setColor(Color.green);
        } else {
            ColorSwing c =(ColorSwing) l.getColor();
            g.setColor(blendColors(Color.green, c.getColorSwing(), 0.6f));
        }
        g.setComposite(AlphaComposite.getInstance(
            AlphaComposite.SRC_OVER, 1.0f));

    }
    /**
        Blend two colors. From
     http://www.java2s.com/Code/Java/2D-Graphics-GUI/Commoncolorutilities.htm

        @param color1  First color to blend.
        @param color2  Second color to blend.
        @param r   Blend ratio. 0.5 will give even blend, 1.0 will return
                   color1, 0.0 will return color2 and so on.
        @return        Blended color.
    */
    public static Color blendColors (Color color1, Color color2, float r)
    {
        float ir = (float) 1.0 - r;

        float rgb1[] = new float[3];
        float rgb2[] = new float[3];

        color1.getColorComponents (rgb1);
        color2.getColorComponents (rgb2);

        return new Color (rgb1[0] * r + rgb2[0] * ir,
            rgb1[1] * r + rgb2[1] * ir,
            rgb1[2] * r + rgb2[2] * ir);
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
        // TODO: is it possible to unify qq and ya? For example, get rid of qq?

        /*  At first, I tried to use an affine transform on the font, without
            pratically touching the graphic context. This technique worked well,
            but I noticed it produced bugs on the case of a jar packed on a
            MacOSX application bundle.
            I therefore choose (from v. 0.20.2) to use only graphic context
            transforms. What a pity!

            February 20, 2009: I noticed this is in fact a bug on JRE < 1.5
            December 14, 2015: Maybe it is a way to obtain a more consistant
                text output? Now FidoCadJ requires Java 1.7, get back to it?
        */
        AffineTransform at=(AffineTransform)g.getTransform().clone();

        // Ats is used to save the current coordinate transform.
        AffineTransform ats=(AffineTransform)at.clone();
        AffineTransform stretching= new AffineTransform();
        AffineTransform mm= new AffineTransform();
        DecoratedText dt=new DecoratedText(this);

        stretching.scale(1,xyfactor);

        if (mirror) {
            mm.scale(-1,1);
        }

        // If it's a simple normal text, draw it in the simple (fastest) way.
        if(orientation==0) {
            if (mirror) {
                // Here the text is mirrored
                at.scale(-1,xyfactor);
                g.setTransform(at);
                if(g.hitClip(-xa,qq,w,h)) {
                    if(!g.getFont().equals(f)) {
                        g.setFont(f);
                    }
                    dt.drawString(txt,-xa,qq+h);
                }
            } else {
                // Here the text is normal
                if(needsStretching) {
                    at.concatenate(stretching);
                    g.setTransform(at);
                }

                if(g.hitClip(xa,qq, w, th)){
                    if(th<Globals.textSizeLimit) {
                        g.drawLine(xa,qq,xa+w,qq);
                        if(needsStretching) {
                            g.setTransform(ats);
                        }
                        return;
                    } else {
                        if(!g.getFont().equals(f)) {
                            g.setFont(f);
                        }
                        dt.drawString(txt,xa,qq+h);
                        if(needsStretching) {
                            g.setTransform(ats);
                        }
                        return;
                    }
                }
            }
        } else {    // Text is rotated.
            if(mirror) {
                // Here the text is rotated and mirrored
                at.concatenate(mm);
                at.rotate(Math.toRadians(orientation),-xa, ya);
                if(needsStretching) {
                    at.concatenate(stretching);
                }
                g.setTransform(at);
                if(!g.getFont().equals(f)) {
                    g.setFont(f);
                }

                dt.drawString(txt,-xa,qq+h);

            } else {
                // Here the text is just rotated
                at.rotate(Math.toRadians(-orientation),xa,ya);
                if(needsStretching) {
                    at.concatenate(stretching);
                }
                g.setTransform(at);
                if(!g.getFont().equals(f)) {
                    g.setFont(f);
                }
                dt.drawString(txt,xa,qq+h);
            }
        }
        g.setTransform(ats);
    }

    /** Draw the grid in the given graphic context.
        @param cs the coordinate map description
        @param xmin the x (screen) coordinate of the upper left corner
        @param ymin the y (screen) coordinate of the upper left corner
        @param xmax the x (screen) coordinate of the bottom right corner
        @param ymax the y (screen) coordinate of the bottom right corner
    */
    public void drawGrid(MapCoordinates cs,
        int xmin, int ymin,
        int xmax, int ymax)
    {
        // Drawing the grid seems easy, but it appears that setting a pixel
        // takes a lot of time. Basically, we create a textured brush and we
        // use it to paint the entire specified region.
        int dx=cs.getXGridStep();   // Horizontal grid pitch in logical units.
        int dy=cs.getYGridStep();   // Vertical grid pitch in logical units.
        int mul=1;
        double toll=0.01;
        double z=cs.getYMagnitude();

        // DB: I tried with d/2 instead of 0, but I get some very
        // unpleasant aliasing effects for zoom such as 237%
        double dd=0;

        double x;
        double y;

        // Fabricate a new image only if necessary, to save time.
        if(oldZoom!=z || bufferedImage == null || tp==null) {
            // It turns out that drawing the grid in an efficient way is not a
            // trivial task. The program here tries to calculate the minimum
            // common integer multiple of the dot espacement, to calculate the
            // size of an image in order to be an integer.
            // The pattern filling (which is fast) is then used to replicate the
            // image (very fast!) over the working surface.

            for (int l=1; l<105; ++l) {
                if (Math.abs(l*z-Math.round(l*z))<toll) {
                    mul=l;
                    break;
                }
            }
            tp = null;
            double ddx=Math.abs(cs.mapXi(dx,0,false)-cs.mapXi(0,0,false));
            double ddy=Math.abs(cs.mapYi(0,dy,false)-cs.mapYi(0,0,false));
            double d=1;

            // This code applies a correction: draws lines if the pitch
            // is very large, or draw much less dots if it is too dense.
            if (ddx>35 || ddy>35) {
                // Lines!
                bufferedImage=null;
                d=2;
                // The loops are done in logical units.
                g.setColor(new Color(220,220,220));
                for (x=cs.unmapXsnap(xmin); x<=cs.unmapXsnap(xmax); x+=dx) {
                    g.drawLine(
                        (int)Math.round(cs.mapXr(x,0)),ymin,
                        (int)Math.round(cs.mapXr(x,0)), ymax);
                }
                for (y=cs.unmapYsnap(ymin); y<=cs.unmapYsnap(ymax); y+=dy) {
                    g.drawLine(
                        xmin,(int)Math.round(cs.mapYr(0,y)),
                        xmax, (int)Math.round(cs.mapYr(0,y)));
                }
                return;
            } else if (ddx<3 || ddy <3) {
                // Less dots
                dx=5*cs.getXGridStep();
                dy=5*cs.getYGridStep();
                ddx=Math.abs(cs.mapXi(dx,0,false)-cs.mapXi(0,0,false));
            }

            width=Math.abs(cs.mapX(mul*dx,0)-cs.mapX(0,0));
            if (width<=0) {
                width=1;
            }

            height=Math.abs(cs.mapY(0,0)-cs.mapY(0,mul*dy));
            if (height<=0) {
                height=1;
            }

            /* Nowadays computers have generally a lot of memory, but this is
               not a good reason to waste it. If it turns out that the image
               size is utterly impratical, use the standard dot by dot grid
               construction.
               This should happen rarely, only for particular zoom sizes.
            */
            if (width>maxAllowableGridBrushWidth ||
                height>maxAllowableGridBrushHeight)
            {
                // Simpler (and generally less efficient) version of the grid
                g.setColor(Color.gray);
                for (x=cs.unmapXsnap(xmin); x<=cs.unmapXsnap(xmax); x+=dx) {
                    for (y=cs.unmapYsnap(ymin); y<=cs.unmapYsnap(ymax); y+=dy) {
                        g.fillRect((int)Math.round(cs.mapXr(x,y)-dd),
                            (int)Math.round(cs.mapYr(x,y)-dd),(int)d,(int)d);
                    }
                }
                return;
            }

            try {
                // Create a buffered image in which to draw
                GraphicsEnvironment env =
                    GraphicsEnvironment.getLocalGraphicsEnvironment();
                GraphicsDevice device = env.getDefaultScreenDevice();
                GraphicsConfiguration config = device.getDefaultConfiguration();
                bufferedImage = config.createCompatibleImage(width, height,
                                          Transparency.TRANSLUCENT);

            } catch (OutOfMemoryError e) {
                System.out.println("Out of memory error when painting grid");
                return;
            }

            // Create a graphics contents on the buffered image
            Graphics2D g2d = bufferedImage.createGraphics();
            g2d.setColor(Color.white);
            g2d.setColor(Color.gray);

            // Prepare the image with the grid.
            for (x=0; x<=cs.unmapXsnap(width); x+=dx) {
                for (y=0; y<=cs.unmapYsnap(height); y+=dy) {
                    g2d.fillRect((int)Math.round(cs.mapXr(x,y)-dd),
                        (int)Math.round(cs.mapYr(x,y)-dd),(int)d,(int)d);
                }
            }
            oldZoom=z;
            Rectangle anchor = new Rectangle(width, height);

            tp = new TexturePaint(bufferedImage, anchor);
        }

        // Textured paint :-)
        g.setPaint(tp);
        g.fillRect(0, 0, xmax, ymax);   // TODO: sometimes I get an exception.
    }

    /** Create a polygon object, compatible with Graphics2DSwing.
        @return a polygon object (instance of PolygonSwing).
    */
    public PolygonInterface createPolygon()
    {
        return new PolygonSwing();
    }

    /** Create a shape object, compatible with Graphics2DSwing.
        @return a shape object (instance of ShapeSwing).
    */
    public ShapeInterface createShape()
    {
        return new ShapeSwing();
    }
    /** Create a color object, compatible with Graphics2DSwing.
        @return a color object (instance of ColorSwing).
    */
    public ColorInterface createColor()
    {
        return new ColorSwing(g.getColor());
    }
    /** Retrieve the current screen density in dots-per-inch.
        @return the screen resolution (density) in dots-per-inch.
    */
    public float getScreenDensity()
    {
        return Toolkit.getDefaultToolkit().getScreenResolution();
    }
}