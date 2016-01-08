package net.sourceforge.fidocadj.dialogs;

import net.sourceforge.fidocadj.graphic.android.ColorAndroid;
import net.sourceforge.fidocadj.graphic.android.GraphicsAndroid;
import net.sourceforge.fidocadj.primitives.Arrow;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

/** This class provides a view showing the different arrow styles which
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

    Copyright 2014-2015 by Dante Loi, Davide Bucci
</pre>

    */

public class CellArrow extends View
{
    private ArrowInfo arrow;

    /** Standard constructor.
    */
    public CellArrow(Context context)
    {
        super(context);

    }

    /** Select the style of the arrow to be used.
        @param arrow the wanted style.
    */
    public void setStyle(ArrowInfo arrow)
    {
        this.arrow = arrow;
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

        // Draw the arrow.
        g.applyStroke(2*mult, 0);
        g.drawLine(getWidth()/3, getHeight()/2,2*getWidth()/3, getHeight()/2);
        Arrow.drawArrow(g, getWidth()/3, getHeight()/2,
                2*getWidth()/3, getHeight()/2, mult*10, mult*4,
                 arrow.style);

    }
}

