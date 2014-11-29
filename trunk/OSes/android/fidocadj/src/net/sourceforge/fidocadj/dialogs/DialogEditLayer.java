package net.sourceforge.fidocadj.dialogs;

import net.sourceforge.fidocadj.R;
import android.app.Activity;
import android.app.Dialog;  
import android.app.DialogFragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;  
import android.view.View;  
import android.view.View.OnClickListener;  
import android.view.Window;   
import android.widget.Button;  
import android.widget.TextView;
import android.util.DisplayMetrics;

import android.widget.SeekBar;

import net.sourceforge.fidocadj.globals.*;
  

/**
  	Allows to select the layer name, color and transparence.
 
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

    Copyright 2014 by Davide Bucci
 	</pre>
 	
  	@author Davide Bucci
 
 */
public class DialogEditLayer extends DialogFragment implements 
	SeekBar.OnSeekBarChangeListener
{
	private Activity context;
	private Dialog dialog;
	
	private SeekBar colorRbar;
	private SeekBar colorGbar;
	private SeekBar colorBbar;
	
	
	@Override  
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{  
		context = getActivity();
		dialog = new Dialog(context);
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_edit_layer);  
		
		colorRbar=(SeekBar)context.findViewById(R.id.color_r);
		colorGbar=(SeekBar)context.findViewById(R.id.color_g);
		colorBbar=(SeekBar)context.findViewById(R.id.color_b);
		
		//colorRbar.setOnSeekBarChangeListener(this);
		//colorGbar.setOnSeekBarChangeListener(this);
		//colorBbar.setOnSeekBarChangeListener(this);
		
		return dialog;
	}
	
	    @Override
    public void onProgressChanged(SeekBar seekBar, 
    	int progress, boolean fromUser)
    {
        android.util.Log.e("fidocadj", "value: "+progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {
    }
 
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) 
    {
 	}
	
	
	public void okSelected(View v)
	{
		dialog.dismiss();
	}
	
	public void cancelSelected(View v)
	{
		dialog.dismiss();
	}
}  

