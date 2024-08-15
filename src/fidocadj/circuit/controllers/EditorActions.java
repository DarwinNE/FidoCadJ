package fidocadj.circuit.controllers;

import java.util.*;

import fidocadj.circuit.model.ProcessElementsInterface;
import fidocadj.circuit.model.DrawingModel;
import fidocadj.geom.MapCoordinates;
import fidocadj.layers.LayerDesc;
import fidocadj.primitives.GraphicPrimitive;
import fidocadj.primitives.PrimitiveMacro;

/** EditorActions: contains a controller which can perform basic editor actions
    on a primitive database. Those actions include rotating and mirroring
    objects and selecting/deselecting them.

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

    Copyright 2014-2023 by Davide Bucci
</pre>

    @author Davide Bucci
*/

public class EditorActions
{
    private final DrawingModel dmp;
    private final UndoActions ua;
    private final SelectionActions sa;

    // Tolerance in pixels to select an object
    public int sel_tolerance = 10;


    /** Standard constructor: provide the database class.
        @param pp the Model containing the database.
        @param s the SelectionActions controller
        @param u the Undo controller, to ease undo operations.
    */
    public EditorActions (DrawingModel pp, SelectionActions s, UndoActions u)
    {
        dmp=pp;
        ua=u;
        sa=s;
        sel_tolerance = 10;
    }

    /** Set the current selection tolerance in pixels (the default when
        the class is created is 10 pixels.
        @param s the new tolerance.
    */
    public void setSelectionTolerance(int s)
    {
        sel_tolerance = s;
    }

    /** Get the selection tolerance in pixels.
        @return the current selection tolerance.
    */
    public int getSelectionTolerance()
    {
        return sel_tolerance;
    }

    /** Rotate all selected primitives.
    */
    public void rotateAllSelected()
    {
        GraphicPrimitive g = sa.getFirstSelectedPrimitive();

        if(g==null) {
            return;
        }

        final int ix = g.getFirstPoint().x;
        final int iy = g.getFirstPoint().y;

        sa.applyToSelectedElements(new ProcessElementsInterface()
        {
            public void doAction(GraphicPrimitive g)
            {
                g.rotatePrimitive(false, ix, iy);
            }
        });

        if(ua!=null) { ua.saveUndoState(); }
    }

    /** Move all selected primitives.
        @param dx relative x movement
        @param dy relative y movement
    */
    public void moveAllSelected(final int dx, final int dy)
    {
        sa.applyToSelectedElements(new ProcessElementsInterface()
        {
            public void doAction(GraphicPrimitive g)
            {
                g.movePrimitive(dx, dy);
            }
        });

        if(ua!=null) { ua.saveUndoState(); }
    }

    /** Mirror all selected primitives.
    */
    public void mirrorAllSelected()
    {
        GraphicPrimitive g = sa.getFirstSelectedPrimitive();
        if(g==null) {
            return;
        }

        final int ix = g.getFirstPoint().x;

        sa.applyToSelectedElements(new ProcessElementsInterface(){
            public void doAction(GraphicPrimitive g)
            {
                g.mirrorPrimitive(ix);
            }
        });

        if(ua!=null) { ua.saveUndoState(); }
    }

    /** Delete all selected primitives.
        @param saveState true if the undo controller should save the state
            of the drawing, after the delete operation is done. It should
            be put to false, when the delete operation is part of a more
            complex operation which is not yet ended after the call to this
            method.
    */
    public void deleteAllSelected(boolean saveState)
    {
        int i;
        List<GraphicPrimitive> v=dmp.getPrimitiveVector();

        for (i=0; i<v.size(); ++i){
            if(v.get(i).getSelected()) {
                v.remove(v.get(i--));
            }
        }
        if (saveState && ua!=null) {
            ua.saveUndoState();
        }
    }

    /** Sets the layer for all selected primitives.
        @param l the wanted layer index.
        @return true if at least a layer has been changed.
    */
    public boolean setLayerForSelectedPrimitives(int l)
    {
        boolean toRedraw=false;
        // Search for all selected primitives.
        for (GraphicPrimitive g: dmp.getPrimitiveVector()) {
            // If selected, change the layer. Macros must be always associated
            // to layer 0.
            if (g.getSelected() && ! (g instanceof PrimitiveMacro)) {
                g.setLayer(l);
                toRedraw=true;
            }
        }
        if(toRedraw) {
            dmp.sortPrimitiveLayers();
            dmp.setChanged(true);
            ua.saveUndoState();
        }
        return toRedraw;
    }

    /** Calculates the minimum distance between the given point and
        a set of primitive. Every coordinate is logical.

        @param px the x coordinate of the given point.
        @param py the y coordinate of the given point.
        @return the distance in logical units.
    */
    public int distancePrimitive(int px, int py)
    {
        int distance;
        int mindistance=Integer.MAX_VALUE;
        int layer=0;
        List<LayerDesc> layerV=dmp.getLayers();

        // Check the minimum distance by searching among all
        // primitives
        for (GraphicPrimitive g: dmp.getPrimitiveVector()) {
            distance=g.getDistanceToPoint(px,py);
            if(distance<=mindistance) {
                layer = g.getLayer();

                if(layerV.get(layer).isVisible) {
                    mindistance=distance;
                }
            }
        }
        return mindistance;
    }

    /** Handle the selection (or deselection) of objects. Search the closest
        graphical objects to the given (screen) coordinates.
        This method provides an interface to the {@link #selectPrimitive}
        method, which is oriented towards a more low-level process.

        @param cs the coordinate mapping to be employed.
        @param x the x coordinate of the click (screen).
        @param y the y coordinate of the click (screen).
        @param toggle select always if false, toggle selection on/off if true.
    */
    public void handleSelection(MapCoordinates cs, int x, int y,
        boolean toggle)
    {
        // Deselect primitives if needed.
        if(!toggle) {
            sa.setSelectionAll(false);
        }

        // Calculate a reasonable tolerance. If it is too small, we ensure
        // that it is rounded up to 2.
        int toll= cs.unmapXnosnap(x+sel_tolerance)-cs.unmapXnosnap(x);
        if (toll<2) { toll=2; }
        selectPrimitive(cs.unmapXnosnap(x), cs.unmapYnosnap(y), toll, toggle);
    }

    /** Select primitives close to the given point. Every parameter is given in
        logical coordinates.
        @param px the x coordinate of the given point (logical).
        @param py the y coordinate of the given point (logical).
        @param tolerance tolerance for the selection.
        @param toggle select always if false, toggle selection on/off if true
        @return true if a primitive has been selected.
    */
    private boolean selectPrimitive(int px, int py, int tolerance,
        boolean toggle)
    {
        int distance;
        int mindistance=Integer.MAX_VALUE;
        int layer;
        GraphicPrimitive gpsel=null;
        List<LayerDesc> layerV=dmp.getLayers();

        /*  The search method is very simple: we compute the distance of the
            given point from each primitive and we retain the minimum value, if
            it is less than a given tolerance.
        */
        for  (GraphicPrimitive g: dmp.getPrimitiveVector()) {
            layer = g.getLayer();
            if(layerV.get(layer).isVisible || g instanceof PrimitiveMacro) {
                distance=g.getDistanceToPoint(px,py);
                if (distance<=mindistance) {
                    gpsel=g;
                    mindistance=distance;
                }
            }
        }

        // Check if we found something!
        if (mindistance<tolerance && gpsel!=null) {
            if(toggle) {
                gpsel.setSelected(!gpsel.getSelected());
            } else {
                gpsel.setSelected(true);
            }
            return true;
        }
        return false;
    }
    /** Select primitives in a rectangular region (given in logical
        coordinates)
        @param px the x coordinate of the top left point.
        @param py the y coordinate of the top left point.
        @param w the width of the region
        @param h the height of the region
        @return true if at least a primitive has been selected
    */
    public boolean selectRect(int px, int py, int w, int h)
    {
        int layer;
        boolean s=false;

        // Avoid processing a trivial case.
        if(w<1 || h <1) {
            return false;
        }

        List<LayerDesc> layerV=dmp.getLayers();
        // Process every primitive, if the corresponding layer is visible.
        for (GraphicPrimitive g: dmp.getPrimitiveVector()){
            layer= g.getLayer();
            if((layer>=layerV.size() ||
                layerV.get(layer).isVisible ||
                g instanceof PrimitiveMacro) && g.selectRect(px,py,w,h))
            {
                s=true;
            }
        }
        return s;
    }
}