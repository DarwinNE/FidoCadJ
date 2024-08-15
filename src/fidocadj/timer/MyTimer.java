package fidocadj.timer;

/** MyTimer.java

    Profiling class.

   ****************************************************************************
   Version History
 <pre>
Version   Date                Author       Remarks
------------------------------------------------------------------------------
1.0       March 2007          D. Bucci     First working version
1.1       December 2007       D. Bucci


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

    Copyright 2007-2023 by Davide Bucci
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