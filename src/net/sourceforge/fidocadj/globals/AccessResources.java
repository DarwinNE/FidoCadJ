package net.sourceforge.fidocadj.globals;

import java.util.*;
import java.io.*;

/** 		SWING VERSION

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
    along with FidoCadJ.  If not, see http://www.gnu.org/licenses/

	Copyright 2014 by Davide Bucci

</pre>
*/

public class AccessResources
{
	// message bundle
    final private ResourceBundle messages;
    
    public AccessResources()
    {
    	messages = null;
    }
    
    public AccessResources(ResourceBundle m)
    {
    	messages = m;
    }
    
    public String getString(String s)
    {
    	return messages.getString(s);
    }
}