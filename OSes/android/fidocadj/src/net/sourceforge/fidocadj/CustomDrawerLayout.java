package net.sourceforge.fidocadj;

import android.support.v4.widget.DrawerLayout;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;

public class CustomDrawerLayout extends DrawerLayout 
{

    public CustomDrawerLayout(Context context) 
    {
        super(context);
    }

    public CustomDrawerLayout(Context context, AttributeSet attrs) 
	{
        super(context, attrs);
    }

    public CustomDrawerLayout(Context context, AttributeSet attrs, 
    	int defStyle) 
    {
        super(context, attrs, defStyle);
    }

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