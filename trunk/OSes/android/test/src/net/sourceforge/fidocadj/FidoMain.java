package net.sourceforge.fidocadj;

import java.io.FileOutputStream;
import java.io.IOException;
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
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.AdapterView.*;
import dialogs.DialogAbout;
import dialogs.DialogOpenFile;
import dialogs.DialogSaveName;
import net.sourceforge.fidocadj.macropicker.*;
import net.sourceforge.fidocadj.librarymodel.*;
import circuit.controllers.*;
import primitives.*;
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
  	private float averagedAngleSpeedX;
	private float averagedAngleSpeedY;
	private float averagedAngleSpeedZ;
	private long holdoff;
	
	/* Loaded libraries and information */
	private List<Category> globalList;
	private List<Library> libsList;
	private int currentLib;
	
	private Spinner librarySpinner;
	private ExpandableMacroListView listAdapter;
    private ExpandableListView expListView;
    private List<String> listDataHeader;
    private HashMap<String, HashMap<String, List<String>>> listDataHeader2;

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
        prepareListData(new LibraryModel(drawingPanel.getDrawingModel()));
 
        listAdapter = new ExpandableMacroListView(this, 
        	listDataHeader, listDataChild);
 
        // setting list adapter
        expListView.setAdapter(listAdapter);
        expListView.setOnChildClickListener(
        	new ExpandableListView.OnChildClickListener() {
        		 public boolean onChildClick(ExpandableListView parent, 
        		 	View v, int groupPosition, int childPosition, long id)
        		 {
        		 	Category c= globalList.get(groupPosition);
        		 	MacroDesc md=c.getAllMacros().
        		 		get(childPosition);
        		 	
        		 	ContinuosMoveActions eea = 
        		 		drawingPanel.getContinuosMoveActions();
        		 	if(md!=null){
        				eea.setState(ElementsEdtActions.MACRO, md.key);
                    } else {
                    	eea.setState(ElementsEdtActions.SELECTION, "");
                    }	
        		 	
        		 	return true;
        		 }
        	});
        
        librarySpinner =  (Spinner)findViewById(R.id.librarySpinner);
        
        ArrayAdapter spinnerAdapter = new ArrayAdapter(this,
        	android.R.layout.simple_spinner_item, libsList);
        librarySpinner.setAdapter(spinnerAdapter);
        librarySpinner.setOnItemSelectedListener(
        	new OnItemSelectedListener() {
    			@Override
   				public void onItemSelected(AdapterView<?> parentView, 
   				 	View selectedItemView, int position, long id) 
   				{
   					listDataHeader.clear();
        			listDataChild.clear();
        			globalList.clear();

        			currentLib=position;
        			Library l = libsList.get(position);
        			
   					List<Category>catList = l.getAllCategories();
        			for(Category c : catList) {
        				listDataHeader.add(c.getName());
						List<String> ts = new ArrayList<String>();
        				List<MacroDesc> macroList = c.getAllMacros();
        				for(MacroDesc m : macroList) {
        					ts.add(m.name);
        				}
	        			listDataChild.put(c.getName(), ts);
	        			globalList.add(c);
        			}
        			listAdapter.notifyDataSetChanged();   					
    			}
    			
    			@Override
    			public void onNothingSelected(AdapterView<?> parentView)
    			{
    				listDataChild.clear();
        			listAdapter.notifyDataSetChanged(); 
    			}
    		});
        
        
        
        // TODO: this is method which works well, but it is discouraged by
        // modern Android APIs. It requires to redo the parsing, which for 
        // *very* complex drawings can be a little waste of resources.
        final Object data = getLastNonConfigurationInstance();
    
    	if (data != null) {
    		List<Object> d = (List<Object>) data;
    		drawingPanel.getParserActions().parseString((StringBuffer)d.get(0));
    		drawingPanel.eea.setActionSelected((Integer) d.get(1));
    	}
    }
    
    @Override
	public Object onRetainNonConfigurationInstance() 
    {
    	List<Object> data = new ArrayList<Object>();
    	
    	data.add(drawingPanel.getParserActions().getText(true));
    	data.add(drawingPanel.eea.getSelectionState());

    	return data;
	}
 
    /*
     * Preparing the list data
     */
    private void prepareListData(LibraryModel lib) 
    {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        globalList=new ArrayList<Category>();
        
        // Adding child data
        libsList = lib.getAllLibraries();
        
        for(Library l : libsList) {        	
        	List<Category>catList = l.getAllCategories();
       /* 	for(Category c : catList) {
        		listDataHeader.add(c.getName());
				List<String> ts = new ArrayList<String>();
        		List<MacroDesc> macroList = c.getAllMacros();
        		for(MacroDesc m : macroList) {
        			ts.add(m.name);
        		}
        		listDataChild.put(c.getName(), ts);
        	}
        	globalList.addAll(catList);*/
        }
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
		/* dialogs */
	    DialogSaveName dsn;
	    DialogOpenFile dof;
	    DialogAbout da;
	    
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
			case R.id.new_drawing:
				drawingPanel.getParserActions()
					.parseString(new StringBuffer(""));
				drawingPanel.getParserActions().openFileName = null;
				drawingPanel.invalidate();
				status = true;
				break;
			case R.id.open_file: 
				dof = new DialogOpenFile();
				dof.show(fragmentManager, "");
				status = true;
				break;
			case R.id.save: 
				String fileName = drawingPanel.getParserActions().openFileName;
				if(fileName == null) {
					dsn = new DialogSaveName();
					dsn.show(fragmentManager, "");
				} else {
					FileOutputStream outputStream;
					
					try {
					  outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
					  outputStream.write(drawingPanel.getText().getBytes());
					  outputStream.close();
					} catch (IOException e) {
					  e.printStackTrace();
					}
				}
				status = true;
				break;
			case R.id.save_with_name:
				dsn = new DialogSaveName();
				dsn.show(fragmentManager, "");
				status = true;
				break;
			case R.id.about:
				da = new DialogAbout();   
				da.show(fragmentManager, "");
				status = true;
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





