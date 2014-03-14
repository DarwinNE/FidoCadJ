package graphic.android;

import android.graphics.*;


import graphic.*;

/** Shape implementation for Android.

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
public class ShapeAndroid implements ShapeInterface
{
	Path path;
	
	public Path getPath()
	{
		return path;
	}
	
	public ShapeAndroid()
	{
		path = new Path();
	}
	
	public void createCubicCurve(int x0, int y0, int x1, int y1, 
		int x2, int y2, int x3, int y3)
	{
		path.reset();
		path.moveTo(x0,y0);
		path.cubicTo (x1, y1, x2, y2, x3, y3);
		
	}
	
	public void createGeneralPath(int npoints)
	{
		path.reset();
	}

	public RectangleG getBounds()
	{
		return new RectangleG(0,0,0,0);
	}
	
	public void moveTo(float x, float y)
	{
		path.moveTo(x, y);
	}
	
	public void curveTo(float x0, float y0, float x1, float y1,
		float x2, float y2) 
	{
		path.cubicTo (x0, y0, x1, y1, x2, y2);
	}
	
	public void closePath()
	{
		path.close();
	}
}