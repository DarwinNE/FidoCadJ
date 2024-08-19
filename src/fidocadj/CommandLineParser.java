package fidocadj;

import java.util.Locale;

import fidocadj.globals.Globals;

/** CommandLineParser.java
    Parse the command line recognizing options, commands and files.

    Only the parsing is done. The operations needed should be then be done
    by checking the state of an instance of this object after that the
    options have been parsed.

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

    Copyright 2015-2023 by Davide Bucci
    </pre>

    @author Davide Bucci
*/
public class CommandLineParser
{
    // If this is true, the GUI will not be loaded and FidoCadJ will run as
    // a command line utility:
    private boolean commandLineOnly = false;

    // Force FidoCadJ to skip some sanity tests during the command line
    // option processing.
    private boolean forceMode=false;

    // The following variable will be true if one requests to convert a
    // file:
    private boolean convertFile = false;
    private int totx=0;
    private int toty=0;
    private String exportFormat="";
    private String outputFile="";
    private boolean headlessMode = false;
    private boolean resolutionBasedExport = false;
    private boolean printSize=false;
    private boolean printTime=false;
    private boolean splitLayers=false;
    private double resolution=1;
    private Locale currentLocale=null;

    // Filename to open or a particular library directory to be considered
    private String loadFile="";
    private String libDirectory="";

    // The standard behavior implies that FidoCadJ tries to activate some
    // optimizations or settings which depends on the platform and should
    // increase things such as the redrawing speed and other stuff. In some
    // cases ("-p" option) they might be deactivated:
    private static boolean stripOptimization=false;

    /** Get the value of the strip optimization flag.
        @return true if platform-dependent optimizations should not be applied.
    */
    public boolean getStripOptimization()
    {
        return stripOptimization;
    }

    /** Get the name (completed with the given path) of the filename to read.
        @return the file name, or "" if no file has been given
    */
    public String getLoadFileName()
    {
        return loadFile;
    }

    /** Get the name (completed with the given path) of the directory
        containing the libraries to be loaded.
        @return the path, or "" if no dir has been given.
    */
    public String getLibDirectory()
    {
        return libDirectory;
    }

    /** Read the current command line arguments and parse it.
        @param orArgs the command line arguments, as provided to the main.
    */
    public void processArguments(String... orArgs)
    {
        int i;
        boolean loaded=false;
        boolean nextLib=false;
        String[] args=orArgs;

        // called by jnlp
        if ("-print".equalsIgnoreCase(args[0])) {
            // TODO: verify if this can happen in a operating system
            // with case sensitive file system! Windows only code?
            String filename =
                args[1].toLowerCase(Locale.US).replace(".fcd", "");
            if (filename.lastIndexOf(System.getProperty("file.separator"))>0) {
                filename = filename.substring(filename.lastIndexOf(
                    System.getProperty("file.separator"))+1);
            }
            args = ("-n -c r72 pdf "+filename+".pdf " // NOPMD
                +args[1]).split(" "); // NOPMD  suppressed PMD false positive
        }

        for(i=0; i<args.length; ++i) {
            if (args[i].startsWith("-")) {
                // It is an option

                // phylum
                if ("-open".equalsIgnoreCase(args[i].trim())) {
                    continue;
                }
                if (args[i].startsWith("-k")) {
                    // -k: show the current locale
                    System.out.println("Detected locale: "+
                        Locale.getDefault().getLanguage());
                } else if (args[i].startsWith("-n")) {
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
                } else if (args[i].startsWith("-f")) {
                    // -f forces FidoCadJ to skip some of the sanity checks
                    // for example in file extensions while exporting.
                    forceMode=true;
                } else if (args[i].startsWith("-m")) {
                    // -m indicates that during export multiple layers should
                    // be exported towards multiple files.
                    splitLayers=true;
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
                        ++i;
                        if (args[i].startsWith("r")) {
                            resolution = Double.parseDouble(
                                args[i].substring(1));
                            resolutionBasedExport = true;
                            if (resolution<=0) {
                                System.err.println("Resolution should be"+
                                    " a positive real number");
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
                    } catch (Exception eE) {
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
                    currentLocale = Locale.forLanguageTag(loc);

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
    public void  showCommandLineHelp()
    {
        // Here, exceptionally, the lenght of the code lines might exceed
        // 80 characters.

        //CHECKSTYLE.OFF: LineLength
        String help = "\nThis is FidoCadJ, version "+Globals.version+".\n"+
            "By the FidoCadJ team, 2007-2020.\n\n"+

            "Use: java -jar fidocadj-0.24.8.jar [-options] [file] \n"+
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
            "        sx and sy.\n"+
            "        NOTE: the correctness of the file extension is checked, unless the -f\n"+
            "        option is specified.\n\n"+

            " -m     if a file export is done towards a vector graphic file format, split\n"+
            "        the layers and write one file for each layer. The file name will be\n"+
            "        obtained by appending _ followed by the layer number to the specified\n"+
            "        file name. For example, the following command will create files\n"+
            "        test_0.svg, test_1.svg ... from the drawing contained in test.fcd:\n\n"+
            "           java -jar fidocadj.jar -n -m -c r2 svg test.svg test.fcd\n\n"+

            " -s     Print the size of the specified file in logical units.\n\n"+

            " -h     Print this help and exit.\n\n"+

            " -t     Print the time used by FidoCadJ for the specified operation.\n\n"+

            " -p     Do not activate some platform-dependent optimizations. You might try\n"+
            "        this option if FidoCadJ hangs or is painfully slow.\n\n"+

            " -l     Force FidoCadJ to use a certain locale (the code might follow\n"+
            "        immediately or be separated by an optional space).\n\n"+

            " -k     Show the current locale.\n\n"+

            " -f     Force FidoCadJ to skip some sanity tests on the input data.\n\n"+

            " [file] The optional (except if you use the -d or -s options) FidoCadJ file to\n"+
            "        load at startup time.\n\n"+

            "Example: load and convert a FidoCadJ drawing to a 800x600 pixel png file\n"+
            "        without using the GUI.\n"+
            "  java -jar fidocadj.jar -n -c 800 600 png out1.png test1.fcd\n\n"+
            "Example: load and convert a FidoCadJ drawing to a png file without using the\n"+
            "        graphic user interface (the so called headless mode).\n"+
            "        Each FidoCadJ logical unit will be converted in 2 pixels on the image.\n"+
            "  java -jar fidocadj.jar -n -c r2 png out2.png test2.fcd\n\n"+
            "Example: load FidoCadJ forcing the locale to simplified Chinese (zh).\n"+
            "  java -jar fidocadj.jar -l zh\n\n";

        //CHECKSTYLE.ON: LineLength
        System.out.println(help);
    }

    /** Check if layers must be split into different files when exporting
        into a vector file format.
        @return true if it's the case.
    */
    public boolean shouldSplitLayers()
    {
        return splitLayers;
    }
    /** Check if a file conversion (export) should be done.
        @return true if an export should be done.
    */
    public boolean shouldConvertFile()
    {
        return convertFile;
    }

    /** Return a string describing the file format.
        @return the description of the file format, as provided by the user,
            or "" if nothing has been specified.
    */
    public String getExportFormat()
    {
        return exportFormat;
    }

    /** Get the name of the output file
        @return the name of the output file, or "" if nothing has been
            specified.
    */
    public String getOutputFile()
    {
        return outputFile;
    }

    /** Get the width of the export
        @return the width in pixels of the image to be exported.
    */
    public int getXSize()
    {
        return totx;
    }

    /** Get the height of the export
        @return the heght in pixels of the image to be exported.
    */
    public int getYSize()
    {
        return toty;
    }

    /** Check if the headless mode (no UI) should be active.
        @return true if the Java headless mode should be activated.
    */
    public boolean getHeadlessMode()
    {
        return headlessMode;
    }

    /** Check if the export should be done on a resolution based and not
        by creating an image of a given height and width.
        @return true if the export should be done calculating the image
            size by taking into account the desired resolution.
    */
    public boolean getResolutionBasedExport()
    {
        return resolutionBasedExport;
    }

     /** Get the wanted locale.
        @return the wanted locale object, or null if no hint about it has
            been given in the command line.
    */
    public Locale getWantedLocale()
    {
        return currentLocale;
    }

    /** Check if the size of the drawing (in logical units) has to be printed.
        @return true if the size has to be printed.
    */
    public boolean getHasToPrintSize()
    {
        return printSize;
    }

    /** Check if the time employed to perform an action has to be printed.
        @return true if the time has to be printed.
    */
    public boolean getHasToPrintTime()
    {
        return printTime;
    }

    /** Get the resolution to be employed for the graphic export.
        @return the resolution in pixels for logical unit.
    */
    public double getResolution()
    {
        return resolution;
    }

    /** Check if some sanity checks over the options should be skipped.
        @return true if the command line should be interpreted as is, even
            it contains something strange such as a wrong file extension and
            so on.
    */
    public boolean getForceMode()
    {
        return forceMode;
    }

    /** Check if only the command line interface should be used.
        @return true if only the command line interface is to be used (i.e.
            no GUI will be started).
    */
    public boolean getCommandLineOnly()
    {
        return commandLineOnly;
    }
}