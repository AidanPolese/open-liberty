/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.boot;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import com.ibm.ws.kernel.boot.logging.TextFileOutputStreamFactory;

/**
 * Lightweight capture of critical diagnostic information for launcher commands.
 */
public class Debug {
    private static PrintStream out = System.err;
    private static File openedFile;

    /**
     * Create the debug log at the specified location if possible. If this call
     * fails, {@link #isOpen} returns false and diagnostic information will be
     * printed to System.err.
     */
    static void open(File dir, String fileName) {
        if (dir.mkdirs() || dir.isDirectory()) {
            File file = new File(dir, fileName);
            try {
                out = new PrintStream(TextFileOutputStreamFactory.createOutputStream(file), true, "UTF-8");
                openedFile = file;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Returns true if {@link #open} has been called successfully.
     */
    static boolean isOpen() {
        return openedFile != null;
    }

    /**
     * Close the debug log opened by {@link #open} and optionally delete it.
     * This method has no effect if {@link #open} was not called successfully.
     * 
     * @param delete true if the debug log should be deleted after closing
     */
    static void close(boolean delete) {
        if (openedFile != null) {
            out.close();
            out = System.err;

            if (delete) {
                openedFile.delete();
            }
        }
    }

    public static void println() {
        out.println();
    }

    public static void println(Object o) {
        out.println(o);
    }

    public static void printStackTrace(Throwable t) {
        t.printStackTrace(out);
    }
}
