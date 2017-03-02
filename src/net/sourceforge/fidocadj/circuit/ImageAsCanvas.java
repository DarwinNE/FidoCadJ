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

    /** Constructor.
    */
    public ImageAsCanvas()
    {
        img=null;
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

    /** Draw the current image in the given graphic context.
        @param g the Graphic2D object where the image has to be drawn.
        @param mc the current coordinate mapping.
    */
    public void drawCanvasImage(Graphics2D g, MapCoordinates mc)
    {
        if(img==null)
            return;

        // The FidoCadJ resolution is 200dpi.
        int w=(int)(200*img.getWidth()/resolution*mc.getXMagnitude()+0.5);
        int h=(int)(200*img.getHeight()/resolution*mc.getYMagnitude()+0.5);

        // Resizing an image is pretty time-consuming. Therefore, this is done
        // only when it is absolutely needed (usually when the zoom is
        // changed).
        if(oldw!=w || oldh!=h) {
            GraphicsEnvironment env =
                GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice device = env.getDefaultScreenDevice();
            GraphicsConfiguration config = device.getDefaultConfiguration();
            // Well, I have some doubts here. I suspect that in some case the
            // image size would be requiring an excessive amount of memory.
            // I will perform some test when I will have a minimum of user
            // interface ready so that the tests become feasible.
            resizedImg = config.createCompatibleImage(
                w, h, Transparency.TRANSLUCENT);

            resizedImg.getGraphics().drawImage(img,0,0,w,h,null);
            oldw=w;
            oldh=h;
        }
        g.drawImage(resizedImg, mc.mapXi(xcorner, ycorner,false),
                mc.mapYi(xcorner, ycorner,false), null);
    }
}