package net.sourceforge.fidocadj.globals;

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
    along with FidoCadJ.  If not, see <http://www.gnu.org/licenses/>.

    Copyright 2008-2015 by Davide Bucci
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
        FileReader input = new FileReader(filename);
        BufferedReader bufRead = new BufferedReader(input);

        String line="";
        StringBuffer txt = new StringBuffer(bufRead.readLine());

        txt.append("\n");

        while (line != null){
            line =bufRead.readLine();
            txt.append(line);
            txt.append("\n");
        }
        bufRead.close();
        return txt.toString();
    }

    /**
        http://subversivebytes.wordpress.com/2012/11/05/java-copy-directory-
            recursive-delete/
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
            for(int i = 0; i < children.length; ++i) {
                copyDirectory(new File(sourceLocation, children[i]),
                    new File(targetLocation, children[i]));
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
        if(sourceLocation.isDirectory())
            return;

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
            if (out!=null) out.close();
            if (in!=null) in.close();
        }
    }

    /** Copy all the files containing the specified criteria in the given
        directory.
        This copy is not recursive: only the first level is processed.
        @param sourceLocation origin of the directory where are the
            files to copy.
        @param targetLocation destination of the files to copy.
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
            for(int i = 0; i < children.length; ++i) {
                if(children[i].toLowerCase().contains(criteria)) {
                    copyFile(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
                    //System.out.println("c: "+targetLocation+"/"+children[i]);
                }
            }
        }
    }

    /**
    http://stackoverflow.com/questions/3775694/deleting-folder-from-java
        @throws IOException if the file access fails.
    */
    public static boolean deleteDirectory(File directory)
        throws IOException
    {
        if(directory.exists()){
            File[] files = directory.listFiles();
            if(null!=files){
                for(int i=0; i<files.length; i++) {
                    if(files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    } else {
                        if (!files[i].delete())
                            throw new IOException("Can not delete file"+
                                files[i]);
                    }
                }
            }
        }
        return directory.delete();
    }
}