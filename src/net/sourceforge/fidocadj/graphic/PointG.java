package net.sourceforge.fidocadj.graphic;

/** PointG is a class implementing a point with its coordinates (integer).
    P.S. why are you smirking?

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
public class PointG
{
    public int x;
    public int y;
    public PointG(int x, int y)
    {
        this.x=x;
        this.y=y;
    }

    public PointG()
    {
        this.x=0;
        this.y=0;
    }
}