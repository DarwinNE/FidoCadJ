package fidocadj;

import javax.swing.*;

import fidocadj.globals.Globals;

import java.io.*;
import java.awt.*;
import java.util.Locale;

/** OpenFile.java

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

    The OpenFile class allows to open a new file by using threads.

    @author Davide Bucci
*/

class OpenFile implements Runnable
{

    private FidoFrame parent=null;

    /** Set up the parent window object
        @param tparent the FidoFrame parent asking for a file open.
    */
    public void setParam(FidoFrame tparent)
    {
        parent=tparent;
    }

    /** Open a new file, eventually in a new window if the current one
        contains some unsaved elements.
        We pay attention to show the file chooser dialog which appears to be
        the best looking one on each operating system.
    */
    @Override public void run()
    {
        String fin;
        String din;
        if(Globals.useNativeFileDialogs) {
            // File chooser provided by the host system.
            // Vastly better on MacOSX
            FileDialog fd = new FileDialog(parent,
                Globals.messages.getString("Open"));
            fd.setDirectory(parent.getFileTools().openFileDirectory);
            fd.setFilenameFilter(new FilenameFilter(){
                @Override public boolean accept(File dir, String name)
                {
                    return name.toLowerCase(
                        parent.getLocale()).endsWith(".fcd");
                }
            });

            fd.setVisible(true);
            fin=fd.getFile();
            din=fd.getDirectory();
        } else {
            // File chooser provided by Swing.
            // Better on Linux

            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(
                new File(parent.getFileTools().openFileDirectory));
            fc.setDialogTitle(Globals.messages.getString("Open"));
            fc.setFileFilter(new javax.swing.filechooser.FileFilter(){
                @Override public boolean accept(File f)
                {
                    return f.getName().toLowerCase(Locale.US)
                        .endsWith(".fcd")||f.isDirectory();
                }
                @Override public String getDescription()
                {
                    return "FidoCadJ (.fcd)";
                }
            });

            if(fc.showOpenDialog(parent)!=JFileChooser.APPROVE_OPTION) {
                return;
            }

            fin=fc.getSelectedFile().getName();
            din=fc.getSelectedFile().getParentFile().getPath();
        }

        // We now have the directory as well as the file name, so we can
        // open it!
        if(fin!= null) {
            File f=new File(Globals.createCompleteFileName(din, fin));

            // We first check if the file name chosen by the user has a correct
            // file extension, coherent with the file format chosen.
            // In reality, a confirm is asked to the user only if the selected
            // file exists and if it has a non standard extension.
            if(!Globals.checkExtension(fin, Globals.DEFAULT_EXTENSION)) {
                int selection;
                if(f.exists()) {
                    selection = JOptionPane.showConfirmDialog(null,
                        Globals.messages.getString("Warning_extension"),
                        Globals.messages.getString("Warning"),
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                } else {
                    selection=JOptionPane.OK_OPTION;
                }
                // If useful, we correct the extension.
                if(selection==JOptionPane.OK_OPTION) {
                    fin = Globals.adjustExtension(
                        fin, Globals.DEFAULT_EXTENSION);
                }
            }
            try {
                // DOUBT: this might be done not on a new thread, but in the
                // normal Swing one.

                FidoFrame popFrame;
                if(parent.circuitPanel.getUndoActions().getModified() ||
                    !parent.circuitPanel.getDrawingModel().isEmpty())
                {
                    // Here we create a new window in order to display
                    // the file.

                    popFrame=new FidoFrame(parent.runsAsApplication,
                        parent.getLocale());
                    popFrame.init();
                    popFrame.setBounds(parent.getX()+20, parent.getY()+20,
                        popFrame.getWidth(),
                        popFrame.getHeight());
                    popFrame.loadLibraries();
                    popFrame.setVisible(true);
                } else {
                    // Here we do not create the new window and we
                    // reuse the current one to load and display the
                    // file to be loaded
                    popFrame=parent;
                }
                popFrame.circuitPanel.getParserActions().openFileName=
                    Globals.createCompleteFileName(din, fin);
                if (parent.runsAsApplication) {
                    parent.preferences.put("OPEN_DIR", din);
                }

                popFrame.getFileTools().openFileDirectory=din;
                popFrame.getFileTools().openFile();
                popFrame.circuitPanel.getUndoActions().saveUndoState();
                popFrame.circuitPanel.getUndoActions().setModified(false);

            } catch (IOException fnfex) {
                JOptionPane.showMessageDialog(parent,
                    Globals.messages.getString("Open_error")+fnfex);
            }
        }
    }
}