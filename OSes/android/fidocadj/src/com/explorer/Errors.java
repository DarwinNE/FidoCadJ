package com.explorer;

import java.io.FileNotFoundException;
import java.io.IOException;

import net.sourceforge.fidocadj.R;
import android.content.Context;
import android.widget.Toast;

/**Manage the error log.
*
*/
public class Errors {

	public static Context context=IO.context;
	
	/** Called on {@link FileNotFoundException}.
	 * 
	 * @param e the {@link Exception}
	 */
	public static void FileNotFound(FileNotFoundException e){
		Toast.makeText(context, R.string.ERROR_FILE_NOT_FOUND,Toast.LENGTH_SHORT).show();
		e.printStackTrace();
	}
	
	/** Called on {@link IOException}.
	 * 
	 * @param e the {@link Exception}
	 */
	public static void IO(IOException e){
		Toast.makeText(context, R.string.ERROR_IO,Toast.LENGTH_SHORT).show();
		e.printStackTrace();
	}
	
	/** Called on a general {@link Exception}.
	 * 
	 * @param e the {@link Exception}
	 */
	public static void Unexpected(Exception e){
		Toast.makeText(context, R.string.ERROR_UNEXPECTED,Toast.LENGTH_SHORT).show();
		e.printStackTrace();
	}
	
	/** Called on general {@link Exception} that has to be silent.
	 * 
	 * @param e the {@link Exception}
	 */
	public static void Silent(Exception e){
		//e.printStackTrace();
	}
}