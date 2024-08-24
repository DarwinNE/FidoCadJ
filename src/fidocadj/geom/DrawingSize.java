package fidocadj.geom;

import fidocadj.circuit.model.DrawingModel;
import fidocadj.circuit.views.Drawing;
import fidocadj.graphic.PointG;
import fidocadj.graphic.DimensionG;
import fidocadj.graphic.nil.GraphicsNull;


/**
    Calculate the size of a given drawing.

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

    Copyright 2008-2023 by Davide Bucci
    </pre>

    @author Davide Bucci
*/
public final class DrawingSize
{
    /** Private constructor, for Utility class pattern
    */
    private DrawingSize ()
    {
        // nothing
    }

    /** Get the image size.
        @param dm the model class containing the drawing.
        @param unitperpixel the zoom set to be used.
        @param countMin specifies that the size should be calculated counting
            the minimum x and y coordinates, and not the origin.
        @param origin is updated with the image origin.
        @return the image size stored in a {@link DimensionG} object.
    */
    public static DimensionG getImageSize(DrawingModel dm,
                                  double unitperpixel,
                                  boolean countMin,
                                  PointG origin)
    {
        int width;
        int height;

        MapCoordinates m=new MapCoordinates();
        m.setMagnitudes(unitperpixel, unitperpixel);
        m.setXCenter(0);
        m.setYCenter(0);

        // force an in-depth recalculation
        dm.setChanged(true);
        Drawing drawingAgent = new Drawing(dm);
        drawingAgent.draw(new GraphicsNull(),m);
        dm.imgCanvas.trackExtremePoints(m);
        dm.setChanged(true);

        // Calculate image size
        if(countMin) {
            width=m.getXMax()-m.getXMin();
            height=m.getYMax()-m.getYMin();
        } else {
            width=m.getXMax();
            height=m.getYMax();
        }

        // Verify that the image size is reasonable
        if(width<=0) {
            width=1;
        }
        if(height<=0) {
            height=1;
        }

        if (m.getXMax() >= m.getXMin() &&
            m.getYMax() >= m.getYMin())
        {
            origin.x=m.getXMin();
            origin.y=m.getYMin();
        } else {
            origin.x=0;
            origin.y=0;
        }

        return new DimensionG(width, height);
    }

    /** Get the image origin.
        @param dm the model class containing the drawing.
        @param unitperpixel the zoom set to be used.
        @return the origin coordinates in logical units, stored in a
        {@link PointG} object.
    */
    public static PointG getImageOrigin(DrawingModel dm, double unitperpixel)
    {
        int originx;
        int originy;

        // force an in-depth recalculation
        dm.setChanged(true);
        MapCoordinates m=new MapCoordinates();
        m.setMagnitudes(unitperpixel, unitperpixel);
        m.setXCenter(0);
        m.setYCenter(0);

        // Draw the image. In this way, the min and max coordinates will be
        // tracked.
        Drawing drawingAgent = new Drawing(dm);
        dm.imgCanvas.trackExtremePoints(m);
        drawingAgent.draw(new GraphicsNull(), m);
        dm.setChanged(true);

        // Verify that the image size is correct
        if (m.getXMax() >= m.getXMin() &&
            m.getYMax() >= m.getYMin())
        {
            originx=m.getXMin();
            originy=m.getYMin();
        } else {
            originx=0;
            originy=0;
        }

        return new PointG(originx, originy);
    }

    /** Calculate the zoom to fit the given size in pixel (i.e. the viewport
        size).
        @param dm the current drawing model.
        @param sizex the width of the area to be used for calculations.
        @param sizey the height of the area to be used for calculations.
        @param countMin specify if the absolute or relative size should be
            taken into account. In other words, consider (countMin=false) or
            not (countMin=true) the origin as part of the drawing.
        @return the zoom to fit settings stored in a new {@link MapCoordinates}
            object.
    */
    public static MapCoordinates calculateZoomToFit(DrawingModel dm, int sizex,
        int sizey, boolean countMin)
    {
        // Here we calculate the zoom to fit parameters
        double maxsizex;
        double maxsizey;
        PointG org=new PointG(0,0);

        MapCoordinates newZoom=new MapCoordinates();

        // Determine the size and the origin of the current drawing.
        DimensionG d = getImageSize(dm,1,countMin, org);
        maxsizex=d.width+1;
        maxsizey=d.height+1;

        if (!countMin) {
            org=new PointG(0,0);
        }

        double zoomx=1.0/(maxsizex/(double)sizex);
        double zoomy=1.0/(maxsizey/(double)sizey);

        double z= zoomx>zoomy ? zoomy:zoomx;

        z=Math.round(z*100.0)/100.0;        // 0.20.5

        if(z<MapCoordinates.MIN_MAGNITUDE) {
            z=MapCoordinates.MIN_MAGNITUDE;
        }

        newZoom.setMagnitudesNoCheck(z,z);
        // The zoom setting might have been rounded, or bounded.
        z = newZoom.getYMagnitude();

        newZoom.setXCenter(org.x*z);
        newZoom.setYCenter(org.y*z);

        return newZoom;
    }
}