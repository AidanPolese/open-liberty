/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.artifact.url;

import java.io.File;

public interface WSJarURLConnection {
    /**
     * Returns a File object referencing the archive addressed by the wsjar URL.
     * 
     * @return
     */
    public File getFile();

    /**
     * Returns a String containing the archive path component of the wsjar URL. This is the path component
     * following the !/ separator.
     * 
     * @return
     */
    public String getEntry();
}
