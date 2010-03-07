package dialogs;

import primitives.*;

/** This class contains information about the arrow style. It is useful 
    for the automatic generation of the properties dialog.


    @author Davide Bucci
    @version 1.0 November 2009
    
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

    Copyright 2009 by Davide Bucci
    
*/
public class ArrowInfo 
{
    public int style;
    
    public ArrowInfo(int i)
    { style=i; }
    
    public int getStyle()
    { return style; }
}
