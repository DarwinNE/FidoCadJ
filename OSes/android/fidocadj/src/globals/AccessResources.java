package globals;

import android.content.Context;
import java.util.*;
import java.io.*;

/** 		ANDROID VERSION

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
    along with FidoCadJ.  If not, see <http://www.gnu.org/licenses/>.

	Copyright 2014 by Davide Bucci

</pre>
*/



public class AccessResources
{
	// message bundle
    private ResourceBundle messages;
    Context cc;
    
    public AccessResources(Context c)
    {
    	messages = null;
    	cc=c;
    }
    
   /* public AccessResources(ResourceBundle m)
    {
    	messages = m;
    }*/
    
    public String getString(String s)
    {
    	String packageName = cc.getPackageName();
    	int resId = cc.getResources()
            .getIdentifier(s, "string", packageName);
    	if (resId == 0) {
        	return "ID Not found";
    	} else {
        	return cc.getString(resId);
    	}
    }
}