package fr.davbucci.test;

import java.util.Vector;

import android.view.View;
import android.view.InputDevice;
import android.view.ViewGroup.LayoutParams;
import android.graphics.*;
import android.content.*;
import android.view.*;
import android.graphics.Paint.*;
import android.util.AttributeSet;
import android.widget.PopupMenu;
import android.os.Handler;
import android.widget.Toast;
import android.app.Activity;
import android.view.ContextMenu.*;


import circuit.model.*;
import primitives.*;
import geom.*;
import circuit.views.*;
import circuit.controllers.*;
import graphic.android.*;
import layers.*;

/** Android Editor view: draw the circuit inside this view. This is one of the
	most important classes, as it is responsible of all editing actions.
    
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
   The circuit panel will contain the whole drawing.
    This class is able to perform its profiling, which is in particular
    the measurement of the time needed to draw the circuit.
    
    @author Davide Bucci
*/

public class FidoEditor extends View implements PrimitivesParInterface
{
	GestureDetector gestureDetector;
	
	int desiredWidth = 1500;
    int desiredHeight = 800;
    
    private DrawingModel dm;
    private Drawing dd;
    private MapCoordinates cs;
    
    private ParserActions pa;
    private UndoActions ua;
    public EditorActions ea;
    public ContinuosMoveActions eea;
    private HandleActions haa;
    private CopyPasteActions cpa;
    
    	// ********** RULER **********
	
    private boolean ruler;	// Is it to be drawn?
    private int rulerStartX;
    private int rulerStartY;
    private int rulerEndX;
    private int rulerEndY;        
    
    	// ********** EDITING *********
    	
    private RectF evidenceRect;
    private Context cc;
	final Handler handler = new Handler(); 

	/** Public constructor.
	*/
    public FidoEditor(Context context, AttributeSet attrs) 
    {
        super(context, attrs);
        cc=context;
        init();
        evidenceRect = null;
        gestureDetector = new GestureDetector(context, new GestureListener());
        
    }

	/** Initialize the view and prepare everything for the drawing.
	*/
    private void init()
    {
        Vector<LayerDesc> layerDesc = StandardLayers.createStandardLayers();
        
        dm = new DrawingModel();
        dd = new Drawing(dm);
           
        dm.setLayers(layerDesc);
		pa = new ParserActions(dm);
        ParserActions pa = new ParserActions(dm);
		ua = new UndoActions(pa);
		ea = new EditorActions(dm, ua);
		eea = new ContinuosMoveActions(dm, ua, ea);
		haa = new HandleActions(dm, ea, ua);
		cpa=new CopyPasteActions(dm, ea, pa, ua, (test_start)cc);

		cpa.setShiftCopyPaste(true);	
	
		eea.setActionSelected(ElementsEdtActions.SELECTION);
		eea.setPrimitivesParListener(this);
        
        StringBuffer s=new StringBuffer(createTestPattern());	
		pa.parseString(s);
		cs = new MapCoordinates();
		
		cs.setXMagnitude(3);
		cs.setYMagnitude(3);
		cs.setXGridStep(5);
		cs.setYGridStep(5);
    }
    
    /** Get the EditorActions controller for the drawing.
    */
    public EditorActions getEditorActions()
    {
    	return ea;
    }

	/** Get the CopyPasteActions controller for the drawing.
    */
    public CopyPasteActions getCopyPasteActions()
    {
    	return cpa;
    }
    
	/** Draw the drawing on the given canvas.
		@param canvas the canvas where the drawing will be drawn.
	*/
    @Override
    protected void onDraw(Canvas canvas)
    {
    	canvas.drawARGB(255, 255, 255, 255);
     		
		GraphicsAndroid g = new GraphicsAndroid(canvas);
		dd.draw(g, cs);
		// Draw the handles of all selected primitives.
        dd.drawSelectedHandles(g, cs);
        
        if (evidenceRect != null 
        	&& eea.actionSelected == ElementsEdtActions.SELECTION) 
        {
        	Paint rectPaint = new Paint();
        	rectPaint.setColor(Color.GREEN);
			rectPaint.setStyle(Style.STROKE);
        	canvas.drawRect(evidenceRect, rectPaint);
        } else {
        	evidenceRect = null;
        }
        eea.showClicks(g, cs);
    }
    
    /** Reacts to a touch event. In reality, since we need to handle a variety
    	of different events, there is somehow a mix between the low level
    	onTouchEvent and the GestureListener (which does not handle slide and
    	move events).
    */
     @Override
    public boolean onTouchEvent(MotionEvent event)
    {
    	gestureDetector.onTouchEvent(event);
    	
    	int x=-1;
    	int y=-1;
    	boolean toRepaint=true;
    	
    	if(event.getPointerCount()>0) {
    		x = (int)event.getX(0);
    		y = (int)event.getY(0);
    	} else {
    		return super.onTouchEvent(event);
    	}
    	
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        int pointerIndex = (event.getAction() & 
        	MotionEvent.ACTION_POINTER_ID_MASK) >> 
        	MotionEvent.ACTION_POINTER_ID_SHIFT;
        int pointerId = event.getPointerId(pointerIndex);

		// Handle move events.
        switch (action) {
        	case MotionEvent.ACTION_UP:
        	case MotionEvent.ACTION_CANCEL:
        		if(Math.abs(x-rulerStartX)>10 || 
    			   Math.abs(y-rulerStartY)>10) {
            		haa.dragHandleEnd(this,x, y, false, cs);
            	}
        		break;
        	case MotionEvent.ACTION_MOVE:
            	int pointerCount = event.getPointerCount();
            	for (int i = 0; i < pointerCount; i++) {
            		x = (int)event.getX(i);
    				y = (int)event.getY(i);
    				if(Math.abs(x-rulerStartX)>10 || 
    				   Math.abs(y-rulerStartY)>10)

					haa.dragHandleDrag(this, x, y, cs);
            	}
            	invalidate();
           		break;
           	default:
           		break;
        }
    	return true;
    }
    
    /** Inform Android's operating system of the size of the view.
    */
    @Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
	{
    	int widthMode = MeasureSpec.getMode(widthMeasureSpec);
    	int widthSize = MeasureSpec.getSize(widthMeasureSpec);
    	int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    	int heightSize = MeasureSpec.getSize(heightMeasureSpec);

    	int width;
    	int height;

    	//Measure Width
    	if (widthMode == MeasureSpec.EXACTLY) {
        	//Must be this size
        	width = widthSize;
    	} else if (widthMode == MeasureSpec.AT_MOST) {
        	//Can't be bigger than...
        	width = Math.min(desiredWidth, widthSize);
    	} else {
        	//Be whatever you want
        	width = desiredWidth;
    	}

    	//Measure Height
    	if (heightMode == MeasureSpec.EXACTLY) {
        	//Must be this size
        	height = heightSize;
    	} else if (heightMode == MeasureSpec.AT_MOST) {
        	//Can't be bigger than...
        	height = Math.min(desiredHeight, heightSize);
    	} else {
        	//Be whatever you want
        	height = desiredHeight;
    	}

    	//MUST CALL THIS
    	setMeasuredDimension(width, height);
	}
	
	/** Create a test pattern, for debug purposes. It contains all the 
		primitives of FidoCadJ.
		@return a string containing the test pattern.  
	*/
	private String createTestPattern()
	{
		String s="[FIDOCAD]\n"+
			"FJC C 1.5\n"+
			"FJC A 0.35\n"+
			"FJC B 0.5\n"+
			"LI 5 50 90 50 0\n"+
			"FCJ 0 0 3 2 4 0\n"+
			"LI 5 45 90 45 0\n"+
			"FCJ 0 0 3 2 3 0\n"+
			"LI 5 40 90 40 0\n"+
			"FCJ 0 0 3 2 2 0\n"+
			"EP 5 5 15 15 0\n"+
			"EV 20 5 30 15 0\n"+
			"EV 50 5 60 15 0\n"+
			"FCJ 2 0\n"+
			"EV 65 5 75 15 0\n"+
			"FCJ 3 0\n"+
			"EV 80 5 90 15 0\n"+
			"FCJ 4 0\n"+
			"RP 5 20 15 30 0\n"+
			"RV 20 20 30 30 0\n"+
			"RV 50 20 60 30 0\n"+
			"FCJ 2 0\n"+
			"RV 65 20 75 30 0\n"+
			"FCJ 3 0\n"+
			"RV 80 20 90 30 0\n"+
			"FCJ 4 0\n"+
			"LI 100 5 185 5 0\n"+
			"FCJ 3 0 3 1 0 0\n"+
			"LI 100 15 185 15 0\n"+
			"FCJ 3 0 3 1 2 0\n"+
			"LI 100 20 185 20 0\n"+
			"FCJ 3 0 3 1 3 0\n"+
			"LI 100 25 185 25 0\n"+
			"FCJ 3 1 3 1 0 0\n"+
			"LI 100 35 185 35 0\n"+
			"FCJ 3 1 3 1 2 0\n"+
			"LI 100 40 185 40 0\n"+
			"FCJ 3 1 3 1 3 0\n"+
			"LI 100 45 185 45 0\n"+
			"FCJ 3 2 3 1 0 0\n"+
			"LI 100 55 185 55 0\n"+
			"FCJ 3 2 3 1 2 0\n"+
			"LI 100 60 185 60 0\n"+
			"FCJ 3 2 3 1 3 0\n"+
			"LI 100 65 185 65 0\n"+
			"FCJ 3 3 3 1 0 0\n"+
			"LI 100 75 185 75 0\n"+
			"FCJ 3 3 3 1 2 0\n"+
			"LI 100 80 185 80 0\n"+
			"FCJ 3 3 3 1 3 0\n"+
			"BE 15 80 10 55 50 55 35 85 0\n"+
			"BE 25 80 20 55 60 55 45 85 0\n"+
			"FCJ 0 0 0 0 2 0\n"+
			"BE 30 80 25 55 65 55 50 85 0\n"+
			"FCJ 0 0 0 0 3 0\n"+
			"BE 35 80 30 55 70 55 55 85 0\n"+
			"FCJ 0 0 0 0 4 0\n"+
			"SA 70 60 0\n"+
			"SA 70 65 0\n"+
			"SA 80 60 0\n"+
			"SA 80 65 0\n"+
			"SA 80 70 0\n"+
			"SA 70 70 0\n"+
			"PL 10 110 90 110 5 0\n"+
			"PL 10 120 90 120 6 0\n"+
			"PL 10 90 90 90 3 0\n"+
			"PL 10 100 90 100 4 0\n"+
			"RP 100 85 110 95 0\n"+
			"PA 15 135 10 10 2 0 0\n"+
			"PA 30 135 10 10 2 1 0\n"+
			"PA 45 135 10 10 2 2 0\n"+
			"BE 130 125 50 150 135 175 80 125 0\n"+
			"FCJ 1 0 3 1 0 0\n"+
			"BE 145 125 65 150 150 175 95 125 0\n"+
			"FCJ 2 0 3 1 0 0\n"+
			"PP 0 165 45 150 0 185 50 160 0\n"+
			"PV 40 165 85 150 40 185 90 160 0\n"+
			"PV 120 165 165 150 120 185 170 160 0\n"+
			"FCJ 2 0\n"+
			"PV 160 165 205 150 160 185 210 160 0\n"+
			"FCJ 3 0\n"+
			"PV 200 165 245 150 200 185 250 160 0\n"+
			"FCJ 4 0\n"+
			"TY 220 10 4 3 0 0 0 * Text\n"+
			"TY 220 10 4 3 0 4 0 * Text\n"+
			"TY 220 20 4 8 0 0 0 * Text\n"+
			"TY 220 20 4 8 0 4 0 * Text\n"+
			"TY 220 25 8 3 0 0 0 * Text\n"+
			"TY 220 25 8 3 0 4 0 * Text\n"+
			"TY 220 35 8 3 0 0 0 * Text\n"+
			"TY 225 40 8 3 20 0 0 * Text\n"+
			"TY 230 45 8 3 40 0 0 * Text\n"+
			"TY 235 50 8 3 60 0 0 * Text\n"+
			"TY 240 55 8 3 80 0 0 * Text\n"+
			"TY 245 60 8 3 90 0 0 * Text\n"+
			"TY 215 40 8 3 20 4 0 * Text\n"+
			"TY 210 45 8 3 40 4 0 * Text\n"+
			"TY 205 50 8 3 60 4 0 * Text\n"+
			"TY 200 55 8 3 80 4 0 * Text\n"+
			"TY 220 35 8 3 0 4 0 * Text\n"+
			"TY 195 60 8 3 90 4 0 * Text\n"+
			"EV 200 60 215 70 0\n"+
			"EV 225 60 240 70 0\n"+
			"EV 215 70 225 80 0\n"+
			"BE 205 80 215 90 225 90 235 80 0\n"+
			"BE 190 60 195 115 245 110 250 60 0\n"+
			"EP 205 65 210 70 0\n"+
			"EP 230 65 235 70 0\n"+
			"TY 230 130 4 3 0 4 0 * Text\n"+
			"TY 230 130 4 3 90 4 0 * Text\n"+
			"TY 230 130 4 3 180 4 0 * Text\n"+
			"TY 230 130 4 3 270 4 0 * Text\n"+
			"CV 0 170 130 195 135 200 110 240 115 245 140 215 145 195 100 0\n"+
			"CP 1 125 150 135 135 175 145 145 130 135 150 0\n"+
			"CV 0 70 80 90 80 100 105 115 125 105 105 0\n"+
			"FCJ 3 0 3 2 0 0\n"+
			"CV 0 240 5 260 25 240 30 0\n"+
			"FCJ 2 2 3 1 0 0\n"+
			"RP 105 90 115 100 1\n"+
			"RP 110 95 120 105 2\n"+
			"RP 115 100 125 110 3\n"+
			"RP 120 105 130 115 4\n"+
			"RP 125 110 135 120 5\n"+
			"RP 130 105 140 115 6\n"+
			"RP 135 100 145 110 7\n"+
			"RP 140 95 150 105 8\n"+
			"RP 145 90 155 100 9\n"+
			"RP 150 85 160 95 10\n"+
			"RP 155 90 165 100 11\n"+
			"RP 160 95 170 105 12\n"+
			"RP 165 100 175 110 13\n"+
			"RP 170 105 180 115 14\n"+
			"RP 175 110 185 120 15\n";
	return s;
	}

	/** Implementation of the PrimitivesParInterface interface.
	*/
	public void selectAndSetProperties(int x,int y){}
	
	/** Implementation of the PrimitivesParInterface interface.
	*/
	public void setPropertiesForPrimitive(){}
	
	/** Implementation of the PrimitivesParInterface interface.
		Show the popup menu. In Android, the menu can be centered inside the
		current view.
	*/
	public void showPopUpMenu(int x, int y)
	{
		((Activity) cc).registerForContextMenu(this); 
    	((Activity) cc).openContextMenu(this);
    	((Activity) cc).unregisterForContextMenu(this);
    }
    
	
	
	/** Increases or decreases the zoom by a step of 33%
    	@param increase if true, increase the zoom, if false decrease
    	@param x coordinate to which center the viewport (screen coordinates)
    	@param y coordinate to which center the viewport (screen coordinates)
    */
	public void changeZoomByStep(boolean increase, int x, int y){}
	
	
    /** Makes sure the object gets focus.
    */
	public void getFocus()
	{
	}
	
	/** Forces a repaint event.
	*/
	public void forcesRepaint()
	{
		invalidate();
	}
	
   	/** Forces a repaint, specify the region to be updated.
   	*/
   	public void forcesRepaint(int a, int b, int c, int d)
   	{
   		invalidate();
   	}
	
	/** Activate and sets an evidence rectangle which will be put on screen
        at the next redraw. All sizes are given in pixel.
        
        @param x   the x coordinate of the left top corner
        @param y   the y coordinate of the left top corner
        @param w    the width of the rectangle
        @param h    the height of the rectangle
    */
    public void setEvidenceRect(int x, int y, int w, int h)
    {
    	evidenceRect = new RectF(x, y, x+w, y+h); 
    }
    
    /** Get the current coordinate mapping object.
    	@return the current coordinate mapping object.
    */
    public MapCoordinates getMapCoordinates()
	{
		return cs;
	}

	private class GestureListener extends 
		GestureDetector.SimpleOnGestureListener 
	{

		@Override
		public boolean onDown(MotionEvent event) 
		{
			int x, y;
			if(event.getPointerCount()>0) {
    			x = (int)event.getX(0);
    			y = (int)event.getY(0);
    		} else {
    			return false;
    		}
    		ruler=false;
			rulerStartX = x;
        	rulerStartY = y;
        	rulerEndX=x;
        	rulerEndY=y;
        	haa.dragHandleStart(x, y, EditorActions.SEL_TOLERANCE,
            	false, cs);
            invalidate();
            return true;
		}
	
		@Override
		public boolean onSingleTapUp(MotionEvent event) 
		{
			return TapUpHandler(event, false, false);
		}
		
		@Override
		public void onLongPress(MotionEvent event) 
		{
			TapUpHandler(event, true, false);
		}
		
		@Override
		public boolean onDoubleTap(MotionEvent event) {
    		return TapUpHandler(event, false, true);

		}
		
		private boolean TapUpHandler(MotionEvent event, boolean longTap,
			boolean doubleTap)
		{
			int x, y;
			if(event.getPointerCount()>0) {
    			x = (int)event.getX(0);
    			y = (int)event.getY(0);
    		} else {
    			return false;
    		}		
    		// If we are in the selection state, either we are ending the
   			// editing
        	// of an element (and thus the dragging of a handle) or we are 
        	// making a click.
        
        	if(eea.actionSelected==ElementsEdtActions.SELECTION) {
        		android.util.Log.e("f", "x="+x+"  rulerStartX="+rulerStartX);
        		if(Math.abs(x-rulerStartX)>10 || 
    			   Math.abs(y-rulerStartY)>10) {
            		haa.dragHandleEnd(FidoEditor.this,x, y, false, cs);
            	} else {
            		android.util.Log.d("f", "long: "+longTap+" double: " 
            			+doubleTap);
            		eea.handleClick(cs, x, y, longTap, false,doubleTap,false);    
            	}
        	} else {
        		android.util.Log.d("f", "long: "+longTap+" double: " 
            			+doubleTap);
            	eea.handleClick(cs, x, y, longTap, false,doubleTap,false); 
        	}
        	invalidate();
			return true;
		} 
	} 
}