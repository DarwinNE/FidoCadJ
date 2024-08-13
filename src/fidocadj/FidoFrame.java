package fidocadj;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowFocusListener;

import javax.swing.*;

import java.io.*;
import java.util.*;
import java.net.*;
import java.util.prefs.*;

import fidocadj.dialogs.DialogUtil;
import fidocadj.dialogs.DialogOptions;
import fidocadj.dialogs.DialogAbout;
import fidocadj.geom.DrawingSize;
import fidocadj.geom.MapCoordinates;
import fidocadj.globals.Globals;
import fidocadj.globals.AccessResources;
import fidocadj.globals.Utf8ResourceBundle;
import fidocadj.globals.LibUtils;
import fidocadj.circuit.HasChangedListener;
import fidocadj.circuit.CircuitPanel;
import fidocadj.circuit.controllers.CopyPasteActions;
import fidocadj.circuit.controllers.AddElements;
import fidocadj.circuit.controllers.ParserActions;
import fidocadj.circuit.controllers.ElementsEdtActions;
import fidocadj.toolbars.ToolbarZoom;
import fidocadj.toolbars.ToolbarTools;
import fidocadj.toolbars.ZoomToFitListener;
import fidocadj.macropicker.MacroTree;
import fidocadj.librarymodel.LibraryModel;
import fidocadj.layermodel.LayerModel;
import fidocadj.layers.StandardLayers;
import fidocadj.layers.LayerDesc;
import fidocadj.librarymodel.utils.CircuitPanelUpdater;
import fidocadj.librarymodel.utils.LibraryUndoExecutor;

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
    along with FidoCadJ. If not,
    @see <a href=http://www.gnu.org/licenses/>http://www.gnu.org/licenses/</a>.

    Copyright 2008-2023 by Davide Bucci
    </pre>

    The FidoFrame class describes a frame which is used to trace schematics
    and printed circuit boards.

    @author Davide Bucci
*/

public final class FidoFrame extends JFrame implements
                                            ActionListener,
                                            ZoomToFitListener,
                                            HasChangedListener,
                                            WindowFocusListener
{
    // Interface elements parts of FidoFrame

    // The circuit panel...
    public CircuitPanel circuitPanel;
    // ... which is contained in a scroll pane.
    private JScrollPane scrollPane;
    // ... which at its turn is in a split pane.
    private JSplitPane splitPane;
    // Macro picker component
    MacroTree macroLib;

    // Macro library model
    private LibraryModel libraryModel;

    // Objects which regroup a certain number of actions somewhat related
    // to the FidoFrame object in different domains.
    final private ExportTools exportTools;
    final private PrintTools printTools;
    final private MenuTools menuTools;
    //final private DragDropTools dt;
    final private FileTools fileTools;

    // Libraries properties
    public String libDirectory;
    public Preferences preferences;

    // Toolbar properties
    // The toolbar dedicated to the available tools (the first one under
    // thewindow title).
    private ToolbarTools toolBar;
    // The second toolbar dedicated to the zoom factors and other niceties
    // (the second one under the window title).
    ToolbarZoom toolZoom;

    // Text description under icons
    private boolean textToolbar;
    // Small (16x16 pixel) icons instead of standard (32x32 pixel)
    private boolean smallIconsToolbar;

    // Locale settings
    public Locale currentLocale;
    // Runs as an application or an applet.
    public boolean runsAsApplication;

    /** The standard constructor: create the frame elements and set up all
        variables. Note that the constructor itself is not sufficient for
        using the frame. You need to call the init procedure after you have
        set the configuration variables available for FidoFrame.

        @param appl should be true if FidoCadJ is run as a stand alone
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

        currentLocale = registerLocale(loc);

        getRootPane().putClientProperty("Aqua.windowStyle", "combinedToolBar");

        prepareLanguageResources();
        Globals.configureInterfaceDetailsFromPlatform(InputEvent.META_MASK,
            InputEvent.CTRL_MASK);

        DialogUtil.center(this, .75,.75,800,500);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        // We need to keep track of the number of open windows. If the last
        // one is closed, we exit from the program.

        ++Globals.openWindowsNumber;
        Globals.openWindows.add(this);

        setIconForApplication();

        if (runsAsApplication) {
            // Prepare the preferences associated to the FidoFrame class
            preferences = Preferences.userNodeForPackage(this.getClass());
        } else {
            // If we can not access to the preferences, we inizialize those
            // configuration variables with default values.
            libDirectory = System.getProperty("user.home");
            smallIconsToolbar = false;
            textToolbar = true;
            preferences=null;
        }
        exportTools = new ExportTools(preferences);
        printTools = new PrintTools();
        menuTools = new MenuTools();
        new DragDropTools(this);
        fileTools = new FileTools(this, preferences);

        readPreferences();
        // In practice, we need to restore the size of the open current window
        // only for the first window.
        if (Globals.openWindowsNumber==1) {
            restorePosition();
        }
    }

    /** By implementing writeObject method,
    // we can prevent
    // subclass from serialization
    */
    private void writeObject(ObjectOutputStream out) throws IOException
    {
        throw new NotSerializableException();
    }

    /* By implementing readObject method,
    // we can prevent
    // subclass from de-serialization
    */
    private void readObject(ObjectInputStream in) throws IOException
    {
        throw new NotSerializableException();
    }

    /** Store location & size of UI.
        vaguely based on: http://stackoverflow.com/questions/7777640/\
            best-practice-for-setting-jframe-locations
    */
    public void savePosition()
    {
        if (!runsAsApplication) {
            return;
        }

        int state=getExtendedState();
        // restore the frame from 'full screen' first!
        setExtendedState(NORMAL);
        Rectangle r = getBounds();
        int x = (int)r.getX();
        int y = (int)r.getY();
        int w = (int)r.getWidth();
        int h = (int)r.getHeight();

        preferences.put("FRAME_POSITION_X", "" + x);
        preferences.put("FRAME_POSITION_Y", "" + y);
        preferences.put("FRAME_WIDTH", "" + w);
        preferences.put("FRAME_HEIGHT", "" + h);
        preferences.put("FRAME_STATE", ""+state);
    }

    /** Restore location & size of UI
    */
    public void restorePosition()
    {
        if (!runsAsApplication) {
            return;
        }

        try{
            int x = Integer.parseInt(preferences.get("FRAME_POSITION_X","no"));
            int y = Integer.parseInt(preferences.get("FRAME_POSITION_Y","no"));
            int w = Integer.parseInt(preferences.get("FRAME_WIDTH","no"));
            int h = Integer.parseInt(preferences.get("FRAME_HEIGHT","no"));
            int state=Integer.parseInt(preferences.get("FRAME_STATE","no"));
            if((state & MAXIMIZED_HORIZ)!=0 ||
                (state & MAXIMIZED_VERT)!=0)
            {
                setExtendedState(state);
            } else {
                Rectangle r = new Rectangle(x,y,w,h);
                setBounds(r);
            }
        } catch (NumberFormatException eE) {
            System.out.println("Choosing default values for frame size");
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

    /** Retrieve the program icon and associate it to the window.
    */
    private void setIconForApplication()
    {
        URL url=DialogAbout.class.getResource(
            "/icons/icona_fidocadj_128x128.png");

        if (url == null) {
            System.err.println("Could not retrieve the FidoCadJ icon!");
        } else {
            Image icon = Toolkit.getDefaultToolkit().getImage(url);
            setIconImage(icon);
        }
    }

    /** Check if a locale has been specified. If not, get the operating
        system's locale and employ this as the current locale.
        @param loc the desired locale, or null if the system one has to be
            employed.
    */
    private Locale registerLocale(Locale loc)
    {
        String systemLanguage = Locale.getDefault().getLanguage();
        Locale newLocale;

        if(loc==null) {
            // Make sort that only the language is used for the current
            newLocale = new Locale(systemLanguage);
        } else {

            newLocale = loc;
            if(!loc.getLanguage().equals(systemLanguage)) {
                System.out.println("Forcing the locale to be: " +loc+
                    " instead of: "+systemLanguage);
            }
        }
        return newLocale;
    }

    /** Get the locale employed by this instance.
        @return the current locale.
    */
    @Override public Locale getLocale()
    {
        return currentLocale;
    }

    /** Get the ExportTools object (containing the code related to interface
        for exporting files).
        @return the ExportTools object.
    */
    public ExportTools getExportTools()
    {
        return exportTools;
    }

    /** Get the PrintTools object (containing the code related to interface
        for printing drawings).
        @return the PrintTools object.
    */
    public PrintTools getPrintTools()
    {
        return printTools;
    }

    /** Get the FileTools object (containing the code related to interface
        for loading and saving drawings).
        @return the FileTools object.
    */
    public FileTools getFileTools()
    {
        return fileTools;
    }

    /** Read the preferences settings (mainly at startup or when a new
        editing window is created.
        If no preferences settings are accessible, does nothing.
    */
    public void readPreferences()
    {
        if(preferences==null) {
            return;
        }

        // The library directory
        libDirectory = preferences.get("DIR_LIBS", "");

        // The icon size
        String defaultSize="";

        // Check the screen resolution. Now (April 2015), a lot of very high
        // resolution screens begin to be widespread. So, if the pixel
        // density is greater than 150 dpi, bigger icons are used by at the
        // very first time FidoCadJ is run.
        /*if(java.awt.Toolkit.getDefaultToolkit().getScreenResolution()>150) {
            defaultSize="false";
        } else {
            defaultSize="true";
        }*/
        // 2020 I suspect the best result is now obtained with "false".
        defaultSize="false";

        smallIconsToolbar = "true".equals(preferences.get("SMALL_ICON_TOOLBAR",
                defaultSize));
        // Presence of the text description in the toolbar
        textToolbar = "true".equals(preferences.get("TEXT_TOOLBAR", "true"));

        // Read export preferences
        exportTools.readPrefs();
        // Read file preferences
        fileTools.readPrefs();

        // Element sizes
        Globals.lineWidth=Double.parseDouble(
                preferences.get("STROKE_SIZE_STRAIGHT", "0.5"));
        
        Globals.lineWidthCircles=Double.parseDouble(
                preferences.get("STROKE_SIZE_OVAL", "0.35"));
        
        Globals.diameterConnection=Double.parseDouble(
                preferences.get("CONNECTION_SIZE", "2.0"));
    }

    /** Load the saved configuration for the grid.
    */
    public void readGridSettings()
    {
        circuitPanel.getMapCoordinates().setXGridStep(
                Integer.parseInt(preferences.get("GRID_SIZE", "5")));
        
        circuitPanel.getMapCoordinates().setYGridStep(
                Integer.parseInt(preferences.get("GRID_SIZE", "5")));
    }

    /** Load the saved configuration for the drawing primitives and zoom.
    */
    public void readDrawingSettings()
    {
        CopyPasteActions cpa = circuitPanel.getCopyPasteActions();

        // Shift elements when copy/pasting them.
        cpa.setShiftCopyPaste("true".equals(preferences.get("SHIFT_CP","true")));
        AddElements ae=circuitPanel.getContinuosMoveActions().getAddElements();

        // Default PCB sizes (pad/line)
        ae.pcbPadSizeX=Integer.parseInt(preferences.get("PCB_pad_sizex","10"));
        ae.pcbPadSizeY=Integer.parseInt(preferences.get("PCB_pad_sizey","10"));
        ae.pcbPadStyle=Integer.parseInt(preferences.get("PCB_pad_style","0"));
        ae.pcbPadDrill=Integer.parseInt(preferences.get("PCB_pad_drill","5"));
        ae.pcbThickness=Integer.parseInt(preferences.get("PCB_thickness","5"));

        MapCoordinates mc=circuitPanel.getMapCoordinates();
        double z=Double.parseDouble(preferences.get("CURRENT_ZOOM","4.0"));
        mc.setMagnitudes(z,z);
    }

    /** Load the standard libraries according to the locale.
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
        circuitPanel.getDrawingModel().resetLibrary();
        ParserActions pa=circuitPanel.getParserActions();

        if(runsAsApplication) {
            FidoMain.readLibrariesProbeDirectory(circuitPanel.getDrawingModel(),
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
        Container contentPane=getContentPane();
        circuitPanel=new CircuitPanel(true);
        circuitPanel.getParserActions().openFileName = "";
        // Useful for automatic scrolling in panning mode.
        ScrollGestureRecognizer sgr;

        // If FidoCadJ runs as a standalone application, we must read the
        // content of the current library directory.
        // at the same time, we see if we should maintain a strict FidoCad
        // compatibility.
        if (runsAsApplication)  {
            circuitPanel.getDrawingModel().setTextFont(
                    preferences.get("MACRO_FONT", Globals.defaultTextFont),
                    Integer.parseInt(preferences.get("MACRO_SIZE", "3")),
                    circuitPanel.getUndoActions());
            
            readGridSettings();
            readDrawingSettings();
        }
        circuitPanel.setStrictCompatibility(false);

        // Here we set the approximate size of the control at startup. This is
        // useful, since the scroll panel (created just below) use those
        // settings for specifying the behaviour of scroll bars.

        circuitPanel.setPreferredSize(new Dimension(1000,1000));
        scrollPane= new JScrollPane((Component)circuitPanel);
        circuitPanel.father=scrollPane;

        scrollPane.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        if (runsAsApplication) {
            sgr = new ScrollGestureRecognizer();
            circuitPanel.addScrollGestureSelectionListener(sgr);
            sgr.getInstance();
        }

        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(20);

        circuitPanel.profileTime=false;
        circuitPanel.antiAlias=true;

        // Create the layer vector. Basically, this is a rather standard
        // attribution in which only the first layers are attributed to
        // something that is circuit-related.
        // I followed the FidoCAD tradition on this.
        java.util.List<LayerDesc> layerDesc=
            StandardLayers.createStandardLayers();
        circuitPanel.getDrawingModel().setLayers(layerDesc);

        toolBar = new ToolbarTools(textToolbar,smallIconsToolbar);
        toolZoom = new ToolbarZoom(layerDesc, this);

        toolBar.addSelectionListener(circuitPanel);
        toolZoom.addLayerListener(circuitPanel);

        toolZoom.addGridStateListener(circuitPanel);
        toolZoom.addZoomToFitListener(this);

        circuitPanel.addChangeZoomListener(toolZoom);
        circuitPanel.addChangeSelectionListener(toolBar);

        circuitPanel.getContinuosMoveActions()
                .addChangeCoordinatesListener(toolZoom);
        
        toolZoom.addChangeZoomListener(circuitPanel);

        Box b=Box.createVerticalBox();

        b.add(toolBar);
        b.add(toolZoom);

        toolZoom.setFloatable(false);
        toolZoom.setRollover(false);

        JMenuBar menuBar=menuTools.defineMenuBar(this);
        setJMenuBar(menuBar);

        // The initial state is the selection one.
        circuitPanel.setSelectionState(ElementsEdtActions.SELECTION, "");

        contentPane.add(b,"North");

        libraryModel = new LibraryModel(circuitPanel.getDrawingModel());
        LayerModel layerModel = new LayerModel(circuitPanel.getDrawingModel());
        macroLib = new MacroTree(libraryModel,layerModel);
        macroLib.setSelectionListener(circuitPanel);

        libraryModel.setUndoActorListener(circuitPanel.getUndoActions());
        libraryModel.addLibraryListener(new CircuitPanelUpdater(this));
        circuitPanel.getUndoActions().setLibraryUndoListener(
                                   new LibraryUndoExecutor(this,libraryModel));

        try {
            LibUtils.saveLibraryState(circuitPanel.getUndoActions());
        } catch (IOException e) {
            System.out.println("Exception: "+e);
        }

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        // Useful for Quaqua with MacOSX.
        //splitPane.putClientProperty("Quaqua.SplitPane.style","bar");
        splitPane.putClientProperty("JSplitPane.style","thick");

        Dimension windowSize = getSize();
        circuitPanel.setPreferredSize(
                new Dimension(windowSize.width*85/100,100));

        splitPane.setTopComponent(scrollPane);
        macroLib.setPreferredSize(new Dimension(450,200));
        splitPane.setBottomComponent(macroLib);
        splitPane.setResizeWeight(.8);

        contentPane.add(splitPane,"Center");

        circuitPanel.getUndoActions().setHasChangedListener(this);

        circuitPanel.setFocusable(true);
        scrollPane.setFocusable(true);

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
            @Override public void windowClosing(WindowEvent e)
            {
                if(!fileTools.checkIfToBeSaved()) {
                    return;
                }
                closeThisFrame();
            }
        });

        addWindowFocusListener(this);
        Globals.activeWindow=this;

        // This is WAY too invasive!!!
        circuitPanel.getUndoActions().setModified(false);
        if(runsAsApplication) {
            // Show the library tab or not.
            boolean s="true".equals(preferences.get("SHOW_LIBS","true"));
            showLibs(s);
            s="true".equals(preferences.get("SHOW_GRID","true"));
            circuitPanel.setGridVisibility(s);
            toolZoom.setShowGrid(s);
            s="true".equals(preferences.get("SNAP_GRID","true"));
            circuitPanel.setSnapState(s);
            toolZoom.setSnapGrid(s);
        }
    }

    /** Procedure to close the current frame, check if there are other open
        frames, and exit the program if there are no frames remaining.
        Ensure that the configuration settings are properly saved.
    */
    public void closeThisFrame()
    {
        setVisible(false);
        circuitPanel.getUndoActions().doTheDishes();
        savePosition();
        // Save the zoom factor. There is no reason to save the other
        // coordinate mapping data as this will be used for an empty drawing.
        MapCoordinates mc=circuitPanel.getMapCoordinates();
        preferences.put("CURRENT_ZOOM",""+mc.getXMagnitude());
        if(areLibsVisible()) {
            preferences.put("SHOW_LIBS","true");
        } else {
            preferences.put("SHOW_LIBS","false");
        }

        if(circuitPanel.getGridVisibility()) {
            preferences.put("SHOW_GRID","true");
        } else {
            preferences.put("SHOW_GRID","false");
        }

        if(circuitPanel.getSnapState()) {
            preferences.put("SNAP_GRID","true");
        } else {
            preferences.put("SNAP_GRID","false");
        }

        dispose();
        Globals.openWindows.remove(this);
        --Globals.openWindowsNumber;
        if (Globals.openWindowsNumber<1 && runsAsApplication) {
            System.exit(0);
        }
    }

    /** The action listener. Recognize menu events and behaves consequently.
        @param evt the event to be processed.
    */
    @Override public void actionPerformed(ActionEvent evt)
    {
        // Recognize and handle menu events
        if(evt.getSource() instanceof JMenuItem) {
            menuTools.processMenuActions(evt, this, toolZoom);
        }
    }

    /** Create a new instance of the window.
        @return the created instance
    */
    public FidoFrame createNewInstance()
    {
        FidoFrame popFrame=new FidoFrame(runsAsApplication, currentLocale);
        popFrame.setBounds(getX()+30, getY()+30, popFrame.getWidth(),
            popFrame.getHeight());

        popFrame.init();

        popFrame.loadLibraries();
        popFrame.setExtendedState(getExtendedState());

        popFrame.setVisible(true);

        return popFrame;
    }

    /** Show the FidoCadJ preferences panel
    */
    public void showPrefs()
    {
        String oldDirectory = libDirectory;
        CopyPasteActions cpa = circuitPanel.getCopyPasteActions();
        ElementsEdtActions eea = circuitPanel.getContinuosMoveActions();
        AddElements ae =eea.getAddElements();

        // At first, we create the preference panel. This kind of code is
        // probably not very easy to read and reutilize. This is probably
        // justified, since the preference panel is after all very specific
        // to the particular program to which it is referred, i.e. in this
        // case FidoCadJ...
        DialogOptions options=new DialogOptions(this,
            circuitPanel.getMapCoordinates().getXMagnitude(),
            circuitPanel.profileTime,circuitPanel.antiAlias,
            circuitPanel.getMapCoordinates().getXGridStep(),
            libDirectory,
            textToolbar,
            smallIconsToolbar,
            ae.getPcbThickness(),
            ae.getPcbPadSizeX(),
            ae.getPcbPadSizeY(),
            ae.getPcbPadDrill(),
            circuitPanel.getStrictCompatibility(),
            circuitPanel.getDrawingModel().getTextFont(),
            Globals.lineWidth,
            Globals.diameterConnection,
            circuitPanel.getDrawingModel().getTextFontSize(),
            cpa.getShiftCopyPaste());

        // The panel is now made visible. Its properties will be updated only
        // if the user clicks on "Ok".
        options.setVisible(true);


        // Now, we can update the properties.
        circuitPanel.profileTime=options.profileTime;
        circuitPanel.antiAlias=options.antiAlias;
        textToolbar=options.textToolbar;
        smallIconsToolbar=options.smallIconsToolbar;

        circuitPanel.getMapCoordinates().setMagnitudes(options.zoomValue,
                                                       options.zoomValue);
        circuitPanel.getMapCoordinates().setXGridStep(options.gridSize);
        circuitPanel.getMapCoordinates().setYGridStep(options.gridSize);

        ae.setPcbThickness(options.pcblinewidth_i);
        ae.setPcbPadSizeX(options.pcbpadwidth_i);
        ae.setPcbPadSizeY(options.pcbpadheight_i);
        ae.setPcbPadDrill(options.pcbpadintw_i);

        circuitPanel.getDrawingModel().setTextFont(options.macroFont_s,
            options.macroSize_i,
            circuitPanel.getUndoActions());

        circuitPanel.setStrictCompatibility(options.extStrict);
        toolBar.setStrictCompatibility(options.extStrict);
        cpa.setShiftCopyPaste(options.shiftCP);

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
            preferences.put("DIR_LIBS", libDirectory);
            preferences.put("MACRO_FONT", 
                    circuitPanel.getDrawingModel().getTextFont());
            
            preferences.put("MACRO_SIZE", 
                    ""+circuitPanel.getDrawingModel().getTextFontSize());
            
            preferences.put("STROKE_SIZE_STRAIGHT", ""+Globals.lineWidth);
            preferences.put("STROKE_SIZE_OVAL", ""+Globals.lineWidthCircles);
            preferences.put("CONNECTION_SIZE", ""+Globals.diameterConnection);
            preferences.put("SMALL_ICON_TOOLBAR", 
                    smallIconsToolbar?"true":"false");
            
            preferences.put("TEXT_TOOLBAR", textToolbar?"true":"false");
            preferences.put("GRID_SIZE", 
                    ""+circuitPanel.getMapCoordinates().getXGridStep());

            // Save default PCB characteristics
            preferences.put("pcbPadSizeX", ""+ae.pcbPadSizeX);
            preferences.put("pcbPadSizeY", ""+ae.pcbPadSizeY);
            preferences.put("pcbPadStyle", ""+ae.pcbPadStyle);
            preferences.put("pcbPadDrill", ""+ae.pcbPadDrill);
            preferences.put("pcbThickness", ""+ae.pcbThickness);
            preferences.put("SHIFT_CP", cpa.getShiftCopyPaste()?"true":"false");

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
		// If the drawing is empty, there's no need to go further.
    	if(circuitPanel.getDrawingModel().isEmpty())
    	    return;
        //double oldz=cc.getMapCoordinates().getXMagnitude();

        // We calculate the zoom to fit factor here.
        MapCoordinates m=DrawingSize.calculateZoomToFit(
                circuitPanel.getDrawingModel(),
                scrollPane.getViewport().getExtentSize().width-35,
                scrollPane.getViewport().getExtentSize().height-35,
                true);

        double z=m.getXMagnitude();

        // We apply the zoom factor to the coordinate transform
        circuitPanel.getMapCoordinates().setMagnitudes(z, z);

        // We make the scroll pane show the interesting part of
        // the drawing.
        Rectangle r= new Rectangle((int)m.getXCenter(), (int)m.getYCenter(),
            scrollPane.getViewport().getExtentSize().width,
            scrollPane.getViewport().getExtentSize().height);

        circuitPanel.updateSizeOfScrollBars(r);
    }

    /** We notify the user that something has changed by putting an asterisk
        in the file name.
        We also show here in the titlebar the (eventually shortened) file name
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
                Boolean.valueOf(circuitPanel.getUndoActions().getModified()));

            toolBar.setTitle(Globals.prettifyPath(
                    circuitPanel.getParserActions().openFileName,45));
        } else {
            setTitle("FidoCadJ "+Globals.version+" "+
                Globals.prettifyPath(
                        circuitPanel.getParserActions().openFileName,45)+
                        (circuitPanel.getUndoActions().getModified()?" *":""));

        }
    }

    /** The current window has gained focus.
        @param e the window event.
    */
    @Override public void windowGainedFocus(WindowEvent e)
    {
        Globals.activeWindow = this;
        // This should fix #182
        circuitPanel.requestFocusInWindow();
    }

    /** The current window has lost focus.
        @param e the window event.
    */
    @Override public void windowLostFocus(WindowEvent e)
    {
        // Nothing to do
    }

    /** Control the presence of the libraries and the preview on the right
        of the frame.
        @param s true if the libs have to be shown.
    */
    public void showLibs(boolean s)
    {
        splitPane.setBottomComponent(s?macroLib:null);
        toolZoom.setShowLibsState(areLibsVisible());
        menuTools.setShowLibsState(areLibsVisible());
        if(s) {
            splitPane.setDividerLocation(0.75);
        }
        splitPane.revalidate();
    }

    /** Determine if the libraries are visible or not.
        @return true if the libs are visible.
    */
    public boolean areLibsVisible()
    {
        return splitPane.getBottomComponent()!=null;
    }
}
