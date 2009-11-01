import java.awt.*;


/** layerDesc.java v.1.0

	
   Provide a complete description of each layer (color, visibility).
<pre>
   ****************************************************************************
   Version History 

Version   Date           	Author      Remarks
------------------------------------------------------------------------------
1.0     December 2007		D. Bucci    First working version
1.1		June 2009			D. Bucci 	Capitalize the first letters                                     

   Written by Davide Bucci, December 2007, davbucci at tiscali dot it
   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

	</pre>

   The class layerDesc provides a complete description of each layer.

	@author Davide Bucci
	@version 1.1 June 2009
	
	*/
	
public class LayerDesc 
{
  	private Color layerColor;
  	private boolean isVisible;
  	private String LayerDescription;
  	
  	/** Standard constructor: obtain a visible layer with a black color and no 
  		description.
  	*/
  	
  	LayerDesc()
  	{
  		layerColor=Color.black;
  		isVisible=true;
  		LayerDescription="";
  	
  	}
  	
  	/** Standard constructor
  		
  		@param c the color which should be used
  		@param v the visibility of the layer
  		@param d the layer description
  	*/
  	
  	LayerDesc(Color c, boolean v, String d)
  	{
  		layerColor=c;
  		isVisible=v;
  		LayerDescription=d;
  	
  	}
  	/** This method allows to obtain the color in which this layer should be
  		drawn.
  		
  		@return the color to be used
  	*/
  	Color getColor()
  	{
  		return layerColor;
  	}
  	
  	
  	/** This method returns true if this layer should be traced
  		
  		@return a boolean value indicating if the layer should be drawn
  	*/
  	boolean getVisible()
  	{
  		return isVisible;
  	}
  	
  	/** This method allows to obtain the color in which this layer should be
  		drawn.
  		
  		@return the color to be used
  	*/
  	String getDescription()
  	{
  		return LayerDescription;
  	}
  	
  	
  	/** This method allows to set the layer description.
  		
  		@param s the layer description
  	*/
  	void setDescription(String s)
  	{
  		LayerDescription=s;
  	}
  	
  	/** This method allows to set the layer visibility.
  		
  		@param s the layer visibility.
  	*/
  	void setVisible(boolean v)
  	{
  		isVisible=v;
  	}
  	
  	/** This method allows to set the layer color.
  		
  		@param s the layer color
  	*/
  	void setColor(Color c)
  	{
  		layerColor=c;
  	}
}