package fidocadj.graphic.swing;

import java.awt.Color;
import fidocadj.graphic.ColorInterface;


/** This class maps the general interface to java.awt.Color.
    We need this because there is no unified color description between
    Swing/PC versions of Java and Android's ones (GRRRRR!!!!).

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

public class ColorSwing implements ColorInterface
{
    Color c;

    /** Standard constructor.
        The default color is black.
    */
    public ColorSwing()
    {
        c= Color.black;
    }

    /** Standard constructor.
        @param c the Swing color to be employed.
    */
    public ColorSwing(Color c)
    {
        this.c=c;
    }

    /** Get the Swing color.
        @return the Swing color.
    */
    public Color getColorSwing()
    {
        return c;
    }

    /** Get a white color.
        @return a white color.
    */
    public ColorInterface white()
    {

        return new ColorSwing(Color.white);
    }

    /** Get a gray color.
        @return a gray color.
    */
    public ColorInterface gray()
    {
        return new ColorSwing(Color.gray);
    }

    /** Get a green color.
        @return a green color.
    */
    public ColorInterface green()
    {
        return new ColorSwing(Color.green);
    }

    /** Get a red color.
        @return a red color.
    */
    public ColorInterface red()
    {
        return new ColorSwing(Color.red);
    }

    /** Get a black color.
        @return a black color.
    */
    public ColorInterface black()
    {
        return new ColorSwing(Color.black);
    }

    /** Get the red component.
        @return the component.
    */
    public int getRed()
    {
        return c.getRed();
    }

    /** Get the green component.
        @return the component.
    */
    public int getGreen()
    {
        return c.getGreen();
    }

    /** Get the blue component.
        @return the component.
    */
    public int getBlue()
    {
        return c.getBlue();
    }

    /** Get the RGB description of the color.
        @return the description, packed in an int.
    */
    public int getRGB()
    {
        return c.getRGB();
    }

     /** Set the color from a RGB description packed in a int.
        @param rgb the packed description..
    */
    public void setRGB(int rgb)
    {
        c=new Color(rgb);
    }
}
