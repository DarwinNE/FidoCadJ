package fidocadj;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import fidocadj.circuit.CircuitPanel;
import fidocadj.circuit.controllers.ElementsEdtActions;
import fidocadj.toolbars.ChangeSelectionListener;


/** Employed in FidoCadJ with the author's permission.
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
    Copyright 2007-2023 Santhosh Kumar T, Davide Bucci
    </pre>
*/
public final class ScrollGestureRecognizer implements AWTEventListener,
    ChangeSelectionListener
{
    private int actionSelected;
    private boolean oldGesture=false;

   /* private ScrollGestureRecognizer instance = new
        ScrollGestureRecognizer();
*/
    Point location= new Point();

    /** Constructor.
    */
    public ScrollGestureRecognizer()
    {
        start();
    }

    /** Get the current instance.
        @return the instance.
    */
    public ScrollGestureRecognizer getInstance()
    {
        return this;
    }

    /** Start the scroll operation.
    */
    void start()
    {
        Toolkit.getDefaultToolkit().addAWTEventListener(this,
            AWTEvent.MOUSE_MOTION_EVENT_MASK);
        Toolkit.getDefaultToolkit().addAWTEventListener(this,
            AWTEvent.MOUSE_EVENT_MASK);
    }

    /** Stop the scroll operation.
    */
    void stop()
    {
        Toolkit.getDefaultToolkit().removeAWTEventListener(this);
    }

    /** Event dispatched (?)
        @param event the AWT event dispatched.
    */
    @Override public void eventDispatched(AWTEvent event)
    {
        MouseEvent me = (MouseEvent)event;
        boolean isGesture = (SwingUtilities.isMiddleMouseButton(me) ||
            actionSelected==ElementsEdtActions.HAND) &&
            me.getID()==MouseEvent.MOUSE_DRAGGED;

        Component co=me.getComponent();

        if (!(co instanceof CircuitPanel)) {
            return;
        }

        JViewport viewport =
            (JViewport)SwingUtilities.getAncestorOfClass(JViewport.class,
            me.getComponent());
        if(viewport==null) {
            return;
        }
        JRootPane rootPane = SwingUtilities.getRootPane(viewport);
        if(rootPane==null) {
            return;
        }

        Point mouseLocation = SwingUtilities.convertPoint(me.getComponent(),
            me.getPoint(), rootPane.getGlassPane());
        if(!oldGesture && !isGesture) {
            location=mouseLocation;
        }
        oldGesture=isGesture;
        if(!isGesture) {
            return;
        }

        int deltax = -(mouseLocation.x - location.x);
        int deltay = -(mouseLocation.y - location.y);

        location.x=mouseLocation.x;
        location.y=mouseLocation.y;
        Point p = viewport.getViewPosition();
        p.translate(deltax, deltay);

        if(p.x<0) {
            p.x=0;
        } else if(p.x>=viewport.getView().getWidth()-viewport.getWidth()) {
            p.x = viewport.getView().getWidth()-viewport.getWidth();
        }

        if(p.y<0) {
            p.y = 0;
        } else if(p.y>=viewport.getView().getHeight()-viewport.getHeight()) {
            p.y = viewport.getView().getHeight()-viewport.getHeight();
        }

        viewport.setViewPosition(p);
    }

    /** ChangeSelectionListener interface implementation .
        @param s the selection state.
        @param macro the current macro key (if applicable).
    */
    public void setSelectionState(int s, String macro)
    {
        actionSelected=s;
    }

    /** Set if the strict FidoCAD compatibility mode is active
        @param strict true if the compatibility with FidoCAD should be
        obtained.
    */
    public void setStrictCompatibility(boolean strict)
    {
        // Nothing is needed here.
    }

    /** Get the current editing action (see the constants defined in this class)
        @return the current editing action
    */
    public int getSelectionState()
    {
        return actionSelected;
    }
}
