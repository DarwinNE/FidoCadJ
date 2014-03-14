package fr.davbucci.test;

import dialogs.DialogAbout;
import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import toolbars.*;

public class test_start extends Activity
{
	private ToolbarTools tt;
	private FidoEditor drawingpanel;
	private FragmentManager fragmentManager = getFragmentManager();
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tt = new ToolbarTools();
        drawingpanel = (FidoEditor)findViewById(R.id.drawingpanel);
        
        tt.activateListeners(this, drawingpanel.eea);
        
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
			default: 					return false;
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
       
}
