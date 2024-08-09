package fidocadj.globals;

import java.io.*;
import java.util.*;

/** The FileUtils class contains methods for file and directory handling,
    which comprises reading a file, copying or deleting a directory
    as well as things like that.

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

*/
public final class FileUtils
{
    /** Private constructor, for Utility class pattern
    */
    private FileUtils ()
    {
        // nothing
    }

    /** Read an input file.
        @param filename the complete path and filename of the file to read.
        @return the file contents.
        @throws IOException if the file access fails.
    */
    public static String readFile(String filename) throws IOException
    {
        FileReader input = null;
        BufferedReader bufRead = null;
        StringBuffer txt=new StringBuffer("");

        try {
            input=new FileReader(filename);
            bufRead =  new BufferedReader(input);
            String line="";
            txt = new StringBuffer(bufRead.readLine());

            txt.append("\n");

            while (line != null){
                line =bufRead.readLine();
                txt.append(line);
                txt.append("\n");
            }
        } finally {
            if(bufRead!=null) { bufRead.close(); }
            if(input!=null) { input.close(); }
        }
        return txt.toString();
    }

    /** Copy a directory recursively.

        http://subversivebytes.wordpress.com/2012/11/05/java-copy-directory-
            recursive-delete/
        @param sourceLocation the original directory.
        @param targetLocation the destination.
        @throws IOException if the file access fails.
    */
    public static void copyDirectory(File sourceLocation, File targetLocation)
        throws IOException
    {
        if(sourceLocation.isDirectory()) {
            if(!targetLocation.exists() && !targetLocation.mkdir()) {
                throw new IOException("Can not create temp. directory.");
            }

            // Process all the elements of the directory.
            String[] children = sourceLocation.list();
            if (children==null) {
                return;
            }
            for(String currentFile: children) {
                copyDirectory(new File(sourceLocation, currentFile),
                    new File(targetLocation, currentFile));
            }
        } else {
            copyFile(sourceLocation, targetLocation);
        }
    }

    /** Copy a file from a location to another one.
        @param sourceLocation origin of the file to copy.
        @param targetLocation destination of the file to copy.
        @throws IOException if the file access fails.
    */
    public static void copyFile(File sourceLocation, File targetLocation)
        throws IOException
    {
        if(sourceLocation.isDirectory()) {
            return;
        }

        InputStream in = null;
        OutputStream out = null;

        // The copy is made by bunch of 1024 bytes.
        // I wander whether better OS copy funcions exist.
        try {
            in= new FileInputStream(sourceLocation);
            out=new FileOutputStream(targetLocation);
            byte[] buf = new byte[1024];
            int len;
            while((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } finally {
            if (out!=null) { out.close(); }
            if (in!=null) { in.close(); }
        }
    }

    /** Copy all the files containing the specified criterium in the given
        directory. The criterium specifies files in a very simple way.
        The function just check if the file name contains it.
        Therefore, specifying "txt" as criteria would match "txtpipo.ed" as
        well as "pipo.txt". Specifying ".txt" would match "lors.txt.bak" as
        well as "rone.txt".
        This copy is not recursive: only the first level is processed.
        @param sourceLocation origin of the directory where are the
            files to copy.
        @param targetLocation destination of the files to copy.
        @param tcriteria the search criteria to be employed.
        @throws IOException if the file access fails.
    */
    public static void copyDirectoryNonRecursive(File sourceLocation,
        File targetLocation, String tcriteria)
        throws IOException
    {
        String criteria=tcriteria;
        if(sourceLocation.isDirectory()) {
            if(!targetLocation.exists() && !targetLocation.mkdir()) {
                throw new IOException("Can not create temp. directory.");
            }

            criteria = criteria.toLowerCase(new Locale("en"));

            String[] children = sourceLocation.list();
            if(children==null) {
                return;
            }
            for(String s : children) {
                if(s.toLowerCase(Locale.US).contains(criteria)) {
                    copyFile(new File(sourceLocation, s),
                        new File(targetLocation, s));
                }
            }
        }
    }

    /**
        http://stackoverflow.com/questions/3775694/deleting-folder-from-java
        @param directory the directory to delete.
        @return true if deletion was successful.
        @throws IOException if the file access fails.
    */
    public static boolean deleteDirectory(File directory)
        throws IOException
    {
        if(directory.exists()){
            File[] files = directory.listFiles();
            if(null!=files){
                for(File f : files) {
                    if(f.isDirectory()) {
                        deleteDirectory(f);
                    } else {
                        if (!f.delete()) {
                            throw new IOException("Can not delete file"+f);
                        }
                    }
                }
            }
        }
        return directory.delete();
    }
}