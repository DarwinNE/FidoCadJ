/* dialogEditLayer.java v.1.0

   Edit the current layer color, visibility and name

   ****************************************************************************
   Version History 

Version   Date           Author       Remarks
------------------------------------------------------------------------------
1.0     March 2007			D. Bucci     First working version
1.1		December 2007		D. Bucci
1.2		January 2008		D. Bucci	 Internationalized



   Written by Davide Bucci, Dec. 2007-Jan. 2008, davbucci at tiscali dot it
   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

/** Profiling class */

public class MyTimer {
        private final long start;
    
        public MyTimer() {
            start = System.currentTimeMillis();
        }
    
        public long getElapsed() {
            return System.currentTimeMillis() - start;
        }
    }
