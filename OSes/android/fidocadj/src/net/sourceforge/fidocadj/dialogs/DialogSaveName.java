package net.sourceforge.fidocadj.dialogs;

import net.sourceforge.fidocadj.FidoEditor;
import net.sourceforge.fidocadj.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.explorer.ExplorerActivity;
import com.explorer.IO;

/**
    Shows a text field for entry the new file name, and save it in the file
    system.

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

    Copyright 2014 by Dante Loi, Giuseppe Amato, Davide Bucci

    </pre>


    @author Dante Loi, Giuseppe Amato, Davide Bucci
 */
@SuppressLint("DefaultLocale")
public class DialogSaveName extends DialogFragment
{
    private String circuit;
    private FidoEditor drawingPanel;
    private Dialog dialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final Activity context = getActivity();
        dialog = new Dialog(context);

        drawingPanel = (FidoEditor) context.findViewById(R.id.drawingPanel);
        circuit = drawingPanel.getText();

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.save_with_name);
        EditText editPath = (EditText) dialog.findViewById(R.id.editPath);
        editPath.setText(IO.rootDir);

        Button save = (Button) dialog.findViewById(R.id.save);
        save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                save();
                dialog.dismiss();
            }
        });

        Button cancel = (Button) dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });

        ((ImageView) dialog.findViewById(R.id.explorerBt))
                .setOnClickListener(new OnClickListener() {
                    public void onClick(View v)
                    {
                        onFolderClick(v);
                    }
                });

        return dialog;
    }

    private void save()
    {
        EditText editName = (EditText) dialog.findViewById(R.id.editName);
        EditText editPath = (EditText) dialog.findViewById(R.id.editPath);

        String fileName = editName.getText().toString();
        if (!fileName.toLowerCase().endsWith(".fcd")) {
            fileName += ".fcd";
        }
        String path = editPath.getText().toString();
        writeFile(IO.joinPath(new String[] { path, fileName }));
    }

    /** Check if the file with the specified filename exists and prompt a
     *  dialog to confirm overwrite
     *
     * @param filename the filename to write to
     */
    private void writeFile(String filename)
    {
        drawingPanel.getParserActions().openFileName = filename;
        if (IO.checkSDFile(drawingPanel.getParserActions().openFileName)) {
            AlertDialog.Builder builder = new
                AlertDialog.Builder(getActivity());
            String msg = getResources().getString(R.string.Warning_overwrite);
            builder.setMessage(msg)
                    .setCancelable(false)
                    .setTitle(R.string.Warning)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton(R.string.Ok_btn,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int id)
                                {
                                    IO.writeFileToSD(
                                            drawingPanel.getParserActions().
                                            openFileName,
                                            circuit);
                                }
                            })
                    .setNegativeButton(R.string.Cancel_btn,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int id)
                                {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            IO.writeFileToSD(drawingPanel.getParserActions().openFileName,
                    circuit);
        }
    }

    /** Invoke the ExplorerActivity to select folder
        @param v not used.
    */
    public void onFolderClick(View v)
    {
        Intent myIntent = new Intent(getActivity(), ExplorerActivity.class);
        myIntent.putExtra(ExplorerActivity.DIRECTORY, true);
        int requestCode = ExplorerActivity.REQUEST_FOLDER;
        startActivityForResult(myIntent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case ExplorerActivity.REQUEST_FOLDER:
                    if (data.hasExtra(ExplorerActivity.DIRECTORY)) {
                        String folder = data.getExtras().getString(
                            ExplorerActivity.DIRECTORY);
                        EditText editpath = (EditText) dialog
                            .findViewById(R.id.editPath);
                        editpath.setText(folder);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
