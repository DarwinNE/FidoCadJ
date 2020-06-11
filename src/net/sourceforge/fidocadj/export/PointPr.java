package net.sourceforge.fidocadj.export;

/** A simple point featuring double-precision coordinates

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
    along with FidoCadJ. If not,
    @see <a href=http://www.gnu.org/licenses/>http://www.gnu.org/licenses/</a>.

    Copyright 2020 by Davide Bucci
    </pre>


    @author Davide Bucci
*/
public class PointPr
{
    public double x;
    public double y;
    /** Standard constructor, yielding a (0,0) coordinate.
    */
    public PointPr()
    {
        x=0;y=0;
    }
    /** Constructor, yielding a generic coordinate.
        @param xx the x coordinate.
        @param yy the y coordinate.
    */
    public PointPr(double xx, double yy)
    {
        x=xx;y=yy;
    }
}