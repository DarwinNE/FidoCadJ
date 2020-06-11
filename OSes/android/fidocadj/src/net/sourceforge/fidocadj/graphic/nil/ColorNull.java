package net.sourceforge.fidocadj.graphic.nil;

import net.sourceforge.fidocadj.graphic.*;


/**     ANDROID VERSION

    Null color class. Does nothing :-)


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
public class ColorNull implements ColorInterface
{
    /** Standard constructor
    */
    public ColorNull()
    {
        // Does nothing.
    }

    /** Get a white color.
        @return a white color.
    */
    public ColorInterface white()
    {
        return new ColorNull();
    }

    /** Get a gray color.
        @return a gray color.
    */
    public ColorInterface gray()
    {
        return new ColorNull();
    }

    /** Get a green color.
        @return a green color.
    */
    public ColorInterface green()
    {
        return new ColorNull();
    }

    /** Get a red color.
        @return a red color.
    */
    public ColorInterface red()
    {
        return new ColorNull();
    }

    /** Get the green component of the color.
        @return the component.
    */
    public ColorInterface black()
    {
        return new ColorNull();
    }

    /** Get the red component of the color.
        @return the component.
    */
    public int getRed()
    {
        return 0;
    }

    /** Get the green component of the color.
        @return the component.
    */
    public int getGreen()
    {
        return 0;
    }

    /** Get the blue component of the color.
        @return the component.
    */
    public int getBlue()
    {
        return 0;
    }

    /** Get the RGB components packed as an integer.
        @return an integer containing the RGB components.
    */
    public int getRGB()
    {
        return 0;
    }

    /** Set the color from a RGB description packed in a int.
        @param rgb the packed description..
    */
    public void setRGB(int rgb)
    {
        // Does nothing.
    }
}
