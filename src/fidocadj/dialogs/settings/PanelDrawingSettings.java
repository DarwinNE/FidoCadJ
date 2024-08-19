package fidocadj.dialogs.settings;

import javax.swing.*;
import java.awt.*;

import fidocadj.dialogs.controls.DialogUtil;
import fidocadj.globals.Globals;

/**
 PanelDrawingSettings is responsible for managing the drawing settings
 of the application, such as grid width, connection size, and stroke size.
 It implements the SettingsPanel interface to load and save settings.

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
 along with FidoCadJ. If not,
 @see <a href=http://www.gnu.org/licenses/>http://www.gnu.org/licenses/</a>.

 Copyright 2007-2024 by Davide Bucci, Manuel Finessi
 </pre>
 */
public class PanelDrawingSettings extends JPanel implements SettingsPanel
{

    private final SettingsManager settingsManager;
    private JTextField gridWidthField;
    private JTextField connectionSizeField;
    private JTextField strokeSizeStraightField;

    /**
     Constructor for PanelDrawingSettings.

     @param settingsManager the settings manager to handle the application settings.
     */
    public PanelDrawingSettings(SettingsManager settingsManager)
    {
        this.settingsManager = settingsManager;
        setupUI();
        loadSettings(); // Load settings during initialization
    }

    /**
     Set up the UI components and layout.
     */
    @Override
    public void setupUI()
    {
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        // Label and text field for grid width setting
        JLabel gridWidthLabel = new JLabel(Globals.messages.getString(
                "Grid_width"));
        constraints = DialogUtil.createConst(0, 0, 1, 1, 0.01, 0.01,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        add(gridWidthLabel, constraints);

        gridWidthField = new JTextField(10);
        constraints = DialogUtil.createConst(1, 0, 2, 1, 0.01, 0.01,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(6, 6, 6, 6));
        add(gridWidthField, constraints);

        // Label and text field for connection size setting
        JLabel connectionSizeLabel = new JLabel(Globals.messages.getString(
                "connection_size"));
        constraints = DialogUtil.createConst(0, 1, 1, 1, 0.01, 0.01,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        add(connectionSizeLabel, constraints);

        connectionSizeField = new JTextField(10);
        constraints = DialogUtil.createConst(1, 1, 2, 1, 0.01, 0.01,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(6, 6, 6, 6));
        add(connectionSizeField, constraints);

        // Label and text field for stroke size setting
        JLabel strokeSizeStraightLabel = new JLabel(Globals.messages.getString(
                "stroke_size_straight"));
        constraints = DialogUtil.createConst(0, 2, 1, 1, 0.01, 0.01,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        add(strokeSizeStraightLabel, constraints);

        strokeSizeStraightField = new JTextField(10);
        constraints = DialogUtil.createConst(1, 2, 2, 1, 0.01, 0.01,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(6, 6, 6, 6));
        add(strokeSizeStraightField, constraints);
    }

    /**
     Load settings from SettingsManager and populate the UI components.
     */
    @Override
    public void loadSettings()
    {
        gridWidthField.setText(settingsManager.get("GRID_SIZE", "5"));
        connectionSizeField.setText(
                settingsManager.get("CONNECTION_SIZE", "2.0"));
        strokeSizeStraightField.setText(settingsManager.get(
                "STROKE_SIZE_STRAIGHT", "0.5"));
    }

    /**
     Save the settings from the UI components into SettingsManager.
     */
    @Override
    public void saveSettings()
    {
        settingsManager.put("GRID_SIZE", gridWidthField.getText());
        settingsManager.put("CONNECTION_SIZE", connectionSizeField.getText());
        settingsManager.put("STROKE_SIZE_STRAIGHT",
                strokeSizeStraightField.getText());
    }
}
