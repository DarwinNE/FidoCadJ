package graphic.swing;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;	// Used in drawGrid

import geom.*;
import layers.*;
import globals.*;
import graphic.*;


/** This class maps the general interface to java.awt.Graphics2D.
	It also provides a method to draw grid. It turns out that it is not
	very easy to draw grids in an efficient way, and the best strategy must 
	be based on the particular context. So the drawGrid method is present 
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
    along with FidoCadJ.  If not, see <http://www.gnu.org/licenses/>.

    Copyright 2014 by Davide Bucci
</pre>
*/

public class Graphics2DSwing implements GraphicsInterface 
{
	Graphics2D g;
	
    // Here are some other local variables made global for avoiding memory
    // allocations (used in drawGrid). 
    private BufferedImage bufferedImage; // Useful for grid calculation
    private double oldZoom;
    private TexturePaint tp;
    private int width;
    private int height;
    
    private BasicStroke[] strokeList;
	private BasicStroke stroke;
	private float actual_w;
	
	private	AffineTransform at;
	private AffineTransform stretching;
	private AffineTransform ats;
	private Font f;
	private AffineTransform mm;
	
	/** Constructor: fabricate a new object form a java.awt.Graphics2D object.
		@param gg the java.awt.Graphics2D graphic context.
	*/
	public Graphics2DSwing(Graphics2D gg) 
	{
		g=gg;
		oldZoom = -1;
	}
	
	/** Constructor: fabricate a new object form a java.awt.Graphics object.
		@param gg the java.awt.Graphics graphic context.
	*/
	public Graphics2DSwing(Graphics gg) 
	{
		g=(Graphics2D)gg;
		oldZoom = -1;
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
	}
	
	/** Retrieves or create a BasicStroke object having the wanted with and
		style and apply it to the current graphic context.
		@param w the width in pixel
		@param dashStyle the style of the stroke
	*/
	public void applyStroke(float w, int dashStyle)
	{
		
		if (w!=actual_w && w>0) {
			strokeList = new BasicStroke[Globals.dashNumber];
			
			// If the line width has been changed, we need to update the 
			// stroke table
			
			// The first entry is non dashed
			strokeList[0]=new BasicStroke(w, BasicStroke.CAP_ROUND, 
            		BasicStroke.JOIN_ROUND);
            // Then, the dashed stroke styles are created
			for(int i=1; i<Globals.dashNumber; ++i) {
				strokeList[i]=new BasicStroke(w, BasicStroke.CAP_ROUND, 
            		BasicStroke.JOIN_ROUND, 
            		10.0f, Globals.dash[i], 
        			0.0f);
        	}
        	actual_w=w;
		}
		
		// Here we retrieve the stroke style corresponding to the given 
		// dashStyle
		stroke=(BasicStroke)strokeList[dashStyle];
		
		// Apparently, on some systems (like my iMac G5 with MacOSX 10.4.11)
        // setting the stroke takes a lot of time!
		if(!stroke.equals(g.getStroke())) 
            g.setStroke(stroke);
		
	}
	
	/** This is a Swing-related method: it sets the current graphic context
		to the given Swing one.
		@param gg the Swing graphic context
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
	
	/** Draws a rectangle on the current graphic context.
		@param x the x coordinate of the uppermost left corner
		@param y the y coordinate of the uppermost left corner
		@param width the width of the rectangle
		@param height the height of the rectangle
	*/
	public void drawRect(int x, int y, int width, int height)
    {
    	g.drawRect(x,y,width,height);
    }
    /** Fills a rectangle on the current graphic context.
		@param x the x coordinate of the uppermost left corner
		@param y the y coordinate of the uppermost left corner
		@param width the width of the rectangle
		@param height the height of the rectangle
	*/
	public void fillRect(int x, int y, int width, int height)
    {
    	g.fillRect(x,y,width,height);
    }
    
    public void fillRoundRect(int x,
                                   int y,
                                   int width,
                                   int height,
                                   int arcWidth,
                                   int arcHeight)
    {
    	g.fillRoundRect(x,y,width,height,arcWidth,arcHeight);
    }

    public boolean hitClip(int x,
                       int y,
                       int width,
                       int height)
    {
    	return g.hitClip(x,y,width,height);
    }
	public void drawLine(int x1,
                              int y1,
                              int x2,
                              int y2)
    {
    	g.drawLine(x1,y1,x2,y2);
    }
	
	public void setFont(String name, int size, boolean isItalic, 
		boolean isBold)
	{
		f = new Font(name, 
			Font.PLAIN+(isItalic?Font.ITALIC:0)+(isBold?Font.BOLD:0), 
			size);
		// Check if there is the need to change the current font. Apparently, 
	   	// on some systems (I have seen this on MacOSX), setting up the font 
	   	// takes a surprisingly long amount of time.
	   	
	   	if(!g.getFont().equals(f))
			g.setFont(f);
	}
	
	public void setFont(String name, int size)
	{
		setFont(name, size, false, false);
	}
	
	public int getFontAscent()
	{
		FontMetrics fm = g.getFontMetrics(g.getFont());
    	return fm.getAscent();
	}
	
	public int getFontDescent()
	{
		FontMetrics fm = g.getFontMetrics(g.getFont());
    	return fm.getDescent();
	}
	
	public int getStringWidth(String s)
	{
		FontMetrics fm = g.getFontMetrics(g.getFont());
    	return fm.stringWidth(s);
	}

	public void drawString(String str,
                                int x,
                                int y)
    {
    	g.drawString(str,x,y);
    }
    public void setAlpha(float alpha)
    {
    	g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 
    		alpha));
    }

    public void fillOval(int x,
                              int y,
                              int width,
                              int height)
    {
    	g.fillOval(x,y,width,height);
    }
	public void drawOval(int x,
                              int y,
                              int width,
                              int height)
    {
    	g.drawOval(x,y,width,height);
    }                          
    public void fill(ShapeInterface s)
    {
    	ShapeSwing ss=(ShapeSwing) s;
    	g.fill(ss.getShapeInSwing());
    }
    public void draw(ShapeInterface s)
    {
    	ShapeSwing ss=(ShapeSwing) s;
    	g.draw(ss.getShapeInSwing());
    }
    public void fillPolygon(PolygonInterface p)
    {
    	PolygonSwing pp=(PolygonSwing) p;
    	g.fillPolygon(pp.getSwingPolygon());
    }
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

    	Color color = new Color (rgb1[0] * r + rgb2[0] * ir, 
                             rgb1[1] * r + rgb2[1] * ir, 
                             rgb1[2] * r + rgb2[2] * ir);
    
    	return color;
  	}
  	    
  	
  	public void drawAdvText(double xyfactor, int xa, int ya,
  		int qq, int h, int w, int th, boolean needsStretching,
  		int orientation, boolean mirror,
  		String txt)
  	{
  		/*  At first, I tried to use an affine transform on the font, without
		   	pratically touching the graphic context. This technique worked well,
		   	but I noticed it produced bugs on the case of a jar packed on a 
		   	MacOSX application bundle.
		   	I therefore choose (from v. 0.20.2) to use only graphic context
		   	transforms. What a pity!
		   
		   	February 20, 2009: I noticed this is in fact a bug on JRE < 1.5 
		   
		*/
   		AffineTransform at=(AffineTransform)g.getTransform().clone();
		AffineTransform ats=(AffineTransform)at.clone();
		AffineTransform stretching= new AffineTransform();
		AffineTransform mm= new AffineTransform();
		
		stretching.scale(1,xyfactor);
		
		if (mirror) {
			mm.scale(-1,1);
		}

		if(orientation==0) {
			if (mirror) {
				// Here the text is mirrored
				at.scale(-1,xyfactor);
				g.setTransform(at);
				if(g.hitClip(-xa,qq,w,h)){
					if(!g.getFont().equals(f))
	   					g.setFont(f);

					g.drawString(txt,-xa,qq+h); 
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
						if(needsStretching) 
							g.setTransform(ats);
	   					return;
	    			} else {
	    				if(!g.getFont().equals(f))
	   						g.setFont(f);
						g.drawString(txt,xa,qq+h);
						if(needsStretching) 
							g.setTransform(ats);
	   					return;
					}
				}
			} 
		} else {
    		if(mirror) {
    			// Here the text is rotated and mirrored
    		    at.concatenate(mm);
				at.rotate(Math.toRadians(orientation),-xa, ya);
				if(needsStretching) at.concatenate(stretching);
   				g.setTransform(at);
				if(!g.getFont().equals(f))
	   				g.setFont(f);

    			g.drawString(txt,-xa,qq+h); 

    		} else {
    			// Here the text is just rotated
				at.rotate(Math.toRadians(-orientation),xa,ya);
				if(needsStretching) at.concatenate(stretching);
   				g.setTransform(at);
   				if(!g.getFont().equals(f))
	   				g.setFont(f);
				g.drawString(txt,xa,qq+h); 
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

        int dx=cs.getXGridStep();
        int dy=cs.getYGridStep();
        int mul=1;
        double toll=0.01;
        double z=cs.getYMagnitude();
        
        double x;
        double y;
        
        double m=1.0;   

        // Fabricate a new image only if necessary, to save time.   
        if(oldZoom!=z || bufferedImage == null || tp==null) {
            // It turns out that drawing the grid in an efficient way is not a 
            // trivial problem. What it is done here is that the program tries
            // to calculate the minimum common integer multiple of the dot 
            // espacement to calculate the size of an image in order to be an 
            // integer.
            // The pattern filling (which is fast) is then used to replicate the
            // image (very fast!) over the working surface.
            
            for (double l=1; l<105; ++l) {
                if (Math.abs(l*z-Math.round(l*z))<toll) {
                    mul=(int)l;
                    break;
                }
            }
            tp = null;
            double ddx=Math.abs(cs.mapXi(dx,0,false)-cs.mapXi(0,0,false));
            double ddy=Math.abs(cs.mapYi(0,dy,false)-cs.mapYi(0,0,false));
            int d=1;
        
            // This code applies a correction: draws bigger points if the pitch
            // is very big, or draw much less points if it is too dense.
            if (ddx>50 || ddy>50) {
                d=2;
            } else if (ddx<3 || ddy <3) {
                dx=5*cs.getXGridStep();
                dy=5*cs.getYGridStep();
                ddx=Math.abs(cs.mapXi(dx,0,false)-cs.mapXi(0,0,false));
            }
                
            width=Math.abs(cs.mapX(mul*dx,0)-cs.mapX(0,0));
            if (width<=0) width=1;
                
            height=Math.abs(cs.mapY(0,0)-cs.mapY(0,mul*dy));
            if (height<=0) height=1;
        
            /* Nowadays computers have generally a lot of memory, but this is 
               not a good reason to waste it. If it turns out that the image
               size is utterly impratical, use the standard dot by dot grid 
               construction.
               This should happen rarely, only for particular zoom sizes.
            */
            if (width>1000 || height>1000) {
                g.setColor(Color.white);
                g.fillRect(xmin,ymin,xmax,ymax);
                g.setColor(Color.gray);
                for (x=cs.unmapXsnap(xmin); x<=cs.unmapXsnap(xmax); x+=dx) {
                    for (y=cs.unmapYsnap(ymin); y<=cs.unmapYsnap(ymax); y+=dy) {
                        g.fillRect(cs.mapXi((int)x,(int)y, 
                            false),cs.mapYi((int)x,
                            (int)y, false),d,d);
                    }
                }
                return;
            }
        
            try {
                // Create a buffered image in which to draw
                bufferedImage = new BufferedImage(width, height,
                                          BufferedImage.TYPE_INT_BGR);
                                          
            } catch (java.lang.OutOfMemoryError E) {
                System.out.println("Out of memory error when painting grid");
                return;
            }
        
            // Create a graphics contents on the buffered image
            Graphics2D g2d = bufferedImage.createGraphics();
            g2d.setColor(Color.white);
            g2d.fillRect(0,0,width,height);
            g2d.setColor(Color.gray);
            
            // Prepare the image with the grid.
            for (x=0; x<=cs.unmapXsnap(width); x+=dx) {
                for (y=0; y<=cs.unmapYsnap(height); y+=dy) {
                    g2d.fillRect(cs.mapX((int)x,(int)y),cs.mapY((int)x,
                        (int)y),d,d);
                }
            }
            //g2d.dispose();
            oldZoom=z;
            Rectangle anchor = new Rectangle(width, height);

            tp = new TexturePaint(bufferedImage, anchor);
        }
        
        // Textured paint :-)
        g.setPaint(tp);
        g.fillRect(0, 0, xmax, ymax);   // TODO: sometimes I get an exception.
	}
	
	public PolygonInterface createPolygon()
	{
		return new PolygonSwing();
	}

	public ShapeInterface createShape()
	{
		return new ShapeSwing();
	}
	
	public ColorInterface createColor()
	{
		return new ColorSwing(g.getColor());
	}
/*	
	public static GraphicsInterface createDummyGraphicElement()
	{

         return new GraphicSwing(gSCI);		
	}*/
}