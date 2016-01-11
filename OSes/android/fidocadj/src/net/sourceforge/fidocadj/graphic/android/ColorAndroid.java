package net.sourceforge.fidocadj.graphic.android;

import android.graphics.*;

import net.sourceforge.fidocadj.graphic.*;

/** Android color class.


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

public class ColorAndroid implements ColorInterface
{
    int c;

    /** Standard constructor, empty.
    */
    public ColorAndroid()
    {
        // Does nothing.
    }

    /** Constructor which creates a color from an integer where the RGB bytes
        are packed.
        @param c the integer where the color code is packed.
    */
    public ColorAndroid(int c)
    {
        this.c=c;
    }

    /** Get the color, as an Android integer description.
        @return the color code as an integer packing the RGB bytes.
    */
    public int getColorAndroid()
    {
        return c;
    }

    /** The white color.
        @return the white color.
    */
    public ColorInterface white()
    {
        return new ColorAndroid(Color.WHITE);
    }

    /** The gray color.
        @return the gray color.
    */
    public ColorInterface gray()
    {
        return new ColorAndroid(Color.GRAY);
    }

    /** The green color.
        @return the green color.
    */
    public ColorInterface green()
    {
        return new ColorAndroid(Color.GREEN);
    }

    /** The red color.
        @return the red color.
    */
    public ColorInterface red()
    {
        return new ColorAndroid(Color.RED);
    }

    /** The black color.
        @return the black color.
    */
    public ColorInterface black()
    {
        return new ColorAndroid(Color.BLACK);
    }

    /** The red component.
        @return the red component.
    */
    public int getRed()
    {
        return Color.red(c);
    }

    /** The green component.
        @return the green component.
    */
    public int getGreen()
    {
        return Color.green(c);
    }

    /** The blue component.
        @return the blue component.
    */
    public int getBlue()
    {
        return Color.blue(c);
    }

    /** The RGB components packed in an integer.
        @return the RGB components.
    */
    public int getRGB()
    {
        return c;
    }

    /** Set the RGB components, packed in an integer.
        @param rgb the integer packing the RGB components.
    */
    public void setRGB(int rgb)
    {
        c=rgb;
    }
}
