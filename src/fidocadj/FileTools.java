package fidocadj;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.util.Locale;

import fidocadj.circuit.CircuitPanel;
import fidocadj.globals.Globals;
import fidocadj.export.ExportGraphic;
import fidocadj.globals.SettingsManager;

/** FileTools.java
 * Class performing high level user interface operation involving files.
 *
 * <pre>
 * This file is part of FidoCadJ.
 *
 * FidoCadJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FidoCadJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FidoCadJ. If not,
 * @see <a href=http://www.gnu.org/licenses/>http://www.gnu.org/licenses/</a>.
 *
 * Copyright 2015-2023 by Davide Bucci
 * </pre>
 *
 * @author Davide Bucci
 */
public class FileTools
{

    final private FidoFrame fidoFrame;

    // Open/save default properties
    public String openFileDirectory;

    /** Standard constructor.
     *
     * @param f the frame which should be associated to those file operations.
     */
    public FileTools(FidoFrame f)
    {
        fidoFrame = f;
        openFileDirectory = "";
    }

    /** Read the preferences associated to file behaviour (if a preference
     * element is available).
     */
    public void readPrefs()
    {
        openFileDirectory = SettingsManager.get("OPEN_DIR", "");
    }

    /** Ask the user if the current file should be saved and do it if yes.
     *
     * @return true if the window should be closed or false if the closing
     * action has been cancelled.
     */
    public boolean checkIfToBeSaved()
    {
        // If the drawing is empty, there's no need to ask if it should be ..
        // saved before closing the program.
        if (fidoFrame.circuitPanel.getDrawingModel().isEmpty()) {
            return true;
        }

        boolean shouldExit = true;
        if (fidoFrame.circuitPanel.getUndoActions().getModified()) {
            Object[] options = {
                Globals.messages.getString("Save"),
                Globals.messages.getString("Do_Not_Save"),
                Globals.messages.getString("Cancel_btn")};

            // We try to show in the title bar of the dialog the file name of
            // the drawing to which the dialog refers to. If not, we just
            // write Warning!
            String filename = Globals.messages.getString("Warning");
            if (!"".equals(fidoFrame.circuitPanel.getParserActions().openFileName)) {
                filename = fidoFrame.circuitPanel.getParserActions().openFileName;
            }
            int choice = JOptionPane.showOptionDialog(fidoFrame,
                    Globals.messages.getString("Warning_unsaved"),
                    Globals.prettifyPath(filename, 35),
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options, //the titles of buttons
                    options[0]); //default button title)

            // Those constant names does not reflect the actual
            // message shown on the buttons.
            if (choice == JOptionPane.YES_OPTION) {
                //  Save and exit
                //System.out.println("Save and exit.");
                if (!save(false)) {
                    shouldExit = false;
                }
            } else {
                if (choice == JOptionPane.CANCEL_OPTION) {
                    // Don't exit
                    shouldExit = false;
                }
            }
        }

        if (shouldExit) {
            fidoFrame.circuitPanel.getUndoActions().doTheDishes();
        }

        return shouldExit;
    }

    /** Open the current file.
     *
     * @throws IOException if the file can not be opened.
     */
    public void openFile()
            throws IOException
    {

        BufferedReader bufRead = null;
        StringBuffer txt = new StringBuffer();

        try {
            bufRead = new BufferedReader(
                    new InputStreamReader(new FileInputStream(
                            fidoFrame.circuitPanel.getParserActions().openFileName),
                            Globals.encoding));

            String line = bufRead.readLine();
            while (line != null) {
                txt.append(line);
                txt.append("\n");
                line = bufRead.readLine();
            }
        } finally {
            if (bufRead != null) {
                bufRead.close();
            }
        }

        // Here txt contains the new circuit: draw it!
        fidoFrame.circuitPanel.getParserActions().parseString(
                new StringBuffer(txt.toString()));

        // Check for ghost primitives (hidden outside the drawing area)
        if (fidoFrame.circuitPanel.checkGhostPrimitives()) {
            int response = JOptionPane.showConfirmDialog(fidoFrame,
                    Globals.messages.getString("GhostPrimitivesFound") + "\n"
                    + Globals.messages.getString(
                            "GhostPrimitivesTranslatePrompt"),
                    Globals.messages.getString("GhostPrimitivesTitle"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                // Normalize the coordinates if user clicks 'Yes'
                fidoFrame.circuitPanel.normalizeCoordinates();
            }
        }

        // Calculate the zoom to fit
        fidoFrame.zoomToFit();
        fidoFrame.circuitPanel.getUndoActions().saveUndoState();
        fidoFrame.circuitPanel.getUndoActions().setModified(false);

        fidoFrame.repaint();
    }

    /** Show the file dialog and save with a new name name.
     * This routine makes use of the standard dialogs (either the Swing or the
     * native one, depending on the host operating system), in order to let
     * the user choose a new name for the file to be saved.
     *
     * @return true if the save operation has gone well.
     *
     * @param splitNonStandardMacroS decides whether the non standard macros
     * should be split during the save operation.
     */
    public boolean saveWithName(boolean splitNonStandardMacroS)
    {
        String fin;
        String din;

        if (Globals.useNativeFileDialogs) {
            // File chooser provided by the host system.
            // Vastly better on MacOSX, but probably not such on other
            // operating systems.

            FileDialog fd = new FileDialog(fidoFrame,
                    Globals.messages.getString("SaveName"),
                    FileDialog.SAVE);
            fd.setDirectory(openFileDirectory);
            fd.setFilenameFilter(new FilenameFilter()
            {
                @Override
                public boolean accept(File dir, String name)
                {
                    return name.toLowerCase(Locale.US).endsWith(".fcd");
                }
            });
            fd.setVisible(true);
            fin = fd.getFile();
            din = fd.getDirectory();
        } else {
            // File chooser provided by Swing.
            // Better on Linux

            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new javax.swing.filechooser.FileFilter()
            {
                @Override
                public boolean accept(File f)
                {
                    return f.getName().toLowerCase(Locale.US).endsWith(".fcd")
                            || f.isDirectory();
                }

                @Override
                public String getDescription()
                {
                    return "FidoCadJ (.fcd)";
                }
            });

            // Set the current working directory as well as the file name.
            fc.setCurrentDirectory(new File(openFileDirectory));
            fc.setDialogTitle(Globals.messages.getString("SaveName"));
            if (fc.showSaveDialog(fidoFrame) != JFileChooser.APPROVE_OPTION) {
                return false;
            }

            fin = fc.getSelectedFile().getName();
            din = fc.getSelectedFile().getParentFile().getPath();
        }

        if (fin == null) {
            return false;
        } else {
            fidoFrame.circuitPanel.getParserActions().openFileName
                = Globals.createCompleteFileName(din, fin);

            fidoFrame.circuitPanel.getParserActions().openFileName
                = Globals.adjustExtension(fidoFrame.circuitPanel.getParserActions().openFileName,
                    Globals.DEFAULT_EXTENSION);

            SettingsManager.put("OPEN_DIR", din);

            openFileDirectory = din;

            // Here everything is ready for saving the current drawing.
            return save(splitNonStandardMacroS);
        }
    }

    /** Save the current file.
     *
     * @param splitNonStandardMacroS decides whether the non standard macros
     * should be split during the save operation.
     *
     * @return true if the save operation has gone well.
     */
    public boolean save(boolean splitNonStandardMacroS)
    {
        CircuitPanel cc = fidoFrame.circuitPanel;

        // If there is not a name currently defined, we use instead the
        // save with name function.
        if ("".equals(cc.getParserActions().openFileName)) {
            return saveWithName(splitNonStandardMacroS);
        }
        try {
            if (splitNonStandardMacroS) {
                /* In fact, splitting the nonstandard macro when saving a file
                 * is indeed an export operation. This ease the job, since
                 * while exporting in a vector graphic format one has
                 * indeed to split macros.
                 */
                ExportGraphic.export(new File(
                        cc.getParserActions().openFileName),
                        cc.getDrawingModel(),
                        "fcd", 1.0, true, false, !cc.extStrict, false, false);
                cc.getUndoActions().setModified(false);
            } else {
                // Create file
                BufferedWriter output = null;
                try {
                    output = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream(
                                    cc.getParserActions().openFileName),
                            Globals.encoding));

                    output.write("[FIDOCAD]\n");
                    output.write(
                            cc.getParserActions().getText(!cc.extStrict)
                                    .toString());
                } finally {
                    if (output != null) {
                        output.close();
                    }
                }
                cc.getUndoActions().setModified(false);
            }
        } catch (IOException fnfex) {
            JOptionPane.showMessageDialog(fidoFrame,
                    Globals.messages.getString("Save_error") + fnfex);
            return false;
        }
        return true;
    }

    /** Load the given file
     *
     * @param s the name of the file to be loaded.
     */
    public void load(String s)
    {
        fidoFrame.circuitPanel.getParserActions().openFileName = s;
        try {
            openFile();
        } catch (IOException fnfex) {
            JOptionPane.showMessageDialog(fidoFrame,
                    Globals.messages.getString("Open_error") + fnfex);
        }
    }
}
