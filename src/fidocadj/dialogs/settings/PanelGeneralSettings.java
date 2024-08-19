package fidocadj.dialogs.settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import fidocadj.dialogs.controls.DialogUtil;
import fidocadj.globals.Globals;

/**
 PanelGeneralSettings is responsible for managing the general settings
 of the application, such as the library directory and toolbar settings.
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
public final class PanelGeneralSettings extends JPanel implements SettingsPanel
{

    private final SettingsManager settingsManager;
    private JTextField libDirectoryField;
    private JCheckBox textToolbarCheckBox;
    private JCheckBox smallIconsCheckBox;

    /**
     Constructor for PanelGeneralSettings.

     @param settingsManager settings manager to handle the application settings.
     */
    public PanelGeneralSettings(SettingsManager settingsManager)
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

        // Label for the library directory setting
        JLabel libDirLabel = new JLabel(Globals.messages.getString("lib_dir"));
        constraints = DialogUtil.createConst(0, 0, 1, 1, 0.01, 0.01,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(20, 40, 6, 0));
        add(libDirLabel, constraints);

        // Text field for the library directory path
        libDirectoryField = new JTextField(10);
        constraints = DialogUtil.createConst(1, 0, 2, 1, 0.01, 0.01,
                GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
                new Insets(6, 40, 6, 20));
        add(libDirectoryField, constraints);

        // Button to browse for a library directory
        JButton browseButton = new JButton(Globals.messages.getString("Browse"));
        constraints = DialogUtil.createConst(3, 0, 1, 1, 0.01, 0.01,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(6, 40, 6, 20));
        add(browseButton, constraints);

        // Checkbox to toggle text in the toolbar
        textToolbarCheckBox = new JCheckBox(Globals.messages.getString(
                "TextToolbar"));
        constraints = DialogUtil.createConst(0, 1, 4, 1, 0.01, 0.01,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(6, 40, 6, 0));
        add(textToolbarCheckBox, constraints);

        // Checkbox to toggle small icons in the toolbar
        smallIconsCheckBox = new JCheckBox(Globals.messages.getString(
                "SmallIcons"));
        constraints = DialogUtil.createConst(0, 2, 4, 1, 0.01, 0.01,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(6, 40, 6, 0));
        add(smallIconsCheckBox, constraints);

        // Action listener for the browse button to select a directory
        browseButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JFileChooser fileChooser = new JFileChooser(
                        libDirectoryField.getText());
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnValue = fileChooser.showOpenDialog(
                        PanelGeneralSettings.this);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    libDirectoryField.setText(
                            fileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
    }

    /**
     Load settings from SettingsManager and populate the UI components.
     */
    @Override
    public void loadSettings()
    {
        libDirectoryField.setText(settingsManager.get("DIR_LIBS", ""));
        textToolbarCheckBox.setSelected(settingsManager.get("TEXT_TOOLBAR",
                "true").equals("true"));
        smallIconsCheckBox.setSelected(settingsManager.get("SMALL_ICON_TOOLBAR",
                "false").equals("true"));
    }

    /**
     Save the settings from the UI components into SettingsManager.
     */
    @Override
    public void saveSettings()
    {
        settingsManager.put("DIR_LIBS", libDirectoryField.getText());
        settingsManager.put("TEXT_TOOLBAR",
                textToolbarCheckBox.isSelected() ? "true" : "false");
        settingsManager.put("SMALL_ICON_TOOLBAR",
                smallIconsCheckBox.isSelected() ? "true" : "false");
    }
}
