package net.sourceforge.fidocadj;

import javax.swing.*;
import java.util.prefs.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.lang.reflect.*;

import globals.*;
import circuit.*;
import circuit.controllers.*;
import circuit.model.*;
import export.*;
import timer.*;
import graphic.*;
import layers.*;

/** FidoMain.java 

	The starting point of FidoCadJ.

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

    // See if there is a filename to open or an option to take into 
    // account
	private static String loadFile="";
    private static String libDirectory="";
    private static Locale currentLocale=null;
           	
	// If this is true, the GUI will not be loaded and FidoCadJ will run as
    // a command line utility:
    private static boolean commandLineOnly = false;
       	
    // The standard behavior implies that FidoCadJ tries to activate some
    // optimizations or settings which depends on the platform and should
    // increase things such as the redrawing speed and other stuff. In some
    // cases ("-p" option) they might be deactivated:
    private static boolean stripOptimization=false;
        
    // The following variable will be true if one requests to convert a 
    // file:
    private static boolean convertFile = false;
    private static int totx=0, toty=0;
    private static String exportFormat="";
    private static String outputFile="";
    private static boolean headlessMode = false;
    private static boolean resolutionBasedExport = false;	
    private static boolean printSize=false;
    private static boolean printTime=false;
    private static double resolution=1;               
    
	public FidoMain() 
	{
	}

    /** The main method. Process the command line options and if necessary
    	shows an instance of FidoFrame.
    */
    public static void main(String[] args)
    {	
        if (args.length>=1) 
        	processArguments(args);
        
        if(!stripOptimization && 
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
		
/*		if(!stripOptimization &&
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
        if(headlessMode) {
        	// Creates a circuit object
        	DrawingModel P = new DrawingModel();
        	
        	if("".equals(loadFile)) {
        		System.err.println("You should specify a FidoCadJ file to"+
        		"read");
        		System.exit(1);
        	}
        	
            // Reads the standard librarie
        	readLibrariesProbeDirectory(P, false, libDirectory);
        	
        	StringBuffer txt=new StringBuffer();    
			MyTimer mt = new MyTimer();
			try {
        		// Read the input file.
        		FileReader input = new FileReader(loadFile);
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
               
        	if (convertFile) {
        		try {
        			if (resolutionBasedExport) {
        				ExportGraphic.export(new File(outputFile),  P, 
                    		exportFormat, resolution,true,false,true, true);
        			} else {
                		ExportGraphic.exportSize(new File(outputFile),  P, 
                    		exportFormat, totx, toty, 
                    		true,false,true,true);
                	}
                	System.out.println("Export completed");
            	} catch(IOException ioe) {
                	System.err.println("Export error: "+ioe);
                }
            }
            
            if (printSize) {
            	PointG o=new PointG(0,0);
            	DimensionG d = ExportGraphic.getImageSize(P,1, true, o);
				System.out.println(""+d.width+" "+d.height);	
            }
            
            if (printTime) {
            	System.out.println("Elapsed time: "+mt.getElapsed()+" ms.");
            }
        }

        if (!commandLineOnly) {
        	SwingUtilities.invokeLater(new CreateSwingInterface(libDirectory, 
        		loadFile, currentLocale));
        }
    }
    
    static private void processArguments(String[] orArgs)
    {
       	int i;
       	boolean loaded=false;
       	boolean nextLib=false;
   		String[] args=orArgs;
	
        
       	// called by jnlp        	
       	if (args[0].equalsIgnoreCase("-print")) {
       		// TODO: verify if this can happen in a operating system
       		// with case sensitive file system! Windows only code?	
       		String filename = args[1].toLowerCase().replace(".fcd", "");  
       		if (filename.lastIndexOf(
       			System.getProperty("file.separator"))>0)
       			filename = 
       				filename.substring(filename.lastIndexOf(
       				System.getProperty("file.separator"))+1);
       		args = ("-n -c r72 pdf "+filename+".pdf " // NOPMD
            	+args[1]).split(" "); // NOPMD  suppressed PMD false positive    		
       	}

       	for(i=0; i<args.length; ++i) {
       		if (args[i].startsWith("-")) {
       			// It is an option
       			
       			// phylum        			
       			if (args[i].trim().equalsIgnoreCase("-open")) {        				
       				continue;
       			}
       			       			   
       			if (args[i].startsWith("-n")) {
       				// -n indicates that FidoCadJ should run only with
       				// the command line interface, without showing any
       				// GUI.
       				commandLineOnly=true;
       				System.setProperty("java.awt.headless", "true");
       			} else if (args[i].startsWith("-d")) {
       				// -d indicates that the following argument is the path
       				// of the library directory. The previous library 
       				// directory will be ignored.
       				nextLib=true;
       			} else if (args[i].startsWith("-c")) {
       				// -c indicates that FidoCadJ should read and convert
       				// the given file. The structure of the command must 
       				// be as follows:
       				// -c 800 600 png test.png
       				// which is the total width and height in pixel, the 
       				// format required (SVG, EPS, PGF, PNG, PDF, EPS, SCH)
       				// and the file name to be used.
       				// The second possibility is that the -c option is 
       				// followed by the r option, followed by a number
       				// specifying the number of pixels for logical units.
       				
       				try {
       					if (args[++i].startsWith("r")) {
       						resolution = Double.parseDouble(
       							args[i].substring(1));
       						resolutionBasedExport = true;
       						if (resolution<=0) {
       							System.err.println("Resolution should be"+
       								"a positive real number");
       							System.exit(1);
       						}
       					} else {
       						totx=Integer.parseInt(args[i]);
       						toty=Integer.parseInt(args[++i]);
       					}
       					exportFormat=args[++i];
       					outputFile=args[++i];
       					convertFile=true;
       					headlessMode = true;
        					
       				} catch (Exception E) {
       					System.err.println("Unable to read the parameters"+
       						" given to -c");
       					System.exit(1);
       				}
       				
       				convertFile=true;
       			} else if (args[i].startsWith("-h")) { // Zu Hilfe!
       				showCommandLineHelp();
       				System.exit(0);
       			} else if (args[i].startsWith("-s")) { // Get size
       				headlessMode = true;
       				printSize=true;
       			} else if (args[i].startsWith("-t")) { // Timer
       				printTime=true;
       			} else if (args[i].startsWith("-p")) { // No optimizations
       				stripOptimization=true;        			
       			} else if (args[i].startsWith("-l")) { // Locale
       				// Extract the code corresponding to the wanted locale
       				String loc;
       				if(args[i].length()==2) {
       					// In this case, the -l xx form is used, where
       					// xx indicates the wanted locale. A space 
       					// separates "-l" from the wanted locale.
        					
       					// At first, check if the user forgot the locale
       					if(i==args.length-1 || args[i+1].startsWith("-")) {
       						System.err.println("-l option requires a "+
       							"locale language code.");
       						System.exit(1);
       					}
 						loc=args[++i];       					
       				} else {
       					// In this case, the -lxx form is used, where
       					// xx indicates the wanted locale. No space is
       					// used.
       					loc=args[i].substring(2);
       				}
       				currentLocale=new Locale(loc);
 
       			} else {
       				System.err.println("Unrecognized option: "+args[i]);
       				showCommandLineHelp();
       				System.exit(1);
       			}
       		} else {
       			// We should process now the arguments of the different 
       			// options (if it applies).
       			if (nextLib) {
       				// This is -d: read the new library directory
       				libDirectory= args[i];
       				System.out.println("Changed the library directory: "
       					+args[i]);
       			} else {
       				if (loaded) {
       					System.err.println("Only one file can be"+
       						" specified in the command line");
       				}
       				// We can not load the file now, since the main frame 
       				// has not been initialized yet.
       				loadFile=args[i];
       				loaded=true;
       			}
       			nextLib=false;
       		}
       	}
    }
    
    /** Print a short summary of each option available for launching
    	FidoCadJ.
    */
    static private void  showCommandLineHelp()
    {
    	// Here, exceptionally, the lenght of the code lines might exceed
    	// 80 characters.
    
    	String help = "\nThis is FidoCadJ, version "+Globals.version+".\n"+
    	    "By Davide Bucci, 2007-2013.\n\n"+
    	    
    		"Use: java -jar fidocadj.jar [-options] [file] \n"+
    		"where options include:\n\n"+
    		
    		" -n     Do not start the graphical user interface (headless mode)\n\n"+
    		
    		" -d     Set the extern library directory\n"+
    		"        Usage: -d dir\n"+
    		"        where 'dir' is the path of the directory you want to use.\n\n"+
    		    		 
    		" -c     Convert the given file to a graphical format.\n"+
    		"        Usage: -c sx sy eps|pdf|svg|png|jpg|fcd|sch outfile\n"+
    		"        If you use this command line option, you *must* specify a FidoCadJ\n"+
    		"        file to convert.\n"+
    		"        An alternative is to specify the resolution in pixels per logical unit\n"+
    		"        by preceding it by the letter 'r' (without spaces), instead of giving\n"+
    		"        sx and sy.\n\n"+
    		
    		" -s     Print the size  of the specified file in logical coordinates.\n\n"+
    		
    		" -h     Print this help and exit.\n\n"+
    		
    		" -t     Print the time used by FidoCadJ for the specified operation.\n\n"+
    		
    		" -p     Do not activate some platform-dependent optimizations. You might try\n"+
    		"        this option if FidoCadJ hangs or is painfully slow.\n\n"+
    		
    		" -l     Force FidoCadJ to use a certain locale (the code might follow\n"+
    		"        immediately or be separated by an optional space).\n\n"+
    		
    		" [file] The optional (except if you use the -d or -s options) FidoCadJ file to\n"+
    		"        load at startup time.\n\n"+
    		
    		"Example: load and convert a FidoCadJ drawing to a 800x600 pixel png file\n"+
    		"        without using the GUI.\n"+
    		"  java -jar fidocadj.jar -n -c 800 600 png out1.png test1.fcd\n\n"+
    		"Example: load and convert a FidoCadJ drawing to a png file without using the\n"+ 
    		"        graphic user interface (the so called headless mode).\n"+
    		"        Each FidoCadJ logical unit will be converted in 2 pixels on the image.\n"+
    		"  java -jar fidocadj.jar -n -c r2 png out2.png test2.fcd\n\n"+
    		"Example: load FidoCadJ forcing the locale to simplified chinese (zh).\n"+
    		"  java -jar fidocadj.jar -l zh\n\n";
    		
    		
    	System.out.println(help);
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