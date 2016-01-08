package net.sourceforge.fidocadj.storage;

import net.sourceforge.fidocadj.*;

public class StaticStorage
{
    private static FidoEditor currentEditor;

    public static void setCurrentEditor(FidoEditor f)
    {
        currentEditor = f;
    }

    public static FidoEditor getCurrentEditor()
    {
        return currentEditor;
    }
}