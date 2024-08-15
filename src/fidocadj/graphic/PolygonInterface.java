package fidocadj.graphic;

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
    along with FidoCadJ. If not,
    @see <a href=http://www.gnu.org/licenses/>http://www.gnu.org/licenses/</a>.

    Copyright 2014-2023 by Davide Bucci
</pre>
*/
public interface PolygonInterface
{
    /** Add a point to the current polygon.
        @param x the x coordinate of the point.
        @param y the y coordinate of the point.
    */
    void addPoint(int x, int y);

    /** Get the current number of points in the polygon.
        @return the number of points.
    */
    int getNpoints();

    /** Reset the current polygon by deleting all the points.
    */
    void reset();

    /** Get a vector containing the x coordinates of the points.
        @return a vector containing the x coordinates of all points.
    */
    int[] getXpoints();

    /** Get a vector containing the y coordinates of the points.
        @return a vector containing the y coordinates of all points.
    */
    int[] getYpoints();

    /** Check if a given point is contained inside the polygon.
        @param x the x coordinate of the point to be checked.
        @param y the y coordinate of the point to be checked.
        @return true of the point is internal to the polygon, false otherwise.
    */
    boolean contains(int x, int y);
}