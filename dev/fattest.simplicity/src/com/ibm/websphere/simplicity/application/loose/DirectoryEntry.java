/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.simplicity.application.loose;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Represents a single directory entry in a virtual archive XML document.
 */
public class DirectoryEntry extends FileEntry {
    protected String excludes;

    /**
     * No-argument constructor (required by JAXB)
     */
    public DirectoryEntry() {
        super(); // required by JAXB
    }

    /**
     * Primary constructor for directory entries
     * 
     * @param sourceOnDisk the physical location of this entry in the file system
     * @param targetInArchive the virtual location of this entry in the parent archive
     */
    public DirectoryEntry(String sourceOnDisk, String targetInArchive) {
        super(sourceOnDisk, targetInArchive);
    }

    /**
     * Supplementary constructor for directory entries
     * 
     * @param sourceOnDisk the physical location of this entry in the file system
     * @param excludes entries to exclude from <code>sourceOnDisk</code> in the virtual archive
     * @param targetInArchive the virtual location of this entry in the parent archive
     */
    public DirectoryEntry(String sourceOnDisk, String excludes, String targetInArchive) {
        super(sourceOnDisk, targetInArchive);
        this.excludes = excludes;
    }

    /**
     * @return entries to exclude from <code>sourceOnDisk</code> in the virtual archive
     */
    public String getExcludes() {
        return excludes;
    }

    /**
     * Defines an exclusion filter for <code>sourceOnDisk</code>
     * 
     * @param excludes entries to exclude from <code>sourceOnDisk</code> in the virtual archive
     */
    @XmlAttribute
    public void setExcludes(String excludes) {
        this.excludes = excludes;
    }

}
