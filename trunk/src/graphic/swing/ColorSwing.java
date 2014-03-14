package graphic.swing;

import java.awt.Color;
import graphic.*;


/** This class maps the general interface to java.awt.Color.
	
	    
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

    Copyright 2014 by Davide Bucci
</pre>
*/

public class ColorSwing implements ColorInterface
{
	Color c;
	
	public ColorSwing()
	{
		c= Color.black;
	}
	
	public ColorSwing(Color c)
	{
		this.c=c;
	}
	
	public Color getColorSwing()
	{
		return c;
	}
	
	public ColorInterface white() 
	{
		
		return new ColorSwing(Color.white);	
	}

	public ColorInterface gray() 
	{
		return new ColorSwing(Color.gray);	
	}

	public ColorInterface green() 
	{
		return new ColorSwing(Color.green);
	}
	
	public ColorInterface red() 
	{
		return new ColorSwing(Color.red);
	}
	
	public ColorInterface black() 
	{
		return new ColorSwing(Color.black);
	}
	
	public int getRed()
	{
		return c.getRed();
	}

	public int getGreen()
	{
		return c.getGreen();
	}

	public int getBlue()
	{
		return c.getBlue();
	}	
	
	public int getRGB()
	{
		return c.getRGB();
	}
	public void setRGB(int rgb)
	{
		c=new Color(rgb);
	}
}
