package net.sourceforge.fidocadj.layers;

import android.graphics.*;

import java.util.Vector;

import android.content.Context;
import android.content.res.Resources;

import net.sourceforge.fidocadj.graphic.android.*;
import net.sourceforge.fidocadj.*;


/**         ANDROID VERSION


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

    Copyright 2008-2015 by Davide Bucci

</pre>

    @author Davide Bucci
*/
public class StandardLayers
{
    // A dummy list of layers.
    private static Vector<LayerDesc> ll_dummy;

    /** Create the standard array containing the layer descriptions, colors
        and transparency.

        @return the list of the layers being created.
    */
    public static Vector<LayerDesc> createStandardLayers()
    {
        Vector<LayerDesc> layerDesc=new Vector<LayerDesc>();

        layerDesc.add(new LayerDesc(new ColorAndroid(Color.BLACK), true,
            "1",1.0f)); // 0
        layerDesc.add(new LayerDesc(new ColorAndroid(Color.rgb(0,0,128)),
            true, "2",1.0f));   // 1
        layerDesc.add(new LayerDesc(new ColorAndroid(Color.RED), true,
            "3",1.0f));         // 2
        layerDesc.add(new LayerDesc(new ColorAndroid(Color.rgb(0,128,128)),
             true,"4",1.0f));// 3

        layerDesc.add(new LayerDesc(new ColorAndroid(Color.YELLOW),
            true,"5",1.0f));        // 4
        layerDesc.add(new LayerDesc(new ColorAndroid(-8388864),
            true,"6",1.0f));    // 5
        layerDesc.add(new LayerDesc(new ColorAndroid(-16711681),
            true,"7",1.0f));// 6
        layerDesc.add(new LayerDesc(new ColorAndroid(-16744448),
            true,"8",1.0f));// 7

        layerDesc.add(new LayerDesc(new ColorAndroid(-6632142),
            true,"9",1.0f));// 8
        layerDesc.add(new LayerDesc(new ColorAndroid(-60269),
            true,"10",1.0f));   // 9
        layerDesc.add(new LayerDesc(new ColorAndroid(-4875508),
            true,"11",1.0f));   // 10
        layerDesc.add(new LayerDesc(new ColorAndroid(-16678657),
            true,"12",1.0f));// 11

        layerDesc.add(new LayerDesc(new ColorAndroid(-1973791),
            true,"13",0.95f));// 12
        layerDesc.add(new LayerDesc(new ColorAndroid(-6118750),
            true,"14",0.9f));   // 13
        layerDesc.add(new LayerDesc(new ColorAndroid(-10526881),
            true,"15",0.9f));// 14
        layerDesc.add(new LayerDesc(new ColorAndroid(Color.BLACK),
            true, "16",1.0f));  // 15

        return layerDesc;
    }

    /** Create the standard array containing the layer descriptions, colors
        and transparency. The name of the layers are read from the resources
        which are related to the given Context.

        @return the list of the layers being created.
    */
    public static Vector<LayerDesc> createStandardLayers(Context context)
    {
        Vector<LayerDesc> layerDesc=new Vector<LayerDesc>();
        Resources resources = context.getResources();

        layerDesc.add(new LayerDesc(new ColorAndroid(Color.BLACK), true,
            resources.getString(R.string.Circuit_l),1.0f)); // 0
        layerDesc.add(new LayerDesc(new ColorAndroid(Color.rgb(0,0,128)),
            true, resources.getString(R.string.Bottom_copper),1.0f));   // 1
        layerDesc.add(new LayerDesc(new ColorAndroid(Color.RED), true,
            resources.getString(R.string.Top_copper),1.0f));            // 2
        layerDesc.add(new LayerDesc(new ColorAndroid(Color.rgb(0,128,128)),
             true,resources.getString(R.string.Silkscreen),1.0f));// 3

        layerDesc.add(new LayerDesc(new ColorAndroid(Color.YELLOW),
            true,resources.getString(R.string.Other_1),1.0f));      // 4
        layerDesc.add(new LayerDesc(new ColorAndroid(-8388864),
            true,resources.getString(R.string.Other_2),1.0f));  // 5
        layerDesc.add(new LayerDesc(new ColorAndroid(-16711681),
            true,resources.getString(R.string.Other_3),1.0f));// 6
        layerDesc.add(new LayerDesc(new ColorAndroid(-16744448),
            true,resources.getString(R.string.Other_4),1.0f));// 7

        layerDesc.add(new LayerDesc(new ColorAndroid(-6632142),
            true, resources.getString(R.string.Other_5),1.0f));// 8
        layerDesc.add(new LayerDesc(new ColorAndroid(-60269),
            true,resources.getString(R.string.Other_6),1.0f));  // 9
        layerDesc.add(new LayerDesc(new ColorAndroid(-4875508),
            true,resources.getString(R.string.Other_7),1.0f));  // 10
        layerDesc.add(new LayerDesc(new ColorAndroid(-16678657),
            true,resources.getString(R.string.Other_8),1.0f));// 11

        layerDesc.add(new LayerDesc(new ColorAndroid(-1973791),
            true,resources.getString(R.string.Other_9),0.95f));// 12
        layerDesc.add(new LayerDesc(new ColorAndroid(-6118750),
            true,resources.getString(R.string.Other_10),0.9f)); // 13
        layerDesc.add(new LayerDesc(new ColorAndroid(-10526881),
            true,resources.getString(R.string.Other_11),0.9f));// 14
        layerDesc.add(new LayerDesc(new ColorAndroid(Color.BLACK),
            true, resources.getString(R.string.Other_12),1.0f));    // 15

        return layerDesc;
    }

    /**  Create a fictionous Array List.

         @return an Vector composed by LayerDesc.MAX_LAYERS opaque layers in
            green.
    */
    public static Vector<LayerDesc> createEditingLayerArray()
    {
        // This is called at each redraw, so it is a good idea to avoid
        // creating it each time.
        if(ll_dummy == null || ll_dummy.isEmpty()) {
            ll_dummy = new Vector<LayerDesc>();
            ll_dummy.add(new LayerDesc(new ColorAndroid(Color.GREEN), true,
                "",1.0f));
        }

        return ll_dummy;
    }
}