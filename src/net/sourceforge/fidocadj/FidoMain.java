package net.sourceforge.fidocadj;

import javax.swing.*;

import java.util.prefs.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.lang.reflect.*;

import net.sourceforge.fidocadj.circuit.*;
import net.sourceforge.fidocadj.circuit.controllers.*;
import net.sourceforge.fidocadj.circuit.model.*;
import net.sourceforge.fidocadj.export.*;
import net.sourceforge.fidocadj.geom.*;
import net.sourceforge.fidocadj.globals.*;
import net.sourceforge.fidocadj.layers.*;
import net.sourceforge.fidocadj.timer.*;
import net.sourceforge.fidocadj.graphic.*;

/** FidoMain.java 
	SWING App: The starting point of FidoCadJ.

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

    Copyright 2008-2014 by Davide Bucci
</pre>

    
    @author Davide Bucci
*/

public class FidoMain 
{   
    private static CommandLineParser clp;
    
    /** Standard constructor.
    */
	public FidoMain() 
	{
		// Nothing to do here. The entry point is the main method and all
		// important settings here are static.
	}

    /** The main method. Process the command line options and if necessary
    	shows an instance of FidoFrame.
    */
    public static void main(String... args)
    {
    	clp = new CommandLineParser();
    	
        if (args.length>=1) 
        	clp.processArguments(args);
        
        if(!clp.getStripOptimization() && 
        	System.getProperty("os.name").startsWith("Mac")) {
        	// CAREFUL**************************************************
        	// In all MacOSX systems I tried, this greatly increases the
        	// redrawing speed. *HOWEVER* the default value for Java 1.6
        	// as distributed by Apple is "false" (whereas it was "true"
        	// for Java 1.5).  This might mean that in a future this can
        	// be not very useful, or worse slowdown the performances.
        	// CAREFUL**************************************************
        	// NOTE: this does not seem to have any effect!
			System.setProperty("apple.awt.graphics.UseQuartz", "true");
		}
		
		/* if(!stripOptimization &&
        	System.getProperty("os.name").toLowerCase().startsWith("linux")) {
        	// CAREFUL**************************************************
			// Various sources  reports that  this option  will increase
			// the redrawing speed using Linux. It might happen, however
			// that the  performances  can be somewhat  degraded in some 
			// systems.
			// CAREFUL**************************************************
			// We tested that in version 0.24.1. In fact, activating this 
			// option renders the software inusable in some systems (Nvidia
			// graphic software?)
           	// System.setProperty("sun.java2d.opengl", "true");
           	// See for example this discussion: http://tinyurl.com/axoxqcb
        }   */
        // Now we proceed with all the operations: opening files, converting...
        if(clp.getHeadlessMode()) {
        	// Creates a circuit object
        	DrawingModel P = new DrawingModel();
        	
        	if("".equals(clp.getLoadFileName())) {
        		System.err.println("You should specify a FidoCadJ file to"+
        		"read");
        		System.exit(1);
        	}
        	
            // Reads the standard libraries
        	readLibrariesProbeDirectory(P, false, clp.getLibDirectory());
        	
        	StringBuffer txt=new StringBuffer();    
			MyTimer mt = new MyTimer();
			try {
        		// Read the input file.
        		FileReader input = new FileReader(clp.getLoadFileName());
        		BufferedReader bufRead = new BufferedReader(input);
                
        		String line="";
        		txt = new StringBuffer(bufRead.readLine());
                        
        		txt.append("\n");
                        
        		while (line != null){
            		line =bufRead.readLine();
            		txt.append(line);
            		txt.append("\n");
        		}
            
        		bufRead.close();
				
        		P.setLayers(StandardLayers.createStandardLayers());
                        
      			// Here txt contains the new circuit: parse it!
				ParserActions pa=new ParserActions(P);
      			pa.parseString(new StringBuffer(txt.toString()));       
	 	
            } catch(IllegalArgumentException iae) {
                System.err.println("Illegal filename");
            } catch(Exception e) {
            	System.err.println("Unable to export: "+e);
            }
               
        	if (clp.shouldConvertFile()) {
        		// We check if the output file has a correct
            	// extension, coherent with the file format chosen.
            	if(!Globals.checkExtension(clp.getOutputFile(), 
            		clp.getExportFormat()) && !clp.getForceMode()) {
               		System.err.println(
               			"File extension is not coherent with the "+
               			"export output format! Use -f to skip this test.");
               		System.exit(1);
            	}
        		try {
        			if (clp.getResolutionBasedExport()) {
        				ExportGraphic.export(new File(clp.getOutputFile()),  P, 
                    		clp.getExportFormat(), clp.getResolution(),
                    		true,false,true, true);
        			} else {
                		ExportGraphic.exportSize(new File(clp.getOutputFile()),
                			P, clp.getExportFormat(), 
                			clp.getXSize(), clp.getYSize(), 
                    		true,false,true,true);
                	}
                	System.out.println("Export completed");
            	} catch(IOException ioe) {
                	System.err.println("Export error: "+ioe);
                }
            }
            
            if (clp.getHasToPrintSize()) {
            	PointG o=new PointG(0,0);
            	DimensionG d = DrawingSize.getImageSize(P,1, true, o);
				System.out.println(""+d.width+" "+d.height);	
            }
            
            if (clp.getHasToPrintTime()) {
            	System.out.println("Elapsed time: "+mt.getElapsed()+" ms.");
            }
        }

        if (!clp.getCommandLineOnly()) {
        	SwingUtilities.invokeLater(new CreateSwingInterface(
        		clp.getLibDirectory(), 
        		clp.getLoadFileName(), clp.getWantedLocale()));
        }
    }
    

	/** Read the libraries, eventually by inspecting the directory specified
		by the user. There are three standard directories: IHRAM.FCL, 
		FCDstdlib.fcl and PCB.fcl. If those files are found in the external 
		directory specified, the internal version is not loaded. Other files
		on the external directory are loaded.
		
		@param P the parsing class in which the libraries should be loaded
		@param englishLibraries a flag to specify if the internal libraries 
			should be loaded in English or in Italian.
		@param libDirectoryO the path of the external directory.

	*/
	public static void readLibrariesProbeDirectory(DrawingModel P, 
		boolean englishLibraries, String libDirectoryO)
	{
		String libDirectory=libDirectoryO;
		ParserActions pa = new ParserActions(P);
		
		synchronized(P) {
		if (libDirectory == null || libDirectory.length()<3) 
			libDirectory = System.getProperty("user.home");	
			
		pa.loadLibraryDirectory(libDirectory);
	    if (new File(Globals.createCompleteFileName(
	    	libDirectory,"IHRAM.FCL")).exists()) {
            System.out.println("IHRAM library got from external file");
        } else {
        	if(englishLibraries)
                pa.loadLibraryInJar(FidoFrame.class.getResource(
                	"lib/IHRAM_en.FCL"), "ihram");
            else
                pa.loadLibraryInJar(FidoFrame.class.getResource(
                	"lib/IHRAM.FCL"), "ihram");
        }
            
       	if (new File(Globals.createCompleteFileName(
       		libDirectory,"FCDstdlib.fcl")).exists()) {
           	System.out.println("Standard library got from external file");
        } else {        
       	  	if(englishLibraries)
           		pa.loadLibraryInJar(FidoFrame.class.getResource(
           			"lib/FCDstdlib_en.fcl"), "");
           	else
               	pa.loadLibraryInJar(FidoFrame.class.getResource(
               		"lib/FCDstdlib.fcl"), "");
        }
        
        if (new File(Globals.createCompleteFileName(
        	libDirectory,"PCB.fcl")).exists()) {
           	System.out.println("Standard PCB library got from external file");
        } else {
        	if(englishLibraries)
               	pa.loadLibraryInJar(FidoFrame.class.getResource(
               		"lib/PCB_en.fcl"), "pcb");
           	else
               	pa.loadLibraryInJar(FidoFrame.class.getResource(
               		"lib/PCB.fcl"), "pcb");
    	}
    	if (new File(Globals.createCompleteFileName(
        	libDirectory,"EY_Libraries.fcl")).exists()) {
           	System.out.println("Standard EY_Libraries got from external file");
        } else {
        	if(englishLibraries)
               	pa.loadLibraryInJar(FidoFrame.class.getResource(
               		"lib/EY_Libraries.fcl"), "EY_Libraries");
           	else
               	pa.loadLibraryInJar(FidoFrame.class.getResource(
               		"lib/EY_Libraries.fcl"), "EY_Libraries");
    	}
        if (new File(Globals.createCompleteFileName(
        	libDirectory,"elettrotecnica.fcl")).exists()) {
           	System.out.println(
           		"Electrotechnics library got from external file");   
        } else {
        	if(englishLibraries)
               	pa.loadLibraryInJar(FidoFrame.class.getResource(
               		"lib/elettrotecnica_en.fcl"), "elettrotecnica");
           	else
               	pa.loadLibraryInJar(FidoFrame.class.getResource(
               		"lib/elettrotecnica.fcl"), "elettrotecnica");
    	}
	}
	}
}


/** Creates the Swing elements needed for the interface.
*/
class CreateSwingInterface implements Runnable {

	String libDirectory;
	String loadFile;
	Locale currentLocale;
	
	/** Constructor where we specify some details concerning the library
		directory, the file to load (if needed) as well as the locale.
		@param ld the library directory
		@param lf the file to load
		@param ll the locale.
	*/
	public CreateSwingInterface (String ld, String lf, Locale ll)
	{
		libDirectory = ld;
		loadFile = lf;
		currentLocale =ll;
	}

	/** Standard constructor.
	
	*/
	public CreateSwingInterface ()
	{
		libDirectory = "";
		loadFile = "";
	}
	
	/** Run the thread.
	*/
	public void run() 
	{
	    /*******************************************************************
	        PLATFORM SELECTION AND CONFIGURATION CODE GOES IN THIS SECTION
	    *******************************************************************/
	    if (System.getProperty("os.name").startsWith("Mac")) {
            Globals g=new Globals();
        
	        Preferences prefs_static = 
	            Preferences.userNodeForPackage(g.getClass());
            
    	    Globals.quaquaActive = prefs_static.get("QUAQUA", 
                "true").equals("true");
        
           	Globals.weAreOnAMac =true;
           	
	        // These settings allows to obtain menus on the right place
    	    System.setProperty("com.apple.macos.useScreenMenuBar","true");
            // This is for JVM < 1.5 It won't harm on higher versions.
         	System.setProperty("apple.laf.useScreenMenuBar","true"); 
            // This is for having the good application name in the menu
            System.setProperty(
            	"com.apple.mrj.application.apple.menu.about.name", 
            	"FidoCadJ");
            
	        try { 
                //Globals.quaquaActive=true;
        	    //System.setProperty("Quaqua.Debug.showVisualBounds","true");
                //System.setProperty("Quaqua.Debug.showClipBounds","true");
               	if(Globals.quaquaActive) { 
                   	UIManager.setLookAndFeel(
                       	"ch.randelshofer.quaqua.QuaquaLookAndFeel");
                
	                System.out.println("Quaqua look and feel active");
 	           	}
                
	        } catch (Exception e) {
	            // Quaqua is not active. Just continue!
            
    	        System.out.println(
    	         	"The Quaqua look and feel is not available");
        	    System.out.println(
        	       	"I will continue with the basic Apple l&f");
	        }
	    } else if (System.getProperty("os.name").startsWith("Win")) {
	        /* If the host system is a window system, select the Windows
    	       look and feel. This is a way to encourage people to use 
               FidoCadJ even on a Windows system, forgotting about Java. 
           	*/
	        try {
	            UIManager.setLookAndFeel(
	              	"com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
 	        } catch (Exception E) {
 	        	System.out.println("Could not load the Windows Look and feel!");
 	        }
           	Globals.quaquaActive=false;
        } else {
           	Globals.quaquaActive=false;
        }
        
        // Un-comment to try to use the Metal LAF
        
    	/*
        try {
           	UIManager.setLookAndFeel(
           	UIManager.getCrossPlatformLookAndFeelClassName());
           	Globals.weAreOnAMac =false;
        } catch (Exception E) {}
        */
        /*******************************************************************
    	                END OF THE PLATFORM SELECTION CODE
        *******************************************************************/
        
        if(Globals.weAreOnAMac) {
           	// Here we use the reflection provided by Java to understand
           	// if the AppleSpecific class is available on the system.
           	// This class should be compiled separately from the main 
           	// program since the compilation can be successful only on
           	// a MacOSX system.
          
           	try {
           		Class<?> a = Class.forName(
           			"net.sourceforge.fidocadj.AppleSpecific");
           		Object b = a.newInstance();
           		Method m = a.getMethod("answerFinder");
				m.invoke(b);
           	
           	} catch (Exception exc) {
     			Globals.weAreOnAMac = false;
     			System.out.println("It seems that this software has been "+
     				"compiled on a system different from MacOSX. Some nice "+
     				"integrations with MacOSX will therefore be absent. If "+
     				"you have compiled on MacOSX, make sure you used the "+
     				"'compile' or 'rebuild' script along with the 'mac' "+
     				"option.");
    		}
        }

        // Here we create the main window object
        
	    FidoFrame popFrame=new FidoFrame(true, currentLocale);
        
        if (!"".equals(libDirectory)) {
			popFrame.libDirectory = libDirectory;
        }

        popFrame.init();

		// We begin by showing immediately the window. This improves the
		// perception of speed given to the user, since the libraries 
		// are not yet loaded
        popFrame.setVisible(true); 

		// We load the libraries (this does not take so long in modern
		// systems).
		popFrame.loadLibraries();
        // If a file should be loaded, load it now, since popFrame has been
        // created and initialized.
        if(!"".equals(loadFile))
			popFrame.load(loadFile);
				
		// We force a global validation of the window size, by including 
		// this time the tree containing the various libraries and the
		// macros.
        popFrame.setVisible(true);
	}
}