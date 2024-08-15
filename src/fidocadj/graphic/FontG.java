package fidocadj.graphic;

/** FontG is a class containing font information.

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
public class FontG
{
    public String fontFamily;

    /** Standard constructor.
        @param n the name of the font family.
    */
    public FontG(String n)
    {
        fontFamily=n;
    }

    /** Get the font family.
        @return the font family.
    */
    public String getFamily()
    {
        return fontFamily;
    }
}