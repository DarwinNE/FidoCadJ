package net.sourceforge.fidocadj;

import javax.swing.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import java.io.*;
import java.awt.print.*;
import java.awt.*;
import java.util.*;
import java.awt.geom.*;

import net.sourceforge.fidocadj.circuit.*;
import net.sourceforge.fidocadj.dialogs.*;
import net.sourceforge.fidocadj.globals.*;
import net.sourceforge.fidocadj.geom.*;
import net.sourceforge.fidocadj.graphic.*;
import net.sourceforge.fidocadj.graphic.swing.*;
import net.sourceforge.fidocadj.layers.*;

/** PrintTools.java

    Class performing interface operations for launching print operations.
    It also reads and stores preferences.

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

    Copyright 2015 by Davide Bucci
    </pre>

    @author Davide Bucci
*/

public class PrintTools implements Printable
{
    // Settings related to the printing mode.
    private boolean printMirror;
    private boolean printFitToPage;
    private boolean printLandscape;
    private boolean printBlackWhite;

    // Margins
    private double topMargin;
    private double bottomMargin;
    private double leftMargin;
    private double rightMargin;

    private CircuitPanel cc;

    /** Standard constructor.
    */
    public PrintTools()
    {
        // some standard configurations
        printMirror = false;
        printFitToPage = false;
        printLandscape = false;
    }

    /** Show a dialog for printing the current drawing.
        @param fff the parent frame which will be used for dialogs and message
            boxes.
        @param CCr the CircuitPanel containing the drawing to be exported.
    */
    public void printDrawing(JFrame fff, CircuitPanel CCr)
    {
        cc=CCr;
        DialogPrint dp=new DialogPrint(fff);
        dp.setMirror(printMirror);
        dp.setFit(printFitToPage);
        dp.setBW(printBlackWhite);
        dp.setLandscape(printLandscape);
        dp.setMargins(topMargin, bottomMargin, leftMargin, rightMargin);

        boolean noexit;
        do {
            noexit=false;
            // Show the (modal) dialog.
            dp.setVisible(true);

            if (!dp.shouldPrint())
                return;

            Vector<LayerDesc> ol=cc.dmp.getLayers();
            // Get some information about the printing options.
            printMirror = dp.getMirror();
            printFitToPage = dp.getFit();
            printLandscape = dp.getLandscape();
            printBlackWhite=dp.getBW();

            try {
                topMargin=dp.getTMargin();
                bottomMargin=dp.getBMargin();
                leftMargin=dp.getLMargin();
                rightMargin=dp.getRMargin();
            } catch (java.lang.NumberFormatException n) {
                JOptionPane.showMessageDialog(dp,
                    Globals.messages.getString("Format_invalid"), "",
                    JOptionPane.INFORMATION_MESSAGE);
                noexit=true;
                continue;
            }

            if(printBlackWhite) {
                Vector<LayerDesc> v=new Vector<LayerDesc>();

                // Here we create an alternative array of layers in
                // which all colors are pitch black. This may be
                // useful for PCB's.

                for (int i=0; i<LayerDesc.MAX_LAYERS;++i)
                    v.add(new LayerDesc(new ColorSwing(Color.black),
                        ((LayerDesc)ol.get(i)).getVisible(),
                         "B/W",((LayerDesc)ol.get(i)).getAlpha()));
                cc.dmp.setLayers(v);
            }
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable(this);
            if (job.printDialog()) {
                try {
                    PrintRequestAttributeSet aset = new
                        HashPrintRequestAttributeSet();
                    // Set the correct printing orientation.
                    if (printLandscape) {
                        aset.add(OrientationRequested.LANDSCAPE);
                    } else {
                        aset.add(OrientationRequested.PORTRAIT);
                    }
                    job.print(aset);
                } catch (PrinterException ex) {
                    // The job did not successfully complete
                    JOptionPane.showMessageDialog(fff,
                        Globals.messages.getString("Print_uncomplete"));
                }
            }
            cc.dmp.setLayers(ol);
        } while (noexit);
    }

    /** The printing interface.
        @param g the graphic context.
        @param pf the page format.
        @param page the page number.
        @return PAGE_EXISTS if the page has to be printed.
        @throws PrinterException if a printing error occurs.
    */
    public int print(Graphics g, PageFormat pf, int page) throws
                                                   PrinterException
    {
        int npages = 0;

        // This might be explained as follows:
        // 1 - The Java printing system normally works with an internal
        // resolution which is 72 dpi (probably inspired by Postscript).
        // 2 - To have a sufficient resolution, this is increased by 16 times,
        // by using the scale method of the graphic object associated to the
        // printer. This gives a 72 dpi * 16=1152 dpi resolution.
        // 3 - The 0.127 mm pitch used in FidoCadJ corresponds to a 200 dpi
        // resolution. Calculating 1152 dpi / 200 dpi gives the 5.76 constant

        double xscale = 1.0/16; // Set 1152 logical units for an inch
        double yscale = 1.0/16; // as the standard resolution is 72
        double zoom = 5.76;     // act in a 1152 dpi resolution as 1:1

        Graphics2D g2d = (Graphics2D)g;

        // User (0,0) is typically outside the imageable area, so we must
        // translate by the X and Y values in the PageFormat to avoid clipping

        if (printMirror) {
            g2d.translate(pf.getImageableX()+pf.getImageableWidth(),
                pf.getImageableY());
            g2d.scale(-xscale,yscale);
        } else {
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            g2d.scale(xscale,yscale);
        }

        int printerWidth = (int)pf.getImageableWidth()*16;

        /*Rectangle2D.Double border = new Rectangle2D.Double(0, 0, printerWidth,
            pf.getImageableHeight()*16);
        g2d.setColor(Color.green);
        g2d.draw(border);*/

        MapCoordinates m;
        int margin=5;
        // Perform an adjustement if we need to fit the drawing to the page.
        if (printFitToPage) {
            m = DrawingSize.calculateZoomToFit(
                cc.getDrawingModel(),
                (int)pf.getImageableWidth()*16-2*margin,
                (int)pf.getImageableHeight()*16-2*margin,
                true);
            zoom=m.getXMagnitude();
        } else {
            m=new MapCoordinates();
            m.setMagnitudes(zoom, zoom);
        }
        PointG o=new PointG(0,0);

        int imageWidth = DrawingSize.getImageSize(
            cc.getDrawingModel(), zoom, true, o).width;
        npages = (int)Math.floor((imageWidth-1)/(double)printerWidth);

        if(printFitToPage) {
            g2d.translate(-2*o.x+margin,-2*o.y+margin);
        }
        // Check if we need more than one page
        if (printerWidth<imageWidth) {
            g2d.translate(-(printerWidth*page),0);
        }

        // Check if printing is finished.
        if(page>npages) {
            return NO_SUCH_PAGE;
        }
        // Now we perform our rendering
        cc.drawingAgent.draw(new Graphics2DSwing(g2d), m);

        /* tell the caller that this page is part of the printed document */
        return PAGE_EXISTS;
    }
}
