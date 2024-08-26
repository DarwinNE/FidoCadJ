package fidocadj;

import javax.swing.*;

import java.io.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import java.awt.datatransfer.*;

import fidocadj.circuit.CircuitPanel;
import fidocadj.dialogs.DialogCopyAsImage;
import fidocadj.dialogs.DialogExport;
import fidocadj.globals.Globals;
import fidocadj.geom.ChangeCoordinatesListener;
import fidocadj.globals.SettingsManager;


/** ExportTools.java

    Class performing interface operations for launching export operations.
    It also reads and stores preferences.
    This class also contains code to export a drawing as a picture, then load
    the exported image in the clipboard.

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

    Copyright 2015-2023 by Davide Bucci
</pre>

    @author Davide Bucci
*/

public class ExportTools implements ClipboardOwner
{
    // Export default properties
    private String exportFilename;
    private String exportFormat;
    private boolean exportBlackWhite;
    private double exportUnitPerPixel;
    private double exportMagnification;
    private int exportXsize;
    private int exportYsize;
    private boolean exportResolutionBased;
    private boolean splitLayers;
    private ChangeCoordinatesListener coordL;

    /** Standard constructor.
    */
    public ExportTools()
    {
        exportFilename="";
        exportMagnification=1.0;
        exportBlackWhite=false;
        exportFormat = "";
        splitLayers=false;
    }

    /** Read the preferences regarding the export.
    */
    public void readPrefs()
    {
        exportFormat = SettingsManager.get("EXPORT_FORMAT", "png");
        exportUnitPerPixel= Double.parseDouble(
            SettingsManager.get("EXPORT_UNITPERPIXEL", "1"));
        exportMagnification = Double.parseDouble(
            SettingsManager.get("EXPORT_MAGNIFICATION", "1"));
        exportBlackWhite = "true".equals(SettingsManager.get("EXPORT_BW",
             "false"));

        exportXsize = Integer.parseInt(
            SettingsManager.get("EXPORT_XSIZE", "800"));
        exportYsize = Integer.parseInt(
            SettingsManager.get("EXPORT_YSIZE", "600"));
        exportResolutionBased = "true".equals(
            SettingsManager.get("EXPORT_RESOLUTION_BASED","false"));
        splitLayers = "true".equals(SettingsManager.get("EXPORT_SPLIT_LAYERS",
            "false"));
    }

    /** Show a dialog for exporting the current drawing in the clipboard.
        @param fff the parent frame which will be used for dialogs and message
            boxes.
        @param cC the CircuitPanel containing the drawing to be exported.
    */
    public void exportAsCopiedImage(JFrame fff, CircuitPanel cC)
    {
        // At first, we create and configure the dialog allowing the user
        // to choose the exporting options
        DialogCopyAsImage dcai=new DialogCopyAsImage(fff, cC.getDrawingModel());
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
            } catch (NumberFormatException eE) {
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
                doExport.setParam(fexp,  cC.getDrawingModel(),
                    "png", exportUnitPerPixel,
                    dcai.getAntiAlias(),exportBlackWhite,!cC.extStrict,
                    exportResolutionBased,
                    exportXsize,
                    exportYsize,
                    false,
                    fff);

                doExport.run();
                BufferedImage img = null;
                img = ImageIO.read(fexp);
                setClipboard(img);
            } catch (IOException eE) {
                System.err.println("Issues reading image: "+eE);
            }
            SettingsManager.put("EXPORT_UNITPERPIXEL", ""+exportUnitPerPixel);
            SettingsManager.put("EXPORT_MAGNIFICATION", ""+exportMagnification);
            SettingsManager.put("EXPORT_BW", exportBlackWhite?"true":"false");
            SettingsManager.put("EXPORT_RESOLUTION_BASED",
                exportResolutionBased?"true":"false");
            SettingsManager.put("EXPORT_XSIZE", ""+exportXsize);
            SettingsManager.put("EXPORT_YSIZE", ""+exportYsize);
        }
    }

    /** Show a dialog for exporting the current drawing.
        @param fff the parent frame which will be used for dialogs and message
            boxes.
        @param cC the CircuitPanel containing the drawing to be exported.
        @param openFileDirectory the directory where to search if no file
            name has been already defined for the export (for example, because
            it is the first time an export is done).
    */
    public void launchExport(JFrame fff, CircuitPanel cC,
        String openFileDirectory)
    {
        // At first, we create and configure the dialog allowing the user
        // to choose the exporting options
        DialogExport export=new DialogExport(fff, cC.getDrawingModel());
        export.setAntiAlias(true);
        export.setFormat(exportFormat);
        export.setXsizeInPixels(exportXsize);
        export.setYsizeInPixels(exportYsize);
        export.setResolutionBasedExport(exportResolutionBased);
        export.setSplitLayers(splitLayers);

        // The default export directory is the same where the FidoCadJ file
        // are opened.
        if("".equals(exportFilename)) {
            exportFilename=openFileDirectory;
        }
        export.setFilename(exportFilename);
        export.setUnitPerPixel(exportUnitPerPixel);
        export.setBlackWhite(exportBlackWhite);
        export.setMagnification(exportMagnification);

        // Once configured, we show the modal dialog
        export.setVisible(true);
        if (export.shouldExport()) {
            exportFilename=export.getFilename();
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
            } catch (NumberFormatException eE) {
                JOptionPane.showMessageDialog(null,
                    Globals.messages.getString("Format_invalid"),
                    Globals.messages.getString("Warning"),
                    JOptionPane.INFORMATION_MESSAGE );
                exportXsize=100;
                exportYsize=100;
            }

            File f = new File(exportFilename);
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
            if(!Globals.checkExtension(exportFilename, exportFormat)) {
                selection = JOptionPane.showConfirmDialog(null,
                    Globals.messages.getString("Warning_extension"),
                    Globals.messages.getString("Warning"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                // If useful, we correct the extension.
                if(selection==JOptionPane.OK_OPTION) {
                    exportFilename = Globals.adjustExtension(
                        exportFilename, exportFormat);
                }
                f = new File(exportFilename);
            }

            // If the file already exists, we asks for confirmation
            if(f.exists()) {
                selection = JOptionPane.showConfirmDialog(null,
                    Globals.messages.getString("Warning_overwrite"),
                    Globals.messages.getString("Warning"),
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                if(selection!=JOptionPane.OK_OPTION) {
                    return;
                }
            }
            // We do the export
            RunExport doExport = new RunExport();
            doExport.setCoordinateListener(coordL);
            // Here we use the multithreaded structure of Java.
            doExport.setParam(new File(exportFilename),  cC.getDrawingModel(),
                exportFormat, exportUnitPerPixel,
                export.getAntiAlias(),exportBlackWhite,!cC.extStrict,
                exportResolutionBased,
                exportXsize,
                exportYsize,
                splitLayers,
                fff);

            SwingUtilities.invokeLater(doExport);

            SettingsManager.put("EXPORT_FORMAT", exportFormat);
            SettingsManager.put("EXPORT_UNITPERPIXEL", ""+exportUnitPerPixel);
            SettingsManager.put("EXPORT_MAGNIFICATION", ""+exportMagnification);
            SettingsManager.put("EXPORT_BW", exportBlackWhite?"true":"false");
            SettingsManager.put("EXPORT_RESOLUTION_BASED",
                    exportResolutionBased?"true":"false");
            SettingsManager.put("EXPORT_XSIZE", ""+exportXsize);
            SettingsManager.put("EXPORT_YSIZE", ""+exportYsize);
            SettingsManager.put("EXPORT_SPLIT_LAYERS",
                    splitLayers?"true":"false");

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
    /** Called by the system when the application looses ownership over the
        clipboard contents. This is here because an export operation is done
        when the "Copy as a picture" operation is performed.

        @param clip the current clipboard object.
        @param trans tha object to be transfered.
    */
    @Override public void lostOwnership(Clipboard clip, Transferable trans)
    {
        // There is no need to do something in particular.
    }
    /** Set the coordinate listener which is employed here for showing
        message in a non-invasive way.
        @param c the listener.
    */
    public void setCoordinateListener(ChangeCoordinatesListener c)
    {
        coordL=c;
    }

    /** This method writes a image to the system clipboard.
        @param image the image to be loaded in the clipboard.
    */
    public void setClipboard(Image image)
    {
        TransferableImage imgSel = new TransferableImage(image);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
            imgSel,
            this);
    }

    /** Origin of this code:
    https://stackoverflow.com/questions/4552045/copy-bufferedimage-to-clipboard

        DB: I checked it, it seems reasonable and robust and it works well.
        Using macOS, I noticed that the system was not working for Java version
        1.7. The types of the object copied in the clipboard were not those
        that standard macOS applications expect. I updated to Java 14 and it
        started to work flawlessly.
    */
    private static class TransferableImage implements Transferable
    {
        final Image i;
        public TransferableImage(Image i) {
            this.i = i;
        }

        @Override public Object getTransferData(DataFlavor flavor) throws
            UnsupportedFlavorException, IOException
        {
            if (flavor.equals(DataFlavor.imageFlavor) && i != null) {
                return i;
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }

        @Override public DataFlavor[] getTransferDataFlavors()
        {
            DataFlavor[] flavors = new DataFlavor[1];
            flavors[0] = DataFlavor.imageFlavor;

            return flavors;
        }

        @Override public boolean isDataFlavorSupported(DataFlavor flavor)
        {
            DataFlavor[] flavors = getTransferDataFlavors();
            for (DataFlavor f: flavors) {
                if (flavor.equals(f)) {
                    return true;
                }
            }
            return false;
        }
    }
}
