package fidocadj.graphic;

/**
 * Represents a line segment between two points (x1, y1) and (x2, y2).
 * This class provides methods to determine if the line segment intersects
 * with a given rectangle.
 *
 * <pre>
 * This file is part of FidoCadJ.
 *
 * FidoCadJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FidoCadJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FidoCadJ. If not,
 * @see <a href=http://www.gnu.org/licenses/>http://www.gnu.org/licenses/</a>.
 *
 * Copyright 2024 by Manuel Finessi
 * </pre>
 */
public class LineSegment
{

    private final double x1, y1, x2, y2;

    /**
     * Constructs a LineSegment with the given endpoints.
     *
     * @param x1 the x-coordinate of the first endpoint.
     * @param y1 the y-coordinate of the first endpoint.
     * @param x2 the x-coordinate of the second endpoint.
     * @param y2 the y-coordinate of the second endpoint.
     */
    public LineSegment(double x1, double y1, double x2, double y2)
    {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    /**
     * Checks if this line segment intersects with a given rectangle.
     * The intersection is checked against the four edges of the rectangle.
     *
     * @param rect the selection rectangle to check for intersection.
     *
     * @return true if the line segment intersects with the rectangle,
     * false otherwise.
     */
    public boolean intersects(RectangleG rect)
    {
        // Check if any of the rectangle's edges intersect with the line segment
        return rect.intersectsLine((int) x1, (int) y1, (int) x2, (int) y2);
    }

    /**
     * Returns the x-coordinate of the first endpoint of the line segment.
     *
     * @return the x-coordinate of the first endpoint.
     */
    public double getX1()
    {
        return x1;
    }

    /**
     * Returns the y-coordinate of the first endpoint of the line segment.
     *
     * @return the y-coordinate of the first endpoint.
     */
    public double getY1()
    {
        return y1;
    }

    /**
     * Returns the x-coordinate of the second endpoint of the line segment.
     *
     * @return the x-coordinate of the second endpoint.
     */
    public double getX2()
    {
        return x2;
    }

    /**
     * Returns the y-coordinate of the second endpoint of the line segment.
     *
     * @return the y-coordinate of the second endpoint.
     */
    public double getY2()
    {
        return y2;
    }
}
