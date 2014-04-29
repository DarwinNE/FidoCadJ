package net.sourceforge.fidocadj;

import java.util.Vector;
import java.io.*;

import android.view.MotionEvent;
import android.view.View;
import android.graphics.*;
import android.content.*;
import android.view.*;
import android.graphics.Paint.*;
import android.util.AttributeSet;
import android.os.Handler;
import android.app.Activity;
import circuit.model.*;
import primitives.*;
import geom.*;
import circuit.views.*;
import circuit.controllers.*;
import graphic.PointG;
import graphic.android.*;
import layers.*;
import dialogs.*;

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
    
    private int mx;
    private int my;
    
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
		cpa=new CopyPasteActions(dm, ea, pa, ua, (FidoMain)cc);

		cpa.setShiftCopyPaste(true);	
	
		eea.setActionSelected(ElementsEdtActions.SELECTION);
		eea.setPrimitivesParListener(this);
		
		
		BufferedReader stdLib = new BufferedReader(
			new InputStreamReader(cc.getResources().openRawResource(
			R.raw.fcdstdlib_en)));
		BufferedReader ihram = new BufferedReader(
			new InputStreamReader(cc.getResources().openRawResource(
			R.raw.ihram_en)));
		BufferedReader elettrotecnica = new BufferedReader(
			new InputStreamReader(cc.getResources().openRawResource(
			R.raw.elettrotecnica_en)));
		BufferedReader pcb = new BufferedReader(
			new InputStreamReader(cc.getResources().openRawResource(
			R.raw.pcb_en)));
		try {
			pa.readLibraryBufferedReader(stdLib, "");
			pa.readLibraryBufferedReader(pcb, "pcb");
			pa.readLibraryBufferedReader(ihram, "ihram");
			pa.readLibraryBufferedReader(elettrotecnica, "elettrotecnica");
        } catch (IOException E) {
        
        }
        StringBuffer s=new StringBuffer(createTestPattern());	
		pa.parseString(s);
		ua.saveUndoState();
		cs = new MapCoordinates();
		
		cs.setXMagnitude(3);
		cs.setYMagnitude(3);
		cs.setXGridStep(5);
		cs.setYGridStep(5);
		
		// Courier New is the standard on PC, but it is not available on 
		// Android. Here the system will find a substitute.
        dm.setTextFont("Courier New", 3, null);
    }
    
    /** Gets the EditorActions controller for the drawing.
    */
    public EditorActions getEditorActions()
    {
    	return ea;
    }

	/** Gets the ContinuosMoveActions for the drawing
	*/
	public ContinuosMoveActions getContinuosMoveActions()
	{
		return eea;
	}

	/** Get the CopyPasteActions controller for the drawing.
    */
    public CopyPasteActions getCopyPasteActions()
    {
    	return cpa;
    }
    
    /** Get the UndoActions controller for the drawing.
    */
    public UndoActions getUndoActions()
    {
    	return ua;
    }
    
    /** Get the ParserActions controller for the drawing.
    */
    public ParserActions getParserActions()
    {
    	return pa;
    }
    
    /** Get the DrawingModel object containing the drawing.
    */
    public DrawingModel getDrawingModel()
    {
    	return dm;
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
    	 
    	int action = event.getAction() & MotionEvent.ACTION_MASK;
    	int curX, curY;
	    int oldDist = 1, newDist = 1;
    	int height = getMeasuredHeight();
    	int width =getMeasuredWidth();
    	
    	if( eea.getSelectionState() == ElementsEdtActions.SELECTION ) 
		{
    		mx = (int) event.getX()+getScrollX();
    		my = (int) event.getY()+getScrollY();
			// Handle selection events.
	        switch (action) {
	        	case MotionEvent.ACTION_DOWN:
	        		haa.dragHandleStart(mx, my, EditorActions.SEL_TOLERANCE,
	        				false, cs);
	        		break;
	        	case MotionEvent.ACTION_MOVE:							
	                haa.dragHandleDrag(this, mx, my, cs);
	           		break;
	        	case MotionEvent.ACTION_UP:
	        	case MotionEvent.ACTION_CANCEL:
	            	haa.dragHandleEnd(this, mx, my, false, cs);     	
	        		break;
	           	default:
	           		break;
	        }
	        invalidate();
	    }
    	
	    if (eea.getSelectionState() == ElementsEdtActions.HAND )
	    {
	    	//Handle Scrolling events
	        switch (action) {
	            case MotionEvent.ACTION_DOWN:
	                mx = (int)event.getX();
	                my = (int)event.getY();
	                break;
	            case MotionEvent.ACTION_MOVE:
	                curX = (int)event.getX();
	                curY = (int)event.getY();
	                scrollBy((mx - curX), (my - curY));
	                mx = curX;
	                my = curY;
	                break;
	            case MotionEvent.ACTION_UP: 
	            	int deltaX = getScrollX();
	            	int deltaY = getScrollY();
	                if(!(deltaX <= width && deltaY <= height
	                		&& deltaX >= 0 && deltaY >= 0))
	                	scrollTo(0, 0);  
	                break;
	            default:
	            	break;
	        }
	    }
	    //stupid zoom
	    if (eea.getSelectionState() == ElementsEdtActions.ZOOM )
	    {
	    	//Handle zoom events
	        switch (action) {
	            case MotionEvent.ACTION_DOWN:
	            	mx = (int) event.getX()+getScrollX();
	        		my = (int) event.getY()+getScrollY();
	            	setPivotX(mx);
	            	setPivotY(my);
	            	setScaleX(2);
	            	setScaleY(2);
	                break;
	            case MotionEvent.ACTION_UP: 
	            	setPivotX(mx);
	            	setPivotY(my);
	            	setScaleX(1);
	            	setScaleY(1);
	            default:
	            	break;
	        }
	    }
	    //multi touch zoom solution
	 /*   if (eea.getSelectionState() == ElementsEdtActions.ZOOM )
	    {
	    	//Handle zoom events
	        switch (action) {
	            case MotionEvent.ACTION_POINTER_DOWN:
	            	oldDist = spacing(event);
	                break;
	            case MotionEvent.ACTION_MOVE:
	            	newDist = spacing(event);
	            	PointG pivot = midPoint(event); 
	            	setPivotX(pivot.x);
	            	setPivotY(pivot.y);
	            	int scale = newDist / oldDist;
	            	setScaleX(scale);
	            	setScaleY(scale);
	                break;
	            default:
	            	break;
	        }
	    }*/
    	return true;		
    }
    
   
    /**
     *    @return the space between two points.
     */
    private int spacing(MotionEvent event)
    {
    	float x = event.getX(0) - event.getX(1) + 2*getScrollX();
    	float y = event.getY(0) - event.getY(1) + 2*getScrollY();
    	return (int) Math.sqrt(x * x + y * y);
    }
    /**
     *    @return the middle point in the line passing by two points.
     */
    private PointG midPoint(MotionEvent event) 
    {
    	PointG point = new PointG();
    	float x = event.getX(0) - event.getX(1) + 2*getScrollX();
    	float y = event.getY(0) - event.getY(1) + 2*getScrollY();
    	point.x = (int) x / 2;
    	point.y = (int) y / 2;
    	return point;
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
			"RP 175 110 185 120 15\n"+
"MC 55 195 0 0 170\n"+
"FCJ\n"+
"TY 55 185 4 3 0 0 0 Helvetica C\n"+
"TY 65 205 4 3 0 0 0 Helvetica \n"+
"LI 65 195 75 195 0\n"+
"LI 75 195 75 210 0\n"+
"LI 55 195 45 195 0\n"+
"LI 45 195 45 210 0\n"+
"SA 45 210 0\n"+
"SA 75 210 0\n"+
"LI 50 230 20 230 0\n"+
"LI 50 240 40 240 0\n"+
"LI 40 240 40 250 0\n"+
"SA 20 230 0\n"+
"LI 65 210 90 210 0\n"+
"LI 90 210 90 235 0\n"+
"LI 75 235 100 235 0\n"+
"SA 90 235 0\n"+
"MC 50 240 2 1 580\n"+
"FCJ\n"+
"TY 65 245 4 3 0 0 0 Helvetica U\n"+
"TY 60 235 4 3 0 0 0 Helvetica \n"+
"MC 20 250 3 0 240\n"+
"FCJ\n"+
"TY 25 240 4 3 0 0 0 Helvetica D\n"+
"TY 30 260 4 3 0 0 0 Helvetica \n"+
"MC 55 210 0 0 080\n"+
"FCJ\n"+
"TY 65 215 4 3 0 0 0 Helvetica R\n"+
"TY 65 220 4 3 0 0 0 Helvetica \n"+
"LI 55 210 20 210 0\n"+
"LI 20 210 20 235 0\n"+
"MC 20 250 0 0 045\n"+
"MC 40 250 0 0 045\n"+
"MC 145 215 0 0 elettrotecnica.mam\n"+
"MC 180 215 0 0 elettrotecnica.matscsaar\n"+
"MC 210 245 0 0 ihram.lcd44780\n"+
"TY 85 195 4 3 0 0 0 Helvetica Stdlib\n"+
"TY 235 195 4 3 0 0 0 Helvetica IHRAM\n"+
"TY 150 195 4 3 0 0 0 Helvetica Elettrotecnica\n"+
"TY 265 110 4 3 0 0 0 Helvetica PCB\n"+
"MC 285 130 0 0 pcb.to92-45";




	return s;
	}

    /** Shows a dialog which allows the user modify the parameters of a given
    	primitive. If more than one primitive is selected, modify only the
    	layer of all selected primitives.
    */
    public void setPropertiesForPrimitive()
    {    	
        GraphicPrimitive gp=ea.getFirstSelectedPrimitive();
        if (gp==null) 
        	return;
        	
        Vector<ParameterDescription> v;
        if (ea.isUniquePrimitiveSelected()) {
           	v=gp.getControls();
        } else {
          	// If more than a primitive is selected, 
           	v=new Vector<ParameterDescription>(1);
           	ParameterDescription pd = new ParameterDescription();
			pd.parameter=new LayerInfo(gp.getLayer());
			pd.description="TODO: add a reference to a resource";
			v.add(pd);
        }
        
        DialogParameters dp = DialogParameters.newInstance(v, 
        	false, dm.getLayers(), this);
        dp.show( ((Activity)cc).getFragmentManager(), "");
	}
	
	public void saveCharacteristics(Vector<ParameterDescription> v)
	{	
		GraphicPrimitive gp=ea.getFirstSelectedPrimitive();
       	if (ea.isUniquePrimitiveSelected()) {
       	    gp.setControls(v);	
       	} else { 
       		ParameterDescription pd=(ParameterDescription)v.get(0);
       		if (pd.parameter instanceof LayerInfo) {
				int l=((LayerInfo)pd.parameter).getLayer();
				ea.setLayerForSelectedPrimitives(l);
			} else {
	 			android.util.Log.e("FidoCadJ",
	 				"Warning: unexpected parameter! (layer)");
	 		}
       	}
       	dm.setChanged(true);
                
        // We need to check and sort the layers, since the user can
        // change the layer associated to a given primitive thanks to
        // the dialog window which has been shown.
                
        dm.sortPrimitiveLayers();
        ua.saveUndoState();
        invalidate();
    }
    
    /** Selects the closest object to the given point (in logical coordinates)
    	and pops up a dialog for the editing of its Param_opt.
    	
    	@param x the x logical coordinate of the point used for the selection
    	@param y the y logical coordinate of the point used for the selection
    
    */
    public void selectAndSetProperties(int x, int y)
    {
        ea.setSelectionAll(false);
        ea.handleSelection(cs, x, y, false);
        invalidate();
        setPropertiesForPrimitive();
    }	
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
	public void changeZoomByStep(boolean increase, int x, int y)
	{
		
	}
	
	
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
    		if(eea.getSelectionState()!=ElementsEdtActions.SELECTION)
        		return false;
        		
			int x, y;
			if(event.getPointerCount()>0) {
    			x = (int)event.getX(0)+getScrollX();
    			y = (int)event.getY(0)+getScrollY();
    		} else {
    			return false;
    		}
    		ruler=false;
			rulerStartX = x;
        	rulerStartY = y;
        	rulerEndX = x;
        	rulerEndY = y;
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
    			x = (int)event.getX(0)+getScrollX();
    			y = (int)event.getY(0)+getScrollY();
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




