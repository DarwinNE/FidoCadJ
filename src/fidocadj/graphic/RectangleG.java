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

    /**
     * Returns the x-coordinate of the upper-left corner of the rectangle.
     *
     * @return the x-coordinate of the rectangle
     */
    public int getX()
    {
        return x;
    }

    /**
     * Returns the y-coordinate of the upper-left corner of the rectangle.
     *
     * @return the y-coordinate of the rectangle
     */
    public int getY()
    {
        return y;
    }

    /**
     * Returns the width of the rectangle.
     *
     * @return the width of the rectangle
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Returns the height of the rectangle.
     *
     * @return the height of the rectangle
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * Checks whether a point (px, py) is contained within this rectangle.
     *
     * @param px the x-coordinate of the point to check
     * @param py the y-coordinate of the point to check
     *
     * @return true if the point is within the rectangle, false otherwise
     */
    public boolean contains(int px, int py)
    {
        return px >= x && px <= x + width && py >= y && py <= y + height;
    }

    /**
     * Checks whether this rectangle intersects with another SelectionRectangle.
     *
     * @param other the other SelectionRectangle to check intersection with
     *
     * @return true if the rectangles intersect, false otherwise
     */
    public boolean intersects(RectangleG other)
    {
        return other.getX() < this.x + this.width
                && other.getX() + other.getWidth() > this.x
                && other.getY() < this.y + this.height
                && other.getY() + other.getHeight() > this.y;
    }

    /**
     * Checks whether a line defined by two points (x1, y1) and (x2, y2)
     * intersects with this rectangle.
     *
     * @param x1 the x-coordinate of the first point of the line
     * @param y1 the y-coordinate of the first point of the line
     * @param x2 the x-coordinate of the second point of the line
     * @param y2 the y-coordinate of the second point of the line
     *
     * @return true if the line intersects the rectangle, false otherwise
     */
    public boolean intersectsLine(int x1, int y1, int x2, int y2)
    {
        // Check if the line intersects any of the four edges of the rectangle
        return lineIntersectsLine(x1, y1, x2, y2,
                x, y, x + width, y)
                || // Top edge
                lineIntersectsLine(x1, y1, x2, y2,
                        x, y, x, y + height)
                || // Left edge
                lineIntersectsLine(x1, y1, x2, y2, x + width, y,
                        x + width, y + height)
                || // Right edge
                lineIntersectsLine(x1, y1, x2, y2, x, y + height,
                        x + width, y + height); // Bottom edge
    }

    /**
     * Helper method to check if two line segments (x1, y1) to (x2, y2) and
     * (x3, y3) to (x4, y4) intersect.
     *
     * @param x1 the x-coordinate of the first point of the first line
     * @param y1 the y-coordinate of the first point of the first line
     * @param x2 the x-coordinate of the second point of the first line
     * @param y2 the y-coordinate of the second point of the first line
     * @param x3 the x-coordinate of the first point of the second line
     * @param y3 the y-coordinate of the first point of the second line
     * @param x4 the x-coordinate of the second point of the second line
     * @param y4 the y-coordinate of the second point of the second line
     *
     * @return true if the two line segments intersect, false otherwise
     */
    private boolean lineIntersectsLine(int x1, int y1, int x2, int y2,
            int x3, int y3, int x4, int y4)
    {
        // Calculate the direction of the lines
        int d1 = direction(x3, y3, x4, y4, x1, y1);
        int d2 = direction(x3, y3, x4, y4, x2, y2);
        int d3 = direction(x1, y1, x2, y2, x3, y3);
        int d4 = direction(x1, y1, x2, y2, x4, y4);

        // If the directions are different, the lines intersect
        if (d1 != d2 && d3 != d4) {
            return true;
        }

        // Check for collinear cases
        if (d1 == 0 && onSegment(x3, y3, x4, y4, x1, y1)) {
            return true;
        }
        if (d2 == 0 && onSegment(x3, y3, x4, y4, x2, y2)) {
            return true;
        }
        if (d3 == 0 && onSegment(x1, y1, x2, y2, x3, y3)) {
            return true;
        }
        if (d4 == 0 && onSegment(x1, y1, x2, y2, x4, y4)) {
            return true;
        }

        return false;
    }

    /**
     * Helper method to find the direction of the triplet..
     * (px, py), (qx, qy), (rx, ry).
     *
     * @param px the x-coordinate of the first point
     * @param py the y-coordinate of the first point
     * @param qx the x-coordinate of the second point
     * @param qy the y-coordinate of the second point
     * @param rx the x-coordinate of the third point
     * @param ry the y-coordinate of the third point
     *
     * @return 0 if collinear, 1 if clockwise, 2 if counterclockwise
     */
    private int direction(int px, int py, int qx, int qy, int rx, int ry)
    {
        int val = (qy - py) * (rx - qx) - (qx - px) * (ry - qy);
        if (val == 0) {
            return 0; // collinear
        } else {
            if (val > 0) {
                return 1; // clockwise
            } else {
                return 2; // counterclockwise
            }
        }
    }

    /**
     * Helper method to check if the point (rx, ry) lies on the line segment
     * defined by (px, py) to (qx, qy).
     *
     * @param px the x-coordinate of the first point of the segment
     * @param py the y-coordinate of the first point of the segment
     * @param qx the x-coordinate of the second point of the segment
     * @param qy the y-coordinate of the second point of the segment
     * @param rx the x-coordinate of the point to check
     * @param ry the y-coordinate of the point to check
     *
     * @return true if the point lies on the segment, false otherwise
     */
    private boolean onSegment(int px, int py, int qx, int qy, int rx, int ry)
    {
        return rx >= Math.min(px, qx) && rx <= Math.max(px, qx)
                && ry >= Math.min(py, qy) && ry <= Math.max(py, qy);
    }
}