package fidocadj.toolbars;
/** Interface used to callback notify that the current grid or snap state has
    changed

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

    Copyright 2008-2023 by Davide Bucci
    </pre>

    @author Davide Bucci
*/

public interface ChangeGridState
{
    /** The callback method which is called when the current grid visibility
        has changed.
        @param v is the wanted grid visibility state
    */
    void setGridVisibility(boolean v);

    /** The callback method which is called when the current snap visibility
        has changed.
        @param v is the wanted snap state
    */
    void setSnapState(boolean v);
}