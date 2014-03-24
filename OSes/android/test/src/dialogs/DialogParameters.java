package dialogs;

import java.util.Vector;

import layers.LayerDesc;
import net.sourceforge.fidocadj.R;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;


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
		
		//instead of Bundle because there'are problem with vector 
		dialog.vec = vec;
		dialog.strict = strict;
		dialog.layers = layers;
		
		return dialog;
	}



	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{  
    	super.onCreate(savedInstanceState);
    	
		final Activity context = getActivity();
		final Dialog dialog = new Dialog(context);
		
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    	
    	LinearLayout v = new LinearLayout(context);
    	
    	v.setMinimumHeight(getResources().
			getDimensionPixelSize(R.dimen.large_dialog_height));
    	v.setMinimumWidth(getResources().
			getDimensionPixelSize(R.dimen.large_dialog_height));
    	v.setBackgroundColor(getResources().
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
		int ycount = 0;
		for (ycount = 0; ycount < vec.size(); ++ycount) {
			if (ycount > MAX)
				break;

			pd = (ParameterDescription) vec.elementAt(ycount);
    	
			// We do not need to store label objects, since we do not need
			// to retrieve data from them.

			lab = new TextView(context);
			lab.setText(pd.description);
			lab.setMinimumHeight(100);
			lab.setMinimumWidth(100);

			
			if (!(pd.parameter instanceof Boolean))
				v.addView(lab);
			// Now, depending on the type of parameter we create interface
			// elements and we populate the dialog.

			if (pd.parameter instanceof Point) {
				etv[ec] = new EditText(context);
				etv[ec].setText("" + ((Point) (pd.parameter)).x);

				v.addView(etv[ec++]);

				etv[ec] = new EditText(context);
				etv[ec].setText("" + ((Point) (pd.parameter)).y);
				
				v.addView(etv[ec++]);
			} else if (pd.parameter instanceof String) {
				etv[ec] = new EditText(context);
				etv[ec].setText((String) (pd.parameter));
				// If we have a String text field in the first position, its
				// contents should be evidenced, since it is supposed to be
				// the most important field (e.g. for the AdvText primitive)
				if (ycount == 0)
					etv[ec].selectAll();
				
				v.addView(etv[ec++]);
			} else if (pd.parameter instanceof Boolean) {
				cbv[cc] = new CheckBox(context);
				cbv[cc].setText(pd.description);
				cbv[cc].setSelected(((Boolean) (pd.parameter)).booleanValue());
				
				v.addView(cbv[cc++]);

			} else if (pd.parameter instanceof Integer) {
				etv[ec] = new EditText(context);
				etv[ec].setText(((Integer) pd.parameter).toString());
				
				v.addView(etv[ec++]);
			} else if (pd.parameter instanceof Float) {
				// TODO. 
				// WARNING: (DB) this is supposed to be temporary. In fact, I 
				// am planning to upgrade some of the parameters from int
				// to float. But for a few months, the users should not be
				// aware of that, even if the internal representation is 
				// slowing being adapted.
				etv[ec] = new EditText(context);
				int dummy = java.lang.Math.round((Float) pd.parameter);
				etv[ec].setText(""+dummy);
				
				v.addView(etv[ec++]);
			} 
			
		}
    	
		LinearLayout buttonView = new LinearLayout(context);
		buttonView.setOrientation(LinearLayout.HORIZONTAL);
		
		Button ok = new Button(context);  
		ok.setText(getResources().getText(R.string.Ok_btn));
		ok.setOnClickListener(new OnClickListener() 
		{  
			@Override  
			public void onClick(View buttonView) 
			{  
				//ok action
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
		
		v.addView(buttonView);
		
		dialog.setContentView((View)v);  
		return dialog;
	}
}
