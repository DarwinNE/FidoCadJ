package net.sourceforge.fidocadj;

import javax.swing.*;

import java.io.*;

import globals.*;
import net.sourceforge.fidocadj.circuit.*;
import net.sourceforge.fidocadj.circuit.model.*;
import net.sourceforge.fidocadj.export.*;

/** The RunExport class implements a runnable class which can be employed
	to perform all exporting operations in a separate thread from the main
	user interface one.

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

    Copyright 2012-2014 by Davide Bucci
</pre>

    Class which realizes the export of a file towards a graphical format.
    
    @author Davide Bucci
*/

class RunExport implements Runnable 
{
	private File file; 
	private DrawingModel P; 
	private String format;
	private double unitPerPixel;
	private boolean antiAlias;
	private boolean blackWhite;
	private boolean ext;
	private JFrame parent;
	//boolean success;
	
	/** Setting up the parameters needed for the export
	@param tfile the file name
	@param tP the DrawingModel object containing the drawing to be exported
	@param tformat the file format to be used
	@param tunitPerPixel the magnification factor to be used for the export
	@param tantiAlias the application of anti alias for bitmap export
	@param tblackWhite black and white export
	@param text the extensions to be activated or not
	
	*/
    public void setParam(File tfile, 
		DrawingModel tP, 
		String tformat,
		double tunitPerPixel,
		boolean tantiAlias,
		boolean tblackWhite,
		boolean text,
		JFrame tparent) 
	{
		file=tfile;
		P = tP;
		format = tformat;
		unitPerPixel = tunitPerPixel;
		antiAlias= tantiAlias;
		blackWhite=tblackWhite;
		ext=text;
		parent=tparent;
	}
	
	/** Launch the export (in a new thread).
	*/
	public void run() 
	{
		try {
    		ExportGraphic.export(file, P, format, unitPerPixel,
				antiAlias, blackWhite, ext, true);
       		JOptionPane.showMessageDialog(parent,
                Globals.messages.getString("Export_completed"));
		}  catch(IOException ioe) {
        	JOptionPane.showMessageDialog(parent,
     	       Globals.messages.getString("Export_error")+ioe);
        } catch(IllegalArgumentException iae) {
            JOptionPane.showMessageDialog(parent,
               Globals.messages.getString("Illegal_filename"));
        }
    }
}