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
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
TODO:  document class and public methods

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

    Copyright 2014 by Cronos80, Davide Bucci
</pre>
*/

public class ExplorerActivity extends ListActivity 
{
	public static String ROOT = IO.rootDir;
	public String curDir = ROOT;
	public String parentDir = ROOT;
	public static final String FILENAME = "filename";
	public static final String DIRECTORY = "directory";

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
		
		String path="FidoCadJ/Drawings";
		File file = new File(Environment.getExternalStorageDirectory(), path);
		curDir=file.getAbsolutePath();
		parentDir=curDir;
		
		FOLDER_SELECTION = getIntent().getBooleanExtra(DIRECTORY, false);
		if (!FOLDER_SELECTION) {
			LinearLayout ll=(LinearLayout)findViewById(R.id.explorer_layout);
			Button bt = (Button) findViewById(R.id.explorer_bt);
			ll.removeView(bt);
		}

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parentDir, View view,
					int position, long id) 
			{
				String item = (String) ((TextView) view
						.findViewById(R.id.explorer_tv)).getText();
				if (item == "." || item == "..") {
					if (item == ".") {
						showDir((new File(curDir)).getAbsolutePath());
					} else {
						showDir((new File(curDir)).getParent());
					}
				} else {
					File path = new File(curDir + "/" + item);
					if (path.isDirectory()) {
						showDir(path.getAbsolutePath());
					} else {
						_FILENAME = path.getAbsolutePath();
						doAction();
					}
				}
			}
		});
		showDir(curDir);
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

	/** Get all the files included in the current directory.
		@param dir the path of the directory to be listed
		@return an array containing all the contents of the given dir.
	*/
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

	/** Eliminate all the elements in the given list, which correspond to
		files and not to directories.
		@param list the array containing the content of a directory
		@return a list where all the files have been filtered out (so that
			just the directories are present).
	*/
	public String[] filterFiles(String[] list) 
	{
		List<String> filtered = new ArrayList<String>();
		for (String f : list) {
			if (new File(curDir + "/" + f).isDirectory()) {
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
		curDir = dir;
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

	/** Perform a selection of a file.
	*/
	public void OnSelectClick(View view) 
	{
		_DIRECTORY = curDir;
		Intent data = new Intent();
		data.putExtra(FILENAME, _FILENAME);
		data.putExtra(DIRECTORY, _DIRECTORY);
		setResult(RESULT_OK, data);
		this.finish();
	}
}