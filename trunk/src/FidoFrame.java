import java.awt.*;
import java.awt.event.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.util.*;
import java.net.*;
import java.awt.print.*;
import java.util.prefs.*;

import globals.*;
import dialogs.*;
import layers.*;
import export.*;
import circuit.*;
import geom.*;
import clipboard.*;
import toolbars.*;
import timer.*;


/** FidoFrame.java 

Probably, it would be a very good idea to implement the editor with a 
model/vista/controller paradigm. Anyway, it would be a lot of code rearranging
work... I will do it for my NEXT vectorial drawing program :-D

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

    Copyright 2008-2012 by Davide Bucci
</pre>

    The FidoFrame class describes a frame which is used to trace schematics
    and printed circuit boards.
    
    @author Davide Bucci
*/

public class FidoFrame extends JFrame implements 
                                            MenuListener, 
                                            ActionListener,
                                            Printable,
                                            DropTargetListener,
                                            ZoomToFitListener,
                                            HasChangedListener,
                                            WindowFocusListener
{
    // Interface elements parts of FidoFrame
    
    // The circuit panel...
    public CircuitPanel CC;
    // ... which is contained in a scroll pane.
    private JScrollPane SC;
    // The toolbar dedicated to the available tools (the first one under the
    // window title).
    private ToolbarTools toolBar;
    // The second toolbar dedicated to the zoom factors and other niceties (the
    // second one under the window title).
    private ToolbarZoom toolZoom;
    
    // Tree allowing the access to the loaded libraries.
    private MacroTree macroLib;
     
 
    // Export default properties
    private String exportFileName;
    private String exportFormat;
    private boolean exportBlackWhite;
    private double exportUnitPerPixel;
    private double exportMagnification;
    
    // Settings related to the printing mode.
    private boolean printMirror;
    private boolean printFitToPage;
    private boolean printLandscape;
   
    // Settings for macro splitting.
    private boolean splitNonStandardMacro_s;    // split non standard macro
                                                // when saving
    private boolean splitNonStandardMacro_c;    // split non standard macro
                                                // when copying
   
   // FidoCadJ extensions
    private boolean extFCJ_s;   // Use FidoCadJ extensions while saving
    private boolean extFCJ_c;   // Use FidoCadJ extensions while copying
        
    // Open/save default properties
    public String openFileDirectory;

    // Libraries properties
    public String libDirectory;
    public Preferences prefs;
    
    // Toolbar properties
    
    // Text description under icons
    private boolean textToolbar;
    // Small (16x16 pixel) icons instead of standard (32x32 pixel)
    private boolean smallIconsToolbar;
    
    // Drag and drop target
    private DropTarget dt;
    
    // Show macro origin (menu item).
    private JCheckBoxMenuItem optionMacroOrigin;
    
    // Locale settings
    static public Locale currentLocale;
    // Runs as an application or an applet.
    static public boolean runsAsApplication;
    
    // Useful for automatic scrolling in panning mode.
    private ScrollGestureRecognizer sgr;
   
    
    /** The standard constructor: create the frame elements and set up all
        variables. Note that the constructor itself is not sufficient for
        using the frame. You need to call the init procedure after you have
        set the configuration variables available for FidoFrame.
    */
    public FidoFrame (boolean appl)
    {
        super("FidoCadJ "+Globals.version);
		runsAsApplication = appl;
        currentLocale = Locale.getDefault();

        // Those lines allow a better Cocoa-like integration
        // under Leopard. Is it overridden by the use of the Quaqua L&F?
        // No! It is actually need to make all the window movable when clicking
        // in the toolbar.
        
        getRootPane().putClientProperty("apple.awt.brushMetalLook", 	
        	Boolean.TRUE);

		exportMagnification=1.0;
        	
        // Uncomment to force FidoCadJ to use a specified locale 
        // currentLocale = new Locale("it", "IT");
        
        // The following code has changed from version 0.20.1.
        // This way, it should tolerate systems in which resource file for the
        // current locale is not available. The English interface will be shown.
        
        try {
            // Try to load the program with the current locale
            Globals.messages = Utf8ResourceBundle.getBundle("MessagesBundle", 
               currentLocale);                             
            
        } catch(MissingResourceException mre) {
            try {
                // If it does not work, try to use the standard English
                Globals.messages = ResourceBundle.getBundle("MessagesBundle",
                    new Locale("en", "US"));
                System.out.println("No locale available, sorry... "+
                	"interface will be in English");
            } catch(MissingResourceException mre1) {
                // Give up!!!
                JOptionPane.showMessageDialog(null,
                    "Unable to find language localization files: " + mre1);
                System.exit(1);
            }
        }        
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
        
        DialogUtil.center(this, .75,.75,800,500);
        setDefaultCloseOperation(
            javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE); 

		// We need to keep track of the number of open windows. If the last
		// one is closed, we exit from the program.
		
        ++Globals.openWindowsNumber;
        Globals.openWindows.add(this);

        URL url=DialogAbout.class.getResource(
            "program_icons/icona_fidocadj_128x128.png");
        
        // Set icon
        if (url != null) {
            Image icon = Toolkit.getDefaultToolkit().getImage(url);
            setIconImage(icon);
        }
                
        Globals g=new Globals();
        
        if (runsAsApplication) {
            prefs = Preferences.userNodeForPackage(g.getClass());
     	 	readPreferences();
        } else {
        
        	// If we can not access to the preferences, we inizialize those
        	// configuration variables with default values.
        	libDirectory = "";
        	
        	openFileDirectory = "";
        	smallIconsToolbar = true;
        	textToolbar = true;
        
        	extFCJ_s = true;
        	extFCJ_c = true;

        	splitNonStandardMacro_s= false;
        	splitNonStandardMacro_c= false;
        }
        // some standard configurations
        exportFileName=new String();
        exportFormat=new String();
        exportBlackWhite=false;
        printMirror = false;
        printFitToPage = false;
        printLandscape = false;    

    }
    
    /** Read the preferences settings (mainly at startup or when a new 
    	editing window is created.
    */
    public void readPreferences()
    {
		// The library directory
       	libDirectory = prefs.get("DIR_LIBS", "");
       	
       	// The open file directory
        openFileDirectory = prefs.get("OPEN_DIR", "");
        
        // The icon size
        smallIconsToolbar = prefs.get("SMALL_ICON_TOOLBAR", 
            	"true").equals("true");
        // Presence of the text description in the toolbar
        textToolbar = prefs.get("TEXT_TOOLBAR", "true").equals("true");
        
        // FidoCadJ extensions when saving, or copy/pasting
        extFCJ_s = prefs.get("FCJ_EXT_SAVE", "true").equals("true");
        extFCJ_c = prefs.get("FCJ_EXT_COPY", "true").equals("true");
        
		// Split non standard macros when saving, or copy/pasting
        splitNonStandardMacro_s= prefs.get("SPLIT_N_MACRO_SAVE", 
        		"false").equals("true");
        splitNonStandardMacro_c= prefs.get("SPLIT_N_MACRO_COPY", 
        		"false").equals("true");

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
    
    /* Load the saved configuration for the PCB drawing primitives.
    */
	public void readDrawingSettings()
	{
 		CC.PCB_pad_sizex = Integer.parseInt(prefs.get("PCB_pad_sizex", "10"));
 		CC.PCB_pad_sizey = Integer.parseInt(prefs.get("PCB_pad_sizey", "10"));
 		CC.PCB_pad_style = Integer.parseInt(prefs.get("PCB_pad_style", "0"));
 		CC.PCB_pad_drill = Integer.parseInt(prefs.get("PCB_pad_drill", "5"));
 		CC.PCB_thickness = Integer.parseInt(prefs.get("PCB_thickness", "5"));
    }
    
    /* Load the standard librairies according to the locale.
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
        
        if(runsAsApplication) {
    		FidoMain.readLibraries(CC.P, englishLibraries, libDirectory);
        } else {
        	// This code is useful when FidoCadJ is used whithout having access
        	// to the user file system, for example because it is run as an
        	// applet. In this case, the only accesses will be internal to
        	// the jar file in order to respect security restrictions.
        	if(englishLibraries) {
        		// Read the english version of the libraries
        	    CC.P.loadLibraryInJar(FidoFrame.class.getResource(
        	    	"lib/IHRAM_en.FCL"), "ihram");
            	CC.P.loadLibraryInJar(FidoFrame.class.getResource(
            		"lib/FCDstdlib_en.fcl"), "");
            	CC.P.loadLibraryInJar(FidoFrame.class.getResource(
            		"lib/PCB_en.fcl"), "pcb");
        	} else {
        		// Read the italian version of the libraries
            	CC.P.loadLibraryInJar(FidoFrame.class.getResource(
            		"lib/IHRAM.FCL"), "ihram");
            	CC.P.loadLibraryInJar(FidoFrame.class.getResource(
            		"lib/FCDstdlib.fcl"), "");
            	CC.P.loadLibraryInJar(FidoFrame.class.getResource(
            		"lib/PCB.fcl"), "pcb");
      		}
 		}
 		macroLib.updateLibraries(CC.P.getLibrary(), CC.P.getLayers());
 	}
    
    /** Perform some initialization tasks: in particular, it reads the library
    	directory and it creates the user interface.
    
    */
    public void init()
    {
        // I wanted to measure the library loading time, in order to ensure
        // that it is reasonably fast (it is, on any reasonable hardware).
        // A measurement is done only if Globals.isBeta is true.
        		    	        
        MyTimer mt;
        mt = new MyTimer();
        Container contentPane=getContentPane();
        
		//((JComponent)getContentPane()).setOpaque(true);
		
        CC=new CircuitPanel(true);
        CC.P.openFileName = new String();
                        	
        dt = new DropTarget(CC, this);
        
        // If FidoCadJ runs as a standalone application, we must read the 
        // content of the current library directory.
        // at the same time, we see if we should maintain a strict FidoCad
        // compatibility.
        if (runsAsApplication)  {
        	CC.setStrict(prefs.get("FCJ_EXT_STRICT", "false").equals("true"));
        	CC.P.setTextFont(prefs.get("MACRO_FONT", Globals.defaultTextFont), 
        		Integer.parseInt(prefs.get("MACRO_SIZE", "3")));
        	readGridSettings();
        	readDrawingSettings();
        }
        
        // Here we set the approximate size of the control at startup. This is 
        // useful, since the scroll panel (created just below) use those
        // settings for specifying the behaviour of scroll bars.
        
        CC.setPreferredSize(new Dimension(1000,1000));
        SC= new JScrollPane((Component)CC);
        SC.setHorizontalScrollBarPolicy(
        	JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        SC.setVerticalScrollBarPolicy(
        	JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		// I planned to add rulers at the borders of the scroll panel.
		// Unfortunately, they does not seem to work as expected and this 
		// feature will be implemented when possible.
        RulerPanel vertRuler = new RulerPanel(
            SwingConstants.VERTICAL, 20, 20, 5,
            CC.getMapCoordinates());
        
        RulerPanel horRuler = new RulerPanel(
            SwingConstants.HORIZONTAL, 20, 20, 5,
            CC.getMapCoordinates());
          
        //SC.setRowHeaderView(vertRuler);
        //SC.setColumnHeaderView(horRuler);
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
		Vector layerDesc=Globals.createStandardLayers();
        CC.P.setLayers(layerDesc);

        toolBar = new ToolbarTools(textToolbar,smallIconsToolbar);
        toolZoom = new ToolbarZoom(layerDesc);
        
        toolBar.addSelectionListener(CC);
        toolZoom.addLayerListener(CC);

        toolZoom.addGridStateListener(CC);
        toolZoom.addZoomToFitListener(this);
        
        CC.addChangeZoomListener(toolZoom);
        CC.addChangeSelectionListener(toolBar);
        CC.addChangeCoordinatesListener(toolZoom);
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

        // Menu creation
        JMenuBar menuBar=new JMenuBar();
        setJMenuBar(menuBar);

        // The initial state is the selection one.
        
        CC.setSelectionState(CircuitPanel.SELECTION, "");

        JMenu fileMenu=new JMenu(Globals.messages.getString("File"));
        JMenuItem fileNew = new JMenuItem(Globals.messages.getString("New"));
        fileNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
            Globals.shortcutKey));
        JMenuItem fileOpen = new JMenuItem(Globals.messages.getString("Open"));
        fileOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
            Globals.shortcutKey));
        JMenuItem fileSave = new 
            JMenuItem(Globals.messages.getString("Save"));
        fileSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
            Globals.shortcutKey));
        JMenuItem fileSaveName = new 
            JMenuItem(Globals.messages.getString("SaveName"));
        fileSaveName.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
            Globals.shortcutKey | InputEvent.SHIFT_MASK));
            
        JMenuItem fileExport = new 
            JMenuItem(Globals.messages.getString("Export"));
        
        fileExport.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
            Globals.shortcutKey));
        
        
        JMenuItem filePrint = new 
            JMenuItem(Globals.messages.getString("Print"));
            
        filePrint.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
            Globals.shortcutKey));
            
        JMenuItem fileClose = new 
            JMenuItem(Globals.messages.getString("Close"));
        
        fileClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
            Globals.shortcutKey));
        
        fileMenu.add(fileNew);
        fileMenu.add(fileOpen);
        fileMenu.add(fileSave);
        fileMenu.add(fileSaveName);
        fileMenu.addSeparator();
        
        fileMenu.add(fileExport);
        fileMenu.add(filePrint);
        fileMenu.addSeparator();
        fileMenu.add(fileClose);

        fileNew.addActionListener((ActionListener)this);
        fileOpen.addActionListener((ActionListener)this);
        fileExport.addActionListener((ActionListener)this);
        filePrint.addActionListener((ActionListener)this);
        fileClose.addActionListener((ActionListener)this);
    
        fileSave.addActionListener((ActionListener)this);
        fileSaveName.addActionListener((ActionListener)this);
    
        menuBar.add(fileMenu);
        
        JMenu editMenu = new JMenu(Globals.messages.getString("Edit_menu"));
        
        JMenuItem editUndo = new 
            JMenuItem(Globals.messages.getString("Undo"));
        editUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
            Globals.shortcutKey));
        //editUndo.setEnabled(false);
        JMenuItem editRedo = new 
            JMenuItem(Globals.messages.getString("Redo"));
        editRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
            Globals.shortcutKey | InputEvent.SHIFT_MASK));
        //editRedo.setEnabled(false);
        JMenuItem editCut = new 
            JMenuItem(Globals.messages.getString("Cut"));
        editCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
            Globals.shortcutKey));
            
        JMenuItem editCopy = new 
            JMenuItem(Globals.messages.getString("Copy"));
        editCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
            Globals.shortcutKey));
        
        JMenuItem editPaste = new 
            JMenuItem(Globals.messages.getString("Paste"));
        editPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
            Globals.shortcutKey));
        JMenuItem clipboardCircuit = new 
            JMenuItem(Globals.messages.getString("DefineClipboard"));
            
        JMenuItem editSelectAll = new 
            JMenuItem(Globals.messages.getString("SelectAll"));
        editSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
            Globals.shortcutKey));
 
        JMenuItem editRotate = new 
            JMenuItem(Globals.messages.getString("Rotate"));
        editRotate.setAccelerator(KeyStroke.getKeyStroke("R"));
        
        JMenuItem editMirror = new 
            JMenuItem(Globals.messages.getString("Mirror_E"));
        editMirror.setAccelerator(KeyStroke.getKeyStroke("S"));
 
        editUndo.addActionListener((ActionListener)this);
        editRedo.addActionListener((ActionListener)this);
        editCut.addActionListener((ActionListener)this);
        editCopy.addActionListener((ActionListener)this);
        editPaste.addActionListener((ActionListener)this);
        editSelectAll.addActionListener((ActionListener)this);
        editMirror.addActionListener((ActionListener)this);
        editRotate.addActionListener((ActionListener)this);
        
        editMenu.add(editUndo);
        editMenu.add(editRedo);
        editMenu.addSeparator();
        
        editMenu.add(editCut);
        editMenu.add(editCopy);
        editMenu.add(editPaste);
        editMenu.add(clipboardCircuit);

        editMenu.addSeparator();
    
        editMenu.add(editSelectAll);
        editMenu.addSeparator();
        editMenu.add(editRotate);
        editMenu.add(editMirror);
        
        menuBar.add(editMenu);
        
        JMenu viewMenu=new JMenu(Globals.messages.getString("View"));
        JMenuItem layerOptions = new 
            JMenuItem(Globals.messages.getString("Layer_opt"));
            
        layerOptions.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,
            Globals.shortcutKey));
            
        JMenuItem optionCircuit = new 
            JMenuItem(Globals.messages.getString("Circ_opt"));
		

        viewMenu.add(layerOptions);
        
        // On a MacOSX system, this menu is associated to preferences menu
        // in the application menu. We do not need to show it in View.
        // This needs the AppleSpecific extensions to be active.
        
        if(!Globals.weAreOnAMac) 
            viewMenu.add(optionCircuit);

        optionMacroOrigin = new 
            JCheckBoxMenuItem(Globals.messages.getString("Macro_origin"));
            
		optionMacroOrigin.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M,
            Globals.shortcutKey));

        viewMenu.add(optionMacroOrigin);
        
        optionMacroOrigin.addActionListener((ActionListener)this);
        layerOptions.addActionListener((ActionListener)this);
        optionCircuit.addActionListener((ActionListener)this);

        menuBar.add(viewMenu);

        JMenu circuitMenu=new JMenu(Globals.messages.getString("Circuit"));
        JMenuItem defineCircuit = new 
            JMenuItem(Globals.messages.getString("Define"));
        
        defineCircuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,
            Globals.shortcutKey));
            
        circuitMenu.add(defineCircuit);

        JMenuItem updateLibraries = new 
            JMenuItem(Globals.messages.getString("LibraryUpdate"));
        
        updateLibraries.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U,
            Globals.shortcutKey));
            
        circuitMenu.add(updateLibraries);
      	defineCircuit.addActionListener((ActionListener)this);
        updateLibraries.addActionListener((ActionListener)this);
        clipboardCircuit.addActionListener((ActionListener)this);

        menuBar.add(circuitMenu);

        JMenu about = new JMenu(Globals.messages.getString("About"));
        JMenuItem aboutMenu = new 
            JMenuItem(Globals.messages.getString("About_menu"));
        about.add(aboutMenu);
        
        contentPane.add(b,"North");

        // On a MacOSX system, this menu is associated to preferences menu
        // in the application menu. We do not need to show it in bar.
        // This needs the AppleSpecific extensions to be active.
        
        if(!Globals.weAreOnAMac)
            menuBar.add(about);
        aboutMenu.addActionListener((ActionListener)this);
		
		macroLib = new MacroTree();
		macroLib.setSelectionListener(CC);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        Dimension windowSize = getSize();
        CC.setPreferredSize(new Dimension(windowSize.width*85/100,100));
        
        splitPane.setTopComponent(SC);
        macroLib.setPreferredSize(new Dimension(450,200));
        splitPane.setBottomComponent(macroLib);
        splitPane.setResizeWeight(.8);

        contentPane.add(splitPane,"Center");

        CC.P.setHasChangedListener(this);
        
        CC.setFocusable(true);
        SC.setFocusable(true);   
        
        
        if (true){
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
                    if(!checkIfToBeSaved()) {
                    	return;
                    }
                    
                    setVisible(false);
                    dispose();
                    Globals.openWindows.remove(this);

                    --Globals.openWindowsNumber;
                    
                    if (Globals.openWindowsNumber<1)
                        if (runsAsApplication) 
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

        
    }
    
   
    
    /** Ask the user if the current file should be saved and do it if yes.
    	@return true if the window should be closed or false if the closing
    		action has been cancelled.
    	
    */
    public boolean checkIfToBeSaved()
    {
    	boolean shouldExit = true;
        if (CC.P.getModified()) {
            Object[] options = {
                Globals.messages.getString("Save"),
                Globals.messages.getString("Do_Not_Save"),
                Globals.messages.getString("Cancel_btn")};
            
            // We try to show in the title bar of the dialog the file name of 
           	// the drawing to which the dialog refers to. If not, we just
           	// write Warning!
           
            String filename=Globals.messages.getString("Warning");
            if(!CC.P.openFileName.equals("")) {
            	filename=CC.P.openFileName;
			}
           	int choice=JOptionPane.showOptionDialog(this, 
                Globals.messages.getString("Warning_unsaved"),
                Globals.prettifyPath(filename,35),
                JOptionPane.YES_NO_CANCEL_OPTION, 
                JOptionPane.QUESTION_MESSAGE, 
                null,
                options,  //the titles of buttons
    	 		options[0]); //default button title)
    						
    		// Those constant names does not reflect the actual 
    		// message shown on the buttons. 
            if(choice==JOptionPane.YES_OPTION) { 
               	//  Save and exit
               	//System.out.println("Save and exit.");
               	if(!save())
               		shouldExit=false;
            } else if (choice==JOptionPane.NO_OPTION) { 
               	// Don't save, exit
               	//System.out.println("Do not save and exit.");
            } else if (choice==JOptionPane.CANCEL_OPTION) {
               	// Don't exit
               	//System.out.println("Do not exit.");
               	shouldExit = false;
            }
                      
        }
    	return shouldExit;
    }
    
    /** The action listener. Recognize menu events and behaves consequently.
    
    */
    public void actionPerformed(ActionEvent evt)
    {
        // Recognize and handle menu events
        if(evt.getSource() instanceof JMenuItem) 
        {
            String arg=evt.getActionCommand();
			// Edit the FidoCadJ code of the drawing
            if (arg.equals(Globals.messages.getString("Define"))) {
                EnterCircuitFrame circuitDialog=new EnterCircuitFrame(this,
                    CC.getCirc(extFCJ_s).toString());
                circuitDialog.setVisible(true);
                
                try {
                    CC.setCirc(new StringBuffer(circuitDialog.stringCircuit));
                    CC.P.saveUndoState();
                    repaint();
                } catch(IOException e) {
                    System.out.println("Error: "+e); 
                }
            }
            // Update libraries
            if (arg.equals(Globals.messages.getString("LibraryUpdate"))) {
           		loadLibraries();
           		show();
            }
            // Options for the current drawing
            if (arg.equals(Globals.messages.getString("Circ_opt"))) {
                showPrefs();
            }
            // Options for the layers
            if (arg.equals(Globals.messages.getString("Layer_opt"))) {
                DialogLayer layerDialog=new DialogLayer(this,CC.P.getLayers());
                layerDialog.setVisible(true);
                
                // It is important that we force a complete recalculation of 
                // all details in the drawing, otherwise the buffered setup
                // will not be responsive to the changes in the layer editing.
                
                CC.P.setChanged(true);
                repaint();
            }
			// Print the current drawing
            if (arg.equals(Globals.messages.getString("Print"))) {
				printDrawing();
            }
            // Save with name
            if (arg.equals(Globals.messages.getString("SaveName"))) {
                saveWithName();
            }  
            // Save with the current name (if available)
            if (arg.equals(Globals.messages.getString("Save"))) {
                save();   
            }
            // New drawing
            if (arg.equals(Globals.messages.getString("New"))) {
                createNewInstance();
            }
            // Undo the last action
            if (arg.equals(Globals.messages.getString("Undo"))) {
                CC.P.undo();
                repaint();
            }
            // Redo the last action
            if (arg.equals(Globals.messages.getString("Redo"))) {
                CC.P.redo();
                repaint();
            }
            // Show the about menu
            if (arg.equals(Globals.messages.getString("About_menu"))) {
                DialogAbout d=new DialogAbout(this);
                d.setVisible(true);
            }
            // Open a file
            if (arg.equals(Globals.messages.getString("Open"))) {
				OpenFile openf=new OpenFile();
				openf.setParam(this);
				SwingUtilities.invokeLater(openf);
				/*
           		The following code would require a thread safe implementation
           		of some of the inner classes (such as ParseSchem), which is 
           		indeed not the case...
           		*/
				/*Thread thread = new Thread(openf);
				thread.setDaemon(true);
				// Start the thread
				thread.start();*/
				
            }
            // Export the current drawing
            if (arg.equals(Globals.messages.getString("Export"))) {
				export();
            }
            // Select all elements in the current drawing
            if (arg.equals(Globals.messages.getString("SelectAll"))) {
                CC.P.selectAll();   
                // Even if the drawing is not changed, a repaint operation is 
                // needed since all selected elements are rendered in green.
                repaint();
            }
        	// Copy all selected elements in the clipboard
            if (arg.equals(Globals.messages.getString("Copy"))) {
                CC.P.copySelected(extFCJ_c, splitNonStandardMacro_c,
                	CC.getMapCoordinates().getXGridStep(), 
                	CC.getMapCoordinates().getYGridStep());   
            }
            // Cut all the selected elements
            if (arg.equals(Globals.messages.getString("Cut"))) {
                CC.P.copySelected(extFCJ_c, splitNonStandardMacro_c,
                	CC.getMapCoordinates().getXGridStep(), 
                	CC.getMapCoordinates().getYGridStep());   
                CC.P.deleteAllSelected();
                repaint();
            }
            // Mirror all the selected elements
            if (arg.equals(Globals.messages.getString("Mirror_E"))) {
                if(!CC.isEnteringMacro())
                	CC.P.mirrorAllSelected();
                else
                	CC.mirrorMacro();               
                repaint();
            }
            // 90 degrees rotation of all selected elements
            if (arg.equals(Globals.messages.getString("Rotate"))) {
                
                if(!CC.isEnteringMacro())
                	CC.P.rotateAllSelected();
                else
                	CC.rotateMacro();
                repaint();
            }
            // Show the macro origin
            if (arg.equals(Globals.messages.getString("Macro_origin"))) {
                CC.P.setMacroOriginVisible(optionMacroOrigin.isSelected()); 
                repaint();
            }
            // Paste as a new circuit
            if (arg.equals(Globals.messages.getString("DefineClipboard"))) {
                TextTransfer textTransfer = new TextTransfer();
                try {
                    FidoFrame popFrame;
                    if(CC.P.getModified()) {
                        popFrame = createNewInstance();
                    } else {
                        popFrame=this;
                    }
                    popFrame.CC.setCirc(new 
                        StringBuffer(textTransfer.getClipboardContents()));
                } catch(IOException e) {
                    System.out.println("Error: "+e); 
                }   
                repaint();
            }
            // Paste some graphical elements 
            if (arg.equals(Globals.messages.getString("Paste"))) {
                CC.P.paste();   
                repaint();
            }
            // Close the current window
            if (arg.equals(Globals.messages.getString("Close"))) {
                if(!checkIfToBeSaved()) {
                   	return;
                }
                    
                setVisible(false);
                dispose();
                Globals.openWindows.remove(this);

                --Globals.openWindowsNumber;
                    
                if (Globals.openWindowsNumber<1)
                    System.exit(0);
            }
        }
    }
    
    /** The menuSelected method, useful for the MenuListener interface.
    */
    public void menuSelected(MenuEvent evt)
    {
    }
    
    /** The menuDeselected method, useful for the MenuListener interface.
    */
    public void menuDeselected(MenuEvent evt)
    {
    }
    
    /** The menuCanceled method, useful for the MenuListener interface.
    */
    public void menuCanceled(MenuEvent evt)
    {
    }
    
    /** Print the current drawing.
    */
    public void printDrawing()
    {
        DialogPrint dp=new DialogPrint(this);
        dp.setMirror(printMirror);
        dp.setFit(printFitToPage);
        dp.setBW(exportBlackWhite);
        dp.setLandscape(printLandscape);
        dp.setVisible(true);
                
        // Get some information about the printing options.
        printMirror = dp.getMirror();
        printFitToPage = dp.getFit();
        printLandscape = dp.getLandscape();
        exportBlackWhite= dp.getBW();
        
        Vector ol=CC.P.getLayers();
        if (dp.shouldPrint()) {
            if(exportBlackWhite) {
                Vector v=new Vector();
                        
                // Here we create an alternative array of layers in 
                // which all colors are pitch black. This may be
                // useful for PCB's.
                        
                for (int i=0; i<Globals.MAX_LAYERS;++i)
                    v.add(new LayerDesc(Color.black, 
                        ((LayerDesc)ol.get(i)).getVisible(),
                         "B/W",((LayerDesc)ol.get(i)).getAlpha()));        
                CC.P.setLayers(v);
            }
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable(this);
            boolean ok = job.printDialog();
            if (ok) {
                try {
                    PrintRequestAttributeSet aset = new 
                    	HashPrintRequestAttributeSet();
                    // Set the correct printing orientation.
                    if (!printLandscape) {
                        aset.add(OrientationRequested.PORTRAIT);
                    } else {
                        aset.add(OrientationRequested.LANDSCAPE);
                    }
                    job.print(aset);
                } catch (PrinterException ex) {
                // The job did not successfully complete
                    JOptionPane.showMessageDialog(this,
                        Globals.messages.getString("Print_uncomplete"));
                }
            }
        CC.P.setLayers(ol);
                
        }
    }
    
    /** Export the current drawing
    */
    public void export()
    {                 
    	// At first, we create and configure the dialog allowing the user
    	// to choose the exporting options
    	DialogExport export=new DialogExport(this);
        export.setAntiAlias(true);
        export.setFormat(exportFormat);
        export.setFileName(exportFileName);
        export.setUnitPerPixel(exportUnitPerPixel);
        export.setBlackWhite(exportBlackWhite);
        export.setMagnification(exportMagnification);
                
        // Once configured, we show the modal dialog
        export.setVisible(true);      
        if (export.shouldExport()) {
	    	exportFileName=export.getFileName();
            exportFormat=export.getFormat();
            // The resolution based export should be used only for bitmap
            // file formats
            if(exportFormat.equals("png") ||
               exportFormat.equals("jpg"))
            	exportUnitPerPixel=export.getUnitPerPixel();
            else
            	exportUnitPerPixel = export.getMagnification();
            
            exportBlackWhite=export.getBlackWhite();
            exportMagnification = export.getMagnification();
            int selection;
            
            // We first check if the file name chosen by the user has a correct
            // file extension, coherent with the file format chosen.
            if(!Globals.checkExtension(exportFileName, exportFormat)) {
               	selection = JOptionPane.showConfirmDialog(null, 
                Globals.messages.getString("Warning_extension"),
                Globals.messages.getString("Warning"),
                JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.WARNING_MESSAGE);
                // If useful, we correct the extension.
            	if(selection==JOptionPane.OK_OPTION) 
                	exportFileName = Globals.adjustExtension(
                		exportFileName, exportFormat);
            }
            // If the file already exists, we asks for confirmation
            if(new File(exportFileName).exists()) {
               	selection = JOptionPane.showConfirmDialog(null, 
                    Globals.messages.getString("Warning_overwrite"),
                    Globals.messages.getString("Warning"),
                    JOptionPane.OK_CANCEL_OPTION, 
                    JOptionPane.WARNING_MESSAGE);
                if(selection!=JOptionPane.OK_OPTION)
                   	return;
             }
          	// We do the export
           	RunExport doExport = new RunExport();
			// Here we try to use the multithreaded structure of Java.
			doExport.setParam(new File(exportFileName),  CC.P, 
               	exportFormat, exportUnitPerPixel, 
               	export.getAntiAlias(),exportBlackWhite,extFCJ_s,
               	this);
               
           	SwingUtilities.invokeLater(doExport);
           	/*
           		The following code would require a thread safe implementation
           		of some of the inner classes (such as ParseSchem), which is 
           		indeed not the case...
           		
           	Thread thread = new Thread(doExport);
			thread.setDaemon(true);
			// Start the thread
			thread.start();
			*/
         }
    }
    
    /**	Create a new instance of the window.
    	@return the created instance
    */
    public FidoFrame createNewInstance()
    {
    	FidoFrame popFrame=new FidoFrame(runsAsApplication);
        popFrame.init();
                
        popFrame.setBounds(getX()+30, getY()+30, popFrame.getWidth(),       
    	    popFrame.getHeight());
    
    	popFrame.loadLibraries();
        popFrame.setVisible(true);
        
        return popFrame;
	}

    /** The printing interface 
    */
    public int print(Graphics g, PageFormat pf, int page) throws
                                                   PrinterException 
    {
        int npages = 0;
        
        // This might be explained as follows: 
		// 1 - The Java printing system normally works with an internal 
		// resolution which is 72 dpi (probably inspired by Postscript).
		// 2 - To have a sufficient resolution, this is increased by 16 times,
		// by using the scale method of the graphic object associated to the 
		// printer. This gives a 72 dpi * 16=1152 dpi resolution.
		// 3 - The 0.127 mm pitch used in FidoCadJ corresponds to a 200 dpi 
		// resolution. Calculating 1152 dpi / 200 dpi gives the 5.76 constant

        double xscale = 1.0/16; // Set 1152 logical units for an inch
        double yscale = 1.0/16; // as the standard resolution is 72
        double zoom = 5.76;     // act in a 1152 dpi resolution as 1:1
               
        Graphics2D g2d = (Graphics2D)g;
        MapCoordinates zoomm=new MapCoordinates();

        // User (0,0) is typically outside the imageable area, so we must
        // translate by the X and Y values in the PageFormat to avoid clipping
         
        if (printMirror) {
            g2d.translate(pf.getImageableX()+pf.getImageableWidth(),
                pf.getImageableY());
            g2d.scale(-xscale,yscale); 
            
        } else {
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            g2d.scale(xscale,yscale); 
        }   
       
        int printerWidth = ((int)pf.getImageableWidth()*16);

		// Perform an adjustement if we need to fit the drawing to the page.
        if (printFitToPage) {        
            zoomm=ExportGraphic.calculateZoomToFit(CC.P, 
                (int)pf.getImageableWidth()*16, (int)pf.getImageableHeight()*16, 
                    true,false);
            zoom=zoomm.getXMagnitude();
        }
         
        MapCoordinates m=new MapCoordinates();
        
        m.setMagnitudes(zoom, zoom);
        
        int imageWidth = ExportGraphic.getImageSize(CC.P, zoom, false).width;
        npages = (int)Math.floor(((imageWidth-1)/printerWidth));
        
 		/*System.out.println("imageWidth="+imageWidth);
 		System.out.println("printerWidth="+printerWidth);

 		System.out.println("npages="+npages);*/
 
        // Check if we need more than one page
        if (printerWidth<imageWidth) {
            g2d.translate(-(printerWidth*page),0);
        }
        
        // Check if printing is finished.
        if(page>npages) {
            return NO_SUCH_PAGE;
        } 
        // Now we perform our rendering 
        CC.P.draw(g2d, m);
        
        /* tell the caller that this page is part of the printed document */
        return PAGE_EXISTS;
    }
    
    /**  This implementation of the DropTargetListener interface is heavily 
        inspired on the example given here:
        http://www.java-tips.org/java-se-tips/javax.swing/how-to-implement-drag-drop-functionality-in-your-applic.html
    */
    public void dragEnter(DropTargetDragEvent dtde) 
    {
    }

    public void dragExit(DropTargetEvent dte) 
    {
    }

    public void dragOver(DropTargetDragEvent dtde) 
    {
    }

    public void dropActionChanged(DropTargetDragEvent dtde) 
    {
    }

	/** This routine is called when a drag and drop of an useful file is done
		on an open instance of FidoCadJ. The difficulty is that depending on
		the operating system flavor, the files are handled differently. 
		For that reason, we check a few things and we need to differentiate
		several cases.
	*/
    public void drop(DropTargetDropEvent dtde) 
    {
        try {
            Transferable tr = dtde.getTransferable();
            DataFlavor[] flavors = tr.getTransferDataFlavors();
            for (int i = 0; i < flavors.length; i++) {
                if (flavors[i].isFlavorJavaFileListType()) {
                    // Great!  Accept copy drops...
                    dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
    
                    // And add the list of file names to our text area
                    java.util.List list = 
                        (java.util.List)tr.getTransferData(flavors[i]);
                    
                    FidoFrame popFrame;
                    
                    if(CC.P.getModified()) {
                        popFrame = createNewInstance();
                    } else {
                        popFrame=this;
                    }
                    
                    // Only the first file of the list will be opened
                    popFrame.CC.P.openFileName=((File)(list.get(0))).getAbsolutePath();
                    popFrame.openFile();
                    // If we made it this far, everything worked.
                    dtde.dropComplete(true);
                    return;
                }
                // Ok, is it another Java object?
                else if (flavors[i].isFlavorSerializedObjectType()) {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    Object o = tr.getTransferData(flavors[i]);
                    // If there is a valid FidoCad code, try to draw it.
                    FidoFrame popFrame;
                    
                    if(CC.P.getModified()) {
                        popFrame = createNewInstance();
                    } else {
                        popFrame=this;
                    }
                    
                    popFrame.CC.setCirc(new StringBuffer(o.toString()));
                    popFrame.CC.P.saveUndoState();
                    popFrame.CC.P.setModified(false);

                    dtde.dropComplete(true);
                    popFrame.CC.repaint();
                    return;
                }
                // How about an input stream? In some Linux flavors, it contains
                // the file name, with a few substitutions.
                
                else if (flavors[i].isRepresentationClassInputStream()) {
             		// Everything seems to be ok here, so we proceed handling
             		// the file
             		dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    BufferedReader in=new BufferedReader(new InputStreamReader(
                     (InputStream)tr.getTransferData(flavors[i])));

                    String line="";
                    int k;
                    while (line != null){
                        line = in.readLine();
                        if ((k=line.toString().indexOf("file://"))>=0) {
                            FidoFrame popFrame;
                            
                            if(CC.P.getModified()) {
                                popFrame=createNewInstance();
                            } else {
                                popFrame=this;
                            }

                            popFrame.CC.P.openFileName = 
                                line.toString().substring(k+7);
                            
                            // Deprecated! It should indicate the encoding. But
                            // WE WANT the encoding using being the same of the
                            // host system.
                            
                            popFrame.CC.P.openFileName = 
                                java.net.URLDecoder.decode(
                                popFrame.CC.P.openFileName);
                            
                            // After we set the current file name, we just open
                            // it.
                            popFrame.openFile();
                            popFrame.CC.P.saveUndoState();
                            popFrame.CC.P.setModified(false);
                        
                            break;
                        }
                    }
                    CC.repaint();
                    
                    dtde.dropComplete(true);
                    return;
                }
            }
            // Hmm, the user must not have dropped a file list
            System.out.println("Drop failed: " + dtde);
            dtde.rejectDrop();
        } catch (Exception e) {
            e.printStackTrace();
            dtde.rejectDrop();
        }
    }
    
    /** Open the current file
    */
    public void openFile() 
        throws IOException
    {
        
        BufferedReader bufRead = new BufferedReader(
        	new InputStreamReader(new FileInputStream(CC.P.openFileName), 
        	Globals.encoding));
        
                
        StringBuffer txt= new StringBuffer();    
        String line="";
                        
        txt = new StringBuffer(bufRead.readLine());
                        
        txt.append("\n");
                        
        while (line != null){
            line =bufRead.readLine();
            txt.append(line);
            txt.append("\n");
        }
            
        bufRead.close();
                        
        // Here txt contains the new circuit: draw it!
        CC.setCirc(new StringBuffer(txt.toString()));

		// Calculate the zoom to fit     
        zoomToFit();
        CC.P.saveUndoState();
        CC.P.setModified(false);

        repaint();
    }  
    
    /** Show the file dialog and save with a new name name.
    	This routine makes use of the standard dialogs (either the Swing or the
    	native one, depending on the host operating system), in order to let 
    	the user choose a new name for the file to be saved.
    	@return true if the save operation has gone well.

    */
    boolean saveWithName()
    {
        String fin;
        String din;
                       
        if(Globals.useNativeFileDialogs) {
                
            // File chooser provided by the host system.
            // Vastly better on MacOSX, but probably not such on other
            // operating systems.
                    
            FileDialog fd = new FileDialog(this, 
                Globals.messages.getString("SaveName"),
                FileDialog.SAVE);
            fd.setDirectory(openFileDirectory);
            fd.setFilenameFilter(new FilenameFilter(){
                public boolean accept(File dir, String name)
                {
                    return (name.toLowerCase().endsWith(".fcd"));
                }
            });
            fd.setVisible(true);
            fin=fd.getFile();
            din=fd.getDirectory();
        } else {
            // File chooser provided by Swing.
            // Better on Linux
                    
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new javax.swing.filechooser.FileFilter(){
                public boolean accept(File f)
                {
                    return (f.getName().toLowerCase().endsWith(".fcd") ||
                        f.isDirectory());
                }
                public String getDescription()
                {
                    return "FidoCadJ (.fcd)";
                }
            });
            
            // Set the current working directory as well as the file name.
            fc.setCurrentDirectory(new File(openFileDirectory));
            fc.setDialogTitle(Globals.messages.getString("SaveName"));
            if(fc.showSaveDialog(this)!=JFileChooser.APPROVE_OPTION)
                return false;
      
            fin=fc.getSelectedFile().getName();
            din=fc.getSelectedFile().getParentFile().getPath();             
        }
                 
        if(fin!= null) {
            CC.P.openFileName= 
            	Globals.createCompleteFileName(din, fin);
            CC.P.openFileName = Globals.adjustExtension(CC.P.openFileName, 
                    Globals.DEFAULT_EXTENSION);
            if (runsAsApplication)
            	prefs.put("OPEN_DIR", din);   
            
            openFileDirectory=din;
            
            // Here everything is ready for saving the current drawing.     
            return save();
        } else {
        	return false;
        }
    }
    
    /** Save the current file.
    	@return true if the save operation has gone well.
    */
    boolean save()
    {
    	// If there is not a name currently defined, we use instead the 
    	// save with name function.
    	if(CC.P.openFileName.equals("")) {
            return saveWithName();
        }
        try {
            if (splitNonStandardMacro_s) {
                /*  In fact, splitting the nonstandard macro when saving a file
                    is indeed an export operation. This ease the job, since
                    while exporting in a vectorial graphic format one has 
                    indeed to split macros.
                */
                ExportGraphic.export(new File(CC.P.openFileName),  CC.P, 
                    "fcd", 1.0,true,false, extFCJ_s, false);
                CC.P.setModified(false);
    
            } else {
                // Create file 
                BufferedWriter output = new BufferedWriter(new 
                	OutputStreamWriter(new FileOutputStream(CC.P.openFileName), 
                	Globals.encoding));
                
                output.write("[FIDOCAD]\n");
                output.write(CC.getCirc(extFCJ_s).toString());
                output.close();
                CC.P.setModified(false);
            
            }
        } catch (IOException fnfex) {
            JOptionPane.showMessageDialog(this,
            Globals.messages.getString("Save_error")+fnfex);
            return false;
        }
        return true;
    }
    
    /** Load the given file
		@param s the name of the file to be loaded.    
    */
    void load(String s)
    {
        CC.P.openFileName= s;     
        try {
            openFile();
        } catch (IOException fnfex) {
            JOptionPane.showMessageDialog(this,
            Globals.messages.getString("Open_error")+fnfex);
        }
    }
    
    /** Show the FidoCadJ preferences panel
    */
    void showPrefs()
    {
    	String oldDirectory = libDirectory;
    	
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
            CC.getPCB_thickness(),
            CC.getPCB_pad_sizex(),
            CC.getPCB_pad_sizey(),
            CC.getPCB_pad_drill(),
            extFCJ_s,
            extFCJ_c,
            Globals.quaquaActive,
            CC.getStrict(),
            CC.P.getTextFont(),
            splitNonStandardMacro_s,
            splitNonStandardMacro_c,
            Globals.lineWidth,
            Globals.lineWidthCircles,
            Globals.diameterConnection,
            CC.P.getTextFontSize());
                    
        // The panel is now made visible. Its properties will be updated only 
        // if the user clicks on "Ok".
        
        options.setVisible(true);
        CC.profileTime=options.profileTime;
        CC.antiAlias=options.antiAlias;
        textToolbar=options.textToolbar;
        smallIconsToolbar=options.smallIconsToolbar;
                
        CC.getMapCoordinates().setMagnitudes(options.zoomValue, 
                                                       options.zoomValue);
        CC.getMapCoordinates().setXGridStep(options.gridSize); 
        CC.getMapCoordinates().setYGridStep(options.gridSize); 
                
        CC.setPCB_thickness(options.pcblinewidth_i);
        CC.setPCB_pad_sizex(options.pcbpadwidth_i);
        CC.setPCB_pad_sizey(options.pcbpadheight_i);
        CC.setPCB_pad_drill(options.pcbpadintw_i);
        CC.P.setTextFont(options.macroFont,options.macroSize_i);
        
        extFCJ_s = options.extFCJ_s;
        extFCJ_c = options.extFCJ_c;
        splitNonStandardMacro_s = options.split_n_s;
        splitNonStandardMacro_c = options.split_n_c;

        CC.setStrict(options.extStrict);

        Globals.quaquaActive=options.quaquaActive;
    
        libDirectory=options.libDirectory;
        
        Globals.lineWidth = options.stroke_size_straight_i;
        Globals.lineWidthCircles = options.stroke_size_oval_i;
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
            	(CC.getStrict()?"true":"false"));
            
        	prefs.put("SPLIT_N_MACRO_SAVE",
            	(splitNonStandardMacro_s?"true":"false"));
        
        	prefs.put("SPLIT_N_MACRO_COPY",
            	(splitNonStandardMacro_c?"true":"false"));
            	
            prefs.put("GRID_SIZE", ""+CC.getMapCoordinates().getXGridStep());
            
            prefs.put("PCB_pad_sizex", ""+CC.PCB_pad_sizex);
            prefs.put("PCB_pad_sizey", ""+CC.PCB_pad_sizey);
			prefs.put("PCB_pad_style", ""+CC.PCB_pad_style);
            prefs.put("PCB_pad_drill", ""+CC.PCB_pad_drill);
            prefs.put("PCB_thickness", ""+CC.PCB_thickness);

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
        double oldz=CC.getMapCoordinates().getXMagnitude();
        
        // We calculate the zoom to fit factor here.
        MapCoordinates m=ExportGraphic.calculateZoomToFit(CC.P,
            SC.getViewport().getExtentSize().width-35,
            SC.getViewport().getExtentSize().height-35,
            false,true);
            
        double z=m.getXMagnitude();
        
        // We apply the zoom factor to the coordinate transform
        CC.getMapCoordinates().setMagnitudes(z, z);     		 		
	    	
       	// We make the scroll pane show the interesting part of
       	// the drawing.
       	Rectangle r= new Rectangle(-(int)(m.getXCenter()), 
   			-(int)(m.getYCenter()), 
   			SC.getViewport().getExtentSize().width, 
   			SC.getViewport().getExtentSize().height);
   		CC.setScrollRectangle(r);
   		CC.scrollRectToVisible(r);
   		if (oldz!=z) CC.repaint();
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
				new Boolean(CC.P.getModified()));
				
			// On MacOSX >= 10.5, associate an icon and a file proxy to the
			// title bar.
			getRootPane( ).putClientProperty( "Window.documentFile", 
				new File(CC.P.openFileName));
					
			setTitle("FidoCadJ "+Globals.version+" "+ 
            Globals.prettifyPath(CC.P.openFileName,45)+ 
            (CC.P.getModified()?" *":""));
		} else {
			setTitle("FidoCadJ "+Globals.version+" "+ 
            Globals.prettifyPath(CC.P.openFileName,45)+ 
            (CC.P.getModified()?" *":""));
        
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
    }
}

/** This class (currently not used) implements a sliding ruler which could be
	possibly used in conjunction with the scroll panel used for the viewing 
	area.
*/
class RulerPanel extends JPanel implements SwingConstants
{
    private int dir;
    private int increment;
    private MapCoordinates sc;
    
    public RulerPanel(int direction, int width, int height, int incr,
        MapCoordinates m)
    {
        increment=incr;
        sc = m;
        dir = direction;
        setPreferredSize(new Dimension(width, height));
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
        //g.setClip(null);
        Rectangle d = g.getClipBounds();
        //Dimension d = getPreferredSize();
        g.setColor(Color.white);
        g.fillRect(d.x, d.y, d.width, d.height);
        g.setColor(Color.black);

        if (dir==HORIZONTAL) {
            int x=0;
  
            for (x=0; x<sc.unmapXnosnap(sc.getXMax());x+=increment) {
                    
                g.drawLine(sc.mapXi(x,0,false), 
                            sc.mapYi(x,0,false),
                            sc.mapXi(x,0,false),
                            sc.mapYi(x,0,false)+ d.height*4/10);
            }
            
            for (x=0; x<sc.unmapXnosnap(sc.getXMax());x+=5*increment) {
                    
                g.drawLine(sc.mapXi(x,0,false), 
                            sc.mapYi(x,0,false),
                            sc.mapXi(x,0,false),
                            sc.mapYi(x,0,false)+ d.height*6/10);
            }
            for (x=0; x<sc.getXMax();x+=10*increment) {
                g.drawString(""+x,sc.mapXi(x,0,false), d.height); 

                g.drawLine(sc.mapXi(x,0,false), 
                            sc.mapYi(x,0,false),
                            sc.mapXi(x,0,false),
                            sc.mapYi(x,0,false)+ d.height);   
            }    
        } else {
            int y=0;
            int inc=(sc.mapYi(increment, increment, false)-sc.mapYi(0, 0, false));
            for (y=0; y<d.height;y+=inc) {
                g.drawLine(0, y, d.width*4/10, y);
            }
        
            for (y=0; y<d.height;y+=5*inc) {
                g.drawLine(0, y, d.width*8/10, y);
            }
        }
    }
}


