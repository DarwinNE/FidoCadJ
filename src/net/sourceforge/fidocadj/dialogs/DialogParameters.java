package net.sourceforge.fidocadj.dialogs;

import java.awt.*;

import javax.swing.*;

import java.awt.event.*;
import java.util.*;

import net.sourceforge.fidocadj.dialogs.*;
import net.sourceforge.fidocadj.dialogs.OSKeybPanel.KEYBMODES;
import net.sourceforge.fidocadj.globals.*;
import net.sourceforge.fidocadj.layers.*;
import net.sourceforge.fidocadj.graphic.*;

/**
 * Allows to create a generic dialog, capable of displaying and let the user
 * modify the parameters of a graphic primitive. The idea is that the dialog
 * uses a ParameterDescripion vector which contains all the elements, their
 * description as well as the type. Depending on the contents of the array, the
 * window will be created automagically.
 * 
 * <pre>
 * 
 *  This file is part of FidoCadJ.
 * 
 *     FidoCadJ is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     FidoCadJ is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with FidoCadJ.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *  Copyright 2007-2015 by Davide Bucci
 * </pre>
 */

public class DialogParameters extends JDialog implements ComponentListener
{
    private int MIN_WIDTH = 450;
    private int MIN_HEIGHT = 350;
    private static final int MAX = 20;

    // Maximum number of user interface elements of the same type present
    // in the dialog window.
    private static final int MAX_ELEMENTS = 100;

    public boolean active; // true if the user selected Ok

    // Text box array and counter
    private final JTextField jtf[];
    private int tc; // NOPMD this field can NOT be final! It is a counter.

    // Check box array and counter
    private final JCheckBox jcb[];
    private int cc; // NOPMD this field can NOT be final! It is a counter.

    private final JComboBox jco[];
    private int co; // NOPMD this field can NOT be final! It is a counter.

    private final Vector<ParameterDescription> v;

    OSKeybPanel keyb1,keyb2;
    JTabbedPane keyb = new JTabbedPane();

    /**
     * Programmatically build a dialog frame containing the appropriate
     * elements, in order to let the user modify the characteristics of a
     * graphic primitive.
     * 
     * @param vec
     *            a ParameterDescription array containing the value and the
     *            description of each parameter that should be edited by the
     *            user.
     * @param strict
     *            true if a strict compatibility with FidoCAD is required
     * @param layers
     *            a vector containing the layers
     */
    // Here some legacy code makes use of generics. They are tested, so there
    // is no risk of an actual error, but Java issues a warning.
    @SuppressWarnings("unchecked")
    public DialogParameters(JFrame parent, Vector<ParameterDescription> vec,
            boolean strict, Vector<LayerDesc> layers) {
        super(parent, Globals.messages.getString("Param_opt"), true);

        keyb1 = new OSKeybPanel(KEYBMODES.GREEK);
        keyb2 = new OSKeybPanel(KEYBMODES.MISC);
        keyb1.setField(this);
        keyb2.setField(this);       
        keyb.addTab("Greek", keyb1);
        keyb.addTab("Misc", keyb2);
        keyb.setVisible(false);
        v = vec;
        

        int ycount = 0;

        // We create dynamically all the needed elements.
        // For this reason, we work on arrays of the potentially useful Swing
        // objects.

        jtf = new JTextField[MAX_ELEMENTS];
        jcb = new JCheckBox[MAX_ELEMENTS];
        jco = new JComboBox[MAX_ELEMENTS];

        active = false;
        addComponentListener(this);

        GridBagLayout bgl = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        Container contentPane = getContentPane();
        contentPane.setLayout(bgl);
        boolean extStrict = strict;

        ParameterDescription pd;

        int top = 0;

        JLabel lab;

        tc = 0;
        cc = 0;
        co = 0;

        // We process all parameter passed. Depending on its type, a
        // corresponding interface element will be created.
        // A symmetrical operation is done when validating parameters.

        for (ycount = 0; ycount < v.size(); ++ycount) {
            if (ycount > MAX)
                break;

            pd = (ParameterDescription) v.elementAt(ycount);

            // We do not need to store label objects, since we do not need
            // to retrieve data from them.

            lab = new JLabel(pd.description);
            constraints.weightx = 100;
            constraints.weighty = 100;
            constraints.gridx = 1;
            constraints.gridy = ycount;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;

            // The first element needs a little bit more space at the top.
            if (ycount == 0)
                top = 10;
            else
                top = 0;

            // Here we configure the grid layout

            constraints.insets = new Insets(top, 20, 0, 6);

            constraints.fill = GridBagConstraints.VERTICAL;
            constraints.anchor = GridBagConstraints.EAST;
            lab.setEnabled(!(pd.isExtension && extStrict));

            if (!(pd.parameter instanceof Boolean))
                contentPane.add(lab, constraints);

            constraints.anchor = GridBagConstraints.WEST;
            constraints.insets = new Insets(top, 0, 0, 0);
            constraints.fill = GridBagConstraints.HORIZONTAL;

            // Now, depending on the type of parameter we create interface
            // elements and we populate the dialog.

            if (pd.parameter instanceof PointG) {
                jtf[tc] = new JTextField(10);
                jtf[tc].setText("" + ((PointG) (pd.parameter)).x);
                constraints.weightx = 100;
                constraints.weighty = 100;
                constraints.gridx = 2;
                constraints.gridy = ycount;
                constraints.gridwidth = 1;
                constraints.gridheight = 1;
                // Disable FidoCadJ extensions in the strict compatibility mode
                jtf[tc].setEnabled(!(pd.isExtension && extStrict));

                contentPane.add(jtf[tc++], constraints);

                jtf[tc] = new JTextField(10);
                jtf[tc].setText("" + ((PointG) (pd.parameter)).y);
                constraints.weightx = 100;
                constraints.weighty = 100;
                constraints.gridx = 3;
                constraints.gridy = ycount;
                constraints.gridwidth = 1;
                constraints.gridheight = 1;
                constraints.insets = new Insets(top, 6, 0, 20);
                constraints.fill = GridBagConstraints.HORIZONTAL;
                jtf[tc].setEnabled(!(pd.isExtension && extStrict));
                contentPane.add(jtf[tc++], constraints);
            } else if (pd.parameter instanceof String) {
                jtf[tc] = new JTextField(24);
                jtf[tc].setText((String) (pd.parameter));
                // If we have a String text field in the first position, its
                // contents should be evidenced, since it is supposed to be
                // the most important field (e.g. for the AdvText primitive)
                if (ycount == 0)
                    jtf[tc].selectAll();
                constraints.weightx = 100;
                constraints.weighty = 100;
                constraints.gridx = 2;
                constraints.gridy = ycount;
                constraints.gridwidth = 2;
                constraints.gridheight = 1;
                constraints.insets = new Insets(top, 0, 0, 20);
                constraints.fill = GridBagConstraints.HORIZONTAL;
                jtf[tc].setEnabled(!(pd.isExtension && extStrict));

                contentPane.add(jtf[tc++], constraints);

            } else if (pd.parameter instanceof Boolean) {
                jcb[cc] = new JCheckBox(pd.description);
                jcb[cc].setSelected(((Boolean) (pd.parameter)).booleanValue());
                constraints.weightx = 100;
                constraints.weighty = 100;
                constraints.gridx = 2;
                constraints.gridy = ycount;
                constraints.gridwidth = 2;
                constraints.gridheight = 1;
                constraints.insets = new Insets(top, 0, 0, 20);
                constraints.fill = GridBagConstraints.HORIZONTAL;
                jcb[cc].setEnabled(!(pd.isExtension && extStrict));
                contentPane.add(jcb[cc++], constraints);
            } else if (pd.parameter instanceof Integer) {
                jtf[tc] = new JTextField(24);
                jtf[tc].setText(((Integer) pd.parameter).toString());
                constraints.weightx = 100;
                constraints.weighty = 100;
                constraints.gridx = 2;
                constraints.gridy = ycount;
                constraints.gridwidth = 2;
                constraints.gridheight = 1;
                constraints.insets = new Insets(top, 0, 0, 20);
                constraints.fill = GridBagConstraints.HORIZONTAL;
                jtf[tc].setEnabled(!(pd.isExtension && extStrict));
                contentPane.add(jtf[tc++], constraints);
            } else if (pd.parameter instanceof Float) {
                // TODO. 
                // WARNING: (DB) this is supposed to be temporary. In fact, I 
                // am planning to upgrade some of the parameters from int
                // to float. But for a few months, the users should not be
                // aware of that, even if the internal representation is 
                // slowing being adapted.
                jtf[tc] = new JTextField(24);
                int dummy = java.lang.Math.round((Float) pd.parameter);
                jtf[tc].setText(""+dummy);
                constraints.weightx = 100;
                constraints.weighty = 100;
                constraints.gridx = 2;
                constraints.gridy = ycount;
                constraints.gridwidth = 2;
                constraints.gridheight = 1;
                constraints.insets = new Insets(top, 0, 0, 20);
                constraints.fill = GridBagConstraints.HORIZONTAL;
                jtf[tc].setEnabled(!(pd.isExtension && extStrict));
                contentPane.add(jtf[tc++], constraints);
            } else if (pd.parameter instanceof FontG) {
                GraphicsEnvironment gE;
                gE = GraphicsEnvironment.getLocalGraphicsEnvironment();
                String[] s = gE.getAvailableFontFamilyNames();
                jco[co] = new JComboBox();

                for (int i = 0; i < s.length; ++i) {
                    jco[co].addItem(s[i]);
                    if (s[i].equals(((FontG) pd.parameter).getFamily()))
                        jco[co].setSelectedIndex(i);
                }
                constraints.weightx = 100;
                constraints.weighty = 100;
                constraints.gridx = 2;
                constraints.gridy = ycount;
                constraints.gridwidth = 2;
                constraints.gridheight = 1;
                constraints.insets = new Insets(top, 0, 0, 20);
                constraints.fill = GridBagConstraints.HORIZONTAL;
                jco[co].setEnabled(!(pd.isExtension && extStrict));
                contentPane.add(jco[co++], constraints);
            } else if (pd.parameter instanceof LayerInfo) {
                jco[co] = new JComboBox(new Vector<LayerDesc>(layers));
                jco[co].setSelectedIndex(((LayerInfo) pd.parameter).layer);
                jco[co].setRenderer(new LayerCellRenderer());

                constraints.weightx = 100;
                constraints.weighty = 100;
                constraints.gridx = 2;
                constraints.gridy = ycount;
                constraints.gridwidth = 2;
                constraints.gridheight = 1;
                constraints.insets = new Insets(top, 0, 0, 20);
                constraints.fill = GridBagConstraints.HORIZONTAL;
                jco[co].setEnabled(!(pd.isExtension && extStrict));
                contentPane.add(jco[co++], constraints);

            } else if (pd.parameter instanceof ArrowInfo) {
                jco[co] = new JComboBox<ArrowInfo>();
                jco[co].addItem(new ArrowInfo(0));
                jco[co].addItem(new ArrowInfo(1));
                jco[co].addItem(new ArrowInfo(2));
                jco[co].addItem(new ArrowInfo(3));

                jco[co].setSelectedIndex(((ArrowInfo) pd.parameter).style);
                jco[co].setRenderer(new ArrowCellRenderer());

                constraints.weightx = 100;
                constraints.weighty = 100;
                constraints.gridx = 2;
                constraints.gridy = ycount;
                constraints.gridwidth = 2;
                constraints.gridheight = 1;
                constraints.insets = new Insets(top, 0, 0, 20);
                constraints.fill = GridBagConstraints.HORIZONTAL;
                jco[co].setEnabled(!(pd.isExtension && extStrict));
                contentPane.add(jco[co++], constraints);

            } else if (pd.parameter instanceof DashInfo) {
            
                jco[co] = new JComboBox<DashInfo>();

                for (int k = 0; k < Globals.dashNumber; ++k) {
                    jco[co].addItem(new DashInfo(k));
                }

                jco[co].setSelectedIndex(((DashInfo) pd.parameter).style);
                jco[co].setRenderer(new DashCellRenderer());

                constraints.weightx = 100;
                constraints.weighty = 100;
                constraints.gridx = 2;
                constraints.gridy = ycount;
                constraints.gridwidth = 2;
                constraints.gridheight = 1;
                constraints.insets = new Insets(top, 0, 0, 20);
                constraints.fill = GridBagConstraints.HORIZONTAL;
                jco[co].setEnabled(!(pd.isExtension && extStrict));
                contentPane.add(jco[co++], constraints);
            }
        }
        // Put the OK and Cancel buttons and make them active.
        JButton ok = new JButton(Globals.messages.getString("Ok_btn"));
        JButton cancel = new JButton(Globals.messages.getString("Cancel_btn"));
        JButton keybd = new JButton("\u00B6\u2211\u221A");// phylum
        keybd.setFocusable(false);
        keybd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // If at this point, the keyboard is not visible, this means
                // that it will become visible in a while. It is better to
                // resize first and then show up the keyboard.
                if (keyb.isVisible()) {
                    MIN_WIDTH = 400;
                    MIN_HEIGHT = 350;
                } else {
                    MIN_WIDTH = 400;
                    MIN_HEIGHT = 500;

                }
                //setSize(MIN_WIDTH, MIN_HEIGHT);
                keyb.setVisible(!keyb.isVisible());
                pack(); 
            }
        });

        constraints.gridx = 0;
        constraints.gridy = ycount++;
        constraints.gridwidth = 4;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(6, 20, 20, 20);

        // Put the OK and Cancel buttons and make them active.
        Box b = Box.createHorizontalBox();
        b.add(keybd); // phylum

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
        // b.add(Box.createHorizontalStrut(12));
        contentPane.add(b, constraints);

        constraints.gridx = 0;
        constraints.gridy = ycount;
        constraints.gridwidth = 4;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(6, 20, 20, 20);

        contentPane.add(keyb, constraints);

        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                try {
                    int ycount;
                    ParameterDescription pd;
                    tc = 0;
                    cc = 0;
                    co = 0;

                    // Here we read all the contents of the interface and we
                    // update the contents of the parameter description array.

                    for (ycount = 0; ycount < v.size(); ++ycount) {
                        if (ycount > MAX)
                            break;
                        pd = (ParameterDescription) v.elementAt(ycount);

                        if (pd.parameter instanceof PointG) {
                            ((PointG) (pd.parameter)).x = Integer
                                    .parseInt(jtf[tc++].getText());
                            ((PointG) (pd.parameter)).y = Integer
                                    .parseInt(jtf[tc++].getText());
                        } else if (pd.parameter instanceof String) {
                            pd.parameter = jtf[tc++].getText();
                        } else if (pd.parameter instanceof Boolean) {
                            pd.parameter = Boolean.valueOf(
                                jcb[cc++].isSelected());
                        } else if (pd.parameter instanceof Integer) {
                            pd.parameter = Integer.valueOf(Integer
                                    .parseInt(jtf[tc++].getText()));
                        } else if (pd.parameter instanceof Float) {
                            pd.parameter = Float.valueOf(Float
                                    .parseFloat(jtf[tc++].getText()));
                        } else if (pd.parameter instanceof FontG) {
                            pd.parameter = new FontG((String) jco[co++]
                                    .getSelectedItem());
                        } else if (pd.parameter instanceof LayerInfo) {
                            pd.parameter = new LayerInfo(jco[co++]
                                    .getSelectedIndex());
                        } else if (pd.parameter instanceof ArrowInfo) {
                            pd.parameter = new ArrowInfo(jco[co++]
                                    .getSelectedIndex());
                        } else if (pd.parameter instanceof DashInfo) {
                            pd.parameter = new DashInfo(jco[co++]
                                    .getSelectedIndex());
                        }
                    }
                } catch (NumberFormatException E) {
                    // Error detected. Probably, the user has entered an
                    // invalid string when FidoCadJ was expecting a numerical
                    // input.

                    JOptionPane.showMessageDialog(null,
                            Globals.messages.getString("Format_invalid"), "",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                active = true;
                //Globals.activeWindow.setEnabled(true);
                setVisible(false);
                keyb.setVisible(false);
            }
        });
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                //Globals.activeWindow.setEnabled(true);
                setVisible(false);
                keyb.setVisible(false);
            }
        });
        // Here is an action in which the dialog is closed

        AbstractAction cancelAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                //Globals.activeWindow.setEnabled(true);
                setVisible(false);
                keyb.setVisible(false);
            }
        };
        DialogUtil.addCancelEscape(this, cancelAction);

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                //Globals.activeWindow.setEnabled(true);
                keyb.setVisible(false);
            }
        });

        pack();
        DialogUtil.center(this);
        getRootPane().setDefaultButton(ok);
    }

    /**
     * Get a ParameterDescription vector describing the characteristics modified
     * by the user.
     * 
     * @return a ParameterDescription vector describing each parameter.
     */
    public Vector<ParameterDescription> getCharacteristics() {
        return v;
    }

    public void componentResized(ComponentEvent e) {
        adjustSize();
    }

    private void adjustSize() {
/*      int width = getWidth();
        int height = getHeight();
        
        boolean resize = false;
        if (width < MIN_WIDTH) {
            resize = true;
            width = MIN_WIDTH;
        }
        if (height < MIN_HEIGHT) {
            resize = true;
            height = MIN_HEIGHT;
        }
        if (resize) {
            setSize(width, height);
        } */
        pack();
        
    }

    public void componentMoved(ComponentEvent e) 
    {
        // does nothing
    }

    public void componentShown(ComponentEvent e) 
    {
        // does nothing
    }

    public void componentHidden(ComponentEvent e) 
    {
        // does nothing
    }
}
