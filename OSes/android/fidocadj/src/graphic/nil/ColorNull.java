package graphic.nil;

import graphic.*;


/**  	ANDROID VERSION

	Null color class. Does nothing :-)
	
	    
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

public class ColorNull implements ColorInterface
{
	
	public ColorNull()
	{
		// Does nothing.
	}
	
	
	public ColorInterface white() 
	{	
		return new ColorNull();
	}

	public ColorInterface gray() 
	{
		return new ColorNull();
	}

	public ColorInterface green() 
	{
		return new ColorNull();
	}
	
	public ColorInterface red() 
	{
		return new ColorNull();
	}
	
	public ColorInterface black() 
	{
		return new ColorNull();
	}
	
	public int getRed()
	{
		return 0;
	}

	public int getGreen()
	{
		return 0;
	}

	public int getBlue()
	{
		return 0;
	}	
	
	public int getRGB()
	{
		return 0;
	}
	public void setRGB(int rgb)
	{
		// Does nothing.
	}
}
