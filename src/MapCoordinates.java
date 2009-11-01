/** MapCoordinates.java v.1.4

<pre>
   ****************************************************************************
   Version History 

Version   Date           Author       Remarks
------------------------------------------------------------------------------
1.0     March 2007      	D. Bucci    First working version
1.1     December 2007   	D. Bucci    Slight optimization
1.2     December 2007   	D. Bucci    Track the min/max coordinates in pixel
                                     	Internal resolution increased
1.2.1   January 2008    	D. Bucci    More flexible coordinate tracking                               
1.3     May 2008        	D. Bucci    Grid snapping
1.4		June 2009			D. Bucci 	Capitalize the first letters                                     

                               
    Written by Davide Bucci, March-December 2007, davbucci at tiscali dot it
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version. 

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

    MapCoordinates performs the coordinate mapping between the logical units
    used in Fidocad schematics (in mils?) with the corrisponding pixel position
    to be used when drawing on the screen or on an image file. The class
    tracks also the minimum and maximum values of the pixel x/y coordinates, in
    order to obtain the drawing size. 
    
    The logical Fidocad resolution is 5 mils. Thus, for the following 
    resolutions, we obtain:
    
    Resolution              units/pixel         x/y magnitude
    ---------------------------------------------------------
    72  pixels/inch         2.7778              0.36000
    150 pixels/inch         1.3333              0.75000
    300 pixels/inch         0.66666             1.50000
    600 pixels/inch         0.33333             3.00000
    1200 pixels/inch        0.16667             6.00000

</pre>


    @author D. Bucci
    @version 1.2.1, January 2008
*/

import java.awt.*;

public class MapCoordinates
{
	// Every member should be made private sooner or later...
    int xCenter;
    int yCenter;
    private double xMagnitude;
    private double yMagnitude;
    int orientation;
    boolean mirror;
    boolean isMacro;
    boolean snapActive;
    
    static final double MIN_MAGNITUDE=0.25;
    static final double MAX_MAGNITUDE=100.0;

    
    private int xMin;
    private int xMax;
    private int yMin;
    private int yMax;
    
    private int xGridStep;
    private int yGridStep;
    
    /** Standard constructor */
    MapCoordinates()
    {   
        xCenter=0;
        yCenter=0;
        xMagnitude=1.0;
        yMagnitude=1.0;
        orientation=0;
        xGridStep=5;
        yGridStep=5;
        isMacro=false;
        snapActive=true;
    }
    
    /** Set the snapping state (used in the unmapping functions)
    	@param s the wanted state.
    */
    public void setSnap(boolean s)
    {
    	snapActive=s;
    }
    
    /** Get the snapping state (used in the unmapping functions)
    	@return the current snapping state.
    */
    public boolean getSnap()
    {
    	return snapActive;
    }
    
    /**	Set the X grid step
    	@param xg the X grid step
    */
    public void setXGridStep(int xg)
    {
    	if (xg>0) 
    		xGridStep=xg;

    }
    /**	Set the Y grid step
    	@param yg the Y grid step
    */
    public void setYGridStep(int yg)
    {
    	if (yg>0) 
    		yGridStep=yg;

    }
    
    /**	Get the X grid step
    	@return the X grid step used
    */
    public int getXGridStep()
    {	
    	return xGridStep;

    }
    /**	Get the Y grid step
    	@return the Y grid step used
    */
    public int getYGridStep()
    {	
    	return yGridStep;

    }
    /**	Get the X magnification factor
    	@return the X magnification factor
    
    */
    public double getXMagnitude()
    {
        return xMagnitude;
    }
    
    
    /**	Get the Y magnification factor
    	@return the Y magnification factor
    
    */
    public double getYMagnitude()
    {
        return yMagnitude;
    }
    
    
    /**	Set the X magnification factor
    	@param xm the X magnification factor
    
    */
    public void setXMagnitude(double xm)
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
    public void setYMagnitude(double ym)
    {
    	if (Math.abs(ym)<MIN_MAGNITUDE)
    		ym=MIN_MAGNITUDE;
    	
    	if (Math.abs(ym)>MAX_MAGNITUDE)
    		ym=MAX_MAGNITUDE;
    	
        yMagnitude=ym;
    }
    
    /**	Set both X and Y magnification factors
    	@param xm the X magnification factor
    	@param ym the Y magnification factor
    
    */
    public void setMagnitudes(double xm, double ym)
    {
        
        setXMagnitude(xm);
        setYMagnitude(ym);
    }
    
    /**	Get the maximum tracked X coordinate
    	@return the maximum tracked X coordinate
    
    */
    public int getXMax()
    {
        return xMax;
    }
    /**	Get the maximum tracked Y coordinate
    	@return the maximum tracked Y coordinate
    
    */
    public int getYMax()
    {
        return yMax;
    }
    /**	Get the minimum tracked X coordinate
    	@return the minimum tracked X coordinate
    
    */
    public int getXMin()
    {
        return xMin;
    }
     /**	Get the minimum tracked Y coordinate
    	@return the minimum tracked Y coordinate
    
    */
    public int getYMin()
    {
        return yMin;
    }
    
    /** Reset the minimum and maximum X/Y pixel coordinates tracked. */
    void resetMinMax()
    {
        xMin=yMin=Integer.MAX_VALUE;
        xMax=yMax=Integer.MIN_VALUE;
    }
    
    /** Map the xc,yc coordinate given in the X pixel coordinate.
        @param xc the horizontal coordinate in the drawing coordinate system.
        @param yc the vertical coordinate in the drawing coordinate system.
    */
    int mapX(int xc,int yc){
        double vx;
        
        if(isMacro){
            xc-=100;
            yc-=100;
        }
        
        if(mirror) {
            switch(orientation){
                case 1:
                    vx=yc*yMagnitude+xCenter;
                    break;
                
                case 2:
                    vx=xc*xMagnitude+xCenter;
                    break;
                
                case 3:
                    vx=-yc*yMagnitude+xCenter;
                    break;
    
                case 0:
                    vx=-xc*xMagnitude+xCenter;
                    break;
    
                default:
                    vx=0;
            }
        } else {
            switch(orientation){
                case 1:
                    vx=-yc*yMagnitude+xCenter;
                    break;
                
                case 2:
                    vx=-xc*xMagnitude+xCenter;
                    break;
                
                case 3:
                    vx=yc*yMagnitude+xCenter;
                    break;
    
                case 0:
                    vx=xc*xMagnitude+xCenter;
                    break;
    
                default:
                    vx= 0;
            }
        }   
        
        int ivx=(int)(vx+.5);   /* The integer cast cuts decimals to the lowest 
                                   integer. We need to round correctly; */
        
        if(ivx<xMin)
            xMin=ivx;
        if(vx>xMax)
            xMax=ivx;
        
        return ivx;
    }


    /** Map the xc,yc coordinate given in the Y pixel coordinate.
        @param xc the horizontal coordinate in the drawing coordinate system.
        @param yc the vertical coordinate in the drawing coordinate system.
    */
    int mapY(int xc,int yc){
        double vy;
        
        if(isMacro){
            xc-=100;
            yc-=100;
        }
        
        
        switch(orientation){
            case 1:
                vy=xc*xMagnitude+yCenter;
                break;
            
            case 2:
                vy=-yc*yMagnitude+yCenter;
                break;
            
            case 3:
                vy=-xc*xMagnitude+yCenter;
                break;
                        
            default:
                vy=yc*yMagnitude+yCenter;
        
        }
        
        int ivy=(int)(vy+.5);   /* The integer cast cuts decimals to the lowest 
                                   integer. We need to round correctly; */
        
        if(ivy<yMin)
            yMin=ivy;
            
        if(ivy>yMax)
            yMax=ivy;
    
        return ivy;
    }
    
    /** Add a point in the min/max tracking system. The point should be 
        specified in the SCREEN coordinates.
        @param xp the X coordinate of the point being tracked.
        @param yp the Y coordinate of the point being tracked.
    */
    void trackPoint(int xp, int yp)
    {
        if(yp<yMin)
            yMin=yp;
            
        if(yp>yMax)
            yMax=yp;
        
        if(xp<xMin)
            xMin=xp;
            
        if(xp>xMax)
            xMax=xp;
        
    }
    /** Add a point in the min/max tracking system. The point should be 
        specified in the SCREEN coordinates. Draw a cross in the specified
        point.
        
        @param xp the X coordinate of the point being tracked.
        @param yp the Y coordinate of the point being tracked.
    */
    void trackPoint(Graphics d, int xp, int yp)
    {
        trackPoint(xp,yp);
        d.drawLine(xp-3,yp,xp+3,yp);
        d.drawLine(xp,yp-3,xp,yp+3);
    }
    
    /** Un Map the X screen coordinate given in the drawing coordinate.
        If the snapping is active, it is NOT applied here.
        @param X the horizontal coordinate in the screen coordinate system.
    */
    int unmapXnosnap(int X){
        int xc;
        //X=-xc*xMagnitude+xCenter;
        xc=(int)((X-xCenter)/xMagnitude);
        
        
        return xc;
    }


    /** Un Map the Y screen coordinate given in the drawing coordinate.
        If the snapping is active, it is NOT applied here.
        @param Y the horizontal coordinate in the screen coordinate system.
    */
    int unmapYnosnap(int Y){
        int yc;
        //Y=yc*yMagnitude+yCenter;
        yc=(int)((Y-yCenter)/yMagnitude);
        return yc;
    }
    
     /** Un Map the X screen coordinate given in the drawing coordinate.
        If the snapping is active, it is applied here.
        @param X the horizontal coordinate in the screen coordinate system.
    */
    int unmapXsnap(int X){
        int xc;
        //X=-xc*xMagnitude+xCenter;
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
    int unmapYsnap(int Y){
        int yc;
        //Y=yc*yMagnitude+yCenter;
        yc=(int)((Y-yCenter)/yMagnitude);
        if(snapActive) {
        	yc=(int)((double)yc/yGridStep+.5);
        	yc*=yGridStep;
        }
        return yc;
    }
    
}