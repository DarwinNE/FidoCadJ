package net.sourceforge.fidocadj.graphic.nil;

import java.awt.*;
import java.awt.geom.*;
import net.sourceforge.fidocadj.graphic.*;



/**		SWING VERSION

	ShapeNull is a wrapper around the Shape Swing class.
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
public class ShapeNull implements ShapeInterface
{
	Shape s;
	
	public ShapeNull()
	{
		s=null;
	}
	
	public void createCubicCurve(int x0, int y0, int x1, int y1, 
		int x2, int y2, int x3, int y3)
	{
		s = new CubicCurve2D.Float(x0, y0, x1, y1, x2, y2, x3, y3);
	}
	
	public void createGeneralPath(int npoints)
	{
		s = new GeneralPath(GeneralPath.WIND_EVEN_ODD, npoints);
	}

	public RectangleG getBounds()
	{
		Rectangle r=s.getBounds();
		return new RectangleG(r.x, r.y, r.width, r.height);
	}
	
	public void moveTo(float x, float y)
	{
		GeneralPath gp=(GeneralPath)s;
		gp.moveTo(x,y);
	}

	
	public void curveTo(float x0, float y0, float x1, float y1,
		float x2, float y2) 
	{
		GeneralPath gp=(GeneralPath)s;
		gp.curveTo(x0, y0,x1, y1,x2, y2);
	}
	
	public void closePath()
	{
		GeneralPath gp=(GeneralPath)s;
		gp.closePath();
	}
}