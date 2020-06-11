package net.sourceforge.fidocadj;

import android.support.v4.widget.DrawerLayout;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;

/** CustomDrawerLayout is a DrawerLayout which is slightly modified so that
    its measurement are exactly what we need.

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
public class CustomDrawerLayout extends DrawerLayout
{

    /** Creator.
        @param context the current context.
    */
    public CustomDrawerLayout(Context context)
    {
        super(context);
    }

    /** Creator.
        @param context the current context.
        @param attrs attributes for the layout.
    */
    public CustomDrawerLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    /** Creator.
        @param context the current context.
        @param attrs attributes for the layout.
        @param defStyle style.
    */
    public CustomDrawerLayout(Context context, AttributeSet attrs,
        int defStyle)
    {
        super(context, attrs, defStyle);
    }

    /** Called when  a measurement operation is done.
        @param widthMeasureSpec the wanted with.
        @param heightMeasureSpec the wanted height.
    */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}