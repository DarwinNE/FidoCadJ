package fidocadj.layers;

import java.awt.*;
import java.util.*;

import fidocadj.globals.Globals;
import fidocadj.graphic.swing.ColorSwing;

/**         SWING VERSION
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

public final class StandardLayers
{
    // A dummy list of layers.
    private static java.util.List<LayerDesc> ll_dummy;
    private final static Object lock = new Object();

    /** Private constructor, for Utility class pattern
    */
    private StandardLayers ()
    {
        // nothing
    }
    /** Create the standard array containing the layer descriptions, colors
        and transparency. The name of the layers are read from the resources
        which may be initizialized. If Globals.messages==null, no description
        is given.

        @return the list of the layers being created.
    */
    public static java.util.List<LayerDesc> createStandardLayers()
    {
        java.util.List<LayerDesc> layerDesc;
        synchronized(lock) {
            String s="";

            layerDesc=new Vector<LayerDesc>();
            if(Globals.messages!=null) {
                s=Globals.messages.getString("Circuit_l");
            }
            layerDesc.add(new LayerDesc(new ColorSwing(Color.black), true,
                s,1.0f));   // 0
            if(Globals.messages!=null) {
                s=Globals.messages.getString("Bottom_copper");
            }
            layerDesc.add(new LayerDesc(new ColorSwing(new Color(0,0,128)),
                true, s,1.0f)); // 1
            if(Globals.messages!=null) {
                s=Globals.messages.getString("Top_copper");
            }
            layerDesc.add(new LayerDesc(new ColorSwing(Color.red), true,
                s,1.0f));           // 2
            if(Globals.messages!=null) {
                s=Globals.messages.getString("Silkscreen");
            }
            layerDesc.add(new LayerDesc(new ColorSwing(new Color(0,128,128)),
                 true,s,1.0f));// 3

            if(Globals.messages!=null) {
                s=Globals.messages.getString("Other_1");
            }
            layerDesc.add(new LayerDesc(new ColorSwing(Color.orange),
                true,s,1.0f));      // 4
            if(Globals.messages!=null) {
                s=Globals.messages.getString("Other_2");
            }
            layerDesc.add(new LayerDesc(new ColorSwing(new Color(-8388864)),
                true,s,1.0f));  // 5
            if(Globals.messages!=null) {
                s=Globals.messages.getString("Other_3");
            }
            layerDesc.add(new LayerDesc(new ColorSwing(new Color(-16711681)),
                true,s,1.0f));// 6
            if(Globals.messages!=null) {
                s=Globals.messages.getString("Other_4");
            }
            layerDesc.add(new LayerDesc(new ColorSwing(new Color(-16744448)),
                true,s,1.0f));// 7

            if(Globals.messages!=null) {
                s=Globals.messages.getString("Other_5");
            }
            layerDesc.add(new LayerDesc(new ColorSwing(new Color(-6632142)),
                true, s,1.0f));// 8
            if(Globals.messages!=null) {
                s=Globals.messages.getString("Other_6");
            }
            layerDesc.add(new LayerDesc(new ColorSwing(new Color(-60269)),
                true,s,1.0f));  // 9
            if(Globals.messages!=null) {
                s=Globals.messages.getString("Other_7");
            }
            layerDesc.add(new LayerDesc(new ColorSwing(new Color(-4875508)),
                true,s,1.0f));  // 10
            if(Globals.messages!=null) {
                s=Globals.messages.getString("Other_8");
            }
            layerDesc.add(new LayerDesc(new ColorSwing(new Color(-16678657)),
                true,s,1.0f));// 11
            if(Globals.messages!=null) {
                s=Globals.messages.getString("Other_9");
            }
            layerDesc.add(new LayerDesc(new ColorSwing(new Color(-1973791)),
                true,s,0.95f));// 12
            if(Globals.messages!=null) {
                s=Globals.messages.getString("Other_10");
            }
            layerDesc.add(new LayerDesc(new ColorSwing(new Color(-6118750)),
                true,s,0.9f));  // 13
            if(Globals.messages!=null) {
                s=Globals.messages.getString("Other_11");
            }
            layerDesc.add(new LayerDesc(new ColorSwing(new Color(-10526881)),
                true,s,0.9f));// 14
            if(Globals.messages!=null) {
                s=Globals.messages.getString("Other_12");
            }
            layerDesc.add(new LayerDesc(new ColorSwing(Color.black),
                true, s,1.0f));     // 15
        }
        return layerDesc;
    }

    /**  Create a fictionous Array List.

         @return an Vector composed by LayerDesc.MAX_LAYERS opaque layers in
            green.
    */
    public static java.util.List<LayerDesc> createEditingLayerArray()
    {
        synchronized(lock) {
            // This is called at each redraw, so it is a good idea to avoid
            // creating it each time.
            if(ll_dummy == null || ll_dummy.isEmpty()) {
                ll_dummy = new Vector<LayerDesc>();
                ll_dummy.add(new LayerDesc(new ColorSwing(Color.green),
                    true, "", 1.0f));
            }
        }
        return ll_dummy;
    }
}