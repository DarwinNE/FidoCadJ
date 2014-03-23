package net.sourceforge.fidocadj;

import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;

/**
 * Abstract class for MacroTreeNode.
 */
public abstract class AbstractMacroTreeNode implements MacroTreeNode
{
    protected Vector childNodes;
    protected TreeNode parent;
    protected String label;
    protected Icon icon;

    AbstractMacroTreeNode()
    {
        childNodes = new Vector();
        label="";
    }

    /** Implements TreeNode interface */
    public Enumeration children()
    {
        return childNodes.elements();
    }

    /** Implements TreeNode interface */
    public boolean getAllowsChildren()
    {
        return true;
    }

    /** Implements TreeNode interface */
    public TreeNode getChildAt(int childIndex)
    {
        return (TreeNode)childNodes.get(childIndex);
    }

    /** Implements TreeNode interface */
    public int getChildCount()
    {
        return childNodes.size();
    }

    /** Implements TreeNode interface */
    public int getIndex(TreeNode node)
    {
        return childNodes.indexOf(node);
    }

    /** Implements TreeNode interface */
    public TreeNode getParent()
    {
        return (TreeNode)parent;
    }

    public void setParent(TreeNode parent)
    {
        this.parent = parent;
    }

    /**
     * Implements TreeNode interface.
     * @return boolean false default.
     */
    public boolean isLeaf()
    {
        return false;
    }

    /** Sort recursive. */
    public void sortTree()
    {
        Collections.sort(childNodes);
        for(Object n:childNodes) {
            ((AbstractMacroTreeNode)n).sortTree();
        }
    }

    public void removeChildNode(TreeNode node)
    {
        childNodes.remove(node);
    }

    public int getIndexAt(TreeNode node)
    {
        return childNodes.indexOf(node);
    }

    public Icon getIcon()
    {
        return icon;
    }

    public String toString()
    {
        return label;
    }

    public void clearChildNodes()
    {
        childNodes.clear();
    }
}

