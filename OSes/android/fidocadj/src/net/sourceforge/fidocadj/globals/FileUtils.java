package net.sourceforge.fidocadj.globals;

import java.io.*;
import java.util.*;


public final class FileUtils
{
    /** Private constructor, for Utility class pattern
    */
    private FileUtils ()
    {
        // nothing
    }

    /**
    http://subversivebytes.wordpress.com/2012/11/05/java-copy-directory-recursive-delete/
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