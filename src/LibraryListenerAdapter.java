/**
 * Adapter class of LibraryListener interface.
 */
public class LibraryListenerAdapter implements LibraryListener
{
	public void libraryLoaded(){}
	public void libraryNodeRenamed(RenameEvent e){}
	public void libraryNodeRemoved(RemoveEvent e){}
	public void libraryNodeAdded(AddEvent e){}
	public void libraryNodeKeyChanged(KeyChangeEvent e){}
}
