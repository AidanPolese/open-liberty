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

import com.ibm.ws.kernel.boot.internal.FileUtils;

public class FileEntryConfig implements ArchiveEntryConfig {
    private final String entryPath;
    private final File source;

    /**
     * Create a file entry config.
     * 
     * @param entryPath the entry path in the archive, could be either a file path or a directory path.
     * @param source the source file.
     * @throws IllegalArgumentException if the source File object does not exist or is not a file.
     */
    public FileEntryConfig(String entryPath, File source) {

        entryPath = FileUtils.normalizeEntryPath(entryPath);
        if (entryPath.equals("") || entryPath.endsWith("/")) {
            this.entryPath = entryPath + source.getName();
        } else {
            this.entryPath = entryPath;
        }

        if (!source.exists()) {
            throw new IllegalArgumentException("The source does not exist.");
        }
        if (!source.isFile()) {
            throw new IllegalArgumentException("The source is not a file.");
        }
        this.source = source;
    }

    @Override
    public String getEntryPath() {
        return this.entryPath;
    }

    @Override
    public File getSource() {
        return this.source;
    }

    @Override
    public void configure(Archive archive) throws IOException {
        archive.addFileEntry(entryPath, source);

    }

}
