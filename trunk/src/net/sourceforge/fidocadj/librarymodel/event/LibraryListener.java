package net.sourceforge.fidocadj.librarymodel.event;

public interface LibraryListener
{
	public void libraryLoaded();
	public void libraryNodeRenamed(RenameEvent e);
	public void libraryNodeRemoved(RemoveEvent e);
	public void libraryNodeAdded(AddEvent e);
	public void libraryNodeKeyChanged(KeyChangeEvent e);
	
}
