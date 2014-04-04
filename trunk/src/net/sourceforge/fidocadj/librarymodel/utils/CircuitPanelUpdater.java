package net.sourceforge.fidocadj.librarymodel.utils;

import net.sourceforge.fidocadj.librarymodel.event.LibraryListener;
import net.sourceforge.fidocadj.librarymodel.event.AddEvent;
import net.sourceforge.fidocadj.librarymodel.event.KeyChangeEvent;
import net.sourceforge.fidocadj.librarymodel.event.RemoveEvent;
import net.sourceforge.fidocadj.librarymodel.event.RenameEvent;
import net.sourceforge.fidocadj.FidoFrame;

import circuit.CircuitPanel;
import circuit.controllers.ParserActions;
import circuit.model.DrawingModel;

public class CircuitPanelUpdater implements LibraryListener
{
	FidoFrame fidoFrame;
	
	public CircuitPanelUpdater(FidoFrame fidoFrame)
	{
		this.fidoFrame = fidoFrame;
	}
	
	public void libraryLoaded()
	{
		updateCircuitPanel();
	}

	public void libraryNodeRenamed(RenameEvent e)
	{
		//NOP
	}

	public void libraryNodeRemoved(RemoveEvent e)
	{
		updateCircuitPanel();
	}

	public void libraryNodeAdded(AddEvent e)
	{
		//NOP
	}

	public void libraryNodeKeyChanged(KeyChangeEvent e)
	{
		updateCircuitPanel();
	}
	
	private void updateCircuitPanel()
	{
		CircuitPanel cp = fidoFrame.CC;
		DrawingModel ps = cp.P;
		ParserActions pa = new ParserActions(ps);
		cp.getParserActions().parseString(pa.getText(true));
		cp.repaint();
	}
}

