import circuit.CircuitPanel;
import circuit.controllers.ParserActions;
import circuit.model.DrawingModel;

public class CircuitPanelUpdater implements LibraryListener
{
	FidoFrame fidoFrame;
	
	CircuitPanelUpdater(FidoFrame fidoFrame)
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

