package graphic.swing;

import java.awt.*;
import graphic.*;


/** PolygonInterface specifies methods for handling a polygon.

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
public class PolygonSwing implements PolygonInterface
{
	private final Polygon p;

	public PolygonSwing()
	{
		p=new Polygon();
	}

	public void addPoint(int x, int y) 
	{
		p.addPoint(x,y);
	}
	
	public Polygon getSwingPolygon()
	{
		return p;
	}
	public void reset()
	{
		p.reset();
	}
	public int getNpoints()
	{
		return p.npoints;
	}
	
	public int[] getXpoints()
	{
		return p.xpoints;
	}
	
	public int[] getYpoints()
	{
		return p.ypoints;
	}
	
	public boolean contains(int x, int y)
	{
		return p.contains(x,y);
	}
}