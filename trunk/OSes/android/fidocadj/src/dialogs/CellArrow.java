package dialogs;

import graphic.android.ColorAndroid;
import graphic.android.GraphicsAndroid;
import primitives.Arrow;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

public class CellArrow extends View 
{
	private ArrowInfo arrow;
	
	public CellArrow(Context context) {
		super(context);

	}
	
	public void setStyle(ArrowInfo arrow)
	{
		this.arrow = arrow;
	}
	
	@Override
    protected void onDraw(Canvas canvas)
    {
		canvas.drawColor(Color.WHITE);
		GraphicsAndroid g = new GraphicsAndroid(canvas);
		
		ColorAndroid c = new ColorAndroid();
		g.setColor(c.black());
		
		int mult=(int)Math.floor(g.getScreenDensity()/112);
		if (mult<1) mult=1;
		
		g.drawLine(getWidth()/3, getHeight()/2,2*getWidth()/3, getHeight()/2);
		Arrow.drawArrow(g, getWidth()/3, getHeight()/2,
				2*getWidth()/3, getHeight()/2, mult*10, mult*4,
				 arrow.style);

    }
}

