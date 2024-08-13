package fidocadj;

import javax.swing.*;

import java.util.prefs.*;
import java.io.*;
import java.util.*;

import fidocadj.circuit.controllers.ParserActions;
import fidocadj.circuit.model.DrawingModel;
import fidocadj.export.ExportGraphic;
import fidocadj.geom.DrawingSize;
import fidocadj.globals.Globals;
import fidocadj.globals.AccessResources;
import fidocadj.globals.FileUtils;
import fidocadj.layers.StandardLayers;
import fidocadj.timer.MyTimer;
import fidocadj.graphic.PointG;
import fidocadj.graphic.DimensionG;

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

        if (args.length >= 1) {
            clp.processArguments(args);
        }

        applyOptimizationSettings(clp);

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

    /** Apply optimisation settings which are platform-dependent.
     *
     * @param clp command-line arguments may deactivate some optimisations.
     */
    private static void applyOptimizationSettings(CommandLineParser clp)
    {
        if (!clp.getStripOptimization()
                && Globals.isMacOS()) {
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
                clp.getExportFormat()) && !clp.getForceMode()) {
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
                libDirectory, "IHRAM.FCL")).exists()) {
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
                libDirectory, "FCDstdlib.fcl")).exists()) {
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
                libDirectory, "PCB.fcl")).exists()) {
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
                libDirectory, "EY_Libraries.fcl")).exists()) {
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
                libDirectory, "elettrotecnica.fcl")).exists()) {
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
        /** *****************************************************************
         * PLATFORM SELECTION AND CONFIGURATION CODE GOES IN THIS SECTION
         ****************************************************************** */
        if (Globals.isMacOS()) {
            AccessResources g = new AccessResources();

            Preferences.userNodeForPackage(g.getClass());

            Globals.weAreOnAMac = true;

            // These settings allows to obtain menus on the right place
            System.setProperty("com.apple.macos.useScreenMenuBar", "true");
            // This is for JVM < 1.5 It won't harm on higher versions.
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            // This is for having the good application name in the menu
            System.setProperty(
                    "com.apple.mrj.application.apple.menu.about.name",
                    "FidoCadJ");

            try {
                System.out.println("Trying to activate VAqua11");
                UIManager.setLookAndFeel(
                        "org.violetlib.aqua.AquaLookAndFeel");
                System.out.println("VAqua11 look and feel active");
            } catch (Exception e) {
                // Quaqua is not active. Just continue!

                System.out.println(
                        "The Quaqua look and feel is not available");
                System.out.println(
                        "I will continue with the basic Apple l&f");
            }
        } else {
            if (System.getProperty("os.name").startsWith("Win")) {
                /* If the host system is a window system, select the Windows
                 * look and feel. This is a way to encourage people to use
                 * FidoCadJ even on a Windows system, forgotting about Java.
                 */
                try {
                    UIManager.setLookAndFeel(
                        "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                } catch (Exception eE) {
                    System.out.println(
                            "Could not load the Windows Look and feel!");
                }
            }
        }

        // Un-comment to try to use the Metal LAF

        /*
         * try {
         * UIManager.setLookAndFeel(
         * UIManager.getCrossPlatformLookAndFeelClassName());
         * Globals.weAreOnAMac =false;
         * } catch (Exception E) {}
         */
        /** *****************************************************************
         * END OF THE PLATFORM SELECTION CODE
         ****************************************************************** */
        // This substitutes the AppleSpecific class for Java >=9 and it is a
        // much more general and desirable solution.
        Globals.desktopInt = new ADesktopIntegration();
        Globals.desktopInt.registerActions();

        // Here we create the main window object
        FidoFrame popFrame = new FidoFrame(true, currentLocale);

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
        if (!"".equals(loadFile)) {
            popFrame.getFileTools().load(loadFile);
        }

        // We force a global validation of the window size, by including
        // this time the tree containing the various libraries and the
        // macros.
        popFrame.setVisible(true);
    }
}
