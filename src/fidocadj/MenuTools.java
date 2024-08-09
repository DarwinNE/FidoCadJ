package fidocadj;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.io.*;

import fidocadj.globals.Globals;
import fidocadj.circuit.controllers.SelectionActions;
import fidocadj.circuit.controllers.ElementsEdtActions;
import fidocadj.circuit.controllers.ParserActions;
import fidocadj.circuit.controllers.CopyPasteActions;
import fidocadj.circuit.controllers.EditorActions;
import fidocadj.circuit.ImageAsCanvas;
import fidocadj.circuit.CircuitPanel;
import fidocadj.dialogs.DialogAttachImage;
import fidocadj.dialogs.DialogAbout;
import fidocadj.dialogs.DialogLayer;
import fidocadj.dialogs.EnterCircuitFrame;
import fidocadj.clipboard.TextTransfer;
import fidocadj.geom.ChangeCoordinatesListener;

/** MenuTools.java

    Class creating and handling the main menu of FidoCadJ. It contains the
    methods to create the menu, as well as an event handling system for
    menu-related operatixons.

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
    @see<a href=http://www.gnu.org/licenses/>http://www.gnu.org/licenses/</a>.

    Copyright 2015-2023 by Davide Bucci
    </pre>

    @author Davide Bucci
*/

public class MenuTools implements MenuListener
{
    JCheckBoxMenuItem libs=new JCheckBoxMenuItem();

    /** Create all the menus and associate to them all the needed listeners.
        @param al the action listener to associate to the menu elements.
        @return the menu bar.
    */
    public JMenuBar defineMenuBar(ActionListener al)
    {
        // Menu creation
        JMenuBar menuBar=new JMenuBar();

        menuBar.add(defineFileMenu(al));
        menuBar.add(defineEditMenu(al));
        menuBar.add(defineViewMenu(al));
        menuBar.add(defineCircuitMenu(al));

        // On a MacOSX system, this menu is associated to preferences menu
        // in the application menu. We do not need to show it in bar.
        // This needs the AppleSpecific extensions to be active.

        JMenu about = defineAboutMenu(al);
        if(!Globals.desktopInt.handleAbout) {
            menuBar.add(about);
        }

        return menuBar;
    }

    /** The menuSelected method, useful for the MenuListener interface.
        @param evt the menu event object.
    */
    @Override public void menuSelected(MenuEvent evt)
    {
        // does nothing
    }

    /** The menuDeselected method, useful for the MenuListener interface.
        @param evt the menu event object.
    */
    @Override public void menuDeselected(MenuEvent evt)
    {
            // does nothing
    }

    /** The menuCanceled method, useful for the MenuListener interface.
        @param evt the menu event object.
    */
    @Override public void menuCanceled(MenuEvent evt)
    {
        // does nothing
    }

    /** Create the main File menu.
        @param al the action listener to associate to the menu.
        @return the menu.
    */
    public JMenu defineFileMenu(ActionListener al)
    {
        JMenu fileMenu=new JMenu(Globals.messages.getString("File"));
        JMenuItem fileNew = new JMenuItem(Globals.messages.getString("New"));
        fileNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
            Globals.shortcutKey));
        JMenuItem fileOpen = new JMenuItem(Globals.messages.getString("Open"));
        fileOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
            Globals.shortcutKey));
        JMenuItem fileSave = new
            JMenuItem(Globals.messages.getString("Save"));
        fileSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
            Globals.shortcutKey));
        JMenuItem fileSaveName = new
            JMenuItem(Globals.messages.getString("SaveName"));
        fileSaveName.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
            Globals.shortcutKey | InputEvent.SHIFT_MASK));

        JMenuItem fileSaveNameSplit = new
            JMenuItem(Globals.messages.getString("Save_split"));

        JMenuItem fileExport = new
            JMenuItem(Globals.messages.getString("Export"));

        fileExport.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
            Globals.shortcutKey));


        JMenuItem filePrint = new
            JMenuItem(Globals.messages.getString("Print"));

        filePrint.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
            Globals.shortcutKey));

        JMenuItem fileClose = new
            JMenuItem(Globals.messages.getString("Close"));

        fileClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
            Globals.shortcutKey));

        JMenuItem options = new
            JMenuItem(Globals.messages.getString("Circ_opt"));

        // Add the items in the file menu.

        fileMenu.add(fileNew);
        fileMenu.add(fileOpen);
        fileMenu.add(fileSave);
        fileMenu.add(fileSaveName);
        fileMenu.addSeparator();
        fileMenu.add(fileSaveNameSplit);
        fileMenu.addSeparator();

        fileMenu.add(fileExport);
        fileMenu.add(filePrint);
        fileMenu.addSeparator();

        // On a MacOSX system, options is associated to preferences menu
        // in the application menu. We do not need to show it in File.
        // This needs the AppleSpecific extensions to be active.


        if(!Globals.desktopInt.handlePreferences) {
            fileMenu.add(options);
            fileMenu.addSeparator();
        }
        fileMenu.add(fileClose);

        // Define all the action listeners

        fileNew.addActionListener(al);
        fileOpen.addActionListener(al);
        fileExport.addActionListener(al);
        filePrint.addActionListener(al);
        fileClose.addActionListener(al);

        fileSave.addActionListener(al);
        fileSaveName.addActionListener(al);
        fileSaveNameSplit.addActionListener(al);

        options.addActionListener(al);

        return fileMenu;
    }

    /** Define the Edit main menu.
        @param al the action listener to associate to the menu.
        @return the menu.
    */
    public JMenu defineEditMenu(ActionListener al)
    {
        JMenu editMenu = new JMenu(Globals.messages.getString("Edit_menu"));

        JMenuItem editUndo = new
            JMenuItem(Globals.messages.getString("Undo"));
        editUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
            Globals.shortcutKey));
        //editUndo.setEnabled(false);
        JMenuItem editRedo = new
            JMenuItem(Globals.messages.getString("Redo"));
        editRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
            Globals.shortcutKey | InputEvent.SHIFT_MASK));
        //editRedo.setEnabled(false);
        JMenuItem editCut = new
            JMenuItem(Globals.messages.getString("Cut"));
        editCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
            Globals.shortcutKey));

        JMenuItem editCopy = new
            JMenuItem(Globals.messages.getString("Copy"));
        editCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
            Globals.shortcutKey));

        JMenuItem editCopySplit = new
            JMenuItem(Globals.messages.getString("Copy_split"));
        editCopySplit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M,
            Globals.shortcutKey));

        JMenuItem editCopyImage = new
            JMenuItem(Globals.messages.getString("Copy_as_image"));
        editCopySplit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,
            Globals.shortcutKey));

        JMenuItem editPaste = new
            JMenuItem(Globals.messages.getString("Paste"));
        editPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
            Globals.shortcutKey));
        JMenuItem clipboardCircuit = new
            JMenuItem(Globals.messages.getString("DefineClipboard"));

        JMenuItem editSelectAll = new
            JMenuItem(Globals.messages.getString("SelectAll"));
        editSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
            Globals.shortcutKey));

        JMenuItem editDuplicate = new
            JMenuItem(Globals.messages.getString("Duplicate"));
        editDuplicate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,
            Globals.shortcutKey));

        JMenuItem editRotate = new
            JMenuItem(Globals.messages.getString("Rotate"));
        editRotate.setAccelerator(KeyStroke.getKeyStroke("R"));

        JMenuItem editMirror = new
            JMenuItem(Globals.messages.getString("Mirror_E"));
        editMirror.setAccelerator(KeyStroke.getKeyStroke("S"));

        editUndo.addActionListener(al);
        editRedo.addActionListener(al);
        editCut.addActionListener(al);
        editCopy.addActionListener(al);
        editCopySplit.addActionListener(al);
        editCopyImage.addActionListener(al);
        editPaste.addActionListener(al);
        editSelectAll.addActionListener(al);
        editDuplicate.addActionListener(al);
        editMirror.addActionListener(al);
        editRotate.addActionListener(al);
        clipboardCircuit.addActionListener(al);

        editMenu.add(editUndo);
        editMenu.add(editRedo);
        editMenu.addSeparator();

        editMenu.add(editCut);
        editMenu.add(editCopy);
        editMenu.add(editCopySplit);
        editMenu.add(editCopyImage);
        editMenu.add(editPaste);
        editMenu.add(clipboardCircuit);
        editMenu.add(editDuplicate);

        editMenu.addSeparator();

        editMenu.add(editSelectAll);
        editMenu.addSeparator();
        editMenu.add(editRotate);
        editMenu.add(editMirror);

        return editMenu;
    }

    /** Define the main View menu.
        @param al the action listener to associate to the menu.
        @return the menu.
    */
    public JMenu defineViewMenu(ActionListener al)
    {
        JMenu viewMenu=new JMenu(Globals.messages.getString("View"));
        JMenuItem layerOptions = new
            JMenuItem(Globals.messages.getString("Layer_opt"));

        layerOptions.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,
            Globals.shortcutKey));
        layerOptions.addActionListener(al);
        viewMenu.add(layerOptions);

        JMenuItem attachImage = new
            JMenuItem(Globals.messages.getString("Attach_image_menu"));
        attachImage.addActionListener(al);

        viewMenu.add(attachImage);
        viewMenu.addSeparator();

        libs = new
            JCheckBoxMenuItem(Globals.messages.getString("Libs"));
        viewMenu.add(libs);
        libs.addActionListener(al);
        return viewMenu;
    }

    /** Define the main Circuit menu.
        @param al the action listener to associate to the menu.
        @return the menu.
    */
    public JMenu defineCircuitMenu(ActionListener al)
    {
        JMenu circuitMenu=new JMenu(Globals.messages.getString("Circuit"));
        JMenuItem defineCircuit = new
            JMenuItem(Globals.messages.getString("Define"));

        defineCircuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G,
            Globals.shortcutKey));

        circuitMenu.add(defineCircuit);

        JMenuItem updateLibraries = new
            JMenuItem(Globals.messages.getString("LibraryUpdate"));

        updateLibraries.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U,
            Globals.shortcutKey));

        circuitMenu.add(updateLibraries);
        defineCircuit.addActionListener(al);
        updateLibraries.addActionListener(al);

        return circuitMenu;
    }

    /** Change the state of the show libs toggle menu item.
        @param s the state of the item.
    */
    public void setShowLibsState(boolean s)
    {
        libs.setState(s);
    }

    /** Define the main About menu.
        @param al the action listener to associate to the menu.
        @return the menu.
    */
    public JMenu defineAboutMenu(ActionListener al)
    {
        JMenu about = new JMenu(Globals.messages.getString("About"));
        JMenuItem aboutMenu = new
            JMenuItem(Globals.messages.getString("About_menu"));
        about.add(aboutMenu);

        aboutMenu.addActionListener(al);

        return about;
    }

    /** Process the menu events.
        @param evt the event.
        @param fff the frame in which the menu is present.
        @param coordL the coordinate listener to show messages if needed.
    */
    public void processMenuActions(ActionEvent evt, FidoFrame fff,
        ChangeCoordinatesListener coordL)
    {
        ExportTools et = fff.getExportTools();
        et.setCoordinateListener(coordL);
        PrintTools pt = fff.getPrintTools();
        CircuitPanel cc = fff.cc;
        String arg=evt.getActionCommand();
        EditorActions edt=cc.getEditorActions();
        CopyPasteActions cpa=cc.getCopyPasteActions();
        ElementsEdtActions eea = cc.getContinuosMoveActions();
        SelectionActions sa = cc.getSelectionActions();
        ParserActions pa = cc.getParserActions();

        // Edit the FidoCadJ code of the drawing
        if (arg.equals(Globals.messages.getString("Define"))) {
            EnterCircuitFrame circuitDialog=new EnterCircuitFrame(fff,
                cc.getParserActions().getText(!cc.extStrict).toString());
            circuitDialog.setVisible(true);

            pa.parseString(new StringBuffer(circuitDialog.getStringCircuit()));
            cc.getUndoActions().saveUndoState();
            fff.repaint();
        } else if (arg.equals(Globals.messages.getString("LibraryUpdate"))) {
            // Update libraries
            fff.loadLibraries();
            fff.show();
        } else if (arg.equals(Globals.messages.getString("Circ_opt"))) {
            // Options for the current drawing
            fff.showPrefs();
        } else if (arg.equals(Globals.messages.getString("Layer_opt"))) {
            // Options for the layers
            DialogLayer layerDialog=new DialogLayer(fff,cc.dmp.getLayers());
            layerDialog.setVisible(true);

            // It is important that we force a complete recalculation of
            // all details in the drawing, otherwise the buffered setup
            // will not be responsive to the changes in the layer editing.

            cc.dmp.setChanged(true);
            fff.repaint();
        } else if(arg.equals(Globals.messages.getString("Libs"))) {
            fff.showLibs(!fff.areLibsVisible());
            libs.setState(fff.areLibsVisible());
        } else if (arg.equals(Globals.messages.getString("Print"))) {
            // Print the current drawing
            pt.associateToCircuitPanel(cc);
            pt.printDrawing(fff);
        } else if (arg.equals(Globals.messages.getString("SaveName"))) {
            // Save with name
            fff.getFileTools().saveWithName(false);
        } else if (arg.equals(Globals.messages.getString("Save_split"))) {
            // Save with name, split non standard macros
            fff.getFileTools().saveWithName(true);
        } else if (arg.equals(Globals.messages.getString("Save"))) {
            // Save with the current name (if available)
            fff.getFileTools().save(false);
        } else if (arg.equals(Globals.messages.getString("New"))) {
            // New drawing
            fff.createNewInstance();
        } else if (arg.equals(Globals.messages.getString("Undo"))) {
            // Undo the last action
            cc.getUndoActions().undo();
            fff.repaint();
        } else if (arg.equals(Globals.messages.getString("Redo"))) {
            // Redo the last action
            cc.getUndoActions().redo();
            fff.repaint();
        } else if (arg.equals(Globals.messages.getString("About_menu"))) {
            // Show the about menu
            DialogAbout d=new DialogAbout(fff);
            d.setVisible(true);
        } else if (arg.equals(Globals.messages.getString("Open"))) {
            // Open a file
            OpenFile openf=new OpenFile();
            openf.setParam(fff);
            /*
                The following code would require a thread safe implementation
                of some of the inner classes (such as CircuitModel), which was
                indeed not the case... Now, yes!
            */
            SwingUtilities.invokeLater(openf);
        } else if (arg.equals(Globals.messages.getString("Export"))) {
            // Export the current drawing
            et.launchExport(fff, cc, fff.getFileTools().openFileDirectory);
        } else if (arg.equals(Globals.messages.getString("SelectAll"))) {
            // Select all elements in the current drawing
            sa.setSelectionAll(true);
            // Even if the drawing is not changed, a repaint operation is
            // needed since all selected elements are rendered in green.
            fff.repaint();
        } else if (arg.equals(Globals.messages.getString("Copy"))) {
            // Copy all selected elements in the clipboard
            cpa.copySelected(!cc.extStrict, false);
        } else if (arg.equals(Globals.messages.getString("Copy_split"))) {
            // Copy elements, splitting non standard macros
            cpa.copySelected(!cc.extStrict, true);
        } else if (arg.equals(Globals.messages.getString("Copy_as_image"))) {
            // Display a dialog similar to the Export menu and create an image
            // that is stored in the clipboard, using a bitmap or vector
            //format.
            et.exportAsCopiedImage(fff, cc);
        } else if (arg.equals(Globals.messages.getString("Cut"))) {
            // Cut all the selected elements
            cpa.copySelected(!cc.extStrict, false);
            edt.deleteAllSelected(true);
            fff.repaint();
        } else if (arg.equals(Globals.messages.getString("Mirror_E"))) {
            // Mirror all the selected elements
            if(eea.isEnteringMacro()) {
                eea.mirrorMacro();
            } else {
                edt.mirrorAllSelected();
            }
            fff.repaint();
        } else if (arg.equals(Globals.messages.getString("Rotate"))) {
            // 90 degrees rotation of all selected elements
            if(eea.isEnteringMacro()) {
                eea.rotateMacro();
            } else {
                edt.rotateAllSelected();
            }
            fff.repaint();
        } else if (arg.equals(Globals.messages.getString("Duplicate"))) {
            // Duplicate
            cpa.copySelected(!cc.extStrict, false);
            cpa.paste(cc.getMapCoordinates().getXGridStep(),
                cc.getMapCoordinates().getYGridStep());
            fff.repaint();
        } else if (arg.equals(Globals.messages.getString("DefineClipboard"))) {
            // Paste as a new circuit
            TextTransfer textTransfer = new TextTransfer();
            //FidoFrame popFrame;
            if(cc.getUndoActions().getModified()) {
                fff.createNewInstance();
            }
            pa.parseString(
                new StringBuffer(textTransfer.getClipboardContents()));
            fff.repaint();
        } else if (arg.equals(Globals.messages.getString("Paste"))) {
            // Paste some graphical elements
            cpa.paste(cc.getMapCoordinates().getXGridStep(),
                    cc.getMapCoordinates().getYGridStep());
            fff.repaint();
        } else if (arg.equals(Globals.messages.getString("Close"))) {
            // Close the current window
            if(!fff.getFileTools().checkIfToBeSaved()) {
                return;
            }
            fff.closeThisFrame();
        } else if(arg.equals(Globals.messages.getString("Attach_image_menu"))){
            // Show the attach image dialog.
            ImageAsCanvas ii=fff.cc.getAttachedImage();
            DialogAttachImage di = new DialogAttachImage(fff);
            di.setFilename(ii.getFilename());
            di.setCorner(ii.getCornerX(),ii.getCornerY());
            di.setResolution(ii.getResolution());
            di.setVisible(true);
            if(di.shouldAttach()) {
                try{
                    if(di.getShowImage()) {
                        ii.loadImage(di.getFilename());
                    } else {
                        ii.removeImage();
                    }
                    ii.setResolution(di.getResolution());
                    ii.setCorner(di.getCornerX(),di.getCornerY());
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(fff,
                        Globals.messages.getString("Can_not_attach_image"),
                        "",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }
}