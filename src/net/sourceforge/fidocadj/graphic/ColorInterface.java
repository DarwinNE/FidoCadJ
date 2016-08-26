package net.sourceforge.fidocadj.graphic;

/** Provides a general way to access colors.
<P>
    D.B.: WHY THE HELL THERE ARE TWO DIFFERENT CLASSES java.awt.Color AND
    android.graphics.Color. TWO DIFFERENT INCOMPATIBLE THINGS TO DO
    EXACTLY THE SAME STUFF.
    SHAME SHAME SHAME!
</P>

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

public interface ColorInterface
{
    /** Get a white color.
        @return a white color.
    */
    public ColorInterface white();

    /** Get a gray color.
        @return a gray color.
    */
    public ColorInterface gray();

    /** Get a green color.
        @return a green color.
    */
    public ColorInterface green();

    /** Get a red color.
        @return a red color.
    */
    public ColorInterface red();

    /** Get a black color.
        @return a black color.
    */
    public ColorInterface black();

    /** Get the green component of the color.
        @return the component.
    */
    public int getGreen();

    /** Get the red component of the color.
        @return the component.
    */
    public int getRed();

    /** Get the blue component of the color.
        @return the component.
    */
    public int getBlue();

    /** Get the RGB description of the color.
        @return the description, packed in an int.
    */
    public int getRGB();

    /** Set the color from a RGB description packed in a int.
        @param rgb the packed description..
    */
    public void setRGB(int rgb);
}
