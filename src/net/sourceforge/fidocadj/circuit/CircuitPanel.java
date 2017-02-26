package net.sourceforge.fidocadj.circuit;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

import javax.swing.*;

import java.util.*;
import java.io.*;

import net.sourceforge.fidocadj.dialogs.*;
import net.sourceforge.fidocadj.primitives.*;
import net.sourceforge.fidocadj.timer.*;
import net.sourceforge.fidocadj.toolbars.*;
import net.sourceforge.fidocadj.circuit.controllers.*;
import net.sourceforge.fidocadj.circuit.model.*;
import net.sourceforge.fidocadj.circuit.views.*;
import net.sourceforge.fidocadj.clipboard.*;
import net.sourceforge.fidocadj.graphic.*;
import net.sourceforge.fidocadj.graphic.swing.*;
import net.sourceforge.fidocadj.export.*;
import net.sourceforge.fidocadj.geom.*;
import net.sourceforge.fidocadj.globals.*;
import net.sourceforge.fidocadj.layers.*;

/** Circuit panel: draw the circuit inside this panel. This is one of the most
    important components, as it is responsible of all editing actions.
    In many ways, this class contains the most important component of
    FidoCadJ.
    This class is able to perform its profiling, which is in particular
    the measurement of the time needed to draw the circuit.

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

    Copyright 2007-2017 by Davide Bucci
    </pre>

    @author Davide Bucci
*/
public class CircuitPanel extends JPanel implements
                                             ChangeSelectedLayer,
                                             ChangeGridState,
                                             ChangeZoomListener,
                                             ChangeSelectionListener,
                                             PrimitivesParInterface
{
    // *********** DRAWING ***********
    Graphics2DSwing graphicSwing;

    // Coordinate system to be used.
    private transient MapCoordinates cs;

    // Use anti alias in drawings
    public boolean antiAlias;

    // Draw the grid
    private boolean isGridVisible;

    // Default background color
    private Color backgroundColor;

    // Position of the rectangle used for the selection
    private Rectangle evidenceRect;

    // Margin size in pixels when calculating component sizes.
    public static final int MARGIN=20;

    // Color of elements during editing
    static final ColorSwing editingColor=new ColorSwing(Color.green);

    // Model:
    // TODO: This should be kept private!
    public transient DrawingModel dmp;

    // Scrolling pane data
    public JScrollPane father;
    private static final int MINSIZEX=1000;
    private static final int MINSIZEY=1000;

    private ImageAsCanvas imgCanvas;

    // Views:
    public Drawing drawingAgent;

    // Controllers:
    private EditorActions edt;
    private CopyPasteActions cpa;
    private ParserActions pa;
    private UndoActions ua;
    private ContinuosMoveActions eea;
    private SelectionActions sa;

    // ********** PROFILING **********

    // Specify that the profiling mode should be activated.
    public boolean profileTime;
    private double average;

    // Record time for the redrawing.
    private double record;

    // Number of times the redraw has occourred.
    private double runs;

    // ********** INTERFACE **********

    // If this variable is different from null, the component will ensure that
    // the corresponding rectangle will be shown in a scroll panel during the
    // next redraw.
    private Rectangle scrollRectangle;

    // Strict FidoCAD compatibility
    public boolean extStrict;

    // ********** RULER **********

    private final Ruler ruler;  // Is it to be drawn?

    // ********** INTERFACE ELEMENTS **********

    PopUpMenu popup;                    // Popup menu
    MouseWheelHandler mwHandler;        // Wheel handler
    MouseMoveClickHandler mmcHandler;   // Mouse click handler

    // ********** LISTENERS **********

    private ChangeZoomListener zoomListener;
    private ChangeSelectionListener selectionListener;
    private ChangeSelectionListener scrollGestureSelectionListener;

    /** Standard constructor
        @param isEditable indicates whether the panel should be responsible
               to keyboard and mouse inputs.
    */
    public CircuitPanel (boolean isEditable)
    {
        backgroundColor=Color.white;
        setDrawingModel(new DrawingModel());

        mwHandler=new MouseWheelHandler(this);
        mmcHandler= new MouseMoveClickHandler(this);

        graphicSwing = new Graphics2DSwing();

        ruler = new Ruler(editingColor.getColorSwing(),
            editingColor.getColorSwing().darker().darker());

        isGridVisible=true;
        zoomListener=null;
        antiAlias = true;
        record = 1e100;
        evidenceRect = new Rectangle(0,0,-1,-1);
        imgCanvas = new ImageAsCanvas();

        // Set up the standard view settings:
        // top left corner, 400% zoom.
        cs = new MapCoordinates();
        cs.setXCenter(0.0);
        cs.setYCenter(0.0);
        cs.setXMagnitude(4.0);
        cs.setYMagnitude(4.0);
        cs.setOrientation(0);
        setOpaque(true);
        runs = 0;
        average = 0;

        // This is not useful when preparing the applet: the circuit panel will
        // not be editable in this case.
        if (isEditable) {
            addMouseListener(mmcHandler);
            addMouseMotionListener(mmcHandler);
            addKeyListener(mwHandler);
            setFocusable(true);

            //Create the popup menu.
            popup=new PopUpMenu(this);
            popup.registerActiveKeys();

            // Patchwork for bug#54.
            // When mouse pointer enters into CircuitPanel with macro,
            // grab focus from macropicker.
            // NOTE: MouseListener.mouseEntered doesn't works stable.
            addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseMoved(MouseEvent e)
                {
                    if(eea.isEnteringMacro() && !isFocusOwner()){
                        requestFocus();
                    }
                }
            });
        }
    }

    /** Show a popup menu representing the actions that can be done on the
        selected context.
        @param x the x coordinate where the popup menu should be put
        @param y the y coordinate where the popup menu should be put
    */
    public void showPopUpMenu(int x, int y)
    {
        popup.showPopUpMenu(this, x, y);
    }

    /** Makes sure the object gets focus.
    */
    public void getFocus()
    {
        requestFocusInWindow();
    }

    /** ChangeSelectionListener interface implementation
        @param s the selection state
        @param macro the macro key (if applies)
    */
    public void setSelectionState(int s, String macro)
    {
        if (selectionListener!=null && s!=eea.actionSelected) {
            selectionListener.setSelectionState(s, macro);
            selectionListener.setStrictCompatibility(extStrict);
        }

        if (scrollGestureSelectionListener!=null) {
            scrollGestureSelectionListener.setSelectionState(s,
                macro);
        }
        eea.setState(s, macro);
        mmcHandler.selectCursor();
    }

    /** Set the rectangle which will be shown during the next redraw.
        @param r the rectangle to show.
    */
    public void setScrollRectangle(Rectangle r)
    {
        scrollRectangle = r;
        repaint();
    }

    /** Define the listener to be called when the zoom is changed
        @param c the new zoom listener.
    */
    public void addChangeZoomListener(ChangeZoomListener c)
    {
        zoomListener=c;
    }
    /** Define the listener to be called when the selected action is changed
        @param c the new selection listener.
    */
    public void addChangeSelectionListener(ChangeSelectionListener c)
    {
        selectionListener=c;
    }

    /** Define the listener to be called when the selected action is changed
        (this is explicitly done for the ScrollGestureSelection).
        @param c the new selection listener.
    */
    public void addScrollGestureSelectionListener(ChangeSelectionListener c)
    {
        scrollGestureSelectionListener=c;
    }

    /** Return the current editing layer.
        @return the index of the layer.
    */
    public int getCurrentLayer()
    {
        return eea.currentLayer;
    }

    /** Set the current editing layer.
        @param cl the wanted layer.
    */
    public void setCurrentLayer(int cl)
    {
        int l=cl;
        /* two little checks... */
        if (l<0)
            l=0;
        if (l>=dmp.getLayers().size())
            l=dmp.getLayers().size()-1;

        eea.currentLayer=l;
    }

    /** Change the current layer state. Change the layer of all selected
        primitives.
        @param s the layer to be selected.
    */
    public void changeSelectedLayer(int s)
    {
        // Change the current layer
        eea.currentLayer=s;
        // Change also the layer of all selected primitives
        if(edt.setLayerForSelectedPrimitives(s)) {
            repaint();
        }
    }

    /** The method which is called when the current grid visibility
        has to be changed.
        @param v is the wanted grid visibility state.
    */
    public void setGridVisibility(boolean v)
    {
        isGridVisible=v;
        repaint();
    }

    /** Determines if the grid is visible or not.
        @return the grid visibility.
    */
    public boolean getGridVisibility()
    {
        return isGridVisible;
    }

    /** The method to be called when the current snap visibility
        has changed.
        @param v is the wanted snap state.
    */
    public void setSnapState(boolean v)
    {
        cs.setSnap(v);
    }

    /** Determines if the grid is visible or not.
        @return the grid visibility.
    */
    public boolean getSnapState()
    {
        return cs.getSnap();
    }

    /** Increase or decrease the zoom by a step of 33%.
        @param increase if true, increase the zoom, if false decrease.
        @param x coordinate to which center the viewport (screen coordinates).
        @param y coordinate to which center the viewport (screen coordinates).
    */
    public void changeZoomByStep(boolean increase, int x, int y)
    {
        int xpos = cs.unmapXnosnap(x);
        int ypos = cs.unmapYnosnap(y);
        double z=cs.getXMagnitude();

        // Click+Meta reduces the zoom
        // Click raises the zoom
        double oldz=z;
        if(increase)
            z=z*3.0/2.0;
        else
            z=z*2.0/3.0;

        // Checking that reasonable limits are not exceeded.
        if(z>20) z=20;
        if(z<.25) z=.25;

        z=Math.round(z*100.0)/100.0;

        if (Math.abs(oldz-z)<1e-5)
            return;

        cs.setMagnitudes(z,z);

        // A little strong...

        int width = father.getViewport().getExtentSize().width;
        int height = father.getViewport().getExtentSize().height;

        Point rr=father.getViewport().getViewPosition();

        int corrx=x-rr.x;
        int corry=y-rr.y;

        Rectangle r=new Rectangle(cs.mapXi(xpos,ypos,false)-corrx,
                cs.mapYi(xpos,ypos,false)-corry,
                width, height);

        updateSizeOfScrollBars(r);
    }

    /** Calculate the size of the image and update the size of the
        scroll bars, with the current zoom.
        @param r the Rectangle which will contain the new image size at the
            end of this method.
    */
    public void updateSizeOfScrollBars(Rectangle r)
    {
        PointG origin=new PointG();
        DimensionG d=DrawingSize.getImageSize(dmp, cs.getXMagnitude(),
                false, origin);

        int minx=cs.mapXi(MINSIZEX,MINSIZEY,false);
        int miny=cs.mapYi(MINSIZEX,MINSIZEY,false);

        Dimension dd=new Dimension(Math.max(d.width
               +MARGIN, minx),Math.max(d.height+MARGIN, miny));

        setPreferredSize(dd);
        if(r!=null)
            scrollRectangle = r;

        revalidate();
        repaint();
    }

    /** Get the current editing action (see the constants defined in this
        class).
        @return the current editing action.
    */
    public int getSelectionState()
    {
        return eea.getSelectionState();
    }

    /** The zoom listener.
        @param tz the zoom factor to be used.
    */
    public void changeZoom(double tz)
    {
        double z=Math.round(tz*100.0)/100.0;
        cs.setMagnitudes(z,z);
        eea.successiveMove=false;

        repaint();
    }

    /** Sets the background color.
        @param sfondo the background color to be used.
    */
    public void setBackground(Color sfondo)
    {
        backgroundColor=sfondo;
    }


    /** Activate and sets an evidence rectangle which will be put on screen
        at the next redraw. All sizes are given in pixel.
        @param lx   the x coordinate of the left top corner.
        @param ly   the y coordinate of the left top corner.
        @param w    the width of the rectangle.
        @param h    the height of the rectangle.
    */
    public void setEvidenceRect(int lx, int ly, int w, int h)
    {
        evidenceRect = new Rectangle();
        evidenceRect.x=lx;
        evidenceRect.y=ly;
        evidenceRect.height=h;
        evidenceRect.width=w;
    }

    /** Repaint the panel.
        This method performs the following operations:<br>
        1. set the anti aliasing on (or off, depending on antiAlias).<br>
        2. paint in white the background, draw the bk. image and the grid.<br>
        3. call drawingAgent draw.<br>
        4. draw all active handles.<br>
        5. if needed, draw the primitive being edited.<br>
        6. draw the ruler, if needed.<br>
        7. if requested, print information about redraw speed.<br>
        @param g the graphic context on which perform the drawing operations.
    */
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        MyTimer mt;
        mt = new MyTimer();

        Graphics2D g2 = (Graphics2D)g;
        graphicSwing.setGraphicContext(g2);
        activateDrawingSettings(g2);

        // Draw the background.
        g.setColor(backgroundColor);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Draw the grid if necessary.
        if(isGridVisible) {
            graphicSwing.drawGrid(cs,0,0,getWidth(), getHeight());
        }

        imgCanvas.drawCanvasImage(g2, cs);

        // The standard color is black.
        g.setColor(Color.black);
        // This is important for taking into account the dashing size
        graphicSwing.setZoom(cs.getXMagnitude());

        // Draw all the elements of the drawing.
        drawingAgent.draw(graphicSwing, cs);

        if (zoomListener!=null)
            zoomListener.changeZoom(cs.getXMagnitude());

        // Draw the handles of all selected primitives.
        drawingAgent.drawSelectedHandles(graphicSwing, cs);

        // If an evidence rectangle is active, draw it.
        g.setColor(editingColor.getColorSwing());
        g2.setStroke(new BasicStroke(1));

        if(evidenceRect!=null && eea.actionSelected ==
            ElementsEdtActions.SELECTION)
            g.drawRect(evidenceRect.x,evidenceRect.y, evidenceRect.width,
                evidenceRect.height);
        else
            evidenceRect = null;

        // If there is a primitive or a macro being edited, draws it.
        eea.drawPrimEdit(graphicSwing, cs);

        // If a ruler.isActive() is active, draw it.
        ruler.drawRuler(g, cs);

        setSizeIfNeeded();

        if(scrollRectangle!=null) {
            Rectangle r=scrollRectangle;
            scrollRectangle = null;
            scrollRectToVisible(r);
        }
        // Since the redraw speed is a capital parameter which determines the
        // perceived speed, we monitor it very carefully if the program
        // profiling is active.

        if(profileTime) {
            double elapsed=mt.getElapsed();
            g2.drawString("Version: "+
                Globals.version, 0,100);
            g.drawString("Time elapsed: " +
                elapsed+" ms" ,0,50);
            ++runs;
            average += elapsed;
            if(elapsed<record) {
                record=elapsed;
            }
            System.out.println("R: Time elapsed: "+
                elapsed+
                " averaging "+
                average/runs+
                "ms in "+runs+
                " redraws; record: "+record+" ms");
        }
    }

    /** Activate or deactivate anti-aliasing if necessary.
    */
    private void activateDrawingSettings(Graphics2D g2)
    {
        if (antiAlias) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
                RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        } else {
            // Faster graphic (??? true??? I do not think so on modern systems)
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_SPEED);
            g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
                RenderingHints.VALUE_COLOR_RENDER_SPEED);
            g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
            g2.setRenderingHint(RenderingHints.KEY_DITHERING,
                RenderingHints.VALUE_DITHER_DISABLE);
        }
    }

    /** Set the new size of the drawing, if needed.
    */
    private void setSizeIfNeeded()
    {
        Dimension d=new Dimension(cs.getXMax(), cs.getYMax());

        if (d.width>0 && d.height>0){
            int minx=cs.mapXi(MINSIZEX,MINSIZEY,false);
            int miny=cs.mapYi(MINSIZEX,MINSIZEY,false);

            Dimension dd=new Dimension(Math.max(d.width
                +MARGIN, minx),Math.max(d.height+MARGIN, miny));
            Dimension nn=getPreferredSize();

            if(dd.width!=nn.width || dd.height!=nn.height) {
                setPreferredSize(dd);
                revalidate();
            }
        }
    }

    /** Update the current size of the object, given the current size of the
        drawing.
    */
    public void validate()
    {
        int minx=cs.mapXi(MINSIZEX,MINSIZEY,false);
        int miny=cs.mapYi(MINSIZEX,MINSIZEY,false);

        Dimension dd=new Dimension(Math.max(cs.getXMax()
            +MARGIN, minx),Math.max(cs.getYMax()+MARGIN, miny));
        Dimension nn=getPreferredSize();

        if(dd.width!=nn.width || dd.height!=nn.height) {
            setPreferredSize(dd);
        }

        super.validate();
    }

    /** Get the current drawing model.
        @return the drawing model.
    */
    public DrawingModel getDrawingModel()
    {
        return dmp;
    }

    /** Set the current drawing model. Changing it mean that all the
        controllers and views associated to the panel will be updated.
        @param dm the drawing model.
    */
    public void setDrawingModel(DrawingModel dm)
    {
        dmp=dm;
        sa = new SelectionActions(dmp);
        pa =new ParserActions(dmp);
        ua = new UndoActions(pa);
        edt = new EditorActions(dmp, sa, ua);
        eea = new ContinuosMoveActions(dmp, sa, ua, edt);
        drawingAgent=new Drawing(dmp);
        eea.setPrimitivesParListener(this);
        cpa=new CopyPasteActions(dmp, edt, sa, pa, ua, new TextTransfer());
    }

    /** Get the current instance of SelectionActions controller class
        @return the class
    */
    public SelectionActions getSelectionActions()
    {
        return sa;
    }

    /** Get the current instance of EditorActions controller class
        @return the class
    */
    public EditorActions getEditorActions()
    {
        return edt;
    }

    /** Get the current instance of CopyPasteActions controller class
        @return the class
    */
    public CopyPasteActions getCopyPasteActions()
    {
        return cpa;
    }

    /** Get the current instance of ParserActions controller class
        @return the class
    */
    public ParserActions getParserActions()
    {
        return pa;
    }

    /** Get the current instance of UndoActions controller class
        @return the class
    */
    public UndoActions getUndoActions()
    {
        return ua;
    }
    /** Get the current instance of ElementsEdtActions controller class
        @return the class
    */
    public ContinuosMoveActions getContinuosMoveActions()
    {
        return eea;
    }

    /** Shows a dialog which allows the user modify the parameters of a given
        primitive. If more than one primitive is selected, modify only the
        layer of all selected primitives.
    */
    public void setPropertiesForPrimitive()
    {
        GraphicPrimitive gp=sa.getFirstSelectedPrimitive();
        if (gp==null)
            return;

        Vector<ParameterDescription> v;
        if (sa.isUniquePrimitiveSelected()) {
            v=gp.getControls();
        } else {
            // If more than a primitive is selected,
            v=new Vector<ParameterDescription>(1);
            ParameterDescription pd = new ParameterDescription();
            pd.parameter=new LayerInfo(gp.getLayer());
            pd.description=Globals.messages.getString("ctrl_layer");
            v.add(pd);
        }
        DialogParameters dp = new DialogParameters(
            (JFrame)Globals.activeWindow,
            v, extStrict,
            dmp.getLayers());
        dp.setVisible(true);
        if(dp.active) {
            if (sa.isUniquePrimitiveSelected()) {
                gp.setControls(dp.getCharacteristics());
            } else {
                ParameterDescription pd=(ParameterDescription)v.get(0);
                dp.getCharacteristics();
                if (pd.parameter instanceof LayerInfo) {
                    int l=((LayerInfo)pd.parameter).getLayer();
                    edt.setLayerForSelectedPrimitives(l);
                } else {
                    System.out.println(
                        "Warning: unexpected parameter! (layer)");
                }
            }
            dmp.setChanged(true);

            // We need to check and sort the layers, since the user can
            // change the layer associated to a given primitive thanks to
            // the dialog window which has been shown.

            dmp.sortPrimitiveLayers();
            ua.saveUndoState();
            repaint();
        }
    }

    /** Selects the closest object to the given point (in logical coordinates)
        and pops up a dialog for the editing of its Param_opt.
        @param x the x logical coordinate of the point used for the selection.
        @param y the y logical coordinate of the point used for the selection.
    */
    public void selectAndSetProperties(int x, int y)
    {
        sa.setSelectionAll(false);
        edt.handleSelection(cs, x, y, false);
        repaint();
        setPropertiesForPrimitive();
    }

    /** Checks if FidoCadJ should strictly comply with the FidoCAD
        format (and limitations).
        @return the compliance mode.
    */
    public boolean getStrictCompatibility()
    {
        return extStrict;
    }

    /** Set if the strict FidoCAD compatibility mode is active.
        @param strict true if the compatibility with FidoCAD should be
        obtained.
    */
    public void setStrictCompatibility(boolean strict)
    {
        extStrict=strict;
    }

    /** Change the current coordinate mapping.
        @param m the new coordinate mapping to be adopted.
    */
    public void setMapCoordinates(MapCoordinates m)
    {
        cs=m;
        // Force an in-depth redraw.
        dmp.setChanged(true);
    }

    /** Get the current coordinate mapping.
        @return the current coordinate mapping.
    */
    public MapCoordinates getMapCoordinates()
    {
        return cs;
    }

    /** Force a repaint.
    */
    public void forcesRepaint()
    {
        repaint();
    }

    /** Force a repaint.
        @param x the x leftmost corner of the dirty region to repaint.
        @param y the y leftmost corner of the dirty region to repaint.
        @param width the width of the dirty region.
        @param height the height of the dirty region.
    */
    public void forcesRepaint(int x, int y, int width, int height)
    {
        repaint(x, y, width, height);
    }

    /** Get the Ruler object.
        @return the Ruler object.
    */
    public Ruler getRuler()
    {
        return ruler;
    }

    /** Check if the profiling is active.
        @return true if the profiling is active.
    */
    public boolean isProfiling()
    {
        return profileTime;
    }

    /** Attach an image as background.
        @param filename name of the file.
        @throws IOException if the file can not be loaded.
    */
    public void attachImage(String filename) throws IOException
    {
        imgCanvas.loadImage(filename);
    }
}
