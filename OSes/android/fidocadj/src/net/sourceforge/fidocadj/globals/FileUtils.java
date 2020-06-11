package net.sourceforge.fidocadj.globals;

import java.io.*;
import java.util.*;

/** General file utilities.

    NOTE: this file is based on some examples found here and there (the links
    are provided below).
    If you own the copyright and you do not agree that this class is licensed
    with a GPL v.3 license, contact the FidoCadJ developers and we will find
    a solution.

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

    /** Copy a directory recursively
        Source:
        http://subversivebytes.wordpress.com/2012/11/05/java-copy-directory-\
            recursive-delete/
        @param sourceLocation the source location.
        @param targetLocation the target location.
        @throws IOException if something goes wrong.
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
        @param sourceLocation the source location.
        @param targetLocation the target location.
        @throws IOException if something goes wrong.
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
        @param sourceLocation the source location.
        @param targetLocation the target location.
        @param tcriteria the criteria to be employed for copying files.
        @throws IOException if something goes wrong.
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
                }
            }
        }
    }

    /** Delete a directory.
        http://stackoverflow.com/questions/3775694/deleting-folder-from-java
        @param directory the directory to be deleted.
        @return boolean
        @throws IOException if something goes wrong.
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