package net.sourceforge.fidocadj.circuit.controllers;

import net.sourceforge.fidocadj.circuit.*;
import net.sourceforge.fidocadj.circuit.model.*;
import net.sourceforge.fidocadj.globals.*;


/** CopyPasteActions: contains a controller which can perform copy and paste 
	actions on a primitive database.
    
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

    Copyright 2007-2014 by Davide Bucci
</pre>

    @author Davide Bucci
*/

public class CopyPasteActions {

	private final DrawingModel P;
	private final EditorActions edt;
	private final ParserActions pa;
	private final UndoActions ua;
	private final ProvidesCopyPasteInterface cpi;

    // True if elements should be shifted when copy/pasted
    private boolean shiftCP;	
	/** Standard constructor.
		@param pp the drawing model.
		@param ed an editor controller.
		@param aa a parser controller (pasting implies parsing).
		@param u an undo controller.
		@param p an object with copy and paste methods available.
	*/
	public CopyPasteActions(DrawingModel pp, EditorActions ed, 
		ParserActions aa, UndoActions u, ProvidesCopyPasteInterface p)
	{
		P=pp;
		edt=ed;
		pa=aa;
		ua=u;
		cpi=p;
		shiftCP=false;
	}
		
	/** Paste from the system clipboard
    	@param xstep if the shift should be applied, this is the x shift
    	@param ystep if the shift should be applied, this is the y shift
    */
    public void paste(int xstep, int ystep)
    {
        edt.setSelectionAll(false);
        
        try {
        	pa.addString(new  StringBuffer(cpi.pasteText()), true);
        } catch (Exception E) {
        	System.out.println("Warning: paste operation has gone wrong.");
        }
        
        if(shiftCP)
        	edt.moveAllSelected(xstep, ystep);
        
        ua.saveUndoState();
        P.setChanged(true);
    }
    
    /** Copy in the system clipboard all selected primitives.
        @param extensions specify if FCJ extensions should be applied.
        @param splitNonStandard specify if non standard macros should be split.
    */
    public void copySelected(boolean extensions, boolean splitNonStandard)
    {
        StringBuffer s = edt.getSelectedString(extensions, pa);
        
        /*  If we have to split non standard macros, we need to work on a 
            temporary file, since the splitting works on the basis of the 
            export technique.       
            The temporary file will then be loaded in the clipboard.
        */
        if (splitNonStandard) {
			s=pa.splitMacros(s,  false);
        }
        
        cpi.copyText(s.toString());
    }
    
    /** Returns true if the elements are shifted when copy/pasted
    
    */
    public boolean getShiftCopyPaste()
    {
    	return shiftCP;
    }
    
    /** Determines if the elements are to be shifted when copy/pasted
    	@param s true if the elements should be shifted
    */
    public void setShiftCopyPaste(boolean s)
    {
    	shiftCP=s;
    }
}    