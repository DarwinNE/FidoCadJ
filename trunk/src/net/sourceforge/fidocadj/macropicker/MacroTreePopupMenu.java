package net.sourceforge.fidocadj.macropicker;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import primitives.MacroDesc;

import globals.*;

import net.sourceforge.fidocadj.librarymodel.Library;
import net.sourceforge.fidocadj.librarymodel.Category;

public class MacroTreePopupMenu extends JPopupMenu implements
			ChangeListener
{
	private MacroTree macroTree;
	private MacroTree.OperationPermission permission;
	
	private JMenuItem copyMenu;
	private JMenuItem pasteMenu;
	private JMenuItem renameMenu;
	private JMenuItem removeMenu;
	private JMenuItem renkeyMenu;
	
	public MacroTreePopupMenu(MacroTree macroTree)
	{
		this.macroTree = macroTree;
		permission = macroTree.getOperationPermission();
		
		copyMenu = new JMenuItem(Globals.messages.getString("Copy"));
		pasteMenu = new JMenuItem(Globals.messages.getString("Paste"));
		removeMenu = new JMenuItem(Globals.messages.getString("Delete"));
		renameMenu = new JMenuItem(Globals.messages.getString("Rename"));
		renkeyMenu = new JMenuItem(Globals.messages.getString("RenKey"));

		this.add(copyMenu);
		this.add(pasteMenu);
		this.add(removeMenu);
		this.add(renameMenu);
		this.add(renkeyMenu);

		copyMenu.addActionListener(createCopyActionListener());
		pasteMenu.addActionListener(createPasteActionListener());
		removeMenu.addActionListener(createRemoveActionListener());
		renameMenu.addActionListener(createRenameActionListener());
		renkeyMenu.addActionListener(createRenkeyActionListener());
	}
	
	public void stateChanged(ChangeEvent e)
	{
		copyMenu.setEnabled(permission.isCopyAvailable());
		pasteMenu.setEnabled(permission.isPasteAvailable());
		removeMenu.setEnabled(permission.isRemoveAvailable());
		renameMenu.setEnabled(permission.isRenameAvailable());
		renkeyMenu.setEnabled(permission.isRenKeyAvailable());
	}
	
	private void enableAllMenu(boolean b)
	{
		copyMenu.setEnabled(b);
		pasteMenu.setEnabled(b);
		removeMenu.setEnabled(b);
		renameMenu.setEnabled(b);
		renkeyMenu.setEnabled(b);
	}
	
	private ActionListener createRenameActionListener()
	{
		final MacroTree mt = macroTree;
		ActionListener al = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				switch(mt.getSelectedType()) {
				case MacroTree.MACRO:
					MacroDesc m = mt.getSelectedMacro();
					if(m!=null){
						mt.rename(m);
					}
					break;
				case MacroTree.CATEGORY:
					Category c = mt.getSelectedCategory();
					if(c!=null){
						mt.rename(c);
					}
					break;
				case MacroTree.LIBRARY:
					Library l = mt.getSelectedLibrary();
					if(l!=null){
						mt.rename(l);
					}
					break;
				}
			}
		};
		
		return al;
	}
	
	private ActionListener createRemoveActionListener()
	{
		final MacroTree mt = macroTree;
		ActionListener al = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				switch(mt.getSelectedType()) {
				case MacroTree.MACRO:
					MacroDesc m = mt.getSelectedMacro();
					if(m!=null){
						mt.remove(m);
					}
					break;
				case MacroTree.CATEGORY:
					Category c = mt.getSelectedCategory();
					if(c!=null){
						mt.remove(c);
					}
					break;
				case MacroTree.LIBRARY:
					Library l = mt.getSelectedLibrary();
					if(l!=null){
						mt.remove(l);
					}
					break;
				}
			}
		};
		return al;
	}
	
	private ActionListener createRenkeyActionListener()
	{
		final MacroTree mt = macroTree;
		ActionListener al = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				switch(mt.getSelectedType()) {
				case MacroTree.MACRO:
					MacroDesc m = mt.getSelectedMacro();
					if(m!=null){
						mt.changeKey(m);
					}
					break;
				case MacroTree.CATEGORY:
					//NOP
					break;
				case MacroTree.LIBRARY:
					//NOP
					break;
				}
			}
		};
		return al;
	}
	
	private ActionListener createCopyActionListener()
	{
		final MacroTree mt = macroTree;
		ActionListener al = new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				mt.setSelectedNodeToCopyTarget();
			}
		};
		return al;
	}

	private ActionListener createPasteActionListener()
	{
		final MacroTree mt = macroTree;
		ActionListener al = new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				mt.pasteIntoSelectedNode();
			}
		};
		return al;
	}	
}

