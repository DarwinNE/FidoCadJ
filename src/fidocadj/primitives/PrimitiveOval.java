package fidocadj.primitives;

import java.io.*;
import java.util.*;

import fidocadj.dialogs.ParameterDescription;
import fidocadj.dialogs.DashInfo;
import fidocadj.export.ExportInterface;
import fidocadj.geom.GeometricDistances;
import fidocadj.geom.MapCoordinates;
import fidocadj.globals.Globals;
import fidocadj.graphic.GraphicsInterface;
import fidocadj.graphic.SelectionRectangle;


/** Class to handle the Oval primitive.

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
    along with FidoCadJ. If not,
    @see <a href=http://www.gnu.org/licenses/>http://www.gnu.org/licenses/</a>.

    Copyright 2007-2023 by Davide Bucci
    </pre>

    @author Davide Bucci
*/

public final class PrimitiveOval extends GraphicPrimitive
{

    // An oval is defined by two points.
    static final int N_POINTS=4;
    private boolean isFilled;
    private int dashStyle;

    // Those are data which are kept for the fast redraw of this primitive.
    // Basically, they are calculated once and then used as much as possible
    // without having to calculate everything from scratch.
    private int xa;
    private int ya;
    private int xb;
    private int yb;
    private int x1;         // NOPMD
    private int x2;         // NOPMD
    private int y1;         // NOPMD
    private int y2;         // NOPMD
    private float w;

    /** Gets the number of control points used.
        @return the number of points used by the primitive
    */
    public int getControlPointNumber()
    {
        return N_POINTS;
    }

    /** Standard constructor.
        @param f the name of the font for attached text.
        @param size the size of the font for attached text.
    */
    public PrimitiveOval(String f, int size)
    {
        super();
        isFilled=false;
        initPrimitive(-1, f, size);
    }
    /** Create an oval defined by two points.
        @param x1 the start x coordinate (logical unit).
        @param y1 the start y coordinate (logical unit).
        @param x2 the end x coordinate (logical unit).
        @param y2 the end y coordinate (logical unit).
        @param f specifies if the ellipse should be filled.
        @param layer the layer to be used.
        @param dashSt the style of the dashing to be used.
        @param font the name of the font for attached text.
        @param size the size of the font for attached text.
    */
    public PrimitiveOval(int x1, int y1, int x2, int y2, boolean f, int layer,
        int dashSt, String font, int size)
    {
        super();
        initPrimitive(-1, font, size);

        virtualPoint[0].x=x1;
        virtualPoint[0].y=y1;
        virtualPoint[1].x=x2;
        virtualPoint[1].y=y2;
        virtualPoint[getNameVirtualPointNumber()].x=x1+5;
        virtualPoint[getNameVirtualPointNumber()].y=y1+5;
        virtualPoint[getValueVirtualPointNumber()].x=x1+5;
        virtualPoint[getValueVirtualPointNumber()].y=y1+10;

        isFilled=f;
        dashStyle =dashSt;

        setLayer(layer);

    }

    /** Draw the graphic primitive on the given graphic context.
        @param g the graphic context in which the primitive should be drawn.
        @param coordSys the graphic coordinates system to be applied.
        @param layerV the layer description.
    */
    public void draw(GraphicsInterface g, MapCoordinates coordSys,
        List layerV)
    {

        if(!selectLayer(g,layerV)) {
            return;
        }

        drawText(g, coordSys, layerV, -1);

        // in the oval primitive, the first two virtual points represent
        // the two corners of the oval diagonal

        if(changed) {
            changed=false;
            x1=coordSys.mapX(virtualPoint[0].x,virtualPoint[0].y);
            y1=coordSys.mapY(virtualPoint[0].x,virtualPoint[0].y);
            x2=coordSys.mapX(virtualPoint[1].x,virtualPoint[1].y);
            y2=coordSys.mapY(virtualPoint[1].x,virtualPoint[1].y);

            // Sort the coordinates
            if (x1>x2) {
                xa=x2;
                xb=x1;
            } else {
                xa=x1;
                xb=x2;
            }
            if (y1>y2) {
                ya=y2;
                yb=y1;
            } else {
                ya=y1;
                yb=y2;
            }
            coordSys.trackPoint(xa,ya);
            coordSys.trackPoint(xb,yb);
            w = (float)(Globals.lineWidth*coordSys.getXMagnitude());
            if (w<D_MIN) { w=D_MIN; }
        }

        if(!g.hitClip(xa,ya, xb-xa+1,yb-ya+1)) {
            return;
        }

        g.applyStroke(w, dashStyle);

        // Draw the oval, filled or not.
        if (isFilled) {
            g.fillOval(xa,ya,xb-xa,yb-ya);
        } else {
            if(xa!=xb && ya!=yb) {
                g.drawOval(xa,ya,xb-xa,yb-ya);
            } else {
                // Degenerate to a single line.
                g.drawLine(xa,ya,xb,yb);
            }
        }
    }

    /** Parse a token array and store the graphic data for a given primitive
        Obviously, that routine should be called *after* having recognized
        that the called primitive is correct.
        That routine also sets the current layer.

        @param tokens the tokens to be processed. tokens[0] should be the
        command of the actual primitive.
        @param nn the number of tokens present in the array
        @throws IOException if the arguments are incorrect or the primitive
            is invalid.
    */
    public void parseTokens(String[] tokens, int nn)
        throws IOException
    {
        changed=true;

        // assert it is the correct primitive
        if ("EV".equals(tokens[0])||"EP".equals(tokens[0])) {   // Oval
            if (nn<5) {
                throw new IOException("Bad arguments on EV/EP");
            }
            int x1 = virtualPoint[0].x=Integer.parseInt(tokens[1]);
            int y1 = virtualPoint[0].y=Integer.parseInt(tokens[2]);
            virtualPoint[1].x=Integer.parseInt(tokens[3]);
            virtualPoint[1].y=Integer.parseInt(tokens[4]);

            virtualPoint[getNameVirtualPointNumber()].x=x1+5;
            virtualPoint[getNameVirtualPointNumber()].y=y1+5;
            virtualPoint[getValueVirtualPointNumber()].x=x1+5;
            virtualPoint[getValueVirtualPointNumber()].y=y1+10;

            if(nn>5) { parseLayer(tokens[5]); }
            if("EP".equals(tokens[0])) {
                isFilled=true;
            } else {
                isFilled=false;
            }
            if(nn>6 && "FCJ".equals(tokens[6])) {
                dashStyle = checkDashStyle(Integer.parseInt(tokens[7]));
            }
        } else {
            throw new IOException("EV/EP: Invalid primitive:"+tokens[0]+
                                          " programming error?");
        }
    }

    /** Get the control parameters of the given primitive.

        @return a vector of ParameterDescription containing each control
                parameter.
                The first parameters should always be the virtual points.

    */
    public List<ParameterDescription> getControls()
    {
        List<ParameterDescription> v=super.getControls();
        ParameterDescription pd = new ParameterDescription();

        pd.parameter=Boolean.valueOf(isFilled);
        pd.description=Globals.messages.getString("ctrl_filled");
        v.add(pd);
        pd = new ParameterDescription();
        pd.parameter=new DashInfo(dashStyle);
        pd.description=Globals.messages.getString("ctrl_dash_style");
        pd.isExtension = true;
        v.add(pd);

        return v;
    }

    /** Set the control parameters of the given primitive.
        This method is specular to getControls().
        @param v a vector of ParameterDescription containing each control
                parameter.
                The first parameters should always be the virtual points.
        @return the next index in v to be scanned (if needed) after the
            execution of this function.
    */
    public int setControls(List<ParameterDescription> v) // NOPMD bug in PMD?
    {
        int i=super.setControls(v);
        ParameterDescription pd;

        pd=(ParameterDescription)v.get(i);
        ++i;
        // Check, just for sure...
        if (pd.parameter instanceof Boolean) {
            isFilled=((Boolean)pd.parameter).booleanValue();
        } else {
            System.out.println("Warning: unexpected parameter!"+pd);
        }
        pd=(ParameterDescription)v.get(i++);
        if (pd.parameter instanceof DashInfo) {
            dashStyle=((DashInfo)pd.parameter).style;
        } else {
            System.out.println("Warning: unexpected parameter!"+pd);
        }

        // Parameters validation and correction
        if(dashStyle>=Globals.dashNumber) {
            dashStyle=Globals.dashNumber-1;
        }
        if(dashStyle<0) {
            dashStyle=0;
        }

        return i;
    }


    /** Gets the distance (in primitive's coordinates space) between a
        given point and the primitive.
        When it is reasonable, the behaviour can be binary (polygons,
        ovals...). In other cases (lines, points), it can be proportional.
        @param px the x coordinate of the given point.
        @param py the y coordinate of the given point.
        @return the distance in logical units.
    */
    public int getDistanceToPoint(int px, int py)
    {
        // Here we check if the given point lies inside the text areas

        if(checkText(px, py)) {
            return 0;
        }

        int xa=Math.min(virtualPoint[0].x,virtualPoint[1].x);
        int ya=Math.min(virtualPoint[0].y,virtualPoint[1].y);
        int xb=Math.max(virtualPoint[0].x,virtualPoint[1].x);
        int yb=Math.max(virtualPoint[0].y,virtualPoint[1].y);

        if(isFilled){
            if(GeometricDistances.pointInEllipse(xa,ya,xb-xa,yb-ya,px,py)) {
                return 0;
            } else {
                return 1000;
            }
        } else {
            return GeometricDistances.pointToEllipse(xa,ya,
                xb-xa,yb-ya,px,py);
        }
    }

    /** Obtain a string command descripion of the primitive.
        @param extensions true if FidoCadJ extensions to the old FidoCAD format
            should be active.
        @return the FIDOCAD command line.
    */
    public String toString(boolean extensions)
    {
        String cmd;

        if (isFilled) {
            cmd="EP ";
        } else {
            cmd="EV ";
        }

        cmd+=virtualPoint[0].x+" "+virtualPoint[0].y+" "+
            +virtualPoint[1].x+" "+virtualPoint[1].y+" "+
            getLayer()+"\n";

        if(extensions && (dashStyle>0 || hasName() || hasValue())) {
            String text = "0";
            if (name.length()!=0 || value.length()!=0) {
                text = "1";
            }
            cmd+="FCJ "+dashStyle+" "+text+"\n";
        }
        // The false is needed since saveText should not write the FCJ tag.
        cmd+=saveText(false);

        return cmd;
    }

    /** Export the primitive on a vector graphic format.
        @param exp the export interface to employ.
        @param cs the coordinate mapping to employ.
        @throws IOException if a problem occurs, such as it is impossible to
            write on the output file.
    */
    public void export(ExportInterface exp, MapCoordinates cs)
        throws IOException
    {
        exportText(exp, cs, -1);
        exp.exportOval(cs.mapX(virtualPoint[0].x,virtualPoint[0].y),
                       cs.mapY(virtualPoint[0].x,virtualPoint[0].y),
                       cs.mapX(virtualPoint[1].x,virtualPoint[1].y),
                       cs.mapY(virtualPoint[1].x,virtualPoint[1].y),
                       isFilled,
                       getLayer(),
                       dashStyle,
                       Globals.lineWidth*cs.getXMagnitude());
    }
    /** Get the number of the virtual point associated to the Name property
        @return the number of the virtual point associated to the Name property
    */
    public int getNameVirtualPointNumber()
    {
        return 2;
    }

    /** Get the number of the virtual point associated to the Value property
        @return the number of the virtual point associated to the Value property
    */
    public  int getValueVirtualPointNumber()
    {
        return 3;
    }
    
    /**
     * Determines whether the oval defined by the points in virtualPoint
     * intersects with the specified rectangle.
     *
     * This method calculates the bounding box of the oval, checks for an
     * intersection with the given rectangle, and then performs a more precise
     * check to see if any point on the edge of the oval falls within the
     * rectangle.
     *
     * @param rect the Rectangle object to check for intersection.
     * 
     * @param isLeftToRightSelection Determine the direction of the selection
     *
     * @return true if any point on the edge of the oval intersects the
     *         rectangle, false otherwise.
     */
    @Override
    public boolean intersects(SelectionRectangle rect, 
                              boolean isLeftToRightSelection)
    {
        if (isLeftToRightSelection)
            return isFullyContained(rect);  
        
        int x1 = Math.min(virtualPoint[0].x, virtualPoint[1].x);
        int y1 = Math.min(virtualPoint[0].y, virtualPoint[1].y);
        int x2 = Math.max(virtualPoint[0].x, virtualPoint[1].x);
        int y2 = Math.max(virtualPoint[0].y, virtualPoint[1].y);
        
        SelectionRectangle ovalBounds = new SelectionRectangle(
                                                x1, y1, x2 - x1, y2 - y1);
        if (rect.intersects(ovalBounds)) {
            int centerX = (x1 + x2) / 2;
            int centerY = (y1 + y2) / 2;
            int a = (x2 - x1) / 2;
            int b = (y2 - y1) / 2;

            for (int i = rect.getX(); i <= rect.getX() + rect.getWidth(); i++) {
                for (int j = rect.getY(); j <= 
                        rect.getY() + rect.getHeight(); j++) {
                    double normalizedX = (double) (i - centerX) / a;
                    double normalizedY = (double) (j - centerY) / b;
                    if (Math.abs(normalizedX * normalizedX + 
                                 normalizedY * normalizedY - 1) < 0.05) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}