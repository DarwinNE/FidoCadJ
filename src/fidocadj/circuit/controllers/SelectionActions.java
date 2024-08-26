package fidocadj.circuit.controllers;

import java.util.*;

import fidocadj.circuit.model.DrawingModel;
import fidocadj.circuit.model.ProcessElementsInterface;
import fidocadj.primitives.GraphicPrimitive;
import fidocadj.primitives.PrimitiveMacro;

/** SelectionActions: contains a controller which handles those actions
    which involve selection operations or which apply to selected elements.
    The actions proposed by this class involve selected elements. However,
    no action proposes a change of the characteristics of the elements, at
    least directly.

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

    Copyright 2015-2023 by Davide Bucci
    </pre>
*/
public class SelectionActions
{
    private final DrawingModel dmp;

    /** Construct the controller and associates it to a given model.
        @param pp the model to be employed.
    */
    public SelectionActions(DrawingModel pp)
    {
        dmp=pp;
    }

    /** Get the first selected primitive
        @return the selected primitive, null if none.
    */
    public GraphicPrimitive getFirstSelectedPrimitive()
    {
        for (GraphicPrimitive g: dmp.getPrimitiveVector()) {
            if (g.getSelected()) {
                return g;
            }
        }
        return null;
    }

    /** Apply an action to selected elements contained in the model.
        @param tt the method containing the action to be performed
    */
    public void applyToSelectedElements(ProcessElementsInterface tt)
    {
        for (GraphicPrimitive g:dmp.getPrimitiveVector()){
            if (g.getSelected()) {
                tt.doAction(g);
            }
        }
    }
    /** Get an array describing the state of selection of the objects.
        @return a vector containing Boolean objects with the selection states
            of all objects in the database.
    */
    public List<Boolean> getSelectionStateVector()
    {
        List<Boolean> v =
            new Vector<Boolean>(dmp.getPrimitiveVector().size());

        for(GraphicPrimitive g : dmp.getPrimitiveVector()) {
            v.add(Boolean.valueOf(g.getSelected()));
        }
        return v;
    }

    /** Select/deselect all primitives.
        @param state true if you want to select, false for deselect.
    */
    public void setSelectionAll(boolean state)
    {
        for (GraphicPrimitive g: dmp.getPrimitiveVector()) {
            g.setSelected(state);
        }
    }

    /** Sets the state of the objects in the database according to the given
        vector.
        @param v the vector containing the selection state of elements
    */
    public void setSelectionStateVector(List<Boolean> v)
    {
        int i=0;

        for(GraphicPrimitive g : dmp.getPrimitiveVector()) {
            g.setSelected(v.get(i++).booleanValue());
        }
    }

    /**
     Obtains a list containing all selected graphic primitives.

     @return a list containing all selected graphical objects.
     */
    public List<GraphicPrimitive> getSelectedPrimitives()
    {
        List<GraphicPrimitive> selectedPrimitives = new ArrayList<>();

        for (GraphicPrimitive g : dmp.getPrimitiveVector()) {
            if (g.getSelected()) {
                selectedPrimitives.add(g);
            }
        }

        return selectedPrimitives;
    }

    /** Determine if only one primitive has been selected
        @return true if only one primitive is selected, false otherwise (which
            means that either more than several primitives or no primitive are
            selected).
    */
    public boolean isUniquePrimitiveSelected()
    {
        boolean isUnique=true;
        boolean hasFound=false;

        for (GraphicPrimitive g: dmp.getPrimitiveVector()) {
            if (g.getSelected()) {
                if(hasFound) {
                    return false;
                }
                hasFound = true;
            }
        }

        return hasFound && isUnique;
    }

    /** Determine if the selection can be splitted
        @return true if the selection contains at least a macro, or some of
            its elements have a name or a value (which are separated).
    */
    public boolean selectionCanBeSplitted()
    {
        for (GraphicPrimitive g: dmp.getPrimitiveVector()) {
            if (g.getSelected() &&
                (g instanceof PrimitiveMacro ||
                 g.hasName() || g.hasValue()))
            {
                return true;
            }
        }
        return false;
    }

    /** Obtain a string containing all the selected elements.
        @param extensions true if FidoCadJ extensions should be used.
        @param pa the parser controller.
        @return the string.
    */
    public StringBuffer getSelectedString(boolean extensions, ParserActions pa)
    {
        StringBuffer s=new StringBuffer("[FIDOCAD]\n");

        s.append(pa.registerConfiguration(extensions));

        for (GraphicPrimitive g: dmp.getPrimitiveVector()){
            if(g.getSelected()) {
                s.append(g.toString(extensions));
            }
        }
        return s;
    }
}