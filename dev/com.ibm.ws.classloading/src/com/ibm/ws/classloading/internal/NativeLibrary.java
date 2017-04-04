/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.classloading.internal;

import java.io.File;

/**
 * A quick interface to represent native libraries for the native library adapter.
 */
public interface NativeLibrary {
    /**
     * Obtain the File on disk representing this library
     * 
     * @return File of library.
     */
    public File getLibraryFile();
}
