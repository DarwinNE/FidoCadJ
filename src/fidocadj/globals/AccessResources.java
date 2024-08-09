package fidocadj.globals;

import java.util.*;

/**         SWING VERSION

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
    along with FidoCadJ. If not,If not, see http://www.gnu.org/licenses/

    Copyright 2014-2023 by Davide Bucci

</pre>
*/

public class AccessResources
{
    // message bundle
    final private ResourceBundle messages;

    /** Standard creator. No resource bundle associated.
    */
    public AccessResources()
    {
        messages = null;
    }

    /** Standard creator.
        @param m the resource bundle to be associated with the object.
    */
    public AccessResources(ResourceBundle m)
    {
        messages = m;
    }

    /** Get the string associated to the given key in the resource bundle.
        @param s the key to retrieve the string resource.
        @return the message associated to the provided key.
    */
    public String getString(String s)
    {
        return messages.getString(s);
    }
}