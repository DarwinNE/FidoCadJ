package fidocadj.circuit;

import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import fidocadj.dialogs.DialogSymbolize;
import fidocadj.globals.Globals;
import fidocadj.globals.LibUtils;
import fidocadj.primitives.GraphicPrimitive;
import fidocadj.primitives.PrimitiveComplexCurve;
import fidocadj.primitives.PrimitivePolygon;
import fidocadj.circuit.controllers.ElementsEdtActions;
import fidocadj.circuit.controllers.ParserActions;
import fidocadj.circuit.controllers.CopyPasteActions;
import fidocadj.circuit.controllers.UndoActions;
import fidocadj.circuit.controllers.ContinuosMoveActions;
import fidocadj.circuit.controllers.EditorActions;
import fidocadj.circuit.controllers.SelectionActions;
import fidocadj.clipboard.TextTransfer;

/** Pop up menu for the main editing panel.

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

    Copyright 2007-2025 by Davide Bucci
    </pre>

    @author Davide Bucci
*/
public class PopUpMenu implements ActionListener
{
    // Elements to be included in the popup menu.
    private JMenuItem editProperties;
    private JMenuItem editCut;
    private JMenuItem editCopy;
    private JMenuItem editPaste;
    private JMenuItem editDuplicate;
    private JMenuItem editSelectAll;
    private JMenuItem editMove;
    private JMenuItem editRotate;
    private JMenuItem editMirror;
    private JMenuItem editSymbolize; // phylum
    private JMenuItem editUSymbolize; // phylum
    private JMenuItem editAddNode;
    private JMenuItem editRemoveNode;

    private final CircuitPanel circuitPanel;
    private final SelectionActions selectionActions;
    private final EditorActions editorActions;
    private final ContinuosMoveActions continuosMoveActions;
    private final UndoActions undoActions;
    private final ParserActions parserActions;
    private final CopyPasteActions copyPasteActions;

    private final JPopupMenu popupMenu;

    // We need to save the position where the popup menu appears.
    private int menux;
    private int menuy;

    /** Constructor. Create a PopUpMenu and associates it to the provided
        handler.
        @param p the CircuitPanel.
    */
    public PopUpMenu(CircuitPanel p)
    {
        circuitPanel=p;
        selectionActions=circuitPanel.getSelectionActions();
        editorActions=circuitPanel.getEditorActions();
        continuosMoveActions=circuitPanel.getContinuosMoveActions();
        undoActions=circuitPanel.getUndoActions();
        parserActions=circuitPanel.getParserActions();
        copyPasteActions=circuitPanel.getCopyPasteActions();

        popupMenu = new JPopupMenu();
        definePopupMenu();
    }

    /** Get the x coordinate of the place where the user clicked for the
        popup menu.
        @return the x coordinate.
    */
    public int getMenuX()
    {
        return menux;
    }

    /** Get the y coordinate of the place where the user clicked for the
        popup menu.
        @return the y coordinate.
    */
    public int getMenuY()
    {
        return menuy;
    }

    /** Create the popup menu.
    */
    private void definePopupMenu()
    {
        editProperties = new
            JMenuItem(Globals.messages.getString("Param_opt"));
        editProperties.setIcon(new ImageIcon(
                getClass().getResource("/icons/menu_icons/param.png")));

        editCut = new JMenuItem(Globals.messages.getString("Cut"));
        editCut.setIcon(new ImageIcon(
                getClass().getResource("/icons/menu_icons/cut.png")));

        editCopy = new JMenuItem(Globals.messages.getString("Copy"));
        editCopy.setIcon(new ImageIcon(
                getClass().getResource("/icons/menu_icons/copy.png")));

        editSelectAll = new JMenuItem(Globals.messages.getString("SelectAll"));
        editSelectAll.setIcon(new ImageIcon(
                getClass().getResource("/icons/menu_icons/select_all.png")));

        editPaste = new JMenuItem(Globals.messages.getString("Paste"));
        editPaste.setIcon(new ImageIcon(
                getClass().getResource("/icons/menu_icons/paste.png")));

        editDuplicate = new JMenuItem(Globals.messages.getString("Duplicate"));
        editDuplicate.setIcon(new ImageIcon(
                getClass().getResource("/icons/menu_icons/duplicate.png")));
        
        editMove = new JMenuItem(Globals.messages.getString("Move"));
        editMove.setIcon(new ImageIcon(
                getClass().getResource("/icons/menu_icons/move.png")));

        editRotate = new JMenuItem(Globals.messages.getString("Rotate"));
        editRotate.setIcon(new ImageIcon(
                getClass().getResource("/icons/menu_icons/rotate.png")));

        editMirror = new JMenuItem(Globals.messages.getString("Mirror_E"));
        editMirror.setIcon(new ImageIcon(
                getClass().getResource("/icons/menu_icons/mirror.png")));

        editSymbolize = new JMenuItem(Globals.messages.getString("Symbolize"));
        editSymbolize.setIcon(new ImageIcon(
                getClass().getResource("/icons/menu_icons/symbolize.png")));

        editUSymbolize =
            new JMenuItem(Globals.messages.getString("Unsymbolize"));
        editUSymbolize.setIcon(new ImageIcon(
                getClass().getResource("/icons/menu_icons/split_macro.png")));

        editAddNode = new JMenuItem(Globals.messages.getString("Add_node"));
        editAddNode.setIcon(new ImageIcon(
                getClass().getResource("/icons/menu_icons/add_node.png")));

        editRemoveNode =
            new JMenuItem(Globals.messages.getString("Remove_node"));
        editRemoveNode.setIcon(new ImageIcon(
                getClass().getResource("/icons/menu_icons/remove_node.png")));

        popupMenu.add(editProperties);
        popupMenu.addSeparator();

        popupMenu.add(editCut);
        popupMenu.add(editCopy);
        popupMenu.add(editPaste);
        popupMenu.add(editDuplicate);
        popupMenu.addSeparator();
        popupMenu.add(editSelectAll);

        popupMenu.addSeparator();
        popupMenu.add(editMove);
        popupMenu.add(editRotate);
        popupMenu.add(editMirror);

        popupMenu.add(editAddNode);
        popupMenu.add(editRemoveNode);

        popupMenu.addSeparator();
        popupMenu.add(editSymbolize); // by phylum
        popupMenu.add(editUSymbolize); // phylum

        // Adding the action listener

        editProperties.addActionListener(this);
        editCut.addActionListener(this);
        editCopy.addActionListener(this);
        editSelectAll.addActionListener(this);
        editPaste.addActionListener(this);
        editDuplicate.addActionListener(this);
        editMove.addActionListener(this);
        editRotate.addActionListener(this);
        editMirror.addActionListener(this);
        editAddNode.addActionListener(this);
        editRemoveNode.addActionListener(this);
        editSymbolize.addActionListener(this); // phylum
        editUSymbolize.addActionListener(this); // phylum
    }

    /** Show a popup menu representing the actions that can be done on the
        selected context.
        @param j the panel to which this menu should be associated.
        @param x the x coordinate where the popup menu should be put.
        @param y the y coordinate where the popup menu should be put.
    */
    public void showPopUpMenu(JPanel j, int x, int y)
    {
        menux=x; menuy=y;
        boolean s=false;
        GraphicPrimitive g=selectionActions.getFirstSelectedPrimitive();
        boolean somethingSelected=g!=null;

        // A certain number of menu options are applied to selected
        // primitives. We therefore check wether are there some
        // of them available and in cp case we activate what should
        // be activated in the pop up menu.
        s=somethingSelected;

        editProperties.setEnabled(s);
        editCut.setEnabled(s);
        editCopy.setEnabled(s);
        editMove.setEnabled(s);
        editRotate.setEnabled(s);
        editMirror.setEnabled(s);
        editDuplicate.setEnabled(s);

        editSelectAll.setEnabled(true);

        if(g instanceof PrimitiveComplexCurve ||
            g instanceof PrimitivePolygon)
        {
            s=true;
        } else {
            s=false;
        }

        if (!selectionActions.isUniquePrimitiveSelected()) {
            s=false;
        }

        editAddNode.setEnabled(s);
        editRemoveNode.setEnabled(s);
        editAddNode.setVisible(s);
        editRemoveNode.setVisible(s);

        // We just check if the clipboard is empty. It would be better
        // to see if there is some FidoCadJ code wich might be pasted

        TextTransfer textTransfer = new TextTransfer();

        if("".equals(textTransfer.getClipboardContents())) {
            editPaste.setEnabled(false);
        } else {
            editPaste.setEnabled(true);
        }

        editSymbolize.setEnabled(somethingSelected);

        editUSymbolize.setEnabled(selectionActions.selectionCanBeSplitted());

        popupMenu.show(j, x, y);
    }

    /** Register an action involving the editing
        @param actionString the action name to be associated to this action
        @param key the key to be used. It will be associated either in
            lower case as well as in upper case.
        @param state the wanted state to be used (see definitions INTERFACE).
    */
    private void registerAction(String actionString, char key, final int state)
    {
        // We need to make this indipendent to the case. So we start by
        // registering the action for the upper case
        circuitPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(Character.toUpperCase(key)),
                actionString);
        // And then we repeat the operation for the lower case
        circuitPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(Character.toLowerCase(key)),
                actionString);

        circuitPanel.getActionMap().put(actionString, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent ignored)
            {
                if (actionString.equals("selection") && 
                    continuosMoveActions.isMovingSelected()) {
                    continuosMoveActions.cancelMove();
                    circuitPanel.repaint();
                    return;
                }

                // We now set the new editing state
                circuitPanel.setSelectionState(state,"");
                // If we are entering or modifying a primitive or a macro,
                // we should be sure it disappears when the state changes
                continuosMoveActions.primEdit = null;
                circuitPanel.repaint();
            }
        });
    }

    /** Register a certain number of keyboard actions with an associated
        meaning:
    <pre>
        [A], [space] or [ESC]   Selection
        [L]                     Line
        [T]                     Text
        [B]                     Bézier
        [P]                     Polygon
        [O]                     Complex curve
        [E]                     Ellipse
        [G]                     Rectangle
        [C]                     Connection
        [I]                     PCB track
        [Z]                     PCB pad
        [DEL] or [BACKSPC]      Delete the selected objects
    </pre>
    */
    public final void registerActiveKeys()
    {
        registerAction("selection", 'a', ElementsEdtActions.SELECTION);
        circuitPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,0,false),
                "selection");
        circuitPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0,false),
                "selection");
        registerAction("line", 'l', ElementsEdtActions.LINE);
        registerAction("text", 't', ElementsEdtActions.TEXT);
        registerAction("bezier", 'b', ElementsEdtActions.BEZIER);
        registerAction("polygon", 'p', ElementsEdtActions.POLYGON);
        registerAction("complexcurve", 'o', ElementsEdtActions.COMPLEXCURVE);
        registerAction("ellipse", 'e', ElementsEdtActions.ELLIPSE);
        registerAction("rectangle", 'g', ElementsEdtActions.RECTANGLE);
        registerAction("connection", 'c', ElementsEdtActions.CONNECTION);
        registerAction("pcbline", 'i', ElementsEdtActions.PCB_LINE);
        registerAction("pcbpad", 'z', ElementsEdtActions.PCB_PAD);
        
        final String moveKey = "move_key";

        circuitPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(
                        Character.toUpperCase('m')), moveKey);
        circuitPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(
                        Character.toLowerCase('m')), moveKey);

        circuitPanel.getActionMap().put(moveKey, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent ignored)
            {
                if (selectionActions.getFirstSelectedPrimitive() != null && 
                    !continuosMoveActions.isMovingSelected()) {
                    continuosMoveActions.startMovingSelected(
                            circuitPanel.getMapCoordinates());
                    circuitPanel.repaint();
                }
            }
        });

        final String delete = "delete";

        // Delete key
        circuitPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("DELETE"), delete);

        circuitPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("BACK_SPACE"), delete);

        circuitPanel.getActionMap().put(delete, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent ignored)
            {
                editorActions.deleteAllSelected(true);
                circuitPanel.repaint();
            }
        });

        final String escape = "escape";

        // Escape: clear everything
        /*cp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke("ESCAPE"), escape);*/

        circuitPanel.getActionMap().put(escape, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent ignored)
            {
                // Cancel Move command if in progress
                if(continuosMoveActions.isMovingSelected()) {
                    continuosMoveActions.cancelMove();
                    circuitPanel.repaint();
                    return;
                }

                if(continuosMoveActions.clickNumber>0){
                    // Here we need to clear the variables which are used
                    // during the primitive introduction and editing.
                    // see mouseMoved method for details.
                    continuosMoveActions.successiveMove = false;
                    continuosMoveActions.clickNumber = 0;
                    continuosMoveActions.primEdit = null;
                    circuitPanel.repaint();
                }
            }
        });
        
        final String left = "lleft";
         // left key
        circuitPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,
            InputEvent.ALT_DOWN_MASK,false), left);

        circuitPanel.getActionMap().put(left, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent ignored)
            {
                editorActions.moveAllSelected(-1,0);
                circuitPanel.repaint();
            }
        });
        final String right = "lright";
         // right key
        circuitPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,
            InputEvent.ALT_DOWN_MASK,false), right);

        circuitPanel.getActionMap().put(right, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent ignored)
            {
                editorActions.moveAllSelected(1,0);
                circuitPanel.repaint();
            }
        });

        final String up = "lup";
         // up key
        circuitPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_UP,
            InputEvent.ALT_DOWN_MASK,false), up);

        circuitPanel.getActionMap().put(up, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent ignored)
            {
                editorActions.moveAllSelected(0,-1);
                circuitPanel.repaint();
            }
        });
        final String down = "ldown";
        // down key
        circuitPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,
            InputEvent.ALT_DOWN_MASK,false), down);

        circuitPanel.getActionMap().put(down, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent ignored)
            {
                editorActions.moveAllSelected(0,1);
                circuitPanel.repaint();
            }
        });
    }

    /** The action listener. Recognize menu events and behaves consequently.
        @param evt the MouseEvent to handle
    */
    @Override public void actionPerformed(ActionEvent evt)
    {
        // TODO: Avoid some copy/paste of code

        // Recognize and handle popup menu events
        if(evt.getSource() instanceof JMenuItem) {
            String arg=evt.getActionCommand();

            if (arg.equals(Globals.messages.getString("Param_opt"))) {
                circuitPanel.setPropertiesForPrimitive();
            } else if (arg.equals(Globals.messages.getString("Copy"))) {
                // Copy all selected elements in the clipboard
                copyPasteActions.copySelected(
                        !circuitPanel.getStrictCompatibility(), false);
            } else if (arg.equals(Globals.messages.getString("Cut"))) {
                // Cut elements
                copyPasteActions.copySelected(
                        !circuitPanel.getStrictCompatibility(), false);
                editorActions.deleteAllSelected(true);
                circuitPanel.repaint();
            } else if (arg.equals(Globals.messages.getString("Paste"))) {
                // Paste elements from the clipboard
                copyPasteActions.paste(
                        circuitPanel.getMapCoordinates().getXGridStep(),
                        circuitPanel.getMapCoordinates().getYGridStep());
                circuitPanel.repaint();
            } else if (arg.equals(Globals.messages.getString("Duplicate"))) {
                // Copy all selected elements in the clipboard
                copyPasteActions.copySelected(
                        !circuitPanel.getStrictCompatibility(), false);
                // Paste elements from the clipboard
                copyPasteActions.paste(
                        circuitPanel.getMapCoordinates().getXGridStep(),
                        circuitPanel.getMapCoordinates().getYGridStep());
                circuitPanel.repaint();
            } else if (arg.equals(Globals.messages.getString("SelectAll"))) {
                // Select all in the drawing.
                selectionActions.setSelectionAll(true);
                // Even if the drawing is not changed, a repaint operation is
                // needed since all selected elements are rendered in green.
                circuitPanel.repaint();
            } else if (arg.equals(Globals.messages.getString("Move"))) {
                // Start moving selected elements
                if (selectionActions.getFirstSelectedPrimitive() != null) {
                    continuosMoveActions.startMovingSelected(
                            circuitPanel.getMapCoordinates());
                    circuitPanel.repaint();
                }
            } else if (arg.equals(Globals.messages.getString("Rotate"))) {
                // Rotate the selected element
                if(continuosMoveActions.isEnteringMacro()) {
                    continuosMoveActions.rotateMacro();
                } else {
                    editorActions.rotateAllSelected();
                }
                circuitPanel.repaint();
            } else if(arg.equals(Globals.messages.getString("Mirror_E"))) {
                // Mirror the selected element
                if(continuosMoveActions.isEnteringMacro()) {
                    continuosMoveActions.mirrorMacro();
                } else {
                    editorActions.mirrorAllSelected();
                }
                circuitPanel.repaint();
            } else if (arg.equals(Globals.messages.getString("Symbolize"))) {
                if (selectionActions.getFirstSelectedPrimitive() == null) {
                    return;
                }
                DialogSymbolize s = new DialogSymbolize(circuitPanel,
                    circuitPanel.getDrawingModel());
                s.setModal(true);
                s.setVisible(true);
                try {
                    LibUtils.saveLibraryState(undoActions);
                } catch (IOException e) {
                    System.out.println("Exception: "+e);
                }
                circuitPanel.repaint();
            } else if (arg.equals(Globals.messages.getString("Unsymbolize"))) {
                StringBuffer s = selectionActions.getSelectedString(
                        true, parserActions);
                editorActions.deleteAllSelected(false);
                parserActions.addString(
                        parserActions.splitMacros(s,  true),true);
                undoActions.saveUndoState();
                circuitPanel.repaint();
            } else if(arg.equals(Globals.messages.getString("Remove_node"))) {
                if(selectionActions.getFirstSelectedPrimitive()
                    instanceof PrimitivePolygon)
                {
                    PrimitivePolygon poly = 
                            (PrimitivePolygon)selectionActions
                                    .getFirstSelectedPrimitive();
                    
                    poly.removePoint(circuitPanel.getMapCoordinates()
                            .unmapXnosnap(getMenuX()),
                        circuitPanel.getMapCoordinates()
                                .unmapYnosnap(getMenuY()),
                        1);
                    undoActions.saveUndoState();
                    circuitPanel.repaint();
                } else if(selectionActions.getFirstSelectedPrimitive()
                    instanceof PrimitiveComplexCurve)
                {
                    PrimitiveComplexCurve curve=
                        (PrimitiveComplexCurve)selectionActions
                                .getFirstSelectedPrimitive();
                    curve.removePoint(circuitPanel.getMapCoordinates()
                            .unmapXnosnap(getMenuX()),
                        circuitPanel.getMapCoordinates()
                                .unmapYnosnap(getMenuY()),
                        1);
                    undoActions.saveUndoState();
                    circuitPanel.repaint();
                }
            } else if(arg.equals(Globals.messages.getString("Add_node"))) {
                if(selectionActions.getFirstSelectedPrimitive()
                    instanceof PrimitivePolygon)
                {
                    PrimitivePolygon poly=
                        (PrimitivePolygon)selectionActions
                                .getFirstSelectedPrimitive();
                    poly.addPointClosest(circuitPanel.getMapCoordinates()
                            .unmapXsnap(getMenuX()),
                        circuitPanel.getMapCoordinates()
                                .unmapYsnap(getMenuY()));
                    undoActions.saveUndoState();
                    circuitPanel.repaint();
                } else if(selectionActions.getFirstSelectedPrimitive() 
                        instanceof PrimitiveComplexCurve)
                {
                    PrimitiveComplexCurve poly=
                        (PrimitiveComplexCurve)selectionActions
                                .getFirstSelectedPrimitive();
                    poly.addPointClosest(circuitPanel.getMapCoordinates()
                            .unmapXsnap(getMenuX()),
                        circuitPanel.getMapCoordinates()
                                .unmapYsnap(getMenuY()));
                    undoActions.saveUndoState();
                    circuitPanel.repaint();
                }
            }
        }
    }
}