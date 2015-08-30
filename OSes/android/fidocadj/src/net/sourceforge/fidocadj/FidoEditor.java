package net.sourceforge.fidocadj;

import java.util.Vector;
import java.util.Locale;
import java.util.MissingResourceException;
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
import net.sourceforge.fidocadj.circuit.model.*;
import net.sourceforge.fidocadj.primitives.*;
import net.sourceforge.fidocadj.geom.*;
import net.sourceforge.fidocadj.circuit.views.*;
import net.sourceforge.fidocadj.circuit.controllers.*;
import net.sourceforge.fidocadj.graphic.android.*;
import net.sourceforge.fidocadj.layers.*;
import net.sourceforge.fidocadj.dialogs.*;

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

    Copyright 2014-2015 by Davide Bucci, Dante Loi
</pre>
   The circuit panel will contain the whole drawing.

    @author Davide Bucci, Dante Loi
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
    private SelectionActions sa;
    
    	// ********** RULER **********
	
    private boolean ruler;	// Is it to be drawn?
    private int rulerStartX;
    private int rulerStartY;
    private int rulerEndX;
    private int rulerEndY;       
    
    	// ***** ZOOM AND PANNING *****
    private int mx;
    private int my;
    private int oldDist;
    private int newDist;
    private double origZoom;
    private int oldScrollX;
    private int oldScrollY;
    private int oldCenterX;
    private int oldCenterY;
    
    	// ********** EDITING *********
    	
    private RectF evidenceRect;
    private Context cc;
	final Handler handler = new Handler(); 
	
	private boolean showGrid;
	
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

	/** Adopt the standard layer description and color. The drawing model
		should be already set when calling initLayers.
	*/ 
	public void initLayers()
	{
		Vector<LayerDesc> layerDesc = 
			StandardLayers.createStandardLayers(cc);
        dm.setLayers(layerDesc);	
	}

	/** Initialize the view and prepare everything for the drawing.
	*/
    private void init()
    {
        this.setMeasuredDimension(this.desiredWidth, this.desiredHeight);
        dm = new DrawingModel();
        initLayers();

        dd = new Drawing(dm);
           
		pa = new ParserActions(dm);
		ua = new UndoActions(pa);
		sa = new SelectionActions(dm);
		ea = new EditorActions(dm, sa, ua);
		eea = new ContinuosMoveActions(dm, sa, ua, ea);
		haa = new HandleActions(dm, ea, sa, ua);
		cpa=new CopyPasteActions(dm, ea, sa, pa, ua, (FidoMain)cc);

		// Specify a reasonable tolerance so you can select objects and handles
		// with your finger.
		ea.setSelectionTolerance(20*
			getResources().getDisplayMetrics().densityDpi/112);
			
		cpa.setShiftCopyPaste(true);	
	
		eea.setActionSelected(ElementsEdtActions.SELECTION);
		eea.setPrimitivesParListener(this);
		
		showGrid = true;
		readInternalLibraries();
		
		
        //StringBuffer s=new StringBuffer(createTestPattern());	
		//pa.parseString(s);
		ua.saveUndoState();
		cs = new MapCoordinates();
		
		cs.setXMagnitude(3*getResources().getDisplayMetrics().densityDpi/112);
		cs.setYMagnitude(3*getResources().getDisplayMetrics().densityDpi/112);
		cs.setXGridStep(5);
		cs.setYGridStep(5);

		// Courier New is the standard on PC, but it is not available on 
		// Android. Here the system will find a substitute.
        dm.setTextFont("Courier New", 3, null);

		//turn off hw accelerator 
		setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }
    
    /** Read the internal libraries (specified as raw resources).
    */
    private void readInternalLibraries()
    {
    	
		BufferedReader stdLib;
		BufferedReader ihram;
		BufferedReader elettrotecnica;
		BufferedReader pcb;
		BufferedReader eylib;
		
    
    	// Libraries are available in Italian or in English
		
		String lang="";
		try {
			lang=Locale.getDefault().getISO3Language();
		} catch (MissingResourceException E) 
		{
			// Not a big issue. Show the English version of the libs.
			lang="";
		}
		
		if("ita".equals(lang)) {
			// Italian version (only for italian locale)
			stdLib = new BufferedReader(new InputStreamReader(cc.getResources()
				.openRawResource(R.raw.fcdstdlib)));
			ihram = new BufferedReader(new InputStreamReader(cc.getResources()
				.openRawResource(R.raw.ihram)));
			elettrotecnica=new BufferedReader(new InputStreamReader(
				cc.getResources().openRawResource(R.raw.elettrotecnica)));
			pcb = new BufferedReader(new InputStreamReader(cc.getResources()
				.openRawResource(R.raw.pcb)));
		} else {
			// English version
			stdLib = new BufferedReader(
				new InputStreamReader(cc.getResources().openRawResource(
				R.raw.fcdstdlib_en)));
			ihram = new BufferedReader(
				new InputStreamReader(cc.getResources().openRawResource(
				R.raw.ihram_en)));
			elettrotecnica = new BufferedReader(
				new InputStreamReader(cc.getResources().openRawResource(
				R.raw.elettrotecnica_en)));
			pcb = new BufferedReader(
				new InputStreamReader(cc.getResources().openRawResource(
				R.raw.pcb_en)));
		}
		eylib = new BufferedReader(
				new InputStreamReader(cc.getResources().openRawResource(
				R.raw.ey_libraries)));
		try {
			pa.readLibraryBufferedReader(stdLib, "");
			pa.readLibraryBufferedReader(pcb, "pcb");
			pa.readLibraryBufferedReader(ihram, "ihram");
			pa.readLibraryBufferedReader(elettrotecnica, "elettrotecnica");
			pa.readLibraryBufferedReader(eylib, "ey_libraries");
        } catch (IOException E) {
        
        }
    }
    
    public String getText()
    {
    	return pa.getText(true).toString();
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
    	@return the drawing model.
    */
    public DrawingModel getDrawingModel()
    {
    	return dm;
    }
    
    /** Set the DrawingModel object containing the drawing.
    */
    public void setDrawingModel(DrawingModel d)
    {
    	dm=d;
    }
    
    /** Get the current selection controller
   		@return the selection controller 
    */
    public SelectionActions getSelectionActions()
    {
    	return sa;
	}
    
	/** Draw the drawing on the given canvas.
		@param canvas the canvas where the drawing will be drawn.
	*/
    @Override
    protected void onDraw(Canvas canvas)
    {
    	canvas.drawARGB(255, 255, 255, 255);
    	GraphicsAndroid g = new GraphicsAndroid(canvas);
		
    	if(showGrid){  
        	g.drawGrid(cs, (int)getScrollX(), (int)getScrollY(), 
        		(int)(getScrollX()+getWidth()), 
        		(int)(getScrollY()+getHeight()));
        }
        
        // Show a sort of an arrow, to indicate that there is the library 
        // hidden on the right.
        
        float xs = getScrollX()+getWidth();
        float ys = getScrollY()+getHeight()/2.0f;
        float mult=g.getScreenDensity()/112.0f;
        
        Paint p= new Paint(Color.GRAY);
        p.setStrokeWidth(3.0f*mult);
        p.setAntiAlias(true);
        p.setStrokeCap(Cap.ROUND);
        p.setStrokeJoin(Join.ROUND); 
        
        canvas.drawLine(xs-12.0f*mult, ys, xs-2.0f*mult, ys-20.0f*mult,p);
        canvas.drawLine(xs-12.0f*mult, ys, xs-2.0f*mult, ys+20.0f*mult,p);

     	// Draw the objects in the database
		dd.draw(g, cs);
		// Draw the handles of all selected primitives.
        dd.drawSelectedHandles(g, cs);
        
        if (evidenceRect != null 
        	&& eea.actionSelected == ElementsEdtActions.SELECTION) 
        {
        	Paint rectPaint = new Paint();
        	rectPaint.setColor(Color.GREEN);
			rectPaint.setStyle(Style.STROKE);
			rectPaint.setStrokeWidth(mult*1.3f);
        	canvas.drawRect(evidenceRect, rectPaint);
        } else {
        	evidenceRect = null;
        }
        eea.showClicks(g, cs);
    }
    
    /** Handles scroll events.
    */
    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) 
    {
    	super.onScrollChanged(x, y, oldx, oldy);
        if(x < 0)	
        	scrollBy(-x,0);
        else if(y < 0)	
        	scrollBy(0,-y);
        else
        	scrollBy(0,0);
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
    	if(event.getPointerCount()<=0)
    		return false;
    	
    	int action = event.getAction() & MotionEvent.ACTION_MASK;
    	int curX, curY;
    	
    	/* Handle all actions related to selection */
    	if(eea.getSelectionState() == ElementsEdtActions.SELECTION) {
    		mx = (int) event.getX()+getScrollX();
    		my = (int) event.getY()+getScrollY();
			// Handle selection events.
	        switch (action) {
	        	case MotionEvent.ACTION_DOWN:
	        		haa.dragHandleStart(mx, my, ea.getSelectionTolerance(),
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
	    
	    /* Handle all actions related to move/zoom */ 	
	    if (eea.getSelectionState() == ElementsEdtActions.HAND) {
	    	//Handle Scrolling and zooming events
	        switch (action) {
	            case MotionEvent.ACTION_DOWN:
	                mx = (int)event.getX();
	                my = (int)event.getY();
	                if(event.getPointerCount() == 1) {
	                	oldDist=-1; 	// Distinguish 1-finger events (pan)
	                					// from 2-fingers ones (pinch, zoom).
	                }
	                break;
	            case MotionEvent.ACTION_POINTER_DOWN:
	            	if(event.getPointerCount() == 2) {	
	            		// Begin of a zoom gesture
	            		oldDist = spacing(event);
	            		oldCenterX=(int)(event.getX(0) + event.getX(1))/2;
	            		oldCenterY=(int)(event.getY(0) + event.getY(1))/2;
	            		oldScrollX=getScrollX();
	            		oldScrollY=getScrollY();
	            	} else {
	            		oldDist=-1; 	// This is just to be on the safe side
	            	}
	            	origZoom=cs.getXMagnitude();
	            	break;
	            case MotionEvent.ACTION_UP:
	            case MotionEvent.ACTION_CANCEL:
	            case MotionEvent.ACTION_MOVE:
	            	// Handle the zoom gesture.
	                if(event.getPointerCount() == 2 && oldDist>0) {
	            		newDist = spacing(event);
	            		
	            		double scale = (double)newDist / (double)oldDist;
	            		// The new zoom is calculated from the original one
	            		// saved in MotionEvent.ACTION_POINTER_DOWN.
						double newZoom=scale*origZoom;
	            		cs.setXMagnitude(newZoom);
	            		cs.setYMagnitude(newZoom);
	            		// There might be some roundup or limiting while
	            		// setting the new zoom. This ensures that a coherent
	            		// value for newZoom is used for what follows.
	            		newZoom=cs.getXMagnitude();
	            		
	            		int corrX=(int)((oldCenterX)
	            			/origZoom*newZoom)-oldCenterX;
	            		int corrY=(int)((oldCenterY)
	            			/origZoom*newZoom)-oldCenterY;
	            		setScrollX((int)(oldScrollX/origZoom*newZoom)+corrX);
	            		setScrollY((int)(oldScrollY/origZoom*newZoom)+corrY);
	            		invalidate();
	                } else if(oldDist<0) {	// Panning
                		curX = (int)event.getX();
	                	curY = (int)event.getY();
			            scrollBy((mx - curX), (my - curY));
		                mx = curX;
		                my = curY;
	                }
	                break;
	            default:
	            	break;
	        }
	    }
    	return true;		
    }
    
    /**
     *    @return the space between two points.
     */
    private int spacing(MotionEvent event)
    {
    	float x = event.getX(0) - event.getX(1);
    	float y = event.getY(0) - event.getY(1);
    	int dist = (int) Math.sqrt(x * x + y * y);
    	return dist;
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
	

    /** Shows a dialog which allows the user modify the parameters of a given
    	primitive. If more than one primitive is selected, modify only the
    	layer of all selected primitives.
    */
    public void setPropertiesForPrimitive()
    {    	
        GraphicPrimitive gp=sa.getFirstSelectedPrimitive();
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
        	false, dm.getLayers());
        dp.show( ((Activity)cc).getFragmentManager(), "");
	}
	
	/** This function is a callback which is used by DialogParameters to save
		the useful data.
	*/
	public void saveCharacteristics(Vector<ParameterDescription> v)
	{	
		//android.util.Log.e("FidoCadJ", "saveCharacteristics: "+v);
		GraphicPrimitive gp=sa.getFirstSelectedPrimitive();
		android.util.Log.e("FidoCadJ", "saveCharacteristics this= "+this);
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
        sa.setSelectionAll(false);
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
		// Not needed with Android.
	}
	
	/** Calculate an optimum zoom which fits to the document. Move the
		scroll bars accordingly.
	*/
	public void zoomToFit()
	{
		zoomOrPanToFit(true);
	}
	
	/** Pan the drawing so it is visible. Does not change the zoom
	*/
	public void panToFit()
	{
		zoomOrPanToFit(false);
	}
	
	private void zoomOrPanToFit(boolean scaleZoom)
	{
		// At first get the size in which the drawing should be fit
		int sizex=getWidth();
		int sizey=getHeight();
		// Calculate the zoom to fit scale
		MapCoordinates mp;
		mp = DrawingSize.calculateZoomToFit(dm, sizex, sizey, true);
		double z=mp.getXMagnitude();
		
		// Set the new coordinate system and force a redraw.
		if(scaleZoom) 
			getMapCoordinates().setMagnitudes(z, z);
				
		setScrollX((int)mp.getXCenter());
		setScrollY((int)mp.getYCenter());
		invalidate();	
	}
	
    /** Makes sure the object gets focus.
    */
	public void getFocus()
	{
		// Not needed with Android.
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
	
	/** Set the current coordinate mapping object.
    	@param c the current coordinate mapping object.
    */
    public void setMapCoordinates(MapCoordinates c)
	{
		cs=c;
	}
	
	/** Sets whether the grid showing the editing points should be shown or
		not.
		@param s true if the grid is to be drawn.
	*/
	public void setShowGrid(boolean s)
	{
		showGrid=s;
	}

	/** Gets if the editing grid has to be shown.
		@return true if the grid is drawn.
	*/
	public boolean getShowGrid()
	{
		return showGrid;
	}

	/** Gesture listener: useful to detect for long taps to show contextual
		menu.
	*/
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
        	
        	long start = System.currentTimeMillis();
        	haa.dragHandleStart(x, y, ea.getSelectionTolerance(),
            	false, cs);
            long millis=System.currentTimeMillis()-start;
            android.util.Log.e("fidocadj", "dragHandleStart done in "+millis+
            	" ms");
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
            		eea.handleClick(cs, x, y, longTap, false,doubleTap);    
            	}
        	} else {
        		android.util.Log.d("f", "long: "+longTap+" double: " 
            			+doubleTap);
            	eea.handleClick(cs, x, y, longTap, false,doubleTap); 
        	}
        	invalidate();
			return true;
		} 
	} 
}