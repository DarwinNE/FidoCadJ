package net.sourceforge.fidocadj;

import android.util.FloatMath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.*;
import android.view.ContextMenu.*;
import android.content.*;
import android.hardware.*;
import android.widget.ExpandableListView;

import dialogs.DialogAbout;

import net.sourceforge.fidocadj.macropicker.*;



import toolbars.*;
import globals.*;

public class FidoMain extends Activity implements ProvidesCopyPasteInterface,
	SensorEventListener
{
	private ToolbarTools tt;
	private FidoEditor drawingPanel;
	private FragmentManager fragmentManager = getFragmentManager();
	
	/* Gyroscope gestures */
	private SensorManager mSensorManager;
  	private Sensor mAccelerometer;
  	float averagedAngleSpeedX;
	float averagedAngleSpeedY;
	float averagedAngleSpeedZ;
	long holdoff;
	
	ExpandableMacroListView listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tt = new ToolbarTools();
        drawingPanel = (FidoEditor)findViewById(R.id.drawingPanel);
        
        Globals.messages = new AccessResources(this);
        tt.activateListeners(this, drawingPanel.eea);
        mSensorManager = (SensorManager) 
        	getSystemService(Context.SENSOR_SERVICE);
    	mAccelerometer = mSensorManager.getDefaultSensor(
    		Sensor.TYPE_GYROSCOPE);
		
		// get the listview
        expListView = (ExpandableListView) findViewById(R.id.macroTree);
 
        // preparing list data
        prepareListData();
 
        listAdapter = new ExpandableMacroListView(this, listDataHeader, listDataChild);
 
        // setting list adapter
        expListView.setAdapter(listAdapter);
    }
 
    /*
     * Preparing the list data
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        
        // TODO: FidoCadJ should construct these lists from the libraries which
        // have been read. Here we just construct some junk data, just to show
        // something vaguely related to FidoCadJ.
 
        // Adding child data
        listDataHeader.add("Standard library");
        listDataHeader.add("IHRAM v. 2fs");
        listDataHeader.add("User library");
 
        // Adding child data
        List<String> std = new ArrayList<String>();
        std.add("NPN transistor");
        std.add("PNP transistor");
        std.add("Resistor");
 
 		// Adding child data
        List<String> ihram = new ArrayList<String>();
        ihram.add("Biased NPN transistor");
        ihram.add("Biased PNP transistor");
        ihram.add("Resistor, alt");
 
 		// Adding child data
        List<String> user = new ArrayList<String>();
        user.add("LED with resistor");
        user.add("JFET transistor");
        user.add("Memristor");
        
 
        listDataChild.put(listDataHeader.get(0), std); // Header, Child data
        listDataChild.put(listDataHeader.get(1), ihram);
        listDataChild.put(listDataHeader.get(2), user);
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
			case R.id.menu_undo:
				drawingPanel.getUndoActions().undo();
				status=true;
				break;
			case R.id.menu_redo:
				drawingPanel.getUndoActions().redo();
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
		else
			drawingPanel.invalidate();
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
  	public final void onAccuracyChanged(Sensor sensor, int accuracy) 
  	{
    	// Do something here if sensor accuracy changes.
  	}

	/** Implement sensor actions to detect rotation and flip gestures with
		the gyroscope.
	*/
  	@Override
  	public final void onSensorChanged(SensorEvent event) 
  	{
    	// Get the gyroscope angles.
    	float xValue = event.values[0];
    	float yValue = event.values[1];
    	float zValue = event.values[2];
    	
    	// Exit if we are in a holdoff time. After one gesture has been
    	// recognized, there is a certain time (until holdoff) where the
    	// system is not responding to new events, to avoid multiple
    	// triggering.
    	if(event.timestamp<holdoff) {
    		return;
    	}
    		
    	// Calculate the averaged angle speed.
    	averagedAngleSpeedX=0.2f*averagedAngleSpeedX+0.8f*xValue;
	    averagedAngleSpeedY=0.2f*averagedAngleSpeedY+0.8f*yValue;
	   	averagedAngleSpeedZ=0.2f*averagedAngleSpeedZ+0.8f*zValue;

    	// This is a delicate value: it should be enough to require a 
    	// deliberate action, but not too much.
    	float threshold=0.75f;
    	
    	// X or Y action: mirror
    	if (averagedAngleSpeedX>threshold ||averagedAngleSpeedX<-threshold ||
    		averagedAngleSpeedY>threshold ||averagedAngleSpeedY<-threshold) {
    		holdoff = event.timestamp+500000000l;
    		drawingPanel.getEditorActions().mirrorAllSelected();

    		drawingPanel.invalidate();			
    	}
    	
    	// Z action: rotation.
    	if (averagedAngleSpeedZ>threshold ||averagedAngleSpeedZ<-threshold) {
    	
    		holdoff = event.timestamp+500000000l;
    		drawingPanel.getEditorActions().rotateAllSelected();
    		if (averagedAngleSpeedZ>0.0f) {
    			drawingPanel.getEditorActions().rotateAllSelected();
    			drawingPanel.getEditorActions().rotateAllSelected();
    		}
    		drawingPanel.invalidate();			
    	}
  	}

  	@Override
  	protected void onResume() 
  	{
    	super.onResume();
    	// Restore registering accelerometer events.
    	mSensorManager.registerListener(this, mAccelerometer, 
    		SensorManager.SENSOR_DELAY_NORMAL);
  	}

  	@Override
  	protected void onPause() 
  	{
    	super.onPause();
    	// Avoid registering accelerometer events when the application is
    	// paused.
    	mSensorManager.unregisterListener(this);
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
				drawingPanel.setPropertiesForPrimitive();
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
