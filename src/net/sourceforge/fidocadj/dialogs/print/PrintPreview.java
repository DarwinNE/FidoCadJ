package net.sourceforge.fidocadj.dialogs.print;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;

import java.awt.print.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.*;

import net.sourceforge.fidocadj.circuit.*;
import net.sourceforge.fidocadj.circuit.controllers.ParserActions;
import net.sourceforge.fidocadj.globals.*;
import net.sourceforge.fidocadj.geom.*;
import net.sourceforge.fidocadj.*;

/** Shows a print preview.

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
public class PrintPreview extends CircuitPanel implements ComponentListener
{
    private PageFormat pageDescription;
    private double topMargin;
    private double bottomMargin;
    private double leftMargin;
    private double rightMargin;
    BufferedImage pageImage;
    PrintTools printObject;
    DialogPrint dialog;

    /** Constructor.
        @param isEditable true if the panel should be editable.
        @param p the PageFormat description.
        @param ddp the DialogPrint object to communicate with.
    */
    public PrintPreview(boolean isEditable, PageFormat p, DialogPrint ddp)
    {
        super(isEditable);
        pageDescription=p;
        dialog=ddp;
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        setGridVisibility(false);
        addComponentListener(this);
        int width=200;
        int height=320;
        pageImage = new BufferedImage(width, height,
            BufferedImage.TYPE_INT_RGB);
        printObject=new PrintTools();
        printObject.associateToCircuitPanel(this);
        printObject.setShowMargins(true);

        Graphics2D g2=(Graphics2D)pageImage.createGraphics();
        g2.setColor(Color.white);
        g2.fillRect(0,0,width,height);
        g2.scale(1.0/160,1.0/160);
        try {
            printObject.print(g2,
                pageDescription, 0);
        } catch (PrinterException pe)
        {
            System.err.println("Some problem here!");
        }
    }

    /** Set the size of the margins, in centimeters. The orientation of those
        margins should correspond to the page in the portrait orientation.
        @param tm top margin.
        @param bm bottom margin.
        @param lm left margin.
        @param rm right margin.
    */
    public void setMargins(double tm, double bm, double lm, double rm)
    {
        topMargin=tm;
        bottomMargin=bm;
        leftMargin=lm;
        rightMargin=rm;
    }

    /** Show the margins.
        @param g the graphic context where to draw.
    */
    @Override
    public void paintComponent (Graphics g)
    {
        getDrawingModel().setChanged(true); // Needed?
        Color c = g.getColor();
        Graphics2D g2 = (Graphics2D) g;
        int shadowShiftX=4;
        int shadowShiftY=4;

        double baseline=getWidth()*0.6;     // TODO: correct getHeight small
        double ratio=pageDescription.getHeight()/pageDescription.getWidth();

        if(dialog.getLandscape()) {
            baseline=getWidth()*0.8;
            ratio=pageDescription.getWidth()/pageDescription.getHeight();
        }

        // Draw the shadow of the page.
        g2.setColor(Color.gray.darker());
        g2.fillRect((int)Math.round(getWidth()/2.0-baseline/2.0)+shadowShiftX,
            (int)Math.round(getHeight()/2.0-baseline*ratio/2.0)+shadowShiftY,
            (int)Math.round(baseline),
            (int)Math.round(baseline*ratio));
        // Draw the image containing the preview.
        g2.drawImage(pageImage,
            (int)Math.round(getWidth()/2.0-baseline/2.0),
            (int)Math.round(getHeight()/2.0-baseline*ratio/2.0),
            null);

        // Draw the contour of the page.
        g2.setColor(Color.black);
        g2.drawRect((int)Math.round(getWidth()/2.0-baseline/2.0),
            (int)Math.round(getHeight()/2.0-baseline*ratio/2.0),
            (int)Math.round(baseline),
            (int)Math.round(baseline*ratio));

        g.setColor(c);
    }

    /** Called when the panel is resized.
        TODO: this is not very memory efficient, since an image is created
        each time the panel is resized.
        @param e the event descriptor.
    */
    public void componentResized(ComponentEvent e)
    {
        updatePreview();
    }

    /** Update the printing preview by calculating again the image
        containing it. It will be shown at the following repaint
        operation.
    */
    public void updatePreview()
    {
        printObject.configurePrinting(dialog, pageDescription, false);
        System.out.println("update preview: topMargin="+topMargin);

        double baseline=getWidth()*0.6;
        double ratio=pageDescription.getHeight()/pageDescription.getWidth();
        if(dialog.getLandscape()) {
            baseline=getWidth()*0.8;
            ratio=pageDescription.getWidth()/pageDescription.getHeight();
        }
        setMapCoordinates(DrawingSize.calculateZoomToFit(getDrawingModel(),
            (int)Math.round(baseline), (int)Math.round(baseline*ratio),true));

        int width=(int)baseline;
        int height=(int)Math.round(baseline*ratio);

        pageImage = new BufferedImage(width, height,
            BufferedImage.TYPE_INT_RGB);
        Graphics2D g2=(Graphics2D)pageImage.createGraphics();

        // Activate anti-aliasing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.white);
        g2.fillRect(0,0,width,height);
        g2.scale(width/pageDescription.getWidth(),
            height/pageDescription.getHeight());

        try {
            printObject.setMargins(topMargin, bottomMargin,
                leftMargin, rightMargin);
            printObject.print(g2, pageDescription, 0);
        } catch (PrinterException pe)
        {
            System.err.println("Some problem here!");
        }
    }

    /** Called when the panel is hidden.
        @param e the event descriptor.
    */
    public void componentHidden(ComponentEvent e)
    {
    }

    /** Called when the panel is moved.
        @param e the event descriptor.
    */
    public void componentMoved(ComponentEvent e)
    {
    }

    /** Called when the panel is shown.
        @param e the event descriptor.
    */
    public void componentShown(ComponentEvent e)
    {
    }
}