package net.sourceforge.fidocadj.circuit;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

import net.sourceforge.fidocadj.geom.*;

/** Employs a bitmap image as a canvas to trace on it.

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

    Copyright 2017 by Davide Bucci
    </pre>
*/

public class ImageAsCanvas
{
    private BufferedImage img;
    private String filename;
    private BufferedImage resizedImg;
    private double resolution=200;
    private double xcorner=0;
    private double ycorner=0;

    private final int MAX_RESIZED_WIDTH;
    private final int MAX_RESIZED_HEIGHT;

    /** Constructor.
    */
    public ImageAsCanvas()
    {
        img=null;
        Dimension screensize=Toolkit.getDefaultToolkit().getScreenSize();
        MAX_RESIZED_WIDTH=screensize.width*3;
        MAX_RESIZED_HEIGHT=screensize.height*3;
        
    }

    /** Specify an image to attach to the current drawing.
        @param f the path and the filename of the image file to
            load and display.
        @throws IOException if the file is not found or can not be loaded.
    */
    public void loadImage(String f)
        throws IOException
    {
        img=ImageIO.read(new File(f));
        filename=f;
    }

    /** Specify the resolution of the image in dots per inch.
        This is employed for the coordinate mapping so that the image size
        is correctly matched with the FidoCadJ coordinate systems.
        @param res image resolution in dots per inch (dpi).
    */
    public void setResolution(double res)
    {
        resolution=res;
    }

    /** Get the current resolution in dpi.
        @return the current resolution in dots per inch.
    */
    public double getResolution()
    {
        return resolution;
    }

    /** Remove the attached image.
    */
    public void removeImage()
    {
        img=null;
    }

    /** Get the current file name.
        @return the current file name
    */
    public String getFilename()
    {
        return filename;
    }

    /** Set the coordinates of the origin corner (left topmost one).
        @param x the x coordinate.
        @param y the y coordinate.
    */
    public void setCorner(double x, double y)
    {
        xcorner=x;
        ycorner=y;
    }

    /** Get the x coordinate of the left topmost point of the image (use
        FidoCadJ coordinates).
        @return the x coordinate.
    */
    public double getCornerX()
    {
        return xcorner;
    }

    /** Get the y coordinate of the left topmost point of the image (use
        FidoCadJ coordinates).
        @return the y coordinate.
    */
    public double getCornerY()
    {
        return ycorner;
    }

    private int oldw=0;
    private int oldh=0;
    private int shiftx=0;
    private int shifty=0;

    /** Draw the current image in the given graphic context.
        @param g the Graphic2D object where the image has to be drawn.
        @param mc the current coordinate mapping.
    */
    public void drawCanvasImage(Graphics2D g, MapCoordinates mc)
    {
        if(img==null)
            return;

        // The image is drawn only in the "dirty" region of the drawing area
        // so to greatly improve redrawing speed.
        Rectangle clip=g.getClipBounds();
        int regionx=clip.x;
        int regiony=clip.y;
        int regionwidth=clip.width;
        int regionheight=clip.height;


        // The FidoCadJ resolution is 200dpi.
        int w=(int)(200.0*img.getWidth()/resolution*mc.getXMagnitude()+0.5);
        int h=(int)(200.0*img.getHeight()/resolution*mc.getYMagnitude()+0.5);

        int ox=mc.mapXi(xcorner, ycorner,false);
        int oy=mc.mapYi(xcorner, ycorner,false);

        // This code is needed to avoid exceeding the boundaries of the
        // images (this produces a tiled effect).
        regionwidth=Math.max(0,Math.min(regionwidth,w-regionx+ox));
        regionheight=Math.max(0,Math.min(regionheight,h-regiony+ox));
        regionx=Math.max(ox,regionx);
        regiony=Math.max(oy,regiony);

        // Resizing an image is pretty time-consuming. Therefore, this is done
        // only when it is absolutely needed. This happens when the zoom is
        // changed, or when the chunk of the image which has been resized
        // should be changed.
        if(oldw!=w || oldh!=h || regionx<shiftx || regiony<shifty||
            regionx+regionwidth>shiftx+MAX_RESIZED_WIDTH ||
            regiony+regionheight>shifty+MAX_RESIZED_HEIGHT)
        {
            /*System.out.println("\nPartial image calculation");
            System.out.println("MAX_RESIZED_WIDTH/3="+(MAX_RESIZED_WIDTH/3));
            System.out.println("MAX_RESIZED_HEIGHT/3="+(MAX_RESIZED_HEIGHT/3));
            System.out.println("shiftx="+shiftx+" shifty="+shifty);
            System.out.println("regionx="+regionx+" regiony="+regiony);
            System.out.println("regionx+regionwidth="+(regionx+regionwidth)+
                " regiony+regionheight="+(regiony+regionheight));
            System.out.println("---------------------------");
            System.out.println("oldw!=w "+(oldw!=w));
            System.out.println("oldh!=h "+ (oldh!=h));
            System.out.println("regionx<shiftx "+ (regionx<shiftx));
            System.out.println("regiony<shifty "+ (regiony<shifty));
            System.out.println(
                "regionx+regionwidth>shiftx+MAX_RESIZED_WIDTH " + 
                (regionx+regionwidth>shiftx+MAX_RESIZED_WIDTH));
            System.out.println(
                "regiony+regionheight>shifty+MAX_RESIZED_HEIGHT " +
                (regiony+regionheight>shifty+MAX_RESIZED_HEIGHT));*/
            GraphicsEnvironment env =
                GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice device = env.getDefaultScreenDevice();
            GraphicsConfiguration config = device.getDefaultConfiguration();
            oldw=w;
            oldh=h;
            if(w<MAX_RESIZED_WIDTH && h<MAX_RESIZED_HEIGHT) {
                // Here the image can be resized all together.
                shiftx=ox;
                shifty=oy;
                resizedImg = config.createCompatibleImage(
                    w, h, Transparency.TRANSLUCENT);

                resizedImg.getGraphics().drawImage(img,0,0,w,h,null);
            } else {
                // Here, resizing the image would produce an image too big.
                // Therefore, the image is resized by chunks.
            
                resizedImg = config.createCompatibleImage(
                    MAX_RESIZED_WIDTH, MAX_RESIZED_HEIGHT,
                        Transparency.TRANSLUCENT);
                shiftx=Math.max(regionx-MAX_RESIZED_WIDTH/3,0)+ox;
                shifty=Math.max(regiony-MAX_RESIZED_HEIGHT/3,0)+oy;
                /*System.out.println("---------------------------");
                System.out.println("New shiftx = "+shiftx+ " shifty = "+shifty);
                */
                resizedImg.getGraphics().drawImage(img, 0, 0,
                    MAX_RESIZED_WIDTH,
                    MAX_RESIZED_HEIGHT,
                    (int)(shiftx/mc.getXMagnitude()*resolution/200.0+0.5),
                    (int)(shifty/mc.getXMagnitude()*resolution/200.0+0.5),
                    (int)((shiftx+MAX_RESIZED_WIDTH)
                        /mc.getXMagnitude()*resolution/200.0
                        +0.5),
                    (int)((shifty+MAX_RESIZED_HEIGHT)/
                            mc.getYMagnitude()*resolution/200.0+0.5),
                    null);
            }
        }

        // Draw the resized image at the right place.
        g.drawImage(resizedImg, regionx, regiony,
            regionx+regionwidth,
            regiony+regionheight,
            regionx-shiftx,
            regiony-shifty,
            regionx+regionwidth-shiftx,
            regiony+regionheight-shifty,
            null);
    }
}