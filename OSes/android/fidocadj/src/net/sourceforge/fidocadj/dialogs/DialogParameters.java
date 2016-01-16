package net.sourceforge.fidocadj.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.widget.Toast;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ScrollView;
import android.widget.Space;

import net.sourceforge.fidocadj.R;

import net.sourceforge.fidocadj.layers.LayerDesc;
import net.sourceforge.fidocadj.globals.Globals;
import net.sourceforge.fidocadj.graphic.FontG;
import net.sourceforge.fidocadj.graphic.PointG;
import net.sourceforge.fidocadj.FidoEditor;
import net.sourceforge.fidocadj.storage.StaticStorage;


/**
    Allows to create a generic dialog, capable of displaying and let the user
    modify the parameters of a graphic primitive. The idea is that the dialog
    uses a ParameterDescripion vector which contains all the elements, their
    description as well as the type. Depending on the contents of the array,
    the window will be created automatically.

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

    Copyright 2014-2015 by Dante Loi, Davide Bucci

    </pre>
    @author Dante Loi
*/
public class DialogParameters extends DialogFragment
{
    private final static int DENSITY_LOW = 120;
    private final static int DENSITY_MEDIUM = 160;
    private final static int DENSITY_HIGH = 240;
    private final static int DENSITY_TV = 213;
    private final static int DENSITY_XHIGH = 320;
    private final static int DENSITY_XXHIGH = 480;
    private final static int DENSITY_XXXHIGH = 640;
    private final static int SYSTEM_UI_LAYOUT_FLAGS=1536;

    private static Vector<ParameterDescription> vec;
    private static boolean strict;
    private static Vector<LayerDesc> layers;

    // Sizes
    private int fieldWidth;
    private int fieldHeight;
    private int textSize;
    private int buttonWidth;
    private int buttonHeight;

    //Dialog border
    private static final int BORDER = 30;

    //maximum strings' length
    private static final int MAX_LEN = 200;

    // Maximum number of user interface elements of the same type present
    // in the dialog window.
    private static final int MAX_ELEMENTS = 10;

    // Text box array and counter
    private EditText etv[];
    private int ec;

    // Check box array and counter
    private CheckBox cbv[];
    private int cc;

    // Spinner array and counter
    private Spinner spv[];
    private int sc;

    /** Get a ParameterDescription vector describing the characteristics
        modified by the user.
     @return a ParameterDescription vector describing each parameter.
    */
    public Vector<ParameterDescription> getCharacteristics()
    {
        return vec;
    }

    /** Creates the dialog and passes its arguments to it.
        @param vec the vector containing the various parameters to be set.
        @param strict true if a strict FidoCAD compatibility is required.
        @param layers the vector describing the current layers.
        @return a new istance of DialogParameters.
     */
    public static DialogParameters newInstance(Vector<ParameterDescription> vec,
            boolean strict, Vector<LayerDesc> layers)
    {
        DialogParameters dialog = new DialogParameters();

        Bundle args = new Bundle();
        args.putSerializable("vec", vec);
        args.putBoolean("strict", strict);
        args.putSerializable("layers", layers);
        dialog.setArguments(args);
        dialog.setRetainInstance(true);
        return dialog;
    }

    /** Create the user interface by processing all the parameters given
        during the construction of the class.
        @param savedInstanceState the saved instance state.
        @return the dialog containing the user interface.
    */
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null){
            vec = (Vector<ParameterDescription>) getArguments()
                                   .getSerializable("vec");
            layers = (Vector<LayerDesc>) getArguments()
                                   .getSerializable("layers");
            strict = getArguments().getBoolean("strict");

        } else{
            vec = (Vector<ParameterDescription>) savedInstanceState
                                       .getSerializable("vec");
            layers = (Vector<LayerDesc>) savedInstanceState
                                       .getSerializable("layers");
            strict = savedInstanceState.getBoolean("strict");
        }

        final Activity context = getActivity();
        final Dialog dialog = new Dialog(context);

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams
                    .SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        LinearLayout vv = new LinearLayout(context){

            //VKB hiding, with a touch on the dialog.
            @Override
            public boolean onTouchEvent(MotionEvent event)
            {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    InputMethodManager imm = (InputMethodManager)
                        context.getSystemService(
                              Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getWindowToken(),
                        SYSTEM_UI_LAYOUT_FLAGS);
                }
                return true;
            }
        };

        vv.setOrientation(LinearLayout.VERTICAL);
        vv.setBackgroundColor(getResources().
            getColor(R.color.background_white));
        vv.setPadding(BORDER, BORDER, BORDER, BORDER);

        etv = new EditText[MAX_ELEMENTS];
        cbv = new CheckBox[MAX_ELEMENTS];
        spv = new Spinner[MAX_ELEMENTS];

        ParameterDescription pd;

        ec = 0;
        cc = 0;
        sc = 0;

        //Setting of the dialog sizes.
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenDensity = metrics.densityDpi;
        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        setSizeByScreen(screenSize, screenDensity);

        //Filter for the Integer EditText,
        //allows to write only digit in the filtered fields.
        InputFilter filter = new InputFilter()
        {
            public CharSequence filter(CharSequence source, int start, int end,
                    Spanned dest, int dstart, int dend)
            {
                for (int i = start; i < end; i++) {
                    if (!Character.isDigit(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };

        // We process all parameter passed. Depending on its type, a
        // corresponding interface element will be created.
        // A symmetrical operation is done when validating parameters.
        for (int ycount = 0; ycount < vec.size(); ++ycount) {
            pd = (ParameterDescription) vec.elementAt(ycount);

            LinearLayout vh = new LinearLayout(context);
            vh.setGravity(Gravity.FILL_HORIZONTAL);
            vh.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);

            // We do not need to store label objects, since we do not need
            // to retrieve data from them.
            TextView lab = new TextView(context);
            lab.setTextColor(Color.BLACK);
            lab.setText(pd.description);
            lab.setPadding(0, 0, 10, 0);
            lab.setGravity(Gravity.CENTER);
            lab.setTextSize(textSize);
            if (!(pd.parameter instanceof Boolean))
                vh.addView(lab);
            // Now, depending on the type of parameter we create interface
            // elements and we populate the dialog.

            if (pd.parameter instanceof PointG) {
                etv[ec] = new EditText(context);
                etv[ec].setTextColor(Color.BLACK);
                etv[ec].setBackgroundResource(R.drawable.field_background);
                Integer x = Integer.valueOf(((PointG) (pd.parameter)).x);
                etv[ec].setText(x.toString());
                etv[ec].setMaxWidth(MAX_LEN);
                etv[ec].setLayoutParams(
                    new LayoutParams(fieldWidth/2,fieldHeight));
                etv[ec].setSingleLine();
                etv[ec].setFilters(new InputFilter[]{filter});
                etv[ec].setTextSize(textSize);

                vh.addView(etv[ec++]);

                etv[ec] = new EditText(context);
                Integer y = Integer.valueOf(((PointG) (pd.parameter)).y);
                etv[ec].setText(y.toString());
                etv[ec].setTextColor(Color.BLACK);
                etv[ec].setBackgroundResource(R.drawable.field_background);
                etv[ec].setMaxWidth(MAX_LEN);
                etv[ec].setLayoutParams(
                    new LayoutParams(fieldWidth/2,fieldHeight));
                etv[ec].setSingleLine();
                etv[ec].setFilters(new InputFilter[]{filter});
                etv[ec].setTextSize(textSize);

                vh.addView(etv[ec++]);
            } else if (pd.parameter instanceof String) {
                etv[ec] = new EditText(context);
                etv[ec].setTextColor(Color.BLACK);
                etv[ec].setGravity(Gravity.FILL_HORIZONTAL|
                    Gravity.CENTER_HORIZONTAL);
                etv[ec].setBackgroundResource(R.drawable.field_background);
                etv[ec].setText((String) (pd.parameter));
                etv[ec].setMaxWidth(MAX_LEN);
                etv[ec].setLayoutParams(
                    new LayoutParams(fieldWidth,fieldHeight));
                etv[ec].setSingleLine();
                etv[ec].setTextSize(textSize);

                // If we have a String text field in the first position, its
                // contents should be evidenced, since it is supposed to be
                // the most important field (e.g. for the AdvText primitive)
                if (ycount == 0)
                    etv[ec].selectAll();

                vh.addView(etv[ec++]);
            } else if (pd.parameter instanceof Boolean) {
                cbv[cc] = new CheckBox(context);
                cbv[cc].setText(pd.description);
                cbv[cc].setTextColor(Color.BLACK);
                cbv[cc].setLayoutParams(
                    new LayoutParams(fieldWidth,fieldHeight));
                cbv[cc].setChecked(((Boolean) (pd.parameter)).booleanValue());
                cbv[cc].setTextSize(textSize);

                vh.addView(cbv[cc++]);
            } else if (pd.parameter instanceof Integer) {
                etv[ec] = new EditText(context);
                etv[ec].setTextColor(Color.BLACK);
                etv[ec].setBackgroundResource(R.drawable.field_background);
                etv[ec].setText(((Integer) pd.parameter).toString());
                etv[ec].setMaxWidth(MAX_LEN);
                etv[ec].setLayoutParams(
                    new LayoutParams(fieldWidth,fieldHeight));
                etv[ec].setSingleLine();
                etv[ec].setFilters(new InputFilter[]{filter});
                etv[ec].setTextSize(textSize);

                vh.addView(etv[ec++]);
            } else if (pd.parameter instanceof Float) {
                etv[ec] = new EditText(context);
                etv[ec].setTextColor(Color.BLACK);
                etv[ec].setBackgroundResource(R.drawable.field_background);
                int dummy = java.lang.Math.round((Float) pd.parameter);
                etv[ec].setText("  "+dummy);
                etv[ec].setMaxWidth(MAX_LEN);
                etv[ec].setLayoutParams(
                    new LayoutParams(fieldWidth,fieldHeight));
                etv[ec].setSingleLine();
                etv[ec].setTextSize(textSize);

                vh.addView(etv[ec++]);
            } else if (pd.parameter instanceof FontG) {
                spv[sc] = new Spinner(context);

                String[] s = {"Normal","Italic","Bold"};

                ArrayAdapter<String> adapter = new ArrayAdapter<String> (
                        context, android.R.layout.simple_spinner_item , s);
                spv[sc].setAdapter(adapter);
                spv[sc].setBackgroundResource(R.drawable.field_background);
                spv[sc].setLayoutParams(
                    new LayoutParams(fieldWidth,fieldHeight));

                for (int i = 0; i < s.length; ++i) {
                    if (s[i].equals(((FontG) pd.parameter).getFamily()))
                        spv[sc].setSelection(i);
                    else
                        spv[sc].setSelection(0);
                }
                vh.addView(spv[sc++]);
            } else if (pd.parameter instanceof LayerInfo) {
                spv[sc] = new Spinner(context);

                LayerSpinnerAdapter adapter = new LayerSpinnerAdapter(context,
                        R.layout.layer_spinner_item, layers);

                spv[sc].setAdapter(adapter);
                spv[sc].setBackgroundResource(R.drawable.field_background);
                spv[sc].setSelection(((LayerInfo) pd.parameter).layer);
                spv[sc].setLayoutParams(
                    new LayoutParams(fieldWidth,fieldHeight));
                vh.addView(spv[sc++]);

            } else if (pd.parameter instanceof ArrowInfo) {
                spv[sc] = new Spinner(context);

                List<ArrowInfo> l = new ArrayList<ArrowInfo>();
                l.add(new ArrowInfo(0));
                l.add(new ArrowInfo(1));
                l.add(new ArrowInfo(2));
                l.add(new ArrowInfo(3));

                ArrowSpinnerAdapter adapter = new ArrowSpinnerAdapter(
                        context, R.layout.spinner_item, l);

                spv[sc].setAdapter(adapter);
                spv[sc].setBackgroundResource(R.drawable.field_background);
                spv[sc].setSelection(((ArrowInfo) pd.parameter).style);
                spv[sc].setLayoutParams(
                    new LayoutParams(fieldWidth,fieldHeight));
                vh.addView(spv[sc++]);
            } else if (pd.parameter instanceof DashInfo) {
                spv[sc] = new Spinner(context);

                List<DashInfo> l = new ArrayList<DashInfo>();
                for (int k = 0; k < Globals.dashNumber; ++k)
                    l.add(new DashInfo(k));
                //TODO: customize the Arrayadapter.


                DashSpinnerAdapter  adapter = new DashSpinnerAdapter(
                        context, android.R.layout.simple_spinner_item, l);

                spv[sc].setAdapter(adapter);
                spv[sc].setBackgroundResource(R.drawable.field_background);
                spv[sc].setSelection(((DashInfo) pd.parameter).style);
                spv[sc].setLayoutParams(
                    new LayoutParams(fieldWidth,fieldHeight));

                vh.addView(spv[sc++]);
            }
            vv.addView(vh);
        }

        LinearLayout buttonView = new LinearLayout(context);
        buttonView.setGravity(Gravity.RIGHT);
        buttonView.setOrientation(LinearLayout.HORIZONTAL);

        Button ok = new Button(context);
        ok.setTextColor(getResources().getColor(R.color.active_light));
        ok.setBackgroundColor(getResources().getColor(R.color.background_dark));
        ok.setText(getResources().getText(R.string.Ok_btn));
        ok.setTextSize(textSize);
        ok.setLayoutParams(
                new LayoutParams(buttonWidth,buttonHeight));
        ok.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View buttonView)
            {
                try {
                    int ycount;
                    ParameterDescription pd;

                    ec = 0;
                    cc = 0;
                    sc = 0;

                    // Here we read all the contents of the interface and we
                    // update the contents of the parameter description array.

                    for (ycount = 0; ycount < vec.size(); ++ycount) {

                        pd = (ParameterDescription) vec.elementAt(ycount);

                        if (pd.parameter instanceof Point) {
                            ((Point) (pd.parameter)).x = Integer
                                    .parseInt(etv[ec++].getText().toString());
                            ((Point) (pd.parameter)).y = Integer
                                    .parseInt(etv[ec++].getText().toString());
                        } else if (pd.parameter instanceof String) {
                            pd.parameter = etv[ec++].getText().toString();
                        } else if (pd.parameter instanceof Boolean) {
                            android.util.Log.e("fidocadj",
                                "value:"+Boolean.valueOf(
                                cbv[cc].isChecked()));
                            pd.parameter = Boolean.valueOf(
                                cbv[cc++].isChecked());

                        } else if (pd.parameter instanceof Integer) {
                            pd.parameter = Integer.valueOf(Integer
                                    .parseInt(etv[ec++].getText().toString()));
                        } else if (pd.parameter instanceof Float) {
                            pd.parameter = Float.valueOf(
                                Float.parseFloat(
                                    etv[ec++].getText().toString()));
                        } else if (pd.parameter instanceof FontG) {
                            pd.parameter = new FontG((String) spv[sc++]
                                    .getSelectedItem());
                        } else if (pd.parameter instanceof LayerInfo) {
                            pd.parameter = new LayerInfo((Integer) spv[sc++]
                                    .getSelectedItemPosition());
                        } else if (pd.parameter instanceof ArrowInfo) {
                            pd.parameter = new ArrowInfo((Integer) spv[sc++]
                                    .getSelectedItemPosition());
                        } else if (pd.parameter instanceof DashInfo) {
                            pd.parameter = new DashInfo((Integer) spv[sc++]
                                    .getSelectedItemPosition());
                        }
                    }
                } catch (NumberFormatException E) {
                    // Error detected. Probably, the user has entered an
                    // invalid string when FidoCadJ was expecting a numerical
                    // input.

                    Toast t = new Toast(context);
                    t.setText(Globals.messages.getString("Format_invalid"));
                    t.show();
                }

                FidoEditor caller = StaticStorage.getCurrentEditor();
                caller.saveCharacteristics(vec);
                dialog.dismiss();
            }
        });

        buttonView.addView(ok);

        Space space = new Space(context);
        space.setLayoutParams(
                new LayoutParams(5, buttonHeight));

        buttonView.addView(space);

        Button cancel = new Button(context);
        cancel.setTextColor(getResources().getColor(R.color.active_dark));
        cancel.setBackgroundColor(getResources().getColor(
                            R.color.background_dark));

        cancel.setText(getResources().getText(R.string.Cancel_btn));
        cancel.setTextSize(textSize);
        cancel.setLayoutParams(
                new LayoutParams(buttonWidth, buttonHeight));
        cancel.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View buttonView)
            {
                dialog.dismiss();
            }
        });
        buttonView.addView(cancel);

        vv.addView(buttonView);
        ScrollView sv=new ScrollView(context);
        sv.addView(vv);
        dialog.setContentView((View)sv);

        return dialog;
    }

    /** Called when the dialog is dismissed.
        @param savedInstanceState the state of the instance to be saved.
    */
    public void onDismiss(Bundle savedInstanceState)
    {
        savedInstanceState.putSerializable("vec", vec);
        savedInstanceState.putBoolean("strict", strict);
        savedInstanceState.putSerializable("layers", layers);
    }

    /** Called when this view is destroyed.
    */
    @Override
    public void onDestroyView()
    {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }

    /** Customized item for the layout spinner.
    */
    private class LayerSpinnerAdapter extends ArrayAdapter<LayerDesc>
    {
        private final Context context;
        private final List<LayerDesc> layers;

        public LayerSpinnerAdapter(Context context, int textViewResourceId,
                List<LayerDesc> layers)
        {
            super(context, textViewResourceId, layers);
            this.context = context;
            this.layers = layers;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getDropDownView(int position,
                                View convertView, ViewGroup parent)
        {
            return getCustomView(position, convertView, parent);
        }

        /** Get a custom view showing each layer in the spinner. Here the
            user is not supposed to edit the layers.
        */
        public View getCustomView(int position,
                                View convertView, ViewGroup parent)
        {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            View row = inflater.inflate(R.layout.layer_spinner_item_noedit,
                 parent, false);
            row.setBackgroundColor(Color.WHITE);

            SurfaceView sv = (SurfaceView) row.findViewById(R.id.surface_view);
            sv.setBackgroundColor(layers.get(position).getColor().getRGB());

            TextView v = (TextView) row.findViewById(R.id.name_item);
            v.setText(layers.get(position).getDescription());
            v.setTextColor(Color.BLACK);
            v.setBackgroundColor(Color.WHITE);
            v.setTextSize(textSize);

            return row;
        }
    }

    /** Customized item for the arrow spinner.
    */
    private class ArrowSpinnerAdapter extends ArrayAdapter<ArrowInfo>
    {
        private final Context context;
        private final List<ArrowInfo> info;

        public ArrowSpinnerAdapter(Context context, int textViewResourceId,
                List<ArrowInfo> info)
        {
            super(context, textViewResourceId, info);
            this.context = context;
            this.info = info;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getDropDownView(int position,
            View convertView, ViewGroup parent)
        {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position,
            View convertView, ViewGroup parent)
        {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            LinearLayout row = (LinearLayout)inflater.
                inflate(R.layout.spinner_item, parent, false);
            CellArrow ca = new CellArrow(context);
            ca.setStyle(info.get(position));
            row.addView(ca);

            return row;
        }
    }

    private class DashSpinnerAdapter extends ArrayAdapter<DashInfo>
    {
        private final Context context;
        private final List<DashInfo> info;

        public DashSpinnerAdapter(Context context, int textViewResourceId,
            List<DashInfo> info)
        {
            super(context, textViewResourceId, info);
            this.context = context;
            this.info = info;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getDropDownView(int position,
            View convertView, ViewGroup parent)
        {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position,
            View convertView, ViewGroup parent)
        {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            LinearLayout row = (LinearLayout)inflater.
                inflate(R.layout.spinner_item, parent, false);
            CellDash da = new CellDash(context);
            da.setStyle(info.get(position));
            row.addView(da);

            return row;
        }
    }

    /** Adapts the various dialog's dimension at the screen density and size.
        @param size, the physical screen size of the device.
        @param density, the screen resolution of the device.
    */
    private void setSizeByScreen(int size, int density)
    {
        // Default values (show something in any case).
        fieldWidth = 300;
        fieldHeight = 50;
        textSize = 15;
        buttonWidth = 115;
        buttonHeight = 60;

        android.util.Log.e("fidocadj", "size: "+size+" density: "+density);

        if(size == Configuration.SCREENLAYOUT_SIZE_SMALL) {
            switch(density) {
                case DENSITY_LOW:
                    // Not tested yet on a real device
                    fieldWidth = 80;
                    fieldHeight = 20;
                    textSize = 7;
                    buttonWidth = 100;
                    buttonHeight = 25;
                    break;
                case DENSITY_MEDIUM:
                    // Not tested on a real device yet!
                    fieldWidth = 130;
                    fieldHeight = 25;
                    textSize = 9;
                    buttonWidth = 120;
                    buttonHeight = 40;
                    break;
                case DENSITY_HIGH: // no break d:240
                case DENSITY_TV:
                    // Not tested on a real device yet!
                    fieldWidth = 300;
                    fieldHeight = 60;
                    textSize = 11;
                    buttonWidth = 160;
                    buttonHeight = 60;
                    break;
                case DENSITY_XHIGH:
                    // Not tested on a real device yet!
                    fieldWidth = 350;
                    fieldHeight = 70;
                    textSize = 13;
                    buttonWidth = 170;
                    buttonHeight = 65;
                    break;
                case DENSITY_XXHIGH: // D: around 480
                    // Not tested on a real device yet!
                    fieldWidth = 400;
                    fieldHeight = 80;
                    textSize = 14;
                    buttonWidth = 200;
                    buttonHeight = 70;
                    break;
                case DENSITY_XXXHIGH:
                    // Not tested on a real device yet!
                    fieldWidth = 450;
                    fieldHeight = 95;
                    textSize = 16;
                    buttonWidth = 230;
                    buttonHeight = 90;
                    break;
                default:
                    fieldWidth = 300;
                    fieldHeight = 50;
                    textSize = 10;
                    break;
            }
        } else if(size == Configuration.SCREENLAYOUT_SIZE_NORMAL) { //s:2
            switch(density) {
                case DENSITY_LOW:
                    // Not tested on a real device yet!
                    fieldWidth = 120;
                    fieldHeight = 25;
                    textSize = 8;
                    buttonWidth = 120;
                    buttonHeight = 30;
                    break;
                case DENSITY_MEDIUM:
                    // Not tested on a real device yet!
                    fieldWidth = 150;
                    fieldHeight = 30;
                    textSize = 9;
                    buttonWidth = 150;
                    buttonHeight = 50;
                    break;
                case DENSITY_HIGH: // no break d:240
                case DENSITY_TV:
                    // Not tested on a real device yet!
                    //Tested with Nexus S (VD)
                    fieldWidth = 190;
                    fieldHeight = 45;
                    textSize = 11;
                    buttonWidth = 80;
                    buttonHeight = 50;
                    break;
                case DENSITY_XHIGH:
                    fieldWidth = 400;
                    fieldHeight = 80;
                    textSize = 14;
                    buttonWidth = 200;
                    buttonHeight = 80;
                    break;
                case DENSITY_XXHIGH: // d: 480
                    //tested with Google Nexus 5
                    //tested with Samsung Galaxy S5 (real device)
                    fieldWidth = 450;
                    fieldHeight = 90;
                    textSize = 15;
                    buttonWidth = 225;
                    buttonHeight = 100;
                    break;
                case DENSITY_XXXHIGH:
                    // Not tested on a real device yet!
                    fieldWidth = 550;
                    fieldHeight = 110;
                    textSize = 18;
                    buttonWidth = 275;
                    buttonHeight = 100;
                    break;
                default:
                    fieldWidth = 300;
                    fieldHeight = 50;
                    textSize = 10;
                    break;
            }
        } else if(size == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            switch(density) {
                /*case DENSITY_LOW:
                    break;
                case DENSITY_MEDIUM:
                    break;
                    break;*/
                case DENSITY_HIGH: // no break, d:240
                case DENSITY_TV:
                    //tested with nexus7 800x1280
                    fieldWidth = 300;
                    fieldHeight = 50;
                    textSize = 16;
                    break;
                case DENSITY_XHIGH:
                    //tested with nexus7 1200x1920
                    fieldWidth = 450;
                    fieldHeight = 80;
                    textSize = 18;
                    break;
                case DENSITY_XXHIGH:
                    // not tested yet
                    fieldWidth = 500;
                    fieldHeight = 100;
                    textSize = 20;
                    break;
                case DENSITY_XXXHIGH:
                    // not tested yet
                    fieldWidth = 550;
                    fieldHeight = 110;
                    textSize = 22;
                    break;
                default:
                    fieldWidth = 300;
                    fieldHeight = 50;
                    textSize = 16;
                    break;
            }
        } else if(size == Configuration.SCREENLAYOUT_SIZE_XLARGE) { // s: 4
            switch(density) {
                case DENSITY_LOW:
                    break;
                case DENSITY_MEDIUM: // d:160
                    // Samsung Galaxy Note 10.1 v. 2013 (real device)
                    fieldWidth = 400;
                    fieldHeight = 40;
                    textSize = 16;
                    buttonWidth = 275;
                    buttonHeight = 70;
                    break;
                case DENSITY_TV: // no break here
                case DENSITY_HIGH:   // d:240
                    // Not tested yet!
                    fieldWidth = 600;
                    fieldHeight = 70;
                    textSize = 17;
                    buttonWidth = 300;
                    buttonHeight = 100;

                    break;
                case DENSITY_XHIGH:
                    // Not tested yet!
                    fieldWidth = 600;
                    fieldHeight = 70;
                    textSize = 18;
                    buttonWidth = 320;
                    buttonHeight = 110;
                    break;
                case DENSITY_XXHIGH:
                    // Not tested yet!
                    fieldWidth = 650;
                    fieldHeight = 80;
                    textSize = 20;
                    buttonWidth = 350;
                    buttonHeight = 130;
                    break;
                case DENSITY_XXXHIGH:
                    // Not tested yet!
                    fieldWidth = 700;
                    fieldHeight = 90;
                    textSize = 22;
                    buttonWidth = 370;
                    buttonHeight = 140;
                    break;
                default:
                    break;
            }
        }
    }
}
