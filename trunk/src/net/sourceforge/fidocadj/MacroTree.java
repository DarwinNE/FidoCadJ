package net.sourceforge.fidocadj;

import javax.swing.*;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.border.*;
import javax.swing.plaf.metal.MetalIconFactory;
import javax.swing.tree.*;
import javax.swing.event.*;

import clipboard.TextTransfer;

import java.net.URL;
import java.io.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.Timer;

import primitives.*;
import circuit.*;
import circuit.controllers.*;
import circuit.model.*;
import export.*;
import geom.*;
import globals.Globals;
import globals.FileUtils;
import globals.LibUtils;
import toolbars.*;
import layers.*;
import undo.*;

import net.sourceforge.fidocadj.macropicker.SearchField;

/* macroTree.java
	
	Show in a tree the macros available in the loaded libraries.

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

	Copyright 2008-2014 by Davide Bucci, phylum2
	
	Some code snippets adapted from lobby.org/java/forums/t19857.html
	(santhosh kumar T - santhosh@in.fiorano.com)
</pre>
*/


@SuppressWarnings("serial")
public class MacroTree extends JPanel
                      implements TreeSelectionListener,
                      			 DocumentListener,
                      			 KeyListener,
                      			 FocusListener,
                      			 MouseListener,
                      			 PopupMenuListener,
                      			 LibraryUndoListener
                      
{
    private CircuitPanel previewPanel;
    private JTree tree;
    private JSplitPane splitPane;
  	private DefaultMutableTreeNode top; 
    private SearchField search;
    private Collection<MacroDesc> library;
    private ChangeSelectionListener selectionListener;
    private UndoActorListener undoActorListener;
    private MacroDesc macro;
    private Map<String, MacroDesc> libMap;
    
    private JMenuItem popRename;
    private JMenuItem popDelete;
    private JMenuItem popRenKey;
    private static final int LEVEL_MACRO = 0;
    private static final int LEVEL_CATEGORY = 1;
    private static final int LEVEL_LIBRARY = 2;
    private static final int LEVEL_ROOT = 3;
    
    private String tlibFName, tcategory;    
    TreePath lpath;

    @SuppressWarnings("unused")
	private static boolean DEBUG = false;
    
    private static boolean enterSearchDownward = false;
    
    private int[] start;
    
    JPopupMenu popup = new JPopupMenu(); // phylum    
	private ActionListener pml;

	/** Constructor: create an empty tree.
	*/
	public MacroTree()
	{
		super(new GridLayout(1,0));
		macro = null; 
		undoActorListener = null;
		selectionListener = null;
        
	}
	
	// is path1 descendant of path2
    public boolean isDescendant(TreePath tpath1, TreePath path2)
    {
        TreePath path1=tpath1;

        int count1 = path1.getPathCount();
        int count2 = path2.getPathCount();
        if(count1<=count2)
            return false;
        while(count1!=count2) {
            path1 = path1.getParentPath();
            count1--;
        }
        return path1.equals(path2);
    }
 	/** Save the expansion state of a tree. It is extremely annoying that a
 		tree is closed when an update operation is done. So we can save its
 		state with this method.
 		@return a string describing the expansion state.
 	*/
    public String getExpansionState(int row)
    {
    	//Thread.dumpStack();
        TreePath rowPath = tree.getPathForRow(row);
        StringBuffer buf = new StringBuffer();
        int rowCount = tree.getRowCount();
        for(int i=row; i<rowCount; ++i){
            TreePath path = tree.getPathForRow(i);
            if(i==row || isDescendant(path, rowPath)){
                if(tree.isExpanded(path))
                    buf.append(","+String.valueOf(i-row));
            }else
                break;
        }
        return buf.toString();
    }
 
 	/** The opposite as getExpansionState
 	*/
    public void restoreExpansionState(int row, String expansionState)
    {
        StringTokenizer stok = new StringTokenizer(expansionState, ",");
        while(stok.hasMoreTokens()){
            int token = row + Integer.parseInt(stok.nextToken());
            tree.expandRow(token);
        }
    }
	
	/**	Create the library tree.
		@param top the top node
	*/
    private void createNodes(DefaultMutableTreeNode top) {
        DefaultMutableTreeNode category = null;
        DefaultMutableTreeNode macroNode = null;
        
        // Make sort that the library symbols are always ordered in the
        // list.
        final java.util.List<MacroDesc> sorted = new ArrayList<MacroDesc>();
        sorted.addAll(library);
        
        // Perform a sort with some complex rules (speed issues there?)
		Collections.sort(sorted, new Comparator<MacroDesc>() {
    		public int compare(MacroDesc g1, MacroDesc g2) {
    			// Make sort that standard libraries must always come first.
    			if(LibUtils.isStdLib(g1)&&!LibUtils.isStdLib(g2))
    				return -1;
    			if(!LibUtils.isStdLib(g1)&&LibUtils.isStdLib(g2))
    				return 1;
    			
    			// Libraries are always compared from the 
    			// library name first.
    			String s1 = g1.library+g1.category+g1.name;
    			String s2 = g2.library+g2.category+g2.name;
        		return s1.compareToIgnoreCase(s2);
    		}
		});
        
        // Now, we iterate through the sorted library.
        Iterator<MacroDesc> it = sorted.iterator();
        
		Map<String, DefaultMutableTreeNode> categories = 
			new HashMap<String, DefaultMutableTreeNode>();
		Map<String, DefaultMutableTreeNode> libraries = 
			new HashMap<String, DefaultMutableTreeNode>();
        DefaultMutableTreeNode library_i = null;

   		while (it.hasNext()) {
       		MacroDesc val = (MacroDesc)it.next();
       		
       		macroNode = new DefaultMutableTreeNode(val);
       		
       		// the "]" character can not be already present in a library name
       		// here, we use it as a separator.
       		
       		String libName=val.filename+"]"+val.library;
       		String catName=val.filename+"]"+val.category+"]"+val.library;
       		
       		// Chech if the current category is already existing.
        	if(categories.get(catName)==null) {
        		// The category is new: a new node must be created.
        		MacroDesc cat=new MacroDesc(val.key+"]cat","toto","",
        			val.category,val.library,val.filename);
        	    cat.level=1;
        	    category = new DefaultMutableTreeNode(cat);
				
				// We see if the library is already existing
        		if(libraries.get(libName)==null) {
        			// If not, we create it
        			MacroDesc lib=new MacroDesc(val.key+"]lib","toto","",
        				cat.category,val.library,val.filename);
        	   	 	lib.level=2;
        			library_i = new DefaultMutableTreeNode(lib);
        			top.add(library_i);
        			if (!"hidden".equals(val.category)) {
        				library_i.add(category);
        			}
        			
        			libraries.put(libName,library_i);
        		} else {
        			if(!"hidden".equals(val.category)) {
        				((DefaultMutableTreeNode)(libraries.get(
        					libName))).add(
        					category);
        			}
        		}
        		category.add(macroNode);
        		categories.put(catName,category);
        	} else {
        		// The category node is already there: we retrieve it and 
        		// we add a leaf for the macro.
        		
        	    if(!"hidden".equals(val.category)) {
        			((DefaultMutableTreeNode)(categories.get(
        				catName))).add(macroNode);
        	    }
        	} 
   		}
    }
	
	public void popupMenuCanceled(PopupMenuEvent e) 
	{
		// Does nothing
	}
	
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) 
	{
		// Does nothing
	}
	
	/** Called just before the popup menu is visible.
		Select if the various menu items are enabled or not.
	*/
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) 
	{
		// Check if it is a standard library (immutable)
		if(LibUtils.isStdLib(macro)) {
			// All the menu items concern some modification, so they must be
			// disabled.
			popRename.setEnabled(false);
    		popDelete.setEnabled(false);
    		popRenKey.setEnabled(false);
		} else if(macro.level==LEVEL_MACRO) {
			// User-modifiable macro
			popRename.setEnabled(true);
    		popDelete.setEnabled(true);
    		popRenKey.setEnabled(true);
		} else {
			// Library or group (no key)
			popRename.setEnabled(true);
    		popDelete.setEnabled(true);
    		popRenKey.setEnabled(false); // Those elements do not have a key
		}
	}
	
	/** Initialize the tree corresponding to the libraries in memory.
		@param lib the library map
		@param layers the layer description
	*/
    public void updateLibraries(Map<String, MacroDesc> lib, 
    	Vector<LayerDesc> layers) 
    {
        libMap=lib;
		library=lib.values();
		
        //Create the nodes.
        
        MacroDesc tt=new MacroDesc("","FidoCadJ","","",
        	"FidoCadJ","FidoCadJ");
        top = new DefaultMutableTreeNode(tt);
        
        createNodes(top);
        createTree(lib, layers);

		// The root node is always expanded.        
        tree.expandPath(new TreePath(top.getPath()));
       	
	}
	
	
	private void createTree(Map<String, MacroDesc> lib, 
    	Vector<LayerDesc> layers)
	{
		//Create a tree that allows one selection at a time.
        tree = new JTree(top);        
        tree.addFocusListener(this);
        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);    
                
        // Phy :) enable the drag&drop
		tree.setDragEnabled(true);
		tree.setDropMode(DropMode.ON_OR_INSERT);
		tree.setTransferHandler(new TransferHandler() {
			/** Called during the drag&drop.
			*/
			public boolean canImport(TransferSupport support) 
			{ 
				if (support == null || support.getDropLocation() == null) 
					return false;
				if(macro==null||macro.category==null)
					return false;
					
				JTree.DropLocation dl = (javax.swing.JTree.DropLocation)
					support.getDropLocation();
				TreePath pt = dl.getPath();
				Object component=pt.getLastPathComponent();
				if (component!=null && component.toString().equalsIgnoreCase(
					macro.category)) 
					return false;
				if (pt.getPathCount()<=2) 
					return false; // is root or lib
				return true;
			}
			public int getSourceActions(JComponent c) 
			{ 
				return MOVE; 
			}
			protected Transferable createTransferable(JComponent c) 
			{ 
				return new NodoDnD(new DefaultMutableTreeNode(macro)); 
			}			
			public boolean importData(TransferHandler.TransferSupport support) 
			{
				if (macro == null) return false;
				Object nodi = null;
				try {
					Transferable t = support.getTransferable();
					nodi = t.getTransferData(new 
						DataFlavor(DataFlavor.javaJVMLocalObjectMimeType));
				} catch (Exception e) {
					e.printStackTrace();	
				}
				JTree.DropLocation dl = (JTree.DropLocation) 
					support.getDropLocation();
				TreePath dest = dl.getPath();
				DefaultMutableTreeNode parent = (DefaultMutableTreeNode) 
					dest.getLastPathComponent();
				JTree tree = (JTree) support.getComponent();
				DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
				model.insertNodeInto((MutableTreeNode) nodi, parent, 0);
				globalUpdate();
				return true;
			}
			class NodoDnD implements Transferable 
			{
				DefaultMutableTreeNode dnd;
				public NodoDnD(
						DefaultMutableTreeNode defaultMutableTreeNode) 
				{
					this.dnd = defaultMutableTreeNode;
				}
				public Object getTransferData(DataFlavor flavor)
						throws UnsupportedFlavorException 
				{					
					return dnd;
				}
				public DataFlavor[] getTransferDataFlavors() 
				{ 
					return new DataFlavor[1];
				}
				public boolean isDataFlavorSupported(DataFlavor flavor) 
				{ 
					return macro != null;
				}
			}
		});
        
        //Listen for when the selection changes.
        tree.addTreeSelectionListener(this);
        
        tree.addMouseListener(this); // phylum
        final Map<String, MacroDesc> libref = lib;
        
        tree.setCellRenderer(new DefaultTreeCellRenderer(){       
        	/** The renderer used to define the icons to be used.
        	*/
        	public Component getTreeCellRendererComponent(JTree tree,
        		      Object value,boolean sel,boolean expanded,boolean leaf,
        		      int row,boolean hasFocus) 
        	{
        		super.getTreeCellRendererComponent(tree, value, sel, 
        	         expanded, leaf, row, hasFocus);
        		
        		// It is a macro?
        	    if (leaf) {
        	    	return this;
        	    } 
        	    DefaultMutableTreeNode dtn = 
        	      	(DefaultMutableTreeNode) value;
        	    
        	    MacroDesc m=null;
        	    if(dtn.getUserObject() instanceof MacroDesc)
        	    	m=(MacroDesc)dtn.getUserObject();
        	    else
        	    	return this;
        		        	
        		// It is a standard library.    
        	    if (LibUtils.isStdLib(m)&&dtn.getDepth()==2) {
        	   		setIcon(MetalIconFactory.getTreeHardDriveIcon());
        	   		return this;
        	   	}
        	   	
        	   	// It is the root.
        	    if (value.toString().equalsIgnoreCase("fidocadj")) {
        	      	setIcon(MetalIconFactory.getTreeComputerIcon());
        	      	return this;
        	    }        	
               		    
        	    if (dtn.getDepth()==2)
        	      	setIcon(MetalIconFactory.getTreeFloppyDriveIcon());   
        		        
        	    return this;
        	}
        });
        
        tree.getModel().addTreeModelListener(new TreeModelListener() {
			
			public void treeStructureChanged(TreeModelEvent e) 
			{
				// Nothing to do
			}
			
			/** Remove a library, a group of macros or a single macro.
			*/
			public void treeNodesRemoved(TreeModelEvent e) 
			{
				// Don't allow to delete elements in the standard libs.
				if(macro==null||LibUtils.isStdLib(macro))
					return;
					
				try {
					// The remove strategy is a little different whether 
					// the level at which we are.
					switch(macro.level) {
						case LEVEL_MACRO:
							libref.remove(macro.key);
							LibUtils.save(libref,
								LibUtils.getLibPath(macro.filename),
								macro.library.trim(), macro.filename);
							LibUtils.saveLibraryState(undoActorListener);
							break;
						case LEVEL_LIBRARY:
							LibUtils.deleteLib(tlibFName);
							LibUtils.saveLibraryState(undoActorListener);
							break;
						case LEVEL_CATEGORY:
							LibUtils.deleteGroup(libref,tlibFName, tcategory);	
							LibUtils.saveLibraryState(undoActorListener);
							break;
						default:
							// This should never happen, but do nothing, just
							// in case.
					}
				} catch (FileNotFoundException F) {
					// Something went wrong, probably because the output
					// directory has not been defined yet.
					JOptionPane.showMessageDialog(null,
    					Globals.messages.getString("DirNotFound"),
    					Globals.messages.getString("Symbolize"),    
    					JOptionPane.ERROR_MESSAGE);
				}
				globalUpdate();		
			}
			
			
			/** Insertion of a new node (end of drag and drop).
			*/
			public void treeNodesInserted(TreeModelEvent e) 
			{
				// macro contains the element to be moved. It should
				// be already created before calling to treeNodeInserted.
				if (macro == null) 
					return; // not enough info to proceed
					
				// Only allows to move macros and not categories.
				if (macro.level!=LEVEL_MACRO)
					return;
				
				// the "old" characteristics are derived from the "macro"
				// element
				String oldLib = macro.library.trim();
				String oldFile = macro.filename.trim();
				String grp = macro.category.trim();
				String mnam = macro.name.trim();
				String oldKey = macro.key;
				
				// Get the destination library (e contains the target node)
				String destLib = e.getPath()[1].toString().trim();
				
				// Get destination group. It is stored in the user object,
				// so we need to retrieve it and cast to a MacroDesc
				MacroDesc destGroup = (MacroDesc)
					((DefaultMutableTreeNode)e.getPath()[2]).getUserObject();
					
				// Once we have the object, we can retrieve the library name 
				// as well as any information needed.
				String newFile = destGroup.filename;
				String destGrp = e.getPath()[2].toString().trim();		

				// If the origin macro does not belong to a standard library, 
				// we eliminate the old macro from the original library.
				boolean isSourceStandard=false;
				if(LibUtils.isStdLib(macro)) {
					isSourceStandard=true;
				}
				
				// Now, we change the category, the library and the file name
				// for the source macro.
				macro.category = destGrp;
				macro.library = destLib;
				macro.filename = newFile;
				
				// Obtain the reduced key by processing the original macro
				// key (remember the format nomefile.reducedkey used for the
				// macro key in the database.
				String reducedKey=macro.key.substring(macro.key.indexOf(".")+1);
				if("".equals(macro.filename)){
					// In fact, this should never happen.
					macro.key=reducedKey;
					System.out.println("Uh, standard libraries changed?");
				} else {
					macro.key = macro.filename+"."+reducedKey;
				}
				if(LibUtils.isStdLib(macro)) {
					globalUpdate();
					return;
				}
				
				// Only remove the old macro if the origin is a non standard
				// library.
				if(!isSourceStandard)
					libref.remove(oldKey);
				
				// Once we have redefined the elements of the macro, we put it
				// in the new library and we save the two modified libraries.
				libref.put(macro.key, macro);
				// update libraries
				try {
					if(!isSourceStandard && !oldFile.equals(newFile)) {	
						LibUtils.save(libref,
							LibUtils.getLibPath(oldFile),
							oldLib, oldFile);
					}
					LibUtils.save(libref,
						LibUtils.getLibPath(newFile),
						destLib, newFile);
					LibUtils.saveLibraryState(undoActorListener);
				} catch (FileNotFoundException F) {
					JOptionPane.showMessageDialog(null,
    					Globals.messages.getString("DirNotFound"),
    					Globals.messages.getString("Symbolize"),    
    					JOptionPane.ERROR_MESSAGE);
				}
				// synch
				//globalUpdate();			
			}
			
			/** Called after a node has been modified. We need to implement
				the modification (renaming of a macro, a group or a whole
				library) in the library file and then actualize the tree.
			*/
			public void treeNodesChanged(TreeModelEvent e) 
			{
				if(macro==null)
					return;
				/*	this is the way to go?
				switch (macro.level) 
				{
        			case LEVEL_LIBRARY: 
        				break;
        			case LEVEL_CATEGORY:
        				break;
        			case LEVEL_ROOT: // isRoot
        				break;
        		}*/
        		
				if ((macro.level==LEVEL_CATEGORY||macro.level==LEVEL_LIBRARY)&& 
					e.getChildren() != null) 
					{
					// Either lib or grp
					String newname = e.getChildren()[0].toString();
					
					if (tcategory == null) { 
					     // It's a library
						// Check if something has changed.
						if (tlibFName.trim().equalsIgnoreCase(newname.trim()))
							return;
							
						/*if(LibUtils.checkLibrary(newname)) {
							JOptionPane.showMessageDialog(null,
    							Globals.messages.getString("InvalidCharLib"),
    							Globals.messages.getString("Rename"),    
    							JOptionPane.ERROR_MESSAGE);
    								
							globalUpdate();
    						return;
    					} */
						// Standard libraries should not be modified.
						if (LibUtils.isStdLib(macro)) 
							return; 	
						
						// Save the library with the new name.
						try {
							LibUtils.renameLib(libref,
								LibUtils.getLibPath(tlibFName),
								tlibFName.trim(), newname.trim());
							LibUtils.saveLibraryState(undoActorListener);
						} catch (FileNotFoundException F) {
							JOptionPane.showMessageDialog(null,
    							Globals.messages.getString("DirNotFound"),
    							Globals.messages.getString("Rename"),    
    							JOptionPane.ERROR_MESSAGE);
						}
					
						globalUpdate();
					} else {	// Standard libraries should not be modified.
						if (LibUtils.isStdLib(macro)) 
							return;
						try {
							LibUtils.renameGroup(libref, tlibFName,
								tcategory, newname);
							LibUtils.saveLibraryState(undoActorListener);
						} catch (FileNotFoundException F) {
							JOptionPane.showMessageDialog(null,
    							Globals.messages.getString("DirNotFound"),
    							Globals.messages.getString("Rename"),    
    							JOptionPane.ERROR_MESSAGE);
						}
						tcategory=newname;
					} 
				} else {
					// It's a group
					// Rename a macro.
					macro.name = e.getChildren()[e.getChildren().length - 1]
							.toString();
							
					libref.remove(macro.key);
					libref.put(macro.key, macro);
					
					try {
						LibUtils.save(libref,
							LibUtils.getLibPath(macro.filename),
							macro.library.trim(), macro.filename);
						LibUtils.saveLibraryState(undoActorListener);
					} catch (FileNotFoundException F) {
						JOptionPane.showMessageDialog(null,
    						Globals.messages.getString("DirNotFound"),
    						Globals.messages.getString("Rename"),    
    						JOptionPane.ERROR_MESSAGE);
					}
					globalUpdate();
				}
				
			}
		});
        
        // The action listener where the menu actions will be handled
        pml = new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{	
				String name = e.getActionCommand();
				tree.setEditable(false);
			
				// Here is where the menu commands are interpreted
				if (name.equalsIgnoreCase(
					Globals.messages.getString("Rename"))) {
					// Renaming macros.
					
					// At first, check if it is a standard element (immutable).
					if (LibUtils.isStdLib(macro)) 
						return;
						
					tree.setEditable(true);  
					// Edit the current element (see treeNodesChanged).
		            tree.startEditingAtPath(tree.getSelectionPath()); 
				} else 	if (name.equalsIgnoreCase(
					Globals.messages.getString("Delete"))) {
					// Delete selected macro
					if (tlibFName == null && macro != null) 
						tlibFName = macro.library;
					// Standard librairies are immutable.
					if (LibUtils.isStdLib(macro)) 
						return;
					
					// Ask for confirmation
					int n = JOptionPane.showConfirmDialog(null,
    					Globals.messages.getString("ChangeKeyWarning"),
    					Globals.messages.getString("Delete"),
   					    JOptionPane.YES_NO_OPTION);
				
					if(n==JOptionPane.NO_OPTION) {
						return;
					}
				
					DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
						tree.getLastSelectedPathComponent();
                    if (node.getParent() != null) {
                        model.removeNodeFromParent(node);
                    }                        
                } else if (name.equalsIgnoreCase(
                	Globals.messages.getString("RenKey"))) {
                	// change the key
					if (macro == null) 
						return;
						
					// Ask for confirmation
					int n = JOptionPane.showConfirmDialog(null,
    					Globals.messages.getString("ChangeKeyWarning"),
    					Globals.messages.getString("RenKey"),
   					    JOptionPane.YES_NO_OPTION);
				
					if(n==JOptionPane.NO_OPTION) {
						return;
					}
					String k = macro.key.substring(macro.key.indexOf(".")+1);
					
					String z = JOptionPane.showInputDialog(
						Globals.messages.getString("Key"), k);
						
					// Check if something has changed or if the user canceled.
					if(z==null || z.equals(k))
						return;
					
					// Check if there is a valid key available. 
					// We can not continue without a key!
            		if (z.length()<1) {
            			JOptionPane.showMessageDialog(null,
    						Globals.messages.getString("InvKey"),
    						Globals.messages.getString("RenKey"),
    						JOptionPane.ERROR_MESSAGE);
            			return; 	
            		} else if(LibUtils.checkKey(libMap,
            				macro.library,
            				macro.library+"."+z.trim())) { 
            			// The key must be unique
            			JOptionPane.showMessageDialog(null,
    						Globals.messages.getString("DupKey"),
    						Globals.messages.getString("RenKey"),    
    						JOptionPane.ERROR_MESSAGE);
            			return; 
            		} else if(z.contains(" ")) {
            			// The key must not contain spaces
            			JOptionPane.showMessageDialog(null,
    						Globals.messages.getString("SpaceKey"),
    						Globals.messages.getString("RenKey"),    
    						JOptionPane.ERROR_MESSAGE);
            			return; 
            		}
					
					libref.remove(macro.key);
					macro.key = macro.key.replace(k, z);
					libref.put(macro.key, macro);
					try {
						LibUtils.save(libref,
							LibUtils.getLibPath(macro.filename),
							macro.library.trim(), macro.filename);
						LibUtils.saveLibraryState(undoActorListener);
					} catch (FileNotFoundException F) {
						JOptionPane.showMessageDialog(null,
    						Globals.messages.getString("DirNotFound"),
    						Globals.messages.getString("Symbolize"),    
    						JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		};
		
        popup.removeAll();              
        popRename = new JMenuItem(Globals.messages.getString("Rename"));
        popup.add(popRename).addActionListener(pml);
        popDelete = new JMenuItem(Globals.messages.getString("Delete"));
        popup.add(popDelete).addActionListener(pml);
        popup.add(new JSeparator());
        popRenKey = new JMenuItem(Globals.messages.getString("RenKey"));
        popup.add(popRenKey).addActionListener(pml);
        tree.setComponentPopupMenu(popup);
        popup.addPopupMenuListener(this);

        start = new int[1];

        //Create the scroll pane and add the tree to it. 
        JScrollPane treeView = new JScrollPane(tree);
        
        // The previewPanel is an instance of CircuitPanel, where we show a
        // preview of the selected macro when the user clicks on the tree.
		previewPanel = new CircuitPanel(false);
		previewPanel.P.setLayers(layers);
		previewPanel.P.setLibrary(lib);
		
		previewPanel.setGridVisibility(false);

		search = new SearchField();
		search.getDocument().addDocumentListener(this);
		search.addKeyListener(this);
		//search.setOpaque(true);
		
		Box topbox = Box.createVerticalBox();
		
		/*  Implementing an intelligent search field (did you ever used 
			Spotlight under MacOSX?) is not trivial. For the moment, we just
			keep everything ready to show the research field, which would be
			implemented in a later moment.
			
			Addendum June 2009: let's try!
			November 2009: this solution seems to be rather effective :-)
			January 2013: indeed: this works very well!!!
		*/
		
		topbox.add(search);
		topbox.add(treeView);
		
		// If the routine has already been called at least once, we need to
		// eliminate the previous splitPane which is memorized in this panel.
        if(splitPane != null) 
        	remove (splitPane);
        
        //Add the scroll panes to a split pane.
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(topbox);
        splitPane.setBottomComponent(previewPanel);

        previewPanel.setMinimumSize(new Dimension(150, 100));
        treeView.setMinimumSize(new Dimension(150, 100));
        previewPanel.setPreferredSize(new Dimension(350, 300));
        treeView.setPreferredSize(new Dimension(350, 600));
        splitPane.setPreferredSize(new Dimension(350, 300));
		splitPane.setResizeWeight(0.9);
		
		//splitPane.putClientProperty("Quaqua.SplitPane.style","bar");
		//splitPane.setDividerSize(10);

        //Add the split pane to this panel.
        add(splitPane);
    }


	/** Force an in-depth reconstruction of the whole tree.
	*/
	public void globalUpdate()
	{
		String s=getExpansionState(0);
		// WARNING: very fragile code!	**************************************
		Container c = (JFrame)Globals.activeWindow;
		((AbstractButton) ((JFrame) c).getJMenuBar().getMenu(3)
			.getSubElements()[0].getSubElements()[1]).doClick();
			
		//System.out.println("just after doclick");
		
		// I wonder why it does not work
		//((FidoFrame)Globals.activeWindow).loadLibraries();
		//((DefaultTreeModel)tree.getModel()).reload();
		
		// also remove macro(s) from current circuit
		CircuitPanel cp = ((FidoFrame) c).CC;
		DrawingModel ps = cp.P;
		ParserActions pa = new ParserActions(ps);
		cp.getParserActions().parseString(pa.getText(true));
		cp.repaint();
		
		restoreExpansionState(0,s);
	}

	/**	Modify the actual selection listener
		@param l the new selection listener
	*/

	public void setSelectionListener(ChangeSelectionListener l)
	{
		selectionListener=l;
	}
	
	/**	Modify the actual UndoActor Listener 
		@param l the new UndoActor listener
	*/

	public void setUndoActorListener(UndoActorListener l)
	{
		undoActorListener=l;
	}
	
	public void focusGained(FocusEvent e)
    {
       	if (selectionListener!=null && macro!=null)
			selectionListener.setSelectionState(ElementsEdtActions.MACRO,
					macro.key);

    }
    
    public void focusLost(FocusEvent e)
    {
    	// Nothing to do 
    }
	
    /** Required by TreeSelectionListener interface. Called when the user
    	clicks on a node of the tree.
    */
    public void valueChanged(TreeSelectionEvent e) 
    {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
            tree.getLastSelectedPathComponent();
        if (node == null) 
        	return;

        Object nodeInfo = node.getUserObject(); 
        
        // Start of the drag&drop operation. Here we save the content of the 
        // source node (i.e. the macro which is being dragged).
        // We do not know yet if the d&d will occour, but we save, just in
        // case.
        macro=(MacroDesc)nodeInfo;
        lpath = tree.getSelectionPath().getParentPath();  
                          
        if (node.isLeaf()) {
        	// Show the preview of the component in the preview area.
        	try {
            	
				previewPanel.setCirc(new StringBuffer(macro.description));
    			MapCoordinates m = 
    				ExportGraphic.calculateZoomToFit(previewPanel.P, 
    				previewPanel.getSize().width*85/100, 
    				previewPanel.getSize().height*85/100, 
    				true);
    			m.setXCenter(m.getXCenter()+10);
    			m.setYCenter(m.getYCenter()+10);
    			
    			previewPanel.setMapCoordinates(m);
				
            	repaint();
            	if (selectionListener!=null)
					selectionListener.setSelectionState(
						ElementsEdtActions.MACRO,
						macro.key);
            } catch (Exception E) {
            	// TODO: this is dangerous, specify the exact exception!
            	// We get an exception if we click on the base node in an empty
            	// library list.
            	// In such cases, it is OK just to ignore the action.
            	// System.out.println("Exception!");
            	// E.printStackTrace();
            }    
        } else {
        
        	// TODO: avoid using obscure variables such as tcategory, tlibFName
        	// In fact, macro should contain everything needed in each moment
        	// of the d&d operation.
        	
        	switch (macro.level) {
        		case LEVEL_LIBRARY: // isLibrary
        			tcategory = null;
        			tlibFName = macro.filename;
        			break;
        		case LEVEL_CATEGORY: // isCategory        			
        			tlibFName = ((MacroDesc)(((DefaultMutableTreeNode)
        				node.getParent()).
        				getUserObject())).filename;
        			tcategory = macro.category;
        			break;
        		case LEVEL_ROOT: // isRoot
        			tlibFName = null;
        			tcategory = null;
        			break;
        		default:
        			tlibFName = null;
        			tcategory = null;
        			break;
        	}
        }
    }
    
    public void insertUpdate(DocumentEvent e)
    {
    	start[0]=0;
    	searchAndSelect(search.getText().trim(), start, true);
    }
    
    public void removeUpdate(DocumentEvent e)
    {
    	start[0]=0;
    	searchAndSelect(search.getText().trim(), start, true);
    }
    
    public void changedUpdate(DocumentEvent e)
    {
    	// Nothing to do
    }
    
    /** Implementation of the KeyListener interface. Here we implement
    	the navigation through the found elements using the arrow keys.
    */
    public void keyPressed(KeyEvent e)
    {
		if(e.getKeyCode()==KeyEvent.VK_DOWN){
			searchAndSelect(search.getText().trim(), start, true);
			e.consume();
		}
		if(e.getKeyCode()==KeyEvent.VK_UP){
			searchAndSelect(search.getText().trim(), start, false);
			e.consume();
		}
		if(e.getKeyCode()==KeyEvent.VK_ENTER){
			boolean res = searchAndSelect(search.getText().trim(), start, 
				enterSearchDownward);
			if(!res) {
				enterSearchDownward = !enterSearchDownward;
				searchAndSelect(search.getText().trim(), start, 
					enterSearchDownward);
			}				
			e.consume();
		}

	}
    
    public void keyReleased(KeyEvent e)
    {
    	// Nothing to do
    }
    
    public void keyTyped(KeyEvent e)
    {
    	// Nothing to do
    }
    
    /** Resets the selection done on the tree. 
    */
    public void resetSelection()
    {
    	tree.clearSelection();	
    }
    
    /** Perform a search in the nodes of the macro tree
    
    	@return Return true if something is found, return false if nothing is
    		found
    
    */
    private boolean searchAndSelect(String what, int[] start, boolean forward)
    {
    	
       	TreePath path=null;
       	
       	if (what.trim().equals(""))
       		return false;
       	
       	if(start[0]==0) {
       		// collapse all rows of the tree and clear the selection
       		int r = tree.getRowCount() - 1;
   			while (r >= 1) {
      			tree.collapseRow(r);
      			--r;
      		}
      		tree.clearSelection();	
      	}
      	
       	// Performs the search.
       	DefaultMutableTreeNode nn=searchNode(what, start, forward);
       	if (nn==null) {
       		// Nothing found. Just exit quietly.
       		return false;
       	}
       	
       	// Something has been found. Expand and select the element.
        path=new TreePath(nn.getPath());
		tree.scrollPathToVisible(path);
		tree.setSelectionPath (path);
		tree.expandPath(path);
		return true;
    }

	/** 
	 * Inspired from: 
	 * http://www.javareference.com/jrexamples/viewexample.jsp?id=99
     * This method takes the node string and 
     * traverses the tree till it finds the node 
     * matching the string. If the match is found  
     * the node is returned else null is returned 
     *  
     * @param nodeStr node string to search for. 
     * @param start an integer array whose first element represent the starting
     *        point of the search in the tree.
     * @return tree node.
     */ 
    public DefaultMutableTreeNode searchNode(String nodeStr, int[] start,
    	boolean forward) 
    { 
        DefaultMutableTreeNode node = null; 
                 
        // Get the enumeration 
        Enumeration en =  top.depthFirstEnumeration(); 
		int i;
		    
        if (forward) { // Search forward
        	// Start the search from the specified node.        
        	for(i=0; i<start[0] && en.hasMoreElements();++i)
                node = (DefaultMutableTreeNode)en.nextElement(); 
        	
        	// iterate through the enumeration 
        	while(en.hasMoreElements()) 
        	{ 
            	// test every node
            	node = (DefaultMutableTreeNode)en.nextElement(); 
            	++i;
            	// match (regex) the string with the user-object of the node 
            	if(node.getUserObject().toString().matches("(?i).*("
            		+nodeStr+").*")) 
            	{ 
            		start[0]=i;
                	// tree node with string found 
                	return node;                          
            	} 
        	} 
        } else {	// Search backward
        	
        	i=0;
        	int oldi=0;
        	DefaultMutableTreeNode oldnode=null;
        	while(en.hasMoreElements()) 
        	{
        		// test every node
            	node = (DefaultMutableTreeNode)en.nextElement(); 
            	
            	++i;
            	// match (regex) the string with the user-object of the node 
            	if(node.getUserObject().toString().matches("(?i).*("
            		+nodeStr+").*")) 
            	{ 
   		
                	if(i>=start[0]) {
                		// tree node with string found 
                		if(oldnode!=null)
	                		start[0]=oldi;

                		return oldnode;
                	}
   	            	oldnode = node;
   	            	oldi=i;

            	} 
            	if(i>=start[0]) {
                	// tree node with string found 
                	return oldnode;
                }
        	}
        }        
        // Node not found
        return null; 
    }

	public void mouseClicked(MouseEvent e) 
	{
		// Nothing to do
	}

	public void mousePressed(MouseEvent e) 
	{		
		if (e.getButton() != e.BUTTON3) 
			return;
		TreePath p = tree.getClosestPathForLocation(e.getX(), e.getY());
		tree.setSelectionPath(p);		
		tree.setComponentPopupMenu(popup);
	}

	public void mouseReleased(MouseEvent e) 
	{
		// Nothing to do	
	}

	public void mouseEntered(MouseEvent e) 
	{
		// Nothing to do
	}

	public void mouseExited(MouseEvent e) 
	{
		// Nothing to do
	} 
	
	/** This is requested by the LibraryUndoListener interface.
		@param s is the name of the temporary directory where the library
		files have been saved.
	*/
	public void undoLibrary(String s)
	{
        try {
        	File sourceDir = new File(s);
        	String d=LibUtils.getLibDir();
        	File destinationDir = new File(d);
        	//System.out.println("undo: copy from "+s+" to "+d);
            FileUtils.copyDirectory(sourceDir, destinationDir);
            globalUpdate();
        } catch (IOException e) {
            System.out.println("Cannot restore library directory contents.");
        }   
	}	
}
