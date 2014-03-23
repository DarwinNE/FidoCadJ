package net.sourceforge.fidocadj;

import dialogs.DialogAbout;
import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.*;
import android.view.ContextMenu.*;
import android.content.*;


import toolbars.*;
import globals.*;

public class test_start extends Activity implements ProvidesCopyPasteInterface
{
	private ToolbarTools tt;
	private FidoEditor drawingPanel;
	private FragmentManager fragmentManager = getFragmentManager();
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tt = new ToolbarTools();
        drawingPanel = (FidoEditor)findViewById(R.id.drawingPanel);
        
        tt.activateListeners(this, drawingPanel.eea);
    }
    
    @Override
	public boolean onCreateOptionsMenu (Menu menu) 
	{
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected (MenuItem item) 
	{
		boolean status=false;

		if (onContextItemSelected(item))
			return true;
			
		switch (item.getItemId()) {
			case R.id.menu_copy_split:
				// TODO: this is not yet working.
				drawingPanel.getCopyPasteActions().copySelected(
					true, true,
                	drawingPanel.getMapCoordinates().getXGridStep(), 
                	drawingPanel.getMapCoordinates().getYGridStep());
                status=true;
				break;
				
			case R.id.file: 			
				status = file();
				break;
			case R.id.edit_menu: 
				status = edit();
				break;
				
			case R.id.view: 
				status = view();
				break;
				
			case R.id.about:
				status = about();
				break;
			default: 					
				
		}
		if(!status)
			android.util.Log.e("fidocadj", "menu not found: "+
					item.getItemId());
		return status;
	}
	
	private boolean file()
	{
		return true;
	}
	
	private boolean edit()
	{
		return true;
	}
	
	private boolean view()
	{
		return true;
	}
    	
	private boolean about() 
	{			
		DialogAbout dialog = new DialogAbout();   
		dialog.show(fragmentManager, "");
	    return true;
	}	
	
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, 
     	ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);

    	MenuInflater inflater = getMenuInflater();
   		inflater.inflate(R.menu.popupmenu, menu);
   	
    }
    
    /** Called when the user selects something in the context menu.
    */
   	@Override
   	public boolean onContextItemSelected (MenuItem item)
   	{
   		boolean status=false;
		
		// Get the action selected by the user and execute it.
		switch (item.getItemId()) 
		{
			case R.id.menu_param: 
				break;			
			case R.id.menu_cut:
				drawingPanel.getCopyPasteActions().copySelected(
					true, false,
                	drawingPanel.getMapCoordinates().getXGridStep(), 
                	drawingPanel.getMapCoordinates().getYGridStep());
				drawingPanel.getEditorActions().deleteAllSelected(true);
				status=true;
				break;
			case R.id.menu_copy:
				drawingPanel.getCopyPasteActions().copySelected(
					true, false,
                	drawingPanel.getMapCoordinates().getXGridStep(), 
                	drawingPanel.getMapCoordinates().getYGridStep());
                status=true;
				break;
 			case R.id.menu_paste: 
 				drawingPanel.getCopyPasteActions().paste(
 					drawingPanel.getMapCoordinates().getXGridStep(), 
                	drawingPanel.getMapCoordinates().getYGridStep());
                status=true;
 				break;			
			case R.id.menu_selectall:
				drawingPanel.getEditorActions().setSelectionAll(true);
				status=true;
				break;
			case R.id.menu_delete:
				drawingPanel.getEditorActions().deleteAllSelected(true);
				status=true;
				break;
			case R.id.menu_rotate:
				drawingPanel.getEditorActions().rotateAllSelected();
				status=true;
				break;
			case R.id.menu_mirror:
				drawingPanel.getEditorActions().mirrorAllSelected();
				status=true;
				break;
			default: 					
				break;
		}
		
		// If something has changed, force a redraw.
		if (status)
			drawingPanel.invalidate();
			
		return status;
	}
	
	public void copyText(String s)
	{
		 // Gets a handle to the clipboard service.
		ClipboardManager clipboard = (ClipboardManager)
        	getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("simple text", s.toString());
        clipboard.setPrimaryClip(clip);
	}
	public String pasteText()
	{
		ClipboardManager clipboard = (ClipboardManager) 
        	getSystemService(Context.CLIPBOARD_SERVICE);

		String pasteData = "";
		ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);

		// Gets the clipboard as text.
		pasteData = item.getText().toString();

		// If the string contains data, then the paste operation is done
		return pasteData;
	}
   
}
