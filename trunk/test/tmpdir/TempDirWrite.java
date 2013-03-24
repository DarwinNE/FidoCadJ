import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
 
public class TempDirWrite
{
    public static void main(String[] args)
    {	
 
    	try {
    	    File temp = File.createTempFile("temp", ".tmp"); 
			String absolutePath = temp.getAbsolutePath();
    		String tempFilePath = absolutePath.
    		    substring(0,absolutePath.lastIndexOf(File.separator));
 
    		System.out.println("Temp file path: " + tempFilePath);
 
    	} catch(IOException e){
    	    e.printStackTrace();
    	}

    }
}