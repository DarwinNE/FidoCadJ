import javax.swing.*;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.border.*;
import javax.swing.plaf.metal.MetalIconFactory;
import javax.swing.tree.*;
import javax.swing.event.*;

import clipboard.TextTransfer;

import java.net.URL;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import export.*;
import geom.*;
import globals.Globals;
import globals.phylum_LibUtils;
import toolbars.*;
import layers.*;

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

	Copyright 2008-2013 by Davide Bucci, phylum2
</pre>
*/


@SuppressWarnings("serial")
public class MacroTree extends JPanel
                      implements TreeSelectionListener,
                      			 DocumentListener,
                      			 KeyListener,
                      			 FocusListener,
                      			 MouseListener,
                      			 PopupMenuListener
                      
{
    private CircuitPanel previewPanel;
    private JTree tree;
    private JSplitPane splitPane;
  	private DefaultMutableTreeNode top; 
    private SearchField search;
    private Collection<MacroDesc> library;
    private ChangeSelectionListener selectionListener;
    private MacroDesc macro;
    private Map<String, MacroDesc> libMap;
    
    private JMenuItem popRename;
    private JMenuItem popDelete;
    private JMenuItem popRenKey;
    private boolean isLeaf;
    
    private String tlibFName, tcategory;    
    //private MacroDesc macro;
    TreePath lpath;

    @SuppressWarnings("unused")
	private static boolean DEBUG = false;
    
    private int[] start;
    
    JPopupMenu popup = new JPopupMenu(); // phylum    
	private ActionListener pml;
	
	  public void expand()
	  {		
		    //System.out.println("Not yet implemented");
		    System.out.println(lpath);
	  }


	public MacroTree()
	{
		super(new GridLayout(1,0));
		macro = null;
	}
	
	/**	Create the library tree.
		@param top the top node
	*/
    private void createNodes(DefaultMutableTreeNode top) {
        DefaultMutableTreeNode category = null;
        DefaultMutableTreeNode macro = null;

        Iterator<MacroDesc> it = library.iterator();
        
		Map<String, DefaultMutableTreeNode> categories = 
			new HashMap<String, DefaultMutableTreeNode>();
		Map<String, DefaultMutableTreeNode> libraries = 
			new HashMap<String, DefaultMutableTreeNode>();
        DefaultMutableTreeNode library_i = null;

   		while (it.hasNext()) {
       		MacroDesc val = (MacroDesc)it.next();
       		
       		macro = new DefaultMutableTreeNode(val);
       		
       		// the "]" character can not be already present in a library name
       		// here, we use it as a separator.
       		
       		String libName=val.filename+"]"+val.library;
       		String catName=val.filename+"]"+val.category+"]"+val.library;
       		
       		// Chech if the current category is already existing.
        	if(categories.get(catName)!=null) {
        		// The category node is already there: we retrieve it and 
        		// we add a leaf for the macro.
        		
        	    if(!val.category.equals("hidden")) {
        			((DefaultMutableTreeNode)(categories.get(
        				catName))).add(macro);
        	    }
        	} else {
        		// The category is new: a new node must be created.
        		MacroDesc cat=new MacroDesc(val.key+"]cat","toto","",
        			val.category,val.library,val.filename);
        	    cat.level=1;
        	    category = new DefaultMutableTreeNode(cat);
				
				// We see if the library is already existing
        		if(libraries.get(libName)!=null) {
        			if(!val.category.equals("hidden")) {
        				((DefaultMutableTreeNode)(libraries.get(
        					libName))).add(
        					category);
        			}
        		} else {
        			// If not, we create it
        			MacroDesc lib=new MacroDesc(val.key+"]lib","toto","",
        				cat.category,val.library,val.filename);
        	   	 	lib.level=2;
        			library_i = new DefaultMutableTreeNode(lib);
        			top.add(library_i);
        			if (!val.category.equals("hidden")) {
        				library_i.add(category);
        			}
        			
        			libraries.put(libName,library_i);
        		}
        		category.add(macro);
        		categories.put(catName,category);
        	}
   		}
    }
	
	public void popupMenuCanceled(PopupMenuEvent e) 
	{
	}
	
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) 
	{
	}
	
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) 
	{
		// Check if it is a standard library (immutable)
		if(phylum_LibUtils.isStdLib(macro)) {
			// All the menu items concern some modification, so they must be
			// disabled.
			popRename.setEnabled(false);
    		popDelete.setEnabled(false);
    		popRenKey.setEnabled(false);
		} else if(!isLeaf) {
			// Library or group
			popRename.setEnabled(true);
    		popDelete.setEnabled(true);
    		popRenKey.setEnabled(false); // This element does not have a key
		} else {
			// User-modifiable library
			popRename.setEnabled(true);
    		popDelete.setEnabled(true);
    		popRenKey.setEnabled(true);
		}
	}
	
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

        //Create a tree that allows one selection at a time.
        tree = new JTree(top);        
        tree.addFocusListener(this);
        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);       
        
        // Phy :)
		tree.setDragEnabled(true);
		tree.setDropMode(DropMode.ON_OR_INSERT);
		tree.setTransferHandler(new TransferHandler() {				
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
				if (pt.getPathCount()<=2 || pt.getPathCount()==4) 
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
				} catch (Exception e) {}
				JTree.DropLocation dl = (JTree.DropLocation) 
					support.getDropLocation();
				TreePath dest = dl.getPath();
				DefaultMutableTreeNode parent = (DefaultMutableTreeNode) 
					dest.getLastPathComponent();
				JTree tree = (JTree) support.getComponent();
				DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
				model.insertNodeInto((MutableTreeNode) nodi, parent, 0);
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
        	    	isLeaf=true;
        	    	return this;
        	    } else {
					isLeaf=false;
				}
        	    DefaultMutableTreeNode dtn = 
        	      	(DefaultMutableTreeNode) value;
        	      	
        	    MacroDesc m=(MacroDesc)dtn.getUserObject();
        		        	
        		// It is a standard library.    
        	    if (phylum_LibUtils.isStdLib(m)&&dtn.getDepth()==2) {
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
				// TODO Auto-generated method stub
				
			}
			
			/** Remove a library, a group of macros or a single macro.
			*/
			public void treeNodesRemoved(TreeModelEvent e) 
			{		
				if(macro==null)
					return;
				try {
					if (macro.level>0) {
						// Either lib or grp					
						if (macro.level==2 &&  
							!phylum_LibUtils.isStdLib(macro)) {
							// it's a lib
							phylum_LibUtils.deleteLib(tlibFName);
						} else 	if (macro.level==1) {
							// it's a category
							phylum_LibUtils.deleteGroup(libref,tlibFName,
								tcategory);	
						}
					} else {
						// It is a macro.
						libref.remove(macro.key);
						phylum_LibUtils.save(libref,
							phylum_LibUtils.getLibPath(macro.filename),
							macro.library.trim(), macro.filename);
					}
				} catch (FileNotFoundException F) {
					JOptionPane.showMessageDialog(null,
    					Globals.messages.getString("DirNotFound"),
    					Globals.messages.getString("Symbolize"),    
    					JOptionPane.ERROR_MESSAGE);
				}
				globalUpdate();		
				expand();
			}
			
			
			/** Insertion of a new node (drag and drop?).
			*/
			public void treeNodesInserted(TreeModelEvent e) 
			{ 				
				// macro will contain the element to be inserted. It should
				// be already created before calling to treeNodeInserted.
				if (macro == null) 
					return; // not enough info to proceed
					
				String lib = macro.library.trim();
				String file = macro.filename.trim();
				String grp = macro.category.trim();
				String destLib = e.getPath()[1].toString().trim();
				String destGrp = e.getPath()[2].toString().trim();
				String mnam = macro.name.trim();
				System.out.printf("\nMoving %s from %s::%s to %s::%s", mnam, lib, grp, destLib, destGrp);
				
				//libref.remove(macro);				
				macro.category = destGrp;
				macro.library = destLib;
				//libref.put(macro.key, macro);
				
				// update libraries
				try {
					phylum_LibUtils.save(libref,
						phylum_LibUtils.getLibPath(macro.filename),
						lib, macro.filename);
					phylum_LibUtils.save(libref,
						phylum_LibUtils.getLibPath(destLib),
						destLib, macro.filename);	// TODO: correct that, it is awful!!!!!!!!!
				} catch (FileNotFoundException F) {
					JOptionPane.showMessageDialog(null,
    					Globals.messages.getString("DirNotFound"),
    					Globals.messages.getString("Symbolize"),    
    					JOptionPane.ERROR_MESSAGE);
				}
				// synch
				globalUpdate();
				
			}
			
			/** Called after a node has been modified. We need to implement
				the modification (renaming of a macro, a group or a whole
				library) in the library file and then actualize the tree.
			*/
			public void treeNodesChanged(TreeModelEvent e) 
			{
				if (macro == null && e.getChildren() != null) {
					// Either lib or grp
					String newname = (e.getChildren()[0]).toString();
					
					if (tcategory != null) { // renaming group
						try {
							phylum_LibUtils
								.renameGroup(libref, tlibFName, tcategory, newname);
						} catch (FileNotFoundException F) {
							JOptionPane.showMessageDialog(null,
    							Globals.messages.getString("DirNotFound"),
    							Globals.messages.getString("Rename"),    
    							JOptionPane.ERROR_MESSAGE);
						}
						tcategory=newname;
					} else { // It's a library
						// Check if something has changed.
						if (tlibFName.trim().equalsIgnoreCase(newname.trim()))
							return;
							
						if(phylum_LibUtils.checkLibrary(newname)) {
							JOptionPane.showMessageDialog(null,
    							Globals.messages.getString("InvalidCharLib"),
    							Globals.messages.getString("Rename"),    
    							JOptionPane.ERROR_MESSAGE);
    								
							globalUpdate();
    						return;
    					}
						// Standard libraries should not be modified.
						if (phylum_LibUtils.isStdLib(macro)) 
							return; 	
						
						// Save the library with the new name.
						/*try {
							phylum_LibUtils.save(libref,
								phylum_LibUtils.getLibPath(tlibFName),
								tlibFName.trim(), newname.trim());
						} catch (FileNotFoundException F) {
							JOptionPane.showMessageDialog(null,
    							Globals.messages.getString("DirNotFound"),
    							Globals.messages.getString("Rename"),    
    							JOptionPane.ERROR_MESSAGE);
						}
						*/
												
						globalUpdate();
					}
				}
				if (macro != null) {
					// Rename a macro.
					macro.name = e.getChildren()[e.getChildren().length - 1]
							.toString();
							
					libref.remove(macro.key);
					libref.put(macro.key, macro);
					//e.getChildren()[0].setUserObject(macro);
					
					try {
						phylum_LibUtils.save(libref,
							phylum_LibUtils.getLibPath(macro.filename),
							macro.library.trim(), macro.filename);
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
					if (phylum_LibUtils.isStdLib(macro)) 
						return;
					
					// Ask for confirmation only if we are trying to change 
					// the name of a library, since it is used in the filename
					// and in the complete key of a symbol.
					// The other cases does not need an explicit confirmation
					// since the modification are not structural.
					if (macro==null && tcategory==null) {
						int n = JOptionPane.showConfirmDialog(null,
    						Globals.messages.getString("ChangeKeyWarning"),
    						Globals.messages.getString("Rename"),
   					    	JOptionPane.YES_NO_OPTION);
				
						if(n==JOptionPane.NO_OPTION) {
							return;
						}
					}
						
					tree.setEditable(true);  
					// Edit the current element (see treeNodesChanged).
		            tree.startEditingAtPath(tree.getSelectionPath()); 
				} else 	if (name.equalsIgnoreCase(
					Globals.messages.getString("Delete"))) {
					// Delete selected macro
					if (tlibFName == null && macro != null) 
						tlibFName = macro.library;
					// Standard librairies are immutable.
					if (phylum_LibUtils.isStdLib(macro)) 
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
            		if (z==null || z.length()<1) {
            			JOptionPane.showMessageDialog(null,
    						Globals.messages.getString("InvKey"),
    						Globals.messages.getString("RenKey"),
    						JOptionPane.ERROR_MESSAGE);
            			return;
            	
            		} else if(phylum_LibUtils.checkKey(libMap,
            				macro.library,
            				macro.library+"."+z.trim())) { 
            			JOptionPane.showMessageDialog(null,
    						Globals.messages.getString("DupKey"),
    						Globals.messages.getString("RenKey"),    
    						JOptionPane.ERROR_MESSAGE);
            			return; 
            		} else if(z.contains(" ")) {
            			JOptionPane.showMessageDialog(null,
    						Globals.messages.getString("SpaceKey"),
    						Globals.messages.getString("RenKey"),    
    						JOptionPane.ERROR_MESSAGE);
            			return; 
            		}
					
					macro.key = macro.key.replace(k, z);					
					libref.remove(macro.key);
					libref.put(macro.key, macro);
					try {
						phylum_LibUtils.save(libref,
							phylum_LibUtils.getLibPath(macro.filename),
							macro.library.trim(), macro.filename);
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
		// WARNING: very fragile code!	**************************************
		Container c = Globals.activeWindow;
		((AbstractButton) ((JFrame) c).getJMenuBar().getMenu(3)
			.getSubElements()[0].getSubElements()[1]).doClick();
			
		// also remove macro(s) from current circuit
		CircuitPanel cp = ((FidoFrame) c).CC;
		ParseSchem ps = cp.P;
		try {
			ps.parseString(ps.getText(true));
			cp.repaint();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**	Modify the actual selection listener
		@param l the new selection listener
	*/

	public void setSelectionListener(ChangeSelectionListener l)
	{
		selectionListener=l;
	}
	
	public void focusGained(FocusEvent e)
    {
       	if (selectionListener!=null && macro!=null)
			selectionListener.setSelectionState(CircuitPanel.MACRO,
					macro.key);

    }
    
    public void focusLost(FocusEvent e)
    {
    }
	
    /** Required by TreeSelectionListener interface. Called when the user
    	clicks on a node of the tree.
    */
    public void valueChanged(TreeSelectionEvent e) 
    {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
            tree.getLastSelectedPathComponent();
        if (node == null) return;
		macro=null;
        Object nodeInfo = node.getUserObject(); 
        macro=(MacroDesc)nodeInfo;
        lpath = tree.getSelectionPath().getParentPath();                    
        if (!node.isLeaf())         	
        {
        	switch (macro.level) //node.getDepth()
        	{
        		case 2: // isLibrary
        			tcategory = null;
        			tlibFName = macro.filename;
        			break;
        		case 1: // isCategory        			
        			tlibFName = ((MacroDesc)(((DefaultMutableTreeNode)
        				node.getParent()).
        				getUserObject())).filename;
        			tcategory = macro.category;
        			break;
        		case 3: // isRoot
        			tlibFName = null;
        			tcategory = null;
        			break;
        	}
        }
        
        if (node.isLeaf()) {
        	// Show the preview of the component in the preview area.
        	try {
            	macro = (MacroDesc)nodeInfo;
            	
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
					selectionListener.setSelectionState(CircuitPanel.MACRO,
						macro.key);
            } catch (Exception E) {
            	// We get an exception if we click on the base node in an empty
            	// library list.
            	// In such cases, it is OK just to ignore the action.
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
	}
    
    public void keyReleased(KeyEvent e)
    {
    }
    
    public void keyTyped(KeyEvent e)
    {  	
    }
    
    /** Resets the selection done on the tree. 
    */
    public void resetSelection()
    {
    	tree.clearSelection();	
    }
    
    /** Perform a search in the nodes of the macro tree
    
    */
    private void searchAndSelect(String what, int[] start, boolean forward)
    {
    	
       	TreePath path=null;
       	
       	if (what.trim().equals(""))
       		return;
       	
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
       		return ;
       	}
       	
       	// Something has been found. Expand and select the element.
        path=new TreePath(nn.getPath());
		tree.scrollPathToVisible(path);
		tree.setSelectionPath (path);
		tree.expandPath(path);
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
	}

	public void mousePressed(MouseEvent e) 
	{		
		if (e.getButton() != e.BUTTON3) return;
		TreePath p = tree.getClosestPathForLocation(e.getX(), e.getY());
		tree.setSelectionPath(p);		
		tree.setComponentPopupMenu(popup);
	}

	public void mouseReleased(MouseEvent e) 
	{		
	}

	public void mouseEntered(MouseEvent e) 
	{		
	}

	public void mouseExited(MouseEvent e) 
	{		
	} 
}
