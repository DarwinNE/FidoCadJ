package dialogs;

import java.io.FileOutputStream;
import java.io.IOException;

import net.sourceforge.fidocadj.FidoEditor;
import net.sourceforge.fidocadj.R;
import android.app.Activity;
import android.app.Dialog;  
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;  
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
	private FidoEditor drawingPanel;
	
	@Override  
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{  
		final Activity context = getActivity();
		final Dialog dialog = new Dialog(context);
		
		drawingPanel = (FidoEditor)context
				.findViewById(R.id.drawingPanel);
		circuit = drawingPanel.getText();
		
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
				editName.setPadding(10, 0, 0, 0);

				String fileName = editName.getText().toString()+".fdc";
				drawingPanel.getParserActions().openFileName = fileName;
				
				try {
				  outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
				  outputStream.write(circuit.getBytes());
				  outputStream.close();
				} catch (IOException e) {
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



