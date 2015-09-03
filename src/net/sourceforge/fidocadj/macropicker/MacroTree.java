// This file is part of FidoCadJ.
// 
// FidoCadJ is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// FidoCadJ is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with FidoCadJ.  If not, see <http://www.gnu.org/licenses/>.
// 
// Copyright 2014 Kohta Ozaki, Davide Bucci

package net.sourceforge.fidocadj.macropicker;

import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.tree.*;

import net.sourceforge.fidocadj.circuit.CircuitPanel;
import net.sourceforge.fidocadj.circuit.controllers.ElementsEdtActions;
import net.sourceforge.fidocadj.export.ExportGraphic;
import net.sourceforge.fidocadj.geom.DrawingSize;
import net.sourceforge.fidocadj.geom.MapCoordinates;
import net.sourceforge.fidocadj.globals.Globals;
import net.sourceforge.fidocadj.toolbars.ChangeSelectionListener;
import net.sourceforge.fidocadj.librarymodel.LibraryModel;
import net.sourceforge.fidocadj.librarymodel.Library;
import net.sourceforge.fidocadj.librarymodel.Category;
import net.sourceforge.fidocadj.layermodel.LayerModel;
import net.sourceforge.fidocadj.librarymodel.event.LibraryListenerAdapter;
import net.sourceforge.fidocadj.librarymodel.event.LibraryListener;
import net.sourceforge.fidocadj.macropicker.model.MacroTreeModel;
import net.sourceforge.fidocadj.macropicker.model.MacroTreeNode;
import net.sourceforge.fidocadj.primitives.MacroDesc;

/**
* Library view component.<br>
* Features:<BR>
* * Shows macros of libraries as tree and previews.<BR>
* * Notice selected macro to related components.<BR>
* * Provides interfaces of renaming, removing, moving and changing key for
* library.<BR>
* @author Kohta Ozaki
*/
public class MacroTree extends JPanel
{
    /** Indicates library */
    public static final int LIBRARY = 0;
    /** Indicates category */
    public static final int CATEGORY = 1;
    /** Indicates macro */
    public static final int MACRO = 2;

    // View components.
    private ExpandableJTree treeComponent;
    private SearchField searchField;
    private CircuitPanel previewPanel;
    private JScrollPane treeScrollPane;

    // Models.
    private LibraryModel libraryModel;
    private LayerModel layerModel;
    private MacroTreeModel macroTreeModel;

    // A Listener for sending selected macro to CircuitPanel.
    private ChangeSelectionListener selectionListener;

    private ArrayList<ChangeListener> changeListeners;

    private TreePath copyTarget = null;

    private OperationPermission permissionObject;

    /**
    * Constructor.
    * @param libraryModel library model. not null.
    * @param layerModel layer model. not null.
    */
    public MacroTree(LibraryModel libraryModel, LayerModel layerModel)
    {
        this.libraryModel = libraryModel;
        this.layerModel = layerModel;
        initComponents();
    }

    /**
    * Initialize view components and relate models.
    */
    private void initComponents()
    {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        Box topBox = Box.createVerticalBox();

        setLayout(new GridLayout(1, 0));

        createListenerArray();

        createTreeView();
        createPreviewPanel();
        createSearchField();
        createPermissionObject();
        createPopupMenu();
        bindSearchField();
        bindLibraryModel();
        bindPreviewPanel();

        topBox.add(searchField);
        topBox.add(treeScrollPane);
        splitPane.setTopComponent(topBox);
        splitPane.setBottomComponent(previewPanel);
        splitPane.setResizeWeight(0.9);
        add(splitPane);
    }

    private void createListenerArray()
    {
        changeListeners = new ArrayList<ChangeListener>();
    }

    public void addChangeListener(ChangeListener l)
    {
        changeListeners.add(l);
    }

    public void removeChangeListener(ChangeListener l)
    {
        changeListeners.remove(l);
    }

    /**
    * Returns node type of selected.
    * @return int A constant of LIBRARY or CATEGORY or MACRO or -1(other). 
    */
    public int getSelectedType()
    {
        TreePath path = treeComponent.getSelectionPath();
        int type;

        if(path==null) return -1;

        type = macroTreeModel.getNodeType(path);
        switch(type) {
            case MacroTreeModel.LIBRARY:
                return LIBRARY;
            case MacroTreeModel.CATEGORY:
                return CATEGORY;
            case MacroTreeModel.MACRO:
                return MACRO;
            default:
                return -1;
        }
    }

    /**
    * Removes library.
    * @param library Library to remove.
    */
    public void remove(Library library)
    {
        int result;

        if(library==null) {
            return;
        }

        result = JOptionPane.showConfirmDialog(null, 
                Globals.messages.getString("remove_library_confirm")+
                library.getName() + "?",
                Globals.messages.getString("remove_library"),
                JOptionPane.YES_NO_OPTION);
        if(result==JOptionPane.YES_OPTION) {
            try {
                libraryModel.remove(library);
            } catch (LibraryModel.IllegalLibraryAccessException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), 
                Globals.messages.getString("error"),
                JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
    * Removes category.
    * @param category Category to remove.
    */
    public void remove(Category category)
    {
        int result;

        if(category==null) {
            return;
        }

        result=JOptionPane.showConfirmDialog(null, 
                Globals.messages.getString("remove_category_confirm")+
                category.getName() + "?",
                Globals.messages.getString("remove_category"),
                JOptionPane.YES_NO_OPTION);
        if(result==JOptionPane.YES_OPTION) {
            try {
                libraryModel.remove(category);
            } catch (LibraryModel.IllegalLibraryAccessException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), 
                    Globals.messages.getString("error"),
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
    * Remove macro.
    * @param macro MacroDesc to remove.
    */
    public void remove(MacroDesc macro)
    {
        int result;

        if(macro==null) {
            return;
        }

        result=JOptionPane.showConfirmDialog(null, 
                Globals.messages.getString("remove_macro_confirm")+
                macro.name + "?",
                Globals.messages.getString("remove_macro"),
                JOptionPane.YES_NO_OPTION);
        if(result==JOptionPane.YES_OPTION) {
            try {
                libraryModel.remove(macro);
            } catch (LibraryModel.IllegalLibraryAccessException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), 
                    Globals.messages.getString("error"),
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    /**
    * Renames macro.
    * @param macro MacroDesc to rename.
    */
    public void rename(MacroDesc macro)
    {
        String newName;

        if(macro==null) {
            return;
        }

        newName = JOptionPane.showInputDialog(null,
            Globals.messages.getString("new_macro_name"),
            macro.name);
        if(newName==null || newName.equals(macro.name)) {
            return;
        }
        try {
            libraryModel.rename(macro,newName);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), 
                Globals.messages.getString("error"),
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
    * Renames a category.
    * @param category Category to rename.
    */
    public void rename(Category category)
    {
        String newName;

        if(category==null) {
            return;
        }

        newName = JOptionPane.showInputDialog(null,
            Globals.messages.getString("new_category_name"),
            category.getName());
        if(newName==null || newName.equals(category.getName())) {
            return;
        }
        try {
            libraryModel.rename(category,newName);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), 
                Globals.messages.getString("error"),
                JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
    * Renames a library.
    * @param library Library to rename.
    */
    public void rename(Library library)
    {
        String newName;

        if(library==null) {
            return;
        }

        newName = JOptionPane.showInputDialog(null,
            Globals.messages.getString("new_library_name"),
            library.getName());
        if(newName==null || newName.equals(library.getName())) {
            return;
        }
        try {
            libraryModel.rename(library,newName);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), 
                Globals.messages.getString("error"),
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Ask to the user to change the key of the given macro.
    @param macro the macro to be modified. 
    */
    public void changeKey(MacroDesc macro)
    {
        String oldKey;
        String newKey;

        if(macro==null) {
            return;
        }
        
        int n = JOptionPane.showConfirmDialog(null,
            Globals.messages.getString("ChangeKeyWarning"),
            Globals.messages.getString("RenKey"),
            JOptionPane.YES_NO_OPTION);
        
        if(n==JOptionPane.NO_OPTION) {
            return;
        }        
       
        oldKey = LibraryModel.getPlainMacroKey(macro);
        newKey = JOptionPane.showInputDialog(null,
                                             Globals.messages.getString("Key"),
                                             oldKey);
        if(newKey==null || newKey.equals(oldKey)) {
            return;
        }
        try {
            libraryModel.changeKey(macro,newKey);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(),
                 Globals.messages.getString("error"),
                 JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setSelectedNodeToCopyTarget()
    {
        copyTarget = treeComponent.getSelectionPath();
    }
    
    public void pasteIntoSelectedNode()
    {
        if(copyTarget==null){
            return;
        }
        
        int copyTargetType = macroTreeModel.getNodeType(copyTarget);
        int selectedNodeType = getSelectedType();
        
        if(copyTargetType==MacroTreeModel.CATEGORY && 
            selectedNodeType==LIBRARY){
            copyCategoryIntoLibrary();
        } else  if(copyTargetType==MacroTreeModel.MACRO && 
            selectedNodeType==CATEGORY){
            copyMacroIntoCategory();
        }
        
        copyTarget = null;
        updateOperationPermission();
    }
    
    private void copyCategoryIntoLibrary()
    {
        Category targetCategory;
        Library destLibrary;
        
        destLibrary = getSelectedLibrary();
        targetCategory = macroTreeModel.getCategory(copyTarget);
        
        libraryModel.copy(targetCategory,destLibrary);
    }
    
    private void copyMacroIntoCategory()
    {
        MacroDesc targetMacro;
        Category destCategory;
        
        destCategory = getSelectedCategory();
        targetMacro = macroTreeModel.getMacro(copyTarget);
        
        libraryModel.copy(targetMacro,destCategory);
    }
    
    private void createPermissionObject()
    {
        permissionObject = new OperationPermission();
    }

    public OperationPermission getOperationPermission()
    {
        return permissionObject;
    }

    private void updateOperationPermission()
    {
        TreePath selectedPath;
        int selectedType;
        int copyTargetType;
        
        Library lib = getSelectedLibrary();
        Category cat = getSelectedCategory();
        MacroDesc macro = getSelectedMacro();

        selectedPath = treeComponent.getSelectionPath();

        permissionObject.disableAll();
        selectedType = getSelectedType();

        //copy permission
        if(selectedType==CATEGORY || selectedType==MACRO) {
            permissionObject.copyAvailable = true;
        }

        if(!macroTreeModel.isSearchMode()) {
            //paste permission
            if(copyTarget!=null && lib!=null && !lib.isStdLib()){
                copyTargetType = macroTreeModel.getNodeType(copyTarget);
                if(copyTargetType==MacroTreeModel.CATEGORY && 
                                                        selectedType==LIBRARY){
                    permissionObject.pasteAvailable = true;
                } else if (copyTargetType==MacroTreeModel.MACRO &&
                                                       selectedType==CATEGORY){
                    permissionObject.pasteAvailable = true;
                }
            }

            //rename/renkey permission
            if(lib!=null && !lib.isStdLib()) {
                permissionObject.renameAvailable = true;
                permissionObject.removeAvailable = true;
                if(selectedType==MACRO) {
                    permissionObject.renKeyAvailable = true;
                }
            }
        }
    }

    private void createPopupMenu()
    {
        MacroTreePopupMenu popupMenu = new MacroTreePopupMenu(this);
        treeComponent.setComponentPopupMenu(popupMenu);
        addChangeListener(popupMenu);
    }

    /**
    * Relate preview panel and JTree selection model.<br>
    * Relate preview panel and library model.
    */
    private void bindPreviewPanel()
    {
        // Relate with JTree.
        treeComponent.getSelectionModel().
        addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) 
            {
                MacroDesc md;
                md = macroTreeModel.getMacro(e.getPath());
                

                if(md!=null) {
                    previewPanel.setCirc(new StringBuffer(md.description));
                    MapCoordinates m =
                        DrawingSize.calculateZoomToFit(
                            previewPanel.P,
                            previewPanel.getSize().width*85/100, 
                            previewPanel.getSize().height*85/100, true);
                    m.setXCenter(-m.getXCenter()+10);
                    m.setYCenter(-m.getYCenter()+10);

                    previewPanel.setMapCoordinates(m);
                    previewPanel.repaint();
                }
            }
        });

        // Relate with library model.
        LibraryListener l = new LibraryListenerAdapter() {
            public void libraryLoaded() 
            {
                previewPanel.P.setLibrary(libraryModel.getAllMacros());
            }
        };
        libraryModel.addLibraryListener(l);
    }

    /**
     * Sets the listener for selecting macro.
     * @param l the new listener. It should not be null.
     */
    public void setSelectionListener(ChangeSelectionListener l)
    {
        selectionListener=l;
        treeComponent.getSelectionModel().
        addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                MacroDesc md;
                if (selectionListener!=null) {
                    md = macroTreeModel.getMacro(e.getPath());
                    if(md==null){
                        selectionListener.setSelectionState(
                                ElementsEdtActions.SELECTION, "");
                    } else {
                        selectionListener.setSelectionState(
                                ElementsEdtActions.MACRO, md.key);
                    }
                }
            }
        });
    }

    /**
    * Relate tree model and library model.<br>
    */
    private void bindLibraryModel()
    {
        macroTreeModel = new MacroTreeModel(libraryModel);
        treeComponent.setModel((TreeModel)macroTreeModel);
        libraryModel.addLibraryListener(macroTreeModel);
    }

    /**
    * Create JTree with scroll pane.
    */
    private void createTreeView()
    {
        treeComponent = new ExpandableJTree();
        treeComponent.setCellRenderer(new MacroTreeCellRenderer());
        treeComponent.getSelectionModel().setSelectionMode(
            TreeSelectionModel.SINGLE_TREE_SELECTION);
        treeScrollPane = new JScrollPane(treeComponent);
        treeScrollPane.setMinimumSize(new Dimension(150, 100));
        treeScrollPane.setPreferredSize(new Dimension(350, 600));

        treeComponent.getSelectionModel().addTreeSelectionListener(
            new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent e) {
                    updateOperationPermission();
                    for(ChangeListener l:changeListeners) {
                        l.stateChanged(new ChangeEvent(this));
                    }
                }
            });
    }

    /**
    * Create search bar.
    */
    private void createSearchField()
    {
        // I think this must be initialized with localized label string
        // means search.
        // searchField = new SearchField(Globals.messages.getString("Search"));
        searchField = new SearchField();
    }

    /**
    * Create preview panel.
    */
    private void createPreviewPanel()
    {
        previewPanel = new CircuitPanel(false);
        previewPanel.P.setLayers(layerModel.getAllLayers());
        previewPanel.P.setLibrary(libraryModel.getAllMacros());
        previewPanel.setGridVisibility(false);
        previewPanel.setMinimumSize(new Dimension(150, 100));
        previewPanel.setPreferredSize(new Dimension(350, 300));
    }

    /**
    * Relate document model of search bar and tree model.
    */
    private void bindSearchField()
    {
        DocumentListener searchFieldListener = new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                // Nothing to do
            }
            public void removeUpdate(DocumentEvent e) {
                setWord(e);
            }
            public void insertUpdate(DocumentEvent e) {
                setWord(e);
            }
            private void setWord(DocumentEvent e) {
                String word = null;
                Document d = e.getDocument();
                try {
                    word = d.getText(0, d.getLength());
                } catch (BadLocationException ex) {
                    word = "";
                    System.out.println(
                        "[SearchFieldListener] BadLocationException");
                } finally {
                    if("".equals(word)) {
                        treeComponent.collapseOnce();
                    } else {
                        treeComponent.expandOnce();
                    }
                    macroTreeModel.setFilterWord(word);
                }
            }
        };
        searchField.getDocument().addDocumentListener(searchFieldListener);

        searchField.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Nothing to do
            }

            @Override
            public void keyReleased(KeyEvent e) {
                //System.out.println("keyreleased"+System.currentTimeMillis());
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if(e.isShiftDown()) {
                        treeComponent.selectPrevLeaf();
                        //System.out.println("prev");
                    } else {
                        treeComponent.selectNextLeaf();
                    }
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
                // Nothing to do
            }
        });
    }

    public void setLibraryModel(LibraryModel libraryModel)
    {
        this.libraryModel = libraryModel;
        previewPanel.P.setLibrary(libraryModel.getAllMacros());
        bindSearchField();
    }

    public void setLayerModel(LayerModel layerModel)
    {
        this.layerModel = layerModel;
        previewPanel.P.setLayers(layerModel.getAllLayers());
    }

    public MacroDesc getSelectedMacro()
    {
        TreePath path = treeComponent.getSelectionPath();
        if(path==null) return null;
        return macroTreeModel.getMacro(path);
    }

    public Category getSelectedCategory()
    {
        TreePath path = treeComponent.getSelectionPath();
        if(path==null) return null;
        return macroTreeModel.getCategory(path);
    }

    public Library getSelectedLibrary()
    {
        TreePath path = treeComponent.getSelectionPath();
        if(path==null) return null;
        return macroTreeModel.getLibrary(path);
    }

    public class OperationPermission
    {
        private boolean copyAvailable;
        private boolean pasteAvailable;
        private boolean renameAvailable;
        private boolean removeAvailable;
        private boolean renKeyAvailable;


        /**
        * Returns the value of copyAvailable.
        */
        public boolean isCopyAvailable()
        {
            return copyAvailable;
        }

        /**
        * Returns the value of pasteAvailable.
        */

        public boolean isPasteAvailable()
        {
            return pasteAvailable;
        }

        /**
        * Returns the value of renameAvailable.
        */
        public boolean isRenameAvailable()
        {
            return renameAvailable;
        }

        /**
        * Returns the value of removeAvailable.
        */
        public boolean isRemoveAvailable()
        {
            return removeAvailable;
        }

        /**
        * Returns the value of renKeyAvailable.
        */
        public boolean isRenKeyAvailable()
        {
            return renKeyAvailable;
        }

        public void disableAll()
        {
            copyAvailable=false;
            pasteAvailable=false;
            renameAvailable=false;
            removeAvailable=false;
            renKeyAvailable=false;
        }
    }

    private class MacroTreeCellRenderer extends DefaultTreeCellRenderer
    {
        @Override
        public Component getTreeCellRendererComponent(JTree tree,
                Object value,
                boolean sel,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus)
        {
            Component c = super.getTreeCellRendererComponent(
                              tree, value, sel,
                              expanded, leaf, row,
                              hasFocus);
            if(value instanceof MacroTreeNode) {
                Icon icon = ((MacroTreeNode)value).getIcon();

                if(icon == null) {
                    return c;
                } else {
                    setIcon(icon);
                }
            }
            
            return this;
        }
    }
}
