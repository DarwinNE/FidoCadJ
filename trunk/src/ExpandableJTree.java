import javax.swing.*;
import javax.swing.tree.*;
import java.awt.Graphics;

public class ExpandableJTree extends JTree
{
	private boolean runOnce = false;
	private boolean direction = false;
	
	private void fillExpandState(boolean expand)
	{
		//NOTES:
		//This only switchs expand state.
		//Actually expanding/collapsing tree is on next repaint.
		TreePath path;
		for(int row=1; row<getRowCount(); row++) {
			path = getPathForRow(row);
			if(!getModel().isLeaf(path.getLastPathComponent())) {
				setExpandedState(path, expand);
			}
		}
	}
	
	public void expandOnce()
	{
		runOnce = true;
		direction = true;
	}
	
	public void collapseOnce()
	{
		runOnce = true;
		direction = false;
	}
	
	public void selectNextLeaf()
	{
		int nextRow = searchNextLeaf(true);
		
		if(0 <= nextRow && nextRow < getRowCount()) {
			setSelectionRow(nextRow);
			scrollRowToVisible(nextRow);
		}
	}
	
	public void selectPrevLeaf()
	{
		int nextRow = searchNextLeaf(false);
		
		if(0 <= nextRow && nextRow < getRowCount()) {
			setSelectionRow(nextRow);
			scrollRowToVisible(nextRow);
		}
	}	

	private int getSelectedRow()
	{
		int selectedRow = -1;
		int[] selectedRows;
		
		selectedRows = getSelectionRows();
		
		if(selectedRows == null || selectedRows.length == 0) {
			selectedRow = -1;
		} else {
			selectedRow = selectedRows[0];
		}
		
		return selectedRow;		
	}
	
	private int searchNextLeaf(boolean searchForward)
	{
		int nextRow = getSelectedRow();
		
		for(int i=0;i<getRowCount();i++){
			if(searchForward){
				nextRow++;
			} else {
				nextRow--;
			}
			
			if(nextRow < 0){
				nextRow = getRowCount();
				System.out.println(nextRow);
				continue;
			} else if(getRowCount() <= nextRow){
				nextRow = -1;
				continue;
			}
			
			if(getModel().isLeaf(getPathForRow(nextRow).getLastPathComponent())) {
				return nextRow;
			}
		}
		
		return -1;
	}
	
	public void paint(Graphics g)
	{
		if(runOnce) {
			fillExpandState(direction);
			runOnce = false;
		}
		super.paint(g);
	}
}
