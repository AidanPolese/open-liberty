/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011,2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.config;

import java.io.File;
import java.util.Collection;

/**
 * Provides access to get the directory path or files represented by a &ltfileset/&gt configuration.
 */
public interface Fileset {

    /**
     * Returns the {@link String} path representing the directory of the fileset.
     * 
     * @return the directory path
     */
    String getDir();

    /**
     * This method returns a {@link java.util.Collection} of {@link File} objects determined by the fileset configuration. The returned {@link java.util.Collection} is not updated
     * if the configuration changes
     * or the contents of a monitored directory are udpated. To be notified of
     * these changes use a {@link FilesetChangeListener}.
     * 
     * @return the collection of matching files
     */
    Collection<File> getFileset();
}