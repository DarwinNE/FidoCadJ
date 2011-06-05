package primitives;

import java.awt.*;
import java.util.*;

import globals.*;

/** StrokeStyle class.

	This class is used to centralize the creation of basic stroke styles 
	between all the drawing primitive. An important advantage is that this 
	allows to cache the creation of them.
	
	

*/

class StrokeStyle {

	private BasicStroke[] strokeList;
	private float actual_w;

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
		if (w!=actual_w) {
			strokeList = new BasicStroke[Globals.dashNumber];
			
			for(int i=0; i<Globals.dashNumber; ++i) {
				strokeList[i]=(new BasicStroke(w, BasicStroke.CAP_ROUND, 
            		BasicStroke.JOIN_ROUND, 
            		10.0f, Globals.dash[i], 
        			0.0f));
        	}
        	actual_w=w;
		}
		BasicStroke stroke=(BasicStroke)strokeList[dashStyle];
		
		
		return stroke; 
	}
	
	
}