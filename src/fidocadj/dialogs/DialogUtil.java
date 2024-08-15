package fidocadj.dialogs;

import java.awt.event.*;
import java.awt.*;

import javax.swing.*;

/**  Some routines useful with dialog windows.

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

    Copyright 2007-2023 by Davide Bucci
    </pre>
*/
public final class DialogUtil
{
    private DialogUtil()
    {
        // nothing
    }

    /** Center the frame on the screen and set its height and width to half
        the screen size.
        @param frame the window to be centered.
    */
    public static void center(Window frame)
    {
        GraphicsEnvironment ge =
            GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point center = ge.getCenterPoint();

        int w = frame.getWidth();
        int h = frame.getHeight();

        int x = center.x - w/2;
        int y = center.y - h/2;

        frame.setBounds(x, y, w, h);
        frame.validate();
    }

    /** Center the frame on the screen and set its height and width to the
        given proportion of the screen size
        @param frame the window to be centered
        @param propX the horisontal proportion, between 0.0 and 1.0.
        @param propY the vertical proportion, between 0.0 and 1.0.
    */
    public static void center(Window frame, double propX, double propY)
    {
        GraphicsEnvironment ge =
             GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point center = ge.getCenterPoint();
        Rectangle bounds = ge.getMaximumWindowBounds();
        int w = Math.max((int)(bounds.width*propX), Math.min(frame.getWidth(),
                        bounds.width));
        int h = Math.max((int)(bounds.height*propY), Math.min(frame.getHeight(),
                        bounds.height));
        int x = center.x - w/2;
        int y = center.y - h/2;
        frame.setBounds(x, y, w, h);
        frame.validate();
    }


    /** Center the frame on the screen and set its height and width to the
        given proportion of the screen size. Specify also the minimum
        size in pixels.
        @param frame the window to be centered
        @param propX the horisontal proportion, between 0.0 and 1.0.
        @param propY the vertical proportion, between 0.0 and 1.0.
        @param minx minimum horisontal size in pixels.
        @param miny minimum vertical size in pixels.
    */
    public static void center(Window frame, double propX, double propY,
        int minx, int miny)
    {
        GraphicsEnvironment ge =
            GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point center = ge.getCenterPoint();
        Rectangle bounds = ge.getMaximumWindowBounds();
        int w = Math.max((int)(bounds.width*propX), Math.min(frame.getWidth(),
                        bounds.width));

        if(w<minx) {
            w=minx;
        }

        int h = Math.max((int)(bounds.height*propY), Math.min(frame.getHeight(),
                        bounds.height));

        if(h<miny) {
            h=miny;
        }

        int x = center.x - w/2;
        int y = center.y - h/2;
        frame.setBounds(x, y, w, h);
        frame.validate();
    }

    /** Maps the escape key with the action given (ideally, it should probably
        close the dialog window). Example follows:

        <pre>
        // Here is an action in which the dialog is closed

        AbstractAction cancelAction = new AbstractAction ()
        {
            public void actionPerformed (ActionEvent e)
            {
                setVisible(false);
            }
        };
        dialogUtil.addCancelEscape (this, cancelAction);
        </pre>
        @param f the dialog window on which the action should be applied
        @param cancelAction the action to be performed in response to the Esc
            key

    */
    public static void addCancelEscape (JDialog f,
        AbstractAction cancelAction)
    {
        // Map the Esc key to the cancel action description in the dialog box's
        // input map.

        String cancelActionKey = "CANCEL_ACTION_KEY";

        int noModifiers = 0;

        KeyStroke escapeKey =
            KeyStroke.getKeyStroke (KeyEvent.VK_ESCAPE, noModifiers, false);

        InputMap inputMap = f.getRootPane ().getInputMap
            (JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        inputMap.put (escapeKey, cancelActionKey);

        f.getRootPane ().getActionMap ().put (cancelActionKey, cancelAction);
    }

    /** Set up the constraints for the GridLayout manager
        @param gridx the x position in the grid
        @param gridy the y position in the grid
        @param width the width of the control
        @param height the heigth of the control
        @param weightx the weightx to be adopted
        @param weighty the weighty to be adopted
        @param anch the anchor value
        @param fill the fill valuue
        @param insets the insets to be used
        @return the constraints object created.
    */
    public static GridBagConstraints createConst(int gridx, int gridy,
        int width, int height, double weightx, double weighty,
        int anch, int fill, Insets insets)
    {
        GridBagConstraints constraints=new GridBagConstraints();
        constraints.gridx=gridx;
        constraints.gridy=gridy;
        constraints.gridwidth=width;
        constraints.gridheight=height;
        constraints.weightx=weightx;
        constraints.weighty=weighty;
        constraints.fill = fill;
        constraints.anchor = anch;
        constraints.insets=insets;

        return constraints;
    }
}