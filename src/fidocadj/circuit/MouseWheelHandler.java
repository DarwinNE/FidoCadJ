package fidocadj.circuit;

import java.awt.event.*;

import fidocadj.globals.Globals;
import fidocadj.globals.OSValidator;

/** MouseWheelHandler: handle wheel events for the zoom in/out.

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

    Copyright 2014-2023 by miklos80, Davide Bucci
    </pre>

    @author Davide Bucci
*/
public class MouseWheelHandler implements KeyListener,
                                          MouseWheelListener
{
    CircuitPanel cc;

    /** Constructor.
        @param c the CircuitPanel associated to the wheel events.
    */
    public MouseWheelHandler(CircuitPanel c)
    {
        cc=c;

        if (OSValidator.isMac() == false) {
            cc.addMouseWheelListener(this);
        }
    }

    /** Windows and Linux users can use Ctrl+Wheel to zoom in and out.
        With MacOSX, however Ctrl+Wheel is associated to the full screen
        zooming. Therefore, we use Command ("meta" with the Java terminology).
    */
    private int getKeyForWheel()
    {
        int keyCode=KeyEvent.VK_CONTROL;
        if(Globals.weAreOnAMac) {
            keyCode=KeyEvent.VK_META;
        }
        return keyCode;
    }

   /** Intercepts the moment when the Ctrl or Command key is pressed (see the
        note for getKeyForWheel(), so that the wheel listener is added.
    */
    @Override
    public void keyPressed(KeyEvent e)
    {
        if (OSValidator.isMac()) {
            if (e.getKeyCode() == getKeyForWheel() && !hasMouseWheelListener()){
                cc.addMouseWheelListener(this);
            }
        }
    }

    /** Intercepts the moment when the Ctrl or Command key is released (see the
        note for getKeyForWheel(), so that the wheel listener is removed.
    */
    @Override
    public void keyReleased(KeyEvent e)
    {
        if (OSValidator.isMac()) {
            if (e.getKeyCode() == getKeyForWheel() && hasMouseWheelListener()) {
                cc.removeMouseWheelListener(this);
            }
        }
    }

    /** Required by the KeyListener interface.
    */
    @Override
    public void keyTyped(KeyEvent e)
    {
        // do nothing
    }

    /** Determines wether in the wheel listener there is this class.
    */
    private boolean hasMouseWheelListener()
    {
        MouseWheelListener[] listeners = cc.getMouseWheelListeners();
        for (MouseWheelListener mouseWheelListener : listeners) {
            if (mouseWheelListener.equals(this)) {
                return true;
            }
        }
        return false;
    }

    /** Handle zoom event via the wheel.
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        cc.changeZoomByStep(e.getWheelRotation() < 0, e.getX(), e.getY(), 1.1);
    }
}
