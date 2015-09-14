package net.sourceforge.fidocadj;

import javax.swing.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import java.io.*;
import java.awt.print.*;
import java.awt.*;
import java.util.*;

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
    private CircuitPanel CC;

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
        CC=CCr;
        DialogPrint dp=new DialogPrint(fff);
        dp.setMirror(printMirror);
        dp.setFit(printFitToPage);
        dp.setBW(printBlackWhite);
        dp.setLandscape(printLandscape);
        dp.setVisible(true);

        // Get some information about the printing options.
        printMirror = dp.getMirror();
        printFitToPage = dp.getFit();
        printLandscape = dp.getLandscape();
        printBlackWhite=dp.getBW();

        Vector<LayerDesc> ol=CC.P.getLayers();
        if (dp.shouldPrint()) {
            if(printBlackWhite) {
                Vector<LayerDesc> v=new Vector<LayerDesc>();

                // Here we create an alternative array of layers in
                // which all colors are pitch black. This may be
                // useful for PCB's.

                for (int i=0; i<LayerDesc.MAX_LAYERS;++i)
                    v.add(new LayerDesc(new ColorSwing(Color.black),
                        ((LayerDesc)ol.get(i)).getVisible(),
                         "B/W",((LayerDesc)ol.get(i)).getAlpha()));
                CC.P.setLayers(v);
            }
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable(this);
            boolean ok = job.printDialog();
            if (ok) {
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
            CC.P.setLayers(ol);
        }
    }

    /** The printing interface
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

        // Perform an adjustement if we need to fit the drawing to the page.
        if (printFitToPage) {
            MapCoordinates zoomm = DrawingSize.calculateZoomToFit(CC.P,
                (int)pf.getImageableWidth()*16,(int)pf.getImageableHeight()*16,
                false);
            zoom=zoomm.getXMagnitude();
        }

        MapCoordinates m=new MapCoordinates();

        m.setMagnitudes(zoom, zoom);

        PointG o=new PointG(0,0);

        int imageWidth = DrawingSize.getImageSize(CC.P, zoom, false, o).width;
        npages = (int)Math.floor((imageWidth-1)/(double)printerWidth);

        // Check if we need more than one page
        if (printerWidth<imageWidth) {
            g2d.translate(-(printerWidth*page),0);
        }

        // Check if printing is finished.
        if(page>npages) {
            return NO_SUCH_PAGE;
        }
        // Now we perform our rendering
        CC.drawingAgent.draw(new Graphics2DSwing(g2d), m);

        /* tell the caller that this page is part of the printed document */
        return PAGE_EXISTS;
    }
}
