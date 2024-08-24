package fidocadj.dialogs.controls;
/**
    The user should check the parameter type before using. The allowed
    parameter types are: (Integer|Double|String|Boolean|Point).


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
public class ParameterDescription
{
    public Object   parameter;    // the parameter to be passed
    public String   description;  // string describing the parameter
    public boolean isExtension;   // is this parameter a extension of FidoCAD?

    /** Creator.
    */
    public ParameterDescription ()
    {
        isExtension = false;
    }

    /** Obtain a text representation of the object, mainly for debug purposes.
        @return the representation of the object.
    */
    @Override public String toString()
    {
        String s;
        s="[ParameterDescription("+parameter+", "+description+"]";
        return s;
    }
}