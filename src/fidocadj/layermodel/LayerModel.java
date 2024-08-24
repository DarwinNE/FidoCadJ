// This file is part of FidoCadJ.
//
// FidoCadJ is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// FidoCadJ is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with FidoCadJ. If not,
//   @see <a href=http://www.gnu.org/licenses/>http://www.gnu.org/licenses/</a>.
//
// Copyright 2014-2023 Kohta Ozaki

package fidocadj.layermodel;

import java.util.*;

import fidocadj.circuit.model.DrawingModel;
import fidocadj.layers.LayerDesc;

/**
* Model for providing layers.<BR>
* @author Kohta Ozaki
*/
public class LayerModel
{
    private final DrawingModel drawingModel;

    /** Standard constructor.
        @param dm the drawing model to be used.
    */
    public LayerModel(DrawingModel dm)
    {
        this.drawingModel = dm;
    }

    /** Get the layer description from the drawing model.
        @return the array of layers.
    */
    public List<LayerDesc> getAllLayers()
    {
        return drawingModel.getLayers();
    }
}
