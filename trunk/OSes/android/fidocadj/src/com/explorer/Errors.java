package com.explorer;

import java.io.FileNotFoundException;
import java.io.IOException;

import net.sourceforge.fidocadj.R;
import android.content.Context;
import android.widget.Toast;

/**Manage the error log.

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

    Copyright 2014 by Cronos80
</pre>
*/
public class Errors 
{

	public static Context context=IO.context;
	
	/** Called on {@link FileNotFoundException}.
	 * 
	 * @param e the {@link Exception}
	 */
	public static void FileNotFound(FileNotFoundException e)
	{
		Toast.makeText(context, 
			R.string.ERROR_FILE_NOT_FOUND,Toast.LENGTH_SHORT).show();
		e.printStackTrace();
	}
	
	/** Called on {@link IOException}.
	 * 
	 * @param e the {@link Exception}
	 */
	public static void IO(IOException e)
	{
		Toast.makeText(context, R.string.ERROR_IO,Toast.LENGTH_SHORT).show();
		e.printStackTrace();
	}
	
	/** Called on a general {@link Exception}.
	 * 
	 * @param e the {@link Exception}
	 */
	public static void Unexpected(Exception e)
	{
		Toast.makeText(context, 
			R.string.ERROR_UNEXPECTED,Toast.LENGTH_SHORT).show();
		e.printStackTrace();
	}
	
	/** Called on general {@link Exception} that has to be silent.
	 * 
	 * @param e the {@link Exception}
	 */
	public static void Silent(Exception e)
	{
		//e.printStackTrace();
	}
}