package primitives;

import java.awt.*;
import java.util.*;

import globals.*;

/** StrokeStyle class.

	This class is used to centralize the creation of basic stroke styles 
	between all the drawing primitive. An important advantage is that this 
	allows to cache their creation, thus saving a little bit of time.
	
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

    Copyright 2010-2013 by Davide Bucci
</pre>
*/

class StrokeStyle 
{
	private BasicStroke[] strokeList;
	private float actual_w;

	/** Standard creator.
		the stroke width will be created at the first use.
	*/
	void StrokeStyle()
	{
		actual_w=-1;
	}
	
	/** Retrieves or create a BasicStroke object having the wanted with and
		style
		@param w the width in pixel
		@param dashStyle the style of the stroke
		@return the retrieved BasicStroke object to be used in drawing ops.
	*/
	BasicStroke getStroke(float w, int dashStyle) 
	{
		
		if (w!=actual_w && w>0) {
			strokeList = new BasicStroke[Globals.dashNumber];
			
			// If the line width has been changed, we need to update the 
			// stroke table
			
			// The first entry is non dashed
			strokeList[0]=new BasicStroke(w, BasicStroke.CAP_ROUND, 
            		BasicStroke.JOIN_ROUND);
            // Then, the dashed stroke styles are created
			for(int i=1; i<Globals.dashNumber; ++i) {
				strokeList[i]=(new BasicStroke(w, BasicStroke.CAP_ROUND, 
            		BasicStroke.JOIN_ROUND, 
            		10.0f, Globals.dash[i], 
        			0.0f));
        	}
        	actual_w=w;
		}
		
		// Here we retrieve the stroke style corresponding to the given 
		// dashStyle
		return (BasicStroke)strokeList[dashStyle];
		
	}
}