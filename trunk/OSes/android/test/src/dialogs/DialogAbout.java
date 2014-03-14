package dialogs;

import fr.davbucci.test.R;
import android.app.Activity;
import android.app.Dialog;  
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;  
import android.view.View;  
import android.view.View.OnClickListener;  
import android.view.Window;   
import android.widget.Button;  
import android.widget.TextView;
import globals.*;
  

/**
 * <pre>
 * Shows a rather standard "About" dialog. Nothing more exotic than showing the 
 * nice icon of the program, its name as well as three lines of description.
 * </pre>
 *
 * @author Dante Loi
 *
 */
public class DialogAbout extends DialogFragment 
{  
	private final String url = "https://sourceforge.net/projects/fidocadj/";
	
	@Override  
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{  
		final Activity context = getActivity();
		final Dialog dialog = new Dialog(context);
		
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_about);  
		
		TextView version = (TextView) dialog.findViewById(R.id.version);
		version.setText(Globals.version);
		
		dialog.show();  
  
		Button link = (Button) dialog.findViewById(R.id.link_button);  
		link.setText(url);
		link.setOnClickListener(new OnClickListener() 
		{  
			@Override  
			public void onClick(View v) 
			{  
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        		Intent chooser = Intent.createChooser(intent, 
        			"Choose the browser:");
        		startActivity(chooser);
			}  
		});
		
		return dialog;
	}
}  

