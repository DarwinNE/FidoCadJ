package net.sourceforge.fidocadj.layermodel;

import java.util.*;
import layers.LayerDesc;
import circuit.model.DrawingModel;

// TODO: specify the license and comment public methods
public class LayerModel
{
    private final DrawingModel DrawingModel;

    public LayerModel(DrawingModel DrawingModel)
    {
        this.DrawingModel = DrawingModel;
    }

    public Vector<LayerDesc> getAllLayers()
    {
        return DrawingModel.getLayers();
    }
}
