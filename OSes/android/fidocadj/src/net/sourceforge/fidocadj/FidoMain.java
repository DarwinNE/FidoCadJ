package net.sourceforge.fidocadj;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import android.content.ContentResolver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
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
import android.util.Log;
import android.net.Uri;

import android.support.v4.widget.DrawerLayout;

import com.explorer.ExplorerActivity;
import com.explorer.IO;

import net.sourceforge.fidocadj.dialogs.DialogAbout;
import net.sourceforge.fidocadj.dialogs.DialogLayer;
import net.sourceforge.fidocadj.dialogs.DialogOpenFile;
import net.sourceforge.fidocadj.dialogs.DialogSaveName;
import net.sourceforge.fidocadj.layers.LayerDesc;
import net.sourceforge.fidocadj.graphic.android.ColorAndroid;
import net.sourceforge.fidocadj.geom.MapCoordinates;
import net.sourceforge.fidocadj.geom.DrawingSize;
import net.sourceforge.fidocadj.primitives.MacroDesc;
import toolbars.ToolbarTools;
import net.sourceforge.fidocadj.globals.AccessResources;
import net.sourceforge.fidocadj.globals.Globals;
import net.sourceforge.fidocadj.globals.ProvidesCopyPasteInterface;
import net.sourceforge.fidocadj.circuit.controllers.ContinuosMoveActions;
import net.sourceforge.fidocadj.circuit.controllers.ElementsEdtActions;

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

    Copyright 2014-2015 by Davide Bucci, Dante Loi
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

    // Sort of a counter to specify a sort of a "dead time" after a sensor
    // gesture has been triggered.
    private long holdoff;

    /* Loaded libraries and information */
    private List<Category> globalList;
    private List<Library> libsList;
    private List<String> listDataHeader;
    private HashMap<String, HashMap<String, List<String>>> listDataHeader2;

    // Elements of the graphic interface.
    private Spinner librarySpinner;
    private ExpandableMacroListView listAdapter;
    private ExpandableListView expListView;

    // Name of the temporary file employed for saving the current drawing.
    private final String tempFileName = "state.fcd.tmp";

    HashMap<String, List<String>> listDataChild;

    /** Called when the activity is first created.
    */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        activateSensors = true;
        setContentView(R.layout.main);
        tt = new ToolbarTools();
        drawingPanel = (FidoEditor) findViewById(R.id.drawingPanel);

        // Create the standard directory for the drawings and libs, if needed:
        createDirIfNotExists("FidoCadJ/Drawings");
        createDirIfNotExists("FidoCadJ/Libs");

        StaticStorage.setCurrentEditor(drawingPanel);
        tt.activateListeners(this, drawingPanel.eea);

        Globals.messages = new AccessResources(this);

        activateSensors();
        readAllLibraries();
        createLibraryDrawer();

        // TODO: this is method which works well, but it is discouraged by
        // modern Android APIs.
        reloadInstanceData(getLastNonConfigurationInstance());
        IO.context = this;

        // Process the intents. It is useful when a file has to be opened.
        Uri data = getIntent().getData();
        boolean readPrevious=false;
        if(data!=null) {
            getIntent().setData(null);
            readPrevious=importData(data);
        }

        // If a file has not been opened, read the content of the previous
        // session and try to restore the drawing, as contained in the
        // temporary file.
        if(!readPrevious)
            readTempFile();

        // Update the color of the layer button.
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

    /** Read all libraries in the FidoCadJ/Libs directory.
    */
    private void readAllLibraries()
    {
        // Get the path of the external storage directory. For example,
        // in most cases it will be /storage/sdcard0/.
        // Therefore, the lib dir will be /storage/sdcard0/FidoCadJ/Libs
        File file_tm = new File(Environment.getExternalStorageDirectory(),
            "FidoCadJ/Libs");
        Log.e("fidocadj", "read lib dir:"+file_tm.getAbsolutePath());
        drawingPanel.getParserActions().loadLibraryDirectory(
            file_tm.getAbsolutePath());
    }

    /** Read the drawing stored in the temporary file
    */
    private void readTempFile()
    {
        // Now we read the current drawing which might be contained in the
        // temporary file. If there is something meaningful, we perform a
        // zoom to fit operation.
        StringBuilder text = new StringBuilder();
        text.append("[FIDOCAD]\n");

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
    }

    /** Inspired from

        http://richardleggett.co.uk/blog/2013/01/26/
        registering_for_file_types_in_android/

        detects if in the data there is a file indication, open it and
        load its contents.
        @return true if something has been loaded, false otherwise
    */
    private boolean importData(Uri data)
    {
        final String scheme = data.getScheme();
        // Check wether there is a file in the data provided.
        if(ContentResolver.SCHEME_FILE.equals(scheme)) {
            try {
                ContentResolver cr = getContentResolver();
                InputStream is = cr.openInputStream(data);
                if(is==null) return false;

                // Read the contents of the file.
                StringBuffer buf = new StringBuffer();
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is));
                String str;
                if(is!=null) {
                    while((str = reader.readLine()) !=null) {
                        buf.append(str+"\n");
                    }
                }
                is.close();
                return true;
            } catch (Exception e) {
                Log.e("fidocadj", "FidoMain.ImportData, Error reading file: "+
                    e.toString());
            }
        }
        return false;
    }

    /**
        Check if a directory exists in the external storage directory,
        and if not, create it. Source:
        http://stackoverflow.com/questions/2130932

        @param path the directory name to be used
        @return true if everything has been OK (either the directory exists
            or it has been created). false if an error occurred.
    */
    public static boolean createDirIfNotExists(String path)
    {
        boolean ret = true;

        File file = new File(Environment.getExternalStorageDirectory(), path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("fidocadj", "Problem creating output folder");
                ret = false;
            }
        }
        return ret;
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
        data.add(drawingPanel.getSelectionActions().getSelectionStateVector());

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
            drawingPanel.getSelectionActions().setSelectionStateVector(
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

        return super.onCreateOptionsMenu(menu);
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
                drawingPanel.getCopyPasteActions().copySelected(true, true);
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

    /** Called when the Activity is put in the "pause" state.
    */
    @Override
    protected void onPause()
    {
        super.onPause();
        // Avoid registering accelerometer events when the activity is
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
                drawingPanel.getCopyPasteActions().copySelected(true, false);
                drawingPanel.getEditorActions().deleteAllSelected(true);
                status = true;
                break;
            case R.id.menu_copy:
                drawingPanel.getCopyPasteActions().copySelected(true, false);
                status = true;
                break;
            case R.id.menu_paste:
                drawingPanel.getCopyPasteActions().paste(
                    drawingPanel.getMapCoordinates().getXGridStep(),
                    drawingPanel.getMapCoordinates().getYGridStep());
                status = true;
                break;
            case R.id.menu_selectall:
                drawingPanel.getSelectionActions().setSelectionAll(true);
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

    /** Stores the given String in the clipboard.
    */
    public void copyText(String s)
    {
        // Gets a handle to the clipboard service.
        ClipboardManager clipboard = (ClipboardManager)
            getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("simple text", s.toString());
        clipboard.setPrimaryClip(clip);
    }

    /** Get the current data (as String) in the clipboard.
    */
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

    /** Callback used when an Activity has finished and must send back data
        to the original caller.
        Here, it is used for the ExplorerActivity file browser, so we need
        to check what to do.
    */
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
                default:
                    break;
            }
        }
    }

    /** Save the current state and finish the application.
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
