package net.sourceforge.fidocadj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.NoSuchElementException;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Button;


import com.explorer.ExplorerActivity;
import com.explorer.IO;

import dialogs.DialogAbout;
import dialogs.DialogLayer;
import dialogs.DialogOpenFile;
import dialogs.DialogSaveName;
import layers.LayerDesc;
import graphic.android.ColorAndroid;
import geom.MapCoordinates;
import geom.DrawingSize;
import primitives.MacroDesc;
import toolbars.ToolbarTools;
import globals.AccessResources;
import globals.Globals;
import globals.ProvidesCopyPasteInterface;
import circuit.controllers.ContinuosMoveActions;
import circuit.controllers.ElementsEdtActions;

import net.sourceforge.fidocadj.librarymodel.Category;
import net.sourceforge.fidocadj.librarymodel.Library;
import net.sourceforge.fidocadj.librarymodel.LibraryModel;
import net.sourceforge.fidocadj.macropicker.ExpandableMacroListView;
import net.sourceforge.fidocadj.storage.StaticStorage;

/** The main activity of the FidoCadJ application. Important things handled
	here are:
	
	- Creation and destruction of the activity (app).
	- Handling menu events (contextual and main menu).
	
<pre>
    This file is part of FidoCadJ.

    FidoCadJ is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FidoCadJ is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with FidoCadJ.  If not, see <http://www.gnu.org/licenses/>.

    Copyright 2014 by Davide Bucci, Dante Loi
</pre>
*/

public class FidoMain extends Activity implements ProvidesCopyPasteInterface,
		SensorEventListener 
{
	private ToolbarTools tt;
	private FidoEditor drawingPanel;
	private final FragmentManager fragmentManager = getFragmentManager();

	/* Gyroscope and sensors gestures */
	private boolean activateSensors;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private float averagedAngleSpeedX;
	private float averagedAngleSpeedY;
	private float averagedAngleSpeedZ;
	private long holdoff;

	/* Loaded libraries and information */
	private List<Category> globalList;
	private List<Library> libsList;

	private Spinner librarySpinner;
	private ExpandableMacroListView listAdapter;
	private ExpandableListView expListView;
	private List<String> listDataHeader;
	private HashMap<String, HashMap<String, List<String>>> listDataHeader2;

	private final String tempFileName = "state.fcd.tmp";

	HashMap<String, List<String>> listDataChild;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		activateSensors = true;
		setContentView(R.layout.main);
		tt = new ToolbarTools();
		drawingPanel = (FidoEditor) findViewById(R.id.drawingPanel);

		StaticStorage.setCurrentEditor(drawingPanel);
		tt.activateListeners(this, drawingPanel.eea);

		Globals.messages = new AccessResources(this);

		activateSensors();
		createLibraryDrawer();

		StringBuilder text = new StringBuilder();
		text.append("[FIDOCAD]\n");
		
		// Now we read the current drawing which might be contained in the
		// temporary file. If there is something meaningful, we perform a 
		// zoom to fit operation. 
		File file = new File(getFilesDir(), tempFileName);
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;

			while ((line = br.readLine()) != null) {
				text.append(line);
				text.append('\n');
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// NOTE: DB: I am not completely sure that the following line is 
		// needed.
		drawingPanel.getParserActions().openFileName = tempFileName;
		drawingPanel.getParserActions().parseString(
				new StringBuffer(text.toString()));
		drawingPanel.getUndoActions().saveUndoState();
		
		drawingPanel.invalidate();

		// TODO: this is method which works well, but it is discouraged by
		// modern Android APIs.
		reloadInstanceData(getLastNonConfigurationInstance());
		IO.context = this;
		
		Button layerButton= (Button)findViewById(R.id.layer);
		Vector<LayerDesc> layers = 
			drawingPanel.getDrawingModel().getLayers();
		layerButton.setBackgroundColor(
					((ColorAndroid)layers.get(0).getColor())
					.getColorAndroid());
					
		// Zoom to fit only if there is something to show.
		if(!drawingPanel.getDrawingModel().isEmpty()) {
			drawingPanel.panToFit();
		}
	}

	/**
	 * Create the drawer on the right of the Activity, showing the list of
	 * libraries when the user slides her finger.
	 */
	public void createLibraryDrawer() 
	{
		// get the listview
		expListView = (ExpandableListView) findViewById(R.id.macroTree);

		// preparing list data
		prepareListData(new LibraryModel(drawingPanel.getDrawingModel()));

		listAdapter = new ExpandableMacroListView(this, listDataHeader,
				listDataChild);

		// setting list adapter
		expListView.setAdapter(listAdapter);
		expListView
				.setOnChildClickListener(new 
					ExpandableListView.OnChildClickListener() {
					public boolean onChildClick(ExpandableListView parent,
							View v, int groupPosition, int childPosition,
							long id) 
					{
						Category c = globalList.get(groupPosition);
						MacroDesc md = c.getAllMacros().get(childPosition);

						ContinuosMoveActions eea = drawingPanel
								.getContinuosMoveActions();
						if (md != null) {
							eea.setState(ElementsEdtActions.MACRO, md.key);
							tt.clear(null);
							// NOTE: close the drawer here!
							DrawerLayout drawer = (DrawerLayout) 
								findViewById(R.id.drawer_layout);
							drawer.closeDrawers();
						} else {
							eea.setState(ElementsEdtActions.SELECTION, "");
						}
						return true;
					}
				});

		librarySpinner = (Spinner) findViewById(R.id.librarySpinner);

		ArrayAdapter spinnerAdapter = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, libsList);
		librarySpinner.setAdapter(spinnerAdapter);
		librarySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) 
			{
				listDataHeader.clear();
				listDataChild.clear();
				globalList.clear();

				//currentLib = position;
				Library l = libsList.get(position);

				List<Category> catList = l.getAllCategories();
				for (Category c : catList) {
					listDataHeader.add(c.getName());
					List<String> ts = new ArrayList<String>();
					List<MacroDesc> macroList = c.getAllMacros();
					for (MacroDesc m : macroList) {
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
	}

	/**
	 * Activate the sensors (gyroscope) which will then be used for actions such
	 * as rotating and mirroring components.
	 */
	public void activateSensors() 
	{
		mSensorManager = (SensorManager) 
			getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
	}

	/**
	 * Saves the important data used in this instance, to be able to recover in
	 * a fast way when the user rotates the screen, for example.
	 * 
	 * @return an array of useful data, used by reloadInstanceData().
	 */
	@Override
	public Object onRetainNonConfigurationInstance() 
	{
		List<Object> data = new ArrayList<Object>();

		// data.add(drawingPanel.getDrawingModel()); // why is it not working?
		data.add(drawingPanel.getParserActions().getText(true));
		data.add(drawingPanel.eea.getSelectionState());
		data.add(drawingPanel.getMapCoordinates());
		data.add(Boolean.valueOf(drawingPanel.getShowGrid()));
		data.add(Boolean.valueOf(activateSensors));
		data.add(drawingPanel.getDrawingModel().getSelectionStateVector());

		return data;
	}

	/**
	 * This routine does the opposite of onRetainNonConfigurationInstance().
	 * 
	 * @param data
	 *            a List<Object> of different objects describing the state of
	 *            the app.
	 */
	public void reloadInstanceData(Object data) 
	{
		// Casting and order of objects is important

		if (data != null) {
			List<Object> d = (List<Object>) data;
			// drawingPanel.setDrawingModel((DrawingModel)d.get(0));
			drawingPanel.getParserActions()
					.parseString((StringBuffer) d.get(0));
			drawingPanel.eea.setActionSelected((Integer) d.get(1));
			drawingPanel.setMapCoordinates((MapCoordinates) d.get(2));
			drawingPanel.setShowGrid(((Boolean) d.get(3)).booleanValue());
			activateSensors = ((Boolean) d.get(4)).booleanValue();
			drawingPanel.getDrawingModel().setSelectionStateVector(
					(Vector<Boolean>) d.get(5));
		}
	}

	/*
	 * Preparing the list data.
	 */
	private void prepareListData(LibraryModel lib) 
	{
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();
		globalList = new ArrayList<Category>();

		// Adding child data
		libsList = lib.getAllLibraries();

		for (Library l : libsList) {
			List<Category> catList = l.getAllCategories();
			/*
			 * for(Category c : catList) { listDataHeader.add(c.getName());
			 * List<String> ts = new ArrayList<String>(); List<MacroDesc>
			 * macroList = c.getAllMacros(); for(MacroDesc m : macroList) {
			 * ts.add(m.name); } listDataChild.put(c.getName(), ts); }
			 * globalList.addAll(catList);
			 */
		}
	}

	/** Create the menus to be shown and ensure that the checkable items
		do reflect the current state of the application.
	*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.main_menu, menu);
		
		// Set the correct checking state.
		MenuItem showGrid = menu.findItem(R.id.showgrid);
		showGrid.setChecked(drawingPanel.getShowGrid());
		MenuItem snapToGrid = menu.findItem(R.id.snaptogrid);
		snapToGrid.setChecked(drawingPanel.getMapCoordinates().getSnap());

		MenuItem useSensors = menu.findItem(R.id.use_sensors_rotate_mirror);
		useSensors.setChecked(activateSensors);

		return true;
	}

	/** One of the most important functions for the user interface: handle 
		all the menu events!
	*/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		// status is a variable which will be used as a return value. It must
		// be put to 'true' if the action is taken into account and handled
		// properly.
		boolean status = false;
		String fileName;
		/* dialogs */
		DialogSaveName dsn;
		DialogOpenFile dof;
		DialogLayer dl;
		DialogAbout da;
		MapCoordinates mp;

		if (onContextItemSelected(item))
			return true;

		switch (item.getItemId()) {
			case R.id.menu_copy_split: // Copy and split nonstandard macros
				// TODO: this is not yet working.
				drawingPanel.getCopyPasteActions().copySelected(true, true,
					drawingPanel.getMapCoordinates().getXGridStep(),
					drawingPanel.getMapCoordinates().getYGridStep());
				status = true;
				break;
			case R.id.menu_undo: // Undo action
				drawingPanel.getUndoActions().undo();
				status = true;
				break;
			case R.id.menu_redo: // Redo action
				try {
					drawingPanel.getUndoActions().redo();
				} catch (NoSuchElementException E) {
					// Does nothing. Actually it is not a big issue.
					android.util.Log.w("fidocadj", "Can not redo.");
				}
				status = true;
				break;
			case R.id.new_drawing: // New drawing
				drawingPanel.getParserActions().parseString(
					new StringBuffer(""));
				drawingPanel.getParserActions().openFileName = null;
				drawingPanel.initLayers();
				drawingPanel.invalidate();
				status = true;
				break;
			case R.id.open_file: // Open an existing file
				Intent myIntent = new Intent(this, ExplorerActivity.class);
				myIntent.putExtra(ExplorerActivity.DIRECTORY, false);
				int requestCode = ExplorerActivity.REQUEST_FILE;
				startActivityForResult(myIntent, requestCode);
				break;
			case R.id.open_file_deprecated: // Open an existing file
				dof = new DialogOpenFile();
				dof.show(fragmentManager, "");
				status = true;
				break;
			case R.id.save: // Save
				fileName = drawingPanel.getParserActions().openFileName;
				if (fileName == null || fileName == tempFileName) {
					dsn = new DialogSaveName();
					dsn.show(fragmentManager, "");
				} else {
					IO.writeFileToSD(
						drawingPanel.getParserActions().openFileName,
						drawingPanel.getText());
				}
				status = true;
				break;
			case R.id.save_with_name: // Save with name
				dsn = new DialogSaveName();
				dsn.show(fragmentManager, "");
				status = true;
				break;
			case R.id.delete: // Delete a saved file
				fileName = drawingPanel.getParserActions().openFileName;
				if (fileName != null) {
					deleteFile(drawingPanel.getParserActions().openFileName);
					drawingPanel.getParserActions().parseString(
						new StringBuffer(""));
					drawingPanel.getParserActions().openFileName = null;
					drawingPanel.invalidate();
				} else {
					Toast toast = Toast.makeText(this, 
						R.string.No_file_opened, 5);
					toast.show();
				}
				status = true;
				break;
			case R.id.layer: // Set the current layer
				dl = new DialogLayer();
				dl.show(fragmentManager, "");
				status = true;
				break;
			case R.id.showgrid: // Toggle grid visibility
				drawingPanel.setShowGrid(!drawingPanel.getShowGrid());
				drawingPanel.invalidate();
				item.setChecked(drawingPanel.getShowGrid());
				status = true;
				break;
			case R.id.snaptogrid: // Toggle snap to grid while editing
				mp = drawingPanel.getMapCoordinates();
				mp.setSnap(!mp.getSnap());
				drawingPanel.invalidate();
				item.setChecked(mp.getSnap());
				status = true;
				break;
			case R.id.use_sensors_rotate_mirror: // Toggle "use sensors..."
				activateSensors = !activateSensors;
				item.setChecked(activateSensors);
				status = true;
				break;
			case R.id.zoomtofit: // Zoom to fit
				drawingPanel.zoomToFit();
				break;
			case R.id.about: // Show the about dialog
				da = new DialogAbout();
				da.show(fragmentManager, "");
				status = true;
				break;
			default:
		}
		if (!status)
			android.util.Log.e("fidocadj",
					"menu not found: " + item.getItemId());
		else
			drawingPanel.invalidate();
		return status;
	}

	/** Do something here if sensor accuracy changes. */
	@Override
	public final void onAccuracyChanged(Sensor sensor, int accuracy) 
	{
		// Normally it is not needed, but required for the interface
		// implementation
	}

	/**
	 * Implement sensor actions to detect rotation and flip gestures with the
	 * gyroscope.
	 */
	@Override
	public final void onSensorChanged(SensorEvent event) 
	{
		// Check if sensor gestures are active or not.
		if (!activateSensors)
			return;

		// Get the gyroscope angles.
		float xValue = event.values[0];
		float yValue = event.values[1];
		float zValue = event.values[2];

		// Exit if we are in a holdoff time. After one gesture has been
		// recognized, there is a certain time (until holdoff) where the
		// system is not responding to new events, to avoid multiple
		// triggering.
		if (event.timestamp < holdoff) {
			return;
		}

		// Calculate the averaged angle speed.
		averagedAngleSpeedX = 0.2f * averagedAngleSpeedX + 0.8f * xValue;
		averagedAngleSpeedY = 0.2f * averagedAngleSpeedY + 0.8f * yValue;
		averagedAngleSpeedZ = 0.2f * averagedAngleSpeedZ + 0.8f * zValue;

		// This is a delicate value: it should be enough to require a
		// deliberate action, but not too much.
		float threshold = 1.0f;

		// X or Y action: mirror
		if (averagedAngleSpeedX > threshold || averagedAngleSpeedX < -threshold
				|| averagedAngleSpeedY > threshold
				|| averagedAngleSpeedY < -threshold) {
			holdoff = event.timestamp + 500000000l;
			drawingPanel.getEditorActions().mirrorAllSelected();

			drawingPanel.invalidate();
		}

		// Z action: rotation.
		if (averagedAngleSpeedZ > threshold || 
			averagedAngleSpeedZ <-threshold) {

			holdoff = event.timestamp + 500000000l;
			drawingPanel.getEditorActions().rotateAllSelected();
			if (averagedAngleSpeedZ > 0.0f) {
				drawingPanel.getEditorActions().rotateAllSelected();
				drawingPanel.getEditorActions().rotateAllSelected();
			}
			drawingPanel.invalidate();
		}
	}

	/** Reactivate the events needed by the FidoCadJ app when it is brought
		on focus. This is in particular useful for the sensors gestures,
		which need to be deactivated when the app is not on the top.
	*/
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

	/** Create the contextual menu.
	
	*/
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.popupmenu, menu);
	}

	/**
	 * Called when the user selects something in the context menu.
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) 
	{
		boolean status = false;

		// Get the action selected by the user and execute it.
		switch (item.getItemId()) {
			case R.id.menu_param:
				drawingPanel.setPropertiesForPrimitive();
				break;
			case R.id.menu_cut:
				drawingPanel.getCopyPasteActions().copySelected(true, false,
					drawingPanel.getMapCoordinates().getXGridStep(),
					drawingPanel.getMapCoordinates().getYGridStep());
				drawingPanel.getEditorActions().deleteAllSelected(true);
				status = true;
				break;
			case R.id.menu_copy:
				drawingPanel.getCopyPasteActions().copySelected(true, false,
					drawingPanel.getMapCoordinates().getXGridStep(),
					drawingPanel.getMapCoordinates().getYGridStep());
				status = true;
				break;
			case R.id.menu_paste:
				drawingPanel.getCopyPasteActions().paste(
					drawingPanel.getMapCoordinates().getXGridStep(),
					drawingPanel.getMapCoordinates().getYGridStep());
				status = true;
				break;
			case R.id.menu_selectall:
				drawingPanel.getEditorActions().setSelectionAll(true);
				status = true;
				break;
			case R.id.menu_delete:
				drawingPanel.getEditorActions().deleteAllSelected(true);
				status = true;
				break;
			case R.id.menu_rotate:
				drawingPanel.getEditorActions().rotateAllSelected();
				status = true;
				break;
			case R.id.menu_mirror:
				drawingPanel.getEditorActions().mirrorAllSelected();
				status = true;
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, 
		Intent data) 
	{
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case ExplorerActivity.REQUEST_FILE:
				/** Manage the opening of file from SD card **/
				if (data.hasExtra(ExplorerActivity.FILENAME)) {
					String filename = data.getExtras().getString(
							ExplorerActivity.FILENAME);
					drawingPanel.getParserActions().openFileName = filename;
					drawingPanel.getParserActions().parseString(
							new StringBuffer(IO.readFileFromSD(filename)));
					drawingPanel.getUndoActions().saveUndoState();
					drawingPanel.invalidate();
				}
				break;
			}
		}
	}

	/** Saves the current state and finish the application.
	
	*/
	@Override
	public void onBackPressed() 
	{
		FileOutputStream outputStream;
		String fileName = tempFileName;
		try {
			outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
			outputStream.write(drawingPanel.getText().getBytes());
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		finish();
	}
}
