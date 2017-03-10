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
package com.ibm.ws.kernel.boot.archive.internal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.ibm.ws.kernel.boot.archive.Archive;
import com.ibm.ws.kernel.boot.archive.ArchiveEntryConfig;

public abstract class AbstractArchive implements Archive {

    protected File archiveFile;

    protected final List<ArchiveEntryConfig> configList = new ArrayList<ArchiveEntryConfig>();

    // the paths that has been added to the archive
    protected final Set<String> entryPaths = new TreeSet<String>();

    @Override
    public void addEntryConfig(ArchiveEntryConfig entryConfig) {
        configList.add(entryConfig);
    }

    @Override
    public void addEntryConfigs(List<ArchiveEntryConfig> entryConfigs) {
        configList.addAll(entryConfigs);
    }

    @Override
    public File create() throws IOException {
        Iterator<ArchiveEntryConfig> configIter = configList.iterator();

        while (configIter.hasNext()) {
            ArchiveEntryConfig config = configIter.next();
            config.configure(this);
        }

        return archiveFile;
    }

}
