package fidocadj.dialogs.settings;

import javax.swing.*;
import java.awt.*;

import fidocadj.dialogs.controls.DialogUtil;
import fidocadj.globals.Globals;
import fidocadj.globals.SettingsManager;

/**
 PanelPCBSettings is responsible for managing the PCB settings in the
 application, such as PCB line width, pad width, pad height,
 and internal pad hole diameter. It implements the SettingsPanel interface
 to load and save settings.

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
public class PanelPCBSettings extends JPanel implements SettingsPanel
{

    private final SettingsManager settingsManager;
    private JTextField pcbLineWidthField;
    private JTextField pcbPadWidthField;
    private JTextField pcbPadHeightField;
    private JTextField pcbPadHoleDiameterField;

    /**
     Constructor for PanelPCBSettings.

     @param settingsManager the settings manager to handle the ..
                            application settings.
     */
    public PanelPCBSettings(SettingsManager settingsManager)
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

        // Set weighty to 1.0 for pushing components to the top
        constraints.weighty = 1.0;
        constraints.anchor = GridBagConstraints.NORTH;

        // Label and text field for PCB line width setting
        JLabel pcbLineWidthLabel = new JLabel(Globals.messages.getString(
                "pcbline_width"));
        constraints = DialogUtil.createConst(0, 0, 1, 1, 0.01, 0.01,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(30, 10, 5, 5));
        add(pcbLineWidthLabel, constraints);

        pcbLineWidthField = new JTextField(10);
        pcbLineWidthField.setPreferredSize(new Dimension(150,
                pcbLineWidthField.getPreferredSize().height));
        constraints = DialogUtil.createConst(1, 0, 1, 1, 0.01, 0.01,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(30, 5, 5, 10));
        add(pcbLineWidthField, constraints);

        // Label and text field for PCB pad width setting
        JLabel pcbPadWidthLabel = new JLabel(Globals.messages.getString(
                "pcbpad_width"));
        constraints = DialogUtil.createConst(0, 1, 1, 1, 0.01, 0.01,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(15, 10, 5, 5));
        add(pcbPadWidthLabel, constraints);

        pcbPadWidthField = new JTextField(10);
        pcbPadWidthField.setPreferredSize(new Dimension(150,
                pcbPadWidthField.getPreferredSize().height));
        constraints = DialogUtil.createConst(1, 1, 1, 1, 0.01, 0.01,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(15, 5, 5, 10));
        add(pcbPadWidthField, constraints);

        // Label and text field for PCB pad height setting
        JLabel pcbPadHeightLabel = new JLabel(Globals.messages.getString(
                "pcbpad_height"));
        constraints = DialogUtil.createConst(0, 2, 1, 1, 0.01, 0.01,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(15, 10, 5, 5));
        add(pcbPadHeightLabel, constraints);

        pcbPadHeightField = new JTextField(10);
        pcbPadHeightField.setPreferredSize(new Dimension(150,
                pcbPadHeightField.getPreferredSize().height));
        constraints = DialogUtil.createConst(1, 2, 1, 1, 0.01, 0.01,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(15, 5, 5, 10));
        add(pcbPadHeightField, constraints);

        // Label and text field for PCB pad hole diameter setting
        JLabel pcbPadHoleDiameterLabel = new JLabel(Globals.messages.getString(
                "pcbpad_intw"));
        constraints = DialogUtil.createConst(0, 3, 1, 1, 0.01, 0.01,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(15, 10, 5, 5));
        add(pcbPadHoleDiameterLabel, constraints);

        pcbPadHoleDiameterField = new JTextField(10);
        pcbPadHoleDiameterField.setPreferredSize(new Dimension(150,
                pcbPadHoleDiameterField.getPreferredSize().height));
        constraints = DialogUtil.createConst(1, 3, 1, 1, 0.01, 0.01,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(15, 5, 5, 10));
        add(pcbPadHoleDiameterField, constraints);

        // Spacer to push all components to the top
        constraints = DialogUtil.createConst(0, 4, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0));
        add(Box.createGlue(), constraints);
    }

    /**
     Load settings from SettingsManager and populate the UI components.
     */
    @Override
    public void loadSettings()
    {
        pcbLineWidthField.setText(settingsManager.get("PCB_LINE_WIDTH", "10"));
        pcbPadWidthField.setText(settingsManager.get("PCB_PAD_WIDTH", "10"));
        pcbPadHeightField.setText(settingsManager.get("PCB_PAD_HEIGHT", "10"));
        pcbPadHoleDiameterField.setText(settingsManager.get(
                "PCB_PAD_HOLE_DIAMETER", "5"));
    }

    /**
     Save the settings from the UI components into SettingsManager.
     */
    @Override
    public void saveSettings()
    {
        settingsManager.put("PCB_LINE_WIDTH", pcbLineWidthField.getText());
        settingsManager.put("PCB_PAD_WIDTH", pcbPadWidthField.getText());
        settingsManager.put("PCB_PAD_HEIGHT", pcbPadHeightField.getText());
        settingsManager.put("PCB_PAD_HOLE_DIAMETER",
                pcbPadHoleDiameterField.getText());
    }
}
