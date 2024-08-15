package fidocadj.geom;

/**
    ChangeCoordinatesListener interface

    @author Davide Bucci
    @version 1.0, June 2008

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

    Copyright 2008 by Davide Bucci
</pre>

*/

public interface ChangeCoordinatesListener
{
    /** Callback when the coordinates are changed.
        @param x the x coordinate of the mouse pointer
        @param y the y coordinate of the mouse pointer
    */
    void changeCoordinates(int x, int y);

    /** Callback useful when some infos are to be shown.
        @param s the text to be shown (usually coordinates of the cursor).
    */
    void changeInfos(String s);
}