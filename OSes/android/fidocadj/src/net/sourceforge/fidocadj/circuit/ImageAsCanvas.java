package net.sourceforge.fidocadj.circuit;

import java.io.IOException;

import net.sourceforge.fidocadj.geom.*;
import net.sourceforge.fidocadj.graphic.*;

/** Dummy class. One may be inspired by the corresponding class in the Swing
    application.

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

    Copyright 2019 by Davide Bucci
    </pre>
*/

public class ImageAsCanvas
{
  
    /** Constructor.
    */
    public ImageAsCanvas()
    {

    }

    /** Specify an image to attach to the current drawing.
        @param f the path and the filename of the image file to
            load and display.
        @throws IOException if the file is not found or can not be loaded.
    */
    public void loadImage(String f)
        throws IOException
    {

    }


    /** Specify the resolution of the image in dots per inch.
        This is employed for the coordinate mapping so that the image size
        is correctly matched with the FidoCadJ coordinate systems.
        @param res image resolution in dots per inch (dpi).
    */
    public void setResolution(double res)
    {
    }

    /** Get the current resolution in dpi.
        @return the current resolution in dots per inch.
    */
    public double getResolution()
    {
        return 0;
    }

    /** Remove the attached image.
    */
    public void removeImage()
    {
    }

    /** Get the current file name.
        @return the current file name
    */
    public String getFilename()
    {
        return "";
    }

    /** Set the coordinates of the origin corner (left topmost one).
        @param x the x coordinate.
        @param y the y coordinate.
    */
    public void setCorner(double x, double y)
    {
    }

    /** Get the x coordinate of the left topmost point of the image (use
        FidoCadJ coordinates).
        @return the x coordinate.
    */
    public double getCornerX()
    {
        return 0;
    }

    /** Track the extreme points of the image in the given coordinate systems.
        @param mc the coordinate systems.
    */
    public void trackExtremePoints(MapCoordinates mc)
    {
    }

    /** Get the y coordinate of the left topmost point of the image (use
        FidoCadJ coordinates).
        @return the y coordinate.
    */
    public double getCornerY()
    {
        return 0;
    }

    /** Draw the current image in the given graphic context.
        @param g the Graphic2D object where the image has to be drawn.
        @param mc the current coordinate mapping.
    */
    public void drawCanvasImage(GraphicsInterface g, MapCoordinates mc)
    {

    }
}