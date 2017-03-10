/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.boot.logging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * This is a factory that creates and returns FileOutputStreams for the provided
 * File arguments. The methods match the constructors of a usual FileOutputStream,
 * but allow different platforms to perform additional operations on the created
 * file, where necessary.
 */
public class TextFileOutputStreamFactory {

    static interface Delegate {
        /**
         * @see FileOutputStream#FileOutputStream(File)
         */
        FileOutputStream createOutputStream(File file) throws IOException;

        /**
         * @see FileOutputStream#FileOutputStream(File, boolean)
         */
        FileOutputStream createOutputStream(File file, boolean append) throws IOException;

        /**
         * @see FileOutputStream#FileOutputStream(String)
         */
        FileOutputStream createOutputStream(String name) throws IOException;

        /**
         * @see FileOutputStream#FileOutputStream(String, boolean)
         */
        FileOutputStream createOutputStream(String name, boolean append) throws IOException;
    }

    private static final class DelegateHolder {

        private static final Delegate delegate;

        static {
            String systemOS = System.getProperty("os.name");
            if (systemOS.equals("z/OS")) {
                delegate = new TaggedFileOutputStreamFactory();
            } else {
                delegate = new DefaultFileStreamFactory();
            }
        }
    }

    /**
     * @see FileOutputStream#FileOutputStream(File)
     */
    public static FileOutputStream createOutputStream(File file) throws IOException {
        return DelegateHolder.delegate.createOutputStream(file);
    }

    /**
     * @see FileOutputStream#FileOutputStream(File, boolean)
     */
    public static FileOutputStream createOutputStream(File file, boolean append) throws IOException {
        return DelegateHolder.delegate.createOutputStream(file, append);
    }

    /**
     * @see FileOutputStream#FileOutputStream(String)
     */
    public static FileOutputStream createOutputStream(String name) throws IOException {
        return DelegateHolder.delegate.createOutputStream(name);
    }

    /**
     * @see FileOutputStream#FileOutputStream(String, boolean)
     */
    public static FileOutputStream createOutputStream(String name, boolean append) throws IOException {
        return DelegateHolder.delegate.createOutputStream(name, append);
    }
}
