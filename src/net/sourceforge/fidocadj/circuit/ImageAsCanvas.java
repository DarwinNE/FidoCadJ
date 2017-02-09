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
    private BufferedImage resizedImg;
    private double resolution=200;
    private int xcorner=0;
    private int ycorner=0;

    /** Constructor.
    */
    public ImageAsCanvas()
    {
        img=null;
    }

    /** Specify an image to attach to the current drawing.
        @param filename the path and the filename of the image file to
            load and display.
        @throws IOException if the file is not found or can not be loaded.
    */
    public void loadImage(String filename)
        throws IOException
    {
        img=ImageIO.read(new File(filename));
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
                img.getWidth(), img.getHeight(), Transparency.TRANSLUCENT);

            resizedImg.getGraphics().drawImage(img, xcorner, ycorner,
                xcorner+w, ycorner+h, null);
            oldw=w;
            oldh=h;
        }
        g.drawImage(resizedImg, xcorner, ycorner, null);
    }
}