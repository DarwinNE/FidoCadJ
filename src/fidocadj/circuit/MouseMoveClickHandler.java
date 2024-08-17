package fidocadj.circuit;

import java.awt.*;
import java.awt.event.*;

import fidocadj.globals.Globals;
import fidocadj.circuit.controllers.ElementsEdtActions;
import fidocadj.circuit.controllers.ContinuosMoveActions;
import fidocadj.circuit.controllers.EditorActions;
import fidocadj.circuit.controllers.HandleActions;
import fidocadj.timer.MyTimer;
import fidocadj.geom.MapCoordinates;


/** Handle the mouse click operations, as well as some dragging actions.
    <pre>
    cp file is part of FidoCadJ.

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

    @author Davide Bucci
*/
public class MouseMoveClickHandler implements MouseMotionListener,
                                             MouseListener
{
    private final CircuitPanel cp;
    private final EditorActions edt;
    private final ContinuosMoveActions eea;
    private final HandleActions haa;

    // Record time for mouse down handle event in selection.
    private double record_c;

    // Record time for click up event in selection.
    private double record_d;

    /** Constructor. Create a MouseMoveClickHandler and associates it to the
        provided handler.
        @param p the CircuitPanel.
    */
    public MouseMoveClickHandler(CircuitPanel p)
    {
        cp=p;
        edt=cp.getEditorActions();
        eea=cp.getContinuosMoveActions();
        haa=cp.getHandleActions();
        record_c=record_d=1e100;
    }

    /** Called when the mouse is clicked inside the control
        0.23.2: the Java click event is a bit too much restrictive. The mouse
        need to be hold still during the click. This is apparently a problem for
        a number of user. I have thus decided to use the mouse release event
        instead of the complete click.
        @param evt the MouseEvent to handle.
    */
    @Override public void mouseClicked(MouseEvent evt)
    {
        cp.requestFocusInWindow();
    }

    /** Handle the mouse movements when editing a graphic primitive.
        This procedure is important since it is used to show interactively
        to the user which element is being modified.
        @param evt the MouseEvent to handle.
    */
    @Override public void mouseMoved(MouseEvent evt)
    {
        int xa=evt.getX();
        int ya=evt.getY();

        boolean toggle = getToggle(evt);

        if (eea.continuosMove(cp.getMapCoordinates(), xa, ya, toggle)) {
            cp.repaint();
        }
    }

    /** Check if the "toggle" keyboard button is pressed during the mouse
        operation. Toggle may be Control or Meta, depending on the operating
        system.
        @param evt the MouseEvent.
        @return true if the toggle should be active.
    */
    private boolean getToggle(MouseEvent evt)
    {
        if(Globals.useMetaForMultipleSelection) {
            return evt.isMetaDown();
        } else {
            return evt.isControlDown();
        }
    }

    /** Mouse interface: start of the dragging operations.
        @param evt the MouseEvent to handle
    */
    @Override public void mousePressed(MouseEvent evt)
    {
        MyTimer mt = new MyTimer();

        int px=evt.getX();
        int py=evt.getY();

        cp.getRuler().setActive(false);
        cp.getRuler().setRulerStart(px, py);
        cp.getRuler().setRulerEnd(px, py);
        boolean toggle = getToggle(evt);

        if(eea.actionSelected == ElementsEdtActions.SELECTION &&
            (evt.getModifiersEx() & InputEvent.BUTTON3_DOWN_MASK)==0 &&
            !evt.isShiftDown())
        {
            haa.dragHandleStart(px, py, edt.getSelectionTolerance(),
                toggle, cp.getMapCoordinates());
        } else if(eea.actionSelected == ElementsEdtActions.SELECTION){
            // Right click during selection
            cp.getRuler().setActive(true);
        }

        if(cp.isProfiling()) {
            double elapsed=mt.getElapsed();
            if(elapsed<record_c) {
                record_c=elapsed;
            }
            System.out.println("MP: Time elapsed: "+elapsed+
                "; record: "+record_c+" ms");
        }
    }

    /** Dragging event with the mouse.
        @param evt the MouseEvent to handle
    */
    @Override public void mouseDragged(MouseEvent evt)
    {
        MyTimer mt = new MyTimer();
        int px=evt.getX();
        int py=evt.getY();

        // Handle the ruler. Basically, we just save the coordinates and
        // we launch a repaint which will be done as soon as possible.
        // No graphical elements are drawn outside a repaint.
        if((evt.getModifiersEx() & InputEvent.BUTTON3_DOWN_MASK)!=0 ||
            evt.isShiftDown())
        {
            cp.getRuler().setRulerEnd(px, py);
            cp.repaint();
            return;
        }

        haa.dragHandleDrag(cp, px, py, cp.getMapCoordinates(),
            (evt.getModifiersEx() & ActionEvent.CTRL_MASK)==
            ActionEvent.CTRL_MASK);
        // A little profiling if necessary. I noticed that time needed for
        // handling clicks is not negligible in large drawings, hence the
        // need of controlling it.
        if(cp.isProfiling()) {
            double elapsed=mt.getElapsed();
            if(elapsed<record_d) {
                record_d=elapsed;
            }
            System.out.println("MD: Time elapsed: "+elapsed+
                "; record: "+record_d+" ms");
        }
    }

    /** Mouse release event.
        @param evt the MouseEvent to handle
    */
    @Override public void mouseReleased(MouseEvent evt)
    {
        MyTimer mt = new MyTimer();
        int px=evt.getX();
        int py=evt.getY();
        MapCoordinates cs=cp.getMapCoordinates();

        boolean button3 = false;
        boolean toggle = getToggle(evt);

        // Key bindings are a little different with macOS.
        if(Globals.weAreOnAMac) {
            if(evt.getButton()==MouseEvent.BUTTON3) {
                button3=true;
            } else if(evt.getButton()==MouseEvent.BUTTON1 &&
                evt.isControlDown())
            {
                button3=true;
            }
        } else {
            button3 = (evt.getModifiersEx() & InputEvent.BUTTON3_DOWN_MASK)==
                    InputEvent.BUTTON3_DOWN_MASK;
        }

        // If we are in the selection state, either we are ending the editing
        // of an element (and thus the dragging of a handle) or we are
        // making a click.
        if(eea.actionSelected==ElementsEdtActions.SELECTION) {
            if(cp.getRuler().getRulerStartX()!=px ||
                cp.getRuler().getRulerStartY()!=py) // NOPMD
            {
                haa.dragHandleEnd(cp, px, py, toggle, cs);
            } else {
                cp.getRuler().setActive(false);
                cp.requestFocusInWindow();

                eea.handleClick(cs,evt.getX(), evt.getY(),
                    button3, toggle, evt.getClickCount() >= 2);
            }
            cp.repaint();
        } else {
            cp.requestFocusInWindow();
            if(eea.handleClick(cs,evt.getX(), evt.getY(),
                button3, toggle, evt.getClickCount() >= 2))
            {
                cp.repaint();
            }
        }

        // Having an idea of the release time is useful for the optimization
        // of the click event handling. The most time-consuming operation
        // which is done in this phase is finding the closest component to
        // the mouse pointer and eventually selecting it.

        if(cp.isProfiling()) {
            double elapsed=mt.getElapsed();
            if(elapsed<record_d) {
                record_d=elapsed;
            }
            System.out.println("MR: Time elapsed: "+elapsed+
                "; record: "+record_d+" ms");
        }
    }

    /** The mouse pointer enters into the control. This method changes the
        cursor associated to it.
        @param evt the MouseEvent to handle
    */
    @Override public void mouseEntered(MouseEvent evt)
    {
        selectCursor();
    }

    /**
        Define the icon used for the mouse cursor, depending on the current
        editing action.
    */
    public void selectCursor()
    {
        switch(eea.actionSelected) {
            case ElementsEdtActions.NONE:
            case ElementsEdtActions.ZOOM:
            case ElementsEdtActions.HAND:
                cp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                break;
            case ElementsEdtActions.LINE:
            case ElementsEdtActions.TEXT:
            case ElementsEdtActions.BEZIER:
            case ElementsEdtActions.POLYGON:
            case ElementsEdtActions.COMPLEXCURVE:
            case ElementsEdtActions.ELLIPSE:
            case ElementsEdtActions.RECTANGLE:
            case ElementsEdtActions.CONNECTION:
            case ElementsEdtActions.PCB_LINE:
            case ElementsEdtActions.PCB_PAD:
                cp.setCursor(Cursor.getPredefinedCursor(
                    Cursor.CROSSHAIR_CURSOR));
                break;
            case ElementsEdtActions.SELECTION:
            default:
                cp.setCursor(Cursor.getPredefinedCursor(
                    Cursor.DEFAULT_CURSOR));
                break;
        }
    }

    /** The mouse pointer has exited the control. This method changes the
        cursor associated and restores the default one.
        @param evt the MouseEvent to handle
    */
    @Override public void mouseExited(MouseEvent evt)
    {
        cp.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        if(eea.successiveMove) {
            eea.successiveMove = false;
            cp.repaint();
        }
    }
}
