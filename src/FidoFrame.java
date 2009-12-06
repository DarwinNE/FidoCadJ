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



/** FidoFrame.java 

   ****************************************************************************
   Version History 

<pre>
Version   Date           Author       Remarks
-------------------------------------------------------------------------------
1.0     January 2008        D. Bucci    First working version
2.0     May 2008            D. Bucci    Editing possibilities
2.1		July 2008		    D. Bucci	A few nice enhancements
2.2		February 2009		D. Bucci	Aquified 
2.2.1	October 2009		D. Bucci	Force Win L&F when run on Windows
2.2.3	December 2009		D. Bucci	Print as landscape possible

jar cvfm fidoreadj.jar Manifest.txt *.class *.properties


TODO
----------------------------------
    - horizontal scroll of the circuit                  (i, ***)    a Java bug?
    - implement the layer dialog as a non-modal frame   (i, ***)    bof...
    - choose resolution in the print dialog             (d, **)		useful?
    - problems in image export size						(i, **) 	DONE?
    

    
    Symbol  Meaning
    ---------------------------------------------------------------------------
    i       platform independent
    d       risk of platform dependence
    n.v.    will be implemented in the next version :-)
    
    (*)       1 hour of work
    (**)      3 hour of work
    (***)     1 day of work
    (****)    1 week of work
    (*****)   1 month of work
    

Probably, it would be a very good idea to implement the editor with a 
model/vista/controller paradigm. Anyway, it would be a lot of code rearranging
work... I will do it for my NEXT vectorial drawing program :-D
    
    
    
NOTES
-----

   Written by Davide Bucci, davbucci at tiscali dot it
   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
   </pre>

    The FidoFrame class describes a frame which is used to trace schematics
    and printed circuit boards.
    
    @author Davide Bucci
    @version 2.2, February 2009
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
    private CircuitPanel CC;
    private JScrollPane SC;
    private Color sfondo;
    private ToolbarTools toolBar;
    private ToolbarZoom toolZoom;
    
 

    // Export default properties
    private String exportFileName;
    private String exportFormat;
    private boolean exportBlackWhite;
    private double exportUnitPerPixel;
    
    private boolean printMirror;
    private boolean printFitToPage;
    private boolean printLandscape;
    
    //private boolean extStrict; 	// Strict FidoCad compatibility
    private boolean extFCJ_s;	// Use FidoCadJ extensions while saving
    private boolean extFCJ_c;	// Use FidoCadJ extensions while copying
        
        
    // Open/save default properties
    private String openFileName;
    private String openFileDirectory;

    
    // Libraries properties
    private String libDirectory;
    private Preferences prefs;
    
    // Toolbar properties
    private boolean textToolbar;
  	private boolean smallIconsToolbar;
    
    
    // Drag and drop target
    private DropTarget dt;
    
    private JCheckBoxMenuItem optionMacroOrigin;
    
    private int xvalue;
    private int yvalue;
    
    private ScrollGestureRecognizer sgr;
    
    /** The main method. Shows an instance of the FidoFrame */
    public static void main(String[] args)
    {
 
 		Locale currentLocale = Locale.getDefault();
 		
 		// The following code has changed from version 0.20.1.
 		// This way, it should tolerate systems in which resource file for the
 		// current locale is not available. The English interface will be shown.
 		
        try {
        	// Try to load the program with the current locale
        	Globals.messages = ResourceBundle.getBundle("MessagesBundle", 
                currentLocale);
            
        } catch(MissingResourceException mre) {
            try {
            	// If it does not work, try to use the standard English
            	Globals.messages = ResourceBundle.getBundle("MessagesBundle",
            		new Locale("en", "US"));
            	System.out.println("No locale available, sorry... interface will be in English");
            } catch(MissingResourceException mre1) {
            	// Give up!!!
            	JOptionPane.showMessageDialog(null,
                    "Unable to find language localization files: " + mre1);
            	System.exit(1);
            }
      	}        
             
            
       	
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
   	        try { 
       	        
       	        //Globals.quaquaActive=true;
	            //System.setProperty("Quaqua.Debug.showVisualBounds","true");
                //System.setProperty("Quaqua.Debug.showClipBounds","true");
    	        if(Globals.quaquaActive) { 
    	        	UIManager.setLookAndFeel(
        	    		"ch.randelshofer.quaqua.QuaquaLookAndFeel");
            	
              		System.out.println("Quaqua look and feel active");
              	}
              	
         		// set UI manager properties here that affect Quaqua
        	} catch (Exception e) {
         		// Quaqua is not active. Just continue!
         	
         		System.out.println("The Quaqua look and feel is not available");
				System.out.println("I will continue with the basic Apple l&f");
			}
        } else if (System.getProperty("os.name").startsWith("Win")) {
        	/* If the host system is a window system, select the Windows
        	   look and feel. This is a way to encourage people to use 
        	   FidoCadJ even on a Windows system, forgotting about Java.
        	   
        	*/
			try {
        		UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		    } catch (Exception E) {}
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
        
        
        /*******************************************************************/

        
        

        Globals.useNativeFileDialogs=false;
        Globals.useMetaForMultipleSelection=false;
        
        if (System.getProperty("os.name").startsWith("Mac")) {
        	// From what I know, only Mac users expect to use the Command (meta)
        	// key for shortcuts, while others will use Control.
        	Globals.shortcutKey=InputEvent.META_MASK;
        	Globals.useMetaForMultipleSelection=true;
			
        	
        	
        	// Standard dialogs are vastly better on MacOSX than the Swing ones
   	        Globals.useNativeFileDialogs=true;

        } else {
        	Globals.shortcutKey=InputEvent.CTRL_MASK;
        }
        
        /*******************************************************************
        				END OF THE PLATFORM SELECTION CODE
        *******************************************************************/
        
        // Here we create the main window object
        
        FidoFrame popFrame=new FidoFrame();
		
		// Probably, you need to strip this code if you need to compile the
		// program under a non-Apple platform.
		if(Globals.weAreOnAMac) {
			AppleSpecific a=new AppleSpecific();
			a.answerFinder();
		}
        
		// See if there is a filename to open
		if (args.length>=1) {
			popFrame.Load(args[0]);
		}
        popFrame.setVisible(true);

		
		
      
    }
    
    /** The standard constructor: create the frame elements and set up all
        variables */
    public FidoFrame ()
    {
        
        super("FidoCadJ "+Globals.version);
        DialogUtil.center(this, .75,.75,800,500);
        setDefaultCloseOperation(
        	javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE); 

		++Globals.openWindows;


		URL url=DialogAbout.class.getResource(
  			"program_icons/icona_fidocadj_128x128.png");
  		
		// Set icon
		if (url != null) {
    		Image icon = Toolkit.getDefaultToolkit().getImage(url);
    		setIconImage(icon);
        }
        
        // Apparently, this line allows a better Cocoa-like integration
        // under Leopard.
	    this.getRootPane().putClientProperty("apple.awt.brushMetalLook", Boolean.TRUE);
        
        
        Container contentPane=getContentPane();
        
        Globals g=new Globals();
        
        prefs = Preferences.userNodeForPackage(g.getClass());
        libDirectory = prefs.get("DIR_LIBS", "");
        openFileDirectory = prefs.get("OPEN_DIR", "");
        smallIconsToolbar = prefs.get("SMALL_ICON_TOOLBAR", 
        	"true").equals("true");
        textToolbar = prefs.get("TEXT_TOOLBAR", "true").equals("true");
        
        extFCJ_s = prefs.get("FCJ_EXT_SAVE", "true").equals("true");
        extFCJ_c = prefs.get("FCJ_EXT_COPY", "true").equals("true");
        
               
        
        exportFileName=new String();
        exportFormat=new String();
        exportBlackWhite=false;
        openFileName = new String();
        printMirror = false;
        printFitToPage = false;
        printLandscape = false;
        
        
        CC=new CircuitPanel(true);
        dt = new DropTarget(CC, this);
               
        CC.P.loadLibraryDirectory(libDirectory);
        
        if (!(new File(Globals.createCompleteFileName(
        	libDirectory,"IHRAM.FCL"))).exists()) {
   	        CC.P.loadLibraryInJar(FidoFrame.class.getResource("lib/IHRAM.FCL"), "ihram");
        
        } else
        	System.out.println("IHRAM library got from external file");
        if (!(new File(Globals.createCompleteFileName(
        	libDirectory,"FCDstdlib.fcl"))).exists()) {
   	        CC.P.loadLibraryInJar(FidoFrame.class.getResource("lib/FCDstdlib.fcl"), "");

        } else 
        	System.out.println("Standard library got from external file");
        if (!(new File(Globals.createCompleteFileName(
        	libDirectory,"PCB.fcl"))).exists()) {

   	        CC.P.loadLibraryInJar(FidoFrame.class.getResource("lib/PCB.fcl"), "pcb");

		} else
        	System.out.println("Standard PCB library got from external file");

        CC.setPreferredSize(new Dimension(1000,1000));
        SC= new JScrollPane((Component)CC);

		
		sgr = new ScrollGestureRecognizer();
		CC.addScrollGestureSelectionListener(sgr);
        sgr.getInstance();

		
        SC.getVerticalScrollBar().setUnitIncrement(20);
        SC.getHorizontalScrollBar().setUnitIncrement(20);
        Vector LayerDesc=new Vector();
        
        CC.profileTime=false;
        CC.antiAlias=true;
        CC.P.setLayers(LayerDesc);
        CC.setFocusable(true);
        SC.setFocusable(true);
        
        LayerDesc.add(new LayerDesc(Color.black, true, 
            Globals.messages.getString("Circuit_l")));
        LayerDesc.add(new LayerDesc(new Color(0,0,128),true, 
            Globals.messages.getString("Bottom_copper")));
        LayerDesc.add(new LayerDesc(Color.red, true, 
            Globals.messages.getString("Top_copper")));
        LayerDesc.add(new LayerDesc(new Color(0,128,128), true, 
            Globals.messages.getString("Silkscreen")));
        LayerDesc.add(new LayerDesc(Color.orange, true, 
            Globals.messages.getString("Other_1")));
        LayerDesc.add(new LayerDesc(Color.orange, true, 
            Globals.messages.getString("Other_2")));
        LayerDesc.add(new LayerDesc(Color.orange, true, 
            Globals.messages.getString("Other_3")));
        LayerDesc.add(new LayerDesc(Color.orange, true, 
            Globals.messages.getString("Other_4")));
        LayerDesc.add(new LayerDesc(Color.orange, true, 
            Globals.messages.getString("Other_5")));
        LayerDesc.add(new LayerDesc(Color.orange, true, 
            Globals.messages.getString("Other_6")));
        LayerDesc.add(new LayerDesc(Color.orange, true, 
            Globals.messages.getString("Other_7")));
        LayerDesc.add(new LayerDesc(Color.orange, true, 
            Globals.messages.getString("Other_8")));
        LayerDesc.add(new LayerDesc(Color.orange, true, 
            Globals.messages.getString("Other_9")));
        LayerDesc.add(new LayerDesc(Color.orange, true, 
            Globals.messages.getString("Other_10")));
        LayerDesc.add(new LayerDesc(Color.orange, true, 
            Globals.messages.getString("Other_11")));
        LayerDesc.add(new LayerDesc(Color.orange, true, 
            Globals.messages.getString("Other_12")));

        toolBar = new ToolbarTools(textToolbar,smallIconsToolbar);
        toolZoom = new ToolbarZoom(LayerDesc);
        
        toolBar.addSelectionListener(CC);
        toolZoom.addLayerListener(CC);

        toolZoom.addGridStateListener(CC);
        toolZoom.addZoomToFitListener(this);
        
        CC.addChangeZoomListener(toolZoom);
        CC.addChangeSelectionListener(toolBar);
        CC.addChangeCoordinatesListener(toolZoom);
        toolZoom.addChangeZoomListener(CC);

        Box b=Box.createVerticalBox();
        
        
        b.add(toolBar);

        b.add(toolZoom);
        
        MacroTree macroLib = new MacroTree(CC.P.getLibrary(),
        	CC.P.getLayers());
        macroLib.setSelectionListener(CC);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        Dimension windowSize = getSize();
        CC.setPreferredSize(new Dimension(windowSize.width*85/100,100));
        CC.setStrict(prefs.get("FCJ_EXT_STRICT", "false").equals("true"));
       
        splitPane.setTopComponent(SC);
        splitPane.setBottomComponent(macroLib);
		splitPane.setResizeWeight(.9);

        contentPane.add(b,"North");
        contentPane.add(splitPane,"Center");
 
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
        JMenuItem filePrint = new 
            JMenuItem(Globals.messages.getString("Print"));
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

		
		editUndo.addActionListener((ActionListener)this);
		editRedo.addActionListener((ActionListener)this);
		editCut.addActionListener((ActionListener)this);
		editCopy.addActionListener((ActionListener)this);
		editPaste.addActionListener((ActionListener)this);
		editSelectAll.addActionListener((ActionListener)this);
		
		editMenu.add(editUndo);
		editMenu.add(editRedo);
		editMenu.addSeparator();
		
		editMenu.add(editCut);
		editMenu.add(editCopy);
		editMenu.add(editPaste);
        editMenu.add(clipboardCircuit);

		editMenu.addSeparator();
		
		editMenu.add(editSelectAll);
	
		menuBar.add(editMenu);
		
		JMenu viewMenu=new JMenu(Globals.messages.getString("View"));
        JMenuItem layerOptions = new 
            JMenuItem(Globals.messages.getString("Layer_opt"));
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
        viewMenu.add(optionMacroOrigin);
        
        optionMacroOrigin.addActionListener((ActionListener)this);
        layerOptions.addActionListener((ActionListener)this);
        optionCircuit.addActionListener((ActionListener)this);

        menuBar.add(viewMenu);

        JMenu circuitMenu=new JMenu(Globals.messages.getString("Circuit"));
        JMenuItem defineCircuit = new 
            JMenuItem(Globals.messages.getString("Define"));
        
        
        
        circuitMenu.add(defineCircuit);

        defineCircuit.addActionListener((ActionListener)this);
        clipboardCircuit.addActionListener((ActionListener)this);


        menuBar.add(circuitMenu);
        
 /*       JMenu windowsMenu=new JMenu(Globals.messages.getString("Window"));
        JMenuItem windowNew = new 
        	JMenuItem(Globals.messages.getString("New"));
        //fileNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
        //	Globals.shortcutKey));
        windowsMenu.add(windowNew);
        windowNew.addActionListener((ActionListener)this);

        
        menuBar.add(windowsMenu);
*/

        JMenu about = new JMenu(Globals.messages.getString("About"));
        JMenuItem aboutMenu = new 
            JMenuItem(Globals.messages.getString("About_menu"));
        about.add(aboutMenu);
        
        // On a MacOSX system, this menu is associated to preferences menu
        // in the application menu. We do not need to show it in bar.
        // This needs the AppleSpecific extensions to be active.
        
        if(!Globals.weAreOnAMac)
        	menuBar.add(about);
        aboutMenu.addActionListener((ActionListener)this);


		CC.P.setHasChangedListener(this);
        
        
        if (true){
            /*  Add a window listener to close the application when the frame is
                closed. This behaviour is platform dependent, for example a 
                Macintosh application can be made run without a visible frame.
                There would anyway the need to customize the menu bar, in order 
                to allow the user to open a new FidoFrame, when it has been
                closed once. The easiest solution to implement is therefore to
                make the application close when the user closes the frame. 
            */
            addWindowListener(new WindowAdapter()
            {
                public void windowClosing(WindowEvent e)
                {
      	            if (CC.P.getModified()) {
      	            	if(JOptionPane.showConfirmDialog(null, 
							Globals.messages.getString("Warning_unsaved"),
							Globals.messages.getString("Warning"),
							JOptionPane.OK_CANCEL_OPTION, 
							JOptionPane.WARNING_MESSAGE)!=JOptionPane.OK_OPTION)
						{
							return;	
						}
					}
					
					setVisible(false);
					dispose();

                	--Globals.openWindows;
                    
                    if (Globals.openWindows<1)
                    	System.exit(0);
                }
            });

        }
        
        //pack();
        addWindowFocusListener(this);
		Globals.activeWindow=this;
        
       
    }
    
    /** The action listener. Recognize menu events and behaves consequently.
    
    */
    public void actionPerformed(ActionEvent evt)
    {
        // Recognize and handle menu events
        if(evt.getSource() instanceof JMenuItem) 
        {
            String arg=evt.getActionCommand();

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
            
            if (arg.equals(Globals.messages.getString("Circ_opt"))) {
                showPrefs();
            }
            if (arg.equals(Globals.messages.getString("Layer_opt"))) {
                DialogLayer layerDialog=new DialogLayer(this,CC.P.getLayers());
                layerDialog.setVisible(true);
                repaint();
                
            }
            if (arg.equals(Globals.messages.getString("Print"))) {
                DialogPrint dp=new DialogPrint(this);
                dp.setMirror(printMirror);
                dp.setFit(printFitToPage);
                dp.setBW(exportBlackWhite);
                dp.setLandscape(printLandscape);
                dp.setVisible(true);
                printMirror = dp.getMirror();
                printFitToPage = dp.getFit();
                printLandscape = dp.getLandscape();
                exportBlackWhite= dp.getBW();
                
		
				Vector ol=CC.P.getLayers();
                if (dp.shouldPrint()) {
                	if(exportBlackWhite) {
						Vector v=new Vector();
						for (int i=0; i<16;++i)
							v.add(new LayerDesc(Color.black, 
								((LayerDesc)ol.get(i)).getVisible(),
								"B/W"));
			
						CC.P.setLayers(v);
					}
                    PrinterJob job = PrinterJob.getPrinterJob();
                    job.setPrintable(this);
                    boolean ok = job.printDialog();
                    if (ok) {
                        try {
                        
     					    PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        					if (!printLandscape) {
            					aset.add(OrientationRequested.PORTRAIT);
        					} else {
            					aset.add(OrientationRequested.LANDSCAPE);
        					}
    
                            job.print(aset);
                        } catch (PrinterException ex) {
                        /* The job did not successfully complete */
                            JOptionPane.showMessageDialog(this,
                                Globals.messages.getString("Print_uncomplete"));
                        }
                    }
                    CC.P.setLayers(ol);
                    
                }
            }
           
            if (arg.equals(Globals.messages.getString("SaveName"))) {
            	SaveWithName();

                
            }  
            if (arg.equals(Globals.messages.getString("Save"))) {
            	
            	
                if(openFileName.equals(""))
                	SaveWithName();
                else 
                	Save();
                 
                
            }
            	
            if (arg.equals(Globals.messages.getString("New"))) {
            	FidoFrame popFrame=new FidoFrame();
            	
        		popFrame.setBounds(getX()+30, getY()+30, popFrame.getWidth(), 		
        			popFrame.getHeight());
        		popFrame.setVisible(true);
            	
            	/*try {
                	CC.setCirc(new StringBuffer(""));
	                CC.P.saveUndoState();
                } catch (IOException E) {}
                openFileName="";
                repaint();*/
           	}
           	if (arg.equals(Globals.messages.getString("Undo"))) {
                CC.P.undo();
                repaint();
           	}
           	if (arg.equals(Globals.messages.getString("Redo"))) {
                CC.P.redo();
                repaint();
           	}
           	
           	if (arg.equals(Globals.messages.getString("About_menu"))) {
           		DialogAbout d=new DialogAbout(this);
				d.setVisible(true);
           	}
            if (arg.equals(Globals.messages.getString("Open"))) {
            
            	String fin;
            	String din;
            	
            	if(Globals.useNativeFileDialogs) {
            	
            		// File chooser provided by the host system.
            		// Vastly better on MacOSX
            		
            		FileDialog fd = new FileDialog(this, 
                    	Globals.messages.getString("Open"));
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
                	fc.setCurrentDirectory(new File(openFileDirectory));
                	fc.setDialogTitle(Globals.messages.getString("Open"));
                	fc.setFileFilter(new javax.swing.filechooser.FileFilter(){
    					public boolean accept(File f)
    					{
      						return (f.getName().toLowerCase().endsWith(".fcd")||
      							f.isDirectory());
    					}
    					public String getDescription()
    					{
    						return "FidoCadJ (.fcd)";
    					}
 					});
                	
                	if(fc.showOpenDialog(this)!=JFileChooser.APPROVE_OPTION)
                		return;
                		              	
                	
                	fin=fc.getSelectedFile().getName();
                	din=fc.getSelectedFile().getParentFile().getPath();
                
                }
                
                
                    
                if(fin!= null) {
                	
                    // An open file action has been performed.
                    // Reads the file, line by line and stores it in memory
                    try {
                    	FidoFrame popFrame;
                    	if(CC.P.getModified() || !CC.P.isEmpty()) {
                    		popFrame=new FidoFrame();
                    		popFrame.setBounds(getX()+30, getY()+30, 	
        					popFrame.getWidth(), 		
        					popFrame.getHeight());
	        				popFrame.setVisible(true);
            	
                    	} else {
                    		popFrame=this;
                    	}
            			popFrame.openFileName= Globals.createCompleteFileName(
                			din, 			fin);
                		prefs.put("OPEN_DIR", din);  
                		popFrame.openFileDirectory=din;
                        popFrame.openFile();
                        popFrame.CC.P.saveUndoState();
                        popFrame.CC.P.setModified(false);

                    } catch (IOException fnfex) {
                        JOptionPane.showMessageDialog(this,
                            Globals.messages.getString("Open_error")+fnfex);
                    }
                }
                
            }
           	if (arg.equals(Globals.messages.getString("Export"))) {
                
                DialogExport export=new DialogExport(this);
                export.setAntiAlias(true);
                export.setFormat(exportFormat);
                export.setFileName(exportFileName);
                export.setUnitPerPixel(exportUnitPerPixel);
                export.setBlackWhite(exportBlackWhite);
                
                export.setVisible(true);      // Show the modal dialog
                if (export.shouldExport()) {
                    exportFileName=export.getFileName();
                    exportFormat=export.getFormat();
                    exportUnitPerPixel=export.getUnitPerPixel();
                    exportBlackWhite=export.getBlackWhite();
                    exportFileName = Globals.checkExtension(exportFileName, 
                    	exportFormat);
                    try {
                        ExportGraphic.export(new File(exportFileName),  CC.P, 
                            exportFormat, exportUnitPerPixel, 
                            export.getAntiAlias(),exportBlackWhite);
                        JOptionPane.showMessageDialog(this,
                            Globals.messages.getString("Export_completed"));

                    } catch(IOException ioe) {
                        JOptionPane.showMessageDialog(this,
                            Globals.messages.getString("Export_error")+ioe);
                    } catch(IllegalArgumentException iae) {
                        JOptionPane.showMessageDialog(this,
                            Globals.messages.getString("Illegal_filename"));
                    }
                }
            }
            if (arg.equals(Globals.messages.getString("SelectAll"))) {
        		CC.P.selectAll();	
        		repaint();
        	}
        
        	if (arg.equals(Globals.messages.getString("Copy"))) {
        		CC.P.copySelected(extFCJ_c);	
      		}
            if (arg.equals(Globals.messages.getString("Cut"))) {
        		CC.P.copySelected(extFCJ_c);	
        		CC.P.deleteAllSelected();
        		repaint();
      		}
            
            if (arg.equals(Globals.messages.getString("Macro_origin"))) {
        		CC.P.setMacroOriginVisible(optionMacroOrigin.isSelected());	
        		repaint();
      		}
            
            if (arg.equals(Globals.messages.getString("DefineClipboard"))) {
                TextTransfer textTransfer = new TextTransfer();
                try {
                	FidoFrame popFrame;
                    if(CC.P.getModified()) {
                    	popFrame=new FidoFrame();
                    	popFrame.setBounds(getX()+30, getY()+30, 	
        				popFrame.getWidth(), 		
        				popFrame.getHeight());
	        			popFrame.setVisible(true);
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
            
        	if (arg.equals(Globals.messages.getString("Paste"))) {
        		CC.P.paste();	
        		repaint();
        	}
        	
        	if (arg.equals(Globals.messages.getString("Close"))) {
        	    if (CC.P.getModified()) {
      	         	if(JOptionPane.showConfirmDialog(null, 
						Globals.messages.getString("Warning_unsaved"),
						Globals.messages.getString("Warning"),
						JOptionPane.OK_CANCEL_OPTION, 
						JOptionPane.WARNING_MESSAGE)!=JOptionPane.OK_OPTION)
					{
						return;	
					}
				}
					
				setVisible(false);
				dispose();

               	--Globals.openWindows;
                    
                if (Globals.openWindows<1)
                   	System.exit(0);
            }
        
        }
             
	   	
    }
    
    
    public void menuSelected(MenuEvent evt)
    {
    }
    public void menuDeselected(MenuEvent evt)
    {
    }
    public void menuCanceled(MenuEvent evt)
    {
    }
    

      /** The print interface */
    public int print(Graphics g, PageFormat pf, int page) throws
                                                   PrinterException 
    {
		int npages = 0;
                
        double xscale = 1.0/16; // Set 1152 logical units for an inch
        double yscale = 1.0/16; // as the standard resolution is 72
        double zoom = 5.76;     // act in a 1152 dpi resolution as 1:1
               
        Graphics2D g2d = (Graphics2D)g;
        MapCoordinates zoomm=new MapCoordinates();

        
        /* User (0,0) is typically outside the imageable area, so we must
         * translate by the X and Y values in the PageFormat to avoid clipping
         */
         
        if (printMirror) {
        	g2d.translate(pf.getImageableX()+pf.getImageableWidth(),
                pf.getImageableY());
            g2d.scale(-xscale,yscale); 
            
        } else {
           	g2d.translate(pf.getImageableX(), pf.getImageableY());
   			g2d.scale(xscale,yscale); 
        }	
        
       
        double om=CC.P.getMapCoordinates().getXMagnitude();
        int printerWidth = ((int)pf.getImageableWidth()*16);

        if (printFitToPage) {
        	CC.P.getMapCoordinates().setMagnitudes(1,1);
  		    CC.P.getMapCoordinates().setMagnitudes(1,1);
  		    CC.P.getMapCoordinates().setXCenter(0);
  		    CC.P.getMapCoordinates().setYCenter(0);
  		    
  		    zoomm=ExportGraphic.calculateZoomToFit(CC.P, 
            	(int)pf.getImageableWidth()*16, (int)pf.getImageableHeight()*16, 
                	true,false);
            
            /*
            Dimension D = ExportGraphic.getImageSize(CC.P,1,true); 
			double zoomx = pf.getImageableWidth()*16/D.width;
			double zoomy = pf.getImageableHeight()*16/D.height;
			
			zoom = (zoomx<zoomy)?zoomx:zoomy;*/
			zoom=zoomm.getXMagnitude();
        }
         
        MapCoordinates m=CC.P.getMapCoordinates();
        
        m.setMagnitudes(zoom, zoom);
        /*m.setXCenter(zoomm.getXCenter());
        m.setYCenter(zoomm.getYCenter());*/
        
        int imageWidth = ExportGraphic.getImageSize(CC.P, zoom, false).width;
            /*-
   			ExportGraphic.getImageOrigin(CC.P, zoom).x;*/
 
        npages = (int)Math.floor(((imageWidth-1)/printerWidth));
/*
   		System.out.println("Page: "+page);
   		System.out.println("ImageWidth: "+imageWidth);
   		System.out.println("PrinterWidth: "+printerWidth);
   		System.out.println("Zoom: "+zoom);
 		System.out.println("Npages: "+npages);
*/        
        // Check if we need more than one page
        if (printerWidth<imageWidth) {
			g2d.translate(-(printerWidth*page),0);
        }
        
        if(page>npages) {
        	CC.P.getMapCoordinates().setMagnitudes(om,om);
        	return NO_SUCH_PAGE;
        }

        CC.P.setMapCoordinates(m);
        
        // Now we perform our rendering 
        CC.P.draw(g2d);
        
        //m.xCenter = 0;
        CC.P.getMapCoordinates().setMagnitudes(om,om);
        
        /* tell the caller that this page is part of the printed document */
        return PAGE_EXISTS;
    }
    
    /*  This implementation of the DropTargetListener interface is heavily 
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
                    	popFrame=new FidoFrame();
                    	popFrame.setBounds(getX()+30, getY()+30, 	
        				popFrame.getWidth(), 		
        				popFrame.getHeight());
	        			popFrame.setVisible(true);
                    } else {
                    	popFrame=this;
                    }
    				
    				// Only the first file of the list will be opened
    				popFrame.openFileName=((File)(list.get(0))).getAbsolutePath();
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
                    	popFrame=new FidoFrame();
                    	popFrame.setBounds(getX()+30, getY()+30, 	
        				popFrame.getWidth(), 		
        				popFrame.getHeight());
	        			popFrame.setVisible(true);
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
  				// How about an input stream? In some Linux flawors, it contains
  				// the file name, with a few substitutions.
  				
  				else if (flavors[i].isRepresentationClassInputStream()) {
    				dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
    				//System.out.println("Successful text drop.\n\n");
    				BufferedReader in=new BufferedReader(new InputStreamReader(
                 	 (InputStream)tr.getTransferData(flavors[i])));
      				
     				
      				String line="";
                    int k;
                  
                        
        			while (line != null){
            			line = in.readLine();
            			if ((k=line.toString().indexOf("file://"))>=0) {
            			    FidoFrame popFrame;
							
							if(CC.P.getModified()) {
                    			popFrame=new FidoFrame();
                    			popFrame.setBounds(getX()+30, getY()+30, 	
        						popFrame.getWidth(), 		
        						popFrame.getHeight());
	        					popFrame.setVisible(true);
            	
                    		} else {
                    			popFrame=this;
                    		}

            				popFrame.openFileName = 
            					line.toString().substring(k+7);
            				
            				// Deprecated! it should indicate encoding. But
            				// WE WANT the encoding using being the same of the
            				// host system.
            				
            				popFrame.openFileName = 
            					java.net.URLDecoder.decode(
            					popFrame.openFileName);
            			                  		
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
    	FileReader input = new FileReader(openFileName);
        BufferedReader bufRead = new BufferedReader(input);
                
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
        
        // Calculate the zoom to fit.
        CC.P.getMapCoordinates().setMagnitudes(1,1);
                        
                        
        /* SCHIFIO *******************************************/
        // Se si usa direttamente m, sembra che tutto sia
        // scombinato...
                        
        MapCoordinates m=ExportGraphic.calculateZoomToFit(CC.P,
        	SC.getViewport().getExtentSize().width,
        	SC.getViewport().getExtentSize().height,
            true,false);
        MapCoordinates mi=CC.P.getMapCoordinates();
        double Z=Math.round(m.getXMagnitude()*100)/100;
                        
        mi.setMagnitudes(Z,Z);
        CC.P.setMapCoordinates(mi);
        CC.P.saveUndoState();
        CC.P.setModified(false);

        repaint();
    }  
    
    /** Show the file dialog and save with the specified name
    
    */
    
    void SaveWithName()
    {
        String fin;
        String din;
        
        
            	
        if(Globals.useNativeFileDialogs) {
            	
        	// File chooser provided by the host system.
        	// Vastly better on MacOSX
            		
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
           	fc.setCurrentDirectory(new File(openFileDirectory));
           	fc.setDialogTitle(Globals.messages.getString("SaveName"));
           	if(fc.showSaveDialog(this)!=JFileChooser.APPROVE_OPTION)
           		return;
                		              	
                	
           	fin=fc.getSelectedFile().getName();
           	din=fc.getSelectedFile().getParentFile().getPath();
                
        }
                
                
        if(fin!= null) {
           	openFileName= Globals.createCompleteFileName(
           		din,
           		fin);
          	openFileName = Globals.checkExtension(openFileName, 
               		Globals.DEFAULT_EXTENSION);
            prefs.put("OPEN_DIR", din);   
    			openFileDirectory=din;
            
            Save();

        }
    }
    
    /** Save the current file
    
    */
    void Save()
    {
    	try {
           	// Create file 
   			FileWriter fstream = new FileWriter(openFileName);
       		BufferedWriter output = new BufferedWriter(fstream);
    		output.write("[FIDOCAD]\n");
    		output.write(CC.getCirc(extFCJ_s).toString());
    		output.close();
            CC.P.setModified(false);

    				
                   
        } catch (IOException fnfex) {
            JOptionPane.showMessageDialog(this,
            Globals.messages.getString("Save_error")+fnfex);
        }
    
    }
    
    /** Load the given file
    
    */
    void Load(String s)
    {
    	openFileName= s;
               
        try {
            openFile();
        } catch (IOException fnfex) {
            JOptionPane.showMessageDialog(this,
            Globals.messages.getString("Open_error")+fnfex);
        }
    }
    
    /**	Show the FidoCadJ preferences panel
    
    */
    void showPrefs()
    {
    	DialogOptions options=new DialogOptions(this,
            CC.P.getMapCoordinates().getXMagnitude(),
            CC.profileTime,CC.antiAlias,
            CC.P.getMapCoordinates().getXGridStep(),
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
            CC.P.getMacroFont());
                    
        options.setVisible(true);
        CC.profileTime=options.profileTime;
        CC.antiAlias=options.antiAlias;
        textToolbar=options.textToolbar;
        smallIconsToolbar=options.smallIconsToolbar;
                
        CC.P.getMapCoordinates().setMagnitudes(options.zoomValue, 
                                                       options.zoomValue);
        CC.P.getMapCoordinates().setXGridStep(options.gridSize); 
        CC.P.getMapCoordinates().setYGridStep(options.gridSize); 
                
        CC.setPCB_thickness(options.pcblinewidth_i);
        CC.setPCB_pad_sizex(options.pcbpadwidth_i);
        CC.setPCB_pad_sizey(options.pcbpadheight_i);
		CC.setPCB_pad_drill(options.pcbpadintw_i);
		CC.P.setMacroFont(options.macroFont);
		
		extFCJ_s = options.extFCJ_s;
		extFCJ_c = options.extFCJ_c;
        CC.setStrict(options.extStrict);

		Globals.quaquaActive=options.quaquaActive;
  	
        libDirectory=options.libDirectory;
        prefs.put("DIR_LIBS", libDirectory);                       
    	prefs.put("SMALL_ICON_TOOLBAR", 
    		(smallIconsToolbar?"true":"false"));
        	
    	prefs.put("TEXT_TOOLBAR",
    		(textToolbar?"true":"false"));
     
    	prefs.put("QUAQUA",
    		(Globals.quaquaActive?"true":"false"));
    	prefs.put("FCJ_EXT_STRICT",
    		(CC.getStrict()?"true":"false"));
    
     
        repaint();
    }
    
    /** Set the current zoom to fit
	
	*/
	public void zoomToFit()
	{
		double oldz=CC.P.getMapCoordinates().getXMagnitude();
		
		MapCoordinates m=ExportGraphic.calculateZoomToFit(CC.P,
        	SC.getViewport().getExtentSize().width-35,
            SC.getViewport().getExtentSize().height-35,
            false,false);
            
        double z=m.getXMagnitude();
        
        CC.P.getMapCoordinates().setMagnitudes(z, z);
       /* CC.P.getMapCoordinates().setXCenter(m.getXCenter());
        CC.P.getMapCoordinates().setYCenter(m.getYCenter());
        */
        
        if (oldz!=z) CC.repaint();

	}
	
	
	public void somethingHasChanged()
	{
    	setTitle("FidoCadJ "+Globals.version+" "+ 
	   		Globals.prettifyPath(openFileName,45)+ 
	   		(CC.P.getModified()?" *":""));
	}
	
	public void windowGainedFocus(WindowEvent e) 
	{
		Globals.activeWindow = this;
	}
	
	public void windowLostFocus(WindowEvent e) 
	{
	
	}
}