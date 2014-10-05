package com.explorer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import net.sourceforge.fidocadj.R;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
TODO: provide a license notice for this file, document class and public methods
*/

public class ExplorerActivity extends ListActivity 
{
	/** Called when the activity is first created. */
	public static String ROOT = IO.rootDir;
	public String CURDIR = ROOT;
	public String PARENT = ROOT;
	public static String FILENAME = "filename";
	public static String DIRECTORY = "directory";

	public static final int REQUEST_FILE = 0;
	public static final int REQUEST_FOLDER = 1;
	public static final int REQUEST_USER = 2;

	String _FILENAME = "";
	String _DIRECTORY = "";

	Context context = null;

	public boolean FOLDER_SELECTION = false;

	/**
		The starting point of the Activity.
		
		The file explorer can be used to select files or folder. 
		When you call the activity you must specify the request that can be
		ExplorerActivity.REQUEST_FILE or ExplorerActivity.REQUEST_FOLDER. 
		I.e.:
		<pre>	
			myIntent = new Intent(this, ExplorerActivity.class);
			this.startActivityForResult(myIntent, 
				ExplorerActivity.REQUEST_FILE);
		</pre>
				
		And then in the onActivityResult you need to manage what to do with 
		the directory retrieved.
		<pre>
			@Override
			protected void onActivityResult(int requestCode, 
				int resultCode, Intent data) 
			{
				if (resultCode == RESULT_OK) {
					switch (requestCode) {
						case ExplorerActivity.REQUEST_FOLDER:
							// Put code here for handling a folder request
							dir = data.getExtras().getString(
								ExplorerActivity.DIRECTORY)
							break;
						case ExplorerActivity.REQUEST_FILE:
							// Put code here for handling a file request
							file = data.getExtras().getString(
								ExplorerActivity.FILENAME);
						default:
							break;
					}
				}
			}
		</pre>
	*/
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.explorer_list_view);

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		context = this;

		FOLDER_SELECTION = getIntent().getBooleanExtra(DIRECTORY, false);
		if (!FOLDER_SELECTION) {
			LinearLayout ll=(LinearLayout)findViewById(R.id.explorer_layout);
			Button bt = (Button) findViewById(R.id.explorer_bt);
			ll.removeView(bt);
		}

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String item = (String) ((TextView) view
						.findViewById(R.id.explorer_tv)).getText();
				if (item == "." || item == "..") {
					if (item == ".") {
						showDir((new File(CURDIR)).getAbsolutePath());
					} else {
						showDir((new File(CURDIR)).getParent());
					}
				} else {
					File path = new File(CURDIR + "/" + item);
					if (path.isDirectory()) {
						showDir(path.getAbsolutePath());
					} else {
						_FILENAME = path.getAbsolutePath();
						doAction();
					}
				}
			}
		});
		showDir(ROOT);
	}

	public void doAction() 
	{
		Intent data = new Intent();
		data.putExtra(FILENAME, _FILENAME);
		data.putExtra(DIRECTORY, _DIRECTORY);

		setResult(RESULT_OK, data);
		this.finish();
	}

	protected void browseFile(String absolutePath) 
	{
		Intent intent = new Intent();
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, 1);
	}

	public String[] getList(String dir) 
	{
		String[] listDir = new String[] {};
		File aDir = new File(dir);
		if (aDir.isDirectory()) {
			if (aDir.list() != null) {
				listDir = aDir.list();
			}
		}
		if (FOLDER_SELECTION) {
			listDir = filterFiles(listDir);
		}
		String[] defDir = new String[listDir.length + 2];
		defDir[0] = "."; // Refresh
		defDir[1] = ".."; // Up one level
		System.arraycopy(listDir, 0, defDir, 2, listDir.length);
		return defDir;
	}

	public String[] filterFiles(String[] list) 
	{
		List<String> filtered = new ArrayList<String>();
		for (String f : list) {
			if (new File(CURDIR + "/" + f).isDirectory()) {
				filtered.add(f);
			}
		}
		return filtered.toArray(new String[] {});
	}

	/** Sets the current dir to be shown.
		@param dir the dir to be shown.
	*/
	public void showDir(String dir) 
	{
		CURDIR = dir;
		File path = new File(dir);
		setTitle(path.getAbsolutePath());
		String[] listDir = getList(path.getAbsolutePath());
		Arrays.sort(listDir, new Comparator<String>() {
			public int compare(String o1, String o2) 
			{
				return o1.compareTo(o2);
			}
		});

		setListAdapter(new ExplorerAdapter(this, listDir,
				path.getAbsolutePath()));

	}

	public void OnSelectClick(View view) 
	{
		_DIRECTORY = CURDIR;
		Intent data = new Intent();
		data.putExtra(FILENAME, _FILENAME);
		data.putExtra(DIRECTORY, _DIRECTORY);
		setResult(RESULT_OK, data);
		this.finish();
	}
}