package net.sourceforge.fidocadj;

import java.util.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.*;
import javax.swing.plaf.metal.MetalIconFactory;

import globals.LibUtils;
import primitives.MacroDesc;

public class MacroTreeModel implements TreeModel,LibraryListener
{
    public static final int ROOT=0;
    public static final int LIBRARY=1;
    public static final int CATEGORY=2;
    public static final int MACRO=3;

    private RootNode rootNode;
    private LibraryModel libraryModel;
    private List<TreeModelListener> listeners;

    //private Map<Object,TreePath> pathMap;
    //private Map<Object,AbstractMacroTreeNode> nodeMap;

    private HashMap<TreePath,AbstractMacroTreeNode> libraryNodeMap;
    
    private String filterWord;

    MacroTreeModel(LibraryModel libraryModel)
    {
        this.libraryModel = libraryModel;
        listeners = new ArrayList<TreeModelListener>();
        createMap();
        synchronizeTree(null);
        fireChanged();
    }

    /**
     * Set filtering word.
     * @param filterWord words splitted by space.
     */
    public void setFilterWord(String filterWord)
    {
        this.filterWord = filterWord;

        if(filterWord == null || filterWord.length()==0) {
            synchronizeTree(null);
            this.filterWord = null;
            fireChanged();
        } else {
            final String chainedWord =
                filterWord.toLowerCase(new Locale("en"));
            synchronizeTree(new NodeFilterInterface() {
                public boolean accept(MacroTreeNode node) {
                    String[] words = chainedWord.trim().split(" ");
                    int matched=0;
                    for(String word:words) {
                        if(0<=node.toString().toLowerCase().indexOf(word)) {
                            matched++;
                        } else if(word.length()==0) {
                            matched++;
                        }
                    }
                    return words.length==matched;
                }
            });
            fireChanged();
        }
    }
    
    private void resetSearchMode()
    {
    	filterWord = null;
    }

    public boolean isSearchMode()
    {
        return (filterWord != null);
    }

    private void createMap()
    {
        libraryNodeMap = new HashMap();
    }

    public int getNodeType(TreePath path)
    {
        Object o;
        int type;

        if(path==null) return -1;

        o = path.getLastPathComponent();
        if(o instanceof RootNode) {
            return ROOT;
        } else if(o instanceof LibraryNode) {
            return LIBRARY;
        } else if(o instanceof CategoryNode) {
            return CATEGORY;
        } else if(o instanceof MacroNode) {
            return MACRO;
        }

        return -1;
    }

    /**
     * Return filtering word.
     * @return null or filtering word.
     */
    public String getFilterWord()
    {
        return filterWord;
    }

    /** Implements TreeModel interface */
    public void addTreeModelListener(TreeModelListener l)
    {
        listeners.add(l);
    }

    /** Implements TreeModel interface */
    public Object getChild(Object parent, int index)
    {
        return ((TreeNode)parent).getChildAt(index);
    }

    /** Implements TreeModel interface */
    public int getChildCount(Object parent)
    {
        return ((TreeNode)parent).getChildCount();
    }

    /** Implements TreeModel interface */
    public int getIndexOfChild(Object parent, Object child)
    {
        return ((TreeNode)parent).getIndex((TreeNode)child);
    }

    /** Implements TreeModel interface */
    public Object getRoot()
    {
        return rootNode;
    }

    /** Implements TreeModel interface */
    public boolean isLeaf(Object node)
    {
        return ((TreeNode)node).isLeaf();
    }

    /** Implements TreeModel interface */
    public void removeTreeModelListener(TreeModelListener l)
    {
        listeners.remove(l);
    }

    /** Implements TreeModel interface */
    public void valueForPathChanged(TreePath path, Object newValue)
    {
        // NOP
    }

    public MacroDesc getMacro(TreePath path)
    {
        Object o;

        if(path==null) return null;

        o = getPathComponentAt(path,3);
        if(o instanceof MacroNode) {
            return ((MacroNode)o).getMacro();
        } else {
            return null;
        }
    }

    public Category getCategory(TreePath path)
    {
        Object o;

        if(path==null) return null;

        o = getPathComponentAt(path,2);
        if(o instanceof CategoryNode) {
            return ((CategoryNode)o).getCategory();
        } else {
            return null;
        }
    }

    public Library getLibrary(TreePath path)
    {
        Object o;

        if(path==null) return null;

        o = getPathComponentAt(path,1);
        if(o instanceof LibraryNode) {
            return ((LibraryNode)o).getLibrary();
        } else {
            return null;
        }
    }

    private Object getPathComponentAt(TreePath path,int index)
    {
        if(index <= path.getPathCount()-1) {
            return path.getPathComponent(index);
        } else {
            return null;
        }

    }

    private void fireChanged()
    {
        for(TreeModelListener l:listeners) {
            l.treeStructureChanged(new TreeModelEvent((Object)this,
                                   new TreePath(rootNode),null,null));
        }
    }
    
    private TreePath createAbsolutePath(TreeNode lastNode){
    	TreeNode parentNode = lastNode.getParent();
    	if(parentNode==null){
    		return new TreePath(lastNode);
    	} else {
    		return createAbsolutePath(parentNode).pathByAddingChild(lastNode);
    	}
    }

    public void libraryNodeRenamed(RenameEvent e)
    {
    	Object renamedNode = e.getRenamedNode();
        TreePath renamedPath;
        TreeNode renamedMacroTreeNode;       	

        if(renamedNode==null) {
            fireTreeNodeChanged(new TreePath(rootNode));
        } else {
			for(TreePath path:(Set<TreePath>)libraryNodeMap.keySet()){
				if(path.getLastPathComponent()==renamedNode){
					renamedMacroTreeNode = (TreeNode)libraryNodeMap.get(path);
					renamedPath = createAbsolutePath(renamedMacroTreeNode);
					fireTreeNodeChanged(renamedPath);
				}
			}
        }
    }

    public void libraryNodeRemoved(RemoveEvent e)
    {
        Object parentNode;
        TreePath parentPath;
        TreeNode parentMacroTreeNode;        

        resetSearchMode();
        synchronizeTree(null);
        
        parentNode = e.getParentNode();
        if(parentNode==null) {
            fireTreeStructureChanged(new TreePath(rootNode));
        } else {
			for(TreePath path:(Set<TreePath>)libraryNodeMap.keySet()){
				if(path.getLastPathComponent()==parentNode){
					parentMacroTreeNode = (TreeNode)libraryNodeMap.get(path);
					parentPath = createAbsolutePath(parentMacroTreeNode);
					fireTreeStructureChanged(parentPath);
					System.out.println("deleted in:"+parentPath);
				}
			}
        }
    }

    public void libraryNodeAdded(AddEvent e) {
        Object parentNode;
        TreePath parentPath;
        TreeNode parentMacroTreeNode;
        
        resetSearchMode();
        synchronizeTree(null);
        
        parentNode = e.getParentNode();
        if(parentNode==null) {
            fireTreeStructureChanged(new TreePath(rootNode));
        } else {
			for(TreePath path:(Set<TreePath>)libraryNodeMap.keySet()){
				if(path.getLastPathComponent()==parentNode){
					parentMacroTreeNode = (TreeNode)libraryNodeMap.get(path);
					parentPath = createAbsolutePath(parentMacroTreeNode);
					fireTreeStructureChanged(parentPath);
					System.out.println("added in:"+parentPath);
				}
			}
        }
    }

    public void libraryNodeKeyChanged(KeyChangeEvent e) {
    }

    public void libraryLoaded()
    {
    	resetSearchMode();
        synchronizeTree(null);
        fireChanged();
    }

    private void fireTreeNodeChanged(TreePath path)
    {
        if(path!=null) {
            for(TreeModelListener l:listeners) {
                l.treeNodesChanged(new TreeModelEvent(this, path));
            }
        }
    }

    private void fireTreeNodeRemoved(TreePath path)
    {
        if(path!=null) {
            for(TreeModelListener l:listeners) {
                l.treeNodesRemoved(new TreeModelEvent(this, path));
            }
        }
    }
    
    private void fireTreeStructureChanged(TreePath path)
    {
    	if(path!=null){
    		for(TreeModelListener l:listeners) {
    			l.treeStructureChanged(new TreeModelEvent(this, path));
    		}
    	}
    }
    
    private void synchronizeTree(NodeFilterInterface filter)
    {
    	LibraryNode ln;
        CategoryNode cn;
        MacroNode mn;

        TreePath libraryPath;
        TreePath categoryPath;
        TreePath macroPath;

        HashMap<TreePath,AbstractMacroTreeNode> tmpMap = 
               (HashMap<TreePath,AbstractMacroTreeNode>)libraryNodeMap.clone();
        libraryNodeMap.clear();

        if(rootNode==null){
        	rootNode = new RootNode();
        }
        
        if(filter==null) {
            rootNode.setLabel("FidoCadJ");
        } else {
            rootNode.setLabel("Search result...");
        }

        rootNode.clearChildNodes();
        
        for(Library library:libraryModel.getAllLibraries()) {
        	libraryPath = new TreePath(library);
        	if(libraryNodeMap.containsKey(libraryPath)){
        		ln = (LibraryNode)tmpMap.get(libraryPath);
        		ln.clearChildNodes();
        	} else {
        		ln = new LibraryNode(library);
        	}

            for(Category category:library.getAllCategories()) {
                if(category.isHidden()) {
                    continue;
                }
                categoryPath = libraryPath.pathByAddingChild(category);
                if(tmpMap.containsKey(categoryPath)){
                	cn = (CategoryNode)tmpMap.get(categoryPath);
                	cn.clearChildNodes();
                } else {
                	cn = new CategoryNode(category);
                }
                
                for(MacroDesc macro:category.getAllMacros()) {
                	macroPath = categoryPath.pathByAddingChild(macro);
                	if(tmpMap.containsKey(macroPath)){
                		mn = (MacroNode)tmpMap.get(macroPath);
                	} else {
                		mn = new MacroNode(macro);
                	}

                    if(filter!=null && !filter.accept(mn)) {
                    	continue;
                    }
					cn.addMacroNode(mn);
					libraryNodeMap.put(macroPath,mn);
                }
                
                if(filter!=null && cn.getChildCount()==0) {
                    continue;
                }
                ln.addCategoryNode(cn);
                libraryNodeMap.put(categoryPath,cn);
            }
            if(filter!=null && ln.getChildCount()==0) {
                continue;
            }
            rootNode.addLibraryNode(ln);
            libraryNodeMap.put(libraryPath,ln);
        }

        rootNode.sortTree();
    }

    private class RootNode extends AbstractMacroTreeNode
    {
        RootNode()
        {
            parent = null;
            label = "FidoCadJ";
            icon = MetalIconFactory.getTreeComputerIcon();
        }

        RootNode(String label)
        {
            parent=null;
            this.label=label;
            icon = MetalIconFactory.getTreeComputerIcon();
        }

        RootNode(String label,Icon icon)
        {
            parent=null;
            this.label=label;
            this.icon = icon;
        }

        public void addLibraryNode(LibraryNode node)
        {
            childNodes.add(node);
            node.setParent((TreeNode)this);
        }
        
        public void setLabel(String label)
        {
        	this.label = label;
        }
    }

    private static class LibraryNode extends AbstractMacroTreeNode
        implements Comparable<LibraryNode>
    {
        private Library library;

        LibraryNode(Library library)
        {
            this.library = library;
            if(library.isStdLib()) {
                icon = MetalIconFactory.getTreeHardDriveIcon();
            } else {
                icon = MetalIconFactory.getTreeFloppyDriveIcon();
            }
        }

        public Library getLibrary()
        {
            return library;
        }

        public void addCategoryNode(CategoryNode node)
        {
            childNodes.add(node);
            node.setParent((TreeNode)this);
        }

        public int compareTo(LibraryNode node)
        {
            Library l1 = this.library;
            Library l2 = node.getLibrary();
            if(l1.isStdLib() == l2.isStdLib()) {
                return l1.getName().compareToIgnoreCase(l2.getName());
            } else {
                if(l1.isStdLib()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }

        /** Inherit the behavior of compareTo.
        */
		public String toString()
		{
            return library.getName();
        }
        
        public boolean equals(LibraryNode node)
        {
        	return compareTo(node)==0;
        }
    }

    private static class CategoryNode extends AbstractMacroTreeNode
        implements Comparable<CategoryNode>
    {
        private Category category;

        CategoryNode(Category category)
        {
            this.category = category;
            icon = null;
        }

        public Category getCategory()
        {
            return category;
        }

        public void addMacroNode(MacroNode node)
        {
            childNodes.add(node);
            node.setParent((TreeNode)this);
        }

        public int compareTo(CategoryNode node)
        {
            Category c1 = this.category;
            Category c2 = node.getCategory();
            return c1.getName().compareToIgnoreCase(c2.getName());
        }


        public String toString()
        {
        	return category.getName();
        }

        /** Inherit the behavior of compareTo.
        */
        public boolean equals(CategoryNode node)
        {
            return compareTo(node)==0;
        }
    }


    private class MacroNode extends AbstractMacroTreeNode
        implements Comparable<MacroNode>
    {
        MacroDesc macro;

        MacroNode(MacroDesc macroDesc)
        {
            this.macro = macroDesc;
            icon = null;
        }

        public MacroDesc getMacro()
        {
            return macro;
        }

        @Override
        public boolean isLeaf()
        {
            return true;
        }

        public int compareTo(MacroNode node)
        {
            MacroDesc m1 = this.macro;
            MacroDesc m2 = node.getMacro();
            return m1.name.compareToIgnoreCase(m2.name);
        }


        public String toString()
        {
            return macro.name;
        }

        /** Inherit the behavior of compareTo.
        */
        public boolean equals(MacroNode node)
        {
            return compareTo(node)==0;
        }
    }
}

