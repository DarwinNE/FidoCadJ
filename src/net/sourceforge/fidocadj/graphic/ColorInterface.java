package net.sourceforge.fidocadj.graphic;

/** Provides a general way to access colors.
<P>
    D.B.: WHY THE HELL THERE ARE TWO DIFFERENT CLASSES java.awt.Color AND
    android.graphics.Color. TWO DIFFERENT INCOMPATIBLE THINGS TO DO
    EXACTLY THE SAME STUFF.
    SHAME SHAME SHAME!
</P>

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

    Copyright 2014-2015 by Davide Bucci
</pre>
*/

public interface ColorInterface
{
    public ColorInterface white();
    public ColorInterface gray();
    public ColorInterface green();
    public ColorInterface red();
    public ColorInterface black();


    public int getGreen();
    public int getRed();
    public int getBlue();

    public int getRGB();
    public void setRGB(int rgb);

    //public PolygonInterface createPolygon();
}
