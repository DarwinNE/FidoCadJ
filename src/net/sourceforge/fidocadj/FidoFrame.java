package net.sourceforge.fidocadj;

import java.awt.*;
import java.awt.List;
import java.awt.event.*;


import javax.swing.*;
import javax.swing.event.*;

import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.security.CodeSource;
import java.util.*;
import java.net.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.prefs.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.sourceforge.fidocadj.FidoMain;
import net.sourceforge.fidocadj.dialogs.*;
import net.sourceforge.fidocadj.export.*;
import net.sourceforge.fidocadj.geom.*;
import net.sourceforge.fidocadj.globals.*;
import net.sourceforge.fidocadj.graphic.*;
import net.sourceforge.fidocadj.graphic.swing.*;
import net.sourceforge.fidocadj.circuit.*;
import net.sourceforge.fidocadj.circuit.controllers.*;
import net.sourceforge.fidocadj.toolbars.*;
import net.sourceforge.fidocadj.timer.*;
import net.sourceforge.fidocadj.macropicker.MacroTree;
import net.sourceforge.fidocadj.librarymodel.LibraryModel;
import net.sourceforge.fidocadj.layermodel.LayerModel;
import net.sourceforge.fidocadj.layers.*;
import net.sourceforge.fidocadj.librarymodel.utils.CircuitPanelUpdater;
import net.sourceforge.fidocadj.librarymodel.utils.LibraryUndoExecutor;

/** FidoFrame.java 

The class describing the main frame in which FidoCadJ runs.

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

    Copyright 2008-2015 by Davide Bucci
</pre>

    The FidoFrame class describes a frame which is used to trace schematics
    and printed circuit boards.
    
    @author Davide Bucci
*/

public class FidoFrame extends JFrame implements 
                                            ActionListener,
                                            ZoomToFitListener,
                                            HasChangedListener,
                                            WindowFocusListener
{
    // Interface elements parts of FidoFrame
    
    // The circuit panel...
    public CircuitPanel CC;
    // ... which is contained in a scroll pane.
    private JScrollPane SC;
    
    // Macro library model
    private LibraryModel libraryModel;
 
 	// Objects which regroup a certain number of actions somewhat related
 	// to the FidoFrame object in different domains.
	final private ExportTools et;
	final private PrintTools pt;
	final private MenuTools mt;
	final private DragDropTools dt;
	final private FileTools ft;
	
    // Libraries properties
    public String libDirectory;
    public Preferences prefs;
    
    // Toolbar properties
    // The toolbar dedicated to the available tools (the first one under 
    // thewindow title).
	private ToolbarTools toolBar;
    
    // Text description under icons
    private boolean textToolbar;
    // Small (16x16 pixel) icons instead of standard (32x32 pixel)
    private boolean smallIconsToolbar;

    // Show macro origin (menu item).
    private JCheckBoxMenuItem optionMacroOrigin;
    
    // Locale settings
    public Locale currentLocale;
    // Runs as an application or an applet.
    public boolean runsAsApplication;
    
    // Useful for automatic scrolling in panning mode.
    private ScrollGestureRecognizer sgr;
    
    /** The standard constructor: create the frame elements and set up all
        variables. Note that the constructor itself is not sufficient for
        using the frame. You need to call the init procedure after you have
        set the configuration variables available for FidoFrame.
        
        @param appl should be true if FidoCadJ is run as a standalone 
        	application or false if it is run as an applet. In this case, some
        	local settings are not accessed because they would raise an
        	exception.
        @param loc the locale which should be used. If it is null, the current
        	locale is automatically determined and FidoCadJ will try to use
        	it for its user interface.
    */
    public FidoFrame (boolean appl, Locale loc)
    {
        super("FidoCadJ "+Globals.version);
		runsAsApplication = appl;
		
		registerLocale(loc);
		
        // Those lines allow a better Cocoa-like integration
        // under Leopard. Is it overridden by the use of the Quaqua L&F?
        // No! It is actually needed to make all the window movable when 
        // clicking in the toolbar.
        
        getRootPane().putClientProperty("apple.awt.brushMetalLook", 	
        	Boolean.TRUE);

		prepareLanguageResources();
		configureInterfaceDetailsFromPlatform();
		
        DialogUtil.center(this, .75,.75,800,500);
        setDefaultCloseOperation(
            javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE); 

		// We need to keep track of the number of open windows. If the last
		// one is closed, we exit from the program.
		
        ++Globals.openWindowsNumber;
        Globals.openWindows.add(this);

        setIconForApplication();
                
        if (runsAsApplication) {
        	// Prepare the preferences associated to the FidoMain class
        	FidoMain fm=new FidoMain();
            prefs = Preferences.userNodeForPackage(fm.getClass());
        } else {
        	// If we can not access to the preferences, we inizialize those
        	// configuration variables with default values.
        	libDirectory = System.getProperty("user.home");
        	smallIconsToolbar = true;
        	textToolbar = true;
        	prefs=null;
        }
        et = new ExportTools(prefs);
        pt = new PrintTools();
        mt = new MenuTools();
        dt = new DragDropTools(this);
        ft = new FileTools(this, prefs);
        
       	readPreferences();
    }
    
    /** Retrieve the program icon and associate it to the window.
    */
    private void setIconForApplication()
    {
		URL url=DialogAbout.class.getResource(
           "icona_fidocadj_128x128.png");
        
        if (url == null) {
        	System.err.println("Could not retrieve the FidoCadJ icon!");
        } else {
        	Image icon = Toolkit.getDefaultToolkit().getImage(url);
            setIconImage(icon);
        }
    }
    
    /** Determine what is the current platform and configures some interface
    	details such as the key to be used for shortcuts (Command/Meta for Mac
    	and Ctrl for Linux and Windows).
    */
    private void configureInterfaceDetailsFromPlatform()
    {
        Globals.useNativeFileDialogs=false;
        Globals.useMetaForMultipleSelection=false;
        
        if (System.getProperty("os.name").startsWith("Mac")) {
            // From what I know, only Mac users expect to use the Command (meta)
            // key for shortcuts, while others will use Control.
            Globals.shortcutKey=InputEvent.META_MASK;
            Globals.useMetaForMultipleSelection=true;
            
            // Standard dialogs are vastly better on MacOSX than the Swing ones
            Globals.useNativeFileDialogs=true;
            // The order of the OK and Cancel buttons differs in Windows and
            // MacOSX. How about the most common Window Managers in Linux?
            Globals.okCancelWinOrder = false;

        } else {
        	// This solves the bug #3076513
            Globals.okCancelWinOrder = true;
            Globals.shortcutKey=InputEvent.CTRL_MASK;
        }
    }
    
    /** Obtain the language resources associated to the current locale.
    	If the current locale is available, then load the appropriate 
    	LanguageResources file. If the current locale is not found, the
    	en-US language resources file is employed, thus showing an interface
    	in American English.
    */
    private void prepareLanguageResources()
    {
        try {
            // Try to load the messages resources with the current locale
            Globals.messages = new 
            	AccessResources (Utf8ResourceBundle.getBundle("MessagesBundle", 
               currentLocale));                             
        } catch(MissingResourceException mre) {
            try {
                // If it does not work, try to use the standard English
                Globals.messages = new 
            	AccessResources (ResourceBundle.getBundle("MessagesBundle",
                    new Locale("en", "US")));
                System.out.println("No locale available, sorry... "+
                	"interface will be in English");
            } catch(MissingResourceException mre1) {
                // Give up!!!
                JOptionPane.showMessageDialog(null,
                    "Unable to find any language localization files: " + mre1);
                System.exit(1);
            }
        }        
    }
    
    /** Check if a locale has been specified. If not, get the operating
    	system's locale and employ this as the current locale.
    	@param loc the desired locale, or null if the system one has to be
    		employed.
    */
    private void registerLocale(Locale loc)
    {
    	String systemLanguage = Locale.getDefault().getLanguage();
		
		if(loc==null) {
			// Make sort that only the language is used for the current 
        	currentLocale = new Locale(systemLanguage);
        } else {
        	currentLocale = loc;
        	System.out.println("Forced the locale to be: " +loc+ 
        		" instead of: "+systemLanguage);
		}
	}    
    
    /** Get the locale employed by this instance.
   		@return the current locale.
   	*/
   	public Locale getLocale()
   	{
   		return currentLocale;
   	}
    
    /** Get the ExportTools object (containing the code related to interface
    	for exporting files).
    	@return the ExportTools object.
    */
	public ExportTools getExportTools()
	{
		return et;
	}
	
	/** Get the PrintTools object (containing the code related to interface
    	for printing drawings).
    	@return the PrintTools object.
    */
	public PrintTools getPrintTools()
	{
		return pt;
	}
	
	/** Get the FileTools object (containing the code related to interface
    	for loading and saving drawings).
    	@return the FileTools object.
    */
	public FileTools getFileTools()
	{
		return ft;
	}
    
    /** Read the preferences settings (mainly at startup or when a new 
    	editing window is created.
    	If no preferences settings are accessible, does nothing.
    */
    public final void readPreferences()
    {
    	if(prefs==null)
    		return;
		// The library directory
       	libDirectory = prefs.get("DIR_LIBS", "");
        
        // The icon size
        String defaultSize="";
        
        // Check the screen resolution. Now (April 2015), a lot of very high
        // resolution screens begin to be widespread. So, if the pixel 
        // density is greater than 150 dpi, bigger icons are used by at the
        // very first time FidoCadJ is run.
        if(java.awt.Toolkit.getDefaultToolkit().getScreenResolution()>150) {
        	defaultSize="false";
        } else {
        	defaultSize="true";
        }
        
        smallIconsToolbar = prefs.get("SMALL_ICON_TOOLBAR", 
            	defaultSize).equals("true");
        // Presence of the text description in the toolbar
        textToolbar = prefs.get("TEXT_TOOLBAR", "true").equals("true");
    
        // Export preferences
        et.readPrefs();
		
		// Element sizes
       	Globals.lineWidth=Double.parseDouble(
       	 		prefs.get("STROKE_SIZE_STRAIGHT", "0.5"));
		Globals.lineWidthCircles=Double.parseDouble(
				prefs.get("STROKE_SIZE_OVAL", "0.35"));		
		Globals.diameterConnection=Double.parseDouble(
				prefs.get("CONNECTION_SIZE", "2.0"));	

    }

	/* Load the saved configuration for the grid.
    */
	public void readGridSettings()
	{
		CC.getMapCoordinates().setXGridStep(Integer.parseInt(
			prefs.get("GRID_SIZE", "5"))); 
        CC.getMapCoordinates().setYGridStep(Integer.parseInt(
        	prefs.get("GRID_SIZE", "5"))); 
	}
    
    /* Load the saved configuration for the drawing primitives.
    */
	public void readDrawingSettings()
	{
		CopyPasteActions cpa = CC.getCopyPasteActions();
		
 		// Shift elements when copy/pasting them
        cpa.setShiftCopyPaste(prefs.get("SHIFT_CP", 
        		"true").equals("true"));
		
		ElementsEdtActions eea = CC.getContinuosMoveActions();
		
		// Default PCB sizes (pad/line)
 		eea.PCB_pad_sizex = Integer.parseInt(prefs.get("PCB_pad_sizex", "10"));
 		eea.PCB_pad_sizey = Integer.parseInt(prefs.get("PCB_pad_sizey", "10"));
 		eea.PCB_pad_style = Integer.parseInt(prefs.get("PCB_pad_style", "0"));
 		eea.PCB_pad_drill = Integer.parseInt(prefs.get("PCB_pad_drill", "5"));
 		eea.PCB_thickness = Integer.parseInt(prefs.get("PCB_thickness", "5"));	
    }
    
    /* Load the standard libraries according to the locale.
    */
    public void loadLibraries()
    {
    	// Check if we are using the english libraries. Basically, since the 
        // only other language available other than english is italian, I
        // suppose that people are less uncomfortable with the current Internet
        // standard...
        
        boolean englishLibraries = !currentLocale.getLanguage().equals(new 
        	Locale("it", "", "").getLanguage());
        
        // This is useful if this is not the first time that libraries are 
        // being loaded.
        CC.P.resetLibrary();
        ParserActions pa=CC.getParserActions();
        
        if(runsAsApplication) {
    		FidoMain.readLibrariesProbeDirectory(CC.P, 
    			englishLibraries, libDirectory);
        } else {
        	// This code is useful when FidoCadJ is used whithout having access
        	// to the user file system, for example because it is run as an
        	// applet. In this case, the only accesses will be internal to
        	// the jar file in order to respect security restrictions.
        	if(englishLibraries) {
        		// Read the english version of the libraries
        	    pa.loadLibraryInJar(FidoFrame.class.getResource(
        	    	"lib/IHRAM_en.FCL"), "ihram");
            	pa.loadLibraryInJar(FidoFrame.class.getResource(
            		"lib/FCDstdlib_en.fcl"), "");
            	pa.loadLibraryInJar(FidoFrame.class.getResource(
            		"lib/PCB_en.fcl"), "pcb");
            	pa.loadLibraryInJar(FidoFrame.class.getResource(
            		"lib/elettrotecnica_en.fcl"), "elettrotecnica");
            	pa.loadLibraryInJar(FidoFrame.class.getResource(
            		"lib/EY_Libraries.fcl"), "EY_Libraries");
        	} else {
        		// Read the italian version of the libraries
            	pa.loadLibraryInJar(FidoFrame.class.getResource(
            		"lib/IHRAM.FCL"), "ihram");
            	pa.loadLibraryInJar(FidoFrame.class.getResource(
            		"lib/FCDstdlib.fcl"), "");
            	pa.loadLibraryInJar(FidoFrame.class.getResource(
            		"lib/PCB.fcl"), "pcb");
            	pa.loadLibraryInJar(FidoFrame.class.getResource(
            		"lib/elettrotecnica.fcl"), "elettrotecnica");
            	pa.loadLibraryInJar(FidoFrame.class.getResource(
            		"lib/EY_Libraries.fcl"), "EY_Libraries");
      		}
 		}
 		libraryModel.forceUpdate();
 	}
    
    /** Perform some initialization tasks: in particular, it reads the library
    	directory and it creates the user interface.
    */
    public void init()
    {

    	// The second toolbar dedicated to the zoom factors and other niceties 
    	// (the
    	// second one under the window title).
    	ToolbarZoom toolZoom;

        Container contentPane=getContentPane();		
        CC=new CircuitPanel(true);
        CC.getParserActions().openFileName = "";
        
        DropTarget drt = new DropTarget(CC, dt);
        
        // If FidoCadJ runs as a standalone application, we must read the 
        // content of the current library directory.
        // at the same time, we see if we should maintain a strict FidoCad
        // compatibility.
        if (runsAsApplication)  {
        	CC.setStrictCompatibility(prefs.get("FCJ_EXT_STRICT", 
        		"false").equals("true"));
        	CC.P.setTextFont(prefs.get("MACRO_FONT", Globals.defaultTextFont), 
        		Integer.parseInt(prefs.get("MACRO_SIZE", "3")), 
        		CC.getUndoActions());
        	readGridSettings();
        	readDrawingSettings();
        }
        
        // Here we set the approximate size of the control at startup. This is 
        // useful, since the scroll panel (created just below) use those
        // settings for specifying the behaviour of scroll bars.
        
        CC.setPreferredSize(new Dimension(1000,1000));
        SC= new JScrollPane((Component)CC);
        CC.father=SC;
        
        SC.setHorizontalScrollBarPolicy(
        	JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        SC.setVerticalScrollBarPolicy(
        	JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        	
		// I planned to add rulers at the borders of the scroll panel.
		// Unfortunately, they does not seem to work as expected and this 
		// feature will be implemented when possible.
        /*RulerPanel vertRuler = new RulerPanel(
            SwingConstants.VERTICAL, 20, 20, 5,
            CC.getMapCoordinates());
        
        RulerPanel horRuler = new RulerPanel(
            SwingConstants.HORIZONTAL, 20, 20, 5,
            CC.getMapCoordinates()); 
          
        SC.setRowHeaderView(vertRuler);
        SC.setColumnHeaderView(horRuler);*/
        if (runsAsApplication) {
        	sgr = new ScrollGestureRecognizer();
        	CC.addScrollGestureSelectionListener(sgr);
        	sgr.getInstance();
		}
        
        SC.getVerticalScrollBar().setUnitIncrement(20);
        SC.getHorizontalScrollBar().setUnitIncrement(20);
        
        CC.profileTime=false;
        CC.antiAlias=true;

        // Create the layer vector. Basically, this is a rather standard
        // attribution in which only the first layers are attributed to
        // something which is circuit-related.
        // I followed merely the FidoCad tradition.
		Vector<LayerDesc> layerDesc=StandardLayers.createStandardLayers();
        CC.P.setLayers(layerDesc);

        toolBar = new ToolbarTools(textToolbar,smallIconsToolbar);
        toolZoom = new ToolbarZoom(layerDesc);
        
        toolBar.addSelectionListener(CC);
        toolZoom.addLayerListener(CC);

        toolZoom.addGridStateListener(CC);
        toolZoom.addZoomToFitListener(this);
        
        CC.addChangeZoomListener(toolZoom);
        CC.addChangeSelectionListener(toolBar);
        
        CC.getContinuosMoveActions().addChangeCoordinatesListener(toolZoom);
        toolZoom.addChangeZoomListener(CC);

        Box b=Box.createVerticalBox();
        
        // In MacOSX with Aqua, make sort that those buttons have a nice
        // rounded shape and appear like native components.
        
        //toolBar.putClientProperty("Quaqua.Button.style","title");
        //toolZoom.putClientProperty("Quaqua.Button.style","title");
        
        b.add(toolBar);

        b.add(toolZoom);
        
 		toolZoom.setFloatable(false);
        toolZoom.setRollover(false);
		
		JMenuBar menuBar=mt.defineMenuBar(this);
		setJMenuBar(menuBar);

        // The initial state is the selection one.
        
        CC.setSelectionState(ElementsEdtActions.SELECTION, "");

        contentPane.add(b,"North");

    	// Macro picker component
    	MacroTree macroLib;
		
        libraryModel = new LibraryModel(CC.P);
        LayerModel layerModel = new LayerModel(CC.P);
        macroLib = new MacroTree(libraryModel,layerModel);
        macroLib.setSelectionListener(CC);
        
        libraryModel.setUndoActorListener(CC.getUndoActions());
        libraryModel.addLibraryListener(new CircuitPanelUpdater(this));
        CC.getUndoActions().setLibraryUndoListener(
        	                       new LibraryUndoExecutor(this,libraryModel));
        
		try {
			LibUtils.saveLibraryState(CC.getUndoActions());
		} catch (IOException e) {
			System.out.println("Exception: "+e);
		
		}
		
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        // Useful for Quaqua with MacOSX.
        //splitPane.putClientProperty("Quaqua.SplitPane.style","bar");

        Dimension windowSize = getSize();
        CC.setPreferredSize(new Dimension(windowSize.width*85/100,100));
        
        splitPane.setTopComponent(SC);
        macroLib.setPreferredSize(new Dimension(450,200));
        splitPane.setBottomComponent(macroLib);
        splitPane.setResizeWeight(.8);

        contentPane.add(splitPane,"Center");

        CC.getUndoActions().setHasChangedListener(this);
        
        CC.setFocusable(true);
        SC.setFocusable(true);   
        
        {
            /*  Add a window listener to close the application when the frame is
                closed. This behavior is platform dependent, for example a 
                Macintosh application can be made run without a visible frame.
                There would anyway the need to customize the menu bar, in order 
                to allow the user to open a new FidoFrame, when it has been
                closed once. The easiest solution to implement is therefore to
                make the application close when the user closes the last frame. 
            */
            addWindowListener(new WindowAdapter()
            {
                public void windowClosing(WindowEvent e)
                {
                    if(!ft.checkIfToBeSaved()) {
                    	return;
                    }
                    
                    setVisible(false);
					CC.getUndoActions().doTheDishes();
                    dispose();
                    Globals.openWindows.remove(FidoFrame.this);

                    --Globals.openWindowsNumber;
                    
                    if (Globals.openWindowsNumber<1 && runsAsApplication) 
                        System.exit(0);
                }
            });
        }
        
        //pack();
        addWindowFocusListener(this);
        Globals.activeWindow=this;
                
        // This is WAY too invasive!!!
        
        //getRootPane().putClientProperty("apple.awt.draggableWindowBackground", 	
        //	Boolean.TRUE);

        CC.getUndoActions().setModified(false);
    }

    /** The action listener. Recognize menu events and behaves consequently.
    	@param evt the event to be processed.
    */
    public void actionPerformed(ActionEvent evt)
    {
        // Recognize and handle menu events
        if(evt.getSource() instanceof JMenuItem) 
        	mt.processMenuActions(evt, this);
    }
    
    
    /**	Create a new instance of the window.
    	@return the created instance
    */
    public FidoFrame createNewInstance()
    {
    	FidoFrame popFrame=new FidoFrame(runsAsApplication, currentLocale);
        popFrame.init();
                
        popFrame.setBounds(getX()+30, getY()+30, popFrame.getWidth(),       
    	    popFrame.getHeight());
    
    	popFrame.loadLibraries();
        popFrame.setVisible(true);
        
        return popFrame;
	}

    /** Show the FidoCadJ preferences panel
    */
    public void showPrefs()
    {
    	String oldDirectory = libDirectory;
    	CopyPasteActions cpa = CC.getCopyPasteActions();
    	ElementsEdtActions eea = CC.getContinuosMoveActions();
    	
    	// At first, we create the preference panel. This kind of code is 
    	// probably not very easy to read and reutilize. This is probably
    	// justified, since the preference panel is after all very specific
    	// to the particular program to which it is referred, i.e. in this
    	// case FidoCadJ...
        DialogOptions options=new DialogOptions(this,
            CC.getMapCoordinates().getXMagnitude(),
            CC.profileTime,CC.antiAlias,
            CC.getMapCoordinates().getXGridStep(),
            libDirectory,
            textToolbar,
            smallIconsToolbar,
            eea.getPCB_thickness(),
            eea.getPCB_pad_sizex(),
            eea.getPCB_pad_sizey(),
            eea.getPCB_pad_drill(),
            Globals.quaquaActive,
            CC.getStrictCompatibility(),
            CC.P.getTextFont(),
            false,
            false,
            Globals.lineWidth,
            Globals.lineWidthCircles,
            Globals.diameterConnection,
            CC.P.getTextFontSize(),
            cpa.getShiftCopyPaste());
                    
        // The panel is now made visible. Its properties will be updated only 
        // if the user clicks on "Ok".
        options.setVisible(true);
        
        
        // Now, we can update the properties.
        CC.profileTime=options.profileTime;
        CC.antiAlias=options.antiAlias;
        textToolbar=options.textToolbar;
        smallIconsToolbar=options.smallIconsToolbar;
                
        CC.getMapCoordinates().setMagnitudes(options.zoomValue, 
                                                       options.zoomValue);
        CC.getMapCoordinates().setXGridStep(options.gridSize); 
        CC.getMapCoordinates().setYGridStep(options.gridSize); 
                
        eea.setPCB_thickness(options.pcblinewidth_i);
        eea.setPCB_pad_sizex(options.pcbpadwidth_i);
        eea.setPCB_pad_sizey(options.pcbpadheight_i);
        eea.setPCB_pad_drill(options.pcbpadintw_i);
        
        CC.P.setTextFont(options.macroFont,
        	options.macroSize_i,
        	CC.getUndoActions());

        CC.setStrictCompatibility(options.extStrict);
        toolBar.setStrictCompatibility(options.extStrict);
        cpa.setShiftCopyPaste(options.shiftCP);

        Globals.quaquaActive=options.quaquaActive;
    
        libDirectory=options.libDirectory;
        
        Globals.lineWidth = options.stroke_size_straight_i;
        Globals.lineWidthCircles = options.stroke_size_straight_i;
        Globals.diameterConnection = options.connectionSize_i;
       
        // We know that this code will be useful only when FidoCadJ will run as
        // a standalone application. If it is used as an applet, this would
        // cause the application crash when the applet security model is active.
        // In this way, we can still use FidoCadJ as an applet, even with the 
        // very restrictive security model applied by default to applets.
        
        if (runsAsApplication) {
       	 	prefs.put("DIR_LIBS", libDirectory);
       	 	prefs.put("MACRO_FONT", CC.P.getTextFont());
       	 	prefs.put("MACRO_SIZE", ""+CC.P.getTextFontSize());
       	 	
       	 	prefs.put("STROKE_SIZE_STRAIGHT", ""+Globals.lineWidth);
       	 	prefs.put("STROKE_SIZE_OVAL", ""+Globals.lineWidthCircles);
       	 	prefs.put("CONNECTION_SIZE", ""+Globals.diameterConnection);
       	 	
        	prefs.put("SMALL_ICON_TOOLBAR", 
            	(smallIconsToolbar?"true":"false"));
            
        	prefs.put("TEXT_TOOLBAR",
            	(textToolbar?"true":"false"));
     
        	prefs.put("QUAQUA",
            	(Globals.quaquaActive?"true":"false"));
        	prefs.put("FCJ_EXT_STRICT",
            	(CC.getStrictCompatibility()?"true":"false"));
            	
            prefs.put("GRID_SIZE", ""+CC.getMapCoordinates().getXGridStep());
            
            // Save default PCB characteristics            
            prefs.put("PCB_pad_sizex", ""+eea.PCB_pad_sizex);
            prefs.put("PCB_pad_sizey", ""+eea.PCB_pad_sizey);
			prefs.put("PCB_pad_style", ""+eea.PCB_pad_style);
            prefs.put("PCB_pad_drill", ""+eea.PCB_pad_drill);
            prefs.put("PCB_thickness", ""+eea.PCB_thickness);
            prefs.put("SHIFT_CP", (cpa.getShiftCopyPaste()?"true":"false"));

        }
     	if(!libDirectory.equals(oldDirectory)) {
     		loadLibraries();
           	show();
     	}	
        repaint();
    }
    
    /** Set the current zoom to fit
    */
    public void zoomToFit()
    {
        //double oldz=CC.getMapCoordinates().getXMagnitude();
        
        // We calculate the zoom to fit factor here.
        MapCoordinates m=DrawingSize.calculateZoomToFit(CC.P,
            SC.getViewport().getExtentSize().width-35,
            SC.getViewport().getExtentSize().height-35,
            true);
            
        double z=m.getXMagnitude();
        
        // We apply the zoom factor to the coordinate transform
        CC.getMapCoordinates().setMagnitudes(z, z);     		 		
	    	
       	// We make the scroll pane show the interesting part of
       	// the drawing.
       	Rectangle r= new Rectangle((int)(m.getXCenter()), 
   			(int)(m.getYCenter()), 
   			SC.getViewport().getExtentSize().width, 
   			SC.getViewport().getExtentSize().height);
   			
   		CC.updateSizeOfScrollBars(r);
    }
    
    /** We notify the user that something has changed by putting an asterisk
     	in the file name.
     	We also show here in the titlebar the (eventually stretched) file name
     	of the drawing being modified or shown.
    */
    public void somethingHasChanged()
    {
        if (Globals.weAreOnAMac) {
			
			// Apparently, this does not work as expected in MacOSX 10.4 Tiger.
			// Those are MacOSX >= 10.5 Leopard features.
			// We thus leave also the classic Window-based asterisk when
			// the file has been modified.
			
			getRootPane().putClientProperty("Window.documentModified", 
				Boolean.valueOf(CC.getUndoActions().getModified()));
				
			// On MacOSX >= 10.5, associate an icon and a file proxy to the
			// title bar.
			getRootPane( ).putClientProperty( "Window.documentFile", 
				new File(CC.getParserActions().openFileName));
					
			setTitle("FidoCadJ "+Globals.version+" "+ 
            Globals.prettifyPath(CC.getParserActions().openFileName,45)+ 
            (CC.getUndoActions().getModified()?" *":""));
		} else {
			setTitle("FidoCadJ "+Globals.version+" "+ 
            Globals.prettifyPath(CC.getParserActions().openFileName,45)+ 
            (CC.getUndoActions().getModified()?" *":""));
        
		}	
	}
    
    /** The current window has gained focus
    */
    public void windowGainedFocus(WindowEvent e) 
    {
        Globals.activeWindow = this;
    }
    
    /** The current window has lost focus
    */
    public void windowLostFocus(WindowEvent e) 
    {
    	// Nothing to do
    }
}
