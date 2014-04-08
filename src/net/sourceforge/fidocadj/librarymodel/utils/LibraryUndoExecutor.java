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
// along with FidoCadJ.  If not, see <http://www.gnu.org/licenses/>.
// 
// Copyright 2014 Kohta Ozaki

package net.sourceforge.fidocadj.librarymodel.utils;

import java.io.*;

import globals.FileUtils;
import globals.LibUtils;
import undo.LibraryUndoListener;

import net.sourceforge.fidocadj.librarymodel.LibraryModel;
import net.sourceforge.fidocadj.FidoFrame;

public class LibraryUndoExecutor implements LibraryUndoListener
{
	FidoFrame fidoFrame;
	LibraryModel libraryModel;
	
	public LibraryUndoExecutor(FidoFrame frame, LibraryModel model)
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

