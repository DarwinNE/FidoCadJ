package net.sourceforge.fidocadj.circuit.controllers;

import java.util.*;

import net.sourceforge.fidocadj.circuit.*;
import net.sourceforge.fidocadj.circuit.model.*;
import net.sourceforge.fidocadj.primitives.GraphicPrimitive;

/** SelectionActions: contains a controller which handles those actions
	which involve selection operations or which apply to selected elements.
    
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
    along with FidoCadJ.  If not, see <http://www.gnu.org/licenses/>.

    Copyright 2015 by Davide Bucci
</pre>
*/

public class SelectionActions
{
	private final DrawingModel P;
	
	public SelectionActions(DrawingModel pp)
	{
		P=pp;
	}
	
	/** Get the first selected primitive
        @return the selected primitive, null if none.
    */
    public GraphicPrimitive getFirstSelectedPrimitive()
    {
        for (GraphicPrimitive g: P.getPrimitiveVector()) {
            if (g.getSelected())
                return g;
        }
        return null;
    }
    
	/** Apply an action to selected elements contained in the model.
    	@tt the method containing the action to be performed
    */
    public void applyToSelectedElements(ProcessElementsInterface tt)
    {
    	for (GraphicPrimitive g:P.getPrimitiveVector()){
    		if (g.getSelected())
    			tt.doAction(g);
    	}
    }
    /** Get an array describing the state of selection of the objects.
    	@return a vector containing Boolean objects with the selection states
    		of all objects in the database.
    */
    public Vector<Boolean> getSelectionStateVector()
    {
        int i;
        Vector<Boolean> v = new Vector<Boolean>(P.getPrimitiveVector().size());
   
        for(GraphicPrimitive g : P.getPrimitiveVector()) {
        	v.add(Boolean.valueOf(g.getSelected()));
        }
        return v;
    }
    
    /** Select/deselect all primitives.
    	@param state true if you want to select, false for deselect.  
    */
    public void setSelectionAll(boolean state)
    {
        for (GraphicPrimitive g: P.getPrimitiveVector()) {
            g.setSelected(state);
        }   
    }
    
    /** Sets the state of the objects in the database according to the given
    	vector.
    	@param v the vector containing the selection state of elements
    */
    public void setSelectionStateVector(Vector<Boolean> v)
    {
    	int i=0;
        
        for(GraphicPrimitive g : P.getPrimitiveVector()) {
        	g.setSelected(v.get(i++).booleanValue());
        }
    } 
}