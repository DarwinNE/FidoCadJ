package dialogs;

import java.io.File;
import net.sourceforge.fidocadj.R;
import android.app.Activity;
import android.app.Dialog;  
import android.app.DialogFragment;
import android.os.Bundle;  
import android.view.View;  
import android.view.Window;   
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * <pre>
 * </pre>
 *
 * @author Dante Loi
 *
 */
public class DialogOpenFile extends DialogFragment 
{  
	@Override  
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{  
		final Activity context = getActivity();
		final Dialog dialog = new Dialog(context);
		
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.open_file);
		
		File dir = context.getFilesDir();
		String[] files = dir.list();
		
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                 context, 
                 R.layout.list_item, R.id.lblListItem,
                 files );
		
		ListView list = (ListView) dialog.findViewById(R.id.fileList);
		list.setAdapter(arrayAdapter);
		
		OnItemClickListener clickListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//TODO: open the file
				
			}
		};
		list.setOnItemClickListener(clickListener);

		
		return dialog;
	}
}  



