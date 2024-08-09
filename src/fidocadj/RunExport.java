package fidocadj;

import javax.swing.*;

import java.io.*;

import fidocadj.circuit.model.DrawingModel;
import fidocadj.export.ExportGraphic;
import fidocadj.globals.Globals;
import fidocadj.geom.ChangeCoordinatesListener;


/** The RunExport class implements a runnable class which can be employed
    to perform all exporting operations in a separate thread from the main
    user interface one.

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

    Copyright 2012-2023 by Davide Bucci
    </pre>

    @author Davide Bucci
*/

class RunExport implements Runnable
{
    private File file;
    private DrawingModel dmp;
    private String format;
    private double unitPerPixel;
    private boolean antiAlias;
    private boolean blackWhite;
    private boolean ext;
    private boolean resBased;
    private boolean splitLayers;
    private int xsize;
    private int ysize;
    private JFrame parent;
    private ChangeCoordinatesListener coordL;

    /** Setting up the parameters needed for the export
    @param tfile the file name
    @param tP the DrawingModel object containing the drawing to be exported
    @param tformat the file format to be used
    @param tunitPerPixel the magnification factor to be used for the export
        (used only if resb is true).
    @param tantiAlias the application of anti alias for bitmap export
    @param tblackWhite black and white export.
    @param text export advanced FidoCadJ code (if applicable).
    @param resb if true, the export is based on the resolution.
    @param xs the x size of the drawing (used only if resb is false).
    @param ys the y size of the drawing (used only if resb is false).
    @param splitL if true split layers in different files.
    @param text the extensions to be activated or not

    */
    public void setParam(File tfile,
        DrawingModel tP,
        String tformat,
        double tunitPerPixel,
        boolean tantiAlias,
        boolean tblackWhite,
        boolean text,
        boolean resb,
        int xs,
        int ys,
        boolean splitL,
        JFrame tparent)
    {
        file=tfile;
        dmp = tP;
        format = tformat;
        unitPerPixel = tunitPerPixel;
        antiAlias= tantiAlias;
        blackWhite=tblackWhite;
        ext=text;
        xsize=xs;
        ysize=ys;
        resBased=resb;
        parent=tparent;
        splitLayers=splitL;
    }

    /** Set the coordinate listener which is employed here for showing
        message in a non-invasive way.
        @param c the listener.
    */
    public void setCoordinateListener(ChangeCoordinatesListener c)
    {
        coordL=c;
    }

    /** Launch the export (in a new thread).
    */
    @Override public void run()
    {
        try {
            if(resBased) {
                ExportGraphic.export(file, dmp, format, unitPerPixel,
                    antiAlias, blackWhite, ext, true, splitLayers);
            } else {
                ExportGraphic.exportSize(file, dmp, format, xsize, ysize,
                    antiAlias, blackWhite, ext, true, splitLayers);
            }
            // It turns out (Issue #117) that this dialog is too disruptive.
            // If we can, we opt for a much less invasive message
            if(coordL==null) {
                // Needed for thread safety!
                SwingUtilities.invokeLater(new Runnable() {
                    @Override public void run()
                    {
                        JOptionPane.showMessageDialog(parent,
                            Globals.messages.getString("Export_completed"));
                    }
                });
            } else {
                // Needed for thread safety!
                // Much les disruptive version of the message.
                SwingUtilities.invokeLater(new Runnable() {
                    @Override public void run()
                    {
                        coordL.changeInfos(
                            Globals.messages.getString("Export_completed"));
                    }
                });
            }
        }  catch(final IOException ioe) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override public void run()
                {
                    JOptionPane.showMessageDialog(parent,
                        Globals.messages.getString("Export_error")+ioe);
                }
            });
        } catch(IllegalArgumentException iae) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override public void run()
                {
                    JOptionPane.showMessageDialog(parent,
                        Globals.messages.getString("Illegal_filename"));
                }
            });
        } catch(OutOfMemoryError|NegativeArraySizeException om) {
            // It is not entirely clear to me (DB) why a negative array size
            // exception occours when there are memory issues creating the
            // images.
            SwingUtilities.invokeLater(new Runnable() {
                @Override public void run()
                {
                    JOptionPane.showMessageDialog(parent,
                        Globals.messages.getString("Eport_Memory_Error"));
                }
            });
        }
    }
}