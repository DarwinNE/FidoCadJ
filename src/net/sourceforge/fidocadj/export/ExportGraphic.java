package net.sourceforge.fidocadj.export;

import javax.imageio.*;

import java.io.*;
import java.awt.*;
import java.awt.image.*;

import javax.swing.*;

import net.sourceforge.fidocadj.circuit.*;
import net.sourceforge.fidocadj.circuit.controllers.*;
import net.sourceforge.fidocadj.circuit.model.*;
import net.sourceforge.fidocadj.circuit.views.*;
import net.sourceforge.fidocadj.geom.*;
import net.sourceforge.fidocadj.globals.*;
import net.sourceforge.fidocadj.layers.*;
import net.sourceforge.fidocadj.graphic.*;
import net.sourceforge.fidocadj.graphic.swing.*;
import net.sourceforge.fidocadj.graphic.nil.*;

import java.awt.event.*;
import java.util.*;
import java.lang.*;

/** ExportGraphic.java

    Handle graphic export of a Fidocad file
    This class should be used to export the circuit under different graphic
    formats.

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

    Copyright 2007-2018 by Davide Bucci
    </pre>

    @author Davide Bucci
*/
public final class ExportGraphic
{
    private ExportGraphic()
    {
        // Nothing to do.
    }

    /** Exports the circuit contained in circ using the specified parsing
        class.

        @param file the file name of the graphic file which will be created.
        @param P the parsing schematics class which should be used (libraries).
        @param format the graphic format which should be used {png|jpg}.
        @param unitPerPixel the number of unit for each graphic pixel.
        @param antiAlias specify whether the anti alias option should be on.
        @param blackWhite specify that the export should be done in B/W.
        @param ext activate FidoCadJ extensions when exporting
        @param shiftMin shift the exported image at the origin.
        @throws IOException if the file can not be created or an error occurs.
    */
    public static void export(File file,
                        DrawingModel P,
                        String format,
                        double unitPerPixel,
                        boolean antiAlias,
                        boolean blackWhite,
                        boolean ext,
                        boolean shiftMin)
        throws IOException
    {
        exportSizeP( file,
                         P,
                         format,
                         0,
                         0,
                         unitPerPixel,
                         false,
                         antiAlias,
                         blackWhite,
                         ext,
                         shiftMin);
    }

    /** Exports the circuit contained in circ using the specified parsing
        class.

        @param file the file name of the graphic file which will be created.
        @param P the parsing schematics class which should be used (libraries).
        @param format the graphic format which should be used {png|jpg}.
        @param width the image width in pixels (raster images only)
        @param height the image heigth in pixels (raster images only)
        @param antiAlias specify whether the anti alias option should be on.
        @param blackWhite specify that the export should be done in B/W.
        @param ext activate FidoCadJ extensions when exporting
        @param shiftMin shift the exported image at the origin.
        @throws IOException if an error occurs.
    */
    public static void exportSize(File file,
                        DrawingModel P,
                        String format,
                        int width,
                        int height,
                        boolean antiAlias,
                        boolean blackWhite,
                        boolean ext,
                        boolean shiftMin)
        throws IOException
    {
        exportSizeP( file,
                         P,
                         format,
                         width,
                         height,
                         1,
                         true,
                         antiAlias,
                         blackWhite,
                         ext,
                         shiftMin);
    }

    /** Exports the circuit contained in circ using the specified parsing
        class.

        @param file the file name of the graphic file which will be created.
        @param P the parsing schematics class which should be used (libraries).
        @param format the graphic format which should be used {png|jpg}.
        @param unitperpixel the number of unit for each graphic pixel.
        @param width the image width in pixels (raster images only)
        @param heith the image heigth in pixels (raster images only)
        @param setSize if true, calculate resolution from size. If false, it
            does the opposite strategy.
        @param antiAlias specify whether the anti alias option should be on.
        @param blackWhite specify that the export should be done in B/W.
        @param ext activate FidoCadJ extensions when exporting.
        @param shiftMin shift the exported image at the origin.
        @throws IOException if an error occurs.
    */
    private static void exportSizeP(File file,
                        DrawingModel P,
                        String format,
                        int width_t,
                        int height_t,
                        double unitPerPixel_t,
                        boolean setSize,
                        boolean antiAlias,
                        boolean blackWhite,
                        boolean ext,
                        boolean shiftMin)
        throws IOException
    {
        int width=width_t;
        int height=height_t;
        double unitPerPixel=unitPerPixel_t;

        // obtain drawing size
        MapCoordinates m=new MapCoordinates();

        // This solves bug #3299281

        new SelectionActions(P).setSelectionAll(false);

        PointG org=new PointG(0,0);

        DimensionG d = DrawingSize.getImageSize(P, 1,true,org);
        if (setSize) {
            // In this case, the image size is set and so we need to calculate
            // the correct zoom factor in order to fit the drawing in the
            // specified area.


            d.width+=Export.exportBorder;
            d.height+=Export.exportBorder;

            unitPerPixel = Math.min((double)width/(double)d.width,
                (double)height/(double)d.height);
        } else {
            // In this situation, we do have to calculate the size from the
            // specified resolution.

            width=(int)((d.width+Export.exportBorder)*unitPerPixel);
            height=(int)((d.height+Export.exportBorder)*unitPerPixel);
        }
        org.x *=unitPerPixel;
        org.y *=unitPerPixel;

        org.x -= Export.exportBorder*unitPerPixel/2.0;
        org.y -= Export.exportBorder*unitPerPixel/2.0;

        Vector<LayerDesc> ol=P.getLayers();

        BufferedImage bufferedImage;

        // To print in black and white, we only need to create a single layer
        // in which all layers will be exported and drawn.
        // Clearly, the choosen color will be black.
        if(blackWhite) {
            Vector<LayerDesc> v=new Vector<LayerDesc>();
            for (int i=0; i<16;++i)
                v.add(new LayerDesc((new ColorSwing()).black(), // NOPMD
                    ((LayerDesc)ol.get(i)).getVisible(),
                    "B/W",((LayerDesc)ol.get(i)).getAlpha()));

            P.setLayers(v);
        }

        // Center the drawing in the given space.

        m.setMagnitudes(unitPerPixel, unitPerPixel);

        if(shiftMin && !"pcb".equals(format)) {// don't alter geometry
            m.setXCenter(-org.x);              // if exported to pcb-rnd
            m.setYCenter(-org.y);
        }
        if ("png".equals(format)||"jpg".equals(format)) {

            // Create a buffered image in which to draw

            /*  To get an error, try to export this in png at 1200 dpi:

                [FIDOCAD]
                RP 25 15 11000 95000 2

            */
            try {
                bufferedImage = new BufferedImage(width, height,
                                          BufferedImage.TYPE_INT_RGB);

                // Create a graphics contents on the buffered image
                Graphics2D g2d =
                    (Graphics2D)bufferedImage.createGraphics();

                if(antiAlias) {
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                }
                g2d.setColor(Color.white);
                g2d.fillRect(0,0, width, height);
                // Save bitmap
                Drawing drawingAgent = new Drawing(P);
                Graphics2DSwing graphicSwing=new Graphics2DSwing(g2d);
                // This is important for taking into account the dashing size
                graphicSwing.setZoom(m.getXMagnitude());
                drawingAgent.draw(graphicSwing,m);

                ImageIO.write(bufferedImage, format, file);
                // Graphics context no longer needed so dispose it
                g2d.dispose();
            } finally {
                P.setLayers(ol);
            }
        } else if("svg".equals(format)) {
            ExportSVG es = new ExportSVG(file);
            new Export(P).exportDrawing(es, true, false, m);
        } else if("eps".equals(format)) {
            ExportEPS ep = new ExportEPS(file);
            new Export(P).exportDrawing(ep, true, false, m);
        } else if("pgf".equals(format)) {
            ExportPGF ef = new ExportPGF(file);
            new Export(P).exportDrawing(ef, true, false, m);
        } else if("pdf".equals(format)) {
            ExportPDF ef = new ExportPDF(file, new GraphicsNull());
            new Export(P).exportDrawing(ef, true, false, m);
        } else if("scr".equals(format)) {
            ExportEagle ef = new ExportEagle(file);
            new Export(P).exportDrawing(ef, true, false, m);
        } else if("pcb".equals(format)) {
            ExportPCBRND ef = new ExportPCBRND(file);
            new Export(P).exportDrawing(ef, true, false, m);
        } else if("fcd".equals(format)) {
            ExportFidoCad ef = new ExportFidoCad(file);
            ef.setSplitStandardMacros(false);
            ef.setExtensions(ext);
            new Export(P).exportDrawing(ef, true, true, m);
        } else if("fcda".equals(format)) {
            ExportFidoCad ef = new ExportFidoCad(file);
            ef.setSplitStandardMacros(true);
            ef.setExtensions(ext);
            new Export(P).exportDrawing(ef, true, true, m);
        } else {
            IOException E=new IOException(
                "Wrong file format");
            throw E;
        }
        P.setLayers(ol);
    }
}
