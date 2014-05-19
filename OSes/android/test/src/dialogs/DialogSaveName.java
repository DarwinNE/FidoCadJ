package dialogs;

import java.io.File;
import java.io.FileOutputStream;

import net.sourceforge.fidocadj.R;
import android.app.Activity;
import android.app.Dialog;  
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;  
import android.util.Log;
import android.view.View;  
import android.view.View.OnClickListener;  
import android.view.Window;   
import android.widget.Button;  
import android.widget.EditText;

/**
 * <pre>
 * </pre>
 *
 * @author Dante Loi
 *
 */
public class DialogSaveName extends DialogFragment 
{  
	private String circuit;
	
	public static DialogSaveName newIstance(String circuit)
	{
		DialogSaveName dialog = new DialogSaveName();
		Bundle args = new Bundle();
		args.putString("circuit", circuit);
		dialog.setArguments(args);
		
		return dialog;
	}
	
	@Override  
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{  
		final Activity context = getActivity();
		final Dialog dialog = new Dialog(context);
		
		if( savedInstanceState != null )
			circuit = savedInstanceState.getString("circuit");
		else 
			circuit = getArguments().getString("circuit").toString();
		
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.save_with_name);  
  
		Button save = (Button) dialog.findViewById(R.id.save);  
		save.setOnClickListener( new OnClickListener() 
		{  
			@Override  
			public void onClick(View v) 
			{  
				FileOutputStream outputStream;
				EditText editName = (EditText) dialog.findViewById(R.id.editName);
				String fileName = editName.getText().toString()+".fdc";
				File file = new File(context.getFilesDir(),fileName);
				
				Log.i("Dante", context.getFilesDir().toString() + fileName);
				
				try {
				  outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
				  outputStream.write(circuit.getBytes());
				  outputStream.close();
				} catch (Exception e) {
				  e.printStackTrace();
				}
				dialog.dismiss();
			}  
		});
		
		Button cancel = (Button) dialog.findViewById(R.id.cancel);  
		cancel.setOnClickListener( new OnClickListener() 
		{  
			@Override  
			public void onClick(View v) 
			{  
				dialog.dismiss();
			}  
		});
		
		return dialog;
	}
}  


