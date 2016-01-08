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

    public ColorAndroid()
    {
        // Does nothing.
    }

    public ColorAndroid(int c)
    {
        this.c=c;
    }

    public int getColorAndroid()
    {
        return c;
    }

    public ColorInterface white()
    {
        return new ColorAndroid(Color.WHITE);
    }

    public ColorInterface gray()
    {
        return new ColorAndroid(Color.GRAY);
    }

    public ColorInterface green()
    {
        return new ColorAndroid(Color.GREEN);
    }

    public ColorInterface red()
    {
        return new ColorAndroid(Color.RED);
    }

    public ColorInterface black()
    {
        return new ColorAndroid(Color.BLACK);
    }

    public int getRed()
    {
        return Color.red(c);
    }

    public int getGreen()
    {
        return Color.green(c);
    }

    public int getBlue()
    {
        return Color.blue(c);
    }

    public int getRGB()
    {
        return c;
    }
    public void setRGB(int rgb)
    {
        c=rgb;
    }
}
