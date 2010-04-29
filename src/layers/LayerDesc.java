package layers;

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

	Copyright 2008-2010 by Davide Bucci

	</pre>

   The class layerDesc provides a complete description of each layer.

	@author Davide Bucci
	@version 1.2 April 2010
	
	*/
	
public class LayerDesc 
{
  	private Color layerColor;
  	private boolean isVisible;
  	private String LayerDescription;
  	private float alpha;
  	
  	/** Standard constructor: obtain a visible layer with a black color and no 
  		description.
  	*/
  	
  	public LayerDesc()
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
  	
  	public LayerDesc(Color c, boolean v, String d, float a)
  	{
  		layerColor=c;
  		isVisible=v;
  		LayerDescription=d;
  		alpha = a;
  	
  	}
  	/** This method allows to obtain the color in which this layer should be
  		drawn.
  		
  		@return the color to be used
  	*/
  	final public Color getColor()
  	{
  		return layerColor;
  	}

  	/** This method allows to obtain the alpha channel of the current layer.
  		
  		@return the alpha blend 
  	*/
  	
  	final public float getAlpha()
  	{
  		return alpha;
  	}
  	
  	
  	/** This method returns true if this layer should be traced
  		
  		@return a boolean value indicating if the layer should be drawn
  	*/
  	final public boolean getVisible()
  	{
  		return isVisible;
  	}
  	
  	/** This method allows to obtain the color in which this layer should be
  		drawn.
  		
  		@return the color to be used
  	*/
  	public String getDescription()
  	{
  		return LayerDescription;
  	}
  	
  	
  	/** This method allows to set the layer description.
  		
  		@param s the layer description
  	*/
  	final public void setDescription(String s)
  	{
  		LayerDescription=s;
  	}
  	
  	/** This method allows to set the layer visibility.
  		
  		@param v the layer visibility.
  	*/
  	final public void setVisible(boolean v)
  	{
  		isVisible=v;
  	}
  	
  	/** This method allows to set the layer color.
  		
  		@param c the layer color
  	*/
  	final public void setColor(Color c)
  	{
  		layerColor=c;
  	}
  	
  	 /** This method allows to set the alpha blend.
  		
  		@param a the alpha blend
  	*/
  	final public void setAlpha(float a)
  	{
  		alpha=a;
  	}
}