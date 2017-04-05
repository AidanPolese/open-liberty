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
package com.ibm.ws.artifact.zip.cache;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 */
public interface ZipFileHandle {

    /**
     * Call to obtain zip file from this zip handle.. each call must be matched by a call to close()
     * 
     * @return
     * @throws IOException
     */
    ZipFile open() throws IOException;

    /**
     * Call to say you no longer need the zip file obtained from open.
     */
    void close();

    /**
     * Call to obtain an input stream for a ZipFile from this handle, for a given zip entry.
     * <br>
     * this method may use caches to speed repeated access to content.
     * 
     * @param zf
     * @param ze
     * @return
     */
    InputStream getInputStream(ZipFile zf, ZipEntry ze) throws IOException;
}
