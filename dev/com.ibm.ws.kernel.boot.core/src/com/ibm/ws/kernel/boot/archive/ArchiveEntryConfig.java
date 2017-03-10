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
package com.ibm.ws.kernel.boot.archive;

import java.io.File;
import java.io.IOException;

/**
 * Represent an entry adding to Archive
 */
public interface ArchiveEntryConfig {

    /**
     * Get the entry path.
     * 
     * @return
     */
    public String getEntryPath();

    /**
     * Get the source file of the entry.
     * 
     * @return
     */
    public File getSource();

    /**
     * Configure the archive
     * 
     * @param archive
     * @throws IOException
     */
    public void configure(Archive archive) throws IOException;

}
