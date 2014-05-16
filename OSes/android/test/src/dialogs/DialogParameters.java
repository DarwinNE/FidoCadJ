package dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import 	android.widget.Toast;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import net.sourceforge.fidocadj.R;
import layers.LayerDesc;
import globals.Globals;
import graphic.FontG;
import graphic.PointG;
import net.sourceforge.fidocadj.FidoEditor;


/**
 * <pre>
 * 
 * Allows to create a generic dialog, capable of displaying and let the user
 * modify the parameters of a graphic primitive. The idea is that the dialog
 * uses a ParameterDescripion vector which contains all the elements, their
 * description as well as the type. Depending on the contents of the array, the
 * window will be created automatically.
 * 
 * </pre>
 *
 * @author Dante Loi
 *
 */

public class DialogParameters extends DialogFragment 
{
	private static Vector<ParameterDescription> vec;
	private static boolean strict;
	private static Vector<LayerDesc> layers;
	
	// Sizes
	private int fieldWidth;
    private int fieldHeight;
    private int textSize;
    
    //Dialog border
	private static final int BORDER = 30;
	
	//maximum strings' length
	private static final int MAX_LEN = 200;
	
	// Maximum number of user interface elements of the same type present
	// in the dialog window.
	private static final int MAX_ELEMENTS = 10;

	// Text box array and counter
	private EditText etv[];
	private int ec;	
	
	// Check box array and counter
	private CheckBox cbv[];
	private int cc; 

	// Spinner array and counter
	private Spinner spv[];
	private int sc; 
	
	private FidoEditor caller;
	
	/**
	 * Get a ParameterDescription vector describing the characteristics modified
	 * by the user.
	 * 
	 * @return a ParameterDescription vector describing each parameter.
	 */
	public Vector<ParameterDescription> getCharacteristics() {
		return vec;
	}	
	
	/**
	 * Makes the dialog and passes its arguments to it.
	 * 
	 * @return a new istance of DialogParameters.
	 */
	public static DialogParameters newInstance(Vector<ParameterDescription> vec,
			boolean strict, Vector<LayerDesc> layers, FidoEditor callback) 
	{
		DialogParameters dialog = new DialogParameters();	
		
		Bundle args = new Bundle();
        args.putSerializable("vec", vec);
        args.putBoolean("strict", strict);
        args.putSerializable("layers", layers);
        dialog.setArguments(args);
        dialog.setRetainInstance(true);
        dialog.caller=callback;
        
		return dialog;
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{  
    	super.onCreate(savedInstanceState);
    	
    	if(savedInstanceState != null){
    		vec = (Vector<ParameterDescription>) savedInstanceState
     			     				   .getSerializable("vec");
            layers = (Vector<LayerDesc>) savedInstanceState
									   .getSerializable("layers");
			strict = savedInstanceState.getBoolean("strict");
    	}
        else{
        	vec = (Vector<ParameterDescription>) getArguments()
								   .getSerializable("vec");
        	layers = (Vector<LayerDesc>) getArguments()
								   .getSerializable("layers");
        	strict = getArguments().getBoolean("strict");
        }
    	
    	final Activity context = getActivity();
		final Dialog dialog = new Dialog(context);
		
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
  		
    	LinearLayout vv = new LinearLayout(context){
    		
    		@Override
    	    public boolean onTouchEvent(MotionEvent event) {
    	        if( event.getAction() == MotionEvent.ACTION_DOWN) {
    	        	InputMethodManager imm = (InputMethodManager) context.getSystemService(
    	        		      Context.INPUT_METHOD_SERVICE);
    	        		imm.hideSoftInputFromWindow(getWindowToken(), SYSTEM_UI_LAYOUT_FLAGS);
    	        }
    	        return true;
    	    }
    	};
    	
    	vv.setOrientation(LinearLayout.VERTICAL);
    	vv.setBackgroundColor(getResources().
			getColor(R.color.background_white));
    	vv.setPadding(BORDER, BORDER, BORDER, BORDER);
    	
    	etv = new EditText[MAX_ELEMENTS];
		cbv = new CheckBox[MAX_ELEMENTS];
		spv = new Spinner[MAX_ELEMENTS];
    	
		ParameterDescription pd;

		ec = 0;
		cc = 0;
		sc = 0;
		
		//Setting of the dialog sizes.
		DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenDensity = metrics.densityDpi;
        int screenSize = getResources().getConfiguration().screenLayout & 
        	    Configuration.SCREENLAYOUT_SIZE_MASK;
        setSizeByScreen(screenSize, screenDensity);
		
		//Filter for the Integer EditText, 
		//allows to write only digit in the filtered fields.
		InputFilter filter = new InputFilter() 
		{ 
	        public CharSequence filter(CharSequence source, int start, int end, 
	        		Spanned dest, int dstart, int dend) { 
	                for (int i = start; i < end; i++) { 
	                        if (!Character.isDigit(source.charAt(i))) { 
	                                return ""; 
	                        } 
	                } 
	                return null; 
	        }
		};
		

		// We process all parameter passed. Depending on its type, a
		// corresponding interface element will be created.
		// A symmetrical operation is done when validating parameters.
		for (int ycount = 0; ycount < vec.size(); ++ycount) {

			pd = (ParameterDescription) vec.elementAt(ycount);
    	
			LinearLayout vh = new LinearLayout(context);
			vh.setGravity(Gravity.FILL_HORIZONTAL);
			vh.setGravity(Gravity.RIGHT);
			
			// We do not need to store label objects, since we do not need
			// to retrieve data from them.
			TextView lab = new TextView(context);
			lab.setTextColor(Color.BLACK);
			lab.setText(pd.description);
			lab.setPadding(0, 0, 10, 0);
			lab.setGravity(Gravity.CENTER);		
			lab.setTextSize(textSize);
			if (!(pd.parameter instanceof Boolean))
				vh.addView(lab);
			// Now, depending on the type of parameter we create interface
			// elements and we populate the dialog.

			if (pd.parameter instanceof PointG) {
				etv[ec] = new EditText(context);
				etv[ec].setTextColor(Color.BLACK);
				etv[ec].setBackgroundResource(R.drawable.field_background);
				Integer x = Integer.valueOf(((PointG) (pd.parameter)).x);
				etv[ec].setText(x.toString());
				etv[ec].setMaxWidth(MAX_LEN);
				etv[ec].setLayoutParams(new LayoutParams(fieldWidth/2,fieldHeight));
				etv[ec].setSingleLine();
				etv[ec].setFilters( new InputFilter[]{filter} );
				etv[ec].setTextSize(textSize);
				
				vh.addView(etv[ec++]);
				
				etv[ec] = new EditText(context);
				Integer y = Integer.valueOf(((PointG) (pd.parameter)).y);
				etv[ec].setText(y.toString());
				etv[ec].setTextColor(Color.BLACK);
				etv[ec].setBackgroundResource(R.drawable.field_background);
				etv[ec].setMaxWidth(MAX_LEN);
				etv[ec].setLayoutParams(new LayoutParams(fieldWidth/2,fieldHeight));
				etv[ec].setSingleLine();
				etv[ec].setFilters( new InputFilter[]{filter} );
				etv[ec].setTextSize(textSize);
				
				vh.addView(etv[ec++]);
			} else if (pd.parameter instanceof String) {
				etv[ec] = new EditText(context){
					@Override
		    	    public boolean onTouchEvent(MotionEvent event) {
		    	        if( event.getAction() == MotionEvent.ACTION_DOWN) {
		    	        	setInputType(InputType.TYPE_CLASS_TEXT);
		    	        	this.requestFocus();
		    				InputMethodManager mgr = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		    				mgr.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT);
		    	        }
		    	        return true;
		    	    }
				};
				etv[ec].setTextColor(Color.BLACK);
				etv[ec].setGravity(Gravity.FILL_HORIZONTAL|
					Gravity.CENTER_HORIZONTAL);
				etv[ec].setBackgroundResource(R.drawable.field_background);
				etv[ec].setText((String) (pd.parameter));
				etv[ec].setMaxWidth(MAX_LEN);
				etv[ec].setLayoutParams(new LayoutParams(fieldWidth,fieldHeight));
				etv[ec].setSingleLine();
				etv[ec].setTextSize(textSize);
				etv[ec].setInputType(InputType.TYPE_NULL);
				
				
				// If we have a String text field in the first position, its
				// contents should be evidenced, since it is supposed to be
				// the most important field (e.g. for the AdvText primitive)
				if (ycount == 0)
					etv[ec].selectAll();
				
				vh.addView(etv[ec++]);
			} else if (pd.parameter instanceof Boolean) {
				cbv[cc] = new CheckBox(context);
				cbv[cc].setText(pd.description);
				cbv[cc].setTextColor(Color.BLACK);
				cbv[cc].setLayoutParams(new LayoutParams(fieldWidth,fieldHeight));
				cbv[cc].setChecked(((Boolean) (pd.parameter)).booleanValue());
				cbv[cc].setTextSize(textSize);
				
				vh.setGravity(Gravity.RIGHT);
				vh.addView(cbv[cc++]);

			} else if (pd.parameter instanceof Integer) {
				etv[ec] = new EditText(context);
				etv[ec].setTextColor(Color.BLACK);
				etv[ec].setBackgroundResource(R.drawable.field_background);
				etv[ec].setText(((Integer) pd.parameter).toString());
				etv[ec].setMaxWidth(MAX_LEN);
				etv[ec].setLayoutParams(new LayoutParams(fieldWidth,fieldHeight));
				etv[ec].setSingleLine();
				etv[ec].setFilters( new InputFilter[]{filter} );
				etv[ec].setTextSize(textSize);
				
				vh.addView(etv[ec++]);
			} else if (pd.parameter instanceof Float) {

				etv[ec] = new EditText(context);
				etv[ec].setTextColor(Color.BLACK);
				etv[ec].setBackgroundResource(R.drawable.field_background);
				int dummy = java.lang.Math.round((Float) pd.parameter);
				etv[ec].setText("  "+dummy);
				etv[ec].setMaxWidth(MAX_LEN);
				etv[ec].setLayoutParams(new LayoutParams(fieldWidth,fieldHeight));
				etv[ec].setSingleLine();
				etv[ec].setTextSize(textSize);
				
				vh.addView(etv[ec++]);
			} else if (pd.parameter instanceof FontG) {
				spv[sc] = new Spinner(context);
				
				String[] s = {"Normal","Italic","Bold"};			
			
				ArrayAdapter<String> adapter = new ArrayAdapter<String> (
						context, android.R.layout.simple_spinner_item , s);
				spv[sc].setAdapter(adapter);
				spv[sc].setBackgroundResource(R.drawable.field_background);
				spv[sc].setLayoutParams(new LayoutParams(fieldWidth,fieldHeight));
				
				for (int i = 0; i < s.length; ++i) {
					if (s[i].equals(((FontG) pd.parameter).getFamily()))
						spv[sc].setSelection(i);
					else 
						spv[sc].setSelection(0);
				}
				vh.addView(spv[sc++]);
				
			} else if (pd.parameter instanceof LayerInfo) {
				spv[sc] = new Spinner(context);
				 
				LayerSpinnerAdapter adapter = new LayerSpinnerAdapter(context, 
						R.layout.layer_spinner_item, layers);
				
				spv[sc].setAdapter(adapter);
				spv[sc].setBackgroundResource(R.drawable.field_background);
				spv[sc].setSelection(((LayerInfo) pd.parameter).layer);
				spv[sc].setLayoutParams(new LayoutParams(fieldWidth,fieldHeight));
				vh.addView(spv[sc++]);
		    
			} else if (pd.parameter instanceof ArrowInfo) {
				spv[sc] = new Spinner(context);
				
				List<ArrowInfo> l = new ArrayList<ArrowInfo>();
				l.add(new ArrowInfo(0));
				l.add(new ArrowInfo(1));
				l.add(new ArrowInfo(2));
				l.add(new ArrowInfo(3));
				
				ArrowSpinnerAdapter adapter = new ArrowSpinnerAdapter(
						context, R.layout.arrow_spinner_item, l);
				
				spv[sc].setAdapter(adapter);
				spv[sc].setBackgroundResource(R.drawable.field_background);
				spv[sc].setSelection(((ArrowInfo) pd.parameter).style);
				spv[sc].setLayoutParams(new LayoutParams(fieldWidth,fieldHeight));
				vh.addView(spv[sc++]);
				
			} else if (pd.parameter instanceof DashInfo) {
				spv[sc] = new Spinner(context);
				
				List<DashInfo> l = new ArrayList<DashInfo>();
				for (int k = 0; k < Globals.dashNumber; ++k)
					l.add(new DashInfo(k));
				//TODO: customize the Arrayadapter.
				ArrayAdapter<DashInfo> adapter = new ArrayAdapter<DashInfo>(
						context, android.R.layout.simple_spinner_item, l);
				
				spv[sc].setAdapter(adapter);
				spv[sc].setBackgroundResource(R.drawable.field_background);
				spv[sc].setSelection(((DashInfo) pd.parameter).style);
				spv[sc].setLayoutParams(new LayoutParams(fieldWidth,fieldHeight));
				
				vh.addView(spv[sc++]);
			}
			vv.addView(vh);
		}
    	
		LinearLayout buttonView = new LinearLayout(context);
		buttonView.setGravity(Gravity.RIGHT);
		buttonView.setOrientation(LinearLayout.HORIZONTAL);
		
		Button ok = new Button(context);  
		ok.setText(getResources().getText(R.string.Ok_btn));
		ok.setOnClickListener(new OnClickListener() 
		{  
			@Override  
			public void onClick(View buttonView) 
			{  
				try {
					int ycount;
					ParameterDescription pd;
					
					ec = 0;
					cc = 0;
					sc = 0;

					// Here we read all the contents of the interface and we
					// update the contents of the parameter description array.

					for (ycount = 0; ycount < vec.size(); ++ycount) {

						pd = (ParameterDescription) vec.elementAt(ycount);

						if (pd.parameter instanceof Point) {
							((Point) (pd.parameter)).x = Integer
									.parseInt(etv[ec++].getText().toString());
							((Point) (pd.parameter)).y = Integer
									.parseInt(etv[ec++].getText().toString());
						} else if (pd.parameter instanceof String) {
							pd.parameter = etv[ec++].getText().toString();
						} else if (pd.parameter instanceof Boolean) {
							android.util.Log.e("fidocadj", 
								"value:"+Boolean.valueOf(
								cbv[cc].isChecked()));
							pd.parameter = Boolean.valueOf(
								cbv[cc++].isChecked());
							
						} else if (pd.parameter instanceof Integer) {
							pd.parameter = Integer.valueOf(Integer
									.parseInt(etv[ec++].getText().toString()));
						} else if (pd.parameter instanceof Float) {
							pd.parameter = Float.valueOf(Float
									.parseFloat(etv[ec++].getText().toString()));
						} else if (pd.parameter instanceof FontG) {
							pd.parameter = new FontG((String) spv[sc++]
									.getSelectedItem());
						} else if (pd.parameter instanceof LayerInfo) {
							pd.parameter = new LayerInfo((Integer) spv[sc++]
									.getSelectedItemPosition());
						} else if (pd.parameter instanceof ArrowInfo) {
							pd.parameter = new ArrowInfo((Integer) spv[sc++]
									.getSelectedItemPosition());
						} else if (pd.parameter instanceof DashInfo) {
							pd.parameter = new DashInfo((Integer) spv[sc++]
									.getSelectedItemPosition());
						}
						else {}
					}
				} catch (NumberFormatException E) {
					// Error detected. Probably, the user has entered an
					// invalid string when FidoCadJ was expecting a numerical
					// input.

					Toast t = new Toast(context);
					t.setText(Globals.messages.getString("Format_invalid"));
					t.show();
				}
				caller.saveCharacteristics(vec);
				dialog.dismiss();
			}
		});
		
		buttonView.addView(ok);
		
		Button cancel = new Button(context);  
		cancel.setText(getResources().getText(R.string.Cancel_btn));
		cancel.setOnClickListener(new OnClickListener() 
		{  
			@Override  
			public void onClick(View buttonView) 
			{  
				dialog.dismiss();
			}  
		});
		buttonView.addView(cancel);
		
		vv.addView(buttonView);
		
		dialog.setContentView((View)vv);  
		
		return dialog;
	}
	
	public void onDismiss(Bundle savedInstanceState)
	{
		savedInstanceState.putSerializable("vec", vec);
		savedInstanceState.putBoolean("strict", strict);
		savedInstanceState.putSerializable("layers", layers);
	}
	
	 @Override
	 public void onDestroyView() 
	 {
	     if (getDialog() != null && getRetainInstance())
	         getDialog().setDismissMessage(null);
	     super.onDestroyView();
	 }
	
	//Customized item for the layout spinner.
    private class LayerSpinnerAdapter extends ArrayAdapter<LayerDesc>
    {
    	private Context context;
    	private List<LayerDesc> layers;
    	
    	public LayerSpinnerAdapter(Context context, int textViewResourceId, 
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
            v.setTextSize(textSize);
            
            return row;
        }
    }
    
  //Customized item for the arrow spinner.
    private class ArrowSpinnerAdapter extends ArrayAdapter<ArrowInfo>
    {
    	private Context context;
    	private List<ArrowInfo> info;
    	
    	public ArrowSpinnerAdapter(Context context, int textViewResourceId, 
    			List<ArrowInfo> info) 
    	{
    		super(context, textViewResourceId, info);
    		this.context = context;
    		this.info = info;
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
    		LinearLayout row = (LinearLayout)inflater.
				inflate(R.layout.arrow_spinner_item, parent, false);
        	CellArrow ca = new CellArrow(context);
        	ca.setStyle(info.get(position));
    		row.addView(ca);
    		
            return row;
        }
    }
    
	/** Adapts the various dialog's dimension at the screen density and size.
	 * 
	 * @param size, the physical screen size of the device.
	 * @param density, the screen resolution of the device.
	 */
    private void setSizeByScreen( int size, int density )
    {   
    	//TODO: to manage other devices.
        if( size == Configuration.SCREENLAYOUT_SIZE_SMALL ) {
        	switch(density) {
        		case DisplayMetrics.DENSITY_LOW:
        			break;
        		case DisplayMetrics.DENSITY_MEDIUM:
        			break;
        		case DisplayMetrics.DENSITY_HIGH:
        			break;
        		case DisplayMetrics.DENSITY_TV:
        			break;
        		case DisplayMetrics.DENSITY_XHIGH:
        			break;
        		case DisplayMetrics.DENSITY_XXHIGH:
        			break;
        		case DisplayMetrics.DENSITY_XXXHIGH:
        			break;
        		default:
        			break;
        	}
        } else if( size == Configuration.SCREENLAYOUT_SIZE_NORMAL ) {
        	switch(density) {
    			case DisplayMetrics.DENSITY_LOW:
    				break;
    			case DisplayMetrics.DENSITY_MEDIUM:
    				break;
    			case DisplayMetrics.DENSITY_HIGH:
    				break;
    			case DisplayMetrics.DENSITY_TV:
    				break;
    			case DisplayMetrics.DENSITY_XHIGH:
    				break;
    			case DisplayMetrics.DENSITY_XXHIGH:
    				//tested with nexus 5
    				fieldWidth = 400;
    				fieldHeight = 80;
    				textSize = 10;
    				break;
    			case DisplayMetrics.DENSITY_XXXHIGH:
    				break;
    			default:
    				break;
        	}

        } else if( size == Configuration.SCREENLAYOUT_SIZE_LARGE ) {
        	switch(density) {
    			case DisplayMetrics.DENSITY_LOW:
    				break;
    			case DisplayMetrics.DENSITY_MEDIUM:
    				break;
    			case DisplayMetrics.DENSITY_HIGH:
    				break;
    			case DisplayMetrics.DENSITY_TV:
            		//tested with nexus7 800x1280
            		fieldWidth = 300;
            		fieldHeight = 50;
            		textSize = 16;
    				break;
    			case DisplayMetrics.DENSITY_XHIGH:
            		//tested with nexus7 1200x1920
            		fieldWidth = 450;
            		fieldHeight = 80;
            		textSize = 18;
    				break;
    			case DisplayMetrics.DENSITY_XXHIGH:
    				break;
    			case DisplayMetrics.DENSITY_XXXHIGH:
    				break;
    			default:
    				break;
        	}	
        } else if( size == Configuration.SCREENLAYOUT_SIZE_XLARGE ) {
        	switch(density) {
    			case DisplayMetrics.DENSITY_LOW:
    				break;
    			case DisplayMetrics.DENSITY_MEDIUM:
    				break;
    			case DisplayMetrics.DENSITY_HIGH:
    				break;
    			case DisplayMetrics.DENSITY_TV:
    				break;
    			case DisplayMetrics.DENSITY_XHIGH:
    				break;
    			case DisplayMetrics.DENSITY_XXHIGH:
    				break;
    			case DisplayMetrics.DENSITY_XXXHIGH:
    				break;
    			default:
    				break;
        	}
        }	
    }

}




