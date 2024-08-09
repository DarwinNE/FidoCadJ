package fidocadj.graphic;

/** RectangleG is a class implementing a rectangle with its coordinates
    (integer).

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
public class RectangleG
{
    public int x;
    public int y;
    public int height;
    public int width;

    /** Standard constructor of the rectangle.
        @param x the x coordinates of the leftmost side.
        @param y the y coordinates of the topmost side.
        @param width the width of the rectangle.
        @param height the height of the rectangle.
    */
    public RectangleG(int x, int y, int width, int height)
    {
        this.x=x;
        this.y=y;
        this.width=width;
        this.height=height;
    }

    /** Standard constructor. All the coordinates and sizes are put equal to
        zero.
    */
    public RectangleG()
    {
        this.x=0;
        this.y=0;
        this.width=0;
        this.height=0;
    }
}