package fidocadj.circuit;

import java.awt.event.*;

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

    Copyright 2014-2025 by miklos80, Davide Bucci, Manuel Finessi
    </pre>

    @author Davide Bucci
*/
public class MouseWheelHandler implements KeyListener, MouseWheelListener
{
    CircuitPanel circuitPanel;

    /** Constructor.
        @param circuitPanel the CircuitPanel associated to the wheel events.
    */
    public MouseWheelHandler(CircuitPanel circuitPanel)
    {
        this.circuitPanel = circuitPanel;

        updateListenerState();
    }
    
    /**
     * Updates the mouse wheel listener state based on current settings.
     * This method should be called whenever enableKeyForZoom setting changes.
     * 
     * Behavior:
     * - If enableKeyForZoom is true: 
     *      listener is always active (no key required)
     * - If enableKeyForZoom is false: 
     *      listener is only active when Ctrl key is pressed
     */
    public void updateListenerState()
    {
        boolean shouldHaveListener = !circuitPanel.isEnabledKeyForZoom();
        boolean hasListener = hasMouseWheelListener();
        
        if (shouldHaveListener && !hasListener) {
            // Add listener if zoom without key is ...
            // enabled and listener is not present
            circuitPanel.addMouseWheelListener(this);
        } else if (!shouldHaveListener && hasListener) {
            // Remove listener if zoom without key is ...
            // disabled and listener is present
            circuitPanel.removeMouseWheelListener(this);
        }
    }
    
    /** Intercepts the moment when the Ctrl key is pressed.
        When enableKeyForZoom is false, the wheel listener is added
        only when Ctrl key is held down.
        @param e the key event
    */
    @Override
    public void keyPressed(KeyEvent e)
    {
        // Only handle dynamic listener addition when key-for-zoom is disabled
        if (circuitPanel.isEnabledKeyForZoom()) {
            if (e.getKeyCode() == KeyEvent.VK_CONTROL && 
                    !hasMouseWheelListener()) {
                circuitPanel.addMouseWheelListener(this);
            }
        }
    }
    
    /** Intercepts the moment when the Ctrl key is released.
        When enableKeyForZoom is false, the wheel listener is removed
        when Ctrl key is released.
        @param e the key event
    */
    @Override
    public void keyReleased(KeyEvent e)
    {
        // Only handle dynamic listener removal when key-for-zoom is disabled
        if (circuitPanel.isEnabledKeyForZoom()) {
            if (e.getKeyCode() == KeyEvent.VK_CONTROL && 
                    hasMouseWheelListener()) {
                circuitPanel.removeMouseWheelListener(this);
            }
        }
    }
    
    /** Required by the KeyListener interface.
        @param e the key event
    */
    @Override
    public void keyTyped(KeyEvent e)
    {
        // do nothing
    }
    
    /** Determines whether the circuit panel has this class as a wheel listener.
        @return true if this handler is registered as a mouse wheel listener
    */
    private boolean hasMouseWheelListener()
    {
        MouseWheelListener[] listeners = circuitPanel.getMouseWheelListeners();
        for (MouseWheelListener mouseWheelListener : listeners) {
            if (mouseWheelListener.equals(this)) {
                return true;
            }
        }
        return false;
    }
    
    /** Handle zoom event via the wheel.
        @param e the mouse wheel event
    */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        circuitPanel.changeZoomByStep(
                e.getWheelRotation() < 0, e.getX(), e.getY(), 1.1);
    }
    
    /** Cleanup method to properly remove listeners.
        Should be called when the handler is no longer needed.
    */
    public void dispose()
    {
        if (hasMouseWheelListener()) {
            circuitPanel.removeMouseWheelListener(this);
        }
    }
}
