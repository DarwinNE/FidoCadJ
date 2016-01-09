package net.sourceforge.fidocadj.globals;

import android.content.Context;
import java.util.*;
import java.io.*;

/**         ANDROID VERSION
    This class is a container for the resource bundles employed by
    FidoCadJ. We need that since the code employing it must run on Swing
    as well as on Android and with Android we do not have the ResourceBundle
    class available.

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

    Copyright 2014-2016 by Davide Bucci

</pre>
*/
public class AccessResources
{
    // message bundle
    private ResourceBundle messages;
    Context cc;

    /** Creator, provide a context.
        @param c the context to be considered.
    */
    public AccessResources(Context c)
    {
        messages = null;
        cc=c;
    }

    /** Get the message associated to the provided key.
        @param s the key to be retrieved.
        @return the message associated to the key.
    */
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
