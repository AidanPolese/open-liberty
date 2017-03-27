package com.ibm.ws.zos.logging.zfat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import componenttest.topology.impl.LibertyServer;

public class EbcdicUtils {
    
    /**
     * The given fileName is copied from the files/ dir to ${server.config.dir}
     * and converted to EBCDIC.  
     * 
     * @returns the fileName (absolute path) of the ebcdic version of the file.
     */
    public static String convertToEbcdic(LibertyServer server, String fileName) throws Exception {
        
        // Copy to server root so that the jbatch utility can find it
        server.copyFileToLibertyServerRoot( fileName );
        String absFileName = StringUtils.join( Arrays.asList( server.getServerRoot(), fileName ), File.separator);
        String absFileNameEbc = absFileName + ".ebc";

        // Convert to ebcdic.
        IOUtils.convertFile(absFileName, 
                            Charset.forName("UTF-8"),
                            absFileNameEbc,
                            Charset.forName("IBM-1047"));
        
        return absFileNameEbc;
    }

}

/**
 * 
 * Could have imported apache.commons.lang3, but only needed a few methods.
 */
class StringUtils {

    /**
     * @return the given strs joined on the given delim.
     */
    public static String join(Collection<String> strs, String delim) {
        StringBuffer retMe = new StringBuffer();
        String d = "";
        for (String str : ( (strs != null) ? strs : new ArrayList<String>() ) ) {
            retMe.append(d).append(str);
            d = delim;
        }
        return retMe.toString();
    }
    
    /**
     * @return true if the string is null or "" or nothing but whitespace.
     */
    public static boolean isEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }
}

/**
 * IO utilities.
 */
class IOUtils {

    /**
     * Copy the given InputStream to the given OutputStream.
     * 
     * Note: the InputStream is closed when the copy is complete.  The OutputStream 
     *       is left open.
     */
    public static void copyStream(InputStream from, OutputStream to) throws IOException {
        byte buffer[] = new byte[2048];
        int bytesRead;
        while ((bytesRead = from.read(buffer)) != -1) {
            to.write(buffer, 0, bytesRead);
        }
        from.close();
    }
    
    /**
     * Copy the given Reader to the given Writer.
     * 
     * This method is basically the same as copyStream; however Reader and Writer
     * objects are cognizant of character encoding, whereas InputStream and OutputStreams
     * objects deal only with bytes.
     * 
     * Note: the Reader is closed when the copy is complete.  The Writer 
     *       is left open.  The Write is flushed when the copy is complete.
     */
    public static void copyReader(Reader from, Writer to) throws IOException {
        
        char buffer[] = new char[2048];
        int charsRead;
        while ((charsRead = from.read(buffer)) != -1) {
            to.write(buffer, 0, charsRead);
        }
        from.close();
        to.flush();
    }

    /**
     * Copy and convert from the given file and charset to the given file and charset.
     * 
     * @return File(toFileName)
     */
    public static File convertFile(String fromFileName, 
                                   Charset fromCharset, 
                                   String toFileName, 
                                   Charset toCharset) throws IOException {
        
        Reader reader = new InputStreamReader( new FileInputStream(fromFileName), fromCharset);
        Writer writer = new OutputStreamWriter( new FileOutputStream(toFileName), toCharset) ;
        
        copyReader(reader, writer);
        writer.close();
        
        return new File(toFileName);
    }

}
