package geom;
import java.awt.*;
import java.util.*;

/** MapCoordinates.java

<pre>
    @author D. Bucci                               
	
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

	Copyright 2007-2012 by Davide Bucci
</pre>

    MapCoordinates performs the coordinate mapping between the logical units
    used in FidoCad schematics with the corrisponding pixel position
    to be used when drawing on the screen or on an image file. The class
    tracks also the minimum and maximum values of the pixel x/y coordinates, in
    order to obtain the drawing size. 
    
    The logical Fidocad resolution is 5 mils (127um). Thus, for the following 
    resolutions, we obtain:

<pre>    
    Resolution              units/pixel         x/y magnitude
    ---------------------------------------------------------
    72  pixels/inch         2.7778              0.36000
    150 pixels/inch         1.3333              0.75000
    300 pixels/inch         0.66666             1.50000
    600 pixels/inch         0.33333             3.00000
    1200 pixels/inch        0.16667             6.00000

</pre>

 	This class allows to concatenate a translation with the xCenter and yCenter
 	variables. This should NOT be used to scroll the actual drawing in the 
 	viewport, since this is done by using the JScrollPane Swing control.
 	This is indeed very useful when exporting or when drawing macros.


*/


public class MapCoordinates
{
	// Every member should be made private sooner or later...
    private double xCenter;
    private double yCenter;
    private double xMagnitude;
    private double yMagnitude;
    public int orientation;
    public boolean mirror;
    public boolean isMacro;
    public boolean snapActive;
    
    public static final double MIN_MAGNITUDE=0.25;
    public static final double MAX_MAGNITUDE=100.0;

    private double vx;
    private int ivx;
    private double vy;
	private int ivy;

    
    private int xMin;
    private int xMax;
    private int yMin;
    private int yMax;
    
    private int xGridStep;
    private int yGridStep;
    
    private Stack stack;
    
    
    /** Standard constructor */
    public MapCoordinates()
    {   
        xCenter=0.0;
        yCenter=0.0;
        xMagnitude=1.0;
        yMagnitude=1.0;
        orientation=0;
        xGridStep=5;
        yGridStep=5;
        isMacro=false;
        snapActive=true;
        resetMinMax();
        stack = new Stack();
    }
    
    public void push()
    {
    	MapCoordinates m = new MapCoordinates();
    	m.xCenter=xCenter;
   		m.yCenter=yCenter;
    	m.xMagnitude=xMagnitude;
    	m.yMagnitude=yMagnitude;
    	m.orientation=orientation;
    	m.mirror=mirror;
    	m.isMacro=isMacro;
    	m.snapActive=snapActive;
   		m.xMin=xMin;
    	m.xMax=xMax;
    	m.yMin=yMin;
    	m.yMax=yMax;
    
    	m.xGridStep=xGridStep;
    	m.yGridStep=yGridStep;
    	stack.push(m);
    }
    
    public void pop()
    {
    	if(!stack.empty()) {
    		MapCoordinates m=(MapCoordinates) stack.pop();
    		xCenter=m.xCenter;
   			yCenter=m.yCenter;
    		xMagnitude=m.xMagnitude;
    		yMagnitude=m.yMagnitude;
    		orientation=m.orientation;
    		mirror=m.mirror;
    		isMacro=m.isMacro;
    		snapActive=m.snapActive;
   			xMin=m.xMin;
    		xMax=m.xMax;
    		yMin=m.yMin;
    		yMax=m.yMax;
    
    		xGridStep=m.xGridStep;
    		yGridStep=m.yGridStep;
    	
    	} else {
    	 	System.out.println("Warning: I can not pop the coordinate state "+
    	 		"out of an empty stack!");
    	}
    }
    
    /** Set the snapping state (used in the unmapping functions)
    	@param s the wanted state.
    */
    public final void setSnap(boolean s)
    {
    	snapActive=s;
    }
    
    
    /** Get the snapping state (used in the unmapping functions)
    	@return the current snapping state.
    */
    public final boolean getSnap()
    {
    	return snapActive;
    }
    
    /**	Set the X grid step
    	@param xg the X grid step
    */
    public final void setXGridStep(int xg)
    {
    	if (xg>0) 
    		xGridStep=xg;

    }
    /**	Set the Y grid step
    	@param yg the Y grid step
    */
    public final void setYGridStep(int yg)
    {
    	if (yg>0) 
    		yGridStep=yg;

    }
    
    /**	Get the X grid step
    	@return the X grid step used
    */
    public final int getXGridStep()
    {	
    	return xGridStep;

    }
    /**	Get the Y grid step
    	@return the Y grid step used
    */
    public final int getYGridStep()
    {	
    	return yGridStep;

    }
    /**	Get the X magnification factor
    	@return the X magnification factor
    
    */
    public final double getXMagnitude()
    {
        return xMagnitude;
    }
    
    
    /**	Get the Y magnification factor
    	@return the Y magnification factor
    
    */
    public final double getYMagnitude()
    {
        return yMagnitude;
    }
    
    
    /**	Set the X magnification factor
    	@param xm the X magnification factor
    
    */
    public final void setXMagnitude(double xm)
    {
    	if (Math.abs(xm)<MIN_MAGNITUDE)
    		xm=MIN_MAGNITUDE;
    	
    	if (Math.abs(xm)>MAX_MAGNITUDE)
    		xm=MAX_MAGNITUDE;
    	
        xMagnitude=xm;
    }
    
    
    /**	Set the Y magnification factor
    	@param ym the Y magnification factor
    
    */
    public final void setYMagnitude(double ym)
    {
    	if (Math.abs(ym)<MIN_MAGNITUDE)
    		ym=MIN_MAGNITUDE;
    	
    	if (Math.abs(ym)>MAX_MAGNITUDE)
    		ym=MAX_MAGNITUDE;
    	
        yMagnitude=ym;
    }
    
        /**	Get the X magnification factor
    	@return the X magnification factor
    
    */
    public final double getXCenter()
    {
        return xCenter;
    }
    
    
    /**	Get the Y magnification factor
    	@return the Y magnification factor
    
    */
    public final double getYCenter()
    {
        return yCenter;
    }
    
    
    /**	Set the X center in pixel
    	@param xm the X center in pixel
    
    */
    public final void setXCenter(double xm)
    {
        xCenter=xm;
    }
    
    
    /**	Set the Y magnification factor
    	@param ym the Y magnification factor
    
    */
    public final void setYCenter(double ym)
    {
        yCenter=ym;
    }
    
    /**	Get the orientation
    	@return the orientation
    
    */
    public final int getOrientation()
    {
        return orientation;
    }

    /**	Set the orientation
    	@param o the wanted orientation
    
    */
    public final void setOrientation(int o)
    {
        orientation = o;
    }
       
    /**	Set both X and Y magnification factors
    	@param xm the X magnification factor
    	@param ym the Y magnification factor
    
    */
    public final void setMagnitudes(double xm, double ym)
    {
        
        setXMagnitude(xm);
        setYMagnitude(ym);
    }
    
    /**	Get the maximum tracked X coordinate
    	@return the maximum tracked X coordinate
    
    */
    public final int getXMax()
    {
        return xMax;
    }
    /**	Get the maximum tracked Y coordinate
    	@return the maximum tracked Y coordinate
    
    */
    public final int getYMax()
    {
        return yMax;
    }
    /**	Get the minimum tracked X coordinate
    	@return the minimum tracked X coordinate
    
    */
    public final int getXMin()
    {
        return xMin;
    }
     /**	Get the minimum tracked Y coordinate
    	@return the minimum tracked Y coordinate
    
    */
    public final int getYMin()
    {
        return yMin;
    }
    
    /** Reset the minimum and maximum X/Y pixel coordinates tracked. */
    public final void resetMinMax()
    {
        xMin=yMin=Integer.MAX_VALUE;
        xMax=yMax=Integer.MIN_VALUE;
    }
    
    
    
    /** Map the xc,yc coordinate given in the X pixel coordinate. The tracking
    	is active
        @param xc the horizontal coordinate in the drawing coordinate system.
        @param yc the vertical coordinate in the drawing coordinate system.
    */
    public final int mapX(double xc,double yc)
    {
    	return mapXi(xc, yc, true);
    }
    
    /** Map the xc,yc coordinate given in the X pixel coordinate.
        @param xc the horizontal coordinate in the drawing coordinate system.
        @param yc the vertical coordinate in the drawing coordinate system.
        @param track specifies if the tracking should be active or not
    */
    public final int mapXi(double xc,double yc, boolean track)
    {

        ivx=(int)Math.round(mapXr(xc,yc));   /* The integer cast cuts decimals 
        	to the lowest integer. We need to round correctly; */

        if(track) {
        	if(ivx<xMin)
            	xMin=ivx;
        	if(ivx>xMax)
            	xMax=ivx;
       	}
       	
        return ivx;
    }
   
   	/** Map the xc,yc coordinate given in the X pixel coordinate. The results
   		are given as double precision. Tracking is not active.
        @param xc the horizontal coordinate in the drawing coordinate system.
        @param yc the vertical coordinate in the drawing coordinate system.
    */
    public final double mapXr(double xc,double yc)
    {
        // The orientation data is not used outside a macro
        if(isMacro){
            xc-=100.0;
            yc-=100.0;
               
        	if(mirror) {
            	switch(orientation){
                	case 0:
                	    vx=-xc*xMagnitude;
             	       	break;
                	case 1:
                    	vx=yc*yMagnitude;
                    	break;
                	case 2:
                    	vx=xc*xMagnitude;
                    	break;
                
                	case 3:
                    	vx=-yc*yMagnitude;
                    	break;
    
                	default:
                	    vx=-xc*xMagnitude;
             	       	break;
            	}
        	} else {
            	switch(orientation){
            	    case 0:
            	        vx=xc*xMagnitude;
                	    break;
                	case 1:
                	    vx=-yc*yMagnitude;
               		    break;
                
                	case 2:
                	    vx=-xc*xMagnitude;
                	    break;
                
            	    case 3:
            	        vx=yc*yMagnitude;
            	        break;
    
            	    default:
            	        vx=xc*xMagnitude;
                	    break;
            	}
        	}   
        } else {
        	vx=(double)xc*xMagnitude;
        }
        return vx+xCenter;  
    }

    /** Map the xc,yc coordinate given in the Y pixel coordinate. The tracking
    	is active.
        @param xc the horizontal coordinate in the drawing coordinate system.
        @param yc the vertical coordinate in the drawing coordinate system.
    */
    public final int mapY(double xc,double yc)
    {
		return mapYi(xc, yc, true);        

    }
    /** Map the xc,yc coordinate given in the Y pixel coordinate. 
        @param xc the horizontal coordinate in the drawing coordinate system.
        @param yc the vertical coordinate in the drawing coordinate system.
        @param track specify if the point should be tracked
    */
    public final int mapYi(double xc,double yc, boolean track)
    {
        ivy=(int)Math.round(mapYr(xc,yc));   /* The integer cast cuts decimals 
        	to the lowest integer. We need to round correctly; */
        
        if(track) {
        	if(ivy<yMin)
            	yMin=ivy;
            
        	if(ivy>yMax)
            	yMax=ivy;
    	}
        return ivy;
    }
   	/** Map the xc,yc coordinate given in the Y pixel coordinate. The results
   		are given as double precision. Tracking is not active.
        @param xc the horizontal coordinate in the drawing coordinate system.
        @param yc the vertical coordinate in the drawing coordinate system.
    */       
    public final double mapYr(double xc,double yc)
    {  
        if(isMacro){
            xc-=100.0;
            yc-=100.0;
        	      
        	switch(orientation){
            	case 0:
                	vy=yc*yMagnitude;
        			break;
        			
            	case 1:
                	vy=xc*xMagnitude;
                	break;
            
            	case 2:
                	vy=-yc*yMagnitude;
                	break;
            
            	case 3:
                	vy=-xc*xMagnitude;
                	break;
                default:
                	vy=0.0;
        	}
        } else {
        	vy=(double)yc*yMagnitude;
        }
        
        return vy+yCenter;   
    }
    /** Add a point in the min/max tracking system. The point should be 
        specified in the SCREEN coordinates.
        @param xp the X coordinate of the point being tracked.
        @param yp the Y coordinate of the point being tracked.
    */
    public final void trackPoint(double xp, double yp)
    {
        if(yp<yMin)
            yMin=(int)yp;
            
        if(yp>yMax)
            yMax=(int)yp;
        
        if(xp<xMin)
            xMin=(int)xp;
            
        if(xp>xMax)
            xMax=(int)xp;
        
    }
    
    /** Un Map the X screen coordinate given in the drawing coordinate.
        If the snapping is active, it is NOT applied here.
        @param X the horizontal coordinate in the screen coordinate system.
    */
    public int unmapXnosnap(int X){
        int xc;
        xc=(int)((X-xCenter)/xMagnitude);
        return xc;
    }

    /** Un Map the Y screen coordinate given in the drawing coordinate.
        If the snapping is active, it is NOT applied here.
        @param Y the horizontal coordinate in the screen coordinate system.
    */
    public int unmapYnosnap(int Y){
        int yc;
        yc=(int)((Y-yCenter)/yMagnitude);
        return yc;
    }
    
    /** Un Map the X screen coordinate given in the drawing coordinate.
        If the snapping is active, it is applied here.
        @param X the horizontal coordinate in the screen coordinate system.
    */
    public int unmapXsnap(int X){
        int xc;
        xc=(int)((X-xCenter)/xMagnitude);
        // perform the snapping.
        if(snapActive) {
        	xc= (int)((double)xc/xGridStep+.5);
        	xc*=xGridStep;
        }
        return xc;
    }

    /** Un Map the Y screen coordinate given in the drawing coordinate.
        If the snapping is active, it is applied here.
        @param Y the horizontal coordinate in the screen coordinate system.
    */
    public int unmapYsnap(int Y){
        int yc;
        yc=(int)((Y-yCenter)/yMagnitude);
        if(snapActive) {
        	yc=(int)((double)yc/yGridStep+.5);
        	yc*=yGridStep;
        }
        return yc;
    }
    
    /** Create a string containing all possibly interesting info about the 
    	internal state of this class.    
    */
    public String toString()
    {
    	String s="";
        s+="[xCenter="+ xCenter;
    	s+="|yCenter="+ yCenter;
	    s+="|xMagnitude="+xMagnitude;
	    s+="|yMagnitude="+yMagnitude;
	    s+="|orientation="+orientation;
	    s+="|mirror="+ mirror;
	    s+="|isMacro="+isMacro;
	    s+="|snapActive="+snapActive;
    
	    s+="|xMin="+ xMin;
	    s+="|xMax="+xMax;
	    s+="|yMin="+yMin;
	    s+="|yMax="+yMax;
    
	    s+="|xGridStep="+xGridStep;
	    s+="|yGridStep="+ yGridStep+"]";
    
    	return s;
    }
}