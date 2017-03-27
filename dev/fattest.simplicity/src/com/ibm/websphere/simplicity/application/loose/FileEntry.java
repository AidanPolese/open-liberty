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
 * Represents a single file entry in a virtual archive XML document.
 */
public class FileEntry extends VirtualEntry {

    private String sourceOnDisk;

    /**
     * No-argument constructor (required by JAXB)
     */
    public FileEntry() {
        super(); // required by JAXB
    }

    /**
     * Primary constructor for file entries
     * 
     * @param sourceOnDisk the physical location of this entry in the file system
     * @param targetInArchive the virtual location of this entry in the parent archive
     */
    public FileEntry(String sourceOnDisk, String targetInArchive) {
        super(targetInArchive);
        this.setSourceOnDisk(sourceOnDisk);
    }

    /**
     * @return the physical location of this entry in the file system
     */
    public String getSourceOnDisk() {
        return sourceOnDisk;
    }

    /**
     * <p>Defines the physical location of this entry in the file system</p>
     * <p>Note that the virtual entry represented by this class may be
     * located on a remote machine, so paths must be expressed by
     * String instances instead of File instances.</p>
     * 
     * @param targetInArchive the physical location of this entry in the file system
     */
    @XmlAttribute
    public void setSourceOnDisk(String sourceOnDisk) {
        this.sourceOnDisk = sourceOnDisk;
    }

}
