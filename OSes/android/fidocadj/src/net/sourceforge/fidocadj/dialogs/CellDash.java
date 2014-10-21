package net.sourceforge.fidocadj.dialogs;

import globals.Globals;
import graphic.GraphicsInterface;
import graphic.android.ColorAndroid;
import graphic.android.GraphicsAndroid;
import primitives.Arrow;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

/** This class provides a view showing the different dash styles which
	might be used in a spinner list.
    
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

    Copyright 2014 by Dante Loi
</pre>
    
    */
    
public class CellDash extends View
{
	private DashInfo dash;
	
	/** Standard constructor.
	*/
	public CellDash(Context context) 
	{
		super(context);
	}
	
	/** Select the wanted dash style.
		@param dash_s the wanted style.
	*/
	public void setStyle(DashInfo dash_s)
	{
		this.dash = dash_s;
	}
	
	@Override
    protected void onDraw(Canvas canvas)
    {
    	// Background: white!
		canvas.drawColor(Color.WHITE);
		GraphicsAndroid g = new GraphicsAndroid(canvas);
		
		ColorAndroid c = new ColorAndroid();
		g.setColor(c.black());
		
		// Compute a reasonable size for the arrows, depending on the
		// screen resolution.
		int mult=(int)Math.floor(g.getScreenDensity()/112);
		if (mult<1) mult=1;
        
        // Draw a line.            
		g.applyStroke(2*mult, dash.getStyle());
        g.drawLine(getWidth()/3, getHeight()/2,2*getWidth()/3, getHeight()/2);
    }
}



