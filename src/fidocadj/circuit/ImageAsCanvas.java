package fidocadj.circuit;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

import fidocadj.geom.MapCoordinates;

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

    Copyright 2017-2023 by Davide Bucci
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

    // Some temporary data used during the calculations.
    private int oldw=0;
    private int oldh=0;
    private int shiftx=0;
    private int shifty=0;
    private final int maxResizedWidth;
    private final int maxResizedHeight;

    /** Constructor.
    */
    public ImageAsCanvas()
    {
        img=null;
        Dimension screensize;

        try {
            screensize=Toolkit.getDefaultToolkit().getScreenSize();
        } catch (HeadlessException eE) {
            screensize=new Dimension(1000,1000);
        }
        maxResizedWidth=screensize.width*3;
        maxResizedHeight=screensize.height*3;
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

    /** Specify an image to attach to the current drawing.
        @param f the path and the filename of the image file to
            load and display.
        @param i the image to be loaded.
    */
    public void loadImage(String f, BufferedImage i)
    {
        img=i;
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

    /** Track the extreme points of the image in the given coordinate systems.
        @param mc the coordinate systems.
    */
    public void trackExtremePoints(MapCoordinates mc)
    {
        if(img==null) {
            return;
        }
        int ox=mc.mapXi(xcorner, ycorner,false);
        int oy=mc.mapYi(xcorner, ycorner,false);
        // The FidoCadJ resolution is 200dpi.
        int w=(int)(200.0*img.getWidth()/resolution*mc.getXMagnitude()+0.5);
        int h=(int)(200.0*img.getHeight()/resolution*mc.getYMagnitude()+0.5);
        mc.trackPoint(ox, oy);
        mc.trackPoint(ox+w, oy+h);
    }

    /** Get the y coordinate of the left topmost point of the image (use
        FidoCadJ coordinates).
        @return the y coordinate.
    */
    public double getCornerY()
    {
        return ycorner;
    }

    /** Draw the current image in the given graphic context.
        @param g the Graphic2D object where the image has to be drawn.
        @param mc the current coordinate mapping.
    */
    public void drawCanvasImage(Graphics2D g, MapCoordinates mc)
    {
        if(img==null) {
            return;
        }

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
            regionx+regionwidth>shiftx+maxResizedWidth ||
            regiony+regionheight>shifty+maxResizedHeight)
        {
            GraphicsEnvironment env =
                GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice device = env.getDefaultScreenDevice();
            GraphicsConfiguration config = device.getDefaultConfiguration();
            oldw=w;
            oldh=h;
            if(w<maxResizedWidth && h<maxResizedHeight) {
                // Here the image can be resized all together.
                shiftx=ox;
                shifty=oy;
                resizedImg = config.createCompatibleImage(
                    w, h, Transparency.TRANSLUCENT);
                // If the resulting image is very small, implement a multi-step
                // resize to improve the rendering quality.
                if(img.getWidth()/w>5) {
                    System.out.print("Multistep reduction");
                    BufferedImage rs=img;
                    BufferedImage rs1=img;
                    BufferedImage rs2;
                    int nw=img.getWidth();
                    int nh=img.getHeight();
                    while (nw>w*2) {
                        System.out.print(".");
                        rs= config.createCompatibleImage(
                            nw/2, nh/2, Transparency.TRANSLUCENT);
                        Graphics2D graphics2D = rs.createGraphics();
                        graphics2D.setRenderingHint(
                            RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                        graphics2D.setRenderingHint(
                            RenderingHints.KEY_RENDERING,
                            RenderingHints.VALUE_RENDER_QUALITY);
                        graphics2D.setRenderingHint(
                            RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
                        graphics2D.drawImage(rs1,0,0,nw/2,nh/2,null);
                        nw=nw/2;
                        nh=nh/2;
                        rs2=rs;
                        rs=rs1;
                        rs1=rs2;
                    }
                    System.out.print("\n");
                    resizedImg.getGraphics().drawImage(rs,0,0,w,h,null);
                } else {
                    resizedImg.getGraphics().drawImage(img,0,0,w,h,null);
                }
            } else {
                // Here, resizing the image would produce an image too big.
                // Therefore, the image is resized by chunks.

                resizedImg = config.createCompatibleImage(
                    maxResizedWidth, maxResizedHeight,
                        Transparency.TRANSLUCENT);
                shiftx=Math.max(regionx-maxResizedWidth/3,0)+ox;
                shifty=Math.max(regiony-maxResizedHeight/3,0)+oy;
                /*System.out.println("---------------------------");
                System.out.println("New shiftx = "+shiftx+ " shifty = "+shifty);
                */
                resizedImg.getGraphics().drawImage(img, 0, 0,
                    maxResizedWidth,
                    maxResizedHeight,
                    (int)(shiftx/mc.getXMagnitude()*resolution/200.0+0.5),
                    (int)(shifty/mc.getXMagnitude()*resolution/200.0+0.5),
                    (int)((shiftx+maxResizedWidth)
                        /mc.getXMagnitude()*resolution/200.0
                        +0.5),
                    (int)((shifty+maxResizedHeight)/
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