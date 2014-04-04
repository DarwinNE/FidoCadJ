package net.sourceforge.fidocadj.librarymodel;

import java.util.*;
import primitives.MacroDesc;

public class Category
{
    String name;
    Library parentLibrary;
    List<MacroDesc> macros;
    boolean isHidden;

    Category(String name,Library parentLibrary,boolean isHidden)
    {
        macros = new ArrayList<MacroDesc>();
        this.name = name;
        this.parentLibrary = parentLibrary;
        this.isHidden = isHidden;
    }

    public Library getParentLibrary()
    {
        return parentLibrary;
    }

    public void setParentLibrary(Library parentLibrary)
    {
        this.parentLibrary = parentLibrary;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void addMacro(MacroDesc macroDesc)
    {
        macros.add(macroDesc);
    }

    public void removeMacro(MacroDesc macroDesc)
    {
        macros.remove(macroDesc);
    }

    public List<MacroDesc> getAllMacros()
    {
        return macros;
    }

    public boolean isHidden()
    {
        return isHidden;
    }

    public static boolean isValidName(String name)
    {
        return true;
    }

    public boolean containsMacroKey(String key)
    {
        if(key==null) {
            return true;
        }

        for(MacroDesc macro:macros) {
            if(LibraryModel.getPlainMacroKey(macro).equals(key)) {
                return true;
            }
        }
        return false;
    }

}
