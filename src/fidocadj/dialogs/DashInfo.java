package fidocadj.dialogs;

/** This class contains some settings about the actual dashing style. It is used
    in the automatic primitive characteristics dialog.

    @author Davide Bucci

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

    Copyright 2009-2023 by Davide Bucci
    </pre>

*/
public class DashInfo
{
    // Here we store the dash style
    public int style;

    /** Creator.
        @param i the style to be stored
    */
    public DashInfo(int i)
    { style=i; }

    /** Retrieve the style.
        @return the style.
    */
    public int getStyle()
    { return style; }
}
