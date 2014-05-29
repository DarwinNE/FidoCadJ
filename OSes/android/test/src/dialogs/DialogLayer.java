package dialogs;

import java.util.List;
import java.util.Vector;

import layers.LayerDesc;
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

/**
 * <pre>
 * </pre>
 *
 * @author Dante Loi
 *
 */
public class DialogLayer extends DialogFragment 
{  
	private  FidoEditor drawingPanel;
	
	@Override  
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{  
		final Activity context = getActivity();
		final Dialog dialog = new Dialog(context);
		
		drawingPanel = (FidoEditor)context.findViewById(R.id.drawingPanel);
		
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.open_file);
		
		Vector<LayerDesc> layers = drawingPanel.getDrawingModel().getLayers();
		
		LayerAdapter adapter = new LayerAdapter(
				context, 
				R.layout.layer_spinner_item, 
				layers);
		
		ListView list = (ListView) dialog.findViewById(R.id.fileList);
		list.setAdapter(adapter);
		list.setPadding(10, 10, 10, 10);
		
		OnItemClickListener clickListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
			drawingPanel.eea.currentLayer = position;
				
				dialog.dismiss();		
			}
		};
		list.setOnItemClickListener(clickListener);

		return dialog;
	}
	
	   private class LayerAdapter extends ArrayAdapter<LayerDesc>
	    {
	    	private Context context;
	    	private List<LayerDesc> layers;
	    	
	    	public LayerAdapter(Context context, int textViewResourceId, 
	    			List<LayerDesc> layers) 
	    	{
	    		super(context, textViewResourceId, layers);
	    		this.context = context;
	    		this.layers = layers;
	    	}

	    	@Override
	    	public View getView(int position, View convertView, ViewGroup parent) 
	    	{
	    		return getCustomView(position, convertView, parent);
	        }
	    	
	    	@Override
	        public View getDropDownView(int position, 
									View convertView, ViewGroup parent){
	        	return getCustomView(position, convertView, parent);
	        }
	        
	        public View getCustomView(int position, 
									View convertView, ViewGroup parent) { 
	        	LayoutInflater inflater = ((Activity) context).getLayoutInflater();
	    		View row = inflater.inflate(R.layout.layer_spinner_item, parent,
	    						false);
	    		row.setBackgroundColor(Color.WHITE);
	    		
	    		SurfaceView sv = (SurfaceView) row.findViewById(R.id.surface_view);
	    		sv.setBackgroundColor(layers.get(position).getColor().getRGB());
	    		
	    		TextView v = (TextView) row.findViewById(R.id.name_item);
	            v.setText(layers.get(position).getDescription());
	            v.setTextColor(Color.BLACK);
	            v.setBackgroundColor(Color.WHITE);
	            v.setTextSize(20);
	            
	            return row;
	        }
	    }
}  






