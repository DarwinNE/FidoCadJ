package undo;

import java.util.*;


public class UndoState {
	
	public String text;
	public boolean isModified;
	public String fileName;
	
	public UndoState()
	{
		text="";
		isModified=false;
		fileName="";
	}

	public String toString() {
		return text;
	}
}