package fr.davbucci.test;

import dialogs.DialogAbout;
import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.*;
import android.view.ContextMenu.*;


import toolbars.*;

public class test_start extends Activity
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
		switch (item.getItemId()) 
		{
			case R.id.file: 			return file();
			case R.id.edit_menu: 		return edit();
			case R.id.view: 			return view();
			case R.id.about:		    return about();
			default: 					
				android.util.Log.e("fidocadj", "menu not found: "+
					item.getItemId());
				return false;
		}
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
				break;
			case R.id.menu_copy: 
				break;			
 			case R.id.menu_paste: 
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
   
}
