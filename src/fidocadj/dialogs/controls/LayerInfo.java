package fidocadj.dialogs.controls;

/**

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
    @author Davide Bucci
*/
public class LayerInfo
{
    int layer;

    /** Create a LayerInfo object with the given layer
    @param i the layer to be used.

    */
    public LayerInfo(int i)
    { layer=i; }

    public int getLayer()
    { return layer; }
}
