package dialogs;

import globals.Globals;
import graphic.GraphicsInterface;
import graphic.android.ColorAndroid;
import graphic.android.GraphicsAndroid;
import primitives.Arrow;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

public class CellDash extends View
{
	private DashInfo dash;
	
	public CellDash(Context context) {
		super(context);

	}
	
	public void setStyle(DashInfo dashes)
	{
		this.dash = dashes;
	}
	
	@Override
    protected void onDraw(Canvas canvas)
    {
		canvas.drawColor(Color.WHITE);
		GraphicsAndroid g = new GraphicsAndroid(canvas);
		
		ColorAndroid c = new ColorAndroid();
		g.setColor(c.black());
		
                                         
		g.applyStroke(2, dash.getStyle());
        g.drawLine(getWidth()/3, getHeight()/2,2*getWidth()/3, getHeight()/2);


    }
}



