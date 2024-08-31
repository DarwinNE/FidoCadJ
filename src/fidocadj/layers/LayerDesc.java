package fidocadj.layers;

import fidocadj.graphic.ColorInterface;

/** layerDesc.java

   Provide a complete description of each layer (color, visibility).
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

    Copyright 2008-2023 by Davide Bucci

    </pre>

    @author Davide Bucci
*/

public class LayerDesc
{
    // Number of layers to be treated:
    public static final int MAX_LAYERS=16;

    // The color of the layer:
    private ColorInterface layerColor;

    // isVisible is true if the layer should be drawn:
    private boolean isVisible;

    // is Modified is true if a redraw is needed:
    private boolean isModified;

    // Name or description of the layer:
    private String layerDescription;

    // Transparency
    private float alpha;

    /** Standard constructor: obtain a visible layer with a black color and no
        description.
    */
    public LayerDesc()
    {
        layerColor=null;
        isVisible=true;
        layerDescription="";

    }

    /** Standard constructor.
        @param c the color which should be used.
        @param v the visibility of the layer.
        @param d the layer description.
        @param a the transparency level (alpha), between 0.0 and 1.0.
    */
    public LayerDesc(ColorInterface c, boolean v, String d, float a)
    {
        layerColor=c;
        isVisible=v;
        layerDescription=d;
        alpha = a;

    }

    /** This method allows to obtain the color in which this layer should be
        drawn.

        @return the color to be used
    */
    final public ColorInterface getColor()
    {
        return layerColor;
    }

    /** This method allows to obtain the alpha channel of the current layer.

        @return the alpha blend
    */
    final public float getAlpha()
    {
        return alpha;
    }

    /** This method returns true if this layer should be traced

        @return a boolean value indicating if the layer should be drawn
    */
    final public boolean isVisible()
    {
        return isVisible;
    }

    /** This method returns true if this layer has been modified

        @return a boolean value indicating that the layer has been modified
    */
    final public boolean isModified()
    {
        return isModified;
    }

    /** This method allows to obtain the color in which this layer should be
        drawn.

        @return the color to be used
    */
    public String getDescription()
    {
        return layerDescription;
    }

    /** This method allows to set the layer description.

        @param s the layer description
    */
    final public void setDescription(String s)
    {
        layerDescription=s;
    }

    /** This method allows to set the layer visibility.

        @param v the layer visibility.
    */
    final public void setVisible(boolean v)
    {
        isVisible=v;
    }

    /** This method allows to indicate that the layer has been modified.

        @param v true if the layer should be considered as modified.
    */
    final public void setModified(boolean v)
    {
        isModified=v;
    }

    /** This method allows to set the layer color.

        @param c the layer color.
    */
    final public void setColor(ColorInterface c)
    {
        layerColor=c;
    }

     /** This method allows to set the alpha blend.

        @param a the alpha blend.
    */
    final public void setAlpha(float a)
    {
        alpha=a;
    }
}