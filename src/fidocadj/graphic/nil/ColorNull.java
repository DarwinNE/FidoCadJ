package fidocadj.graphic.nil;

import fidocadj.graphic.ColorInterface;


/**         SWING VERSION

    Null color class. Does nothing :-)
    Class like this one are useful when calculating the size of the drawings.

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

public class ColorNull implements ColorInterface
{

    /** Does nothing.
        @return a new ColorNull instance.
    */
    public ColorInterface white()
    {
        return new ColorNull();
    }

    /** Does nothing.
        @return a new ColorNull instance.
    */
    public ColorInterface gray()
    {
        return new ColorNull();
    }

    /** Does nothing.
        @return a new ColorNull instance.
    */
    public ColorInterface green()
    {
        return new ColorNull();
    }

    /** Does nothing.
        @return a new ColorNull instance.
    */
    public ColorInterface red()
    {
        return new ColorNull();
    }

    /** Does nothing.
        @return a new ColorNull instance.
    */
    public ColorInterface black()
    {
        return new ColorNull();
    }

    /** Does nothing.
        @return 0.
    */
    public int getRed()
    {
        return 0;
    }

    /** Does nothing.
        @return 0.
    */
    public int getGreen()
    {
        return 0;
    }

    /** Does nothing.
        @return 0.
    */
    public int getBlue()
    {
        return 0;
    }

    /** Does nothing.
        @return 0.
    */
    public int getRGB()
    {
        return 0;
    }

    /** Does nothing.
        @param rgb not used.
    */
    public void setRGB(int rgb)
    {
        // Does nothing.
    }
}
