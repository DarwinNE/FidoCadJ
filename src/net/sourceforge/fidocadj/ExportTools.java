package net.sourceforge.fidocadj;

import javax.swing.*;

import java.util.prefs.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import java.awt.datatransfer.*;

import net.sourceforge.fidocadj.export.*;
import net.sourceforge.fidocadj.circuit.*;
import net.sourceforge.fidocadj.circuit.model.*;
import net.sourceforge.fidocadj.dialogs.*;
import net.sourceforge.fidocadj.globals.*;
import net.sourceforge.fidocadj.geom.*;


/** ExportTools.java

    Class performing interface operations for launching export operations.
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
    along with FidoCadJ. If not,
    @see <a href=http://www.gnu.org/licenses/>http://www.gnu.org/licenses/</a>.

    Copyright 2015-2020 by Davide Bucci
</pre>

    @author Davide Bucci
*/

public class ExportTools implements ClipboardOwner
{
    // Export default properties
    private String exportFileName;
    private String exportFormat;
    private boolean exportBlackWhite;
    private double exportUnitPerPixel;
    private double exportMagnification;
    private int exportXsize;
    private int exportYsize;
    private boolean exportResolutionBased;
    private boolean splitLayers;
    final private Preferences prefs;
    private ChangeCoordinatesListener coordL;

    /** Standard constructor.
        @param p the preferences object which will be used to save or
        retrieve the settings. If null, preferences will not be stored.
    */
    public ExportTools(Preferences p)
    {
        exportFileName="";
        exportMagnification=1.0;
        prefs=p;
        exportBlackWhite=false;
        exportFormat = "";
        splitLayers=false;
    }

    /** Read the preferences regarding the export.
    */
    public void readPrefs()
    {
        if(prefs!=null) {
            exportFormat = prefs.get("EXPORT_FORMAT", "png");
            exportUnitPerPixel= Double.parseDouble(
                prefs.get("EXPORT_UNITPERPIXEL", "1"));
            exportMagnification = Double.parseDouble(
                prefs.get("EXPORT_MAGNIFICATION", "1"));
            exportBlackWhite = prefs.get("EXPORT_BW", "false").equals("true");

            exportXsize = Integer.parseInt(
                prefs.get("EXPORT_XSIZE", "800"));
            exportYsize = Integer.parseInt(
                prefs.get("EXPORT_YSIZE", "600"));
            exportResolutionBased = prefs.get("EXPORT_RESOLUTION_BASED",
                "false").equals("true");
            splitLayers = prefs.get("EXPORT_SPLIT_LAYERS",
                "false").equals("true");
        }
    }

    /** Show a dialog for exporting the current drawing in the clipboard.
        @param fff the parent frame which will be used for dialogs and message
            boxes.
        @param CC the CircuitPanel containing the drawing to be exported.
    */
    public void exportAsCopiedImage(JFrame fff, CircuitPanel CC)
    {
        // At first, we create and configure the dialog allowing the user
        // to choose the exporting options
        DialogCopyAsImage dcai=new DialogCopyAsImage(fff, CC.getDrawingModel());
        dcai.setAntiAlias(true);
        dcai.setXsizeInPixels(exportXsize);
        dcai.setYsizeInPixels(exportYsize);
        dcai.setResolutionBasedExport(exportResolutionBased);
        dcai.setUnitPerPixel(exportUnitPerPixel);
        dcai.setBlackWhite(exportBlackWhite);
        // Once configured, we show the modal dialog
        dcai.setVisible(true);
        if (dcai.shouldExport()) {
            exportUnitPerPixel=dcai.getUnitPerPixel();
            exportBlackWhite=dcai.getBlackWhite();
            exportResolutionBased=dcai.getResolutionBasedExport();
            try {
                exportXsize=dcai.getXsizeInPixels();
                exportYsize=dcai.getYsizeInPixels();
            } catch (java.lang.NumberFormatException E) {
                JOptionPane.showMessageDialog(null,
                    Globals.messages.getString("Format_invalid"),
                    Globals.messages.getString("Warning"),
                    JOptionPane.INFORMATION_MESSAGE );
                exportXsize=100;
                exportYsize=100;
            }

            // We do the export
            RunExport doExport = new RunExport();
            doExport.setCoordinateListener(coordL);
            try {
                File fexp=File.createTempFile("FidoCadJ",".png");
                doExport.setParam(fexp,  CC.dmp,
                    "png", exportUnitPerPixel,
                    dcai.getAntiAlias(),exportBlackWhite,!CC.extStrict,
                    exportResolutionBased,
                    exportXsize,
                    exportYsize,
                    false,
                    fff);

                doExport.run();
                BufferedImage img = null;
                img = ImageIO.read(fexp);
                setClipboard(img);
            } catch (IOException E) {
                System.err.println("Issues reading image: "+E);
            } 
            if(prefs!=null) {
                prefs.put("EXPORT_UNITPERPIXEL", ""+exportUnitPerPixel);
                prefs.put("EXPORT_MAGNIFICATION", ""+exportMagnification);
                prefs.put("EXPORT_BW", exportBlackWhite?"true":"false");
                prefs.put("EXPORT_RESOLUTION_BASED",
                    exportResolutionBased?"true":"false");
                prefs.put("EXPORT_XSIZE", ""+exportXsize);
                prefs.put("EXPORT_YSIZE", ""+exportYsize);
            }
        }
    }

    /** Show a dialog for exporting the current drawing.
        @param fff the parent frame which will be used for dialogs and message
            boxes.
        @param CC the CircuitPanel containing the drawing to be exported.
        @param openFileDirectory the directory where to search if no file
            name has been already defined for the export (for example, because
            it is the first time an export is done).
    */
    public void launchExport(JFrame fff, CircuitPanel CC,
        String openFileDirectory)
    {
        // At first, we create and configure the dialog allowing the user
        // to choose the exporting options
        DialogExport export=new DialogExport(fff, CC.getDrawingModel());
        export.setAntiAlias(true);
        export.setFormat(exportFormat);
        export.setXsizeInPixels(exportXsize);
        export.setYsizeInPixels(exportYsize);
        export.setResolutionBasedExport(exportResolutionBased);
        export.setSplitLayers(splitLayers);

        // The default export directory is the same where the FidoCadJ file
        // are opened.
        if("".equals(exportFileName)) {
            exportFileName=openFileDirectory;
        }
        export.setFileName(exportFileName);
        export.setUnitPerPixel(exportUnitPerPixel);
        export.setBlackWhite(exportBlackWhite);
        export.setMagnification(exportMagnification);

        // Once configured, we show the modal dialog
        export.setVisible(true);
        if (export.shouldExport()) {
            exportFileName=export.getFileName();
            exportFormat=export.getFormat();
            // The resolution based export should be used only for bitmap
            // file formats
            if("png".equals(exportFormat) ||
                "jpg".equals(exportFormat)) 
            {
                exportResolutionBased=export.getResolutionBasedExport();
                exportUnitPerPixel=export.getUnitPerPixel();
            } else {
                exportResolutionBased=true;
                exportUnitPerPixel = export.getMagnification();
            }

            exportBlackWhite=export.getBlackWhite();
            exportMagnification = export.getMagnification();
            splitLayers=export.getSplitLayers();

            try {
                exportXsize=export.getXsizeInPixels();
                exportYsize=export.getYsizeInPixels();
            } catch (java.lang.NumberFormatException E) {
                JOptionPane.showMessageDialog(null,
                    Globals.messages.getString("Format_invalid"),
                    Globals.messages.getString("Warning"),
                    JOptionPane.INFORMATION_MESSAGE );
                exportXsize=100;
                exportYsize=100;
            }

            File f = new File(exportFileName);
            // We first check if the file is a directory
            if(f.isDirectory()) {
                JOptionPane.showMessageDialog(null,
                    Globals.messages.getString("Warning_noname"),
                    Globals.messages.getString("Warning"),
                    JOptionPane.INFORMATION_MESSAGE );
                return;
            }

            int selection;

            // We first check if the file name chosen by the user has a correct
            // file extension, coherent with the file format chosen.
            if(!Globals.checkExtension(exportFileName, exportFormat)) {
                selection = JOptionPane.showConfirmDialog(null,
                    Globals.messages.getString("Warning_extension"),
                    Globals.messages.getString("Warning"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                // If useful, we correct the extension.
                if(selection==JOptionPane.OK_OPTION)
                    exportFileName = Globals.adjustExtension(
                        exportFileName, exportFormat);
                f = new File(exportFileName);
            }

            // If the file already exists, we asks for confirmation
            if(f.exists()) {
                selection = JOptionPane.showConfirmDialog(null,
                    Globals.messages.getString("Warning_overwrite"),
                    Globals.messages.getString("Warning"),
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                if(selection!=JOptionPane.OK_OPTION)
                    return;
            }
            // We do the export
            RunExport doExport = new RunExport();
            doExport.setCoordinateListener(coordL);
            // Here we use the multithreaded structure of Java.
            doExport.setParam(new File(exportFileName),  CC.dmp,
                exportFormat, exportUnitPerPixel,
                export.getAntiAlias(),exportBlackWhite,!CC.extStrict,
                exportResolutionBased,
                exportXsize,
                exportYsize,
                splitLayers,
                fff);

            SwingUtilities.invokeLater(doExport);

            if(prefs!=null) {
                prefs.put("EXPORT_FORMAT", exportFormat);
                prefs.put("EXPORT_UNITPERPIXEL", ""+exportUnitPerPixel);
                prefs.put("EXPORT_MAGNIFICATION", ""+exportMagnification);
                prefs.put("EXPORT_BW", exportBlackWhite?"true":"false");
                prefs.put("EXPORT_RESOLUTION_BASED",
                    exportResolutionBased?"true":"false");
                prefs.put("EXPORT_XSIZE", ""+exportXsize);
                prefs.put("EXPORT_YSIZE", ""+exportYsize);
                prefs.put("EXPORT_SPLIT_LAYERS", splitLayers?"true":"false");
            }
            /*
                The following code would require a thread safe implementation
                of some of the inner classes (such as CircuitModel), which is
                indeed not the case...

            Thread thread = new Thread(doExport);
            thread.setDaemon(true);
            // Start the thread
            thread.start();
            */
        }
    }
    public void lostOwnership( Clipboard clip, Transferable trans )
    {
    }
    /** Set the coordinate listener which is employed here for showing
        message in a non-invasive way.
        @param c the listener.
    */
    public void setCoordinateListener(ChangeCoordinatesListener c)
    {
        coordL=c;
    }

    //This method writes a image to the system clipboard.
    public void setClipboard(Image image)
    {
        TransferableImage imgSel = new TransferableImage(image);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
            imgSel,
            this);
    }
    private class TransferableImage implements Transferable {
         
        Image i;
         
        public TransferableImage(Image i) {
            this.i = i;
        }
         
        public Object getTransferData(DataFlavor flavor) throws 
            UnsupportedFlavorException, IOException
        {
            if (flavor.equals(DataFlavor.imageFlavor) && i != null) {
                return i;
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }
         
        public DataFlavor[] getTransferDataFlavors()
        {
            DataFlavor[] flavors = new DataFlavor[1];
            flavors[0] = DataFlavor.imageFlavor;

            return flavors;
        }
         
        public boolean isDataFlavorSupported(DataFlavor flavor)
        {
            DataFlavor[] flavors = getTransferDataFlavors();
            for (int i = 0; i < flavors.length; ++i) {
                if (flavor.equals(flavors[i])) {
                    return true;
                }
            }
            return false;
        }
    }
}

