package fidocadj.circuit.model;

import fidocadj.primitives.GraphicPrimitive;

/** Provides a general way to apply an action to a graphic element.

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

public interface ProcessElementsInterface
{
    /** Process the given graphic primitive and execute a generic action on it.
        @param g the graphic primitive to be processed.
    */
    void doAction(GraphicPrimitive g);
}