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

public class ExplorerAdapter extends ArrayAdapter<String> {
	private final Context context;
	private final String[] items;
	private final String parentDir;

	/**
	 * Constructor
	 * */
	public ExplorerAdapter(Context context, String[] values, String parent) {
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
	public View getView(int position, View convertView, ViewGroup parent) {
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
