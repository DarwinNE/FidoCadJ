package fidocadj.dialogs.settings;

import fidocadj.dialogs.controls.ColorPicker;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;

import fidocadj.globals.Globals;
import fidocadj.dialogs.controls.MinimumSizeDialog;
import fidocadj.dialogs.controls.LibraryPanel;
import fidocadj.dialogs.controls.DialogUtil;

/** The DialogSettings class implements a modal dialog, which allows the user to
 * choose which circuit drawing settings (size, anti aliasing, profiling) should
 * be activated.
 *
 * <pre>
 *
 * This file is part of FidoCadJ.
 *
 * FidoCadJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FidoCadJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FidoCadJ. If not,
 * @see <a href=http://www.gnu.org/licenses/>http://www.gnu.org/licenses/</a>.
 *
 * Copyright 2007-2024 by Davide Bucci, Manuel Finessi
 * </pre>
 */
public final class DialogSettings extends MinimumSizeDialog
{

    public double zoomValue;
    public boolean profileTime;
    public boolean antiAlias;
    public boolean textToolbar;
    public boolean smallIconsToolbar;
    public int gridSize;
    public boolean extStrict;
    public boolean shiftCP;
    public double stroke_size_straight_i;
    public double connectionSize_i;
    public String libDirectory;
    public int pcblinewidth_i;
    public int pcbpadwidth_i;
    public int pcbpadheight_i;
    public int pcbpadintw_i;
    public String macroFont_s;
    public int macroSize_i;

    private final JFrame parent;

    private JCheckBox antiAlias_CB;
    private JCheckBox profile_CB;
    private JCheckBox extStrict_CB;
    private JCheckBox shiftCP_CB;
    private JTextField gridWidth;
    private JTextField libD;
    private JCheckBox textToolbar_CB;
    private JCheckBox smallIconsToolbar_CB;
    private JTextField pcblinewidth;
    private JTextField pcbpadwidth;
    private JTextField pcbpadheight;
    private JTextField pcbpadintw;
    private JTextField connectionSize;
    private JTextField macroSize;
    private JTextField stroke_size_straight;
    private JComboBox<String> comboFont;

    /** Standard constructor.
     *
     * @param parentFrame the parent frame.
     * @param currentZoom the current zoom.
     * @param profileStatus the current profile status.
     * @param antiAliasingState the current anti aliasing state.
     * @param gridState the current grid activation state.
     * @param libDir the current library directory.
     * @param toolBarTextState the current text in the toolbar state.
     * @param toolBarSmallIconState the current small icon state.
     * @param pcbLineWidth the current PCB line width.
     * @param pcbPadWidth the current PCB pad width.
     * @param pcbPadHeight the current PCB pad height.
     * @param pcbPadHoleDiameter the current PCB pad internal hole diameter.
     * @param fidocadCompatibility strict compatibility with ...
     *                             FidoCAD for Windows.
     * @param macroFont the current Macro font.
     * @param strokeWidth stroke width to be used for segments ...
     *                    and straight lines.
     * @param connSize connection size.
     * @param macroTextHeight text height for macros.
     * @param shiftDuringCopyAndPaste shift during copy and paste.
     */
    public DialogSettings(JFrame parentFrame, double currentZoom,
            boolean profileStatus,
            boolean antiAliasingState, int gridState, String libDir,
            boolean toolBarTextState, boolean toolBarSmallIconState,
            int pcbLineWidth, int pcbPadWidth, int pcbPadHeight,
            int pcbPadHoleDiameter, boolean fidocadCompatibility,
            String macroFont, double strokeWidth, double connSize,
            int macroTextHeight, boolean shiftDuringCopyAndPaste)
    {
        super(600, 450, parentFrame, Globals.messages.getString("Cir_opt_t"),
                true);
        addComponentListener(this);

        shiftCP = shiftDuringCopyAndPaste;
        parent = parentFrame;
        zoomValue = currentZoom;
        profileTime = profileStatus;
        antiAlias = antiAliasingState;
        gridSize = gridState;
        libDirectory = libDir;
        textToolbar = toolBarTextState;
        smallIconsToolbar = toolBarSmallIconState;

        extStrict = fidocadCompatibility;
        macroFont_s = macroFont;

        pcblinewidth_i = pcbLineWidth;
        pcbpadwidth_i = pcbPadWidth;
        pcbpadheight_i = pcbPadHeight;
        pcbpadintw_i = pcbPadHoleDiameter;
        connectionSize_i = connSize;
        macroSize_i = macroTextHeight;

        stroke_size_straight_i = strokeWidth;

        setSize(600, 500);

        GridBagConstraints constraints;
        Container contentPane = getContentPane();

        contentPane.setLayout(new GridBagLayout());

        JTabbedPane tabsPane = new JTabbedPane();

        // Creates four panes and then populate them.
        tabsPane.addTab(Globals.messages.getString("Restart"),
                createRestartPane());

        tabsPane.addTab(Globals.messages.getString("Drawing"),
                createDrawingOptPanel());

        tabsPane.addTab(Globals.messages.getString("PCBsizes"),
                createPCBsizePanel());

        tabsPane.addTab(Globals.messages.getString("Theme_management"),
                createThemePanel());

        constraints = DialogUtil.createConst(0, 0, 3, 1, 100, 100,
                GridBagConstraints.EAST, GridBagConstraints.BOTH,
                new Insets(6, 20, 6, 20));

        contentPane.add(tabsPane, constraints);

        // Put the OK and Cancel buttons and make them active.
        JButton ok = new JButton(Globals.messages.getString("Ok_btn"));
        JButton cancel = new JButton(Globals.messages.getString("Cancel_btn"));

        constraints = DialogUtil.createConst(0, 1, 3, 1, 100, 100,
                GridBagConstraints.EAST, GridBagConstraints.BOTH,
                new Insets(6, 20, 20, 20));

        // Put the OK and Cancel buttons and make them active.
        Box b = Box.createHorizontalBox();
        b.add(Box.createHorizontalGlue());
        ok.setPreferredSize(cancel.getPreferredSize());

        if (Globals.okCancelWinOrder) {
            b.add(ok);
            b.add(Box.createHorizontalStrut(12));
            b.add(cancel);

        } else {
            b.add(cancel);
            b.add(Box.createHorizontalStrut(12));
            b.add(ok);
        }
        contentPane.add(b, constraints);            // Add cancel button

        ok.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent evt)
            {
                int ng = -1;
                shiftCP = shiftCP_CB.isSelected();
                antiAlias = antiAlias_CB.isSelected();
                profileTime = profile_CB.isSelected();
                textToolbar = textToolbar_CB.isSelected();
                smallIconsToolbar = smallIconsToolbar_CB.isSelected();
                extStrict = extStrict_CB.isSelected();
                macroFont_s = (String) comboFont.getSelectedItem();
                int s = 0;

                try {
                    s = Integer.parseInt(macroSize.getText().trim());

                    ng = Integer.parseInt(gridWidth.getText().trim());
                    libDirectory = libD.getText().trim();

                    pcblinewidth_i = Integer.parseInt(
                            pcblinewidth.getText().trim());

                    pcbpadwidth_i = Integer.parseInt(
                            pcbpadwidth.getText().trim());

                    pcbpadheight_i = Integer.parseInt(
                            pcbpadheight.getText().trim());

                    pcbpadintw_i = Integer.parseInt(
                            pcbpadintw.getText().trim());

                    stroke_size_straight_i = Double.parseDouble(
                            stroke_size_straight.getText().trim());

                    connectionSize_i = Double.parseDouble(
                            connectionSize.getText().trim());

                    if (ng > 0) {
                        gridSize = ng;
                    } else {
                        JOptionPane.showMessageDialog(parent,
                                Globals.messages.getString("Format_invalid"),
                                "",
                                JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    if (s > 0) {
                        macroSize_i = s;
                    } else {
                        JOptionPane.showMessageDialog(null,
                                Globals.messages.getString("Font_size_invalid"),
                                "",
                                JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    setVisible(false);
                } catch (NumberFormatException eE) {
                    JOptionPane.showMessageDialog(parent,
                            Globals.messages.getString("Format_invalid")
                            + " " + eE.getMessage(), "",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        cancel.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent evt)
            {
                setVisible(false);
            }
        });
        // Here is an action in which the dialog is closed

        AbstractAction cancelAction = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                setVisible(false);
            }
        };
        DialogUtil.addCancelEscape(this, cancelAction);
        pack();
        DialogUtil.center(this);
        getRootPane().setDefaultButton(ok);
    }

    /** Creates the panel dedicated to the startup options of FidoCadJ.
     *
     * @return the panel containing startup options.
     */
    private JPanel createRestartPane()
    {
        JPanel restartOptionPanel = new JPanel();

        restartOptionPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        restartOptionPanel.setOpaque(false);

        JLabel liblbl = new JLabel(Globals.messages.getString("lib_dir"));

        constraints = DialogUtil.createConst(0, 0, 1, 1, 0.01, 0.01,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(20, 40, 6, 0));

        // Add lib dir label
        restartOptionPanel.add(liblbl, constraints);

        libD = new JTextField(10);
        libD.setText(libDirectory);
        constraints = DialogUtil.createConst(0, 1, 1, 1, 0.01, 0.01,
                GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
                new Insets(6, 40, 6, 20));

        restartOptionPanel.add(libD, constraints);       // Add lib dir tf

        JButton libB = new JButton(Globals.messages.getString("Browse"));
        constraints.insets = new Insets(0, 0, 0, 40);
        libB.setOpaque(false);
        constraints.gridx = 1;
        constraints.gridy = 1;

        restartOptionPanel.add(libB, constraints);      // Add lib dir button
        libB.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent evt)
            {
                String din;

                if (Globals.useNativeFileDialogs) {
                    // Use the native (AWT) file dialogs instead of Swing's
                    FileDialog fd = new FileDialog(parent,
                            Globals.messages.getString("Select_lib_directory"),
                            FileDialog.LOAD);

                    fd.setDirectory(libD.getText());
                    System.setProperty("apple.awt.fileDialogForDirectories",
                            "true");
                    fd.setVisible(true);
                    System.setProperty("apple.awt.fileDialogForDirectories",
                            "false");
                    if (fd.getDirectory() == null || fd.getFile() == null) {
                        din = null;
                    } else {
                        din = new File(fd.getDirectory(), fd.getFile()).
                                getPath();
                    }
                } else {
                    // Use Swing's file dialog.
                    JFileChooser fc = new JFileChooser(
                            new File(libD.getText()).getPath());
                    fc.setDialogTitle(
                            Globals.messages.getString("Select_lib_directory"));
                    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                    // Dock library panel.
                    new LibraryPanel(fc);

                    int r = fc.showOpenDialog(null);
                    if (r == JFileChooser.APPROVE_OPTION) {
                        din = fc.getSelectedFile().getPath();
                    } else {
                        din = null;
                    }
                }

                if (din != null) {
                    libD.setText(din);
                }
            }
        });

        JLabel restw = new JLabel(Globals.messages.getString("restart_info"));
        constraints = DialogUtil.createConst(0, 3, 1, 1, 0.01, 0.01,
                GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
                new Insets(6, 40, 6, 0));
        restw.setForeground(Color.BLUE);
        restartOptionPanel.add(restw, constraints);

        textToolbar_CB = new JCheckBox(
                Globals.messages.getString("TextToolbar"));

        textToolbar_CB.setSelected(textToolbar);
        textToolbar_CB.setOpaque(false);
        constraints = DialogUtil.createConst(0, 4, 1, 1, 0.01, 0.01,
                GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
                new Insets(6, 40, 6, 0));

        restartOptionPanel.add(textToolbar_CB, constraints);

        smallIconsToolbar_CB = new JCheckBox(Globals.messages.getString(
                "SmallIcons"));
        smallIconsToolbar_CB.setSelected(smallIconsToolbar);
        smallIconsToolbar_CB.setOpaque(false);

        constraints = DialogUtil.createConst(0, 5, 1, 1, 0.01, 0.01,
                GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
                new Insets(6, 40, 6, 0));

        // Add small icons
        restartOptionPanel.add(smallIconsToolbar_CB, constraints);
        
        
        extStrict_CB = new JCheckBox(Globals.messages.getString(
                "strict_FC_comp"));
        extStrict_CB.setSelected(extStrict);
        extStrict_CB.setOpaque(false);
        constraints = DialogUtil.createConst(0, 6, 1, 1, 0.05, 0.05,
                GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
                new Insets(6, 40, 6, 0));
        restartOptionPanel.add(extStrict_CB, constraints); // Strict FidoCAD

        return restartOptionPanel;
    }

    /** Creates the panel dedicated to the drawing options of FidoCadJ.
     *
     * @return the panel containing the drawing options.
     */
    private JPanel createDrawingOptPanel()
    {
        JPanel drawingOptPanel = new JPanel();

        GridBagConstraints constraints = new GridBagConstraints();
        drawingOptPanel.setLayout(new GridBagLayout());
        drawingOptPanel.setOpaque(false);

        profile_CB = new JCheckBox(Globals.messages.getString("Profile"));
        profile_CB.setOpaque(false);
        profile_CB.setSelected(profileTime);

        constraints = DialogUtil.createConst(1, 1, 2, 1, 100, 100,
                GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
                new Insets(6, 6, 6, 20));

        if (Globals.isBeta) {
            drawingOptPanel.add(profile_CB, constraints);   // Add profiler cb
        }

        JLabel gridlbl = new JLabel(Globals.messages.getString("Grid_width"));

        constraints = DialogUtil.createConst(0, 2, 1, 1, 100, 100,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));

        drawingOptPanel.add(gridlbl, constraints);          // Add Grid label

        gridWidth = new JTextField(10);
        gridWidth.setText("" + gridSize);

        constraints = DialogUtil.createConst(1, 2, 1, 1, 100, 100,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));

        drawingOptPanel.add(gridWidth, constraints);        // Add grid width tf

        /** ********************************************************************
         * Stroke sizes
         **********************************************************************/
        JLabel connectionSizelbl = new JLabel(Globals.messages.getString(
                "connection_size"));
        constraints = DialogUtil.createConst(0, 8, 1, 1, 100, 100,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        drawingOptPanel.add(connectionSizelbl, constraints);

        connectionSize = new JTextField(10);
        connectionSize.setText("" + connectionSize_i);
        constraints = DialogUtil.createConst(1, 8, 1, 1, 100, 100,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        drawingOptPanel.add(connectionSize, constraints);

        JLabel strokeSizeStrlbl = new JLabel(Globals.messages.getString(
                "stroke_size_straight"));
        constraints = DialogUtil.createConst(0, 9, 1, 1, 100, 100,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        drawingOptPanel.add(strokeSizeStrlbl, constraints);

        stroke_size_straight = new JTextField(10);
        stroke_size_straight.setText("" + stroke_size_straight_i);
        constraints = DialogUtil.createConst(1, 9, 1, 1, 100, 100,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));

        drawingOptPanel.add(stroke_size_straight, constraints);

        /** ********************************************************************
         * Macro font
         **********************************************************************/
        JLabel macroFontlbl = new JLabel(Globals.messages.getString(
                "macrofont"));
        constraints = DialogUtil.createConst(0, 11, 1, 1, 100, 100,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        drawingOptPanel.add(macroFontlbl, constraints);// Macro font selection

        // Get all installed font families
        GraphicsEnvironment gE;

        gE = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] s = gE.getAvailableFontFamilyNames();
        comboFont = new JComboBox<String>();

        //System.out.println(macroFont);
        for (int i = 0; i < s.length; ++i) {
            comboFont.addItem(s[i]);
            if (s[i].equals(macroFont_s)) {
                comboFont.setSelectedIndex(i);
            }
        }
        constraints = DialogUtil.createConst(1, 11, 1, 1, 100, 100,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));

        drawingOptPanel.add(comboFont, constraints);// Add primitive font combo

        JLabel macroSizelbl = new JLabel(Globals.messages.getString(
                "macroSize"));
        constraints = DialogUtil.createConst(0, 12, 1, 1, 100, 100,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));

        drawingOptPanel.add(macroSizelbl, constraints);

        macroSize = new JTextField(10);
        macroSize.setText("" + macroSize_i);
        constraints = DialogUtil.createConst(1, 12, 1, 1, 100, 100,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        drawingOptPanel.add(macroSize, constraints);

        antiAlias_CB = new JCheckBox(Globals.messages.getString("Anti_al"));
        antiAlias_CB.setSelected(antiAlias);
        antiAlias_CB.setOpaque(false);
        constraints = DialogUtil.createConst(1, 13, 1, 1, 100, 100,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 40));

        drawingOptPanel.add(antiAlias_CB, constraints);     // Add antialias cb

        shiftCP_CB = new JCheckBox(Globals.messages.getString("Shift_cp"));
        shiftCP_CB.setSelected(shiftCP);
        shiftCP_CB.setOpaque(false);
        constraints = DialogUtil.createConst(1, 14, 1, 1, 100, 100,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 40));
        drawingOptPanel.add(shiftCP_CB, constraints);       // Add shift C/P cb

        return drawingOptPanel;
    }

    /** Creates the panel dedicated to the default PCB track and pad sizes
     * options of FidoCadJ.
     *
     * @return the panel containing the PCB settings.
     */
    private JPanel createPCBsizePanel()
    {
        /** ********************************************************************
         * PCB line and pad default sizes
         **********************************************************************/
        JPanel pcbSizePanel = new JPanel();

        GridBagConstraints constraints = new GridBagConstraints();
        pcbSizePanel.setLayout(new GridBagLayout());
        pcbSizePanel.setOpaque(false);

        JLabel pcblinelbl = new JLabel(Globals.messages.getString(
                "pcbline_width"));
        constraints = DialogUtil.createConst(0, 0, 1, 1, 0.5, 0.01,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(30, 6, 6, 6));
        pcbSizePanel.add(pcblinelbl, constraints);          // Add pcbline label

        pcblinewidth = new JTextField(10);
        pcblinewidth.setText("" + pcblinewidth_i);
        constraints = DialogUtil.createConst(1, 0, 1, 1, 0.5, 0.01,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(30, 6, 6, 6));

        pcbSizePanel.add(pcblinewidth, constraints);    // Add pcbline width tf

        JLabel pcbpadwidthlbl = new JLabel(Globals.messages.getString(
                "pcbpad_width"));
        constraints = DialogUtil.createConst(0, 1, 1, 1, 0.5, 0.01,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(8, 6, 6, 6));
        pcbSizePanel.add(pcbpadwidthlbl, constraints); // Add pcbpad width label

        pcbpadwidth = new JTextField(10);
        pcbpadwidth.setText("" + pcbpadwidth_i);
        constraints = DialogUtil.createConst(1, 1, 1, 1, 0.5, 0.01,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(8, 6, 6, 6));

        pcbSizePanel.add(pcbpadwidth, constraints);     // Add pcbpad width tf

        JLabel pcbpadheightlbl = new JLabel(Globals.messages.getString(
                "pcbpad_height"));
        constraints = DialogUtil.createConst(0, 2, 1, 1, 0.5, 0.01,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(8, 6, 6, 6));
        pcbSizePanel.add(pcbpadheightlbl, constraints); // Add pcbline label

        pcbpadheight = new JTextField(10);
        pcbpadheight.setText("" + pcbpadheight_i);
        constraints = DialogUtil.createConst(1, 2, 1, 1, 0.5, 0.01,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(8, 6, 6, 6));
        pcbSizePanel.add(pcbpadheight, constraints);  // Add pcbline height tf

        JLabel pcbpadintwlbl = new JLabel(Globals.messages.getString(
                "pcbpad_intw"));
        constraints = DialogUtil.createConst(0, 3, 1, 1, 0.5, 0.01,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(8, 6, 6, 6));
        pcbSizePanel.add(pcbpadintwlbl, constraints);// Add pcbpad int w label

        pcbpadintw = new JTextField(10);
        pcbpadintw.setText("" + pcbpadintw_i);
        constraints = DialogUtil.createConst(1, 3, 1, 1, 0.5, 0.01,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(8, 6, 6, 6));
        pcbSizePanel.add(pcbpadintw, constraints);      // Add pcbline width tf

        constraints = DialogUtil.createConst(0, 4, 2, 1, 0.0, 1.0, 
            GridBagConstraints.CENTER, GridBagConstraints.BOTH, 
            new Insets(0, 0, 0, 0));
        pcbSizePanel.add(new JLabel(), constraints);
        
        return pcbSizePanel;
    }

    /** Creates the panel dedicated to the theme settings
     *
     * @return the panel containing the theme settings.
     */
    private JPanel createThemePanel()
    {
        JPanel themePanel = new JPanel();
        themePanel.setLayout(new GridBagLayout());
        themePanel.setOpaque(false);

        GridBagConstraints constraints = new GridBagConstraints();

        // Groupbox "Theme Management"
        JPanel themeManagementPanel = new JPanel();
        themeManagementPanel.setBorder(
                BorderFactory.createTitledBorder("Gestione tema"));
        themeManagementPanel.setLayout(new GridBagLayout());

        // Checkbox to enable custom themes support
        JCheckBox enableCustomThemes_CB = new JCheckBox(
                "Abilita il supporto per temi personalizzati");
        enableCustomThemes_CB.setOpaque(false);
        constraints = DialogUtil.createConst(0, 0, 2, 1, 100, 100,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(6, 6, 6, 6));
        themeManagementPanel.add(enableCustomThemes_CB, constraints);

        // Radio buttons for theme selection
        JRadioButton lightTheme_RB = new JRadioButton("Tema light");
        JRadioButton darkTheme_RB = new JRadioButton("Tema dark");
        JRadioButton customTheme_RB = new JRadioButton("Tema personalizzato");

        ButtonGroup themeGroup = new ButtonGroup();
        themeGroup.add(lightTheme_RB);
        themeGroup.add(darkTheme_RB);
        themeGroup.add(customTheme_RB);

        // Set "Tema light" as the default selected option
        lightTheme_RB.setSelected(true);

        constraints = DialogUtil.createConst(0, 1, 1, 1, 100, 100,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        themeManagementPanel.add(lightTheme_RB, constraints);

        constraints = DialogUtil.createConst(0, 2, 1, 1, 100, 100,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        themeManagementPanel.add(darkTheme_RB, constraints);

        constraints = DialogUtil.createConst(0, 3, 1, 1, 100, 100,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        themeManagementPanel.add(customTheme_RB, constraints);

        // TextBox and Button for selecting custom theme file
        JTextField customThemePath_TF = new JTextField(20);
        customThemePath_TF.setEnabled(false);
        JButton browseCustomTheme_Btn = new JButton("Sfoglia");
        browseCustomTheme_Btn.setEnabled(false);

        constraints = DialogUtil.createConst(1, 3, 1, 1, 100, 100,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(6, 6, 6, 6));
        themeManagementPanel.add(customThemePath_TF, constraints);

        constraints = DialogUtil.createConst(2, 3, 1, 1, 100, 100,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        themeManagementPanel.add(browseCustomTheme_Btn, constraints);

        // Enable/Disable Controls based on Custom Themes
        enableCustomThemes_CB.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                boolean enabled = enableCustomThemes_CB.isSelected();
                lightTheme_RB.setEnabled(enabled);
                darkTheme_RB.setEnabled(enabled);
                customTheme_RB.setEnabled(enabled);
                if (!enabled) {
                    customThemePath_TF.setEnabled(false);
                    browseCustomTheme_Btn.setEnabled(false);
                } else {
                    customThemePath_TF.setEnabled(customTheme_RB.isSelected());
                    browseCustomTheme_Btn.setEnabled(customTheme_RB.isSelected());
                }
            }
        });

        ActionListener themeSelectionListener = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                boolean customSelected = customTheme_RB.isSelected();
                customThemePath_TF.setEnabled(customSelected);
                browseCustomTheme_Btn.setEnabled(customSelected);
            }
        };

        lightTheme_RB.addActionListener(themeSelectionListener);
        darkTheme_RB.addActionListener(themeSelectionListener);
        customTheme_RB.addActionListener(themeSelectionListener);

        // Groupbox "Color Management"
        JPanel colorManagementPanel = new JPanel();
        colorManagementPanel.setBorder(
                BorderFactory.createTitledBorder("Gestione colori"));
        colorManagementPanel.setLayout(new GridBagLayout());

        // Column 1: Long text labels
        JLabel backgroundColorLabel = new JLabel(
                "Colore di sfondo dell'area di disegno");
        constraints = DialogUtil.createConst(0, 0, 1, 1, 100, 100,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        colorManagementPanel.add(backgroundColorLabel, constraints);

        ColorPicker backgroundColorPicker = new ColorPicker(30, 20, Color.WHITE);
        constraints = DialogUtil.createConst(1, 0, 1, 1, 100, 100,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        colorManagementPanel.add(backgroundColorPicker, constraints);

        JLabel selectionRightToLeftColorLabel = new JLabel(
                "Colore rettangolo di selezione da destra a sinistra");
        constraints = DialogUtil.createConst(0, 1, 1, 1, 100, 100,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        colorManagementPanel.add(selectionRightToLeftColorLabel, constraints);

        ColorPicker selectionRightToLeftColorPicker = new ColorPicker(30, 20,
                Color.BLUE);
        constraints = DialogUtil.createConst(1, 1, 1, 1, 100, 100,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        colorManagementPanel.add(selectionRightToLeftColorPicker, constraints);

        JLabel selectionLeftToRightColorLabel = new JLabel(
                "Colore rettangolo di selezione da sinistra a destra");
        constraints = DialogUtil.createConst(0, 2, 1, 1, 100, 100,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        colorManagementPanel.add(selectionLeftToRightColorLabel, constraints);

        ColorPicker selectionLeftToRightColorPicker = new ColorPicker(30, 20,
                Color.GREEN);
        constraints = DialogUtil.createConst(1, 2, 1, 1, 100, 100,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        colorManagementPanel.add(selectionLeftToRightColorPicker, constraints);

        JLabel selectedElementsColorLabel = new JLabel(
                "Colore elementi selezionati");
        constraints = DialogUtil.createConst(0, 3, 1, 1, 100, 100,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        colorManagementPanel.add(selectedElementsColorLabel, constraints);

        ColorPicker selectedElementsColorPicker = new ColorPicker(30, 20,
                Color.YELLOW);
        constraints = DialogUtil.createConst(1, 3, 1, 1, 100, 100,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        colorManagementPanel.add(selectedElementsColorPicker, constraints);

        // Column 2: Short text labels
        JLabel gridDotsColorLabel = new JLabel("Colore griglia (punti)");
        constraints = DialogUtil.createConst(2, 0, 1, 1, 100, 100,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        colorManagementPanel.add(gridDotsColorLabel, constraints);

        ColorPicker gridDotsColorPicker = new ColorPicker(30, 20, Color.GRAY);
        constraints = DialogUtil.createConst(3, 0, 1, 1, 100, 100,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        colorManagementPanel.add(gridDotsColorPicker, constraints);

        JLabel gridLinesColorLabel = new JLabel("Colore griglia (linee)");
        constraints = DialogUtil.createConst(2, 1, 1, 1, 100, 100,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        colorManagementPanel.add(gridLinesColorLabel, constraints);

        ColorPicker gridLinesColorPicker = new ColorPicker(30, 20,
                Color.LIGHT_GRAY);
        constraints = DialogUtil.createConst(3, 1, 1, 1, 100, 100,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        colorManagementPanel.add(gridLinesColorPicker, constraints);

        JLabel drawingColorLabel = new JLabel("Colore di default per il disegno");
        constraints = DialogUtil.createConst(2, 2, 1, 1, 100, 100,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        colorManagementPanel.add(drawingColorLabel, constraints);

        ColorPicker drawingColorPicker = new ColorPicker(30, 20, Color.BLACK);
        constraints = DialogUtil.createConst(3, 2, 1, 1, 100, 100,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(6, 6, 6, 6));
        colorManagementPanel.add(drawingColorPicker, constraints);

        // Adding both GroupBoxes to the main panel
        constraints = DialogUtil.createConst(0, 0, 1, 1, 100, 100,
                GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10));
        themePanel.add(themeManagementPanel, constraints);

        constraints = DialogUtil.createConst(0, 1, 1, 1, 100, 100,
                GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10));
        themePanel.add(colorManagementPanel, constraints);

        return themePanel;
    }
}
