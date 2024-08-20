package fidocadj.dialogs.settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import fidocadj.dialogs.controls.ColorPicker;
import fidocadj.dialogs.controls.DialogUtil;
import fidocadj.globals.Globals;

/**
 PanelThemeSettings is responsible for managing the theme and color settings
 in the application. It allows the user to select between predefined themes
 (light, dark) or a custom theme and customize various colors used in the
 application's interface.
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
public class PanelThemeSettings extends JPanel implements SettingsPanel
{

    private SettingsManager settingsManager;
    private JRadioButton lightThemeRadioButton;
    private JRadioButton darkThemeRadioButton;
    private JRadioButton customThemeRadioButton;
    private JTextField customThemePathField;
    private JButton browseCustomThemeButton;
    private JCheckBox enableCustomThemesCheckBox;

    private ColorPicker backgroundColorPicker;
    private ColorPicker gridDotsColorPicker;
    private ColorPicker gridLinesColorPicker;
    private ColorPicker drawingColorPicker;
    private ColorPicker selectionRightToLeftColorPicker;
    private ColorPicker selectionLeftToRightColorPicker;
    private ColorPicker selectedElementsColorPicker;

    /**
     Constructor for PanelThemeSettings.

     @param settingsManager the settings manager to handle the application settings.
     */
    public PanelThemeSettings(SettingsManager settingsManager)
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

        // Label for restart information
        JLabel restw = new JLabel(Globals.messages.getString("restart_info"));
        restw.setForeground(Color.BLUE);

        // Set the constraints to center the label horizontally
        constraints = new GridBagConstraints();
        constraints.gridx = 0; 
        constraints.gridy = 0; 
        constraints.gridwidth = 3; 
        constraints.anchor = GridBagConstraints.CENTER; 
        constraints.fill = GridBagConstraints.NONE; 
        constraints.insets = new Insets(5, 0, 0, 0); 

        add(restw, constraints);

        // Groupbox "Theme Management"
        JPanel themeManagementPanel = new JPanel();
        themeManagementPanel.setBorder(
                BorderFactory.createTitledBorder(
                        Globals.messages.getString("Theme_groupBox")));
        themeManagementPanel.setLayout(new GridBagLayout());

        // Checkbox to enable custom themes support
        enableCustomThemesCheckBox = new JCheckBox(
                Globals.messages.getString("Theme_enableSupport"));
        enableCustomThemesCheckBox.setOpaque(false);
        constraints = DialogUtil.createConst(0, 1, 2, 1, 100, 100,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(6, 6, 6, 6));
        themeManagementPanel.add(enableCustomThemesCheckBox, constraints);

        // Radio buttons for theme selection
        lightThemeRadioButton = new JRadioButton(
                Globals.messages.getString("FlatLaf_light"));
        darkThemeRadioButton = new JRadioButton(
                Globals.messages.getString("FlatLaf_dark"));
        customThemeRadioButton = new JRadioButton(
                Globals.messages.getString("FlatLaf_custom"));

        ButtonGroup themeGroup = new ButtonGroup();
        themeGroup.add(lightThemeRadioButton);
        themeGroup.add(darkThemeRadioButton);
        themeGroup.add(customThemeRadioButton);

        // Set "Tema light" as the default selected option
        lightThemeRadioButton.setSelected(true);

        constraints = DialogUtil.createConst(0, 2, 1, 1, 100, 100,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        themeManagementPanel.add(lightThemeRadioButton, constraints);

        constraints = DialogUtil.createConst(0, 3, 1, 1, 100, 100,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        themeManagementPanel.add(darkThemeRadioButton, constraints);

        constraints = DialogUtil.createConst(0, 4, 1, 1, 100, 100,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        themeManagementPanel.add(customThemeRadioButton, constraints);

        // TextBox and Button for selecting custom theme file
        customThemePathField = new JTextField(20);
        browseCustomThemeButton = new JButton(
                Globals.messages.getString("Browse"));

        constraints = DialogUtil.createConst(1, 4, 1, 1, 100, 100,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(6, 6, 6, 6));
        themeManagementPanel.add(customThemePathField, constraints);

        constraints = DialogUtil.createConst(2, 4, 1, 1, 100, 100,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        themeManagementPanel.add(browseCustomThemeButton, constraints);

        // Enable/Disable Controls based on Custom Themes
        enableCustomThemesCheckBox.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                enableThemeControls(enableCustomThemesCheckBox.isSelected());
            }
        });

        ActionListener themeSelectionListener = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // Only enable custom theme controls if custom theme is selected
                enableThemeControls(true);
            }
        };

        lightThemeRadioButton.addActionListener(themeSelectionListener);
        darkThemeRadioButton.addActionListener(themeSelectionListener);
        customThemeRadioButton.addActionListener(themeSelectionListener);

        // Groupbox "Color Management"
        JPanel colorManagementPanel = new JPanel();
        colorManagementPanel.setBorder(
                BorderFactory.createTitledBorder(
                        Globals.messages.getString("Colors_management")));
        colorManagementPanel.setLayout(new GridBagLayout());

        // Column 1: Long text labels
        JLabel backgroundColorLabel = new JLabel(
                Globals.messages.getString("Circuit_backgroud"));
        constraints = DialogUtil.createConst(0, 0, 1, 1, 100, 100,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        colorManagementPanel.add(backgroundColorLabel, constraints);

        backgroundColorPicker = new ColorPicker(30, 20, Color.WHITE);
        constraints = DialogUtil.createConst(1, 0, 1, 1, 100, 100,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        colorManagementPanel.add(backgroundColorPicker, constraints);

        JLabel selectionRightToLeftColorLabel = new JLabel(
                Globals.messages.getString("Select_RL_color"));
        constraints = DialogUtil.createConst(0, 1, 1, 1, 100, 100,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        colorManagementPanel.add(selectionRightToLeftColorLabel, constraints);

        selectionRightToLeftColorPicker = new ColorPicker(30, 20, Color.BLUE);
        constraints = DialogUtil.createConst(1, 1, 1, 1, 100, 100,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        colorManagementPanel.add(selectionRightToLeftColorPicker, constraints);

        JLabel selectionLeftToRightColorLabel = new JLabel(
                Globals.messages.getString("Select_LR_color"));
        constraints = DialogUtil.createConst(0, 2, 1, 1, 100, 100,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        colorManagementPanel.add(selectionLeftToRightColorLabel, constraints);

        selectionLeftToRightColorPicker = new ColorPicker(30, 20, Color.GREEN);
        constraints = DialogUtil.createConst(1, 2, 1, 1, 100, 100,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        colorManagementPanel.add(selectionLeftToRightColorPicker, constraints);

        JLabel selectedElementsColorLabel = new JLabel(
                Globals.messages.getString("Select_color"));
        constraints = DialogUtil.createConst(0, 3, 1, 1, 100, 100,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        colorManagementPanel.add(selectedElementsColorLabel, constraints);

        selectedElementsColorPicker = new ColorPicker(30, 20, Color.YELLOW);
        constraints = DialogUtil.createConst(1, 3, 1, 1, 100, 100,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        colorManagementPanel.add(selectedElementsColorPicker, constraints);

        // Column 2: Short text labels
        JLabel gridDotsColorLabel = new JLabel(
                Globals.messages.getString("Grid_dots_color"));
        constraints = DialogUtil.createConst(2, 0, 1, 1, 100, 100,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        colorManagementPanel.add(gridDotsColorLabel, constraints);

        gridDotsColorPicker = new ColorPicker(30, 20, Color.GRAY);
        constraints = DialogUtil.createConst(3, 0, 1, 1, 100, 100,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        colorManagementPanel.add(gridDotsColorPicker, constraints);

        JLabel gridLinesColorLabel = new JLabel(
                Globals.messages.getString("Grid_lines_color"));
        constraints = DialogUtil.createConst(2, 1, 1, 1, 100, 100,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        colorManagementPanel.add(gridLinesColorLabel, constraints);

        gridLinesColorPicker = new ColorPicker(30, 20, Color.LIGHT_GRAY);
        constraints = DialogUtil.createConst(3, 1, 1, 1, 100, 100,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        colorManagementPanel.add(gridLinesColorPicker, constraints);

        JLabel drawingColorLabel = new JLabel(
                Globals.messages.getString("Primitive_color"));
        constraints = DialogUtil.createConst(2, 2, 1, 1, 100, 100,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        colorManagementPanel.add(drawingColorLabel, constraints);

        drawingColorPicker = new ColorPicker(30, 20, Color.BLACK);
        constraints = DialogUtil.createConst(3, 2, 1, 1, 100, 100,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        colorManagementPanel.add(drawingColorPicker, constraints);

        // Adding both GroupBoxes to the main panel
        constraints = DialogUtil.createConst(0, 1, 1, 1, 100, 100,
                GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10));
        add(themeManagementPanel, constraints);

        constraints = DialogUtil.createConst(0, 2, 1, 1, 100, 100,
                GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10));
        add(colorManagementPanel, constraints);
    }

    /**
     Enables or disables the custom theme controls based on the state
     of the custom theme option.

     @param isEnabled true if custom theme controls should be enabled,
     false otherwise.
     */
    private void enableThemeControls(boolean isEnabled)
    {
        lightThemeRadioButton.setEnabled(isEnabled);
        darkThemeRadioButton.setEnabled(isEnabled);
        customThemeRadioButton.setEnabled(isEnabled);

        boolean customThemeSelected = 
                            isEnabled && customThemeRadioButton.isSelected();
        customThemePathField.setEnabled(customThemeSelected);
        browseCustomThemeButton.setEnabled(customThemeSelected);
    }

    /**
     Loads settings from SettingsManager and populates the UI components.
     This method is called during the initialization of the panel to restore
     the saved settings.
     */
    @Override
    public void loadSettings()
    {
        // Load theme settings
        String theme = settingsManager.get("THEME", "light");
        switch (theme) {
            case "dark":
                darkThemeRadioButton.setSelected(true);
                break;
            case "custom":
                customThemeRadioButton.setSelected(true);
                break;
            default:
                lightThemeRadioButton.setSelected(true);
                break;
        }

        customThemePathField.setText(
                settingsManager.get("CUSTOM_THEME_PATH", ""));
        boolean enableCustomThemes = settingsManager.get("ENABLE_CUSTOM_THEMES",
                "false").equals("true");
        enableCustomThemesCheckBox.setSelected(enableCustomThemes);

        enableThemeControls(enableCustomThemes);

        // Load color settings
        backgroundColorPicker.setColor(Color.decode(settingsManager.get(
                "BACKGROUND_COLOR", "#FFFFFF")));
        gridDotsColorPicker.setColor(Color.decode(settingsManager.get(
                "GRID_DOTS_COLOR", "#808080")));
        gridLinesColorPicker.setColor(Color.decode(settingsManager.get(
                "GRID_LINES_COLOR", "#D3D3D3")));
        drawingColorPicker.setColor(Color.decode(settingsManager.get(
                "DRAWING_COLOR", "#000000")));
        selectionRightToLeftColorPicker.setColor(Color.decode(
                settingsManager.get("SELECTION_RTL_COLOR", "#0000FF")));
        selectionLeftToRightColorPicker.setColor(Color.decode(
                settingsManager.get("SELECTION_LTR_COLOR", "#008000")));
        selectedElementsColorPicker.setColor(Color.decode(settingsManager.get(
                "SELECTED_ELEMENTS_COLOR", "#FFFF00")));
    }

    /**
     Saves the settings from the UI components into the SettingsManager.
     This method is called when the settings are applied or saved by the user.
     */
    @Override
    public void saveSettings()
    {
        // Save theme settings
        String theme = "light";  // Default to light theme
        if (darkThemeRadioButton.isSelected()) {
            theme = "dark";
        } else {
            if (customThemeRadioButton.isSelected()) {
                theme = "custom";
            }
        }

        settingsManager.put("THEME", theme);
        settingsManager.put("CUSTOM_THEME_PATH", customThemePathField.getText());
        settingsManager.put("ENABLE_CUSTOM_THEMES",
                enableCustomThemesCheckBox.isSelected() ? "true" : "false");

        // Save color settings
        settingsManager.put("BACKGROUND_COLOR", String.format("#%06X",
                (0xFFFFFF & backgroundColorPicker.getColor().getRGB())));
        settingsManager.put("GRID_DOTS_COLOR", String.format("#%06X",
                (0xFFFFFF & gridDotsColorPicker.getColor().getRGB())));
        settingsManager.put("GRID_LINES_COLOR", String.format("#%06X",
                (0xFFFFFF & gridLinesColorPicker.getColor().getRGB())));
        settingsManager.put("DRAWING_COLOR", String.format("#%06X",
                (0xFFFFFF & drawingColorPicker.getColor().getRGB())));
        settingsManager.put("SELECTION_RTL_COLOR", String.format("#%06X",
                (0xFFFFFF & selectionRightToLeftColorPicker.getColor().getRGB())));
        settingsManager.put("SELECTION_LTR_COLOR", String.format("#%06X",
                (0xFFFFFF & selectionLeftToRightColorPicker.getColor().getRGB())));
        settingsManager.put("SELECTED_ELEMENTS_COLOR", String.format("#%06X",
                (0xFFFFFF & selectedElementsColorPicker.getColor().getRGB())));
    }
}
