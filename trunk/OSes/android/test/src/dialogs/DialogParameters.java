package dialogs;

import java.util.Vector;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import net.sourceforge.fidocadj.R;
import layers.LayerDesc;
import graphic.FontG;;


public class DialogParameters extends DialogFragment 
{
	private static Vector<ParameterDescription> vec;
	private static boolean strict;
	private static Vector<LayerDesc> layers;
	
	private static final int MAX = 20;

	// Maximum number of user interface elements of the same type present
	// in the dialog window.
	private static final int MAX_ELEMENTS = 100;

	public boolean active; // true if the user selected Ok

	// Text box array and counter
	private EditText etv[];
	private int ec;	
	
	// Check box array and counter
	private CheckBox cbv[];
	private int cc; // 

	private Spinner spv[];
	private int sc; 
	
	
	public static DialogParameters newInstance(Vector<ParameterDescription> vec,
			boolean strict, Vector<LayerDesc> layers) 
	{
		DialogParameters dialog = new DialogParameters();
		
		Bundle args = new Bundle();
        args.putSerializable("vec", vec);
        args.putBoolean("strict", strict);
        args.putSerializable("layers", layers);
        dialog.setArguments(args);
		
		return dialog;
	}

	public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        
        vec = (Vector<ParameterDescription>) getArguments().getSerializable("vec");
        strict = getArguments().getBoolean("strict");
        layers = (Vector<LayerDesc>) getArguments().getSerializable("layers");
	}

	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{  
    	super.onCreate(savedInstanceState);
    	
    	if(savedInstanceState != null){
    		vec = (Vector<ParameterDescription>) savedInstanceState.getSerializable("vec");
            strict = savedInstanceState.getBoolean("strict");
            layers = (Vector<LayerDesc>) savedInstanceState.getSerializable("layers");
    	}
    	
    	
		final Activity context = getActivity();
		final Dialog dialog = new Dialog(context);
		
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    	
    	LinearLayout vv = new LinearLayout(context);
    	
    	vv.setOrientation(LinearLayout.VERTICAL);
    	vv.setMinimumHeight(getResources().
			getDimensionPixelSize(R.dimen.large_dialog_height));
    	vv.setMinimumWidth(getResources().
			getDimensionPixelSize(R.dimen.large_dialog_width));
    	vv.setBackgroundColor(getResources().
			getColor(R.color.background_white));
    	
    	etv = new EditText[MAX_ELEMENTS];
		cbv = new CheckBox[MAX_ELEMENTS];
		spv = new Spinner[MAX_ELEMENTS];
    	
		ParameterDescription pd;

		TextView lab;

		ec = 0;
		cc = 0;
		sc = 0;

		// We process all parameter passed. Depending on its type, a
		// corresponding interface element will be created.
		// A symmetrical operation is done when validating parameters.
		for (int ycount = 0; ycount < vec.size(); ++ycount) {
			//if (ycount > MAX)
				//break;

			pd = (ParameterDescription) vec.elementAt(ycount);
    	
			// We do not need to store label objects, since we do not need
			// to retrieve data from them.

			lab = new TextView(context);
			lab.setTextColor(Color.BLACK);
			lab.setText(pd.description);
			//lab.setMinimumHeight(100);
			//lab.setMinimumWidth(100);
			
			LinearLayout vh = new LinearLayout(context);
			vh.setGravity(Gravity.FILL_HORIZONTAL);
			vv.addView(vh);
			
			if (!(pd.parameter instanceof Boolean))
				vh.addView(lab);
			// Now, depending on the type of parameter we create interface
			// elements and we populate the dialog.

			if (pd.parameter instanceof Point) {
				etv[ec] = new EditText(context);
				etv[ec].setTextColor(Color.BLACK);
				etv[ec].setText("" + ((Point) (pd.parameter)).x);

				vh.addView(etv[ec++]);

				etv[ec] = new EditText(context);
				etv[ec].setText("" + ((Point) (pd.parameter)).y);
				etv[ec].setTextColor(Color.BLACK);
				
				vv.addView(etv[ec++]);
			} else if (pd.parameter instanceof String) {
				etv[ec] = new EditText(context);
				etv[ec].setTextColor(Color.BLACK);
				etv[ec].setText((String) (pd.parameter));
				// If we have a String text field in the first position, its
				// contents should be evidenced, since it is supposed to be
				// the most important field (e.g. for the AdvText primitive)
				//if (ycount == 0)
					//etv[ec].selectAll();
				
				vh.addView(etv[ec++]);
			} else if (pd.parameter instanceof Boolean) {
				cbv[cc] = new CheckBox(context);
				cbv[cc].setText(pd.description);
				cbv[cc].setSelected(((Boolean) (pd.parameter)).booleanValue());
				
				vh.addView(cbv[cc++]);

			} else if (pd.parameter instanceof Integer) {
				etv[ec] = new EditText(context);
				etv[ec].setTextColor(Color.BLACK);
				etv[ec].setText(((Integer) pd.parameter).toString());
				
				vh.addView(etv[ec++]);
			} else if (pd.parameter instanceof Float) {
				// TODO. 
				// WARNING: (DB) this is supposed to be temporary. In fact, I 
				// am planning to upgrade some of the parameters from int
				// to float. But for a few months, the users should not be
				// aware of that, even if the internal representation is 
				// slowing being adapted.
				etv[ec] = new EditText(context);
				etv[ec].setTextColor(Color.BLACK);
				int dummy = java.lang.Math.round((Float) pd.parameter);
				etv[ec].setText(""+dummy);
				
				vh.addView(etv[ec++]);
			} else if (pd.parameter instanceof FontG) {
				spv[sc] = new Spinner(context);
				
				/*GraphicsEnvironment gE;
				gE = GraphicsEnvironment.getLocalGraphicsEnvironment();
				String[] s = gE.getAvailableFontFamilyNames();
				
				for (int i = 0; i < s.length; ++i) {
					spv[sc].addItem(s[i])
					if (s[i].equals(((FontG) pd.parameter).getFamily()))
						spv[sc].setSelectedIndex(i);
				}*/
				vh.addView(spv[sc++]);
			} else if (pd.parameter instanceof LayerInfo) {
				spv[sc] = new Spinner(context);
				

		    }
			
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
						if (ycount > MAX)
							break;
						pd = (ParameterDescription) vec.elementAt(ycount);

						if (pd.parameter instanceof Point) {
							((Point) (pd.parameter)).x = Integer
									.parseInt(etv[ec++].getText().toString());
							((Point) (pd.parameter)).y = Integer
									.parseInt(etv[ec++].getText().toString());
						} else if (pd.parameter instanceof String) {
							pd.parameter = etv[ec++].getText().toString();
						} else if (pd.parameter instanceof Boolean) {
							pd.parameter = Boolean.valueOf(
								cbv[cc++].isSelected());
						} else if (pd.parameter instanceof Integer) {
							pd.parameter = Integer.valueOf(Integer
									.parseInt(etv[ec++].getText().toString()));
						} else if (pd.parameter instanceof Float) {
							pd.parameter = Float.valueOf(Float
									.parseFloat(etv[ec++].getText().toString()));
						} /*else if (pd.parameter instanceof FontG) {
							pd.parameter = new FontG((String) spv[sc++]
									.getSelectedItem());
						} else if (pd.parameter instanceof LayerInfo) {
							pd.parameter = new LayerInfo((Integer) spv[sc++]
									.getSelectedItem());
						} else if (pd.parameter instanceof ArrowInfo) {
							pd.parameter = new ArrowInfo((Integer) spv[sc++]
									.getSelectedItem());
						} else if (pd.parameter instanceof DashInfo) {
							pd.parameter = new DashInfo((Integer) spv[sc++]
									.getSelectedItem());
						}*/
						else {}
					}
				} catch (NumberFormatException E) {
					// Error detected. Probably, the user has entered an
					// invalid string when FidoCadJ was expecting a numerical
					// input.

					/*JOptionPane.showMessageDialog(null,
							Globals.messages.getString("Format_invalid"), "",
							JOptionPane.INFORMATION_MESSAGE);
					return;*/
				}

				active = true;
				
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
				//cancel action
			}  
		});
		buttonView.addView(cancel);
		
		vv.addView(buttonView);
		
		dialog.setContentView((View)vv);  
		return dialog;
	}
	
	/*public void onDismiss(Bundle savedInstanceState)
	{
		savedInstanceState.putSerializable("vec", vec);
		savedInstanceState.putBoolean("strict", strict);
		savedInstanceState.putSerializable("layers", layers);
	}*/
}
