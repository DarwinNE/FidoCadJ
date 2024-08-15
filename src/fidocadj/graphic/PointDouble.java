package fidocadj.graphic;

/** PointDouble is a class implementing a point with its coordinates (double).

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

    Copyright 2014-2015 by Davide Bucci
    </pre>
*/
public class PointDouble
{
    public double x;
    public double y;

    /** Standard constructor.
        @param x the x coordinate of the point.
        @param y the y coordinate of the point.
    */
    public PointDouble(double x, double y)
    {
        this.x=x;
        this.y=y;
    }

    /** Standard constructor. The x and y coordinates are put equal to zero.
    */
    public PointDouble()
    {
        this.x=0;
        this.y=0;
    }
}