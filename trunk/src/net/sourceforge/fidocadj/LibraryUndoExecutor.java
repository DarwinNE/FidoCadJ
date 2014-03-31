package net.sourceforge.fidocadj;

import java.io.*;

import globals.FileUtils;
import globals.LibUtils;
import undo.LibraryUndoListener;
import undo.LibraryUndoListener;

public class LibraryUndoExecutor implements LibraryUndoListener
{
	FidoFrame fidoFrame;
	LibraryModel libraryModel;
	
	LibraryUndoExecutor(FidoFrame frame, LibraryModel model)
	{
		fidoFrame = frame;
		libraryModel = model;
	}
	
	public void undoLibrary(String s){
        try {
        	File sourceDir = new File(s);
        	String d=LibUtils.getLibDir();
        	File destinationDir = new File(d);
        	//System.out.println("undo: copy from "+s+" to "+d);
            FileUtils.copyDirectory(sourceDir, destinationDir);
            fidoFrame.loadLibraries();
            libraryModel.forceUpdate();
        } catch (IOException e) {
            System.out.println("Cannot restore library directory contents.");
        }
    }
}
