package net.sourceforge.fidocadj.timer;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

/** MyTimer.java v.1.1

    Profiling class.

   ****************************************************************************
   Version History

Version   Date           Author       Remarks
------------------------------------------------------------------------------
1.0     March 2007          D. Bucci     First working version
1.1     December 2007       D. Bucci



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

    Copyright 2007 by Davide Bucci
</pre>


*/

public class MyTimer
{
    private final long start;

    /** Standard constructor. Time measurement begins from here.

    */
    public MyTimer()
    {
        start = System.currentTimeMillis();
    }

    /** Get the elapsed time from class construction.
        @return the elapsed time in milliseconds. Time resolution will
        depend on your operating system.
    */
    public long getElapsed()
    {
        return System.currentTimeMillis() - start;
    }
}