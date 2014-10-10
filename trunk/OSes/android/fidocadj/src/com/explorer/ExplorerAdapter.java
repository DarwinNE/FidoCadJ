package com.explorer;

import java.io.File;

import net.sourceforge.fidocadj.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
TODO: document class

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

    Copyright 2014 by Cronos80
</pre>
*/
public class ExplorerAdapter extends ArrayAdapter<String> 
{
	private final Context context;
	private final String[] items;
	private final String parentDir;

	/**
	 * Constructor
	 * */
	public ExplorerAdapter(Context context, String[] values, String parent) 
	{
		super(context, R.layout.explorer_list_item, values);
		this.context = context;
		this.items = values;
		this.parentDir = parent;
	}

	/**
	 * Manage the String[] data to populate the adapter
	 * 
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.explorer_list_item, parent,
				false);

		TextView tv = (TextView) rowView.findViewById(R.id.explorer_tv);
		tv.setText(items[position]);
		TextView tv1 = (TextView) rowView.findViewById(R.id.explorer_count_tv);
		tv1.setText("");
		ImageView iv = (ImageView) rowView.findViewById(R.id.explorer_iv);
		iv.setImageResource(R.drawable.file);
		File path = new File(parentDir + "/" + items[position]);
		if (path.isDirectory() || items[position] == "."
				|| items[position] == "..") {
			iv.setImageResource(R.drawable.iron_folder);

			File[] listDir = new File(path.getAbsolutePath()).listFiles();
			
			if (listDir != null && items[position] != "."
					&& items[position] != ".." && listDir.length>0) {				
				tv1.setText(""+listDir.length);
			}
		}
		return rowView;
	}
}
