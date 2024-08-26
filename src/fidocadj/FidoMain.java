package fidocadj;

import javax.swing.*;

import java.io.*;
import java.util.*;

import fidocadj.circuit.controllers.ParserActions;
import fidocadj.circuit.model.DrawingModel;
import fidocadj.export.ExportGraphic;
import fidocadj.geom.DrawingSize;
import fidocadj.globals.Globals;
import fidocadj.globals.FileUtils;
import fidocadj.globals.OSValidator;
import fidocadj.globals.SettingsManager;
import fidocadj.layers.StandardLayers;
import fidocadj.timer.MyTimer;
import fidocadj.graphic.PointG;
import fidocadj.graphic.DimensionG;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import fidocadj.dialogs.controls.ErrorDialog;
import java.lang.reflect.InvocationTargetException;



/** FidoMain.java
 * SWING App: The starting point of FidoCadJ.
 *
 * <pre>
 * This file is part of FidoCadJ.
 *
 * FidoCadJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FidoCadJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FidoCadJ. If not,
 * @see <a href=http://www.gnu.org/licenses/>http://www.gnu.org/licenses/</a>.
 *
 * Copyright 2008-2023 by Davide Bucci
 * </pre>
 *
 * @author Davide Bucci
 */
public final class FidoMain
{

    private static CommandLineParser clp;

    /** Ensure that this is an utility class.
     */
    private FidoMain()
    {
    }

    /** The main method. Process the command line options and if necessary
     * shows an instance of FidoFrame.
     *
     * @param args the command line arguments.
     */
    public static void main(String... args)
    {
        clp = new CommandLineParser();

        applyOptimizationSettings(clp);

        // Sets a global exception handler for all non-EDT threads
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            handleUncaughtException(thread, throwable);
        });

        // Sets an exception handler for the EDT (Event Dispatch Thread)
        try {
            SwingUtilities.invokeAndWait(() -> {
                Thread.currentThread().setUncaughtExceptionHandler(
                        (thread, throwable) -> {
                            handleUncaughtException(thread, throwable);
                        });
            });
        } catch (InterruptedException | InvocationTargetException e) {
            SwingUtilities.invokeLater(() -> {
                JFrame parentFrame = null;
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                String exceptionText = sw.toString();

                ErrorDialog errorDialog = new ErrorDialog(parentFrame,
                        "Error setting the exception handler for the EDT:\n" +
                                exceptionText);
                errorDialog.setVisible(true);
            });
        }


        if (args.length >= 1) {
            clp.processArguments(args);
        }

        // Now we proceed with all the operations: opening files, converting...
        if (clp.getHeadlessMode()) {
            // Creates a circuit object
            DrawingModel pP = new DrawingModel();

            if ("".equals(clp.getLoadFileName())) {
                System.err.println("You should specify a FidoCadJ file to"
                        + " read");
                System.exit(1);
            }

            // Reads the standard libraries
            readLibrariesProbeDirectory(pP, false, clp.getLibDirectory());
            pP.setLayers(StandardLayers.createStandardLayers());
            ParserActions pa = new ParserActions(pP);

            MyTimer mt = new MyTimer();
            try {
                String txt = FileUtils.readFile(clp.getLoadFileName());
                // Here txt contains the new circuit: parse it!
                pa.parseString(new StringBuffer(txt));
            } catch (IllegalArgumentException iae) {
                System.err.println("Illegal filename");
            } catch (Exception e) {
                System.err.println("Unable to process: " + e);
            }

            if (clp.shouldConvertFile()) {
                doConvert(clp, pP, clp.shouldSplitLayers());
            }

            if (clp.getHasToPrintSize()) {
                PointG o = new PointG(0, 0);
                DimensionG d = DrawingSize.getImageSize(pP, 1, true, o);
                System.out.println("" + d.width + " " + d.height);
            }

            if (clp.getHasToPrintTime()) {
                System.out.println("Elapsed time: " + mt.getElapsed() + " ms.");
            }
        }

        if (!clp.getCommandLineOnly()) {
            SwingUtilities.invokeLater(new CreateSwingInterface(
                    clp.getLibDirectory(),
                    clp.getLoadFileName(), clp.getWantedLocale()));
        }
    }

    /**
     Handles uncaught exceptions by logging the error and displaying ..
     an error dialog.

     @param thread the thread where the uncaught exception occurred.
     @param throwable the uncaught exception.
     */
    private static void handleUncaughtException(Thread thread,
            Throwable throwable)
    {
        // Log the exception
        System.err.println(
                "Uncaught exception in thread " +
                        thread.getName() + ": " +
                        throwable.getMessage());

        throwable.printStackTrace();

        // Create a string containing the exception message and stack trace
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        String exceptionText = sw.toString();

        // Show a dialog with the exception text
        SwingUtilities.invokeLater(() -> {
            JFrame parentFrame = null;
            ErrorDialog errorDialog =
                    new ErrorDialog(parentFrame, exceptionText);
            errorDialog.setVisible(true);
        });
    }

    /** Apply optimisation settings which are platform-dependent. This function
     *  is called early in the execution of the program, before the AWT/Swing
     *  is initialized.
     *
     * @param clp command-line arguments may deactivate some optimisations.
     */
    private static void applyOptimizationSettings(CommandLineParser clp)
    {
        if (!clp.getStripOptimization()
                && OSValidator.isMac())
        {
            // CAREFUL**************************************************
            // In all MacOSX systems I tried, this greatly increases the
            // redrawing speed. *HOWEVER* the default value for Java 1.6
            // as distributed by Apple is "false" (whereas it was "true"
            // for Java 1.5).  This might mean that in a future this can
            // be not very useful, or worse slowdown the performances.
            // CAREFUL**************************************************
            // NOTE: this does not seem to have any effect!
            System.setProperty("apple.awt.graphics.UseQuartz", "true");
            // Important tweaks to the appearance.
            System.setProperty( "apple.awt.application.name", "FidoCadJ" );
            System.setProperty( "apple.awt.application.appearance", "system" );

        }

        /* if(!clp.getStripOptimization() &&
         * System.getProperty("os.name").toLowerCase().startsWith("linux")) {
         * // CAREFUL**************************************************
         * // Various sources reports that this option will increase
         * // the redrawing speed using Linux. It might happen, however
         * // that the performances can be somewhat degraded in some
         * // systems.
         * // CAREFUL**************************************************
         * // We tested that in version 0.24.1. In fact, activating this
         * // option renders the software inusable in some systems (Nvidia
         * // graphic software?) So this option is definitively turned off.
         * // System.setProperty("sun.java2d.opengl", "true");
         * // See for example this discussion: http://tinyurl.com/axoxqcb
         * } */
    }

    /** Perform a conversion into a graphic file, from command line parameters.
     * A file should have already been loaded and parsed into pP.
     * This routine also checks if the output file has a correct extension,
     * coherent with the file format chosen.
     *
     * @param clp command-line arguments.
     * @param pP the model containing the drawing.
     * @param splitLayers split layers into different files when exporting.
     */
    private static void doConvert(CommandLineParser clp, DrawingModel pP,
            boolean splitLayers)
    {
        if (!Globals.checkExtension(clp.getOutputFile(),
                clp.getExportFormat()) && !clp.getForceMode())
        {
            System.err.println(
                    "File extension is not coherent with the "
                    + "export output format! Use -f to skip this test.");
            System.exit(1);
        }

        try {
            if (clp.getResolutionBasedExport()) {
                ExportGraphic.export(new File(clp.getOutputFile()), pP,
                        clp.getExportFormat(), clp.getResolution(),
                        true, false, true, true, splitLayers);
            } else {
                ExportGraphic.exportSize(new File(clp.getOutputFile()),
                        pP, clp.getExportFormat(), clp.getXSize(),
                        clp.getYSize(),
                        true, false, true, true, splitLayers);
            }
            System.out.println("Export completed");
        } catch (IOException ioe) {
            System.err.println("Export error: " + ioe);
        }
    }

    /** Read all libraries, eventually by inspecting the directory specified
     * by the user. There are three standard directories: IHRAM.FCL,
     * FCDstdlib.fcl and PCB.fcl. If those files are found in the external
     * directory specified, the internal version is not loaded. Other files
     * on the external directory are loaded.
     *
     * @param pP the parsing class in which the libraries should be loaded
     * @param englishLibraries a flag to specify if the internal libraries
     * should be loaded in English or in Italian.
     * @param libDirectoryO the path of the external directory.
     */
    public static void readLibrariesProbeDirectory(DrawingModel pP,
            boolean englishLibraries, String libDirectoryO)
    {
        String libDirectory = libDirectoryO;
        ParserActions pa = new ParserActions(pP);

        synchronized (pP) {
            if (libDirectory == null || libDirectory.length() < 3) {
                libDirectory = System.getProperty("user.home");
            }

            readIHRAM(englishLibraries, libDirectory, pa);
            readFCDstdlib(englishLibraries, libDirectory, pa);
            readPCBlib(englishLibraries, libDirectory, pa);
            readEYLibraries(englishLibraries, libDirectory, pa);
            readElecLib(englishLibraries, libDirectory, pa);
        }
    }

    /** Read the internal IHRAM library, unless a file called
     * IHRAM.FCL is present in the library directory
     *
     * @param libDirectory path where to search for the library.
     * @param englishLibraries specify if the English version of the lib
     * should be loaded instead of the Italian one.
     * @param pa the object by which the library will be crunched.
     */
    private static void readIHRAM(boolean englishLibraries,
            String libDirectory,
            ParserActions pa)
    {
        pa.loadLibraryDirectory(libDirectory);
        if (new File(Globals.createCompleteFileName(
                libDirectory, "IHRAM.FCL")).exists())
        {
            System.out.println("IHRAM library got from external file");
        } else {
            if (englishLibraries) {
                pa.loadLibraryInJar(FidoFrame.class.getResource(
                        "/lib/IHRAM_en.FCL"), "ihram");
            } else {
                pa.loadLibraryInJar(FidoFrame.class.getResource(
                        "/lib/IHRAM.FCL"), "ihram");
            }
        }
    }

    /** Read the internal FCDstdlib library, unless a file called
     * FCDstdlib.fcl is present in the library directory
     *
     * @param libDirectory path where to search for the library.
     * @param englishLibraries specify if the English version of the lib
     * should be loaded instead of the Italian one.
     * @param pa the object by which the library will be crunched.
     */
    private static void readFCDstdlib(boolean englishLibraries,
            String libDirectory,
            ParserActions pa)
    {
        if (new File(Globals.createCompleteFileName(
                libDirectory, "FCDstdlib.fcl")).exists())
        {
            System.out.println("Standard library got from external file");
        } else {
            if (englishLibraries) {
                pa.loadLibraryInJar(FidoFrame.class.getResource(
                        "/lib/FCDstdlib_en.fcl"), "");
            } else {
                pa.loadLibraryInJar(FidoFrame.class.getResource(
                        "/lib/FCDstdlib.fcl"), "");
            }
        }
    }

    /** Read the internal PCB library, unless a file called
     * PCB.fcl is present in the library directory
     *
     * @param libDirectory path where to search for the library.
     * @param englishLibraries specify if the English version of the lib
     * should be loaded instead of the Italian one.
     * @param pa the object by which the library will be crunched.
     */
    private static void readPCBlib(boolean englishLibraries,
            String libDirectory,
            ParserActions pa)
    {
        if (new File(Globals.createCompleteFileName(
                libDirectory, "PCB.fcl")).exists())
        {
            System.out.println("Standard PCB library got from external file");
        } else {
            if (englishLibraries) {
                pa.loadLibraryInJar(FidoFrame.class.getResource(
                        "/lib/PCB_en.fcl"), "pcb");
            } else {
                pa.loadLibraryInJar(FidoFrame.class.getResource(
                        "/lib/PCB.fcl"), "pcb");
            }
        }
    }

    /** Read the internal EYLibraries library, unless a file called
     * EY_Libraries.fcl is present in the library directory
     *
     * @param libDirectory path where to search for the library.
     * @param englishLibraries specify if the English version of the lib
     * should be loaded instead of the Italian one.
     * @param pa the object by which the library will be crunched.
     */
    private static void readEYLibraries(boolean englishLibraries,
            String libDirectory,
            ParserActions pa)
    {/*
         * if(!englishLibraries) {
         * System.out.println("EY library is only available in english");
         * } */
        if (new File(Globals.createCompleteFileName(
                libDirectory, "EY_Libraries.fcl")).exists())
        {
            System.out.println("Standard EY_Libraries got from external file");
        } else {
            pa.loadLibraryInJar(FidoFrame.class.getResource(
                    "/lib/EY_Libraries.fcl"), "EY_Libraries");
        }
    }

    /** Read the internal elettrotecnica library, unless a file called
     * elettrotecnica.fcl is present in the library directory
     *
     * @param libDirectory path where to search for the library.
     * @param englishLibraries specify if the English version of the lib
     * should be loaded instead of the Italian one.
     * @param pa the object by which the library will be crunched.
     */
    private static void readElecLib(boolean englishLibraries,
            String libDirectory,
            ParserActions pa)
    {
        if (new File(Globals.createCompleteFileName(
                libDirectory, "elettrotecnica.fcl")).exists())
        {
            System.out.println(
                    "Electrotechnics library got from external file");
        } else {
            if (englishLibraries) {
                pa.loadLibraryInJar(FidoFrame.class.getResource(
                        "/lib/elettrotecnica_en.fcl"), "elettrotecnica");
            } else {
                pa.loadLibraryInJar(FidoFrame.class.getResource(
                        "/lib/elettrotecnica.fcl"), "elettrotecnica");
            }
        }
    }
}

/** Creates the Swing elements needed for the interface.
 */
class CreateSwingInterface implements Runnable
{

    String libDirectory;
    String loadFile;
    Locale currentLocale;

    /** Constructor where we specify some details concerning the library
     * directory, the file to load (if needed) as well as the locale.
     *
     * @param ld the library directory
     * @param lf the file to load
     * @param ll the locale.
     */
    public CreateSwingInterface(String ld, String lf, Locale ll)
    {
        libDirectory = ld;
        loadFile = lf;
        currentLocale = ll;
    }

    /** Standard constructor.
     */
    public CreateSwingInterface()
    {
        libDirectory = "";
        loadFile = "";
    }

    /** Run the thread.
     */
    @Override
    public void run()
    {
        boolean enableThemesSupport =
            SettingsManager.get("ENABLE_CUSTOM_THEMES",
                "false").equals("true");
        String theme = SettingsManager.get("THEME", "light");
        boolean isLightTheme = theme.equals("light");
        boolean isDarkTheme = theme.equals("dark");
        boolean isCustomTheme = SettingsManager.get("PERSONALIZED_THEME",
                "false").equals("true");
        String customThemePath = null;
        boolean flatLafNotFound = false;

        if (isCustomTheme && enableThemesSupport) {
            customThemePath = SettingsManager.get("CUSTOM_THEME_PATH", "");
        }

        try {
            if (enableThemesSupport) {
                applyTheme(isLightTheme, isDarkTheme, isCustomTheme,
                        customThemePath);
            }
        } catch (Exception e) {
            System.out.println(
                "Failed to apply theme. Falling back to default.");
        } catch (NoClassDefFoundError e) {
            flatLafNotFound = true;
            System.out.println(
                "Can not locate FlatLaf. Falling back to default.");
        }

        /**
         *****************************************************************
         PLATFORM SELECTION AND CONFIGURATION CODE GOES IN THIS SECTION
         ******************************************************************

         NOTE: this is executed AFTER the AWT/Swing is initialized.
               see applyOptimizationSettings if you need to setup things
               before that happens.

         */
        if (OSValidator.isMac()) {
            System.setProperty("com.apple.macos.useScreenMenuBar", "true");
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty(
                "com.apple.mrj.application.apple.menu.about.name",
                    "FidoCadJ");
            try {
                if (!enableThemesSupport || flatLafNotFound) {
                    System.out.println("Trying to activate VAqua11");
                    UIManager.setLookAndFeel(
                            "org.violetlib.aqua.AquaLookAndFeel");
                    System.out.println("VAqua11 look and feel active");
                }
            } catch (Exception e) {
                System.out.println(
                     "Failed to activate macOS Look and Feel."+
                     " Continuing with default.");
            }
        } else {
            if (OSValidator.isWindows()) {
                try {
                    if (!enableThemesSupport || flatLafNotFound) {
                        UIManager.setLookAndFeel(
                                "com.sun.java.swing.plaf.windows."+
                                "WindowsLookAndFeel");
                    }
                } catch (Exception eE) {
                    System.out.println(
                            "Could not load the Windows Look and Feel!");
                }
            }
        }

        /**
         *****************************************************************
         END OF THE PLATFORM SELECTION CODE
         ******************************************************************
         */
        Globals.desktopInt = new ADesktopIntegration();
        Globals.desktopInt.registerActions();

        FidoFrame popFrame = new FidoFrame(true, currentLocale);

        if (!"".equals(libDirectory)) {
            popFrame.libDirectory = libDirectory;
        }

        popFrame.init();

        popFrame.setVisible(true);

        popFrame.loadLibraries();

        if (!"".equals(loadFile)) {
            popFrame.getFileTools().load(loadFile);
        }

        popFrame.setVisible(true);
    }

    /**
     Applies the selected theme based on the user's preferences.

     This method handles the application of either a predefined light or ..
     dark theme, or a custom theme loaded from an external properties file.
     If the custom theme is selected and the specified path is valid,
     the properties are loaded and applied using FlatLaf.
     If a custom theme is not specified, it falls back to the ..
     light or dark theme based on the user's preferences.

     @param isLightTheme true if the light theme should be applied
     @param isDarkTheme true if the dark theme should be applied
     @param isCustomTheme true if a custom theme should be applied
     @param customThemePath the path to the custom theme properties file.
     */
    private void applyTheme(boolean isLightTheme, boolean isDarkTheme,
            boolean isCustomTheme, String customThemePath)
    {
        try {
            if (isCustomTheme && customThemePath != null &&
                                            !customThemePath.isEmpty())
            {
                // Load the custom theme from the properties file
                Properties props = new Properties();
                try (FileInputStream inputStream = new FileInputStream(
                        customThemePath)) {
                    props.load(inputStream);
                }

                // Convert Properties to Map<String, String> as required by
                // FlatLaf
                Map<String, String> themeProperties = new HashMap<>();
                for (String key : props.stringPropertyNames()) {
                    themeProperties.put(key, props.getProperty(key));
                }

                // Apply the custom theme properties
                FlatLaf.setGlobalExtraDefaults(themeProperties);

                // Set up the base theme before applying custom properties
                if (isDarkTheme) {
                    FlatDarkLaf.setup();
                } else {
                    FlatLightLaf.setup();
                }

                // Ensure that the UI reflects the changes
                FlatLaf.updateUI();
            } else {
                // Apply default themes based on user preference
                if (isLightTheme) {
                    FlatLightLaf.setup();
                } else {
                    if (isDarkTheme) {
                        FlatDarkLaf.setup();
                    }
                }

                // Ensure that the UI reflects the changes
                FlatLaf.updateUI();
            }
        } catch (Exception e) {
            // Handle any exceptions that may occur during theme application
            System.err.println("Failed to apply the theme: " + e.getMessage());
        }
    }
}
