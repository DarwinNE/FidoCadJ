package net.sourceforge.fidocadj.dialogs;

import java.util.Vector;

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
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.TextView;
import android.util.DisplayMetrics;
import android.graphics.Color;
import android.widget.SeekBar;
import android.widget.EditText;

import net.sourceforge.fidocadj.globals.*;
import net.sourceforge.fidocadj.layers.*;
import net.sourceforge.fidocadj.*;
import net.sourceforge.fidocadj.graphic.android.ColorAndroid;

import net.sourceforge.fidocadj.R;

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

    Copyright 2014-2015 by Davide Bucci
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
    private SeekBar colorAlphaBar;

    private SurfaceView preview;

    private EditText editLayerName;

    public int valueR;
    public int valueG;
    public int valueB;
    public int valueAlpha;

    private FidoEditor drawingPanel;
    private Vector<LayerDesc> layers;
    private int currentLayer;
    private View currentView;


    /** Creator
    */
    public DialogEditLayer(int cl, View v)
    {
        currentLayer=cl;
        currentView=v;
    }

    /** Called when the dialog is being create, this method updates the
        user interface.
    */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        context = getActivity();
        dialog = new Dialog(context);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_layer);

        // Get the bars corresponding to the R,G,B and alpha coefficients.
        colorRbar=(SeekBar)dialog.findViewById(R.id.color_r);
        colorGbar=(SeekBar)dialog.findViewById(R.id.color_g);
        colorBbar=(SeekBar)dialog.findViewById(R.id.color_b);
        colorAlphaBar=(SeekBar)dialog.findViewById(R.id.color_alpha);
        // Get the preview area.
        preview=(SurfaceView)dialog.findViewById(R.id.preview_surface);
        editLayerName = (EditText)dialog.findViewById(R.id.edit_layername);

        // Get the drawing panel.
        drawingPanel = (FidoEditor)context.findViewById(R.id.drawingPanel);

        // Get the list of the layers.
        layers = drawingPanel.getDrawingModel().getLayers();
        //currentLayer=drawingPanel.eea.currentLayer;

        // Set the listeners.
        colorRbar.setOnSeekBarChangeListener(this);
        colorGbar.setOnSeekBarChangeListener(this);
        colorBbar.setOnSeekBarChangeListener(this);
        colorAlphaBar.setOnSeekBarChangeListener(this);

        // Get the current layer data and set the cursors and the info
        colorRbar.setProgress(layers.get(currentLayer).getColor().getRed());
        colorGbar.setProgress(layers.get(currentLayer).getColor().getGreen());
        colorBbar.setProgress(layers.get(currentLayer).getColor().getBlue());
        colorAlphaBar.setProgress(
            (int)(layers.get(currentLayer).getAlpha()*255.0));
        editLayerName.setText(layers.get(currentLayer).getDescription());

        // Get the Ok button and add a click handler.
        Button okButton=(Button)dialog.findViewById(
            R.id.dialog_edit_layer_ok);
        okButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // Save the current state.
                layers.get(currentLayer).setColor(
                    new ColorAndroid(
                        Color.rgb(colorRbar.getProgress(),
                        colorGbar.getProgress(), colorBbar.getProgress())));
                layers.get(currentLayer).setDescription(
                    editLayerName.getText().toString());
                layers.get(currentLayer).setAlpha(
                    (float)colorAlphaBar.getProgress()/255.0f);
                dialog.dismiss();
                currentView.invalidate();
            }
        });

        // Get the Cancel button and add a click handler.
        Button cancelButton=(Button)dialog.findViewById(
            R.id.dialog_edit_layer_cancel);
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });

        return dialog;
    }

    /** This method is called when a slider is operated. In our case, this
        happens when the user changes a value for red, green, blue or alpha.
    */
    @Override
    public void onProgressChanged(SeekBar seekBar,
        int progress, boolean fromUser)
    {
        // Determine which color bar is being manipulated.
        if(seekBar.equals(colorRbar)) {
            valueR=progress;
        } else if(seekBar.equals(colorGbar)) {
            valueG=progress;
        } else if(seekBar.equals(colorBbar)) {
            valueB=progress;
        } else {
            valueAlpha=progress;
        }
        preview.setBackgroundColor(Color.argb(valueAlpha,
            valueR, valueG, valueB));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
    }
}
