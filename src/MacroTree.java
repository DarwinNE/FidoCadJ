import javax.swing.*;
import javax.swing.border.*;
import javax.swing.tree.*;
import javax.swing.event.*;

import java.net.URL;
import java.io.IOException;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import primitives.*;
import circuit.*;
import export.*;
import geom.*;
import toolbars.*;

/* macroTree.java v.1.3
	
	Show in a tree the macros available in the loaded libraries.

<pre>
   ****************************************************************************
   Version History 

Version   Date           	Author      Remarks
------------------------------------------------------------------------------
1.0     June 2008			D. Bucci    First working version
1.1		July 2008			D. Bucci	A few bugs corrected
1.2		February 2009		D. Bucci	
1.3		June 2009			D. Bucci 	Capitalize the first letters   
										Search possibilities



   Written by Davide Bucci, June 2009, davbucci at tiscali dot it
   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
</pre>
*/


public class MacroTree extends JPanel
                      implements TreeSelectionListener,
                      			 DocumentListener,
                      			 KeyListener
                      
{
    private CircuitPanel previewPanel;
    private JTree tree;
  	private DefaultMutableTreeNode top; 
    private SearchField search;
    private Collection library;
    private ChangeSelectionListener selectionListener;

    private static boolean DEBUG = false;
    
    private int[] start;


    public MacroTree(Map lib, Vector layers) {
        super(new GridLayout(1,0));
		library=lib.values();
        //Create the nodes.
        top = new DefaultMutableTreeNode("Fidocad");
        createNodes(top);

        //Create a tree that allows one selection at a time.
        tree = new JTree(top);
        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);

        //Listen for when the selection changes.
        tree.addTreeSelectionListener(this);

        start = new int[1];

        //Create the scroll pane and add the tree to it. 
        JScrollPane treeView = new JScrollPane(tree);
        
		previewPanel = new CircuitPanel(false);
		previewPanel.P.setLayers(layers);
		previewPanel.P.setLibrary(lib);
		
		
		previewPanel.setGridVisibility(false);

		search = new SearchField();
		search.getDocument().addDocumentListener(this);
		search.addKeyListener(this);
		
		Box topbox = Box.createVerticalBox();
		
		/*  Implementing an intelligent search field (did you ever used 
			Spotlight under MacOSX?) is not trivial. For the moment, we just
			keep everything ready to show the research field, which would be
			implemented in a later moment.
			
			Addendum June 2009: let's try!
			November 2009: this solution seems to be rather effective :-)
		*/
		
		search.putClientProperty("Quaqua.TextField.style","search");
		topbox.add(search);
		topbox.add(treeView);
		
		
        //Add the scroll panes to a split pane.
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
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

	/**	Modify the actual selection listener
		@param l the new selection listener
		
	*/

	public void setSelectionListener(ChangeSelectionListener l)
	{
		selectionListener=l;
	}
	
    /** Required by TreeSelectionListener interface. Called when the user
    	clicks on a node of the tree.
    
    */
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
            tree.getLastSelectedPathComponent();

        if (node == null) return;

        Object nodeInfo = node.getUserObject();
        if (node.isLeaf()) {
        	try {
            	MacroDesc macro = (MacroDesc)nodeInfo;
            	//System.out.println(macro.description);
            	//previewPanel.P.setMapCoordinates(new MapCoordinates());
				previewPanel.setCirc(new StringBuffer(macro.description));
    			MapCoordinates m = 
    				ExportGraphic.calculateZoomToFit(previewPanel.P, 
    				previewPanel.getSize().width, previewPanel.getSize().height, 
    				true,true);
    			previewPanel.P.setMapCoordinates(m);
				//System.out.println(m);
				
            	repaint();
            	if (selectionListener!=null)
					selectionListener.setSelectionState(CircuitPanel.MACRO,
						macro.key);
            } catch (Exception E) {
            	//System.out.println(E);
            	// We get an exception if we click on the base node in an empty
            	// library list.
            	// In such cases, it is OK just to ignore the action.
            }
            
        }
    }

  
	/**	Create the library tree.
	
		@param top the top node
	*/
    
    private void createNodes(DefaultMutableTreeNode top) {
        DefaultMutableTreeNode category = null;
        DefaultMutableTreeNode macro = null;

        Iterator it = library.iterator();
        
        	
		Map categories = new HashMap();
		Map libraries = new HashMap();
        DefaultMutableTreeNode library_i = null;

   		while (it.hasNext()) {
       		MacroDesc val = (MacroDesc)it.next();
       		
       		macro = new DefaultMutableTreeNode(val);
       		
       		// the "]" caracter can not be already present in a library name
       		// here, we use it as a separator.
       		
        	if(categories.get(val.category+
        	    	"]"+val.library)!=null) {
        	    	
        	    if(!val.category.equals("hidden")) {
        			((DefaultMutableTreeNode)(categories.get(val.category+
        	    		"]"+val.library))).add(macro);
        	    }
        	} else {
        	    category = new DefaultMutableTreeNode(val.category.trim());

        		if(libraries.get(val.library)!=null) {
        			if(!val.category.equals("hidden")) {
        				((DefaultMutableTreeNode)(libraries.get(
        					val.library))).add(
        					category);
        			}
        		} else {
        			library_i = new DefaultMutableTreeNode(val.library.trim());
        			top.add(library_i);
        			if (!val.category.equals("hidden")) {
        				//System.out.println(""+val.category);
        				library_i.add(category);
        			}
        			
        			libraries.put(val.library,library_i);
        		}
        		
        		
        		//library.add(category);
        		
        		
        		
        		category.add(macro);
        		categories.put(val.category+
        	    	"]"+val.library,category);
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
    
    /** Implementation of the KeyListener interface */
    
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
    
    
    private void searchAndSelect(String what, int[] start, boolean forward)
    {
    	// Perform a search in the nodes of the macro tree
       	TreePath path=null;
       	
       	if(start[0]==0) {
       		int r = tree.getRowCount() - 1;
   			while (r >= 1) {
      			tree.collapseRow(r);
      			--r;
      		}
      		tree.clearSelection();
      		
      		
      	}
      	
       	
       	if (what.trim().equals(""))
       		return;
       	
       	DefaultMutableTreeNode nn=searchNode(what, start, forward);
       	if (nn==null) {
       		
       		return ;
       	}
        path=new TreePath(nn.getPath());
		tree.scrollPathToVisible(path);
		tree.setSelectionPath (path);
		tree.expandPath(path);
    }


	/** 
	 * Inspired from: http://www.javareference.com/jrexamples/viewexample.jsp?id=99
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
	
}
