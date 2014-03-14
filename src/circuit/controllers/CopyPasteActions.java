package circuit.controllers;

import java.awt.datatransfer.*; 	// Used in copySelected
import java.awt.*;	// To remove (Toolkit)

import clipboard.*;
import circuit.*;
import circuit.model.*;


/** CopyPasteActions: contains a controller which can perform copy and paste 
	actions on a primitive database
    
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

    // True if elements should be shifted when copy/pasted
    private boolean shiftCP;	
	/** Standard constructor: provide the database class.
	*/
	public CopyPasteActions(DrawingModel pp, EditorActions ed, 
		ParserActions aa, UndoActions u)
	{
		P=pp;
		edt=ed;
		pa=aa;
		ua=u;
		shiftCP=false;
	}
		
	/** Paste from the system clipboard
    	@param xstep if the shift should be applied, this is the x shift
    	@param ystep if the shift should be applied, this is the y shift
    */
    public void paste(int xstep, int ystep)
    {
        TextTransfer textTransfer = new TextTransfer();
        
        edt.setSelectionAll(false);
        
        try {
            pa.addString(new 
                StringBuffer(textTransfer.getClipboardContents()),true);
        } catch (Exception E) {
        	System.out.println("Warning: paste operation has gone wrong.");
        }
        
        if(shiftCP)
        	edt.moveAllSelected(xstep, ystep);
        
        ua.saveUndoState();
        P.setChanged(true);
    }
    
    /** Copy in the system clipboard all selected primitives.
        @param extensions specify if FCJ extensions should be applied
        @param splitNonStandard specify if non standard macros should be split
    */
    public void copySelected(boolean extensions, boolean splitNonStandard,
    	int xstep, int ystep)
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
        
        // get the system clipboard
        Clipboard systemClipboard =Toolkit.getDefaultToolkit()
            .getSystemClipboard();
        
        Transferable transferableText = new StringSelection(s.toString());
        systemClipboard.setContents(transferableText,null);
    }
    
    /** Returns true if the elements are shifted when copy/pasted
    
    */
    public boolean getShiftCopyPaste()
    {
    	return shiftCP;
    }
    
    /** Determines if the elements are shifted when copy/pasted
    
    */
    public void setShiftCopyPaste(boolean s)
    {
    	shiftCP=s;
    }
}    