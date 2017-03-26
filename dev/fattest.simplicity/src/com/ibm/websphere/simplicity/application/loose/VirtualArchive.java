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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a virtual archive XML document, or an embedded archive within a parent XML document.
 */
@XmlRootElement(name = "archive")
public class VirtualArchive extends VirtualEntry {

    @XmlElement(name = "file")
    private final ArrayList<FileEntry> fileEntries = new ArrayList<FileEntry>();
    @XmlElement(name = "dir")
    private final ArrayList<DirectoryEntry> dirEntries = new ArrayList<DirectoryEntry>();
    @XmlElement(name = "archive")
    private final ArrayList<VirtualArchive> archiveEntries = new ArrayList<VirtualArchive>();

    /**
     * Constructs a virtual archive XML document.
     * This constructor <b>should not</b> be used to construct an embedded archive within a parent XML document.
     */
    public VirtualArchive() {
        super(); // required by JAXB
    }

    /**
     * Constructs an embedded archive within a parent XML document.
     * This constructor <b>should not</b> be used to construct virtual archive XML document.
     * 
     * @param targetInArchive the virtual location of this entry in the parent archive
     */
    public VirtualArchive(String targetInArchive) {
        super(targetInArchive);
    }

    /**
     * Gets the internal list of file entries in this archive. The list can be modified to change the state of this instance.
     * 
     * @return the file entries in this archive
     */
    public List<FileEntry> getFileEntries() {
        return this.fileEntries;
    }

    /**
     * Gets the internal list of directory entries in this archive. The list can be modified to change the state of this instance.
     * 
     * @return the directory entries in this archive
     */
    public List<DirectoryEntry> getDirectoryEntries() {
        return this.dirEntries;
    }

    /**
     * Gets the internal list of child archives. The list can be modified to change the state of this instance.
     * 
     * @return child virtual archives
     */
    public List<VirtualArchive> getVirtualArchiveEntries() {
        return this.archiveEntries;
    }

}
