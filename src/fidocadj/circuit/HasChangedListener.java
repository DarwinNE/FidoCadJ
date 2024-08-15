package fidocadj.circuit;

/** Interface used to callback notify that something has changed
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

    </pre>
    @version 1.0
    @author Davide Bucci

    Copyright 2007-2023 by Davide Bucci
*/

public interface HasChangedListener
{
    /** Method to be called to notify that something has changed in the
        drawing.
    */
    void somethingHasChanged();

}