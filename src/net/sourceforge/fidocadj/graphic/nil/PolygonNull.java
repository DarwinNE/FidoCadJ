package net.sourceforge.fidocadj.graphic.nil;

import java.awt.*;
import net.sourceforge.fidocadj.graphic.*;


/**		SWING VERSION


	PolygonInterface specifies methods for handling a polygon.
	TODO: reduce dependency on java.awt.*;

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
public class PolygonNull implements PolygonInterface
{

	private final Polygon p;
	
	public PolygonNull()
	{
		p=new Polygon();
	}

	public void addPoint(int x, int y) 
	{
		p.addPoint(x,y);
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