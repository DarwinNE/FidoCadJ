package com.explorer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.os.Environment;

/**
 * Provides static functions for reading/writing file.
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
public class IO 
{
	public static String rootDir = android.os.Environment
			.getExternalStorageDirectory().getAbsolutePath();
	public static Context context;

	/**
	 * Read a file and return a {@link String} containing the text in the file.
	 * 
	 * @param fileName
	 *            the file to open.
	 * @return the {@link String} containing the text read.
	 */
	public static String readFile(String fileName) 
	{
		FileInputStream fis = null;
		InputStreamReader isr = null;
		char[] inputBuffer = new char[255];
		String data = "";
		try {
			fis = context.openFileInput(fileName);
		} catch (FileNotFoundException e) {
			Errors.FileNotFound(e);
			return data;
		}
		isr = new InputStreamReader(fis);
		try {
			isr.read(inputBuffer);
			data = new String(inputBuffer);
			// Toast.makeText(context,
			// "Settings read",Toast.LENGTH_SHORT).show();
			fis.close();
		} catch (IOException e) {
			Errors.IO(e);
		}

		return data.trim();
	}

	/**
	 * Read a file and return a {@link String} containing the text in the file.
	 * 
	 * @param fileName
	 *            the file to open.
	 * @return the {@link String} containing the text read.
	 */
	public static String readFileFromSD(String fileName) 
	{
		FileInputStream fis = null;
		String data = "";
		byte[] buf = new byte[1024];
		try {
			fis = new FileInputStream(new File(fileName));
			while (fis.read(buf) != -1) {
				data += new String(buf);
				buf = new byte[1024];
			}
			fis.close();
		} catch (FileNotFoundException e) {
			Errors.FileNotFound(e);
			return null;
		} catch (IOException e) {
			Errors.IO(e);
		}
		return data.trim();
	}

	/**
	 * Write a {@link String} to a file.
	 * 
	 * @param fileName
	 *            the file to write.
	 * @param string
	 *            the {@link String} containing the text to write.
	 */
	public static void writeFile(String fileName, String string) 
	{
		FileOutputStream fos = null;
		try {
			fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			fos.write(string.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			Errors.FileNotFound(e);
			return;
		} catch (IOException e) {
			Errors.IO(e);
		}
	}

	/**
	 * Write a {@link String} to a file.
	 * 
	 * @param fileName
	 *            the file to write.
	 * @param string
	 *            the {@link String} containing the text to write.
	 */
	public static void writeFileToSD(String fileName, String string) 
	{
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(fileName));
			fos.write(string.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			Errors.FileNotFound(e);
			return;
		} catch (IOException e) {
			Errors.IO(e);
		}
	}

	/**
	 * Check if the file exists in the current app directory.
	 * 
	 * @param filename
	 *            the file to check.
	 * @return true if the file exists, false otherwise.
	 */
	public static boolean checkFile(String filename) 
	{
		File f = new File(context.getFilesDir(), filename);
		if (f.exists())
			return true;
		return false;
	}
	
	/**
	 * Check if the file exists.
	 * 
	 * @param filename
	 *            the absolute path of the file to check.
	 * @return true if the file exists, false otherwise.
	 */
	public static boolean checkSDFile(String filename) 
	{
		
		File f = new File(filename);
		if (f.exists())
			return true;
		return false;
	}

	public static boolean[] checkEsternalStorage() 
	{
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but
			// all we need
			// to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		// System.out.println("storage status: "+
		// mExternalStorageAvailable +" , " + mExternalStorageWriteable);
		return new boolean[] { mExternalStorageAvailable,
				mExternalStorageWriteable };
	}

	public static String joinPath(String[] mList) 
	{
		String result="";
		for(String val : mList){
			result+=val+File.separator;
		}
		return result.substring(0, result.length()-1);
	}
}
