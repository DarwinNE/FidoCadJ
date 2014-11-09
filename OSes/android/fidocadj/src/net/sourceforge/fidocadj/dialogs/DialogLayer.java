package net.sourceforge.fidocadj.dialogs;

import java.util.List;
import java.util.Vector;

import net.sourceforge.fidocadj.FidoEditor;
import net.sourceforge.fidocadj.R;
import android.app.Activity;
import android.app.Dialog;  
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;  
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;  
import android.view.ViewGroup;
import android.view.Window;   
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Switch;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;

import layers.LayerDesc;
import graphic.android.ColorAndroid;

/**
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

    Copyright 2014 by Dante Loi, Davide Bucci
  </pre>
 
  @author Dante Loi
  TODO: comment public methods
 
 */
public class DialogLayer extends DialogFragment 
{  
	private FidoEditor drawingPanel;
	private Button layerButton;
	private Dialog diag;
	
	/** Create the dialog where the user can choose the current layer.
	*/
	@Override  
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{  
		final Activity context = getActivity();
		final Dialog dialog = new Dialog(context);
		
		drawingPanel = (FidoEditor)context.findViewById(R.id.drawingPanel);
		layerButton= (Button)context.findViewById(R.id.layer);
		
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.open_file);
		
		final Vector<LayerDesc> layers = 
			drawingPanel.getDrawingModel().getLayers();
		
		// Here we create an adapter for the list. It is the custom class
		// defined in this very file.
		LayerAdapter customLayerAdapter = new LayerAdapter(
				context, 
				R.layout.layer_spinner_item, 
				layers);
		
		// We associate the adapter with the 
		ListView list = (ListView) dialog.findViewById(R.id.fileList);
		list.setAdapter(customLayerAdapter);
		list.setPadding(10, 10, 10, 10);
		diag=dialog;
		return dialog;
	}
	
	/** Inner class which provides a custom-made view which will be
		embedded in the list shown by the dialog.
	*/
	private class LayerAdapter extends ArrayAdapter<LayerDesc>
	{
	   	private final Context context;
	   	private final List<LayerDesc> layers;
	    	
	   	public LayerAdapter(Context context, int textViewResourceId, 
	   			List<LayerDesc> layers) 
	   	{
	   		super(context, textViewResourceId, layers);
	   		this.context = context;
	   		this.layers = layers;
	   	}

	   	@Override
	   	public View getView(int position, View convertView, 
	   		ViewGroup parent) 
	   	{
	   		return getCustomView(position, convertView, parent);
	    }
	    	
	   	@Override
	    public View getDropDownView(int position, 
							View convertView, ViewGroup parent)
		{
	    	return getCustomView(position, convertView, parent);
	    }
	        
	    /** Get a custom View showing the useful information about the
	      	layers: color and visibility.
	       	@param position the ordinal position in the list (layer number)
	       	@param convertView
	       	@param parent the parent component.
	    */
	    public View getCustomView(int position, 
								View convertView, ViewGroup parent) 
		{ 
        	LayoutInflater inflater = 
        		((Activity) context).getLayoutInflater();
    		View row = inflater.inflate(R.layout.layer_spinner_item, 
    			parent, false);
	    			
    		row.setBackgroundColor(Color.WHITE);
	    		
    		// Here we show a small rectangle of the layer color
    		SurfaceView sv = 
    			(SurfaceView) row.findViewById(R.id.surface_view);
    		sv.setBackgroundColor(layers.get(position).getColor().getRGB());
    		// A text element showing the layer name, in black if the
    		// layer is visible or in gray if it is not.
    		Button b = (Button) row.findViewById(R.id.name_item);
            b.setText(layers.get(position).getDescription());
            b.setOnClickListener(new specSelectCurrentLayer(drawingPanel,
            	layerButton, layers, diag, position));
            
            // We have a switch which determines whether a layer is visible
            // or not.
    		Switch ss = (Switch) row.findViewById(R.id.visibility_item);
    		
            ss.setChecked(layers.get(position).isVisible);
            ss.setOnCheckedChangeListener(
            	new specCheckedChangeListener(convertView,
            		layers, position)); 
            
            b.setTextColor(Color.BLACK);
            	
            b.setBackgroundColor(Color.WHITE);
            b.setTextSize(20);
            
            Button edit = (Button) row.findViewById(R.id.edit_item);
            edit.setOnClickListener(new View.OnClickListener()
            {
            	@Override
            	public void onClick(View v)
            	{
            		// show layer dialog
					DialogEditLayer dl = new DialogEditLayer();
					dl.show(getFragmentManager(), "");
            	}
            });
            
            return row;
        }
    }
	/** Handle a click on the selection button
    */
    private class specSelectCurrentLayer implements View.OnClickListener
    {
    	private FidoEditor drawingPanel;
    	private List<LayerDesc> layers;
   		private Button layerButton;
    	private int position;
    	private Dialog dialog;
    	
    	public specSelectCurrentLayer(FidoEditor dp, Button lb, 
    		List<LayerDesc> l, Dialog d, int pos)
    	{
    		drawingPanel=dp;
    		layers=l;
    		layerButton=lb;
    		dialog=d;
    		position=pos;
    	}
    	
    	/** Handle a click on the visibility switch
    	*/
    	@Override
		public void onClick(View v)
		{
    			drawingPanel.eea.currentLayer = position;
				layerButton.setBackgroundColor(
					((ColorAndroid)layers.get(position).getColor())
					.getColorAndroid());
				drawingPanel.invalidate();
				dialog.dismiss();	
		}
    }

    /** Handle the change of the visibility toggle switch.
    */
    private class specCheckedChangeListener implements OnCheckedChangeListener
    {
    	private int position;
    	private List<LayerDesc> layers;
    	private View parentView;
    	
    	public specCheckedChangeListener(View p, List<LayerDesc> l, int pos)
    	{
    		layers=l;
    		position=pos;
    		parentView=p;
    	}
    	
    	/** Handle a click on the visibility switch
    	*/
    	@Override
		public void onCheckedChanged(CompoundButton buttonView, 
			boolean isChecked)
		{
    			layers.get(position).setVisible(isChecked);
    			parentView.invalidate();
		}
    }
}  






